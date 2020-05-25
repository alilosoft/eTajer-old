/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package panels.maj;

import entities.EntityClass;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import myComponents.MyJPanel;
import panels.ResultSet_Panel;
import panels.RSTablePanel;
import dao.TableDAO;
import dialogs.MajDialog;
import dialogs.MyJDialog;
import panels.CRUDPanel;

/**
 *
 * @author alilo
 * @param <Entity>
 * @param <DAO>
 */
public abstract class MajPanel<Entity extends EntityClass, DAO extends TableDAO<Entity, ?>> extends MyJPanel implements PropertyChangeListener {

    /**
     * A return status code - returned if Cancel button has been pressed
     */
    public static final int RET_CANCEL = 0;
    public static final String CANCEL_MODIFICATIONS = "Cancel Modif";
    /*
     * A return status code - returned if OK button has been pressed.
     */
    public static final int RET_OK = 1;
    public static final String SAVE_MODIFICATIONS = "Save Modif";
    /*
     * A return status code - returned if Add button has been pressed.
     */
    public static final int RET_ADD = 2;
    public static final String ADD_NEW = "Save Modif and Add New";

    /**
     * Used to hold the returned status.
     */
    private int returnStatus = RET_CANCEL;
    private static final String MODIFIED_PROPERTY = "modified";
    /**
     * Set to true if this panel is used to update an item.
     */
    private boolean update = false;
    /**
     * Indicate if the save operation was run properly.
     */
    private boolean saved = false;
    /**
     * Indicate if there are not saved changes.
     */
    private boolean modified = false;
    /**
     * This object represent a db row that will be edited.
     */
    private Entity oldEntity;
    /**
     * This object represent the new inserted/edited row.
     */
    private Entity editedEntity;
    /**
     * The URL to the properties file that contain the default fields values for
     * this panel
     */
    private boolean modifAllowed = true;
    private CRUDPanel<Entity> crudPanel;
    /**
     * Creates new form MajPanel
     *
     * @param parent
     */
    public MajPanel(CRUDPanel parent) {
        super(parent);
        this.crudPanel = parent;
        this.dialog = new MajDialog(parent) {

            @Override
            public void doOnClose() {
                if (cancel()) {
                    super.doOnClose();
                }
            }
        };
    }

    public void initMajPanel(Component fieldsPanel) {
        initComponents();
        setActionsShortcuts();
        add(fieldsPanel, BorderLayout.CENTER);
    }

    public void showNewPanel(Entity e) {
        clearFields(); // clear all fileds
        loadPreferences();
        editEntity(e);// then init the fields by the given entity if not null;
        if (crudPanel != null) {
            EntityClass masterEntity = ((ResultSet_Panel) crudPanel).getMasterEntity();
            if (masterEntity.getId() > 0) {
                setChildEntity(masterEntity, false);
            }
        }
        getDialog().setTitle("Nouveau");
        showPanel(true);
    }

    public void showEditPanel(final Entity e) {
        clearFields();
        loadPreferences();
        editEntity(e);
        getDialog().setTitle("Maj: " + e);
        showPanel(true);
    }

    protected final Action goNextEntityAct = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (crudPanel != null) {
                crudPanel.down();
                editEntity(crudPanel.getSelectedEntity());
            }

        }
    };

    protected final Action goPrevEntityAct = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (crudPanel != null) {
                crudPanel.up();
                editEntity(crudPanel.getSelectedEntity());
            }
        }
    };

    private final Action goLastEntityAct = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (crudPanel != null) {
                crudPanel.last();
                editEntity(crudPanel.getSelectedEntity());
            }
        }
    };

    private final Action goFirstEntityAct = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (crudPanel != null) {
                crudPanel.first();
                editEntity(crudPanel.getSelectedEntity());
            }
        }
    };

    public void setCrudPanel(CRUDPanel<Entity> crudPanel) {
        this.crudPanel = crudPanel;
    }

    public CRUDPanel getCRUDPanel() {
        return crudPanel;
    }

    public int getReturnStatus() {
        return returnStatus;
    }

    public boolean isUpdate() {
        return update;
    }

    public boolean isSaved() {
        return saved;
    }

    public boolean isModified() {
        return modified;
    }

    /**
     * Holds the entity being edited by this panel
     *
     * @return
     */
    public Entity getEditedEntity() {
        return editedEntity;
    }

    /**
     * Holds the old entity being updated in case of update operation.
     *
     * @return
     */
    public Entity getOldEntity() {
        return oldEntity;
    }

    public boolean isAllowEdit() {
        return modifAllowed;
    }
    // Setters

    public void setParentPanel(CRUDPanel parentPanel) {
        this.crudPanel = parentPanel;
    }

    public void setFieldsPanel(JPanel fieldsPanel) {
        this.add(fieldsPanel, BorderLayout.CENTER);
    }

    public void setChildEntity(EntityClass childEntity, boolean allowChng) {
    }

    public void setEditPermission(boolean allow) {
        okAction.setEnabled(allow);
        addAction.setEnabled(allow);
    }

    public void setIsUpdate(boolean isUpdate) {
        this.update = isUpdate;
    }

    public void setSaved(boolean isSaved) {
        this.saved = isSaved;
    }

    public void setModified(boolean modified) {
        //System.out.println("setModified! " + modified);
        if (modified && !this.modified) {
            firePropertyChange(MODIFIED_PROPERTY, this.modified, modified);
        }
        this.modified = modified;
    }

    public void setEditedEntity(Entity editedObject) {
        this.editedEntity = editedObject;
    }

    public void setOldEntity(Entity oldObject) {
        this.oldEntity = oldObject;
    }

    /**
     * Allow or forbid this Panel to do changes to the entity that is represent.
     * Redefine this method in subclasses to enable or deseable the components
     * that do changes.
     *
     * @param allow
     */
    public void setModifAllowed(boolean allow) {
        this.modifAllowed = allow;
    }

    public boolean isModifAllowed() {
        return modifAllowed;
    }

    /**
     * Initialize this MajPanel to edit the given entity.
     *
     * @param entity
     */
    public void editEntity(Entity entity) {
        setSaved(false);
        setIsUpdate(false);
        setEditedEntity(entity);
        if (entity == null || entity.getId() < 0) {
            clearFields();
        } else {
            if (entity.getId() > 0) {// update entity;
                setIsUpdate(true);
                setOldEntity(entity);
                getDialog().setTitle("Maj: " + entity);
            }
            initFields(entity);// init the MajPanel with new entity.
        }
    }

    /**
     * @return the TableDAO object corresponding to the Edited entity.
     */
    public abstract DAO getTableDAO();

    /**
     * Initiate the fields with appropriate values from the edited entity in
     * case of update.
     *
     * @param oldEntity the id of the object to initiate fields with its values.
     */
    public abstract void initFields(Entity oldEntity);

    /**
     * Validate the input values
     *
     * @return true if all fields are conform to constraints
     */
    public abstract boolean verifyFields();

    /**
     * All sub classes must implement this method, to initialize theirs fields!
     * Called directly after ok() & add() methods , to avoid any confliction the
     * next use.
     */
    public abstract void clearFields();

    /**
     * Validate the update to DB if some fields are updated.
     *
     * @return
     */
    public boolean update() {
        System.out.print("updating...");
        // Set the id of the edited entity with the id of updated entity.
        if (getEditedEntity().getId() == 0) {
            getEditedEntity().setId(getOldEntity().getId());
        }
        // !getEditedEntity().equals(getOldEntity())
        if (true) {
            boolean updated = getTableDAO().update(getEditedEntity());
            System.out.println(getEditedEntity().toString() + "..." + updated);
            setOldEntity(getEditedEntity());
            return updated;
        } else {
            System.out.println("....No changes made!");
            return true;
        }
    }

    /**
     * Insert the new entity and update it with the generated key.
     *
     * @return true if inserted without problem.
     */
    public boolean insert() {
        System.out.print("Inserting...");
        boolean insert = getTableDAO().insert(getEditedEntity());
        System.out.println(getEditedEntity().toString() + "..." + insert);
        setOldEntity(getEditedEntity());
        return insert;
    }

    /**
     * Save the modification to the Data Base.
     *
     * @return true if save complete
     */
    public boolean save() {
        savePreferences();
        boolean save;
        /*
         * if the entity is already saved then do not re-insert it but just call
         * the update methode; this case hapen if the edited entity have childs
         * or details like 'Achats' and 'Ventes'
         */
        if (isUpdate() || isSaved()) {
            save = update() || isSaved();
        } else {
            save = insert();
        }
        setSaved(save);
        return save;
    }

    /**
     * Insert new item without closing the dialog.
     *
     * @return
     */
    public boolean add() {
        boolean add = save();
        if (add) {
            dialog.setTitle("Nouveau");
            setSaved(false);
            setIsUpdate(false);
            if (crudPanel != null) {
                crudPanel.reload();
                crudPanel.clearSelection();
            }
            clearFields();
        }
        returnStatus = MajPanel.RET_ADD;
        return add;
    }
    public Action addAction = new AbstractAction("Nouveau") {

        {
            putValue(Action.ACCELERATOR_KEY, "CTRL+N");
            putValue(Action.SHORT_DESCRIPTION, "Sauvegarder et Ajouter un nouveau élément (CTRL+N)");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            add();
        }
    };

    /**
     * This method must be reloaded to call commit method if the auto commit is
     * set to false.
     *
     * @return
     */
    public boolean ok() {
        returnStatus = MajPanel.RET_OK;
        if (save()) {
            getTableDAO().commit();
            clearFields();
            return true;
        } else {
            return false;
        }
    }
    public Action okAction = new AbstractAction("OK") {

        {
            putValue(Action.ACTION_COMMAND_KEY, SAVE_MODIFICATIONS);
            putValue(Action.ACCELERATOR_KEY, "CTRL+S");
            putValue(Action.SHORT_DESCRIPTION, "Sauvegarder les modification (CTRL+S)");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (ok()) {
                if (getTopLevelAncestor() instanceof MyJDialog) {
                    ((MyJDialog) getTopLevelAncestor()).closeDialog();
                }
            }
        }
    };

    /**
     * Cancel the modifications made in this panel.
     *
     * @return
     */
    public boolean cancel() {
        if (!saved) {
            int rep = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment annuler les modifications?", "Annulation!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (rep == JOptionPane.YES_OPTION) {
                returnStatus = MajPanel.RET_CANCEL;
                clearFields();
                return true;
            } else {
                returnStatus = -1;
                return false;
            }
        }
        setSaved(false);
        setIsUpdate(false);
        return true;
    }

    public Action cancelAction = new AbstractAction("Annuler") {

        {
            putValue(Action.ACTION_COMMAND_KEY, CANCEL_MODIFICATIONS);
            putValue(Action.ACCELERATOR_KEY, "CTRL+Z");
            putValue(Action.SHORT_DESCRIPTION, "Annuler les modification (ESC)");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            dialog.doOnClose();// overrided in constructor to call cancel before closing!
        }
    };

    /**
     * Show help about this panel.
     */
    public void help() {
        JOptionPane.showMessageDialog(this, "L'aide est non disponible encore\nContacter le développeur du logiciel si vous rencontrer des problèmes", "Aide", JOptionPane.INFORMATION_MESSAGE);
    }
    public Action helpAction = new AbstractAction() {

        {
            putValue(Action.ACCELERATOR_KEY, "F1");
            putValue(Action.SHORT_DESCRIPTION, "Afficher l'aide (F1)");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            help();
        }
    };

    /**
     * Listen to children/master liste panels row selection changes to reflect
     * the selected row on the corresponding fields.
     *
     * @param evt
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof RSTablePanel) {
            RSTablePanel panel = (RSTablePanel) evt.getSource();
            if (evt.getPropertyName().equalsIgnoreCase(ResultSet_Panel.ENTITY_TO_VIEW_PROPERTY)) {
                // Call this method in every case, even if no selection is made; (sel_row < 0).
                viewSelEntity(panel);
                viewSelEntity(panel.getSelectedEntity());
            }
            if (evt.getPropertyName().equalsIgnoreCase(ResultSet_Panel.ENTITY_TO_EDIT_PROPERTY)) {
                Entity entity = (Entity) evt.getNewValue();
                if (entity != null) {
                    editEntity(entity);
                }
            }
        }
    }

    /**
     * Invoked when a child list panel changes the selected row.
     *
     * @param listePanel
     */
    public void viewSelEntity(RSTablePanel listePanel) {

    }

    public void viewSelEntity(EntityClass entity) {

    }

    public void setActionsShortcuts() {
        doOnPress(KeyEvent.VK_LEFT, KeyEvent.ALT_DOWN_MASK, goNextEntityAct, JComponent.WHEN_IN_FOCUSED_WINDOW);
        doOnPress(KeyEvent.VK_RIGHT, KeyEvent.ALT_DOWN_MASK, goPrevEntityAct, JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        doOnPress(KeyEvent.VK_END, KeyEvent.ALT_DOWN_MASK, goLastEntityAct, JComponent.WHEN_IN_FOCUSED_WINDOW);
        doOnPress(KeyEvent.VK_HOME, KeyEvent.ALT_DOWN_MASK, goFirstEntityAct, JComponent.WHEN_IN_FOCUSED_WINDOW);

        doOnPress(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK, okAction, JComponent.WHEN_IN_FOCUSED_WINDOW);
        doOnPress(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK, addAction, JComponent.WHEN_IN_FOCUSED_WINDOW);
        doOnPress(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK, cancelAction, JComponent.WHEN_IN_FOCUSED_WINDOW);
        doOnPress(KeyEvent.VK_F1, 0, helpAction, JComponent.WHEN_IN_FOCUSED_WINDOW);
        doOnPress(KeyEvent.VK_ENTER, 0, okAction, JComponent.WHEN_IN_FOCUSED_WINDOW);
        doOnPress(KeyEvent.VK_ESCAPE, 0, cancelAction, JComponent.WHEN_IN_FOCUSED_WINDOW);
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

        navigationPanel = new javax.swing.JPanel();
        firstButton = new javax.swing.JButton();
        previousButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        lastButton = new javax.swing.JButton();
        buttonsPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        insertButton = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        calcButton = new javax.swing.JButton();
        helpButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        setFocusCycleRoot(true);
        setLayout(new java.awt.BorderLayout());

        navigationPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 2, 0));
        navigationPanel.setPreferredSize(new java.awt.Dimension(10, 26));
        navigationPanel.setLayout(new java.awt.GridBagLayout());

        firstButton.setAction(goFirstEntityAct);
        firstButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/first2-16.png"))); // NOI18N
        firstButton.setText("");
        firstButton.setToolTipText("Aller au premier élement.");
        firstButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        firstButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        firstButton.setMaximumSize(new java.awt.Dimension(26, 26));
        firstButton.setMinimumSize(new java.awt.Dimension(26, 26));
        firstButton.setPreferredSize(new java.awt.Dimension(26, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        navigationPanel.add(firstButton, gridBagConstraints);

        previousButton.setAction(goPrevEntityAct);
        previousButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/prev16.png"))); // NOI18N
        previousButton.setText("Alt+Gauche");
        previousButton.setToolTipText("Précédent");
        previousButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        previousButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        previousButton.setMaximumSize(new java.awt.Dimension(26, 26));
        previousButton.setMinimumSize(new java.awt.Dimension(26, 26));
        previousButton.setPreferredSize(new java.awt.Dimension(26, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        navigationPanel.add(previousButton, gridBagConstraints);

        nextButton.setAction(goNextEntityAct);
        nextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/next16.png"))); // NOI18N
        nextButton.setText("Alt+Droite");
        nextButton.setToolTipText("Suivant");
        nextButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        nextButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        nextButton.setMaximumSize(new java.awt.Dimension(26, 26));
        nextButton.setMinimumSize(new java.awt.Dimension(26, 26));
        nextButton.setPreferredSize(new java.awt.Dimension(26, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        navigationPanel.add(nextButton, gridBagConstraints);

        lastButton.setAction(goLastEntityAct);
        lastButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/last2-16.png"))); // NOI18N
        lastButton.setText("");
        lastButton.setToolTipText("Aller au dernier élement.");
        lastButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        lastButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        lastButton.setMaximumSize(new java.awt.Dimension(26, 26));
        lastButton.setMinimumSize(new java.awt.Dimension(26, 26));
        lastButton.setPreferredSize(new java.awt.Dimension(26, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        navigationPanel.add(lastButton, gridBagConstraints);

        add(navigationPanel, java.awt.BorderLayout.NORTH);

        buttonsPanel.setPreferredSize(new java.awt.Dimension(447, 26));
        buttonsPanel.setLayout(new javax.swing.BoxLayout(buttonsPanel, javax.swing.BoxLayout.LINE_AXIS));

        okButton.setAction(okAction);
        okButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/save16.png"))); // NOI18N
        buttonsPanel.add(okButton);

        insertButton.setAction(addAction);
        insertButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/new16.png"))); // NOI18N
        insertButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        buttonsPanel.add(insertButton);
        buttonsPanel.add(filler2);

        calcButton.setAction(main.MainApp.calcAction);
        calcButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/calc16.png"))); // NOI18N
        calcButton.setFocusable(false);
        buttonsPanel.add(calcButton);

        helpButton.setAction(helpAction);
        helpButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/help16.png"))); // NOI18N
        helpButton.setFocusable(false);
        buttonsPanel.add(helpButton);

        cancelButton.setAction(cancelAction);
        cancelButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/cancel16.png"))); // NOI18N
        buttonsPanel.add(cancelButton);

        add(buttonsPanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JPanel buttonsPanel;
    private javax.swing.JButton calcButton;
    public javax.swing.JButton cancelButton;
    private javax.swing.Box.Filler filler2;
    public javax.swing.JButton firstButton;
    private javax.swing.JButton helpButton;
    private javax.swing.JButton insertButton;
    private javax.swing.JButton lastButton;
    protected javax.swing.JPanel navigationPanel;
    public javax.swing.JButton nextButton;
    public javax.swing.JButton okButton;
    private javax.swing.JButton previousButton;
    // End of variables declaration//GEN-END:variables

    public static void main(String[] args) {
        new MajProduitPanel(null).showNewPanel(null);
    }
}
