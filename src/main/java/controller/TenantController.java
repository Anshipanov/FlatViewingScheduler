package controller;

import com.google.inject.Inject;
import entity.Tenant;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import request.CreateTenantRequest;
import response.EntityCreatedResponse;
import service.TenantService;

public class TenantController extends Controller
{
    private final TenantService tenantService;

    @Inject
    public TenantController(TenantService tenantService)
    {
        this.tenantService = tenantService;
    }

    @Override
    public String getPath()
    {
        return "/tenant";
    }

    @Override
    public Router getRouter(Vertx vertx)
    {
        Router router = Router.router(vertx);
        router.post().handler(BodyHandler.create());
        router.post().handler(this::createTenant);
        router.get("/:id").handler(this::getTenant);
        return router;
    }

    private void createTenant(RoutingContext routingContext)
    {
        CreateTenantRequest request = Json.decodeValue(routingContext.getBodyAsString(), CreateTenantRequest.class);
        int tenantId = tenantService.createTenant(request.getName());
        successResponse(routingContext, new EntityCreatedResponse(tenantId));
    }

    private void getTenant(RoutingContext routingContext)
    {
        int tenantId = Integer.parseInt(routingContext.request().getParam("id"));
        Tenant tenant = tenantService.findById(tenantId);
        if (tenant != null)
        {
            successResponse(routingContext, tenant);
        }
        else
        {
            notFound(routingContext);
        }
    }
}
