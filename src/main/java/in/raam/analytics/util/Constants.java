package in.raam.analytics.util;

/**
 * @author ramasubramanian on 07/03/15.
 */
public class Constants {
    private Constants() {
    }

    //System properties
    public static final String CASSANDRA_HOST = "spark.cassandra.connection.host";
    public static final String SPARK_APP_NAME = "spark.ctx.app.name";
    public static final String SPARK_MASTER = "spark.ctx.master";
    public static final String SPARK_STREAMING_INTERVAL = "spark.stream.interval";
    public static final String IN_MEMORY_Q_SIZE = "in.memory.q.size";
    public static final String REST_API_PORT = "spring.boot.container.port";

    //DB objects
    public static final String KEY_SPACE_NAME = "dev";
    public static final String PAGEVIEW_TABLE_NAME = "user_page_views";

    //column names
    public static final String COL_USER_ID = "user_id";
    public static final String COL_PAGE_ID = "page_id";
    public static final String COL_VIEWED_AT = "viewed_at";
    public static final String COL_VISITED_PAGE = "visited_page";
    public static final String COL_METADATA = "data";

    //tracking cookie
    public static final String TRACKING_COOKIE_NAME = "analytics.c";
    public static final int TRACKING_COOKIE_LIFE = 60 /*second*/ * 60 /*minute*/ * 24 /*hour*/ * 365 /*day*/ * 2 /*year*/;
}