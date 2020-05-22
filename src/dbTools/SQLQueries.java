package dbTools;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author alilo
 */
public class SQLQueries {

    //les commandes SQL pour l'activation de la protection de la base de données
    public static final String setProperty = "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY";
    public static final String getProperty = "VALUES SYSCS_UTIL.SYSCS_GET_DATABASE_PROPERTY";
    //to enable user authentication
    public static final String enableRequireAuth = "('derby.connection.requireAuthentication','true')";
    //set the default connection mode
    public static final String defaultConnMode = "('derby.database.defaultConnectionMode','noAccess')";
    //set the authorized users
    public static final String fullAccessUsers = "('derby.database.fullAccessUsers',";
    public static final String readOnlyAccessUsers = "('derby.database.readOnlyAccessUsers','guest')";
    // Set authentication scheme to Derby builtin
    public static final String setProviderBUILTIN = "('derby.authentication.provider','BUILTIN')";
    // We would set the following property to TRUE only when we were
    // ready to deploy. Setting it to FALSE means that we can always
    // override using system properties if we accidentally paint
    // ourselves into a corner.
    public static final String dbPropertiesOnly = "('derby.database.propertiesOnly', 'true')";
    
}
