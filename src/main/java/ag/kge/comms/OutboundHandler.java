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

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Sends all outbound messages from the system.
 */
public class OutboundHandler implements Runnable {

    private final c conn;
    private final LinkedBlockingQueue<String> outQueue;

    public OutboundHandler(c conn, LinkedBlockingQueue<String> outQueue) {
        this.conn = conn;
        this.outQueue = outQueue;
    }

    /**
     * Takes messages off the outbound queue and sends them back to the server
     * as a query.
     */
    @Override
    public void run() {
        while (true) try  {
            String out = outQueue.take();
            conn.ks(out); //send query
        } catch (IOException | InterruptedException e) {
            //if the thread is interrupted or the connection is lost, exit
            System.exit(1);
        }
    }
}
