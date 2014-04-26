package ag.kge.display.controllers;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by adnan on 25/04/14.
 */
public abstract class AbstractController extends JPanel implements Observer{

    protected enum KType {
        STRING, NUMERIC, ATOM, C_ARRAY, ARRAY, UUID, DICT, TABLE;

        public static KType getTypeOf(Object object){

            if (object instanceof HashMap) return DICT;
            else if (object instanceof TableModel) return TABLE;
            else if (object instanceof char[]) return C_ARRAY;
            else if (object instanceof String) return STRING;
            else if (object instanceof java.util.UUID) return UUID;
            else if (object.getClass().isArray()) return ARRAY;
            else try {
                    Double.parseDouble(object.toString());
                    return NUMERIC;
                } catch (NumberFormatException e){
                    return ATOM;
                }

        }
    }

    protected final HashMap<String, Object> infoDict;
    protected final LinkedBlockingQueue<String> outQueue;

    protected AbstractController(HashMap<String, Object> infoDict, LinkedBlockingQueue<String> outQueue) {
        this.infoDict = infoDict;
        this.outQueue = outQueue;
    }

    protected abstract void updateServer();

    protected abstract Object filterData(Object data);

}
