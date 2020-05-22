/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package panels;

import entities.EntityClass;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import myModels.ResultSet2DataModel;
import dao.TableDAO;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author alilo
 * @param <Entity>
 * @param <DAO>
 * @param <Model>
 */
public abstract class ResultSet_Panel<Entity extends EntityClass, DAO extends TableDAO, Model extends ResultSet2DataModel> extends CRUDPanel<Entity> implements PropertyChangeListener {

    /**
     * Bean property used to listen for changes in the data-model selection of
     * the JTable/JList hold by this panel.
     */
    public static final String SELECTED_ROW_PROPERTY = "selectedRow";
    public static final String MASTER_ROW_PROPERTY = "masterRow";
    /**
     * This property used to notify listener that the 'entity to view '
     * selection is changed.
     */
    public static final String ENTITY_TO_VIEW_PROPERTY = "viewEntity";
    /**
     * This property used to notify listener that the 'entity to edit '
     * selection is changed.
     */
    public static final String ENTITY_TO_EDIT_PROPERTY = "editEntity";
    /**
     * The data-model of JTable/JList hold by this panel.
     */
    protected Model model;
    /**
     * The index of the model selected row.
     */
    protected int modelSelectedRow = -1;
    /**
     * The index of previous model selected row.
     */
    protected int oldModelSelRow = -1;
    /**
     * The entity object representing the selected row.
     */
    private EntityClass selectedEntity = new EntityClass();
    /**
     * The entity object representing the previously selected row.
     */
    private EntityClass oldSelectedEntity = new EntityClass();
    /**
     * The list of checked entities, if the model is checkable.
     */
    private final List<Entity> checkedEntities = Collections.synchronizedList(new ArrayList<Entity>());
    /**
     * Property change support used to add listeners for this bean properties
     * changes.
     */
    protected PropertyChangeSupport propertyChangeSupport;
    private EntityClass masterEntity = new EntityClass();
    private final Map<ResultSet_Panel, String> masterPanelsCols = new HashMap<>();

    public ResultSet_Panel(java.awt.Container owner) {
        super(owner);
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    // Abstract Metods
    public abstract DAO getTableDAO();

    public ResultSet getResultSet() {
        return getTableDAO().getAll();
    }

    //Setters
    public void setModel(Model model) {
        this.model = model;
    }

    public void setOldModelSelRow(int oldModelSelRow) {
        this.oldModelSelRow = oldModelSelRow;
    }

    /**
     *
     * @param rowIndex
     */
    public void setModelSelectedRow(int rowIndex) {
        setEnabledOperation(EDIT, rowIndex >= 0 && isAllowEdit());
        setEnabledOperation(DELETE, rowIndex >= 0 && isAllowDelete());
        // Save old values //
        oldModelSelRow = modelSelectedRow;
        oldSelectedEntity = getSelectedEntity();

        // Reflect the new changes //
        modelSelectedRow = rowIndex;
        getModel().clearSelectedRows();
        getModel().addSelectedRow(modelSelectedRow);
        if (rowIndex >= 0) {
            int selID = getModel().getSelectedIDs().get(0);
            setSelectedEntity(getTableDAO().getObjectByID(selID));
        } else {
            setSelectedEntity(new EntityClass());
        }
        getPropertyChangeSupport().firePropertyChange(ResultSet_Panel.SELECTED_ROW_PROPERTY, getOldModelSelRow(), getModelSelectedRow());
        getPropertyChangeSupport().firePropertyChange(ResultSet_Panel.MASTER_ROW_PROPERTY, getOldModelSelRow(), getModelSelectedRow());
        getPropertyChangeSupport().firePropertyChange(ResultSet_Panel.ENTITY_TO_VIEW_PROPERTY, getOldSelectedEntity(), getSelectedEntity());
        getPropertyChangeSupport().firePropertyChange(ResultSet_Panel.ENTITY_TO_EDIT_PROPERTY, getOldSelectedEntity(), getSelectedEntity());
    }

    private final Notification selectionNotif = Notification.createNotification(2, "Sél: ", "Aucune ligne sélectionnée", Color.DARK_GRAY, "Tahoma", 12, true, false);

    private void setSelectedEntity(EntityClass selectedEntity) {
        this.selectedEntity = selectedEntity;
        if (selectedEntity != null && selectedEntity.getId() > 0) {
            selectionNotif.setMess(selectedEntity.toString());
            addNotification(selectionNotif, false);
        } else {
            selectionNotif.setMess("Aucune sélection");
            addNotification(selectionNotif, false);
            //removeNotification(selectionNotif, false);
        }
    }

    /**
     * This method will invoked when a row check is changed, its add the checked
     * entity to the liste of checked enteties or remove it.
     *
     * @param entity
     * @param checked
     */
    public void doOnEntityCheckChanged(Entity entity, boolean checked) {
        if (checked) {
            if (!checkedEntities.contains(entity)) {
                checkedEntities.add(entity);
            }
        } else {
            checkedEntities.remove(entity);
        }
    }

    private void setOldSelectedEntity(EntityClass oldSelectedEntity) {
        this.oldSelectedEntity = oldSelectedEntity;
    }

    abstract public void selectID(int id);

    abstract public void selectEntity(Entity entity);

    abstract public void setEntityChecke(Entity entity, boolean check);

    public void setPropertyChangeSupport(PropertyChangeSupport propertyChangeSupport) {
        this.propertyChangeSupport = propertyChangeSupport;
    }

    /**
     * Sets the master entity, thats used to filter this liste, accordingly to
     * the shared field.
     *
     * @param masterEntity
     */
    public void setMasterEntity(EntityClass masterEntity) {
        this.masterEntity = masterEntity;
    }

    //Getters
    public Model getModel() {
        return model;
    }

    public int getOldModelSelRow() {
        return oldModelSelRow;
    }

    public int getModelSelectedRow() {
        return modelSelectedRow;
    }

    @Override
    public Entity getSelectedEntity() {
        return (Entity) selectedEntity;
    }

    public Entity getOldSelectedEntity() {
        return (Entity) oldSelectedEntity;
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    public Map<ResultSet_Panel, String> getMasterPanelsCols() {
        return masterPanelsCols;
    }

    public EntityClass getMasterEntity() {
        return masterEntity;
    }

    public List<Entity> getCheckedEntities() {
        return checkedEntities;
    }

    // CRUD operations
    @Override
    public void edit() {
        boolean waseChecked = false;
        if (isAllowEdit() && getModel().isCheckable() && getModel().isRowChecked(getModelSelectedRow())) {
            waseChecked = true;
            doOnEntityCheckChanged(getSelectedEntity(), false);
        }
        //setEntityToEdit(getSelectedEntity());
        super.edit();
        selectEntity(getOldSelectedEntity());
        if (waseChecked) {
            doOnEntityCheckChanged(getSelectedEntity(), true);
        }
    }

    @Override
    public void delete() {
        if (!isAllowDelete()) {
            JOptionPane.showMessageDialog(this, "Vous n'avez pas le droit de supprimer!!!\nContactez votre administrateur SVP!", "Attention...", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (getModelSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Aucun ligne séléctionné!!!\n Sélectionner la ligne a supprimer SVP!", "Attention...", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String confMess = "Voullez-vous vraiment supprimer les éléments séléctionnés?";
        int rep = JOptionPane.showConfirmDialog(this, confMess, "Suppression", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (rep == JOptionPane.YES_OPTION) {
            for (Integer id : getModel().getSelectedIDs()) {
                Entity entityToDelete = (Entity) new EntityClass(id);
                if (getModel().isCheckable()) {
                    setEntityChecke(entityToDelete, false);
                }
                getTableDAO().delete(id);
                getTableDAO().commit();
            }
        }
        super.delete();
    }

    /**
     * Reload the liste (JTable/JList) in the panel, using the getAll()
     * ResultSet if a specified ResultSet is used then this method must be
     * redefined.
     */
    @Override
    public void reload() {
        getModel().loadResultSet(getResultSet());
    }

    // Master details views management section
    /**
     * Set the master panel and the master col_index (foreign_key), in a
     * mater-details view
     *
     * @param masterPanel the master panel
     * @param masterColInd the column index of master column
     */
    public void setMasterPanel(ResultSet_Panel masterPanel, int masterColInd) {
        masterPanel.propertyChangeSupport.addPropertyChangeListener(ResultSet_Panel.MASTER_ROW_PROPERTY, this);
    }

    /**
     * Set the master panel and the master col_name (foreign_key), in a
     * mater-details view
     *
     * @param masterPanel the master panel
     * @param masterColName the column name of master column
     */
    public void setMasterPanel(ResultSet_Panel masterPanel, String masterColName) {
        masterPanel.getPropertyChangeSupport().addPropertyChangeListener(ResultSet_Panel.MASTER_ROW_PROPERTY, this);
        if (masterColName != null && masterColName.length() > 0) {
            masterPanelsCols.put(masterPanel, masterColName);
        }
    }

    /**
     * Listen to changes in master panel; if changed property is ModelSelRow
     * then filter the details panel table to show only the corresponding items;
     * the filter is based on the id only in this version.
     *
     * @param evt
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        ResultSet_Panel masterP;
        if (evt.getSource() instanceof ResultSet_Panel) {
            masterP = (ResultSet_Panel) evt.getSource();
            if (evt.getPropertyName().equalsIgnoreCase(ResultSet_Panel.MASTER_ROW_PROPERTY)) {
                setMasterEntity(masterP.getSelectedEntity());
                String masterCol = masterPanelsCols.get(masterP);
                if (masterCol != null) {
                    masterRowChanged(masterCol);
                }
            }
        }
    }

    /**
     * Invoked when the a selected row in master panel changed;
     *
     * @param masterColName
     */
    public abstract void masterRowChanged(String masterColName);
    /**
     * Add key stroke to the panel.
     */
}
