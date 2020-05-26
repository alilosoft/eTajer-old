/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myModels;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.JCheckBox;
import tools.ExceptionReporting;
import tools.MessageReporting;

/**
 * This class instances represent a MyJList model created from a ResultSet
 * object
 *
 * @author alilo
 */
public class ResultSet2ListeModel extends MyJListeModel implements ResultSet2DataModel {

    //the resultset that provides data to the model;
    private ResultSet resultSet;
    private ResultSetMetaData metaData;
    private int colCount;
    private List<String> colNames = null;
    private List<Class> colClasses = null;
    private List<List<Object>> data = null;
    private List<Integer> elementID = Collections.synchronizedList(new ArrayList<Integer>());
    private int idIndex;
    private String[] listedColsNames;
    private int selectedRow;
    //TODO use hach map to map ids with elements

    /**
     * Create a MyJList model from result set
     *
     * @param rs the result set to represent
     * @param idIndex the index of the id in a result set row,
     * @param elemenetCols the indexes of result set columns to put in the list
     * element
     * @param isCheckable is this list is checkable
     */
    public ResultSet2ListeModel(ResultSet rs, int idIndex, String[] listedColsNames, boolean isCheckable) {
        super(isCheckable);
        this.idIndex = idIndex;
        this.listedColsNames = listedColsNames;
        loadResultSet(rs);
    }

    @Override
    public final void loadResultSet(ResultSet rs) {
        elementID.clear();
        clear();

        try {
            this.resultSet = rs;
            this.metaData = rs.getMetaData();
            this.colCount = metaData.getColumnCount();
        } catch (SQLException ex) {
            MessageReporting.showMessage(Level.SEVERE, getClass(), "loadResultSet", "Loading model from resultset fail!");
            ExceptionReporting.showException(ex);
        }

        try {
            resultSet.beforeFirst();
            while (rs.next()) {
                elementID.add(rs.getInt(idIndex));
                String item = "";
                for (String colName : listedColsNames) {
                    item += rs.getObject(colName) + "  ";
                }
                addElement(item.trim());
            }
        } catch (SQLException ex) {
            MessageReporting.showMessage(Level.SEVERE, getClass(), "loadResultSet", "Creating element fail!!");
            ExceptionReporting.showException(ex);
        } finally {
            try {
                resultSet.close();
            } catch (SQLException ex) {
                ExceptionReporting.showException(ex);
            }
        }
    }

    //Setters
    public void setSelectedID(int id) {
        if (elementID.contains(id)) {
            int index = elementID.indexOf(id);
        }
    }

    public void setCheckedID(int id, boolean isChecked) {
        if (isCheckable()) {
            if (elementID.contains(id)) {
                int index = elementID.indexOf(id);
                ((JCheckBox) getElementAt(index)).setSelected(isChecked);
            }
        }
    }

    @Override
    public void addSelectedRow(int row) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    //Getters

    /**
     * Get the ID of elements in the liste.
     *
     * @param index
     * @return ID of specified index
     */
    public int getElementID(int index) {
        return elementID.get(index);
    }

    public int getIndexOfID(int id) {
        if (elementID.contains(id)) {
            return elementID.indexOf(id);
        } else {
            return -1;
        }
    }

    @Override
    public int getColumnIndex(String colName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Integer> getSelectedRows() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Integer> getSelectedIDs() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isRowChecked(int row) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<Integer, Boolean> getCheckedIDs() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getRowCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clearSelectedRows() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
