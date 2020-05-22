/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package panels.maj;

import dao.CreditFrDAO;
import entities.*;
import java.awt.Component;
import java.math.BigDecimal;
import javax.swing.JOptionPane;
import panels.ResultSet_Panel;
import java.util.Date;
import panels.CRUDPanel;
import panels.crud.ListeFournissPanel;

/**
 *
 * @author alilo
 */
public class MajCreditFrPanel extends MajPanel<CreditFr, CreditFrDAO> {

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
    private String commentaire;

    /**
     * Creates new form MajCategoriePanel
     *
     * @param listPanel
     */
    public MajCreditFrPanel(CRUDPanel listPanel) {
        super(listPanel);
        initComponents();
        initMajPanel(fieldsPanel);
        initFournissPanel();
    }

    @Override
    public CreditFrDAO getTableDAO() {
        return CreditFrDAO.getInstance();
    }

    @Override
    public Component getDefaultFocusedComp() {
        return selFournissPanel.getDescField();
    }

    @java.lang.Override
    public void initFields(CreditFr oldEntity) {
        montant = oldEntity.getMontant();
        date = oldEntity.getDate();
        heure = oldEntity.getHeure();
        commentaire = oldEntity.getComment();
        // UI
        montantField.setBigDecimalValue(montant);
        montantField.setEditable(!oldEntity.isInitial());

        commentTextArea.setText(commentaire);
        commentTextArea.setEditable(!oldEntity.isInitial());
        if(oldEntity.isInitial()){
            commentTextArea.append("\nVous ne pouvez pas modifier un crédit initial ici!");
        }
        

        setFourniss(oldEntity.getFournisseur());
        selFournissPanel.setAllowSelChange(!oldEntity.isInitial());

        setAchat(oldEntity.getAchat());
        addAction.setEnabled(oldEntity.getId() > 0);
        
                
    }

    @Override
    public void setChildEntity(EntityClass childEntity, boolean allowChng) {
        if (childEntity instanceof Fournisseur) {
            selFournissPanel.setAllowSelChange(allowChng);
            setFourniss((Fournisseur) childEntity);
            montantField.requestFocus();
            return;
        }
        if (childEntity instanceof Achat) {
            setAchat((Achat) childEntity);
        }
    }

    public void setAchat(Achat a) {
        achat = a;
        if (achat != null) {
            if (achat.getFournisseur() != null && !achat.getFournisseur().isAnonyme()) {
                fournissPanel.selectEntity(achat.getFournisseur());
                selFournissPanel.setAllowSelChange(false);
            }

            commentTextArea.setText("Crédit issu du l'" + achat);
            commentTextArea.setEditable(false);
            montantField.requestFocus();
        }
    }

    public void setFourniss(Fournisseur f) {
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
        if (isUpdate() && fournisseur.equals(getOldEntity().getFournisseur())) {// if is update sub 'Montant' of 'Credit' from old 'Fournisseur' 'Dette'
            oldCredit = oldCredit.subtract(getOldEntity().getMontant());
        }
        montant = montantField.getBigDecimalValue();
        //BigDecimal newSolde = oldCredit.add(montant);
        nouvSoldField.setBigDecimalValue(oldCredit.add(montant));
    }

    @Override
    public void viewSelEntity(EntityClass entity) {
        if (entity instanceof Fournisseur) {
            setFourniss((Fournisseur) entity);
        } else {
            setFourniss(null);
        }
    }

    @Override
    public boolean verifyFields() {
        String creditTxt = montantField.getText().trim();
        String commentTxt = commentTextArea.getText().trim();
        if (fournisseur == null) {
            JOptionPane.showMessageDialog(this, "Séléctionnez un Fournisseur SVP!!!", "Attention...", JOptionPane.WARNING_MESSAGE);
            selFournissPanel.requestFocus();
            selFournissPanel.showSelList();
            return false;
        }
        if (creditTxt.length() == 0) {
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
        commentaire = commentTextArea.getText().trim();
        return true;
    }

    @Override
    public boolean save() {
        if(isUpdate() && getOldEntity().isInitial()){
            return true; // no changes will be done if the current 'Crédit' is initial.
        }
        if (verifyFields()) {
            CreditFr cc = new CreditFr(0, date, heure, montant, commentaire, false);
            cc.setFournisseur(fournisseur);
            cc.setAchat(achat);
            setEditedEntity(cc);
            return super.save();
        } else {
            setSaved(false);
            return false;
        }
    }

    @Override
    public void clearFields() {
        setFourniss(null);
        selFournissPanel.setAllowSelChange(true);
        // Vars
        montant = null;
        date = null;
        heure = null;
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

        fieldsPanel = new javax.swing.JPanel();
        clientPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        selFournissPanel = new panels.SelectionPanel<Fournisseur>();
        jSeparator3 = new javax.swing.JSeparator();
        montantPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        montantField = new myComponents.CurrencyField();
        currencyUniteLabel1 = new myComponents.CurrencyUniteLabel();
        jLabel10 = new javax.swing.JLabel();
        nouvSoldField = new myComponents.CurrencyField();
        currencyUniteLabel2 = new myComponents.CurrencyUniteLabel();
        jPanel1 = new javax.swing.JPanel();
        commentTextArea = new javax.swing.JTextArea();

        fieldsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        fieldsPanel.setMaximumSize(new java.awt.Dimension(296, 222));
        fieldsPanel.setMinimumSize(new java.awt.Dimension(296, 222));

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
        jLabel4.setText("Montant.Crédit:");
        montantPanel.add(jLabel4);

        montantField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                montantFieldKeyReleased(evt);
            }
        });
        montantPanel.add(montantField);
        montantPanel.add(currencyUniteLabel1);

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel10.setText("  Nouveau.Solde:");
        montantPanel.add(jLabel10);

        nouvSoldField.setEditable(false);
        nouvSoldField.setBackground(new java.awt.Color(229, 229, 255));
        nouvSoldField.setFocusable(false);
        nouvSoldField.setRequestFocusEnabled(false);
        montantPanel.add(nouvSoldField);
        montantPanel.add(currencyUniteLabel2);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, " Commentaire ", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        jPanel1.setLayout(new java.awt.BorderLayout());

        commentTextArea.setColumns(20);
        commentTextArea.setFont(new java.awt.Font("Tahoma", 2, 14)); // NOI18N
        commentTextArea.setLineWrap(true);
        commentTextArea.setRows(5);
        commentTextArea.setWrapStyleWord(true);
        jPanel1.add(commentTextArea, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout fieldsPanelLayout = new javax.swing.GroupLayout(fieldsPanel);
        fieldsPanel.setLayout(fieldsPanelLayout);
        fieldsPanelLayout.setHorizontalGroup(
            fieldsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(clientPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSeparator3)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(montantPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        fieldsPanelLayout.setVerticalGroup(
            fieldsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fieldsPanelLayout.createSequentialGroup()
                .addComponent(clientPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(montantPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE))
        );

        setLayout(null);
    }// </editor-fold>//GEN-END:initComponents

    private void montantFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_montantFieldKeyReleased
        calculateNouvSolde();
    }//GEN-LAST:event_montantFieldKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel clientPanel;
    private javax.swing.JTextArea commentTextArea;
    private myComponents.CurrencyUniteLabel currencyUniteLabel1;
    private myComponents.CurrencyUniteLabel currencyUniteLabel2;
    private javax.swing.JPanel fieldsPanel;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator3;
    private myComponents.CurrencyField montantField;
    private javax.swing.JPanel montantPanel;
    private myComponents.CurrencyField nouvSoldField;
    private panels.SelectionPanel<Fournisseur> selFournissPanel;
    // End of variables declaration//GEN-END:variables

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new MajCreditFrPanel(null).showNewPanel(null);
            }
        });
    }
}
