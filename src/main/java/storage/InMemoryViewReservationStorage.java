package storage;

import entity.ViewReservation;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InMemoryViewReservationStorage implements ViewReservationStorage
{
    private final Map<Integer, ViewReservation> reservations = new ConcurrentHashMap<>();
    private final AtomicInteger autoIncrementId = new AtomicInteger(0);

    @Override
    public int save(ViewReservation viewReservation)
    {
        if (viewReservation.getId() == null)
        {
            int id = autoIncrementId.incrementAndGet();
            viewReservation.setId(id);
        }
        reservations.putIfAbsent(viewReservation.getId(), viewReservation);
        return viewReservation.getId();
    }

    @Override
    public List<ViewReservation> find(int flatId, LocalDateTime startTime)
    {
        return reservations.values().stream()
                .filter(
                        viewReservation -> viewReservation.getFlatId() == flatId
                                && viewReservation.getStartTime().equals(startTime)
                )
                .collect(Collectors.toList());
    }

    @Override
    public ViewReservation find(int id)
    {
        return reservations.get(id);
    }

    @Override
    public void update(ViewReservation viewReservation)
    {
        reservations.put(viewReservation.getId(), viewReservation);
    }
}
