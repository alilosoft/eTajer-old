/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package appLogic;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.prefs.Preferences;
import tools.ExceptionReporting;

/**
 *
 * @author alilo
 */
public class PriceCalculator {

    private static final String PRICE_MODE = "Price_Mode";
    public static final String HT = "Hors-Taxes";
    public static final String TTC = "Toutes-Taxes-Comprises";

    private static final String MARGE_MODE = "Marge_Mode";
    public static final String PERCENT = "%";
    public static final String DINARS = "DA";

    private static final Preferences userPrefs = Preferences.userNodeForPackage(PriceCalculator.class);

    /**
     * Set the default currency unite.
     *
     * @param priceMode one of: PriceTools.HT, PriceTools.TTC.
     */
    public static void setPriceMode(String priceMode) {
        userPrefs.put(PRICE_MODE, priceMode);
    }

    /**
     * Get the default currency unite.
     *
     * @return
     */
    public static String getPriceMode() {
        return userPrefs.get(PRICE_MODE, HT);
    }

    /**
     * @param priceHT
     * @param tva
     * @return .
     */
    public static BigDecimal HT2TTC(BigDecimal priceHT, int tva) {
        double priceDoubleVal = 0;
        try {
            priceDoubleVal = priceHT.doubleValue();
            priceDoubleVal += priceDoubleVal * tva / 100;
        } catch (Exception e) {
            ExceptionReporting.showException(e);
        }
        return new BigDecimal(priceDoubleVal).setScale(2, RoundingMode.CEILING);
    }

    public static BigDecimal TTC2HT(BigDecimal priceTTC, int tva) {
        double priceDoubleVal = 0;
        try {
            priceDoubleVal = priceTTC.doubleValue();
            priceDoubleVal = priceDoubleVal * 100 / (100 + tva);
        } catch (Exception e) {
            ExceptionReporting.showException(e);
        }
        return new BigDecimal(priceDoubleVal).setScale(2, RoundingMode.CEILING);
    }

    /**
     * Set the default 'Marge' mode.
     *
     * @param margeMode one of: PriceTools.HT, PriceTools.TTC.
     */
    public static void setMargeMode(String margeMode) {
        userPrefs.put(MARGE_MODE, margeMode);
    }

    /**
     * Get the default 'Marge' mode.
     *
     * @return
     */
    public static String getMargeMode() {
        return userPrefs.get(MARGE_MODE, DINARS);
    }

    public static BigDecimal getPrixVnt(BigDecimal prixAch, double margeVal, String margeType) {
        double prixVntDbl = 0; 
        double prixAchDbl = prixAch.doubleValue();
        switch (margeType) {
            case DINARS:
                prixVntDbl = prixAchDbl + margeVal;
                break;
            case PERCENT:
                prixVntDbl = prixAchDbl + margeVal * prixAchDbl / 100;
                break;
        }
        return new BigDecimal(prixVntDbl).setScale(2, RoundingMode.CEILING);
    }

    public static double getMargeVal(BigDecimal prixAch, BigDecimal prixVnt, String margeType) {
        double prixVntDbl = prixVnt.doubleValue(), prixAchDbl = prixAch.doubleValue(), marge = 0;
        switch (margeType) {
            case DINARS:
                marge = prixVntDbl - prixAchDbl;
                break;
            case PERCENT:
                if (prixAchDbl == 0) {
                    if (prixVntDbl == 0) {
                        marge = 0;
                    } else {
                        marge = 100;
                    }
                } else {
                    marge = (prixVntDbl - prixAchDbl) * 100 / prixAchDbl;
                }
                break;
        }
        return marge;
    }

    public static void main(String args[]) {
        System.out.println(PriceCalculator.HT2TTC(new BigDecimal(234), 17));
        System.out.println(PriceCalculator.TTC2HT(new BigDecimal(273.78), 17));
        PriceCalculator.setPriceMode(PriceCalculator.HT);
        System.out.println("pv: " + getPrixVnt(new BigDecimal(250.50), 50, DINARS));
        System.out.println("pv: " + getMargeVal(new BigDecimal(250.50), new BigDecimal(300.50), DINARS));

        System.out.println("pv: " + getPrixVnt(new BigDecimal(200.50), 10.5, PERCENT));
        System.out.println("pv: " + getMargeVal(new BigDecimal(200.50), new BigDecimal(221.56), PERCENT));
    }
}
