package service;

import entity.Flat;
import entity.Tenant;
import entity.ViewReservation;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import notification.NotificationService;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import response.CreateViewReservationResponse;
import response.ViewReservationValidationStatus;
import storage.ViewReservationStorage;

public class ViewReservationServiceTest
{
    private NotificationService notificationService = Mockito.mock(NotificationService.class);
    private FlatService flatService = Mockito.mock(FlatService.class);
    private TenantService tenantService = Mockito.mock(TenantService.class);
    private ViewReservationStorage viewReservationStorage = Mockito.mock(ViewReservationStorage.class);
    private Clock clock = Mockito.mock(Clock.class);

    private ViewReservationService viewReservationService = new ViewReservationService(
            viewReservationStorage,
            tenantService,
            flatService,
            notificationService,
            clock
    );

    @Test
    public void createReservationFlatNotExists()
    {
        int flatId = 1;
        int tenantId = 1;
        LocalDateTime startTime = LocalDateTime.now();

        Mockito.when(flatService.findById(flatId)).thenReturn(null);
        CreateViewReservationResponse reservationResponse = viewReservationService.createReservation(
                flatId, tenantId, startTime
        );
        Assert.assertEquals(
                "Reservation status has to be FLAT_NOT_EXISTS",
                ViewReservationValidationStatus.FLAT_NOT_EXISTS,
                reservationResponse.getStatus()
        );
    }

    @Test
    public void createReservationTenantNotExists()
    {
        int flatId = 1;
        int tenantId = 1;
        LocalDateTime startTime = LocalDateTime.now();

        Mockito.when(flatService.findById(flatId)).thenReturn(new Flat(flatId, "some address", null));
        Mockito.when(tenantService.findById(tenantId)).thenReturn(null);
        CreateViewReservationResponse reservationResponse = viewReservationService.createReservation(
                flatId, tenantId, startTime
        );
        Assert.assertEquals(
                "Reservation status has to be TENANT_NOT_EXISTS",
                ViewReservationValidationStatus.TENANT_NOT_EXISTS,
                reservationResponse.getStatus()
        );
    }

    @Test
    public void createReservationStartTimeIsInThePast()
    {
        int flatId = 1;
        int tenantId = 1;

        Mockito.when(flatService.findById(flatId)).thenReturn(new Flat(flatId, "some address", null));
        Mockito.when(tenantService.findById(tenantId)).thenReturn(new Tenant(tenantId, "some name"));

        Mockito.when(clock.instant()).thenReturn(Instant.parse("2019-11-23T14:00:00.000Z"));
        LocalDateTime startTime = LocalDateTime.ofInstant(
                Instant.parse("2019-11-22T14:00:00.000Z"),
                ZoneId.systemDefault()
        );

        CreateViewReservationResponse reservationResponse = viewReservationService.createReservation(
                flatId, tenantId, startTime
        );
        Assert.assertEquals(
                "Reservation status has to be NOT_VALID_START_TIME",
                ViewReservationValidationStatus.NOT_VALID_START_TIME,
                reservationResponse.getStatus()
        );
    }

    @Test
    public void createReservationStartTimeIsAtCurrentWeek()
    {
        int flatId = 1;
        int tenantId = 1;

        Mockito.when(flatService.findById(flatId)).thenReturn(new Flat(flatId, "some address", null));
        Mockito.when(tenantService.findById(tenantId)).thenReturn(new Tenant(tenantId, "some name"));

        Mockito.when(clock.instant()).thenReturn(Instant.parse("2019-11-23T14:00:00.000Z"));
        LocalDateTime startTime = LocalDateTime.ofInstant(
                Instant.parse("2019-11-24T14:00:00.000Z"),
                ZoneId.systemDefault()
        );

        CreateViewReservationResponse reservationResponse = viewReservationService.createReservation(
                flatId, tenantId, startTime
        );
        Assert.assertEquals(
                "Reservation status has to be NOT_VALID_START_TIME",
                ViewReservationValidationStatus.NOT_VALID_START_TIME,
                reservationResponse.getStatus()
        );
    }

    @Test
    public void createReservationStartTimeIsTwoWeeksLater()
    {
        int flatId = 1;
        int tenantId = 1;

        Mockito.when(flatService.findById(flatId)).thenReturn(new Flat(flatId, "some address", null));
        Mockito.when(tenantService.findById(tenantId)).thenReturn(new Tenant(tenantId, "some name"));

        Mockito.when(clock.instant()).thenReturn(Instant.parse("2019-11-23T14:00:00.000Z"));
        LocalDateTime startTime = LocalDateTime.ofInstant(
                Instant.parse("2019-12-07T14:00:00.000Z"),
                ZoneId.systemDefault()
        );

        CreateViewReservationResponse reservationResponse = viewReservationService.createReservation(
                flatId, tenantId, startTime
        );
        Assert.assertEquals(
                "Reservation status has to be NOT_VALID_START_TIME",
                ViewReservationValidationStatus.NOT_VALID_START_TIME,
                reservationResponse.getStatus()
        );
    }

    @Test
    public void createReservationStartTimeIsLessThan24HoursFromNow()
    {
        int flatId = 1;
        int tenantId = 1;

        Mockito.when(flatService.findById(flatId)).thenReturn(new Flat(flatId, "some address", null));
        Mockito.when(tenantService.findById(tenantId)).thenReturn(new Tenant(tenantId, "some name"));

        Mockito.when(clock.instant()).thenReturn(Instant.parse("2019-11-24T14:00:00.000Z"));
        LocalDateTime startTime = LocalDateTime.ofInstant(
                Instant.parse("2019-11-25T13:00:00.000Z"),
                ZoneId.systemDefault()
        );

        CreateViewReservationResponse reservationResponse = viewReservationService.createReservation(
                flatId, tenantId, startTime
        );
        Assert.assertEquals(
                "Reservation status has to be NOT_VALID_START_TIME",
                ViewReservationValidationStatus.NOT_VALID_START_TIME,
                reservationResponse.getStatus()
        );
    }

    @Test
    public void createReservationStartTimeIsToEarly()
    {
        int flatId = 1;
        int tenantId = 1;

        Mockito.when(flatService.findById(flatId)).thenReturn(new Flat(flatId, "some address", null));
        Mockito.when(tenantService.findById(tenantId)).thenReturn(new Tenant(tenantId, "some name"));

        Mockito.when(clock.instant()).thenReturn(Instant.parse("2019-11-24T14:00:00.000Z"));
        LocalDateTime startTime = LocalDateTime.ofInstant(
                Instant.parse("2019-11-27T05:00:00.000Z"),
                ZoneId.systemDefault()
        );

        CreateViewReservationResponse reservationResponse = viewReservationService.createReservation(
                flatId, tenantId, startTime
        );
        Assert.assertEquals(
                "Reservation status has to be NOT_VALID_START_TIME",
                ViewReservationValidationStatus.NOT_VALID_START_TIME,
                reservationResponse.getStatus()
        );
    }

    @Test
    public void createReservationStartTimeIsToLate()
    {
        int flatId = 1;
        int tenantId = 1;

        Mockito.when(flatService.findById(flatId)).thenReturn(new Flat(flatId, "some address", null));
        Mockito.when(tenantService.findById(tenantId)).thenReturn(new Tenant(tenantId, "some name"));

        Mockito.when(clock.instant()).thenReturn(Instant.parse("2019-11-24T14:00:00.000Z"));
        LocalDateTime startTime = LocalDateTime.ofInstant(
                Instant.parse("2019-11-27T20:40:00.000Z"),
                ZoneId.systemDefault()
        );

        CreateViewReservationResponse reservationResponse = viewReservationService.createReservation(
                flatId, tenantId, startTime
        );
        Assert.assertEquals(
                "Reservation status has to be NOT_VALID_START_TIME",
                ViewReservationValidationStatus.NOT_VALID_START_TIME,
                reservationResponse.getStatus()
        );
    }

    @Test
    public void createReservationWrongSlotStartTime()
    {
        int flatId = 1;
        int tenantId = 1;

        Mockito.when(flatService.findById(flatId)).thenReturn(new Flat(flatId, "some address", null));
        Mockito.when(tenantService.findById(tenantId)).thenReturn(new Tenant(tenantId, "some name"));

        Mockito.when(clock.instant()).thenReturn(Instant.parse("2019-11-24T14:00:00.000Z"));
        LocalDateTime startTime = LocalDateTime.ofInstant(
                Instant.parse("2019-11-27T12:30:00.000Z"),
                ZoneId.systemDefault()
        );

        CreateViewReservationResponse reservationResponse = viewReservationService.createReservation(
                flatId, tenantId, startTime
        );
        Assert.assertEquals(
                "Reservation status has to be NOT_VALID_START_TIME",
                ViewReservationValidationStatus.NOT_VALID_START_TIME,
                reservationResponse.getStatus()
        );
    }

    @Test
    public void createTimeAlreadyReserved()
    {
        int flatId = 1;
        int tenantId = 1;

        Mockito.when(flatService.findById(flatId)).thenReturn(new Flat(flatId, "some address", null));
        Mockito.when(tenantService.findById(tenantId)).thenReturn(new Tenant(tenantId, "some name"));

        Mockito.when(clock.instant()).thenReturn(Instant.parse("2019-11-24T14:00:00.000Z"));
        LocalDateTime startTime = LocalDateTime.ofInstant(
                Instant.parse("2019-11-27T12:00:00.000Z"),
                ZoneId.systemDefault()
        );

        Mockito.when(viewReservationStorage.find(flatId, startTime)).thenReturn(
                Collections.singletonList(
                        new ViewReservation(1, flatId, tenantId, startTime, false, false, false)
                )
        );

        CreateViewReservationResponse reservationResponse = viewReservationService.createReservation(
                flatId, tenantId, startTime
        );
        Assert.assertEquals(
                "Reservation status has to be TIME_ALREADY_RESERVED",
                ViewReservationValidationStatus.TIME_ALREADY_RESERVED,
                reservationResponse.getStatus()
        );
    }

    @Test
    public void createReservationTimeAlreadyRejected()
    {
        int flatId = 1;
        int tenantId = 1;

        Mockito.when(flatService.findById(flatId)).thenReturn(new Flat(flatId, "some address", null));
        Mockito.when(tenantService.findById(tenantId)).thenReturn(new Tenant(tenantId, "some name"));

        Mockito.when(clock.instant()).thenReturn(Instant.parse("2019-11-24T14:00:00.000Z"));
        LocalDateTime startTime = LocalDateTime.ofInstant(
                Instant.parse("2019-11-27T12:00:00.000Z"),
                ZoneId.systemDefault()
        );

        Mockito.when(viewReservationStorage.find(flatId, startTime)).thenReturn(
                Collections.singletonList(
                        new ViewReservation(1, flatId, tenantId, startTime, false, true, false)
                )
        );

        CreateViewReservationResponse reservationResponse = viewReservationService.createReservation(
                flatId, tenantId, startTime
        );
        Assert.assertEquals(
                "Reservation status has to be TIME_ALREADY_RESERVED",
                ViewReservationValidationStatus.TIME_ALREADY_RESERVED,
                reservationResponse.getStatus()
        );
    }

    @Test
    public void createReservationSuccessIfOldReservationWasApprovedAndThenCanceled()
    {
        int flatId = 1;
        int tenantId = 1;
        int currentTenantId = 2;

        Mockito.when(flatService.findById(flatId)).thenReturn(new Flat(flatId, "some address", currentTenantId));
        Mockito.when(tenantService.findById(tenantId)).thenReturn(new Tenant(tenantId, "some name"));

        Mockito.when(clock.instant()).thenReturn(Instant.parse("2019-11-24T14:00:00.000Z"));
        LocalDateTime startTime = LocalDateTime.ofInstant(
                Instant.parse("2019-11-27T12:00:00.000Z"),
                ZoneId.systemDefault()
        );

        Mockito.when(viewReservationStorage.find(flatId, startTime)).thenReturn(
                Collections.singletonList(
                        new ViewReservation(1, flatId, tenantId, startTime, true, false, true)
                )
        );

        Integer newReservationId = 2;
        Mockito.when(viewReservationStorage.save(Mockito.any())).thenReturn(newReservationId);

        CreateViewReservationResponse reservationResponse = viewReservationService.createReservation(
                flatId, tenantId, startTime
        );
        Assert.assertEquals(
                "Reservation status has to be OK",
                ViewReservationValidationStatus.OK,
                reservationResponse.getStatus()
        );
        Assert.assertEquals(
                "Reservation is has to be 2",
                newReservationId,
                reservationResponse.getId()
        );
    }

    @Test
    public void createReservationSuccessIfNewWeekIsInNewYear()
    {
        int flatId = 1;
        int tenantId = 1;
        int currentTenantId = 2;

        Mockito.when(flatService.findById(flatId)).thenReturn(new Flat(flatId, "some address", currentTenantId));
        Mockito.when(tenantService.findById(tenantId)).thenReturn(new Tenant(tenantId, "some name"));

        Mockito.when(clock.instant()).thenReturn(Instant.parse("2019-12-27T14:00:00.000Z"));
        LocalDateTime startTime = LocalDateTime.ofInstant(
                Instant.parse("2020-01-03T12:00:00.000Z"),
                ZoneId.systemDefault()
        );

        Mockito.when(viewReservationStorage.find(flatId, startTime)).thenReturn(Collections.emptyList());

        Integer newReservationId = 2;
        Mockito.when(viewReservationStorage.save(Mockito.any())).thenReturn(newReservationId);

        CreateViewReservationResponse reservationResponse = viewReservationService.createReservation(
                flatId, tenantId, startTime
        );
        Assert.assertEquals(
                "Reservation status has to be OK",
                ViewReservationValidationStatus.OK,
                reservationResponse.getStatus()
        );
        Assert.assertEquals(
                "Reservation is has to be 2",
                newReservationId,
                reservationResponse.getId()
        );
    }

    @Test
    public void approveReservation()
    {
        int viewReservationId = 1;
        ViewReservation reservation = Mockito.mock(ViewReservation.class);
        Mockito.when(viewReservationStorage.find(viewReservationId)).thenReturn(reservation);
        viewReservationService.approveReservation(viewReservationId);
        Mockito.verify(reservation, Mockito.times(1)).setApproved(true);
        Mockito.verify(viewReservationStorage, Mockito.times(1)).update(Mockito.any());
        Mockito.verify(notificationService, Mockito.times(1))
                .sendReservationApproved(Mockito.anyInt(), Mockito.anyInt(), Mockito.any());
    }

    @Test
    public void rejectReservation()
    {
        int viewReservationId = 1;
        ViewReservation reservation = Mockito.mock(ViewReservation.class);
        Mockito.when(viewReservationStorage.find(viewReservationId)).thenReturn(reservation);
        viewReservationService.rejectReservation(viewReservationId);
        Mockito.verify(reservation, Mockito.times(1)).setRejected(true);
        Mockito.verify(viewReservationStorage, Mockito.times(1)).update(Mockito.any());
        Mockito.verify(notificationService, Mockito.times(1))
                .sendReservationRejected(Mockito.anyInt(), Mockito.anyInt(), Mockito.any());
    }

    @Test
    public void cancelReservation()
    {
        int viewReservationId = 1;
        int flatId = 1;
        ViewReservation reservation = Mockito.mock(ViewReservation.class);
        Mockito.when(reservation.getFlatId()).thenReturn(flatId);
        Mockito.when(reservation.getTenantId()).thenReturn(1);
        Mockito.when(viewReservationStorage.find(viewReservationId)).thenReturn(reservation);
        Mockito.when(flatService.findById(flatId)).thenReturn(new Flat("", 1));
        viewReservationService.cancelReservation(viewReservationId);
        Mockito.verify(reservation, Mockito.times(1)).setCanceled(true);
        Mockito.verify(viewReservationStorage, Mockito.times(1)).update(Mockito.any());
        Mockito.verify(notificationService, Mockito.times(1))
                .sendReservationCanceled(Mockito.anyInt(), Mockito.anyInt(), Mockito.any());
    }
}