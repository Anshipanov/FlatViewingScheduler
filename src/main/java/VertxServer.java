import com.google.inject.Inject;
import controller.Controller;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import java.util.Set;

public class VertxServer
{
    private final Set<Controller> controllers;

    @Inject
    public VertxServer(Set<Controller> controllers)
    {
        this.controllers = controllers;
    }

    public void start()
    {
        Vertx vertx = Vertx.vertx();
        HttpServer server = vertx.createHttpServer();
        Router mainRouter = Router.router(vertx);

        controllers.forEach(controller -> mainRouter.mountSubRouter(controller.getPath(), controller.getRouter(vertx)));

        server.requestHandler(mainRouter).listen(8080);
    }
}
