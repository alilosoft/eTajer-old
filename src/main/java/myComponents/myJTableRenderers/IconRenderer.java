/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myComponents.myJTableRenderers;

import javax.swing.Icon;
import javax.swing.JLabel;

/**
 *
 * @author alilo
 */
public class IconRenderer extends ObjectRenderer{
            public IconRenderer() {
            super();
            setHorizontalAlignment(JLabel.CENTER);
        }

        @Override
        public void setValue(Object value) {
            setIcon((value instanceof Icon) ? (Icon) value : null);
        }
}