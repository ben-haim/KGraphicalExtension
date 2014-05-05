package ag.kge.display.controllers;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.ArrayList;

import java.util.Observable;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Adnan on 04/05/2014.
 */
public class ListController extends AbstractController {

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

        //variables names are stored using namespace indexing
        String t = textFields.get(lastChangedIndex).getText();
        String[] n = binding.split("\\.");
        String m;


        if (n.length > 1){

            m = ".[`"+ n[0] + ";(";

            for (int i = 1; i < n.length;i++){
                m+= "`" + n[i]+ ";";
            }

            m+= lastChangedIndex + ");:;";
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
        else if (!(data instanceof TreeMap) &&
                !(data instanceof TableModel) &&
                !(data.getClass().isArray()))
            return data.toString();
        else return "(...)";
    }

    @Override
    public void update(Observable o, Object arg) {

        ArrayList updateList = (ArrayList) arg;
        Object head = updateList.get(0);

        if (updateList.size() == 1){

            removeAll();
            textFields.clear();
            JTextField temp;
            String x;
            for (int i = 0;i < Array.getLength(head); i++){

                x = filterData(Array.get(head,i));
                temp = new JTextField(x);
//                temp.setPreferredSize(new Dimension(100,40));
                temp.setBorder(new TitledBorder("[" + i +"]"));
                temp.setName(""+i);
                temp.setActionCommand(""+i);
                temp.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        lastChangedIndex = Integer.parseInt(e.getActionCommand());
                        outQueue.add(generateQuery());
                    }
                });
                textFields.add(temp);
                add(temp);
            }

        } else {

            //indices exist, treat as single index
            int ind;
            Object data = updateList.get(1);

            if (isNumeric(head)){
                //if it's a single index change
                ind = (int)head;

                //in case the data is a singular
                if (data.getClass().isArray() && Array.getLength(data)==1)
                    data = Array.get(data,0);

                textFields.get(ind).setText(filterData(data));
            }
        }
        setBorder(new TitledBorder(label + ":" + textFields.size()));
    }
}
