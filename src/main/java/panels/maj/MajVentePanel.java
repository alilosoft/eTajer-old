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
import panels.ResultSet_Panel;
import panels.details.DetailsVentePanel;
import panels.crud.ListeClientsPanel;
import dao.ClientDAO;
import dao.ReglementClDAO;
import dao.VenteDAO;
import java.awt.Component;
import java.util.logging.Level;
import panels.CRUDPanel;
import printing.VentePrinting;
import tools.MessageReporting;

/**
 *
 * @author alilo
 */
public class MajVentePanel extends MajPanel<Vente, VenteDAO> {

    private static final String TYPE_VNT_PREF = "TypeVntPref";
    private static final String AUTO_PRINT_PAYE = "PrintAfterPaye";
    private static final String AUTO_PRINT_VERSE = "PrintAfterVerse";
    private static final String AUTO_PRINT_CREDIT = "PrintAfterCredit";

    private static final String AUTO_ADD_PAYE = "AddAfterPaye";
    private static final String AUTO_ADD_VERSE = "AddAfterVerse";
    private static final String AUTO_ADD_CREDIT = "AddAfterCredit";

    private static final String PRINT_FACT = "PrintFacture";
    private static final String PRINT_BL = "PrintBL";
    private static final String PRINT_BS = "PrintBS";

    protected DetailsVentePanel detailsVentePanel;
    private ListeClientsPanel clientsPanel;
    // Vente fields
    private Client client;
    private Date date;
    private Date heure;
    private int numVnt;
    private TypeVnt typeVente;
    private boolean validee = false;
    //formater la date et l'heure
    private final DateFormat shortDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final DateFormat timeFormat = new SimpleDateFormat("HH:mm");

    /**
     * Creates new form DetailsVentePanel
     *
     * @param listPanel
     */
    public MajVentePanel(CRUDPanel listPanel) {
        super(listPanel);
        initComponents();
        initMajPanel(fieldsPanel);
        initClientsPanel();
        conifgCalendar();
        setDetailsVntPanel(null);
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
        int typeVntPrefID = typeVentCmboBox.getSelectedIndex() + 1;
        try {
            typeVntPrefID = getIntPreference(TYPE_VNT_PREF, typeVntPrefID);
        } catch (Exception e) {
            MessageReporting.showMessage(Level.SEVERE, this.getClass(), "loadPreferences()", "inccorect values!");
        }
        typeVente = new TypeVnt(typeVntPrefID);
        typeVentCmboBox.setSelectedIndex(typeVntPrefID - 1);

        boolean autoPrintPaye = getBooleanPreference(AUTO_PRINT_PAYE, false);
        autoPrintPayeItem.setSelected(autoPrintPaye);
        boolean autoAddPaye = getBooleanPreference(AUTO_ADD_PAYE, false);
        autoAddPayeItem.setSelected(autoAddPaye);
        
        boolean autoPrintVerse = getBooleanPreference(AUTO_PRINT_VERSE, false);
        autoPrintVerseItem.setSelected(autoPrintVerse);
        boolean autoAddVerse = getBooleanPreference(AUTO_ADD_VERSE, false);
        autoAddVersItem.setSelected(autoAddVerse);
        
        boolean autoPrintCredit = getBooleanPreference(AUTO_PRINT_CREDIT, false);
        autoPrintCreditItem.setSelected(autoPrintCredit);
        boolean autoAddCredit = getBooleanPreference(AUTO_ADD_CREDIT, false);
        autoAddCredItem.setSelected(autoAddCredit);

        boolean printFact = getBooleanPreference(PRINT_FACT, false);
        printFactureMenItem.setSelected(printFact);
        boolean printBL = getBooleanPreference(PRINT_BL, true);
        printBLMenItem.setSelected(printBL);
        boolean printBS = getBooleanPreference(PRINT_BS, false);
        printBSMenItem.setSelected(printBS);
    }

    @Override
    public final VenteDAO getTableDAO() {
        return VenteDAO.getInstance();
    }

    @Override
    public void setActionsShortcuts() {

        super.setActionsShortcuts();
        doOnPress(KeyEvent.VK_M, KeyEvent.CTRL_DOWN_MASK, switchTypeVntAction, JComponent.WHEN_IN_FOCUSED_WINDOW);

        doOnPress(KeyEvent.VK_F4, 0, paiementAction, JComponent.WHEN_IN_FOCUSED_WINDOW);
        doOnPress(KeyEvent.VK_F7, 0, versementAction, JComponent.WHEN_IN_FOCUSED_WINDOW);
        doOnPress(KeyEvent.VK_F9, 0, creditAction, JComponent.WHEN_IN_FOCUSED_WINDOW);
        doOnPress(KeyEvent.VK_F12, 0, retourAction, JComponent.WHEN_IN_FOCUSED_WINDOW);
        doOnPress(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK, printAction, JComponent.WHEN_IN_FOCUSED_WINDOW);
        doOnPress(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK, previewAction, JComponent.WHEN_IN_FOCUSED_WINDOW);

        doOnPress(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK, new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                showBenifice();
            }
        }, JComponent.WHEN_IN_FOCUSED_WINDOW);

        doOnRelease(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK, new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                showTotal();
            }
        }, JComponent.WHEN_IN_FOCUSED_WINDOW);
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
        int num = getTableDAO().getNumVente(beginDate, date);
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
        setModified(this.numVnt != num);
        this.numVnt = num;
        numField.setText(String.valueOf(num));
    }

    public int getNum() {
        return numVnt;
    }

    public void setTypeVente(TypeVnt typeVente) {
        setModified(!typeVente.equals(this.typeVente));
        this.typeVente = typeVente;
    }

    public TypeVnt getTypeVente() {
        return typeVente;
    }

    public void setClient(Client client) {
        setModified(!client.equals(this.client));
        this.client = client;
        selClientPanel.setSelEntity(client);
    }

    public Client getClient() {
        return client;
    }

    /**
     * Commit the changes to <b>'Vente, RegelementVente'</b> tables, and reload
     * the details panel of this <b>'Vente'</b>;\n This method must called after
     * all validation operations.
     *
     * @param validated : boolean, indicate if this 'Vente' is validated or not.
     */
    public void setValidated(boolean validated) {
        // if the validation state is changed then we need to reflect that in 'DetailsVentePanel' and 'Client'.
        detailsVentePanel.setMasterVente(getEditedEntity());
        if (client != null && client.getId() > 0) {
            // Update client infos after validation because 'Credit' must be changed after validation
            setClient(ClientDAO.getInstance().getObjectByID(client.getId()));
        }
        this.validee = validated;
    }

    public boolean isValidated() {
        return validee;
    }

    public DetailsVentePanel getDetailsVentePanel() {
        return detailsVentePanel;
    }

    @Override
    public void setChildEntity(EntityClass childEntity, boolean allowChng) {
        if (childEntity instanceof Client) {
            selClientPanel.setAllowSelChange(allowChng);
            setClient((Client) childEntity);
        }
    }

    @Override
    public void viewSelEntity(EntityClass entity) {
        if (entity instanceof Client) {
            setClient((Client) entity);
        } else {
            setClient(Client.ANONYME);
        }
    }

    @Override
    public void initFields(Vente oldEntity) {
        typeVente = oldEntity.getTypeVnt();
        typeVentCmboBox.setSelectedItem(typeVente.getDes());
        setDate(oldEntity.getDate());
        setNum(oldEntity.getNum());//must setting the num after setting the date to ignore num calc.
        setHeure(oldEntity.getHeure());
        validee = oldEntity.isValidee();
        setClient(oldEntity.getClient());
        detailsVentePanel.setMasterEntity(oldEntity);
    }

    @Override
    public void setModifAllowed(boolean allow) {
        super.setModifAllowed(allow);
        dateChooser.setEnabled(allow);
        heureField.setEnabled(allow);
        numField.setEnabled(allow);
        typeVentCmboBox.setEnabled(allow);
        selClientPanel.setAllowSelChange(allow);
    }

    public final void conifgCalendar() {
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

    private BigDecimal total;

    public final void showTotal() {
        benificeShown = false;
        total = BigDecimal.ZERO;
        if (getEditedEntity() instanceof Vente) {
            total = getEditedEntity().getTotal();
        }
        totalPanel.setMontant(total);
    }

    private boolean benificeShown = false;

    public void showBenifice() {
        if (!benificeShown && (getEditedEntity() instanceof Vente)) {
            benificeShown = true;
            totalPanel.setMontant(getEditedEntity().getBenifice());
        }
    }

    @Override
    public boolean verifyFields() {
        setTypeVente(new TypeVnt(typeVentCmboBox.getSelectedIndex() + 1));
        return true;
    }

    public void switchTypeVnt() {
        typeVentCmboBox.setSelectedIndex((typeVentCmboBox.getSelectedIndex() + 1) % 2);
    }

    public Action switchTypeVntAction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            switchTypeVnt();
        }
    };

    @Override
    public void clearFields() {
        setFirstFocusedComp(typeVentCmboBox);
        //Clear vente fields
        setDate(new Date());
        setHeure(new Date());
        setClient(new Client(0));
        validee = false;
        //Clear detailsVente
        detailsVentePanel.setMasterEntity(new Vente());
    }

    private void initClientsPanel() {
        clientsPanel = new ListeClientsPanel(this, false);
        clientsPanel.getPropertyChangeSupport().addPropertyChangeListener(ResultSet_Panel.ENTITY_TO_VIEW_PROPERTY, this);
        selClientPanel.setSelListPanel(clientsPanel.getNavigNEditList());
    }

    private void initDetailsPanel() {
        detailsVentePanel = (DetailsVentePanel) new DetailsVentePanel(this, false) {

            @Override
            public void insert() {
                // if the current 'Vente' is not in update mode and is not saved then save it before inserting new 'LingeVente'.
                if (((isUpdate() || isSaved()) && !isModified()) || save()) {
                    super.insert();
                }
            }

            @Override
            public void addLigneVnt(EnStock enStock) {
                if (((isUpdate() || isSaved()) && !isModified()) || save()) {
                    super.addLigneVnt(enStock);
                }
            }

        }.getNavigNSelectList();
    }

    public final void setDetailsVntPanel(DetailsVentePanel details) {
        if (details == null) {
            initDetailsPanel();
        } else {
            detailsHolderPanel.remove(detailsVentePanel);
            detailsVentePanel = details;
        }
        detailsVentePanel.getModel().addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                boolean validated = detailsVentePanel.getMasterVente().isValidee();
                boolean isEmpty = detailsVentePanel.getModel().getRowCount() == 0;
                setModifAllowed(!validated);
                typeVentCmboBox.setEnabled(isEmpty);
                okAction.setEnabled(!isEmpty && !validated);
                addAction.setEnabled(!isEmpty);
                deleteAction.setEnabled(!isEmpty && !validated);

                paiementAction.setEnabled(!isEmpty && !validated);
                versementAction.setEnabled(!isEmpty && !(validated && client.isAnonyme()));
                creditAction.setEnabled(paiementAction.isEnabled());
                retourAction.setEnabled(validated);
                printAction.setEnabled(validated);
                previewAction.setEnabled(validated);
                showTotal();
            }
        });
        detailsHolderPanel.add(detailsVentePanel);
    }

    @Override
    public boolean save() {
        if (isValidated()) {
            return true;
        }
        boolean save = false;
        if (verifyFields()) {
            Vente vente = new Vente(0, numVnt, date, heure, false);
            vente.setClient(client);
            vente.setTypeVnt(typeVente);
            setEditedEntity(vente);
            save = super.save();
            // Reload the detailsVente panel with the new Vente
            detailsVentePanel.setMasterEntity(getEditedEntity());
        } else {
            setSaved(false);
        }
        return save;
    }

    @Override
    public void savePreferences() {
        savePreference(TYPE_VNT_PREF, typeVente.getId());
    }
    
    private final Action paiementAction = new AbstractAction() {

        {
            putValue(Action.SHORT_DESCRIPTION, "Valider la livraison, avec paiement!");
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (paiement()) {
                if (autoPrintPayeItem.isSelected()) {
                    print();
                }
                if (autoAddPayeItem.isSelected()) {
                    add();
                }
            }
        }
    };

    public boolean paiement() {
        if (isValidated()) {
            return true;
        }
        boolean validationDone = save() && getEditedEntity().validate();
        if (validationDone) {
            if (getEditedEntity().getClient() != null && !getEditedEntity().getClient().isAnonyme()) {
                BigDecimal totalVente = getEditedEntity().getTotal();
                if (totalVente.doubleValue() != 0) {
                    ReglementCl reglementCl = new ReglementCl(0, date, heure, totalVente, ModePaye.ESPECE, "Règlement du: " + getEditedEntity());
                    reglementCl.setClient(getEditedEntity().getClient());
                    reglementCl.setVente(getEditedEntity());
                    ReglementClDAO.getInstance().insert(reglementCl);
                }
            }
            setValidated(validationDone);
        }
        return validationDone;
    }

    private final Action versementAction = new AbstractAction("Versement") {

        {
            putValue(Action.SHORT_DESCRIPTION, "Valider la livraison, avec un versement.");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (versement()) {
                if (autoPrintVerseItem.isSelected()) {
                    print();
                }
                if (autoAddVersItem.isSelected()) {
                    add();
                }
            }
        }
    };

    private final MajReglementClPanel reglementClPanel = new MajReglementClPanel(null);

    public boolean versement() {
        boolean versmentDone = false;
        if (credit()) {
            ReglementCl reglCl = getEditedEntity().getReglement();
            if (reglCl.getMontant().doubleValue() == 0) {
                reglCl.setMontant(getEditedEntity().getTotal());
                reglCl.setModePaye(ModePaye.ESPECE);
            }
            reglementClPanel.showEditPanel(reglCl);
            versmentDone = reglementClPanel.isSaved();
            setValidated(isValidated() || versmentDone);
        }
        return versmentDone;
    }

    private final Action creditAction = new AbstractAction("Crédit") {

        {
            putValue(Action.SHORT_DESCRIPTION, "Valider la livraison, sans paiement!");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (credit()) {
                if (autoPrintCreditItem.isSelected()) {
                    print();
                }
                if (autoAddCredItem.isSelected()) {
                    add();
                }
            }
        }
    };

    /**
     * Validate this 'Vente' as not payed.
     *
     * @return
     */
    public boolean credit() {
        if (client == null || client.isAnonyme()) {
            selClientPanel.showSelList();
            if (client == null || client.isAnonyme()) {
                return false;
            }
        }
        if (isValidated()) {
            return true;
        }
        boolean validationDone = save() && getEditedEntity().validate();
        if (validationDone) {
            BigDecimal totalVente = getEditedEntity().getTotal();
            if (totalVente.doubleValue() != 0) {
                // insert a 'Reglement' with 'Montant' = 0 to indicate that is not payed.
                ReglementCl reglementCl = new ReglementCl(0, date, heure, BigDecimal.ZERO, ModePaye.CREDIT, "Règlement du: " + getEditedEntity());
                reglementCl.setClient(getEditedEntity().getClient());
                reglementCl.setVente(getEditedEntity());
                reglementCl.insert();
            }
            setValidated(validationDone);
        }
        return validationDone;
    }
    private final Action retourAction = new AbstractAction() {

        {
            putValue(Action.SHORT_DESCRIPTION, "Annuler la livraison, et recupérer les produits vendus! (F12)");
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            retourLivraison();
        }
    };

    public boolean retourLivraison() {
        String mess = "Attention! Le retour d'une livraison implique:\n"
                + " - La récupération des produits vendus vers le stock.\n"
                + " - Annuler le règlement ou le crédit du client lié.\n"
                + "Voulez-vous continuer?\n";
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
    
    private final Action deleteAction = new AbstractAction() {

        {
            putValue(Action.SHORT_DESCRIPTION, "Supprimer la commande courant! (ESC)");
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            delete();
        }
    };

    public boolean delete() {
        boolean delete;
        if (isValidated()) {
            String mess = "Attention! Cette livraison est validée.\n"
                    + "Vous devez éffectuer le retour puis supprimer SVP!";
            JOptionPane.showMessageDialog(this, mess, "Confirmation", JOptionPane.INFORMATION_MESSAGE);
            return false;
        } else {
            String mess = "Voulez vous vraiment supprimer cette commande?";
            int rep = JOptionPane.showConfirmDialog(this, mess, "Confirmation", JOptionPane.YES_NO_OPTION);
            if (rep == JOptionPane.NO_OPTION) {
                return false;
            }
            delete = getEditedEntity().delete();
            editEntity(null);
            return delete;
        }
    }

    @Override
    public boolean cancel() {
        //return super.cancel(); 
        return false;
    }


    public void preview() {
        if (printBSMenItem.isSelected()) {
            VentePrinting.preview(getEditedEntity(), VentePrinting.BON_STK);
        }
        if (printBLMenItem.isSelected()) {
            VentePrinting.preview(getEditedEntity(), VentePrinting.BON_LIVR);
        }
        if (printFactureMenItem.isSelected()) {
            VentePrinting.preview(getEditedEntity(), VentePrinting.FACTURE);
        }
    }

    public Action previewAction = new AbstractAction("Aperçu") {

        {
            //putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl alt released p"));
            putValue(Action.SHORT_DESCRIPTION, "Aperçu le bon avant l'impression (Ctrl+Alt+P)");
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            preview();
        }
    };

    public void print() {
        if (printBSMenItem.isSelected()) {
            VentePrinting.print(getEditedEntity(), VentePrinting.BON_STK);
        }
        if (printBLMenItem.isSelected()) {
            VentePrinting.print(getEditedEntity(), VentePrinting.BON_LIVR);
        }
        if (printFactureMenItem.isSelected()) {
            VentePrinting.print(getEditedEntity(), VentePrinting.FACTURE);
        }
    }
    public Action printAction = new AbstractAction("Imprimer") {

        {
            //putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl released p"));
            putValue(Action.SHORT_DESCRIPTION, "Imprimer le bon du vente (Ctrl+P)");
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

        printOptionsPopupMenu = new javax.swing.JPopupMenu();
        printMItem = new javax.swing.JMenuItem();
        previewItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        printBSMenItem = new javax.swing.JCheckBoxMenuItem();
        printBLMenItem = new javax.swing.JCheckBoxMenuItem();
        printFactureMenItem = new javax.swing.JCheckBoxMenuItem();
        printOptionsBGroup = new javax.swing.ButtonGroup();
        buttonsPanel = new javax.swing.JPanel();
        saveButton = new com.l2fprod.common.swing.JLinkButton();
        insertButton = new com.l2fprod.common.swing.JLinkButton();
        printButton = new com.l2fprod.common.swing.JLinkButton();
        invalidaeButton = new com.l2fprod.common.swing.JLinkButton();
        jLabel1 = new javax.swing.JLabel();
        verseButton = new com.l2fprod.common.swing.JLinkButton();
        creditButton = new com.l2fprod.common.swing.JLinkButton();
        payeMenu = new javax.swing.JPopupMenu();
        autoAddPayeItem = new javax.swing.JCheckBoxMenuItem();
        autoPrintPayeItem = new javax.swing.JCheckBoxMenuItem();
        verseMenu = new javax.swing.JPopupMenu();
        autoAddVersItem = new javax.swing.JCheckBoxMenuItem();
        autoPrintVerseItem = new javax.swing.JCheckBoxMenuItem();
        creditMenu = new javax.swing.JPopupMenu();
        autoAddCredItem = new javax.swing.JCheckBoxMenuItem();
        autoPrintCreditItem = new javax.swing.JCheckBoxMenuItem();
        fieldsPanel = new javax.swing.JPanel();
        vntFieldsPanel = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        typeVentCmboBox = new myComponents.MyJComboBox();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jLabel3 = new javax.swing.JLabel();
        dateChooser = new myComponents.MyJDateChooser();
        jLabel5 = new javax.swing.JLabel();
        heureField = new myComponents.IntegerJField();
        jLabel2 = new javax.swing.JLabel();
        numField = new myComponents.IntegerJField();
        jSeparator8 = new javax.swing.JSeparator();
        jLabel7 = new javax.swing.JLabel();
        selClientPanel = new panels.SelectionPanel<Client>();
        detailsHolderPanel = new javax.swing.JPanel();
        operationsP = new javax.swing.JPanel();
        creditBtn = new com.l2fprod.common.swing.JLinkButton();
        retourBtn = new com.l2fprod.common.swing.JLinkButton();
        totalPanel = new panels.views.MontantPanel();
        versBtn = new com.l2fprod.common.swing.JLinkButton();
        payeBtn = new com.l2fprod.common.swing.JLinkButton();
        imprBtn = new com.l2fprod.common.swing.JLinkButton();
        supprBtn = new com.l2fprod.common.swing.JLinkButton();

        printMItem.setAction(printAction);
        printMItem.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        printMItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/print16-2.png"))); // NOI18N
        printMItem.setText("Imprimer Ctrl+P");
        printOptionsPopupMenu.add(printMItem);

        previewItem.setAction(previewAction);
        previewItem.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        previewItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/print-view16.png"))); // NOI18N
        previewItem.setText("Aperçu Ctrl+Alt+P");
        printOptionsPopupMenu.add(previewItem);
        printOptionsPopupMenu.add(jSeparator1);
        printOptionsPopupMenu.add(jSeparator2);

        printBSMenItem.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        printBSMenItem.setSelected(true);
        printBSMenItem.setText("Bon de Stocke");
        printBSMenItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printBSMenItemActionPerformed(evt);
            }
        });
        printOptionsPopupMenu.add(printBSMenItem);

        printBLMenItem.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        printBLMenItem.setSelected(true);
        printBLMenItem.setText("Bon de Livraison");
        printBLMenItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printBLMenItemActionPerformed(evt);
            }
        });
        printOptionsPopupMenu.add(printBLMenItem);

        printFactureMenItem.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        printFactureMenItem.setSelected(true);
        printFactureMenItem.setText("Facture");
        printFactureMenItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printFactureMenItemActionPerformed(evt);
            }
        });
        printOptionsPopupMenu.add(printFactureMenItem);

        buttonsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 1, 3, 1));
        buttonsPanel.setPreferredSize(new java.awt.Dimension(597, 44));
        java.awt.GridBagLayout buttonsPanelLayout = new java.awt.GridBagLayout();
        buttonsPanelLayout.columnWidths = new int[] {0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0};
        buttonsPanelLayout.rowHeights = new int[] {0, 2, 0, 2, 0, 2, 0};
        buttonsPanel.setLayout(buttonsPanelLayout);

        saveButton.setAction(okAction);
        saveButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/save16.png"))); // NOI18N
        saveButton.setText("Enrégistrer");
        saveButton.setToolTipText("Sauvguarder les modification (Ctrl+S)");
        saveButton.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        buttonsPanel.add(saveButton, gridBagConstraints);

        insertButton.setAction(addAction);
        insertButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        insertButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/new16.png"))); // NOI18N
        insertButton.setText("Nouveau (Ctrl+N) ");
        insertButton.setToolTipText("Sauvegarder et Crée une nouvelle vente (Ctrl+N)");
        insertButton.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        buttonsPanel.add(insertButton, gridBagConstraints);

        printButton.setAction(printAction);
        printButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        printButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/print16.png"))); // NOI18N
        printButton.setText("Imprimer (Ctrl+P)");
        printButton.setComponentPopupMenu(printOptionsPopupMenu);
        printButton.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        printButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        buttonsPanel.add(printButton, gridBagConstraints);

        invalidaeButton.setAction(retourAction);
        invalidaeButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        invalidaeButton.setForeground(new java.awt.Color(255, 0, 51));
        invalidaeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/cancel16.png"))); // NOI18N
        invalidaeButton.setText("Annuler");
        invalidaeButton.setToolTipText("Invalider la vente si elle est déja validée (Ctrl+Z)");
        invalidaeButton.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        buttonsPanel.add(invalidaeButton, gridBagConstraints);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 204, 0));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/validate16.png"))); // NOI18N
        jLabel1.setText("Validation:");
        jLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        buttonsPanel.add(jLabel1, gridBagConstraints);

        verseButton.setAction(versementAction);
        verseButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        verseButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/vers16.png"))); // NOI18N
        verseButton.setText("Versement (F7)");
        verseButton.setToolTipText("Le client a versé une somme différente du total de vente (F7)");
        verseButton.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        buttonsPanel.add(verseButton, gridBagConstraints);

        creditButton.setAction(creditAction);
        creditButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        creditButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/credit16.png"))); // NOI18N
        creditButton.setText("Crédit (F9)");
        creditButton.setToolTipText("Le client n'a pas réglé cette vente (F9)");
        creditButton.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        buttonsPanel.add(creditButton, gridBagConstraints);

        autoAddPayeItem.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        autoAddPayeItem.setSelected(true);
        autoAddPayeItem.setText("Auto-Vider aprés le paiement.");
        autoAddPayeItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/new16.png"))); // NOI18N
        autoAddPayeItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoAddPayeItemActionPerformed(evt);
            }
        });
        payeMenu.add(autoAddPayeItem);

        autoPrintPayeItem.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        autoPrintPayeItem.setSelected(true);
        autoPrintPayeItem.setText("Auto-Imprimer aprés paiement");
        autoPrintPayeItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/print16-2.png"))); // NOI18N
        autoPrintPayeItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoPrintPayeItemActionPerformed(evt);
            }
        });
        payeMenu.add(autoPrintPayeItem);

        autoAddVersItem.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        autoAddVersItem.setSelected(true);
        autoAddVersItem.setText("Auto-Vider aprés versement.");
        autoAddVersItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/new16.png"))); // NOI18N
        autoAddVersItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoAddVersItemActionPerformed(evt);
            }
        });
        verseMenu.add(autoAddVersItem);

        autoPrintVerseItem.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        autoPrintVerseItem.setSelected(true);
        autoPrintVerseItem.setText("Auto-Imprimer aprés versement");
        autoPrintVerseItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/print16-2.png"))); // NOI18N
        autoPrintVerseItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoPrintVerseItemActionPerformed(evt);
            }
        });
        verseMenu.add(autoPrintVerseItem);

        autoAddCredItem.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        autoAddCredItem.setSelected(true);
        autoAddCredItem.setText("Auto-Vider aprés crédit.");
        autoAddCredItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/new16.png"))); // NOI18N
        autoAddCredItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoAddCredItemActionPerformed(evt);
            }
        });
        creditMenu.add(autoAddCredItem);

        autoPrintCreditItem.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        autoPrintCreditItem.setSelected(true);
        autoPrintCreditItem.setText("Auto-Imprimer aprés crédit");
        autoPrintCreditItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/print16-2.png"))); // NOI18N
        autoPrintCreditItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoPrintCreditItemActionPerformed(evt);
            }
        });
        creditMenu.add(autoPrintCreditItem);

        setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        setPreferredSize(new java.awt.Dimension(600, 400));
        setLayout(new java.awt.BorderLayout());

        fieldsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        fieldsPanel.setLayout(new java.awt.GridBagLayout());

        vntFieldsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 5, 1));
        java.awt.GridBagLayout vntFieldsPanelLayout = new java.awt.GridBagLayout();
        vntFieldsPanelLayout.columnWidths = new int[] {0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0};
        vntFieldsPanelLayout.rowHeights = new int[] {0, 3, 0, 3, 0};
        vntFieldsPanel.setLayout(vntFieldsPanelLayout);

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel8.setText("Vente En:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        vntFieldsPanel.add(jLabel8, gridBagConstraints);

        typeVentCmboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Détail", "Gros", "Demi-Gros", "Super-Gros" }));
        typeVentCmboBox.setToolTipText("Changer le mode du vente (Ctrl+M)");
        typeVentCmboBox.setFont(new java.awt.Font("tahoma", 1, 14)); // NOI18N
        typeVentCmboBox.setMinimumSize(new java.awt.Dimension(65, 26));
        typeVentCmboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeVentCmboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        vntFieldsPanel.add(typeVentCmboBox, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.02;
        vntFieldsPanel.add(filler1, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText(" Date:");
        jLabel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        vntFieldsPanel.add(jLabel3, gridBagConstraints);

        dateChooser.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        dateChooser.setMinimumSize(new java.awt.Dimension(60, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        vntFieldsPanel.add(dateChooser, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel5.setText(" Heure:");
        jLabel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        vntFieldsPanel.add(jLabel5, gridBagConstraints);

        heureField.setEditable(false);
        heureField.setBackground(new java.awt.Color(229, 229, 255));
        heureField.setText("--:--");
        heureField.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.4;
        vntFieldsPanel.add(heureField, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("Vente N°.:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        vntFieldsPanel.add(jLabel2, gridBagConstraints);

        numField.setEditable(false);
        numField.setBackground(new java.awt.Color(229, 229, 255));
        numField.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        vntFieldsPanel.add(numField, gridBagConstraints);

        jSeparator8.setMinimumSize(new java.awt.Dimension(0, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        vntFieldsPanel.add(jSeparator8, gridBagConstraints);

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText(" Client:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        vntFieldsPanel.add(jLabel7, gridBagConstraints);

        selClientPanel.setBackground(new java.awt.Color(229, 229, 255));
        selClientPanel.setDescription("Anonyme!");
        selClientPanel.setMinimumSize(new java.awt.Dimension(38, 25));
        selClientPanel.setPreferredSize(new java.awt.Dimension(44, 25));
        selClientPanel.setShortcutKey("F3");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        vntFieldsPanel.add(selClientPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        fieldsPanel.add(vntFieldsPanel, gridBagConstraints);

        detailsHolderPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        detailsHolderPanel.setFocusable(false);
        detailsHolderPanel.setPreferredSize(new java.awt.Dimension(12, 400));
        detailsHolderPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        fieldsPanel.add(detailsHolderPanel, gridBagConstraints);

        operationsP.setBackground(new java.awt.Color(0, 0, 0));
        operationsP.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 3, 0, 3));
        operationsP.setMinimumSize(new java.awt.Dimension(16, 65));
        operationsP.setPreferredSize(new java.awt.Dimension(16, 60));
        java.awt.GridBagLayout operationsPLayout = new java.awt.GridBagLayout();
        operationsPLayout.columnWidths = new int[] {0, 7, 0, 7, 0, 7, 0};
        operationsPLayout.rowHeights = new int[] {0, 0, 0};
        operationsP.setLayout(operationsPLayout);

        creditBtn.setAction(creditAction);
        creditBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        creditBtn.setForeground(new java.awt.Color(51, 255, 0));
        creditBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/credit24.png"))); // NOI18N
        creditBtn.setText("Crédit");
        creditBtn.setToolTipText("F9");
        creditBtn.setComponentPopupMenu(creditMenu);
        creditBtn.setFont(new java.awt.Font("Square721 BT", 0, 16)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        operationsP.add(creditBtn, gridBagConstraints);

        retourBtn.setAction(retourAction);
        retourBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        retourBtn.setForeground(new java.awt.Color(51, 255, 0));
        retourBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/cancelVnt24.png"))); // NOI18N
        retourBtn.setText("Retour");
        retourBtn.setFont(new java.awt.Font("Square721 BT", 0, 16)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.1;
        operationsP.add(retourBtn, gridBagConstraints);

        totalPanel.setBackground(new java.awt.Color(0, 0, 0));
        totalPanel.setMontantFont(new java.awt.Font("DS-Digital", 0, 70)); // NOI18N
        totalPanel.setTitleIcon("/res/icons/mony32.png");
        totalPanel.setTitleString("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        operationsP.add(totalPanel, gridBagConstraints);

        versBtn.setAction(versementAction);
        versBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        versBtn.setForeground(new java.awt.Color(51, 255, 0));
        versBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/vers24.png"))); // NOI18N
        versBtn.setText("Versé");
        versBtn.setToolTipText("F7");
        versBtn.setComponentPopupMenu(verseMenu);
        versBtn.setFont(new java.awt.Font("Square721 BT", 0, 16)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        operationsP.add(versBtn, gridBagConstraints);

        payeBtn.setAction(paiementAction);
        payeBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        payeBtn.setForeground(new java.awt.Color(51, 255, 0));
        payeBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/dollar2-24.png"))); // NOI18N
        payeBtn.setText("Payée");
        payeBtn.setToolTipText("F4");
        payeBtn.setComponentPopupMenu(payeMenu);
        payeBtn.setFont(new java.awt.Font("Square721 BT", 0, 16)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        operationsP.add(payeBtn, gridBagConstraints);

        imprBtn.setAction(printAction);
        imprBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        imprBtn.setForeground(new java.awt.Color(51, 255, 0));
        imprBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/print24-2.png"))); // NOI18N
        imprBtn.setText("Impr.");
        imprBtn.setToolTipText("Ctrl+P");
        imprBtn.setComponentPopupMenu(printOptionsPopupMenu);
        imprBtn.setFont(new java.awt.Font("Square721 BT", 0, 16)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        operationsP.add(imprBtn, gridBagConstraints);

        supprBtn.setAction(deleteAction);
        supprBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        supprBtn.setForeground(new java.awt.Color(51, 255, 0));
        supprBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/cancel24.png"))); // NOI18N
        supprBtn.setText("Suppr.");
        supprBtn.setFont(new java.awt.Font("Square721 BT", 0, 16)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.1;
        operationsP.add(supprBtn, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        fieldsPanel.add(operationsP, gridBagConstraints);

        add(fieldsPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void printButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_printButtonActionPerformed

    private void typeVentCmboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeVentCmboBoxActionPerformed
        setTypeVente(new TypeVnt(typeVentCmboBox.getSelectedIndex() + 1));
    }//GEN-LAST:event_typeVentCmboBoxActionPerformed

    private void autoPrintPayeItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoPrintPayeItemActionPerformed
        savePreference(AUTO_PRINT_PAYE, autoPrintPayeItem.isSelected());
    }//GEN-LAST:event_autoPrintPayeItemActionPerformed

    private void printBSMenItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printBSMenItemActionPerformed
        savePreference(PRINT_BS, printBSMenItem.isSelected());
    }//GEN-LAST:event_printBSMenItemActionPerformed

    private void printBLMenItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printBLMenItemActionPerformed
        savePreference(PRINT_BL, printBLMenItem.isSelected());
    }//GEN-LAST:event_printBLMenItemActionPerformed

    private void printFactureMenItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printFactureMenItemActionPerformed
        savePreference(PRINT_FACT, printFactureMenItem.isSelected());
    }//GEN-LAST:event_printFactureMenItemActionPerformed

    private void autoPrintVerseItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoPrintVerseItemActionPerformed
        savePreference(AUTO_PRINT_VERSE, autoPrintVerseItem.isSelected());
    }//GEN-LAST:event_autoPrintVerseItemActionPerformed

    private void autoPrintCreditItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoPrintCreditItemActionPerformed
        savePreference(AUTO_PRINT_CREDIT, autoPrintCreditItem.isSelected());
    }//GEN-LAST:event_autoPrintCreditItemActionPerformed

    private void autoAddPayeItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoAddPayeItemActionPerformed
        savePreference(AUTO_ADD_PAYE, autoAddPayeItem.isSelected());
    }//GEN-LAST:event_autoAddPayeItemActionPerformed

    private void autoAddVersItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoAddVersItemActionPerformed
        savePreference(AUTO_ADD_VERSE, autoAddVersItem.isSelected());
    }//GEN-LAST:event_autoAddVersItemActionPerformed

    private void autoAddCredItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoAddCredItemActionPerformed
        savePreference(AUTO_ADD_CREDIT, autoAddCredItem.isSelected());
    }//GEN-LAST:event_autoAddCredItemActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBoxMenuItem autoAddCredItem;
    private javax.swing.JCheckBoxMenuItem autoAddPayeItem;
    private javax.swing.JCheckBoxMenuItem autoAddVersItem;
    private javax.swing.JCheckBoxMenuItem autoPrintCreditItem;
    private javax.swing.JCheckBoxMenuItem autoPrintPayeItem;
    private javax.swing.JCheckBoxMenuItem autoPrintVerseItem;
    private javax.swing.JPanel buttonsPanel;
    private com.l2fprod.common.swing.JLinkButton creditBtn;
    private com.l2fprod.common.swing.JLinkButton creditButton;
    private javax.swing.JPopupMenu creditMenu;
    private myComponents.MyJDateChooser dateChooser;
    private javax.swing.JPanel detailsHolderPanel;
    private javax.swing.JPanel fieldsPanel;
    private javax.swing.Box.Filler filler1;
    private myComponents.IntegerJField heureField;
    private com.l2fprod.common.swing.JLinkButton imprBtn;
    private com.l2fprod.common.swing.JLinkButton insertButton;
    private com.l2fprod.common.swing.JLinkButton invalidaeButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JSeparator jSeparator8;
    private myComponents.IntegerJField numField;
    private javax.swing.JPanel operationsP;
    private com.l2fprod.common.swing.JLinkButton payeBtn;
    private javax.swing.JPopupMenu payeMenu;
    private javax.swing.JMenuItem previewItem;
    private javax.swing.JCheckBoxMenuItem printBLMenItem;
    private javax.swing.JCheckBoxMenuItem printBSMenItem;
    private com.l2fprod.common.swing.JLinkButton printButton;
    private javax.swing.JCheckBoxMenuItem printFactureMenItem;
    private javax.swing.JMenuItem printMItem;
    private javax.swing.ButtonGroup printOptionsBGroup;
    private javax.swing.JPopupMenu printOptionsPopupMenu;
    private com.l2fprod.common.swing.JLinkButton retourBtn;
    private com.l2fprod.common.swing.JLinkButton saveButton;
    private panels.SelectionPanel<Client> selClientPanel;
    private com.l2fprod.common.swing.JLinkButton supprBtn;
    private panels.views.MontantPanel totalPanel;
    private myComponents.MyJComboBox typeVentCmboBox;
    private com.l2fprod.common.swing.JLinkButton versBtn;
    private com.l2fprod.common.swing.JLinkButton verseButton;
    private javax.swing.JPopupMenu verseMenu;
    private javax.swing.JPanel vntFieldsPanel;
    // End of variables declaration//GEN-END:variables

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new MajVentePanel(null).showNewPanel(null);
            }
        });
    }
}
