/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dialogs;


import java.awt.Container;

/**
 *
 * @author alilo
 */
public class MajDialog extends MyJDialog{

    public MajDialog(Container owner, boolean modal, boolean undecorated) {
        super(owner, modal, undecorated);
    }

    public MajDialog(Container owner) {
        super(owner, true, false);
        setModalityType(ModalityType.APPLICATION_MODAL);
        setResizable(false);
        //setAlwaysOnTop(true);
    }
}
