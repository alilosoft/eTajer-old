package dao;

import dbTools.DBManager;
import entities.Client;
import entities.CreditCl;
import entities.EntityPK;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
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
public class ClientDAO extends TableDAO<Client,EntityPK> {
    public static final String SQL_FILES_PATH = "/sql/client/";
    private static ClientDAO instance = null;
    
    private final String getTotalCreditQry;
    private final String getCreditInitQry;
    
    private PreparedStatement getTotalCreditStm;
    private PreparedStatement getCreditInitStm;

    public ClientDAO() {
        super(SQL_FILES_PATH);
        getTotalCreditQry = QueryReader.getQueries(sqlFilesPath + "GetTotalCredit.sql", false)[0];
        getCreditInitQry = QueryReader.getQueries(sqlFilesPath + "GetCreditInitial.sql", false)[0];
    }
    
    @Override
    public synchronized boolean prepare(Connection c) {
        boolean prep = super.prepare(c);
        try {
            getTotalCreditStm = getConnection().prepareStatement(getTotalCreditQry);
            getCreditInitStm = getConnection().prepareStatement(getCreditInitQry);
            return prep;
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
            return false;
        }
    }

    public static ClientDAO getInstance() {
        if (instance == null) {
            instance = new ClientDAO();
            instance.prepare(DBManager.getInstance().getDefaultConnection());
        }
        return instance;
    }

    public static ClientDAO getInstance(Connection c) {
        if (instance == null) {
            instance = new ClientDAO();
            if (c != null) {
                instance.prepare(c);
            } else {
                instance.prepare(DBManager.getInstance().getDefaultConnection());
            }
        }
        return instance;
    }

    @Override
    public Client getObjectByID(int id) {
        Client client = null;
        ResultSet rs = getWhereID(id);
        try {
            if (rs.next()) {
                String code = rs.getString("CODE");
                String name = rs.getString("NOM");
                String adr = rs.getString("ADR");
                String tel = rs.getString("TEL");
                String mobile = rs.getString("MOBILE");
                String email = rs.getString("EMAIL");
                String rc = rs.getString("NUM_RC");
                String fisc = rs.getString("NUM_FISC");
                String art = rs.getString("NUM_ART");
                boolean appTva = rs.getBoolean("APP_TVA");
                BigDecimal dette = rs.getBigDecimal("DETTE");
                boolean fellah = rs.getBoolean("FELLAH");
                client = new Client(id, code, name, adr, tel, mobile, email, rc, fisc, art, appTva);
                client.setDette(dette);
                client.setFellah(fellah);
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{id});
        }
        return client;
    }

    @Override
    public Client getObjectByPK(EntityPK pK) {
        return getObjectByID(pK.getId());
    }
    
    public BigDecimal getTotalCredit() {
        BigDecimal total = new BigDecimal(0);
        try {
            ResultSet rs = getTotalCreditStm.executeQuery();
            if (rs.next()) {
                total = rs.getBigDecimal(1);
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
        }
        return total;
    }
    
    public CreditCl getCreditInitial(Client c) {
        CreditCl cInit = new CreditCl(0);
        try {
            getCreditInitStm.setInt(1, c.getId());
            ResultSet rs = getCreditInitStm.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                cInit = CreditClDAO.getInstance().getObjectByID(id);
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
        }
        return cInit;
    }

    @Override
    public boolean insert(Client entity) {
        int ind = 1;
        try {
            getInsertPreStatement().setString(ind++, entity.getCode());
            getInsertPreStatement().setString(ind++, entity.getNom());
            getInsertPreStatement().setString(ind++, entity.getAdr());
            getInsertPreStatement().setString(ind++, entity.getTel());
            getInsertPreStatement().setString(ind++, entity.getMobile());
            getInsertPreStatement().setString(ind++, entity.getEmail());
            getInsertPreStatement().setString(ind++, entity.getNumRc());
            getInsertPreStatement().setString(ind++, entity.getNumFisc());
            getInsertPreStatement().setString(ind++, entity.getNumArt());
            getInsertPreStatement().setBoolean(ind++, entity.isAppTva());
            getInsertPreStatement().setBoolean(ind++, entity.isFellah());
            getInsertPreStatement().execute();
            return super.insert(entity);
        } catch (SQLException ex) {
            if (ex.getSQLState().equals("23505")) {
                String mess = "Cette opération entraîne la duplication du certain information,"
                        + "\npenser a utiliser un autre code pour ce client!"
                        + "\n\nError: " + ex.getSQLState() + "\n" + ex.getLocalizedMessage();
                MessageReporting.showMessage(Level.WARNING, getClass(), "insert()", mess, new Object[]{entity});
            } else {
                ExceptionReporting.showException(ex, new Object[]{entity});
            }
            return false;
        }
    }

    @Override
    public boolean update(Client entity) {
        int ind = 1;
        try {
            getUpdatePreStatement().setString(ind++, entity.getCode());
            getUpdatePreStatement().setString(ind++, entity.getNom());
            getUpdatePreStatement().setString(ind++, entity.getAdr());
            getUpdatePreStatement().setString(ind++, entity.getTel());
            getUpdatePreStatement().setString(ind++, entity.getMobile());
            getUpdatePreStatement().setString(ind++, entity.getEmail());
            getUpdatePreStatement().setString(ind++, entity.getNumRc());
            getUpdatePreStatement().setString(ind++, entity.getNumFisc());
            getUpdatePreStatement().setString(ind++, entity.getNumArt());
            getUpdatePreStatement().setBoolean(ind++, entity.isAppTva());
            getUpdatePreStatement().setBoolean(ind++, entity.isFellah());
            getUpdatePreStatement().setInt(ind++, entity.getId());
            getUpdatePreStatement().execute();
            return super.update(entity);
        } catch (SQLException ex) {
            if (ex.getSQLState().equals("23505")) {
                String mess = "Cette opération entraîne la duplication du certain information,"
                        + "\npenser a utiliser un autre code pour ce client!"
                        + "\n\nError: " + ex.getSQLState() + "\n" + ex.getLocalizedMessage();
                MessageReporting.showMessage(Level.WARNING, getClass(), "update()", mess, new Object[]{entity});
            } else {
                ExceptionReporting.showException(ex, new Object[]{entity});
            }
            return false;
        }
    }
}
