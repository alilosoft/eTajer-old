/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package panels.masterDetails;

import dao.LigneAchatDAO;
import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import myComponents.MyJFrame;
import panels.MasterDetailsPanel;
import panels.ResultSet_Panel;
import entities.Achat;
import entities.LigneAch;
import entities.Produit;
import entities.Unite;
import java.math.BigDecimal;
import javax.swing.JComponent;
import static panels.CRUDPanel.INSERT;
import panels.crud.ListeAchatsPanel;
import panels.crud.ListeProduitsPanel;
import panels.maj.MajAchatPanel;

/**
 *
 * @author alilo
 */
public class GestAchatsPanel extends MasterDetailsPanel {

    private static final String ACH_SPLIT_POS = "mainAch_AchSplitPos";

    //<editor-fold defaultstate="collapsed" desc="Select Produit Enter Key Event Litener."> 
    private KeyAdapter addProduit2Achat = new KeyAdapter() {

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                if (listeProduits.getModelSelectedRow() >= 0) {
                    // if !update and !saved then save the current vnt before inserting new 'LigneVente'.

                    boolean insert = ((majAchatPanel.isUpdate() || majAchatPanel.isSaved()) && !majAchatPanel.isModified()) || majAchatPanel.save();
                    if (insert) {
                        Achat currentAch = majAchatPanel.getEditedEntity();
                        while (currentAch.isValide()) {
                            String mess = "Vous ne pouvez pas ajouter des produits a un achat validé!\n"
                                    + "Voulez vous crée un nouveau achat?";
                            int rep = JOptionPane.showConfirmDialog(listeProduits, mess, "Attention!", JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION);
                            if (rep == JOptionPane.YES_OPTION) {
                                majAchatPanel.add();
                                majAchatPanel.save();
                                currentAch = majAchatPanel.getEditedEntity();
                            } else {
                                e.consume();
                                return;
                            }
                        }
                        if (e.getModifiers() != 0) {
                            String mess = "Assurez qu'aucune touche parmi [Ctrl, Alt, AltGr, Shift] n'est appuyée!";
                            JOptionPane.showMessageDialog(listeProduits, mess, "Attention!", JOptionPane.WARNING_MESSAGE);
                            e.consume();
                            return;
                        }
                        // create new 'LigneVente'.
                        LigneAch la = new LigneAch(0, BigDecimal.ZERO, 1, 1, BigDecimal.ZERO);
                        // set the 'Vente'
                        la.setAchat(currentAch);
                        // set the 'Produit'
                        Produit selProd = listeProduits.getSelectedEntity();
                        la.setProduit(selProd);
                        // set the 'Unite.Vente'
                        la.setUnite(selProd.getUnite());
                        la.setQteUnit(selProd.getUnite().getQte());
                        majAchatPanel.getDetailsAchatPanel().getMajPanel().showNewPanel(la);
                        majAchatPanel.getDetailsAchatPanel().reload();
                        majAchatPanel.getDetailsAchatPanel().selectID(LigneAchatDAO.getInstance().getGeneratedID());
                    }
                }
                e.consume();
                listeProduits.doFilter();
            }
        }
    };
    //</editor-fold>

    private ListeProduitsPanel listeProduits = new ListeProduitsPanel(this, false, true) {
        {
            setEnterKeyListener(addProduit2Achat);
            statusPanel.remove(printSelCBBtn);
            statusPanel.remove(printAllCBBtn);
        }

        @Override
        public void doFilter() {
            prodsAchsTabbedPane.setSelectedComponent(prodsPanel);
            super.doFilter();
        }
    }.initTableView();

    private ListeAchatsPanel listeAchatsPanel = new ListeAchatsPanel(this, false) {

        {
            setAllowedOperation(INSERT, false);
            setAllowedOperation(EDIT, false);
        }

        @Override
        public void doFilter() {
            super.doFilter();
            prodsAchsTabbedPane.setSelectedComponent(vntsPanel);
        }

        @Override
        public void delete() {
            super.delete();
            listeProduits.reload();
        }
    }.initTableView();

    private MajAchatPanel majAchatPanel = new MajAchatPanel(listeAchatsPanel) {
        {
            loadPreferences();
        }

        @Override
        public boolean save() {
            boolean newVnt = !isUpdate();
            if (super.save()) {
                if (newVnt || isModified()) {
                    Achat current = getEditedEntity();
                    listeAchatsPanel.reload();// reload the vente list after saving!
                    listeAchatsPanel.selectEntity(current); // select the new edited/inserted entity!
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean ok() {
            if (super.ok()) {// every time the ok action made reload the vente liste to reflect changes!
                listeAchatsPanel.reload();
                return true;
            }
            return false;
        }

        @Override
        public boolean add() {
            if (super.add()) {
                listeAchatsPanel.filterList("");// TO-DO: clear the filter field!
                //listeAchatsPanel.clearSelection();
                listeProduits.doFilter();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void setValidated(boolean validated) {
            super.setValidated(validated);
            if (prodsAchsTabbedPane.getSelectedIndex() == 0) {
                listeProduits.reload();
            } else {
                listeAchatsPanel.reload();
            }
        }
    };

    // Constructor
    public GestAchatsPanel() {
        initComponents();
        prodsPanel.add(listeProduits, BorderLayout.CENTER);
        vntsPanel.add(listeAchatsPanel, BorderLayout.CENTER);
        listeAchatsPanel.getPropertyChangeSupport().addPropertyChangeListener(ResultSet_Panel.ENTITY_TO_EDIT_PROPERTY, majAchatPanel);
        majVntP.add(majAchatPanel, BorderLayout.CENTER);
        artSplitPane.setDividerLocation(getUserPreferences().getInt(ACH_SPLIT_POS, 0));
        listeAchatsPanel.reload();
        majAchatPanel.clearFields();

        doOnRelease("R", "CTRL", listeProduits.getFilterAction(), JComponent.WHEN_IN_FOCUSED_WINDOW);
        doOnRelease("T", "CTRL", listeAchatsPanel.getFilterAction(), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    @Override
    public void reload() {
        if (prodsAchsTabbedPane.getSelectedIndex() == 0) {
            listeProduits.reload();
        } else {
            if (prodsAchsTabbedPane.getSelectedIndex() == 1) {
                if (listeAchatsPanel.getSelectedEntity() instanceof Achat && listeAchatsPanel.getSelectedEntity().isValide()) {
                    listeAchatsPanel.reload();
                }
            }
        }
    }

    @Override
    public void doFilter() {
        listeProduits.doFilter();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        artSplitPane = new javax.swing.JSplitPane();
        prodsAchsTabbedPane = new javax.swing.JTabbedPane();
        prodsPanel = new javax.swing.JPanel();
        vntsPanel = new javax.swing.JPanel();
        majVntP = new javax.swing.JPanel();

        setFocusable(false);
        setLayout(new java.awt.BorderLayout());

        artSplitPane.setDividerLocation(500);
        artSplitPane.setDividerSize(15);
        artSplitPane.setFocusable(false);
        artSplitPane.setOneTouchExpandable(true);
        artSplitPane.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                artSplitPanePropertyChange(evt);
            }
        });

        prodsAchsTabbedPane.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        prodsAchsTabbedPane.setFocusable(false);
        prodsAchsTabbedPane.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        prodsAchsTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                prodsAchsTabbedPaneStateChanged(evt);
            }
        });

        prodsPanel.setFocusable(false);
        prodsPanel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        prodsPanel.setLayout(new java.awt.BorderLayout());
        prodsAchsTabbedPane.addTab("Produits (Ctrl+R)", new javax.swing.ImageIcon(getClass().getResource("/res/icons/prod24.png")), prodsPanel); // NOI18N

        vntsPanel.setFocusable(false);
        vntsPanel.setPreferredSize(new java.awt.Dimension(500, 400));
        vntsPanel.setLayout(new java.awt.BorderLayout());
        prodsAchsTabbedPane.addTab("Achats (Ctrl+T)", new javax.swing.ImageIcon(getClass().getResource("/res/icons/vente24.png")), vntsPanel); // NOI18N

        artSplitPane.setLeftComponent(prodsAchsTabbedPane);
        prodsAchsTabbedPane.getAccessibleContext().setAccessibleName("Liste des Articles à Vendre");

        majVntP.setBorder(javax.swing.BorderFactory.createTitledBorder(null, " Détails du Vente (Ctrl+F)", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 13))); // NOI18N
        majVntP.setFocusable(false);
        majVntP.setPreferredSize(new java.awt.Dimension(500, 28));
        majVntP.setLayout(new java.awt.BorderLayout());
        artSplitPane.setRightComponent(majVntP);

        add(artSplitPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void artSplitPanePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_artSplitPanePropertyChange
        if (evt.getPropertyName().equals(JSplitPane.DIVIDER_LOCATION_PROPERTY)) {
            savePreference(ACH_SPLIT_POS, artSplitPane.getDividerLocation());
        }
    }//GEN-LAST:event_artSplitPanePropertyChange

    private void prodsAchsTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_prodsAchsTabbedPaneStateChanged
        if (prodsAchsTabbedPane.getSelectedIndex() == 0) {
            listeProduits.reload();
        } else {
            listeAchatsPanel.reload();
        }
    }//GEN-LAST:event_prodsAchsTabbedPaneStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSplitPane artSplitPane;
    private javax.swing.JPanel majVntP;
    private javax.swing.JTabbedPane prodsAchsTabbedPane;
    private javax.swing.JPanel prodsPanel;
    private javax.swing.JPanel vntsPanel;
    // End of variables declaration//GEN-END:variables

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                MyJFrame frame = new MyJFrame("Gest. Achats");
                frame.add(new GestAchatsPanel());
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
