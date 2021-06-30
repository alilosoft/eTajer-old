/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myComponents;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;

/**
 *
 * @author alilo
 */
public class MyJDateChooser extends JDateChooser {
    private MaskFormatter maskFormatter;
    
    public MyJDateChooser() {
        super();
        
        try {
            //setLocale(Locale.FRENCH);
            maskFormatter = new MaskFormatter("##/##/####");
            maskFormatter.setPlaceholderCharacter('-');
        } catch (ParseException ex) {
        }
        //getDateTextFieldEditor().setMaskVisible(true);
        getDateTextFieldEditor().setFormatterFactory(new DefaultFormatterFactory(maskFormatter));
        
        setDateFormatString("dd/MM/yyyy");
        //((JTextFieldDateEditor) dateEditor).setEditable(false);

        setDate(new Date());
        //setMaxSelectableDate(new Date());

        getDateTextFieldEditor().setBackground(Color.WHITE);
        setFont(new java.awt.Font("Tahoma", 1, 12));

        getDateTextFieldEditor().addFocusListener(new FocusAdapter() {

            private Color oldBackColor = getDateTextFieldEditor().getBackground();

            @Override
            public void focusGained(FocusEvent e) {
                
                getDateTextFieldEditor().setCaretPosition(0);
                
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        while (getDateTextFieldEditor().hasFocus()) {
                            getDateTextFieldEditor().setBackground(new Color(255, 255, 204));
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException ex) {
                            }
                            getDateTextFieldEditor().setBackground(oldBackColor);

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
                getDateTextFieldEditor().setBackground(oldBackColor);
            }
        });


    }

    public MyJDateChooser(String dateFormat) {
        super();
        setLocale(getDefaultLocale());
        setDateFormatString(dateFormat);
    }

    public final JTextFieldDateEditor getDateTextFieldEditor() {
        return (JTextFieldDateEditor) dateEditor;
    }
    
    public String getFormatedDate(){
        return getDateTextFieldEditor().getText();
    }
    
    public String getFormatedDate(DateFormat format){
        Date d = getDate();
        return format.format(d);
    }
    
    public void clearDate(){
        getDateTextFieldEditor().setText("");
    }
    
    public void setDateStr(String str){
        clearDate();
        getDateTextFieldEditor().setText(str);
    }
}
