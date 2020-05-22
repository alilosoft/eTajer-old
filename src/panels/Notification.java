/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package panels;

import java.awt.Color;
import java.util.Objects;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 *
 * @author alilo
 */
public class Notification {
    private static Notification instance;
    private int index;
    private String title;
    private String mess;
    private SimpleAttributeSet style;
    private Color color = Color.BLACK;
    private String fontName = "Tahoma";
    private int fontSize = 13;
    private boolean bold = false;
    private boolean italic = false;

    public Notification(int ind) {
        this.title = "Notif"+ ind;
        this.mess = "Hello, I am empty notification!";
    }

    public Notification(String title, String mess) {
        this.title = title;
        this.mess = mess;
        this.style = getStyle();
    }

    public static Notification createNotification(int ind, String title, String mess, Color color, String fontName, int fontSize, boolean bold, boolean italic) {
        Notification notif = new Notification(ind);
        notif.setTitle(title);
        notif.setMess(mess);
        notif.setColor(color);
        notif.setFontSize(fontSize);
        notif.setBold(bold);
        notif.setItalic(italic);
        return notif;
    }

    public Notification setTitle(String title) {
        this.title = title;
        return this;
    }

    public Notification setMess(String mess) {
        this.mess = mess;
        return this;
    }

    public Notification setStyle(SimpleAttributeSet style) {
        this.style = style;
        return this;
    }

    public Notification setColor(Color color) {
        this.color = color;
        return this;
    }

    public Notification setFontName(String fontName) {
        this.fontName = fontName;
        return this;
    }

    public Notification setFontSize(int fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public Notification setBold(boolean bold) {
        this.bold = bold;
        return this;
    }

    public Notification setItalic(boolean italic) {
        this.italic = italic;
        return this;
    }
    
    public int getIndex() {
        return index;
    }

    public String getTitle() {
        return title;
    }
    
    public String getMess() {
        return mess;
    }

    public final SimpleAttributeSet getStyle() {
        if(style != null){
            return style;
        }
        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        StyleConstants.setForeground(attributeSet, color);
        StyleConstants.setFontFamily(attributeSet, fontName);
        StyleConstants.setFontSize(attributeSet, fontSize);
        StyleConstants.setBold(attributeSet, bold);
        StyleConstants.setItalic(attributeSet, italic);
        return attributeSet;
    }

    public Color getColor() {
        return color;
    }

    public int getFontSize() {
        return fontSize;
    }

    public boolean isBold() {
        return bold;
    }

    public boolean isItalic() {
        return italic;
    }

    @Override
    public String toString() {
        return title+": "+mess;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Notification) {
            Notification other = (Notification) obj;
            return title.equals(other.title);
        }else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.title);
        return hash;
    }
}
