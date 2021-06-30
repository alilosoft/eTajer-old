/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myModels;

import entities.EntityClass;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author alilo
 * @param <Entity>
 */
public class MyComboBoxModel<Entity extends EntityClass> extends DefaultComboBoxModel<Entity> {

    @Override
    public void addElement(Entity item) {
        super.addElement(item);
    }

    public int getElementID(int index) {
        return getElementAt(index).getId();
    }
    
    protected List<Entity> elements = Collections.synchronizedList(new ArrayList<Entity>());
    public List<Entity> getAllElements() {
        elements.clear();
        for (int i = 0; i < getSize(); i++) {
            elements.add(getElementAt(i));
        }
        return elements;
    }

    @Override
    public Entity getSelectedItem() {
        return (Entity) super.getSelectedItem();
    }
    
}
