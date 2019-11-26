package service;

import com.google.inject.Inject;
import entity.Flat;
import storage.FlatStorage;

public class FlatService
{
    private final FlatStorage flatStorage;

    @Inject
    public FlatService(FlatStorage flatStorage)
    {
        this.flatStorage = flatStorage;
    }

    public int createFlat(String address, Integer currentTenantId)
    {
        Flat flat = new Flat(address, currentTenantId);
        return flatStorage.save(flat);
    }

    public Flat findById(int id)
    {
        return flatStorage.find(id);
    }
}
