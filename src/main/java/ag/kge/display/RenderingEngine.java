package ag.kge.display;

import ag.kge.control.FrameCache;
import ag.kge.control.ModelCache;
import ag.kge.display.controllers.PanelController;

import javax.swing.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by adnan on 25/04/14.
 */
public class RenderingEngine implements Runnable {

    private final LinkedBlockingQueue<Object[]> showQueue;
    private final LinkedBlockingQueue<String> outQueue;

    public RenderingEngine(LinkedBlockingQueue<Object[]> showQueue,
                           LinkedBlockingQueue<String> outQueue) {
        this.showQueue = showQueue;
        this.outQueue = outQueue;
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

        String widgetClass = "text";
        if (description.containsKey("class")){
            widgetClass = description.get("class").toString();
        }

        JFrame frame = new JFrame();

        switch (widgetClass){
            case "panel":
                description.put("data", ModelCache.INSTANCE.getData(
                        description.get("binding").toString()));
                frame.setContentPane(new PanelController(description, outQueue));
                break;
            default: break;
        }

        FrameCache.INSTANCE.addFrame(name,frame);



    }

}
