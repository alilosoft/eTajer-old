package dao;

import dbTools.DBManager;
import entities.EntityPK;
import entities.Unite;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
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
public class UniteDAO extends TableDAO<Unite, EntityPK> {
    public static final String SQL_FILES_PATH = "/sql/unite/";
    private static UniteDAO instance = null;

    public UniteDAO() {
        super(SQL_FILES_PATH);
    }

    public static UniteDAO getInstance() {
        if (instance == null) {
            instance = new UniteDAO();
            instance.prepare(DBManager.getInstance().getDefaultConnection());
        }
        return instance;
    }

    public static UniteDAO getInstance(Connection c) {
        if (instance == null) {
            instance = new UniteDAO();
            if (c != null) {
                instance.prepare(c);
            } else {
                instance.prepare(DBManager.getInstance().getDefaultConnection());
            }
        }
        return instance;
    }

    @Override
    public Unite getObjectByID(int id) {
        Unite unite = null;
        ResultSet rs = getWhereID(id);
        try {
            if (rs.next()) {
                String des = rs.getString("DES");
                double  qte = rs.getDouble("QTE");
                unite = new Unite(id, des, qte);
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{id});
        }
        return unite;
    }
    
    @Override
    public Unite getObjectByPK(EntityPK pK) {
        return getObjectByID(pK.getId());
    }

    @Override
    public boolean insert(Unite entity) {
        int ind = 1;
        try {
            getInsertPreStatement().setString(ind++, entity.getDes());
            getInsertPreStatement().setDouble(ind++, entity.getQte());
            getInsertPreStatement().execute();
            return super.insert(entity);
        } catch (SQLException ex) {
            if (ex.getSQLState().equals("23505")) {
                String mess;
                mess = "Cette Unité déja existe: "+ entity.getDes()
                        + "\nChoisisez une  autre désignation. SVP!"
                        + "\n\nError: " + ex.getSQLState() + "\n" + ex.getLocalizedMessage();
                MessageReporting.showMessage(Level.WARNING, getClass(), "insert()", mess, new Object[]{entity});
            } else {
                ExceptionReporting.showException(ex, new Object[]{entity});
            }
            return false;
        }
    }

    @Override
    public boolean update(Unite entity) {
        int ind = 1;
        try {
            getUpdatePreStatement().setString(ind++, entity.getDes());
            getUpdatePreStatement().setDouble(ind++, entity.getQte());
            getUpdatePreStatement().setInt(ind++, entity.getId());
            getUpdatePreStatement().execute();
            return super.update(entity);
        } catch (SQLException ex) {
            if (ex.getSQLState().equals("23505")) {
                String mess;
                mess = "Cette Unité déja existe: "+ entity.getDes()
                        + "\nChoisisez une  autre désignation. SVP!"
                        + "\n\nError: " + ex.getSQLState() + "\n" + ex.getLocalizedMessage();
                MessageReporting.showMessage(Level.WARNING, getClass(), "insert()", mess, new Object[]{entity});
            } else {
                ExceptionReporting.showException(ex, new Object[]{entity});
            }
            return false;
        }
    }

    public static void main(String args[]) throws Exception {
        //UniteDAO.getInstance().insert(new Unite(0, "Kg", 1, 1));
        //UniteDAO.getInstance().insert(new Unite(0, "Sac50Kg", 25, 1));
        //UniteDAO.getInstance().insert(new Unite(0, "g", 1, 1000));
        //UniteDAO.getInstance().update(new Unite(4, "Sac50Kg", 50, 1));
    }
}
