/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myComponents;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.im.InputContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.plaf.FontUIResource;
import myComponents.myLiteners.ValueChangeListener;
import tools.ExceptionReporting;
import tools.LangTools;
import tools.MissFormatedLocal;

/**
 *
 * @author alilo
 */
public class MyJField extends JTextField {

    

    private final Preferences prefs;
    private String prefsLangKey;
    private String defLocaleStr;
    private Locale defLocale;
    private Font normalFont = new FontUIResource("tahoma", FontUIResource.BOLD, 14);
    private Color flashBG = new Color(153, 255, 153);
    private Color flashFG = Color.BLACK;
    private Color origBG, origFG, specialBG, specialFG;
    private String description;
    private Font descFont = new FontUIResource("tahoma", FontUIResource.BOLD, 14);
    private Color descFG = new Color(204, 204, 255);
    private boolean autoNumLock, autoVerMaj;

    private final List<ValueChangeListener> changeListeners = new ArrayList<>();

    public MyJField() {
        super();
        prefs = Preferences.userNodeForPackage(this.getClass());
        setFont(normalFont);
        
        origBG = getBackground();
        origFG = getForeground();
        //setPreferredSize(new Dimension(10, 27));
        addFocusListner();
        setSelectionColor(Color.BLUE);
        setSelectedTextColor(Color.WHITE);

        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                char c = e.getKeyChar();
                if (Character.isDigit(c) || Character.isLetter(c)) {
                    if (getText().equals(description)) {
                        setText("");
                        setCaretPosition(0);
                        setFont(normalFont);
                        if (specialFG == null) {
                            setForeground(flashFG);
                        }
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (getText().isEmpty()) {
                    if (description != null && !description.isEmpty()) {
                        setDescription(description);
                    }
                }
            }
        });
    }

    private void addFocusListner() {
        addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                if (isEditable()) {
                    selectAll(); //problem for filter field;
                }
                //***
                if (getText().isEmpty()) {
                    if (description != null && !description.isEmpty()) {
                        setDescription(description);
                    }
                } else {
                    if (description != null && getText().equals(description)) {
                        setCaretPosition(0);
                    }
                }
                //***
                if (defLocaleStr == null) { // if any default locale is set to this comp then set the system keybolard layout as default
                    defLocaleStr = InputContext.getInstance().getLocale().toString();
                }
                try {
                    if (prefsLangKey != null) {
                        defLocale = LangTools.parseStr2Locale(prefs.get(prefsLangKey, defLocaleStr));
                    } else {
                        defLocale = InputContext.getInstance().getLocale();
                    }
                } catch (MissFormatedLocal ex) {
                    ExceptionReporting.showException(ex);
                }

                if (defLocale != null) {
                    try {
                        getInputContext().selectInputMethod(defLocale);
                    } catch (Exception ex) {
                        ExceptionReporting.showException(ex);
                    }
                }

                MyJField.this.setBackground(flashBG);
                if (description != null && getText().equals(description)) {
                    MyJField.this.setForeground(descFG);
                } else {
                    MyJField.this.setForeground(flashFG);
                }
                //flashTimer.start();
                if (isAutoNumLock()) {
                    try {// Auto Turn-ON Num_Lock Key 
                        Toolkit.getDefaultToolkit().setLockingKeyState(KeyEvent.VK_NUM_LOCK, true);
                    } catch (Exception ex) {
                    }
                }

                if (isAutoCapsLock()) {
                    try {// Auto Turn-ON Num_Lock Key 
                        Toolkit.getDefaultToolkit().setLockingKeyState(KeyEvent.VK_CAPS_LOCK, true);
                    } catch (Exception ex) {
                    }
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                //flashTimer.stop();
                if (prefsLangKey != null) {
                    prefs.put(prefsLangKey, InputContext.getInstance().getLocale().toString());
                }

                if (getText().isEmpty()) {
                    if (description != null && !description.isEmpty()) {
                        setDescription(description);
                    }
                }

                if (specialBG == null) {
                    MyJField.this.setBackground(origBG);
                } else {
                    MyJField.this.setBackground(specialBG);
                }

                if (specialFG == null) {
                    if (description != null && getText().equals(description)) {
                        MyJField.this.setForeground(descFG);
                    } else {
                        MyJField.this.setForeground(origFG);
                    }
                } else {
                    MyJField.this.setForeground(specialFG);
                }
            }
        });
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        if (bg != flashBG && bg != specialBG) {
            origBG = bg;
        }
        if (bg == origBG) {
            specialBG = null;
        }
    }

    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
        if (fg != flashFG && fg != descFG && fg != specialFG) {
            origFG = fg;
        }
        if (fg == origFG) {
            specialFG = null;
        }
    }

    public void setFlashBG(Color flashBG) {
        this.flashBG = flashBG;
    }

    public Color getFlashBG() {
        return flashBG;
    }

    public void setFlashFG(Color flashFG) {
        this.flashFG = flashFG;
    }

    public Color getFlashFG() {
        return flashFG;
    }

    public void setSpecialBG(Color specialBG) {
        this.specialBG = specialBG;
    }

    public void setSpecialFG(Color specialFG) {
        this.specialFG = specialFG;
    }

    public void setDescription(String description) {
        this.description = description;
        setFont(descFont);
        if (specialFG == null) {
            setForeground(descFG);
        }
        setText(description);
        setCaretPosition(0);
    }

    public String getDescription() {
        return description;
    }

    public void setDescFG(Color descFG) {
        this.descFG = descFG;
        if (getText().equals(description)) {
            setForeground(descFG);
        }
    }

    public Color getDescFG() {
        return descFG;
    }

    @Override
    public final void setFont(Font f) {
        super.setFont(f);
        if (f != descFont) {
            normalFont = f;
        }
    }

    public void setDescFont(Font descFont) {
        this.descFont = descFont;
        if (getText().equals(description)) {
            setFont(descFont);
            updateUI();
        }
    }

    public Font getDescFont() {
        return descFont;
    }

    public void setAutoCapsLock(boolean autoVerMaj) {
        this.autoVerMaj = autoVerMaj;
    }

    public boolean isAutoCapsLock() {
        return autoVerMaj;
    }

    public void setAutoNumLock(boolean autoVerNum) {
        this.autoNumLock = autoVerNum;
    }

    public boolean isAutoNumLock() {
        return autoNumLock;
    }

    private boolean origColors = true;
    private final Action flashAct = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (origColors) {
                MyJField.this.setBackground(flashBG);
                origColors = false;
            } else {
                if (specialBG == null) {
                    MyJField.this.setBackground(origBG);
                } else {
                    MyJField.this.setBackground(specialBG);
                }

                if (specialFG == null) {
                    if (description != null && getText().equals(description)) {
                        MyJField.this.setForeground(descFG);
                    } else {
                        MyJField.this.setForeground(origFG);
                    }
                } else {
                    MyJField.this.setForeground(specialFG);
                }
                origColors = true;
            }
        }
    };
    private final Timer flashTimer = new Timer(500, flashAct);

    public void setPrefsLangKey(String prefsLangKey) {
        if (prefsLangKey == null && this.prefsLangKey != null) {
            prefs.remove(this.prefsLangKey);
        }
        this.prefsLangKey = prefsLangKey;
    }

    public String getPrefsLangKey() {
        return prefsLangKey;
    }

    public void setDefLocaleStr(String defLocaleStr) {
        this.defLocaleStr = defLocaleStr;
    }

    public String getDefLocaleStr() {
        return defLocaleStr;
    }

    public void addValueChangeListener(ValueChangeListener listener) {
        changeListeners.add(listener);
    }

    protected void fireValueChanged(Object oldVal, Object newVal) {
        for (ValueChangeListener listener : changeListeners) {
            listener.vlaueChanged(oldVal, newVal);
        }
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                MyJFrame frame = new MyJFrame();
                frame.add(new MyJField(), BorderLayout.NORTH);
                frame.add(new MyJField(), BorderLayout.SOUTH);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
