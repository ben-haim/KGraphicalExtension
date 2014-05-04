package ag.kge.control;

import ag.kge.c;
import ag.kge.display.FrameCache;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by adnan on 26/04/14.
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
            e.printStackTrace();
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
        The updates in the widget controllers are handled by popping off
        a stack represented by the arraydeque, where the bottom of the stack
        contains the data.

        The popped value could be an index, in which case the widget should try
        to call the update method on the child widget with the index
        */
        ArrayList<Object> updateStack = new ArrayList<>();

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
                updateStack.add(c.at(names, i));
            }
        } else {
            name = names.toString();
        }



        System.out.println(name);
        //now we can check if the cache contains the data
        if (!ModelCache.INSTANCE.checkExists(name)) return;

        //if it does, put the rest of the indices array on the stack
        for (int i = 0; i < Array.getLength(indices); i++){
            updateStack.add(c.at(indices,i));
        }

        //add the actual data to the bottom of the stack/tail of deque
        //putting it through the parser first
        updateStack.add(ModelCache.INSTANCE.parseData(value));

        ModelCache.INSTANCE.updateModel(name, updateStack);
        FrameCache.INSTANCE.refreshFrames();
    }
}
