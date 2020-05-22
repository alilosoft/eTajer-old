package dao;

import dbTools.DBManager;
import entities.EntityPK;
import entities.Produit;
import entities.Quantifier;
import entities.Unite;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import sql.QueryReader;
import tools.ExceptionReporting;


/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author alilo
 */
public class QuantifierDAO extends TableDAO<Quantifier, EntityPK> {

    public static final String SQL_FILES_PATH = "/sql/quantif/";

    private static QuantifierDAO instance = null;
    private final String getByProdQry;
    private final String deleteByProdQry;
    private PreparedStatement getByProdStmt;
    private PreparedStatement deleteByProdStmt;

    public QuantifierDAO() {
        super(SQL_FILES_PATH);
        getByProdQry = QueryReader.getQueries(sqlFilesPath + "GetWhIDProd.sql", false)[0];
        deleteByProdQry = QueryReader.getQueries(sqlFilesPath + "DeleteWhIDProd.sql", false)[0];
    }

    public static QuantifierDAO getInstance() {
        if (instance == null) {
            instance = new QuantifierDAO();
            instance.prepare(DBManager.getInstance().getDefaultConnection());
        }
        return instance;
    }

    public static QuantifierDAO getInstance(Connection c) {
        if (instance == null) {
            instance = new QuantifierDAO();
            if (c != null) {
                instance.prepare(c);
            } else {
                instance.prepare(DBManager.getInstance().getDefaultConnection());
            }
        }
        return instance;
    }

    @Override
    public boolean prepare(Connection c) {
        boolean prep = super.prepare(c);
        try {
            getByProdStmt = getConnection().prepareStatement(getByProdQry);
            deleteByProdStmt = getConnection().prepareStatement(deleteByProdQry);
            return prep;
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
            return false;
        }
    }

    @Override
    public Quantifier getObjectByID(int id) {
        Quantifier q = null;
        ResultSet rs = getWhereID(id);
        try {
            if (rs.next()) {
                int idProd = rs.getInt("ID_PROD");
                int idUnit = rs.getInt("ID_UNITE");
                boolean unitDt = rs.getBoolean("UNITE_DT");
                boolean unitGr = rs.getBoolean("UNITE_GR");
                boolean unitDGr = rs.getBoolean("UNITE_DGR");
                boolean unitSGr = rs.getBoolean("UNITE_SGR");
                q = new Quantifier(id, unitDt, unitGr, unitDGr, unitSGr);
                q.setProduit(ProduitDAO.getInstance().getObjectByID(idProd));
                q.setUnite(UniteDAO.getInstance().getObjectByID(idUnit));
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{id});
        }
        return q;
    }

    @Override
    public Quantifier getObjectByPK(EntityPK pK) {
        return getObjectByID(pK.getId());
    }

    /**
     * Get the list of 'Emballages' associated with a 'Produit'.<br> This method
     * sort the list by the 'Qte.Lot'.
     *
     * @param prod
     * @return
     */
    public List<Quantifier> getUnitesOfProd(Produit prod) {
        List<Quantifier> unites = Collections.synchronizedList(new ArrayList<Quantifier>());
        Quantifier q;
        ResultSet rs;
        try {
            getByProdStmt.setInt(1, prod.getId());
            rs = getByProdStmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("ID");
                int idUnit = rs.getInt("ID_UNITE");
                boolean unitDt = rs.getBoolean("UNITE_DT");
                boolean unitGr = rs.getBoolean("UNITE_GR");
                boolean unitDGr = rs.getBoolean("UNITE_DGR");
                boolean unitSGr = rs.getBoolean("UNITE_SGR");
                q = new Quantifier(id, unitDt, unitGr, unitDGr, unitSGr);
                q.setProduit(prod);
                Unite u = UniteDAO.getInstance().getObjectByID(idUnit);
                q.setUnite(u);
                unites.add(q);
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{prod});
        }
        return unites;
    }

    @Override
    public boolean insert(Quantifier entity) {
        int ind = 1;
        try {
            getInsertPreStatement().setInt(ind++, entity.getProduit().getId());
            getInsertPreStatement().setInt(ind++, entity.getUnite().getId());
            getInsertPreStatement().setBoolean(ind++, entity.getUniteDt());
            getInsertPreStatement().setBoolean(ind++, entity.getUniteGr());
            getInsertPreStatement().setBoolean(ind++, entity.getUniteDGr());
            getInsertPreStatement().setBoolean(ind++, entity.getUniteSGr());
            getInsertPreStatement().execute();
            return super.insert(entity);
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{entity});
            return false;
        }
    }

    @Override
    public boolean update(Quantifier entity) {
        int ind = 1;
        try {
            getUpdatePreStatement().setInt(ind++, entity.getProduit().getId());
            getUpdatePreStatement().setInt(ind++, entity.getUnite().getId());
            getUpdatePreStatement().setBoolean(ind++, entity.getUniteDt());
            getUpdatePreStatement().setBoolean(ind++, entity.getUniteGr());
            getUpdatePreStatement().setBoolean(ind++, entity.getUniteDGr());
            getUpdatePreStatement().setBoolean(ind++, entity.getUniteSGr());
            getUpdatePreStatement().setInt(ind++, entity.getId());
            getUpdatePreStatement().execute();
            return super.update(entity);
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{entity});
            return false;
        }
    }

    public boolean deleteByProduit(Produit p) {
        try {
            deleteByProdStmt.setInt(1, p.getId());
            return deleteByProdStmt.executeUpdate() != 0;
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{p});
            return false;
        }
    }
}
