package dao;

import dbTools.DBManager;
import entities.EntityPK;
import entities.ModePaye;
import entities.ReglementCl;
import entities.Vente;
import java.math.BigDecimal;
import java.sql.*;
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
public class ReglementClDAO extends TableDAO<ReglementCl, EntityPK> {

    public static final String SQL_FILES_PATH = "/sql/reglementCl/";
    private static ReglementClDAO instance = null;
    private final String getReglByVntQry;
    private PreparedStatement getReglByVntSt;
    private final String getTotalReglInPeriodQry;
    private PreparedStatement getTotalReglInPeriodStm;
    
    public ReglementClDAO() {
        super(SQL_FILES_PATH);
        getReglByVntQry = QueryReader.getQueries(SQL_FILES_PATH + "GetByVnt.sql", false)[0];
        getTotalReglInPeriodQry = QueryReader.getQueries(sqlFilesPath + "GetTotalReglsClients.sql", false)[0];
    }

    @Override
    public boolean prepare(Connection c) {
        boolean prep = super.prepare(c);
        try {
            getReglByVntSt = getConnection().prepareStatement(getReglByVntQry);
            getTotalReglInPeriodStm = getConnection().prepareStatement(getTotalReglInPeriodQry);
            return prep;
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
            return false;
        }
    }

    public static ReglementClDAO getInstance() {
        if (instance == null) {
            instance = new ReglementClDAO();
            instance.prepare(DBManager.getInstance().getDefaultConnection());
        }
        return instance;
    }

    public static ReglementClDAO getInstance(Connection c) {
        if (instance == null) {
            instance = new ReglementClDAO();
            if (c != null) {
                instance.prepare(c);
            } else {
                instance.prepare(DBManager.getInstance().getDefaultConnection());
            }
        }
        return instance;
    }

    @Override
    public ReglementCl getObjectByID(int id) {
        ReglementCl reglement = null;
        ResultSet rs = getWhereID(id);
        try {
            if (rs.next()) {
                int idCl = rs.getInt("ID_CL");
                int idVnt = rs.getInt("ID_VNT");
                Date dateRegl = rs.getDate("DATE");
                Time heureRegl = rs.getTime("HEURE");
                BigDecimal montant = rs.getBigDecimal("MONTANT");
                int idModePaye = rs.getInt("MODE_PAY");
                String comment = rs.getString("COMMENT");
                reglement = new ReglementCl(id, dateRegl, heureRegl, montant, new ModePaye(idModePaye), comment);
                reglement.setClient(ClientDAO.getInstance().getObjectByID(idCl));
                if (idVnt != 0) {
                    reglement.setVente(VenteDAO.getInstance().getObjectByID(idVnt));
                }
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{id});
        }
        return reglement;
    }

    @Override
    public ReglementCl getObjectByPK(EntityPK pK) {
        return getObjectByID(pK.getId());
    }
    
    public ReglementCl getReglementOfVnt(Vente vnt){
        ReglementCl rc = null;
        ResultSet rs;
        try {
            getReglByVntSt.setInt(1, vnt.getId());
            rs = getReglByVntSt.executeQuery();
            if(rs.next()){
                int idRegl = rs.getInt("ID");
                rc = getObjectByID(idRegl);
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{vnt});
        }
        return rc;
    }


    @Override
    public boolean insert(ReglementCl entity) {
        int ind = 1;
        try {
            getInsertPreStatement().setInt(ind++, entity.getClient().getId());
            if (entity.getVente() == null || entity.getVente().getId() == 0) {
                getInsertPreStatement().setNull(ind++, Types.INTEGER);
            } else {
                getInsertPreStatement().setInt(ind++, entity.getVente().getId());
            }
            getInsertPreStatement().setDate(ind++, DateTools.getSqlDate(entity.getDate()));
            getInsertPreStatement().setTime(ind++, DateTools.getSqlTime(entity.getHeure()));
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
    public boolean update(ReglementCl entity) {
        int ind = 1;
        try {
            getUpdatePreStatement().setInt(ind++, entity.getClient().getId());
            if (entity.getVente() == null || entity.getVente().getId() == 0) {
                getUpdatePreStatement().setNull(ind++, Types.INTEGER);
            } else {
                getUpdatePreStatement().setInt(ind++, entity.getVente().getId());
            }
            getUpdatePreStatement().setDate(ind++, DateTools.getSqlDate(entity.getDate()));
            getUpdatePreStatement().setTime(ind++, DateTools.getSqlTime(entity.getHeure()));
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
    
    public BigDecimal getTotalReglsClients(java.util.Date beginDate, java.util.Date endDate) {
        BigDecimal total = new BigDecimal(0);
        try {
            getTotalReglInPeriodStm.setDate(1, DateTools.getSqlDate(beginDate));
            getTotalReglInPeriodStm.setDate(2, DateTools.getSqlDate(endDate));
            ResultSet rs = getTotalReglInPeriodStm.executeQuery();
            if (rs.next() && rs.getBigDecimal(1) != null) {
                total = rs.getBigDecimal(1);
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{beginDate});
        }
        return total;
    }
}
