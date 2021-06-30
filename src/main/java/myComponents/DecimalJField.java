/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myComponents;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;

/**
 *
 * @author alilo
 */
public class DecimalJField extends MyJField {
    private String pattern = "0.00";
    private DecimalFormat df = new DecimalFormat(pattern);
    public static final String INTEGER_REG_EXP = "^[+-]?\\d+$";
    public static final String DECIMAL_REG_EXP = "^[+-]?\\d+(\\.?\\d+)?$";
    public static final String POSITIF_DECIMAL_REG_EXP = "^(\\d+(\\.?\\d+)?)?$";

    public DecimalJField() {
        super();
        addKeyListener(new KeyAdapter() {

            @Override
            public void keyTyped(KeyEvent e) {

                char key = e.getKeyChar();

                if (!(Character.isDigit(key) || key == KeyEvent.VK_PERIOD || key == KeyEvent.VK_MINUS)) {
                    e.consume();
                    Toolkit.getDefaultToolkit().beep();
                }

                if (key == KeyEvent.VK_MINUS) {
                    if (getText().matches(POSITIF_DECIMAL_REG_EXP)) {
                        setText("-" + getText());
                    }
                    e.consume();
                    Toolkit.getDefaultToolkit().beep();
                }

                if (key == KeyEvent.VK_PERIOD) {
                    if (!getText().matches(INTEGER_REG_EXP)) {
                        e.consume();
                        Toolkit.getDefaultToolkit().beep();
                    }
                }
            }
        });

        addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                try {// Auto Turn-ON Num_Lock Key 
                    Toolkit.getDefaultToolkit().setLockingKeyState(KeyEvent.VK_NUM_LOCK, true);
                } catch (Exception ex) {
                }
            }
        });
    }

    
    public void setValue(double val) {
        setText(df.format(val));
    }

    public double getValue() {
        if (getText().trim().length() == 0 || getText().matches("[+-.]")) {
            return 0;
        }
        return Double.valueOf(getText().trim());
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
        df.applyPattern(pattern);
    }

    @Override
    public Container getParent() {
        return super.getParent(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    public static void main(String[] args) {
        System.out.println("02565".matches(INTEGER_REG_EXP));
        MyJFrame frame = new MyJFrame();
        frame.add(new DecimalJField(), BorderLayout.NORTH);
        frame.add(new IntegerJField(), BorderLayout.SOUTH);
        //frame.pack();
        frame.setVisible(true);
    }
}
