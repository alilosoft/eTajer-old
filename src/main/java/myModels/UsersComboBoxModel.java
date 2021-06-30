/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myModels;

import java.sql.ResultSet;
import java.sql.SQLException;
import dao.UserDAO;
import entities.AppUser;

/**
 *
 * @author alilo
 */
public class UsersComboBoxModel extends ResultSet2ComboBoxModel<AppUser, UserDAO> {
    
    public UsersComboBoxModel() {
        super(UserDAO.getInstance().getAll());
    }
    
    public UsersComboBoxModel(ResultSet rs) {
        super(rs);
    }

    @Override
    public void addItem(ResultSet rs) throws SQLException {
        addElement(rs.getString("Utilisateur")+" ("+rs.getString("Groupe")+")");
    }

    @Override
    public UserDAO getTableDAO() {
        return UserDAO.getInstance();
    }
}
