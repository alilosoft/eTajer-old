/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myComponents.myJListeRendrer;

import java.awt.Color;
import java.awt.Component;
import java.util.EventObject;
import javax.swing.CellEditor;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.event.CellEditorListener;
import javax.swing.plaf.FontUIResource;

/**
 *
 * @author alilo
 */
public class MyCheckListCellRenderer extends JCheckBox implements ListCellRenderer, CellEditor {

    public MyCheckListCellRenderer() {
        setFont(new FontUIResource("tahoma", FontUIResource.BOLD, 13));
        setToolTipText("Double Click ou Espace pour cocher!");
    }

    @Override
    public Component getListCellRendererComponent(final JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        JCheckBox item = (JCheckBox) value;

        if (index % 2 == 0) {
            setBackground(new Color(0xCC, 0xCC, 0xFF));
        } else {
            setBackground(new Color(0xCC, 0xFF, 0xCC));
        }

        setSelected(item.isSelected());

        if (isSelected) {
            setBackground(Color.BLUE);
            setForeground(Color.WHITE);
        } else {
            setForeground(Color.BLACK);
        }
        setText(((JCheckBox) value).getText());
        return this;
    }

    @Override
    public Object getCellEditorValue() {
        return isSelected();
    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
        return true;
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        return true;
    }

    @Override
    public boolean stopCellEditing() {
        return true;
    }

    @Override
    public void cancelCellEditing() {
        
    }

    @Override
    public void addCellEditorListener(CellEditorListener l) {
        
    }

    @Override
    public void removeCellEditorListener(CellEditorListener l) {
        
    }
}
