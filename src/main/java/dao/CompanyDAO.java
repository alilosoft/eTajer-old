package dao;

import dbTools.DBManager;
import entities.Company;
import entities.EntityPK;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import tools.ExceptionReporting;


/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author alilo
 */
public class CompanyDAO extends TableDAO<Company, EntityPK> {

    public static final String SQL_FILES_PATH = "/sql/company/";
    private static CompanyDAO instance = null;

    public CompanyDAO() {
        super(SQL_FILES_PATH);
    }

    public static CompanyDAO getInstance() {
        if (instance == null) {
            instance = new CompanyDAO();
            instance.prepare(DBManager.getInstance().getDefaultConnection());
        }
        return instance;
    }

    public static CompanyDAO getInstance(Connection c) {
        if (instance == null) {
            instance = new CompanyDAO();
            if (c != null) {
                instance.prepare(c);
            } else {
                instance.prepare(DBManager.getInstance().getDefaultConnection());
            }
        }
        return instance;
    }

    @Override
    public Company getObjectByID(int id) {
        Company comp = null;
        ResultSet rs = getWhereID(id);
        try {
            if (rs.next()) {
                String name = rs.getString("COMPANY");
                String activ = rs.getString("ACTIVITY");
                String adr = rs.getString("ADRESSE");
                String email = rs.getString("EMAIL");
                String site = rs.getString("SITE");
                String tel = rs.getString("TEL");
                String fax = rs.getString("FAX");
                String rc = rs.getString("NUM_RC");
                String fisc = rs.getString("NUM_FISC");
                String art = rs.getString("NUM_ART");
                BigDecimal capital = rs.getBigDecimal("CAPITAL");
                comp = new Company(id, name, activ, adr, email, site, tel, fax, rc, fisc, art, capital);
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{id});
        }
        return comp;
    }

    @Override
    public Company getObjectByPK(EntityPK pK) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public boolean insert(Company entity) {
        int ind = 1;
        try {
            getInsertPreStatement().setString(ind++, entity.getCompany());
            getInsertPreStatement().setString(ind++, entity.getActivity());
            getInsertPreStatement().setString(ind++, entity.getAdresse());
            getInsertPreStatement().setString(ind++, entity.getEmail());
            getInsertPreStatement().setString(ind++, entity.getSite());
            getInsertPreStatement().setString(ind++, entity.getTel());
            getInsertPreStatement().setString(ind++, entity.getFax());
            getInsertPreStatement().setString(ind++, entity.getNumRc());
            getInsertPreStatement().setString(ind++, entity.getNumFisc());
            getInsertPreStatement().setString(ind++, entity.getNumArt());
            getInsertPreStatement().setBigDecimal(ind++, entity.getCapital());
            getInsertPreStatement().execute();
            return super.insert(entity);
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{entity});
            return false;
        }
    }

    @Override
    public boolean update(Company entity) {
        int ind = 1;
        try {
            getUpdatePreStatement().setString(ind++, entity.getCompany());
            getUpdatePreStatement().setString(ind++, entity.getActivity());
            getUpdatePreStatement().setString(ind++, entity.getAdresse());
            getUpdatePreStatement().setString(ind++, entity.getEmail());
            getUpdatePreStatement().setString(ind++, entity.getSite());
            getUpdatePreStatement().setString(ind++, entity.getTel());
            getUpdatePreStatement().setString(ind++, entity.getFax());
            getUpdatePreStatement().setString(ind++, entity.getNumRc());
            getUpdatePreStatement().setString(ind++, entity.getNumFisc());
            getUpdatePreStatement().setString(ind++, entity.getNumArt());
            getUpdatePreStatement().setBigDecimal(ind++, entity.getCapital());
            getUpdatePreStatement().setInt(ind++, entity.getId());
            getUpdatePreStatement().execute();
            return super.update(entity);
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{entity});
            return false;
        }
    }
}
