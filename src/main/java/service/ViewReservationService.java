package service;

import com.google.inject.Inject;
import entity.ViewReservation;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import notification.NotificationService;
import response.CreateViewReservationResponse;
import response.ViewReservationValidationStatus;
import storage.ViewReservationStorage;

public class ViewReservationService
{
    private static final Duration MIN_DURATION_BEFORE_VIEW_START_TIME = Duration.ofHours(24);
    private static final LocalTime MIN_TIME = LocalTime.of(10, 0);
    private static final LocalTime MAX_TIME = LocalTime.of(19, 40);

    private final ViewReservationStorage viewReservationStorage;
    private final TenantService tenantService;
    private final FlatService flatService;
    private final NotificationService notificationService;
    private final Clock clock;

    @Inject
    public ViewReservationService(
            ViewReservationStorage viewReservationStorage,
            TenantService tenantService,
            FlatService flatService,
            NotificationService notificationService,
            Clock clock)
    {
        this.viewReservationStorage = viewReservationStorage;
        this.tenantService = tenantService;
        this.flatService = flatService;
        this.notificationService = notificationService;
        this.clock = clock;
    }

    public CreateViewReservationResponse createReservation(int flatId, int tenantId, LocalDateTime startTime)
    {
        ViewReservationValidationStatus validationStatus = validateRequest(flatId, tenantId, startTime);
        if (validationStatus != ViewReservationValidationStatus.OK)
        {
            return CreateViewReservationResponse.fail(validationStatus);
        }
        ViewReservation viewReservation = new ViewReservation();
        viewReservation.setFlatId(flatId);
        viewReservation.setTenantId(tenantId);
        viewReservation.setStartTime(startTime);
        int id = viewReservationStorage.save(viewReservation);
        Integer currentTenantId = flatService.findById(flatId).getCurrentTenantId();
        notificationService.sendNewReservation(currentTenantId, flatId, startTime);
        return CreateViewReservationResponse.ok(id);
    }

    public ViewReservation findById(int id)
    {
        return viewReservationStorage.find(id);
    }

    public boolean approveReservation(int viewReservationId)
    {
        ViewReservation viewReservation = viewReservationStorage.find(viewReservationId);
        if (viewReservation.isCanceled() || viewReservation.isRejected() || viewReservation.isApproved())
        {
            return false;
        }
        else
        {
            viewReservation.setApproved(true);
            viewReservationStorage.update(viewReservation);
            notificationService.sendReservationApproved(
                    viewReservation.getTenantId(),
                    viewReservation.getFlatId(),
                    viewReservation.getStartTime()
            );
            return true;
        }
    }

    public boolean rejectReservation(int viewReservationId)
    {
        ViewReservation viewReservation = viewReservationStorage.find(viewReservationId);
        if (viewReservation.isCanceled() || viewReservation.isRejected() || viewReservation.isApproved())
        {
            return false;
        }
        else
        {
            viewReservation.setRejected(true);
            viewReservationStorage.update(viewReservation);
            notificationService.sendReservationRejected(
                    viewReservation.getTenantId(),
                    viewReservation.getFlatId(),
                    viewReservation.getStartTime()
            );
            return true;
        }
    }

    public boolean cancelReservation(int viewReservationId)
    {
        ViewReservation viewReservation = viewReservationStorage.find(viewReservationId);
        if (viewReservation.isCanceled())
        {
            return false;
        }
        else
        {
            viewReservation.setCanceled(true);
            viewReservationStorage.update(viewReservation);
            Integer currentTenantId = flatService.findById(viewReservation.getFlatId()).getCurrentTenantId();
            notificationService.sendReservationCanceled(
                    currentTenantId,
                    viewReservation.getFlatId(),
                    viewReservation.getStartTime()
            );
            return true;
        }
    }

    private ViewReservationValidationStatus validateRequest(int flatId, int tenantId, LocalDateTime startTime)
    {
        if (flatService.findById(flatId) == null)
        {
            return ViewReservationValidationStatus.FLAT_NOT_EXISTS;
        }
        if (tenantService.findById(tenantId) == null)
        {
            return ViewReservationValidationStatus.TENANT_NOT_EXISTS;
        }
        if (!isValidStartTime(startTime))
        {
            return ViewReservationValidationStatus.NOT_VALID_START_TIME;
        }
        if (!isTimeFree(flatId, startTime))
        {
            return ViewReservationValidationStatus.TIME_ALREADY_RESERVED;
        }
        return ViewReservationValidationStatus.OK;
    }

    private boolean isValidStartTime(LocalDateTime startDateTime)
    {
        if (!isDateInUpcomingWeek(startDateTime.toLocalDate()))
        {
            return false;
        }
        LocalDateTime now = LocalDateTime.ofInstant(clock.instant(), ZoneId.systemDefault());
        if (now.plus(MIN_DURATION_BEFORE_VIEW_START_TIME).isAfter(startDateTime))
        {
            return false;
        }
        LocalTime startTime = startDateTime.atZone(ZoneId.systemDefault()).toLocalTime();
        if (MIN_TIME.isAfter(startTime) || MAX_TIME.isBefore(startTime))
        {
            return false;
        }
        if (startTime.getMinute() % 20 != 0)
        {
            return false;
        }
        return true;
    }

    private boolean isDateInUpcomingWeek(LocalDate date) {
        LocalDate nextMonday = clock.instant().atZone(ZoneId.systemDefault()).toLocalDate()
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        LocalDate nextSunday = nextMonday.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
        return !nextSunday.isBefore(date) && !nextMonday.isAfter(date);
    }

    private boolean isTimeFree(int flatId, LocalDateTime startTime)
    {
        return viewReservationStorage.find(flatId, startTime).stream()
                .allMatch(reservation -> reservation.isCanceled() && !reservation.isRejected());
    }
}
