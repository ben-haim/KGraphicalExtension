package ag.kge.display.controllers;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Adnan on 04/05/2014.
 */
public class ListController extends AbstractController implements ListDataListener {

    private final LinkedBlockingQueue<String> outQueue;
    private final DefaultListModel<Object> model;
    private int lastChangedIndex = 0;
    private final String label;
    public ListController(HashMap<String, Object> tempalate, LinkedBlockingQueue<String> outQueue) {
        this.outQueue = outQueue;
        model = new DefaultListModel<>();
        JList<Object> list = new JList<>(model);
        setName(tempalate.get("name").toString());
        binding = tempalate.get("binding").toString();
        label = tempalate.get("label").toString();
        model.addListDataListener(this);
        JScrollPane pane = new JScrollPane(list);
        pane.setPreferredSize(new Dimension(75, 150));
        add(pane);
    }

    @Override
    public String generateQuery() {

        //variables names are stored using namespace indexing
        String t = model.get(lastChangedIndex).toString();
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
        else if (!(data instanceof HashMap) &&
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
            //at this point the head should be the full data
            model.clear();

            for (int i =0; i < Array.getLength(head); i++){
                model.add(i,filterData(Array.get(head, i)));
            }
        } else {

            //indices exist, treat as single index
            int ind;
            Object data = updateList.get(1);

            //incase we have a bulk index update
            if (head instanceof int[] &&
                    data instanceof Object[] &&
                    Array.getLength(head) == Array.getLength(data)){

                for (int j = 0; j < Array.getLength(head); j++){
                    ind = (int) Array.get(head,j);
                    model.set(ind, filterData(Array.get(data,j)));
                }
            } else if (isNumeric(head)){
                //if it's a single index change
                ind = (int) head;
                model.set(ind,filterData(data));

            }
        }

        setBorder(new TitledBorder(label + ":" + model.getSize()));


    }

    @Override
    public void intervalAdded(ListDataEvent e) {

    }

    @Override
    public void intervalRemoved(ListDataEvent e) {

    }

    @Override
    public void contentsChanged(ListDataEvent e) {

        if (e.getIndex0() == e.getIndex1()){
            lastChangedIndex = e.getIndex0();
            outQueue.add(generateQuery());
        }

    }
}
