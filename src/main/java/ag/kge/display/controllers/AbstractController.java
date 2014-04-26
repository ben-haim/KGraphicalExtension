package ag.kge.display.controllers;

import javax.swing.*;
import java.util.*;

/**
 * Created by adnan on 25/04/14.
 */
public abstract class AbstractController extends JPanel implements Observer{

    protected String binding;

    public abstract String generateQuery();

    public abstract Object filterData(Object data);

}
