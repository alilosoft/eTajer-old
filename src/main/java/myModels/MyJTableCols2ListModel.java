/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myModels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JCheckBox;
import myComponents.MyJTable;

/**
 *
 * @author alilo
 */
public class MyJTableCols2ListModel extends MyJListeModel {

    public static final int ALL_COLS = 1;
    public static final int ONLY_VISIBLE_COLS = 2;
    public static final int ONLY_HIDEN_COLS = 3;
    private boolean onlyVisible, onlyHiden, all;

    protected List<String> colsNames = Collections.synchronizedList(new ArrayList<>());

    public MyJTableCols2ListModel(MyJTable table, int colsGroup) {
        super(true);
        switch (colsGroup) {
            case ONLY_VISIBLE_COLS:
                onlyVisible = true;
                break;
            case ONLY_HIDEN_COLS:
                onlyHiden = true;
                break;
            default:
                all = true;
        }
        for (String col : table.getModel().getColNames()) {
            if (onlyVisible && table.isColumnVisible(col)) {
                colsNames.add(col);
                addElement(col);
            } else {
                if (onlyHiden && !table.isColumnVisible(col)) {
                    colsNames.add(col);
                    addElement(col);
                } else {
                    if (all) {
                        colsNames.add(col);
                        addElement(col);
                    }
                }
            }
        }
    }

    public String[] getCheckedCols() {
        List<String> checkedCols = new ArrayList<>();
        for (int i = 0; i < getSize(); i++) {
            JCheckBox item = getElementAt(i);
            if (item.isSelected()) {
                checkedCols.add(item.getText());
            }
        }
        return checkedCols.toArray(new String[0]);
    }

    public void setCheckedCols(String[] cols) {
        for (String col : cols) {
            if (colsNames.contains(col)) {
                int colInd = colsNames.indexOf(col);
                getElementAt(colInd).setSelected(true);
            }
        }
    }

    @Override
    public JCheckBox getElementAt(int index) {
        return (JCheckBox) super.getElementAt(index);
    }
}
