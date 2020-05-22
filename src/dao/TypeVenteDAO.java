package dao;

import dbTools.DBManager;
import entities.EntityPK;
import entities.TypeVnt;
import java.math.BigDecimal;
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
public class TypeVenteDAO extends TableDAO<TypeVnt, EntityPK> {
    public static final String SQL_FILES_PATH = "/sql/typeVente/";
    private static TypeVenteDAO instance = null;

    public TypeVenteDAO() {
        super(SQL_FILES_PATH);
    }

    public static TypeVenteDAO getInstance() {
        if (instance == null) {
            instance = new TypeVenteDAO();
            instance.prepare(DBManager.getInstance().getDefaultConnection());
        }
        return instance;
    }

    public static TypeVenteDAO getInstance(Connection c) {
        if (instance == null) {
            instance = new TypeVenteDAO();
            if (c != null) {
                instance.prepare(c);
            } else {
                instance.prepare(DBManager.getInstance().getDefaultConnection());
            }
        }
        return instance;
    }

    @Override
    public TypeVnt getObjectByID(int id) {
        TypeVnt tv = null;
        ResultSet rs = getWhereID(id);
        try {
            if (rs.next()) {
                String des = rs.getString("DES");
                BigDecimal  marge = rs.getBigDecimal("DEF_MARGE");
                tv = new TypeVnt(id, des, marge);
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{id});
        }
        return tv;
    }
    
    @Override
    public TypeVnt getObjectByPK(EntityPK pK) {
        return getObjectByID(pK.getId());
    }

    @Override
    public boolean insert(TypeVnt entity) {
        int ind = 1;
        try {
            getInsertPreStatement().setString(ind++, entity.getDes());
            getInsertPreStatement().setBigDecimal(ind++, entity.getDefMarge());
            getInsertPreStatement().execute();
            return super.insert(entity);
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{entity});
            return false;
        }
    }

    @Override
    public boolean update(TypeVnt entity) {
        int ind = 1;
        try {
            getUpdatePreStatement().setString(ind++, entity.getDes());
            getUpdatePreStatement().setBigDecimal(ind++, entity.getDefMarge());
            getUpdatePreStatement().setInt(ind++, entity.getId());
            getUpdatePreStatement().execute();
            return super.update(entity);
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{entity});
            return false;
        }
    }

    public static void main(String args[]) throws Exception {
        TypeVenteDAO.getInstance().insert(TypeVnt.GROS);
        TypeVenteDAO.getInstance().insert(TypeVnt.DETAIL);
    }
}
