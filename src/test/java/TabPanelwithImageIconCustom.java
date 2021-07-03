 import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

public class TabPanelwithImageIconCustom extends JFrame {
  private JTextField textfield = new JTextField();

  public static void main(String[] args) {
    TabPanelwithImageIconCustom that = new TabPanelwithImageIconCustom();
    that.setVisible(true);
  }

  public TabPanelwithImageIconCustom() {
    setSize(450, 350);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    getContentPane().add(textfield, BorderLayout.SOUTH);

    JMenuBar mbar = new JMenuBar();
    JMenu menu = new JMenu("File");
    menu.add(new JCheckBoxMenuItem("Check Me"));
    menu.addSeparator();
    JMenuItem item = new JMenuItem("Exit");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });
    menu.add(item);
    mbar.add(menu);
    setJMenuBar(mbar);

    JTabbedPane tabbedPane = new JTabbedPane();

    tabbedPane.addTab("Button", 
        new TabIcon(), 
        new JButton(""), 
        "Click here for Button demo"); 
    
    getContentPane().add(tabbedPane, BorderLayout.CENTER);
  }
}
class TabIcon implements Icon {
  public int getIconWidth() {
    return 16;
  }

  public int getIconHeight() {
    return 16;
  }

  public void paintIcon(Component c, Graphics g, int x, int y) {
    g.setColor(Color.black);
    g.fillRect(x + 4, y + 4, getIconWidth() - 8, getIconHeight() - 8);
    g.setColor(Color.cyan);
    g.fillRect(x + 6, y + 6, getIconWidth() - 12, getIconHeight() - 12);
  }
}