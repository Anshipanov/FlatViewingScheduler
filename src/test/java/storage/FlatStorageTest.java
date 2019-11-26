package storage;

import entity.Flat;
import java.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FlatStorageTest
{

    private FlatStorage flatStorage;

    @Before
    public void prepare()
    {
        flatStorage = new InMemoryFlatStorage();
    }

    @Test
    public void save()
    {
        Flat flat1 = new Flat("Some address 1", null);
        Flat flat2 = new Flat("Some address 2", null);
        Flat flat3 = new Flat("Some address 3", null);
        Assert.assertEquals("Assigned id has to be 1", 1, flatStorage.save(flat1));
        Assert.assertEquals("Assigned id has to be 2", 2, flatStorage.save(flat2));
        Assert.assertEquals("Assigned id has to be 3", 3, flatStorage.save(flat3));
    }

    @Test
    public void find()
    {
        String address1 = "Some address 1";
        Flat flat1 = new Flat(address1, null);
        String address2 = "Some address 2";
        Flat flat2 = new Flat(address2, null);
        String address3 = "Some address 3";
        Flat flat3 = new Flat(address3, null);
        flatStorage.save(flat1);
        flatStorage.save(flat2);
        flatStorage.save(flat3);
        Assert.assertEquals(
                "Address of flat with id = 1 has to be \"Some address 1\"",
                address1,
                flatStorage.find(1).getAddress()
        );
        Assert.assertEquals(
                "Address of flat with id = 2 has to be \"Some address 2\"",
                address2,
                flatStorage.find(2).getAddress()
        );
        Assert.assertEquals(
                "Address of flat with id = 3 has to be \"Some address 3\"",
                address3,
                flatStorage.find(3).getAddress()
        );
    }

    @Test
    public void count()
    {
        Assert.assertEquals("Count has to be 0", 0, flatStorage.count());
        Flat flat1 = new Flat("Some address 1", null);
        flatStorage.save(flat1);
        Assert.assertEquals("Count has to be 1", 1, flatStorage.count());
        Flat flat2 = new Flat("Some address 2", null);
        flatStorage.save(flat2);
        Assert.assertEquals("Count has to be 2", 2, flatStorage.count());
        Flat flat3 = new Flat("Some address 3", null);
        flatStorage.save(flat3);
        Assert.assertEquals("Count has to be 3", 3, flatStorage.count());
    }
}