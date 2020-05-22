/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package appLogic;

import javax.swing.JOptionPane;
import dao.LicenceDAO;

/**
 *
 * @author alilo
 */
public class Licence {

    public static boolean verify() {
        if (LicenceDAO.getInstance().isActivated()) {
            if (isLicenced()) {
                return true;
            } else {
                String mess = "Vous ête pas autoriser à utiliser ce logiciel.";
                JOptionPane.showMessageDialog(null, mess, "Licence incorrecte", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } else {
            if (LicenceDAO.getInstance().requestTrial()) {
                return true;
            } else {
                String mess = "Votre version d'évaluation vient d'être expirer,\nContacter le vendure de l'application pour plus d'informations.";
                JOptionPane.showMessageDialog(null, mess, "Licence expirée", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
    }
    
    public static String getLicensedUser(){
        return LicenceDAO.getInstance().getRegestredUser();
    }

    public static String getActivationKey() {
        String key = "alilo";
        //Cipher.getInstance("").;
        return key;
    }

    public static boolean isLicenced() {
        return LicenceDAO.getInstance().getActivationCode() == null;
    }

    public static String getLicence() {
        String lic;
        if (LicenceDAO.getInstance().isActivated()) {
            if (isLicenced()) {
                lic = "Autorisé à: " + getLicensedUser();
            } else {
                lic = "Licence incorrect!";
            }
        } else {
            lic = "Version d'essai (Reste:" + (100 - LicenceDAO.getInstance().getTrialCount()) + ")";
        }
        return lic;
    }
    
    public static boolean requestTrial(){
        return LicenceDAO.getInstance().requestTrial();
    }

    public static void main(String[] args) {
        Licence.verify();
    }
}
