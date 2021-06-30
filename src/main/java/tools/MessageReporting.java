package tools;

import dialogs.MessageReportingDialog;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author alilo
 */
public class MessageReporting {

    private static MessageReporting reporting;
    public static final int NORMAL_MESSAGE = 0;
    public static final int INFO_MESSAGE = 1;
    public static final int WARNING_MESSAGE = 2;
    public static final int ERROR_MESSAGE = 3;
    private static final Logger logger = Logger.getLogger(MessageReporting.class.getName());
    //private static final Logger infoLogger = Logger.getLogger(MessageReporting.class.getName()+".infos");
    //private static final Logger warningLogger = Logger.getLogger(MessageReporting.class.getName()+".warnings");
    //private static final Logger errorLogger = Logger.getLogger(MessageReporting.class.getName()+".errors");
    private FileHandler fileHandler;
    //private FileHandler infoHandler;
    //private FileHandler warningHandler;
    //private FileHandler errorHandler;
    private String logDirPath = "log";

    public MessageReporting() {
        File logDir = new File(logDirPath);
        if (!logDir.exists()) {
            logDir.mkdir();
            logDir.setWritable(true);
        }
        try {
            LogManager.getLogManager().readConfiguration(getClass().getResourceAsStream("LoggerProperties.properties"));
        } catch (IOException | SecurityException ex) {
            String mess = "Erreur lors du configuration du logger\n" + ex.toString() + "\nClass: " + getClass().getName();
            MessageReportingDialog.showMessage(mess, ERROR_MESSAGE);
            System.exit(0);
        }

        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        try {
            fileHandler = new FileHandler(logDirPath + File.separator + "Messages_" + date + ".log", true);
        } catch (IOException | SecurityException ex) {
            String mess = "Erreur lor de l'initialisation du MessageReporting\n" + ex.toString() + "\nClass: " + getClass().getName();
            MessageReportingDialog.showMessage(mess, ERROR_MESSAGE);
            System.exit(0);
        }
        logger.addHandler(fileHandler);

        //System.out.println("logger: " + logger.getName() + " level: " + logger.getLevel());
        //System.out.println("fileHandeler: " + fileHandler.getFormatter() + " level: " + fileHandler.getLevel());
    }

    synchronized public static void logOnly(Level level, String mess) {
        if (reporting == null) {
            reporting = new MessageReporting();
        }
        logger.log(level, mess);
    }

    synchronized public static void showMessage(Level level, java.lang.Class srcClass, String srcMethod, String mess) {
        if (reporting == null) {
            reporting = new MessageReporting();
        }
        showMessage(level, srcClass, srcMethod, mess, null);
    }

    synchronized public static void showMessage(Level level, Class srcClass, String srcMethod, String mess, Object[] args) {
        if (reporting == null) {
            reporting = new MessageReporting();
        }
        String className = "Unknown Class";
        if (srcClass != null) {
            className = srcClass.getName();
        }
        String methodName = "Unknown Method";
        if (srcMethod != null) {
            methodName = srcMethod;
        }
        Method method = new Method(methodName, args);
        // loggin to log file
        if (args != null && args.length != 0) {
            logger.logp(level, className, methodName, mess, args);
        } else {
            logger.logp(level, className, methodName, mess);
        }
        // showing mess to user
        mess += "\n\nClass: " + className + "\n" + method.getDescription();
        if (level == Level.INFO) {
            MessageReportingDialog.showMessage(mess, INFO_MESSAGE);
        } else {
            if (level == Level.WARNING) {
                MessageReportingDialog.showMessage(mess , WARNING_MESSAGE);
            } else {
                if (level == Level.SEVERE) {
                    MessageReportingDialog.showMessage(mess , ERROR_MESSAGE);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        MessageReporting.logOnly(Level.CONFIG, "this this is unlogable mess");
        MessageReporting.logOnly(Level.INFO, "this this is logable mess");
        
    }
}
