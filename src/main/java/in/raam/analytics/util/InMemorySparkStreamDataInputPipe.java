package in.raam.analytics.util;

import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

/**
 * Custom {@link in.raam.analytics.util.DataInputPipe} implementation that uses Spark Streaming framework to ingest data
 * into underlying distributed storage
 * @author ramasubramanian on 06/03/15.
 */
public class InMemorySparkStreamDataInputPipe<E> implements DataInputPipe<E> {

    private static final Logger logger = LoggerFactory.getLogger(InMemorySparkStreamDataInputPipe.class);

    private JavaStreamingContext streamingContext = SparkHelper.streamingContext();
    public final BlockingQueue<E> queue = new LinkedBlockingQueue<>(AppConfig.I.inMemoryQueueSize());

    public InMemorySparkStreamDataInputPipe(Consumer<JavaStreamingContext> streamInitializer) {
        new Thread() {
            @Override
            public void run() {
                streamInitializer.accept(streamingContext);
            }
        }.start();
    }


    @Override
    public void push(E data) {
        try {
            logger.info("Pushing object to stream!");
            queue.put(data);
        } catch (InterruptedException e) {
            logger.error("Error occurred while pushing data to pipe!", e);
        }
    }

    @Override
    public void terminate() {
        streamingContext.stop(true);
        logger.info("Stopping data input pipe!");
    }
}
