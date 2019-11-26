package service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import storage.InMemoryFlatStorage;

public class FlatServiceTest
{
    private static final int TENANT_ID = 1;

    private FlatService flatService;

    @Before
    public void prepare()
    {
        flatService = new FlatService(new InMemoryFlatStorage());
    }

    @Test
    public void createFlat()
    {
        String address1 = "Some address 1";
        String address2 = "Some address 2";
        String address3 = "Some address 3";
        Assert.assertEquals("Assigned id has to be 1", 1, flatService.createFlat(address1, TENANT_ID));
        Assert.assertEquals("Assigned id has to be 2", 2, flatService.createFlat(address2, TENANT_ID));
        Assert.assertEquals("Assigned id has to be 3", 3, flatService.createFlat(address3, TENANT_ID));
    }

    @Test
    public void findById()
    {
        String address1 = "Some address 1";
        String address2 = "Some address 2";
        String address3 = "Some address 3";
        flatService.createFlat(address1, TENANT_ID);
        flatService.createFlat(address2, TENANT_ID);
        flatService.createFlat(address3, TENANT_ID);
        Assert.assertEquals(
                "Address of flat with id = 1 has to be \"Some address 1\"",
                address1,
                flatService.findById(1).getAddress()
        );
        Assert.assertEquals(
                "Address of flat with id = 2 has to be \"Some address 2\"",
                address2,
                flatService.findById(2).getAddress()
        );
        Assert.assertEquals(
                "Address of flat with id = 3 has to be \"Some address 3\"",
                address3,
                flatService.findById(3).getAddress()
        );
    }
}