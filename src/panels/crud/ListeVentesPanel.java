/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * CtegoriePanel.java
 *
 * Created on 17/10/2009, 11:25:33 ص
 */
package panels.crud;

import entities.Client;
import entities.Vente;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JOptionPane;
import myComponents.MyJFrame;
import panels.ResultSet2Table_Panel;
import dao.VenteDAO;
import dialogs.LoginDialog;
import entities.AppUser;
import java.math.BigDecimal;
import java.sql.ResultSet;
import panels.maj.MajVentePanel;
import tools.DateTools;

/**
 *
 * @author alilo
 */
public class ListeVentesPanel extends ResultSet2Table_Panel<Vente, VenteDAO> {

    {
        setPreferredSize(new Dimension(700, 400));
        setMajPanel(new MajVentePanel(this));
        setFilterShortcut("CTRL", "T");
    }

    private ListeClientsPanel clientsPanel;
    private ListeUtilisateursPanel usersListP;
    private Date beginDate, endDate;

    public ListeVentesPanel(Container owner, boolean checkable) {
        super(owner, checkable);
        initComponents();
        add(mainPanel, BorderLayout.CENTER);
        //statusPanel.add(recettePanel);
        configCalendar();
        initUsersPanel();
        initClientsPanel();
        if (LoginDialog.isLoginAsAdmin()) {
            statusPanel.add(totalVntsP);
        } else {
            //filterList("Vendeur", LoginDialog.getUser().getLogin(), MyJTable.EXACT_MATCH_FILTER);
            selUserPanel.setSelEntity(LoginDialog.getUser());
            selUserPanel.setEnabled(false);
        }
    }

    @Override
    public ListeVentesPanel initTableView() {
        getTable().setColumnVisible("ID", false);
        getTable().setColumnVisible("ID_CL", false);
        getTable().setColumnVisible("ID_USER", false);
        getTable().setColumnsPreferredWidths(new int[]{20, 60, 60, 40});
        return this;
    }

    @Override
    public VenteDAO getTableDAO() {
        return VenteDAO.getInstance();
    }

    @Override
    public ResultSet getResultSet() {
        if (beginDate == null) {
            beginDate = DateTools.TODAY;
        }
        if (endDate == null) {
            endDate = DateTools.TODAY;
        }
        //return getTableDAO().getByDate(beginDate, endDate);
        return getTableDAO().getAll();
    }

    @Override
    public void delete() {
        if (isAllowDelete() && !getModel().getSelectedRows().isEmpty()) {
            String confMess = "Voullez-vous vraiment supprimer les ventes sélectionnées?";
            int rep = JOptionPane.showConfirmDialog(this, confMess, "Suppression", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (rep == JOptionPane.YES_OPTION) {
                for (Integer id : getModel().getSelectedIDs()) {
                    Vente vntToDelete = getTableDAO().getObjectByID(id);
                    if (vntToDelete.isValidee()) {
                        String mess = "Attention! La: " + vntToDelete + "\nest déja validée, vous ne pouvez pas la supprimer."
                                + "\nSi vous-voulez vraiment la supprimer vous devez l'annuler d'abord!"
                                + "\nPour annuler, utilliser la fonction 'Annuler' dans la fenêtre de modification.";
                        rep = JOptionPane.showConfirmDialog(this, mess, "Attention!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                        if (rep == JOptionPane.CANCEL_OPTION) {
                            break;
                        }
                    } else {
                        getTableDAO().delete(id);
                        getTableDAO().commit();
                    }
                }
                reload();
            }
        }
    }

    public void setPeriod(java.util.Date beginDate, java.util.Date endDate) {
        this.beginDate = beginDate;
        this.endDate = endDate;
        reload();
    }

    private void configCalendar() {
        // set the date to yesterday
        final Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);
        dateChooser.setDate(c.getTime());

        // add date change listener.
        dateChooser.addPropertyChangeListener("date", new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                Date day = dateChooser.getDate();
                setPeriod(day, day);
            }
        });

        beginDateChooser.setDate(c.getTime());
        beginDateChooser.addPropertyChangeListener("date", new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (periodRadioBox.isSelected()) {
                    c.setTime(beginDateChooser.getDate());
                    c.add(Calendar.DATE, 1);
                    endDateChooser.setMinSelectableDate(c.getTime());
                }
            }
        });

        c.add(Calendar.DATE, 1);
        endDateChooser.setMinSelectableDate(c.getTime());
        endDateChooser.addPropertyChangeListener("date", new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (periodRadioBox.isSelected()) {
                    if (beginDateChooser.getDate().before(endDateChooser.getDate())) {
                        setPeriod(beginDateChooser.getDate(), endDateChooser.getDate());
                    } else {
                        JOptionPane.showMessageDialog(null, "La date de fin de période doit être aprés la date de début!", "Attention", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
    }

    public final void initClientsPanel() {
        clientsPanel = new ListeClientsPanel(this, false).getNavigNEditList();
        setMasterPanel(clientsPanel, "ID_CL");
        selClientPanel.setSelListPanel(clientsPanel);
    }

    public final void initUsersPanel() {
        usersListP = new ListeUtilisateursPanel(this, false);
        setMasterPanel(usersListP, "ID_USER");
        selUserPanel.setSelListPanel(usersListP);
    }

    @Override
    public void doOnTableModelChanged() {
        super.doOnTableModelChanged();
        showTotal();
    }

    @Override
    public void doOnTableRowSortChanged() {
        //System.out.println("table row sort change");
        super.doOnTableRowSortChanged();
        showTotal();
    }

    public final BigDecimal calculateTotal() {
        long t = System.currentTimeMillis();
        BigDecimal total = new BigDecimal(0);
        for (int i = 0; i < getTable().getRowCount(); i++) {
            BigDecimal totalRow = (BigDecimal) getTable().getValueAt(i, "Total(DA)");
            total = total.add(totalRow);
        }
        //System.out.println("calc total: " + (System.currentTimeMillis() - t));
        return total;
    }

    private void showTotal() {
        if (LoginDialog.isLoginAsAdmin()) {
            totalVntsP.setMontant(calculateTotal());
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
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        buttonGroup1 = new javax.swing.ButtonGroup();
        totalVntsP = new panels.views.MontantPanel();
        filterPanel = new javax.swing.JPanel();
        allRadioBox = new javax.swing.JRadioButton();
        todayRadioBox = new javax.swing.JRadioButton();
        dateRadioBox = new javax.swing.JRadioButton();
        dateChooser = new myComponents.MyJDateChooser();
        selClientPanel = new panels.SelectionPanel<>();
        jSeparator1 = new javax.swing.JSeparator();
        periodRadioBox = new javax.swing.JRadioButton();
        beginDateChooser = new myComponents.MyJDateChooser();
        toL = new javax.swing.JLabel();
        endDateChooser = new myComponents.MyJDateChooser();
        selUserPanel = new panels.SelectionPanel<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();

        totalVntsP.setMontantFont(new java.awt.Font("Agency FB", 0, 36)); // NOI18N
        totalVntsP.setTitleFont(new java.awt.Font("Agency FB", 0, 36)); // NOI18N
        totalVntsP.setTitleString("Total.Ventes:");

        setMinimumSize(new java.awt.Dimension(400, 300));
        setLayout(new java.awt.BorderLayout());

        filterPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 2, 2, 2));
        filterPanel.setFocusable(false);
        java.awt.GridBagLayout filterPanelLayout = new java.awt.GridBagLayout();
        filterPanelLayout.columnWidths = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        filterPanelLayout.rowHeights = new int[] {0, 5, 0, 5, 0, 5, 0};
        filterPanel.setLayout(filterPanelLayout);

        buttonGroup1.add(allRadioBox);
        allRadioBox.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        allRadioBox.setText("Tous");
        allRadioBox.setBorder(null);
        allRadioBox.setFocusable(false);
        allRadioBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allRadioBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        filterPanel.add(allRadioBox, gridBagConstraints);

        buttonGroup1.add(todayRadioBox);
        todayRadioBox.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        todayRadioBox.setSelected(true);
        todayRadioBox.setText("Aujourd'hui ");
        todayRadioBox.setBorder(null);
        todayRadioBox.setFocusable(false);
        todayRadioBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                todayRadioBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.3;
        filterPanel.add(todayRadioBox, gridBagConstraints);

        buttonGroup1.add(dateRadioBox);
        dateRadioBox.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        dateRadioBox.setText("Le: ");
        dateRadioBox.setBorder(null);
        dateRadioBox.setFocusable(false);
        dateRadioBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dateRadioBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        filterPanel.add(dateRadioBox, gridBagConstraints);

        dateChooser.setFocusable(false);
        dateChooser.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        dateChooser.setMinimumSize(new java.awt.Dimension(105, 19));
        dateChooser.setPreferredSize(new java.awt.Dimension(105, 19));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, dateRadioBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), dateChooser, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        filterPanel.add(dateChooser, gridBagConstraints);

        selClientPanel.setDescription("Tous les clients!");
        selClientPanel.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        filterPanel.add(selClientPanel, gridBagConstraints);

        jSeparator1.setPreferredSize(new java.awt.Dimension(0, 5));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 15;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        filterPanel.add(jSeparator1, gridBagConstraints);

        buttonGroup1.add(periodRadioBox);
        periodRadioBox.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        periodRadioBox.setText("De:");
        periodRadioBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        periodRadioBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                periodRadioBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        filterPanel.add(periodRadioBox, gridBagConstraints);

        beginDateChooser.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        beginDateChooser.setPreferredSize(new java.awt.Dimension(130, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, periodRadioBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), beginDateChooser, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        filterPanel.add(beginDateChooser, gridBagConstraints);

        toL.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        toL.setText(" à: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        filterPanel.add(toL, gridBagConstraints);

        endDateChooser.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        endDateChooser.setPreferredSize(new java.awt.Dimension(130, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, periodRadioBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), endDateChooser, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 14;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        filterPanel.add(endDateChooser, gridBagConstraints);

        selUserPanel.setDescription("Tous les vendeurs!");
        selUserPanel.setFocusTraversalPolicyProvider(true);
        selUserPanel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        filterPanel.add(selUserPanel, gridBagConstraints);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Vendeur: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        filterPanel.add(jLabel1, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Client: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        filterPanel.add(jLabel2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 15;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        filterPanel.add(jSeparator2, gridBagConstraints);

        add(filterPanel, java.awt.BorderLayout.PAGE_START);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void todayRadioBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_todayRadioBoxActionPerformed
        setPeriod(DateTools.TODAY, DateTools.TODAY);
    }//GEN-LAST:event_todayRadioBoxActionPerformed

    private void dateRadioBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dateRadioBoxActionPerformed
        Date date = dateChooser.getDate();
        setPeriod(date, date);
    }//GEN-LAST:event_dateRadioBoxActionPerformed

    private void allRadioBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allRadioBoxActionPerformed
        setPeriod(DateTools.getMinJavaDate(), DateTools.getMaxJavaDate());
    }//GEN-LAST:event_allRadioBoxActionPerformed

    private void periodRadioBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_periodRadioBoxActionPerformed
        setPeriod(beginDateChooser.getDate(), endDateChooser.getDate());
    }//GEN-LAST:event_periodRadioBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton allRadioBox;
    private myComponents.MyJDateChooser beginDateChooser;
    private javax.swing.ButtonGroup buttonGroup1;
    private myComponents.MyJDateChooser dateChooser;
    private javax.swing.JRadioButton dateRadioBox;
    private myComponents.MyJDateChooser endDateChooser;
    private javax.swing.JPanel filterPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JRadioButton periodRadioBox;
    private panels.SelectionPanel<Client> selClientPanel;
    private panels.SelectionPanel<AppUser> selUserPanel;
    private javax.swing.JLabel toL;
    private javax.swing.JRadioButton todayRadioBox;
    private panels.views.MontantPanel totalVntsP;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        MyJFrame frame = new MyJFrame();
        //frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setTitle("Gestion des Ventes");
        frame.getContentPane().add(new ListeVentesPanel(frame, false).initTableView(), BorderLayout.CENTER);
        frame.pack();

        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
}
