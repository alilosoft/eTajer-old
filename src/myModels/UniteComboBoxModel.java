/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myModels;

import entities.Unite;

/**
 *
 * @author alilo
 */
public class UniteComboBoxModel extends MyComboBoxModel<Unite> {

    private final Unite emptyItem = new Unite(0, "Vide!", 0);

    public UniteComboBoxModel() {
        addElement(emptyItem);
    }

    @Override
    public final void addElement(Unite item) {
        if(getIndexOf(emptyItem) >= 0){
            removeElement(emptyItem);
        }
        super.addElement(item); 
    }
}
