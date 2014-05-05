package ag.kge.display;

import ag.kge.control.ModelCache;
import ag.kge.display.controllers.AbstractController;
import ag.kge.display.controllers.PanelController;
import ag.kge.display.controllers.TextFieldController;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Adnan on 03/05/2014.
 */
public class RenderingEngine implements Runnable {

    private final LinkedBlockingQueue<String> outQueue;
    private final LinkedBlockingQueue<TreeMap> templateQueue;
    private final List<String> possibleAttributes = Arrays.asList(
            "class","label","binding", "width", "height", "x", "y", "name");


    public RenderingEngine(LinkedBlockingQueue<String> outQueue,
                           LinkedBlockingQueue<TreeMap> templateQueue) {
        this.outQueue = outQueue;
        this.templateQueue = templateQueue;
    }

    @Override
    public void run() {
        while (true) try{
            createAndShow(templateQueue.take());
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


    private void createAndShow(TreeMap<String,Object> template) {

        JFrame frame = new JFrame(template.get("label").toString());
        frame.setContentPane(createControllerHierarchy(template));
        frame.pack();
        frame.setLocationRelativeTo(null);
        FrameCache.INSTANCE.addFrame(template.get("name").toString(), frame);
        //don't want to set default close operation to exit, since we may have multiple frames

    }

    /**
     * Creates a default frame from a template, passing off to panel controller for better appearance control if
     * class is panel, and then storing it in the frame cache
     *
     * @param template
     */
    private JPanel createControllerHierarchy(TreeMap<String,Object> template){

        AbstractController c;
        String binding;

        if (template.get("class").equals("panel")){

            //if it's a panel, let the panel controller deal with everything
            return new PanelController(template, outQueue);

        } else if (template.containsKey("binding")){

            //if it has a binding, it's a single object
            c = new TextFieldController(template,outQueue);
            ModelCache.INSTANCE.addObserver(binding = template.get("binding").toString(),c);
            outQueue.add("gUpdate[`"+binding+"; ()]");
            return c;

        } else {
            //it probably is a pane without class set as panel, use data
            JPanel topPanel = new JPanel();
            topPanel.setLayout(new BoxLayout(topPanel,BoxLayout.Y_AXIS));

            TreeMap h;
            for (String x: template.keySet()) {
                if (!(possibleAttributes.contains(x)) && //if the value isn't an attribute
                        template.get(x) instanceof TreeMap) { //and it's a dictionary
                    //then it must be a child widget
                    h = (TreeMap) template.get(x);
                    topPanel.add(createControllerHierarchy(h));
                }
            }
            return topPanel;
        }

    }

}
