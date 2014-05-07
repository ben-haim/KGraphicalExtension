package ag.kge.display.controllers;

import javax.swing.*;
import java.util.*;

/**
 * The super class for all widget controllers
 */
public abstract class AbstractController extends JPanel
        implements Observer
{

    /**
     * The name of the associated variable
     */
    protected String binding;

    public abstract String generateQuery();

    public abstract Object filterData(Object data);


    /**
     * takes an array of strings and generates the first half of a
     * dot index amend
     *
     * @param n the name of the binding variable, split by periods
     * @return
     */
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

    /**
     *
     * @param object
     * @return whether an object is a number
     */
    public static boolean isNumeric(Object object){
        try {
            Double.parseDouble(object.toString());
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }
}
