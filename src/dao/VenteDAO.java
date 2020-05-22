package dao;

import dbTools.DBManager;
import dialogs.LoginDialog;
import entities.AppUser;
import entities.EntityPK;
import entities.TypeVnt;
import entities.Vente;
import java.math.BigDecimal;
import java.sql.*;
import java.util.logging.Level;
import sql.QueryReader;
import tools.DateTools;
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
public class VenteDAO extends TableDAO<Vente,  EntityPK> {
    public static final String SQL_FILES_PATH = "/sql/vente/";
    private static VenteDAO instance = null;
    private final String getVntClQry;
    private final String getNumVenteQry;
    private final String getTotalVenteQry;
    private final String validateVenteQry;
    private final String invalidateVenteQry;
    private final String getTotalVentsQry;
    private final String getTotalVentsByUserQry;
    private final String getBenificeVntQry;
    private final String getVntsByDateQry;
    
    private PreparedStatement getVntClStm;
    private PreparedStatement validateVenteStm;
    private PreparedStatement invalidateVenteStm;
    private PreparedStatement getNumVenteStm;
    private PreparedStatement getTotalVenteStm;
    private PreparedStatement getTotalVentesStm;
    private PreparedStatement getTotalVentesByUserStm;
    private PreparedStatement getBenificeVntStm;
    private PreparedStatement getByDateStm;

    public VenteDAO() {
        super(SQL_FILES_PATH);
        getVntClQry = QueryReader.getQueries(sqlFilesPath + "GetVntClient.sql", false)[0];
        getNumVenteQry = QueryReader.getQueries(sqlFilesPath + "GetNumVnt.sql", false)[0];
        getTotalVenteQry = QueryReader.getQueries(sqlFilesPath + "GetTotalVnt.sql", false)[0];
        getBenificeVntQry = QueryReader.getQueries(sqlFilesPath + "GetBenificeByVnt.sql", false)[0];
        
        validateVenteQry = QueryReader.getQueries(sqlFilesPath + "Validate.sql", false)[0];
        invalidateVenteQry = QueryReader.getQueries(sqlFilesPath + "Invalidate.sql", false)[0];
        
        getTotalVentsQry = QueryReader.getQueries(sqlFilesPath + "GetTotalVentes.sql", false)[0];
        getTotalVentsByUserQry = QueryReader.getQueries(sqlFilesPath + "GetTotalVentesByUser.sql", false)[0];
        getVntsByDateQry = QueryReader.getQueries(sqlFilesPath + "GetByDate.sql", false)[0];
    }

    @Override
    public boolean prepare(Connection c) {
        boolean prep = super.prepare(c);
        try {
            //getVntClStm = getConnection().prepareStatement(getVntClQry, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            getNumVenteStm = getConnection().prepareStatement(getNumVenteQry);
            getTotalVenteStm = getConnection().prepareStatement(getTotalVenteQry);
            getBenificeVntStm = getConnection().prepareStatement(getBenificeVntQry);
            
            validateVenteStm = getConnection().prepareStatement(validateVenteQry);
            invalidateVenteStm = getConnection().prepareStatement(invalidateVenteQry);
            
            getTotalVentesStm = getConnection().prepareStatement(getTotalVentsQry);
            getTotalVentesByUserStm = getConnection().prepareStatement(getTotalVentsByUserQry);
            
            getByDateStm = getConnection().prepareStatement(getVntsByDateQry, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            return prep;
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
            return false;
        }
    }

    public static VenteDAO getInstance() {
        if (instance == null) {
            instance = new VenteDAO();
            instance.prepare(DBManager.getInstance().getDefaultConnection());
        }
        return instance;
    }

    public static VenteDAO getInstance(Connection c) {
        if (instance == null) {
            instance = new VenteDAO();
            if (c != null) {
                instance.prepare(c);
            } else {
                instance.prepare(DBManager.getInstance().getDefaultConnection());
            }
        }
        return instance;
    }

    @Override
    public Vente getObjectByID(int id) {
        Vente vente = null;
        ResultSet rs = getWhereID(id);
        try {
            if (rs.next()) {
                int idCl = rs.getInt("ID_CL");
                int idType = rs.getInt("ID_TYPE");
                int num = rs.getInt("NUM");
                Date date = rs.getDate("DATE");
                Time heure = rs.getTime("HEURE");
                boolean valide = rs.getBoolean("VALIDEE");
                vente = new Vente(id, num, date, heure, valide);
                vente.setClient(ClientDAO.getInstance().getObjectByID(idCl));
                vente.setTypeVnt(new TypeVnt(idType));
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{id});
        }
        return vente;
    }

    @Override
    public Vente getObjectByPK(EntityPK pK) {
        return getObjectByID(pK.getId());
    }
    
    public ResultSet getByDate(java.util.Date beginDate, java.util.Date endDate) {
        ResultSet rs = null;
        try {
            getByDateStm.setDate(1, DateTools.getSqlDate(beginDate));
            getByDateStm.setDate(2, DateTools.getSqlDate(endDate));
            rs = getByDateStm.executeQuery();
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
        }
        return rs;
    }
    
    public ResultSet getVentesCl(){
        ResultSet rs = null;
        try {
            rs = getVntClStm.executeQuery();
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
        }
        return rs;
    }
    
    public int getNumVente(java.util.Date beginPeriod, java.util.Date endPeriod) {
        int numVnt = 1;
        try {
            getNumVenteStm.setDate(1, DateTools.getSqlDate(beginPeriod));
            getNumVenteStm.setDate(2, DateTools.getSqlDate(endPeriod));
            ResultSet rs = getNumVenteStm.executeQuery();
            if (rs.next()) {
                numVnt = rs.getInt(1) + 1;
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{beginPeriod, endPeriod});
        }
        return numVnt;
    }
    
     
    
    public BigDecimal getTotalVente(Vente vente) {
        BigDecimal total = new BigDecimal(0);
        try {
            getTotalVenteStm.setInt(1, vente.getId());
            ResultSet rs = getTotalVenteStm.executeQuery();
            if (rs.next()) {
                total = rs.getBigDecimal("TOTAL");
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{vente});
        }
        return total;
    }
    
    public BigDecimal getBenificeVente(Vente vente) {
        BigDecimal benefice = new BigDecimal(0);
        try {
            getBenificeVntStm.setInt(1, vente.getId());
            ResultSet rs = getBenificeVntStm.executeQuery();
            if (rs.next()) {
                benefice = rs.getBigDecimal(1);
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{vente});
        }
        return benefice;
    }

    public BigDecimal getTotalVentes(java.util.Date beginDate, java.util.Date endDate) {
        BigDecimal total = new BigDecimal(0);
        try {
            getTotalVentesStm.setDate(1, DateTools.getSqlDate(beginDate));
            getTotalVentesStm.setDate(2, DateTools.getSqlDate(endDate));
            ResultSet rs = getTotalVentesStm.executeQuery();
            if (rs.next() && rs.getBigDecimal(1) != null) {
                total = rs.getBigDecimal(1);
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{beginDate});
        }
        return total;
    }
    
    public BigDecimal getTotalVentes(java.util.Date beginDate, java.util.Date endDate, AppUser user) {
        BigDecimal total = new BigDecimal(0);
        try {
            getTotalVentesByUserStm.setDate(1, DateTools.getSqlDate(beginDate));
            getTotalVentesByUserStm.setDate(2, DateTools.getSqlDate(endDate));
            getTotalVentesByUserStm.setInt(3, user.getId());
            ResultSet rs = getTotalVentesByUserStm.executeQuery();
            if (rs.next() && rs.getBigDecimal(1) != null) {
                total = rs.getBigDecimal(1);
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{beginDate});
        }
        return total;
    }
    

    @Override
    public boolean insert(Vente entity) {
        int ind = 1;
        try {
            getInsertPreStatement().setInt(ind++, LoginDialog.getUser().getId());
            if(entity.getClient() == null || entity.getClient().getId() == 0){
                getInsertPreStatement().setNull(ind++, Types.INTEGER);
            }else{
                getInsertPreStatement().setInt(ind++, entity.getClient().getId());
            }
            getInsertPreStatement().setInt(ind++, entity.getTypeVnt().getId());
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
    public boolean update(Vente entity) {
        int ind = 1;
        try {
            getUpdatePreStatement().setInt(ind++, LoginDialog.getUser().getId());
            if(entity.getClient() == null || entity.getClient().getId() == 0){
                getUpdatePreStatement().setNull(ind++, Types.INTEGER);
            }else{
                getUpdatePreStatement().setInt(ind++, entity.getClient().getId());
            }
            getUpdatePreStatement().setInt(ind++, entity.getTypeVnt().getId());
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

    public boolean validate(Vente vnt) {
        try {
            validateVenteStm.setInt(1, vnt.getId());
            return validateVenteStm.executeUpdate() == 1;
        } catch (SQLException ex) {
            if (ex.getSQLState().equals("23513") && ex.getMessage().contains("CHECK_QTE_STK")) {
                String mess;
                mess = "La validation de cette vente ne peut être éffectuer!\n"
                        + "Vérifier que tous les quantités demandés sont disponible en stock puis réessayer.\nError: " + ex.getSQLState();
                MessageReporting.showMessage(Level.WARNING, getClass(), "validate(Vente vnt)", mess, new Object[]{vnt});
            } else {
                ExceptionReporting.showException(ex, new Object[]{vnt});
            }
            return false;
        }
    }

    public boolean invalidate(Vente vente) {
        try {
            invalidateVenteStm.setInt(1, vente.getId());
            return invalidateVenteStm.executeUpdate() == 1;
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{vente});
            return false;
        }
    }
    
}
