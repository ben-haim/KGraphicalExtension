package ag.kge.control;

import ag.kge.c;
import ag.kge.display.FrameCache;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * From the update message, creates a list of updates to send to the
 * observers in the model cache.
 * Also parses c.Dict objects into a TreeMap for later use with the
 * controllers
 */
public class UpdateHandler implements Runnable {

    private final LinkedBlockingQueue<Object[]> updateQueue;

    public UpdateHandler(LinkedBlockingQueue<Object[]> updateQueue) {
        this.updateQueue = updateQueue;
    }

    @Override
    public void run() {
        while (true) try{
            parseUpdate(updateQueue.take());
        } catch (InterruptedException e) {
            System.exit(1);
        }
    }

    /**
     * Handles the update data that comes into the KGE, converting it into a stack and
     * notifying the observers of the model to which it belongs.
     *
     * @param update contains the update information in an array
     */
    private void parseUpdate(Object[] update){

        Object names = update[0];
        //the name of the updated variable, possibly an array

        Object indices = update[1];
        //the indices array

        Object value = update[2];
        //value the value of the updated variable at the index

        String name;

        /*
        The updates in the widget controllers are handled by reading
        from the updateList. The front of the list contains the
        indices, the tail of the list contains the actual data
        */
        ArrayList<Object> updateList = new ArrayList<>();

        /*
        The name that comes on the update message may be an array of strings.

        For example, if a value in a dictionary of vectors gets updated,
        instead of the key being part of the indices array, it will be another
        string in the name, making it an array
        */
        if (names.getClass().isArray()){
            //The first string in the name array is always the name of the
            //container object
            name = Array.get(names, 0).toString();

            //add the rest of the array to the deque
            for (int i = 1; i < Array.getLength(names); i++){
                updateList.add(c.at(names, i));
            }
        } else {
            name = names.toString();
        }

        //now we can check if the cache contains the data
        if (!ModelCache.INSTANCE.checkExists(name)) return;

        //if it does, put the rest of the indices array on the stack
        for (int i = 0; i < Array.getLength(indices); i++){
            updateList.add(c.at(indices, i));
        }

        //add the actual data to the bottom of the stack/tail of deque
        //putting it through the data parser first
        updateList.add(parseData(value));

        //sends the update message to the model cache
        ModelCache.INSTANCE.updateModel(name, updateList);

        //refreshes the frames in the frame cache
        FrameCache.INSTANCE.refreshFrames();
    }

    /**
     * Converts complex K data into their native java type, i.e. dictionaries to HashMaps
     * and tables to KTableModel. Otherwise returns the data as is.
     *
     * @param data the data to be parsed
     * @return the converted data
     */
    private Object parseData(Object data) {
        if (data instanceof c.Dict){
            TreeMap<String, Object> treeMap = new TreeMap<>();
            c.Dict d = (c.Dict) data;
            int i = 0;
            if (c.at(d.x,0) == "") i++; //ignores the null index

            //recursively puts data into treeMap
            for (; i < Array.getLength(d.x); i++){
                treeMap.put(Array.get(d.x, i).toString(),
                        parseData(Array.get(d.y, i)));
            }

            return treeMap;
        } else if (data instanceof c.Flip) {
            return ""; //empty string for tables, will work on later
        } else return data; //return the data, it's either an atom or list
    }
}
