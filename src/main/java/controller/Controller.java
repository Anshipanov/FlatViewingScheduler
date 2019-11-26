package controller;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public abstract class Controller
{
    public abstract String getPath();
    public abstract Router getRouter(Vertx vertx);

    protected <T> void successResponse(RoutingContext routingContext, T response)
    {
        routingContext.response()
                .setStatusCode(200)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(response));
    }

    protected void success(RoutingContext routingContext)
    {
        routingContext.response().setStatusCode(200).end();
    }

    protected void notFound(RoutingContext routingContext)
    {
        routingContext.response().setStatusCode(404).end();
    }

    protected void badRequest(RoutingContext routingContext)
    {
        routingContext.response().setStatusCode(400).end();
    }
}
