/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myComponents;

import appLogic.CurrencyConverter;
import java.math.BigDecimal;

/**
 *
 * @author alilo
 */
public class CurrencyField extends DecimalJField {

    public CurrencyField() {
        setText("0.00");
    }

    @Override
    public final void setText(String t) {
        if (!t.matches("[+-.]") && CurrencyConverter.getCurrencyUnit().equals(CurrencyConverter.CENTIMES)) {
            super.setText(CurrencyConverter.dinares2Centimes(t).toPlainString());
        } else {
            super.setText(t);
        }

    }

    @Override
    public String getText() {
        String val = super.getText();
        if (!val.matches("[+-.]") && CurrencyConverter.getCurrencyUnit().equals(CurrencyConverter.CENTIMES)) {
            return CurrencyConverter.centimes2Dinares(val).toPlainString();
        } else {
            return val;
        }
    }

    public void setBigDecimalValue(BigDecimal val) {
        setText(val.toPlainString());
    }

    public BigDecimal getBigDecimalValue() {
        if (getText().trim().length() == 0 || getText().matches("[+-.]")) {
            return new BigDecimal(0);
        } else {
            return new BigDecimal(getText().trim());
        }
    }
}
