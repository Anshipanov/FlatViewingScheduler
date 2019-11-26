package integration;

import entity.Flat;
import entity.Tenant;
import entity.ViewReservation;
import io.vertx.core.json.Json;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import request.CreateFlatRequest;
import request.CreateTenantRequest;
import request.CreateViewReservationRequest;
import response.CreateViewReservationResponse;
import response.EntityCreatedResponse;
import response.ViewReservationValidationStatus;

public class CreateReservationIT
{
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd\'T\'HH:mm");

    @ClassRule
    public static GenericContainer flatViewingScheduler = new GenericContainer("flat-viewing-scheduler")
            .withExposedPorts(8080);

    @Test
    public void testCreateReservation()
    {
        testCreateTenant();
        testCreateFlat();
        testCreateReservationFlatNotExists();
        testCreateReservationTenantNotExists();
        testCreateReservationNotValidStartTime();
        int reservationId = testCreateReservationSuccess();
        testCreateReservationTimeAlreadyReserved();
        testApproveReservation(reservationId);
        testRejectReservation(reservationId);
        testCancelReservation(reservationId);
    }

    private void testCreateTenant()
    {
        int id = createTenant("Some name 1");
        Assert.assertEquals("Some name 1", getTenant(id).getName());
    }

    private void testCreateFlat()
    {
        int id = createFlat("Some address 1", 1);
        Assert.assertEquals("Some address 1", getFlat(id).getAddress());
    }

    private void testCreateReservationFlatNotExists()
    {
        CreateViewReservationResponse response = createReservation(2, 1, "2019-12-02T12:00");
        Assert.assertEquals(ViewReservationValidationStatus.FLAT_NOT_EXISTS, response.getStatus());
    }

    private void testCreateReservationTenantNotExists()
    {
        CreateViewReservationResponse response = createReservation(1, 2, "2019-12-02T12:00");
        Assert.assertEquals(ViewReservationValidationStatus.TENANT_NOT_EXISTS, response.getStatus());
    }

    private void testCreateReservationNotValidStartTime()
    {
        String startTime = LocalDateTime.now().format(FORMATTER);
        CreateViewReservationResponse response = createReservation(1, 1, startTime);
        Assert.assertEquals(ViewReservationValidationStatus.NOT_VALID_START_TIME, response.getStatus());
    }

    private int testCreateReservationSuccess()
    {
        String startTime = LocalDateTime.now().plusDays(7).withHour(12).withMinute(0).format(FORMATTER);
        CreateViewReservationResponse response = createReservation(1, 1, startTime);
        Assert.assertEquals(ViewReservationValidationStatus.OK, response.getStatus());
        return response.getId();
    }

    private void testCreateReservationTimeAlreadyReserved()
    {
        String startTime = LocalDateTime.now().plusDays(7).withHour(12).withMinute(0).format(FORMATTER);
        CreateViewReservationResponse response = createReservation(1, 1, startTime);
        Assert.assertEquals(ViewReservationValidationStatus.TIME_ALREADY_RESERVED, response.getStatus());
    }

    private void testApproveReservation(int id)
    {
        int statusCode = approveReservation(id);
        Assert.assertEquals(200, statusCode);
    }

    private void testRejectReservation(int id)
    {
        int statusCode = rejectReservation(id);
        Assert.assertEquals(400, statusCode);
    }

    private void testCancelReservation(int id)
    {
        int statusCode = cancelReservation(id);
        Assert.assertEquals(200, statusCode);
    }


    private int createTenant(String name)
    {
        return doPost("/tenant/", new CreateTenantRequest(name), EntityCreatedResponse.class).getId();
    }

    private Tenant getTenant(int id)
    {
        return doGet("/tenant/" + id, Tenant.class);
    }

    private int createFlat(String address, int currentTenantId)
    {
        return doPost("/flat/", new CreateFlatRequest(address, currentTenantId), EntityCreatedResponse.class).getId();
    }

    private Flat getFlat(int id)
    {
        return doGet("/flat/" + id, Flat.class);
    }

    private CreateViewReservationResponse createReservation(int flatId, int tenantId, String startTime)
    {
        return doPost(
                "/reservation",
                new CreateViewReservationRequest(flatId, tenantId, startTime),
                CreateViewReservationResponse.class
        );
    }

    private ViewReservation getViewReservation(int id)
    {
        return doGet("/reservation/" + id, ViewReservation.class);
    }

    private int approveReservation(int id)
    {
        return doPost("/reservation/" + id + "/approve");
    }

    private int rejectReservation(int id)
    {
        return doPost("/reservation/" + id + "/reject");
    }

    private int cancelReservation(int id)
    {
        return doPost("/reservation/" + id + "/cancel");
    }

    private <T> T doGet(String path, Class<T> responseClass)
    {
        try (CloseableHttpClient httpClient = HttpClients.createMinimal())
        {
            HttpGet request = new HttpGet();
            request.setURI(
                    URI.create(
                            "http://" + flatViewingScheduler.getContainerIpAddress() + ":"
                                    + flatViewingScheduler.getMappedPort(8080) + path
                    )
            );

            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() == 200)
            {
                return Json.decodeValue(EntityUtils.toString(response.getEntity(), "UTF-8"), responseClass);
            }
            else
            {
                return null;
            }
        }
        catch (Exception ignored)
        {
            return null;
        }
    }

    private int doPost(String path)
    {
        try (CloseableHttpClient httpClient = HttpClients.createMinimal())
        {
            HttpPost request = new HttpPost();
            request.setURI(
                    URI.create(
                            "http://" + flatViewingScheduler.getContainerIpAddress() + ":"
                                    + flatViewingScheduler.getMappedPort(8080) + path
                    )
            );

            return httpClient.execute(request).getStatusLine().getStatusCode();
        } catch (Exception ignored) {
            return -1;
        }
    }
    
    private <B, R> R doPost(String path, B body, Class<R> responseClass)
    {
        try (CloseableHttpClient httpClient = HttpClients.createMinimal())
        {
            HttpPost request = new HttpPost();
            request.addHeader(
                    HttpHeaders.CONTENT_TYPE,
                    ContentType.APPLICATION_JSON.withCharset(StandardCharsets.UTF_8).toString()
            );
            request.setEntity(new StringEntity(Json.encode(body)));
            request.setURI(
                    URI.create(
                            "http://" + flatViewingScheduler.getContainerIpAddress() + ":"
                                    + flatViewingScheduler.getMappedPort(8080) + path
                    )
            );

            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() == 200)
            {
                return Json.decodeValue(EntityUtils.toString(response.getEntity(), "UTF-8"), responseClass);
            }
            else
            {
                return null;
            }
        }
        catch (Exception ignored)
        {
            return null;
        }
    }
}
