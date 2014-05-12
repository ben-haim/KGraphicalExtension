package ag.kge.comms;

import ag.kge.c;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Sends all outbound messages from the system.
 */
public class OutboundHandler implements Runnable {

    private final c conn;
    private final LinkedBlockingQueue<String> outQueue;

    public OutboundHandler(c conn, LinkedBlockingQueue<String> outQueue) {
        this.conn = conn;
        this.outQueue = outQueue;
    }

    /**
     * Takes messages off the outbound queue and sends them back to the server
     * as a query.
     */
    @Override
    public void run() {
        while (true) try  {
            String out = outQueue.take();
            conn.ks(out); //send query
        } catch (IOException | InterruptedException e) {
            //if the thread is interrupted or the connection is lost, exit
            System.exit(1);
        }
    }
}
