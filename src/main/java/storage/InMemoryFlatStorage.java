package storage;

import entity.Flat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryFlatStorage implements FlatStorage
{
    private final Map<Integer, Flat> flats = new ConcurrentHashMap<>();
    private final AtomicInteger autoIncrementId = new AtomicInteger(0);

    @Override
    public int save(Flat flat)
    {
        if (flat.getId() == null)
        {
            int id = autoIncrementId.incrementAndGet();
            flat.setId(id);
        }
        flats.putIfAbsent(flat.getId(), flat);
        return flat.getId();
    }

    @Override
    public Flat find(int id)
    {
        return flats.get(id);
    }

    @Override
    public int count()
    {
        return flats.size();
    }
}
