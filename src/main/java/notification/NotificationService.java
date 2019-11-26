package notification;

import java.time.LocalDateTime;

public interface NotificationService
{
    void sendNewReservation(int tenantId, int flatId, LocalDateTime startTime);
    void sendReservationApproved(int tenantId, int flatId, LocalDateTime startTime);
    void sendReservationRejected(int tenantId, int flatId, LocalDateTime startTime);
    void sendReservationCanceled(int tenantId, int flatId, LocalDateTime startTime);
}
