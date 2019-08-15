package group.u.records.config.security;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.security.test.context.support.WithUserDetails;

public class JWTSecurityEnhancementFilterTest {

    @Test
    public void shouldGenerateJWTWhenBasicAuthIsPresent(){

    }

    @Test
    public void shouldGenerateSessionWhenValidJWTIsPresent(){

    }

    @Test
    public void shouldReturnInvalidJWTIfJWTISInvalidStructually(){

    }

    @Test
    @Ignore
    public void shouldReturnInvalidSessionWhenJWTSessionHasExpired(){

    }
}
