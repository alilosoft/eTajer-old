/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package panels.maj;

import entities.*;
import java.math.BigDecimal;
import javax.swing.JOptionPane;
import panels.ResultSet_Panel;
import dao.ReglementFrDAO;
import java.awt.Component;
import java.util.Date;
import panels.CRUDPanel;
import panels.crud.ListeFournissPanel;

/**
 *
 * @author alilo
 */
public class MajReglementFrPanel extends MajPanel<ReglementFr, ReglementFrDAO> {

    private static final String DEF_MODE_PAYE = "MajReglFr_ModePaye";
    private String currencyUnite;
    // Client fields
    private ListeFournissPanel fournissPanel;
    private Fournisseur fournisseur;
    private BigDecimal oldCredit;
    // Vente
    private Achat achat;
    // Paiement fields
    private Date date;
    private Date heure;
    private BigDecimal montant;
    private ModePaye modePaye;
    private String commentaire;

    /**
     * Creates new form MajCategoriePanel
     *
     * @param listPanel
     */
    public MajReglementFrPanel(CRUDPanel listPanel) {
        super(listPanel);
        initComponents();
        initMajPanel(fieldsPanel);
        initFournissPanel();
    }
    
    @Override
    public ReglementFrDAO getTableDAO() {
        return ReglementFrDAO.getInstance();
    }

    @Override
    public Component getDefaultFocusedComp() {
        return selFournissPanel.getDescField();
    }

    @Override
    public void loadPreferences() {
        int defPaye = getIntPreference(DEF_MODE_PAYE, modePayComboBox.getSelectedIndex()+1);
        modePaye = new ModePaye(defPaye);
        modePayComboBox.setSelectedItem(modePaye);
    }

    @java.lang.Override
    public void initFields(ReglementFr oldEntity) {
        setFirstFocusedComp(montantField);
        montant = oldEntity.getMontant();
        date = oldEntity.getDate();
        heure = oldEntity.getHeure();
        modePaye = oldEntity.getModePaye();
        commentaire = oldEntity.getComment();
        // UI
        montantField.setText(montant.toPlainString());
        modePayComboBox.setSelectedItem(modePaye);
        commentTextArea.setText(commentaire);
        setFournisseur(oldEntity.getFournisseur());
        setAchat(oldEntity.getAchat());
        addAction.setEnabled(oldEntity.getId() > 0);
    }

    @Override
    public void setChildEntity(EntityClass childEntity, boolean allowChng) {
        if (childEntity instanceof Fournisseur) {
            selFournissPanel.setAllowSelChange(allowChng);
            setFournisseur((Fournisseur) childEntity);
            montantField.requestFocus();
            return;
        }
        if (childEntity instanceof Achat) {
            setAchat((Achat) childEntity);
        }
    }

    public void setAchat(Achat v) {
        achat = v;
        if (achat != null) {
            if (achat.getFournisseur() != null && !achat.getFournisseur().isAnonyme()) {
                fournissPanel.selectEntity(achat.getFournisseur());
                selFournissPanel.setAllowSelChange(false);
            }
            warningTextArea.setText("Attention!\nCe règlement sera supprimer automatiquement si vous annuler l'achat!");
            warningTextArea.setVisible(true);
            commentTextArea.setText("Réglement du l'" + achat);
            commentTextArea.setEditable(false);
            montantField.requestFocus();
        }
    }

    public void setFournisseur(Fournisseur f) {
        fournisseur = f;
        selFournissPanel.setSelEntity(f);
        calculateNouvSolde();
    }

    @Override
    public void setEditPermission(boolean allow) {
        super.setEditPermission(allow);
        selFournissPanel.setEnabled(allow);
        montantField.setEnabled(allow);
        commentTextArea.setEnabled(allow);
    }

    public void calculateNouvSolde() {
        if (fournisseur == null) {
            nouvSoldField.setText("");
            return;
        }
        oldCredit = fournisseur.getDette();
        if (isUpdate() && fournisseur.equals(getOldEntity().getFournisseur())) {
            oldCredit = oldCredit.add(getOldEntity().getMontant());
        }
        
        montant = montantField.getBigDecimalValue();
        nouvSoldField.setBigDecimalValue(oldCredit.subtract(montant));
    }

    @Override
    public void viewSelEntity(EntityClass entity) {
        if (entity instanceof Fournisseur) {
            setFournisseur((Fournisseur) entity);
        } else {
            setFournisseur(null);
        }
    }

    @Override
    public boolean verifyFields() {
        String versTxt = montantField.getText().trim();
        String commentTxt = commentTextArea.getText().trim();
        if (fournisseur == null) {
            JOptionPane.showMessageDialog(this, "Séléctionnez un Fournisseur SVP!!!", "Attention...", JOptionPane.WARNING_MESSAGE);
            selFournissPanel.requestFocus();
            selFournissPanel.showSelList();
            return false;
        }
        if (versTxt.length() == 0) {
            JOptionPane.showMessageDialog(this, "Entrez le montant versé  SVP!!!", "Attention...", JOptionPane.WARNING_MESSAGE);
            montantField.requestFocus();
            return false;
        }
        if (commentTxt.length() == 0) {
            int rep = JOptionPane.showConfirmDialog(this, "Aucun commentaire entré!  Continuer sans commentaire?", "Attention...", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (rep == JOptionPane.NO_OPTION) {
                commentTextArea.requestFocus();
                return false;
            }
        }
        date = new Date();
        heure = date;
        modePaye = new ModePaye(modePayComboBox.getSelectedIndex()+1);
        commentaire = commentTextArea.getText().trim();
        return true;
    }

    @Override
    public void savePreferences() {
        savePreference(DEF_MODE_PAYE, modePaye.getId());
    }

    @Override
    public boolean save() {
        if (verifyFields()) {
            ReglementFr rf = new ReglementFr(0, date, heure, montant, modePaye, commentaire);
            rf.setFournisseur(fournisseur);
            rf.setAchat(achat);
            setEditedEntity(rf);
            return super.save();
        } else {
            setSaved(false);
            return false;
        }
    }

    @Override
    public void clearFields() {
        setFournisseur(null);
        selFournissPanel.setAllowSelChange(true);
        //Values
        montant = null;
        date = null;
        heure = null;
        modePaye = null;
        commentaire = null;
        // UI
        montantField.setText("");
        montantField.setEditable(true);
        nouvSoldField.setText("");
        commentTextArea.setText("");
        commentTextArea.setEnabled(true);
    }

    private void initFournissPanel() {
        fournissPanel = new ListeFournissPanel(this, false).getNavigNEditList();
        fournissPanel.getPropertyChangeSupport().addPropertyChangeListener(ResultSet_Panel.ENTITY_TO_VIEW_PROPERTY, this);
        selFournissPanel.setSelListPanel(fournissPanel);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        fieldsPanel = new javax.swing.JPanel();
        clientPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        selFournissPanel = new panels.SelectionPanel<Fournisseur>();
        jSeparator3 = new javax.swing.JSeparator();
        montantPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        montantField = new myComponents.CurrencyField();
        currencyUniteLabel1 = new myComponents.CurrencyUniteLabel();
        newSoldePanel = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        nouvSoldField = new myComponents.CurrencyField();
        currencyUniteLabel2 = new myComponents.CurrencyUniteLabel();
        modePayPanel = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        modePayComboBox = new javax.swing.JComboBox();
        commentPanel = new javax.swing.JPanel();
        commentTextArea = new javax.swing.JTextArea();
        warningTextArea = new javax.swing.JTextArea();

        fieldsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));

        clientPanel.setLayout(new javax.swing.BoxLayout(clientPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel6.setText("Fournisseur: ");
        clientPanel.add(jLabel6);

        selFournissPanel.setDescription("Séléctionner le fournisseur SVP!");
        selFournissPanel.setShortcutKey("F3");
        clientPanel.add(selFournissPanel);

        montantPanel.setPreferredSize(new java.awt.Dimension(150, 25));
        montantPanel.setLayout(new javax.swing.BoxLayout(montantPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel4.setText("Montant.Payé:");
        jLabel4.setMinimumSize(new java.awt.Dimension(90, 16));
        jLabel4.setPreferredSize(new java.awt.Dimension(90, 17));
        montantPanel.add(jLabel4);

        montantField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                montantFieldKeyReleased(evt);
            }
        });
        montantPanel.add(montantField);
        montantPanel.add(currencyUniteLabel1);

        newSoldePanel.setLayout(new javax.swing.BoxLayout(newSoldePanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel10.setText("Nouveau.Solde:");
        newSoldePanel.add(jLabel10);

        nouvSoldField.setEditable(false);
        nouvSoldField.setBackground(new java.awt.Color(229, 229, 255));
        nouvSoldField.setFocusable(false);
        nouvSoldField.setRequestFocusEnabled(false);
        newSoldePanel.add(nouvSoldField);
        newSoldePanel.add(currencyUniteLabel2);

        modePayPanel.setPreferredSize(new java.awt.Dimension(520, 25));
        modePayPanel.setLayout(new javax.swing.BoxLayout(modePayPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel9.setText("Payement.Par:");
        modePayPanel.add(jLabel9);

        modePayComboBox.setBackground(new java.awt.Color(229, 229, 255));
        modePayComboBox.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        modePayComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Espèce", "Chèque" }));
        modePayPanel.add(modePayComboBox);

        commentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, " Commentaire ", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        commentPanel.setLayout(new java.awt.BorderLayout());

        commentTextArea.setColumns(20);
        commentTextArea.setFont(new java.awt.Font("Tahoma", 2, 14)); // NOI18N
        commentTextArea.setForeground(new java.awt.Color(0, 0, 0));
        commentTextArea.setLineWrap(true);
        commentTextArea.setRows(5);
        commentTextArea.setWrapStyleWord(true);
        commentPanel.add(commentTextArea, java.awt.BorderLayout.CENTER);

        warningTextArea.setEditable(false);
        warningTextArea.setColumns(20);
        warningTextArea.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        warningTextArea.setForeground(new java.awt.Color(255, 0, 0));
        warningTextArea.setLineWrap(true);
        warningTextArea.setRows(2);
        warningTextArea.setText("Attention!     ");
        warningTextArea.setWrapStyleWord(true);
        commentPanel.add(warningTextArea, java.awt.BorderLayout.NORTH);

        javax.swing.GroupLayout fieldsPanelLayout = new javax.swing.GroupLayout(fieldsPanel);
        fieldsPanel.setLayout(fieldsPanelLayout);
        fieldsPanelLayout.setHorizontalGroup(
            fieldsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(clientPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSeparator3)
            .addComponent(commentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, fieldsPanelLayout.createSequentialGroup()
                .addGroup(fieldsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(newSoldePanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(montantPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(modePayPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        fieldsPanelLayout.setVerticalGroup(
            fieldsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fieldsPanelLayout.createSequentialGroup()
                .addComponent(clientPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(fieldsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(montantPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(modePayPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(newSoldePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(commentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE))
        );

        setLayout(null);
    }// </editor-fold>//GEN-END:initComponents

    private void montantFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_montantFieldKeyReleased
        calculateNouvSolde();
    }//GEN-LAST:event_montantFieldKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel clientPanel;
    private javax.swing.JPanel commentPanel;
    private javax.swing.JTextArea commentTextArea;
    private myComponents.CurrencyUniteLabel currencyUniteLabel1;
    private myComponents.CurrencyUniteLabel currencyUniteLabel2;
    private javax.swing.JPanel fieldsPanel;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JComboBox modePayComboBox;
    private javax.swing.JPanel modePayPanel;
    private myComponents.CurrencyField montantField;
    private javax.swing.JPanel montantPanel;
    private javax.swing.JPanel newSoldePanel;
    private myComponents.CurrencyField nouvSoldField;
    private panels.SelectionPanel<Fournisseur> selFournissPanel;
    private javax.swing.JTextArea warningTextArea;
    // End of variables declaration//GEN-END:variables

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new MajReglementFrPanel(null).showNewPanel(null);
            }
        });
    }
}
