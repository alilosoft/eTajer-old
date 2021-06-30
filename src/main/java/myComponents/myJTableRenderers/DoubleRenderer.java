/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myComponents.myJTableRenderers;

import java.awt.Component;
import java.text.NumberFormat;
import javax.swing.JTable;

/**
 *
 * @author alilo
 */
public class DoubleRenderer extends NumberRenderer {

    NumberFormat formatter;

    public DoubleRenderer() {
        super();
    }

    @Override
    public void setValue(Object value) {
        if (formatter == null) {
            formatter = NumberFormat.getInstance();
        }
        setText((value == null) ? "" : formatter.format(value));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}
