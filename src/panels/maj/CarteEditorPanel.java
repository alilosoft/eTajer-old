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
import panels.crud.ListeClientsPanel;
import dao.ClientDAO;
import dao.ReglementClDAO;
import dao.VenteDAO;
import dialogs.LoginDialog;
import dialogs.MajDialog;
import dialogs.SelectionDialog;
import java.awt.Component;
import java.awt.Dimension;
import myComponents.MyJFrame;
import panels.CRUDPanel;
import static panels.CRUDPanel.DELETE;
import static panels.CRUDPanel.EDIT;
import static panels.CRUDPanel.INSERT;
import panels.details.CarteItemsPanel;
import panels.crud.ListeVentesPanel;
import printing.VentePrinting;

/**
 *
 * @author alilo
 */
public class CarteEditorPanel extends MajPanel<Vente, VenteDAO> {

    private static final String TYPE_VNT_PREF = "TypeVntPref";
    private static final String AUTO_PRINT_PAYE = "PrintAfterPaye";
    private static final String AUTO_PRINT_VERSE = "PrintAfterVerse";
    private static final String AUTO_PRINT_CREDIT = "PrintAfterCredit";

    private static final String AUTO_ADD_PAYE = "AddAfterPaye";
    private static final String AUTO_ADD_VERSE = "AddAfterVerse";
    private static final String AUTO_ADD_CREDIT = "AddAfterCredit";

    private ListeVentesPanel listeVentesPanel;
    protected CarteItemsPanel detailsVentePanel;
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
    public CarteEditorPanel(CRUDPanel listPanel) {
        super(listPanel);
        initComponents();
        initMajPanel(fieldsPanel);
    }

    @Override
    public final void initMajPanel(Component fieldsPanel) {
        //super.initMajPanel(fieldsPanel);
        setActionsShortcuts();
        conifgDateChooser();
        initClientsPanel();
        initVentesPanel();
        setDetailsVntPanel(null);
        setDefaultFocusedComp(detailsVentePanel.cbField);
        clearFields();
        loadPreferences();
    }

    @Override
    public void loadPreferences() {
        typeVente = TypeVnt.DETAIL;
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
    }

    @Override
    public final VenteDAO getTableDAO() {
        return VenteDAO.getInstance();
    }

    @Override
    public void setActionsShortcuts() {
        super.setActionsShortcuts();
        doOnPress(KeyEvent.VK_F5, 0, addAction, JComponent.WHEN_IN_FOCUSED_WINDOW);
        doOnPress(KeyEvent.VK_F4, 0, paiementAction, JComponent.WHEN_IN_FOCUSED_WINDOW);
        doOnPress(KeyEvent.VK_F7, 0, versementAction, JComponent.WHEN_IN_FOCUSED_WINDOW);
        doOnPress(KeyEvent.VK_F9, 0, creditAction, JComponent.WHEN_IN_FOCUSED_WINDOW);
        doOnPress(KeyEvent.VK_ESCAPE, 0, retourAction, JComponent.WHEN_IN_FOCUSED_WINDOW);
        doOnPress(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK, printAction, JComponent.WHEN_IN_FOCUSED_WINDOW);
        doOnPress(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK, previewAction, JComponent.WHEN_IN_FOCUSED_WINDOW);
        doOnPress(KeyEvent.VK_F11, 0, showVentsListAct, JComponent.WHEN_IN_FOCUSED_WINDOW);
        doOnPress(KeyEvent.VK_DELETE, InputEvent.CTRL_DOWN_MASK, deleteAction, JComponent.WHEN_IN_FOCUSED_WINDOW);

        //clear the default ENTER action that wase been bounded to okAction;
        doOnPress(KeyEvent.VK_ENTER, 0, noAction, JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        doOnPress(KeyEvent.VK_PAGE_UP, 0, goNextEntityAct, JComponent.WHEN_IN_FOCUSED_WINDOW);
        doOnPress(KeyEvent.VK_PAGE_DOWN, 0, goPrevEntityAct, JComponent.WHEN_IN_FOCUSED_WINDOW);
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

    public CarteItemsPanel getDetailsVentePanel() {
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
        //
        typeVente = oldEntity.getTypeVnt();
        setDate(oldEntity.getDate());
        setNum(oldEntity.getNum());//must setting the num after setting the date to ignore num calc.
        setHeure(oldEntity.getHeure());
        validee = oldEntity.isValidee();
        setClient(oldEntity.getClient());
        detailsVentePanel.setMasterEntity(oldEntity);
        detailsVentePanel.cbField.requestFocus();
    }

    @Override
    public void setModifAllowed(boolean allow) {
        super.setModifAllowed(allow);
        dateChooser.setEnabled(allow);
        heureField.setEnabled(allow);
        numField.setEnabled(allow);
        selClientPanel.setAllowSelChange(allow);
    }

    public final void conifgDateChooser() {
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
        setTypeVente(TypeVnt.DETAIL);
        return true;
    }

    @Override
    public void clearFields() {
        //Clear vente fields
        setDate(new Date());
        setHeure(new Date());
        setClient(new Client(0));
        validee = false;
        //Clear detailsVente
        detailsVentePanel.setMasterEntity(new Vente());
        detailsVentePanel.cbField.requestFocus();
        showTotal();
    }

    private void initClientsPanel() {
        clientsPanel = new ListeClientsPanel(this, false);
        clientsPanel.getPropertyChangeSupport().addPropertyChangeListener(ResultSet_Panel.ENTITY_TO_VIEW_PROPERTY, this);
        selClientPanel.setSelListPanel(clientsPanel.getNavigNEditList());
    }

    private void initDetailsPanel() {
        detailsVentePanel = (CarteItemsPanel) new CarteItemsPanel(this, false) {
            {
                setAllowedOperation(INSERT, false);
                setAllowedOperation(EDIT, false);
            }

            @Override
            public void addLigneVnt(EnStock enStock) {
                if (((isUpdate() || isSaved()) && !isModified()) || save()) {
                    if (getEditedEntity().isValidee()) {
                        String mess = "Vous ne pouvez pas ajouter des produits a une livraison validée!\n"
                                + "Voulez vous crée une nouvelle livraison?";
                        int rep = JOptionPane.showConfirmDialog(CarteEditorPanel.this, mess, "Attention!", JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION);
                        if (rep == JOptionPane.YES_OPTION) {
                            CarteEditorPanel.this.add();
                            CarteEditorPanel.this.save();
                        } else {
                            return;
                        }
                    }
                    super.addLigneVnt(enStock);
                }
            }

        }.getNavigNSelectList();
    }

    public final void setDetailsVntPanel(CarteItemsPanel details) {
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
                okAction.setEnabled(!isEmpty && !validated);
                addAction.setEnabled(!isEmpty);
                deleteAction.setEnabled(!isEmpty && !validated);

                paiementAction.setEnabled(!isEmpty && !validated);
                versementAction.setEnabled(!isEmpty && !(validated && client.isAnonyme()));
                creditAction.setEnabled(paiementAction.isEnabled());
                retourAction.setEnabled(!isEmpty);
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
            putValue(Action.SHORT_DESCRIPTION, "Annuler la livraison, et recupérer les produits vendus! (ESC)");
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(isValidated()){
                retourLivraison();
            }else{
                delete();
            }
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
        VentePrinting.preview(getEditedEntity(), VentePrinting.TICKET);
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
        VentePrinting.print(getEditedEntity(), VentePrinting.TICKET);
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

    private void initVentesPanel() {
        listeVentesPanel = new ListeVentesPanel(this, false) {
            {
                setPreferredSize(new Dimension(700, 600));
                setAllowedOperation(INSERT, false);
                setAllowedOperation(EDIT, false);
                setAllowedOperation(DELETE, LoginDialog.isLoginAsAdmin());

            }

            @Override
            public void reload() {
                super.reload(); 
                System.out.println("reload vnts");
            }
            
        };
        setCrudPanel(listeVentesPanel);
        listeVentesPanel.getPropertyChangeSupport().addPropertyChangeListener(ResultSet_Panel.ENTITY_TO_EDIT_PROPERTY, this);
    }

    private final SelectionDialog sd = new SelectionDialog();

    private void showVentesList() {
        if (listeVentesPanel == null) {
            initVentesPanel();
        }
        sd.showPanel(listeVentesPanel, null, null);
        if (sd.getReturnStatus() == MajDialog.RET_OK) {
        }
    }

    private final Action showVentsListAct = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            showVentesList();
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
        payeMenu = new javax.swing.JPopupMenu();
        autoAddPayeItem = new javax.swing.JCheckBoxMenuItem();
        autoPrintPayeItem = new javax.swing.JCheckBoxMenuItem();
        verseMenu = new javax.swing.JPopupMenu();
        autoAddVersItem = new javax.swing.JCheckBoxMenuItem();
        autoPrintVerseItem = new javax.swing.JCheckBoxMenuItem();
        creditMenu = new javax.swing.JPopupMenu();
        autoAddCredItem = new javax.swing.JCheckBoxMenuItem();
        autoPrintCreditItem = new javax.swing.JCheckBoxMenuItem();
        versBtn = new com.l2fprod.common.swing.JLinkButton();
        creditBtn = new com.l2fprod.common.swing.JLinkButton();
        fieldsPanel = new javax.swing.JPanel();
        vntFieldsPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        numField = new myComponents.IntegerJField();
        jLabel3 = new javax.swing.JLabel();
        dateChooser = new myComponents.MyJDateChooser();
        jLabel5 = new javax.swing.JLabel();
        heureField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        selClientPanel = new panels.SelectionPanel<>();
        reglP = new javax.swing.JPanel();
        retourBtn = new com.l2fprod.common.swing.JLinkButton();
        payeBtn = new com.l2fprod.common.swing.JLinkButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 32767));
        totalPanel = new panels.views.MontantPanel();
        detailsHolderPanel = new javax.swing.JPanel();
        opersP = new javax.swing.JPanel();
        imprBtn = new com.l2fprod.common.swing.JLinkButton();
        newBtn = new com.l2fprod.common.swing.JLinkButton();
        allVntsBtn = new com.l2fprod.common.swing.JLinkButton();
        prevVntsBtn = new com.l2fprod.common.swing.JLinkButton();
        nextVntsBtn = new com.l2fprod.common.swing.JLinkButton();

        printMItem.setAction(printAction);
        printMItem.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        printMItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/print16-2.png"))); // NOI18N
        printMItem.setText("Imprimer Ctrl+P");
        printOptionsPopupMenu.add(printMItem);

        previewItem.setAction(previewAction);
        previewItem.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        printOptionsPopupMenu.add(previewItem);

        autoAddPayeItem.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        autoAddPayeItem.setText("Auto-Vider aprés le paiement.");
        autoAddPayeItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/new16.png"))); // NOI18N
        autoAddPayeItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoAddPayeItemActionPerformed(evt);
            }
        });
        payeMenu.add(autoAddPayeItem);

        autoPrintPayeItem.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        autoPrintPayeItem.setText("Auto-Imprimer aprés paiement");
        autoPrintPayeItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/print16-2.png"))); // NOI18N
        autoPrintPayeItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoPrintPayeItemActionPerformed(evt);
            }
        });
        payeMenu.add(autoPrintPayeItem);

        autoAddVersItem.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        autoAddVersItem.setText("Auto-Vider aprés versement.");
        autoAddVersItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/new16.png"))); // NOI18N
        autoAddVersItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoAddVersItemActionPerformed(evt);
            }
        });
        verseMenu.add(autoAddVersItem);

        autoPrintVerseItem.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        autoPrintVerseItem.setText("Auto-Imprimer aprés versement");
        autoPrintVerseItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/print16-2.png"))); // NOI18N
        autoPrintVerseItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoPrintVerseItemActionPerformed(evt);
            }
        });
        verseMenu.add(autoPrintVerseItem);

        autoAddCredItem.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        autoAddCredItem.setText("Auto-Vider aprés crédit.");
        autoAddCredItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/new16.png"))); // NOI18N
        autoAddCredItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoAddCredItemActionPerformed(evt);
            }
        });
        creditMenu.add(autoAddCredItem);

        autoPrintCreditItem.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        autoPrintCreditItem.setText("Auto-Imprimer aprés crédit");
        autoPrintCreditItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/print16-2.png"))); // NOI18N
        autoPrintCreditItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoPrintCreditItemActionPerformed(evt);
            }
        });
        creditMenu.add(autoPrintCreditItem);

        versBtn.setAction(versementAction);
        versBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        versBtn.setForeground(new java.awt.Color(51, 255, 0));
        versBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/vers24.png"))); // NOI18N
        versBtn.setText("Versé (F7)");
        versBtn.setToolTipText("F7");
        versBtn.setComponentPopupMenu(verseMenu);
        versBtn.setFocusable(false);
        versBtn.setFont(new java.awt.Font("Square721 BT", 0, 16)); // NOI18N
        versBtn.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        creditBtn.setAction(creditAction);
        creditBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        creditBtn.setForeground(new java.awt.Color(51, 255, 0));
        creditBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/credit24.png"))); // NOI18N
        creditBtn.setText("Crédit (F9)");
        creditBtn.setToolTipText("F9");
        creditBtn.setComponentPopupMenu(creditMenu);
        creditBtn.setFocusable(false);
        creditBtn.setFont(new java.awt.Font("Square721 BT", 0, 16)); // NOI18N

        setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        setFocusCycleRoot(true);
        setLayout(new java.awt.BorderLayout());

        fieldsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        fieldsPanel.setFocusCycleRoot(true);
        fieldsPanel.setLayout(new java.awt.GridBagLayout());

        vntFieldsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 5, 1));
        vntFieldsPanel.setFocusable(false);
        java.awt.GridBagLayout vntFieldsPanelLayout = new java.awt.GridBagLayout();
        vntFieldsPanelLayout.columnWidths = new int[] {0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0};
        vntFieldsPanelLayout.rowHeights = new int[] {0};
        vntFieldsPanel.setLayout(vntFieldsPanelLayout);

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
        gridBagConstraints.weightx = 0.1;
        vntFieldsPanel.add(numField, gridBagConstraints);

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
        gridBagConstraints.weightx = 0.2;
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
        heureField.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        heureField.setText("00:00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.05;
        vntFieldsPanel.add(heureField, gridBagConstraints);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText(" Client:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 0;
        vntFieldsPanel.add(jLabel1, gridBagConstraints);

        selClientPanel.setBackground(new java.awt.Color(229, 229, 255));
        selClientPanel.setDescription("Anonyme!");
        selClientPanel.setMinimumSize(new java.awt.Dimension(38, 25));
        selClientPanel.setPreferredSize(new java.awt.Dimension(44, 30));
        selClientPanel.setShortcutKey("F10");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 14;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        vntFieldsPanel.add(selClientPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(vntFieldsPanel, gridBagConstraints);

        reglP.setBackground(new java.awt.Color(0, 0, 0));
        reglP.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        reglP.setFocusable(false);
        reglP.setMinimumSize(new java.awt.Dimension(287, 10));
        java.awt.GridBagLayout reglPLayout = new java.awt.GridBagLayout();
        reglPLayout.columnWidths = new int[] {0};
        reglPLayout.rowHeights = new int[] {0, 0, 0, 0, 0};
        reglP.setLayout(reglPLayout);

        retourBtn.setAction(retourAction);
        retourBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        retourBtn.setForeground(new java.awt.Color(255, 0, 0));
        retourBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/cancelVnt24.png"))); // NOI18N
        retourBtn.setText("Annuler ESC");
        retourBtn.setToolTipText("Annuler la livraison sans suppression (Esc)");
        retourBtn.setFocusable(false);
        retourBtn.setFont(new java.awt.Font("Square721 BT", 1, 16)); // NOI18N
        retourBtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        retourBtn.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        reglP.add(retourBtn, gridBagConstraints);

        payeBtn.setAction(paiementAction);
        payeBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        payeBtn.setForeground(new java.awt.Color(51, 255, 0));
        payeBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/vente32.png"))); // NOI18N
        payeBtn.setText("Validée F4");
        payeBtn.setToolTipText("F4");
        payeBtn.setComponentPopupMenu(payeMenu);
        payeBtn.setFocusable(false);
        payeBtn.setFont(new java.awt.Font("Square721 BT", 1, 24)); // NOI18N
        payeBtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        payeBtn.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        payeBtn.setIconTextGap(10);
        payeBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        reglP.add(payeBtn, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        reglP.add(filler1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.1;
        fieldsPanel.add(reglP, gridBagConstraints);

        totalPanel.setMontantFont(new java.awt.Font("DS-Digital", 0, 100)); // NOI18N
        totalPanel.setOpaque(true);
        totalPanel.setTitleIcon("\"\"");
        totalPanel.setTitleString("Total:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        fieldsPanel.add(totalPanel, gridBagConstraints);

        detailsHolderPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        detailsHolderPanel.setFocusable(false);
        detailsHolderPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        fieldsPanel.add(detailsHolderPanel, gridBagConstraints);

        opersP.setBackground(new java.awt.Color(0, 0, 0));
        opersP.setFocusable(false);
        opersP.setMinimumSize(new java.awt.Dimension(641, 30));
        opersP.setPreferredSize(new java.awt.Dimension(814, 30));
        opersP.setLayout(new java.awt.GridBagLayout());

        imprBtn.setAction(printAction);
        imprBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        imprBtn.setForeground(new java.awt.Color(51, 255, 0));
        imprBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/print24-2.png"))); // NOI18N
        imprBtn.setText("(Ctrl+P)");
        imprBtn.setComponentPopupMenu(printOptionsPopupMenu);
        imprBtn.setFocusable(false);
        imprBtn.setFont(new java.awt.Font("Square721 BT", 0, 18)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        opersP.add(imprBtn, gridBagConstraints);

        newBtn.setAction(addAction);
        newBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        newBtn.setForeground(new java.awt.Color(51, 255, 0));
        newBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/new24.png"))); // NOI18N
        newBtn.setText("Nouvelle Vente (F5) ");
        newBtn.setToolTipText("Crée une nouvelle livraison (F5)");
        newBtn.setComponentPopupMenu(printOptionsPopupMenu);
        newBtn.setFocusable(false);
        newBtn.setFont(new java.awt.Font("Square721 BT", 0, 18)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        opersP.add(newBtn, gridBagConstraints);

        allVntsBtn.setAction(showVentsListAct);
        allVntsBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        allVntsBtn.setForeground(new java.awt.Color(51, 255, 0));
        allVntsBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/details.png"))); // NOI18N
        allVntsBtn.setText("Tous les Ventes (F11)");
        allVntsBtn.setFocusable(false);
        allVntsBtn.setFont(new java.awt.Font("Square721 BT", 0, 18)); // NOI18N
        allVntsBtn.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        allVntsBtn.setPreferredSize(new java.awt.Dimension(200, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        opersP.add(allVntsBtn, gridBagConstraints);

        prevVntsBtn.setAction(goNextEntityAct);
        prevVntsBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        prevVntsBtn.setForeground(new java.awt.Color(51, 255, 0));
        prevVntsBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/prev32.png"))); // NOI18N
        prevVntsBtn.setText("");
        prevVntsBtn.setToolTipText("Vente Précédente (Alt+Gauche)");
        prevVntsBtn.setFocusable(false);
        prevVntsBtn.setFont(new java.awt.Font("Square721 BT", 0, 18)); // NOI18N
        prevVntsBtn.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        prevVntsBtn.setPreferredSize(new java.awt.Dimension(200, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        opersP.add(prevVntsBtn, gridBagConstraints);

        nextVntsBtn.setAction(goPrevEntityAct);
        nextVntsBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        nextVntsBtn.setForeground(new java.awt.Color(51, 255, 0));
        nextVntsBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/next32.png"))); // NOI18N
        nextVntsBtn.setText("");
        nextVntsBtn.setToolTipText("Vente Suivente (Alt+Droite)");
        nextVntsBtn.setFocusable(false);
        nextVntsBtn.setFont(new java.awt.Font("Square721 BT", 0, 18)); // NOI18N
        nextVntsBtn.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        nextVntsBtn.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        nextVntsBtn.setPreferredSize(new java.awt.Dimension(200, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        opersP.add(nextVntsBtn, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.05;
        fieldsPanel.add(opersP, gridBagConstraints);

        add(fieldsPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void autoPrintPayeItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoPrintPayeItemActionPerformed
        savePreference(AUTO_PRINT_PAYE, autoPrintPayeItem.isSelected());
    }//GEN-LAST:event_autoPrintPayeItemActionPerformed

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
    private com.l2fprod.common.swing.JLinkButton allVntsBtn;
    private javax.swing.JCheckBoxMenuItem autoAddCredItem;
    private javax.swing.JCheckBoxMenuItem autoAddPayeItem;
    private javax.swing.JCheckBoxMenuItem autoAddVersItem;
    private javax.swing.JCheckBoxMenuItem autoPrintCreditItem;
    private javax.swing.JCheckBoxMenuItem autoPrintPayeItem;
    private javax.swing.JCheckBoxMenuItem autoPrintVerseItem;
    private com.l2fprod.common.swing.JLinkButton creditBtn;
    private javax.swing.JPopupMenu creditMenu;
    private myComponents.MyJDateChooser dateChooser;
    private javax.swing.JPanel detailsHolderPanel;
    private javax.swing.JPanel fieldsPanel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JTextField heureField;
    private com.l2fprod.common.swing.JLinkButton imprBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private com.l2fprod.common.swing.JLinkButton newBtn;
    private com.l2fprod.common.swing.JLinkButton nextVntsBtn;
    private myComponents.IntegerJField numField;
    private javax.swing.JPanel opersP;
    private com.l2fprod.common.swing.JLinkButton payeBtn;
    private javax.swing.JPopupMenu payeMenu;
    private com.l2fprod.common.swing.JLinkButton prevVntsBtn;
    private javax.swing.JMenuItem previewItem;
    private javax.swing.JMenuItem printMItem;
    private javax.swing.JPopupMenu printOptionsPopupMenu;
    private javax.swing.JPanel reglP;
    private com.l2fprod.common.swing.JLinkButton retourBtn;
    private panels.SelectionPanel<Client> selClientPanel;
    private panels.views.MontantPanel totalPanel;
    private com.l2fprod.common.swing.JLinkButton versBtn;
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
                MyJFrame frame = new MyJFrame();
                frame.add(new CarteEditorPanel(null));
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
