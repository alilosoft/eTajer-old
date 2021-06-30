/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myComponents.myJTableRenderers;

import java.awt.Color;
import java.awt.Component;
import java.math.BigDecimal;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author alilo
 */
public class ObjectRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        if (value == null) {
            if (table.getColumnClass(column).getSimpleName().equals("Integer")) {
                value = 0;
            } else {
                if (table.getColumnClass(column).getSimpleName().equals("String")) {
                    value = "Anonyme!";
                } else {
                    if (table.getColumnClass(column).getSimpleName().equals("BigDecimal")) {
                        value = new BigDecimal("0.00");
                    }
                }
            }
        }

        if (!isSelected) {
            setForeground(Color.BLACK);
            if (row % 2 == 0) {
                setBackground(new Color(0xCC, 0xCC, 0xFF));
            } else {
                setBackground(new Color(0xCC, 0xFF, 0xCC));
            }
        } else {
            setBackground(Color.BLUE);
            setForeground(Color.WHITE);
            return super.getTableCellRendererComponent(table, value, false, false, row, column);
        }
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}
