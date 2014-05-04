package ag.kge.control;

import java.util.ArrayList;
import java.util.Observable;

/**
 * Created by Adnan on 03/05/2014.
 */
public class KDataModel extends Observable {

    public void callUpdate(ArrayList updateList){
        setChanged();

        notifyObservers(updateList);
    }

}
