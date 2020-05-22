package tools;

import dialogs.MessageReportingDialog;
import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import dao.AchatDAO;
import java.io.IOException;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author alilo
 */
public final class ExceptionReporting {

    private static ExceptionReporting reporting;
    private static final Logger logger = Logger.getLogger(ExceptionReporting.class.getName());
    private FileHandler fileHandler;
    private String logDirPath = "log";

    public ExceptionReporting() {
        File logDir = new File(logDirPath);
        if (!logDir.exists()) {
            logDir.mkdir();
        }

        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        try {
            fileHandler = new FileHandler(logDirPath + File.separator + "Exceptions_" + date + ".log", true);
        } catch (IOException | SecurityException ex) {
            String mess = "Erreur lor de l'initialisation du ExceptionReporting\n" + ex.toString() + "\n\nClass:" + getClass().getName();
            MessageReportingDialog.showMessage(mess, MessageReporting.ERROR_MESSAGE);
            System.exit(0);
        }
        fileHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(fileHandler);
        logger.setLevel(Level.FINE);
    }

    public static void showException(Throwable ex, Object[] params) {
        logger.log(Level.SEVERE, null, params);
        showException(ex);
    }

    public static void showException(Throwable ex) {
        if (reporting == null) {
            reporting = new ExceptionReporting();
        }

        logger.log(Level.SEVERE, ex.toString(), ex);
        String src = "Class: " + ex.getStackTrace()[0].getClassName()
                + "\nMethod: " + ex.getStackTrace()[0].getMethodName()
                + "\nLine: " + ex.getStackTrace()[0].getLineNumber();
        String mess = "";
        if (ex instanceof SQLException) {
            SQLException e = (SQLException) ex;
            //afficher la chaine des exceptions SQL
            do {
                mess += "SQLState:" + e.getSQLState() + "\nMessage: " + e.getMessage() + "\n";
                e = e.getNextException();
            } while (e != null);
        } else {
            mess += ex.toString();
            for (StackTraceElement element : ex.getStackTrace()) {
                mess = mess + "\nat " + element;
            }
        }
        MessageReportingDialog.showMessage(mess + "\n\n" + src, MessageReporting.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            int x = 1 / 0;
        } catch (Exception e) {
            ExceptionReporting.showException(e);
        }
    }
}
