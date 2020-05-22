/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myComponents;

import dialogs.MyJDialog;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.FocusManager;
import javax.swing.KeyStroke;
import javax.swing.LayoutFocusTraversalPolicy;

/**
 *
 * @author alilo
 */
public class MyJPanel extends javax.swing.JPanel {

    private final Preferences userPrefs;
    protected MyJDialog dialog;
    private Container parent;
    /**
     * The default component that will be focused when this panel shown.
     */
    private Component defaultFocusedComp, firstFocusedComp;
    private final FocusTraversalPolicy focusPolicy = new LayoutFocusTraversalPolicy() {

        @Override
        public Component getFirstComponent(Container aContainer) {
            return getFirstFocusedComp();
        }

        @Override
        public Component getDefaultComponent(Container aContainer) {
            Component defComp;
            if (firstFocusedComp == null) {
                defComp = getDefaultFocusedComp();
            } else {
                defComp = firstFocusedComp;
                firstFocusedComp = null;
            }
            return defComp;
        }
    };

    /**
     * Creates new form MyJPanel
     */
    public MyJPanel() {
        super();
        userPrefs = Preferences.userNodeForPackage(getClass());
        //Config focus policy
        //setFocusCycleRoot(true);
        setFocusTraversalPolicy(focusPolicy);
        Set<KeyStroke> focusKeys = new HashSet<>();
        focusKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0));
        setFocusTraversalKeys(FocusManager.FORWARD_TRAVERSAL_KEYS, focusKeys);
        focusKeys.clear();
        focusKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.SHIFT_DOWN_MASK));
        setFocusTraversalKeys(FocusManager.BACKWARD_TRAVERSAL_KEYS, focusKeys);
    }

    public MyJPanel(Container parent) {
        this();
        this.parent = parent;
    }

    public void showPanel(boolean modal) {
        if (dialog == null) {
            dialog = new MyJDialog(null, false, false);
            dialog.setOwnerContainer(parent);
        }
        dialog.setModal(modal);
        dialog.show(this);
    }

    /**
     * Set the first focused field.
     *
     * @param firstFocusedComp
     */
    public void setFirstFocusedComp(Component firstFocusedComp) {
        this.firstFocusedComp = firstFocusedComp;
    }

    /**
     * Return the component representing the field that must be focused when
     * this panel shown.
     *
     * @return
     */
    public Component getFirstFocusedComp() {
        return firstFocusedComp;
    }

    public void setDefaultFocusedComp(Component defaultFocusedComp) {
        this.defaultFocusedComp = defaultFocusedComp;
    }

    public Component getDefaultFocusedComp() {
        if (defaultFocusedComp == null) {
            return getComponent(0);
        } else {
            return defaultFocusedComp;
        }
    }

    public void savePreference(String prefName, Object prefValue) {
        if (prefValue instanceof Integer) {
            userPrefs.putInt(prefName, (Integer) prefValue);
        } else {
            if (prefValue instanceof Boolean) {
                userPrefs.putBoolean(prefName, (Boolean) prefValue);
            } else {
                if (prefValue instanceof Double) {
                    userPrefs.putDouble(prefName, (Double) prefValue);
                } else {
                    userPrefs.put(prefName, (String) prefValue);
                }
            }
        }
    }

    public String getPreferenceKeyName(String prefName) {
        return getClass().getName() + "_" + prefName;
    }

    public String getPreference(String prefName, String defValue) {
        return userPrefs.get(prefName, defValue);
    }

    public int getIntPreference(String prefName, int defValue) {
        return userPrefs.getInt(prefName, defValue);
    }

    public double getDoublePreference(String prefName, double defValue) {
        return userPrefs.getDouble(prefName, defValue);
    }

    public boolean getBooleanPreference(String prefName, boolean defValue) {
        return userPrefs.getBoolean(prefName, defValue);
    }

    public Preferences getUserPreferences() {
        return userPrefs;
    }

    public void savePreferences() {

    }

    public void loadPreferences() {
    }

    public MyJDialog getDialog() {
        return dialog;
    }

    //*************************************************
    public final void doOnPress(int key, int modifier, javax.swing.Action action, int condition) {
        //action.putValue(Action.ACCELERATOR_KEY, (modifier != 0 ? InputEvent.getModifiersExText(modifier) + "+" : "") + KeyEvent.getKeyText(key));
        getInputMap(condition).put(KeyStroke.getKeyStroke(key, modifier, false), action.hashCode());
        getActionMap().put(action.hashCode(), action);
    }

    public final void doOnPress(String key, String modifier, javax.swing.Action action, int condition) {
        //action.putValue(Action.ACCELERATOR_KEY, (modifier != null ? modifier + "+" : "") + key);
        String keyStroke = modifier == null ? "pressed " + key.toUpperCase() : modifier.toLowerCase() + " pressed " + key.toUpperCase();
        getInputMap(condition).put(KeyStroke.getKeyStroke(keyStroke), action.hashCode());
        getActionMap().put(action.hashCode(), action);
    }

    public final void doOnRelease(int key, int modifier, javax.swing.Action action, int condition) {
        //action.putValue(Action.ACCELERATOR_KEY, (modifier != 0 ? InputEvent.getModifiersExText(modifier) + "+" : "") + KeyEvent.getKeyText(key));
        getInputMap(condition).put(KeyStroke.getKeyStroke(key, modifier, true), action.hashCode());
        getActionMap().put(action.hashCode(), action);
    }

    /**
     * Associate an Action with a KeyStroke thats fire on release.
     *
     * @param key : KeyEvent key code name, i.e. the name following "VK_".
     * @param modifier : shift | control | ctrl | meta | alt | altGraph
     * @param action : action to fire.
     * @param condition
     */
    public final void doOnRelease(String key, String modifier, javax.swing.Action action, int condition) {
        //action.putValue(Action.ACCELERATOR_KEY, (modifier != null ? modifier + "+" : "") + key);
        String keyStroke = modifier == null ? "released " + key.toUpperCase() : modifier.toLowerCase() + " released " + key.toUpperCase();
        getInputMap(condition).put(KeyStroke.getKeyStroke(keyStroke), action.hashCode());
        getActionMap().put(action.hashCode(), action);
    }

    public String getActionDescription(Action a) {
        Action action = getActionMap().get(a.hashCode());
        if (action != null) {
            return action.getValue(Action.NAME) + " (" + action.getValue(Action.ACCELERATOR_KEY) + ")";
        } else {
            return a.getValue(Action.NAME).toString();
        }
    }

    public Action noAction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            // no thing to do!!!! used to clear actions shortcut
        }
    };

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setFocusable(false);
        setRequestFocusEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 439, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 330, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
