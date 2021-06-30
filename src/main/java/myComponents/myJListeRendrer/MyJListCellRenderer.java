/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myComponents.myJListeRendrer;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.FontUIResource;

/**
 *
 * @author alilo
 */
public class MyJListCellRenderer extends JLabel implements ListCellRenderer {

    public MyJListCellRenderer() {
        //To show background
        setOpaque(true);
        setFont(new FontUIResource("tahoma", FontUIResource.BOLD, 13));
    }

    @Override
    public Component getListCellRendererComponent(final JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        if (index % 2 == 0) {
            setBackground(new Color(0xCC, 0xCC, 0xFF));
        } else {
            setBackground(new Color(0xCC, 0xFF, 0xCC));
        }

        if (isSelected) {
            setBackground(Color.BLUE);
            setForeground(Color.WHITE);
        } else {
            setForeground(Color.BLACK);
        }
        setText((String) value);
        return this;
    }
}
