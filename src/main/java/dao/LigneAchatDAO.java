/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dbTools.DBManager;
import entities.Achat;
import entities.EntityPK;
import entities.LigneAch;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import sql.QueryReader;
import tools.ExceptionReporting;

/**
 *
 * @author alilo
 */
public class LigneAchatDAO extends TableDAO<LigneAch, EntityPK> {

    public static final String SQL_FILES_PATH = "/sql/ligneAchat/";
    private static LigneAchatDAO instance = null;
    private final String getByAchatQry;
    private final String getByProdQry;

    private PreparedStatement getByAchatSt;
    private PreparedStatement getByProdSt;

    public LigneAchatDAO() {
        super(SQL_FILES_PATH);
        getByAchatQry = QueryReader.getQueries(SQL_FILES_PATH + "GetByAchat.sql", false)[0];
        getByProdQry = QueryReader.getQueries(SQL_FILES_PATH + "GetByProd.sql", false)[0];
    }

    @Override
    public boolean prepare(Connection c) {
        boolean prep = super.prepare(c);
        try {
            getByAchatSt = getConnection().prepareStatement(getByAchatQry, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            getByProdSt = getConnection().prepareStatement(getByProdQry);
            return prep;
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
            return false;
        }
    }

    public static LigneAchatDAO getInstance() {
        if (instance == null) {
            instance = new LigneAchatDAO();
            instance.prepare(DBManager.getInstance().getDefaultConnection());
        }
        return instance;
    }

    public static LigneAchatDAO getInstance(Connection c) {
        if (instance == null) {
            instance = new LigneAchatDAO();
            if (c != null) {
                instance.prepare(c);
            } else {
                instance.prepare(DBManager.getInstance().getDefaultConnection());
            }
        }
        return instance;
    }

    @Override
    public LigneAch getObjectByID(int id) {
        LigneAch ligneAch = null;
        ResultSet rs = getWhereID(id);
        try {
            if (rs.next()) {
                int idAch = rs.getInt("ID_ACH");
                int idProd = rs.getInt("ID_PROD");
                int idEnStk = rs.getInt("ID_EN_STK");
                BigDecimal puAch = rs.getBigDecimal("PU_ACH");
                int qteAch = rs.getInt("QTE");
                int idUnit = rs.getInt("UNITE");
                double qteUnitAch = rs.getInt("QTE_UNIT");
                BigDecimal totalLAch = rs.getBigDecimal("TOTAL_LACH");
                ligneAch = new LigneAch(id, puAch, qteAch, qteUnitAch, totalLAch);
                ligneAch.setAchat(AchatDAO.getInstance().getObjectByID(idAch));
                ligneAch.setProduit(ProduitDAO.getInstance().getObjectByID(idProd));
                ligneAch.setUnite(UniteDAO.getInstance().getObjectByID(idUnit));
                ligneAch.setEnStock(LotEnStockDAO.getInstance().getObjectByID(idEnStk));
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{id});
        }
        return ligneAch;
    }

    @Override
    public LigneAch getObjectByPK(EntityPK pK) {
        return getObjectByID(pK.getId());
    }

    public ResultSet getDetailsAchat(Achat achat) {
        ResultSet rs = null;
        try {
            getByAchatSt.setInt(1, achat.getId());
            rs = getByAchatSt.executeQuery();
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{achat});
        }
        return rs;
    }

    public LigneAch getByProduit(LigneAch ligneAch) {
        LigneAch lach = null;
        ResultSet rs;
        try {
            int ind = 1;
            getByProdSt.setInt(ind++, ligneAch.getAchat().getId());
            getByProdSt.setInt(ind++, ligneAch.getProduit().getId());
            getByProdSt.setInt(ind++, ligneAch.getEnStock().getId());
            getByProdSt.setInt(ind++, ligneAch.getUnite().getId());
            getByProdSt.setBigDecimal(ind++, ligneAch.getPuAch());
            rs = getByProdSt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("ID");
                lach = getObjectByID(id);
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{ligneAch});
        }
        return lach;
    }

    @Override
    public boolean insert(LigneAch entity) {
        LigneAch oldLAch = getByProduit(entity);
        if (oldLAch == null) {
            int ind = 1;
            try {
                getInsertPreStatement().setInt(ind++, entity.getAchat().getId());
                getInsertPreStatement().setInt(ind++, entity.getProduit().getId());
                getInsertPreStatement().setInt(ind++, entity.getEnStock().getId());
                getInsertPreStatement().setBigDecimal(ind++, entity.getPuAch());
                getInsertPreStatement().setInt(ind++, entity.getQte());
                getInsertPreStatement().setInt(ind++, entity.getUnite().getId());
                getInsertPreStatement().setDouble(ind++, entity.getQteUnit());
                getInsertPreStatement().setBigDecimal(ind++, entity.getTotalLach());
                getInsertPreStatement().execute();
                boolean ins = super.insert(entity);
                return ins;
            } catch (SQLException ex) {
                ExceptionReporting.showException(ex, new Object[]{entity});
                return false;
            }
        } else {
            oldLAch.setQte(oldLAch.getQte() + entity.getQte());
            oldLAch.setQteUnit(oldLAch.getQteUnit() + entity.getQteUnit());
            oldLAch.setTotalLach(oldLAch.getTotalLach().add(entity.getTotalLach()));
            return update(oldLAch);
        }

    }

    @Override
    public boolean update(LigneAch entity) {
        int ind = 1;
        try {
            getUpdatePreStatement().setInt(ind++, entity.getAchat().getId());
            getUpdatePreStatement().setInt(ind++, entity.getProduit().getId());
            getUpdatePreStatement().setInt(ind++, entity.getEnStock().getId());
            getUpdatePreStatement().setBigDecimal(ind++, entity.getPuAch());
            getUpdatePreStatement().setInt(ind++, entity.getQte());
            getUpdatePreStatement().setInt(ind++, entity.getUnite().getId());
            getUpdatePreStatement().setDouble(ind++, entity.getQteUnit());
            getUpdatePreStatement().setBigDecimal(ind++, entity.getTotalLach());
            getUpdatePreStatement().setInt(ind++, entity.getId());
            getUpdatePreStatement().execute();
            return super.update(entity);
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{entity});
            return false;
        }
    }
}
