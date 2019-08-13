package group.u.records.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static java.util.Arrays.asList;

public class JWTSecurityEnhancementFilter implements Filter {

    public static final String ISSUER = "ugroup";
    public static final String ROLES = "roles";
    private static final String SECURITY_TOKEN_HEADER = "x-authentication";

    private Logger logger = LoggerFactory.getLogger(JWTSecurityEnhancementFilter.class);
    private Algorithm algorithm;

    public JWTSecurityEnhancementFilter(@Value("${app.jwt.secret}") String secret ) throws UnsupportedEncodingException {
        algorithm = Algorithm.HMAC256(secret);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            auth = extractJWT((HttpServletRequest) servletRequest);

        }

        SecurityContextHolder.getContext().setAuthentication(auth);
        setJWTResponseHeader(auth, (HttpServletResponse) servletResponse);

        auth = SecurityContextHolder.getContext().getAuthentication();
        logger.debug("Authentication:  " + auth.getName());
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private void setJWTResponseHeader(Authentication auth, HttpServletResponse servletResponse) throws UnsupportedEncodingException {
        String jwt = JWT.create()
                .withIssuer(ISSUER)
                .withSubject(auth.getName())
                .withClaim(ROLES, auth.getAuthorities().toString())
                .sign(algorithm);

        servletResponse.setHeader(SECURITY_TOKEN_HEADER, jwt);
    }

    private Authentication extractJWT(HttpServletRequest servletRequest) {
        String jwtContent = servletRequest.getHeader(SECURITY_TOKEN_HEADER);

        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build();
        DecodedJWT jwt = verifier.verify(jwtContent);
        logger.debug("Decoded jwt:  " + jwt.getClaims());

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                jwt.getSubject(), null, asList(new SimpleGrantedAuthority("ROLE_USER")));

        return authenticationToken;
    }
}
