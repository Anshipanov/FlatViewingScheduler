package storage;

import entity.ViewReservation;
import java.time.LocalDateTime;
import java.util.List;

public interface ViewReservationStorage
{
    int save(ViewReservation viewReservation);
    List<ViewReservation> find(int flatId, LocalDateTime startTime);
    ViewReservation find(int id);
    void update(ViewReservation viewReservation);
}
