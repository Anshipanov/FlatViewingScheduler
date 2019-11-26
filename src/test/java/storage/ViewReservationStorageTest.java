package storage;

import entity.ViewReservation;
import java.time.Duration;
import java.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ViewReservationStorageTest
{

    private ViewReservationStorage viewReservationStorage;

    @Before
    public void prepare()
    {
        viewReservationStorage = new InMemoryViewReservationStorage();
    }

    @Test
    public void save()
    {
        ViewReservation viewReservation1 = new ViewReservation(1, 1, LocalDateTime.now());
        ViewReservation viewReservation2 = new ViewReservation(2, 2, LocalDateTime.now());
        ViewReservation viewReservation3 = new ViewReservation(3, 3, LocalDateTime.now());
        Assert.assertEquals("Assigned id has to be 1", 1, viewReservationStorage.save(viewReservation1));
        Assert.assertEquals("Assigned id has to be 2", 2, viewReservationStorage.save(viewReservation2));
        Assert.assertEquals("Assigned id has to be 3", 3, viewReservationStorage.save(viewReservation3));
    }

    @Test
    public void find()
    {
        LocalDateTime startTime1 = LocalDateTime.now().plus(Duration.ofDays(1));
        ViewReservation viewReservation1 = new ViewReservation(1, 1, startTime1);
        LocalDateTime startTime2 = LocalDateTime.now().plus(Duration.ofDays(2));
        ViewReservation viewReservation2 = new ViewReservation(1, 1, startTime2);
        LocalDateTime startTime3 = LocalDateTime.now().plus(Duration.ofDays(3));
        ViewReservation viewReservation3 = new ViewReservation(1, 1, startTime3);
        viewReservationStorage.save(viewReservation1);
        viewReservationStorage.save(viewReservation2);
        viewReservationStorage.save(viewReservation3);
        Assert.assertEquals(
                "Start time of view reservation with id = 1 has to be " + startTime1.toString(),
                startTime1,
                viewReservationStorage.find(1).getStartTime()
        );
        Assert.assertEquals(
                "Start time of view reservation with id = 2 has to be " + startTime2.toString(),
                startTime2,
                viewReservationStorage.find(2).getStartTime()
        );
        Assert.assertEquals(
                "Start time of view reservation with id = 3 has to be " + startTime3.toString(),
                startTime3,
                viewReservationStorage.find(3).getStartTime()
        );
    }

    @Test
    public void findByFlatAndStartTime()
    {
        int flatId = 1;
        int tenantId = 1;
        LocalDateTime startTime1 = LocalDateTime.now().plus(Duration.ofDays(1));
        ViewReservation viewReservation1 = new ViewReservation(flatId, tenantId, startTime1);
        LocalDateTime startTime2 = LocalDateTime.now().plus(Duration.ofDays(2));
        ViewReservation viewReservation2 = new ViewReservation(flatId, tenantId, startTime2);
        ViewReservation viewReservation3 = new ViewReservation(flatId, tenantId, startTime2);
        viewReservationStorage.save(viewReservation1);
        viewReservationStorage.save(viewReservation2);
        viewReservationStorage.save(viewReservation3);

        Assert.assertEquals(1, viewReservationStorage.find(flatId, startTime1).size());
        Assert.assertEquals(2, viewReservationStorage.find(flatId, startTime2).size());
    }

    @Test
    public void update()
    {
        ViewReservation viewReservation = new ViewReservation(1, 1, LocalDateTime.now());
        int id = viewReservationStorage.save(viewReservation);
        ViewReservation reservation = viewReservationStorage.find(id);
        Assert.assertFalse(
                "All reservation flag has to be false",
                reservation.isApproved() || reservation.isRejected() || reservation.isCanceled()
        );
        reservation.setApproved(true);
        reservation.setRejected(true);
        reservation.setCanceled(true);
        viewReservationStorage.update(reservation);
        reservation = viewReservationStorage.find(id);
        Assert.assertTrue(
                "All reservation flag has to be true",
                reservation.isApproved() && reservation.isRejected() && reservation.isCanceled()
        );
    }
}