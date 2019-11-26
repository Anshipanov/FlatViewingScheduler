import com.google.inject.Guice;
import com.google.inject.Injector;

public class FlatViewingSchedulerApp
{

    public static void main(String[] args)
    {
        Injector injector = Guice.createInjector(new Configuration());
        injector.getInstance(VertxServer.class).start();
    }

}
