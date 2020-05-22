
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myModels;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;


/**
 *
 * @author alilo
 */
public interface ResultSet2DataModel {
    public void loadResultSet(ResultSet rs);
    public void readRows(ResultSet rs);
    public void readCols(ResultSet rs);
    // Setters //
    public void addSelectedRow(int row);
    public void clearSelectedRows();
    // Getters //
    public int getRowCount();
    public List<Integer> getSelectedRows();
    public List<Integer> getSelectedIDs();
    public int getColumnIndex(String colName);
    public boolean isCheckable();
    public boolean isRowChecked(int row);
    public Map<Integer, Boolean> getCheckedIDs();
}