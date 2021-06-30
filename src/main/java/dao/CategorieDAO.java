package dao;

import static dao.LigneVenteDAO.SQL_FILES_PATH;
import dbTools.DBManager;
import entities.Categorie;
import entities.EntityPK;
import entities.Famille;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
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
public class CategorieDAO extends TableDAO<Categorie, EntityPK> {
    public static final String SQL_FILES_PATH = "/sql/categorie/";
    private static CategorieDAO instance = null;
    private final String getByFamQry;
    private PreparedStatement getByFamSt;

    public CategorieDAO() {
        super(SQL_FILES_PATH);
        getByFamQry = QueryReader.getQueries(SQL_FILES_PATH + "GetByFamille.sql", false)[0];
    }
    
    @Override
    public boolean prepare(Connection c) {
        boolean prep = super.prepare(c);
        try {
            getByFamSt = getConnection().prepareStatement(getByFamQry, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            return prep;
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
            return false;
        }
    }

    public static CategorieDAO getInstance() {
        if (instance == null) {
            instance = new CategorieDAO();
            instance.prepare(DBManager.getInstance().getDefaultConnection());
        }
        return instance;
    }
    
    public static CategorieDAO getInstance(Connection c) {
        if (instance == null) {
            instance = new CategorieDAO();
            if (c != null) {
                instance.prepare(c);
            }else{
                instance.prepare(DBManager.getInstance().getDefaultConnection());
            }
        }
        return instance;
    }

    @Override
    public Categorie getObjectByID(int id) {
        Categorie categorie = null;
        ResultSet rs = getWhereID(id);
        try {
            if (rs.next()) {
                String des = rs.getString("DES");
                int idFam = rs.getInt("ID_FAM");
                Famille famille = FamilleDAO.getInstance().getObjectByID(idFam);
                categorie = new Categorie(id, des, famille);
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
        }
        return categorie;
    }

    @Override
    public Categorie getObjectByPK(EntityPK pK) {
        return  getObjectByID(pK.getId());
    }
    
    public ResultSet getByFamille(Famille fam) {
        ResultSet rs = null;
        try {
            getByFamSt.setInt(1, fam.getId());
            rs = getByFamSt.executeQuery();
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{fam});
        }
        return rs;
    }

    @Override
    public boolean insert(Categorie entity) {
        int ind = 1;
        try {
            getInsertPreStatement().setString(ind++, entity.getDes());
            if(entity.getFamille() == null || entity.getFamille().getId() == 0){
                getInsertPreStatement().setNull(ind++, Types.INTEGER);
            }else{
                getInsertPreStatement().setInt(ind++, entity.getFamille().getId());
            }
            getInsertPreStatement().execute();
            return super.insert(entity);
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{entity});
            return false;
        }
    }

    @Override
    public boolean update(Categorie entity) {
        int ind = 1;
        try {
            getUpdatePreStatement().setString(ind++, entity.getDes());
            if(entity.getFamille() == null || entity.getFamille().getId() == 0){
                getUpdatePreStatement().setNull(ind++, Types.INTEGER);
            }else{
                getUpdatePreStatement().setInt(ind++, entity.getFamille().getId());
            }
            getUpdatePreStatement().setInt(ind++, entity.getId());
            getUpdatePreStatement().execute();
            return super.update(entity);
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{entity});
            return false;
        }
    }
}
