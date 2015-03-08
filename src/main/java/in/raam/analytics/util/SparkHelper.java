package in.raam.analytics.util;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Seconds;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import static in.raam.analytics.util.Constants.CASSANDRA_HOST;

/**
 * Helper class to create Apache Spark configurations and streaming context objects based on system properties
 * @author ramasubramanian on 07/03/15.
 */
public class SparkHelper {

    private static final JavaSparkContext sparkContext = initSparkContext();

    public static JavaStreamingContext streamingContext() {
        int interval = AppConfig.I.sparkStreamingInterval();
        JavaStreamingContext streamingContext = new JavaStreamingContext(sparkContext(), Seconds.apply(interval));
        return streamingContext;
    }

    private static SparkConf sparkConf() {
        SparkConf conf = new SparkConf();
        conf.setAppName(AppConfig.I.sparkCtxAppName());
        conf.setMaster(AppConfig.I.sparkCtxMaster());
        conf.set(CASSANDRA_HOST, AppConfig.I.cassandraHost());
        return conf;
    }

    private static JavaSparkContext initSparkContext() {
        return new JavaSparkContext(sparkConf());
    }

    public static JavaSparkContext sparkContext() {
        return sparkContext;
    }
}
