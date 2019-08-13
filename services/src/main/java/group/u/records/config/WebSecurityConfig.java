package group.u.records.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static java.util.Arrays.asList;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{

    private Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);
    @Value("${app.business.user.username}")
    private String businessUserName;
    @Value("${app.business.user.password}")
    private String businessUserPassword;

    @Value("${app.business.supervisor.username}")
    private String businessSupervisorUserName;
    @Value("${app.business.supervisor.password}")
    private String businessSupervisorPassword;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();


        http.addFilterAfter(new JWTSecurityEnhancementFilter(), BasicAuthenticationFilter.class);
        http.addFilterAfter(new JWTSecurityEnhancementFilter(), BasicAuthenticationFilter.class);
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {

        logger.debug("Busines User username;  " + businessSupervisorUserName );
        return new InMemoryUserDetailsManager(asList(
                User.withDefaultPasswordEncoder()
                        .username(businessUserName)
                        .password(businessUserPassword)
                        .roles("USER")
                        .build(),
                User.withDefaultPasswordEncoder()
                        .username(businessSupervisorUserName)
                        .password(businessSupervisorPassword)
                        .roles("SUPERVISOR")
                        .build()
        ));
    }
}
