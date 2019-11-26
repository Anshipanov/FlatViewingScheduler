package storage;

import entity.Flat;

public interface FlatStorage
{
    int save(Flat flat);
    Flat find(int id);
    int count();
}
