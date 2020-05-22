/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myComponents.myJTableRenderers;

import java.awt.Color;
import javax.swing.JLabel;
import tools.DateTools;

/**
 *
 * @author alilo
 */
public class DateRenderer extends ObjectRenderer {

    public DateRenderer() {
        super();
        setHorizontalAlignment(JLabel.CENTER);
    }

    @Override
    public void setValue(Object value) {
        if (value != null) {
            if (value.getClass().getSimpleName().equals("Time")) {
                setText(DateTools.SHORT_TIME.format(value));
            } else {
                //setText(formatter.format(value)); the value is formated by the model.getValue() method to do filter!
                setText(value.toString());
            }
        } else {
            setBackground(Color.PINK);
            setText("ND!");
        }
    }
}
