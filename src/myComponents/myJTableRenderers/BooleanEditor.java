/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myComponents.myJTableRenderers;

import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;


/**
 *
 * @author alilo
 */
public class BooleanEditor extends DefaultCellEditor {

    public BooleanEditor() {
        super(new BooleanCellEditor());
    }
    

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if(value == null){
            return new JLabel("Non Specifier");
        }
        JCheckBox checkBox = (JCheckBox) value;
        checkBox.setSelected(!checkBox.isSelected());
        delegate.setValue(checkBox.isSelected());
        cellCheckChenged((Boolean)getCellEditorValue(), row);
        return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }
    /**
     * This method will invoked when a cell check state is changed.
     * @param checked 
     */
    public void cellCheckChenged(boolean checked, int row){
    }
}
class BooleanCellEditor extends JCheckBox {

    public BooleanCellEditor() {
        super();
        setHorizontalAlignment(JLabel.CENTER);
    }
}
