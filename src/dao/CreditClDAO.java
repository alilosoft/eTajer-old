package dao;

import dbTools.DBManager;
import entities.CreditCl;
import entities.EntityPK;
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
public class CreditClDAO extends TableDAO<CreditCl, EntityPK> {

    public static final String SQL_FILES_PATH = "/sql/creditCl/";
    private static CreditClDAO instance = null;
    private final String getTotalCredInPeriodQry;
    private PreparedStatement getTotalCredInPeriodStm;

    public CreditClDAO() {
        super(SQL_FILES_PATH);
        getTotalCredInPeriodQry = QueryReader.getQueries(sqlFilesPath + "GetTotalCreditInPeriod.sql", false)[0];
    }

    @Override
    public synchronized boolean prepare(Connection c) {
        boolean prep = super.prepare(c);
        try {
            getTotalCredInPeriodStm = getConnection().prepareStatement(getTotalCredInPeriodQry);
            return prep;
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
            return false;
        }
    }

    public static CreditClDAO getInstance() {
        if (instance == null) {
            instance = new CreditClDAO();
            instance.prepare(DBManager.getInstance().getDefaultConnection());
        }
        return instance;
    }

    public static CreditClDAO getInstance(Connection c) {
        if (instance == null) {
            instance = new CreditClDAO();
            if (c != null) {
                instance.prepare(c);
            } else {
                instance.prepare(DBManager.getInstance().getDefaultConnection());
            }
        }
        return instance;
    }

    @Override
    public CreditCl getObjectByID(int id) {
        CreditCl credit = null;
        ResultSet rs = getWhereID(id);
        try {
            if (rs.next()) {
                int idCl = rs.getInt("ID_CL");
                int idVnt = rs.getInt("ID_VNT");
                Date date = rs.getDate("DATE");
                Time heure = rs.getTime("HEURE");
                BigDecimal montant = rs.getBigDecimal("MONTANT");
                String comment = rs.getString("COMMENT");
                boolean init = rs.getBoolean("INITIAL");
                credit = new CreditCl(id, date, heure, montant, comment, init);
                credit.setClient(ClientDAO.getInstance().getObjectByID(idCl));
                if (idVnt != 0) {
                    credit.setVente(VenteDAO.getInstance().getObjectByID(idVnt));
                }
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{id});
        }
        return credit;
    }

    @Override
    public CreditCl getObjectByPK(EntityPK pK) {
        return getObjectByID(pK.getId());
    }

    @Override
    public boolean insert(CreditCl entity) {
        int ind = 1;
        try {
            getInsertPreStatement().setInt(ind++, entity.getClient().getId());
            if (entity.getVente() == null || entity.getVente().getId() == 0) {
                getInsertPreStatement().setNull(ind++, Types.INTEGER);
            } else {
                getInsertPreStatement().setInt(ind++, entity.getVente().getId());
            }
            getInsertPreStatement().setDate(ind++, DateTools.getSqlDate(entity.getDate()));
            getInsertPreStatement().setTime(ind++, DateTools.getSqlTime(entity.getDate()));
            getInsertPreStatement().setBigDecimal(ind++, entity.getMontant());
            getInsertPreStatement().setString(ind++, entity.getComment());
            getInsertPreStatement().setBoolean(ind++, entity.isInitial());
            getInsertPreStatement().execute();
            return super.insert(entity);
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{entity});
            return false;
        }
    }

    @Override
    public boolean update(CreditCl entity) {
        int ind = 1;
        try {
            getUpdatePreStatement().setInt(ind++, entity.getClient().getId());
            if (entity.getVente() == null || entity.getVente().getId() == 0) {
                getUpdatePreStatement().setNull(ind++, Types.INTEGER);
            } else {
                getUpdatePreStatement().setInt(ind++, entity.getVente().getId());
            }
            getUpdatePreStatement().setDate(ind++, DateTools.getSqlDate(entity.getDate()));
            getUpdatePreStatement().setTime(ind++, DateTools.getSqlTime(entity.getDate()));
            getUpdatePreStatement().setBigDecimal(ind++, entity.getMontant());
            getUpdatePreStatement().setString(ind++, entity.getComment());
            getUpdatePreStatement().setInt(ind++, entity.getId());
            getUpdatePreStatement().execute();
            return super.update(entity);
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{entity});
            return false;
        }
    }
    
    public BigDecimal getTotalCredits(java.util.Date beginDate, java.util.Date endDate) {
        BigDecimal total = new BigDecimal(0);
        try {
            getTotalCredInPeriodStm.setDate(1, DateTools.getSqlDate(beginDate));
            getTotalCredInPeriodStm.setDate(2, DateTools.getSqlDate(endDate));
            ResultSet rs = getTotalCredInPeriodStm.executeQuery();
            if (rs.next() && rs.getBigDecimal(1) != null) {
                total = rs.getBigDecimal(1);
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{beginDate});
        }
        return total;
    }
}
