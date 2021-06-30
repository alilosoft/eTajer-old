/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myComponents;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;

/**
 *
 * @author alilo
 */
public class IntegerJField extends MyJField {

    public static final String INTEGER_REG_EXP = "^[+-]?\\d+$";
    public static final String POSITIF_INT_REG_EXP = "^\\d*$";
    private int value, oldVal = 0;

    public IntegerJField() {
        super();
        setText("0");
        setAutoNumLock(true);
        addKeyListener(new KeyAdapter() {

            @Override
            public void keyTyped(KeyEvent e) {
                char key = e.getKeyChar();
                if (Character.isDigit(key) || key == KeyEvent.VK_BACK_SPACE || key == KeyEvent.VK_MINUS) {
                    e.consume();
                    if (Character.isDigit(key)) {
                        int caretPos = getCaretPosition();
                        int selLength = getSelectionEnd() - getSelectionStart();
                        if (selLength != 0) {
                            caretPos = getSelectionStart();
                        }
                        try {
                            getDocument().remove(getSelectionStart(), selLength);
                            getDocument().insertString(caretPos, String.valueOf(key), null);
                            caretPos++;
                            setValue(Integer.valueOf(getText()));
                            setCaretPosition(caretPos);
                        } catch (BadLocationException | NumberFormatException ex) {
                            JOptionPane.showMessageDialog(null, "Incorrect int value!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else if (key == KeyEvent.VK_MINUS) {
                        if (getValue() == 0) {
                            setText("-");
                        } else {
                            int caretPos = getCaretPosition();
                            if (getValue() < 0) {
                                caretPos--;
                            } else {
                                caretPos++;
                            }
                            setValue(-getValue());
                            setCaretPosition(caretPos);
                        }
                    } else {// VK_BACK_SPACE
                        if (getText().length() == 0) {
                            setValue(0);
                            selectAll();
                        } else if (getText().equals("-")) {
                            setValue(0);
                            setText("-");
                        } else {
                            int caretPos = getCaretPosition();
                            try {
                                setValue(Integer.valueOf(getText()));
                                setCaretPosition(caretPos);
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(null, "Incorrect int value!", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                } else {
                    e.consume();
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });
    }

    public void setValue(int val) {
        this.oldVal = this.value;
        this.value = val;
        setText(String.valueOf(val));
        fireValueChanged(oldVal, value);
    }

    public int getValue() {
        if (getText().length() == 0 || getText().equals("-")) {
            value = 0;
        } else {
            try {
                value = Integer.valueOf(getText());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Incorrect int value!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return value;
    }

    public static void main(String[] args) {
        System.out.println("123".matches(POSITIF_INT_REG_EXP));
        MyJFrame frame = new MyJFrame();
        frame.setSize(200, 150);
        frame.add(new IntegerJField(), BorderLayout.NORTH);
        frame.setVisible(true);
    }
}
