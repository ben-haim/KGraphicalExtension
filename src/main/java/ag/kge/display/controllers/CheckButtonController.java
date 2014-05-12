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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Observable;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A check button for boolean values
 */
public class CheckButtonController extends AbstractController {

    private final JCheckBox checkBox;
    private final LinkedBlockingQueue<String> outQueue;

    public CheckButtonController(TreeMap<String, Object> template,
                                 final LinkedBlockingQueue<String> outQueue) {

        this.outQueue = outQueue;
        binding = template.get("binding").toString();
        setName(template.get("name").toString());

        checkBox = new JCheckBox(template.get("label").toString());
        checkBox.addItemListener(new ItemListener() {
            //respond to item events
            @Override
            public void itemStateChanged(ItemEvent e) {
                outQueue.add(generateQuery());
            }
        });

        add(checkBox);

    }

    @Override
    public String generateQuery() {
        String m = generateAmend(binding.split("\\."));
        if (checkBox.isSelected())
            m += "1b";
        else m += "0b";

        if (binding.length() > 1)
            m += "]";

        m+=";";
        return m;
    }

    @Override
    public Object filterData(Object data) {
        return  null;
    }

    @Override
    public void update(Observable o, Object arg) {

        List updateList = (List) arg;
        Object head;
        if (updateList.size() == 1){
            // only handle atoms
            if ((head = updateList.get(0)) instanceof Boolean){
                //only respond to booleans
                checkBox.setSelected((Boolean)head);
                setBorder(null);
            } else {
                checkBox.setSelected(false);
                setBorder(new TitledBorder("Type Error"));
            }

        }

    }
}
