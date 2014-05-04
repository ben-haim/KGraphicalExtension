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

    private JTextField textField;

    private boolean isCharArray = false;
    private boolean isNumber = false;

    public TextFieldController(TreeMap<String, Object> template, final LinkedBlockingQueue<String> outQueue) {

        binding = template.get("binding").toString();

        textField = new JTextField(10);
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                outQueue.add(generateQuery());
            }
        });
        setName(template.get("name").toString());


        setBorder(new TitledBorder(template.get("label").toString()));
        add(textField);

    }

    @Override
    public String generateQuery() {

        if (isNumber)
            return generateNumericQuery();

        //variables names are stored using namespace indexing
        String t = textField.getText();
        String[] n = binding.split("\\.");

        String m = generateAmend(n);

        /*
        numeric data only checked by numfieldcontroller
        */
        String v = "\"" + t + "\""; //set it up as a char array

        //cast to symbol if it's not a char array
        if (!isCharArray)
            v = "`$" + v;

        m+= v;
        if (n.length > 1)
            m += "];"; //close dot indexing
        else m+=";"; //otherwise just close statement

        return m;
    }

    /**
     * Generates the amend if the current data is a number, throwing an error if non numeric
     * data in text field.
     *
     * @return generated query String
     */
    private String generateNumericQuery() {
        String t = textField.getText();
        String n[] = binding.split("\\.");
        String m = generateAmend(n);

        if (isNumeric(t)){
            m += t;
        } else {
            textField.setText("ERROR: NOT A NUMBER");
            return "";
        }

        if (n.length > 1) {
            System.out.println("Uses Dot Indexing");
            m += "];"; //close dot indexing
        }
        else m+=";"; //otherwise just close statement
        System.out.println(m);
        return m;
    }


    @Override
    public String filterData(Object data) {
        if (data instanceof char[])//takes char array
            return new String((char[]) data);
        else if (!(data instanceof HashMap) &&
                !(data instanceof TableModel) &&
                !(data.getClass().isArray()))
            return data.toString();
        else return "(...)";
    }

    @Override
    public void update(Observable o, Object arg) {
        int pointer = 0;
        List updateList = (List) arg;
        //pop off the head of the stack
        System.out.println(getName() + " stack " + updateList.size());
        Object head = updateList.get(pointer);
        pointer++;

        //if the stack isn't empty, the head is an index
        if (!(updateList.size() == 1)){
            //if not currently a char array, return as index into symbol doesn't mean anything
            if (!isCharArray){
                return;
            }

            Object data = updateList.get(pointer);
            String current = textField.getText();
            int ind;
            //if the index and data have a one to one mapping
            if (head instanceof int[] &&
                data instanceof char[] &&
                Array.getLength(head) == Array.getLength(data)){

                for (int i = 0; i < Array.getLength(head);i++){
                    ind = (int)Array.get(head,i);
                    current = replaceCharAt(current,ind, (char)Array.get(data,i));
                }

            } else if (head instanceof Integer && data instanceof Character){

                //data is a single character as needed
                ind = (int)head;
                current = replaceCharAt(current,ind,(char)data);

            } else return; //else something wrong with update

            //if problem with update, current stays as is
            textField.setText(current);

        } else { //the head is the complete data
            if (head instanceof char[]) isCharArray = true;
            if (isNumeric(head)) {
                isNumber = true;
            }
            String out = filterData(head);
            textField.setText(out);
            textField.setColumns(out.length()+2);
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
