package other;


import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JWindow;

public class OkCancel extends JWindow {
    private JButton okB = new JButton(new ImageIcon(getClass().getResource("/res/actions/ok16.png") ));
    private JButton cancelB = new JButton(new ImageIcon(getClass().getResource("/res/actions/cancel16.png")));
    private int w = 50;
    private int h = 24;

    public OkCancel( ) {
      setSize(w,h);
      setBackground(Color.yellow);
      JPanel p = new JPanel(new GridLayout(0,2));
      // p.setBorder(BorderFactory.createLineBorder(Color.gray));
      // okB.setBorder(null);
      // cancelB.setBorder(null);
      p.add(okB);
      p.add(cancelB);
      setContentPane(p);

      okB.addActionListener(new ActionListener( ) {
        public void actionPerformed(ActionEvent ae) {
          //stopCellEditing( );
        }
      });

      cancelB.addActionListener(new ActionListener( ) {
        public void actionPerformed(ActionEvent ae) {
          //cancelCellEditing( );
        }
      });
    }
  }
