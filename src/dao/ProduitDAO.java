package dao;

import dbTools.DBManager;
import entities.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sql.QueryReader;
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
public class ProduitDAO extends TableDAO<Produit, EntityPK> {

    public static final String SQL_FILES_PATH = "/sql/produit/";
    private static ProduitDAO instance = null;

    private final String getQteGlobalQry;
    private final String checkBarCodeQry;
    private final String getByCBQry;

    private PreparedStatement getByCBSt;
    private PreparedStatement checkBarCodeSt;
    private PreparedStatement getQteGlobalSt;

    public ProduitDAO() {
        super(SQL_FILES_PATH);
        getQteGlobalQry = QueryReader.getQueries(SQL_FILES_PATH + "GetQteGlobal.sql", false)[0];
        getByCBQry = QueryReader.getQueries(SQL_FILES_PATH + "GetByCodBar.sql", false)[0];
        checkBarCodeQry = QueryReader.getQueries(SQL_FILES_PATH + "CheckCodeBarres.sql", false)[0];
    }

    @Override
    public boolean prepare(Connection c) {
        boolean prep = super.prepare(c);
        try {
            getQteGlobalSt = getConnection().prepareStatement(getQteGlobalQry);
            getByCBSt = getConnection().prepareStatement(getByCBQry);
            checkBarCodeSt = getConnection().prepareStatement(checkBarCodeQry);
            return prep;
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
            return false;
        }
    }

    public static ProduitDAO getInstance() {
        if (instance == null) {
            instance = new ProduitDAO();
            instance.prepare(DBManager.getInstance().getDefaultConnection());
        }
        return instance;
    }

    public static ProduitDAO getInstance(Connection c) {
        if (instance == null) {
            instance = new ProduitDAO();
            if (c != null) {
                instance.prepare(c);
            } else {
                instance.prepare(DBManager.getInstance().getDefaultConnection());
            }
        }
        return instance;
    }

    @Override
    public Produit getObjectByID(int id) {
        Produit produit = null;
        ResultSet rs = getWhereID(id);
        try {
            if (rs.next()) {
                int idFam = rs.getInt("ID_FAM");
                int idCat = rs.getInt("ID_CATEG");
                String cb = rs.getString("COD_BAR");
                String des = rs.getString("DES");
                int qteMin = rs.getInt("QTE_MIN");
                int qteMax = rs.getInt("QTE_MAX");
                BigDecimal pmp = rs.getBigDecimal("PMP");
                double margeDt = rs.getDouble("MARGE_DT");
                double margeGr = rs.getDouble("MARGE_GR");
                double margeDGr = rs.getDouble("MARGE_DGR");
                double margeSGr = rs.getDouble("MARGE_SGR");
                produit = new Produit(id, cb, des, qteMin, qteMax, pmp, margeDt, margeGr, margeDGr, margeSGr);
                Famille famille = FamilleDAO.getInstance().getObjectByID(idFam);
                produit.setFamille(famille);
                Categorie categorie = CategorieDAO.getInstance().getObjectByID(idCat);
                produit.setCateg(categorie);
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{id});
        }
        return produit;
    }

    @Override
    public Produit getObjectByPK(EntityPK pK) {
        return getObjectByID(pK.getId());
    }

    public Produit getByCodBar(String cb) {
        Produit produit = null;

        try {
            getByCBSt.setString(1, cb);
            ResultSet rs = getByCBSt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("ID");
                int idFam = rs.getInt("ID_FAM");
                int idCat = rs.getInt("ID_CATEG");
                String des = rs.getString("DES");
                int qteMin = rs.getInt("QTE_MIN");
                int qteMax = rs.getInt("QTE_MAX");
                BigDecimal pmp = rs.getBigDecimal("PMP");
                double margeDt = rs.getDouble("MARGE_DT");
                double margeGr = rs.getDouble("MARGE_GR");
                double margeDGr = rs.getDouble("MARGE_DGR");
                double margeSGr = rs.getDouble("MARGE_SGR");
                produit = new Produit(id, cb, des, qteMin, qteMax, pmp, margeDt, margeGr, margeDGr, margeSGr);
                Famille famille = FamilleDAO.getInstance().getObjectByID(idFam);
                produit.setFamille(famille);
                Categorie categorie = CategorieDAO.getInstance().getObjectByID(idCat);
                produit.setCateg(categorie);
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{cb});
        }
        return produit;
    }

    public boolean checkBarCode(String cb, int idProd) {
        boolean exist = false;
        try {
            checkBarCodeSt.setString(1, cb);
            checkBarCodeSt.setInt(2, idProd);
            ResultSet rs = checkBarCodeSt.executeQuery();
            if (rs.next()) {
                exist = rs.getInt(1) != 0;
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{cb});
        }
        return exist;
    }

    @Override
    public boolean insert(Produit entity) {
        int ind = 1;
        try {
            getInsertPreStatement().setInt(ind++, entity.getFamille().getId());
            getInsertPreStatement().setInt(ind++, entity.getCateg().getId());
            getInsertPreStatement().setString(ind++, entity.getCodBar());
            getInsertPreStatement().setString(ind++, entity.getDes());
            getInsertPreStatement().setInt(ind++, entity.getQteMin());
            getInsertPreStatement().setInt(ind++, entity.getQteMax());
            getInsertPreStatement().setDouble(ind++, entity.getMargeDt());
            getInsertPreStatement().setDouble(ind++, entity.getMargeGr());
            getInsertPreStatement().setDouble(ind++, entity.getMargeDgr());
            getInsertPreStatement().setDouble(ind++, entity.getMargeSgr());
            getInsertPreStatement().execute();
            return super.insert(entity);
        } catch (SQLException ex) {
            if (ex.getSQLState().equals("23505")) {
                String mess = "Cette opération entraîne la duplication du certain information,"
                        + "\npenser a utiliser un autre code barres pour ce produit!"
                        + "\n\nError: " + ex.getSQLState() + "\n" + ex.getLocalizedMessage();
                MessageReporting.showMessage(Level.WARNING, getClass(), "insert()", mess, new Object[]{entity});
            } else {
                ExceptionReporting.showException(ex, new Object[]{entity});
            }
            return false;
        }
    }

    @Override
    public boolean update(Produit entity) {
        int ind = 1;
        try {
            getUpdatePreStatement().setInt(ind++, entity.getFamille().getId());
            getUpdatePreStatement().setInt(ind++, entity.getCateg().getId());
            getUpdatePreStatement().setString(ind++, entity.getCodBar());
            getUpdatePreStatement().setString(ind++, entity.getDes());
            getUpdatePreStatement().setInt(ind++, entity.getQteMin());
            getUpdatePreStatement().setInt(ind++, entity.getQteMax());
            getUpdatePreStatement().setDouble(ind++, entity.getMargeDt());
            getUpdatePreStatement().setDouble(ind++, entity.getMargeGr());
            getUpdatePreStatement().setDouble(ind++, entity.getMargeDgr());
            getUpdatePreStatement().setDouble(ind++, entity.getMargeSgr());
            getUpdatePreStatement().setInt(ind++, entity.getId());
            getUpdatePreStatement().execute();
            return super.update(entity);
        } catch (SQLException ex) {
            if (ex.getSQLState().equals("23505")) {
                String mess;
                mess = "Cette opération entraîne la duplication du certain information,"
                        + "\npenser a utiliser un autre référence/C.B pour ce produit!"
                        + "\n\nError: " + ex.getSQLState() + "\n" + ex.getLocalizedMessage();
                MessageReporting.showMessage(Level.WARNING, getClass(), "update()", mess, new Object[]{entity});
            } else {
                ExceptionReporting.showException(ex, new Object[]{entity});
            }
            return false;
        }
    }

    public double getQteGlobal(Produit p) {
        double qte = 0;
        try {
            getQteGlobalSt.setInt(1, p.getId());
            ResultSet rs = getQteGlobalSt.executeQuery();
            if (rs.next()) {
                qte = rs.getDouble(1);
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
        }
        return qte;
    }
}
