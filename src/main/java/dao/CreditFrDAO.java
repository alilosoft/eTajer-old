package dao;

import dbTools.DBManager;
import entities.CreditFr;
import entities.EntityPK;
import java.math.BigDecimal;
import java.sql.*;
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
public class CreditFrDAO extends TableDAO<CreditFr, EntityPK> {

    public static final String SQL_FILES_PATH = "/sql/creditFr/";
    private static CreditFrDAO instance = null;

    public CreditFrDAO() {
        super(SQL_FILES_PATH);
    }

    public static CreditFrDAO getInstance() {
        if (instance == null) {
            instance = new CreditFrDAO();
            instance.prepare(DBManager.getInstance().getDefaultConnection());
        }
        return instance;
    }

    public static CreditFrDAO getInstance(Connection c) {
        if (instance == null) {
            instance = new CreditFrDAO();
            if (c != null) {
                instance.prepare(c);
            } else {
                instance.prepare(DBManager.getInstance().getDefaultConnection());
            }
        }
        return instance;
    }

    @Override
    public CreditFr getObjectByID(int id) {
        CreditFr credit = null;
        ResultSet rs = getWhereID(id);
        try {
            if (rs.next()) {
                int idFr = rs.getInt("ID_FR");
                int idAch = rs.getInt("ID_ACH");
                Date date = rs.getDate("DATE");
                Time heure = rs.getTime("HEURE");
                BigDecimal montant = rs.getBigDecimal("MONTANT");
                String comment = rs.getString("COMMENT");
                boolean init = rs.getBoolean("INITIAL");
                credit = new CreditFr(id, date, heure, montant, comment, init);
                credit.setFournisseur(FournissDAO.getInstance().getObjectByID(idFr));
                if (idAch != 0) {
                    credit.setAchat(AchatDAO.getInstance().getObjectByID(idAch));
                }
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{id});
        }
        return credit;
    }

    @Override
    public CreditFr getObjectByPK(EntityPK pK) {
        return getObjectByID(pK.getId());
    }

    @Override
    public boolean insert(CreditFr entity) {
        int ind = 1;
        try {
            getInsertPreStatement().setInt(ind++, entity.getFournisseur().getId());
            if (entity.getAchat()== null || entity.getAchat().getId() == 0) {
                getInsertPreStatement().setNull(ind++, Types.INTEGER);
            } else {
                getInsertPreStatement().setInt(ind++, entity.getAchat().getId());
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
    public boolean update(CreditFr entity) {
        int ind = 1;
        try {
            getUpdatePreStatement().setInt(ind++, entity.getFournisseur().getId());
            if (entity.getAchat()== null || entity.getAchat().getId() == 0) {
                getUpdatePreStatement().setNull(ind++, Types.INTEGER);
            } else {
                getUpdatePreStatement().setInt(ind++, entity.getAchat().getId());
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
}
