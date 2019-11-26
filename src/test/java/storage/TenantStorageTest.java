package storage;

import entity.Tenant;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TenantStorageTest
{

    private TenantStorage tenantStorage;

    @Before
    public void prepare()
    {
        tenantStorage = new InMemoryTenantStorage();
    }

    @Test
    public void find()
    {
        String name1 = "Some name 1";
        Tenant tenant1 = new Tenant(name1);
        String name2 = "Some name 2";
        Tenant tenant2 = new Tenant(name2);
        String name3 = "Some name 3";
        Tenant tenant3 = new Tenant(name3);
        tenantStorage.save(tenant1);
        tenantStorage.save(tenant2);
        tenantStorage.save(tenant3);
        Assert.assertEquals(
                "Name of tenant with id = 1 has to be \"Some name 1\"",
                name1,
                tenantStorage.find(1).getName()
        );
        Assert.assertEquals(
                "Name of tenant with id = 2 has to be \"Some name 2\"",
                name2,
                tenantStorage.find(2).getName()
        );
        Assert.assertEquals(
                "Name of tenant with id = 3 has to be \"Some name 3\"",
                name3,
                tenantStorage.find(3).getName()
        );
    }

    @Test
    public void save()
    {

        Tenant tenant1 = new Tenant("Some name 1");
        Tenant tenant2 = new Tenant("Some name 2");
        Tenant tenant3 = new Tenant("Some name 3");
        Assert.assertEquals("Assigned id has to be 1", 1, tenantStorage.save(tenant1));
        Assert.assertEquals("Assigned id has to be 2", 2, tenantStorage.save(tenant2));
        Assert.assertEquals("Assigned id has to be 3", 3, tenantStorage.save(tenant3));
    }

    @Test
    public void count()
    {
        Assert.assertEquals("Count has to be 0", 0, tenantStorage.count());
        Tenant tenant1 = new Tenant("Some name 1");
        tenantStorage.save(tenant1);
        Assert.assertEquals("Count has to be 1", 1, tenantStorage.count());
        Tenant tenant2 = new Tenant("Some name 2");
        tenantStorage.save(tenant2);
        Assert.assertEquals("Count has to be 2", 2, tenantStorage.count());
        Tenant tenant3 = new Tenant("Some name 3");
        tenantStorage.save(tenant3);
        Assert.assertEquals("Count has to be 3", 3, tenantStorage.count());
    }
}