/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package appLogic;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.logging.Level;
import java.util.prefs.Preferences;
import tools.ExceptionReporting;
import tools.MessageReporting;

/**
 *
 * @author alilo
 */
public class CurrencyConverter {

    public static final String CENTIMES = "Cent";
    public static final String DINARES = "DA";
    private static final String CURRENCY_UNIT = "Pref_Currency_Unite";
    private static final BigDecimal ONE_HUNDRED = new BigDecimal(100);
    private static final Preferences userPrefs = Preferences.userNodeForPackage(CurrencyConverter.class);

    /**
     * Set the default currency unite.
     *
     * @param currencyUnit one of: CurrencyConverter.CENTIMES,
     * CurrencyConverter.DINARES.
     */
    public static void setCurrencyUnit(String currencyUnit) {
        userPrefs.put(CURRENCY_UNIT, currencyUnit);
    }

    /**
     * Get the default currency unite.
     *
     * @return
     */
    public static String getCurrencyUnit() {
        return userPrefs.get(CURRENCY_UNIT, CENTIMES);
    }

    /**
     * Convert the given value (as string) from centimes to dinares.
     *
     * @param value
     * @return BigDecimal representing the value in Dinares.
     */
    public static BigDecimal centimes2Dinares(String value) {
        if(value.length() == 0){
            value = "0";
        }
        BigDecimal val = new BigDecimal(0);
        try {
            val = new BigDecimal(value);
        } catch (Exception e) {
            String mess = "La valeur entrée: " + value + ", n'est pas un chifre!";
            MessageReporting.showMessage(Level.SEVERE, CurrencyConverter.class, "dinares2Centimes(String value)", mess);
            ExceptionReporting.showException(e);
        }
        //new BigDecimal(doubleVal).setScale(2, RoundingMode.DOWN);
        return val.divide(ONE_HUNDRED).setScale(2);
    }

    public static BigDecimal dinares2Centimes(String value) {
        if(value.length() == 0){
            value = "0";
        }
        BigDecimal val = new BigDecimal(0);
        try {
            val = new BigDecimal(value);
        } catch (Exception e) {
            String mess = "La valeur entrée: " + value + ", n'est pas un chifre!";
            MessageReporting.showMessage(Level.SEVERE, CurrencyConverter.class, "dinares2Centimes(String value)", mess);
            ExceptionReporting.showException(e);
        }
        //new BigDecimal(doubleVal).setScale(0, RoundingMode.DOWN);
        return val.multiply(ONE_HUNDRED).setScale(0);
    }

    public static void main(String args[]) {
        System.out.println(CurrencyConverter.centimes2Dinares("1000"));
        System.out.println(CurrencyConverter.dinares2Centimes("235.00"));
        CurrencyConverter.setCurrencyUnit(CENTIMES);
    }
}
