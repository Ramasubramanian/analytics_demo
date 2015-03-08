package in.raam.analytics.util;

import static in.raam.analytics.util.Constants.*;

/**
 * Convenience class to hold application configuration details
 * @author ramasubramanian on 07/03/15.
 */
public class AppConfig {

    public static AppConfig I = init();

    private int webContainerPort;
    private int sparkStreamingInterval;
    private String sparkCtxAppName;
    private String sparkCtxMaster;
    private String cassandraHost;
    private int inMemoryQueueSize;

    private AppConfig() {
    }

    public int webContainerPort() {
        return webContainerPort;
    }

    public int sparkStreamingInterval() {
        return sparkStreamingInterval;
    }

    public String sparkCtxAppName() {
        return sparkCtxAppName;
    }

    public String sparkCtxMaster() {
        return sparkCtxMaster;
    }

    public String cassandraHost() {
        return cassandraHost;
    }

    public int inMemoryQueueSize() {
        return inMemoryQueueSize;
    }

    private static AppConfig init() {
        AppConfig cfg = new AppConfig();
        cfg.cassandraHost = System.getProperty(CASSANDRA_HOST, "localhost");
        cfg.sparkCtxAppName = System.getProperty(SPARK_APP_NAME, "Analytics Demo");
        cfg.sparkCtxMaster = System.getProperty(SPARK_MASTER, "local[2]");
        cfg.sparkStreamingInterval = Integer.parseInt(System.getProperty(SPARK_STREAMING_INTERVAL, "10"));
        cfg.inMemoryQueueSize = Integer.parseInt(System.getProperty(IN_MEMORY_Q_SIZE, "1000"));
        cfg.webContainerPort = Integer.parseInt(System.getProperty(REST_API_PORT, "9000"));
        return cfg;
    }
}
