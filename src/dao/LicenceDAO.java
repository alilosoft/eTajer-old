/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dbTools.DBManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import tools.ExceptionReporting;

/**
 *
 * @author alilo
 */
public class LicenceDAO {

    private static LicenceDAO instance;
    private String activateQuery = "UPDATE LICENCE SET ACTIV_CODE = ?, REG_USER = ?";
    private String getRegestredUserQuery = "SELECT REG_USER FROM LICENCE";
    private String getActivationCodeQuery = "SELECT ACTIV_CODE FROM LICENCE";
    private String getTrialCountQuery = "SELECT TRIAL_COUNT FROM LICENCE";
    private String requestTrialQuery = "UPDATE LICENCE SET TRIAL_COUNT = TRIAL_COUNT + 1";
    private PreparedStatement activateStmnt;
    private PreparedStatement getActivationCode;
    private PreparedStatement getRegestredUser;
    private PreparedStatement getTrialCount;
    private PreparedStatement requestTrial;

    public LicenceDAO() {
        try {
            activateStmnt = DBManager.getInstance().getDefaultConnection().prepareStatement(activateQuery);
            getActivationCode = DBManager.getInstance().getDefaultConnection().prepareStatement(getActivationCodeQuery);
            getRegestredUser = DBManager.getInstance().getDefaultConnection().prepareStatement(getRegestredUserQuery);
            getTrialCount = DBManager.getInstance().getDefaultConnection().prepareStatement(getTrialCountQuery);
            requestTrial = DBManager.getInstance().getDefaultConnection().prepareStatement(requestTrialQuery);
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
        }
    }

    public static LicenceDAO getInstance() {
        if (instance == null) {
            instance = new LicenceDAO();
        }
        return instance;
    }
    
    public boolean activate(String code, String user){
        try {
            activateStmnt.setString(1, code);
            activateStmnt.setString(2, user);
            return activateStmnt.execute();
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
            return false;
        }
    }

    public String getActivationCode() {
        String code = null;
        try {
            ResultSet rs = getActivationCode.executeQuery();
            if (rs.next()) {
                code = rs.getString(1);
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
        }
        return code;
    }
    
    public int getTrialCount() {
        int nbr = 0;
        try {
            ResultSet rs = getTrialCount.executeQuery();
            if (rs.next()) {
                nbr = rs.getInt(1);
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
        }
        return nbr;
    }
    
    public String getRegestredUser(){
        String user = null;
        try {
            ResultSet rs = getRegestredUser.executeQuery();
            if (rs.next()) {
                user = rs.getString(1);
            }
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
        }
        return user;
    }
    
    public boolean isActivated(){
        return getActivationCode() != null;
    }

    public boolean requestTrial() {
        try {
            return requestTrial.executeUpdate() == 1;
        } catch (SQLException ex) {
            if(ex.getSQLState().equals("23513")){
                return false;
            }
            ExceptionReporting.showException(ex);
            return false;
        }
    }
}
