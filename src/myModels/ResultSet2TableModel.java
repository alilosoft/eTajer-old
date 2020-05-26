package myModels;

import entities.EntityClass;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import java.util.logging.Level;
import javax.swing.JCheckBox;
import javax.swing.table.AbstractTableModel;
import tools.DateTools;
import tools.ExceptionReporting;
import tools.MessageReporting;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author alilo
 */
public class ResultSet2TableModel extends AbstractTableModel implements ResultSet2DataModel {

    private ResultSet resultSet = null;
    private ResultSetMetaData metaData;
    private List<String> colNames = null;
    private List<Class> colClasses = null;
    private int colCount = 0;
    
    private List<List<Object>> rows = Collections.synchronizedList(new ArrayList<>());
    
    
    //private int selectedRow = -1;
    private final List<Integer> selectedRows = Collections.synchronizedList(new ArrayList<Integer>());
    
    private boolean checkable = false;
    private final Map<Integer, Boolean> checkedIDs = new HashMap<>();

    //generate a Tabelmodel from resultset
    public ResultSet2TableModel(ResultSet rs, boolean checkable) {
        super();
        this.checkable = checkable;
        loadResultSet(rs);
    }

    /**
     * Read data from result set and load the model.
     *
     * @param rs
     */
    @Override
    public final void loadResultSet(ResultSet rs) {
        if(rs == null) return;
        MessageReporting.logOnly(Level.CONFIG, "loadResultSet()....."); 
        try {
            this.resultSet = rs;
            this.metaData = rs.getMetaData();
            this.colCount = metaData.getColumnCount();
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
        }

        this.colNames = getColNames();
        this.colClasses = getColClasses();

        //save the IDs of checked rows and restor thier state after relaod.
        if (rows != null && isCheckable()) {
            Iterator<List<Object>> it = rows.iterator();
            while (it.hasNext()) {
                List row = it.next();
                int id = (Integer) row.get(0);
                // if the model is checkable, so the column of the checkbox is always the last column.
                boolean checked = ((JCheckBox) row.get(row.size() - 1)).isSelected();
                checkedIDs.put(id, checked);
            }
        }

        rows = Collections.synchronizedList(new ArrayList<List<Object>>());
        List<Object> currentRow;
        try {
            resultSet.beforeFirst();
            while (resultSet.next()) {
                currentRow = Collections.synchronizedList(new ArrayList<Object>());
                for (int i = 1; i <= colCount; i++) {
                    currentRow.add(resultSet.getObject(i));
                }
                if (isCheckable()) {
                    int currentID = (Integer) currentRow.get(0);
                    if (checkedIDs.containsKey(currentID)) {
                        JCheckBox oldCheckBox = new JCheckBox();
                        oldCheckBox.setSelected(checkedIDs.get(currentID));
                        currentRow.add(oldCheckBox);
                    } else {
                        currentRow.add(new JCheckBox());
                        checkedIDs.put(currentID, false);
                    }
                }
                rows.add(currentRow);
            }
        } catch (SQLException ex) {
            MessageReporting.showMessage(Level.SEVERE, getClass(), "loadResultSet()", "Loading model from resultset fail!");
            ExceptionReporting.showException(ex);
        } finally {
            fireTableDataChanged();
            try {
                resultSet.close();
            } catch (SQLException ex) {
                ExceptionReporting.showException(ex);
            }
        }
    }

    @Override
    public void addSelectedRow(int modelRow) {
        selectedRows.add(modelRow);
    }

    @Override
    public void clearSelectedRows() {
        selectedRows.clear();
    }

    public void setCheckable(boolean checkable) {
        this.checkable = checkable;
    }

    /**
     * If the model is checkable, sets the row check to checked.
     *
     * @param row the model index of the checked row.
     * @param checked true to select or false otherwise.
     */
    public void setRowCheck(int row, boolean checked) {
        if (isCheckable()) {
            JCheckBox checkBox = (JCheckBox) getValueAt(row, getColumnCount() - 1);
            checkBox.setSelected(checked);
            fireTableDataChanged();
        }
    }

    @Override
    public List<Integer> getSelectedRows() {
        return selectedRows;
    }

    private final List<Integer> selectedIDs = Collections.synchronizedList(new ArrayList<Integer>());

    @Override
    public List<Integer> getSelectedIDs() {
        selectedIDs.clear();
        for (int row : selectedRows) {
            selectedIDs.add((Integer) getValueAt(row, getColumnIndex("ID")));
        }
        return selectedIDs;
    }

    @Override
    public boolean isCheckable() {
        return checkable;
    }

    @Override
    public boolean isRowChecked(int row) {
        if (row >= 0 && isCheckable()) {
            JCheckBox checkBox = (JCheckBox) getValueAt(row, getColumnCount() - 1);
            return checkBox.isSelected();
        } else {
            return false;
        }
    }

    //public 
    @Override
    public Map<Integer, Boolean> getCheckedIDs() {
        return checkedIDs;
    }

    //return the column names at the begining
    public List<String> getColNames() {
        List<String> cNames = Collections.synchronizedList(new ArrayList<String>());

        for (int i = 1; i <= colCount; i++) {
            try {
                cNames.add(metaData.getColumnLabel(i));
            } catch (SQLException ex) {
                ExceptionReporting.showException(ex);
            }
        }
        if (isCheckable()) {
            cNames.add(" ");
        }
        return cNames;
    }

    //retourner un vector de classes des colonnes
    //pour le utiliser pour la methode getColumnClass
    //pour minimizer le cout de recherche de type
    public List<Class> getColClasses() {
        List<Class> listClasses = Collections.synchronizedList(new ArrayList<Class>());

        for (int i = 0; i < colCount; i++) {
            listClasses.add(getColClass(i));
        }
        try {
            if (isCheckable()) {
                listClasses.add(Class.forName("java.lang.Boolean"));
            }
        } catch (ClassNotFoundException ex) {
            ExceptionReporting.showException(ex);
        }
        return listClasses;
    }

    //data types inspirated from rowset exemple in java2s exemples
    //cette methode est utiliser comme aide de la methode getColClasses
    //juste pour eclairer le code
    public Class<?> getColClass(int columnIndex) {
        int type;
        String className;
        int length;
        try {
            type = metaData.getColumnType(columnIndex + 1);
            length = metaData.getPrecision(columnIndex + 1);
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
            return super.getColumnClass(columnIndex);
        }

        switch (type) {
            case Types.BOOLEAN: {
                className = "java.lang.Boolean";
                break;
            }
            case Types.TINYINT: {
                className = "java.lang.Byte";
                break;
            }
            case Types.SMALLINT: {
                className = "java.lang.Short";
                break;
            }
            case Types.INTEGER: {
                className = "java.lang.Integer";
                break;
            }
            case Types.BIGINT: {
                className = "java.lang.Long";
                break;
            }
            case Types.FLOAT:
            case Types.REAL: {
                className = "java.lang.Float";
                break;
            }
            case Types.DOUBLE: {
                className = "java.lang.Double";
                break;
            }
            case Types.NUMERIC: {
                className = "java.math.BigDecimal";
                break;
            }
            case Types.DECIMAL: {
                className = "java.math.BigDecimal";
                break;
            }
            case Types.CHAR: {
                if (length == 1) {
                    className = "java.lang.Boolean";
                    break;
                }
            }
            case Types.VARCHAR:
            case Types.LONGVARCHAR: {
                className = "java.lang.String";
                break;
            }
            case Types.DATE: {
                className = "java.sql.Date";
                break;
            }
            case Types.TIME: {
                className = "java.sql.Time";
                break;
            }
            case Types.TIMESTAMP: {
                className = "java.sql.Timestamp";
                break;
            }
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY: {
                className = "byte[]";
                break;
            }
            case Types.OTHER:
            case Types.JAVA_OBJECT: {
                className = "java.lang.Object";
                break;
            }
            case Types.CLOB: {
                className = "java.sql.Clob";
                break;
            }
            case Types.BLOB: {
                className = "java.ssql.Blob";
                break;
            }
            case Types.REF: {
                className = "java.sql.Ref";
                break;
            }
            case Types.STRUCT: {
                className = "java.sql.Struct";
                break;
            }
            default: {
                return super.getColumnClass(columnIndex);
            }
        }
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ex) {
            ExceptionReporting.showException(ex);
            return super.getColumnClass(columnIndex);
        }
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        if (isCheckable()) {
            return colCount + 1;
        } else {
            return colCount;
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || columnIndex < 0) {
            return null;
        }
        Object val = rows.get(rowIndex).get(columnIndex);
        if (getColumnClass(columnIndex).getName().equals("java.sql.Date")) {
            if (val != null && !DateTools.isMaxSqlTime((java.sql.Date) val)) {
                val = DateTools.SHORT_DATE.format(val);
            } else {
                val = "∞";
            }
        }
        return val;
    }

    public Object getValueAt(int rowIndex, String columnName) {
        return getValueAt(rowIndex, getColumnIndex(columnName));
    }

    @Override
    public String getColumnName(int column) {
        return colNames.get(column);
    }

    /**
     * Get the model index of specified column.
     *
     * @param colName the column name.
     * @return the column index if exist else return -1.
     */
    @Override
    public int getColumnIndex(String colName) {
        return colNames.indexOf(colName);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return colClasses.get(columnIndex);
    }

    /**
     * return the index of a specified row value at a colIndex<br> return -1 if
     * not found
     *
     * @param value
     * @param colIndex
     * @return
     */
    synchronized public int getRowIndexOfValueAtCol(Object value, int colIndex) {
        int index = 0;
        Iterator<List<Object>> rowsIter = rows.iterator();
        while (rowsIter.hasNext()) {
            List row = rowsIter.next();
            if (value.equals(row.get(colIndex))) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public int getRowIndexOfValueAtCol(Object value, String colName) {
        try {
            return getRowIndexOfValueAtCol(value, getColumnIndex(colName));
        } catch (Exception e) {
            ExceptionReporting.showException(e);
            return -1;
        }
    }

    /**
     * This method retirn the index of the entity by searshing its id in the ID
     * column;
     *
     * @param entity
     * @return the row index if the ID wase found, else -1;
     */
    public int getRowIndexOfEntity(EntityClass entity) {
        if (entity == null || entity.getId() == 0) {
            return -1;
        } else {
            return getRowIndexOfValueAtCol(entity.getId(), "ID");
        }
    }

    /**
     * Removes the row at <code>row</code> from the model. Notification of the
     * row being removed will be sent to all the listeners.
     *
     * @param row the row index of the row to be removed
     * @throws ArrayIndexOutOfBoundsException if the row was invalid
     */
    public void removeRow(int row) {
        rows.remove(row);
        fireTableRowsDeleted(row, row);
    }

    /**
     * remove a all rows matches the specified rowValue at colIndex,
     */
    public int removeRowByValueAtCol(Object rowValue, int colIndex) {
        int nbrMaches = 0;
        for (int rowIndex = getRowIndexOfValueAtCol(rowValue, colIndex); rowIndex != -1;) {
            removeRow(rowIndex);
            nbrMaches++;
        }
        return nbrMaches;
    }

    public void removeRowsByValueAtCol(ArrayList rowValues, int colIndex) {
        if (!rowValues.isEmpty()) {
            Iterator valIter = rowValues.iterator();
            while (valIter.hasNext()) {
                Object value = valIter.next();
                removeRowByValueAtCol(value, colIndex);
            }
        }

    }
}
