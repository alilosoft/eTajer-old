/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ProduitPanel.java
 *
 * Created on 17/10/2009, 03:35:32 م
 */
package panels.crud;

import entities.Produit;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import myComponents.MyJFrame;
import panels.RSTablePanel;
import dao.ProduitDAO;
import dbTools.DBManager;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import panels.maj.MajProduitPanel;
import printing.PrintingTools;

/**
 *
 * @author alilo
 */
public class ListeProduitsPanel extends RSTablePanel<Produit, ProduitDAO> {

    {
        setPreferredSize(new Dimension(600, 400));
        setFilterShortcut("CTRL", "R");
    }

    public ListeProduitsPanel(Container owner, boolean checkable, boolean defaultMajP) {
        this(owner, checkable);
        if (defaultMajP) {
            setMajPanel(new MajProduitPanel(this).setEnableGestStock(true));
        }
    }

    public ListeProduitsPanel(Container owner, boolean checkable) {
        super(owner, checkable);
        initComponents();
        add(mainPanel, BorderLayout.CENTER);
        //statusPanel.add(printP);
    }
    
    
    @Override
    public ListeProduitsPanel initTableView() {
        getTable().setColumnVisible("ID", false);
        getTable().setColumnVisible("ID_FAM", false);
        getTable().setColumnVisible("ID_CATEG", false);
        //getTable().setColumnVisible("Réf/C.B", true);
        //getTable().setColumnVisible("Désignation", true);
        getTable().setColumnsPreferredWidths(new int[]{100, 250});
        return this;
    }

    @Override
    public ProduitDAO getTableDAO() {
        return ProduitDAO.getInstance();
    }

    private final Action printTicketPrixAct = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if(!(getSelectedEntity() instanceof  Produit)){
                JOptionPane.showMessageDialog(ListeProduitsPanel.this, "Acun produit sélectionné!");
                return;
            }
            HashMap<String, Object> params = new HashMap<>();
            params.put("ID_PROD", getSelectedEntity().getId());
            PrintingTools.previewReport("/printing/Prod_PrixVnt.jasper", DBManager.getInstance().getDefaultConnection(), params);
        }
    };

    private final Action printCodeBarAct = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if(!(getSelectedEntity() instanceof  Produit)){
                JOptionPane.showMessageDialog(ListeProduitsPanel.this, "Acun produit sélectionné!");
                return;
            }
            HashMap<String, Object> params = new HashMap<>();
            params.put("ID_PROD", getSelectedEntity().getId());
            PrintingTools.previewReport("/printing/Prod_CodeBar.jasper", DBManager.getInstance().getDefaultConnection(), params);
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
        java.awt.GridBagConstraints gridBagConstraints;

        printP = new javax.swing.JPanel();
        printSelCBBtn = new javax.swing.JButton();
        printAllCBBtn = new javax.swing.JButton();

        printP.setLayout(new java.awt.GridBagLayout());

        printSelCBBtn.setAction(printCodeBarAct);
        printSelCBBtn.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        printSelCBBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/print16.png"))); // NOI18N
        printSelCBBtn.setText("Imprimer le Code à Barres du produit sélectionné.");
        printSelCBBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printSelCBBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        printP.add(printSelCBBtn, gridBagConstraints);

        printAllCBBtn.setAction(printTicketPrixAct);
        printAllCBBtn.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        printAllCBBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/print16.png"))); // NOI18N
        printAllCBBtn.setText("Imprimer le Ticket de Prix du produit sélectionné.");
        printAllCBBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printAllCBBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        printP.add(printAllCBBtn, gridBagConstraints);

        setFocusable(false);
        setPreferredSize(new java.awt.Dimension(700, 500));
        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    private void printAllCBBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printAllCBBtnActionPerformed

    }//GEN-LAST:event_printAllCBBtnActionPerformed

    private void printSelCBBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printSelCBBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_printSelCBBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JButton printAllCBBtn;
    protected javax.swing.JPanel printP;
    protected javax.swing.JButton printSelCBBtn;
    // End of variables declaration//GEN-END:variables

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                MyJFrame frame = new MyJFrame();
                frame.getContentPane().add(new ListeProduitsPanel(frame, false, true).initTableView(), BorderLayout.CENTER);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
