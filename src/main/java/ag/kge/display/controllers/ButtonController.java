package ag.kge.display.controllers;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Observable;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by adnan on 26/04/14.
 */
public class ButtonController extends AbstractController{

    private final JButton button;
    private final String cmd;

    public ButtonController(HashMap<String,Object> template,
                            final LinkedBlockingQueue<String> outQueue){

        setName(template.get("name").toString());
        button = new JButton(template.get("label").toString());
        cmd = filterData(template.get("binding"));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                outQueue.add(cmd);
            }
        });
        add(button);
    }

    @Override
    public String generateQuery() {
        return null;
    }

    @Override
    public String filterData(Object data) {
        if (data instanceof String){
            return data.toString();
        } else return "";
    }

    @Override
    public void update(Observable o, Object arg) {
    }
}
