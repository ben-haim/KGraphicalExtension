package ag.kge.display;

import javax.swing.table.TableModel;
import java.util.HashMap;

/**
 * Created by adnan on 26/04/14.
 */
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
