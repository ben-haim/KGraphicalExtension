package ag.kge.display.controllers;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Observable;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A simple button
 */
public class ButtonController extends AbstractController{

    private final JButton button;
    private final String cmd; //the buttons command string

    public ButtonController(TreeMap<String,Object> template,
                            final LinkedBlockingQueue<String> outQueue){

        setName(template.get("name").toString());
        button = new JButton(template.get("label").toString());
        cmd = filterData(template.get("binding"));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                outQueue.add(cmd);
            }
        }); //put the command string on queue
        add(button);
    }

    @Override
    public String generateQuery() {
        return null;
    }

    /**
     * The show handler parses char arrays given as a binding into a string
     * @param data
     * @return
     */
    @Override
    public String filterData(Object data) {
        if (data instanceof String){
            return data.toString();
        } else return ""; //if the data isn't a string, it's invalid

    }

    @Override
    public void update(Observable o, Object arg) {

        //doesn't get updated as it's not bound to a variable
    }
}
