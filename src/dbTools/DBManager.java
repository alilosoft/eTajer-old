package dbTools;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.logging.Level;
import java.util.prefs.Preferences;
import java.util.zip.ZipFile;
import javax.swing.JOptionPane;
import org.apache.derby.drda.NetworkServerControl;
import sql.QueryReader;
import tools.ExceptionReporting;
import tools.FileTools;
import tools.MessageReporting;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 * 21:13.
 *
 * @author alilo
 */
public class DBManager {

    private final Preferences userConfig;
    private final DBSettings settings = new DBSettings();
    private static DBManager instance;

    private NetworkServerControl serverControl;
    private Properties engineConfig = new Properties();
    private Properties defaultConnSettings = new Properties();
    // application mode
    public static final String MONO_POST = "Mono-Post";
    public static final String CLIENT_SRV = "Client/Server";
    private String appMode = DBManager.MONO_POST;
    //engine type
    private static final String NETWORK = "NET";
    private static final String EMBEDDED = "EMB";
    //jdbc driver name
    private String netDriverName = "org.apache.derby.jdbc.ClientDriver";
    private String embDriverName = "org.apache.derby.jdbc.EmbeddedDriver";
    //the database URL_Prefix for Client/Server connection
    private String urlPrefix = "jdbc:derby:";

    public static final String LOCAL = "Local";
    public static final String DISTANT = "Network";
    private String hostType = LOCAL;
    private String localHost = "localhost";
    private String hostName = "localhost";
    private String port = "1527";
    private String networkUrlPrefix;
    //the database URL_Prefix for Embedded Database
    private String embeddedUrlPrefix;
    //the database location
    private String dbPath = ".";
    //the database name
    private String dbName = "db";
    //the database URL
    private String dbURL;
    //specifiy the connection type to use Server or embedded
    private String engineType = DBManager.NETWORK;
    //Connection attributs
    private String userName = "alilo";
    private String password = "alilo";
    private String backupDir = "./Backup";
    private Connection defConnection = null;
    private String[] updateDBQueries;
    private Statement updateDBStmnt;
    private boolean restore = false;
    private String restorPath;
    private boolean update = false;
    private String sqlUpdateFile;

    private DBManager() {
        userConfig = Preferences.userNodeForPackage(getClass());
        try {
            localHost = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            ExceptionReporting.showException(ex);
        }
    }

    public static DBManager getInstance() {
        if (instance == null) {
            instance = new DBManager();
            instance.initialize();
        }
        return instance;
    }

    public static DBManager getInstance(Properties engineConfig, Properties connectionProps) {
        if (instance == null) {
            instance = new DBManager();
            instance.initialize(engineConfig, connectionProps);
        }
        return instance;
    }

    public void configServer() {
        settings.showSettings();
        //initialize();
    }

    public void initialize(Properties engineConfig, Properties connectionProps) {
        this.engineConfig = engineConfig;
        this.defaultConnSettings = connectionProps;
        initialize();
    }

    // Class initialization.
    public void initialize() {
        URL urlEngineConfig = ClassLoader.getSystemResource("dbTools/Config.properties");
        URL urlDBProps = ClassLoader.getSystemResource("dbTools/DB_Props.properties");

        if (urlEngineConfig != null) {
            try {
                engineConfig.load(urlEngineConfig.openStream());
                //System.out.println("Engine_Config: " + engineConfig);
            } catch (IOException ex) {
                ExceptionReporting.showException(ex);
                System.exit(0);
            }
        } else {
            MessageReporting.showMessage(Level.SEVERE, getClass(), "intialize()", "DB Engine Config file not found!");
            System.exit(0);
        }
        // reading db engine config!
        netDriverName = engineConfig.getProperty("NET_DRIVER_NAME", netDriverName);
        embDriverName = engineConfig.getProperty("EMB_DRIVER_NAME", netDriverName);
        urlPrefix = engineConfig.getProperty("DB_URL_PREFIX", urlPrefix);
        engineType = engineConfig.getProperty("ENGINE_MODE", engineType);

        if (urlDBProps != null) {
            try {
                defaultConnSettings.load(urlDBProps.openStream());
                //System.out.println("Connection_Props: " + connectionProps);
                hostName = defaultConnSettings.getProperty("HOST_NAME", localHost);
                port = defaultConnSettings.getProperty("PORT", port);
                dbPath = defaultConnSettings.getProperty("DB_PATH", dbPath);
                dbName = defaultConnSettings.getProperty("DB_NAME", dbName);
            } catch (IOException ex) {
                ExceptionReporting.showException(ex);
                //System.exit(0);
            }
        } else {
            MessageReporting.showMessage(Level.SEVERE, getClass(), "intialize()", "DB Connection settings file not found!");
            //System.exit(0);
        }
        appMode = userConfig.get("APP_MODE", appMode);
        
        hostType = userConfig.get("HOST_TYPE", hostType);
        if (hostType.equals(LOCAL)) {
            hostName = localHost;
        } else {
            hostName = userConfig.get("HOST", localHost);
        }
        port = userConfig.get("PORT", port);
        dbName = userConfig.get("DB_NAME", dbName);

        //userConfig.put("DB_PATH", ".");
        dbPath = userConfig.get("DB_PATH", dbPath);
       
        String derbyHome;
        if (engineType.equalsIgnoreCase(NETWORK)) {
            MessageReporting.logOnly(Level.INFO, "Derby Engine Mode: Network");
            // if this app mode is server then start the derby server.
            if (appMode.equalsIgnoreCase(MONO_POST)) {
                MessageReporting.logOnly(Level.INFO, "Application running mode: Server");
                try {
                    System.setProperty("derby.system.home", dbPath);
                    startServer();
                    derbyHome = serverControl.getCurrentProperties().getProperty("derby.drda.traceDirectory");
                    if (derbyHome != null && !derbyHome.equals(dbPath)) {
                        System.setProperty("derby.system.home", derbyHome);
                    }
                } catch (Exception ex) {
                    MessageReporting.showMessage(Level.SEVERE, getClass(), "intialize()", "Starting Server...Failed!");
                    ExceptionReporting.showException(ex);
                }
            } else {
                MessageReporting.logOnly(Level.INFO, "Application running mode: Client");
                try {
                    serverControl = new NetworkServerControl(InetAddress.getByName(hostName), Integer.valueOf(port));
                    serverControl.ping();
                    //derbyHome = serverControl.getCurrentProperties().getProperty("derby.drda.traceDirectory");
                } catch (Exception ex) {
                    String mess = "Impossible de connecter au serveur de base de donnée à l'adresse: " + hostName + ":" + port + "\n"
                            + "Vérifier que le serveur de base de donnée est en cours d'exécution et que vous ête connecter a ce serveur!\n"
                            + "Si la connexion réseau est ok!, vérifier les paramètres du connexion!\n"
                            + "Voullez vous vérifier les paramètres et ressayer?";
                    int rep = JOptionPane.showConfirmDialog(null, mess, "Erreur de connexion", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                    if (rep == JOptionPane.YES_OPTION) {
                        configServer();
                        System.exit(0);
                        return;
                    } else {
                        ExceptionReporting.showException(ex);
                        System.exit(0);
                    }
                }
            }

            try {
                Class.forName(netDriverName).newInstance();
                MessageReporting.logOnly(Level.INFO, "Loading JDBC Driver...OK!");
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                MessageReporting.showMessage(Level.SEVERE, getClass(), "intialize()", "Loading JDBC Driver...Fail!");
                ExceptionReporting.showException(ex);
                System.exit(0);
            }
            // Building the jdbc url corresponding to client/server mode connection.
            dbURL = urlPrefix + "//" + hostName + ":" + port + "/" + dbName;
        } else {
            if (engineType.equalsIgnoreCase(EMBEDDED)) {
                MessageReporting.logOnly(Level.INFO, "Server Mode: Embedded");
                System.setProperty("derby.system.home", dbPath);
                try {
                    Class.forName(embDriverName);
                    MessageReporting.logOnly(Level.INFO, "Loading JDBC Embedded Driver...OK!");
                } catch (ClassNotFoundException ex) {
                    ExceptionReporting.showException(ex);
                    System.exit(0);
                }
                // Building the jdbc url corresponding to embedded mode connection.
                dbURL = urlPrefix + dbPath + File.separator + dbName;
            }
        }
        MessageReporting.logOnly(Level.CONFIG, "Derby Home: " + System.getProperty("derby.system.home"));
        MessageReporting.logOnly(Level.CONFIG, "Connection URL: " + dbURL);
        // preparing restoration
        if (isRestore()) {
            restorDB(getRestorPath());
            setRestore(false);
        }

        if (isUpdate()) {
            updateDB(getSqlUpdateFile());
            setUpdate(false);
        }
    }

    public void startServer() {
        try {
            serverControl = new NetworkServerControl(InetAddress.getByName(hostName), Integer.valueOf(port));
            PrintWriter consoleWriter = new PrintWriter(System.out, true);
            serverControl.start(consoleWriter);
            MessageReporting.logOnly(Level.INFO, "Server started...OK!");
        } catch (UnknownHostException ex) {
            ExceptionReporting.showException(ex);
            System.exit(0);
        } catch (Exception ex) {
            ExceptionReporting.showException(ex);
            System.exit(0);
        }
    }

    public void stopServer() {
        try {
            serverControl.shutdown();
        } catch (Exception ex) {
            ExceptionReporting.showException(ex);
            System.exit(0);
        }
    }

    public void setAppMode(String appMode) {
        this.appMode = appMode;
        userConfig.put("APP_MODE", appMode);
    }

    public String getAppMode() {
        return appMode;
    }
    
    

    public void setHostType(String hostType) {
        this.hostType = hostType;
        userConfig.put("HOST_TYPE", hostType);
    }

    public String getHostType() {
        return hostType;
    }

    public String getLocalHost() {
        return localHost;
    }

    public void setHostName(String host) {
        this.hostName = host;
        userConfig.put("HOST", host);
    }

    public String getHostName() {
        return hostName;
    }

    public void setPort(String port) {
        this.port = port;
        userConfig.put("PORT", port);
    }

    public String getPort() {
        return port;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
        userConfig.put("DB_NAME", dbName);
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbLocPath(String dbPath) {
        this.dbPath = dbPath;
        userConfig.put("DB_PATH", dbPath);
    }

    public String getDbLocPath() {
        dbPath = userConfig.get("DB_PATH", dbPath);
        try {
            dbPath = new File(dbPath).getCanonicalPath();
        } catch (IOException ex) {
            ExceptionReporting.showException(ex);
        }
        return dbPath;
    }

    public void setBackupDir(String backupDir) {
        this.backupDir = backupDir;
        userConfig.put("BACKUP_DIR", backupDir);
    }

    public String getBackupDir() {
        backupDir = userConfig.get("BACKUP_DIR", backupDir);
        File dir = new File(backupDir);
        dir.setWritable(true);
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                JOptionPane.showMessageDialog(null, "L'emplacement: " + dir + " n'exist pas!", "Erreur", JOptionPane.ERROR_MESSAGE);
                return ".";
            }
        }
        if (!dir.canWrite()) {
            JOptionPane.showMessageDialog(null, "L'emplacement: " + dir + " est en lecteur seul!", "Erreur", JOptionPane.ERROR_MESSAGE);
            return ".";
        }
        return backupDir;
    }

    public void setRestore(boolean restore) {
        this.restore = restore;
        userConfig.putBoolean("RESTORE", restore);
    }

    public boolean isRestore() {
        restore = userConfig.getBoolean("RESTORE", false);
        return restore;
    }

    public void setRestorePath(String tempDB) {
        this.restorPath = tempDB;
        userConfig.put("RESTOR_PATH", tempDB);
    }

    public String getRestorPath() {
        String defPath = userConfig.get("BACKUP_DIR", backupDir);
        restorPath = userConfig.get("RESTOR_PATH", defPath);
        File dir = new File(restorPath);
        if (!dir.exists()) {
            JOptionPane.showMessageDialog(null, "L'emplacement: " + dir + " n'exist pas!", "Erreur", JOptionPane.ERROR_MESSAGE);
            return ".";
        }
        return restorPath;
    }

    public void setUpdate(boolean update) {
        this.update = update;
        userConfig.putBoolean("UPDATE", update);
    }

    public boolean isUpdate() {
        update = userConfig.getBoolean("UPDATE", false);
        return update;
    }

    public void setSqlUpdateFile(String sqlUpdateFile) {
        this.sqlUpdateFile = sqlUpdateFile;
        userConfig.put("UPDATE_FILE", sqlUpdateFile);
    }

    public String getSqlUpdateFile() {
        sqlUpdateFile = userConfig.get("UPDATE_FILE", UPDATE_FILE);
        return sqlUpdateFile;
    }

    public boolean isLocalHost() {
        boolean isLocal;
        try {
            isLocal = (InetAddress.getLocalHost().getHostAddress().equals(InetAddress.getByName(hostName).getHostAddress()));
            //System.out.println("localHost:"+ InetAddress.getLocalHost().getHostAddress()+"/ name: "+ InetAddress.getLocalHost().getHostName());
            //System.out.println("host:"+ InetAddress.getByName(hostServer).getHostAddress()+"/ name: "+ InetAddress.getByName(hostServer).getHostName());
        } catch (UnknownHostException ex) {
            //ExceptionReporting.showException(ex);
            isLocal = false;
        }
        return isLocal;
    }

    /**
     * Attempts to establish a connection to the db using given Authentication
     * informations.
     *
     * @param user
     * @param pw
     * @return
     */
    public Connection getConnection(String user, String pw) {
        if (user == null) {
            user = this.userName;
        }
        if (pw == null) {
            pw = this.password;
        }

        try {
            Connection conn = DriverManager.getConnection(dbURL, user, pw);
            MessageReporting.logOnly(Level.INFO, "New Connection to: " + dbURL + "...OK!");
            return conn;
        } catch (SQLException ex) {
            String mess = "La connection au base de données " + dbURL + " a été échouée!\n"
                    + "Voullez vous vérifier les paramètres du connexion et ressayer?";
            int rep = JOptionPane.showConfirmDialog(null, mess, "Erreur de connexion", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
            if (rep == JOptionPane.YES_OPTION) {
                configServer();
                System.exit(0);
                //return getConnection();
            } else {
                ExceptionReporting.showException(ex);
                System.exit(0);
            }
            return null;
        }
    }

    /**
     * Return the default connection with default user & password & auto-commit
     * set to true!
     *
     * @return
     */
    public Connection getDefaultConnection() {
        if (defConnection == null) {
            defConnection = getConnection(null, null);
        }
        return defConnection;
    }

    /**
     * Create new connection to the data base.
     *
     * @return
     */
    public Connection getNewConnection() {
        return getConnection(null, null);
    }

    /**
     * Create new connection to the data base using given user & pw.
     *
     * @param user
     * @param pw
     * @return
     */
    public Connection getNewConnection(String user, String pw) {
        return getConnection(user, pw);
    }

    /**
     * Return new connection to the database with auto-commit property set to
     * false<br> used to manage operation with possible roll-backs
     *
     * @param user
     * @param pw
     * @return
     */
    public Connection getNonAutoCommitedConnection(String user, String pw) {
        Connection c = null;
        try {
            // Create new connection //
            c = getConnection(user, pw);
            MessageReporting.logOnly(Level.INFO, "New Connection...OK!");
            c.setAutoCommit(false);
            MessageReporting.logOnly(Level.INFO, "Disable autocommit...OK!");
            c.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            MessageReporting.logOnly(Level.INFO, "Set transaction isolation level to 'READ_UNCOMMITTED'...OK!");
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
        }
        return c;
    }

    public Connection getNonAutoCommitedConnection() {
        return getNonAutoCommitedConnection(null, null);
    }

    public boolean shutDownDB() {
        boolean connectionClosed = false;
        /**
         * Shutting down the database! copied from derby start guide: Important:
         * The XJ015 error (successful shutdown of the Derby engine) and the
         * 08006 error (successful shutdown of a single database) are among the
         * few exceptions thrown by Derby that indicate that an operation
         * succeeded. Most other exceptions indicate that an operation failed.
         * You should check the log file to be certain.*
         */
        try {
            if (defConnection != null) {
                defConnection.commit();
                defConnection.close();
            }
            DriverManager.getConnection(dbURL + ";shutdown=true", userName, password);
        } catch (SQLException ex) {
            if (ex.getSQLState().equals("08006")) {
                connectionClosed = true;
                MessageReporting.logOnly(Level.INFO, "Shutting down db: " + dbName + " OK!");
            } else {
                connectionClosed = false;
                ExceptionReporting.showException(ex);
            }
        }
        return connectionClosed;
    }

    public boolean shutDownDerby() {
        boolean engineOff = false;
        /**
         * shutting down derby system copied from derby start guide: Important:
         * The XJ015 error (successful shutdown of the Derby engine) and the
         * 08006 error (successful shutdown of a single database) are among the
         * few exceptions thrown by Derby that indicate that an operation
         * succeeded. Most other exceptions indicate that an operation failed.
         * You should check the log file to be certain.*
         */
        try {
            DriverManager.getConnection("jdbc:derby:" + ";shutdown=true");
        } catch (SQLException ex) {
            if (ex.getSQLState().equals("XJ015")) {
                engineOff = true;
                MessageReporting.logOnly(Level.INFO, "Derby engine turned OFF!");
            } else {
                ExceptionReporting.showException(ex);
            }
        }
        return engineOff;
    }

    public void protectDataBase(Connection conn) {
        try {
            Statement stm = conn.createStatement();
            MessageReporting.logOnly(Level.CONFIG, "Enabeling authentication...");
            stm.executeUpdate(SQLQueries.setProperty + SQLQueries.enableRequireAuth);
            MessageReporting.logOnly(Level.CONFIG, "Seting up security provider...");
            stm.executeUpdate(SQLQueries.setProperty + SQLQueries.setProviderBUILTIN);
            MessageReporting.logOnly(Level.CONFIG, "Adding users...");
            stm.executeUpdate(SQLQueries.setProperty + "('derby.user." + userName + "','" + password + "')");
            stm.executeUpdate(SQLQueries.setProperty + "('derby.user.guest','')");
            //specifing the default access mode
            MessageReporting.logOnly(Level.CONFIG, "Set up default access mode...");
            stm.executeUpdate(SQLQueries.setProperty + SQLQueries.defaultConnMode);
            //specifing the full access users
            MessageReporting.logOnly(Level.CONFIG, "Set up the full access users");
            stm.executeUpdate(SQLQueries.setProperty + SQLQueries.fullAccessUsers + "'" + userName + "')");
            stm.executeUpdate(SQLQueries.setProperty + SQLQueries.readOnlyAccessUsers);
            //go to devguid for more details on protecting db properties
            MessageReporting.logOnly(Level.CONFIG, "Set up dbProperties only...");
            stm.executeUpdate(SQLQueries.setProperty + SQLQueries.dbPropertiesOnly);
        } catch (SQLException ex) {
            ExceptionReporting.showException(ex);
        }
    }

    public static final String UPDATE_FILE = "./Update.sql";

    public void updateDB(String sqlFile) {
        if (sqlFile == null) {
            sqlFile = getSqlUpdateFile();
        }
        Connection conn = getNonAutoCommitedConnection();
        try {
            updateDBStmnt = conn.createStatement();
            updateDBQueries = QueryReader.getQueries(sqlFile, true);
            for (String qry : updateDBQueries) {
                System.out.println("Update Query: " + qry);
                if (qry.trim().equalsIgnoreCase("DONE")) {
                    break;
                } else {
                    if (qry.startsWith("--")) { // ignore comment lines
                        continue;
                    }
                    updateDBStmnt.addBatch(qry);
                }
            }
            updateDBStmnt.executeBatch();
            conn.commit();
            MessageReporting.logOnly(Level.CONFIG, "Updating DB done!");
        } catch (SQLException ex) {
            try {
                conn.rollback();
            } catch (SQLException ex1) {
                //ExceptionReporting.showException(ex1);
            }
            ExceptionReporting.showException(ex);
        }
    }

    /**
     * Backing up the db to the spécified location. if the path a default path
     * will be used.
     *
     * @param path
     */
    public void backupDB(String path) {
        String date = new SimpleDateFormat("dd.MM.yyyy@hh.mm").format(new java.util.Date());
        String backUpDir;
        if (path == null) {
            backUpDir = getBackupDir();
        } else {
            backUpDir = path;
        }
        try {
            File dir = new File(backUpDir);
            if (!dir.canWrite()) {
                JOptionPane.showMessageDialog(null, "L'emplacement: " + dir + " est en lecteur seul!", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            backUpDir = dir.getCanonicalPath();
            setBackupDir(backUpDir);
        } catch (IOException ex) {
            ExceptionReporting.showException(ex);
        }
        backUpDir += "/" + date;
        CallableStatement cs;
        try {
            cs = getDefaultConnection().prepareCall("CALL SYSCS_UTIL.SYSCS_BACKUP_DATABASE(?)");
            cs.setString(1, backUpDir);
            MessageReporting.logOnly(Level.INFO, "Backing up to: " + backUpDir + "...Start");
            cs.execute();
            MessageReporting.logOnly(Level.INFO, "Backing up to: " + backUpDir + "...Finished");
            cs.close();
            try {
                String zipFileName = backUpDir + ".tjr";
                MessageReporting.logOnly(Level.INFO, "Zipping up to: " + zipFileName + "...Start");
                FileTools.zipDir(backUpDir, zipFileName, true);
                MessageReporting.logOnly(Level.INFO, "Zipping up to: " + zipFileName + "...Finished");
            } catch (IOException ex) {
                ExceptionReporting.showException(ex);
            }
        } catch (SQLException ex) {
            MessageReporting.showMessage(Level.SEVERE, DBManager.class, "backupDataBase(loc)", "Sauvgarde des données échoué", new Object[]{backUpDir});
            ExceptionReporting.showException(ex);
        }
    }

    public boolean restorDB(String bakupPath) {
        String restorURL = dbURL + ";restoreFrom=" + bakupPath;
        try {
            shutDownDB();// release lockes
            MessageReporting.logOnly(Level.INFO, "Restoring DB from: " + bakupPath + "...Start!");
            defConnection = DriverManager.getConnection(restorURL, userName, password);
            MessageReporting.logOnly(Level.INFO, "Restoring DB from: " + bakupPath + "...OK!");
            FileTools.deleteDir(new File(bakupPath).getParentFile());
            return true;
        } catch (SQLException ex) {
            String mess = "La réstauration de la base de données a été échouée!\nURL: " + dbURL
                    + "\nVoullez vous vérifier les paramètres du connexion et ressayer?";
            int rep = JOptionPane.showConfirmDialog(null, mess, "Erreur de connexion", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
            if (rep == JOptionPane.YES_OPTION) {
                configServer();
                System.exit(0);
            } else {
                ExceptionReporting.showException(ex);
                System.exit(0);
            }
            return false;
        }
    }

    public void importBackup(ZipFile backup) {
        File tmpBackupDir = new File("./temp");
        try {
            MessageReporting.logOnly(Level.INFO, "Extracting backup into: " + tmpBackupDir + "...Start");
            FileTools.unzipIntoDirectory(backup, tmpBackupDir);
            MessageReporting.logOnly(Level.INFO, "Extracting backup...Finished");
            File tmpBackup = tmpBackupDir.listFiles()[0];
            String mess = "L'application doit être arrêter pour terminer la réstauration!\n"
                    + "Si-non elle sera éffectuer a la prochaine démarrage.\n"
                    + "Voullez vous continuer la réstauration et arrêter l'application?";
            int rep = JOptionPane.showConfirmDialog(null, mess, "Info", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (rep == JOptionPane.YES_OPTION) {
                if (restorDB(tmpBackup.getCanonicalPath())) {
                    System.exit(0);
                }
            } else {
                setRestore(true);
                setRestorePath(tmpBackup.getCanonicalPath());
            }
        } catch (IOException ex) {
            ExceptionReporting.showException(ex);
        }
    }

    public static void main(String args[]) throws IOException {
        //DBManager.getInstance().updateDB(null);
        //DBManager.getInstance().isLocalHost();
        DBManager.getInstance().configServer();
        DBManager.getInstance().getDefaultConnection();
    }
}
