package ag.kge.display.controllers;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by adnan on 25/04/14.
 */
public class TextFieldController extends AbstractController {

    private final JTextField textField;
    private KType currentType;

    protected TextFieldController(HashMap<String, Object> infoDict, LinkedBlockingQueue<String> outQueue) {
        super(infoDict, outQueue);

        /*
        At this point the data should also be in the infoDict so we can initialise
        the component with a get() call
        */
        Object data = infoDict.get("data");
        currentType = KType.getTypeOf(data);
        textField = new JTextField(filterData(infoDict.get("data")));
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateServer();
            }
        });
        this.setBorder(new TitledBorder(infoDict.get("label").toString()));
    }

    @Override
    protected void updateServer() {

    }

    @Override
    protected String filterData(Object data) {
        KType type = KType.getTypeOf(data);
        if (type == KType.C_ARRAY)
            return new String((char[]) data);
        else if (type == KType.STRING ||
                type == KType.NUMERIC ||
                type == KType.ATOM
                )
            return data.toString();
        else return "(...)";
    }

    @Override
    public void update(Observable o, Object arg) {
        ArrayDeque<Object> stack = (ArrayDeque) arg;

        //pop off the head of the stack
        Object head = stack.pop();

        //if the stack isn't empty, the head is an index
        if (!stack.isEmpty()){
            Object data = stack.pop();

            //the index references  multiple positions
            if (head instanceof int[] &&
                    data instanceof char[]) {
                String current  = textField.getText();
                for (int i = 0 ; i < Array.getLength(head);i++){

                }
            }
        } else { //the head is the complete data
            currentType = KType.getTypeOf(head);
            textField.setText(filterData(head));
        }

    }

    private String changeString(String oldString, int[] indices, char[] newChars){

        for ()

    }
}
