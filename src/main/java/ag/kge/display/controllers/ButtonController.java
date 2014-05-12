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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Observable;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A simple button
 */
public class ButtonController extends AbstractController{

    private final JButton button;
    private final String cmd; //the buttons command string

    public ButtonController(TreeMap<String,Object> template,
                            final LinkedBlockingQueue<String> outQueue){

        setName(template.get("name").toString());
        button = new JButton(template.get("label").toString());
        cmd = filterData(template.get("binding"));
        button.addActionListener(new ActionListener() {
            //put the command string on queue
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

    /**
     * The show handler parses char arrays given as a binding
     * into a string
     *
     * @param data
     * @return
     */
    @Override
    public String filterData(Object data) {
        if (data instanceof String){
            return data.toString();
        } else return ""; //if the data isn't a string, it's invalid

    }

    @Override
    public void update(Observable o, Object arg) {

        //doesn't get updated as it's not bound to a variable
    }
}
