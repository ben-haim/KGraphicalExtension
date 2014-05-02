package ag.kge.display.controllers;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableModel;
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

    private boolean isCharArray;

    public TextFieldController(HashMap<String, Object> template, final LinkedBlockingQueue<String> outQueue) {

        /*
        At this point the data should also be in the infoDict so we can initialise
        the component with a get() call
        */
        Object data = template.get("data");
        binding = template.get("binding").toString();
        if (data instanceof char[]) isCharArray = true;

        textField = new JTextField(filterData(data));
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                outQueue.add(generateQuery());
            }
        });
        setName(template.get("name").toString());
        //label's should be a char array
        setBorder(new TitledBorder(new String((char[]) template.get("label"))));
        add(textField);

    }

    @Override
    public String generateQuery() {
        //variables names are stored using namespace indexing
        String t = textField.getText();
        String[] n = binding.split("\\.");

        String m = generateAmend(n);

        /*
        numeric data only checked by numfieldcontroller
        */
        m += "\"" + t + "\""; //set it up as a char array

        //cast to symbol if it's not a char array
        if (!isCharArray)
            m = "`$" + m;

        if (n.length > 1)
            m += "];"; //close dot indexing
        else m+=";"; //otherwise just close statement

        return m;
    }


    @Override
    public String filterData(Object data) {
        if (data instanceof char[])
            return new String((char[]) data);
        else if (!(data instanceof HashMap) &&
                !(data instanceof TableModel) &&
                !(data.getClass().isArray()))
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

            //if not currently a char array, return as index into symbol doesn't mean anything
            if (!isCharArray){
                return;
            }

            Object data = stack.pop();
            String current = textField.getText();
            int ind;
            //if the index is an array and if data is array of chars and lengths are same
            if (head instanceof int[] &&
                data instanceof char[] &&
                Array.getLength(head) == Array.getLength(data)){

                for (int i = 0; i < Array.getLength(head);i++){
                    ind = (int)Array.get(head,i);
                    current = replaceCharAt(current,ind, (char)Array.get(data,i));
                }

            } else if (head instanceof Integer && data instanceof Character){
                //data is a character as needed
                ind = (int)head;
                current = replaceCharAt(current,ind,(char)data);

            } else return; //else something wrong with update

            //if problem with update, current stays as is
            textField.setText(current);

        } else { //the head is the complete data

            if (head instanceof char[]) isCharArray = true;
            textField.setText(filterData(head));
        }

    }

    /**
     * Replaces character at a given location in a string. Can also append a character.
     *
     * @param current
     * @param index
     * @param insert
     * @return
     */
    private String replaceCharAt(String current, int index, char insert){
        if (index <= current.length())
            return current.substring(0,index-1) +
                insert + current.substring(index, current.length());
        else if (index == current.length() + 1){
            return current + insert;
        } else return "";
    }

}
