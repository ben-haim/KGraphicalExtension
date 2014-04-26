package ag.kge.display.controllers;

import ag.kge.display.KType;

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

        button = new JButton(new String((char[]) template.get("label")));
        cmd = filterData(template.get("data"));
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
        if (KType.getTypeOf(data)  == KType.C_ARRAY){
            return new String((char[]) data);
        } else return "";
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
