/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package panels.maj;

import java.awt.Component;
import java.awt.Toolkit;
import java.math.BigDecimal;
import javax.swing.JOptionPane;
import dao.CompanyDAO;
import entities.Company;
import panels.CRUDPanel;

/**
 *
 * @author alilo
 */
public class MajCompanyPanel extends panels.maj.MajPanel<Company, CompanyDAO> {

    private String company = "eTajer Version Demo";
    private String activity = "";
    private String adr = "";
    private String email = "";
    private String site = "";
    private String tel = "";
    private String fax = "";
    private String numRc = "";
    private String numFisc = "";
    private String numArt = "";
    private BigDecimal capital = new BigDecimal(0);

    /**
     * Creates new form MajFamillePanel
     *
     * @param listPanel
     */
    public MajCompanyPanel(CRUDPanel listPanel) {
        super(listPanel);
        initComponents();
        initMajPanel(fieldsPanel);
        //add(mainTabbedPane, BorderLayout.CENTER);
        addAction.setEnabled(false);
    }

    @Override
    public CompanyDAO getTableDAO() {
        return CompanyDAO.getInstance();
    }

    @Override
    public Component getDefaultFocusedComp() {
        return compField;
    }

    @java.lang.Override
    public void initFields(Company oldEntity) {
        compField.setText(oldEntity.getCompany());
        activField.setText(oldEntity.getActivity());
        adrField.setText(oldEntity.getAdresse());
        telField.setText(oldEntity.getTel());
        faxField.setText(oldEntity.getFax());
        emailField.setText(oldEntity.getEmail());
        siteField.setText(oldEntity.getSite());
        rcField.setText(oldEntity.getNumRc());
        fiscField.setText(oldEntity.getNumFisc());
        artField.setText(oldEntity.getNumArt());
        capital = oldEntity.getCapital();
        capitalField.setBigDecimalValue(capital);
    }

    @Override
    public void clearFields() {
        compField.requestFocus();
        company = "";
        compField.setText("");
        activity = "";
        activField.setText("");
        adr = "";
        adrField.setText("");
        tel = "";
        telField.setText("");
        fax = "";
        faxField.setText("");
        email = "";
        emailField.setText("");
        site = "";
        siteField.setText("");
        numRc = "";
        rcField.setText("");
        numFisc = "";
        fiscField.setText("");
        numArt = "";
        artField.setText("");
        capital = new BigDecimal(0);
        capitalField.setText("0.00");
    }

    @Override
    public boolean verifyFields() {
        String compTxt = compField.getText().trim();
        String activTxt = activField.getText().trim();
        String adrTxt = adrField.getText().trim();
        String telTxt = telField.getText().trim();
        String faxTxt = faxField.getText().trim();
        String emailTxt = emailField.getText().trim();
        String siteTxt = siteField.getText().trim();
        String rcTxt = rcField.getText().trim();
        String fisclTxt = fiscField.getText().trim();
        String artTxt = artField.getText().trim();

        if (compTxt.length() == 0) {
            JOptionPane.showMessageDialog(null, "Vous devez donner un nom du societé!", "Attention...", JOptionPane.ERROR_MESSAGE);
            compField.requestFocus();
            compField.selectAll();
            return false;
        }

        if (telTxt.length() != 0 & telTxt.length() < 9) {
            JOptionPane.showMessageDialog(this, "Numéro du téléphone incorrect!!!\n",
                    "Attention...", JOptionPane.ERROR_MESSAGE);
            telField.requestFocus();
            telField.selectAll();
            return false;
        }

        if (faxTxt.length() != 0 & faxTxt.length() != 9) {
            JOptionPane.showMessageDialog(this, "Numéro du fax incorrect!!!\n",
                    "Attention...", JOptionPane.ERROR_MESSAGE);
            telField.requestFocus();
            telField.selectAll();
            return false;
        }

        if (emailTxt.length() != 0) {
            if ((emailTxt.contains("@") & emailTxt.contains(".") & emailTxt.indexOf("@") < emailTxt.lastIndexOf("."))) {
            } else {
                JOptionPane.showMessageDialog(this, "Adresse E-Mail incorrect!",
                        "Attention...", JOptionPane.ERROR_MESSAGE);
                emailField.requestFocus();
                emailField.selectAll();
                return false;
            }
        }
        
        this.company = compTxt.toUpperCase();
        this.activity = activTxt;
        this.adr = adrTxt;
        this.tel = telTxt;
        this.fax = faxTxt;
        this.email = emailTxt.toLowerCase();
        this.site = siteTxt.toLowerCase();
        this.numRc = rcTxt;
        this.numFisc = fisclTxt;
        this.numArt = artTxt;
        this.capital = capitalField.getBigDecimalValue();
        return true;
    }

    @Override
    public boolean save() {
        if (verifyFields()) {
            Company comp = new Company(0, company, activity, adr, email, site, tel, fax, numRc, numFisc, numArt, capital);
            setEditedEntity(comp);
            return super.save();
        } else {
            setSaved(false);
            return false;
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

        mainTabbedPane = new javax.swing.JTabbedPane();
        fieldsPanel = new javax.swing.JPanel();
        nomPanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        compField = new myComponents.MyJField();
        actPanel = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        activField = new myComponents.MyJField();
        adrPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        adrField = new myComponents.MyJField();
        telPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        telField = new myComponents.IntegerJField();
        jLabel10 = new javax.swing.JLabel();
        faxField = new myComponents.IntegerJField();
        emailPanel = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        emailField = new myComponents.MyJField();
        sitePanel = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        siteField = new myComponents.MyJField();
        jToolBar3 = new javax.swing.JToolBar();
        numRCPanel = new javax.swing.JPanel();
        rcLabel = new javax.swing.JLabel();
        rcField = new myComponents.MyJField();
        idFiscalPanel = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        fiscField = new myComponents.IntegerJField();
        numArtPanel = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        artField = new myComponents.IntegerJField();
        jToolBar1 = new javax.swing.JToolBar();
        creditInitPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        capitalField = new myComponents.CurrencyField();
        currencyUniteLabel1 = new myComponents.CurrencyUniteLabel();

        mainTabbedPane.setFocusable(false);

        fieldsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));

        nomPanel.setLayout(new javax.swing.BoxLayout(nomPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel5.setText("Nom:");
        jLabel5.setPreferredSize(new java.awt.Dimension(55, 15));
        nomPanel.add(jLabel5);

        compField.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        compField.setPrefsLangKey("MajCl_Nom_Lang");
        compField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                compFieldMouseClicked(evt);
            }
        });
        nomPanel.add(compField);

        actPanel.setLayout(new javax.swing.BoxLayout(actPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel13.setText("Activité:");
        jLabel13.setPreferredSize(new java.awt.Dimension(55, 15));
        actPanel.add(jLabel13);

        activField.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        activField.setPrefsLangKey("MajCl_Nom_Lang");
        actPanel.add(activField);

        adrPanel.setLayout(new javax.swing.BoxLayout(adrPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel3.setText("Adresse:");
        jLabel3.setPreferredSize(new java.awt.Dimension(55, 15));
        adrPanel.add(jLabel3);

        adrField.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        adrField.setPrefsLangKey("MAjCl_Adr_Lang");
        adrPanel.add(adrField);

        telPanel.setLayout(new javax.swing.BoxLayout(telPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel4.setText("N° Tél:");
        jLabel4.setPreferredSize(new java.awt.Dimension(55, 15));
        telPanel.add(jLabel4);

        telField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                telFieldKeyTyped(evt);
            }
        });
        telPanel.add(telField);

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel10.setText("  N°  Fax:");
        telPanel.add(jLabel10);

        faxField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                faxFieldKeyTyped(evt);
            }
        });
        telPanel.add(faxField);

        emailPanel.setLayout(new javax.swing.BoxLayout(emailPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel12.setText("E-Mail:");
        jLabel12.setPreferredSize(new java.awt.Dimension(55, 15));
        emailPanel.add(jLabel12);

        emailField.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        emailField.setPrefsLangKey("MAjCl_Adr_Lang");
        emailPanel.add(emailField);

        sitePanel.setLayout(new javax.swing.BoxLayout(sitePanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel14.setText("SiteWeb:");
        jLabel14.setPreferredSize(new java.awt.Dimension(55, 15));
        sitePanel.add(jLabel14);

        siteField.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        siteField.setPrefsLangKey("MAjCl_Adr_Lang");
        sitePanel.add(siteField);

        jToolBar3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jToolBar3.setFloatable(false);
        jToolBar3.setRollover(true);

        numRCPanel.setLayout(new javax.swing.BoxLayout(numRCPanel, javax.swing.BoxLayout.LINE_AXIS));

        rcLabel.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        rcLabel.setText("N° R.C:");
        rcLabel.setPreferredSize(new java.awt.Dimension(55, 15));
        numRCPanel.add(rcLabel);

        rcField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        rcField.setDefLocaleStr("fr_FR");
        rcField.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        rcField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                rcFieldKeyTyped(evt);
            }
        });
        numRCPanel.add(rcField);

        idFiscalPanel.setLayout(new javax.swing.BoxLayout(idFiscalPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel8.setText("ID.FISC:");
        jLabel8.setPreferredSize(new java.awt.Dimension(55, 15));
        idFiscalPanel.add(jLabel8);

        fiscField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        fiscField.setDefLocaleStr("fr_FR");
        fiscField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fiscFieldKeyTyped(evt);
            }
        });
        idFiscalPanel.add(fiscField);

        numArtPanel.setLayout(new javax.swing.BoxLayout(numArtPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel9.setText("N° ART:");
        jLabel9.setPreferredSize(new java.awt.Dimension(55, 15));
        numArtPanel.add(jLabel9);

        artField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        artField.setDefLocaleStr("fr_FR");
        artField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                artFieldKeyTyped(evt);
            }
        });
        numArtPanel.add(artField);

        jToolBar1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        creditInitPanel.setLayout(new javax.swing.BoxLayout(creditInitPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel6.setText("Capital:");
        jLabel6.setPreferredSize(new java.awt.Dimension(55, 15));
        creditInitPanel.add(jLabel6);

        capitalField.setPreferredSize(new java.awt.Dimension(65, 27));
        creditInitPanel.add(capitalField);
        creditInitPanel.add(currencyUniteLabel1);

        javax.swing.GroupLayout fieldsPanelLayout = new javax.swing.GroupLayout(fieldsPanel);
        fieldsPanel.setLayout(fieldsPanelLayout);
        fieldsPanelLayout.setHorizontalGroup(
            fieldsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fieldsPanelLayout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(fieldsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nomPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(actPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(adrPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(telPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
                    .addComponent(emailPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(sitePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(fieldsPanelLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(fieldsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(fieldsPanelLayout.createSequentialGroup()
                                .addComponent(creditInitPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jToolBar3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(numArtPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(numRCPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(idFiscalPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
        );
        fieldsPanelLayout.setVerticalGroup(
            fieldsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, fieldsPanelLayout.createSequentialGroup()
                .addComponent(nomPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(actPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(adrPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(telPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(emailPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(sitePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(numRCPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(idFiscalPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(numArtPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(creditInitPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        mainTabbedPane.addTab("Societé", fieldsPanel);

        setLayout(null);
    }// </editor-fold>//GEN-END:initComponents

    private void artFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_artFieldKeyTyped
        if (artField.getText().length() >= 25 && artField.getSelectionStart() == artField.getSelectionEnd()) {
            evt.consume();
            Toolkit.getDefaultToolkit().beep();
        }
    }//GEN-LAST:event_artFieldKeyTyped

    private void fiscFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fiscFieldKeyTyped
        if (fiscField.getText().length() >= 25 && fiscField.getSelectionStart() == fiscField.getSelectionEnd()) {
            evt.consume();
            Toolkit.getDefaultToolkit().beep();
        }
    }//GEN-LAST:event_fiscFieldKeyTyped

    private void rcFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rcFieldKeyTyped
        if (rcField.getText().length() >= 25 && rcField.getSelectionStart() == rcField.getSelectionEnd()) {
            evt.consume();
            Toolkit.getDefaultToolkit().beep();
        }
    }//GEN-LAST:event_rcFieldKeyTyped

    private void faxFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_faxFieldKeyTyped
        if (faxField.getText().length() >= 9 && faxField.getSelectionStart() == faxField.getSelectionEnd()) {
            evt.consume();
            Toolkit.getDefaultToolkit().beep();
        }
    }//GEN-LAST:event_faxFieldKeyTyped

    private void telFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_telFieldKeyTyped
        if (telField.getText().length() >= 10 && telField.getSelectionStart() == telField.getSelectionEnd()) {
            evt.consume();
            Toolkit.getDefaultToolkit().beep();
        }
    }//GEN-LAST:event_telFieldKeyTyped

    private void compFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_compFieldMouseClicked
        //compField.setEditable(evt.getClickCount() == 5);
    }//GEN-LAST:event_compFieldMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel actPanel;
    private myComponents.MyJField activField;
    private myComponents.MyJField adrField;
    private javax.swing.JPanel adrPanel;
    private myComponents.IntegerJField artField;
    private myComponents.CurrencyField capitalField;
    private myComponents.MyJField compField;
    private javax.swing.JPanel creditInitPanel;
    private myComponents.CurrencyUniteLabel currencyUniteLabel1;
    private myComponents.MyJField emailField;
    private javax.swing.JPanel emailPanel;
    private myComponents.IntegerJField faxField;
    private javax.swing.JPanel fieldsPanel;
    private myComponents.IntegerJField fiscField;
    private javax.swing.JPanel idFiscalPanel;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JTabbedPane mainTabbedPane;
    private javax.swing.JPanel nomPanel;
    private javax.swing.JPanel numArtPanel;
    private javax.swing.JPanel numRCPanel;
    private myComponents.MyJField rcField;
    private javax.swing.JLabel rcLabel;
    private myComponents.MyJField siteField;
    private javax.swing.JPanel sitePanel;
    private myComponents.IntegerJField telField;
    private javax.swing.JPanel telPanel;
    // End of variables declaration//GEN-END:variables

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new MajCompanyPanel(null).showEditPanel(CompanyDAO.getInstance().getObjectByID(1));
            }
        });
    }
}
