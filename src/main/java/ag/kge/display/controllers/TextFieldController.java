package ag.kge.display.controllers;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class TextFieldController extends AbstractController {

    private JTextField textField;

    //for type persistence
    private boolean isCharArray = false;
    private boolean isNumber = false;
    private final String label;
    public TextFieldController(TreeMap<String, Object> template,
                               final LinkedBlockingQueue<String> outQueue) {

        binding = template.get("binding").toString();

        textField = new JTextField();
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                outQueue.add(generateQuery());
            }
        });
        setName(template.get("name").toString());
        setBorder(new TitledBorder(label = template.get("label").toString()));
        add(textField);

    }

    /**
     * Generates the query to be sent back to the server when the text
     * field value changes
     *
     * @return outbound string
     */
    @Override
    public String generateQuery() {

        if (isNumber) //check if numeric value
            return generateNumericQuery();

        //variables names are stored using dot indexing
        String t = textField.getText().trim();
        String[] n = binding.split("\\.");

        String m = generateAmend(n); //generates first half of amend

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
     * Generates the amend if the current data is a number, throwing an
     * error if non numeric data in text field.
     *
     * @return generated query String
     */
    private String generateNumericQuery() {
        String t = textField.getText().trim();
        String n[] = binding.split("\\.");
        String m = generateAmend(n);
        setBorder(new TitledBorder(label));
        if (isNumeric(t)){
            m += t;
        } else {
            setBorder(new TitledBorder(("ERROR: NOT A NUMBER")));
            return "";
        }

        if (n.length > 1) {
            m += "];"; //close dot indexing
        } else m+=";"; //otherwise just close statement
        return m;
    }


    /**
     * Filters data to only allow char arrays and atoms.
     *
     * @param data input data
     * @return the string that is displayed
     */
    @Override
    public String filterData(Object data) {
        if (data instanceof char[])//takes char array
            return new String((char[]) data);
        else if (!(data instanceof Map) &&
                !(data instanceof TableModel) &&
                //tablemodel to be used with tables
                !(data.getClass().isArray()))
            return data.toString();
        else return "(...)"; //needs atom
    }

    /**
     * Updates a text field. The update list can either be the complete data,
     * a single index with a character, or a list of indices and a list of
     * corresponding characters (bulk indexing)
     *
     * @param o
     * @param arg the update list
     */
    @Override
    public void update(Observable o, Object arg) {

        int pointer = 0;
        List updateList = (List) arg;

        //get the head of the stack
        Object head = updateList.get(pointer);
        pointer++;

        //if the stack isn't empty, the head is an index
        if (!(updateList.size() == 1)){
            //if not currently a char array, return as index into symbol
            // doesn't mean anything
            if (!isCharArray) return;

            Object data = updateList.get(pointer);
            String current = textField.getText();

            int ind;
            //if the index and data have a one to one mapping
            if (head instanceof int[] &&
                data instanceof char[] &&
                Array.getLength(head) == Array.getLength(data)){

                for (int i = 0; i < Array.getLength(head);i++){
                    ind = (int)Array.get(head,i);
                    current = replaceCharAt(current,ind,
                            (char)Array.get(data,i));
                }
            } else if (head instanceof Integer &&
                    data instanceof Character){
                //data is a single character as needed
                ind = (int)head;
                current = replaceCharAt(current,ind,(char)data);
            } else return; //else something wrong with update

            //if problem with update, current stays as is
            textField.setText("  " + current + "  ");

        } else { //the head is the complete data
            if (head instanceof char[]) isCharArray = true;
            if (isNumeric(head))
                isNumber = true;
            String out = filterData(head);
            textField.setText("  " + out + "  ");
        }
    }

    /**
     * Replaces character at a given location in a string.
     *
     * @param current current whole string
     * @param index index of new char
     * @param insert new char value
     * @return
     */
    private String replaceCharAt(String current, int index,
                                 char insert){
        if (index <= current.length())
            return current.substring(0,index-1) +
                insert + current.substring(index,
                        current.length());
         else return "";
    }

}
