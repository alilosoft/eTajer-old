/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myComponents;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

/**
 *
 * @author alilo
 */
public class FindButton extends  JButton{

    public FindButton() {
        super();
        setAction(new AbstractAction("F3", new ImageIcon(getClass().getResource("/res/actions/find.png"))) {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("i am clicked!");
            }
        });
    }
    
    public static void main(String args[]){
        
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                MyJFrame frame = new MyJFrame();
                frame.getContentPane().add(new FindButton());
                frame.pack();
                frame.setVisible(true);
            }
        });
        
        
    }
}
