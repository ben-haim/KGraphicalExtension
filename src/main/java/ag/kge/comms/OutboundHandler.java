package ag.kge.comms;

import ag.kge.c;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by adnan on 25/04/14.
 */
public class OutboundHandler implements Runnable {

    private final c conn;
    private final LinkedBlockingQueue<String> outQueue;

    public OutboundHandler(c conn, LinkedBlockingQueue<String> outQueue) {
        this.conn = conn;
        this.outQueue = outQueue;
    }

    /**
     * Takes outbound messages off the outbound queue and sends them back to the server
     * as a query.
     */
    @Override
    public void run() {

        while (true) try  {
            conn.ks(outQueue.take());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
