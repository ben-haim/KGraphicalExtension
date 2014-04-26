package ag.kge.display;

import ag.kge.control.ModelCache;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by adnan on 25/04/14.
 */
public class RenderingEngine implements Runnable {

    private final LinkedBlockingQueue<Object[]> showQueue;

    public RenderingEngine(LinkedBlockingQueue<Object[]> showQueue) {
        this.showQueue = showQueue;
    }

    @Override
    public void run() {
        while (true) try {
            createAndShow(showQueue.take());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void createAndShow(Object[] message) {

        String name = message[0].toString();

        HashMap<String, Object> description =
                (HashMap<String, Object>) ModelCache.INSTANCE.parseData(message[1]);

    }

}
