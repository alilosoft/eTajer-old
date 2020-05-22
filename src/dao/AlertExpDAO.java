package dao;

import dbTools.DBManager;
import entities.AlerteExp;
import entities.EnStock;
import entities.EntityPK;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import sql.QueryReader;
import tools.DateTools;
import tools.ExceptionReporting;


/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author alilo
 */
public class AlertExpDAO extends TableDAO<AlerteExp, EntityPK> {
    public static final String SQL_FILES_PATH = "/sql/alertExp/";
    private static AlertExpDAO instance = null;
    
    private final String getByLotQry;
    private PreparedStatement getByLotSt;
    
    public AlertExpDAO() {
        super(SQL_FILES_PATH);
        getByLotQry = QueryReader.getQueries(SQL_FILES_PATH + "GetByLot.sql", false)[0];
    }
    
    @Override
    public boolean prepare(Connection c) {
        boolean prep = super.prepare(c);
        try {
            getByLotSt = getConnection().prepareStatement(getByLotQry);
            return prep;
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
            return false;
        }
    }
    
    public static AlertExpDAO getInstance() {
        if (instance == null) {
            instance = new AlertExpDAO();
            instance.prepare(DBManager.getInstance().getDefaultConnection());
        }
        return instance;
    }
    
    public static AlertExpDAO getInstance(Connection c) {
        if (instance == null) {
            instance = new AlertExpDAO();
            if (c != null) {
                instance.prepare(c);
            }else{
                instance.prepare(DBManager.getInstance().getDefaultConnection());
            }
        }
        return instance;
    }

    @Override
    public AlerteExp getObjectByID(int id) {
        AlerteExp alert = null;
        ResultSet rs = getWhereID(id);
        try {
            if (rs.next()) {
                int idLot = rs.getInt("ID_LOT");
                Date dateAlert = rs.getDate("DATE_ALERT");
                EnStock lot = LotEnStockDAO.getInstance().getObjectByID(idLot);
                alert = new AlerteExp(id, dateAlert);
                alert.setEnStock(lot);
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
        }
        return alert;
    }

    @Override
    public AlerteExp getObjectByPK(EntityPK pK) {
        return  getObjectByID(pK.getId());
    }

    @Override
    public boolean insert(AlerteExp entity) {
        int ind = 1;
        try {
            getInsertPreStatement().setInt(ind++, entity.getEnStock().getId());
            getInsertPreStatement().setDate(ind++, DateTools.getSqlDate(entity.getDateAlert()));
            getInsertPreStatement().execute();
            return super.insert(entity);
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{entity});
            return false;
        }
    }

    @Override
    public boolean update(AlerteExp entity) {
        int ind = 1;
        try {
            getUpdatePreStatement().setInt(ind++, entity.getEnStock().getId());
            getUpdatePreStatement().setDate(ind++, DateTools.getSqlDate(entity.getDateAlert()));
            getUpdatePreStatement().setInt(ind++, entity.getId());
            getUpdatePreStatement().execute();
            return super.update(entity);
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{entity});
            return false;
        }
    }
    
    public AlerteExp getByLot(int idLot) {
        AlerteExp alert = null;
        try {
            getByLotSt.setInt(1, idLot);
            ResultSet rs = getByLotSt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("ID");
                alert = getObjectByID(id);
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{idLot});
        }
        return alert;
    }
}
