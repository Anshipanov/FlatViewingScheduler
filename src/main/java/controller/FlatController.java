package controller;

import com.google.inject.Inject;
import entity.Flat;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import request.CreateFlatRequest;
import response.EntityCreatedResponse;
import service.FlatService;

public class FlatController extends Controller
{
    private final FlatService flatService;

    @Inject
    public FlatController(FlatService flatService)
    {
        this.flatService = flatService;
    }

    @Override
    public String getPath()
    {
        return "/flat";
    }

    @Override
    public Router getRouter(Vertx vertx)
    {
        Router router = Router.router(vertx);
        router.post().handler(BodyHandler.create());
        router.post().handler(this::createFlat);
        router.get("/:id").handler(this::getFlat);
        return router;
    }

    private void createFlat(RoutingContext routingContext)
    {
        CreateFlatRequest request = Json.decodeValue(routingContext.getBodyAsString(), CreateFlatRequest.class);
        int flatId = flatService.createFlat(request.getAddress(), request.getCurrentTenantId());
        successResponse(routingContext, new EntityCreatedResponse(flatId));
    }

    private void getFlat(RoutingContext routingContext)
    {
        int flatId = Integer.parseInt(routingContext.request().getParam("id"));
        Flat flat = flatService.findById(flatId);
        if (flat != null)
        {
            successResponse(routingContext, flat);
        }
        else
        {
            notFound(routingContext);
        }
    }
}
