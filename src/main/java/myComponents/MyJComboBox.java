/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myComponents;

import com.l2fprod.common.swing.renderer.DefaultCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Locale;
import javax.swing.ComboBoxEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

/**
 *
 * @author alilo
 */
public class MyJComboBox extends JComboBox {

    private Locale keyBoardLayout;
    private boolean addNewItems;
    Locale oldLocal;
    String orientation;
    Color focusColor = new Color(255,255,204);
    ComboBoxEditor editor = new BasicComboBoxEditor(){

        @Override
        protected JTextField createEditorComponent() {
            MyJField field = new MyJField();
            return field; 
        }
    };
    
    ListCellRenderer cellRenderer = new BasicComboBoxRenderer(){
        {
            setBorder(null);
        }
    };
    
    public MyJComboBox() {
        //setRenderer(cellRenderer);
        //setEditor(editor);
        
        setFont(new FontUIResource("tahoma", FontUIResource.BOLD, 13));
        setBackground(Color.WHITE);
        addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                getEditor().selectAll();
                oldLocal = getInputContext().getLocale();
                if (keyBoardLayout != null) {
                    getInputContext().selectInputMethod(keyBoardLayout);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                getInputContext().selectInputMethod(oldLocal);
                if (isAddNewItems()) {
                    ((DefaultComboBoxModel) getModel()).addElement(getSelectedItem());
                }
            }
        });

        addFocusListener(new FocusAdapter() {

            private final Color oldBackground = getBackground();
            private final Color oldForeground = getForeground();

            @Override
            public void focusGained(FocusEvent e) {

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        while (hasFocus()) {
                            //new Color(255, 255, 204)
                            setBackground(focusColor);
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException ex) {
                            }
                            setBackground(oldBackground);
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException ex) {
                            }
                        }
                    }
                }).start();
            }

            @Override
            public void focusLost(FocusEvent e) {
                //getDateEditor().getDate().
                super.focusLost(e);
                setBackground(oldBackground);
            }
        });
    }

    public void setKeyBoardLayout(Locale keyBoardLayout) {
        this.keyBoardLayout = keyBoardLayout;
    }

    public Locale getKeyBoardLayout() {
        return keyBoardLayout;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;

        if (orientation.equalsIgnoreCase(Orientation.RTL)) {
            setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        } else {
            if (orientation.equalsIgnoreCase(Orientation.LTR)) {
                setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
            } else {
                setComponentOrientation(ComponentOrientation.getOrientation(getDefaultLocale()));
            }
        }
    }

    public String getOrientation() {
        return orientation;
    }

    public void setAddNewItems(boolean addNewItems) {
        this.addNewItems = addNewItems;
    }

    public boolean isAddNewItems() {
        return addNewItems;
    }

    @Override
    public final synchronized void addFocusListener(FocusListener l) {
        super.addFocusListener(l);
        getEditor().getEditorComponent().addFocusListener(l);
    }
}
