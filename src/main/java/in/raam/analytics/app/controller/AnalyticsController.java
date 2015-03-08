package in.raam.analytics.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import in.raam.analytics.dao.PageViewDAO;
import in.raam.analytics.model.ErrorResponse;
import in.raam.analytics.model.PageView;
import in.raam.analytics.model.UserPageViews;
import in.raam.analytics.util.DataInputPipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static in.raam.analytics.util.Constants.*;
/**
 * REST API controller to handle pageview analytics HTTP requests
 * @author ramasubramanian on 06/03/15.
 */
@RestController
public class AnalyticsController {

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsController.class);

    @Autowired
    @Qualifier("pageViewDataPipe")
    private DataInputPipe<PageView> pageViewDataInputPipe;

    @Autowired
    private PageViewDAO pageViewDAO;

    @RequestMapping(value = "/analytics/pageview", method = RequestMethod.PUT)
    public ResponseEntity<String> inputData(@RequestBody PageView pageViewData,
                                            @CookieValue(value = TRACKING_COOKIE_NAME, defaultValue = "") String analyticsCookie,
                                            HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<String> errors = validatePageViewData(pageViewData);
        if(errors.isEmpty()) {
            ObjectMapper mapper = new ObjectMapper();
            pageViewData.setViewedAt(new Timestamp(System.currentTimeMillis()));
            pageViewData.setVisitedPage(pageViewData.getPageId());
            Map<String,String> metadata = extractMetaData(request);
            if(!Strings.isNullOrEmpty(analyticsCookie)) {metadata.put("trackingCookie", analyticsCookie);}
            pageViewData.setMetadata(mapper.writeValueAsString(metadata));
            logPageViewData(pageViewData);
            pageViewDataInputPipe.push(pageViewData);
            return ResponseEntity.ok().body("Success");
        } else {
            return ResponseEntity.badRequest().body(new ObjectMapper().writeValueAsString(new ErrorResponse(errors, request)));
        }
    }

    private List<String> validatePageViewData(PageView pageView) {
        List<String> errors = new ArrayList<>();
        if(Strings.isNullOrEmpty(pageView.getPageId())) {
            errors.add("pageId is mandatory");
        }
        if(Strings.isNullOrEmpty(pageView.getUserId())) {
            errors.add("userId is mandatory");
        }
        return errors;
    }

    private Map<String,String> extractMetaData(HttpServletRequest request) {
        Map<String, String> metadataMap = new HashMap<>();
        metadataMap.put("userAgent", request.getHeader("User-Agent"));
        metadataMap.put("remoteAddress", request.getRemoteAddr());
        metadataMap.put("referrer", request.getHeader("referer"));
        return metadataMap;
    }

    private void logPageViewData(PageView pageView) {
        if(logger.isDebugEnabled()) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                logger.debug("Pushing pageview data", mapper.writeValueAsString(pageView));
            } catch (JsonProcessingException e) {
                //do nothing
            }
        }
    }

    @RequestMapping(value = "/analytics/{userId}/pageviews", method = RequestMethod.GET)
    public UserPageViews getPageViewsPerUser(@PathVariable String userId, @RequestParam int limit) {
        if(logger.isDebugEnabled()) {
            logger.debug(String.format("Querying for user %s with limit %d", userId, limit));
        }
        UserPageViews userPageViews = new UserPageViews();
        userPageViews.userId = userId;
        userPageViews.pageViews = pageViewDAO.fetchPageViewsByUser(userId, limit).stream()
                .map(pv -> new UserPageViews.UserPageView(pv.getViewedAt(), pv.getPageId()))
                .collect(Collectors.toList());
        return userPageViews;
    }

}
