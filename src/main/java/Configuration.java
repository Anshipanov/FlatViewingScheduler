import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import controller.Controller;
import controller.FlatController;
import controller.TenantController;
import controller.ViewReservationController;
import java.time.Clock;
import java.util.Set;
import notification.NotificationService;
import notification.StubNotificationService;
import service.FlatService;
import service.TenantService;
import service.ViewReservationService;
import storage.FlatStorage;
import storage.InMemoryFlatStorage;
import storage.InMemoryTenantStorage;
import storage.InMemoryViewReservationStorage;
import storage.TenantStorage;
import storage.ViewReservationStorage;

public class Configuration extends AbstractModule
{
    public void configure() {
        Multibinder<Controller> controllers = Multibinder.newSetBinder(binder(), Controller.class);
        controllers.addBinding().to(FlatController.class);
        controllers.addBinding().to(TenantController.class);
        controllers.addBinding().to(ViewReservationController.class);
    }

    @Provides
    @Singleton
    public FlatStorage flatStorage()
    {
        return new InMemoryFlatStorage();
    }

    @Provides
    @Singleton
    public TenantStorage tenantStorage()
    {
        return new InMemoryTenantStorage();
    }

    @Provides
    @Singleton
    public ViewReservationStorage viewReservationStorage()
    {
        return new InMemoryViewReservationStorage();
    }

    @Provides
    @Singleton
    public FlatService flatService(FlatStorage flatStorage)
    {
        return new FlatService(flatStorage);
    }

    @Provides
    @Singleton
    public TenantService tenantService(TenantStorage tenantStorage)
    {
        return new TenantService(tenantStorage);
    }

    @Provides
    @Singleton
    public NotificationService notificationService()
    {
        return new StubNotificationService();
    }

    @Provides
    @Singleton
    public Clock clock()
    {
        return Clock.systemDefaultZone();
    }

    @Provides
    @Singleton
    public ViewReservationService viewReservationService(
            ViewReservationStorage viewReservationStorage,
            TenantService tenantService,
            FlatService flatService,
            NotificationService notificationService,
            Clock clock)
    {
        return new ViewReservationService(viewReservationStorage, tenantService, flatService, notificationService, clock);
    }

    @Provides
    @Singleton
    public FlatController flatController(FlatService flatService)
    {
        return new FlatController(flatService);
    }

    @Provides
    @Singleton
    public TenantController tenantController(TenantService tenantService)
    {
        return new TenantController(tenantService);
    }

    @Provides
    @Singleton
    public ViewReservationController viewReservationController(ViewReservationService viewReservationService)
    {
        return new ViewReservationController(viewReservationService);
    }

    @Provides
    @Singleton
    public VertxServer vertxServer(Set<Controller> controllers)
    {
        return new VertxServer(controllers);
    }
}
