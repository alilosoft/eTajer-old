/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package panels.maj;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Toolkit;
import java.math.BigDecimal;
import javax.swing.JOptionPane;
import dao.FournissDAO;
import entities.CreditFr;
import entities.Fournisseur;
import panels.CRUDPanel;

/**
 *
 * @author alilo
 */
public class MajFournisseurPanel extends panels.maj.MajPanel<Fournisseur, FournissDAO> {

    private String code;
    private String nom;
    private String adr = "";
    private String tel = "";
    private String mobile = "";
    private String email = "";
    private String numRc = "";
    private String numFisc = "";
    private String numArt = "";
    private BigDecimal creditInitial = new BigDecimal(0);
    private BigDecimal creditTotal = new BigDecimal(0);

    /**
     * @param listPanel
     */
    public MajFournisseurPanel(CRUDPanel listPanel) {
        super(listPanel);
        initComponents();
        initMajPanel(mainTabbedPane);
        add(mainTabbedPane, BorderLayout.CENTER);
    }

    // Getters
    @Override
    public FournissDAO getTableDAO() {
        return FournissDAO.getInstance();
    }

    @Override
    public Component getDefaultFocusedComp() {
        return codeField;
    }

    @java.lang.Override
    public void initFields(Fournisseur oldEntity) {
        codeField.setText(oldEntity.getCode());
        nomField.setText(oldEntity.getNom());
        adrField.setText(oldEntity.getAdr());
        telField.setText(oldEntity.getTel());
        mobileField.setText(oldEntity.getMobile());
        emailField.setText(oldEntity.getEmail());
        rcField.setText(oldEntity.getNumRc());
        fiscField.setText(oldEntity.getNumFisc());
        artField.setText(oldEntity.getNumArt());
        creditInitial = oldEntity.getCreditInitial().getMontant();
        creditInitField.setBigDecimalValue(creditInitial);
        creditTotal = oldEntity.getDette();
        creditTotalField.setBigDecimalValue(creditTotal);

    }

    @Override
    public void clearFields() {
        codeField.requestFocus();
        codeField.setText("");
        code = "";
        nomField.setText(""); 
        nom = "";
        adrField.setText("");
        adr = "";
        telField.setText("");
        tel = "";
        mobileField.setText("");
        mobile = "";
        emailField.setText("");
        email = "";
        rcField.setText("");
        numRc = "";
        fiscField.setText("");
        numFisc = "";
        artField.setText("");
        numArt = "";
        creditTotalField.setText("0.00");
        creditTotal = new BigDecimal(0);
        creditInitField.setText("0.00");
        creditInitial = new BigDecimal(0);
    }

    @Override
    public boolean verifyFields() {
        String codeTxt = codeField.getText().trim();
        String nomTxt = nomField.getText().trim();
        String adrTxt = adrField.getText().trim();
        String telTxt = telField.getText().trim();
        String mobTxt = mobileField.getText().trim();
        String emailTxt = emailField.getText().trim();
        String rcTxt = rcField.getText().trim();
        String fisclTxt = fiscField.getText().trim();
        String artTxt = artField.getText().trim();
        String creditInitTxt = creditInitField.getText().trim();

        if (codeTxt.length() == 0) {
            JOptionPane.showMessageDialog(this, "Vous devez donner un code ou un numéro a votre fournisseur!!!\n"
                    + "Remarque: Ce code sera vous aider a trouver ce fournisseur facilement.", "Attention...", JOptionPane.ERROR_MESSAGE);
            codeField.requestFocus();
            codeField.selectAll();
            return false;
        }

        if (nomTxt.length() == 0) {
            JOptionPane.showMessageDialog(this, "Vous devez entrer un nom pour le client!!!",
                    "Attention...", JOptionPane.WARNING_MESSAGE);
            nomField.requestFocus();
            return false;
        }

        if (telTxt.length() != 0 & telTxt.length() != 9) {
            JOptionPane.showMessageDialog(this, "Numéro du téléphone fixe/fax incorrect!!!\n"
                    + "Le Numéro du téléphone fixe doit comport 9 chifres sinon vide",
                    "Attention...", JOptionPane.ERROR_MESSAGE);
            telField.requestFocus();
            telField.selectAll();
            return false;
        }

        if (mobTxt.length() != 0 & mobTxt.length() != 10) {
            JOptionPane.showMessageDialog(this, "Numéro du téléphone portable incorrect!!!\n"
                    + "Le Numéro du téléphone portable doit comport 10 chifres sinon vide",
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

        if (rcTxt.length() < 10 & rcTxt.length() != 0) {
            JOptionPane.showMessageDialog(null, "Numéro de registre incorrect!!!\n"
                    + "Le Numéro du registre doit comport 10 caractères sinon vide",
                    "Attention...", JOptionPane.ERROR_MESSAGE);
            rcField.requestFocus();
            rcField.selectAll();
            return false;
        }

        if (fisclTxt.length() < 15 & fisclTxt.length() != 0) {
            JOptionPane.showMessageDialog(null, "ID.Fiscal incorrect!!!\n"
                    + "L'ID fiscal doit comport 15 caractères sinon vide",
                    "Attention...", JOptionPane.ERROR_MESSAGE);
            fiscField.requestFocus();
            fiscField.selectAll();
            return false;
        }

        if (artTxt.length() < 11 & artTxt.length() != 0) {
            JOptionPane.showMessageDialog(null, "Numéro d'article incorrect!!!\n"
                    + "Le Numéro d'article doit comport 11 caractères sinon vide",
                    "Attention...", JOptionPane.ERROR_MESSAGE);
            artField.requestFocus();
            artField.selectAll();
            return false;
        }

        if (artTxt.length() < 11 & artTxt.length() != 0) {
            JOptionPane.showMessageDialog(null, "Numéro d'article incorrect!!!\n"
                    + "Le Numéro d'article doit comport 11 caractères sinon vide",
                    "Attention...", JOptionPane.ERROR_MESSAGE);
            artField.requestFocus();
            artField.selectAll();
            return false;
        }

        if (creditInitTxt.length() == 0) {
            JOptionPane.showMessageDialog(null, "Entrez le crédit  initial SVP!!!\nS'il n y a pas entrée 0.",
                    "Attention...", JOptionPane.WARNING_MESSAGE);
            creditInitField.requestFocus();
            return false;
        }

        this.code = codeTxt.toUpperCase();
        this.nom = nomTxt.toUpperCase();

        if (adrTxt.length() > 0) {
            this.adr = adrTxt.toUpperCase();
        }
        if (telTxt.length() > 0) {
            this.tel = telTxt;
        }
        if (mobTxt.length() > 0) {
            this.mobile = mobTxt;
        }
        if (emailTxt.length() > 0) {
            this.email = emailTxt.toLowerCase();
        }
        if (rcTxt.length() > 0) {
            this.numRc = rcTxt;
        }
        if (fisclTxt.length() > 0) {
            this.numFisc = fisclTxt;
        }
        if (artTxt.length() > 0) {
            this.numArt = artTxt;
        }
        this.creditInitial = creditInitField.getBigDecimalValue();
        return true;
    }

    @Override
    public boolean save() {
        if (verifyFields()) {
            setEditedEntity(new Fournisseur(0, code, nom, adr, tel, mobile, email, numRc, numFisc, numArt));
            return super.save();
        } else {
            setSaved(false);
            return false;
        }
    }

    @Override
    public boolean insert() {
        boolean insertFr = super.insert();
        if (creditInitial.doubleValue() > 0) {
            CreditFr creditFr = new CreditFr(0);
            creditFr.setFournisseur(getEditedEntity());
            creditFr.setMontant(creditInitial);
            creditFr.setComment("CREDIT INITIAL");
            creditFr.setInitial(true);
            return insertFr && creditFr.insert();
        }
        return insertFr;
    }

    @Override
    public boolean update() {
        boolean updateFr = super.update();
        CreditFr creditFr = getOldEntity().getCreditInitial();
        creditFr.setMontant(creditInitial);
        if (creditFr.getId() == 0) {// no initial 'Credit'
            if (creditInitial.doubleValue() != 0) {
                creditFr.setFournisseur(getOldEntity());
                creditFr.setComment("CREDIT INITIAL");
                creditFr.setInitial(true);
                return updateFr && creditFr.insert();
            } else {
                return updateFr;
            }
        } else {// there wase an initial 'Crédit'
            if (creditInitial.doubleValue() != 0) {
                return updateFr && creditFr.update();
            } else {
                return updateFr && creditFr.delete();
            }
        }
    }

    public void calculerCreditTotal() {
        creditTotalField.setBigDecimalValue(creditTotal.subtract(creditInitial).add(creditInitField.getBigDecimalValue()));
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
        numPanel = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        codeField = new myComponents.MyJField();
        jLabel14 = new javax.swing.JLabel();
        nomPanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        nomField = new myComponents.MyJField();
        adrPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        adrField = new myComponents.MyJField();
        telPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        telField = new myComponents.IntegerJField();
        jLabel10 = new javax.swing.JLabel();
        mobileField = new myComponents.IntegerJField();
        emailPanel = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        emailField = new myComponents.MyJField();
        jToolBar3 = new javax.swing.JToolBar();
        numRCPanel = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
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
        creditInitField = new myComponents.CurrencyField();
        jLabel11 = new javax.swing.JLabel();
        creditTotalField = new myComponents.CurrencyField();
        currencyUniteLabel1 = new myComponents.CurrencyUniteLabel();
        vntsPanel = new javax.swing.JPanel();
        creditPanel = new javax.swing.JPanel();
        reglsPanel = new javax.swing.JPanel();

        mainTabbedPane.setFocusable(false);

        fieldsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));

        numPanel.setLayout(new javax.swing.BoxLayout(numPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel13.setText("Code/N°:");
        jLabel13.setPreferredSize(new java.awt.Dimension(55, 15));
        numPanel.add(jLabel13);

        codeField.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        codeField.setPrefsLangKey("MajCl_Nom_Lang");
        codeField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                codeFieldKeyTyped(evt);
            }
        });
        numPanel.add(codeField);

        jLabel14.setFont(new java.awt.Font("Tahoma", 2, 13)); // NOI18N
        jLabel14.setText(" 5 caractères au maximume.");
        numPanel.add(jLabel14);

        nomPanel.setLayout(new javax.swing.BoxLayout(nomPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel5.setText("Nom:");
        jLabel5.setPreferredSize(new java.awt.Dimension(55, 15));
        nomPanel.add(jLabel5);

        nomField.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        nomField.setPrefsLangKey("MajCl_Nom_Lang");
        nomPanel.add(nomField);

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
        jLabel4.setText("Tél/Fax:");
        jLabel4.setPreferredSize(new java.awt.Dimension(55, 15));
        telPanel.add(jLabel4);

        telField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                telFieldKeyTyped(evt);
            }
        });
        telPanel.add(telField);

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel10.setText(" Tél.Portable:");
        telPanel.add(jLabel10);

        mobileField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                mobileFieldKeyTyped(evt);
            }
        });
        telPanel.add(mobileField);

        emailPanel.setLayout(new javax.swing.BoxLayout(emailPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel12.setText("E-Mail:");
        jLabel12.setPreferredSize(new java.awt.Dimension(55, 15));
        emailPanel.add(jLabel12);

        emailField.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        emailField.setPrefsLangKey("MAjCl_Adr_Lang");
        emailPanel.add(emailField);

        jToolBar3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jToolBar3.setFloatable(false);
        jToolBar3.setRollover(true);

        numRCPanel.setLayout(new javax.swing.BoxLayout(numRCPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel7.setText("N° R.C:");
        jLabel7.setPreferredSize(new java.awt.Dimension(55, 15));
        numRCPanel.add(jLabel7);

        rcField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
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

        fiscField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
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

        artField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
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
        jLabel6.setText("Crédit.Initial:");
        jLabel6.setPreferredSize(new java.awt.Dimension(75, 15));
        creditInitPanel.add(jLabel6);

        creditInitField.setPreferredSize(new java.awt.Dimension(65, 27));
        creditInitField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                creditInitFieldKeyReleased(evt);
            }
        });
        creditInitPanel.add(creditInitField);

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel11.setText("  Crédit.Total:");
        creditInitPanel.add(jLabel11);

        creditTotalField.setEditable(false);
        creditTotalField.setBackground(new java.awt.Color(229, 229, 255));
        creditTotalField.setFocusable(false);
        creditTotalField.setPreferredSize(new java.awt.Dimension(60, 27));
        creditTotalField.setRequestFocusEnabled(false);
        creditInitPanel.add(creditTotalField);
        creditInitPanel.add(currencyUniteLabel1);

        javax.swing.GroupLayout fieldsPanelLayout = new javax.swing.GroupLayout(fieldsPanel);
        fieldsPanel.setLayout(fieldsPanelLayout);
        fieldsPanelLayout.setHorizontalGroup(
            fieldsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(creditInitPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
            .addComponent(nomPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(adrPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(telPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(emailPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(fieldsPanelLayout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(fieldsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jToolBar3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(numArtPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(numRCPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(idFiscalPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addGroup(fieldsPanelLayout.createSequentialGroup()
                .addComponent(numPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        fieldsPanelLayout.setVerticalGroup(
            fieldsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, fieldsPanelLayout.createSequentialGroup()
                .addComponent(numPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nomPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(adrPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(telPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(emailPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addComponent(creditInitPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        mainTabbedPane.addTab("Fournisseur", fieldsPanel);

        javax.swing.GroupLayout vntsPanelLayout = new javax.swing.GroupLayout(vntsPanel);
        vntsPanel.setLayout(vntsPanelLayout);
        vntsPanelLayout.setHorizontalGroup(
            vntsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 376, Short.MAX_VALUE)
        );
        vntsPanelLayout.setVerticalGroup(
            vntsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 349, Short.MAX_VALUE)
        );

        mainTabbedPane.addTab("Achat", vntsPanel);

        javax.swing.GroupLayout creditPanelLayout = new javax.swing.GroupLayout(creditPanel);
        creditPanel.setLayout(creditPanelLayout);
        creditPanelLayout.setHorizontalGroup(
            creditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 376, Short.MAX_VALUE)
        );
        creditPanelLayout.setVerticalGroup(
            creditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 349, Short.MAX_VALUE)
        );

        mainTabbedPane.addTab("Crédit", creditPanel);

        javax.swing.GroupLayout reglsPanelLayout = new javax.swing.GroupLayout(reglsPanel);
        reglsPanel.setLayout(reglsPanelLayout);
        reglsPanelLayout.setHorizontalGroup(
            reglsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 376, Short.MAX_VALUE)
        );
        reglsPanelLayout.setVerticalGroup(
            reglsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 349, Short.MAX_VALUE)
        );

        mainTabbedPane.addTab("Règlements", reglsPanel);

        setLayout(null);
    }// </editor-fold>//GEN-END:initComponents

    private void telFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_telFieldKeyTyped
        if (telField.getText().length() >= 9 && telField.getSelectionStart() == telField.getSelectionEnd()) {
            evt.consume();
            Toolkit.getDefaultToolkit().beep();
        }
    }//GEN-LAST:event_telFieldKeyTyped

    private void rcFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rcFieldKeyTyped
        if (rcField.getText().length() >= 10 && rcField.getSelectionStart() == rcField.getSelectionEnd()) {
            evt.consume();
            Toolkit.getDefaultToolkit().beep();
        }
    }//GEN-LAST:event_rcFieldKeyTyped

    private void fiscFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fiscFieldKeyTyped
        if (fiscField.getText().length() >= 15 && fiscField.getSelectionStart() == fiscField.getSelectionEnd()) {
            evt.consume();
            Toolkit.getDefaultToolkit().beep();
        }
    }//GEN-LAST:event_fiscFieldKeyTyped

    private void artFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_artFieldKeyTyped
        if (artField.getText().length() >= 11 && artField.getSelectionStart() == artField.getSelectionEnd()) {
            evt.consume();
            Toolkit.getDefaultToolkit().beep();
        }
    }//GEN-LAST:event_artFieldKeyTyped

    private void codeFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_codeFieldKeyTyped
        if (codeField.getText().length() >= 5 && codeField.getSelectionStart() == codeField.getSelectionEnd()) {
            evt.consume();
            Toolkit.getDefaultToolkit().beep();
        }
    }//GEN-LAST:event_codeFieldKeyTyped

    private void creditInitFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_creditInitFieldKeyReleased
        calculerCreditTotal();
    }//GEN-LAST:event_creditInitFieldKeyReleased

    private void mobileFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mobileFieldKeyTyped
        if (mobileField.getText().length() >= 10 && mobileField.getSelectionStart() == mobileField.getSelectionEnd()) {
            evt.consume();
            Toolkit.getDefaultToolkit().beep();
        }
    }//GEN-LAST:event_mobileFieldKeyTyped

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private myComponents.MyJField adrField;
    private javax.swing.JPanel adrPanel;
    private myComponents.IntegerJField artField;
    private myComponents.MyJField codeField;
    private myComponents.CurrencyField creditInitField;
    private javax.swing.JPanel creditInitPanel;
    private javax.swing.JPanel creditPanel;
    private myComponents.CurrencyField creditTotalField;
    private myComponents.CurrencyUniteLabel currencyUniteLabel1;
    private myComponents.MyJField emailField;
    private javax.swing.JPanel emailPanel;
    private javax.swing.JPanel fieldsPanel;
    private myComponents.IntegerJField fiscField;
    private javax.swing.JPanel idFiscalPanel;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JTabbedPane mainTabbedPane;
    private myComponents.IntegerJField mobileField;
    private myComponents.MyJField nomField;
    private javax.swing.JPanel nomPanel;
    private javax.swing.JPanel numArtPanel;
    private javax.swing.JPanel numPanel;
    private javax.swing.JPanel numRCPanel;
    private myComponents.MyJField rcField;
    private javax.swing.JPanel reglsPanel;
    private myComponents.IntegerJField telField;
    private javax.swing.JPanel telPanel;
    private javax.swing.JPanel vntsPanel;
    // End of variables declaration//GEN-END:variables

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new MajFournisseurPanel(null).showNewPanel(null);
            }
        });
    }
}
