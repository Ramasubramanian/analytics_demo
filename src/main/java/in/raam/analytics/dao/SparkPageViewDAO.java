package in.raam.analytics.dao;

import in.raam.analytics.model.PageView;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.javaFunctions;
import static in.raam.analytics.util.Constants.*;

/**
 * Apache Spark and Cassandra based implementation for fetching PageView analytics data
 * @author ramasubramanian on 07/03/15.
 */
@Configuration
@Repository("sparkPageViewDAO")
public class SparkPageViewDAO implements PageViewDAO {

    @Autowired
    @Qualifier("sparkContext")
    JavaSparkContext sparkContext;

    @Override
    public List<PageView> fetchPageViewsByUser(String userId, int limit) {
        return javaFunctions(sparkContext).cassandraTable(KEY_SPACE_NAME, PAGEVIEW_TABLE_NAME)
                .where(COL_USER_ID + "=?", userId).map(row -> {
                    PageView view = new PageView();
                    view.setUserId(row.getString(COL_USER_ID));
                    view.setViewedAt(new Timestamp(row.getDateTime(COL_VIEWED_AT).toDate().getTime()));
                    view.setPageId(row.getString(COL_PAGE_ID));
                    view.setMetadata(row.getString(COL_METADATA));
                    view.setVisitedPage(row.getString(COL_VISITED_PAGE));
                    return view;
                }).take(limit).stream().collect(Collectors.toList());
    }
}
