package ag.kge.control;

import ag.kge.c;

import java.lang.reflect.Array;
import java.util.HashMap;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by adnan on 25/04/14.
 */
public class ShowHandler implements Runnable {

    private final LinkedBlockingQueue<Object[]> showQueue;
    private final LinkedBlockingQueue<String> outQueue;

    public ShowHandler(LinkedBlockingQueue<Object[]> showQueue,
                       LinkedBlockingQueue<String> outQueue) {
        this.showQueue = showQueue;
        this.outQueue = outQueue;
    }

    @Override
    public void run() {
        while (true) try {
            Object[] message = showQueue.take();
            FrameCache.INSTANCE.createAndShow(
                    parseShowMessage(message[0].toString(),
                            (c.Dict) message[1]),
                    outQueue
            );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Parses a c.Dict into a pre-formatted hashmap, defaulting values for class and label.
     *
     * @param name
     * @param infoDict
     * @return
     */
    private HashMap<String,Object> parseShowMessage(String name, c.Dict infoDict){
        HashMap<String, Object> description = new HashMap<>();
        description.put("name", name);
        int i = 0;
        if (c.at(infoDict.x,0) == "") i++;
        String currentX; Object currentY;

        for (; i < Array.getLength(infoDict.x); i++){

            currentX = c.at(infoDict.x,i).toString();
            currentY = c.at(infoDict.y,i);

            switch (currentX){
                case "c": description.put("class", currentY);
                case "l": description.put("label", currentY);
                    break;
                case "b":
                    description.put("binding",currentY);
                    //also grabs data from model cache
                    description.put("data",ModelCache.INSTANCE.getData(currentY.toString()));
                    break;
                case "w": description.put("width",currentY);
                    break;
                case "h": description.put("height", currentY);
                    break;
                case "x": description.put("x", currentY);
                    break;
                case "y": description.put("y",currentY);
                    break;
                //any other attributes can be added later
                default:
                    if (currentY instanceof c.Dict) // if it's only some name, ignore it
                        description.put(currentX, parseShowMessage(currentX,(c.Dict)currentY));
            }
        }

        if (!description.containsKey("class"))
            description.put("class","data");

        if (!description.containsKey("label"))
            if (description.containsKey("binding")) //1st default label is binding name
                description.put("label", description.get("binding").toString());
            else  //2nd default label is widget name
                description.put("label", name);

        return description;
    }



}
