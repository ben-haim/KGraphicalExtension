package ag.kge.display.controllers;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by adnan on 25/04/14.
 */
public abstract class AbstractController extends JPanel implements Observer{

    public enum KType {
        STRING, NUMERIC, ATOM, C_ARRAY, ARRAY, UUID, DICT, TABLE, NULL;

        public static boolean isNumeric(Object object){
            try {
                Double.parseDouble(object.toString());
                return true;
            } catch (NumberFormatException e){
                return false;
            }
        }

        public static KType getTypeOf(Object object){

            if (object == null) return NULL;
            else if (object instanceof HashMap) return DICT;
            else if (object instanceof TableModel) return TABLE;
            else if (object instanceof char[]) return C_ARRAY;
            else if (object instanceof String) return STRING;
            else if (object instanceof java.util.UUID) return UUID;
            else if (object.getClass().isArray()) return ARRAY;
            else if (isNumeric(object)) return NUMERIC;
            else return ATOM;

        }
    }

    protected String binding;

    public AbstractController(HashMap<String, Object> template,
                              final LinkedBlockingQueue<String> outQueue){}

    public abstract String generateQuery();

    public abstract Object filterData(Object data);

}
