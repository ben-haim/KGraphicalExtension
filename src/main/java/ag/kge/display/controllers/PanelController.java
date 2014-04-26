package ag.kge.display.controllers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Observable;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by adnan on 26/04/14.
 */
public class PanelController extends AbstractController {

    private final LinkedList<AbstractController> children = new LinkedList<>();

    public PanelController(HashMap<String, Object> template,
                           final LinkedBlockingQueue<String> outQueue){

    }

    @Override
    public String generateQuery() {
        return null;
    }

    @Override
    public Object filterData(Object data) {
        return null;
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
