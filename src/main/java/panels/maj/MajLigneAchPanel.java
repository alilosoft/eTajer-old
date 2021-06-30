/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package panels.maj;

import appLogic.PriceCalculator;
import dao.LigneAchatDAO;
import entities.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import myModels.UniteComboBoxModel;
import panels.ResultSet_Panel;
import dao.QuantifierDAO;
import dbTools.DBManager;
import dialogs.MyJDialog;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import panels.CRUDPanel;
import panels.RSTablePanel;
import panels.crud.ListeDepotsPanel;
import panels.crud.ListeProduitsPanel;
import printing.PrintingTools;
import tools.DateTools;
import tools.MessageReporting;

/**
 *
 * @author alilo
 */
public class MajLigneAchPanel extends MajPanel<LigneAch, LigneAchatDAO> {

    private static final String DEF_DEP = "MajLAch_Depot";
    private static final String DEF_MRG_DT_MODE = "MajLAch_MargeMode";
    // Vente Fields
    private Achat achat = new Achat(0, 0, null, null, false);
    // Produit Fields
    private ListeProduitsPanel listeProduits;
    private Produit produit;
    private int qteAchete = 1;
    private Unite unite;
    private double qteUnitaire = 0;
    //
    private EnStock lotEnStk;
    private BigDecimal puAch = new BigDecimal(0);
    private BigDecimal puVntDt = new BigDecimal(0);
    private BigDecimal puVntGr = new BigDecimal(0);
    private BigDecimal puVntDGr = new BigDecimal(0);
    private BigDecimal puVntSGr = new BigDecimal(0);
    private BigDecimal totalLAch = new BigDecimal(0);
    // EnStock Fields
    private ListeDepotsPanel listeDepots;
    private Depot depot;
    private Date dateExp, dateAlert;
    private String cbLot;
    // UI
    private final UniteComboBoxModel uniteCBModel = new UniteComboBoxModel();
    /**
     * @param listPanel
     */
    public MajLigneAchPanel(CRUDPanel listPanel) {
        super(listPanel);
        initComponents();
        initMajPanel(fieldsPanel);
        initProduitsPanel();
        initDepotsPanel();
        initUniteComboBox();
        configCalendar();
        setDefaultFocusedComp(selProduitP.getDescField());
    }

    @Override
    public void loadPreferences() {
        try {
            int defDep = getIntPreference(DEF_DEP, 0);
            setDepot(new Depot(defDep));
            String margeDtMode = getPreference(DEF_MRG_DT_MODE, "%");
            modeMrgDtCB.setSelectedItem(margeDtMode);
        } catch (Exception e) {
            MessageReporting.showMessage(Level.SEVERE, this.getClass(), "loadPreferences()", "inccorect values!");
        }
    }

    @Override
    public LigneAchatDAO getTableDAO() {
        return LigneAchatDAO.getInstance();
    }

    @Override
    public void setChildEntity(EntityClass child, boolean allowChng) {
        if (child instanceof Produit) {
            setProduit((Produit) child);
            qteF.requestFocus();
        }
        if (child instanceof Achat) {
            setAchat((Achat) child);
        }
    }

    public void setAchat(Achat ach) {
        this.achat = ach;
    }

    @Override
    public void setActionsShortcuts() {
        super.setActionsShortcuts(); //To change body of generated methods, choose Tools | Templates.
        doOnPress(KeyEvent.VK_F6, 0, gestTarifsAct, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    @java.lang.Override
    public void initFields(LigneAch oldEntity) {
        setAchat(oldEntity.getAchat());
        // Init Ligne_Achat
        qteAchete = oldEntity.getQte();
        qteF.setText(String.valueOf(qteAchete));
        setUnite(oldEntity.getUnite());
        puAch = oldEntity.getPuAch();
        puAchF.setBigDecimalValue(puAch);
        // init Prod/Stock
        setProduit(oldEntity.getProduit());
        if (isUpdate()) {
            setLotEnStock(oldEntity.getEnStock());
        }
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
        selProduitP.setSelEntity(produit);
        initUnitsComboModel();
        if (produit != null) {
            setDefaultFocusedComp(qteF);
            initProdFields();
            lotEnStk = produit.getLastLot();
            setLotEnStock(lotEnStk);
        } else {
            clearProduitFields();
            setLotEnStock(null);
        }
    }

    public void setLotEnStock(EnStock lot) {
        if (lot != null) {
            puAch = lot.getPuAch();
            puAchF.setBigDecimalValue(puAch);
            puVntDt = lot.getPuVntDt();
            puVntDtF.setBigDecimalValue(puVntDt);
            puVntGrF.setBigDecimalValue(lot.getPuVntGr());
            puVntDGrF.setBigDecimalValue(lot.getPuVntDgr());
            puVntSGrF.setBigDecimalValue(lot.getPuVntSgr());
            setDepot(lot.getDepot());
            setDateExp(lot.getDateExp());
            setDateAlert(lot.getDateAlert());
            cbLot = lot.getCodBar();
            cbLotF.setText(cbLot);
        } else {
            puAchF.setValue(0);
            puVntDtF.setValue(0);
            setDateExp(null);
            setDateAlert(null);
        }
    }

    public void initProdFields() {
//        margeDtF.setValue(produit.getMargeDt());
        modeMrgDtCB.setSelectedItem("%");
        tvaField.setText(produit.getFamille().getTva().toString());
        cbLotF.setText(produit.getCodBar());
    }

    public void clearProduitFields() {
        margeDtF.setValue(0);
        cbLotF.setText("");
        tvaField.setText("");
    }

    public final void calcQtes() {
        qteAchete = 0;
        qteUnitaire = 0;
        if (produit == null || unite == null) {
            return;
        }
        qteAchete = qteF.getValue();
        qteUnitaire = qteAchete * unite.getQte();
    }

    private void calcMarges() {
        double marge = PriceCalculator.getMargeVal(puAchF.getBigDecimalValue(), puVntDtF.getBigDecimalValue(), modeMrgDtCB.getSelectedItem().toString());
        margeDtF.setValue(marge);
        if (marge <= 0) {
            //margeDtF.setSpecialFG(Color.RED);
        } else {
            margeDtF.setSpecialFG(Color.BLACK);
        }
    }

    private void calcPrixVntDt() {
        BigDecimal prixVnt = PriceCalculator.getPrixVnt(puAchF.getBigDecimalValue(), margeDtF.getValue(), modeMrgDtCB.getSelectedItem().toString());
        puVntDtF.setBigDecimalValue(prixVnt);
        if (prixVnt.doubleValue() <= 0) {
            //puVntDtF.setSpecialFG(Color.RED);
        } else {
            puVntDtF.setSpecialFG(Color.BLACK);
        }
    }

    public final void initUniteComboBox() {
        uniteCB.setModel(uniteCBModel);
        uniteCB.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                unite = uniteCBModel.getSelectedItem();
            }
        });
    }

    public void initUnitsComboModel() {
        uniteCBModel.removeAllElements();
        if (produit != null) {
            List<Quantifier> units = QuantifierDAO.getInstance().getUnitesOfProd(produit);
            for (Quantifier q : units) {
                uniteCBModel.addElement(q.getUnite());
            }
        }
        uniteCB.setFocusable(uniteCBModel.getSize() != 1);
    }

    public void setUnite(Unite u) {
        unite = u;
        uniteCBModel.setSelectedItem(unite);
    }

    public void setDepot(Depot d) {
        this.depot = d;
        selDepotPanel.setSelEntity(d);
    }

    public final void configCalendar() {
        dateChooser.setMinSelectableDate(new Date());
        dateChooser.addPropertyChangeListener("date", new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                setDateExp(dateChooser.getDate());
            }
        });
    }

    public void setDateExp(Date date) {
        this.dateExp = date;
        if (dateExp != null && !DateTools.isMaxJavaTime(dateExp)) {
            expireCheckBox.setSelected(true);
            if (dateChooser.getDate() == null || dateExp.compareTo(dateChooser.getDate()) != 0) {
                dateChooser.setDate(dateExp);
            }
        } else {
            expireCheckBox.setSelected(false);
            if (dateChooser.getDate() != null) {
                dateChooser.setDate(null);
            }
        }
    }

    public void setDateAlert(Date d) {
        this.dateAlert = d;
        if (dateExp == null || dateAlert == null || dateExp.compareTo(DateTools.getMaxJavaDate()) == 0) {
            return;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(dateExp);
        int yearExpir = cal.get(Calendar.YEAR);
        int dayExpir = cal.get(Calendar.DAY_OF_YEAR);

        cal.setTime(dateAlert);
        int yearAlert = cal.get(Calendar.YEAR);
        int dayAlert = cal.get(Calendar.DAY_OF_YEAR);
        int days = (yearExpir - yearAlert) * 365 + dayExpir - dayAlert;
        alertCountField.setText(days + "");
        alertTimeComboBox.setSelectedIndex(1);
    }

    public final void initProduitsPanel() {
        listeProduits = new ListeProduitsPanel(this, false, true);
        listeProduits.getPropertyChangeSupport().addPropertyChangeListener(ResultSet_Panel.ENTITY_TO_VIEW_PROPERTY, this);
        selProduitP.setSelListPanel(listeProduits);
    }

    public final void initDepotsPanel() {
        listeDepots = new ListeDepotsPanel(this, false).getNavigNEditList();
        listeDepots.getPropertyChangeSupport().addPropertyChangeListener(ResultSet_Panel.ENTITY_TO_VIEW_PROPERTY, this);
        selDepotPanel.setSelListPanel(listeDepots);
    }

    @Override
    public void viewSelEntity(RSTablePanel listePanel) {
        if (listePanel instanceof ListeProduitsPanel) {
            if (listePanel.getSelectedEntity().getId() > 0) {
                setProduit((Produit) listePanel.getSelectedEntity());
            } else {
                setProduit(null);
            }
        }

        if (listePanel instanceof ListeDepotsPanel) {
            if (listePanel.getSelectedEntity().getId() > 0) {
                setDepot((Depot) listePanel.getSelectedEntity());
            } else {
                setDepot(null);
            }
        }
    }

    private final MyJDialog tarifsDialog = new MyJDialog(this, true, false);

    public void gestTarifs() {
        tarifsDialog.setContentPane(tarifsP);
        tarifsDialog.pack();

        tarifsDialog.setLocation(addTarifsLinkB.getLocationOnScreen().x, addTarifsLinkB.getLocationOnScreen().y);
        tarifsDialog.setVisible(true);
    }
    private final Action gestTarifsAct = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            gestTarifs();
        }
    };

    @Override
    public boolean verifyFields() {
        produit = selProduitP.getSelEntity();
        if (produit == null) {
            JOptionPane.showMessageDialog(this, "Sélectionner le Produit acheté SVP!!!", "Attention...", JOptionPane.WARNING_MESSAGE);
            selProduitP.showSelList();
            return false;
        }

        String qteTxt = qteF.getText().trim();
        if (qteTxt.length() == 0) {
            JOptionPane.showMessageDialog(this, "Entrez la quantité acheté SVP!!!", "Attention...", JOptionPane.WARNING_MESSAGE);
            qteF.requestFocus();
            return false;
        }
        if (qteF.getValue() <= 0) {
            JOptionPane.showMessageDialog(this, "La quantité achetée doit être > 0 !!!", "Attention...", JOptionPane.WARNING_MESSAGE);
            qteF.requestFocus();
            return false;
        }

        if (unite == null || unite.getId() <= 0) {
            JOptionPane.showMessageDialog(this, "Sélectionner une unité SVP!!!", "Attention...", JOptionPane.WARNING_MESSAGE);
            uniteCB.requestFocus();
            return false;
        }

        String puAchTxt = puAchF.getText().trim();
        if (puAchTxt.length() == 0) {
            JOptionPane.showMessageDialog(this, "Entrez le prix d'achat SVP!!!", "Attention...", JOptionPane.WARNING_MESSAGE);
            puAchF.requestFocus();
            return false;
        } else {
            puAch = puAchF.getBigDecimalValue();
        }
        //

        String puVntTxt = puVntDtF.getText().trim();
        if (puVntTxt.length() == 0) {
            JOptionPane.showMessageDialog(this, "Entrez le nouveau prix de vente SVP!!!", "Attention...", JOptionPane.WARNING_MESSAGE);
            puVntDtF.requestFocus();
            return false;
        } else {
            puVntDt = puVntDtF.getBigDecimalValue();
            if (puAch.doubleValue() >= puVntDt.doubleValue()) {
                int rep = JOptionPane.showConfirmDialog(this, "Le nouveau prix d'achat est supérieur ou égale au prix de vente.\n"
                        + "Voullez vous corriger?", "Attention...", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (rep == JOptionPane.YES_OPTION) {
                    puAchF.requestFocus();
                }
                return false;
            }
        }

        String puVntGrStr = puVntGrF.getText().trim();
        if (puVntGrStr.length() == 0) {
            JOptionPane.showMessageDialog(this, "Entrez le prix unitaire de vente en gros SVP!!!", "Attention...", JOptionPane.WARNING_MESSAGE);
            gestTarifs();
            puVntGrF.requestFocus();
            return false;
        }

        if (puVntGr.doubleValue() != 0 && puVntGr.doubleValue() <= puAch.doubleValue()) {
            String mess = "Le prix de vente en gros est inférieur (ou égal) au prix d'achat!!!\n"
                    + "Voullez vous le corriger?";
            int rep = JOptionPane.showConfirmDialog(this, mess, "Attention", JOptionPane.YES_NO_OPTION);
            if (rep == JOptionPane.YES_OPTION) {
                gestTarifs();
                puVntGrF.requestFocus();
                return false;
            }
        }

        if (puVntDt.doubleValue() <= puVntGr.doubleValue()) {
            String mess = "Le prix de vente en détail est inférieur (ou égal) au prix de vente en gros!!!\n"
                    + "Voullez vous le corriger?";
            int rep = JOptionPane.showConfirmDialog(this, mess, "Attention", JOptionPane.YES_NO_OPTION);
            if (rep == JOptionPane.YES_OPTION) {
                puVntDtF.requestFocus();
                return false;
            }
        }

        depot = selDepotPanel.getSelEntity();
        if (depot == null) {
            JOptionPane.showMessageDialog(this, "Sélectionner un Dépôt SVP!!!", "Attention...", JOptionPane.WARNING_MESSAGE);
            selDepotPanel.showSelList();
            return false;
        }

        if (expireCheckBox.isSelected()) {
            dateExp = dateChooser.getDate();
            if (dateExp == null) {
                String mess = "Si ce produit s'expire, entrez la date d'expiration.\n"
                        + "Si non décochez la case 'Date.Exp' SVP!";
                JOptionPane.showMessageDialog(this, mess, "Attention", JOptionPane.ERROR_MESSAGE);
                dateChooser.requestFocus();
                return false;
            }
            if (dateExp.compareTo(new Date()) <= 0) {
                String mess = "La date d'expiration doit être aprés la date d'aujourd'hui SVP!";
                JOptionPane.showMessageDialog(this, mess, "Attention", JOptionPane.ERROR_MESSAGE);
                dateChooser.requestFocus();
                return false;
            }
            if (!calcDateAlert()) {
                return false;
            }
        } else {
            dateExp = DateTools.getMaxJavaDate();
            dateAlert = DateTools.getMaxJavaDate();
        }

        String cbStr = cbLotF.getText().trim();
        if (cbStr.length() == 0) {
            String mess = "Aucun code a barres ou référence n'est donnée a ce lot!\n"
                    + "Le code a barres (référence) de produit sera utiliser par défaut.\n"
                    + "Mais il est conseillé d'utiliser un code différent s'il y a plusieurs lots en stock.\n"
                    + "Voullez vous continuer quand même?";
            int rep = JOptionPane.showConfirmDialog(this, mess, "Attention", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (rep == JOptionPane.NO_OPTION) {
                cbLotF.requestFocus();
                return false;
            } else {
                cbLot = produit.getCodBar();
            }
        } else {
            cbLot = cbStr;
        }
        //===
        calcQtes();// this methode set all quantities variables.
        double total = puAch.doubleValue() * qteUnitaire;
        totalLAch = new BigDecimal(total).setScale(2, BigDecimal.ROUND_CEILING);
        return true;
    }

    private boolean calcDateAlert() {
        int count = alertCountField.getValue();
        if (count == 0) {
            String mess = "L'alert d'expiration doit être régler avant l'expiration d'au moins un jour!";
            JOptionPane.showMessageDialog(this, mess, "Attention", JOptionPane.ERROR_MESSAGE);
            alertCountField.requestFocus();
            return false;
        }
        // calc date d'alert
        int timeUnite = 0;
        switch (alertTimeComboBox.getSelectedIndex()) {
            case 0:
                timeUnite = Calendar.MONTH;
                break;
            case 1:
                timeUnite = Calendar.DAY_OF_MONTH;
                break;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(dateExp);
        cal.add(timeUnite, -count);
        dateAlert = cal.getTime();
        return true;
    }

    @Override
    public void savePreferences() {
        savePreference(DEF_DEP, depot.getId());
        savePreference(DEF_MRG_DT_MODE, modeMrgDtCB.getSelectedItem().toString());
    }

    @Override
    public boolean save() {
        if (verifyFields()) {
            // the qte is set to zero, will be updated after validating LAch.
            EnStock newLot = new EnStock(0, 0, dateExp, cbLot, false);
            newLot.setDateEntr(new Date());
            newLot.setPuAch(puAch);
            newLot.setPuVntDt(puVntDt);
            newLot.setPuVntGr(puVntGr);
            newLot.setPuVntDgr(puVntDGr);
            newLot.setPuVntSgr(puVntSGr);
            newLot.setProduit(produit);
            newLot.setDepot(depot);

            boolean lotSaved;
            if (isUpdate()) {// update corresponding 'Lot' only if is not actif.
                EnStock oldLot = getOldEntity().getEnStock();
                // if related stock is not actif => update it;
                if(!oldLot.getActif()){
                    newLot.setId(oldLot.getId());
                    lotSaved = newLot.update();
                }else{// insert new lot
                    lotSaved = newLot.insert();
                }
            } else {
                lotSaved = newLot.insert();
            }
            if (lotSaved) {
                LigneAch ligneAch = new LigneAch(0, puAch, qteAchete, qteUnitaire, totalLAch);
                ligneAch.setAchat(achat);
                ligneAch.setProduit(produit);
                ligneAch.setEnStock(newLot);
                ligneAch.setUnite(unite);
                setEditedEntity(ligneAch);
                // maj produit
//                produit.setMargeDt(margeDtF.getValue());
//                produit.setMargeGr(margeGrF.getValue());
//                produit.setMargeDgr(margeDGrF.getValue());
//                produit.setMargeSgr(margeSGrF.getValue());
                return super.save() && produit.update();
            } else {
                return false;
            }
        } else {
            setSaved(false);
            return false;
        }
    }

    @Override
    public boolean add() {
        if (super.add()) {
            selProduitP.showSelList();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void clearFields() {
        setProduit(null);
        qteAchete = 0;
        qteUnitaire = 0;
        qteF.setText("");
        puAch = new BigDecimal(0);
        puAchF.setText("");
        expireCheckBox.setSelected(false);
    }

    private final Action printCodeBarAct = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (save()) {
                HashMap<String, Object> params = new HashMap<>();
                params.put("ID_LOT", getEditedEntity().getEnStock().getId());
                PrintingTools.previewReport("/printing/Lot_CodeBar.jasper", DBManager.getInstance().getDefaultConnection(), params);
            }
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

        fieldsPanel = new javax.swing.JPanel();
        prodPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        selProduitP = new panels.SelectionPanel<Produit>();
        qteAchtePanel = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        qteF = new myComponents.IntegerJField();
        X = new javax.swing.JLabel();
        uniteCB = new myComponents.MyJComboBox();
        depotPanel = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        selDepotPanel = new panels.SelectionPanel<Depot>();
        jSeparator2 = new javax.swing.JSeparator();
        tvaPanel = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        tvaField = new myComponents.MyJField();
        htRadioBox = new javax.swing.JRadioButton();
        ttcRB = new javax.swing.JRadioButton();
        prixAchP = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        puAchF = new myComponents.CurrencyField();
        currencyUniteLabel2 = new myComponents.CurrencyUniteLabel();
        margeDtP = new javax.swing.JPanel();
        margeDtChB = new javax.swing.JCheckBox();
        margeDtF = new myComponents.DecimalJField();
        modeMrgDtCB = new javax.swing.JComboBox();
        prixVntDtP = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        puVntDtF = new myComponents.CurrencyField();
        currencyUniteLabel1 = new myComponents.CurrencyUniteLabel();
        jSeparator13 = new javax.swing.JSeparator();
        expirePanel = new javax.swing.JPanel();
        expireCheckBox = new javax.swing.JCheckBox();
        dateChooser = new myComponents.MyJDateChooser();
        jLabel1 = new javax.swing.JLabel();
        alertCountField = new myComponents.IntegerJField();
        alertTimeComboBox = new javax.swing.JComboBox();
        refPanel = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        cbLotF = new myComponents.MyJField();
        printCBLotBtn = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        addTarifsLinkB = new com.l2fprod.common.swing.JLinkButton();
        tarifsP = new javax.swing.JPanel();
        prixVntGrP = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        puVntGrF = new myComponents.CurrencyField();
        currencyUniteLabel4 = new myComponents.CurrencyUniteLabel();
        margeGrP = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        margeGrF = new myComponents.IntegerJField();
        typeMrgGrC = new javax.swing.JComboBox();
        prixVntDGP = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        puVntDGrF = new myComponents.CurrencyField();
        currencyUniteLabel5 = new myComponents.CurrencyUniteLabel();
        prixVntSGP = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        puVntSGrF = new myComponents.CurrencyField();
        currencyUniteLabel6 = new myComponents.CurrencyUniteLabel();
        margeDGP = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        margeDGrF = new myComponents.DecimalJField();
        typeMrgDemGrC = new javax.swing.JComboBox();
        margeSGP = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        margeSGrF = new myComponents.DecimalJField();
        typeMrgDemGrC1 = new javax.swing.JComboBox();

        fieldsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        fieldsPanel.setFocusable(false);
        fieldsPanel.setMinimumSize(new java.awt.Dimension(565, 218));
        fieldsPanel.setRequestFocusEnabled(false);

        prodPanel.setFocusable(false);
        prodPanel.setRequestFocusEnabled(false);
        prodPanel.setLayout(new javax.swing.BoxLayout(prodPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setText("Produit Acheté:");
        jLabel6.setPreferredSize(new java.awt.Dimension(98, 16));
        prodPanel.add(jLabel6);

        selProduitP.setDescription("Sélectionnez le produit acheté SVP!");
        selProduitP.setShortcutKey("F3");
        prodPanel.add(selProduitP);

        qteAchtePanel.setFocusable(false);
        qteAchtePanel.setPreferredSize(new java.awt.Dimension(520, 25));
        qteAchtePanel.setLayout(new javax.swing.BoxLayout(qteAchtePanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel10.setText("Qte.Achetée:");
        jLabel10.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jLabel10.setMinimumSize(new java.awt.Dimension(83, 17));
        jLabel10.setPreferredSize(new java.awt.Dimension(98, 16));
        qteAchtePanel.add(jLabel10);

        qteF.setText("1");
        qteF.setPreferredSize(new java.awt.Dimension(100, 23));
        qteAchtePanel.add(qteF);

        X.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        X.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        X.setText(" X ");
        X.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        qteAchtePanel.add(X);

        uniteCB.setBackground(new java.awt.Color(229, 229, 255));
        uniteCB.setPreferredSize(new java.awt.Dimension(350, 22));
        uniteCB.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                uniteCBKeyPressed(evt);
            }
        });
        qteAchtePanel.add(uniteCB);

        depotPanel.setLayout(new javax.swing.BoxLayout(depotPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel11.setText("Dépôt/Magasin:");
        jLabel11.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        depotPanel.add(jLabel11);

        selDepotPanel.setDescription("Sélectionner le dépôt de stockage SVP!");
        selDepotPanel.setShortcutKey("F4");
        depotPanel.add(selDepotPanel);

        tvaPanel.setLayout(new javax.swing.BoxLayout(tvaPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel19.setText("Taux.TVA (%): ");
        jLabel19.setPreferredSize(new java.awt.Dimension(98, 17));
        tvaPanel.add(jLabel19);

        tvaField.setEditable(false);
        tvaField.setBackground(new java.awt.Color(229, 229, 255));
        tvaField.setToolTipText("Appuyer 'Entrée' et séléctionner la catégorie du produit");
        tvaField.setFocusable(false);
        tvaField.setMinimumSize(new java.awt.Dimension(16, 17));
        tvaField.setPreferredSize(new java.awt.Dimension(25, 25));
        tvaPanel.add(tvaField);

        htRadioBox.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        htRadioBox.setSelected(true);
        htRadioBox.setText("Entrer les Prix en Hors Taxe (HT)");
        htRadioBox.setBorder(null);
        htRadioBox.setFocusable(false);
        htRadioBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        htRadioBox.setPreferredSize(new java.awt.Dimension(250, 19));
        tvaPanel.add(htRadioBox);

        ttcRB.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        ttcRB.setText("Entrer les Prix en TTC");
        ttcRB.setBorder(null);
        ttcRB.setFocusable(false);
        ttcRB.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        ttcRB.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ttcRB.setPreferredSize(new java.awt.Dimension(180, 17));
        tvaPanel.add(ttcRB);

        prixAchP.setPreferredSize(new java.awt.Dimension(404, 27));
        prixAchP.setLayout(new javax.swing.BoxLayout(prixAchP, javax.swing.BoxLayout.LINE_AXIS));

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel14.setText("Prix d'Achat: ");
        jLabel14.setPreferredSize(new java.awt.Dimension(98, 17));
        prixAchP.add(jLabel14);

        puAchF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                puAchFKeyReleased(evt);
            }
        });
        prixAchP.add(puAchF);
        prixAchP.add(currencyUniteLabel2);

        margeDtP.setLayout(new javax.swing.BoxLayout(margeDtP, javax.swing.BoxLayout.LINE_AXIS));

        margeDtChB.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        margeDtChB.setText("Marge de Bénéfice:");
        margeDtChB.setBorder(null);
        margeDtChB.setFocusable(false);
        margeDtChB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                margeDtChBActionPerformed(evt);
            }
        });
        margeDtP.add(margeDtChB);

        margeDtF.setText("0");
        margeDtF.setToolTipText("La marge de bénéfice du vente en Détails");
        margeDtF.setEnabled(false);
        margeDtF.setPreferredSize(new java.awt.Dimension(30, 23));
        margeDtF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                margeDtFKeyReleased(evt);
            }
        });
        margeDtP.add(margeDtF);

        modeMrgDtCB.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        modeMrgDtCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "%", "DA" }));
        modeMrgDtCB.setEnabled(false);
        modeMrgDtCB.setFocusable(false);
        modeMrgDtCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modeMrgDtCBActionPerformed(evt);
            }
        });
        margeDtP.add(modeMrgDtCB);

        prixVntDtP.setLayout(new javax.swing.BoxLayout(prixVntDtP, javax.swing.BoxLayout.LINE_AXIS));

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel16.setText("Prix de Vente: ");
        jLabel16.setPreferredSize(new java.awt.Dimension(98, 17));
        prixVntDtP.add(jLabel16);

        puVntDtF.setPreferredSize(new java.awt.Dimension(70, 22));
        puVntDtF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                puVntDtFKeyReleased(evt);
            }
        });
        prixVntDtP.add(puVntDtF);
        prixVntDtP.add(currencyUniteLabel1);

        expirePanel.setPreferredSize(new java.awt.Dimension(144, 27));
        expirePanel.setLayout(new javax.swing.BoxLayout(expirePanel, javax.swing.BoxLayout.LINE_AXIS));

        expireCheckBox.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        expireCheckBox.setText("Date.Expir.:");
        expireCheckBox.setBorder(null);
        expireCheckBox.setPreferredSize(new java.awt.Dimension(98, 16));
        expireCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                expireCheckBoxStateChanged(evt);
            }
        });
        expireCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expireCheckBoxActionPerformed(evt);
            }
        });
        expirePanel.add(expireCheckBox);

        dateChooser.setEnabled(false);
        expirePanel.add(dateChooser);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("  Alerte.Avant:");
        expirePanel.add(jLabel1);

        alertCountField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        alertCountField.setText("1");
        alertCountField.setEnabled(false);
        alertCountField.setPreferredSize(new java.awt.Dimension(0, 27));
        expirePanel.add(alertCountField);

        alertTimeComboBox.setBackground(new java.awt.Color(204, 204, 255));
        alertTimeComboBox.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        alertTimeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Mois", "Jours" }));
        alertTimeComboBox.setEnabled(false);
        expirePanel.add(alertTimeComboBox);

        refPanel.setLayout(new javax.swing.BoxLayout(refPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel9.setText("Référ/C.B.Lot:");
        jLabel9.setPreferredSize(new java.awt.Dimension(98, 16));
        refPanel.add(jLabel9);

        cbLotF.setToolTipText("La référence ou le code bare de cette date d'expiration. Util pour accélérer et faciliter la recherche.");
        cbLotF.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        cbLotF.setPrefsLangKey("MajEnStock_Ref_Lang");
        refPanel.add(cbLotF);

        printCBLotBtn.setAction(printCodeBarAct);
        printCBLotBtn.setFont(new java.awt.Font("Tahoma", 2, 13)); // NOI18N
        printCBLotBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/print16.png"))); // NOI18N
        printCBLotBtn.setToolTipText("Ctrl+Espace");
        printCBLotBtn.setFocusable(false);
        printCBLotBtn.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        printCBLotBtn.setMaximumSize(new java.awt.Dimension(49, 30));
        printCBLotBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printCBLotBtnActionPerformed(evt);
            }
        });
        refPanel.add(printCBLotBtn);

        jLabel13.setFont(new java.awt.Font("Tahoma", 2, 14)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel13.setText(" (Utilisé s'il existe plusieurs lots en stock)");
        refPanel.add(jLabel13);

        addTarifsLinkB.setAction(gestTarifsAct);
        addTarifsLinkB.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 3, 0, 0));
        addTarifsLinkB.setForeground(new java.awt.Color(51, 51, 255));
        addTarifsLinkB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/dollar16.png"))); // NOI18N
        addTarifsLinkB.setText("Entrer les prix de vente en Gros (F6)");
        addTarifsLinkB.setFocusable(false);
        addTarifsLinkB.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        addTarifsLinkB.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        javax.swing.GroupLayout fieldsPanelLayout = new javax.swing.GroupLayout(fieldsPanel);
        fieldsPanel.setLayout(fieldsPanelLayout);
        fieldsPanelLayout.setHorizontalGroup(
            fieldsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(expirePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(refPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(depotPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(prodPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(qteAchtePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE)
            .addComponent(jSeparator2)
            .addComponent(jSeparator13)
            .addGroup(fieldsPanelLayout.createSequentialGroup()
                .addGroup(fieldsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(prixAchP, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(prixVntDtP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(fieldsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(margeDtP, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addTarifsLinkB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addComponent(tvaPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE)
        );
        fieldsPanelLayout.setVerticalGroup(
            fieldsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, fieldsPanelLayout.createSequentialGroup()
                .addComponent(prodPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(qteAchtePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tvaPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(fieldsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(prixAchP, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(margeDtP, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(fieldsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(prixVntDtP, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addTarifsLinkB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator13, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(depotPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(expirePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(refPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        prixVntGrP.setLayout(new javax.swing.BoxLayout(prixVntGrP, javax.swing.BoxLayout.LINE_AXIS));

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel29.setText("Prix de Vente.Gros: ");
        jLabel29.setPreferredSize(new java.awt.Dimension(127, 17));
        prixVntGrP.add(jLabel29);

        puVntGrF.setPreferredSize(new java.awt.Dimension(70, 22));
        puVntGrF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                puVntGrFKeyPressed(evt);
            }
        });
        prixVntGrP.add(puVntGrF);
        prixVntGrP.add(currencyUniteLabel4);

        margeGrP.setLayout(new javax.swing.BoxLayout(margeGrP, javax.swing.BoxLayout.LINE_AXIS));

        jLabel32.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel32.setText("Marge de Bénéfice de Gros: ");
        jLabel32.setPreferredSize(new java.awt.Dimension(175, 17));
        margeGrP.add(jLabel32);

        margeGrF.setEnabled(false);
        margeGrP.add(margeGrF);

        typeMrgGrC.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        typeMrgGrC.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "%", "DA" }));
        typeMrgGrC.setEnabled(false);
        margeGrP.add(typeMrgGrC);

        prixVntDGP.setLayout(new javax.swing.BoxLayout(prixVntDGP, javax.swing.BoxLayout.LINE_AXIS));

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel31.setText("Prix de Demi-Gros: ");
        jLabel31.setPreferredSize(new java.awt.Dimension(127, 17));
        prixVntDGP.add(jLabel31);

        puVntDGrF.setPreferredSize(new java.awt.Dimension(70, 22));
        puVntDGrF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                puVntDGrFKeyPressed(evt);
            }
        });
        prixVntDGP.add(puVntDGrF);
        prixVntDGP.add(currencyUniteLabel5);

        prixVntSGP.setLayout(new javax.swing.BoxLayout(prixVntSGP, javax.swing.BoxLayout.LINE_AXIS));

        jLabel33.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel33.setText("Prix de Super-Gros: ");
        jLabel33.setPreferredSize(new java.awt.Dimension(127, 17));
        prixVntSGP.add(jLabel33);

        puVntSGrF.setPreferredSize(new java.awt.Dimension(70, 22));
        puVntSGrF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                puVntSGrFKeyPressed(evt);
            }
        });
        prixVntSGP.add(puVntSGrF);
        prixVntSGP.add(currencyUniteLabel6);

        margeDGP.setLayout(new javax.swing.BoxLayout(margeDGP, javax.swing.BoxLayout.LINE_AXIS));

        jLabel34.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel34.setText("Marge de Bénéf. Demi-Gros: ");
        margeDGP.add(jLabel34);

        margeDGrF.setText("0");
        margeDGrF.setToolTipText("La marge de bénéfice du vente en Détails");
        margeDGrF.setEnabled(false);
        margeDGP.add(margeDGrF);

        typeMrgDemGrC.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        typeMrgDemGrC.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "%", "DA" }));
        typeMrgDemGrC.setEnabled(false);
        margeDGP.add(typeMrgDemGrC);

        margeSGP.setLayout(new javax.swing.BoxLayout(margeSGP, javax.swing.BoxLayout.LINE_AXIS));

        jLabel37.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel37.setText("Marge de Bénéf. Sup-Gros: ");
        jLabel37.setPreferredSize(new java.awt.Dimension(175, 27));
        margeSGP.add(jLabel37);

        margeSGrF.setText("0");
        margeSGrF.setToolTipText("La marge de bénéfice du vente en Détails");
        margeSGrF.setEnabled(false);
        margeSGP.add(margeSGrF);

        typeMrgDemGrC1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        typeMrgDemGrC1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "%", "DA" }));
        typeMrgDemGrC1.setEnabled(false);
        margeSGP.add(typeMrgDemGrC1);

        javax.swing.GroupLayout tarifsPLayout = new javax.swing.GroupLayout(tarifsP);
        tarifsP.setLayout(tarifsPLayout);
        tarifsPLayout.setHorizontalGroup(
            tarifsPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tarifsPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tarifsPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tarifsPLayout.createSequentialGroup()
                        .addComponent(prixVntSGP, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(margeSGP, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE))
                    .addGroup(tarifsPLayout.createSequentialGroup()
                        .addGroup(tarifsPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(prixVntDGP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(prixVntGrP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tarifsPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(margeGrP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(margeDGP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        tarifsPLayout.setVerticalGroup(
            tarifsPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tarifsPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tarifsPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(margeGrP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(prixVntGrP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tarifsPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(prixVntDGP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(margeDGP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tarifsPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(prixVntSGP, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                    .addComponent(margeSGP, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setMaximumSize(new java.awt.Dimension(480, 185));
        setMinimumSize(new java.awt.Dimension(480, 185));
        setLayout(null);
    }// </editor-fold>//GEN-END:initComponents

    private void uniteCBKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_uniteCBKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            okAction.actionPerformed(null);
        }
    }//GEN-LAST:event_uniteCBKeyPressed

    private void expireCheckBoxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_expireCheckBoxStateChanged
        dateChooser.setEnabled(expireCheckBox.isSelected());
        alertCountField.setEnabled(expireCheckBox.isSelected());
        alertTimeComboBox.setEnabled(expireCheckBox.isSelected());
        //cbLotF.setEnabled(expireCheckBox.isSelected());
    }//GEN-LAST:event_expireCheckBoxStateChanged

    private void expireCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expireCheckBoxActionPerformed
        if (expireCheckBox.isSelected() && dateChooser.getDate() != null) {
            setDateExp(dateChooser.getDate());
        } else {
            //setDateExp(null);
        }
    }//GEN-LAST:event_expireCheckBoxActionPerformed

    private void printCBLotBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printCBLotBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_printCBLotBtnActionPerformed

    private void puAchFKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_puAchFKeyReleased
        calcMarges();
    }//GEN-LAST:event_puAchFKeyReleased

    private void margeDtChBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_margeDtChBActionPerformed
        margeDtF.setEnabled(margeDtChB.isSelected());
        modeMrgDtCB.setEnabled(margeDtChB.isSelected());
    }//GEN-LAST:event_margeDtChBActionPerformed

    private void margeDtFKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_margeDtFKeyReleased
        calcPrixVntDt();
    }//GEN-LAST:event_margeDtFKeyReleased

    private void modeMrgDtCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modeMrgDtCBActionPerformed
        calcPrixVntDt();
    }//GEN-LAST:event_modeMrgDtCBActionPerformed

    private void puVntDtFKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_puVntDtFKeyReleased
        calcMarges();
    }//GEN-LAST:event_puVntDtFKeyReleased

    private void puVntGrFKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_puVntGrFKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tarifsDialog.closeDialog();
        }
    }//GEN-LAST:event_puVntGrFKeyPressed

    private void puVntDGrFKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_puVntDGrFKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tarifsDialog.closeDialog();
        }
    }//GEN-LAST:event_puVntDGrFKeyPressed

    private void puVntSGrFKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_puVntSGrFKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tarifsDialog.closeDialog();
        }
    }//GEN-LAST:event_puVntSGrFKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel X;
    private com.l2fprod.common.swing.JLinkButton addTarifsLinkB;
    private myComponents.IntegerJField alertCountField;
    private javax.swing.JComboBox alertTimeComboBox;
    private myComponents.MyJField cbLotF;
    private myComponents.CurrencyUniteLabel currencyUniteLabel1;
    private myComponents.CurrencyUniteLabel currencyUniteLabel2;
    private myComponents.CurrencyUniteLabel currencyUniteLabel4;
    private myComponents.CurrencyUniteLabel currencyUniteLabel5;
    private myComponents.CurrencyUniteLabel currencyUniteLabel6;
    private myComponents.MyJDateChooser dateChooser;
    private javax.swing.JPanel depotPanel;
    private javax.swing.JCheckBox expireCheckBox;
    private javax.swing.JPanel expirePanel;
    private javax.swing.JPanel fieldsPanel;
    private javax.swing.JRadioButton htRadioBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPanel margeDGP;
    private myComponents.DecimalJField margeDGrF;
    private javax.swing.JCheckBox margeDtChB;
    private myComponents.DecimalJField margeDtF;
    private javax.swing.JPanel margeDtP;
    private myComponents.IntegerJField margeGrF;
    private javax.swing.JPanel margeGrP;
    private javax.swing.JPanel margeSGP;
    private myComponents.DecimalJField margeSGrF;
    private javax.swing.JComboBox modeMrgDtCB;
    private javax.swing.JButton printCBLotBtn;
    private javax.swing.JPanel prixAchP;
    private javax.swing.JPanel prixVntDGP;
    private javax.swing.JPanel prixVntDtP;
    private javax.swing.JPanel prixVntGrP;
    private javax.swing.JPanel prixVntSGP;
    private javax.swing.JPanel prodPanel;
    private myComponents.CurrencyField puAchF;
    private myComponents.CurrencyField puVntDGrF;
    private myComponents.CurrencyField puVntDtF;
    private myComponents.CurrencyField puVntGrF;
    private myComponents.CurrencyField puVntSGrF;
    private javax.swing.JPanel qteAchtePanel;
    private myComponents.IntegerJField qteF;
    private javax.swing.JPanel refPanel;
    private panels.SelectionPanel<Depot> selDepotPanel;
    private panels.SelectionPanel<Produit> selProduitP;
    private javax.swing.JPanel tarifsP;
    private javax.swing.JRadioButton ttcRB;
    private myComponents.MyJField tvaField;
    private javax.swing.JPanel tvaPanel;
    private javax.swing.JComboBox typeMrgDemGrC;
    private javax.swing.JComboBox typeMrgDemGrC1;
    private javax.swing.JComboBox typeMrgGrC;
    private myComponents.MyJComboBox uniteCB;
    // End of variables declaration//GEN-END:variables

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new MajLigneAchPanel(null).showNewPanel(null);
                System.exit(0);
            }
        });
    }
}
