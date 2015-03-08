package in.raam.analytics.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Class to wire up interceptors for each Http request to Spring MVC to implement generic functionality
 * @author ramasubramanian on 07/03/15.
 */
@Configuration
@EnableWebMvc
@ComponentScan
public class AnalyticsApplicationConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TrackingCookieInterceptor()).addPathPatterns("/**");;
    }
}
