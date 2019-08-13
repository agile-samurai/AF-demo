package group.u.records.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTSecurityEnhancementFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(JWTSecurityEnhancementFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.debug("Authentication:  " + auth.getName() );

        if( auth != null ){
            Algorithm algorithm = Algorithm.HMAC256("secret");
            String header = JWT.create()
                    .withIssuer("auth0")
                    .withClaim( "user-type", "normal")
                    .sign(algorithm);

            ((HttpServletResponse)servletResponse).addHeader("x-authentication", header);
        }
    }
}
