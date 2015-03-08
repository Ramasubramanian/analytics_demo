package in.raam.analytics.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;
import java.util.List;


/**
 * @author ramasubramanian on 07/03/15.
 */
public class UserPageViews {

    public static class UserPageView {
        public String pageId;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
        public Timestamp viewedAt;

        public UserPageView(Timestamp viewedAt, String pageId) {
            this.viewedAt = viewedAt;
            this.pageId = pageId;
        }
    }

    public String userId;
    public List<UserPageView> pageViews;
}
