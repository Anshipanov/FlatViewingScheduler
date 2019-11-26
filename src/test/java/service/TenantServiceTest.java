package service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import storage.InMemoryTenantStorage;

public class TenantServiceTest
{

    private TenantService tenantService;

    @Before
    public void prepare()
    {
        tenantService = new TenantService(new InMemoryTenantStorage());
    }

    @Test
    public void createTenant()
    {
        String name1 = "Some name 1";
        String name2 = "Some name 2";
        String name3 = "Some name 3";
        Assert.assertEquals("Assigned id has to be 1", 1, tenantService.createTenant(name1));
        Assert.assertEquals("Assigned id has to be 2", 2, tenantService.createTenant(name2));
        Assert.assertEquals("Assigned id has to be 3", 3, tenantService.createTenant(name3));
    }

    @Test
    public void findById()
    {
        String name1 = "Some name 1";
        String name2 = "Some name 2";
        String name3 = "Some name 3";
        tenantService.createTenant(name1);
        tenantService.createTenant(name2);
        tenantService.createTenant(name3);
        Assert.assertEquals(
                "Name of tenant with id = 1 has to be \"Some name 1\"",
                name1,
                tenantService.findById(1).getName()
        );
        Assert.assertEquals(
                "Name of tenant with id = 2 has to be \"Some name 2\"",
                name2,
                tenantService.findById(2).getName()
        );
        Assert.assertEquals(
                "Name of tenant with id = 3 has to be \"Some name 3\"",
                name3,
                tenantService.findById(3).getName()
        );
    }
}