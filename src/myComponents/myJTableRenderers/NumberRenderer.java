/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myComponents.myJTableRenderers;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;

/**
 *
 * @author alilo
 */
public class NumberRenderer extends ObjectRenderer {

    public NumberRenderer() {
        super();
        setHorizontalAlignment(JLabel.TRAILING);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        return this;
    }
}
