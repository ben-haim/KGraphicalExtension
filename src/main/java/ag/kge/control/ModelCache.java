package ag.kge.control;

import ag.kge.c;

import java.io.IOException;
import java.lang.reflect.Array;
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
     * Handles the update data that comes into the KGE, converting it into a stack and
     * notifying the observers of the model to which it belongs.
     *
     * @param names the name of the updated variable, possibly an array
     * @param indices the indices array
     * @param value the value of the updated variable at the index
     */
    public synchronized void updateModel(Object names, Object indices, Object value) {

        String name;

        /*
        The updates in the widget controllers are handled by popping off
        a stack represented by the arraydeque, where the bottom of the stack
        contains the data.

        The popped value could be an index, in which case the widget should try
        to call the update method on the child widget with the index
        */
        ArrayDeque<Object> updateStack = new ArrayDeque<>();

        /*
        The name that comes on the update message may be an array of strings.

        For example, if a value in a dictionary of vectors gets updated,
        instead of the key being part of the indices array, it will be another
        string in the name, making it an array
        */
        if (names.getClass().isArray()){
            //The first string in the name array is always the name of the
            //container object
            name = Array.get(names,0).toString();

            //add the rest of the array to the deque
            for (int i = 1; i < Array.getLength(names); i++){
                updateStack.add(c.at(names, i));
            }
        } else {
            name = names.toString();
        }

        //now we can check if the cache contains the data
        if (!cache.contains(name)) return;

        //if it does, put the rest of the indices array on the stack
        for (int i = 0; i < Array.getLength(indices); i++){
            updateStack.add(c.at(indices,i));
        }

        //add the actual data to the bottom of the stack/tail of deque
        updateStack.add(value);

        //call notify observers on the model
        cache.get(name).notifyObservers(updateStack);
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
