/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myComponents;

import appLogic.CurrencyConverter;
import javax.swing.JLabel;

/**
 *
 * @author alilo
 */
public class CurrencyUniteLabel extends JLabel{

    public CurrencyUniteLabel() {
        super(CurrencyConverter.getCurrencyUnit());
        setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 1));
        setFont(new java.awt.Font("Tahoma", 1, 13));
    }

    @Override
    public String getText() {
        return CurrencyConverter.getCurrencyUnit();
    }
}
