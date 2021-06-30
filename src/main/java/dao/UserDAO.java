package dao;

import dbTools.DBManager;
import entities.AppUser;
import entities.EntityPK;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import sql.QueryReader;
import tools.ExceptionReporting;


/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author alilo
 */
public class UserDAO extends TableDAO<AppUser, EntityPK> {
    public static final String SQL_FILES_PATH = "/sql/appUser/";
    private static UserDAO instance = null;
    private final String getAdminsCountQry;
    private PreparedStatement getAdminsCountStmt;

    public UserDAO() {
        super(SQL_FILES_PATH);
        getAdminsCountQry = QueryReader.getQueries(SQL_FILES_PATH+"GetAdminsCount.sql", false)[0];
    }
    
    @Override
    public boolean prepare(Connection c) {
        boolean prep = super.prepare(c);
        try {
            getAdminsCountStmt  = getConnection().prepareStatement(getAdminsCountQry);
            return prep;
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
            return false;
        }
    }

    public static UserDAO getInstance() {
        if (instance == null) {
            instance = new UserDAO();
            instance.prepare(DBManager.getInstance().getDefaultConnection());
        }
        return instance;
    }

    public static UserDAO getInstance(Connection c) {
        if (instance == null) {
            instance = new UserDAO();
            if (c != null) {
                instance.prepare(c);
            } else {
                instance.prepare(DBManager.getInstance().getDefaultConnection());
            }
        }
        return instance;
    }

    @Override
    public AppUser getObjectByID(int id) {
        AppUser user = null;
        ResultSet rs = getWhereID(id);
        try {
            if (rs.next()) {
                String login = rs.getString("LOGIN");
                String pw = rs.getString("PW");
                int idGrp = rs.getInt("ID_GROUP");
                user = new AppUser(id, login, pw);
                user.setUserGp(UGroupDAO.getInstance().getObjectByID(idGrp));
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{id});
        }
        return user;
    }

    @Override
    public AppUser getObjectByPK(EntityPK pK) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public boolean isLastAdmin() {
        boolean isLastAdmin = true;
        try {
            ResultSet rs = getAdminsCountStmt.executeQuery();
            if (rs.next()) {
                int adminsCount = rs.getInt(1);
                isLastAdmin = adminsCount == 1;
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
        }
        return isLastAdmin;
    }

    @Override
    public boolean insert(AppUser entity) {
        try {
            getInsertPreStatement().setString(1, entity.getLogin());
            getInsertPreStatement().setString(2, entity.getPw());
            getInsertPreStatement().setInt(3, entity.getUserGp().getId());
            getInsertPreStatement().execute();
            return super.insert(entity);
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{entity});
            return false;
        }
    }

    @Override
    public boolean update(AppUser entity) {
        try {
            getUpdatePreStatement().setString(1, entity.getLogin());
            getUpdatePreStatement().setString(2, entity.getPw());
            getUpdatePreStatement().setInt(3, entity.getUserGp().getId());
            getUpdatePreStatement().setInt(4, entity.getId());
            getUpdatePreStatement().execute();
            return super.update(entity);
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{entity});
            return false;
        }
    }
}
