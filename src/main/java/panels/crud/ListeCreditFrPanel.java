/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * CtegoriePanel.java
 *
 * Created on 17/10/2009, 11:25:33 ص
 */
package panels.crud;

import dao.CreditFrDAO;
import java.awt.BorderLayout;
import java.awt.Container;
import myComponents.MyJFrame;
import panels.RSTablePanel;
import entities.CreditFr;
import java.awt.Dimension;
import javax.swing.JOptionPane;
import panels.maj.MajCreditFrPanel;

/**
 *
 * @author alilo
 */
public class ListeCreditFrPanel extends RSTablePanel<CreditFr, CreditFrDAO> {
    {
        setPreferredSize(new Dimension(750, 450));
        setMajPanel(new MajCreditFrPanel(this));
        setFilterShortcut("CTRL", "F");
    }
    public ListeCreditFrPanel(Container owner, boolean checkable) {
        super(owner, checkable);
    }

    @Override
    public ListeCreditFrPanel initTableView() {
        getTable().setColumnVisible("ID", false);
        getTable().setColumnVisible("ID_FR", false);
        getTable().setColumnVisible("ID_ACH", false);
        getTable().setColumnsPreferredWidths(new int[]{40, 40, 40, 40, 40, 200});
        return this;
    }

    @Override
    public CreditFrDAO getTableDAO() {
        return CreditFrDAO.getInstance();
    }

    @Override
    public void edit() {
        if(getSelectedEntity() instanceof CreditFr && getSelectedEntity().getAchat()!= null){
            String mess = "Vous ne pouvez pas modifier ce 'Crédit'!\nCar il est issue de l': " + getSelectedEntity().getAchat();
            JOptionPane.showMessageDialog(this, mess, "Attention!", JOptionPane.ERROR_MESSAGE);
            return;
        }
        super.edit();
    }
    @Override
    public void delete() {
        if(getSelectedEntity() instanceof CreditFr && getSelectedEntity().getAchat()!= null){
            String mess = "Vous ne pouvez pas supprimer ce 'Crédit'!\nCar il est issue de l': " + getSelectedEntity().getAchat();
            JOptionPane.showMessageDialog(this, mess, "Attention!", JOptionPane.ERROR_MESSAGE);
            return;
        }
        super.delete(); //To change body of generated methods, choose Tools | Templates.
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setPreferredSize(new java.awt.Dimension(500, 450));
        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                MyJFrame frame = new MyJFrame();
                frame.getContentPane().add(new ListeCreditFrPanel(frame, false).initTableView(), BorderLayout.CENTER);
                frame.pack();
                frame.setVisible(true);
                frame.setLocationRelativeTo(null);
            }
        });
    }
}
