package dao;

import dbTools.DBManager;
import entities.EntityPK;
import entities.Famille;
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
public class FamilleDAO extends TableDAO<Famille, EntityPK> {
    public static final String SQL_FILES_PATH = "/sql/famille/";
    private static FamilleDAO instance = null;

    public FamilleDAO() {
        super(SQL_FILES_PATH);
    }

    public static FamilleDAO getInstance() {
        if (instance == null) {
            instance = new FamilleDAO();
            instance.prepare(DBManager.getInstance().getDefaultConnection());
        }
        return instance;
    }

    public static FamilleDAO getInstance(Connection c) {
        if (instance == null) {
            instance = new FamilleDAO();
            if (c != null) {
                instance.prepare(c);
            } else {
                instance.prepare(DBManager.getInstance().getDefaultConnection());
            }
        }
        return instance;
    }

    @Override
    public Famille getObjectByID(int id) {
        Famille famille = null;
        ResultSet rs = getWhereID(id);
        try {
            if (rs.next()) {
                String des = rs.getString("DES");
                short tva = rs.getShort("TVA");
                boolean serv = rs.getBoolean("SERVICE");
                famille = new Famille(id, des, tva, serv);
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{id});
        }
        return famille;
    }
    
    @Override
    public Famille getObjectByPK(EntityPK pK) {
        return getObjectByID(pK.getId());
    }

    @Override
    public boolean insert(Famille entity) {
        int ind = 1;
        try {
            getInsertPreStatement().setString(ind++, entity.getDes()); //DES
            getInsertPreStatement().setInt(ind++, entity.getTva()); //TVA
            getInsertPreStatement().setBoolean(ind++, entity.isServices());
            getInsertPreStatement().execute();
            return super.insert(entity);
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{entity});
            return false;
        }
    }

    @Override
    public boolean update(Famille entity) {
        int ind = 1;
        try {
            getUpdatePreStatement().setString(ind++, entity.getDes()); //DES
            getUpdatePreStatement().setInt(ind++, entity.getTva()); //TVA
            getUpdatePreStatement().setBoolean(ind++, entity.isServices());
            getUpdatePreStatement().setInt(ind++, entity.getId()); //ID
            getUpdatePreStatement().execute();
            return super.update(entity);
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{entity});
            return false;
        }
    }
}
