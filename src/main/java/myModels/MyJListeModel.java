/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package myModels; 

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;

/**
 *This class extends the DefaultListModel class to support a Checkable JList,
 * it overrides the addElements() method, to add JCheckBox if the list is checkable
 * @author alilo
 */
public class MyJListeModel extends DefaultListModel {

    private boolean checkable;

    public MyJListeModel(boolean isCheckable) {
        this.checkable = isCheckable;
    }
    /**#
     * Override the addElements() to add jCkeckBox to the list if the Liste is checkable
     * @param obj
     */
    @Override
    public void addElement(Object obj) {
        if (this.checkable) {
            super.addElement(new JCheckBox(obj.toString()));
        } else {
            super.addElement(obj);
        }
    }

    public void setCheckable(boolean isCheckable) {
        this.checkable = isCheckable;
    }

    public boolean isCheckable() {
        return checkable;
    }
}
