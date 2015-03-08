package in.raam.analytics.app.controllers;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Header;
import in.raam.analytics.app.AnalyticsApplication;
import in.raam.analytics.util.AppConfig;
import org.apache.commons.httpclient.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static com.jayway.restassured.RestAssured.*;

/**
 * @author ramasubramanian on 08/03/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AnalyticsApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:9000")
@Category(in.raam.analytics.app.IntegrationTest.class)
public class AnalyticsControllerTest {

   private static final String PAGEID_ERROR =  "pageId is mandatory";
   private static final String USERID_ERROR = "userId is mandatory";

   private String userId() {
       return System.currentTimeMillis() + "@domain.com";
   }

    @Before
    public void setUp() {
        RestAssured.port = 9000;
    }

    @Test
    public void testPushEmptyData() {
        given().body("{}").header("Content-Type","application/json")
                .header("Accept","application/json, text/plain, */*")
                .put("/analytics/pageview")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("errors", Matchers.contains(PAGEID_ERROR, USERID_ERROR));
    }

    @Test
    public void testPushInvalidPageViewData() {
        given().body("{\"userId\" : \""+userId()+"\"}").header("Content-Type","application/json")
                .header("Accept","application/json, text/plain, */*")
                .put("/analytics/pageview")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("errors", Matchers.contains(PAGEID_ERROR));

        given().body("{\"pageId\" : \"homepage\"}").header("Content-Type","application/json")
                .header("Accept","application/json, text/plain, */*")
                .put("/analytics/pageview")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("errors", Matchers.contains(USERID_ERROR));
    }

    @Test
    public void testFetchWithoutLimit() {
        when().get("/analytics/userid/pageviews").then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", Matchers.is("Required int parameter 'limit' is not present"));
    }

    @Test
    public void testFetchPageViewsWithoutPush() {
        int limit = 10;
        String userId = userId();
        given().param("limit", limit).when().get("/analytics/{userId}/pageviews", userId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("userId", Matchers.is(userId))
                .body("pageViews", Matchers.hasSize(0));
    }

    @Test
    public void testFetchPageViewsWithDataPush() {
        String userId = userId();
        int limit = 10;
        for(int i=0; i< limit; i++) {
            given().body("{\"userId\" : \""+userId+"\", \"pageId\" : \"homepage\"}")
                    .header("Content-Type","application/json")
                    .header("Accept","application/json, text/plain, */*")
                    .put("/analytics/pageview")
                    .then()
                    .statusCode(HttpStatus.SC_OK);
        }
        //wait for streaming sync
        try {
            Thread.sleep(AppConfig.I.sparkStreamingInterval() * 2 * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        given().param("limit", 50).when().get("/analytics/{userId}/pageviews", userId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("userId", Matchers.is(userId))
                .body("pageViews", Matchers.hasSize(limit));
    }

}
