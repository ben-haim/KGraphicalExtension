package ag.kge.display.controllers;

import ag.kge.control.ModelCache;

import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by adnan on 26/04/14.
 */
public class FormController extends AbstractController {

    private final LinkedList<AbstractController> children = new LinkedList<>();
    private final GridBagConstraints gbc = new GridBagConstraints();
    private final LinkedBlockingQueue<String> outQueue;
    private final boolean hasDataBinding;

    public FormController(TreeMap<String, Object> template,
                          LinkedBlockingQueue<String> outQueue){

        this.outQueue = outQueue;
        hasDataBinding = filterData(template);
        setLayout(new GridBagLayout());
        setName(template.get("name").toString());
        if (!hasDataBinding)
            addChildrenToPanel(template);
        else {
            ModelCache.INSTANCE.addObserver(this.binding = template.get("binding").toString(), this);
            outQueue.add("gUpdate[`" + binding + "; ()]");
        }
    }

    /**
     * Creates a default barebones template required by the controllers for each value in the dictionary.
     * Vector-value dictionaries should just have single types.
     * @param variable
     * @return
     */
    private TreeMap<String, Object> createDefaultTemplate(String variable){
        TreeMap<String, Object> template = new TreeMap<>();

        template.put("name", variable);
        template.put("label",variable);
        template.put("binding",this.binding + "." +variable);
        template.put("class", "data");
        template.put("width", 1);
        template.put("height", 1);
        template.put("x", 0); //gbc defaults x and y to -1
        template.put("y", 0);

        return template;
    }

    /**
     * Places widgets as determined by their positioning after selecting their controller
     * @param template
     */
    private void addChildrenToPanel(TreeMap<String,Object> template){

        int maxY = 0;
        AbstractController widget;
        String currentB;
        for (Object x: template.values())
            if (x instanceof TreeMap) {
                TreeMap<String,Object> h = (TreeMap<String, Object>) x;


                if (h.containsKey("width"))
                    gbc.gridwidth= (Integer) h.get("width");
                else
                    gbc.gridwidth= 1;

                if (h.containsKey("height"))
                    gbc.gridheight = (Integer) h.get("height");
                else
                    gbc.gridheight= 1;

                gbc.fill = GridBagConstraints.BOTH;

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

                if (h.get("class").equals("list"))
                    gbc.ipadx=20;
                else
                    gbc.ipadx=0;

                children.add(widget = selectController(h));

                if (!hasDataBinding){
                    if (!(widget instanceof ButtonController)) {
                        ModelCache.INSTANCE.addObserver(currentB = h.get("binding").toString(), widget);
                        outQueue.add("gUpdate[`" + currentB + "; ()]");
                    }
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
    private AbstractController selectController(TreeMap<String,Object> template) {

        switch (template.get("class").toString()){
            case "data": return new TextFieldController(template,outQueue);
            case "button": return new ButtonController(template,outQueue);
            case "list": return new ListController(template,outQueue);
            case "check": return new CheckButtonController(template,outQueue);
            case "form": //needs to externally set label
                AbstractController c =  new FormController(template,outQueue);
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

        TreeMap d = (TreeMap) data;
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
        TreeMap templateData;
        if (head instanceof TreeMap){
            //whole dictionary given?

            TreeMap<String, Object> createMap = new TreeMap<>();
            templateData = (TreeMap) head;

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
