package storage;

import entity.Tenant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryTenantStorage implements TenantStorage
{
    private final Map<Integer, Tenant> tenants = new ConcurrentHashMap<>();
    private final AtomicInteger autoIncrementId = new AtomicInteger(0);

    @Override
    public Tenant find(int id)
    {
        return tenants.get(id);
    }

    @Override
    public int save(Tenant tenant)
    {
        if (tenant.getId() == null)
        {
            int id = autoIncrementId.incrementAndGet();
            tenant.setId(id);
        }
        tenants.putIfAbsent(tenant.getId(), tenant);
        return tenant.getId();
    }

    @Override
    public int count()
    {
        return tenants.size();
    }
}
