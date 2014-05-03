package ag.kge.display;

import ag.kge.control.ModelCache;
import ag.kge.display.controllers.AbstractController;
import ag.kge.display.controllers.PanelController;
import ag.kge.display.controllers.TextFieldController;

import javax.swing.*;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Adnan on 03/05/2014.
 */
public class RenderingEngine implements Runnable {

    private final LinkedBlockingQueue<String> outQueue;
    private final LinkedBlockingQueue<HashMap> templateQueue;

    public RenderingEngine(LinkedBlockingQueue<String> outQueue,
                           LinkedBlockingQueue<HashMap> templateQueue) {
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

    /**
     * Creates a default frame from a template, passing off to panel controller for better appearance control if
     * class is panel, and then storing it in the frame cache
     *
     * @param template
     */
    private void createAndShow(HashMap template) {
        JFrame frame = new JFrame();
        AbstractController c;
        String binding;
        if (template.get("class").equals("panel")){
            //create a panel
            frame.setContentPane(c = new PanelController(template,outQueue));
            if (template.containsKey("binding")) {
                ModelCache.INSTANCE.addObserver(binding = template.get("binding").toString(), c);
                outQueue.add("gUpdate[`" + binding + "; ()]");
            }
        } else {
            System.out.println("Class not panel");
            //just add a default controller
            JPanel topPanel = new JPanel();
            topPanel.setLayout(new BoxLayout(topPanel,BoxLayout.Y_AXIS));
            System.out.println(template.values().size());

            c = new TextFieldController(template,outQueue);
            System.out.println("Controller Created");
            if (template.containsKey("binding")){ //add to model cache as observer
                ModelCache.INSTANCE.addObserver(binding = template.get("binding").toString(),c);
                System.out.println("Message sent");
                topPanel.add(c); //add before the update is sent
                outQueue.add("gUpdate[`"+binding+"; ()]");
            } else
                topPanel.add(c);


            frame.setContentPane(topPanel);
        }

        frame.setTitle(template.get("label").toString());
        frame.pack();
        frame.setLocationRelativeTo(null);
        FrameCache.INSTANCE.addFrame(template.get("name").toString(), frame);
        //don't want to set default close operation to exit, since we may have multiple frames

    }
}
