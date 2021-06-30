/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myComponents;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import myComponents.myJListeRendrer.MyCheckListCellRenderer;
import myComponents.myJListeRendrer.MyJListCellRenderer;

/**
 *
 * @author alilo
 */
public class MyJListe extends JList {

    private boolean checkable = false;

    //private MyCheckListCellRenderer cellRenderer;
    public MyJListe() {
        setFixedCellHeight(22);
        customizeBehaviour();
    }

    /**
     *
     * @param dataModel
     * @param isCheckable
     */
    public MyJListe(ListModel dataModel, Boolean isCheckable) {
        super(dataModel);
        setCheckable(isCheckable);
        customizeBehaviour();
    }

    public final void customizeBehaviour() {
        addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }

                if (!getSelectionModel().isSelectionEmpty()) {
                    int selIndex = e.getFirstIndex();
                    doOnSelectionChange(e, selIndex);
                } else {
                    doOnSelectionChange(e, -1);
                }
            }
        });
    }

    public final void customizeView() {
    }

    public final void setCheckable(boolean checkable) {
        if (this.checkable == checkable) {
            return;
        }
        this.checkable = checkable;
        if (checkable) {
            setCellRenderer(new MyCheckListCellRenderer());
            
            addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    //if (e.getClickCount() != 2) {return;}
                    if (getSelectedIndex() < 0) {
                        return;
                    }
                    JCheckBox selectedItem = (JCheckBox) getModel().getElementAt(getSelectedIndex());
                    selectedItem.setSelected(!selectedItem.isSelected());
                    repaint();//repaint the jlist to reflect changes in model
                }
            });

            addKeyListener(new KeyAdapter() {

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        if (getSelectedIndex() < 0) {
                            return;
                        }
                        JCheckBox selectedItem = (JCheckBox) getModel().getElementAt(getSelectedIndex());
                        selectedItem.setSelected(!selectedItem.isSelected());
                        repaint();//repaint the jlist to reflect changes in model
                    }
                }
            });
        } else {
            setCellRenderer(new MyJListCellRenderer());
        }
    }

    public boolean isCheckable() {
        return checkable;
    }

    public void doOnSelectionChange(ListSelectionEvent evt, int selIndex) {
    }
}
