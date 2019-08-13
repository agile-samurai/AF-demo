package group.u.records.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public static final String USER_TYPE = "user-type";
    private static final  String SECURITY_TOKEN_HEADER = "x-authentication";

    private Logger logger = LoggerFactory.getLogger(JWTSecurityEnhancementFilter.class);
    private Algorithm algorithm;

    public JWTSecurityEnhancementFilter() throws UnsupportedEncodingException {
        algorithm = Algorithm.HMAC256("secret");

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        logger.debug("Authentication:  " + auth.getName() );
//        logger.debug("Authorities:  " + auth.getAuthorities());

        if( auth == null ){
            auth = extractJWT((HttpServletRequest) servletRequest );

        }

        SecurityContextHolder.getContext().setAuthentication(auth);
        setJWTResponseHeader(auth, (HttpServletResponse) servletResponse);

        auth = SecurityContextHolder.getContext().getAuthentication();
        logger.debug("Authentication:  " + auth.getName() );
        filterChain.doFilter(servletRequest,servletResponse);
    }

    private void setJWTResponseHeader(Authentication auth, HttpServletResponse servletResponse) throws UnsupportedEncodingException {
        String header = JWT.create()
                .withIssuer(ISSUER)
                .withClaim(USER_TYPE, "normal")
                .sign(algorithm);

        servletResponse.addHeader(SECURITY_TOKEN_HEADER, header);
    }

    private Authentication extractJWT(HttpServletRequest servletRequest) {
        String jwtContent = servletRequest.getHeader(SECURITY_TOKEN_HEADER);

        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build();
        DecodedJWT jwt = verifier.verify(jwtContent);

        logger.debug("Decoded jwt:  " + jwt.getClaims());

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                "Fake", "Fake", asList(new SimpleGrantedAuthority("ROLE_USER")));

        return authenticationToken;
    }
}
