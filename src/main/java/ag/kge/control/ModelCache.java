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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A singleton enum that keeps track of the K data on the server and their observer widgets,
 * with methods to add Observers, get the data from the server, notify the widgets of an update,
 * and parse complex K data into a Java type.
 *
 * Created by adnan on 25/04/14.
 */
public enum ModelCache {

    /**
     * The singleton object that this enum represents
     */
    INSTANCE;

    /**
     * Contains the Observables mapped to their variable names
     */
    private final ConcurrentHashMap<String, KObservable> cache = new ConcurrentHashMap<>();

    /**
     * Sends a stack containing update data to the observers of a given variable
     *
     * @param name the name of the variable
     * @param updateStack the stack containing the update data
     */
    protected synchronized void updateModel(String name, ArrayList updateStack) {
        //call notify observers on the model
        cache.get(name).sendUpdate(updateStack);
    }

    //checks if a variable exists on the server
    public synchronized boolean checkExists(String name){
        return cache.containsKey(name);
    }

    /**
     * Checks if some data object is currently maintained on the server, if not a blank observable is added to the
     * cache. Then adds an observer widget controller to the Observable value of the cache.
     *
     * @param modelName
     * @param observer
     */
    public synchronized void addObserver(String modelName, Observer observer){
        if (!cache.containsKey(modelName)){
            cache.put(modelName, new KObservable());
        }

        cache.get(modelName).addObserver(observer);
    }
}
