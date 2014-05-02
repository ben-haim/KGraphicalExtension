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


    protected String generateAmend( String[] n){

        String m;
        //set up amend into variable
        if (n.length == 1) m = n[0] + ":"; //atom
        else {
            //list, use dot indexing, raze names
            m = ".[`"+ n[0] + ";raze " ;

            for (int i = 1; i < n.length; i++)
                m += "`" + n[i];

            m += ";:;"; //add amend operator
        }

        return m;

    }

    protected boolean isNumeric(Object object){
        try {
            Double.parseDouble(object.toString());
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }

}
