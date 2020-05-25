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

import entities.EntityClass;
import java.awt.BorderLayout;
import java.awt.Container;
import java.sql.ResultSet;
import myComponents.MyJFrame;
import panels.RSTablePanel;
import dao.LotEnStockDAO;
import dbTools.DBManager;
import dialogs.MyJDialog;
import entities.Depot;
import entities.Produit;
import entities.EnStock;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.HashMap;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import myComponents.MyJTable;
import panels.ResultSet_Panel;
import panels.maj.MajLotEnStockPanel;
import printing.PrintingTools;

/**
 *
 * @author alilo
 */
public class ListeLotsEnStockPanel extends RSTablePanel<EnStock, LotEnStockDAO> {

    {
        setPreferredSize(new Dimension(750, 400));
        setMajPanel(new MajLotEnStockPanel(this));
        setFilterShortcut("CTRL", "R");
    }

    private Produit masterProd;
    private ListeDepotsPanel listeDepots;

    public ListeLotsEnStockPanel(Container owner, boolean checkable) {
        super(owner, checkable);
        initComponents();
        initDepotsPanel();
        add(mainPanel, BorderLayout.CENTER);
        setMasterPanel(listeDepots, "ID_DEPOT");
        initStatusP();
    }

    @Override
    public ListeLotsEnStockPanel initTableView() {
        getTable().setColumnVisible("ID", false);
        getTable().setColumnVisible("ID_PROD", false);
        getTable().setColumnVisible("ID_DEPOT", false);
        getTable().setColumnVisible("ID_FAM", false);
        getTable().setColumnVisible("ID_CATEG", false);
        getTable().setColumnVisible("Réf.Produit", false);
        getTable().setColumnVisible("Dépôt", false);
        getTable().setColumnVisible("PU.Gros", false);
        getTable().setColumnVisible("PU.Demi-Gr", false);
        getTable().setColumnVisible("PU.Super-Gr", false);
        getTable().setColumnVisible("Date.Exp", false);
        getTable().setColumnVisible("Actif", false);
        //getTable().setColumnVisible("Total/Ach", false);
        getTable().setColumnsPreferredWidths(new int[]{90, 200, 10, 60, 40, 40, 40, 40, 5});
        return this;
    }

    @Override
    public LotEnStockDAO getTableDAO() {
        return LotEnStockDAO.getInstance();
    }

    @Override
    public ResultSet getResultSet() {
        if (masterProd != null) {
            return getTableDAO().getDetailsStock(masterProd);
        }
        if (qteMoreZeroRB != null && qteMoreZeroRB.isSelected()) {
            return getTableDAO().getLotsDispo();
        }
        if (qteZeroRB != null && qteZeroRB.isSelected()) {
            return getTableDAO().getLotsNonDispo();
        }
        return super.getResultSet();
    }

    private void initStatusP() {
        totalStkP.setMontant(calculateTotal());
        statusPanel.add(totalStkP);
        //statusPanel.add(printP);
    }

    public void showTotalStock(boolean show) {
        if (show) {
            statusPanel.add(totalStkP);
        } else {
            statusPanel.remove(totalStkP);
        }
    }

    @Override
    public void insert() {
        if (getSelectedEntity() instanceof EnStock) {
            EnStock selEnt = getSelectedEntity();
            if (selEnt != null && selEnt.getId() > 0) {
                getMajPanel().setChildEntity(selEnt.getProduit(), true);
            }
        }
        super.insert();
    }

    private MyJDialog mjd = new MyJDialog(this, true, false);

    @Override
    public void print() {
        mjd.show(printP);
    }

    @Override
    public void doOnTableModelChanged() {
        super.doOnTableModelChanged();
        showQteGlobal();
        if (totalStkP != null) {
            totalStkP.setMontant(calculateTotal());
        }
    }

    @Override
    public void doOnTableRowSortChanged() {
        super.doOnTableRowSortChanged();
        if (totalStkP != null) {
            totalStkP.setMontant(calculateTotal());
        }
    }

    public BigDecimal calculateTotal() {
        double total = 0;
        for (int i = 0; i < getTable().getRowCount(); i++) {
            double totalRow = (double) getModel().getValueAt(getTable().convertRowIndexToModel(i), "Total/Ach");
            total += totalRow;
        }
        return new BigDecimal(total);
    }

    @Override
    public void setMasterEntity(EntityClass masterEntity) {
        super.setMasterEntity(masterEntity);
        if (masterEntity instanceof Produit) {
            setMasterProd((Produit) masterEntity);
        }
    }

    public void setMasterProd(Produit prod) {
        masterProd = prod;
        reload();
    }

    public Produit getMasterProd() {
        return masterProd;
    }

    public void setMasterDepot(Depot depot) {
        if (depot != null && depot.getId() > 0) {
            filterList("ID_DEPOT", depot.getId().toString(), MyJTable.EXACT_MATCH_FILTER);
        } else {
            filterList("ID_DEPOT", "", MyJTable.EXACT_MATCH_FILTER);
        }
    }

    public final void showQteGlobal() {
        if (masterProd != null) {

        }
    }

    public final void initDepotsPanel() {
        listeDepots = new ListeDepotsPanel(this, false).getNavigNEditList();
        listeDepots.getPropertyChangeSupport().addPropertyChangeListener(ResultSet_Panel.ENTITY_TO_VIEW_PROPERTY, this);
        selDepotPanel.setSelListPanel(listeDepots);
    }

    private final Action printTicketPrixAct = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!(getSelectedEntity() instanceof EnStock)) {
                JOptionPane.showMessageDialog(ListeLotsEnStockPanel.this, "Acun lot sélectionné!");
                return;
            }
            HashMap<String, Object> params = new HashMap<>();
            params.put("ID_LOT", getSelectedEntity().getId());
            PrintingTools.previewReport("/printing/Lot_PrixVnt.jasper", DBManager.getInstance().getDefaultConnection(), params);
        }
    };

    private final Action printCodeBarAct = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!(getSelectedEntity() instanceof EnStock)) {
                JOptionPane.showMessageDialog(ListeLotsEnStockPanel.this, "Acun lot sélectionné!");
                return;
            }
            HashMap<String, Object> params = new HashMap<>();
            params.put("ID_LOT", getSelectedEntity().getId());
            PrintingTools.previewReport("/printing/Lot_CodeBar.jasper", DBManager.getInstance().getDefaultConnection(), params);
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

        depotBG = new javax.swing.ButtonGroup();
        qteBG = new javax.swing.ButtonGroup();
        totalStkP = new panels.views.MontantPanel();
        printP = new javax.swing.JPanel();
        printSelCBBtn = new javax.swing.JButton();
        printAllCBBtn = new javax.swing.JButton();
        filterPanel = new javax.swing.JPanel();
        selDepotPanel = new panels.SelectionPanel<>();
        jSeparator1 = new javax.swing.JSeparator();
        qteZeroRB = new javax.swing.JRadioButton();
        qteLessMinRB = new javax.swing.JRadioButton();
        jLabel10 = new javax.swing.JLabel();
        qteMoreZeroRB = new javax.swing.JRadioButton();

        totalStkP.setMontantFont(new java.awt.Font("Agency FB", 0, 36)); // NOI18N
        totalStkP.setTitleFont(new java.awt.Font("Agency FB", 0, 36)); // NOI18N
        totalStkP.setTitleString("Total.Stock: ");

        printP.setPreferredSize(new java.awt.Dimension(353, 100));
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
        setLayout(new java.awt.BorderLayout());

        filterPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 0, 5));
        java.awt.GridBagLayout depotPanelLayout = new java.awt.GridBagLayout();
        depotPanelLayout.columnWidths = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        depotPanelLayout.rowHeights = new int[] {0, 3, 0};
        filterPanel.setLayout(depotPanelLayout);

        selDepotPanel.setDescription("Tous les dépôts!");
        selDepotPanel.setShortcutKey("F3");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        filterPanel.add(selDepotPanel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        filterPanel.add(jSeparator1, gridBagConstraints);

        qteBG.add(qteZeroRB);
        qteZeroRB.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        qteZeroRB.setText("Qte = 0");
        qteZeroRB.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 1));
        qteZeroRB.setFocusable(false);
        qteZeroRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                qteZeroRBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        filterPanel.add(qteZeroRB, gridBagConstraints);

        qteBG.add(qteLessMinRB);
        qteLessMinRB.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        qteLessMinRB.setText("Qte < Min");
        qteLessMinRB.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 1));
        qteLessMinRB.setEnabled(false);
        qteLessMinRB.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 0;
        filterPanel.add(qteLessMinRB, gridBagConstraints);

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel10.setText("Dépôt:");
        jLabel10.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        filterPanel.add(jLabel10, gridBagConstraints);

        qteBG.add(qteMoreZeroRB);
        qteMoreZeroRB.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        qteMoreZeroRB.setSelected(true);
        qteMoreZeroRB.setText("Qte > 0");
        qteMoreZeroRB.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 1));
        qteMoreZeroRB.setFocusable(false);
        qteMoreZeroRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                qteMoreZeroRBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 0;
        filterPanel.add(qteMoreZeroRB, gridBagConstraints);

        add(filterPanel, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents

    private void qteZeroRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_qteZeroRBActionPerformed
        reload();
    }//GEN-LAST:event_qteZeroRBActionPerformed

    private void qteMoreZeroRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_qteMoreZeroRBActionPerformed
        reload();
    }//GEN-LAST:event_qteMoreZeroRBActionPerformed

    private void printSelCBBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printSelCBBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_printSelCBBtnActionPerformed

    private void printAllCBBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printAllCBBtnActionPerformed

    }//GEN-LAST:event_printAllCBBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup depotBG;
    private javax.swing.JPanel filterPanel;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JSeparator jSeparator1;
    protected javax.swing.JButton printAllCBBtn;
    protected javax.swing.JPanel printP;
    protected javax.swing.JButton printSelCBBtn;
    private javax.swing.ButtonGroup qteBG;
    private javax.swing.JRadioButton qteLessMinRB;
    private javax.swing.JRadioButton qteMoreZeroRB;
    private javax.swing.JRadioButton qteZeroRB;
    private panels.SelectionPanel<Depot> selDepotPanel;
    private panels.views.MontantPanel totalStkP;
    // End of variables declaration//GEN-END:variables
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        MyJFrame frame = new MyJFrame();
        frame.getContentPane().add(new ListeLotsEnStockPanel(frame, false).initTableView(), BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
}
