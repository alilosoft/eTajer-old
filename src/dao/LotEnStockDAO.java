package dao;

import dbTools.DBManager;
import entities.Depot;
import entities.EntityPK;
import entities.Produit;
import entities.EnStock;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
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
public class LotEnStockDAO extends TableDAO<EnStock, EntityPK> {

    public static final String SQL_FILES_PATH = "/sql/lotEnStock/";
    private static LotEnStockDAO instance = null;
    private final String getByProdQry;
    private final String findLotEnStockQry;
    private final String getProdsAVendreQry;
    private final String getByCBQry;
    private final String getLastLotQry;
    private final String getLotDispoQry;
    private final String getLotNotDispoQry;

    private PreparedStatement getByProdSt;
    private PreparedStatement findLotEnStockSt;
    private PreparedStatement getProdsAVendreSt;
    private PreparedStatement getByCBSt;
    private PreparedStatement getLastLotSt;
    private PreparedStatement getLotDispoSt;
    private PreparedStatement getLotNotDispoSt;

    public LotEnStockDAO() {
        super(SQL_FILES_PATH);
        getByProdQry = QueryReader.getQueries(SQL_FILES_PATH + "GetByProd.sql", false)[0];
        findLotEnStockQry = QueryReader.getQueries(SQL_FILES_PATH + "FindLotEnStock.sql", false)[0];
        getProdsAVendreQry = QueryReader.getQueries(SQL_FILES_PATH + "GetLotsAVendre.sql", false)[0];
        getByCBQry = QueryReader.getQueries(SQL_FILES_PATH + "GetByCodBar.sql", false)[0];
        getLastLotQry = QueryReader.getQueries(SQL_FILES_PATH + "GetLastLot.sql", false)[0];
        getLotDispoQry = QueryReader.getQueries(SQL_FILES_PATH + "GetDisponible.sql", false)[0];
        getLotNotDispoQry = QueryReader.getQueries(SQL_FILES_PATH + "GetNotDisponible.sql", false)[0];
    }

    @Override
    public boolean prepare(Connection c) {
        boolean prep = super.prepare(c);
        try {
            getByProdSt = getConnection().prepareStatement(getByProdQry, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            findLotEnStockSt = getConnection().prepareStatement(findLotEnStockQry);
            getProdsAVendreSt = getConnection().prepareStatement(getProdsAVendreQry, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            getByCBSt = getConnection().prepareStatement(getByCBQry);
            getLastLotSt = getConnection().prepareStatement(getLastLotQry, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            getLotDispoSt = getConnection().prepareStatement(getLotDispoQry, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            getLotNotDispoSt = getConnection().prepareStatement(getLotNotDispoQry, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            return prep;
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
            return false;
        }
    }

    public static LotEnStockDAO getInstance() {
        if (instance == null) {
            instance = new LotEnStockDAO();
            instance.prepare(DBManager.getInstance().getDefaultConnection());
        }
        return instance;
    }

    public static LotEnStockDAO getInstance(Connection c) {
        if (instance == null) {
            instance = new LotEnStockDAO();
            if (c != null) {
                instance.prepare(c);
            } else {
                instance.prepare(DBManager.getInstance().getDefaultConnection());
            }
        }
        return instance;
    }

    @Override
    public EnStock getObjectByID(int id) {
        EnStock s = null;
        ResultSet rs = getWhereID(id);
        try {
            if (rs.next()) {
                int idProd = rs.getInt("ID_PROD");
                int idStk = rs.getInt("ID_DEPOT");
                double qte = rs.getDouble("QTE");
                Date dateExp = rs.getDate("DATE_EXP");
                String cb = rs.getString("COD_BAR");
                boolean actif = rs.getBoolean("ACTIF");
                Date dateEntr = rs.getDate("DATE_ENTR");
                BigDecimal puAchat = rs.getBigDecimal("PU_ACH");
                BigDecimal puVntDt = rs.getBigDecimal("PU_VNT_DT");
                BigDecimal puVntGr = rs.getBigDecimal("PU_VNT_GR");
                BigDecimal puVntDGr = rs.getBigDecimal("PU_VNT_DGR");
                BigDecimal puVntSGr = rs.getBigDecimal("PU_VNT_SGR");
                s = new EnStock(id, qte, dateExp, cb, actif);
                s.setDateEntr(dateEntr);
                s.setPuAch(puAchat);
                s.setPuVntDt(puVntDt);
                s.setPuVntGr(puVntGr);
                s.setPuVntDgr(puVntDGr);
                s.setPuVntSgr(puVntSGr);
                s.setProduit(ProduitDAO.getInstance().getObjectByID(idProd));
                s.setDepot(DepotDAO.getInstance().getObjectByID(idStk));
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{id});
        }
        return s;
    }

    @Override
    public EnStock getObjectByPK(EntityPK pK) {
        return getObjectByID(pK.getId());
    }

    public EnStock findLotEnStock(Produit p, Depot d, Date dateExp, BigDecimal puAch) {
        EnStock enStock = null;
        ResultSet rs;
        int ind = 1;
        try {
            findLotEnStockSt.setInt(ind++, p.getId());
            findLotEnStockSt.setInt(ind++, d.getId());
            findLotEnStockSt.setDate(ind++, DateTools.getSqlDate(dateExp));
            findLotEnStockSt.setBigDecimal(ind++, puAch);
            rs = findLotEnStockSt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("ID");
                enStock = getObjectByID(id);
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{p, d});
        }
        return enStock;
    }

    public EnStock getByCodBar(String cb) {
        EnStock enStock = null;

        try {
            getByCBSt.setString(1, cb);
            ResultSet rs = getByCBSt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("ID");
                enStock = getObjectByID(id);
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{cb});
        }
        return enStock;
    }

    public EnStock getLastLot(int idProd) {
        EnStock enStock = null;
        try {
            getLastLotSt.setInt(1, idProd);
            ResultSet rs = getLastLotSt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("ID");
                enStock = getObjectByID(id);
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{idProd});
        }
        return enStock;
    }

    @Override
    public boolean insert(EnStock entity) {
        EnStock oldLot = findLotEnStock(entity.getProduit(), entity.getDepot(), entity.getDateExp(), entity.getPuAch());
        if (oldLot == null) {
            int ind = 1;
            try {
                getInsertPreStatement().setInt(ind++, entity.getProduit().getId());
                getInsertPreStatement().setInt(ind++, entity.getDepot().getId());
                getInsertPreStatement().setDouble(ind++, entity.getQte());
                getInsertPreStatement().setDate(ind++, DateTools.getSqlDate(entity.getDateExp()));
                getInsertPreStatement().setString(ind++, entity.getCodBar());
                getInsertPreStatement().setBoolean(ind++, entity.getActif());
                getInsertPreStatement().setDate(ind++, DateTools.getSqlDate(entity.getDateEntr()));
                getInsertPreStatement().setBigDecimal(ind++, entity.getPuAch());
                getInsertPreStatement().setBigDecimal(ind++, entity.getPuVntDt());
                getInsertPreStatement().setBigDecimal(ind++, entity.getPuVntGr());
                getInsertPreStatement().setBigDecimal(ind++, entity.getPuVntDgr());
                getInsertPreStatement().setBigDecimal(ind++, entity.getPuVntSgr());
                getInsertPreStatement().execute();
                return super.insert(entity);
            } catch (SQLException ex) {
                ExceptionReporting.showException(ex, new Object[]{entity});
                return false;
            }
        } else {
            entity.setId(oldLot.getId());// used by MajLigneAchat, as foreign key!
            oldLot.setQte(oldLot.getQte() + entity.getQte());
            oldLot.setCodBar(entity.getCodBar());
            oldLot.setDateEntr(entity.getDateEntr());
            oldLot.setPuVntDt(entity.getPuVntDt());
            oldLot.setPuVntGr(entity.getPuVntGr());
            oldLot.setPuVntDgr(entity.getPuVntDgr());
            oldLot.setPuVntSgr(entity.getPuVntSgr());
            setGeneratedID(oldLot.getId());// used by UI to re-select the coresponding row after reload!
            return oldLot.update();
        }
    }

    @Override
    public boolean update(EnStock entity) {
        EnStock oldLot = findLotEnStock(entity.getProduit(), entity.getDepot(), entity.getDateExp(), entity.getPuAch());
        if (oldLot == null || oldLot.getId().intValue() == entity.getId()) {
            int ind = 1;
            try {
                getUpdatePreStatement().setInt(ind++, entity.getProduit().getId());
                getUpdatePreStatement().setInt(ind++, entity.getDepot().getId());
                getUpdatePreStatement().setDouble(ind++, entity.getQte());
                getUpdatePreStatement().setDate(ind++, DateTools.getSqlDate(entity.getDateExp()));
                getUpdatePreStatement().setString(ind++, entity.getCodBar());
                getUpdatePreStatement().setDate(ind++, DateTools.getSqlDate(entity.getDateEntr()));
                getUpdatePreStatement().setBigDecimal(ind++, entity.getPuAch());
                getUpdatePreStatement().setBigDecimal(ind++, entity.getPuVntDt());
                getUpdatePreStatement().setBigDecimal(ind++, entity.getPuVntGr());
                getUpdatePreStatement().setBigDecimal(ind++, entity.getPuVntDgr());
                getUpdatePreStatement().setBigDecimal(ind++, entity.getPuVntSgr());
                getUpdatePreStatement().setInt(ind++, entity.getId());
                getUpdatePreStatement().execute();
                return super.update(entity);
            } catch (SQLException ex) {
                ExceptionReporting.showException(ex, new Object[]{entity});
                return false;
            }
        } else {
            // add the actual lot to the existing one!
            oldLot.setQte(oldLot.getQte() + entity.getQte());
            oldLot.setCodBar(entity.getCodBar());
            oldLot.setDateEntr(entity.getDateEntr());
            oldLot.setPuVntDt(entity.getPuVntDt());
            oldLot.setPuVntGr(entity.getPuVntGr());
            oldLot.setPuVntDgr(entity.getPuVntDgr());
            oldLot.setPuVntSgr(entity.getPuVntSgr());

            EnStock updatedLot = getObjectByID(entity.getId());
            updatedLot.setQte(0);
            entity.setId(oldLot.getId());// (to be used by MajLigneAchat, as foreign key!)
            return updatedLot.update() && oldLot.update();
        }
    }

    public ResultSet getDetailsStock(Produit prod) {
        ResultSet rs = null;
        try {
            getByProdSt.setInt(1, prod.getId());
            rs = getByProdSt.executeQuery();
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{prod});
        }
        return rs;
    }

    public ResultSet getLotsAVendre() {
        ResultSet rs = null;
        try {
            rs = getProdsAVendreSt.executeQuery();
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
        }
        return rs;
    }
    
    public ResultSet getLotsDispo() {
        ResultSet rs = null;
        try {
            rs = getLotDispoSt.executeQuery();
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
        }
        return rs;
    }
    
    public ResultSet getLotsNonDispo() {
        ResultSet rs = null;
        try {
            rs = getLotNotDispoSt.executeQuery();
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
        }
        return rs;
    }

    public static void main(String[] args) {

        System.out.println("max: " + new java.sql.Date(253402210800000L).getTime());
        System.out.println("max: " + DateTools.getMaxSqlDate());
    }

}
