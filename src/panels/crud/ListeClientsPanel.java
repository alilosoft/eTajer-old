/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package panels.crud;

import entities.Client;
import java.awt.BorderLayout;
import java.awt.Container;
import myComponents.MyJFrame;
import panels.RSTablePanel;
import dao.ClientDAO;
import java.awt.Dimension;
import panels.maj.MajClientPanel;

/**
 * @author alilo
 */
public class ListeClientsPanel extends RSTablePanel<Client, ClientDAO> {


    {
        setPreferredSize(new Dimension(600, 400));
        setMajPanel(new MajClientPanel(this));
        setFilterShortcut("CTRL", "F");
    }

    public ListeClientsPanel(Container owner, boolean checkable) {
        super(owner, checkable);
        initComponents();
        add(mainPanel);
        statusPanel.add(totalPanel);
        totalPanel.setMontant(ClientDAO.getInstance().getTotalCredit());

        //setAllowedOperation(DELETE, false);
        //setAllowedOperation(INSERT, false);
        //setAllowedOperation(EDIT, false);
    }

    //customize the table view
    @Override
    public ListeClientsPanel initTableView() {
        getTable().setColumnVisible("ID", false);
        getTable().setColumnVisible("App.TVA", false);
        getTable().setColumnsPreferredWidths(new int[]{80, 150, 150, 100, 100});
        return this;
    }

    @Override
    public ListeClientsPanel getNavigNEditList() {
        getStatusPanel().remove(totalPanel);
        getTable().setColumnVisible("Adresse", false);
        getTable().setColumnVisible("N° Tél/Fax", false);
        getTable().setColumnVisible("Tél.Portable", false);
        getTable().setColumnVisible("E-Mail", false);
        getTable().setColumnVisible("App.TVA", false);
        getTable().setColumnsPreferredWidths(new int[]{50, 200, 100});
        setPreferredSize(new Dimension(300, 400));
        return this;
    }

    @Override
    public ClientDAO getTableDAO() {
        return ClientDAO.getInstance();
    }

    @Override
    public void doOnTableModelChanged() {
        super.doOnTableModelChanged();
        //totalPanel.setMontant(ClientDAO.getInstance().getTotalCredit());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        totalPanel = new panels.views.MontantPanel();

        setLayout(new java.awt.BorderLayout());

        totalPanel.setMontantFont(new java.awt.Font("Agency FB", 0, 30)); // NOI18N
        totalPanel.setTitleFont(new java.awt.Font("Square721 BT", 0, 18)); // NOI18N
        totalPanel.setTitleIcon("");
        totalPanel.setTitleString("Crédit.Total:");
        add(totalPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private panels.views.MontantPanel totalPanel;
    // End of variables declaration//GEN-END:variables

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                MyJFrame frame = new MyJFrame();
                frame.getContentPane().add(new ListeClientsPanel(frame, false), BorderLayout.CENTER);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}