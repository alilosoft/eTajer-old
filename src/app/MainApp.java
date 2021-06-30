/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import panels.crud.ListeCreditClPanel;
import panels.crud.InventaireStockPanel;
import panels.crud.ListeClientsPanel;
import panels.crud.ListeReglementsFrPanel;
import panels.crud.ListeUtilisateursPanel;
import panels.crud.ListeUnitesPanel;
import panels.crud.ListeReglementsClPanel;
import panels.crud.ListeFournissPanel;
import panels.crud.ListeDepotsPanel;
import appLogic.Licence;
import dao.AchatDAO;
import dao.DepotDAO;
import dao.ClientDAO;
import dao.CategorieDAO;
import dao.CreditClDAO;
import dao.CreditFrDAO;
import dao.LotEnStockDAO;
import dao.ProduitDAO;
import dao.UniteDAO;
import dao.FamilleDAO;
import dao.FournissDAO;
import dao.LigneAchatDAO;
import dao.LigneVenteDAO;
import dao.QuantifierDAO;
import dao.ReglementClDAO;
import dao.ReglementFrDAO;
import dao.VenteDAO;
import dbTools.DBManager;
import dialogs.AboutDialog;
import dialogs.BackupDBDialog;
import dialogs.LoginDialog;
import dialogs.PopupDialog;
import dialogs.RestoreDBDialog;
import dialogs.UpdateDBDialog;
import entities.AppUser;
import java.awt.*;
import java.awt.event.*;
import java.awt.im.InputContext;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipFile;
import javax.swing.*;
import myComponents.MyJPanel;
import panels.CRUDPanel;
import panels.MasterDetailsPanel;
import panels.maj.CarteEditorPanel;
import panels.masterDetails.GestAchatsPanel;
import panels.masterDetails.GestStockPanel;
import panels.masterDetails.GestProduitsPanel;
import panels.masterDetails.GestVentesPanel;
import panels.views.StatistiquesPanel;
import printing.PrintingTools;
import tools.ExceptionReporting;

/**
 *
 * @author alilo
 */
public class MainApp extends javax.swing.JFrame {

    private static final String versionStr = "eTajer 1.2";
    private final Image appIcon = new javax.swing.ImageIcon(getClass().getResource("/res/icons/tajer32.png")).getImage();

    private static MainApp instance;
    private DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    //private DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private boolean locked = true;
    private boolean alreadyLocked = false;
    private Timer mainAppTimer = new Timer(250, null);
    private AppUser currentUser, previousUser;
    private LoginDialog loginDialog;
    private int progress = 0;
    //<editor-fold defaultstate="collapsed" desc=" Date and Time Listener ">
    private ActionListener dateTimeListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            Date now = new Date();
            dateLabel.setText(dateFormat.format(now));
            timeLabel.setText(timeFormat.format(now));
        }
    };
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc=" Language Change Listener ">
    private ActionListener langChangeListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            langLabel.setText(InputContext.getInstance().getLocale().getISO3Language().toUpperCase());
        }
    };
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc=" Num Lock Listener ">
    private boolean numLockOn = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_NUM_LOCK);
    private boolean capsLockOn = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
    private ActionListener numLockListener = new ActionListener() {

        Color onColor = new Color(0, 180, 0);

        @Override
        public void actionPerformed(ActionEvent e) {
            if (Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_NUM_LOCK)) {
                numLockLabel.setForeground(onColor);
                numLockOn = true;
            } else {
                numLockLabel.setForeground(Color.GRAY);
                numLockOn = false;
            }
            if (Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK)) {
                capsLockLabel.setForeground(onColor);
                capsLockOn = true;
            } else {
                capsLockLabel.setForeground(Color.GRAY);
                capsLockOn = false;
            }
        }
    };
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Init Connection 0% ">
    final Runnable initConnection = new Runnable() {

        @Override
        public void run() {
            DBManager.getInstance().getDefaultConnection();
            if (!Licence.verify()) {
                System.exit(0);
            }
        }
    };
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc=" Init LoginDialog (0-5%) ">
    final Runnable initLoginDialog = new Runnable() {

        @Override
        public void run() {
            loginDialog = new LoginDialog(null, true) {

                @Override
                public void logout() {
                    previousUser = currentUser;
                    super.logout();
                }

                @Override
                public void login() {
                    loginDialog.setProgress(progress += 5);
                    currentUser = getUser();
                    try {
                        initMainUI.run();
                        initDAOs.run();
                        additionalInit.run();
                        initReports.run();
                    } catch (Exception e) {
                        ExceptionReporting.showException(e);
                        String mess = "Un erreur est survenu lors de l'initialisation de l'application!\n"
                                + "Voullez vous utiliser l'outil du maintenance pour corriger les problèmes?";
                        int rep = JOptionPane.showConfirmDialog(loginDialog, mess, "Erreur", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                        if (rep == JOptionPane.YES_OPTION) {
                            UpdateDBDialog.getInstance().setVisible(true);
                        }
                    }
                }

                @Override
                public void unlock() {
                    super.unlock();
                    MainApp.this.unlock();
                    //set
                }
            };
            new Thread() {

                @Override
                public void run() {
                    loginDialog.setVisible(true);
                }
            }.start();
            loginDialog.initUsers();
            loginDialog.addWindowListener(new WindowAdapter() {

                @Override
                public void windowActivated(WindowEvent e) {
                    super.windowActivated(e);
                    setLocked(true);
                    repaint();
                }
            });
        }
    };
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc=" Init GUI 20% (5-25%)">
    final Runnable initMainUI = new Runnable() {

        @Override
        public void run() {
            initComponents();
            initStateL.setText("Préparation: MainApp GUI");
            loginDialog.setProgress(progress += 15);
            setExtendedState(Frame.MAXIMIZED_BOTH);
            mainAppTimer.addActionListener(dateTimeListener);
            mainAppTimer.addActionListener(langChangeListener);
            mainAppTimer.addActionListener(numLockListener);
            mainAppTimer.start();
            loginDialog.setProgress(progress += 5);
            setVisible(true);
        }
    };
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc=" Init DAOs  25% (25-50%) ">
    final Runnable initDAOs = new Runnable() {

        @Override
        public void run() {
            initStateL.setText("Préparation: TablesDAO");
            int incrementBy = 1;
            FamilleDAO.getInstance();
            loginDialog.setProgress(progress += incrementBy);
            CategorieDAO.getInstance();
            loginDialog.setProgress(progress += incrementBy);
            ProduitDAO.getInstance();
            loginDialog.setProgress(progress += incrementBy);
            UniteDAO.getInstance();
            loginDialog.setProgress(progress += incrementBy);
            QuantifierDAO.getInstance();
            loginDialog.setProgress(progress += incrementBy);
            DepotDAO.getInstance();
            loginDialog.setProgress(progress += incrementBy);
            LotEnStockDAO.getInstance();
            loginDialog.setProgress(progress += incrementBy);
            FournissDAO.getInstance();
            loginDialog.setProgress(progress += incrementBy);
            AchatDAO.getInstance();
            loginDialog.setProgress(progress += incrementBy);
            LigneAchatDAO.getInstance();
            loginDialog.setProgress(progress += incrementBy);
            CreditFrDAO.getInstance();
            loginDialog.setProgress(progress += incrementBy);
            ReglementFrDAO.getInstance();
            loginDialog.setProgress(progress += incrementBy);
            ClientDAO.getInstance();
            loginDialog.setProgress(progress += incrementBy);
            VenteDAO.getInstance();
            loginDialog.setProgress(progress += incrementBy);
            LigneVenteDAO.getInstance();
            loginDialog.setProgress(progress += incrementBy);
            CreditClDAO.getInstance();
            loginDialog.setProgress(progress += incrementBy);
            ReglementClDAO.getInstance();
            loginDialog.setProgress(progress += incrementBy);
            // others
            loginDialog.setProgress(progress += 8);
        }
    };
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc=" Additional init 40% (50-90%)">
    final Runnable additionalInit = new Runnable() {

        @Override
        public void run() {
            if (currentUser.isAdmin()) {
                if (mainProdsPanel == null || currentUser.getUserGp().equals(previousUser.getUserGp())) {
                    initStateL.setText("Préparation: Gestion Porduits");
                    mainProdsPanel = new GestProduitsPanel(null, null, null);
                    loginDialog.setProgress(progress += 10);
                }

                if (mainAchatsPanel == null || currentUser.getUserGp().equals(previousUser.getUserGp())) {
                    initStateL.setText("Préparation: Gestion Achats");
                    mainAchatsPanel = new GestAchatsPanel();
                    loginDialog.setProgress(progress += 10);
                }

                if (mainVentePanel == null || currentUser.getUserGp().equals(previousUser.getUserGp())) {
                    initStateL.setText("Préparation: Gestion Ventes");
                    mainVentePanel = new GestVentesPanel();
                    loginDialog.setProgress(progress += 10);
                }

                if (vntComptoirPanel == null || currentUser.getUserGp().equals(previousUser.getUserGp())) {
                    initStateL.setText("Préparation: Vente au Comptoire");
                    vntComptoirPanel = new CarteEditorPanel(null);
                    loginDialog.setProgress(progress += 5);
                }

                if (lotStkPanel == null || currentUser.getUserGp().equals(previousUser.getUserGp())) {
                    initStateL.setText("Préparation: Gestion Stock");
                    lotStkPanel = new GestStockPanel(null, null, null);
                    loginDialog.setProgress(progress += 5);
                }
                
                if (recettePanel == null || currentUser.getUserGp().equals(previousUser.getUserGp())) {
                    recettePanel = new StatistiquesPanel();
                }

            } else {
                if (vntComptoirPanel == null || currentUser.getUserGp().equals(previousUser.getUserGp())) {
                    initStateL.setText("Préparation: Vente au Comptoire");
                    vntComptoirPanel = new CarteEditorPanel(null);
                    loginDialog.setProgress(progress += 40);
                    //setContentPane(vntComptoirPanel);
                    venteComptoir();
                }
            }
        }
    };
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc=" Init Reports 10% (90-100%) ">
    private boolean reportsInitialized = false;
    final Runnable initReports = new Runnable() {

        @Override
        public void run() {
            if (!reportsInitialized) {
                initStateL.setText("Préparation: Etats d'impression");
                PrintingTools.initializeReports(DBManager.getInstance().getDefaultConnection());
                loginDialog.setProgress(progress += 10);
                reportsInitialized = true;
                initStateL.setText(versionStr);
            }
        }
    };
    //</editor-fold>

    private final PopupDialog popupMenu = new PopupDialog(this);

    public MainApp() throws HeadlessException {
        setIconImage(appIcon);
    }

    public static MainApp getInstance() {
        if (instance == null) {
            instance = new MainApp();
        }
        return instance;
    }

    public void startApp() {
        //<editor-fold defaultstate="collapsed" desc=" Main Thread ">
        new Thread() {

            @Override
            public void run() {
                try {
                    SwingUtilities.invokeAndWait(initConnection);
                    SwingUtilities.invokeAndWait(initLoginDialog);
                    //SwingUtilities.invokeAndWait(initDAOs);
                } catch (InterruptedException | InvocationTargetException ex) {
                    ExceptionReporting.showException(ex);
                }
            }
        }.start();
        //</editor-fold>
    }

    @Override
    public void paint(Graphics g) {
        if (!isLocked()) {
            super.paint(g);
            alreadyLocked = false;
        } else {
            if (!alreadyLocked) {
                super.paint(g);
                String lockedImg = "/res/png/locked.png";
                ImageIcon ico = new javax.swing.ImageIcon(getClass().getResource(lockedImg));
                Image img = ico.getImage();
                g.drawImage(img, 0, 0, getWidth(), getHeight() - 27, this);
                alreadyLocked = true;
            }
        }
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isLocked() {
        return locked;
    }

    public void lock() {
        setLocked(true);
        //homePanelAnimator.stop();
        loginDialog.lock();
    }
    public Action lockAction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            lock();
        }
    };

    public void unlock() {
        setLocked(false);
        repaint();
        requestFocus();
        toFront();
        userLabel.setText(LoginDialog.getUser().getLogin());
        if (isUserChanged()) {
            mainTabbedPane.removeAll();
        }
        gestProdBtn.setEnabled(LoginDialog.getUser().isAdmin());
        gestStockBtn.setEnabled(LoginDialog.getUser().isAdmin());
        gestAchatBtn.setEnabled(LoginDialog.getUser().isAdmin());
        usersBtn.setEnabled(LoginDialog.getUser().isAdmin());
        connSettingsBtn.setEnabled(LoginDialog.getUser().isAdmin());
        //settingsBtn.setEnabled(LoginDialog.getUser().isAdmin());
        uniteAction.setEnabled(LoginDialog.getUser().isAdmin());
        connSettingsBtn.setEnabled(LoginDialog.getUser().isAdmin());
    }

    public boolean isNumLockOn() {
        return numLockOn;
    }

    public void setNumLockOn(boolean numLockOn) {
        this.numLockOn = numLockOn;
        Toolkit.getDefaultToolkit().setLockingKeyState(KeyEvent.VK_NUM_LOCK, this.numLockOn);
    }

    public boolean isCapsLockOn() {
        return capsLockOn;
    }

    public void setCapsLockOn(boolean capsLockOn) {
        this.capsLockOn = capsLockOn;
        Toolkit.getDefaultToolkit().setLockingKeyState(KeyEvent.VK_CAPS_LOCK, this.capsLockOn);
    }

    public void removeTopToolBar() {
        rootPanel.remove(topBar);
        rootPanel.validate();
    }

    public void restorTopToolBar() {
        rootPanel.add(topBar, BorderLayout.NORTH);
        rootPanel.validate();
    }

    public void removeCenterPanel() {
        rootPanel.remove(centerPanel);
        rootPanel.validate();
    }

    public void restorCenterPanel() {
        rootPanel.add(centerPanel, BorderLayout.CENTER);
        rootPanel.validate();
    }

    public void removeBottomToolBar() {
        rootPanel.remove(statusBar);
        rootPanel.validate();
    }

    public void restorBottomToolBar() {
        rootPanel.add(statusBar, BorderLayout.SOUTH);
        rootPanel.validate();
    }

    public MyJPanel getRootPanel() {
        return rootPanel;
    }

    public boolean isUserChanged() {
        if (previousUser != null) {
            return currentUser.getUserGp().getId().doubleValue() != previousUser.getUserGp().getId().doubleValue();
        } else {
            return false;
        }
    }
    private final Action settingAction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            MainSettingsPanel.open();
        }
    };
    private final Icon showToolBarIcon = new ImageIcon(getClass().getResource("/res/actions/showToolBar24.png"));
    private Action showToolBarAction = new AbstractAction("", showToolBarIcon) {

        {
            putValue(Action.SHORT_DESCRIPTION, "Afficher la barre d'outils");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //toolBar.setVisible(true);
            //toggelToolBarButton.setAction(hideToolBarAction);
        }
    };
    private final Icon hideToolBarIcon = new ImageIcon(getClass().getResource("/res/actions/hideToolBar24.png"));
    private Action hideToolBarAction = new AbstractAction("", hideToolBarIcon) {

        {
            putValue(Action.SHORT_DESCRIPTION, "Masquer la barre d'outils");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //toolBar.setVisible(false);
            //toggelToolBarButton.setAction(showToolBarAction);
        }
    };

    public void home() {
        if (mainTabbedPane.indexOfComponent(homeTab) == -1) {
            mainTabbedPane.addTab("Accueil", homeButton.getIcon(), homeTab);
            mainTabbedPane.setSelectedComponent(homeTab);
        } else {
            if (mainTabbedPane.getSelectedComponent() == homeTab) {
                mainTabbedPane.remove(homeTab);
            } else {
                mainTabbedPane.setSelectedComponent(homeTab);
            }
        }
    }
    public Action goHomeAction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            home();
        }
    };
    private GestProduitsPanel mainProdsPanel;

    public void gestProduits() {
        if (mainProdsPanel == null || isUserChanged()) {
            mainProdsPanel = new GestProduitsPanel(null, null, null);
        }
        if (mainTabbedPane.indexOfComponent(mainProdsPanel) == -1) {
            mainTabbedPane.addTab("Produits", gestProdBtn.getIcon(), mainProdsPanel);
            mainTabbedPane.setSelectedComponent(mainProdsPanel);
        } else {
            if (mainTabbedPane.getSelectedComponent() == mainProdsPanel) {
                mainTabbedPane.remove(mainProdsPanel);
            } else {
                mainTabbedPane.setSelectedComponent(mainProdsPanel);
            }
        }
    }
    public Action gestProdAction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            gestProduits();
        }
    };

    //private ListeLotsEnStockPanel enStkPanel;
    private GestStockPanel lotStkPanel;

    public void gestStock() {
        if (lotStkPanel == null || isUserChanged()) {
            lotStkPanel = new GestStockPanel(null, null, null);
        }
        if (mainTabbedPane.indexOfComponent(lotStkPanel) == -1) {
            mainTabbedPane.addTab("Lots en stock", gestStockBtn.getIcon(), lotStkPanel);
            mainTabbedPane.setSelectedComponent(lotStkPanel);

        } else {
            if (mainTabbedPane.getSelectedComponent() == lotStkPanel) {
                mainTabbedPane.remove(lotStkPanel);
            } else {
                mainTabbedPane.setSelectedComponent(lotStkPanel);
            }
        }
    }
    public Action gestStockAction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            gestStock();
        }
    };

    private InventaireStockPanel inventairePanel;

    public void inventaireStk() {
        if (inventairePanel == null || isUserChanged()) {
            inventairePanel = new InventaireStockPanel(this);
        }
        if (mainTabbedPane.indexOfComponent(inventairePanel) == -1) {
            mainTabbedPane.addTab("Inventaire", inventaireBtn.getIcon(), inventairePanel);
            mainTabbedPane.setSelectedComponent(inventairePanel);
        } else {
            if (mainTabbedPane.getSelectedComponent() == inventairePanel) {
                mainTabbedPane.remove(inventairePanel);
            } else {
                mainTabbedPane.setSelectedComponent(inventairePanel);
            }
        }
    }
    public Action inventaireAction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            inventaireStk();
        }
    };

    private ListeFournissPanel fournisseursPanel;

    public void gestFournisseurs() {
        if (fournisseursPanel == null || isUserChanged()) {
            fournisseursPanel = new ListeFournissPanel(mainTabbedPane, false);
        }
        if (mainTabbedPane.indexOfComponent(fournisseursPanel) == -1) {
            mainTabbedPane.addTab("Fournisseurs", fournissBtn.getIcon(), fournisseursPanel);
            mainTabbedPane.setSelectedComponent(fournisseursPanel);
        } else {
            if (mainTabbedPane.getSelectedComponent() == fournisseursPanel) {
                mainTabbedPane.remove(fournisseursPanel);
            } else {
                mainTabbedPane.setSelectedComponent(fournisseursPanel);
            }
        }
    }
    public Action gestFournAction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            gestFournisseurs();
        }
    };
    private GestAchatsPanel mainAchatsPanel;

    public void gestAchats() {
        if (mainAchatsPanel == null || isUserChanged()) {
            mainAchatsPanel = new GestAchatsPanel();
        }
        if (mainTabbedPane.indexOfComponent(mainAchatsPanel) == -1) {
            mainTabbedPane.addTab("Achats", gestAchatBtn.getIcon(), mainAchatsPanel);
            mainTabbedPane.setSelectedComponent(mainAchatsPanel);
        } else {
            if (mainTabbedPane.getSelectedComponent() == mainAchatsPanel) {
                mainTabbedPane.remove(mainAchatsPanel);
            } else {
                mainTabbedPane.setSelectedComponent(mainAchatsPanel);
            }
        }
    }
    public Action gestAchatAction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            gestAchats();
        }
    };

    private ListeReglementsFrPanel reglementsFrPanel;

    public void gestReglFr() {
        if (reglementsFrPanel == null || isUserChanged()) {
            reglementsFrPanel = new ListeReglementsFrPanel(mainTabbedPane, false);
        }
        if (mainTabbedPane.indexOfComponent(reglementsFrPanel) == -1) {
            mainTabbedPane.addTab("Paiement des fournisseurs", regAchatButton.getIcon(), reglementsFrPanel);
            mainTabbedPane.setSelectedComponent(reglementsFrPanel);
        } else {
            if (mainTabbedPane.getSelectedComponent() == reglementsFrPanel) {
                mainTabbedPane.remove(reglementsFrPanel);
            } else {
                mainTabbedPane.setSelectedComponent(reglementsFrPanel);
            }
        }
    }

    public Action gestReglFrAction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            gestReglFr();
        }
    };

    private ListeClientsPanel clientsPanel;

    public void gestClients() {
        if (clientsPanel == null || isUserChanged()) {
            clientsPanel = new ListeClientsPanel(mainTabbedPane, false).initTableView();
        }
        if (mainTabbedPane.indexOfComponent(clientsPanel) == -1) {
            mainTabbedPane.addTab("Clients", clientsBtn.getIcon(), clientsPanel);
            mainTabbedPane.setSelectedComponent(clientsPanel);
        } else {
            if (mainTabbedPane.getSelectedComponent() == clientsPanel) {
                mainTabbedPane.remove(clientsPanel);
            } else {
                mainTabbedPane.setSelectedComponent(clientsPanel);
            }
        }
    }
    public Action gestClientAction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            gestClients();
        }
    };

    private GestVentesPanel mainVentePanel = null;

    public void gestVentes() {
        if (mainVentePanel == null || isUserChanged()) {
            mainVentePanel = new GestVentesPanel();
        }
        if (mainTabbedPane.indexOfComponent(mainVentePanel) == -1) {
            mainTabbedPane.addTab("Ventes", gestVentesBtn.getIcon(), mainVentePanel);
            mainTabbedPane.setSelectedComponent(mainVentePanel);
        } else {
            if (mainTabbedPane.getSelectedComponent() == mainVentePanel) {
                mainTabbedPane.remove(mainVentePanel);
            } else {
                mainTabbedPane.setSelectedComponent(mainVentePanel);
            }
        }
    }
    public Action gestVenteAction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            gestVentes();
        }
    };

    private CarteEditorPanel vntComptoirPanel = null;

    public void venteComptoir() {
        if (vntComptoirPanel == null || isUserChanged()) {
            vntComptoirPanel = new CarteEditorPanel(null);
        }
        if (mainTabbedPane.indexOfComponent(vntComptoirPanel) == -1) {
            mainTabbedPane.addTab("Comptoir", gestVentesBtn.getIcon(), vntComptoirPanel);
            mainTabbedPane.setSelectedComponent(vntComptoirPanel);
        } else {
            if (mainTabbedPane.getSelectedComponent() == vntComptoirPanel) {
                mainTabbedPane.remove(vntComptoirPanel);
            } else {
                mainTabbedPane.setSelectedComponent(vntComptoirPanel);
            }
        }
        vntComptoirPanel.getDetailsVentePanel().cbField.requestFocus();
        
    }

    public Action vntComptoirAction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            venteComptoir();
        }
    };

    private ListeCreditClPanel creditClPanel;

    public void gestCreditCL() {
        if (creditClPanel == null || isUserChanged()) {
            creditClPanel = new ListeCreditClPanel(mainTabbedPane, false).initTableView();
        }
        if (mainTabbedPane.indexOfComponent(creditClPanel) == -1) {
            mainTabbedPane.addTab("Crédit des Clients", creditClButton.getIcon(), creditClPanel);
            mainTabbedPane.setSelectedComponent(creditClPanel);
        } else {
            if (mainTabbedPane.getSelectedComponent() == creditClPanel) {
                mainTabbedPane.remove(creditClPanel);
            } else {
                mainTabbedPane.setSelectedComponent(creditClPanel);
            }
        }
    }
    public Action gestCreditClAction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            gestCreditCL();
        }
    };
    // Gestion des Règlement Client
    private ListeReglementsClPanel reglementClPanel;

    public void gestReglCl() {
        if (reglementClPanel == null || isUserChanged()) {
            reglementClPanel = new ListeReglementsClPanel(mainTabbedPane, false).initTableView();
        }
        if (mainTabbedPane.indexOfComponent(reglementClPanel) == -1) {
            mainTabbedPane.addTab("Règlements des Clients", regClButton.getIcon(), reglementClPanel);
            mainTabbedPane.setSelectedComponent(reglementClPanel);
        } else {
            if (mainTabbedPane.getSelectedComponent() == reglementClPanel) {
                mainTabbedPane.remove(reglementClPanel);
            } else {
                mainTabbedPane.setSelectedComponent(reglementClPanel);
            }
        }
    }
    public Action reglClAction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            gestReglCl();
        }
    };

    private StatistiquesPanel recettePanel;

    public void recette() {
        if (recettePanel == null || isUserChanged()) {
            recettePanel = new StatistiquesPanel();
        }
        if (mainTabbedPane.indexOfComponent(recettePanel) == -1) {
            mainTabbedPane.addTab("Recette", recetteButton.getIcon(), recettePanel);
            mainTabbedPane.setSelectedComponent(recettePanel);
        } else {
            if (mainTabbedPane.getSelectedComponent() == recettePanel) {
                mainTabbedPane.remove(recettePanel);
            } else {
                mainTabbedPane.setSelectedComponent(recettePanel);
            }
        }
    }
    public Action recetteAction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            recette();
        }
    };

    private ListeDepotsPanel depotsPanel;

    public void gestDepots() {
        if (depotsPanel == null || isUserChanged()) {
            depotsPanel = new ListeDepotsPanel(mainTabbedPane, false).initTableView();
        }
        if (mainTabbedPane.indexOfComponent(depotsPanel) == -1) {
            mainTabbedPane.addTab("Dépôts", depotBtn.getIcon(), depotsPanel);
            mainTabbedPane.setSelectedComponent(depotsPanel);
        } else {
            if (mainTabbedPane.getSelectedComponent() == depotsPanel) {
                mainTabbedPane.remove(depotsPanel);
            } else {
                mainTabbedPane.setSelectedComponent(depotsPanel);
            }
        }
    }
    public Action depotsAction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            gestDepots();
        }
    };
    private ListeUnitesPanel unitesPanel;

    public void gestUnites() {
        if (unitesPanel == null || isUserChanged()) {
            unitesPanel = new ListeUnitesPanel(mainTabbedPane, false).initTableView();
        }
        if (mainTabbedPane.indexOfComponent(unitesPanel) == -1) {
            mainTabbedPane.addTab("Unités", uniteBtn.getIcon(), unitesPanel);
            mainTabbedPane.setSelectedComponent(unitesPanel);
        } else {
            if (mainTabbedPane.getSelectedComponent() == unitesPanel) {
                mainTabbedPane.remove(unitesPanel);
            } else {
                mainTabbedPane.setSelectedComponent(unitesPanel);
            }
        }
    }
    public Action uniteAction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            gestUnites();
        }
    };
    private ListeUtilisateursPanel utilisateursPanel;

    public void gestUsers() {
        if (utilisateursPanel == null || isUserChanged()) {
            utilisateursPanel = new ListeUtilisateursPanel(mainTabbedPane, false).initTableView();
        }
        if (mainTabbedPane.indexOfComponent(utilisateursPanel) == -1) {
            mainTabbedPane.addTab("Utilisateurs", usersButton.getIcon(), utilisateursPanel);
            mainTabbedPane.setSelectedComponent(utilisateursPanel);
        } else {
            if (mainTabbedPane.getSelectedComponent() == utilisateursPanel) {
                mainTabbedPane.remove(utilisateursPanel);
            } else {
                mainTabbedPane.setSelectedComponent(utilisateursPanel);
            }
        }
    }
    public Action userAction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            gestUsers();
        }
    };

    public void swithFullScreen() {
        if (isUndecorated()) {
            dispose();
            setUndecorated(false);
            setExtendedState(Frame.MAXIMIZED_BOTH);
            setVisible(true);
        } else {
            dispose();
            setUndecorated(true);
            setExtendedState(Frame.MAXIMIZED_BOTH);
            setVisible(true);
        }
    }
    public Action switchFullScreenAction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            swithFullScreen();
        }
    };

    public static Action calcAction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            MainTools.runSystemCalc();
        }
    };

    public void exit() {
        if (JOptionPane.showConfirmDialog(this, "Voullez vous vraiment quiter l'application?", "Attention!!!", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            setVisible(false);
            dispose();
            if (DBManager.getInstance().isLocalHost()) {
                BackupDBDialog.getInstance().setVisible(true);
            }
            System.exit(0);
        }
    }
    public Action exitAction = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            exit();
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

        toolsTB = new javax.swing.JToolBar();
        usersButton = new javax.swing.JButton();
        usersButton1 = new javax.swing.JButton();
        gestProduitsP = new javax.swing.JPanel();
        produitBtn = new javax.swing.JButton();
        familleBtn = new javax.swing.JButton();
        categBtn = new javax.swing.JButton();
        uniteBtn = new javax.swing.JButton();
        gestVentesP = new javax.swing.JPanel();
        venteBtn = new javax.swing.JButton();
        vntComptoirBtn = new javax.swing.JButton();
        recetteButton = new javax.swing.JButton();
        clientsBtn = new javax.swing.JButton();
        creditClButton = new javax.swing.JButton();
        regClButton = new javax.swing.JButton();
        venteButton6 = new javax.swing.JButton();
        venteButton3 = new javax.swing.JButton();
        venteButton4 = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        venteButton8 = new javax.swing.JButton();
        venteButton9 = new javax.swing.JButton();
        gestStockP = new myComponents.MyJPanel();
        stockBtn = new javax.swing.JButton();
        inventaireBtn = new javax.swing.JButton();
        depotBtn = new javax.swing.JButton();
        inventaireBtn1 = new javax.swing.JButton();
        gestAchP = new myComponents.MyJPanel();
        achatButton3 = new javax.swing.JButton();
        achatButton = new javax.swing.JButton();
        fournissBtn = new javax.swing.JButton();
        regAchatButton = new javax.swing.JButton();
        achatButton4 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        achatButton5 = new javax.swing.JButton();
        myJPanel1 = new myComponents.MyJPanel();
        rootPanel = new myComponents.MyJPanel();
        topBar = new javax.swing.JToolBar();
        homeButton = new javax.swing.JButton();
        gestProdBtn = new javax.swing.JButton();
        gestStockBtn = new javax.swing.JButton();
        gestAchatBtn = new javax.swing.JButton();
        gestVentesBtn = new javax.swing.JButton();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        bismillahLabel = new javax.swing.JLabel();
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        usersBtn = new javax.swing.JButton();
        calcButton = new javax.swing.JButton();
        connSettingsBtn = new javax.swing.JButton();
        settingsBtn = new javax.swing.JButton();
        about = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        fullScreenButton = new javax.swing.JButton();
        minimizeButton = new javax.swing.JButton();
        exitButton = new javax.swing.JButton();
        centerPanel = new javax.swing.JPanel();
        mainTabbedPane = new myComponents.MyJTabbedPane();
        homeTab = new javax.swing.JScrollPane();
        imagePanel1 = new myComponents.ImagePanel();
        statusBar = new javax.swing.JToolBar();
        lockButton = new javax.swing.JButton();
        statusPanel = new javax.swing.JPanel();
        uLabel = new javax.swing.JLabel();
        userLabel = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(300, 0), new java.awt.Dimension(32767, 0));
        dateLabel = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        timeLabel = new javax.swing.JLabel();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        langLabel = new javax.swing.JLabel();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        numLockLabel = new javax.swing.JLabel();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        capsLockLabel = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(300, 0), new java.awt.Dimension(32767, 0));
        initStateL = new javax.swing.JLabel();

        toolsTB.setRollover(true);

        usersButton.setAction(userAction);
        usersButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        usersButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/user24.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("app/resources"); // NOI18N
        usersButton.setText(bundle.getString("MainApp.usersButton.text")); // NOI18N
        usersButton.setFocusable(false);
        toolsTB.add(usersButton);

        usersButton1.setAction(userAction);
        usersButton1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        usersButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/user24.png"))); // NOI18N
        usersButton1.setText(bundle.getString("MainApp.usersButton1.text")); // NOI18N
        usersButton1.setFocusable(false);
        usersButton1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        toolsTB.add(usersButton1);

        gestProduitsP.setLayout(new java.awt.GridBagLayout());

        produitBtn.setAction(gestProdAction);
        produitBtn.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        produitBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/prod48.png"))); // NOI18N
        produitBtn.setText(bundle.getString("MainApp.produitBtn.text_1")); // NOI18N
        produitBtn.setFocusable(false);
        produitBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        produitBtn.setOpaque(false);
        produitBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        produitBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                produitBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gestProduitsP.add(produitBtn, gridBagConstraints);

        familleBtn.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        familleBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/categ24.png"))); // NOI18N
        familleBtn.setText(bundle.getString("MainApp.familleBtn.text")); // NOI18N
        familleBtn.setToolTipText(bundle.getString("MainApp.familleBtn.toolTipText")); // NOI18N
        familleBtn.setFocusable(false);
        familleBtn.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        familleBtn.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gestProduitsP.add(familleBtn, gridBagConstraints);

        categBtn.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        categBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/categ24.png"))); // NOI18N
        categBtn.setText(bundle.getString("MainApp.categBtn.text")); // NOI18N
        categBtn.setFocusable(false);
        categBtn.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        categBtn.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gestProduitsP.add(categBtn, gridBagConstraints);

        uniteBtn.setAction(uniteAction);
        uniteBtn.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        uniteBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/unite24.png"))); // NOI18N
        uniteBtn.setText(bundle.getString("MainApp.uniteBtn.text")); // NOI18N
        uniteBtn.setFocusable(false);
        uniteBtn.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gestProduitsP.add(uniteBtn, gridBagConstraints);

        gestVentesP.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        gestVentesP.setLayout(new java.awt.GridBagLayout());

        venteBtn.setAction(vntComptoirAction);
        venteBtn.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        venteBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/vente32.png"))); // NOI18N
        venteBtn.setText(bundle.getString("MainApp.venteBtn.text_1")); // NOI18N
        venteBtn.setFocusable(false);
        venteBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        venteBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.weighty = 0.5;
        gestVentesP.add(venteBtn, gridBagConstraints);

        vntComptoirBtn.setAction(gestVenteAction);
        vntComptoirBtn.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        vntComptoirBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/vente24.png"))); // NOI18N
        vntComptoirBtn.setText(bundle.getString("MainApp.vntComptoirBtn.text_1")); // NOI18N
        vntComptoirBtn.setFocusable(false);
        vntComptoirBtn.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gestVentesP.add(vntComptoirBtn, gridBagConstraints);

        recetteButton.setAction(recetteAction);
        recetteButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        recetteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/dollar24.png"))); // NOI18N
        recetteButton.setText(bundle.getString("MainApp.recetteButton.text")); // NOI18N
        recetteButton.setToolTipText(bundle.getString("MainApp.recetteButton.toolTipText")); // NOI18N
        recetteButton.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gestVentesP.add(recetteButton, gridBagConstraints);

        clientsBtn.setAction(gestClientAction);
        clientsBtn.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        clientsBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/client32.png"))); // NOI18N
        clientsBtn.setText(bundle.getString("MainApp.clientsBtn.text")); // NOI18N
        clientsBtn.setFocusable(false);
        clientsBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        clientsBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        clientsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clientsBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gestVentesP.add(clientsBtn, gridBagConstraints);

        creditClButton.setAction(gestCreditClAction);
        creditClButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        creditClButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/credit24.png"))); // NOI18N
        creditClButton.setText(bundle.getString("MainApp.creditClButton.text_1")); // NOI18N
        creditClButton.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gestVentesP.add(creditClButton, gridBagConstraints);

        regClButton.setAction(reglClAction);
        regClButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        regClButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/regl_vente24.png"))); // NOI18N
        regClButton.setText(bundle.getString("MainApp.regClButton.text")); // NOI18N
        regClButton.setToolTipText(bundle.getString("MainApp.regClButton.toolTipText")); // NOI18N
        regClButton.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gestVentesP.add(regClButton, gridBagConstraints);

        venteButton6.setAction(gestVenteAction);
        venteButton6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        venteButton6.setText(bundle.getString("MainApp.venteButton6.text")); // NOI18N
        venteButton6.setEnabled(false);
        venteButton6.setFocusable(false);
        venteButton6.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        venteButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gestVentesP.add(venteButton6, gridBagConstraints);

        venteButton3.setAction(gestVenteAction);
        venteButton3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        venteButton3.setText(bundle.getString("MainApp.venteButton3.text")); // NOI18N
        venteButton3.setEnabled(false);
        venteButton3.setFocusable(false);
        venteButton3.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gestVentesP.add(venteButton3, gridBagConstraints);

        venteButton4.setAction(gestVenteAction);
        venteButton4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        venteButton4.setText(bundle.getString("MainApp.venteButton4.text")); // NOI18N
        venteButton4.setEnabled(false);
        venteButton4.setFocusable(false);
        venteButton4.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gestVentesP.add(venteButton4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gestVentesP.add(jSeparator3, gridBagConstraints);

        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator4.setPreferredSize(new java.awt.Dimension(5, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gestVentesP.add(jSeparator4, gridBagConstraints);

        venteButton8.setAction(gestVenteAction);
        venteButton8.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        venteButton8.setText(bundle.getString("MainApp.venteButton8.text")); // NOI18N
        venteButton8.setEnabled(false);
        venteButton8.setFocusable(false);
        venteButton8.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gestVentesP.add(venteButton8, gridBagConstraints);

        venteButton9.setAction(gestVenteAction);
        venteButton9.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        venteButton9.setText(bundle.getString("MainApp.venteButton9.text")); // NOI18N
        venteButton9.setEnabled(false);
        venteButton9.setFocusable(false);
        venteButton9.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gestVentesP.add(venteButton9, gridBagConstraints);

        gestStockP.setLayout(new java.awt.GridBagLayout());

        stockBtn.setAction(gestStockAction);
        stockBtn.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        stockBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/colis48.png"))); // NOI18N
        stockBtn.setText(bundle.getString("MainApp.stockBtn.text_1")); // NOI18N
        stockBtn.setFocusable(false);
        stockBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        stockBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gestStockP.add(stockBtn, gridBagConstraints);

        inventaireBtn.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        inventaireBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/inventaire24.png"))); // NOI18N
        inventaireBtn.setText(bundle.getString("MainApp.inventaireBtn.text")); // NOI18N
        inventaireBtn.setEnabled(false);
        inventaireBtn.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gestStockP.add(inventaireBtn, gridBagConstraints);

        depotBtn.setAction(depotsAction);
        depotBtn.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        depotBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/stock24.png"))); // NOI18N
        depotBtn.setText(bundle.getString("MainApp.depotBtn.text")); // NOI18N
        depotBtn.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gestStockP.add(depotBtn, gridBagConstraints);

        inventaireBtn1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        inventaireBtn1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/inventaire24.png"))); // NOI18N
        inventaireBtn1.setText(bundle.getString("MainApp.inventaireBtn1.text")); // NOI18N
        inventaireBtn1.setEnabled(false);
        inventaireBtn1.setFocusable(false);
        inventaireBtn1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gestStockP.add(inventaireBtn1, gridBagConstraints);

        gestAchP.setLayout(new java.awt.GridBagLayout());

        achatButton3.setAction(gestAchatAction);
        achatButton3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        achatButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/details.png"))); // NOI18N
        achatButton3.setText(bundle.getString("MainApp.achatButton3.text")); // NOI18N
        achatButton3.setEnabled(false);
        achatButton3.setFocusable(false);
        achatButton3.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gestAchP.add(achatButton3, gridBagConstraints);

        achatButton.setAction(gestAchatAction);
        achatButton.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        achatButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/achat48.png"))); // NOI18N
        achatButton.setText(bundle.getString("MainApp.achatButton.text_1")); // NOI18N
        achatButton.setFocusable(false);
        achatButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        achatButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gestAchP.add(achatButton, gridBagConstraints);

        fournissBtn.setAction(gestAchatAction);
        fournissBtn.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        fournissBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/fourniss24.png"))); // NOI18N
        fournissBtn.setText(bundle.getString("MainApp.fournissBtn.text")); // NOI18N
        fournissBtn.setFocusable(false);
        fournissBtn.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        fournissBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fournissBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gestAchP.add(fournissBtn, gridBagConstraints);

        regAchatButton.setAction(gestReglFrAction);
        regAchatButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        regAchatButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/regl_achat24.png"))); // NOI18N
        regAchatButton.setText(bundle.getString("MainApp.regAchatButton.text_1")); // NOI18N
        regAchatButton.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gestAchP.add(regAchatButton, gridBagConstraints);

        achatButton4.setAction(gestAchatAction);
        achatButton4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        achatButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/credit24.png"))); // NOI18N
        achatButton4.setText(bundle.getString("MainApp.achatButton4.text")); // NOI18N
        achatButton4.setFocusable(false);
        achatButton4.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gestAchP.add(achatButton4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gestAchP.add(jSeparator2, gridBagConstraints);

        achatButton5.setAction(gestAchatAction);
        achatButton5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        achatButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/details.png"))); // NOI18N
        achatButton5.setText(bundle.getString("MainApp.achatButton5.text")); // NOI18N
        achatButton5.setEnabled(false);
        achatButton5.setFocusable(false);
        achatButton5.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gestAchP.add(achatButton5, gridBagConstraints);

        javax.swing.GroupLayout myJPanel1Layout = new javax.swing.GroupLayout(myJPanel1);
        myJPanel1.setLayout(myJPanel1Layout);
        myJPanel1Layout.setHorizontalGroup(
            myJPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        myJPanel1Layout.setVerticalGroup(
            myJPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(bundle.getString("MainApp.title")); // NOI18N
        setFocusable(false);
        setMinimumSize(new java.awt.Dimension(850, 550));
        setName(bundle.getString("MainApp.name")); // NOI18N
        setUndecorated(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowDeiconified(java.awt.event.WindowEvent evt) {
                formWindowDeiconified(evt);
            }
        });

        rootPanel.setLayout(new java.awt.BorderLayout());

        topBar.setFloatable(false);
        topBar.setPreferredSize(new java.awt.Dimension(671, 30));

        homeButton.setAction(goHomeAction);
        homeButton.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        homeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tajer24.png"))); // NOI18N
        homeButton.setText(bundle.getString("MainApp.homeButton.text")); // NOI18N
        homeButton.setFocusable(false);
        homeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                homeButtonActionPerformed(evt);
            }
        });
        topBar.add(homeButton);

        gestProdBtn.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        gestProdBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/prod24.png"))); // NOI18N
        gestProdBtn.setText(bundle.getString("MainApp.gestProdBtn.text")); // NOI18N
        gestProdBtn.setToolTipText(bundle.getString("MainApp.gestProdBtn.toolTipText")); // NOI18N
        gestProdBtn.setFocusable(false);
        gestProdBtn.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        gestProdBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gestProdBtnActionPerformed(evt);
            }
        });
        topBar.add(gestProdBtn);

        gestStockBtn.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        gestStockBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/colis24.png"))); // NOI18N
        gestStockBtn.setText(bundle.getString("MainApp.gestStockBtn.text")); // NOI18N
        gestStockBtn.setFocusable(false);
        gestStockBtn.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        gestStockBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gestStockBtnActionPerformed(evt);
            }
        });
        topBar.add(gestStockBtn);

        gestAchatBtn.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        gestAchatBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/achat24.png"))); // NOI18N
        gestAchatBtn.setText(bundle.getString("MainApp.gestAchatBtn.text")); // NOI18N
        gestAchatBtn.setFocusable(false);
        gestAchatBtn.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        gestAchatBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gestAchatBtnActionPerformed(evt);
            }
        });
        topBar.add(gestAchatBtn);

        gestVentesBtn.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        gestVentesBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/vente24.png"))); // NOI18N
        gestVentesBtn.setText(bundle.getString("MainApp.gestVentesBtn.text")); // NOI18N
        gestVentesBtn.setToolTipText(bundle.getString("MainApp.gestVentesBtn.toolTipText")); // NOI18N
        gestVentesBtn.setFocusable(false);
        gestVentesBtn.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        gestVentesBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gestVentesBtnActionPerformed(evt);
            }
        });
        topBar.add(gestVentesBtn);
        topBar.add(filler7);

        bismillahLabel.setFont(new java.awt.Font("KacstDecorative", 0, 24)); // NOI18N
        bismillahLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        bismillahLabel.setText("بسم الله، توكلت على الله و لا حول ولا قوة إلا بالله"); // NOI18N
        bismillahLabel.setToolTipText("لا تنس ذكر الله"); // NOI18N
        bismillahLabel.setMinimumSize(new java.awt.Dimension(375, 30));
        topBar.add(bismillahLabel);
        topBar.add(filler8);

        usersBtn.setAction(userAction);
        usersBtn.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        usersBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/user24.png"))); // NOI18N
        usersBtn.setFocusable(false);
        usersBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        usersBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        topBar.add(usersBtn);

        calcButton.setAction(calcAction);
        calcButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        calcButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/calc24.png"))); // NOI18N
        calcButton.setText(bundle.getString("MainApp.calcButton.text")); // NOI18N
        calcButton.setFocusable(false);
        topBar.add(calcButton);

        connSettingsBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/dbSettings24.png"))); // NOI18N
        connSettingsBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 5));
        connSettingsBtn.setFocusable(false);
        connSettingsBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        connSettingsBtn.setOpaque(false);
        connSettingsBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        connSettingsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connSettingsBtnActionPerformed(evt);
            }
        });
        topBar.add(connSettingsBtn);

        settingsBtn.setAction(settingAction);
        settingsBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/setting24.png"))); // NOI18N
        settingsBtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 5));
        settingsBtn.setFocusable(false);
        settingsBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        settingsBtn.setOpaque(false);
        settingsBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        topBar.add(settingsBtn);

        about.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/about24.png"))); // NOI18N
        about.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 5));
        about.setFocusable(false);
        about.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        about.setOpaque(false);
        about.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        about.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutActionPerformed(evt);
            }
        });
        topBar.add(about);
        topBar.add(jSeparator1);

        fullScreenButton.setAction(switchFullScreenAction);
        fullScreenButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/fullScreen24.png"))); // NOI18N
        fullScreenButton.setToolTipText(bundle.getString("MainApp.fullScreenButton.toolTipText")); // NOI18N
        fullScreenButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 5));
        fullScreenButton.setFocusable(false);
        fullScreenButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fullScreenButton.setOpaque(false);
        fullScreenButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        topBar.add(fullScreenButton);

        minimizeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/minimize.png"))); // NOI18N
        minimizeButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 5));
        minimizeButton.setFocusable(false);
        minimizeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        minimizeButton.setOpaque(false);
        minimizeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        minimizeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minimizeButtonActionPerformed(evt);
            }
        });
        topBar.add(minimizeButton);

        exitButton.setAction(exitAction);
        exitButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/quit24.png"))); // NOI18N
        exitButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 5));
        exitButton.setFocusable(false);
        exitButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        exitButton.setOpaque(false);
        exitButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        topBar.add(exitButton);

        rootPanel.add(topBar, java.awt.BorderLayout.NORTH);

        centerPanel.setLayout(new java.awt.BorderLayout());

        mainTabbedPane.setFocusable(false);
        mainTabbedPane.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        mainTabbedPane.setRequestFocusEnabled(false);
        mainTabbedPane.addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentAdded(java.awt.event.ContainerEvent evt) {
                mainTabbedPaneComponentAdded(evt);
            }
        });
        mainTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                mainTabbedPaneStateChanged(evt);
            }
        });

        imagePanel1.setImgLocationPath(bundle.getString("MainApp.imagePanel1.imgLocationPath")); // NOI18N

        javax.swing.GroupLayout imagePanel1Layout = new javax.swing.GroupLayout(imagePanel1);
        imagePanel1.setLayout(imagePanel1Layout);
        imagePanel1Layout.setHorizontalGroup(
            imagePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1463, Short.MAX_VALUE)
        );
        imagePanel1Layout.setVerticalGroup(
            imagePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 680, Short.MAX_VALUE)
        );

        homeTab.setViewportView(imagePanel1);

        mainTabbedPane.addTab(bundle.getString("MainApp.homeTab.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/res/icons/tajer24.png")), homeTab); // NOI18N

        centerPanel.add(mainTabbedPane, java.awt.BorderLayout.PAGE_START);

        rootPanel.add(centerPanel, java.awt.BorderLayout.CENTER);

        statusBar.setFloatable(false);
        statusBar.setRollover(true);

        lockButton.setAction(lockAction);
        lockButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/actions/lock24.png"))); // NOI18N
        lockButton.setBorder(null);
        lockButton.setFocusable(false);
        lockButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lockButton.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        statusBar.add(lockButton);

        statusPanel.setLayout(new java.awt.GridBagLayout());

        uLabel.setFont(new java.awt.Font("Square721 BT", 1, 14)); // NOI18N
        uLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        uLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/user16.png"))); // NOI18N
        uLabel.setText(bundle.getString("MainApp.uLabel.text")); // NOI18N
        uLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 1));
        statusPanel.add(uLabel, new java.awt.GridBagConstraints());

        userLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        userLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        userLabel.setText(bundle.getString("MainApp.userLabel.text")); // NOI18N
        userLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        statusPanel.add(userLabel, gridBagConstraints);
        statusPanel.add(filler1, new java.awt.GridBagConstraints());

        dateLabel.setFont(new java.awt.Font("Square721 BT", 1, 14)); // NOI18N
        dateLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        dateLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/calendar16.png"))); // NOI18N
        dateLabel.setText(bundle.getString("MainApp.dateLabel.text")); // NOI18N
        dateLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        statusPanel.add(dateLabel, new java.awt.GridBagConstraints());
        statusPanel.add(filler3, new java.awt.GridBagConstraints());

        timeLabel.setFont(new java.awt.Font("Square721 BT", 1, 14)); // NOI18N
        timeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        timeLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/clock16.png"))); // NOI18N
        timeLabel.setText(bundle.getString("MainApp.timeLabel.text")); // NOI18N
        timeLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        statusPanel.add(timeLabel, new java.awt.GridBagConstraints());
        statusPanel.add(filler4, new java.awt.GridBagConstraints());

        langLabel.setFont(new java.awt.Font("Square721 BT", 1, 14)); // NOI18N
        langLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        langLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/keyboard16.png"))); // NOI18N
        langLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        statusPanel.add(langLabel, new java.awt.GridBagConstraints());
        statusPanel.add(filler6, new java.awt.GridBagConstraints());

        numLockLabel.setFont(new java.awt.Font("Square721 BT", 1, 14)); // NOI18N
        numLockLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        numLockLabel.setText(bundle.getString("MainApp.numLockLabel.text")); // NOI18N
        numLockLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        statusPanel.add(numLockLabel, new java.awt.GridBagConstraints());
        statusPanel.add(filler5, new java.awt.GridBagConstraints());

        capsLockLabel.setFont(new java.awt.Font("Square721 BT", 1, 14)); // NOI18N
        capsLockLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        capsLockLabel.setText(bundle.getString("MainApp.capsLockLabel.text")); // NOI18N
        capsLockLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        statusPanel.add(capsLockLabel, new java.awt.GridBagConstraints());
        statusPanel.add(filler2, new java.awt.GridBagConstraints());

        initStateL.setFont(new java.awt.Font("Square721 BT", 1, 14)); // NOI18N
        initStateL.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        initStateL.setText(bundle.getString("MainApp.initStateL.text")); // NOI18N
        initStateL.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 3));
        initStateL.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        statusPanel.add(initStateL, gridBagConstraints);

        statusBar.add(statusPanel);

        rootPanel.add(statusBar, java.awt.BorderLayout.SOUTH);

        getContentPane().add(rootPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void minimizeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minimizeButtonActionPerformed
        setExtendedState(Frame.ICONIFIED);
    }//GEN-LAST:event_minimizeButtonActionPerformed
    private void formWindowDeiconified(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowDeiconified
        setExtendedState(Frame.MAXIMIZED_BOTH);
    }//GEN-LAST:event_formWindowDeiconified
    private void aboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutActionPerformed
        AboutDialog.showDialog();
    }//GEN-LAST:event_aboutActionPerformed
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        exit();
    }//GEN-LAST:event_formWindowClosing

    private void mainTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_mainTabbedPaneStateChanged
        if (mainTabbedPane.getSelectedComponent() instanceof MasterDetailsPanel) {
            ((MasterDetailsPanel) mainTabbedPane.getSelectedComponent()).reload();
            ((MasterDetailsPanel) mainTabbedPane.getSelectedComponent()).doFilter();
        } else {
            if (mainTabbedPane.getSelectedComponent() instanceof CRUDPanel) {
                ((CRUDPanel) mainTabbedPane.getSelectedComponent()).reload();
                ((CRUDPanel) mainTabbedPane.getSelectedComponent()).doFilter();
            }
        }
    }//GEN-LAST:event_mainTabbedPaneStateChanged

    private void mainTabbedPaneComponentAdded(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_mainTabbedPaneComponentAdded

    }//GEN-LAST:event_mainTabbedPaneComponentAdded

    private void connSettingsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connSettingsBtnActionPerformed
        DBManager.getInstance().configServer();
    }//GEN-LAST:event_connSettingsBtnActionPerformed

    private void produitBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_produitBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_produitBtnActionPerformed

    private void fournissBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fournissBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fournissBtnActionPerformed

    private void gestProdBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gestProdBtnActionPerformed
        popupMenu.showMenu(gestProduitsP, gestProdBtn);
    }//GEN-LAST:event_gestProdBtnActionPerformed

    private void clientsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clientsBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_clientsBtnActionPerformed

    private void homeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_homeButtonActionPerformed

    }//GEN-LAST:event_homeButtonActionPerformed

    private void gestVentesBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gestVentesBtnActionPerformed
        if(currentUser.isAdmin()){
            popupMenu.showMenu(gestVentesP, gestVentesBtn);
        }else{
            venteComptoir();
        }
    }//GEN-LAST:event_gestVentesBtnActionPerformed

    private void gestStockBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gestStockBtnActionPerformed
        popupMenu.showMenu(gestStockP, gestStockBtn);
    }//GEN-LAST:event_gestStockBtnActionPerformed

    private void gestAchatBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gestAchatBtnActionPerformed
        popupMenu.showMenu(gestAchP, gestAchatBtn);
    }//GEN-LAST:event_gestAchatBtnActionPerformed

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String args[]) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        if (args.length > 0) {
            File archive = new File(args[0]);
            if (archive.isFile()) {
                if (archive.getName().endsWith("tjr")) {
                    ZipFile archiveZip = new ZipFile(archive);
                    RestoreDBDialog rd = RestoreDBDialog.getInstance();
                    rd.setArchiveFile(archiveZip);
                    rd.setVisible(true);
                }
            }
        } else {
            MainApp.getInstance().startApp();
            //MainApp.
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton about;
    private javax.swing.JButton achatButton;
    private javax.swing.JButton achatButton3;
    private javax.swing.JButton achatButton4;
    private javax.swing.JButton achatButton5;
    private javax.swing.JLabel bismillahLabel;
    private javax.swing.JButton calcButton;
    private javax.swing.JLabel capsLockLabel;
    private javax.swing.JButton categBtn;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JButton clientsBtn;
    private javax.swing.JButton connSettingsBtn;
    private javax.swing.JButton creditClButton;
    private javax.swing.JLabel dateLabel;
    private javax.swing.JButton depotBtn;
    private javax.swing.JButton exitButton;
    private javax.swing.JButton familleBtn;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.JButton fournissBtn;
    private javax.swing.JButton fullScreenButton;
    private myComponents.MyJPanel gestAchP;
    private javax.swing.JButton gestAchatBtn;
    private javax.swing.JButton gestProdBtn;
    private javax.swing.JPanel gestProduitsP;
    private javax.swing.JButton gestStockBtn;
    private myComponents.MyJPanel gestStockP;
    private javax.swing.JButton gestVentesBtn;
    private javax.swing.JPanel gestVentesP;
    private javax.swing.JButton homeButton;
    private javax.swing.JScrollPane homeTab;
    private myComponents.ImagePanel imagePanel1;
    private javax.swing.JLabel initStateL;
    private javax.swing.JButton inventaireBtn;
    private javax.swing.JButton inventaireBtn1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JLabel langLabel;
    private javax.swing.JButton lockButton;
    private static javax.swing.JTabbedPane mainTabbedPane;
    private javax.swing.JButton minimizeButton;
    private myComponents.MyJPanel myJPanel1;
    private javax.swing.JLabel numLockLabel;
    private javax.swing.JButton produitBtn;
    private static javax.swing.JButton recetteButton;
    private javax.swing.JButton regAchatButton;
    private javax.swing.JButton regClButton;
    private myComponents.MyJPanel rootPanel;
    private javax.swing.JButton settingsBtn;
    private javax.swing.JToolBar statusBar;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JButton stockBtn;
    private javax.swing.JLabel timeLabel;
    private javax.swing.JToolBar toolsTB;
    private javax.swing.JToolBar topBar;
    private javax.swing.JLabel uLabel;
    private javax.swing.JButton uniteBtn;
    private javax.swing.JLabel userLabel;
    private javax.swing.JButton usersBtn;
    private javax.swing.JButton usersButton;
    private javax.swing.JButton usersButton1;
    private javax.swing.JButton venteBtn;
    private javax.swing.JButton venteButton3;
    private javax.swing.JButton venteButton4;
    private javax.swing.JButton venteButton6;
    private javax.swing.JButton venteButton8;
    private javax.swing.JButton venteButton9;
    private javax.swing.JButton vntComptoirBtn;
    // End of variables declaration//GEN-END:variables
}
