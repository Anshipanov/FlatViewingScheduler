package notification;

import java.time.LocalDateTime;

public class StubNotificationService implements NotificationService
{
    @Override
    public void sendNewReservation(int tenantId, int flatId, LocalDateTime startTime)
    {

    }

    @Override
    public void sendReservationApproved(int tenantId, int flatId, LocalDateTime startTime)
    {

    }

    @Override
    public void sendReservationRejected(int tenantId, int flatId, LocalDateTime startTime)
    {

    }

    @Override
    public void sendReservationCanceled(int tenantId, int flatId, LocalDateTime startTime)
    {

    }
}
