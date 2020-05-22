/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package panels;

import dialogs.MyJDialog;
import dialogs.PopupDialog;
import dialogs.SelectionDialog;
import entities.EntityClass;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import myComponents.MyJField;
import myComponents.MyJPanel;
import tools.MessageReporting;

/**
 *
 * @author alilo
 * @param <E>
 */
public class SelectionPanel<E extends EntityClass> extends MyJPanel implements PropertyChangeListener {

    private final SelectionDialog sd = new SelectionDialog();
    private final PopupDialog pd = new PopupDialog(this);
    private ResultSet_Panel selListPanel;
    private E defaultSel;
    private E selEntity;
    private E oldSel;
    private boolean allowSelChange = true;
    private String description;
    private String shortcutKey;
    private String shortcutModif;

    /**
     * Creates new form SelectionPanel
     */
    public SelectionPanel() {
        initComponents();
        doOnPress(KeyEvent.VK_ENTER, 0, showSelListAction, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        doOnPress(KeyEvent.VK_INSERT, 0, newEntityAct, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        doOnPress(KeyEvent.VK_F2, 0, editEntityAct, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        doOnPress(KeyEvent.VK_SPACE, 0, clearSelAction, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public void showSelList() {
        if (sd.isVisible()) {
            return;
        }
        if (selListPanel == null) {
            MessageReporting.showMessage(Level.SEVERE, this.getClass(), "showSelList()", "'selListPanel' is not set!");
            return;
        }
        if (allowSelChange) {
            oldSel = selEntity;
            //sd.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
            //sd.setModal(false);
            //sd.setAlwaysOnTop(true);
            sd.showPanel(selListPanel, defaultSel, descField);
            if (sd.getReturnStatus() == MyJDialog.RET_CANCEL) {
                //setSelEntity(oldSel);
                selListPanel.selectEntity(oldSel);
            }
        }
    }

    private final Action showSelListAction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            showSelList();
        }
    };

    public void clearSelection() {
        if (selListPanel == null) {
            MessageReporting.showMessage(Level.SEVERE, this.getClass(), "showSelList()", "'selListPanel' is not set!");
            return;
        }
        selListPanel.clearSelection();
    }

    private final Action clearSelAction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            clearSelection();
        }
    };

    public void setSelListPanel(ResultSet_Panel selListPanel) {
        this.selListPanel = selListPanel;
        this.selListPanel.getPropertyChangeSupport().addPropertyChangeListener(ResultSet_Panel.ENTITY_TO_VIEW_PROPERTY, this);
        this.selListPanel.remove(this.selListPanel.navigPanel);
        this.selListPanel.remove(this.selListPanel.operPanel);
        this.selListPanel.add(this.selListPanel.operPanel, BorderLayout.SOUTH);
    }

    public ResultSet_Panel getSelListPanel() {
        return selListPanel;
    }

    public void setDefaultSel(E defaultSel) {
        this.defaultSel = defaultSel;
    }

    public E getDefaultSel() {
        return defaultSel;
    }

    public void setSelEntity(E selEntity) {
        this.selEntity = selEntity;
        if (selEntity != null && selEntity.getId() > 0) {
            descField.setForeground(Color.BLACK);
            descField.setText(selEntity.getShortDesc());
            if (!selListPanel.getSelectedEntity().equals(selEntity)) {
                selListPanel.selectEntity(selEntity);
            }
        } else {
            descField.setSpecialFG(Color.RED);
            descField.setText(description);
        }
    }

    public E getSelEntity() {
        if (selEntity != null && selEntity.getId() > 0) {
            return selEntity;
        } else {
            return null;
        }
    }

    public void newEntity() {
        selListPanel.insert();
    }

    public Action newEntityAct = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            newEntity();
        }
    };

    public void editSelEntity() {
        selListPanel.edit();
    }

    public Action editEntityAct = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            editSelEntity();
        }
    };

    public void setAllowSelChange(boolean allowSelChange) {
        this.allowSelChange = allowSelChange;
        showSelListAction.setEnabled(allowSelChange);
        clearSelAction.setEnabled(allowSelChange);
        newEntityAct.setEnabled(allowSelChange);
        //editEntityAct.setEnabled(allowSelChange);
    }

    public boolean isAllowSelChange() {
        return allowSelChange;
    }

    public void setDescription(String description) {
        this.description = description;
        descField.setDescription(description);
    }

    public String getDescription() {
        return description;
    }

    @Override
    public void setEnabled(boolean enabled) {
        descField.setEnabled(enabled);
        showSelListAction.setEnabled(enabled);
        clearSelAction.setEnabled(enabled);
        newEntityAct.setEnabled(enabled);
        editEntityAct.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    public MyJField getDescField() {
        return descField;
    }

    @Override
    public void requestFocus() {
        descField.requestFocus();
    }

    @Override
    public void setFocusable(boolean focusable) {
        super.setFocusable(false);
        descField.setFocusable(focusable);
    }

    public void setShortcutKey(String shortcutKey) {
        this.shortcutKey = shortcutKey;
        selBtn.setText(shortcutKey);
        setShortcutModifier(null);
    }

    public String getShortcutKey() {
        return shortcutKey;
    }

    public void setShortcutModifier(String shortcutModifier) {
        this.shortcutModif = shortcutModifier;

        if (shortcutModifier != null && shortcutModifier.trim().length() != 0) {
            selBtn.setText(shortcutModifier + "+" + shortcutKey);
        } else {
            this.shortcutModif = null;
            selBtn.setText(shortcutKey);
        }
        doOnPress(shortcutKey, this.shortcutModif, showSelListAction, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    public String getShortcutModif() {
        return shortcutModif;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        descField = new myComponents.MyJField();
        selBtn = new javax.swing.JButton();
        clearSelBtn = new javax.swing.JButton();
        editBtn = new javax.swing.JButton();
        newBtn = new javax.swing.JButton();

        setRequestFocusEnabled(false);
        setLayout(new java.awt.GridBagLayout());

        descField.setEditable(false);
        descField.setBackground(new java.awt.Color(227, 227, 253));
        descField.setToolTipText("Appuyer Entrer pour sélectionner!");
        descField.setDescFG(new java.awt.Color(102, 102, 102));
        descField.setDescription("Tapez 'Entrer' pour Chercher");
        descField.setPreferredSize(new java.awt.Dimension(100, 25));
        descField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                descFieldFocusLost(evt);
            }
        });
        descField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                descFieldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                descFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(descField, gridBagConstraints);

        selBtn.setAction(showSelListAction);
        selBtn.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        selBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/find16.png"))); // NOI18N
        selBtn.setToolTipText("Choisire un élément (Entrer)");
        selBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        selBtn.setBorderPainted(false);
        selBtn.setFocusable(false);
        selBtn.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(selBtn, gridBagConstraints);

        clearSelBtn.setAction(clearSelAction);
        clearSelBtn.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        clearSelBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/clear16.png"))); // NOI18N
        clearSelBtn.setToolTipText("Effacer (Espace)");
        clearSelBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        clearSelBtn.setFocusable(false);
        clearSelBtn.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(clearSelBtn, gridBagConstraints);

        editBtn.setAction(editEntityAct);
        editBtn.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        editBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/edit16.png"))); // NOI18N
        editBtn.setToolTipText("Modifier (F2)");
        editBtn.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        editBtn.setFocusable(false);
        editBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        editBtn.setMaximumSize(new java.awt.Dimension(27, 27));
        editBtn.setMinimumSize(new java.awt.Dimension(27, 27));
        editBtn.setPreferredSize(new java.awt.Dimension(27, 27));
        editBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(editBtn, gridBagConstraints);

        newBtn.setAction(newEntityAct);
        newBtn.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        newBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/new16.png"))); // NOI18N
        newBtn.setToolTipText("Ajouter (Insert)");
        newBtn.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        newBtn.setFocusable(false);
        newBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        newBtn.setMaximumSize(new java.awt.Dimension(27, 27));
        newBtn.setMinimumSize(new java.awt.Dimension(27, 27));
        newBtn.setPreferredSize(new java.awt.Dimension(27, 27));
        newBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(newBtn, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void descFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_descFieldKeyTyped
        //if (Character.isAlphabetic(evt.getKeyChar()) || Character.isDigit(evt.getKeyChar())) {
        //selListPanel.doFilter(String.valueOf(evt.getKeyChar()));
        //selListPanel.doFilter(descField.getText());
        //showSelList();
        //}
        //selListPanel.doFilter(descField.getText());
        //showSelList();
    }//GEN-LAST:event_descFieldKeyTyped

    private void descFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_descFieldKeyReleased
        //selListPanel.doFilter(descField.getText());
        //showSelList();
        if (evt.getKeyCode() == KeyEvent.VK_UP) {
            selListPanel.up();
        } else {
            if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
                selListPanel.down();
            }
        }
        if (false) {
            if (descField.getText().isEmpty()) {
                descField.setForeground(new Color(102, 102, 102));
                descField.setText(getDescription());
                descField.setCaretPosition(0);
            } else {
                if (descField.getText().equals(getDescription())) {
                    selListPanel.filterList("");
                } else {
                    selListPanel.filterList(descField.getText().toUpperCase().trim());
                }
            }

            pd.add(selListPanel);
            pd.pack();
            pd.setFocusableWindowState(false);
            pd.setLocation(descField.getLocationOnScreen().x, descField.getLocationOnScreen().y + descField.getSize().height);
            if (!sd.isVisible()) {
                pd.setVisible(true);
                pd.setFocusableWindowState(true);
            }
        }
    }//GEN-LAST:event_descFieldKeyReleased

    private void descFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_descFieldFocusLost
        //pd.setVisible(false);
    }//GEN-LAST:event_descFieldFocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton clearSelBtn;
    private myComponents.MyJField descField;
    private javax.swing.JButton editBtn;
    private javax.swing.JButton newBtn;
    private javax.swing.JButton selBtn;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof ResultSet_Panel) {
            ResultSet_Panel panel = (ResultSet_Panel) evt.getSource();
            if (evt.getPropertyName().equalsIgnoreCase(ResultSet_Panel.ENTITY_TO_VIEW_PROPERTY)) {
                setSelEntity((E) panel.getSelectedEntity());
            }
        }
    }
}
