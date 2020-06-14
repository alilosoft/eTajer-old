/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package panels;

import dialogs.SelectionDialog;
import entities.EntityClass;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import myComponents.MyJFrame;
import myComponents.MyJPanel;
import panels.crud.ListeProduitsPanel;
import panels.maj.MajPanel;
import tools.ExceptionReporting;

/**
 *
 * @author alilo
 * @param <E>
 */
public abstract class CRUDPanel<E extends EntityClass> extends MyJPanel {

    public final String PREF_FILTER_MODE = this.getClass().getName() + "_PrefFilterMode";
    public final String PREF_FILTER_COLS = this.getClass().getName() + "_PrefFilterCols";

    public static final int READ = 0;
    public static final int INSERT = 1;
    public static final int EDIT = 2;
    public static final int DELETE = 3;
    private Window containerWindow = null;
    private Frame containerFrame = null;
    private Dialog containerDialog = null;
    private JPanel containerPanel = null;
    private boolean allowRead = true;
    private boolean allowInsert = true;
    private boolean allowEdit = true;
    private boolean allowDelete = true;
    private String filterShortcut = "";
    protected MajPanel majPanel;
    private final SelectionDialog sd = new SelectionDialog();
    
    public CRUDPanel(java.awt.Container owner) {
        super(owner);
        initComponents();
        if (owner instanceof Frame) {
            setOwnerFrame((Frame) owner);
        } else {
            if (owner instanceof Dialog) {
                setOwnerDialog((Dialog) owner);
            } else {
                if (owner instanceof Window) {
                    setOwnerWindow((Window) owner);
                } else {
                    if (owner instanceof JPanel) {
                        containerPanel = (JPanel) owner;
                    }
                }
            }
        }
        customizeBehavior();
    }

    @Override
    public Component getDefaultFocusedComp() {
        return filterField;
    }

    public void setMajPanel(MajPanel majPanel) {
        this.majPanel = majPanel;
    }

    public MajPanel getMajPanel() {
        return majPanel;
    }

    /**
     * Call this method in subclasses to do some UI initialisation using object
     * in thes subclesses. This method must not called in this class because it
     * may cause NullPointException if the used objects are not initiated yet.
     */
    protected void customizeUI() {
        loadPreferences();
    }

    public final void customizeBehavior() {
        setActionsShortcuts();
    }

    public void setActionsShortcuts() {
        doOnRelease(KeyEvent.VK_INSERT, 0, insertAction, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        doOnRelease(KeyEvent.VK_F2, 0, editAction, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        doOnRelease(KeyEvent.VK_DELETE, 0, deleteAction, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        doOnRelease(KeyEvent.VK_F5, 0, reloadAction, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        doOnPress(KeyEvent.VK_UP, 0, upAction, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        doOnPress(KeyEvent.VK_DOWN, 0, downAction, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        doOnPress(KeyEvent.VK_PAGE_UP, 0, firstAction, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        doOnPress(KeyEvent.VK_PAGE_DOWN, 0, lastAction, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private final Action settingsAction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            openSettings();
        }
    };

    abstract public void openSettings();
    /**
     * Select previous element.
     */
    public void up() {
    }
    protected final Action upAction = new AbstractAction() {
        {
            putValue(Action.SHORT_DESCRIPTION, "Sélectionner l'élément précédent (Flèche-Haut)");
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            up();
        }
    };
    /**
     * Select next element.
     */
    public void down() {
    }
    protected final Action downAction = new AbstractAction() {
        {
            putValue(Action.SHORT_DESCRIPTION, "Sélectionner l'élément suivant (Flèche-Bas)");
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            down();
        }
    };

    public void first() {
    }
    protected final Action firstAction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            first();
        }
    };

    public void last() {
    }
    protected final Action lastAction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            last();
        }
    };

    /**
     * This method must be overridable to make possible the change of
     * 'filterAction' shortcut.
     *
     * @param key shortcut key
     * @param modifier shortcut key modifier (i.e: CTRL, ALT, SHIFT)
     */
    public void setFilterShortcut(String modifier, String key) {
        filterShortcut = modifier + "+" + key;
        doOnRelease(key.toUpperCase(), modifier.toUpperCase(), filterAction, JComponent.WHEN_IN_FOCUSED_WINDOW);
        filterField.setText(getFilterFieldDesc());
    }

    private final Action filterAction = new AbstractAction("Recherche") {

        @Override
        public void actionPerformed(ActionEvent e) {
            doFilter();
        }
    };

    public Action getFilterAction() {
        return filterAction;
    }

    public void setFilterPatern(String pat) {
        filterField.setText(pat);
    }

    public void doFilter() {
        filterField.requestFocus();
    }

    public void doFilter(String pattern) {
        if (!pattern.isEmpty()) {
           // filterField.setText(pattern);
        }
        filterField.setText(pattern);
        //filterField.requestFocus();
    }

    /**
     *
     * @return
     */
    abstract public ComboBoxModel getFilterColsComboBoxModel();

    /**
     * Set one of crud operations authorisation.
     *
     * @param op
     * @param allowed
     */
    public final void setAllowedOperation(int op, boolean allowed) {
        switch (op) {
            case READ: {
                allowRead = allowed;
                if (allowed == false) {
                    remove(mainPanel);
                    add(new LockedPanel());
                }
                break;
            }
            case INSERT: {
                allowInsert = allowed;
                //setEnabledOperation(op, allowed);
                insertAction.setEnabled(allowed);
                break;
            }
            case EDIT: {
                allowEdit = allowed;
                //setEnabledOperation(op, allowed);
                editAction.setEnabled(allowed);
                break;
            }
            case DELETE: {
                allowDelete = allowed;
                //setEnabledOperation(op, allowed);
                deleteAction.setEnabled(allowed);
                break;
            }
        }
    }

    public void setEnabledOperation(int op, boolean enabled) {
        switch (op) {
            case INSERT: {
                //insertButton.setEnabled(enabled);
                insertAction.setEnabled(enabled);
                break;
            }
            case EDIT: {
                //editButton.setEnabled(enabled);
                editAction.setEnabled(enabled);
                break;
            }
            case DELETE: {
                //deleteButton.setEnabled(enabled);
                deleteAction.setEnabled(enabled);
                break;
            }
        }
    }

    public final void setAllowedOperations(int[] ops, boolean[] allowed) {
        int i = 0;
        for (int op : ops) {
            setAllowedOperation(op, allowed[i++]);
        }
    }

    public final void setOwnerWindow(Window ownerWindow) {
        this.containerWindow = ownerWindow;
    }

    public final void setOwnerFrame(Frame ownerFrame) {
        this.containerFrame = ownerFrame;
    }

    public final void setOwnerDialog(Dialog ownerDialog) {
        this.containerDialog = ownerDialog;
    }

    public void setOwnerPanel(JPanel ownerPanel) {
        this.containerPanel = ownerPanel;
    }
    /**
     * This liste will contain the notifications number in the report TextPane.
     */
    private final List<Notification> notifications = Collections.synchronizedList(new ArrayList<Notification>());

    public void addNotification(Notification n, boolean showTitle) {
        setNotification(n, showTitle);
    }

    /**
     * Add a notification string to the report text pane.
     *
     * @param numNotif
     * @param info
     * @param attributeSet
     */
    private void setNotification(Notification notif, boolean showTitle) {
        Element rootElement = reportTextPane.getDocument().getDefaultRootElement();
        int notifIndex;
        int notifStartOffset;
        String notifTxt = "";
        if (notifications.contains(notif)) {// replace the content of old notification  //
            notifIndex = notifications.indexOf(notif);
            Element notifElt = rootElement.getElement(notifIndex);
            notifStartOffset = notifElt.getStartOffset();
            removeNotification(notif, true);
        } else {
            if (notifications.isEmpty()) {
                notifStartOffset = rootElement.getStartOffset();
            } else {
                notifTxt = "\n";
                notifStartOffset = rootElement.getEndOffset() - 1;
            }
            notifications.add(notif);
        }
        try {
            if (showTitle) {
                notifTxt += notif.getTitle() + ": " + notif.getMess();
            } else {
                notifTxt += notif.getMess();
            }
            rootElement.getDocument().insertString(notifStartOffset, notifTxt, notif.getStyle());
        } catch (BadLocationException ex) {
            ExceptionReporting.showException(ex, new Object[]{notif});
        }
    }

    /**
     * Remove the specified notification from the report pane
     *
     * @param notif
     * @param replace
     */
    public void removeNotification(Notification notif, boolean replace) {
        Element rootElement = reportTextPane.getDocument().getDefaultRootElement();
        if (notifications.contains(notif)) {
            int notifIndex = notifications.indexOf(notif);
            Element notifElt = rootElement.getElement(notifIndex);
            int notifStart = notifElt.getStartOffset();
            int notifEnd = notifElt.getEndOffset();
            int notifLength = notifEnd - notifStart - 1;
            if (!replace) {

            }
            try {
                reportTextPane.getDocument().remove(notifStart, notifLength);
            } catch (BadLocationException ex) {
                ExceptionReporting.showException(ex);
            }
            if (!replace) {
                notifications.remove(notif);
            }
        }
    }

    public boolean isAllowRead() {
        return allowRead;
    }

    public boolean isAllowInsert() {
        return allowInsert;
    }

    public boolean isAllowEdit() {
        return allowEdit;
    }

    public boolean isAllowDelete() {
        return allowDelete;
    }

    public Window getOwnerWindow() {
        return containerWindow;
    }

    public Frame getOwnerFrame() {
        return containerFrame;
    }

    public Dialog getOwnerDialog() {
        return containerDialog;
    }

    public Container getOwnerContainer() {
        if (containerWindow != null) {
            return containerWindow;
        }

        if (containerFrame != null) {
            return containerFrame;
        }

        if (containerDialog != null) {
            return containerDialog;
        }

        if (containerPanel != null) {
            return containerPanel;
        }
        return null;
    }

    public JPanel getStatusPanel() {
        return statusPanel;
    }

    public void insert() {
        if (allowInsert) {
            majPanel.showNewPanel(null);
            reload();
        } else {
            JOptionPane.showMessageDialog(this, "Vous n'avez pas le droit de faire cet opération!!!\nContactez votre administrateur SVP!", "Attention...", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Action insertAction = new AbstractAction("Ajouter") {

        {
            putValue(Action.SHORT_DESCRIPTION, "Ajouter un nouveau élément (Raccourcie: 'Inser')");
            putValue(Action.LONG_DESCRIPTION, "Ajouter un élément au tableau ci-dessous.");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            insert();
        }
    };

    abstract public E getSelectedEntity();

    public void edit() {
        if (allowEdit) {
            if (getSelectedEntity().getId() > 0) {
                majPanel.showEditPanel(getSelectedEntity());
                reload();
            } else {
                JOptionPane.showMessageDialog(this, "Aucun élément séléctionné!!!\n Sélectionnez l'élément a modifier SVP!", "Attention...", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vous n'avez pas le droit de modifier!!!", "Attention...", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Action editAction = new AbstractAction("Modifier") {

        {
            putValue(Action.SHORT_DESCRIPTION, "Modifier l'élément séléctionner (F2)");
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            edit();
        }
    };

    public void delete() {
        reload();
        clearSelection();
    }
    public Action deleteAction = new AbstractAction("Supprimer") {

        {
            putValue(Action.SHORT_DESCRIPTION, "Supprimer l'élément séléctionner (Suppr)");
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            delete();
        }
    };

    public void reload() {
        //doFilter("");
    }
    ;
    
    public Action reloadAction = new AbstractAction("Reload") {

        {
            putValue(Action.SHORT_DESCRIPTION, "Actualiser la liste et annuler la sélection (F5)");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            reload();
            clearSelection();
        }
    };
    
    public void print(){
        
    };
    
    public Action printAction = new AbstractAction("Print") {

        {
            //putValue(Action.SHORT_DESCRIPTION, "Actualiser la liste et annuler la sélection (F5)");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            print();
        }
    };

    public abstract void clearSelection();

    /**
     *
     * @param e
     */
    public abstract void doOnFilterFildKeyPressed(KeyEvent e);

    /**
     * Filter the liste in the panel, based on input of filterFeild.
     *
     * @param input
     */
    abstract public void filterList(String input);

    public void setFilterResultsCount(int resultCount) {
        if (resultCount == 0) {
            if (!filterField.getText().isEmpty() && isFiltered()) {
                filterField.setForeground(Color.RED);
            }
            //Toolkit.getDefaultToolkit().beep();
        } else {
            if (isFiltered()) {
                filterField.setForeground(Color.BLACK);
            } else {
                filterField.setForeground(new Color(102, 102, 102));
            }
        }
    }

    public String getFilterFieldDesc() {
        return "Recherche ("+filterShortcut+")";
    }

    public boolean isFiltered() {
        return !filterField.getText().equals(getFilterFieldDesc());
    }

    /**
     * CRUD panel without status panel (navigation & CRUD).
     * @return
     */
    public CRUDPanel getNavigNEditList() {
        mainPanel.remove(statusPanel);
        return this;
    }
    
    /**
     * CRUD panel without CRUD operations (navigation & status only).
     * @return 
     */
    public CRUDPanel getNavigNSelectList() {
        mainPanel.remove(operPanel);
        return this;
    }
    /**
     * CRUD panel without CRUD operations and status panel (Navigation only)
     * @return 
     */
    public CRUDPanel getNavigOnlyList() {
        mainPanel.remove(operPanel);
        mainPanel.remove(statusPanel);
        return this;
    }
    /**
     * CRUD panel without CRUD operations & navigation (selection status only)
     * @return 
     */
    public CRUDPanel getSelectOnlyList() {
        mainPanel.remove(operPanel);
        mainPanel.remove(navigPanel);
        return this;
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

        mainPanel = new javax.swing.JPanel();
        operPanel = new javax.swing.JPanel();
        optionsButton = new javax.swing.JButton();
        insertButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        reloadButton = new javax.swing.JButton();
        printButton = new javax.swing.JButton();
        navigPanel = new javax.swing.JPanel();
        filterField = new myComponents.MyJField();
        firstButton = new javax.swing.JButton();
        previousButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        lastButton = new javax.swing.JButton();
        scrollPan = new javax.swing.JScrollPane();
        statusPanel = new javax.swing.JPanel();
        infosPanel = new javax.swing.JPanel();
        reportTextPane = new javax.swing.JTextPane();

        setFocusCycleRoot(true);
        setLayout(new java.awt.BorderLayout());

        mainPanel.setLayout(new java.awt.GridBagLayout());

        operPanel.setLayout(new java.awt.GridBagLayout());

        optionsButton.setAction(settingsAction);
        optionsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/setting24.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("panels/Bundle"); // NOI18N
        optionsButton.setText(bundle.getString("CRUDPanel.optionsButton.text")); // NOI18N
        optionsButton.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        optionsButton.setFocusable(false);
        optionsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optionsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        operPanel.add(optionsButton, gridBagConstraints);

        insertButton.setAction(insertAction);
        insertButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/new24.png"))); // NOI18N
        insertButton.setText(bundle.getString("CRUDPanel.insertButton.text")); // NOI18N
        insertButton.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        insertButton.setFocusable(false);
        insertButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        insertButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        operPanel.add(insertButton, gridBagConstraints);

        editButton.setAction(editAction);
        editButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/edit24.png"))); // NOI18N
        editButton.setText(bundle.getString("CRUDPanel.editButton.text")); // NOI18N
        editButton.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        editButton.setEnabled(false);
        editButton.setFocusable(false);
        editButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        editButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        operPanel.add(editButton, gridBagConstraints);

        deleteButton.setAction(deleteAction);
        deleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/delete24.png"))); // NOI18N
        deleteButton.setText(bundle.getString("CRUDPanel.deleteButton.text")); // NOI18N
        deleteButton.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        deleteButton.setEnabled(false);
        deleteButton.setFocusable(false);
        deleteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        deleteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        operPanel.add(deleteButton, gridBagConstraints);

        reloadButton.setAction(reloadAction);
        reloadButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/reload24.png"))); // NOI18N
        reloadButton.setText(bundle.getString("CRUDPanel.reloadButton.text")); // NOI18N
        reloadButton.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        reloadButton.setFocusable(false);
        reloadButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        reloadButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        operPanel.add(reloadButton, gridBagConstraints);

        printButton.setAction(printAction);
        printButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/print16.png"))); // NOI18N
        printButton.setText(bundle.getString("CRUDPanel.printButton.text")); // NOI18N
        printButton.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        printButton.setFocusable(false);
        printButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        printButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        operPanel.add(printButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        mainPanel.add(operPanel, gridBagConstraints);

        navigPanel.setFocusable(false);
        java.awt.GridBagLayout toolsPanelLayout = new java.awt.GridBagLayout();
        toolsPanelLayout.columnWidths = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        toolsPanelLayout.rowHeights = new int[] {0, 2, 0};
        navigPanel.setLayout(toolsPanelLayout);

        filterField.setForeground(new java.awt.Color(102, 102, 102));
        filterField.setText(bundle.getString("CRUDPanel.filterField.text")); // NOI18N
        filterField.setPreferredSize(new java.awt.Dimension(80, 24));
        filterField.setPrefsLangKey(bundle.getString("CRUDPanel.filterField.prefsLangKey")); // NOI18N
        filterField.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                filterFieldCaretUpdate(evt);
            }
        });
        filterField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                filterFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                filterFieldFocusLost(evt);
            }
        });
        filterField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                filterFieldKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filterFieldKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.1;
        navigPanel.add(filterField, gridBagConstraints);

        firstButton.setAction(firstAction);
        firstButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        firstButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/up2-16.png"))); // NOI18N
        firstButton.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        firstButton.setFocusable(false);
        firstButton.setMaximumSize(new java.awt.Dimension(26, 26));
        firstButton.setMinimumSize(new java.awt.Dimension(26, 26));
        firstButton.setPreferredSize(new java.awt.Dimension(26, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        navigPanel.add(firstButton, gridBagConstraints);

        previousButton.setAction(upAction);
        previousButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/up16.png"))); // NOI18N
        previousButton.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        previousButton.setFocusable(false);
        previousButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        previousButton.setMaximumSize(new java.awt.Dimension(26, 26));
        previousButton.setMinimumSize(new java.awt.Dimension(26, 26));
        previousButton.setPreferredSize(new java.awt.Dimension(26, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        navigPanel.add(previousButton, gridBagConstraints);

        nextButton.setAction(downAction);
        nextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/down16.png"))); // NOI18N
        nextButton.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        nextButton.setFocusable(false);
        nextButton.setMaximumSize(new java.awt.Dimension(26, 26));
        nextButton.setMinimumSize(new java.awt.Dimension(26, 26));
        nextButton.setPreferredSize(new java.awt.Dimension(26, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        navigPanel.add(nextButton, gridBagConstraints);

        lastButton.setAction(lastAction);
        lastButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lastButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/down2-16.png"))); // NOI18N
        lastButton.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        lastButton.setFocusable(false);
        lastButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lastButton.setMaximumSize(new java.awt.Dimension(26, 26));
        lastButton.setMinimumSize(new java.awt.Dimension(26, 26));
        lastButton.setPreferredSize(new java.awt.Dimension(26, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        navigPanel.add(lastButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        mainPanel.add(navigPanel, gridBagConstraints);

        scrollPan.setBorder(null);
        scrollPan.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        mainPanel.add(scrollPan, gridBagConstraints);

        statusPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 0, 0, 0));
        statusPanel.setFocusable(false);
        statusPanel.setLayout(new java.awt.GridBagLayout());

        infosPanel.setFocusable(false);
        infosPanel.setLayout(new java.awt.GridBagLayout());

        reportTextPane.setEditable(false);
        reportTextPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 1));
        reportTextPane.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        reportTextPane.setFocusable(false);
        reportTextPane.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        infosPanel.add(reportTextPane, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        statusPanel.add(infosPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        mainPanel.add(statusPanel, gridBagConstraints);

        add(mainPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void filterFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_filterFieldKeyPressed
        if (filterField.getText().equals(getFilterFieldDesc())) {
            filterField.setCaretPosition(0);
            filterField.setText("");
            filterField.setForeground(Color.BLACK);
        }
        doOnFilterFildKeyPressed(evt);
    }//GEN-LAST:event_filterFieldKeyPressed

    private void filterFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_filterFieldKeyReleased
        if (filterField.getText().isEmpty()) {
            filterField.setForeground(new Color(102, 102, 102));
            filterField.setText(getFilterFieldDesc());
            filterField.setCaretPosition(0);
        }
        if (filterField.getText().equals(getFilterFieldDesc())) {
            filterList("");
        } else {
            filterList(filterField.getText().toUpperCase());
        }
    }//GEN-LAST:event_filterFieldKeyReleased

    private void optionsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optionsButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_optionsButtonActionPerformed

    private void filterFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_filterFieldFocusGained
        if (filterField.getText().equals(getFilterFieldDesc())) {
            filterField.setCaretPosition(0);
        }
        if (filterField.getText().isEmpty()) {
            filterField.setForeground(new Color(102, 102, 102));
            filterField.setText(getFilterFieldDesc());
            filterField.setCaretPosition(0);
        }

        try {
            Toolkit.getDefaultToolkit().setLockingKeyState(KeyEvent.VK_CAPS_LOCK, true);
        } catch (UnsupportedOperationException e) {
        }
    }//GEN-LAST:event_filterFieldFocusGained

    private void filterFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_filterFieldFocusLost
        if (filterField.getText().isEmpty()) {
            filterField.setForeground(new Color(102, 102, 102));
            filterField.setText(getFilterFieldDesc());
        }
    }//GEN-LAST:event_filterFieldFocusLost

    private void filterFieldCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_filterFieldCaretUpdate
        if (filterField.getCaretPosition() != 0 && filterField.getText().equals(getFilterFieldDesc())) {
            filterField.setCaretPosition(0);
        }
    }//GEN-LAST:event_filterFieldCaretUpdate

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton editButton;
    private myComponents.MyJField filterField;
    public javax.swing.JButton firstButton;
    protected javax.swing.JPanel infosPanel;
    private javax.swing.JButton insertButton;
    private javax.swing.JButton lastButton;
    protected javax.swing.JPanel mainPanel;
    public javax.swing.JPanel navigPanel;
    public javax.swing.JButton nextButton;
    public javax.swing.JPanel operPanel;
    private javax.swing.JButton optionsButton;
    private javax.swing.JButton previousButton;
    private javax.swing.JButton printButton;
    private javax.swing.JButton reloadButton;
    private javax.swing.JTextPane reportTextPane;
    public javax.swing.JScrollPane scrollPan;
    protected javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                MyJFrame frame = new MyJFrame();
                //ArticleDAO.getInstance().setGetAllParam(0);
                ListeProduitsPanel lpp = new ListeProduitsPanel(frame, false, true);
                frame.getContentPane().add(lpp, BorderLayout.CENTER);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
