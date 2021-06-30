package dao;

import dbTools.DBManager;
import entities.Achat;
import entities.EntityPK;
import entities.Fournisseur;
import entities.ModePaye;
import entities.ReglementFr;
import java.math.BigDecimal;
import java.sql.*;
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
public class ReglementFrDAO extends TableDAO<ReglementFr, EntityPK> {

    public static final String SQL_FILES_PATH = "/sql/reglementFr/";
    private static ReglementFrDAO instance = null;
    private final String getReglByAchQry;
    private PreparedStatement getReglByAchSt;
    
    public ReglementFrDAO() {
        super(SQL_FILES_PATH);
        getReglByAchQry = QueryReader.getQueries(SQL_FILES_PATH + "GetByAch.sql", false)[0];
    }

    @Override
    public boolean prepare(Connection c) {
        boolean prep = super.prepare(c);
        try {
            getReglByAchSt = getConnection().prepareStatement(getReglByAchQry);
            return prep;
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
            return false;
        }
    }

    public static ReglementFrDAO getInstance() {
        if (instance == null) {
            instance = new ReglementFrDAO();
            instance.prepare(DBManager.getInstance().getDefaultConnection());
        }
        return instance;
    }

    public static ReglementFrDAO getInstance(Connection c) {
        if (instance == null) {
            instance = new ReglementFrDAO();
            if (c != null) {
                instance.prepare(c);
            } else {
                instance.prepare(DBManager.getInstance().getDefaultConnection());
            }
        }
        return instance;
    }

    @Override
    public ReglementFr getObjectByID(int id) {
        ReglementFr reglement = null;
        ResultSet rs = getWhereID(id);
        try {
            if (rs.next()) {
                int idFr = rs.getInt("ID_FR");
                int idAch = rs.getInt("ID_ACH");
                Date dateRegl = rs.getDate("DATE");
                Time heureRegl = rs.getTime("HEURE");
                BigDecimal montant = rs.getBigDecimal("MONTANT");
                int idModePaye = rs.getInt("MODE_PAY");
                String comment = rs.getString("COMMENT");
                reglement = new ReglementFr(id, dateRegl, heureRegl, montant, new ModePaye(idModePaye), comment);
                reglement.setFournisseur(new Fournisseur(idFr));
                if (idAch != 0) {
                    reglement.setAchat(new Achat(idAch));
                }
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{id});
        }
        return reglement;
    }

    @Override
    public ReglementFr getObjectByPK(EntityPK pK) {
        return getObjectByID(pK.getId());
    }
    
    public ReglementFr getReglementOfAch(Achat ach){
        ReglementFr regl = null;
        ResultSet rs;
        try {
            getReglByAchSt.setInt(1, ach.getId());
            rs = getReglByAchSt.executeQuery();
            if(rs.next()){
                int idRegl = rs.getInt("ID");
                regl = getObjectByID(idRegl);
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{ach});
        }
        return regl;
    }

    @Override
    public boolean insert(ReglementFr entity) {
        int ind = 1;
        try {
            getInsertPreStatement().setInt(ind++, entity.getFournisseur().getId());
            if (entity.getAchat() == null || entity.getAchat().getId() == 0) {
                getInsertPreStatement().setNull(ind++, Types.INTEGER);
            } else {
                getInsertPreStatement().setInt(ind++, entity.getAchat().getId());
            }
            getInsertPreStatement().setDate(ind++, new Date(entity.getDate().getTime()));
            getInsertPreStatement().setTime(ind++, new Time(entity.getHeure().getTime()));
            getInsertPreStatement().setBigDecimal(ind++, entity.getMontant());
            getInsertPreStatement().setInt(ind++, entity.getModePaye().getId());
            getInsertPreStatement().setString(ind++, entity.getComment());
            getInsertPreStatement().execute();
            return super.insert(entity);
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{entity});
            return false;
        }
    }

    @Override
    public boolean update(ReglementFr entity) {
        int ind = 1;
        try {
            getUpdatePreStatement().setInt(ind++, entity.getFournisseur().getId());
            if (entity.getAchat()== null || entity.getAchat().getId() == 0) {
                getUpdatePreStatement().setNull(ind++, Types.INTEGER);
            } else {
                getUpdatePreStatement().setInt(ind++, entity.getAchat().getId());
            }
            getUpdatePreStatement().setDate(ind++, new Date(entity.getDate().getTime()));
            getUpdatePreStatement().setTime(ind++, new Time(entity.getHeure().getTime()));
            getUpdatePreStatement().setBigDecimal(ind++, entity.getMontant());
            getUpdatePreStatement().setInt(ind++, entity.getModePaye().getId());
            getUpdatePreStatement().setString(ind++, entity.getComment());
            getUpdatePreStatement().setInt(ind++, entity.getId());
            getUpdatePreStatement().execute();
            return super.update(entity);
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{entity});
            return false;
        }
    }
}
