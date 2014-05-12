package ag.kge.display;

import ag.kge.control.ModelCache;
import ag.kge.display.controllers.AbstractController;
import ag.kge.display.controllers.FormController;
import ag.kge.display.controllers.TextFieldController;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Renders a JFrame using the template, sets up the observable, adds
 * frame to cache, and calls initial update... if the form class has
 * not been set
 */
public class RenderingEngine implements Runnable {

    private final LinkedBlockingQueue<String> outQueue;
    private final LinkedBlockingQueue<TreeMap> templateQueue;
    /**
     * List of available attributes
     */
    private final List<String> possibleAttributes = Arrays.asList(
            "class","label","binding", "width", "height", "x", "y", "name");


    public RenderingEngine(LinkedBlockingQueue<String> outQueue,
                           LinkedBlockingQueue<TreeMap> templateQueue) {
        this.outQueue = outQueue;
        this.templateQueue = templateQueue;
        javax.swing.plaf.FontUIResource f = new javax.swing.plaf.FontUIResource("Sans-Serif",Font.PLAIN,20);
        Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get (key);
            if (value != null && value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put (key, f);
        }
    }

    @Override
    public void run() {
        while (true) try{
            createAndShow(templateQueue.take());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a JFrame and adds it to the cache
     * @param template
     */
    private void createAndShow(TreeMap<String,Object> template) {

        JFrame frame = new JFrame(template.get("label").toString());
        frame.setContentPane(createControllerHierarchy(template));
        frame.pack();
        frame.setLocationRelativeTo(null); //puts frame in middle of screen

        FrameCache.INSTANCE.addFrame(template.get("name").toString(), frame);
        //don't want to set default close operation to exit, since we may have multiple frames
        //on screen

    }

    /**
     * Recursively add widgets to a default frame, passing off to form controller for
     * better appearance control if class is form and calling initita update as required
     *
     * @param template
     */
    private JPanel createControllerHierarchy(TreeMap<String,Object> template){

        AbstractController c;
        String binding;

        if (template.get("class").equals("form")){

            //if it's a form, let the form controller deal with everything
            return new FormController(template, outQueue);

        } else if (template.containsKey("binding")){

            //if it has a binding, it's a single object
            c = new TextFieldController(template,outQueue);
            ModelCache.INSTANCE.addObserver(binding = template.get("binding").toString(),c);
            outQueue.add("gUpdate[`"+binding+"; ()]");
            return c;

        } else {
            //it probably is a dict without class set as form, use data
            JPanel topPanel = new JPanel();
            topPanel.setLayout(new BoxLayout(topPanel,BoxLayout.Y_AXIS));

            TreeMap h;
            for (String x: template.keySet()) {
                if (!(possibleAttributes.contains(x)) && //if the value isn't an attribute
                        template.get(x) instanceof TreeMap) { //and it's a dictionary
                    //then it must be a child widget
                    h = (TreeMap) template.get(x);
                    topPanel.add(createControllerHierarchy(h));
                } //ignoring other variables
            }
            return topPanel;
        }
    }
}
