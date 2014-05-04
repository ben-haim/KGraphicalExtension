package ag.kge.control;

import ag.kge.c;

import java.io.IOException;
import java.lang.reflect.Array;
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
     * Contains the model's mapped to their names as
     */
    private final ConcurrentHashMap<String, KDataModel> cache = new ConcurrentHashMap<>();

    /**
     * Sends a stack containing update data to the observers of a given variable
     *
     * @param name the name of the variable
     * @param updateStack the stack containing the update data
     */
    protected synchronized void updateModel(String name, ArrayList updateStack) {
        System.out.println("Update called on: " + name);

        //call notify observers on the model
        System.out.println("Obs at update: " + cache.get(name).countObservers());
        cache.get(name).callUpdate(updateStack);
    }

    public synchronized boolean checkExists(String name){
        return cache.containsKey(name);
    }

    /**
     * Converts complex K data into their native java type, i.e. dictionaries to HashMaps
     * and tables to KTableModel. Otherwise returns the data as is.
     *
     * @param data the data to be parsed
     * @return the converted data
     */
    public synchronized Object parseData(Object data) {
        if (data instanceof c.Dict){
            HashMap<String, Object> hashMap = new HashMap<>();
            c.Dict d = (c.Dict) data;
            int i = 0;
            if (c.at(d.x,0) == "") i++;

            for (; i < Array.getLength(d.x); i++){
                hashMap.put(c.at(d.x,i).toString(),
                        parseData(c.at(d.y,i)));
            }

            return hashMap;
        } else if (data instanceof c.Flip) {
            return new KTableModel((c.Flip)data);
        } else return data;
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
            cache.put(modelName, new KDataModel());
        }

        System.out.println("Adding Observer to " + modelName);
        cache.get(modelName).addObserver(observer);
    }
}
