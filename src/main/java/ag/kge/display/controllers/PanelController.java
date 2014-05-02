package ag.kge.display.controllers;

import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Observable;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by adnan on 26/04/14.
 */
public class PanelController extends AbstractController {

    private final LinkedList<AbstractController> children = new LinkedList<>();
    private final GridBagConstraints gbc = new GridBagConstraints();
    private final LinkedBlockingQueue<String> outQueue;
    private final boolean hasDataBinding;

    public PanelController(HashMap<String, Object> template,
                           LinkedBlockingQueue<String> outQueue){

        this.outQueue = outQueue;
        hasDataBinding = filterData(template);

        this.setLayout(new GridBagLayout());


    }


    private void addChildrenToPanel(HashMap<String,Object> template){

        int maxY = 1;
        AbstractController widget;

        for (Object x: template.values())
            if (x instanceof HashMap) {
                HashMap<String,Object> h = (HashMap<String, Object>) x;

                gbc.gridwidth = (int) h.get("width");
                gbc.gridheight = (int) h.get("height");

                if (h.containsKey("x"))
                    gbc.gridx = (int) h.get("x");
                else
                    gbc.gridx = 0;

                if (h.containsKey("y")) {
                    gbc.gridy = (int) h.get("y");
                    if (maxY <= gbc.gridy) maxY = gbc.gridy + 1;
                } else {
                    gbc.gridy = maxY;
                    maxY++;
                }

                children.add(widget = selectController(h));

                this.add(widget,gbc);


            }
    }

    private AbstractController selectController(HashMap<String,Object> template) {

        switch (template.get("class").toString()){
            case "data":
                if (isNumeric(template.get("data")))
                    return new NumFieldController(template, outQueue);
                else
                    return new TextFieldController(template,outQueue);
            case "button":
                return new ButtonController(template,outQueue);
            case "panel": //needs to externally set panel label
                AbstractController c =  new PanelController(template,outQueue);
                c.setBorder(new TitledBorder(template.get("label").toString()));
                return c;
        }

        return null;
    }


    @Override
    public String generateQuery() {
        return null;
    }

    @Override
    public Boolean filterData(Object data) {

        HashMap d = (HashMap) data;
        if (d.containsKey("binding")){
            return true;
        } else {
            return false;
        }

    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
