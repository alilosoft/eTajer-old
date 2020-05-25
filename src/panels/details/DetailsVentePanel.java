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
import entities.EnStock;
import entities.LigneVnt;
import entities.Produit;
import entities.TypeVnt;
import entities.Unite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import panels.Notification;
import panels.maj.MajLigneVentePanel;

/**
 *
 * @author alilo
 */
public class DetailsVentePanel extends RSTablePanel<LigneVnt, LigneVenteDAO> {

    {
        setPreferredSize(new Dimension(600, 400));
        setMajPanel(new MajLigneVentePanel(this));
        setFilterShortcut("CTRL", "F");
    }
    private Vente masterVente;

    public DetailsVentePanel(Container owner, boolean checkable) {
        super(owner, checkable);
        initComponents();
        add(mainPanel, BorderLayout.CENTER);
        statusPanel.add(selViewPanel);
    }

    @Override
    public DetailsVentePanel initTableView() {
        getTable().setColumnVisible("ID", false);
        getTable().setColumnVisible("ID_VNT", false);
        getTable().setColumnVisible("ID_PROD", false);
        getTable().setColumnVisible("ID_DEPOT", false);
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

    public Action cbFocusAct;

    @Override
    public void setActionsShortcuts() {
        super.setActionsShortcuts();
        cbFocusAct = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                cbField.requestFocus();
            }
        };
        doOnPress(KeyEvent.VK_C, KeyEvent.ALT_DOWN_MASK, cbFocusAct, JComponent.WHEN_IN_FOCUSED_WINDOW);

        doOnPress(KeyEvent.VK_E, KeyEvent.ALT_DOWN_MASK, editAction, JComponent.WHEN_IN_FOCUSED_WINDOW);
        doOnPress(KeyEvent.VK_S, KeyEvent.ALT_DOWN_MASK, deleteAction, JComponent.WHEN_IN_FOCUSED_WINDOW);

        doOnPress(KeyEvent.VK_UP, KeyEvent.ALT_DOWN_MASK, upAction, JComponent.WHEN_IN_FOCUSED_WINDOW);
        doOnPress(KeyEvent.VK_DOWN, KeyEvent.ALT_DOWN_MASK, downAction, JComponent.WHEN_IN_FOCUSED_WINDOW);

        upAction.putValue(Action.SHORT_DESCRIPTION, "Sélectionner l'élément précédent (Alt+Flèche-Haut)");
        downAction.putValue(Action.SHORT_DESCRIPTION, "Sélectionner l'élément suivant (Alt+Flèche-Bas)");
    }

    private final DecimalFormat montantFormat = new DecimalFormat("0.00");
    private final DecimalFormat qteFormat = new DecimalFormat("0.###");

    @Override
    public void doOnTableSelectionChanged(int tableRow, int modelRow) {
        super.doOnTableSelectionChanged(tableRow, modelRow);
        if (getSelectedEntity() instanceof LigneVnt) {
            cbField.setText(getSelectedEntity().getEnStock().getProduit().getCodBar());
            prixLbl.setText(montantFormat.format(getSelectedEntity().getPuVnt().doubleValue()));

            qteLbl.setText(getSelectedEntity().getQte() + "");
            uniteLbl.setText(getSelectedEntity().getUniteVnt().getDes());
            totalLbl.setText(montantFormat.format(getSelectedEntity().getTotalLvnt()));
        } else {
            cbField.setText("Entrer C.Bar!");
            qteLbl.setText("0");
            prixLbl.setText("0.00");
            totalLbl.setText("0.00");
        }
    }

    private final Notification notifStatus = Notification.createNotification(3, "Status: ", "?", java.awt.Color.BLACK, "Segoe UI", 20, true, true);
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
        setAllowedOperation(INSERT, !masterVente.isValidee());
        setAllowedOperation(EDIT, !masterVente.isValidee());
        setAllowedOperation(DELETE, !masterVente.isValidee());
        cbField.setEnabled(!masterVente.isValidee());
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

    public void addLigneVnt(EnStock lotEnStock) {
        if (masterVente == null || masterVente.getId() <= 0 || masterVente.isValidee()) {
            return;
        }

        Produit prodVend = lotEnStock.getProduit();
        
        BigDecimal puVnt = lotEnStock.getPuVntDt();
        // create new 'LigneVente'.
        LigneVnt lv = new LigneVnt(0, puVnt, lotEnStock.getPuAch(), 1, 1, puVnt);
        // set the 'Vente'
        lv.setVente(masterVente);
        // set the 'Produit'
        lv.setEnStock(lotEnStock);
        // set the 'Unite.Vente'
        lv.setUniteVnt(prodVend.getUnite());
        if (masterVente.getTypeVnt().equals(TypeVnt.GROS)) {
            lv.setPuVnt(lotEnStock.getPuVntGr());
            getMajPanel().showNewPanel(lv);
        } else {
            if (main.MainApp.getInstance().isNumLockOn()) {
                getMajPanel().showNewPanel(lv);
                //Toolkit.getDefaultToolkit().setLockingKeyState(KeyEvent.VK_NUM_LOCK, false);
            } else {
                LigneVenteDAO.getInstance().insert(lv);
            }
        }
        if (lv.getQteUnitaire() > lv.getEnStock().getQte()) {
            String mess = "La quantité demandée est non disponible en Stock!\n"
                    + "Cette livraison ne pourra pas être valider.";
            JOptionPane.showMessageDialog(this, mess, "Attention!", JOptionPane.WARNING_MESSAGE);
        }
        reload();
        selectEntity(lv);
    }

    public boolean isCBActive() {
        return cbField.isEditable();
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

        selViewPanel = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        curvedPanel = new myComponents.ImagePanel();
        cbImgPanel = new myComponents.ImagePanel();
        cbField = new myComponents.MyJField();
        x2Lbl1 = new javax.swing.JLabel();
        prixLbl = new javax.swing.JLabel();
        editBtn = new com.l2fprod.common.swing.JLinkButton();
        deleteBtn = new com.l2fprod.common.swing.JLinkButton();
        jLabel = new javax.swing.JLabel();
        qteLbl = new javax.swing.JLabel();
        x2Lbl = new javax.swing.JLabel();
        uniteLbl = new javax.swing.JLabel();
        x2Lbl2 = new javax.swing.JLabel();
        totalLbl = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        cbFieldMenu = new javax.swing.JPopupMenu();
        useCBMenuItem = new javax.swing.JCheckBoxMenuItem();

        selViewPanel.setFocusable(false);
        selViewPanel.setOpaque(false);
        selViewPanel.setLayout(new java.awt.GridBagLayout());

        jSeparator1.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator1.setForeground(new java.awt.Color(0, 255, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        selViewPanel.add(jSeparator1, gridBagConstraints);

        curvedPanel.setImgLocationPath("/res/png/curvedPanel.png");
        curvedPanel.setMinimumSize(new java.awt.Dimension(60, 4));
        curvedPanel.setPreferredSize(new java.awt.Dimension(80, 4));

        javax.swing.GroupLayout curvedPanelLayout = new javax.swing.GroupLayout(curvedPanel);
        curvedPanel.setLayout(curvedPanelLayout);
        curvedPanelLayout.setHorizontalGroup(
            curvedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        curvedPanelLayout.setVerticalGroup(
            curvedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 81, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        selViewPanel.add(curvedPanel, gridBagConstraints);

        cbImgPanel.setBorder(null);
        cbImgPanel.setImgLocationPath("/res/png/cb.png");
        cbImgPanel.setMinimumSize(new java.awt.Dimension(50, 30));
        cbImgPanel.setPreferredSize(new java.awt.Dimension(70, 30));

        javax.swing.GroupLayout cbImgPanelLayout = new javax.swing.GroupLayout(cbImgPanel);
        cbImgPanel.setLayout(cbImgPanelLayout);
        cbImgPanelLayout.setHorizontalGroup(
            cbImgPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 109, Short.MAX_VALUE)
        );
        cbImgPanelLayout.setVerticalGroup(
            cbImgPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        selViewPanel.add(cbImgPanel, gridBagConstraints);

        cbField.setEditable(false);
        cbField.setBackground(new java.awt.Color(0, 0, 0));
        cbField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 0)));
        cbField.setForeground(new java.awt.Color(0, 255, 0));
        cbField.setToolTipText("Clicker button droit pour activer");
        cbField.setComponentPopupMenu(cbFieldMenu);
        cbField.setFocusable(false);
        cbField.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        cbField.setMinimumSize(new java.awt.Dimension(2, 30));
        cbField.setPreferredSize(new java.awt.Dimension(150, 30));
        cbField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cbFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cbFieldFocusLost(evt);
            }
        });
        cbField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cbFieldKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        selViewPanel.add(cbField, gridBagConstraints);

        x2Lbl1.setBackground(new java.awt.Color(0, 0, 0));
        x2Lbl1.setFont(new java.awt.Font("Square721 BT", 0, 18)); // NOI18N
        x2Lbl1.setForeground(new java.awt.Color(51, 255, 0));
        x2Lbl1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        x2Lbl1.setText(" PU:");
        x2Lbl1.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        selViewPanel.add(x2Lbl1, gridBagConstraints);

        prixLbl.setBackground(new java.awt.Color(0, 0, 0));
        prixLbl.setFont(new java.awt.Font("DS-Digital", 0, 36)); // NOI18N
        prixLbl.setForeground(new java.awt.Color(0, 255, 0));
        prixLbl.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        prixLbl.setText("0.00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        selViewPanel.add(prixLbl, gridBagConstraints);

        editBtn.setAction(editAction);
        editBtn.setBackground(new java.awt.Color(0, 0, 0));
        editBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        editBtn.setForeground(new java.awt.Color(51, 255, 0));
        editBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/edit24.png"))); // NOI18N
        editBtn.setText("");
        editBtn.setToolTipText("Modifier (Alt+E)");
        editBtn.setFocusable(false);
        editBtn.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        selViewPanel.add(editBtn, gridBagConstraints);

        deleteBtn.setAction(deleteAction);
        deleteBtn.setBackground(new java.awt.Color(0, 0, 0));
        deleteBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        deleteBtn.setForeground(new java.awt.Color(51, 255, 0));
        deleteBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/delete24.png"))); // NOI18N
        deleteBtn.setText("");
        deleteBtn.setToolTipText("Supprimer (Ctrl+S)");
        deleteBtn.setFocusable(false);
        deleteBtn.setFont(new java.awt.Font("Agency FB", 0, 18)); // NOI18N
        deleteBtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        deleteBtn.setMinimumSize(new java.awt.Dimension(32, 26));
        deleteBtn.setOpaque(true);
        deleteBtn.setPreferredSize(new java.awt.Dimension(32, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        selViewPanel.add(deleteBtn, gridBagConstraints);

        jLabel.setBackground(new java.awt.Color(0, 0, 0));
        jLabel.setFont(new java.awt.Font("Square721 BT", 0, 18)); // NOI18N
        jLabel.setForeground(new java.awt.Color(0, 255, 0));
        jLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel.setText(" Qte: ");
        jLabel.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        selViewPanel.add(jLabel, gridBagConstraints);

        qteLbl.setBackground(new java.awt.Color(0, 0, 0));
        qteLbl.setFont(new java.awt.Font("DS-Digital", 0, 36)); // NOI18N
        qteLbl.setForeground(new java.awt.Color(0, 255, 0));
        qteLbl.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        qteLbl.setText("1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        selViewPanel.add(qteLbl, gridBagConstraints);

        x2Lbl.setBackground(new java.awt.Color(0, 0, 0));
        x2Lbl.setFont(new java.awt.Font("Square721 BT", 0, 16)); // NOI18N
        x2Lbl.setForeground(new java.awt.Color(51, 255, 0));
        x2Lbl.setText(" X ");
        x2Lbl.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        selViewPanel.add(x2Lbl, gridBagConstraints);

        uniteLbl.setBackground(new java.awt.Color(0, 0, 0));
        uniteLbl.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        uniteLbl.setForeground(new java.awt.Color(51, 255, 0));
        uniteLbl.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        uniteLbl.setText("Unité");
        uniteLbl.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        selViewPanel.add(uniteLbl, gridBagConstraints);

        x2Lbl2.setBackground(new java.awt.Color(0, 0, 0));
        x2Lbl2.setFont(new java.awt.Font("Square721 BT", 0, 18)); // NOI18N
        x2Lbl2.setForeground(new java.awt.Color(51, 255, 0));
        x2Lbl2.setText(" S.Total:");
        x2Lbl2.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        selViewPanel.add(x2Lbl2, gridBagConstraints);

        totalLbl.setBackground(new java.awt.Color(0, 0, 0));
        totalLbl.setFont(new java.awt.Font("DS-Digital", 0, 40)); // NOI18N
        totalLbl.setForeground(new java.awt.Color(0, 255, 0));
        totalLbl.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        totalLbl.setText("0.00");
        totalLbl.setPreferredSize(new java.awt.Dimension(67, 36));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        selViewPanel.add(totalLbl, gridBagConstraints);

        jLabel1.setBackground(new java.awt.Color(0, 0, 0));
        jLabel1.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 10;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        selViewPanel.add(jLabel1, gridBagConstraints);

        useCBMenuItem.setText("Chercher par Code Barres");
        useCBMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useCBMenuItemActionPerformed(evt);
            }
        });
        cbFieldMenu.add(useCBMenuItem);

        setFocusable(false);
        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    private void useCBMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useCBMenuItemActionPerformed
        cbField.setFocusable(useCBMenuItem.isSelected());
        cbField.setEditable(useCBMenuItem.isSelected());
    }//GEN-LAST:event_useCBMenuItemActionPerformed

    private void cbFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cbFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            EnStock lotEnStk = LotEnStockDAO.getInstance().getByCodBar(cbField.getText().trim());
            if (lotEnStk == null) {
                cbField.setBackground(Color.RED);
                Toolkit.getDefaultToolkit().beep();
            } else {
                cbField.setBackground(Color.BLACK);
                addLigneVnt(lotEnStk);
            }
            cbField.selectAll();
            evt.consume();
        }
    }//GEN-LAST:event_cbFieldKeyPressed

    private void cbFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbFieldFocusLost
        cbField.setText("Alt+C");
    }//GEN-LAST:event_cbFieldFocusLost

    private void cbFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbFieldFocusGained
        cbField.setText("Entrer C.Bar!");
        cbField.selectAll();
    }//GEN-LAST:event_cbFieldFocusGained

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public myComponents.MyJField cbField;
    private javax.swing.JPopupMenu cbFieldMenu;
    private myComponents.ImagePanel cbImgPanel;
    private myComponents.ImagePanel curvedPanel;
    private com.l2fprod.common.swing.JLinkButton deleteBtn;
    private com.l2fprod.common.swing.JLinkButton editBtn;
    private javax.swing.JLabel jLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel prixLbl;
    private javax.swing.JLabel qteLbl;
    private javax.swing.JPanel selViewPanel;
    private javax.swing.JLabel totalLbl;
    private javax.swing.JLabel uniteLbl;
    private javax.swing.JCheckBoxMenuItem useCBMenuItem;
    private javax.swing.JLabel x2Lbl;
    private javax.swing.JLabel x2Lbl1;
    private javax.swing.JLabel x2Lbl2;
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
                frame.getContentPane().add(new DetailsVentePanel(frame, false).initTableView(), BorderLayout.CENTER);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
