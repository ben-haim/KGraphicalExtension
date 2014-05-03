package ag.kge.display.controllers;

import ag.kge.control.ModelCache;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayDeque;
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
    private final JScrollPane scrollPane;

    public PanelController(HashMap<String, Object> template,
                           LinkedBlockingQueue<String> outQueue){

        this.outQueue = outQueue;
        hasDataBinding = filterData(template);
        setMinimumSize(new Dimension(75,150));
        scrollPane = new JScrollPane();
        this.setLayout(new GridBagLayout());
        setName(template.get("name").toString());
        if (!hasDataBinding)
            addChildrenToPanel(template);

    }

    /**
     * Creates a default barebones template required by the controllers for each value in the dictionary.
     * Vector-value dictionaries should just have single types.
     * @param name
     * @return
     */
    private HashMap<String, Object> createDefaultTemplate(String name){
        HashMap<String, Object> template = new HashMap<>();

        template.put("name", name);
        template.put("binding",getName()+ "." +name);
        template.put("label",name);
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
        String binding;
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

                if (h.containsKey("binding")){
                    ModelCache.INSTANCE.addObserver(binding = h.get("binding").toString(), widget);
                    outQueue.add("gUpdate[`"+binding+"; ()]");
                }

                scrollPane.add(widget,gbc);
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
            //can't put anything but a hashmap in a panel
            return true;
        } else {
            return false;
        }

    }

    @Override
    public void update(Observable o, Object arg) {

        ArrayDeque updateStack = (ArrayDeque) arg;
        Object head;
        HashMap templateData;

        if ((head = updateStack.pop()) instanceof HashMap){
            //whole dictionary given?

            HashMap<String, Object> createMap = new HashMap<>();
            templateData = (HashMap) head;

            for (Object x: templateData.keySet()){
                createMap.put(x.toString(), createDefaultTemplate(x.toString()));
            }

            addChildrenToPanel(createMap);
        } else {
            //the head is a symbol of the name of the child to be udpate
            String childName = head.toString();
            for (AbstractController x: children){

                if (x.getName().equals(childName)){
                    x.update(null,updateStack);
                }

            }

        }

    }
}
