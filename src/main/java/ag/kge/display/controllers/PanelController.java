package ag.kge.display.controllers;

import ag.kge.control.ModelCache;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.*;
import java.util.List;
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
        setMinimumSize(new Dimension(75,150));
        setLayout(new GridBagLayout());
        setName(template.get("name").toString());
        if (!hasDataBinding)
            addChildrenToPanel(template);
        else {
            ModelCache.INSTANCE.addObserver(this.binding = template.get("binding").toString(),this);
            outQueue.add("gUpdate[`"+binding+"; ()]");
        }
    }

    /**
     * Creates a default barebones template required by the controllers for each value in the dictionary.
     * Vector-value dictionaries should just have single types.
     * @param variable
     * @return
     */
    private HashMap<String, Object> createDefaultTemplate(String variable){
        HashMap<String, Object> template = new HashMap<>();

        template.put("name", variable);
        template.put("label",variable);
        template.put("binding",this.binding + "." +variable);
        template.put("class", "data");
        template.put("width", 1);
        template.put("height", 1);
        return template;
    }

    /**
     * Places widgets as determined by their positioning after selecting their controller
     * @param template
     */
    private void addChildrenToPanel(HashMap<String,Object> template){

        int maxY = 1;
        AbstractController widget;
        String currentB;
        for (Object x: template.values())
            if (x instanceof HashMap) {
                HashMap<String,Object> h = (HashMap<String, Object>) x;

                gbc.gridwidth = (Integer) h.get("width");
                gbc.gridheight = (Integer) h.get("height");

                if (h.containsKey("x"))
                    gbc.gridx = (Integer) h.get("x");
                else
                    gbc.gridx = 0;

                if (h.containsKey("y")) {
                    gbc.gridy = (Integer) h.get("y");
                    if (maxY <= gbc.gridy) maxY = gbc.gridy + 1;
                } else {
                    gbc.gridy = maxY;
                    maxY++;
                }

                children.add(widget = selectController(h));

                if (!hasDataBinding){
                    ModelCache.INSTANCE.addObserver(currentB = h.get("binding").toString(), widget);
                    outQueue.add("gUpdate[`"+currentB+"; ()]");
                }

                add(widget, gbc);
            }
    }

    /**
     * Instantiates a controller based on the `class` attribute.
     *
     * @param template
     * @return
     */
    private AbstractController selectController(HashMap<String,Object> template) {

        switch (template.get("class").toString()){
            case "data":
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

        ArrayList updateList = (ArrayList) arg;
        Object head = updateList.get(0);
        HashMap templateData;

        if (head instanceof HashMap){
            //whole dictionary given?

            HashMap<String, Object> createMap = new HashMap<>();
            templateData = (HashMap) head;

            for (Object x: templateData.keySet()){
                createMap.put(x.toString(), createDefaultTemplate(x.toString()));
            }
            addChildrenToPanel(createMap);

            for (Object x: templateData.keySet()){
                for (AbstractController c: children){
                    if (x.toString().equals(c.getName())){
                        c.update(null, Arrays.asList(templateData.get(x)));
                    }
                }
            }

        } else {
            //the head is a symbol of the name of the child to be udpate
            String childName = head.toString();
            List newList = updateList.subList(1, updateList.size());
            for (AbstractController x: children){
                if (x.getName().equals(childName)){
                    x.update(null,newList);
                }

            }

        }

    }
}
