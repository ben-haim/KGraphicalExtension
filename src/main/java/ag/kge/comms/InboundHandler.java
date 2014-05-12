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

package ag.kge.comms;

import ag.kge.c;
import ag.kge.control.*;
import ag.kge.display.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Listens to the connection object for messages from the kdb+ server, and handles them appropriately *
 */
public class InboundHandler implements Runnable{

    private final c conn;
    private final LinkedBlockingQueue<Object[]> showQueue;
    private final LinkedBlockingQueue<Object[]> updateQueue;

    public InboundHandler(c conn,
                          LinkedBlockingQueue<Object[]> showQueue,
                          LinkedBlockingQueue<Object[]> updateQueue) {
        this.conn = conn;
        this.showQueue = showQueue;
        this.updateQueue = updateQueue;
    }

    @Override
    public void run() {
        Object in;
        while (true) try{
            //c.qn checks if a K object is null
            if (!c.qn(in = conn.k())){
                    readMessage(in);
            }
        } catch (c.KException|IOException e) {
            //if the connection is lost, exit
            System.exit(1);
        }
    }

    /**
     * Handles inbound messages from kdb+
     *
     * @param msg the object containing the inbound message
     */
    private void readMessage(Object msg){

        //since the kge.q script only ever sends an array of objects, anything that isn't an
        //array should be ignored
        if (!msg.getClass().isArray()){
            System.out.println(msg.toString());
            return;
        }

        //cast message as array
        Object[] message = (Object[]) msg;

        //the command is the first message in the array
        String cmd = message[0].toString();

        switch (cmd){
            case "show": //push to the show queue
                showQueue.add(new Object[]{message[1], message[2]});
                break;
            case "hide": //hide the frame with the string at message[1]
                FrameCache.INSTANCE.hideFrame(message[1].toString());
                break;
            case "update": //add the name, indices, and update data to update queue
                updateQueue.add(new Object[]{message[1], message[2], message[3]});
                break;
            case "kill": //exit the system
                System.exit(0);
        }


    }

    /**
     * Main method, opens connection to server given an integer argument, and starts  threads
     *
     * @param args first element in array should be a integer
     */
    public static void main(String[] args) {
        int port = 0;

        //if the argument cannot be parsed as an integer, there is something wrong, so exit.
        if (args.length == 1) try{
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e){
            System.err.println("Error: Invalid Port Given");
            System.exit(1);
        } else { //there is also something wrong if more than one argument is given
            System.err.println("Error: Invalid Arguments Given");
            System.exit(1);
        }

        //the connection object to the kbd+ server
        c conn = null;

        try {
            //connect to server and run init function to set up handle
            conn = new c("localhost",port);
            conn.ks("gInit[]");
            System.out.println("Connected to port: " + port);
        } catch (IOException| c.KException e) {
            System.err.println("Error: Couldn't Connect To Server");
            System.exit(1);
        }

        //outbound message queue
        final LinkedBlockingQueue<String> outQueue = new LinkedBlockingQueue<>();
        //gui dictionary queue
        final LinkedBlockingQueue<Object[]> showQueue = new LinkedBlockingQueue<>();
        //update message queue
        final LinkedBlockingQueue<Object[]> updateQueue = new LinkedBlockingQueue<>();
        //gui template queue
        final LinkedBlockingQueue<TreeMap> templateQueue = new LinkedBlockingQueue<>();

        //communication layer threads
        new Thread(new InboundHandler(conn,showQueue, updateQueue)).start();
        new Thread(new OutboundHandler(conn,outQueue)).start();

        //control layer threads
        new Thread(new UpdateHandler(updateQueue)).start();
        new Thread(new ShowHandler(showQueue,templateQueue)).start();

        //display later thread
        new Thread(new RenderingEngine(outQueue,templateQueue)).start();
    }
}
