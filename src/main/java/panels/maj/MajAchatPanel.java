/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DetailsVentePanel.java
 *
 * Created on 31/08/2011, 09:16:35 ص
 */
package panels.maj;

import dao.AchatDAO;
import entities.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import dao.FournissDAO;
import java.awt.Component;
import panels.CRUDPanel;
import panels.ResultSet_Panel;
import panels.details.DetailsAchatPanel;
import panels.crud.ListeFournissPanel;
import tools.DateTools;

/**
 *
 * @author alilo
 */
public class MajAchatPanel extends MajPanel<Achat, AchatDAO> {

    private DetailsAchatPanel detailsAchatPanel;
    private ListeFournissPanel fournissPanel;
    // Vente fields
    private Fournisseur fourniss;
    private Date date;
    private Date heure;
    private int numAchat;
    private boolean validated = false;
    //formater la date et l'heure
    private final DateFormat shortDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final DateFormat timeFormat = new SimpleDateFormat("HH:mm");

    /**
     * Creates new form DetailsVentePanel
     *
     * @param listPanel
     */
    public MajAchatPanel(CRUDPanel listPanel) {
        super(listPanel);
        initComponents();
        initMajPanel(fieldsPanel);
        initFournissPanel();
        configCalendar();
        setDetailsAchPanel(null);
    }

    @Override
    public final void initMajPanel(Component fieldsPanel) {
        super.initMajPanel(fieldsPanel);
        //setActionsShortcuts();
        //okAction.setEnabled(false);
        //addAction.setEnabled(false);
        //printAction.setEnabled(false);
    }

    @Override
    public void loadPreferences() {
    }

    @Override
    public final AchatDAO getTableDAO() {
        return AchatDAO.getInstance();
    }

    @Override
    public void setActionsShortcuts() {
        super.setActionsShortcuts();
        doOnPress(KeyEvent.VK_F4, 0, paiementAct, JComponent.WHEN_IN_FOCUSED_WINDOW);
        doOnPress(KeyEvent.VK_F7, 0, versementAction, JComponent.WHEN_IN_FOCUSED_WINDOW);
        doOnPress(KeyEvent.VK_F9, 0, creditAction, JComponent.WHEN_IN_FOCUSED_WINDOW);
        doOnPress(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK, annulerReceptionAct, JComponent.WHEN_IN_FOCUSED_WINDOW);
        doOnPress(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK, printAction, JComponent.WHEN_IN_FOCUSED_WINDOW);
        doOnPress(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK, previewAction, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    public void setDate(Date date) {
        setModified(this.date != date);
        this.date = date;
        dateChooser.setDateStr(shortDateFormat.format(date));
        // get the first day in the current year;
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_YEAR, 1);
        Date beginDate = c.getTime();
        int num = getTableDAO().getNumAchat(beginDate, date);
        setNum(num);
    }

    public Date getDate() {
        return date;
    }

    public void setHeure(Date heure) {
        setModified(this.heure != heure);
        this.heure = heure;
        heureField.setText(timeFormat.format(heure));
    }

    public Date getHeure() {
        return heure;
    }

    public void setNum(int num) {
        setModified(this.numAchat != num);
        this.numAchat = num;
        numField.setText(String.valueOf(num));
    }

    public int getNum() {
        return numAchat;
    }

    public void setFourniss(Fournisseur fourn) {
        setModified(!fourn.equals(this.fourniss));
        this.fourniss = fourn;
        selFournissPanel.setSelEntity(fourniss);
    }

    public Fournisseur getFourniss() {
        return fourniss;
    }

    /**
     * Commit the changes to <b>'Vente, RegelementVente'</b> tables, and reload
     * the details panel of this <b>'Vente'</b>;\n This method must called after
     * all validation operations.
     *
     * @param valide : boolean, indicate if this 'Vente' is validated or not.
     */
    public void setValidated(boolean valide) {
        // if the validation state is changed then we need to reflect that in 'DetailsVentePanel' and 'Client'.
        detailsAchatPanel.setMasterAchat(getEditedEntity());
        if (fourniss != null && fourniss.getId() > 0) {
            // Update client infos after validation because 'Credit' must be changed after validation
            setFourniss(FournissDAO.getInstance().getObjectByID(fourniss.getId()));
        }
        this.validated = valide;
    }

    public boolean isValide() {
        return validated;
    }

    public DetailsAchatPanel getDetailsAchatPanel() {
        return detailsAchatPanel;
    }

    @Override
    public void setChildEntity(EntityClass childEntity, boolean allowChng) {
        if (childEntity instanceof Fournisseur) {
            selFournissPanel.setAllowSelChange(allowChng);
            setFourniss((Fournisseur) childEntity);
        }
    }

    @Override
    public void viewSelEntity(EntityClass entity) {
        if (entity instanceof Fournisseur) {
            setFourniss((Fournisseur) entity);
        } else {
            setFourniss(Fournisseur.ANONYME);
        }
    }

    @Override
    public void initFields(Achat oldEntity) {
        setDate(DateTools.getSqlDate(oldEntity.getDate()));
        setNum(oldEntity.getNum());//must setting the num after setting the date to ignore num calc.
        setHeure(DateTools.getSqlTime(oldEntity.getHeure()));
        validated = oldEntity.isValide();
        setFourniss(oldEntity.getFournisseur());
        detailsAchatPanel.setMasterEntity(oldEntity);
    }

    @Override
    public void setModifAllowed(boolean allow) {
        super.setModifAllowed(allow);
        dateChooser.setEnabled(allow);
        heureField.setEnabled(allow);
        numField.setEnabled(allow);
        selFournissPanel.setEnabled(allow);
    }

    public final void configCalendar() {
        dateChooser.getDateTextFieldEditor().setFocusable(false);
        // Restrict the max selectable date to today.
        dateChooser.setMaxSelectableDate(new Date());
        Calendar c = Calendar.getInstance();
        // // Restrict the minimum date to one month ago. Substract one month from the currnet date;
        c.add(Calendar.MONTH, -3);
        // Set the minimal date.
        dateChooser.setMinSelectableDate(c.getTime());
        // Listen to date changes.
        dateChooser.addPropertyChangeListener("date", new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                setDate(dateChooser.getDate());
            }
        });
    }

    @Override
    public boolean verifyFields() {
        return true;
    }

    @Override
    public void clearFields() {
        //Clear vente fields
        setDate(new Date());
        setHeure(new Date());
        setFourniss(new Fournisseur(0));
        validated = false;
        //Clear detailsVente
        detailsAchatPanel.setMasterEntity(new Achat());
        detailsAchatPanel.getDefaultFocusedComp().requestFocus();
    }

    private void initFournissPanel() {
        fournissPanel = new ListeFournissPanel(this, false).getNavigNEditList();
        fournissPanel.getPropertyChangeSupport().addPropertyChangeListener(ResultSet_Panel.ENTITY_TO_VIEW_PROPERTY, this);
        selFournissPanel.setSelListPanel(fournissPanel);
    }

    private void initDetailsPanel() {
        detailsAchatPanel = new DetailsAchatPanel(this, false) {

            @Override
            public void insert() {
                // if the current 'Vente' is not in update mode and is not saved then save it before inserting new 'LingeVente'.
                if (((isUpdate() || isSaved()) && !isModified()) || save()) {
                    super.insert();
                }
            }
        }.initTableView();
    }

    public final void setDetailsAchPanel(DetailsAchatPanel details) {
        if (details == null) {
            initDetailsPanel();
        } else {
            detailsHolderPanel.remove(detailsAchatPanel);
            detailsAchatPanel = details;
        }
        detailsAchatPanel.getModel().addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                boolean validated = detailsAchatPanel.getMasterAchat().isValide();
                boolean isEmpty = detailsAchatPanel.getModel().getRowCount() == 0;
                commandRB.setEnabled(isEmpty);
                receptionRB.setEnabled(isEmpty);
                retourRB.setEnabled(isEmpty);
                
                setModifAllowed(!validated);
                okAction.setEnabled(!isEmpty && !validated);
                addAction.setEnabled(!isEmpty);
                paiementAct.setEnabled(!isEmpty && !validated);
                versementAction.setEnabled(!isEmpty && !(validated && fourniss.isAnonyme()));
                if (detailsAchatPanel.getMasterAchat().getReglement() != null) {
                    verseButton.setText("Modif.Regl (F7)");
                } else {
                    verseButton.setText("Versement (F7)");
                }
                creditAction.setEnabled(paiementAct.isEnabled());
                annulerReceptionAct.setEnabled(validated);
                printAction.setEnabled(!isEmpty);
                previewAction.setEnabled(!isEmpty);
            }
        });
        detailsHolderPanel.add(detailsAchatPanel);
    }

    @Override
    public boolean save() {
        if (isValide()) {
            return true;
        }
        boolean save = false;
        if (verifyFields()) {
            Achat achat = new Achat(0, numAchat, date, heure, false);
            achat.setFournisseur(fourniss);
            setEditedEntity(achat);
            save = super.save();
            // Reload the detailsVente panel with the new Vente
            detailsAchatPanel.setMasterEntity(getEditedEntity());
        } else {
            setSaved(false);
        }
        return save;
    }

    /**
     * Call the payVente() method to validate this 'Vente' as payed, then call
     * add() method.
     */
    private final Action paiementAct = new AbstractAction("Payée") {

        {
            putValue(Action.SHORT_DESCRIPTION, "Valider cet achat, et consédérer comme payé!");
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (paiement()) {
                //add();
            }
        }
    };

    /**
     * Validate the actuel 'Vente' as payed.
     *
     * @return
     */
    public boolean paiement() {
        if (isValide()) {
            return true;
        }
        boolean validationDone = save() && getEditedEntity().validate();
        if (validationDone) {
            if (getEditedEntity().getFournisseur() != null && !getEditedEntity().getFournisseur().isAnonyme()) {
                BigDecimal total = getEditedEntity().getTotal();
                if (total.doubleValue() != 0) {
                    ReglementFr reglementFr = new ReglementFr(0, date, heure, total, ModePaye.ESPECE, "Règlement de l'" + getEditedEntity());
                    reglementFr.setFournisseur(getEditedEntity().getFournisseur());
                    reglementFr.setAchat(getEditedEntity());
                    reglementFr.insert();
                }
            }
            setValidated(validationDone);
        }
        return validationDone;
    }
    /**
     * Call the versement() method to do an 'Versement' for this 'Vente', then
     * call add() method.
     */
    private final Action versementAction = new AbstractAction("Versement") {

        {
            putValue(Action.SHORT_DESCRIPTION, "Règlement du l'achat.");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (versement()) {
                //add();
            }
        }
    };

    private final MajReglementFrPanel reglementFrPanel = new MajReglementFrPanel(null);

    public boolean versement() {
        boolean versmentDone = false;
        if (credit()) {
            ReglementFr reglFr = getEditedEntity().getReglement();
            if (reglFr.getMontant().doubleValue() == 0) {
                reglFr.setMontant(getEditedEntity().getTotal());
                reglFr.setModePaye(ModePaye.ESPECE);
            }
            reglementFrPanel.showEditPanel(reglFr);
            versmentDone = reglementFrPanel.isSaved();
            setValidated(isValide() || versmentDone);
        }
        return versmentDone;
    }
    /**
     * Call the credit() method to validate this 'Vente' as not payed, then call
     * add() method.
     */
    private final Action creditAction = new AbstractAction("Crédit") {

        {
            putValue(Action.SHORT_DESCRIPTION, "Valider cet achat, en tant que non payée!");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (credit()) {
                //add();
            }
        }
    };

    /**
     * Validate this 'Vente' as not payed.
     *
     * @return
     */
    public boolean credit() {
        if (isValide()) {
            return true;
        }
        if (fourniss == null || fourniss.isAnonyme()) {
            //JOptionPane.showMessageDialog(this, "Séléctionnez le Client SVP!", "Attention!", JOptionPane.ERROR_MESSAGE);
            selFournissPanel.showSelList();
            if (fourniss == null || fourniss.isAnonyme()) {
                return false;
            }
        }
        boolean validationDone = save() && getEditedEntity().validate();
        if (validationDone) {
            BigDecimal totalAchat = getEditedEntity().getTotal();
            if (totalAchat.doubleValue() != 0) {
                // insert the Reglement to indicate that is not payed.
                ReglementFr reglementFr = new ReglementFr(0, date, heure, BigDecimal.ZERO, ModePaye.CREDIT, "Règlement de l': " + getEditedEntity());
                reglementFr.setFournisseur(getEditedEntity().getFournisseur());
                reglementFr.setAchat(getEditedEntity());
                reglementFr.insert();
            }
            setValidated(validationDone);
        }
        return validationDone;
    }
    private final Action annulerReceptionAct = new AbstractAction() {

        {
            putValue(Action.SHORT_DESCRIPTION, "Annuler la réception!");
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            annulerReception();
        }
    };

    public boolean annulerReception() {
        String mess = "Voulez-vous vraiment annuler cet Reception?\n"
                + "Attention:\n"
                + " - Les règlements de cet reception serons supprimer.\n"
                + " - Les lot en stock issus du cet reception serons supprimer.";
        int rep = JOptionPane.showConfirmDialog(this, mess, "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (rep == JOptionPane.YES_OPTION) {
            if (getEditedEntity().invalidate()) {
                setValidated(false);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean cancel() {
        if(isValide()){
            return annulerReception();
        }
        return super.cancel(); 
    }
 
    public void preview() {
    }
    public Action previewAction = new AbstractAction("Aperçu") {

        {
            putValue(Action.ACCELERATOR_KEY, "Ctrl+Alt+P");
            putValue(Action.SHORT_DESCRIPTION, "Aperçu le bon avant l'impression (Ctrl+Alt+P)");
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            preview();
        }
    };

    public void print() {
    }
    public Action printAction = new AbstractAction("Imprimer") {

        {
            putValue(Action.ACCELERATOR_KEY, "Ctrl+P");
            putValue(Action.SHORT_DESCRIPTION, "Imprimer le bon du vente (Ctrl+P),\nPour afficher l'aperçu (Ctrl+Alt+P)");
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            print();
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

        fieldsPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        commandRB = new javax.swing.JRadioButton();
        receptionRB = new javax.swing.JRadioButton();
        retourRB = new javax.swing.JRadioButton();
        jSeparator9 = new javax.swing.JSeparator();
        achatFieldsPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        dateChooser = new myComponents.MyJDateChooser();
        jLabel5 = new javax.swing.JLabel();
        heureField = new myComponents.IntegerJField();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jLabel2 = new javax.swing.JLabel();
        numField = new myComponents.IntegerJField();
        jSeparator8 = new javax.swing.JSeparator();
        jLabel7 = new javax.swing.JLabel();
        selFournissPanel = new panels.SelectionPanel<Fournisseur>();
        detailsHolderPanel = new javax.swing.JPanel();
        buttonsPanel = new javax.swing.JPanel();
        printButton = new com.l2fprod.common.swing.JLinkButton();
        catchButton = new com.l2fprod.common.swing.JLinkButton();
        verseButton = new com.l2fprod.common.swing.JLinkButton();
        creditButton = new com.l2fprod.common.swing.JLinkButton();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        setLayout(new java.awt.BorderLayout());

        fieldsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        fieldsPanel.setLayout(new java.awt.GridBagLayout());

        jPanel1.setMinimumSize(new java.awt.Dimension(273, 30));
        jPanel1.setPreferredSize(new java.awt.Dimension(273, 30));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        commandRB.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        commandRB.setText("Commande");
        commandRB.setEnabled(false);
        commandRB.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(commandRB, gridBagConstraints);

        receptionRB.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        receptionRB.setSelected(true);
        receptionRB.setText("Réception");
        receptionRB.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        receptionRB.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(receptionRB, gridBagConstraints);

        retourRB.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        retourRB.setText("Retour");
        retourRB.setEnabled(false);
        retourRB.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        retourRB.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(retourRB, gridBagConstraints);

        jSeparator9.setMinimumSize(new java.awt.Dimension(0, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(jSeparator9, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        fieldsPanel.add(jPanel1, gridBagConstraints);

        achatFieldsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 5, 1));
        java.awt.GridBagLayout vntFieldsPanelLayout = new java.awt.GridBagLayout();
        vntFieldsPanelLayout.columnWidths = new int[] {0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0};
        vntFieldsPanelLayout.rowHeights = new int[] {0, 3, 0, 3, 0};
        achatFieldsPanel.setLayout(vntFieldsPanelLayout);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel3.setText(" Date:");
        jLabel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        achatFieldsPanel.add(jLabel3, gridBagConstraints);

        dateChooser.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        dateChooser.setMinimumSize(new java.awt.Dimension(60, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        achatFieldsPanel.add(dateChooser, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel5.setText(" Heure:");
        jLabel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        achatFieldsPanel.add(jLabel5, gridBagConstraints);

        heureField.setEditable(false);
        heureField.setBackground(new java.awt.Color(229, 229, 255));
        heureField.setText("--:--");
        heureField.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.4;
        achatFieldsPanel.add(heureField, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.01;
        achatFieldsPanel.add(filler1, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("  Achat N°:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        achatFieldsPanel.add(jLabel2, gridBagConstraints);

        numField.setEditable(false);
        numField.setBackground(new java.awt.Color(229, 229, 255));
        numField.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.3;
        achatFieldsPanel.add(numField, gridBagConstraints);

        jSeparator8.setMinimumSize(new java.awt.Dimension(0, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        achatFieldsPanel.add(jSeparator8, gridBagConstraints);

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText(" Fournisseur:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        achatFieldsPanel.add(jLabel7, gridBagConstraints);

        selFournissPanel.setBackground(new java.awt.Color(229, 229, 255));
        selFournissPanel.setDescription("Anonyme!");
        selFournissPanel.setMinimumSize(new java.awt.Dimension(38, 25));
        selFournissPanel.setPreferredSize(new java.awt.Dimension(44, 25));
        selFournissPanel.setShortcutKey("F3");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        achatFieldsPanel.add(selFournissPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        fieldsPanel.add(achatFieldsPanel, gridBagConstraints);

        detailsHolderPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        detailsHolderPanel.setFocusable(false);
        detailsHolderPanel.setPreferredSize(new java.awt.Dimension(12, 400));
        detailsHolderPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        fieldsPanel.add(detailsHolderPanel, gridBagConstraints);

        buttonsPanel.setBackground(new java.awt.Color(0, 0, 0));
        buttonsPanel.setMinimumSize(new java.awt.Dimension(525, 38));
        buttonsPanel.setPreferredSize(new java.awt.Dimension(597, 40));
        java.awt.GridBagLayout buttonsPanelLayout = new java.awt.GridBagLayout();
        buttonsPanelLayout.columnWidths = new int[] {0, 5, 0, 5, 0, 5, 0};
        buttonsPanelLayout.rowHeights = new int[] {0};
        buttonsPanel.setLayout(buttonsPanelLayout);

        printButton.setAction(printAction);
        printButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        printButton.setForeground(new java.awt.Color(0, 255, 0));
        printButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/print16.png"))); // NOI18N
        printButton.setText("Imprimer (Ctrl+P)");
        printButton.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        printButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        buttonsPanel.add(printButton, gridBagConstraints);

        catchButton.setAction(paiementAct);
        catchButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        catchButton.setForeground(new java.awt.Color(0, 255, 0));
        catchButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/mony24.png"))); // NOI18N
        catchButton.setText("Payement (F4)");
        catchButton.setToolTipText("Le client a réglé cette vente (F4)");
        catchButton.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        buttonsPanel.add(catchButton, gridBagConstraints);

        verseButton.setAction(versementAction);
        verseButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        verseButton.setForeground(new java.awt.Color(0, 255, 0));
        verseButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/vers24.png"))); // NOI18N
        verseButton.setText("Versement (F7)");
        verseButton.setToolTipText("Le client a versé une somme différente du total de vente (F7)");
        verseButton.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        buttonsPanel.add(verseButton, gridBagConstraints);

        creditButton.setAction(creditAction);
        creditButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        creditButton.setForeground(new java.awt.Color(0, 255, 0));
        creditButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/credit24.png"))); // NOI18N
        creditButton.setText("Crédit (F9)");
        creditButton.setToolTipText("Le client n'a pas réglé cette vente (F9)");
        creditButton.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        buttonsPanel.add(creditButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        fieldsPanel.add(buttonsPanel, gridBagConstraints);

        add(fieldsPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void printButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_printButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel achatFieldsPanel;
    private javax.swing.JPanel buttonsPanel;
    private com.l2fprod.common.swing.JLinkButton catchButton;
    private javax.swing.JRadioButton commandRB;
    private com.l2fprod.common.swing.JLinkButton creditButton;
    private myComponents.MyJDateChooser dateChooser;
    private javax.swing.JPanel detailsHolderPanel;
    private javax.swing.JPanel fieldsPanel;
    private javax.swing.Box.Filler filler1;
    private myComponents.IntegerJField heureField;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private myComponents.IntegerJField numField;
    private com.l2fprod.common.swing.JLinkButton printButton;
    private javax.swing.JRadioButton receptionRB;
    private javax.swing.JRadioButton retourRB;
    private panels.SelectionPanel<Fournisseur> selFournissPanel;
    private com.l2fprod.common.swing.JLinkButton verseButton;
    // End of variables declaration//GEN-END:variables

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new MajAchatPanel(null).showNewPanel(null);
            }
        });
    }
}
