package myComponents;

import panels.ResultSet_Panel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author alilo
 */
public class MyBeanListener implements PropertyChangeListener {

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equalsIgnoreCase(ResultSet_Panel.ENTITY_TO_VIEW_PROPERTY)) {
            System.out.println("Property Change Event");
            String source = evt.getSource().toString();
            System.out.println("Source:" + source);
            String newVal = evt.getNewValue().toString();
            System.out.println("Old Val:" + evt.getOldValue() + "\nNew Val:" + newVal);
            String propName = evt.getPropertyName();
            System.out.println("Prop Name:" + propName);
        }

    }
}
