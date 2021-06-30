/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myModels;

import entities.EntityClass;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import dao.TableDAO;
import entities.EntityPK;
import tools.ExceptionReporting;

/**
 *
 * @author alilo
 * @param <Entity>
 * @param <DAO>
 */
public abstract class ResultSet2ComboBoxModel<Entity extends EntityClass, DAO extends TableDAO<Entity, EntityPK>> extends DefaultComboBoxModel {

    private ResultSet resultSet;
    private List<Integer> elementID = Collections.synchronizedList(new ArrayList<Integer>());
    private String[] listedColsNames;

    public ResultSet2ComboBoxModel(ResultSet rs) {
        this.resultSet = rs;
        loadModel(rs);
    }

    private void loadModel(ResultSet rs) {
        if (rs != null) {
            this.resultSet = rs;
        }
        
        try {
            while (resultSet.next()) {
                elementID.add(resultSet.getInt(1));
                addItem(resultSet);
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
        } finally {
            try {
                resultSet.close();
            } catch (SQLException ex) {
                ExceptionReporting.showException(ex);
            }
        }
    }

    /**
     * Override this method to add custom result set columns to the model.
     * This method by default adds the 2nd col to the model because the first col is added to IDs array. <br>
     * <b> Note: </b>This method is called for every row in the <code> rs </code>  so don't call it in a loop.
     * @param rs
     * @throws java.sql.SQLException
     */
    public void addItem(ResultSet rs) throws SQLException {
        addElement(rs.getObject(2));
    }

    public ResultSet2ComboBoxModel(ResultSet rs, String[] listedColsNames) {
        this.listedColsNames = listedColsNames;
        try {
            while (rs.next()) {
                elementID.add(rs.getInt(1));
                String item = "";
                for (String colName : listedColsNames) {
                    item += rs.getObject(colName) + "  ";
                }
                addElement(item);
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
        } finally {
            try {
                rs.close();
            } catch (SQLException ex) {
                ExceptionReporting.showException(ex);
            }
        }
    }

    public int getElementID(int index) {
        return elementID.get(index);
    }

    public void setSelectedID(int id) {
        if (elementID.contains(id)) {
            int index = elementID.indexOf(id);
            setSelectedItem(getElementAt(index));
        }
    }
    
    abstract public DAO getTableDAO();
    
    /**
     * Get the id of the selected Item. 
     * @return 
     */
    public int getSelectedID(){
        int selIndex = getIndexOf(getSelectedItem());
        return elementID.get(selIndex);
    }
    
    public Entity getSelectedEntity(){
        return getTableDAO().getObjectByID(getSelectedID());
    }
}
