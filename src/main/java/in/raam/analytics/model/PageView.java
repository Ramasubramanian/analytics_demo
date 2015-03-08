package in.raam.analytics.model;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by ramasubramanian on 06/03/15.
 */
public class PageView implements Serializable {

    private String userId;
    private String pageId;
    private Timestamp viewedAt;
    private String metadata;
    private String visitedPage;

    public PageView() {
    }

    public PageView(String userId, String pageId, Timestamp viewedAt, String metadata) {
        this.userId = userId;
        this.pageId = pageId;
        this.viewedAt = viewedAt;
        this.metadata = metadata;
        this.visitedPage = pageId;
    }

    public String getUserId() {
        return userId;
    }

    public String getPageId() {
        return pageId;
    }

    @JsonIgnore
    public Timestamp getViewedAt() {
        return viewedAt;
    }

    @JsonIgnore
    public String getMetadata() {
        return metadata;
    }

    @JsonIgnore
    public String getVisitedPage() {
        return visitedPage;
    }

    @JsonIgnore
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    @JsonIgnore
    public void setViewedAt(Timestamp viewedAt) {
        this.viewedAt = viewedAt;
    }

    @JsonIgnore
    public void setVisitedPage(String visitedPage) {
        this.visitedPage = visitedPage;
    }
}

