/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myModels;

import java.util.Vector;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author alilo
 */
public class UMComboBoxModel extends DefaultComboBoxModel {

    public Integer[] qteLot = new Integer[2];

    public UMComboBoxModel() {
        super(new String[]{"Unité","Pack"});
    }

    public void setElementAt(Object item, int index) {
        if (getElementAt(index) != null) {
            //removeElementAt(index);
        }
        removeElementAt(index);
        insertElementAt(item, index);
    }

    public void setQteLotAt(int qteLot, int index) {
        this.qteLot[index] = qteLot;
    }

    public int getQteLotAt(int index) {
        return qteLot[index];
    }


}
