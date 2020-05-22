/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package panels.maj;

import appLogic.PriceCalculator;
import entities.Produit;
import javax.swing.JOptionPane;
import myModels.UniteComboBoxModel;
import dao.QuantifierDAO;
import dao.LotEnStockDAO;
import dbTools.DBManager;
import dialogs.MyJDialog;
import entities.AlerteExp;
import entities.Depot;
import entities.EntityClass;
import entities.EnStock;
import entities.Quantifier;
import entities.Unite;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import panels.CRUDPanel;
import panels.ResultSet2Table_Panel;
import panels.ResultSet_Panel;
import panels.crud.ListeDepotsPanel;
import panels.crud.ListeProduitsPanel;
import printing.PrintingTools;
import tools.DateTools;
import tools.ExceptionReporting;
import tools.MessageReporting;

/**
 *
 * @author alilo
 */
public class MajLotEnStockPanel extends MajPanel<EnStock, LotEnStockDAO> {

    private static final String DEF_DEP = "MajEnStk_Depot";
    private static final String DEF_MRG_DT_MODE = "MajLot_MargeMode";

    private ListeProduitsPanel listeProds;
    private final MajProduitPanel majProdPanel = new MajProduitPanel(listeProds).setEnableGestStock(false);
    private Produit produit;
    private ListeDepotsPanel listeDepots;
    private Depot depot;

    private double qteStk;
    private BigDecimal puAch = new BigDecimal(0);
    private BigDecimal puVntDt = new BigDecimal(0);
    private BigDecimal puVntGr = new BigDecimal(0);
    private Date dateExp, dateAlert, dateEntree;
    private String cbLot;
    private double margeDt, margeGr;

    private final UniteComboBoxModel uniteComboBoxModel = new UniteComboBoxModel();
    private Unite unite;

    /**
     * Creates new form MajCategoriePanel
     *
     * @param listPanel
     */
    public MajLotEnStockPanel(CRUDPanel listPanel) {
        super(listPanel);
        initComponents();
        initMajPanel(fieldsPanel);
        configCalendar();
        initDepotsPanel();
        initProduitsPanel();
        addAction.setEnabled(false);
        uniteComboBox.setModel(uniteComboBoxModel);
        setDefaultFocusedComp(selProduitPanel.getDescField());
    }

    @Override
    public LotEnStockDAO getTableDAO() {
        return LotEnStockDAO.getInstance();
    }

    @Override
    public void loadPreferences() {
        try {
            int defDep = getIntPreference(DEF_DEP, 0);
            setDepot(new Depot(defDep));
            String margeDtMode = getPreference(DEF_MRG_DT_MODE, "%");
            typeMrgDtCB.setSelectedItem(margeDtMode);
        } catch (Exception e) {
            MessageReporting.showMessage(Level.SEVERE, this.getClass(), "loadPreferences()", "inccorect values!");
            ExceptionReporting.showException(e);
        }
    }

    @Override
    public void setActionsShortcuts() {
        super.setActionsShortcuts(); //To change body of generated methods, choose Tools | Templates.
        doOnPress(KeyEvent.VK_F6, 0, gestTarifsAct, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    @java.lang.Override
    public void initFields(EnStock oldEntity) {
        selDepotPanel.setAllowSelChange(oldEntity.getId() <= 0);
        selProduitPanel.setAllowSelChange(oldEntity.getId() <= 0);
        //---
        setProduit(oldEntity.getProduit());
        setDepot(oldEntity.getDepot());
        qteStk = oldEntity.getQte();
        qteStkField.setValue(qteStk);
        //---
        puAchF.setBigDecimalValue(oldEntity.getPuAch());
        puVntDtF.setBigDecimalValue(oldEntity.getPuVntDt());
        puVntGrF.setBigDecimalValue(oldEntity.getPuVntGr());
        calcMargeDt();
        calcMargeGr();
        //---
        setDateExp(oldEntity.getDateExp());
        setDateAlert(oldEntity.getDateAlert());
        cbLot = oldEntity.getCodBar();
        cbLotF.setText(cbLot);
    }

    @Override
    public void setChildEntity(EntityClass childEntity, boolean allowChng) {
        if (childEntity instanceof Produit) {
            selProduitPanel.setAllowSelChange(allowChng);
            setProduit((Produit) childEntity);
        }
        if (childEntity instanceof Depot) {
            selDepotPanel.setAllowSelChange(allowChng);
            setDepot((Depot) childEntity);
        }
    }

    @Override
    public void viewSelEntity(ResultSet2Table_Panel listePanel) {
        if (listePanel instanceof ListeProduitsPanel) {
            if (listePanel.getSelectedEntity().getId() > 0) {
                setProduit(listeProds.getSelectedEntity());
            } else {
                setProduit(null);
            }
        }

        if (listePanel instanceof ListeDepotsPanel) {
            if (listePanel.getSelectedEntity().getId() > 0) {
                setDepot(listeDepots.getSelectedEntity());
            } else {
                setDepot(null);
            }
        }
    }

    public final void initProduitsPanel() {
        if (listeProds == null) {
            listeProds = new ListeProduitsPanel(this, false, false) {
                {
                    mainPanel.remove(statusPanel);
                }
            };
            listeProds.setMajPanel(majProdPanel);
            listeProds.getPropertyChangeSupport().addPropertyChangeListener(ResultSet_Panel.ENTITY_TO_VIEW_PROPERTY, this);
            selProduitPanel.setSelListPanel(listeProds);
        }
    }

    public void setProduit(Produit p) {
        this.produit = p;
        selProduitPanel.setSelEntity(produit);
        initUnitsComboModel();
        if (produit != null) {
            setDefaultFocusedComp(qteStkField);
            margeDtF.setValue(produit.getMargeDt());
            margeGrF.setValue(produit.getMargeGr());
            typeMrgDtCB.setSelectedItem("%");
            typeMrgGrCB.setSelectedItem("%");
            cbLotF.setText(produit.getCodBar());
            tvaField.setText(produit.getFamille().getTva().toString());
        } else {
            margeDtF.setValue(0);
            margeGrF.setValue(0);
            cbLotF.setText("");
            tvaField.setText("");
        }
    }

    public void initUnitsComboModel() {
        uniteComboBoxModel.removeAllElements();
        if (produit != null && produit.getId() > 0) {
            List<Quantifier> quantifs = QuantifierDAO.getInstance().getUnitesOfProd(produit);
            for (Quantifier q : quantifs) {
                Unite u = q.getUnite();
                uniteComboBoxModel.addElement(u);
            }
        }
    }

    public final void initDepotsPanel() {
        listeDepots = new ListeDepotsPanel(this, false).getNavigNEditList();
        listeDepots.getPropertyChangeSupport().addPropertyChangeListener(ResultSet_Panel.ENTITY_TO_VIEW_PROPERTY, this);
        selDepotPanel.setSelListPanel(listeDepots);
    }

    public void setDepot(Depot d) {
        this.depot = d;
        selDepotPanel.setSelEntity(d);
    }

    public final void configCalendar() {
        //dateChooser.setMinSelectableDate(new Date());
        dateChooser.addPropertyChangeListener("date", new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                setDateExp(dateChooser.getDate());
            }
        });
    }

    public void setDateExp(Date dateExp) {
        this.dateExp = dateExp;
        if (dateExp != null && !DateTools.isMaxJavaTime(dateExp)) {
            expireCheckBox.setSelected(true);
            if (dateChooser.getDate() == null || dateExp.compareTo(dateChooser.getDate()) != 0) {
                dateChooser.setDate(dateExp);
            }
        } else {
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

    private void calcMargeDt() {
        double marge = PriceCalculator.getMargeVal(puAchF.getBigDecimalValue(), puVntDtF.getBigDecimalValue(), typeMrgDtCB.getSelectedItem().toString());
        margeDtF.setValue(marge);
        if (marge <= 0) {
            //margeDtF.setSpecialFG(Color.RED);
        } else {
            margeDtF.setSpecialFG(Color.BLACK);
        }
    }

    private void calcMargeGr() {
        double marge = PriceCalculator.getMargeVal(puAchF.getBigDecimalValue(), puVntGrF.getBigDecimalValue(), typeMrgGrCB.getSelectedItem().toString());
        margeGrF.setValue(marge);
        if (marge <= 0) {
            //margeDtF.setSpecialFG(Color.RED);
        } else {
            margeGrF.setSpecialFG(Color.BLACK);
        }
    }

    private void calcPrixVntDt() {
        BigDecimal prixVnt = PriceCalculator.getPrixVnt(puAchF.getBigDecimalValue(), margeDtF.getValue(), typeMrgDtCB.getSelectedItem().toString());
        puVntDtF.setBigDecimalValue(prixVnt);
        if (prixVnt.doubleValue() <= 0) {
            //puVntDtF.setSpecialFG(Color.RED);
        } else {
            puVntDtF.setSpecialFG(Color.BLACK);
        }
    }

    private void calcPrixVntGr() {
        BigDecimal prixVnt = PriceCalculator.getPrixVnt(puAchF.getBigDecimalValue(), margeGrF.getValue(), typeMrgGrCB.getSelectedItem().toString());
        puVntGrF.setBigDecimalValue(prixVnt);
        if (prixVnt.doubleValue() <= 0) {
            //puVntDtF.setSpecialFG(Color.RED);
        } else {
            puVntGrF.setSpecialFG(Color.BLACK);
        }
    }

    private final MyJDialog tarifsDialog = new MyJDialog(this, true, false);

    public void gestTarifs() {
        tarifsDialog.setContentPane(tarifsP);
        tarifsDialog.pack();

        tarifsDialog.setLocation(gestTarifsB.getLocationOnScreen().x, gestTarifsB.getLocationOnScreen().y);
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
        produit = selProduitPanel.getSelEntity();
        if (produit == null) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un Produit SVP!!!", "Attention...", JOptionPane.WARNING_MESSAGE);
            selProduitPanel.requestFocus();
            return false;
        }

        depot = selDepotPanel.getSelEntity();
        if (depot == null || depot.getId() <= 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez le Dépôt SVP!!!", "Attention...", JOptionPane.WARNING_MESSAGE);
            selDepotPanel.requestFocus();
            return false;
        }

        String qteStkStr = qteStkField.getText().trim();
        if (qteStkStr.length() == 0) {
            JOptionPane.showMessageDialog(this, "Entrée la Quantité en stock SVP!!!", "Attention...", JOptionPane.WARNING_MESSAGE);
            if (qteStkStr.length() == 0) {
                qteStkField.requestFocus();
                return false;
            }
        }

        unite = uniteComboBoxModel.getSelectedItem();
        if (unite == null) {
            JOptionPane.showMessageDialog(this, "Sélectionnez l'Unité de Mésure SVP!!!", "Attention...", JOptionPane.WARNING_MESSAGE);
            uniteComboBox.requestFocus();
            return false;
        }

        qteStk = qteStkField.getValue() * unite.getQte();
        // verif. prix
        String puAchatStr = puAchF.getText().trim();
        if (puAchatStr.length() == 0) {
            JOptionPane.showMessageDialog(this, "Entrez le prix unitaire d'achat SVP!!!", "Attention...", JOptionPane.WARNING_MESSAGE);
            puAchF.requestFocus();
            return false;
        }

        String puVntDtStr = puVntDtF.getText().trim();
        if (puVntDtStr.length() == 0) {
            JOptionPane.showMessageDialog(this, "Entrez le prix unitaire de vente en détail SVP!!!", "Attention...", JOptionPane.WARNING_MESSAGE);
            puVntDtF.requestFocus();
            return false;
        }

        String puVntGrStr = puVntGrF.getText().trim();
        if (puVntGrStr.length() == 0) {
            JOptionPane.showMessageDialog(this, "Entrez le prix unitaire de vente en gros SVP!!!", "Attention...", JOptionPane.WARNING_MESSAGE);
            puVntGrF.requestFocus();
            return false;
        }

        puAch = puAchF.getBigDecimalValue();
        puVntDt = puVntDtF.getBigDecimalValue();
        puVntGr = puVntGrF.getBigDecimalValue();

        margeDt = margeDtF.getValue();
        margeGr = margeGrF.getValue();

        if (ttcRB.isSelected()) {
            int tva = produit.getFamille().getTva();
            puAch = PriceCalculator.TTC2HT(puAch, tva);
            puVntDt = PriceCalculator.TTC2HT(puVntDt, tva);
            puVntGr = PriceCalculator.TTC2HT(puVntGr, tva);
        }

        if (puVntDt.doubleValue() <= puAch.doubleValue()) {
            String mess = "Le prix de vente en détail est inférieur (ou égal) au prix d'achat!!!\n"
                    + "Voullez vous le corriger?";
            int rep = JOptionPane.showConfirmDialog(this, mess, "Attention", JOptionPane.YES_NO_OPTION);
            if (rep == JOptionPane.YES_OPTION) {
                puVntDtF.requestFocus();
                return false;
            }
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

        if (puVntGr.doubleValue() != 0 && puVntDt.doubleValue() <= puVntGr.doubleValue()) {
            String mess = "Le prix de vente en détail est inférieur (ou égal) au prix de vente en gros!!!\n"
                    + "Voullez vous le corriger?";
            int rep = JOptionPane.showConfirmDialog(this, mess, "Attention", JOptionPane.YES_NO_OPTION);
            if (rep == JOptionPane.YES_OPTION) {
                puVntDtF.requestFocus();
                return false;
            }
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
    public boolean save() {
        if (verifyFields()) {
            EnStock oldLotEnStk;
            oldLotEnStk = getTableDAO().findLotEnStock(produit, depot, dateExp, puAch);

            if (oldLotEnStk != null && oldLotEnStk.getQte() > 0 && (!isUpdate() || oldLotEnStk.getId().intValue() != getOldEntity().getId())) {
                String mess = "Attention, ce lot du produit déja existe en stock avec une quantité de: "
                        + oldLotEnStk.getQte() + " " + produit.getUnite().getDes()
                        + "\nLa quantité entrée sera ajouter au lot existant. Voullez vous continuer?";
                int rep = JOptionPane.showConfirmDialog(this, mess, "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (rep == JOptionPane.NO_OPTION) {
                    qteStkField.requestFocus();
                    return false;
                }
            }

            EnStock newLotEnStk = new EnStock(0, qteStk, dateExp, cbLot, true);
            if (isUpdate()) {
                newLotEnStk.setDateEntr(getOldEntity().getDateEntr());
            } else {
                newLotEnStk.setDateEntr(new Date());
            }
            newLotEnStk.setPuAch(puAch);
            newLotEnStk.setPuVntDt(puVntDt);
            newLotEnStk.setPuVntGr(puVntGr);
            newLotEnStk.setProduit(produit);
            newLotEnStk.setDepot(depot);
            setEditedEntity(newLotEnStk);

            produit.setMargeDt(margeDt);
            produit.setMargeGr(margeGr);

            return super.save() && saveAlertExp() && produit.update();

        } else {
            setSaved(false);
            return false;
        }
    }

    private boolean saveAlertExp() {
        if (expireCheckBox.isSelected()) {
            AlerteExp alerteExp = null;
            if (isUpdate()) {
                alerteExp = getOldEntity().getAlertExp();
                if (alerteExp != null) {
                    alerteExp.setDateAlert(dateAlert);
                    return alerteExp.update();
                }
            }
            if (alerteExp == null) {
                alerteExp = new AlerteExp(0, dateAlert);
                alerteExp.setEnStock(getEditedEntity());
                return alerteExp.insert();
            }
        } else {
            if (isUpdate()) {
                AlerteExp alerteExp = getOldEntity().getAlertExp();
                if (alerteExp != null) {
                    alerteExp.delete();
                }
            }

        }
        return true;
    }

    @Override
    public void savePreferences() {
        savePreference(DEF_DEP, depot.getId());
        savePreference(DEF_MRG_DT_MODE, typeMrgDtCB.getSelectedItem().toString());
    }

    @Override
    public void clearFields() {
        setDefaultFocusedComp(selProduitPanel.getDescField());
        expireCheckBox.setSelected(false);
        selProduitPanel.setAllowSelChange(true);
        selDepotPanel.setAllowSelChange(true);
        // stock
        qteStkField.setText("0");
        // prix
        puAchF.setValue(0);
        puVntDtF.setValue(0);
        puVntGrF.setValue(0);
        //dateExp = null;
        //dateAlert = null;
        //cb = null;
    }

    private final Action printCodeBarAct = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (save()) {
                HashMap<String, Object> params = new HashMap<>();
                params.put("ID_LOT", getEditedEntity().getId());
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
        selProduitPanel = new panels.SelectionPanel<Produit>();
        refPanel = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        cbLotF = new myComponents.MyJField();
        printCBLotBtn = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jSeparator14 = new javax.swing.JSeparator();
        qtePanel = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        qteStkField = new myComponents.DecimalJField();
        X = new javax.swing.JLabel();
        uniteComboBox = new javax.swing.JComboBox();
        depotPanel = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        selDepotPanel = new panels.SelectionPanel<Depot>();
        jSeparator12 = new javax.swing.JSeparator();
        tvaPanel = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        tvaField = new myComponents.MyJField();
        htRadioBox = new javax.swing.JRadioButton();
        ttcRB = new javax.swing.JRadioButton();
        prixAchP = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        puAchF = new myComponents.CurrencyField();
        currencyUniteLabel2 = new myComponents.CurrencyUniteLabel();
        gestTarifsB = new javax.swing.JButton();
        prixVntDtP = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        puVntDtF = new myComponents.CurrencyField();
        currencyUniteLabel1 = new myComponents.CurrencyUniteLabel();
        margeDtP = new javax.swing.JPanel();
        margeDtChB = new javax.swing.JCheckBox();
        margeDtF = new myComponents.DecimalJField();
        typeMrgDtCB = new javax.swing.JComboBox();
        prixVntGrP = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        puVntGrF = new myComponents.CurrencyField();
        currencyUniteLabel3 = new myComponents.CurrencyUniteLabel();
        margeGrP = new javax.swing.JPanel();
        margeDtChB1 = new javax.swing.JCheckBox();
        margeGrF = new myComponents.DecimalJField();
        typeMrgGrCB = new javax.swing.JComboBox();
        jSeparator13 = new javax.swing.JSeparator();
        expirePanel = new javax.swing.JPanel();
        expireCheckBox = new javax.swing.JCheckBox();
        dateChooser = new myComponents.MyJDateChooser();
        jLabel1 = new javax.swing.JLabel();
        alertCountField = new myComponents.IntegerJField();
        alertTimeComboBox = new javax.swing.JComboBox();
        buttonGroup1 = new javax.swing.ButtonGroup();
        tarifsP = new javax.swing.JPanel();

        fieldsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));

        prodPanel.setFocusable(false);
        prodPanel.setRequestFocusEnabled(false);
        prodPanel.setLayout(new javax.swing.BoxLayout(prodPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setText("Produit En Stock:");
        jLabel6.setPreferredSize(new java.awt.Dimension(131, 0));
        prodPanel.add(jLabel6);

        selProduitPanel.setDescription("Sélec. le produit SVP!");
        selProduitPanel.setShortcutKey("F3");
        prodPanel.add(selProduitPanel);

        refPanel.setLayout(new javax.swing.BoxLayout(refPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel9.setText("Code.Barres du Lot:");
        jLabel9.setPreferredSize(new java.awt.Dimension(131, 16));
        refPanel.add(jLabel9);

        cbLotF.setToolTipText("Cette référence est utile pour accélérer la recherche d'un lot.");
        cbLotF.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        cbLotF.setPreferredSize(new java.awt.Dimension(60, 27));
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

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel13.setText(" (A Utilisé s'il existe plusieurs lots en stock)");
        refPanel.add(jLabel13);

        qtePanel.setPreferredSize(new java.awt.Dimension(520, 27));
        qtePanel.setLayout(new javax.swing.BoxLayout(qtePanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel10.setText("Quantité En Stock:");
        jLabel10.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jLabel10.setPreferredSize(new java.awt.Dimension(131, 0));
        qtePanel.add(jLabel10);
        qtePanel.add(qteStkField);

        X.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        X.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        X.setText(" X Unité de Mésure:");
        X.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        qtePanel.add(X);

        uniteComboBox.setBackground(new java.awt.Color(229, 229, 255));
        uniteComboBox.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        uniteComboBox.setPreferredSize(new java.awt.Dimension(200, 21));
        qtePanel.add(uniteComboBox);

        depotPanel.setLayout(new javax.swing.BoxLayout(depotPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel11.setText("Dépôt / Magasin:");
        jLabel11.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jLabel11.setPreferredSize(new java.awt.Dimension(131, 0));
        depotPanel.add(jLabel11);

        selDepotPanel.setDescription("Sélec. le dépôt SVP!");
        depotPanel.add(selDepotPanel);

        tvaPanel.setLayout(new javax.swing.BoxLayout(tvaPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel19.setText("Taux de TVA (%): ");
        jLabel19.setPreferredSize(new java.awt.Dimension(131, 17));
        tvaPanel.add(jLabel19);

        tvaField.setEditable(false);
        tvaField.setBackground(new java.awt.Color(229, 229, 255));
        tvaField.setToolTipText("Appuyer 'Entrée' et séléctionner la catégorie du produit");
        tvaField.setFocusable(false);
        tvaField.setMinimumSize(new java.awt.Dimension(16, 17));
        tvaField.setPreferredSize(new java.awt.Dimension(25, 25));
        tvaPanel.add(tvaField);

        buttonGroup1.add(htRadioBox);
        htRadioBox.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        htRadioBox.setText("Entrer les Prix en Hors Taxe (HT)");
        htRadioBox.setBorder(null);
        htRadioBox.setFocusable(false);
        htRadioBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        htRadioBox.setPreferredSize(new java.awt.Dimension(250, 19));
        tvaPanel.add(htRadioBox);

        buttonGroup1.add(ttcRB);
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
        jLabel14.setText("Prix Unitaire d'Achat: ");
        prixAchP.add(jLabel14);

        puAchF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                puAchFKeyReleased(evt);
            }
        });
        prixAchP.add(puAchF);
        prixAchP.add(currencyUniteLabel2);

        gestTarifsB.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        gestTarifsB.setText("Autres Tarifs");
        gestTarifsB.setBorderPainted(false);

        prixVntDtP.setLayout(new javax.swing.BoxLayout(prixVntDtP, javax.swing.BoxLayout.LINE_AXIS));

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel16.setText("PU.Vente en Détails:");
        jLabel16.setPreferredSize(new java.awt.Dimension(131, 17));
        prixVntDtP.add(jLabel16);

        puVntDtF.setPreferredSize(new java.awt.Dimension(70, 22));
        puVntDtF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                puVntDtFKeyReleased(evt);
            }
        });
        prixVntDtP.add(puVntDtF);
        prixVntDtP.add(currencyUniteLabel1);

        margeDtP.setLayout(new javax.swing.BoxLayout(margeDtP, javax.swing.BoxLayout.LINE_AXIS));

        margeDtChB.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        margeDtChB.setText("Marge de Bénéfice (Détails):");
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

        typeMrgDtCB.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        typeMrgDtCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "%", "DA" }));
        typeMrgDtCB.setEnabled(false);
        typeMrgDtCB.setFocusable(false);
        typeMrgDtCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeMrgDtCBActionPerformed(evt);
            }
        });
        margeDtP.add(typeMrgDtCB);

        prixVntGrP.setLayout(new javax.swing.BoxLayout(prixVntGrP, javax.swing.BoxLayout.LINE_AXIS));

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel17.setText("PU.Vente en Gros: ");
        jLabel17.setPreferredSize(new java.awt.Dimension(131, 17));
        prixVntGrP.add(jLabel17);

        puVntGrF.setPreferredSize(new java.awt.Dimension(70, 22));
        puVntGrF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                puVntGrFKeyReleased(evt);
            }
        });
        prixVntGrP.add(puVntGrF);
        prixVntGrP.add(currencyUniteLabel3);

        margeGrP.setLayout(new javax.swing.BoxLayout(margeGrP, javax.swing.BoxLayout.LINE_AXIS));

        margeDtChB1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        margeDtChB1.setText("Marge de Bénéfice (Gros):");
        margeDtChB1.setBorder(null);
        margeDtChB1.setFocusable(false);
        margeDtChB1.setPreferredSize(new java.awt.Dimension(185, 17));
        margeDtChB1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                margeDtChB1ActionPerformed(evt);
            }
        });
        margeGrP.add(margeDtChB1);

        margeGrF.setText("0");
        margeGrF.setToolTipText("La marge de bénéfice du vente en Détails");
        margeGrF.setEnabled(false);
        margeGrF.setPreferredSize(new java.awt.Dimension(30, 23));
        margeGrF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                margeGrFKeyReleased(evt);
            }
        });
        margeGrP.add(margeGrF);

        typeMrgGrCB.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        typeMrgGrCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "%", "DA" }));
        typeMrgGrCB.setEnabled(false);
        typeMrgGrCB.setFocusable(false);
        typeMrgGrCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeMrgGrCBActionPerformed(evt);
            }
        });
        margeGrP.add(typeMrgGrCB);

        expirePanel.setPreferredSize(new java.awt.Dimension(144, 27));
        expirePanel.setLayout(new javax.swing.BoxLayout(expirePanel, javax.swing.BoxLayout.LINE_AXIS));

        expireCheckBox.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        expireCheckBox.setText("Date d'Expiration:");
        expireCheckBox.setBorder(null);
        expireCheckBox.setPreferredSize(new java.awt.Dimension(131, 15));
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
        dateChooser.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        dateChooser.setPreferredSize(new java.awt.Dimension(80, 19));
        expirePanel.add(dateChooser);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("   Alerte d'Expiration Avant:");
        expirePanel.add(jLabel1);

        alertCountField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        alertCountField.setText("1");
        alertCountField.setEnabled(false);
        expirePanel.add(alertCountField);

        alertTimeComboBox.setBackground(new java.awt.Color(204, 204, 255));
        alertTimeComboBox.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        alertTimeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Mois", "Jours" }));
        alertTimeComboBox.setEnabled(false);
        expirePanel.add(alertTimeComboBox);

        javax.swing.GroupLayout fieldsPanelLayout = new javax.swing.GroupLayout(fieldsPanel);
        fieldsPanel.setLayout(fieldsPanelLayout);
        fieldsPanelLayout.setHorizontalGroup(
            fieldsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(depotPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(qtePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 610, Short.MAX_VALUE)
            .addComponent(expirePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(refPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSeparator12)
            .addComponent(prodPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSeparator13)
            .addComponent(tvaPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 610, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, fieldsPanelLayout.createSequentialGroup()
                .addGroup(fieldsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(fieldsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(prixAchP, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(prixVntDtP, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE))
                    .addComponent(prixVntGrP, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(fieldsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(margeGrP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(margeDtP, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                    .addComponent(gestTarifsB, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addComponent(jSeparator14)
        );
        fieldsPanelLayout.setVerticalGroup(
            fieldsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, fieldsPanelLayout.createSequentialGroup()
                .addComponent(prodPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(refPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator14, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(qtePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(depotPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator12, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tvaPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(fieldsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(prixAchP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(gestTarifsB, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(fieldsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(margeDtP, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(prixVntDtP, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(fieldsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(prixVntGrP, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(margeGrP, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator13, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(expirePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout tarifsPLayout = new javax.swing.GroupLayout(tarifsP);
        tarifsP.setLayout(tarifsPLayout);
        tarifsPLayout.setHorizontalGroup(
            tarifsPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        tarifsPLayout.setVerticalGroup(
            tarifsPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        setLayout(null);
    }// </editor-fold>//GEN-END:initComponents

    private void expireCheckBoxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_expireCheckBoxStateChanged
        dateChooser.setEnabled(expireCheckBox.isSelected());
        alertCountField.setEnabled(expireCheckBox.isSelected());
        alertTimeComboBox.setEnabled(expireCheckBox.isSelected());
    }//GEN-LAST:event_expireCheckBoxStateChanged

    private void expireCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expireCheckBoxActionPerformed
        if (expireCheckBox.isSelected()) {
            setDateExp(dateChooser.getDate());
            dateChooser.getDateTextFieldEditor().requestFocus();
        } else {
            setDateExp(null);
        }
    }//GEN-LAST:event_expireCheckBoxActionPerformed

    private void printCBLotBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printCBLotBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_printCBLotBtnActionPerformed

    private void puAchFKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_puAchFKeyReleased
        //calcMargeDt();
        if (margeDtF.getValue() != 0) {
            calcPrixVntDt();
        } else {
            calcMargeDt();
        }
        if (margeGrF.getValue() != 0) {
            calcPrixVntGr();
        } else {
            calcMargeGr();
        }
    }//GEN-LAST:event_puAchFKeyReleased

    private void puVntDtFKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_puVntDtFKeyReleased
        calcMargeDt();
    }//GEN-LAST:event_puVntDtFKeyReleased

    private void margeDtChBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_margeDtChBActionPerformed
        margeDtF.setEnabled(margeDtChB.isSelected());
        typeMrgDtCB.setEnabled(margeDtChB.isSelected());
    }//GEN-LAST:event_margeDtChBActionPerformed

    private void margeDtFKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_margeDtFKeyReleased
        calcPrixVntDt();
    }//GEN-LAST:event_margeDtFKeyReleased

    private void typeMrgDtCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeMrgDtCBActionPerformed
        calcPrixVntDt();
    }//GEN-LAST:event_typeMrgDtCBActionPerformed

    private void puVntGrFKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_puVntGrFKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_puVntGrFKeyReleased

    private void margeDtChB1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_margeDtChB1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_margeDtChB1ActionPerformed

    private void margeGrFKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_margeGrFKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_margeGrFKeyReleased

    private void typeMrgGrCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeMrgGrCBActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_typeMrgGrCBActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel X;
    private myComponents.IntegerJField alertCountField;
    private javax.swing.JComboBox alertTimeComboBox;
    private javax.swing.ButtonGroup buttonGroup1;
    private myComponents.MyJField cbLotF;
    private myComponents.CurrencyUniteLabel currencyUniteLabel1;
    private myComponents.CurrencyUniteLabel currencyUniteLabel2;
    private myComponents.CurrencyUniteLabel currencyUniteLabel3;
    private myComponents.MyJDateChooser dateChooser;
    private javax.swing.JPanel depotPanel;
    private javax.swing.JCheckBox expireCheckBox;
    private javax.swing.JPanel expirePanel;
    private javax.swing.JPanel fieldsPanel;
    private javax.swing.JButton gestTarifsB;
    private javax.swing.JRadioButton htRadioBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator14;
    private javax.swing.JCheckBox margeDtChB;
    private javax.swing.JCheckBox margeDtChB1;
    private myComponents.DecimalJField margeDtF;
    private javax.swing.JPanel margeDtP;
    private myComponents.DecimalJField margeGrF;
    private javax.swing.JPanel margeGrP;
    private javax.swing.JButton printCBLotBtn;
    private javax.swing.JPanel prixAchP;
    private javax.swing.JPanel prixVntDtP;
    private javax.swing.JPanel prixVntGrP;
    private javax.swing.JPanel prodPanel;
    private myComponents.CurrencyField puAchF;
    private myComponents.CurrencyField puVntDtF;
    private myComponents.CurrencyField puVntGrF;
    private javax.swing.JPanel qtePanel;
    private myComponents.DecimalJField qteStkField;
    private javax.swing.JPanel refPanel;
    private panels.SelectionPanel<Depot> selDepotPanel;
    private panels.SelectionPanel<Produit> selProduitPanel;
    private javax.swing.JPanel tarifsP;
    private javax.swing.JRadioButton ttcRB;
    private myComponents.MyJField tvaField;
    private javax.swing.JPanel tvaPanel;
    private javax.swing.JComboBox typeMrgDtCB;
    private javax.swing.JComboBox typeMrgGrCB;
    private javax.swing.JComboBox uniteComboBox;
    // End of variables declaration//GEN-END:variables

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {

                //System.out.println(""+getClass().getResource("/reporting/DetialsVnt.jasper").getPath());
                new MajLotEnStockPanel(null).showNewPanel(null);
                System.exit(0);
            }
        });
    }
}
