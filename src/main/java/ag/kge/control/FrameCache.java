package ag.kge.control;


import ag.kge.display.controllers.AbstractController;
import ag.kge.display.controllers.PanelController;
import ag.kge.display.controllers.TextFieldController;

import javax.swing.*;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by adnan on 25/04/14.
 */
public enum FrameCache {

    INSTANCE;

    private final ConcurrentHashMap<String, JFrame> cache = new ConcurrentHashMap<>();

    public synchronized void hideFrame(String frameName) {
        if (cache.containsKey(frameName))
            cache.remove(frameName).setVisible(false);
    }

    public synchronized void addFrame(String frameName, JFrame frame){
        cache.put(frameName,frame);
    }

    /**
     * Creates a default frame from a template, passing off to panel controller for better appearance control
     *
     * @param template
     * @param outQueue
     */
    public synchronized void createAndShow(HashMap<String, Object> template,
                                           final LinkedBlockingQueue<String> outQueue) {

        JFrame frame = new JFrame();

        if (template.get("class").equals("panel")){
            //create a panel
            frame.setContentPane(new PanelController(template,outQueue));
        } else {
            //just add a default controller
            JPanel topPanel = new JPanel();
            topPanel.setLayout(new BoxLayout(topPanel,BoxLayout.Y_AXIS));

            for (Object x: template.values()){
                if (x instanceof HashMap){
                    HashMap h = (HashMap) x;
                    AbstractController c = new TextFieldController(h,outQueue);

                    if (h.containsKey("binding")){ //add to model cache as observer
                        ModelCache.INSTANCE.addObserver(h.get("binding").toString(),c);
                    }

                    topPanel.add(c);
                }
            }

            frame.setContentPane(topPanel);
        }

        frame.setTitle(template.get("label").toString());
        addFrame(template.get("name").toString(), frame);
    }




}
