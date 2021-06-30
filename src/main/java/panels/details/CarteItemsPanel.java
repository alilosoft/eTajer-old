package panels.details;

import dao.LotEnStockDAO;
import entities.EntityClass;
import entities.Vente;
import java.awt.BorderLayout;
import java.awt.Container;
import java.sql.ResultSet;
import myComponents.MyJFrame;
import panels.RSTablePanel;
import dao.LigneVenteDAO;
import dialogs.LoginDialog;
import dialogs.MajDialog;
import dialogs.SelectionDialog;
import entities.EnStock;
import entities.LigneVnt;
import entities.Produit;
import entities.Unite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import static panels.CRUDPanel.DELETE;
import panels.Notification;
import panels.crud.ListeLotsAVendrePanel;
import panels.maj.MajLigneVentePanel;

/**
 *
 * @author alilo
 */
public class CarteItemsPanel extends RSTablePanel<LigneVnt, LigneVenteDAO> {

    {
        setPreferredSize(new Dimension(600, 400));
        setMajPanel(new MajLigneVentePanel(this));
        setFilterShortcut("CTRL", "F");
    }
    private Vente masterVente;

    public CarteItemsPanel(Container owner, boolean checkable) {
        super(owner, checkable);
        initComponents();
        add(mainPanel, BorderLayout.CENTER);
        //
        GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        statusPanel.add(selViewPanel, gridBagConstraints);
        //
        doOnPress(KeyEvent.VK_F12, 0, showLotsListAct, JComponent.WHEN_IN_FOCUSED_WINDOW);
        doOnPress(KeyEvent.VK_F3, 0, cbFocusAct, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    @Override
    public Component getDefaultFocusedComp() {
        return cbField;
    }

    @Override
    public CarteItemsPanel initTableView() {
        getTable().setColumnVisible("ID", false);
        getTable().setColumnVisible("ID_VNT", false);
        getTable().setColumnVisible("ID_PROD", false);
        getTable().setColumnVisible("ID_DEPOT", false);
        getTable().setColumnVisible("Unité.Vnt", false);

        getTable().setColumnsPreferredWidths(new int[]{35, 100, 160});
        return this;
    }

    @Override
    public ResultSet getResultSet() {
        if (masterVente != null) {
            return getTableDAO().getDetailsVente(masterVente);
        } else {
            return super.getResultSet();
        }
    }

    private Action cbFocusAct = cbFocusAct = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            cbField.requestFocus();
        }
    };

    @Override
    public void setActionsShortcuts() {
        super.setActionsShortcuts();
        doOnPress(KeyEvent.VK_UP, KeyEvent.ALT_DOWN_MASK, upAction, JComponent.WHEN_IN_FOCUSED_WINDOW);
        doOnPress(KeyEvent.VK_DOWN, KeyEvent.ALT_DOWN_MASK, downAction, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private final DecimalFormat montantFormat = new DecimalFormat("0.00");
    private final DecimalFormat qteFormat = new DecimalFormat("0.###");

    @Override
    public void doOnTableSelectionChanged(int tableRow, int modelRow) {
        super.doOnTableSelectionChanged(tableRow, modelRow);
        if (getSelectedEntity() instanceof LigneVnt) {
            cbField.setText(getSelectedEntity().getEnStock().getProduit().getCodBar());
            cbField.selectAll();
            desL.setText(getSelectedEntity().getEnStock().getProduit().getDes());
            puVntF.setText(montantFormat.format(getSelectedEntity().getPuVnt().doubleValue()));
            qteF.setValue(getSelectedEntity().getQte());
            totalLbl.setText(montantFormat.format(getSelectedEntity().getTotalLvnt()));
        } else {
            cbField.setText("");
            qteF.setText("0");
            puVntF.setText("0.00");
            totalLbl.setText("0.00");
        }
        setAllowedOperation(DELETE, !masterVente.isValidee());
    }

    private final Notification notifStatus = Notification.createNotification(3, "Status: ", "?", java.awt.Color.BLACK, "Segoe UI", 16, true, false);
    private final Color valideeColor = new Color(0, 204, 0);

    /**
     * Set the master 'Achat' of 'LigneAchat' list.\n the list will filtered to
     * show only the sale lines associated with this 'Achat'.
     *
     * @param vente the master sale.
     */
    public void setMasterVente(Vente vente) {
        masterVente = vente;
        reload();
        setAllowedOperation(INSERT, isAllowInsert() && !masterVente.isValidee());
        setAllowedOperation(EDIT, isAllowEdit() && !masterVente.isValidee());
        setAllowedOperation(DELETE, isAllowDelete() && !masterVente.isValidee());
        cbField.setEnabled(!masterVente.isValidee());
        qteF.setEnabled(!masterVente.isValidee());
        puVntF.setEnabled(!masterVente.isValidee());

        if (masterVente.getId() >= 0) {
            if (masterVente.isValidee()) {
                notifStatus.setMess("Livrée!");
                notifStatus.setColor(valideeColor);
                addNotification(notifStatus, false);
            } else {
                notifStatus.setMess("Non Livrée!");
                notifStatus.setColor(Color.RED);
                addNotification(notifStatus, false);
            }
        }
    }

    @Override
    public void setMasterEntity(EntityClass masterEntity) {
        super.setMasterEntity(masterEntity);
        if (masterEntity instanceof Vente) {
            setMasterVente((Vente) masterEntity);
        }
    }

    @Override
    public LigneVenteDAO getTableDAO() {
        return LigneVenteDAO.getInstance();
    }

    public Vente getMasterVente() {
        return masterVente;
    }

    public void addLigneVnt(EnStock lot) {
        if (masterVente == null || masterVente.getId() <= 0 || masterVente.isValidee()) {
            return;
        }
        // create new 'LigneVente'.
        LigneVnt newLVnt = new LigneVnt(0);
        // set the 'Vente'
        newLVnt.setVente(masterVente);
        // set the 'Lot'
        newLVnt.setEnStock(lot);
        // set qte & price
        BigDecimal puVnt = lot.getPuVntDt();
        newLVnt.setPuVnt(puVnt);
        newLVnt.setPuAch(lot.getPuAch());
        newLVnt.setQte(1);
        // set the 'Unite.Vente'
        Produit prodVend = lot.getProduit();
        Unite unite = prodVend.getUnite();
        newLVnt.setUniteVnt(unite);
        newLVnt.setQteUnitair(unite.getQte());
        double total = puVnt.doubleValue() * unite.getQte();
        newLVnt.setTotalLvnt(new BigDecimal(total).setScale(2, RoundingMode.HALF_UP));
        if (newLVnt.getQteUnitaire() > newLVnt.getEnStock().getQte()) {
            String mess = "La quantité demandée est non disponible en Stock!\n"
                    + "Cette livraison ne pourra pas être valider.";
            JOptionPane.showMessageDialog(this, mess, "Attention!", JOptionPane.WARNING_MESSAGE);
        }
        newLVnt.insert();
        reload();
        selectEntity(newLVnt);
    }

    public void updateLigneVnt() {
        if (masterVente == null || masterVente.getId() <= 0 || masterVente.isValidee()) {
            return;
        }
        LigneVnt selLV = getSelectedEntity();
        if (selLV instanceof LigneVnt) {
            double puVnt = puVntF.getValue();
            double qte = qteF.getValue();
            double qteUnit = selLV.getUniteVnt().getQte() * qte;
            double total = puVnt * qteUnit;
            BigDecimal totalLVnt = new BigDecimal(total).setScale(2, BigDecimal.ROUND_CEILING);
            selLV.setQte(qte);
            selLV.setQteUnitair(qteUnit);
            selLV.setPuVnt(puVntF.getBigDecimalValue());
            selLV.setTotalLvnt(totalLVnt);
            selLV.update();
            reload();
            selectEntity(selLV);
        }
        cbField.requestFocus();
    }

    private final ListeLotsAVendrePanel listeLotsAVendre = new ListeLotsAVendrePanel(this, false) {
        {
            setAllowedOperation(INSERT, LoginDialog.isLoginAsAdmin());
            setAllowedOperation(EDIT, LoginDialog.isLoginAsAdmin());
            setAllowedOperation(DELETE, LoginDialog.isLoginAsAdmin());
        }
    };
    private final SelectionDialog sd = new SelectionDialog();

    private void showLotsAVendre() {
        sd.showPanel(listeLotsAVendre, null, null);
        if (sd.getReturnStatus() == MajDialog.RET_OK) {
            if (listeLotsAVendre.getSelectedEntity() instanceof EnStock) {
                addLigneVnt(listeLotsAVendre.getSelectedEntity());
            }
        }

    }

    private final Action showLotsListAct = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            showLotsAVendre();
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
        java.awt.GridBagConstraints gridBagConstraints;

        selViewPanel = new javax.swing.JPanel();
        curvedPanel = new myComponents.ImagePanel();
        f3L = new javax.swing.JLabel();
        cbImgPanel = new myComponents.ImagePanel();
        cbField = new myComponents.MyJField();
        qteL = new javax.swing.JLabel();
        qteF = new myComponents.DecimalJField();
        prixL = new javax.swing.JLabel();
        puVntF = new myComponents.CurrencyField();
        currencyUniteLabel1 = new myComponents.CurrencyUniteLabel();
        jPanel1 = new javax.swing.JPanel();
        desL = new javax.swing.JLabel();
        findBtn = new com.l2fprod.common.swing.JLinkButton();
        deleteBtn = new com.l2fprod.common.swing.JLinkButton();
        backgroundL = new javax.swing.JLabel();
        x2Lbl3 = new javax.swing.JLabel();
        totalLbl = new javax.swing.JLabel();

        selViewPanel.setFocusCycleRoot(true);
        selViewPanel.setMinimumSize(new java.awt.Dimension(500, 85));
        selViewPanel.setOpaque(false);
        selViewPanel.setPreferredSize(new java.awt.Dimension(750, 90));
        selViewPanel.setLayout(new java.awt.GridBagLayout());

        curvedPanel.setImgLocationPath("/res/png/curvedPanel.png");
        curvedPanel.setMaximumSize(new java.awt.Dimension(70, 32767));
        curvedPanel.setMinimumSize(new java.awt.Dimension(70, 4));
        curvedPanel.setPreferredSize(new java.awt.Dimension(70, 4));

        javax.swing.GroupLayout curvedPanelLayout = new javax.swing.GroupLayout(curvedPanel);
        curvedPanel.setLayout(curvedPanelLayout);
        curvedPanelLayout.setHorizontalGroup(
            curvedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 66, Short.MAX_VALUE)
        );
        curvedPanelLayout.setVerticalGroup(
            curvedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 187, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        selViewPanel.add(curvedPanel, gridBagConstraints);

        f3L.setBackground(new java.awt.Color(0, 0, 0));
        f3L.setFont(new java.awt.Font("Square721 BT", 1, 18)); // NOI18N
        f3L.setForeground(new java.awt.Color(0, 255, 0));
        f3L.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        f3L.setText("(F3)");
        f3L.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        selViewPanel.add(f3L, gridBagConstraints);

        cbImgPanel.setBorder(null);
        cbImgPanel.setImgLocationPath("/res/png/cb.png");
        cbImgPanel.setMinimumSize(new java.awt.Dimension(70, 32));
        cbImgPanel.setPreferredSize(new java.awt.Dimension(80, 32));

        javax.swing.GroupLayout cbImgPanelLayout = new javax.swing.GroupLayout(cbImgPanel);
        cbImgPanel.setLayout(cbImgPanelLayout);
        cbImgPanelLayout.setHorizontalGroup(
            cbImgPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 80, Short.MAX_VALUE)
        );
        cbImgPanelLayout.setVerticalGroup(
            cbImgPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 32, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        selViewPanel.add(cbImgPanel, gridBagConstraints);

        cbField.setBackground(new java.awt.Color(0, 0, 0));
        cbField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 0)));
        cbField.setForeground(new java.awt.Color(0, 255, 0));
        cbField.setToolTipText("");
        cbField.setDescFG(new java.awt.Color(0, 255, 0));
        cbField.setDescFont(new java.awt.Font("tahoma", 1, 18)); // NOI18N
        cbField.setFlashBG(new java.awt.Color(255, 255, 153));
        cbField.setFont(new java.awt.Font("Square721 BT", 1, 18)); // NOI18N
        cbField.setMinimumSize(new java.awt.Dimension(2, 32));
        cbField.setPreferredSize(new java.awt.Dimension(136, 32));
        cbField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cbFieldKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        selViewPanel.add(cbField, gridBagConstraints);

        qteL.setBackground(new java.awt.Color(0, 0, 0));
        qteL.setFont(new java.awt.Font("Square721 BT", 1, 20)); // NOI18N
        qteL.setForeground(new java.awt.Color(0, 255, 0));
        qteL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        qteL.setText(" Qte: ");
        qteL.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        selViewPanel.add(qteL, gridBagConstraints);

        qteF.setBackground(new java.awt.Color(0, 0, 0));
        qteF.setBorder(null);
        qteF.setForeground(new java.awt.Color(0, 255, 0));
        qteF.setText("1");
        qteF.setFlashBG(new java.awt.Color(255, 255, 153));
        qteF.setFont(new java.awt.Font("Square721 BT", 1, 24)); // NOI18N
        qteF.setMinimumSize(new java.awt.Dimension(6, 32));
        qteF.setPattern("0.###");
        qteF.setPreferredSize(new java.awt.Dimension(25, 32));
        qteF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                qteFKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.3;
        selViewPanel.add(qteF, gridBagConstraints);

        prixL.setBackground(new java.awt.Color(0, 0, 0));
        prixL.setFont(new java.awt.Font("Square721 BT", 1, 20)); // NOI18N
        prixL.setForeground(new java.awt.Color(51, 255, 0));
        prixL.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        prixL.setText(" Prix: ");
        prixL.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        selViewPanel.add(prixL, gridBagConstraints);

        puVntF.setBackground(new java.awt.Color(0, 0, 0));
        puVntF.setBorder(null);
        puVntF.setForeground(new java.awt.Color(0, 255, 0));
        puVntF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        puVntF.setFlashBG(new java.awt.Color(255, 255, 153));
        puVntF.setFont(new java.awt.Font("DS-Digital", 0, 36)); // NOI18N
        puVntF.setMinimumSize(new java.awt.Dimension(2, 32));
        puVntF.setPreferredSize(new java.awt.Dimension(62, 32));
        puVntF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                puVntFKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        selViewPanel.add(puVntF, gridBagConstraints);

        currencyUniteLabel1.setBackground(new java.awt.Color(0, 0, 0));
        currencyUniteLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 2));
        currencyUniteLabel1.setForeground(new java.awt.Color(51, 255, 0));
        currencyUniteLabel1.setFont(new java.awt.Font("Square721 BT", 1, 20)); // NOI18N
        currencyUniteLabel1.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        selViewPanel.add(currencyUniteLabel1, gridBagConstraints);

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        desL.setBackground(new java.awt.Color(0, 0, 0));
        desL.setFont(new java.awt.Font("Segoe Print", 0, 18)); // NOI18N
        desL.setForeground(new java.awt.Color(51, 255, 0));
        desL.setText("Aucun produit sélectionné!");
        desL.setOpaque(true);
        desL.setPreferredSize(new java.awt.Dimension(227, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        jPanel1.add(desL, gridBagConstraints);

        findBtn.setAction(showLotsListAct);
        findBtn.setBackground(new java.awt.Color(0, 0, 0));
        findBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        findBtn.setForeground(new java.awt.Color(51, 255, 0));
        findBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/find24.png"))); // NOI18N
        findBtn.setText("(F12) ");
        findBtn.setToolTipText("Chercher un produit (F12)");
        findBtn.setFocusable(false);
        findBtn.setFont(new java.awt.Font("Square721 BT", 1, 18)); // NOI18N
        findBtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        findBtn.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        findBtn.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(findBtn, gridBagConstraints);

        deleteBtn.setAction(deleteAction);
        deleteBtn.setBackground(new java.awt.Color(0, 0, 0));
        deleteBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 5));
        deleteBtn.setForeground(new java.awt.Color(255, 0, 0));
        deleteBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/trash24.png"))); // NOI18N
        deleteBtn.setText("(Suppr)");
        deleteBtn.setFocusable(false);
        deleteBtn.setFont(new java.awt.Font("Square721 BT", 1, 18)); // NOI18N
        deleteBtn.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        deleteBtn.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        deleteBtn.setMinimumSize(new java.awt.Dimension(115, 26));
        deleteBtn.setOpaque(true);
        deleteBtn.setPreferredSize(new java.awt.Dimension(115, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(deleteBtn, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.5;
        selViewPanel.add(jPanel1, gridBagConstraints);

        backgroundL.setBackground(new java.awt.Color(0, 0, 0));
        backgroundL.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        selViewPanel.add(backgroundL, gridBagConstraints);

        x2Lbl3.setBackground(new java.awt.Color(0, 0, 0));
        x2Lbl3.setFont(new java.awt.Font("Square721 BT", 1, 20)); // NOI18N
        x2Lbl3.setForeground(new java.awt.Color(51, 255, 0));
        x2Lbl3.setText(" S.Total:");
        x2Lbl3.setOpaque(true);

        totalLbl.setBackground(new java.awt.Color(0, 0, 0));
        totalLbl.setFont(new java.awt.Font("DS-Digital", 0, 40)); // NOI18N
        totalLbl.setForeground(new java.awt.Color(0, 255, 0));
        totalLbl.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        totalLbl.setText("0.00");
        totalLbl.setOpaque(true);
        totalLbl.setPreferredSize(new java.awt.Dimension(67, 36));

        setFocusable(false);
        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    private void cbFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cbFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            EnStock lotEnStk = LotEnStockDAO.getInstance().getByCodBar(cbField.getText().trim());
            if (lotEnStk == null) {
                cbField.setBackground(Color.RED);
                Toolkit.getDefaultToolkit().beep();
            } else {
                if (evt.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK) {
                    System.out.println("ctrl pressed");
                }
                cbField.setBackground(cbField.getFlashBG());
                addLigneVnt(lotEnStk);
            }
            evt.consume();
        }
    }//GEN-LAST:event_cbFieldKeyPressed

    private void puVntFKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_puVntFKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            updateLigneVnt();
        }
    }//GEN-LAST:event_puVntFKeyPressed

    private void qteFKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_qteFKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            updateLigneVnt();
        }
    }//GEN-LAST:event_qteFKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel backgroundL;
    public myComponents.MyJField cbField;
    private myComponents.ImagePanel cbImgPanel;
    private myComponents.CurrencyUniteLabel currencyUniteLabel1;
    private myComponents.ImagePanel curvedPanel;
    private com.l2fprod.common.swing.JLinkButton deleteBtn;
    private javax.swing.JLabel desL;
    private javax.swing.JLabel f3L;
    private com.l2fprod.common.swing.JLinkButton findBtn;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel prixL;
    private myComponents.CurrencyField puVntF;
    private myComponents.DecimalJField qteF;
    private javax.swing.JLabel qteL;
    private javax.swing.JPanel selViewPanel;
    private javax.swing.JLabel totalLbl;
    private javax.swing.JLabel x2Lbl3;
    // End of variables declaration//GEN-END:variables
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                MyJFrame frame = new MyJFrame();
                //ArticleDAO.getInstance().setGetAllParam(0);
                frame.getContentPane().add(new CarteItemsPanel(frame, false).initTableView(), BorderLayout.CENTER);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
