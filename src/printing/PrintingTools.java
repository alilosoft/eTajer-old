/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package printing;

import dao.ProduitDAO;
import dbTools.DBManager;
import dialogs.MyJDialog;
import java.awt.Dimension;
import java.sql.Connection;
import java.util.HashMap;
import myComponents.MyJTable;
import myComponents.MyTableJRDataSource;
import myModels.ResultSet2TableModel;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.swing.JRViewer;
import tools.ExceptionReporting;

/**
 *
 * @author alilo
 */
public class PrintingTools {
    public static final String SHOW_DIALOG = "ShowDialog";
    public static final String ARABIC = "AR";
    public static final String FRENCH = "FR";
    public static final String A4 = "A4";
    public static final String A5 = "A5";
    public static final String A6 = "A6";
    
    private static Connection conn;
    private static HashMap<String, Object> params;
    private static JasperPrint print;
    private static final MyJDialog prevDialog = new MyJDialog(null, true, false);
    private static boolean showDialog = false;
    public static JasperPrint getJasperPrint(String repPath, Connection c, HashMap<String, Object> p) {
        if (c == null) {
            conn = DBManager.getInstance().getDefaultConnection();
        } else {
            conn = c;
        }
        params = p;
        try {
            print = JasperFillManager.fillReport(PrintingTools.class.getResourceAsStream(repPath), params, conn);
        } catch (JRException ex) {
            ExceptionReporting.showException(ex);
        }
        return print;

    }
    
    public static JasperPrint getJasperPrint(String repPath, JRDataSource dataSource, HashMap<String, Object> p) {
        params = p;
        try {
            print = JasperFillManager.fillReport(PrintingTools.class.getResourceAsStream(repPath), params, dataSource);
        } catch (JRException ex) {
            ExceptionReporting.showException(ex);
        }
        return print;

    }

    public static JRViewer getJRViewer(String repPath, Connection c, HashMap<String, Object> p) {
        JRViewer viewer = new JRViewer(getJasperPrint(repPath, c, p));
        viewer.setPreferredSize(new Dimension(900, 700));
        return viewer;
    }
    
    public static JRViewer getJRViewer(String repPath, JRDataSource dataSource, HashMap<String, Object> p) {
        JRViewer viewer = new JRViewer(getJasperPrint(repPath, dataSource, p));
        viewer.setPreferredSize(new Dimension(900, 700));
        return viewer;
    }

    public static void printReport(String repPath, Connection c, HashMap<String, Object> params) {
        try {
            JasperPrintManager.printReport(getJasperPrint(repPath, c, params), showDialog);
        } catch (JRException ex) {
            ExceptionReporting.showException(ex);
        }
    }
    
    public static void printReport(String repPath, JRDataSource dataSource, HashMap<String, Object> params) {
        try {
            JasperPrintManager.printReport(getJasperPrint(repPath, dataSource, params), showDialog);
        } catch (JRException ex) {
            ExceptionReporting.showException(ex);
        }
    }

    public static void previewReport(String repPath, Connection c, HashMap<String, Object> params) {
        System.out.println("rep path: "+ repPath);
        prevDialog.show(getJRViewer(repPath, c, params));
    }
    
    public static void previewReport(String repPath, JRDataSource dataSource, HashMap<String, Object> params) {
        prevDialog.show(getJRViewer(repPath, dataSource, params));
    }

    public static void initializeReports(Connection c) {
        try {
            //JasperFillManager.fillReport(PrintingTools.class.getResourceAsStream("/printing/AR/A6/ByColis/BStk/BS.jasper"), null, c);
            
            //JasperFillManager.fillReport(PrintingTools.class.getResourceAsStream("/printing/AR/A6/ByColis/BLC/AN.jasper"), null, c);
            //JasperFillManager.fillReport(PrintingTools.class.getResourceAsStream("/printing/AR/A6/ByColis/BLC/CL.jasper"), null, c);
            
            //JasperFillManager.fillReport(PrintingTools.class.getResourceAsStream("/printing/AR/A6/NoColis/BLC/AN.jasper"), null, c);
            //JasperFillManager.fillReport(PrintingTools.class.getResourceAsStream("/printing/AR/A6/NoColis/BLC/CL.jasper"), null, c);
            
            //JasperFillManager.fillReport(PrintingTools.class.getResourceAsStream("/printing/FR/A4/NoColis/BLC/AN.jasper"), null, c);
            //JasperFillManager.fillReport(PrintingTools.class.getResourceAsStream("/printing/FR/A4/NoColis/BLC/CL.jasper"), null, c);
            //JasperFillManager.fillReport(PrintingTools.class.getResourceAsStream("/printing/FR/A4/NoColis/BLC/Fellah.jasper"), null, c);
            
            //JasperFillManager.fillReport(PrintingTools.class.getResourceAsStream("/printing/FR/FactureCL/Facture.jasper"), null, c);
            
            JasperFillManager.fillReport(PrintingTools.class.getResourceAsStream("/printing/Prod_CodeBar.jasper"), null, c);
            //JasperFillManager.fillReport(PrintingTools.class.getResourceAsStream("/printing/Table_CodeBar.jasper"), null, c);
            JasperFillManager.fillReport(PrintingTools.class.getResourceAsStream("/printing/Lot_PrixVnt.jasper"), null, c);
            JasperFillManager.fillReport(PrintingTools.class.getResourceAsStream("/printing/FR/Ticket/AN.jasper"), null, c);
        } catch (JRException ex) {
            ExceptionReporting.showException(ex, new Object[]{c});
        }
    }

    public static void main(String[] args) throws JRException {
        HashMap<String, Object> p = new HashMap<>();
        p.clear();
        p.put("ID_VNT", 7);
        //PrintingTools.previewReport("/reporting/Facture_FR_A4_FullPage.jasper", null, p);
        
        ResultSet2TableModel model = new ResultSet2TableModel(ProduitDAO.getInstance().getAll(), false);
        MyJTable jTable = new MyJTable(model);
        MyTableJRDataSource tableDataSource = new MyTableJRDataSource(jTable);
        JRTableModelDataSource modelDataSource = new JRTableModelDataSource(model);
        //PrintingTools.previewReport("/reporting/Table_CodeBar.jasper", tableDataSource, null);
        PrintingTools.initializeReports(DBManager.getInstance().getDefaultConnection());
    }
}
