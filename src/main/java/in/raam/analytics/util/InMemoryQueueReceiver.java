package in.raam.analytics.util;

import in.raam.analytics.app.AnalyticsApplication;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.receiver.Receiver;

import java.util.concurrent.BlockingQueue;

/**
 * Custom {@link org.apache.spark.streaming.receiver.Receiver} implementation which uses an in-memory BlockingQueue
 * to receive data as opposed to plain sockets or files etc. This is only for demo purpose and real time implementation
 * should use a distributed queue implementation like ZeroMQ etc.
 * @author ramasubramanian on 06/03/15.
 */
public class InMemoryQueueReceiver<T> extends Receiver<T> {

    public InMemoryQueueReceiver() {
        super(StorageLevel.MEMORY_AND_DISK_2());
    }

    @Override
    public void onStart() {
        new Thread() {
            @Override
            public void run() {
                receive();
            }
        }.start();
    }

    @Override
    public void onStop() {

    }

    private void receive() {
        BlockingQueue<T> queue = (BlockingQueue<T>) AnalyticsApplication.PAGEVIEW_DATA_QUEUE;
        try {
            while (!isStopped()) {
                T element = queue.take();
                store(element);
            }
            restart("Trying to connect again");
        } catch (InterruptedException e) {
            restart("Restarting receiver due to error!", e);
        }
    }
}
