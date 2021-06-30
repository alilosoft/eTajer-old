/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package printing;

import dbTools.DBManager;
import entities.Vente;
import java.util.HashMap;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;

/**
 *
 * @author alilo
 */
public class VentePrinting {

    private static VentePrinting instance;

    private final Preferences userPrefs = Preferences.userNodeForPackage(VentePrinting.class);
    public static final String FACT_LANG = "FACT_Language";

    public static final String BL_LANG = "BLC_Language";
    public static final String BL_FORAMT = "BLC_Format";

    public static final String BS_LANG = "BS_Language";
    //
    public static final String BL_QTE_AS = "BL_Qte_As";
    public static final String BS_QTE_AS = "BS_Qte_As";

    public static final String NO_COLIS = "NoColis";
    public static final String BY_COLIS = "ByColis";
    //
    public static final String BL_COLIS_AS = "BL_Colis_As";
    public static final String BS_COLIS_AS = "BS_Colis_As";

    public static final String COLIS_AS_QTE = "ColisAsQte";
    public static final String COLIS_AS_DES = "ColisAsDes";
    //
    public static final String TICKET = "Ticket";
    public static final String BON_CMND = "BCC";
    public static final String BON_STK = "BStk";
    public static final String BON_LIVR = "BLC";
    public static final String FACTURE = "FactureCL";

    private final String piece;
    private String lang, format, printQteAs, printColisAs;

    public VentePrinting(String piece) {
        this.piece = piece;
    }

    public static VentePrinting getInstance(String piece) {
        if (instance == null || !instance.getPiece().equals(piece)) {
            instance = new VentePrinting(piece);
        }
        return instance;
    }

    public String getPiece() {
        return piece;
    }

    public static String getReportPath(Vente vnt, String piece) {
        String path = null;
        switch (piece) {
            case TICKET:
                path = "/printing/FR/Ticket/AN.jasper";
                break;
            case BON_CMND:
                break;
            case BON_STK:
                path = "/printing/AR/A6/ByColis/BStk/BS.jasper";
                break;
            case BON_LIVR:
                VentePrinting vp = getInstance(BON_LIVR);
                path = "/printing/" + vp.getLang() + "/" + vp.getFormat() + "/" + vp.getPrintQteAs() + "/" + vp.getPiece() + "/";
                if (vnt.getClient().isAnonyme()) {
                    path += "AN.jasper";
                } else {
                    if (vnt.getClient().isFellah()) {
                        path += "Fellah.jasper";
                    } else {
                        path += "CL.jasper";
                    }
                }
                break;
            case FACTURE:
                if (vnt.getClient().isAnonyme()) {
                    String mess = "Vous ne pouvez pas imprimer une facture pour un client anonyme!";
                    JOptionPane.showMessageDialog(null, mess, "Erreur!", JOptionPane.ERROR_MESSAGE);
                } else {
                    path = "/printing/FR/FactureCL/Facture.jasper";
                }
        }
        return path;
    }

    public void setLang(String lang) {
        this.lang = lang;
        switch (piece) {
            case BON_CMND:
                break;
            case BON_STK:
                userPrefs.put(BS_LANG, lang);
                break;
            case BON_LIVR:
                userPrefs.put(BL_LANG, lang);
                break;
            case FACTURE:
                userPrefs.put(FACT_LANG, lang);
        }

    }

    public String getLang() {
        switch (piece) {
            case BON_CMND:
                break;
            case BON_STK:
                lang = userPrefs.get(BS_LANG, PrintingTools.ARABIC);
                break;
            case BON_LIVR:
                lang = userPrefs.get(BL_LANG, PrintingTools.ARABIC);
                break;
            case FACTURE:
                lang = userPrefs.get(FACT_LANG, PrintingTools.FRENCH);
        }
        return lang;
    }

    public void setFormat(String format) {
        this.format = format;
        switch (piece) {
            case BON_CMND:
                break;
            case BON_STK:
                break;
            case BON_LIVR:
                userPrefs.put(BL_FORAMT, format);
                break;
            case FACTURE:
        }
    }

    public String getFormat() {
        switch (piece) {
            case BON_CMND:
                format = userPrefs.get(BL_FORAMT, PrintingTools.A6);
                break;
            case BON_STK:
                format = PrintingTools.A6;
                break;
            case BON_LIVR:
                format = userPrefs.get(BL_FORAMT, PrintingTools.A6);
                break;
            case FACTURE:
                format = PrintingTools.A4;
        }
        return format;
    }

    public void setPrintQteAs(String printQteAs) {
        this.printQteAs = printQteAs;
        switch (piece) {
            case BON_CMND:
                break;
            case BON_STK:
                userPrefs.put(BS_QTE_AS, printQteAs);
                break;
            case BON_LIVR:
                userPrefs.put(BL_QTE_AS, printQteAs);
                break;
            case FACTURE:
        }
    }

    public String getPrintQteAs() {
        switch (piece) {
            case BON_CMND:
                printQteAs = userPrefs.get(BL_QTE_AS, BY_COLIS);
                break;
            case BON_STK:
                printQteAs = userPrefs.get(BS_QTE_AS, BY_COLIS);
                break;
            case BON_LIVR:
                printQteAs = userPrefs.get(BL_QTE_AS, BY_COLIS);
                break;
            case FACTURE:
                printQteAs = NO_COLIS;
        }
        return printQteAs;
    }

    public void setPrintColisAs(String printColisAs) {
        this.printColisAs = printColisAs;
        switch (piece) {
            case BON_CMND:
                userPrefs.put(BL_COLIS_AS, printColisAs);
                break;
            case BON_STK:
                userPrefs.put(BS_COLIS_AS, printColisAs);
                break;
            case BON_LIVR:
                userPrefs.put(BL_COLIS_AS, printColisAs);
                break;
            case FACTURE:
                printQteAs = NO_COLIS;
        }
    }

    public String getPrintColisAs() {
        switch (piece) {
            case BON_CMND:
                printColisAs = userPrefs.get(BL_COLIS_AS, COLIS_AS_DES);
                break;
            case BON_STK:
                printColisAs = userPrefs.get(BS_COLIS_AS, COLIS_AS_DES);
                break;
            case BON_LIVR:
                printColisAs = userPrefs.get(BL_COLIS_AS, COLIS_AS_DES);
                break;
            case FACTURE:
                printQteAs = NO_COLIS;
        }
        return printColisAs;
    }

    public static void print(Vente vente, String piece) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("ID_VNT", vente.getId());
        PrintingTools.printReport(getReportPath(vente, piece), DBManager.getInstance().getDefaultConnection(), params);
    }

    public static void preview(Vente vente, String piece) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("ID_VNT", vente.getId());
        PrintingTools.previewReport(getReportPath(vente, piece), DBManager.getInstance().getDefaultConnection(), params);
    }
}
