package in.raam.analytics.app;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

import static in.raam.analytics.util.Constants.*;

/**
 * Custom interceptor to check and if not exists add Tracking cookie to identify unique users/browsers for this analytics system, cookie name is analytics.c
 * and life time is 2 years. Cookie value is a BASE64 encoded string of a random UUID.
 * @author ramasubramanian on 07/03/15.
 */
public class TrackingCookieInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(TrackingCookieInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String trackingCookieValue = Arrays.stream(request.getCookies())
                .filter(c -> TRACKING_COOKIE_NAME.equalsIgnoreCase(c.getName()))
                .findFirst()
                .map(c -> c.getValue()).orElse("");
        if(Strings.isNullOrEmpty(trackingCookieValue)) {
            generateAndSetTrackingCookie(response);
        }
        return super.preHandle(request, response, handler);
    }

    private void generateAndSetTrackingCookie(HttpServletResponse response) {
        String uuid = UUID.randomUUID().toString();
        String cookieValue = Base64.getEncoder().encodeToString(uuid.getBytes(Charset.defaultCharset()));
        Cookie trackingCookie = new Cookie(TRACKING_COOKIE_NAME, cookieValue);
        trackingCookie.setMaxAge(TRACKING_COOKIE_LIFE);
        response.addCookie(trackingCookie);
        if(logger.isDebugEnabled()) {
            logger.debug(String.format("Added tracking cookie %s with value %s", trackingCookie.getName(), trackingCookie.getValue()));
        }
    }
}
