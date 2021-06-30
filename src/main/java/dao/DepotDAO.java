package dao;

import dbTools.DBManager;
import entities.Depot;
import entities.EntityPK;
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
public class DepotDAO extends TableDAO<Depot, EntityPK> {
    public static final String SQL_FILES_PATH = "/sql/depot/";
    private static DepotDAO instance = null;

    public DepotDAO() {
        super(SQL_FILES_PATH);
    }

    public static DepotDAO getInstance() {
        if (instance == null) {
            instance = new DepotDAO();
            instance.prepare(DBManager.getInstance().getDefaultConnection());
        }
        return instance;
    }

    public static DepotDAO getInstance(Connection c) {
        if (instance == null) {
            instance = new DepotDAO();
            if (c != null) {
                instance.prepare(c);
            } else {
                instance.prepare(DBManager.getInstance().getDefaultConnection());
            }
        }
        return instance;
    }

    @Override
    public Depot getObjectByID(int id) {
        Depot depot = null;
        ResultSet rs = getWhereID(id);
        try {
            if (rs.next()) {
                String adr = rs.getString("ADR");
                boolean deVente = rs.getBoolean("DE_VENTE");
                boolean deReserve = rs.getBoolean("DE_RESERVE");
                boolean deStk = rs.getBoolean("DE_STOCKAGE");
                boolean dePert = rs.getBoolean("DE_PERTES");
                boolean deCmdCl = rs.getBoolean("DE_CMND_CL");
                boolean deCmdFr = rs.getBoolean("DE_CMND_FR");
                depot = new Depot(id, adr, deVente, deReserve, deStk, dePert, deCmdCl, deCmdFr);
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{id});
        }
        return depot;
    }

    @Override
    public Depot getObjectByPK(EntityPK pK) {
        return getObjectByID(pK.getId());
    }

    @Override
    public boolean insert(Depot entity) {
        int ind = 1;
        try {
            getInsertPreStatement().setString(ind++, entity.getAdr());
            getInsertPreStatement().setBoolean(ind++, entity.getDeVente());
            getInsertPreStatement().setBoolean(ind++, entity.getDeReserve());
            getInsertPreStatement().setBoolean(ind++, entity.getDeStockage());
            getInsertPreStatement().setBoolean(ind++, entity.getDePertes());
            getInsertPreStatement().setBoolean(ind++, entity.getDeCmndCl());
            getInsertPreStatement().setBoolean(ind++, entity.getDeCmndFr());
            getInsertPreStatement().execute();
            return super.insert(entity);
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{entity});
            return false;
        }
    }

    @Override
    public boolean update(Depot entity) {
        int ind = 1;
        try {
            getUpdatePreStatement().setString(ind++, entity.getAdr());
            getUpdatePreStatement().setBoolean(ind++, entity.getDeVente());
            getUpdatePreStatement().setBoolean(ind++, entity.getDeReserve());
            getUpdatePreStatement().setBoolean(ind++, entity.getDeStockage());
            getUpdatePreStatement().setBoolean(ind++, entity.getDePertes());
            getUpdatePreStatement().setBoolean(ind++, entity.getDeCmndCl());
            getUpdatePreStatement().setBoolean(ind++, entity.getDeCmndFr());
            getUpdatePreStatement().setInt(ind++, entity.getId());
            getUpdatePreStatement().execute();
            return super.update(entity);
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{entity});
            return false;
        }
    }
}
