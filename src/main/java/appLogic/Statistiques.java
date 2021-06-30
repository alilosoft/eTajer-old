/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package appLogic;

import dao.CreditClDAO;
import dao.ReglementClDAO;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import dao.VenteDAO;
import entities.AppUser;
import java.util.Date;
import tools.DateTools;

/**
 *
 * @author alilo
 */
public class Statistiques {

    private static Statistiques instance;
    private Date beginDate, endDate;
    private AppUser user;
    private BigDecimal totalVentes = new BigDecimal(0);
    private BigDecimal totalCred = new BigDecimal(0);
    private BigDecimal totalReglsCl = new BigDecimal(0);
    private BigDecimal totalRetour = new BigDecimal(0);
    private BigDecimal totalDepense = new BigDecimal(0);
    private BigDecimal caisse = new BigDecimal(0);
    private BigDecimal benifice = new BigDecimal(0);

    private final String decimalFormatPattern = "###,##0.00";
    private final DecimalFormat decimalFormat = new DecimalFormat(decimalFormatPattern);

    public Statistiques(Date beginDate, Date endDate, AppUser user) {
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.user = user;

        calculateRecette();
    }

    public static Statistiques getInstance(Date beginDate, Date endDate, AppUser user) {
        if (instance == null || beginDate != instance.beginDate || endDate != instance.endDate || user != instance.user) {
            instance = new Statistiques(beginDate, endDate, user);
        }
        return instance;
    }

    public final BigDecimal calculateRecette() {
        long t = System.currentTimeMillis();
        if (beginDate == null) {
            beginDate = DateTools.TODAY;
        }
        if (endDate == null) {
            endDate = DateTools.TODAY;
        }
        t = System.currentTimeMillis() - t;
        System.out.println("init: " + t);
        if (user == null || user.getId() <= 0) {
            t = System.currentTimeMillis();
            totalVentes = VenteDAO.getInstance().getTotalVentes(beginDate, endDate);
            t = System.currentTimeMillis() - t;
            System.out.println("total vnts: " + t);
            t = System.currentTimeMillis();
            //benifice = LigneVenteDAO.getInstance().getTotalBenifice(beginDate, endDate);
            t = System.currentTimeMillis() - t;
            System.out.println("total benific: " + t);
        } else {
            totalVentes = VenteDAO.getInstance().getTotalVentes(beginDate, endDate, user);
            //benifice = LigneVenteDAO.getInstance().getTotalBenifice(beginDate, endDate, user);
        }
        t = System.currentTimeMillis();
        totalCred = CreditClDAO.getInstance().getTotalCredits(beginDate, endDate);
        t = System.currentTimeMillis() - t;
        System.out.println("total cred: " + t);
        t = System.currentTimeMillis();
        totalReglsCl = ReglementClDAO.getInstance().getTotalReglsClients(beginDate, endDate);
        t = System.currentTimeMillis() - t;
        System.out.println("total regl: " + t);
        
        double totalVntD = totalVentes.doubleValue();
        double totalCredD = totalCred.doubleValue();
        double totalReglD = totalReglsCl.doubleValue();
        double totalRetourD = totalRetour.doubleValue();
        double totalDepenseD = totalDepense.doubleValue();

        double caisseD = totalVntD + totalReglD - totalCredD - totalRetourD - totalDepenseD;

        caisse = new BigDecimal(caisseD);
        return caisse;
    }

    public String format(BigDecimal decimal) {
        return decimalFormat.format(decimal);
    }

    public BigDecimal getRecette() {
        return totalVentes;
    }

    public BigDecimal getTotalCred() {
        return totalCred;
    }

    public BigDecimal getTotalRegl() {
        return totalReglsCl;
    }

    public BigDecimal getTotalRetour() {
        return totalRetour;
    }

    public BigDecimal getTotalDepense() {
        return totalDepense;
    }

    public BigDecimal getCaisse() {
        return caisse;
    }

    public BigDecimal getBenifice() {
        return benifice;
    }
}
