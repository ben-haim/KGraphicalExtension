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

package ag.kge.display;

import javax.swing.*;
import java.util.concurrent.ConcurrentHashMap;

public enum FrameCache {
    /**
     * The enum representing the single reference point
     */
    INSTANCE;

    /**
     * The map containing a mapping of the Frames against their names
     */
    private final ConcurrentHashMap<String, JFrame> cache = new ConcurrentHashMap<>();

    /**
     * Hides a frame and removes it from the cache
     * @param frameName
     */
    public synchronized void hideFrame(String frameName) {
        if (cache.containsKey(frameName))
            cache.remove(frameName).setVisible(false);
        else System.out.println("Frame doesn't exist.");
    }

    /**
     * Adds a frame to the cache and displays it. If a frame of the same name exists,
     * remove it and replace it with the new one
     *
     * @param frameName
     * @param frame
     */
    public synchronized void addFrame(String frameName, JFrame frame){
        //if the frame already exists, destroy it
        if (cache.containsKey(frameName))
            cache.remove(frameName).setVisible(false);

        //add the new frame
        cache.put(frameName,frame);
        cache.get(frameName).setVisible(true);
        System.out.println(frameName  + " added.");
    }

    /**
     * Repacks all the frames in the cache. This is necessary due to the nature of sending
     * an initial update request to populate the widgets, otherwise they will stay in their
     * shrunken sizes as if they have no data
     */
    public void refreshFrames() {
        for (JFrame x: cache.values())
            x.pack();
    }
}
