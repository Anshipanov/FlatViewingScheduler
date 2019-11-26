package storage;

import entity.Tenant;

public interface TenantStorage
{
    Tenant find(int id);
    int save(Tenant tenant);
    int count();
}
