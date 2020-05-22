/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myModels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import myComponents.MyJTable;

/**
 *
 * @author alilo
 */
public class FilterColsComboBoxModel extends DefaultComboBoxModel {
    public static final String ALL_ITEMS = "Tous";
    protected List<String> items = Collections.synchronizedList(new ArrayList<String>());
    private final String[] allCols;
    public FilterColsComboBoxModel(MyJTable table) {
        items.add(ALL_ITEMS);
        allCols = new String[table.getColumnCount()];
        int colInd = 0;
        for (String col : table.getModel().getColNames()) {
            if (table.isColumnVisible(col)) {
                items.add(col);
                allCols[colInd] = col;
                colInd++;
            }
        }
        setSelectedItem(items.get(0));
    }

    @Override
    public int getSize() {
        return items.size();
    }

    @Override
    public Object getElementAt(int index) {
        return items.get(index);
    }

    @Override
    public Object getSelectedItem() {
        return super.getSelectedItem();
    }
    
    public String[] getSelectedCols(){
        //System.out.println("getSelectedCols");
        if(getSelectedItem().equals(ALL_ITEMS)){
            return allCols;
        }else{
            return new String[]{(String)getSelectedItem()};
        }
    }
}
