package ag.kge.display.controllers;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.ArrayList;

import java.util.Map;
import java.util.Observable;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Displays a one-dimensional array in a list of text fields
 */
public class ListController extends AbstractController {

    /**
     * store the text field objects in an array list
     */
    private final ArrayList<JTextField> textFields = new ArrayList<>();
    private final LinkedBlockingQueue<String> outQueue;

    private int lastChangedIndex = 0;
    private final String label;


    public ListController(TreeMap<String, Object> template, LinkedBlockingQueue<String> outQueue) {
        this.outQueue = outQueue;
        setName(template.get("name").toString());
        binding = template.get("binding").toString();
        label = template.get("label").toString();
        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
    }

    @Override
    public String generateQuery() {

        String t = textFields.get(lastChangedIndex).getText();
        String[] n = binding.split("\\.");
        String m;


        if (n.length > 1){

            m = ".[`"+ n[0] + ";(";

            for (int i = 1; i < n.length;i++){
                m+= "`" + n[i]+ ";";
            }
            m+= lastChangedIndex + ");:;"; //add index to dot amend
        } else {
            m = n[0] + "[" + lastChangedIndex + "]:";
        }

        m+= "`$\"" + t + "\""; //set it up as a char array

        if (n.length > 1)
            m += "];"; //close dot indexing
        else m+=";"; //otherwise just close statement

        return m;
    }

    @Override
    public String filterData(Object data) {
        if (data instanceof char[])//takes char array
            return new String((char[]) data);
        else if (!(data instanceof Map) &&
                !(data instanceof TableModel) &&
                !(data.getClass().isArray()))
            return data.toString(); //needs atom
        else return "(...)";
    }

    @Override
    public void update(Observable o, Object arg) {

        ArrayList updateList = (ArrayList) arg;
        Object head = updateList.get(0);

        if (updateList.size() == 1){
            //if a whole list is sent with no indices, replace current list

            removeAll(); //clear components on panel
            textFields.clear(); //clear textFields list
            JTextField temp;
            String x;

            //iterate through update array
            for (int i = 0;i < Array.getLength(head); i++){
                //get the data in array at i
                x = filterData(Array.get(head,i));
                //create new text field
                temp = new JTextField(x);
                //add the index to the title of the field
                temp.setBorder(new TitledBorder("[" + i + "]"));
                temp.setActionCommand("" + i); //used to set changed index integer

                temp.addActionListener(new ActionListener() {
                    @Override //set last changed index and add query to queue
                    public void actionPerformed(ActionEvent e) {
                        lastChangedIndex = Integer.parseInt(e.getActionCommand());
                        outQueue.add(generateQuery());
                    }
                });
                textFields.add(temp);
                //add temp field to panel
                add(temp);
            }

        } else {

            //indices exist, treat as single index for now
            int ind;
            Object data = updateList.get(1);

            if (isNumeric(head)){
                //if it's a single index change
                ind = (int)head;

                //if the data is a singular i.e. treat as single index change
                if (data.getClass().isArray() && Array.getLength(data)==1)
                    data = Array.get(data,0);

                //set text at index
                textFields.get(ind).setText(filterData(data));
            }
        }
        //add array size to border
        setBorder(new TitledBorder(label + ":" + textFields.size()));
    }
}
