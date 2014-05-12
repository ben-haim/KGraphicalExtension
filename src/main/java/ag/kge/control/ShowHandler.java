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

package ag.kge.control;

import ag.kge.c;

import java.lang.reflect.Array;

import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;

public class ShowHandler implements Runnable {

    private final LinkedBlockingQueue<Object[]> showQueue;
    private final LinkedBlockingQueue<TreeMap> templateQueue;

    public ShowHandler(LinkedBlockingQueue<Object[]> showQueue,
                       LinkedBlockingQueue<TreeMap> templateQueue) {
        this.showQueue = showQueue;
        this.templateQueue = templateQueue;
    }

    @Override
    public void run() {
        while (true) try {
            Object[] message = showQueue.take();

            templateQueue.put( //send it to rendering engine after parsing.
                parseShowMessage(message[0].toString(),
                        (c.Dict) message[1])
            );
        } catch (InterruptedException e) {
            System.exit(1);
        }
    }


    /**
     * Parses a c.Dict into a pre-formatted TreeMap, defaulting values for
     * class, label, etc..
     *
     * @param name name of gui
     * @param infoDict original GUI dictionary
     * @return a treemap containing a GUI template
     */
    public TreeMap<String, Object> parseShowMessage(String name, c.Dict infoDict)
    {

        TreeMap<String, Object> template = new TreeMap<>();

        //set the name
        template.put("name", name);
        int i = 0; //ignores null index in dictionaries
        if (Array.get(infoDict.x, 0).toString().equals("")) i = 1;

        String currentX;
        Object currentY;

        //pre-format

        //puts some blank data that can't be displayed by text controllers
        template.put("value", new String[]{});
        template.put("class", "data"); //sets default class to data
        template.put("width", 1);
        template.put("height", 1);
//        template.put("x", 0); //grid bag layout defaults x and y to -1
//        template.put("y", 0);

        for (; i < Array.getLength(infoDict.x); i++) {

            currentX = c.at(infoDict.x, i).toString();
            currentY = c.at(infoDict.y, i);

            switch (currentX) {
                case "c": //class attribute
                    template.put("class", currentY);
                    break;
                case "l": //label attribute
                    if (currentY instanceof String)
                        template.put("label", currentY);
                    else if (currentY instanceof char[]) //could be a char[] or sym
                        template.put("label", new String((char[]) currentY));
                    else //wrong type error
                        System.out.println("Error: attribute type (l)");
                    break;
                case "b": //binding attribute
                    if (currentY instanceof String)
                        //symbol, standard binding
                        template.put("binding", currentY);
                    else if (currentY instanceof char[]) {
                        //char[], button command
                        template.put("binding", new String((char[]) currentY));
                    }else System.out.println("Error: attribute type (b)");
                    break;
                case "w": //grid bag constraints require integers
                    if ((currentY instanceof Integer))
                        template.put("width", currentY);
                    else System.out.println("Error: attribute type (w)");
                    break;
                case "h":
                    if ((currentY instanceof Integer))
                        template.put("height", currentY);
                    else System.out.println("Error: attribute type (h)");
                    break;
                case "x":
                    if ((currentY instanceof Integer))
                        template.put("x", currentY);
                    else System.out.println("Error: attribute type (x)");
                    break;
                case "y":
                    if ((currentY instanceof Integer))
                        template.put("y", currentY);
                    else System.out.println("Error: attribute type (y)");
                    break;
                //any other attributes can be added later
                default:
                    if (currentY instanceof c.Dict) // if it's a c.Dict, start parsing
                    //again for child widget
                        template.put(currentX,
                                parseShowMessage(currentX, (c.Dict) currentY));
            }
        }

        //post-format
        if (!template.containsKey("label"))
            //1st default label is binding name for non-buttons
            if (template.containsKey("binding") &&
                    !template.get("class").equals("button"))
                template.put("label", template.get("binding").toString());
            else  //2nd default label is widget name
                template.put("label", name);

        return template;
    }
}


