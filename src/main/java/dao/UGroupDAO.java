package dao;

import dbTools.DBManager;
import entities.EntityPK;
import entities.UserGp;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import tools.ExceptionReporting;


/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author alilo
 */
public class UGroupDAO extends TableDAO<UserGp, EntityPK> {

    public static final String SQL_FILES_PATH = "/sql/group/";
    private static UGroupDAO instance = null;

    public UGroupDAO() {
        super(SQL_FILES_PATH);
    }

    public static UGroupDAO getInstance() {
        if (instance == null) {
            instance = new UGroupDAO();
            instance.prepare(DBManager.getInstance().getDefaultConnection());
        }
        return instance;
    }

    public static UGroupDAO getInstance(Connection c) {
        if (instance == null) {
            instance = new UGroupDAO();
            if (c != null) {
                instance.prepare(c);
            } else {
                instance.prepare(DBManager.getInstance().getDefaultConnection());
            }
        }
        return instance;
    }

    @Override
    public UserGp getObjectByID(int id) {
        UserGp gp = null;
        ResultSet rs = getWhereID(id);
        try {
            if (rs.next()) {
                String des = rs.getString("DES");
                gp = new UserGp(id, des);
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{id});
        }
        return gp;
    }

    @Override
    public UserGp getObjectByPK(EntityPK pK) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public boolean insert(UserGp entity) {
        try {
            getInsertPreStatement().setString(1, entity.getDes());
            getInsertPreStatement().execute();
            return super.insert(entity);
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{entity});
            return false;
        }
    }

    @Override
    public boolean update(UserGp entity) {
        try {
            getUpdatePreStatement().setString(1, entity.getDes());
            getUpdatePreStatement().setInt(2, entity.getId());
            getUpdatePreStatement().execute();
            return super.update(entity);
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{entity});
            return false;
        }
    }
}
