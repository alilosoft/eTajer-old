package dao;

import dbTools.DBManager;
import entities.Achat;
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
public class AchatDAO extends TableDAO<Achat,  EntityPK> {
    public static final String SQL_FILES_PATH = "/sql/achat/";
    private static AchatDAO instance = null;
    private final String getAchFournQry;
    private final String getNumAchQry;
    private final String getTotalAchatQry;
    private final String validateAchQry;
    private final String invalidateAchQry;
    private PreparedStatement getAchFournStm;
    private PreparedStatement validateAchStm;
    private PreparedStatement invalidateAchStm;
    private PreparedStatement getNumAchStm;
    private PreparedStatement getTotalAchStm;

    public AchatDAO() {
        super(SQL_FILES_PATH);
        getAchFournQry = QueryReader.getQueries(sqlFilesPath + "GetAchatFourn.sql", false)[0];
        getNumAchQry = QueryReader.getQueries(sqlFilesPath + "GetNumAchat.sql", false)[0];
        getTotalAchatQry = QueryReader.getQueries(sqlFilesPath + "GetTotalAch.sql", false)[0];
        
        validateAchQry = QueryReader.getQueries(sqlFilesPath + "Validate.sql", false)[0];
        invalidateAchQry = QueryReader.getQueries(sqlFilesPath + "Invalidate.sql", false)[0];
    }

    @Override
    public boolean prepare(Connection c) {
        boolean prep = super.prepare(c);
        try {
            //getAchFournStm = getConnection().prepareStatement(getAchFournQry, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            getNumAchStm = getConnection().prepareStatement(getNumAchQry);
            getTotalAchStm = getConnection().prepareStatement(getTotalAchatQry);
            
            validateAchStm = getConnection().prepareStatement(validateAchQry);
            invalidateAchStm = getConnection().prepareStatement(invalidateAchQry);
            
            return prep;
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
            return false;
        }
    }

    public static AchatDAO getInstance() {
        if (instance == null) {
            instance = new AchatDAO();
            instance.prepare(DBManager.getInstance().getDefaultConnection());
        }
        return instance;
    }

    public static AchatDAO getInstance(Connection c) {
        if (instance == null) {
            instance = new AchatDAO();
            if (c != null) {
                instance.prepare(c);
            } else {
                instance.prepare(DBManager.getInstance().getDefaultConnection());
            }
        }
        return instance;
    }

    @Override
    public Achat getObjectByID(int id) {
        Achat ach = null;
        ResultSet rs = getWhereID(id);
        try {
            if (rs.next()) {
                int idCl = rs.getInt("ID_FR");
                int num = rs.getInt("NUM");
                Date date = rs.getDate("DATE");
                Time heure = rs.getTime("HEURE");
                boolean valide = rs.getBoolean("VALIDE");
                ach = new Achat(id, num, date, heure, valide);
                ach.setFournisseur(FournissDAO.getInstance().getObjectByID(idCl));
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{id});
        }
        return ach;
    }

    @Override
    public Achat getObjectByPK(EntityPK pK) {
        return getObjectByID(pK.getId());
    }
    
    public ResultSet getAchatsFourn(){
        ResultSet rs = null;
        try {
            rs = getAchFournStm.executeQuery();
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
        }
        return rs;
    }
    
    public int getNumAchat(java.util.Date beginPeriod, java.util.Date endPeriod) {
        int numVnt = 1;
        try {
            getNumAchStm.setDate(1, DateTools.getSqlDate(beginPeriod));
            getNumAchStm.setDate(2, DateTools.getSqlDate(endPeriod));
            ResultSet rs = getNumAchStm.executeQuery();
            if (rs.next()) {
                numVnt = rs.getInt(1) + 1;
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{beginPeriod, endPeriod});
        }
        return numVnt;
    }

    public BigDecimal getTotalAchat(Achat ach) {
        BigDecimal total = new BigDecimal(0);
        try {
            getTotalAchStm.setInt(1, ach.getId());
            ResultSet rs = getTotalAchStm.executeQuery();
            if (rs.next()) {
                total = rs.getBigDecimal("TOTAL");
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{ach});
        }
        return total;
    }

    @Override
    public boolean insert(Achat entity) {
        int ind = 1;
        try {
            if(entity.getFournisseur() == null || entity.getFournisseur().getId() == 0){
                getInsertPreStatement().setNull(ind++, Types.INTEGER);
            }else{
                getInsertPreStatement().setInt(ind++, entity.getFournisseur().getId());
            }
            getInsertPreStatement().setInt(ind++, entity.getNum());
            getInsertPreStatement().setDate(ind++, DateTools.getSqlDate(entity.getDate()));
            getInsertPreStatement().setTime(ind++, DateTools.getSqlTime(entity.getHeure()));
            getInsertPreStatement().execute();
            return super.insert(entity);
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{entity});
            return false;
        }
    }

    @Override
    public boolean update(Achat entity) {
        int ind = 1;
        try {
            if(entity.getFournisseur() == null || entity.getFournisseur().getId() == 0){
                getUpdatePreStatement().setNull(ind++, Types.INTEGER);
            }else{
                getUpdatePreStatement().setInt(ind++, entity.getFournisseur().getId());
            }
            getUpdatePreStatement().setInt(ind++, entity.getNum());
            getUpdatePreStatement().setDate(ind++, DateTools.getSqlDate(entity.getDate()));
            getUpdatePreStatement().setTime(ind++, DateTools.getSqlTime(entity.getHeure()));
            getUpdatePreStatement().setInt(ind++, entity.getId());
            getUpdatePreStatement().execute();
            return super.update(entity);
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{entity});
            return false;
        }
    }

    public boolean validate(Achat achat) {
        try {
            validateAchStm.setInt(1, achat.getId());
            return validateAchStm.executeUpdate() == 1;
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{achat});
            return false;
        }
    }

    public boolean invalidate(Achat achat) {
        try {
            invalidateAchStm.setInt(1, achat.getId());
            return invalidateAchStm.executeUpdate() == 1;
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{achat});
            return false;
        }
    }
    
}
