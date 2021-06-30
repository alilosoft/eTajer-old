/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import dao.AlertExpDAO;
import dao.LotEnStockDAO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import tools.DateTools;

/**
 *
 * @author alilo
 */
@Entity
@Table(name = "EN_STOCK")
public class EnStock extends EntityClass<LotEnStockDAO> implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "enStock")
    private List<AlerteExp> alerteExpList;

    @Basic(optional = false)
    @Column(name = "DATE_ENTR")
    @Temporal(TemporalType.DATE)
    private Date dateEntr;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "PU_ACH")
    private BigDecimal puAch;
    @Basic(optional = false)
    @Column(name = "PU_VNT_DT")
    private BigDecimal puVntDt;
    @Basic(optional = false)
    @Column(name = "PU_VNT_GR")
    private BigDecimal puVntGr;
    @Basic(optional = false)
    @Column(name = "PU_VNT_DGR")
    private BigDecimal puVntDgr;
    @Basic(optional = false)
    @Column(name = "PU_VNT_SGR")
    private BigDecimal puVntSgr;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "QTE")
    private double qte;
    @Column(name = "DATE_EXP")
    @Temporal(TemporalType.DATE)
    private Date dateExp;
    @Basic(optional = false)
    @Column(name = "COD_BAR")
    private String codBar;
    @JoinColumn(name = "ID_PROD", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Produit produit;
    @JoinColumn(name = "ID_DEPOT", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Depot depot;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "enStock")
    private List<LigneVnt> ligneVntList;
    @Basic(optional = false)
    @Column(name = "ACTIF")
    private Boolean actif;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "enStock")
    private List<LigneAch> ligneAchList;

    public EnStock() {
        this(0);
    }

    public EnStock(Integer id) {
        this.id = id;
    }

    public EnStock(Integer id, double qte, Date dateExp, String codBar, boolean actif) {
        this.id = id;
        this.qte = qte;
        this.dateExp = dateExp;
        this.codBar = codBar;
        this.actif = actif;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public double getQte() {
        return qte;
    }

    public void setQte(double qte) {
        this.qte = qte;
    }

    public Date getDateExp() {
        return dateExp;
    }

    public void setDateExp(Date dateExp) {
        this.dateExp = dateExp;
    }

    public String getCodBar() {
        return codBar;
    }

    public void setCodBar(String codBar) {
        this.codBar = codBar;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public Depot getDepot() {
        return depot;
    }

    public void setDepot(Depot depot) {
        this.depot = depot;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EnStock)) {
            return false;
        }
        return super.equals(object);
    }

    @Override
    public String toString() {
        return "EnStk[ID:" + id + ", Prod:" + produit.getDes() + " Dépôt:" + depot.getAdr() + ", Qte:" + qte + ", Exp:" + DateTools.getSqlDate(dateExp) + ", Réf:" + codBar + "]";
    }

    @Override
    public LotEnStockDAO getTableDAO() {
        return LotEnStockDAO.getInstance();
    }

    public List<LigneVnt> getLigneVntList() {
        return ligneVntList;
    }

    public void setLigneVntList(List<LigneVnt> ligneVntList) {
        this.ligneVntList = ligneVntList;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public List<LigneAch> getLigneAchList() {
        return ligneAchList;
    }

    public void setLigneAchList(List<LigneAch> ligneAchList) {
        this.ligneAchList = ligneAchList;
    }

    public Date getDateEntr() {
        return dateEntr;
    }

    public void setDateEntr(Date dateEntr) {
        this.dateEntr = dateEntr;
    }

    public BigDecimal getPuAch() {
        return puAch;
    }

    public void setPuAch(BigDecimal puAch) {
        this.puAch = puAch;
    }

    public BigDecimal getPuVntDt() {
        return puVntDt;
    }

    public void setPuVntDt(BigDecimal puVntDt) {
        this.puVntDt = puVntDt;
    }

    public BigDecimal getPuVntGr() {
        return puVntGr;
    }

    public void setPuVntGr(BigDecimal puVntGr) {
        this.puVntGr = puVntGr;
    }

    public BigDecimal getPuVntDgr() {
        return puVntDgr;
    }

    public void setPuVntDgr(BigDecimal puVntDgr) {
        this.puVntDgr = puVntDgr;
    }

    public BigDecimal getPuVntSgr() {
        return puVntSgr;
    }

    public void setPuVntSgr(BigDecimal puVntSgr) {
        this.puVntSgr = puVntSgr;
    }

    public Date getDateAlert() {
        AlerteExp alert = getAlertExp();
        if (getAlertExp() == null) {
            return DateTools.getMaxJavaDate();
        } else {
            return getAlertExp().getDateAlert();
        }
    }

    public AlerteExp getAlertExp() {
        return AlertExpDAO.getInstance().getByLot(this.getId());
    }

    public List<AlerteExp> getAlerteExpList() {
        return alerteExpList;
    }

    public void setAlerteExpList(List<AlerteExp> alerteExpList) {
        this.alerteExpList = alerteExpList;
    }
}
