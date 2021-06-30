/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dbTools.DBManager;
import entities.AppUser;
import entities.EntityPK;
import entities.LigneVnt;
import entities.Vente;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import sql.QueryReader;
import tools.DateTools;
import tools.ExceptionReporting;

/**
 *
 * @author alilo
 */
public class LigneVenteDAO extends TableDAO<LigneVnt, EntityPK> {

    public static final String SQL_FILES_PATH = "/sql/ligneVente/";
    private static LigneVenteDAO instance = null;
    private final String getByVenteQry;
    private final String getByLotQry;
    private final String getJournalVntsQry;
    private final String getTotalBenificeQry;
    private final String getTotalBenificeByUserQry;

    private PreparedStatement getByVenteSt;
    private PreparedStatement getByLotSt;
    private PreparedStatement getJournalVntsSt;
    private PreparedStatement getTotalBenificeStm;
    private PreparedStatement getTotalBenificeByUserStm;

    public LigneVenteDAO() {
        super(SQL_FILES_PATH);
        getByVenteQry = QueryReader.getQueries(SQL_FILES_PATH + "GetByVente.sql", false)[0];
        getByLotQry = QueryReader.getQueries(SQL_FILES_PATH + "GetByLot.sql", false)[0];
        getJournalVntsQry = QueryReader.getQueries(SQL_FILES_PATH + "GetJournalVnts.sql", false)[0];
        getTotalBenificeQry = QueryReader.getQueries(sqlFilesPath + "GetTotalBenifice.sql", false)[0];
        getTotalBenificeByUserQry = QueryReader.getQueries(sqlFilesPath + "GetTotalBenificeByUser.sql", false)[0];
    }

    @Override
    public boolean prepare(Connection c) {
        boolean prep = super.prepare(c);
        try {
            getByVenteSt = getConnection().prepareStatement(getByVenteQry, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            getByLotSt = getConnection().prepareStatement(getByLotQry);
            getJournalVntsSt = getConnection().prepareStatement(getJournalVntsQry, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            getTotalBenificeStm = getConnection().prepareStatement(getTotalBenificeQry);
            getTotalBenificeByUserStm = getConnection().prepareStatement(getTotalBenificeByUserQry);
            return prep;
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
            return false;
        }
    }

    public static LigneVenteDAO getInstance() {
        if (instance == null) {
            instance = new LigneVenteDAO();
            instance.prepare(DBManager.getInstance().getDefaultConnection());
        }
        return instance;
    }

    public static LigneVenteDAO getInstance(Connection c) {
        if (instance == null) {
            instance = new LigneVenteDAO();
            if (c != null) {
                instance.prepare(c);
            } else {
                instance.prepare(DBManager.getInstance().getDefaultConnection());
            }
        }
        return instance;
    }

    @Override
    public LigneVnt getObjectByID(int id) {
        LigneVnt ligneVnt = null;
        ResultSet rs = getWhereID(id);
        try {
            if (rs.next()) {
                int idVente = rs.getInt("ID_VNT");
                int idEnStk = rs.getInt("ID_EN_STK");
                BigDecimal puVente = rs.getBigDecimal("PU_VNT");
                BigDecimal puAchat = rs.getBigDecimal("PU_ACH");
                double qteVendu = rs.getDouble("QTE");
                int idUnitVente = rs.getInt("UNITE_VNT");
                double qteUnitVendu = rs.getDouble("QTE_UNIT");
                BigDecimal totalLVnt = rs.getBigDecimal("TOTAL_LVNT");
                ligneVnt = new LigneVnt(id, puVente, puAchat, qteVendu, qteUnitVendu, totalLVnt);
                ligneVnt.setVente(VenteDAO.getInstance().getObjectByID(idVente));
                ligneVnt.setEnStock(LotEnStockDAO.getInstance().getObjectByID(idEnStk));
                ligneVnt.setUniteVnt(UniteDAO.getInstance().getObjectByID(idUnitVente));
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{id});
        }
        return ligneVnt;
    }

    @Override
    public LigneVnt getObjectByPK(EntityPK pK) {
        return getObjectByID(pK.getId());
    }

    public ResultSet getDetailsVente(Vente vente) {
        ResultSet rs = null;
        try {
            getByVenteSt.setInt(1, vente.getId());
            rs = getByVenteSt.executeQuery();
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{vente});
        }
        return rs;
    }

    public LigneVnt getByProduit(LigneVnt ligneVnt) {
        LigneVnt lv = null;
        ResultSet rs;
        try {
            int ind = 1;
            getByLotSt.setInt(ind++, ligneVnt.getVente().getId());
            getByLotSt.setInt(ind++, ligneVnt.getEnStock().getId());
            getByLotSt.setInt(ind++, ligneVnt.getUniteVnt().getId());
            getByLotSt.setBigDecimal(ind++, ligneVnt.getPuVnt());
            rs = getByLotSt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("ID");
                lv = getObjectByID(id);
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{ligneVnt});
        }
        return lv;
    }

    public ResultSet getJournalVentes(java.util.Date beginDate, java.util.Date endDate) {
        ResultSet rs = null;
        try {
            getJournalVntsSt.setDate(1, DateTools.getSqlDate(beginDate));
            getJournalVntsSt.setDate(2, DateTools.getSqlDate(endDate));
            rs = getJournalVntsSt.executeQuery();
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
        }
        return rs;
    }

    public BigDecimal getTotalBenifice(java.util.Date beginDate, java.util.Date endDate) {
        BigDecimal benifice = new BigDecimal(0);
        try {
            getTotalBenificeStm.setDate(1, DateTools.getSqlDate(beginDate));
            getTotalBenificeStm.setDate(2, DateTools.getSqlDate(endDate));
            ResultSet rs = getTotalBenificeStm.executeQuery();
            if (rs.next() && rs.getBigDecimal(1) != null) {
                benifice = rs.getBigDecimal(1);
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{beginDate});
        }
        return benifice;
    }
    
    public BigDecimal getTotalBenifice(java.util.Date beginDate, java.util.Date endDate, AppUser user) {
        BigDecimal benifice = new BigDecimal(0);
        try {
            getTotalBenificeByUserStm.setDate(1, DateTools.getSqlDate(beginDate));
            getTotalBenificeByUserStm.setDate(2, DateTools.getSqlDate(endDate));
            getTotalBenificeByUserStm.setInt(3, user.getId());
            ResultSet rs = getTotalBenificeByUserStm.executeQuery();
            if (rs.next() && rs.getBigDecimal(1) != null) {
                benifice = rs.getBigDecimal(1);
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{beginDate});
        }
        return benifice;
    }

    @Override
    public boolean insert(LigneVnt entity) {
        LigneVnt lv = getByProduit(entity);
        if (lv == null) {
            int ind = 1;
            try {
                getInsertPreStatement().setInt(ind++, entity.getVente().getId());
                getInsertPreStatement().setInt(ind++, entity.getEnStock().getId());
                getInsertPreStatement().setBigDecimal(ind++, entity.getPuVnt());
                getInsertPreStatement().setBigDecimal(ind++, entity.getPuAch());
                getInsertPreStatement().setDouble(ind++, entity.getQte());
                getInsertPreStatement().setInt(ind++, entity.getUniteVnt().getId());
                getInsertPreStatement().setDouble(ind++, entity.getQteUnitaire());
                getInsertPreStatement().setBigDecimal(ind++, entity.getTotalLvnt());
                getInsertPreStatement().execute();
                boolean ins = super.insert(entity);
                return ins;
            } catch (SQLException ex) {
                ExceptionReporting.showException(ex, new Object[]{entity});
                return false;
            }
        } else {
            entity.setId(lv.getId());
            entity.setQte(lv.getQte() + entity.getQte());
            entity.setQteUnitair(lv.getQteUnitaire() + entity.getQteUnitaire());
            entity.setTotalLvnt(lv.getTotalLvnt().add(entity.getTotalLvnt()));
            setGeneratedID(entity.getId());// to re-select its row after reload!
            return update(entity);
        }
    }

    @Override
    public boolean update(LigneVnt entity) {
        int ind = 1;
        try {
            getUpdatePreStatement().setInt(ind++, entity.getVente().getId());
            getUpdatePreStatement().setInt(ind++, entity.getEnStock().getId());
            getUpdatePreStatement().setBigDecimal(ind++, entity.getPuVnt());
            getUpdatePreStatement().setBigDecimal(ind++, entity.getPuAch());
            getUpdatePreStatement().setDouble(ind++, entity.getQte());
            getUpdatePreStatement().setInt(ind++, entity.getUniteVnt().getId());
            getUpdatePreStatement().setDouble(ind++, entity.getQteUnitaire());
            getUpdatePreStatement().setBigDecimal(ind++, entity.getTotalLvnt());
            getUpdatePreStatement().setInt(ind++, entity.getId());
            getUpdatePreStatement().execute();
            return super.update(entity);
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{entity});
            return false;
        }
    }
}
