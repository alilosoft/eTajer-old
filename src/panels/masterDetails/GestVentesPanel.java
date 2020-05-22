/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package panels.masterDetails;

import entities.Vente;
import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import myComponents.MyJFrame;
import panels.MasterDetailsPanel;
import panels.ResultSet_Panel;
import panels.crud.ListeVentesPanel;
import panels.maj.MajVentePanel;
import dao.LigneVenteDAO;
import entities.EnStock;
import entities.LigneVnt;
import entities.Produit;
import entities.TypeVnt;
import entities.Unite;
import java.math.BigDecimal;
import javax.swing.JComponent;
import static panels.CRUDPanel.INSERT;
import panels.crud.ListeLotsAVendrePanel;
/**
 *
 * @author alilo
 */
public class GestVentesPanel extends MasterDetailsPanel {

    private static final String VNT_SPLIT_POS = "mainVnt_VntSplitPos";

    //<editor-fold defaultstate="collapsed" desc="Select Produit Enter Key Event Litener."> 
    private KeyAdapter addProduit2Vente = new KeyAdapter() {

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                if (listeProdsAVendre.getModelSelectedRow() >= 0) {
                    // if !update and !saved then save the current vnt before inserting new 'LigneVente'.

                    boolean insert = ((majVentePanel.isUpdate() || majVentePanel.isSaved()) && !majVentePanel.isModified()) || majVentePanel.save();
                    if (insert) {
                        Vente currentVente = majVentePanel.getEditedEntity();
                        while (currentVente.isValidee()) {
                            String mess = "Vous ne pouvez pas ajouter des produits a une livraison validée!\n"
                                    + "Voulez vous crée une nouvelle commande?";
                            int rep = JOptionPane.showConfirmDialog(listeProdsAVendre, mess, "Attention!", JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION);
                            if (rep == JOptionPane.YES_OPTION) {
                                majVentePanel.add();
                                majVentePanel.save();
                                currentVente = majVentePanel.getEditedEntity();
                            } else {
                                e.consume();
                                return;
                            }
                        }
                        if (e.getModifiers() != 0) {
                            String mess = "Assurez qu'aucune touche parmi [Ctrl, Alt, AltGr, Shift] n'est appuyée!";
                            JOptionPane.showMessageDialog(listeProdsAVendre, mess, "Attention!", JOptionPane.WARNING_MESSAGE);
                            e.consume();
                            return;
                        }
                        // Create a 'LigneVnt' from selection!
                        EnStock lotAVendre = listeProdsAVendre.getSelectedEntity();
                        Produit produitVendu = lotAVendre.getProduit();
                        BigDecimal puVnt = lotAVendre.getPuVntDt();
                        // create new 'LigneVente'.
                        LigneVnt lv = new LigneVnt(0, puVnt, lotAVendre.getPuAch(), 1, 1, puVnt);
                        // set the 'Vente'
                        lv.setVente(currentVente);
                        // set the 'Produit'
                        lv.setEnStock(lotAVendre);
                        // set the 'Unite.Vente'
                        lv.setUniteVnt(produitVendu.getUnite());
                        if (currentVente.getTypeVnt().equals(TypeVnt.GROS)) {
                            lv.setPuVnt(lotAVendre.getPuVntGr());
                            // get unité de gros;
                            lv.setQteUnitair(produitVendu.getUnite().getQte());
                            majVentePanel.getDetailsVentePanel().getMajPanel().showNewPanel(lv);
                        } else {// TypeVnt.DETAIL
                            if (main.MainApp.getInstance().isNumLockOn()) {
                                lv.setQteUnitair(produitVendu.getUnite().getQte());
                                majVentePanel.getDetailsVentePanel().getMajPanel().showNewPanel(lv);
                                //Toolkit.getDefaultToolkit().setLockingKeyState(KeyEvent.VK_NUM_LOCK, false);
                            } else {
                                LigneVenteDAO.getInstance().insert(lv);
                            }
                        }
                        if (lv.getQteUnitaire() > lv.getEnStock().getQte()) {
                            String mess = "La quantité demandée est non disponible en Stock!\n"
                                    + "Cette livraison ne pourra pas être valider.";
                            JOptionPane.showMessageDialog(majVentePanel, mess, "Attention!", JOptionPane.WARNING_MESSAGE);
                        }
                        majVentePanel.getDetailsVentePanel().reload();
                        majVentePanel.getDetailsVentePanel().selectEntity(lv);
                    }
                }
                e.consume();
                if (majVentePanel.getDetailsVentePanel().isCBActive()) {
                    majVentePanel.getDetailsVentePanel().cbField.requestFocus();
                } else {
                    listeProdsAVendre.doFilter();
                }
            }
        }
    };
    //</editor-fold>

    private ListeLotsAVendrePanel listeProdsAVendre = new ListeLotsAVendrePanel(this, false) {
        {
            setEnterKeyListener(addProduit2Vente);
        }

        @Override
        public void doFilter() {
            prodsVntsTabbedPane.setSelectedComponent(prodsPanel);
            super.doFilter();
        }
    };

    private ListeVentesPanel listeVentesPanel = new ListeVentesPanel(this, false) {

        {
            setAllowedOperation(INSERT, false);
            setAllowedOperation(EDIT, false);
        }

        @Override
        public void doFilter() {
            super.doFilter();
            prodsVntsTabbedPane.setSelectedComponent(vntsPanel);
        }

        @Override
        public void delete() {
            super.delete();
            listeProdsAVendre.reload();
        }
    };

    private MajVentePanel majVentePanel = new MajVentePanel(listeVentesPanel) {
        {
            loadPreferences();
        }

        @Override
        public boolean save() {
            boolean newVnt = !isUpdate();
            if (super.save()) {
                if (newVnt || isModified()) {
                    Vente current = getEditedEntity();
                    listeVentesPanel.reload();// reload the vente list after saving!
                    listeVentesPanel.selectEntity(current); // select the new edited/inserted entity!
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean ok() {
            if (super.ok()) {// every time the ok action made reload the vente liste to reflect changes!
                listeVentesPanel.reload();
                return true;
            }
            return false;
        }

        @Override
        public boolean add() {
            if (super.add()) {
                listeVentesPanel.filterList("");// TO-DO: clear the filter field!
                if (majVentePanel.getDetailsVentePanel().isCBActive()) {
                    majVentePanel.getDetailsVentePanel().cbField.requestFocus();
                } else {
                    listeProdsAVendre.doFilter();
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void setValidated(boolean validated) {
            super.setValidated(validated);
            if (prodsVntsTabbedPane.getSelectedIndex() == 0) {
                listeProdsAVendre.reload();
            } else {
                listeVentesPanel.reload();
            }
        }

        @Override
        public boolean retourLivraison() {
            if (super.retourLivraison()) {
                if (prodsVntsTabbedPane.getSelectedIndex() == 0) {
                    listeProdsAVendre.reload();
                } else {
                    listeVentesPanel.reload();
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean delete() {
            if (super.delete()) {
                if (prodsVntsTabbedPane.getSelectedIndex() == 1) {
                    listeVentesPanel.reload();
                }
                return true;
            } else {
                return false;
            }
        }

    };

    // Constructor
    public GestVentesPanel() {
        initComponents();
        prodsPanel.add(listeProdsAVendre, BorderLayout.CENTER);
        vntsPanel.add(listeVentesPanel, BorderLayout.CENTER);
        listeVentesPanel.getPropertyChangeSupport().addPropertyChangeListener(ResultSet_Panel.ENTITY_TO_EDIT_PROPERTY, majVentePanel);
        majVntP.add(majVentePanel, BorderLayout.CENTER);
        artSplitPane.setDividerLocation(getUserPreferences().getInt(VNT_SPLIT_POS, 0));
        listeVentesPanel.reload();
        majVentePanel.clearFields();

        doOnRelease("R", "CTRL", listeProdsAVendre.getFilterAction(), JComponent.WHEN_IN_FOCUSED_WINDOW);
        doOnRelease("T", "CTRL", listeVentesPanel.getFilterAction(), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    @Override
    public void reload() {
        if (prodsVntsTabbedPane.getSelectedIndex() == 0) {
            listeProdsAVendre.reload();
        } else {
            if (prodsVntsTabbedPane.getSelectedIndex() == 1) {
                if (listeVentesPanel.getSelectedEntity() instanceof Vente && listeVentesPanel.getSelectedEntity().isValidee()) {
                    listeVentesPanel.reload();
                }
            }
        }
    }

    @Override
    public void doFilter() {
        if (majVentePanel.getDetailsVentePanel().isCBActive()) {
            majVentePanel.getDetailsVentePanel().cbField.requestFocus();
        } else {
            listeProdsAVendre.doFilter();
        }
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
        prodsVntsTabbedPane = new javax.swing.JTabbedPane();
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

        prodsVntsTabbedPane.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        prodsVntsTabbedPane.setFocusable(false);
        prodsVntsTabbedPane.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        prodsVntsTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                prodsVntsTabbedPaneStateChanged(evt);
            }
        });

        prodsPanel.setFocusable(false);
        prodsPanel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        prodsPanel.setLayout(new java.awt.BorderLayout());
        prodsVntsTabbedPane.addTab("Produits à Vendre (Ctrl+R)", new javax.swing.ImageIcon(getClass().getResource("/res/icons/prod24.png")), prodsPanel); // NOI18N

        vntsPanel.setFocusable(false);
        vntsPanel.setPreferredSize(new java.awt.Dimension(500, 400));
        vntsPanel.setLayout(new java.awt.BorderLayout());
        prodsVntsTabbedPane.addTab("Ventes Effectuées (Ctrl+T)", new javax.swing.ImageIcon(getClass().getResource("/res/icons/vente24.png")), vntsPanel); // NOI18N

        artSplitPane.setLeftComponent(prodsVntsTabbedPane);
        prodsVntsTabbedPane.getAccessibleContext().setAccessibleName("Liste des Articles à Vendre");

        majVntP.setBorder(javax.swing.BorderFactory.createTitledBorder(null, " Détails du Vente (Ctrl+F)", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 13))); // NOI18N
        majVntP.setFocusable(false);
        majVntP.setPreferredSize(new java.awt.Dimension(500, 28));
        majVntP.setLayout(new java.awt.BorderLayout());
        artSplitPane.setRightComponent(majVntP);

        add(artSplitPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void artSplitPanePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_artSplitPanePropertyChange
        if (evt.getPropertyName().equals(JSplitPane.DIVIDER_LOCATION_PROPERTY)) {
            savePreference(VNT_SPLIT_POS, artSplitPane.getDividerLocation());
        }
    }//GEN-LAST:event_artSplitPanePropertyChange

    private void prodsVntsTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_prodsVntsTabbedPaneStateChanged
        if (prodsVntsTabbedPane.getSelectedIndex() == 0) {
            listeProdsAVendre.reload();
        } else {
            listeVentesPanel.reload();
        }
    }//GEN-LAST:event_prodsVntsTabbedPaneStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSplitPane artSplitPane;
    private javax.swing.JPanel majVntP;
    private javax.swing.JPanel prodsPanel;
    private javax.swing.JTabbedPane prodsVntsTabbedPane;
    private javax.swing.JPanel vntsPanel;
    // End of variables declaration//GEN-END:variables

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                MyJFrame frame = new MyJFrame();
                frame.add(new GestVentesPanel());
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
