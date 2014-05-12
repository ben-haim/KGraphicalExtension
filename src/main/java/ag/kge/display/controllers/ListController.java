/*
 * K Graphical Extension
 * Copyright (C) 2014  Adnan A Gazi
 * Contact: adnan.gazi01@gmail.com
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

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
    private boolean isIntVec, isCharVec, isFloatVec;
    private int lastChangedIndex = 0;
    private final String label;

    public ListController(TreeMap<String, Object> template,
                          LinkedBlockingQueue<String> outQueue) {
        this.outQueue = outQueue;
        setName(template.get("name").toString());
        binding = template.get("binding").toString();
        label = template.get("label").toString();
        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

    }

    @Override
    public String generateQuery() {

        String t = textFields.get(lastChangedIndex).getText().trim();
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


        if (isIntVec) try {
            m+= Integer.parseInt(t);
        } catch (NumberFormatException e){
            setBorder(new TitledBorder("ERROR: VECTOR"));
            return "";
        } else if (isFloatVec) try {
            m+= Double.parseDouble(t);
        } catch (NumberFormatException e){
            setBorder(new TitledBorder("ERROR: VECTOR"));
            return "";
        } else if (isCharVec) {
            if(t.length() == 1)
                m+= "\"" + t + "\"";
            else
                setBorder(new TitledBorder("ERROR: VECTOR"));
        } else {
            m += "`$\"" + t + "\""; //set it up as a char array
        }
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

            if (head instanceof int[] ||
                    head instanceof long[] ||
                    head instanceof short[] ||
                    head instanceof byte[]){
                isIntVec = true;
            } else if (head instanceof double[] || head instanceof float[]){
                isFloatVec = true;
            } else if (head instanceof char[]){
                isCharVec = true;
            }

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
                        lastChangedIndex = Integer.parseInt(
                                e.getActionCommand());
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
