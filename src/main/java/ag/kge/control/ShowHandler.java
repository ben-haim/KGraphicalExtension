package ag.kge.control;

import ag.kge.c;
import ag.kge.display.FrameCache;

import java.lang.reflect.Array;

import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by adnan on 25/04/14.
 */
public class ShowHandler implements Runnable {

    private final LinkedBlockingQueue<Object[]> showQueue;
    private final LinkedBlockingQueue<TreeMap> templateQueue;

    public ShowHandler(LinkedBlockingQueue<Object[]> showQueue,
                       LinkedBlockingQueue<TreeMap> templateQueue) {
        this.showQueue = showQueue;
        this.templateQueue = templateQueue;
    }

    @Override
    public void run() {
        while (true) try {
            Object[] message = showQueue.take();

            templateQueue.put( //send it to createAndShow after parsing.
                parseShowMessage(message[0].toString(),
                        (c.Dict) message[1])
            );
        } catch (InterruptedException e) {
            System.exit(1);
        }
    }


    /**
     * Parses a c.Dict into a pre-formatted hashmap, defaulting values for class, label, etc..
     *
     * @param name
     * @param infoDict
     * @return
     */
    public TreeMap<String, Object> parseShowMessage(String name, c.Dict infoDict) {



        TreeMap<String, Object> template = new TreeMap<>();
        template.put("name", name);
        int i = 0;
        if (Array.get(infoDict.x, 0).toString().equals("")) i = 1;

        String currentX;
        Object currentY;

        //pre-format
        template.put("value", new String[]{}); //puts some blank data that can't be displayed by text controllers
        template.put("class", "data"); //sets default class to data
        template.put("width", 1);
        template.put("height", 1);
        template.put("x", 0); //gbc defaults x and y to -1
        template.put("y", 0);

        for (; i < Array.getLength(infoDict.x); i++) {

            currentX = c.at(infoDict.x, i).toString();
            currentY = c.at(infoDict.y, i);

            switch (currentX) {
                case "c":
                    template.put("class", currentY);
                    break;
                case "l":
                    if (currentY instanceof String)
                        template.put("label", currentY);
                    else if (currentY instanceof char[])
                        template.put("label", new String((char[]) currentY));
                    else
                        System.out.println("Error: attribute type (l)");
                    break;
                case "b":
                    if (currentY instanceof String) {
                        template.put("binding", currentY);
                    } else if (currentY instanceof char[]) {
                        template.put("binding", new String((char[]) currentY));
                    }else{
                        System.out.println("Error: attribute type (b)");
                    }
                    break;
                case "w":
                    if ((currentY instanceof Integer) || (currentY instanceof Long))
                        template.put("width", currentY);
                    else
                        System.out.println("Error: attribute type (w)");
                    break;
                case "h":
                    if ((currentY instanceof Integer) || (currentY instanceof Long))
                        template.put("height", currentY);
                    else
                        System.out.println("Error: attribute type (h)");

                    break;
                case "x":
                    if ((currentY instanceof Integer) || (currentY instanceof Long))
                        template.put("x", currentY);
                    else
                        System.out.println("Error: attribute type (x)");

                    break;
                case "y":
                    if ((currentY instanceof Integer) || (currentY instanceof Long))
                        template.put("y", currentY);
                    else
                        System.out.println("Error: attribute type (y)");

                    break;
                //any other attributes can be added later
                default:
                    if (currentY instanceof c.Dict) // if it's only some atom, ignore it
                        template.put(currentX, parseShowMessage(currentX, (c.Dict) currentY));
            }
        }

        //post-format
        if (!template.containsKey("label"))
            if (template.containsKey("binding")) //1st default label is binding name
                template.put("label", template.get("binding").toString());
            else  //2nd default label is widget name
                template.put("label", name);


        return template;
    }
}


