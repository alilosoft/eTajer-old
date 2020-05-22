/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myComponents;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

/**
 *
 * @author alilo
 */
public class MyJTabbedPane extends JTabbedPane {

    public MyJTabbedPane() {
        super();
        setFocusable(false);
        setRequestFocusEnabled(false);
    }

    @Override
    public void addTab(String title, Component component) {
        super.addTab(title, component);
        setTabComponentAt(indexOfComponent(component), new MyJTabbedPanTitle(title, this, component));
    }

    @Override
    public void addTab(String title, Icon icon, Component component) {
        super.addTab(title, component);
        setTabComponentAt(indexOfComponent(component), new MyJTabbedPanTitle(title, icon, this, component));
    }
}
