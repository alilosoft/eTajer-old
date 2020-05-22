/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myComponents;

import java.util.HashMap;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRewindableDataSource;

/**
 *
 * @author alilo
 */
public class MyTableJRDataSource implements JRRewindableDataSource {

    private final MyJTable myJTable;
    private int index = -1;
    private final HashMap<String, Integer> columnNames = new HashMap<>();

    public MyTableJRDataSource(MyJTable jTable) {
        this.myJTable = jTable;

        if (myJTable != null) {
            for (int i = 0; i < myJTable.getColumnCount(); i++) {
                columnNames.put(myJTable.getColumnName(i), i);
            }
        }
    }

    @Override
    public void moveFirst() throws JRException {
        index = -1;
    }

    @Override
    public boolean next() throws JRException {
        index ++;
        if (myJTable != null) {
            return (index < myJTable.getRowCount());
        }
        return false;
    }

    @Override
    public Object getFieldValue(JRField jrField) throws JRException {
        
        String fieldName = jrField.getName();
        Integer colInd = columnNames.get(fieldName);
        if (colInd != null) {
            return myJTable.getValueAt(index, colInd);
        } else {
            return null;
            //throw new JRException("Unknown column name : " + fieldName);
        }
    }

}
