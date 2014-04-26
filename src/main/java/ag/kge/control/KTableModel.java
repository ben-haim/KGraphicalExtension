package ag.kge.control;

import ag.kge.c;

import javax.swing.table.AbstractTableModel;
import java.lang.reflect.Array;

/**
 * Created by adnan on 26/04/14.
 */
public class KTableModel extends AbstractTableModel {

    private final c.Flip table;

    public KTableModel(c.Flip table){
        this.table = table;
    }

    public int getRowCount() {
        return Array.getLength(table.y[0]);
    }

    @Override
    public int getColumnCount() {
        return table.y.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return c.at(table.y[columnIndex], rowIndex);
    }


    @Override
    public String getColumnName(int columnIndex){
        return table.x[columnIndex];
    }


}

