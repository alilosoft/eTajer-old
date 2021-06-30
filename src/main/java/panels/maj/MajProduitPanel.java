/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package panels.maj;

import panels.crud.ListeUUnitairePanel;
import panels.crud.ListeLotsEnStockPanel;
import panels.crud.ListeMorceauxPanel;
import panels.crud.ListeFamillesPanel;
import panels.crud.ListeColisagesPanel;
import panels.crud.ListeUnitesPanel;
import panels.crud.ListeDepotsPanel;
import panels.crud.ListeCategoriesPanel;
import dao.UniteDAO;
import dao.CategorieDAO;
import dao.FamilleDAO;
import dao.QuantifierDAO;
import dao.ProduitDAO;
import dialogs.SelectionDialog;
import entities.*;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import panels.CRUDPanel;
import panels.RSTablePanel;
import panels.ResultSet_Panel;
import tools.ExceptionReporting;
import tools.MessageReporting;

/**
 *
 * @author alilo
 */
public class MajProduitPanel extends MajPanel<Produit, ProduitDAO> {

    private static final String DEF_FAM = "MajProd_Famille";
    private static final String DEF_CATEG = "MajProd_Categ";
    private static final String DEF_UNITE = "MajProd_Unite";
    private static final String DEF_DEPOT = "MajProd_Depot";
    private ListeFamillesPanel famillesPanel;
    private ListeCategoriesPanel categoriesPanel;
    private ListeUnitesPanel unitesPanel;
    private ListeColisagesPanel colisagesPanel;
    private ListeMorceauxPanel morcellesPanel;
    private ListeDepotsPanel listeDepots;
    private Famille famille;
    private int tva;
    private Categorie categorie;
    private String cb;
    private String des;

    private Unite unite;
    private int qteMin;
    private int qteMax;
    private ListeLotsEnStockPanel listeLotsEnStk;
    private boolean gestStkEnabled;
    private boolean enableGros;
    private double margeDt, margeGr, margeDGr, margeSGr;

    /**
     * Creates new form MajProduitPanel
     *
     * @param listPanel
     */
    public MajProduitPanel(CRUDPanel listPanel) {
        super(listPanel);
        initComponents();
        initMajPanel(fieldsPanel);
        initFamPanel();
        initCategPanel();
        initUnitesPanel();
        setDefaultFocusedComp(selFamPanel.getDescField());
    }

    public MajProduitPanel setEnableGestStock(boolean enabled) {
        gestStkEnabled = enabled;
        if (enabled) {
            initLotsPanel();
        } else {
            listeLotsEnStk = null;
            addLotAct.setEnabled(enabled);
            showLotsAct.setEnabled(enabled);
        }
        return this;
    }

    @Override
    public void setActionsShortcuts() {
        super.setActionsShortcuts();
        doOnPress(KeyEvent.VK_SPACE, KeyEvent.CTRL_DOWN_MASK, swichTabs, JComponent.WHEN_IN_FOCUSED_WINDOW);
        doOnPress(KeyEvent.VK_F6, 0, addLotAct, JComponent.WHEN_IN_FOCUSED_WINDOW);
        doOnPress(KeyEvent.VK_F7, 0, showLotsAct, JComponent.WHEN_IN_FOCUSED_WINDOW);
        //clear the default ENTER action that wase been bounded to okAction;
        //doOnPress(KeyEvent.VK_ENTER, 0, noAction, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private final Action swichTabs = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            tabPane.setSelectedIndex((tabPane.getSelectedIndex() + 1) % tabPane.getTabCount());
        }
    };

    @Override
    public ProduitDAO getTableDAO() {
        return ProduitDAO.getInstance();
    }

    @Override
    final public void loadPreferences() {

        int defFamID = 0;
        int defCatID = 0;
        int defUnitID = 0;
        int defDepotID = 0;

        try {
            defFamID = getIntPreference(DEF_FAM, 0);
            defCatID = getIntPreference(DEF_CATEG, 0);
            defUnitID = getIntPreference(DEF_UNITE, 0);
            defDepotID = getIntPreference(DEF_DEPOT, 0);
        } catch (Exception e) {
            MessageReporting.showMessage(Level.SEVERE, this.getClass(), "loadPreferences()", "inccorect values!");
        }
        setFamille(FamilleDAO.getInstance().getObjectByID(defFamID));
        setCategorie(CategorieDAO.getInstance().getObjectByID(defCatID));
        setUnite(UniteDAO.getInstance().getObjectByID(defUnitID));
    }

    @Override
    public void initFields(Produit oldEntity) {
        setDefaultFocusedComp(cbF);
        desF.setText(oldEntity.getDes());
        cbF.setText(oldEntity.getCodBar());
        setFamille(oldEntity.getFamille());
        setCategorie(oldEntity.getCateg());
        //Unité Tab
        List<Quantifier> unites = oldEntity.getUnitesList();
        for (Quantifier q : unites) {
            Unite u = q.getUnite();
            if (q.getUniteDt()) {
                unite = u;
                setUnite(unite);
            }
        }
        // StockTab
        qteMinF.setText(oldEntity.getQteMin() + "");
        qteMaxF.setText(oldEntity.getQteMax() + "");
        if (listeLotsEnStk != null) {
            listeLotsEnStk.setMasterEntity(oldEntity);
        }

        margeDtF.setValue(oldEntity.getMargeDt());
        margeGrF.setValue(oldEntity.getMargeGr());
    }

    private void setFamille(Famille f) {
        selFamPanel.setSelEntity(f);
        if (f == null || f.getId() <= 0) {
            famille = null;
        } else {
            famille = f;
            tva = famille.getTva();
        }
    }

    private void setCategorie(Categorie c) {
        categorie = c;
        selCategPanel.setSelEntity(c);
    }

    private void setUnite(Unite u) {
        unite = u;
        selUnitePanel.setSelEntity(u);
        if (u != null) {
            uniteMinL.setText(u.getDes());
            uniteMaxL.setText(u.getDes());
        }
    }

    @Override
    public void setChildEntity(EntityClass childEntity, boolean allowChng) {
        if (childEntity instanceof Famille) {
            setFamille((Famille) childEntity);
        }

        if (childEntity instanceof Categorie) {
            setCategorie((Categorie) childEntity);
        }

        if (childEntity instanceof Unite) {
            setUnite((Unite) childEntity);
        }
    }

    @Override
    public boolean verifyFields() {
        // verif. famille
        famille = selFamPanel.getSelEntity();
        if (famille == null) {
            JOptionPane.showMessageDialog(this, "Sélectionner une famille SVP!!!", "Attention...", JOptionPane.WARNING_MESSAGE);
            selFamPanel.requestFocus();
            selFamPanel.showSelList();
            return false;
        }
        // verif. catégorie
        categorie = selCategPanel.getSelEntity();
        if (categorie == null) {
            JOptionPane.showMessageDialog(this, "Sélectionner une catégorie SVP!!!", "Attention...", JOptionPane.WARNING_MESSAGE);
            selCategPanel.requestFocus();
            selCategPanel.showSelList();
            return false;
        }
        // verif. réference
        String refStr = cbF.getText().trim();
        if (refStr.length() == 0) {
            JOptionPane.showMessageDialog(this, "Donnez une référence/code-barres au produit SVP!!!"
                    + "\nElle sera utilisée pour accélérer la recherche.", "Attention...", JOptionPane.WARNING_MESSAGE);
            cbF.requestFocus();
            return false;
        } else {
            int idProd = 0;
            if (isUpdate()) {
                idProd = getEditedEntity().getId();
            }
            if (ProduitDAO.getInstance().checkBarCode(refStr, idProd)) {
                String mess = "Ce Code-Barres ou Référence est déja associer a un autre produit!\n"
                        + "Utiliser un autre Code-Barres ou Référence. SVP!";
                JOptionPane.showMessageDialog(this, mess, "Attention!", JOptionPane.ERROR_MESSAGE);
                cbF.requestFocus();
                return false;
            }
        }
        // verif. désignation
        String desStr = desF.getText().trim();
        if (desStr.length() == 0) {
            JOptionPane.showMessageDialog(this, "Donnez une désignation au produit SVP!!!", "Attention...", JOptionPane.WARNING_MESSAGE);
            desF.requestFocus();
            return false;
        }
        // verif. unité
        unite = selUnitePanel.getSelEntity();
        if (unite == null) {
            JOptionPane.showMessageDialog(this, "Sélectionner l'unité de mésure unitaire de ce produit, SVP!!!", "Attention...", JOptionPane.WARNING_MESSAGE);
            tabPane.setSelectedComponent(unitsTab);
            selUnitePanel.requestFocus();
            selUnitePanel.showSelList();
            return false;
        }
        // verif. stock
        String qteMinStr = qteMinF.getText().trim();
        if (qteMinStr.length() == 0) {
            JOptionPane.showMessageDialog(this, "Entrez le seuil minimum du quantité en stock SVP!!!", "Attention...", JOptionPane.WARNING_MESSAGE);
            qteMinF.requestFocus();
            return false;
        }

        String qteMaxStr = qteMaxF.getText().trim();
        if (qteMaxStr.length() == 0) {
            JOptionPane.showMessageDialog(this, "Entrez le seuil maximum du quantité en stock SVP!!!", "Attention...", JOptionPane.WARNING_MESSAGE);
            qteMaxF.requestFocus();
            return false;
        }
        // set variables
        try {
            cb = refStr.toUpperCase();
            des = desStr.toUpperCase();
            qteMin = qteMinF.getValue();
            qteMax = qteMaxF.getValue();

            margeDt = margeDtF.getValue();
            margeGr = margeGrF.getValue();
            return true;
        } catch (HeadlessException | NumberFormatException e) {
            ExceptionReporting.showException(e);
            return false;
        }
    }

    @Override
    public void savePreferences() {
        savePreference(DEF_FAM, famille.getId());
        savePreference(DEF_CATEG, categorie.getId());
        savePreference(DEF_UNITE, unite.getId());
    }

    @Override
    public boolean save() {
        if (verifyFields()) {
            int id = 0;
            if (isSaved()) {
                // If is already saved the super.save() will call the update(), so we must to set the oldEntity.
                setOldEntity(getEditedEntity());
            }
            Produit produit = new Produit(id, cb, des, qteMin, qteMax, BigDecimal.ZERO, margeDt, margeGr, margeDGr, margeSGr);
            produit.setFamille(famille);
            produit.setCateg(categorie);
            setEditedEntity(produit);
            return super.save() && saveQauntifier();
        } else {
            setSaved(false);
            return false;
        }
    }

    public boolean saveQauntifier() {
        // delete all unites from quantif before saving new ones!
        QuantifierDAO.getInstance().deleteByProduit(getEditedEntity());
        // save unité unitaire;
        Quantifier quantifier = new Quantifier(0);
        quantifier.setProduit(getEditedEntity());
        quantifier.setUnite(unite);
        boolean save = QuantifierDAO.getInstance().insert(quantifier);
        return save;
    }

    public void addLot() {
        if (save()) {
            listeLotsEnStk.setMasterEntity(getEditedEntity());
            listeLotsEnStk.insert();
        }
    }

    private final Action addLotAct = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            addLot();
        }
    };

    private final SelectionDialog sd = new SelectionDialog();

    public void showLots() {
        if (save()) {
            listeLotsEnStk.setMasterEntity(getEditedEntity());
            //sd.showPanel(listeLotsEnStk, null, selLotLinkB);
            listeLotsEnStk.showPanel(true);
        }
    }

    private final Action showLotsAct = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            showLots();
        }
    };

    @Override
    public void clearFields() {
        // Clear Variables
        des = null;
        cb = null;
        qteMin = 0;
        qteMax = 0;
        // Clear UI
        cbF.setText("");
        desF.setText("");
        qteMinF.setText("0");
        qteMaxF.setText("0");
        //
        //tabPane.setSelectedIndex(0);
        //
        if (listeLotsEnStk != null) {
            listeLotsEnStk.setMasterEntity(new Produit());
        }
        desF.requestFocus();
    }

    @Override
    public void viewSelEntity(RSTablePanel listePanel) {

        if (listePanel instanceof ListeFamillesPanel) {
            if (listePanel.getSelectedEntity().getId() > 0) {
                setFamille(famillesPanel.getSelectedEntity());
            } else {
                setFamille(null);
            }
            return;
        }

        if (listePanel instanceof ListeCategoriesPanel) {
            if (listePanel.getSelectedEntity().getId() > 0) {
                setCategorie(categoriesPanel.getSelectedEntity());
            } else {
                setCategorie(null);
            }
            return;
        }

        if (listePanel instanceof ListeUnitesPanel) {
            if (listePanel.getSelectedEntity().getId() > 0) {
                setUnite(unitesPanel.getSelectedEntity());
            } else {
                setUnite(null);
            }
        }
    }

    private void initFamPanel() {
        famillesPanel = new ListeFamillesPanel(this, false);
        famillesPanel.getPropertyChangeSupport().addPropertyChangeListener(ResultSet_Panel.ENTITY_TO_VIEW_PROPERTY, this);
        selFamPanel.setSelListPanel(famillesPanel);
    }

    private void initCategPanel() {
        categoriesPanel = new ListeCategoriesPanel(this, false);
        categoriesPanel.getPropertyChangeSupport().addPropertyChangeListener(ResultSet_Panel.ENTITY_TO_VIEW_PROPERTY, this);
        if (famillesPanel == null) {
            initFamPanel();
        }
        categoriesPanel.setMasterPanel(famillesPanel, null);
        selCategPanel.setSelListPanel(categoriesPanel);
    }

    private void initUnitesPanel() {
        unitesPanel = new ListeUUnitairePanel(this, false);
        colisagesPanel = new ListeColisagesPanel(this, false);

        unitesPanel.getPropertyChangeSupport().addPropertyChangeListener(ResultSet_Panel.ENTITY_TO_VIEW_PROPERTY, this);
        colisagesPanel.getPropertyChangeSupport().addPropertyChangeListener(ResultSet_Panel.ENTITY_TO_VIEW_PROPERTY, this);

        selUnitePanel.setSelListPanel(unitesPanel);
        selColisPanel.setSelListPanel(colisagesPanel);
    }

    private void initLotsPanel() {
        listeLotsEnStk = new ListeLotsEnStockPanel(this, false) {
            {
                setPreferredSize(new Dimension(750, 550));
            }

            @Override
            public ListeLotsEnStockPanel initTableView() {
                super.initTableView();
                getTable().setColumnVisible("Réf.Produit", false);
                getTable().setColumnVisible("Désignation", false);
                getTable().setColumnVisible("PU.Achat", false);
                getTable().setColumnVisible("Actif", false);
                return this;
            }
        };
        //listeLotsEnStk = (ListeLotsEnStockPanel) listeLotsEnStk.getNavigOnlyList();
        if (getEditedEntity() instanceof Produit) {
            listeLotsEnStk.setMasterEntity(getEditedEntity());
        } else {
            listeLotsEnStk.setMasterEntity(new Produit());
        }
        listeLotsEnStk.getPropertyChangeSupport().addPropertyChangeListener(ResultSet_Panel.ENTITY_TO_VIEW_PROPERTY, this);
        //lotsHolderP.add(listeLotsEnStk);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        currencyGroup = new javax.swing.ButtonGroup();
        priceGroup = new javax.swing.ButtonGroup();
        defaultUnitRG = new javax.swing.ButtonGroup();
        fieldsPanel = new javax.swing.JPanel();
        tabPane = new javax.swing.JTabbedPane();
        generalTab = new javax.swing.JPanel();
        famPanel = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        selFamPanel = new panels.SelectionPanel<Famille>();
        categPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        selCategPanel = new panels.SelectionPanel<Categorie>();
        desPanel = new javax.swing.JPanel();
        cbImgPanel = new myComponents.ImagePanel();
        cbF = new myComponents.MyJField();
        jLabel1 = new javax.swing.JLabel();
        desF = new myComponents.MyJField();
        jSeparator3 = new javax.swing.JSeparator();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        selUnitePanel = new panels.SelectionPanel<Unite>();
        qteMinMaxPanel = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        qteMinF = new myComponents.IntegerJField();
        uniteMinL = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        qteMaxF = new myComponents.IntegerJField();
        uniteMaxL = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jSeparator5 = new javax.swing.JSeparator();
        go2StockButton4 = new javax.swing.JButton();
        unitsTab = new javax.swing.JPanel();
        colisP = new javax.swing.JPanel();
        selColisPanel = new panels.SelectionPanel<Unite>();
        jLabel26 = new javax.swing.JLabel();
        jSeparator19 = new javax.swing.JSeparator();
        go2StockButton1 = new javax.swing.JButton();
        prixTab = new javax.swing.JPanel();
        go2StockButton = new javax.swing.JButton();
        jSeparator11 = new javax.swing.JSeparator();
        margeDtP = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        margeDtF = new myComponents.DecimalJField();
        modeMrgDtCB = new javax.swing.JComboBox();
        margeGrP = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        margeGrF = new myComponents.DecimalJField();
        typeMrgGrC = new javax.swing.JComboBox();

        fieldsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 5, 5, 5));
        fieldsPanel.setFocusable(false);
        fieldsPanel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        fieldsPanel.setRequestFocusEnabled(false);

        tabPane.setToolTipText("(Ctrl+Espace)");
        tabPane.setFocusable(false);
        tabPane.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tabPane.setRequestFocusEnabled(false);

        generalTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 3, 5, 3));

        famPanel.setLayout(new javax.swing.BoxLayout(famPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel7.setText("Famille: ");
        jLabel7.setMinimumSize(new java.awt.Dimension(71, 16));
        jLabel7.setPreferredSize(new java.awt.Dimension(87, 15));
        famPanel.add(jLabel7);

        selFamPanel.setDescription("Sélectionnez une famille SVP!");
        famPanel.add(selFamPanel);

        categPanel.setLayout(new javax.swing.BoxLayout(categPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setText("Catégorie:");
        jLabel6.setPreferredSize(new java.awt.Dimension(87, 15));
        categPanel.add(jLabel6);

        selCategPanel.setDescription("Sélectionnez une catégorie  SVP!");
        categPanel.add(selCategPanel);

        desPanel.setMinimumSize(new java.awt.Dimension(75, 32));
        desPanel.setPreferredSize(new java.awt.Dimension(83, 32));
        desPanel.setLayout(new javax.swing.BoxLayout(desPanel, javax.swing.BoxLayout.LINE_AXIS));

        cbImgPanel.setBorder(null);
        cbImgPanel.setImgLocationPath("/res/png/cb.png");
        cbImgPanel.setMinimumSize(new java.awt.Dimension(70, 32));
        cbImgPanel.setPreferredSize(new java.awt.Dimension(87, 32));

        javax.swing.GroupLayout cbImgPanelLayout = new javax.swing.GroupLayout(cbImgPanel);
        cbImgPanel.setLayout(cbImgPanelLayout);
        cbImgPanelLayout.setHorizontalGroup(
            cbImgPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 87, Short.MAX_VALUE)
        );
        cbImgPanelLayout.setVerticalGroup(
            cbImgPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 32, Short.MAX_VALUE)
        );

        desPanel.add(cbImgPanel);

        cbF.setToolTipText("La référence ou le code bare du produit. Util pour accélérer la recherche.");
        cbF.setAutoCapsLock(true);
        cbF.setPreferredSize(new java.awt.Dimension(50, 23));
        cbF.setPrefsLangKey("MajProd_Ref_Lang");
        cbF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cbFKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cbFKeyReleased(evt);
            }
        });
        desPanel.add(cbF);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("   Désignation: ");
        desPanel.add(jLabel1);

        desF.setPreferredSize(new java.awt.Dimension(260, 23));
        desF.setPrefsLangKey("MajProd_Des_Lang");
        desPanel.add(desF);

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("Unité de Mésure: ");
        jPanel3.add(jLabel3);

        selUnitePanel.setDescription("Sélec. l'Unité Unitaire SVP!");
        selUnitePanel.setPreferredSize(new java.awt.Dimension(155, 25));
        jPanel3.add(selUnitePanel);

        qteMinMaxPanel.setPreferredSize(new java.awt.Dimension(440, 27));
        qteMinMaxPanel.setLayout(new javax.swing.BoxLayout(qteMinMaxPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel23.setText("Qte d'alert minimum: ");
        qteMinMaxPanel.add(jLabel23);
        qteMinMaxPanel.add(qteMinF);

        uniteMinL.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        uniteMinL.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        uniteMinL.setText("?");
        uniteMinL.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 1));
        uniteMinL.setMaximumSize(new java.awt.Dimension(9, 27));
        uniteMinL.setMinimumSize(new java.awt.Dimension(9, 27));
        uniteMinL.setOpaque(true);
        qteMinMaxPanel.add(uniteMinL);

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel24.setText("   Qte d'alert maximum: ");
        qteMinMaxPanel.add(jLabel24);
        qteMinMaxPanel.add(qteMaxF);

        uniteMaxL.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        uniteMaxL.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        uniteMaxL.setText("?");
        uniteMaxL.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 1));
        uniteMaxL.setMaximumSize(new java.awt.Dimension(9, 27));
        uniteMaxL.setMinimumSize(new java.awt.Dimension(9, 27));
        uniteMaxL.setOpaque(true);
        qteMinMaxPanel.add(uniteMaxL);

        go2StockButton4.setAction(swichTabs);
        go2StockButton4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        go2StockButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/last16.png"))); // NOI18N
        go2StockButton4.setText("Page suivante (Ctrl+Espace)");
        go2StockButton4.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        javax.swing.GroupLayout generalTabLayout = new javax.swing.GroupLayout(generalTab);
        generalTab.setLayout(generalTabLayout);
        generalTabLayout.setHorizontalGroup(
            generalTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(categPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(desPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSeparator3)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 677, Short.MAX_VALUE)
            .addComponent(qteMinMaxPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(generalTabLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(go2StockButton4))
            .addComponent(famPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSeparator4)
            .addComponent(jSeparator5)
        );
        generalTabLayout.setVerticalGroup(
            generalTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generalTabLayout.createSequentialGroup()
                .addComponent(famPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(categPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(desPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(qteMinMaxPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(go2StockButton4))
        );

        tabPane.addTab("Général", new javax.swing.ImageIcon(getClass().getResource("/res/icons/details.png")), generalTab); // NOI18N

        unitsTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 3, 5, 3));

        colisP.setLayout(new javax.swing.BoxLayout(colisP, javax.swing.BoxLayout.LINE_AXIS));

        selColisPanel.setDescription("Sélectionnez le Colisage SVP!");
        colisP.add(selColisPanel);

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel26.setText("Si ce produit peut être vendu par colis de plusieurs unités (Colis 12, Fardo 8, Carton 24, Sac 50...etc): ");

        go2StockButton1.setAction(swichTabs);
        go2StockButton1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        go2StockButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/last16.png"))); // NOI18N
        go2StockButton1.setText("Page suivante (Ctrl+Espace)");
        go2StockButton1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        javax.swing.GroupLayout unitsTabLayout = new javax.swing.GroupLayout(unitsTab);
        unitsTab.setLayout(unitsTabLayout);
        unitsTabLayout.setHorizontalGroup(
            unitsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(colisP, javax.swing.GroupLayout.DEFAULT_SIZE, 677, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, unitsTabLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(go2StockButton1))
            .addComponent(jSeparator19)
            .addGroup(unitsTabLayout.createSequentialGroup()
                .addComponent(jLabel26)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        unitsTabLayout.setVerticalGroup(
            unitsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(unitsTabLayout.createSequentialGroup()
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(colisP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 143, Short.MAX_VALUE)
                .addComponent(jSeparator19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(go2StockButton1))
        );

        tabPane.addTab("Conditionnement", new javax.swing.ImageIcon(getClass().getResource("/res/icons/unite16.png")), unitsTab); // NOI18N

        prixTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 3, 5, 3));
        prixTab.setToolTipText("Utiliser (Ctrl+Espace) pour basculer entre les deux catégories!");

        go2StockButton.setAction(swichTabs);
        go2StockButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        go2StockButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/last16.png"))); // NOI18N
        go2StockButton.setText("Page suivante (Ctrl+Espace)");
        go2StockButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        margeDtP.setLayout(new javax.swing.BoxLayout(margeDtP, javax.swing.BoxLayout.LINE_AXIS));

        jLabel33.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel33.setText("Marge de vente en Détail: ");
        jLabel33.setPreferredSize(new java.awt.Dimension(264, 17));
        margeDtP.add(jLabel33);

        margeDtF.setText("0");
        margeDtF.setToolTipText("La marge de bénéfice du vente en Détails");
        margeDtF.setPreferredSize(new java.awt.Dimension(30, 23));
        margeDtP.add(margeDtF);

        modeMrgDtCB.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        modeMrgDtCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "%", "DA" }));
        modeMrgDtCB.setEnabled(false);
        modeMrgDtCB.setFocusable(false);
        margeDtP.add(modeMrgDtCB);

        margeGrP.setLayout(new javax.swing.BoxLayout(margeGrP, javax.swing.BoxLayout.LINE_AXIS));

        jLabel32.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel32.setText("Marge de Vente en Gros: ");
        jLabel32.setPreferredSize(new java.awt.Dimension(264, 17));
        margeGrP.add(jLabel32);

        margeGrF.setText("0");
        margeGrF.setToolTipText("La marge de bénéfice du vente en Détails");
        margeGrF.setPreferredSize(new java.awt.Dimension(30, 23));
        margeGrP.add(margeGrF);

        typeMrgGrC.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        typeMrgGrC.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "%", "DA" }));
        typeMrgGrC.setEnabled(false);
        margeGrP.add(typeMrgGrC);

        javax.swing.GroupLayout prixTabLayout = new javax.swing.GroupLayout(prixTab);
        prixTab.setLayout(prixTabLayout);
        prixTabLayout.setHorizontalGroup(
            prixTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, prixTabLayout.createSequentialGroup()
                .addGap(0, 452, Short.MAX_VALUE)
                .addComponent(go2StockButton))
            .addComponent(jSeparator11)
            .addGroup(prixTabLayout.createSequentialGroup()
                .addGroup(prixTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(margeDtP, javax.swing.GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE)
                    .addComponent(margeGrP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        prixTabLayout.setVerticalGroup(
            prixTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(prixTabLayout.createSequentialGroup()
                .addComponent(margeDtP, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(margeGrP, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 133, Short.MAX_VALUE)
                .addComponent(jSeparator11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(go2StockButton))
        );

        tabPane.addTab("Tarification", new javax.swing.ImageIcon(getClass().getResource("/res/icons/dollar16.png")), prixTab); // NOI18N

        javax.swing.GroupLayout fieldsPanelLayout = new javax.swing.GroupLayout(fieldsPanel);
        fieldsPanel.setLayout(fieldsPanelLayout);
        fieldsPanelLayout.setHorizontalGroup(
            fieldsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabPane)
        );
        fieldsPanelLayout.setVerticalGroup(
            fieldsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        tabPane.getAccessibleContext().setAccessibleName("Articles");

        setRequestFocusEnabled(false);
        setLayout(null);
    }// </editor-fold>//GEN-END:initComponents

    private void majArticlePanelFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_majArticlePanelFocusGained
    }//GEN-LAST:event_majArticlePanelFocusGained

    private void cbFKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cbFKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evt.consume();
        }
    }//GEN-LAST:event_cbFKeyPressed

    private void cbFKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cbFKeyReleased

    }//GEN-LAST:event_cbFKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel categPanel;
    private myComponents.MyJField cbF;
    private myComponents.ImagePanel cbImgPanel;
    private javax.swing.JPanel colisP;
    private javax.swing.ButtonGroup currencyGroup;
    private javax.swing.ButtonGroup defaultUnitRG;
    private myComponents.MyJField desF;
    private javax.swing.JPanel desPanel;
    private javax.swing.JPanel famPanel;
    private javax.swing.JPanel fieldsPanel;
    private javax.swing.JPanel generalTab;
    private javax.swing.JButton go2StockButton;
    private javax.swing.JButton go2StockButton1;
    private javax.swing.JButton go2StockButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator19;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private myComponents.DecimalJField margeDtF;
    private javax.swing.JPanel margeDtP;
    private myComponents.DecimalJField margeGrF;
    private javax.swing.JPanel margeGrP;
    private javax.swing.JComboBox modeMrgDtCB;
    private javax.swing.ButtonGroup priceGroup;
    private javax.swing.JPanel prixTab;
    private myComponents.IntegerJField qteMaxF;
    private myComponents.IntegerJField qteMinF;
    private javax.swing.JPanel qteMinMaxPanel;
    private panels.SelectionPanel<Categorie> selCategPanel;
    private panels.SelectionPanel<Unite> selColisPanel;
    private panels.SelectionPanel<Famille> selFamPanel;
    private panels.SelectionPanel<Unite> selUnitePanel;
    private javax.swing.JTabbedPane tabPane;
    private javax.swing.JComboBox typeMrgGrC;
    private javax.swing.JLabel uniteMaxL;
    private javax.swing.JLabel uniteMinL;
    private javax.swing.JPanel unitsTab;
    // End of variables declaration//GEN-END:variables

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new MajProduitPanel(null).showNewPanel(null);
                System.exit(0);
            }
        });
    }
}
