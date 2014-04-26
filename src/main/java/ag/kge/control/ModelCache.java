package ag.kge.control;

import ag.kge.c;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Observable;
import java.util.Observer;
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

    private c conn = null;

    /**
     * Contains the data for the
     */
    private final ConcurrentHashMap<String, Observable> cache = new ConcurrentHashMap<>();

    public void setConn(c conn){
        this.conn = conn;
    }

    /**
     * Sends a stack containing update data to the observers of a given variable
     *
     * @param name the name of the variable
     * @param updateStack the stack containing the update data
     */
    protected synchronized void updateModel(String name, ArrayDeque<Object> updateStack) {

        //call notify observers on the model
        cache.get(name).notifyObservers(updateStack);
    }

    public synchronized boolean checkExists(String name){
        return cache.contains(name);
    }

    /**
     * Checks if some data object is currently maintained on the server, if not a blank
     * observable is added to the cache.
     *
     * Then it queries the server for the data, converts into java's format, before
     * returning it back to the caller.
     *
     * @param modelName
     * @return
     */
    public synchronized Object getData(String modelName){
        if (!cache.containsKey(modelName)){
            cache.put(modelName, new Observable());
        }

        Object data;
        try {
            data = conn.k(modelName);
        } catch (IOException | c.KException e) {
            return null;
        }

        if (!c.qn(data)){
            return parseData(data);
        }
        return null;
    }

    /**
     * Converts complex K data into their native java type, i.e. dictionaries to HashMaps
     * and tables to KTableModel. Otherwise returns the data as is.
     *
     * @param data the data to be parsed
     * @return the converted data
     */
    public synchronized Object parseData(Object data) {
        return null;
    }

    /**
     * Adds an observer widget controller to the Observable value of the cache.
     *
     * @param modelName
     * @param observer
     */
    public synchronized void addObserver(String modelName, Observer observer){

        //this should never be called before the model is added to the cache
        //..hopefully
        cache.get(modelName).addObserver(observer);
    }
}
