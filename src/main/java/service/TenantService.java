package service;

import com.google.inject.Inject;
import entity.Tenant;
import storage.TenantStorage;

public class TenantService
{
    @Inject
    private final TenantStorage tenantStorage;

    public TenantService(TenantStorage tenantStorage)
    {
        this.tenantStorage = tenantStorage;
    }

    public int createTenant(String name)
    {
        return tenantStorage.save(new Tenant(name));
    }

    public Tenant findById(int id)
    {
        return tenantStorage.find(id);
    }
}
