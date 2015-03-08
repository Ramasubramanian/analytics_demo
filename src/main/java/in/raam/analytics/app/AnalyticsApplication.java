package in.raam.analytics.app;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import in.raam.analytics.dao.PageViewDAO;
import in.raam.analytics.dao.SparkPageViewDAO;
import in.raam.analytics.model.PageView;
import in.raam.analytics.util.DataInputPipe;
import in.raam.analytics.util.InMemoryQueueReceiver;
import in.raam.analytics.util.InMemorySparkStreamDataInputPipe;
import in.raam.analytics.util.SparkHelper;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.concurrent.BlockingQueue;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.mapToRow;
import static in.raam.analytics.util.Constants.*;

/**
 * Main entry point of the Spring boot application, contains the application Spring beans configuration as well.
 * Behavior of application can be modified using below System properties
 * <table border="1">
 *     <tr><th>Property</th><th>Purpose</th></tr>
 *     <tr><td>spring.boot.container.port</td><td>Port in which the REST API service hosted by Spring boot has to run, default is 9000</td></tr>
 *     <tr><td>spark.cassandra.connection.host</td><td>Cassandra host name for Apache spark MapReduce framework to interact with, default is localhost</td></tr>
 *     <tr><td>spark.ctx.app.name</td><td>Application name for Apache Spark Context, default is Analytics Demo</td></tr>
 *     <tr><td>spark.ctx.master</td><td>Master URL to which Apache Spark has to connect to, default local[2]. <br> 2 is the number of worker threads and is the minimum value and ideally same as number of cores in the machine <br> refer https://spark.apache.org/docs/0.8.1/scala-programming-guide.html#master-urls</td></tr>
 *     <tr><td>spark.stream.interval</td><td>Frequency at which as Spark Streaming framework has to ingest or process streamed input data in seconds, default 10. This will reflect in the application latency as well</td></tr>
 *     <tr><td>in.memory.q.size</td><td>Streaming is implemented to use an in-memory BlockingQueue, this parameter specifies the maximum size of the queue. Default is 1000</td></tr>
 * </table>
 * @author ramasubramanian on 06/03/15.
 */
@SpringBootApplication
@Configuration
public class AnalyticsApplication {

    //hack for demo in-memory Queue, will be a Zero MQ or some equivalent distributed queue in actual prod
    public static BlockingQueue<PageView> PAGEVIEW_DATA_QUEUE;


    @Bean(name = "pageViewDataPipe")
    @Scope("singleton")
    public DataInputPipe<PageView> pageViewPipe() {
        DataInputPipe<PageView> ret = new InMemorySparkStreamDataInputPipe<>(sc -> {
            JavaDStream<PageView> jdStream = sc.receiverStream(new InMemoryQueueReceiver<PageView>());
            CassandraJavaUtil.javaFunctions(jdStream).writerBuilder(KEY_SPACE_NAME, PAGEVIEW_TABLE_NAME, mapToRow(PageView.class,
                    Pair.of("userId", COL_USER_ID),
                    Pair.of("pageId", COL_PAGE_ID),
                    Pair.of("viewedAt", COL_VIEWED_AT),
                    Pair.of("metadata", COL_METADATA),
                    Pair.of("visitedPage", COL_VISITED_PAGE))).saveToCassandra();
            sc.start();
        });
        //hack - assign queue to global variable
        PAGEVIEW_DATA_QUEUE = ((InMemorySparkStreamDataInputPipe) ret).queue;
        return ret;
    }

    @Bean(name = "sparkContext")
    @Scope("singleton")
    public JavaSparkContext sparkContext() {
        return SparkHelper.sparkContext();
    }

    @Bean
    @Scope("singleton")
    public PageViewDAO pageViewDAO() {
        return new SparkPageViewDAO();
    }

    public static void main(String[] args) {
        SpringApplication.run(AnalyticsApplication.class, args);
    }
}
