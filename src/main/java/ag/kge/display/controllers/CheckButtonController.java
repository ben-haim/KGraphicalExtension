package ag.kge.display.controllers;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Observable;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Adnan on 05/05/2014.
 */
public class CheckButtonController extends AbstractController {

    private final JCheckBox checkBox;
    private final LinkedBlockingQueue<String> outQueue;

    public CheckButtonController(TreeMap<String, Object> template, final LinkedBlockingQueue<String> outQueue) {

        this.outQueue = outQueue;
        binding = template.get("binding").toString();
        setName(template.get("name").toString());

        checkBox = new JCheckBox(template.get("label").toString());
        checkBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                outQueue.add(generateQuery());
            }
        });

        add(checkBox);

    }

    @Override
    public String generateQuery() {
        String m = generateAmend(binding.split("\\."));
        if (checkBox.isSelected()){
            m += "1b";
        } else {
            m+="0b";
        }

        if (binding.length() > 1){
            m += "]";
        }

        m+=";";

        return m;
    }

    @Override
    public Object filterData(Object data) {
        return  null;
    }

    @Override
    public void update(Observable o, Object arg) {

        List updateList = (List) arg;
        Object head;
        if (updateList.size() == 1){

            if ((head = updateList.get(0)) instanceof Boolean){
                checkBox.setSelected((Boolean)head);
                setBorder(null);
            } else {
                checkBox.setSelected(false);
                setBorder(new TitledBorder("Type Error"));
            }

        }

    }
}
