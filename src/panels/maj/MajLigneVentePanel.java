/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package panels.maj;

import entities.*;
import java.awt.Color;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import myModels.UniteComboBoxModel;
import panels.ResultSet_Panel;
import dao.LigneVenteDAO;
import dao.QuantifierDAO;
import java.awt.Dimension;
import java.util.Date;
import panels.CRUDPanel;
import panels.ResultSet2Table_Panel;
import panels.crud.ListeLotsAVendrePanel;
import panels.crud.ListeProduitsPanel;
import tools.DateTools;

/**
 *
 * @author alilo
 */
public class MajLigneVentePanel extends MajPanel<LigneVnt, LigneVenteDAO> {

    // Vente Fields
    private Vente vente = new Vente(0, 0, null, null, false);
    // Ligne de Vente Fields
    private BigDecimal puVente = new BigDecimal(0);
    private double qteVendu = 1;
    private double qteUnitaireVendu = 0;
    private Unite uniteVente;
    private BigDecimal totalLVnt = new BigDecimal(0);
    // EnStock Fields
    private ListeLotsAVendrePanel listeProdsEnStk;
    private EnStock lotEnStock;
    private Produit produit;
    private Depot depot;
    private Date dateExp;
    private double qteStk;
    // UI
    private final UniteComboBoxModel uniteComboBoxModel = new UniteComboBoxModel();

    private ListeProduitsPanel listeProds;
    private final MajProduitPanel majProdPanel = new MajProduitPanel(listeProds).setEnableGestStock(false);

    /**
     * @param listPanel
     */
    public MajLigneVentePanel(CRUDPanel listPanel) {
        super(listPanel);
        initComponents();
        initMajPanel(fieldsPanel);
        initUniteVenteComboBox();
        vente.setTypeVnt(TypeVnt.DETAIL);
        initProduitsPanel();
        initProdsAVendrePanel();
        //addAction.setEnabled(false);
        setDefaultFocusedComp(selProdP.getDescField());
    }

    // Getters
    @Override
    public LigneVenteDAO getTableDAO() {
        return LigneVenteDAO.getInstance();
    }

    @Override
    public void setActionsShortcuts() {
        super.setActionsShortcuts();
        doOnRelease(KeyEvent.VK_F2, 0, fixeQteStkAction, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        doOnPress(KeyEvent.VK_SPACE, InputEvent.CTRL_DOWN_MASK, new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                puAchatField.setBackground(Color.WHITE);
                puAchatField.setForeground(Color.BLUE);
            }
        }, JComponent.WHEN_IN_FOCUSED_WINDOW);

        doOnRelease(KeyEvent.VK_SPACE, InputEvent.CTRL_DOWN_MASK, new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                puAchatField.setBackground(Color.WHITE);
                puAchatField.setForeground(Color.WHITE);
            }
        }, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    @Override
    public void setChildEntity(EntityClass child, boolean allowChng) {
        if (child instanceof Produit) {
            setProduit((Produit) child);
        }

        if (child instanceof Vente) {
            setVente((Vente) child);
        }
    }

    public void setVente(Vente vente) {
        this.vente = vente;
    }

    @java.lang.Override
    public void initFields(LigneVnt oldEntity) {
        setVente(oldEntity.getVente());
        setProduit(oldEntity.getEnStock().getProduit());
        setLotEnStk(oldEntity.getEnStock());
        puVente = oldEntity.getPuVnt();
        qteVendu = oldEntity.getQte();
        qteUnitaireVendu = oldEntity.getQteUnitaire();
        // Fill fields
        puVentField.setBigDecimalValue(puVente);
        qteAVendreF.setText(String.valueOf(qteVendu));
        // Init Unite Vente
        uniteVente = oldEntity.getUniteVnt();
        uniteComboBoxModel.setSelectedItem(uniteVente);
    }

    @Override
    public void viewSelEntity(ResultSet2Table_Panel listePanel) {
        if (listePanel instanceof ListeLotsAVendrePanel) {
            if (listePanel.getSelectedEntity().getId() > 0) {
                setLotEnStk((EnStock) listePanel.getSelectedEntity());
            } else {
                setLotEnStk(null);
            }
        }

        if (listePanel instanceof ListeProduitsPanel) {
            if (listePanel.getSelectedEntity().getId() > 0) {
                setProduit((Produit) listePanel.getSelectedEntity());
            } else {
                setProduit(null);
            }
        }
    }

    public final void initProdsAVendrePanel() {
        listeProdsEnStk = (ListeLotsAVendrePanel) new ListeLotsAVendrePanel(this, false) {
            {
                setPreferredSize(new Dimension(550, 350));
                getTable().setColumnVisible("Qte", false); // hide then re-show after 'Dépôt' col!
                getTable().setColumnVisible("Dépôt", true);
                getTable().setColumnVisible("Qte", true);
                getTable().setColumnVisible("Date.Exp", true);

                getTable().setColumnVisible("Réf/C.B", false);
                getTable().setColumnVisible("Désignation", false);
                getTable().setColumnVisible("PU.Vnt.Gr", false);
                getTable().setColumnVisible("PU.Vnt.Dt", false);
                getTable().setColumnsPreferredWidths(new int[]{150, 50, 90});
            }
        };

        selLotEnStkP.setSelListPanel(listeProdsEnStk);
        listeProdsEnStk.setMasterPanel(listeProds, "ID_PROD");
        listeProdsEnStk.getPropertyChangeSupport().addPropertyChangeListener(ResultSet_Panel.ENTITY_TO_VIEW_PROPERTY, this);

    }

    public void setLotEnStk(EnStock lotStk) {
        lotEnStock = lotStk;
        selLotEnStkP.setSelEntity(lotStk);
        fixeQteStkAction.setEnabled(lotStk != null);
        if (lotStk != null) {
            if (!lotStk.getProduit().equals(produit)) {
                //setProduit(enStk.getProduit());
            }
            setDepot(lotStk.getDepot());
            setDateExp(lotStk.getDateExp());

            puAchatField.setBigDecimalValue(lotStk.getPuAch());
            if (isUpdate() && produit.equals(getOldEntity().getEnStock().getProduit())) {
                puVente = getOldEntity().getPuVnt();
            } else {
                puVente = vente.getTypeVnt().equals(TypeVnt.GROS) ? lotStk.getPuVntGr() : lotStk.getPuVntDt();
            }
            puVentField.setBigDecimalValue(puVente);

            qteStk = lotStk.getQte();
            qteField.setText(String.valueOf(qteStk));

            Color qteFieldBG = qteField.getBackground();
            if (qteStk <= 0) {
                qteField.setBackground(Color.RED);
            } else {
                if (qteStk <= produit.getQteMin()) {
                    qteField.setBackground(Color.ORANGE);
                } else {
                    qteField.setBackground(qteFieldBG);
                }
            }
        } else {
            // don't set produit we won't clear it!
            setDepot(null);
            setDateExp(null);
            qteStk = 0;
            qteField.setText("");
        }
    }

    public final void initProduitsPanel() {
        listeProds = new ListeProduitsPanel(this, false, false) {
            {
                mainPanel.remove(statusPanel);
                getTable().setColumnVisible("PU.Achat", false);
                getTable().setColumnVisible("Total/Achat", false);
            }
        };

        listeProds.setMajPanel(majProdPanel);
        selProdP.setSelListPanel(listeProds);
        listeProds.getPropertyChangeSupport().addPropertyChangeListener(ResultSet_Panel.ENTITY_TO_VIEW_PROPERTY, this);
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
        initUnitsCBoxModel();
        if (produit == null) {
            clearProduitFields();
            setLotEnStk(null);
            selLotEnStkP.setAllowSelChange(false);
            setDefaultFocusedComp(selProdP.getDescField());
        } else {
            selProdP.setSelEntity(produit);
            initProdFields();
            selLotEnStkP.setAllowSelChange(true);
            setDefaultFocusedComp(qteAVendreF);
        }
    }

    public void initProdFields() {
        selProdP.setDescription(produit.getDes() + "  Qte.Global: " + produit.getQteGlobal());
    }

    public void clearProduitFields() {
        selProdP.setDescription("Aucun Produit Sélectioné!");
        puVente = new BigDecimal(0);
        puVentField.setText("0");
        puAchatField.setText("0");
    }

    public void editProduit() {
        listeProds.edit();
    }

    public void setDepot(Depot d) {
        this.depot = d;
        if (d == null) {
            selLotEnStkP.setDescription("Aucun Dépôt Sélectioné!");
        } else {
            selLotEnStkP.setDescription("Dépôt:" + d.getShortDesc());
        }
    }

    public void setDateExp(Date date) {
        this.dateExp = date;
        if (date != null && date.getTime() != DateTools.getMaxJavaDate().getTime()) {
            selLotEnStkP.setDescription("Dépôt:" + depot.getShortDesc() + " / Exp:" + date.toString());
        }
    }

    public final void calcQtesVendues() {
        qteVendu = 0;
        qteUnitaireVendu = 0;
        if (produit == null || uniteVente == null) {
            return;
        }
        qteVendu = qteAVendreF.getValue();
        qteUnitaireVendu = qteVendu * uniteVente.getQte();
    }

    public Action fixeQteStkAction = new AbstractAction() {
        {
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (listeProdsEnStk.getModelSelectedRow() >= 0) {
                listeProdsEnStk.edit();
            } else {
                listeProdsEnStk.insert();
            }
        }
    };

    public final void initUniteVenteComboBox() {
        uniteVenteComboBox.setModel(uniteComboBoxModel);
        uniteVenteComboBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                uniteVente = uniteComboBoxModel.getSelectedItem();
            }
        });
    }

    public void initUnitsCBoxModel() {
        uniteComboBoxModel.removeAllElements();
        if (produit != null) {
            List<Quantifier> units = QuantifierDAO.getInstance().getUnitesOfProd(produit);
            for (Quantifier q : units) {
                Unite u = q.getUnite();
                uniteComboBoxModel.addElement(u);
            }
        }
    }

    @Override
    public boolean verifyFields() {
        String puVenteTxt = puVentField.getText().trim();
        String qteVenduTxt = qteAVendreF.getText().trim();

        if (produit == null) {
            JOptionPane.showMessageDialog(this, "Sélectionner le Produit à vendre SVP!!!", "Attention...", JOptionPane.WARNING_MESSAGE);
            selProdP.requestFocus();
            return false;
        }
        if (puVenteTxt.length() == 0) {
            JOptionPane.showMessageDialog(this, "Entrez le prix du vente SVP!!!", "Attention...", JOptionPane.WARNING_MESSAGE);
            puVentField.requestFocus();
            return false;
        }
        if (qteVenduTxt.length() == 0) {
            JOptionPane.showMessageDialog(this, "Entrez la quantité à vendre SVP!!!", "Attention...", JOptionPane.WARNING_MESSAGE);
            qteAVendreF.requestFocus();
            return false;
        }
        if (qteAVendreF.getValue() <= 0) {
            JOptionPane.showMessageDialog(this, "Entrez la quantité à vendre doit être > 0 !!!", "Attention...", JOptionPane.WARNING_MESSAGE);
            qteAVendreF.requestFocus();
            return false;
        }
        
        uniteVente = uniteComboBoxModel.getSelectedItem();
        if (uniteVente == null || uniteVente.getId() <= 0) {
            JOptionPane.showMessageDialog(this, "Sélectionner une unité de vente SVP!!!", "Attention...", JOptionPane.WARNING_MESSAGE);
            uniteVenteComboBox.requestFocus();
            return false;
        }

        puVente = puVentField.getBigDecimalValue();
        if (puVente.doubleValue() <= 0) {
            String mess = "Attention!\nVous avez entré un prix de vente <= 0 !!!"
                    + "\nVoullez vous corriger le prix de vente?";
            int rep = JOptionPane.showConfirmDialog(this, mess, "Attention...", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (rep == JOptionPane.YES_OPTION) {
                puVentField.requestFocus();
                return false;
            }
        }

        if (puVente.doubleValue() <= lotEnStock.getPuAch().doubleValue()) {
            String mess = "Le prix de vente est inférieur ou égal au prix d'achat.\n"
                    + "Voullez vous continuer quand même?";
            int rep = JOptionPane.showConfirmDialog(this, mess, "Attention...", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (rep == JOptionPane.NO_OPTION) {
                puVentField.requestFocus();
                return false;
            }
        }
        //================================================================//
        calcQtesVendues();// this methode set all quantities variables.
        if (qteUnitaireVendu > qteStk) {
            String mess = "La quantité demandée est non disponible en stock!"
                    + "\nVoullez vous corriger la Quantité?";
            int rep = JOptionPane.showConfirmDialog(this, mess, "Attention...", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (rep == JOptionPane.YES_OPTION) {
                fixeQteStkAction.actionPerformed(null);
                qteAVendreF.requestFocus();
                return false;
            } else {
                return false;
            }
        }
        double total = puVente.doubleValue() * qteUnitaireVendu;
        totalLVnt = new BigDecimal(total).setScale(2, BigDecimal.ROUND_CEILING);
        return true;
    }

    @Override
    public boolean save() {
        if (verifyFields()) {
            if (getEditedEntity() == null) {
                setEditedEntity(new LigneVnt());
            }
            getEditedEntity().setVente(vente);
            getEditedEntity().setEnStock(lotEnStock);
            getEditedEntity().setPuVnt(puVente);
            getEditedEntity().setPuAch(lotEnStock.getPuAch());
            getEditedEntity().setQte(qteVendu);
            getEditedEntity().setUniteVnt(uniteVente);
            getEditedEntity().setQteUnitair(qteUnitaireVendu);
            getEditedEntity().setTotalLvnt(totalLVnt);
            return super.save();
        } else {
            setSaved(false);
            return false;
        }
    }

    @Override
    public boolean add() {
        if (super.add()) {
            selProdP.showSelList();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void clearFields() {
        setFirstFocusedComp(selProdP.getDescField());
        qteVendu = 0;
        qteUnitaireVendu = 0;
        qteAVendreF.setText("1");
        //===============================================//
        setLotEnStk(null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        fieldsPanel = new javax.swing.JPanel();
        prodPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        selProdP = new panels.SelectionPanel<Produit>();
        depotPanel = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        selLotEnStkP = new panels.SelectionPanel<entities.EnStock>();
        qteActuellePanel = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        qteField = new myComponents.MyJField();
        fixStockLinkButton = new com.l2fprod.common.swing.JLinkButton();
        jSeparator2 = new javax.swing.JSeparator();
        qteVendrePanel = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        X = new javax.swing.JLabel();
        uniteVenteComboBox = new myComponents.MyJComboBox();
        qteAVendreF = new myComponents.DecimalJField();
        jSeparator1 = new javax.swing.JSeparator();
        puVentePanel = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        puVentField = new myComponents.CurrencyField();
        currencyUniteLabel1 = new myComponents.CurrencyUniteLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(50, 0), new java.awt.Dimension(32767, 0));
        jLabel4 = new javax.swing.JLabel();
        puAchatField = new myComponents.CurrencyField();

        fieldsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        fieldsPanel.setFocusable(false);
        fieldsPanel.setMinimumSize(new java.awt.Dimension(500, 180));
        fieldsPanel.setPreferredSize(new java.awt.Dimension(500, 180));
        fieldsPanel.setRequestFocusEnabled(false);

        prodPanel.setFocusable(false);
        prodPanel.setMinimumSize(new java.awt.Dimension(177, 25));
        prodPanel.setPreferredSize(new java.awt.Dimension(273, 25));
        prodPanel.setRequestFocusEnabled(false);
        prodPanel.setLayout(new javax.swing.BoxLayout(prodPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setText("Produit à Vendre:");
        prodPanel.add(jLabel6);

        selProdP.setDescription("Aucun Produit Sélectioné!");
        selProdP.setShortcutKey("");
        prodPanel.add(selProdP);

        depotPanel.setFocusable(false);
        depotPanel.setMinimumSize(new java.awt.Dimension(163, 25));
        depotPanel.setPreferredSize(new java.awt.Dimension(248, 25));
        depotPanel.setRequestFocusEnabled(false);
        depotPanel.setLayout(new javax.swing.BoxLayout(depotPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel14.setText("Lot à Vendre:");
        jLabel14.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jLabel14.setPreferredSize(new java.awt.Dimension(107, 17));
        depotPanel.add(jLabel14);

        selLotEnStkP.setAllowSelChange(false);
        selLotEnStkP.setDescription("Aucun Dépot Sélectioné!");
        selLotEnStkP.setMinimumSize(new java.awt.Dimension(41, 27));
        selLotEnStkP.setPreferredSize(new java.awt.Dimension(120, 27));
        selLotEnStkP.setShortcutKey("");
        depotPanel.add(selLotEnStkP);

        qteActuellePanel.setMinimumSize(new java.awt.Dimension(311, 25));
        qteActuellePanel.setPreferredSize(new java.awt.Dimension(376, 25));
        qteActuellePanel.setLayout(new javax.swing.BoxLayout(qteActuellePanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel15.setText("Qte.En.Stock: ");
        jLabel15.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jLabel15.setPreferredSize(new java.awt.Dimension(107, 18));
        qteActuellePanel.add(jLabel15);

        qteField.setEditable(false);
        qteField.setBackground(new java.awt.Color(229, 229, 255));
        qteField.setToolTipText("Appuyer Entrer pour sélectionner!");
        qteField.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        qteField.setMinimumSize(new java.awt.Dimension(4, 27));
        qteField.setPreferredSize(new java.awt.Dimension(4, 27));
        qteActuellePanel.add(qteField);

        fixStockLinkButton.setAction(fixeQteStkAction);
        fixStockLinkButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 0));
        fixStockLinkButton.setForeground(new java.awt.Color(51, 51, 255));
        fixStockLinkButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/fixStock16.png"))); // NOI18N
        fixStockLinkButton.setText("Corriger la Qte (F2)");
        fixStockLinkButton.setToolTipText("Corriger la quantité en stock de ce produit si elle est incorrect! (F2)");
        fixStockLinkButton.setFocusable(false);
        fixStockLinkButton.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        qteActuellePanel.add(fixStockLinkButton);

        qteVendrePanel.setFocusable(false);
        qteVendrePanel.setPreferredSize(new java.awt.Dimension(520, 25));
        qteVendrePanel.setLayout(new java.awt.GridBagLayout());

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel10.setText("Qte.à.Vendre:");
        jLabel10.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jLabel10.setPreferredSize(new java.awt.Dimension(107, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        qteVendrePanel.add(jLabel10, gridBagConstraints);

        X.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        X.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        X.setText("X");
        X.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        qteVendrePanel.add(X, gridBagConstraints);

        uniteVenteComboBox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                uniteVenteComboBoxKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 1.0;
        qteVendrePanel.add(uniteVenteComboBox, gridBagConstraints);

        qteAVendreF.setText("1");
        qteAVendreF.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.weightx = 0.2;
        qteVendrePanel.add(qteAVendreF, gridBagConstraints);

        puVentePanel.setFocusable(false);
        puVentePanel.setMinimumSize(new java.awt.Dimension(192, 25));
        puVentePanel.setPreferredSize(new java.awt.Dimension(150, 25));
        puVentePanel.setLayout(new javax.swing.BoxLayout(puVentePanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel12.setText("Prix.U.Vente:");
        jLabel12.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 5));
        jLabel12.setPreferredSize(new java.awt.Dimension(107, 19));
        puVentePanel.add(jLabel12);
        puVentePanel.add(puVentField);
        puVentePanel.add(currencyUniteLabel1);
        puVentePanel.add(filler2);

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel4.setText("Prix.U.Achat:");
        jLabel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 5));
        puVentePanel.add(jLabel4);

        puAchatField.setBackground(new java.awt.Color(229, 229, 255));
        puAchatField.setForeground(new java.awt.Color(229, 229, 255));
        puAchatField.setFocusable(false);
        puVentePanel.add(puAchatField);

        javax.swing.GroupLayout fieldsPanelLayout = new javax.swing.GroupLayout(fieldsPanel);
        fieldsPanel.setLayout(fieldsPanelLayout);
        fieldsPanelLayout.setHorizontalGroup(
            fieldsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(depotPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(puVentePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSeparator2)
            .addComponent(jSeparator1)
            .addComponent(qteVendrePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 557, Short.MAX_VALUE)
            .addComponent(prodPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 557, Short.MAX_VALUE)
            .addComponent(qteActuellePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        fieldsPanelLayout.setVerticalGroup(
            fieldsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fieldsPanelLayout.createSequentialGroup()
                .addComponent(prodPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(depotPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(qteActuellePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(qteVendrePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(puVentePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setLayout(null);
    }// </editor-fold>//GEN-END:initComponents

    private void uniteVenteComboBoxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_uniteVenteComboBoxKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            okAction.actionPerformed(null);
        }
    }//GEN-LAST:event_uniteVenteComboBoxKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel X;
    private myComponents.CurrencyUniteLabel currencyUniteLabel1;
    private javax.swing.JPanel depotPanel;
    private javax.swing.JPanel fieldsPanel;
    private javax.swing.Box.Filler filler2;
    private com.l2fprod.common.swing.JLinkButton fixStockLinkButton;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPanel prodPanel;
    private myComponents.CurrencyField puAchatField;
    private myComponents.CurrencyField puVentField;
    private javax.swing.JPanel puVentePanel;
    private myComponents.DecimalJField qteAVendreF;
    private javax.swing.JPanel qteActuellePanel;
    private myComponents.MyJField qteField;
    private javax.swing.JPanel qteVendrePanel;
    private panels.SelectionPanel<entities.EnStock> selLotEnStkP;
    private panels.SelectionPanel<Produit> selProdP;
    private myComponents.MyJComboBox uniteVenteComboBox;
    // End of variables declaration//GEN-END:variables

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new MajLigneVentePanel(null).showNewPanel(null);
                System.exit(0);
            }
        });
    }
}
