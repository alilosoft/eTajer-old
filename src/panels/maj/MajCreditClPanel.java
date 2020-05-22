/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package panels.maj;

import dao.CreditClDAO;
import entities.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.*;
import java.math.BigDecimal;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import panels.ResultSet_Panel;
import panels.crud.ListeClientsPanel;
import java.awt.Dimension;
import java.util.Date;
import panels.CRUDPanel;
import tools.DateTools;
import tools.ExceptionReporting;

/**
 *
 * @author alilo
 */
public class MajCreditClPanel extends MajPanel<CreditCl, CreditClDAO> {

    // Client fields

    private ListeClientsPanel clientsPanel;
    private Client client;
    private BigDecimal dette;
    // Vente
    private Vente vente;
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
    public MajCreditClPanel(CRUDPanel listPanel) {
        super(listPanel);
        initComponents();
        initMajPanel(fieldsPanel);
        initClientsPanel();
    }

    @Override
    public CreditClDAO getTableDAO() {
        return CreditClDAO.getInstance();
    }

    @Override
    public Component getDefaultFocusedComp() {
        return selClientPanel.getDescField();
    }
    
    @java.lang.Override
    public void initFields(CreditCl oldEntity) {
        //ReglementClDAO.getInstance().delete(oldEntity.getId());
        montant = oldEntity.getMontant();
        date = oldEntity.getDate();
        heure = DateTools.getSqlTime(oldEntity.getHeure());
        commentaire = oldEntity.getComment();
        // UI
        montantField.setText(montant.toPlainString());
        commentTextArea.setText(commentaire);
        commentTextArea.setEditable(!oldEntity.isInitial());

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
            commentTextArea.setEnabled(false);
            montantField.requestFocus();
        }
    }

    public void setClient(Client c) {
        client = c;
        selClientPanel.setSelEntity(client);
        if (client != null) {
            initClientFields();
            calculateNouvSolde();
        }
    }

    public void initClientFields() {
        dette = client.getDette();
        if (isUpdate()) {// if is update sub 'Montant' of 'Credit' from old 'Client' 'Dette'
            if (client.equals(getOldEntity().getClient())) {
                dette = dette.subtract(getOldEntity().getMontant());
            }
        }
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
        String montCredit = montantField.getText().trim();
        if (montCredit.length() == 0) {
            montant = new BigDecimal(0);
        } else {
            try {
                montant = new BigDecimal(montCredit);
            } catch (Exception e) {
                ExceptionReporting.showException(e);
            }
        }
        BigDecimal nouvSolde = dette.add(montant);
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
        String creditTxt = montantField.getText().trim();
        String commentTxt = commentTextArea.getText().trim();
        if (client == null) {
            JOptionPane.showMessageDialog(this, "Séléctionnez un Client SVP!!!", "Attention...", JOptionPane.WARNING_MESSAGE);
            selClientPanel.requestFocus();
            selClientPanel.showSelList();
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
        if (verifyFields()) {
            CreditCl cc = new CreditCl(0, date, heure, montant, commentaire, false);
            cc.setClient(client);
            cc.setVente(vente);
            setEditedEntity(cc);
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
        commentaire = null;

        montantField.setText("");
        nouvSoldField.setText("");
        commentTextArea.setText("");
        commentTextArea.setEnabled(true);
    }

    private void initClientsPanel() {
        clientsPanel = new ListeClientsPanel(this, false) {
            {
                setPreferredSize(new Dimension(300, 400));
                getTable().setColumnVisible("Adresse", false);
                getTable().setColumnVisible("N° Tél", false);
                getTable().setColumnVisible("App.TVA", false);
            }
        }.initTableView();
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

        fieldsPanel = new javax.swing.JPanel();
        clientPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        selClientPanel = new panels.SelectionPanel<Client>();
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
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE))
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
    private panels.SelectionPanel<Client> selClientPanel;
    // End of variables declaration//GEN-END:variables

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new MajCreditClPanel(null).showNewPanel(null);
            }
        });
    }
}
