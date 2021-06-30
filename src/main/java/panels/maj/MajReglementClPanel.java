/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package panels.maj;

import entities.*;
import java.math.BigDecimal;
import javax.swing.JOptionPane;
import panels.ResultSet_Panel;
import panels.crud.ListeClientsPanel;
import dao.ReglementClDAO;
import java.awt.Component;
import java.util.Date;
import panels.CRUDPanel;
import tools.ExceptionReporting;

/**
 *
 * @author alilo
 */
public class MajReglementClPanel extends MajPanel<ReglementCl, ReglementClDAO> {

    private static final String DEF_MODE_PAYE = "MajReglCl_ModePaye";
    // Client fields
    private ListeClientsPanel clientsPanel;
    private Client client;
    private BigDecimal credit;
    // Vente
    private Vente vente;
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
    public MajReglementClPanel(CRUDPanel listPanel) {
        super(listPanel);
        initComponents();
        initMajPanel(fieldsPanel);
        initClientsPanel();
    }
    
    @Override
    public ReglementClDAO getTableDAO() {
        return ReglementClDAO.getInstance();
    }

    @Override
    public Component getDefaultFocusedComp() {
        return selClientPanel.getDescField();
    }

    @Override
    public void loadPreferences() {
        int defPaye = getIntPreference(DEF_MODE_PAYE, modePayComboBox.getSelectedIndex()+1);
        modePaye = new ModePaye(defPaye);
        modePayComboBox.setSelectedItem(modePaye);
    }

    @java.lang.Override
    public void initFields(ReglementCl oldEntity) {
        setFirstFocusedComp(montantField);
        //ReglementClDAO.getInstance().delete(oldEntity.getId());
        montant = oldEntity.getMontant();
        date = oldEntity.getDate();
        heure = oldEntity.getHeure();
        modePaye = oldEntity.getModePaye();
        commentaire = oldEntity.getComment();
        // UI
        montantField.setText(montant.toPlainString());
        modePayComboBox.setSelectedItem(modePaye);
        commentTextArea.setText(commentaire);

        setClient(oldEntity.getClient());
        setVente(oldEntity.getVente());
        addAction.setEnabled(oldEntity.getId() > 0);
    }

    @Override
    public void setChildEntity(EntityClass childEntity, boolean allowChng) {
        if (childEntity instanceof Client) {
            setClient((Client) childEntity);
            montantField.requestFocus();
            addAction.setEnabled(allowChng);
        }
        if (childEntity instanceof Vente) {
            setVente((Vente) childEntity);
        }

    }

    public void setVente(Vente v) {
        vente = v;
        if (vente != null) {
            commentTextArea.setEditable(false);
            //commentTextArea.setForeground(Color.BLACK);
            montantField.requestFocus();
            warningTextArea.setText("Attention!\nCe règlement sera supprimer automatiquement si vous annuler la vente!");
            warningTextArea.setVisible(true);
        }
    }

    public void setClient(Client c) {
        client = c;
        selClientPanel.setSelEntity(client);
        if (client != null) {
            initClientFields();
            calculateNouvSolde();
        } else {
            clearClient();
        }
    }

    public void initClientFields() {
        credit = client.getDette();
        if (isUpdate()) {// if is update add 'Montant' of 'Reglement' to old 'Client' 'Dettes'
            if (client.getId().intValue() == getOldEntity().getClient().getId()) {
                credit = credit.add(getOldEntity().getMontant());
            }
        }
    }

    public void clearClient() {
        warningTextArea.setVisible(false);
    }

    @Override
    public void setEditPermission(boolean allow) {
        super.setEditPermission(allow);
        montantField.setEnabled(allow);
    }

    /**
     * Call this method every time the 'Client' or 'Montant' changes.
     */
    public void calculateNouvSolde() {
        String montVers = montantField.getText().trim();
        if (montVers.length() == 0) {
            montant = new BigDecimal(0);
        } else {
            try {
                montant = new BigDecimal(montVers);
            } catch (Exception e) {
                ExceptionReporting.showException(e);
            }
        }
        BigDecimal nouvSolde = credit.subtract(montant);
        nouvSoldField.setText(nouvSolde.toPlainString());
    }

    @Override
    public void viewSelEntity(EntityClass entity) {
        if (entity instanceof Client) {
            setClient((Client) entity);
        } else {
            setClient(null);
        }
    }

    @Override
    public boolean verifyFields() {
        String versTxt = montantField.getText().trim();
        String commentTxt = commentTextArea.getText().trim();
        if (client == null || client.isAnonyme()) {
            JOptionPane.showMessageDialog(this, "Séléctionnez un Client SVP!!!", "Attention...", JOptionPane.WARNING_MESSAGE);
            selClientPanel.requestFocus();
            selClientPanel.showSelList();
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
            ReglementCl rv = new ReglementCl(0, date, heure, montant, modePaye, commentaire);
            rv.setClient(client);
            rv.setVente(vente);
            setEditedEntity(rv);
            return super.save();
        } else {
            setSaved(false);
            return false;
        }
    }

    @Override
    public void clearFields() {
        setClient(null);
        selClientPanel.requestFocus();
        //===============
        montant = null;
        date = null;
        heure = null;
        modePaye = null;
        commentaire = null;

        montantField.setText("");
        nouvSoldField.setText("");
        commentTextArea.setText("");
        commentTextArea.setEnabled(true);
    }

    private void initClientsPanel() {
        clientsPanel = new ListeClientsPanel(this, false);
        clientsPanel.getPropertyChangeSupport().addPropertyChangeListener(ResultSet_Panel.ENTITY_TO_VIEW_PROPERTY, this);
        selClientPanel.setSelListPanel(clientsPanel.getNavigNEditList());
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
        selClientPanel = new panels.SelectionPanel<Client>();
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
        jLabel6.setText("Client: ");
        clientPanel.add(jLabel6);

        selClientPanel.setBackground(new java.awt.Color(229, 229, 255));
        selClientPanel.setDescription("Sélectionner le client SVP!");
        selClientPanel.setFocusable(true);
        selClientPanel.setMinimumSize(new java.awt.Dimension(38, 25));
        selClientPanel.setPreferredSize(new java.awt.Dimension(44, 25));
        selClientPanel.setShortcutKey("F3");
        clientPanel.add(selClientPanel);

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
                .addComponent(commentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE))
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
    private panels.SelectionPanel<Client> selClientPanel;
    private javax.swing.JTextArea warningTextArea;
    // End of variables declaration//GEN-END:variables

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new MajReglementClPanel(null).showNewPanel(null);
            }
        });
    }
}
