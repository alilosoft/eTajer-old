/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myComponents.myJTableRenderers;

import java.awt.Color;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author alilo
 */
public class BooleanRenderer extends JCheckBox implements TableCellRenderer {

    public BooleanRenderer() {
        super();
        setHorizontalAlignment(JLabel.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        if (value == null) {
            value = false;
            //setBackground(Color.RED);
        } else {
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
            }
        }


        if (value != null) {
            if (value instanceof String) {
                boolean val = ((String) value).equalsIgnoreCase("Y") ? true : false;
                setSelected(val);
                return this;
            }

            if (value instanceof JCheckBox) {
                setSelected(((JCheckBox) value).isSelected());
                return this;
            }

            if (value instanceof Boolean) {
                boolean val = (Boolean) value;
                setSelected(val);
                if (val) {
                    setIcon(new ImageIcon(getClass().getResource("res/true.png")));
                } else {
                    setIcon(new ImageIcon(getClass().getResource("res/false.png")));
                }
                return this;
            }
        }

        return this;
    }
}
