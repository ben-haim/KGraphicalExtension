package ag.kge.display.controllers;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by adnan on 25/04/14.
 */
public abstract class AbstractController extends JPanel implements Observer{


    protected String binding;

    public AbstractController(HashMap<String, Object> template,
                              final LinkedBlockingQueue<String> outQueue){}

    public abstract String generateQuery();

    public abstract Object filterData(Object data);

}
