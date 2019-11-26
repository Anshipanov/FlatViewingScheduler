package controller;

import com.google.inject.Inject;
import entity.ViewReservation;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import java.time.LocalDateTime;
import request.CreateViewReservationRequest;
import response.CreateViewReservationResponse;
import service.ViewReservationService;

public class ViewReservationController extends Controller
{
    private final ViewReservationService viewReservationService;

    @Inject
    public ViewReservationController(ViewReservationService viewReservationService)
    {
        this.viewReservationService = viewReservationService;
    }

    @Override
    public String getPath()
    {
        return "/reservation";
    }

    @Override
    public Router getRouter(Vertx vertx)
    {
        Router router = Router.router(vertx);
        router.get("/:id").handler(this::getReservation);
        router.post("/:id/approve").handler(this::approveReservation);
        router.post("/:id/reject").handler(this::rejectReservation);
        router.post("/:id/cancel").handler(this::cancelReservation);
        router.post().handler(BodyHandler.create());
        router.post().handler(this::createReservation);
        return router;
    }

    private void createReservation(RoutingContext routingContext)
    {
        CreateViewReservationRequest request = Json.decodeValue(
                routingContext.getBodyAsString(),
                CreateViewReservationRequest.class
        );
        CreateViewReservationResponse response = viewReservationService.createReservation(
                request.getFlatId(), request.getTenantId(), LocalDateTime.parse(request.getStartTime())
        );
        successResponse(routingContext, response);
    }

    private void getReservation(RoutingContext routingContext)
    {
        int reservationId = Integer.parseInt(routingContext.request().getParam("id"));
        ViewReservation reservation = viewReservationService.findById(reservationId);
        if (reservation != null)
        {
            successResponse(routingContext, reservation);
        }
        else
        {
            notFound(routingContext);
        }
    }

    private void approveReservation(RoutingContext routingContext)
    {
        int reservationId = Integer.parseInt(routingContext.request().getParam("id"));
        boolean result = viewReservationService.approveReservation(reservationId);
        if (result)
        {
            success(routingContext);
        }
        else
        {
            badRequest(routingContext);
        }
    }

    private void rejectReservation(RoutingContext routingContext)
    {
        int reservationId = Integer.parseInt(routingContext.request().getParam("id"));
        boolean result = viewReservationService.rejectReservation(reservationId);
        if (result)
        {
            success(routingContext);
        }
        else
        {
            badRequest(routingContext);
        }
    }

    private void cancelReservation(RoutingContext routingContext)
    {
        int reservationId = Integer.parseInt(routingContext.request().getParam("id"));
        boolean result = viewReservationService.cancelReservation(reservationId);
        if (result)
        {
            success(routingContext);
        }
        else
        {
            badRequest(routingContext);
        }
    }
}
