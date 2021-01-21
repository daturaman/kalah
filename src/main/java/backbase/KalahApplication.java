package backbase;

import backbase.health.GameHealthCheck;
import backbase.resources.GameResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class KalahApplication extends Application<KalahConfiguration> {

    public static void main(final String[] args) throws Exception {
        new KalahApplication().run(args);
    }

    @Override
    public String getName() {
        return "kalah";
    }

    @Override
    public void initialize(final Bootstrap<KalahConfiguration> bootstrap) {
    }

    @Override
    public void run(final KalahConfiguration configuration,
            final Environment environment) {
        final GameResource resource = new GameResource();
        final GameHealthCheck healthCheck = new GameHealthCheck();
        environment.jersey().register(resource);
        environment.jersey().register(healthCheck);
    }
}
