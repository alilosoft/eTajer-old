package panels;

import entities.EntityClass;
import java.awt.Container;
import java.awt.event.*;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import myComponents.MyJTable;
import myModels.FilterColsComboBoxModel;
import myModels.RSTableModel;
import tools.ExceptionReporting;

public abstract class RSTablePanel<Entity extends EntityClass, DAO extends dao.TableDAO<Entity, ?>> extends ResultSet_Panel<Entity, DAO, RSTableModel> {

    public final String VISIBLE_COLS = this.getClass().getName() + "_VisibleCols";

    protected MyJTable table;
    private final TablePanelSettings settingsPanel = new TablePanelSettings(this);
    ;
    private String[] filterCols;
    private String[] visibleCols;
    private int filterMode;

    /**
     * Create uncheckable table.
     *
     * @param owner
     */
    public RSTablePanel(Container owner) {
        this(owner, false);
    }

    public RSTablePanel(Container owner, boolean checkable) {
        super(owner);
        model = new RSTableModel(getResultSet(), checkable);

        table = new MyJTable(model) {

            @Override
            public void doOnSelectionChange(ListSelectionEvent evt, int tabSelRow, int modelSelRow) {
                setModelSelectedRow(modelSelRow);
                doOnTableSelectionChanged(tabSelRow, modelSelRow);
            }

            @Override
            public void doOnRowCheckChange(int tableRow, int modelRow, final boolean checked) {
                doOnModelRowCheckChanged(modelRow, checked);
            }

            @Override
            public void doOnTableRowSorte() {
                doOnTableRowSortChanged();
            }
        };

        model.addTableModelListener((TableModelEvent e) -> {
            doOnTableModelChanged();
        });

        table.setFillsViewportHeight(true);
        scrollPan.setViewportView(table);
        customizeUI();
        initTableBehavior();
    }

    @Override
    protected final void customizeUI() {
        initTableView();
        super.customizeUI();
    }

    @Override
    public void loadPreferences() {
        // load filter prefs
        filterMode = getIntPreference(PREF_FILTER_MODE, MyJTable.START_WITH_FILTER);
        String prefFilterCols = getPreference(PREF_FILTER_COLS, "*");
        if (prefFilterCols.equals("*")) {
            filterCols = getModel().getColNames().toArray(new String[0]);
        } else {
            filterCols = prefFilterCols.split("\\s");
        }

        String visCols = getPreference(VISIBLE_COLS, "");
        //System.out.println("load prefs-> Visible Cols: " + VISIBLE_COLS+" = "+ visCols);
        if (visCols.equals("")) {
            for (String col : table.getModel().getColNames()) {
                if (table.isColumnVisible(col)) {
                    visCols += col + " ";
                }
            }
            visibleCols = visCols.split("\\s");
        } else {
            setVisibleCols(visCols.split("\\s"));
        }
    }

    public String[] getFilterColumns() {
        return filterCols;
    }

    public void setFilterCols(String[] filterCols) {
        this.filterCols = filterCols;
        String cols = "";
        for (String col : filterCols) {
            cols += col + " ";
        }
        savePreference(PREF_FILTER_COLS, cols);
    }

    public String[] getVisibleCols() {
        return visibleCols;
    }

    public void setVisibleCols(String[] visibleCols) {
        this.visibleCols = visibleCols;
        String visCols = "";
        List<String> hidenCols = model.getColNames();
        // To keep ordering: all cols must be hidded then show only visible.
        for (String col : hidenCols) {
            table.setColumnVisible(col, false);
        }
        for (String col : visibleCols) {
            visCols += col + " ";
            //hidenCols.remove(col);
            table.setColumnVisible(col, true);
        }
        //System.out.println("save prefs-> Visible Cols: " + VISIBLE_COLS+" = "+ visCols);
        savePreference(VISIBLE_COLS, visCols);
    }

    public int getFilterMode() {
        return filterMode;
    }

    public void setFilterMode(int filterMode) {
        this.filterMode = filterMode;
        savePreference(PREF_FILTER_MODE, filterMode);
    }

    @Override
    public void openSettings() {
        settingsPanel.showSettings();
    }

    @Override
    public ComboBoxModel getFilterColsComboBoxModel() {
        return new FilterColsComboBoxModel(table);
    }

    @Override
    public void filterList(String input) {
        filterList(getFilterColumns(), input, getFilterMode());
    }

    private final Notification notif = Notification.createNotification(1, "Nbr.Eléments", "0", java.awt.Color.blue, "Tahoma", 13, false, true);

    public void doOnTableModelChanged() {
        String info = getTable().getRowCount() + "/" + getModel().getRowCount();
        notif.setMess(info);
        addNotification(notif, true);
    }

    public void doOnTableRowSortChanged() {
        String info = getTable().getRowCount() + "/" + getModel().getRowCount();
        notif.setMess(info);
        addNotification(notif, true);
    }

    //Setters
    /**
     * Set the default behaviour for mouse double clicked on the JTable. This
     * method is used by the find dialogue to replace the default behaviour from
     * editing the selected row to closing dialogue.
     *
     * @param mouseClickeListener
     */
    public void setMouseClickListener(MouseAdapter mouseClickeListener) {
        getTable().removeMouseListener(this.mouseClickListener);
        this.mouseClickListener = mouseClickeListener;
        getTable().addMouseListener(mouseClickeListener);
    }

    public void setEnterKeyListener(KeyListener enterKeyListener) {
        getTable().removeKeyListener(this.enterKeyListener);
        this.enterKeyListener = enterKeyListener;
        getTable().addKeyListener(enterKeyListener);
    }

    @Override
    public final void setModel(RSTableModel model) {
        this.model = model;
        this.table.setModel(model);
    }

    /**
     * If the model is checkable, this method sets the check status of an entity
     * to true or false programaticely.
     *
     * @param entity the entity to change their check status.
     * @param check true or false.
     */
    @Override
    public void setEntityChecke(Entity entity, boolean check) {
        //System.out.println("setEntityCheck: " + entity + "...." + check);
        int modelIndOfEntity = getModel().getRowIndexOfEntity(entity);
        if (modelIndOfEntity >= 0) {
            getModel().setRowCheck(modelIndOfEntity, check);
            doOnEntityCheckChanged(entity, check);
        }
    }

    /**
     * Check the rows corresponding to the list of entities programmatically.
     *
     * @param checkedEntities
     */
    public void setCheckedEntities(List<Entity> checkedEntities) {
        for (Entity entity : checkedEntities) {
            setEntityChecke(entity, true);
        }
    }

    public void clearCheckedEntities() {
        for (Object e : getCheckedEntities().toArray()) {
            setEntityChecke((Entity) e, false);
        }
    }

    @Override
    public void reload() {
        super.reload();
        // Reselect edited row after reload.
        selectEntity(getOldSelectedEntity());
    }

    @Override
    public void clearSelection() {
        getTable().getSelectionModel().clearSelection();
    }

    //Getters
    public final MyJTable getTable() {
        return table;
    }

    @Override
    public final RSTableModel getModel() {
        return model;
    }

    public MouseAdapter getMouseDoubleClickedListener() {
        return mouseClickListener;
    }

    /**
     * Insert new item then select it in the list.
     */
    @Override
    public void insert() {
        super.insert();
        // Select the new row after reload;

        int newID = getTableDAO().getGeneratedID();
        System.out.println("new id: " + newID);
        int indexOfNewID = getModel().getRowIndexOfValueAtCol(newID, "ID");

        getTable().selectModelRow(indexOfNewID);
    }

    /**
     * Filter the Rows liste in this panel based on a column criteria.
     *
     * @param colName the name of column to filter on;
     * @param input the pattern input to find;
     * @param matchType
     */
    public void filterList(String colName, String input, int matchType) {
        getTable().andFilter(colName, input, matchType);
        setFilterResultsCount(getTable().getRowCount());
    }

    /**
     * Filter the Rows List based on one or more columns criteria.
     *
     * @param colNames
     * @param input
     * @param matchType
     */
    public void filterList(String[] colNames, String input, int matchType) {
        getTable().orFilter(colNames, input, matchType);
        setFilterResultsCount(getTable().getRowCount());
    }

    @Override
    public void doOnFilterFildKeyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            //getTable().requestFocus();
            if (getTable().getRowCount() > 0 && getTable().getSelectedRow() < 0) {
                getTable().selectViewRow(0);// if no selection, select the first row in table.
            }
            for (KeyListener kl : getTable().getKeyListeners()) {
                kl.keyPressed(e);
            }
            e.consume();
        }
    }

    @Override
    public void masterRowChanged(String masterColName) {
        if (getMasterEntity().getId() > 0) {
            filterList(masterColName, getMasterEntity().getId().toString(), MyJTable.EXACT_MATCH_FILTER);
        } else {
            filterList(masterColName, "", MyJTable.ANY_MATCH_FILTER);
        }
    }

    /**
     * Initialize the table view (show or hide some columns, set columns
     * preferred widths...etc). Appearance options have priority against this
     * method.
     *
     * @return this panel with the customized table.
     */
    public abstract RSTablePanel initTableView();

    /**
     * Add keys and mouse listeners to the table.
     */
    public final void initTableBehavior() {
        getTable().addKeyListener(enterKeyListener);
        getTable().addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                doOnTableKeyReleased(e);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();// cancel the default event (ie: next record)
                }
            }
        });
        getTable().addMouseListener(mouseClickListener);

    }
    private MouseAdapter mouseClickListener = new MouseAdapter() {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                doOnTableMouseDoubleClicked(e);
            }
        }
    };

    public void doOnTableKeyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (getModel().isCheckable() && getTable().getSelectedRow() >= 0) {
                int selRow = getModel().getSelectedRows().get(0);
                boolean isRowChecked = getModel().isRowChecked(selRow);
                getModel().setRowCheck(selRow, !isRowChecked);
                doOnModelRowCheckChanged(selRow, !isRowChecked);
                getTable().selectModelRow(selRow);
            }
        }
    }
    private KeyListener enterKeyListener = new KeyAdapter() {

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                if (getTable().getSelectedRow() >= 0) {
                    edit();
                    e.consume();
                }
                if (getModel().isCheckable()) {
                    //e.consume();
                }
            }
        }
    };

    public void doOnTableMouseDoubleClicked(MouseEvent e) {
        edit();
    }

    @Override
    public void up() {
        int selRow = getTable().getSelectedRow();
        if (selRow <= 0) {
            getTable().selectViewRow(0);
        } else {
            getTable().selectViewRow(selRow - 1);
        }
    }

    @Override
    public void down() {
        int selRow = getTable().getSelectedRow();
        if (selRow < getTable().getRowCount() - 1) {
            getTable().selectViewRow(selRow + 1);
        }
    }

    @Override
    public void first() {
        getTable().selectViewRow(0);
    }

    @Override
    public void last() {
        int last = getTable().getRowCount() - 1;
        getTable().selectViewRow(last);
    }

    public void doOnTableSelectionChanged(int tableRow, int modelRow) {
    }

    /**
     * Invoked when a JTable is checkable and a row check state changed.<br>
     * This method is designed to be overridden in subclasses to do something if
     * a row check is changed.<br> Its add also the selected entity -if checked-
     * to the list of checked entities or remove it -if unchecked-.
     *
     * @param rowIndex
     * @param checked the state of the row (checked or no)
     */
    public void doOnModelRowCheckChanged(int rowIndex, boolean checked) {
        int checkedID = (Integer) getModel().getValueAt(rowIndex, "ID");
        Entity e = getTableDAO().getObjectByID(checkedID);
        setEntityChecke(e, checked);
    }

    @Override
    public void selectEntity(Entity entity) {
        if (entity == null) {
            selectID(0);
        } else {
            selectID(entity.getId());
        }
    }

    @Override
    public void selectID(int id) {
        int modelIndex = getModel().getRowIndexOfValueAtCol(id, "ID");
        getTable().selectModelRow(modelIndex);
    }

    /**
     * Checks a row by its model index, then invoke the doOnModelRowCheck method
     * on it.
     *
     * @param modelRow
     * @param checked
     */
    public void setModelRowCheck(int modelRow, boolean checked) {
        if (modelRow >= 0) {
            getModel().setRowCheck(modelRow, checked);
            doOnModelRowCheckChanged(modelRow, checked);
        }
    }

    public void setTableRowCheck(int tableRow, boolean checked) {
        if (tableRow >= 0) {
            int modelRow = getTable().convertRowIndexToModel(tableRow);
            setModelRowCheck(modelRow, checked);

        }
    }
}

class ModelRowCheckNotifier implements Runnable {

    boolean ready = false;

    public ModelRowCheckNotifier(boolean ready) {
        this.ready = ready;

    }

    @Override
    public void run() {
        synchronized (this) {
            while (!ready) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    ExceptionReporting.showException(ex);
                }
            }
        }
    }

    synchronized public void resume(boolean ready) {
        this.ready = ready;
        notify();
    }
}
