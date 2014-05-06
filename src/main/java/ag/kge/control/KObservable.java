package ag.kge.control;

import java.util.ArrayList;
import java.util.Observable;

/**
 * A blank class to which widget controllers can subscribe to.
 */
public class KObservable extends Observable {

    /**
     * Need to call setChanged() before notifying observers
     * @param updateList
     */
    public void sendUpdate(ArrayList updateList){
        setChanged();
        notifyObservers(updateList);
    }

}


