package ag.kge.control;

import java.util.ArrayDeque;
import java.util.Observable;

/**
 * Created by Adnan on 03/05/2014.
 */
public class KDataModel extends Observable {

    public void callUpdate(ArrayDeque updateStack){
        setChanged();
        notifyObservers(updateStack);
    }

}
