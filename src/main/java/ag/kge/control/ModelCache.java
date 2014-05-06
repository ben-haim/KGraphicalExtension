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
        //call notify observers on the model
        cache.get(name).sendUpdate(updateStack);
    }

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
            cache.put(modelName, new KDataModel());
        }

        cache.get(modelName).addObserver(observer);
    }
}
