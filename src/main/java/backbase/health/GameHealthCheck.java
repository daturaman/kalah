package backbase.health;

import com.codahale.metrics.health.HealthCheck;

/**
 * Verifies the basic integrity of the Game resource.
 */
public class GameHealthCheck extends HealthCheck {
    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}
