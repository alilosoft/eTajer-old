/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import entities.EntityClass;
import entities.EntityPK;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import sql.QueryReader;
import tools.ExceptionReporting;
import tools.MessageReporting;

/**
 *
 * @author alilo
 * @param <Entity>
 * @param <EntPK>
 */
public abstract class TableDAO<Entity extends EntityClass, EntPK extends EntityPK> {
    public final String sqlFilesPath;
    
    public static final String GET_ALL = "GetAll.sql";
    public static final String GET_WH_PK = "GetWhPK.sql";
    public static final String INSERT = "Insert.sql";
    public static final String UPDATE = "Update.sql";
    public static final String DELETE = "Delete.sql";

    protected List<Entity> objectsListe = Collections.synchronizedList(new ArrayList<Entity>());
    protected Connection connection;
    
    protected String getAllQuery;
    protected PreparedStatement getAllPrepStmnt;
    
    protected String getByPKQuery;
    protected PreparedStatement getByPKPrepStmnt;
    
    protected String insertQuery;
    protected PreparedStatement insertPrepStmnt;
    
    protected String updateWhereIDQuery;
    protected PreparedStatement updatePrepStmnt;
    
    protected String deleteWhereIDQuery;
    protected PreparedStatement deletePrepStmnt;
    
    protected int generatedID;

    public TableDAO(String sqlPath) {
        this.sqlFilesPath = sqlPath;
        MessageReporting.logOnly(Level.INFO, "New instance created from: "+ getClass().getName()+", SQLFilesPath:"+ sqlFilesPath);
        getAllQuery = QueryReader.getQueries(sqlFilesPath + GET_ALL, false)[0];
        getByPKQuery = QueryReader.getQueries(sqlFilesPath + GET_WH_PK, false)[0];
        insertQuery = QueryReader.getQueries(sqlFilesPath + INSERT, false)[0];
        updateWhereIDQuery = QueryReader.getQueries(sqlFilesPath + UPDATE, false)[0];
        deleteWhereIDQuery = QueryReader.getQueries(sqlFilesPath + DELETE, false)[0];
    }

    /**
     * Prepare this TableDAO by providing the connection used to
     * compile/prpare/run SQL statements.
     *
     * @param c
     * @return true if the compilation of all queries success.
     */
    synchronized public boolean prepare(Connection c) {
        this.connection = c;
        try {
            getAllPrepStmnt = connection.prepareStatement(getAllQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            getByPKPrepStmnt = connection.prepareStatement(getByPKQuery, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            insertPrepStmnt = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            updatePrepStmnt = connection.prepareStatement(updateWhereIDQuery);
            deletePrepStmnt = connection.prepareStatement(deleteWhereIDQuery);
            MessageReporting.logOnly(Level.CONFIG, "Preparing " + getClass() + "...OK!");
            return true;
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
            return false;
        }
    }

    public ResultSet getAll() {
        ResultSet rs = null;
        try {
            rs = getAllPrepStmnt.executeQuery();
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
        }
        return rs;
    }

    public ResultSet getWhereID(int id) {
        ResultSet rs = null;
        try {
            getByPKPrepStmnt.setInt(1, id);
            rs = getByPKPrepStmnt.executeQuery();
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
        }
        return rs;
    }

    public abstract Entity getObjectByID(int id);
    public abstract Entity getObjectByPK(EntPK pK);
    
    public int getGeneratedID() {
        if (generatedID > 0) {
            return generatedID;
        } else {
            try {
                ResultSet rs = insertPrepStmnt.getGeneratedKeys();

                if (rs != null && rs.next()) {
                    generatedID = rs.getInt(1);
                }
            } catch (SQLException ex) {
                ExceptionReporting.showException(ex);
            }
            return generatedID;
        }
    }

    public void setGeneratedID(int generatedKey) {
        this.generatedID = generatedKey;
    }

    /**
     * Execute the insert statement to insert the given <i>entity</i> to the
     * Database. This method retrivs the generated key explicitely and put it in
     * the <i>'id'</i> field of the <i>entity</i> object.
     *
     * @param entity
     * @return true if the operation committed correctly
     */
    public boolean insert(Entity entity) {
        try {
            ResultSet rs = insertPrepStmnt.getGeneratedKeys();
            if (rs.next()) {
                generatedID = rs.getInt(1);
                entity.setId(generatedID);
            }
            int updateCount = insertPrepStmnt.getUpdateCount();
            return updateCount != 0;
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{entity});
            return false;
        } finally {
            MessageReporting.showMessage(Level.FINE, getClass(), "insert()", "Inserting...", new Object[]{entity});
        }
    }

    public boolean update(Entity entity) {
        try {
            return updatePrepStmnt.getUpdateCount() != 0;
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{entity});
            return false;
        } finally {
            MessageReporting.showMessage(Level.FINE, getClass(), "update()", "Updating...", new Object[]{entity});
        }
    }

    public boolean delete(int id) {
        try {
            deletePrepStmnt.setInt(1, id);
            //deletePrepStmnt.execute();
            return deletePrepStmnt.executeUpdate() != 0;
        } catch (SQLException ex) {
            if (ex.getSQLState().equals("23503")) {
                String mess = "Supression du: "+ getObjectByID(id) + " échoué!\n"
                        + "Cet élément est lié à un autre élément, vous ne pouvez pas le supprimer.\n"
                        + "Pour plus d'infos voir le message d'erreur ci-dessous:"
                        + "\n\nError: " + ex.getSQLState() + "\n" + ex.getLocalizedMessage();
                MessageReporting.showMessage(Level.SEVERE, getClass(), "delete()", mess, new Object[]{id});
            } else {
                ExceptionReporting.showException(ex, new Object[]{id});
            }
            return false;
        } finally {
            MessageReporting.showMessage(Level.FINE, getClass(), "delete()", "Deleting...", new Object[]{id});
        }
    }

    /**
     * Set the commit mode of this tableDAO connection, and recomile/prepare all
     * statements.
     *
     * @param autoCommit true/false.
     */
    public void setAutoCommit(boolean autoCommit) {
        try {
            connection.setAutoCommit(autoCommit);
            prepare(connection);
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex, new Object[]{autoCommit});
        }
    }

    /**
     * If the auto-commit is disabled, call this method will commit the current
     * transaction.
     *
     * @return
     */
    public boolean commit() {
        try {
            MessageReporting.logOnly(Level.INFO, "commiting..." + getClass().getSimpleName());
            getConnection().commit();
            return true;
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
            return false;
        }
    }

    public boolean rollBack() {
        try {
            MessageReporting.logOnly(Level.INFO, "rolling-back..." + getClass().getSimpleName());
            getConnection().rollback();
            return true;
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
            return false;
        }
    }

    //Setters
    public void setConnection(Connection connection) {
        this.connection = connection;
        prepare(connection);
    }

    public void setGetAllPreStatement(PreparedStatement getAllPreStatement) {
        this.getAllPrepStmnt = getAllPreStatement;
    }

    public void setGetWhereIDPreStatement(PreparedStatement getWhereIDPreStatement) {
        this.getByPKPrepStmnt = getWhereIDPreStatement;
    }

    public void setInsertPreStatement(PreparedStatement insertPreStatement) {
        this.insertPrepStmnt = insertPreStatement;
    }

    public void setUpdatePreStatement(PreparedStatement updatePreStatement) {
        this.updatePrepStmnt = updatePreStatement;
    }

    public void setDeletePreStatement(PreparedStatement deletePreStatement) {
        this.deletePrepStmnt = deletePreStatement;
    }

    //Getters
    public Connection getConnection() {
        return connection;
    }

    public PreparedStatement getGetAllPreStatement() {
        return getAllPrepStmnt;
    }

    public PreparedStatement getGetWhereIDPrepStmnt() {
        return getByPKPrepStmnt;
    }

    public PreparedStatement getInsertPreStatement() {
        return insertPrepStmnt;
    }

    public PreparedStatement getUpdatePreStatement() {
        return updatePrepStmnt;
    }

    public PreparedStatement getDeletePreStatement() {
        return deletePrepStmnt;
    }
}
