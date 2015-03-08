package in.raam.analytics.util;

/**
 * Abstraction layer that accepts data as a stream and percolates the same to underlying Streaming framework implementation
 * @author ramasubramanian on 06/03/15.
 */
public interface DataInputPipe<E> {
    public void push(E data);

    public void terminate();
}
