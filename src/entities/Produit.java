/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import dao.LotEnStockDAO;
import dao.ProduitDAO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import dao.QuantifierDAO;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author alilo
 */
@Entity
@Table(name = "PRODUIT")
@XmlRootElement
public class Produit extends EntityClass<ProduitDAO> implements Serializable {
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "CUMP")
    private BigDecimal cump;
    @Basic(optional = false)
    @Column(name = "QTE")
    private double qte;
    @Basic(optional = false)
    @Column(name = "PU_ACH")
    private BigDecimal puAch;
    @Basic(optional = false)
    @Column(name = "PU_VNT")
    private BigDecimal puVnt;
    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @JoinColumn(name = "ID_FAM", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Famille famille;
    @JoinColumn(name = "ID_CATEG", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Categorie categ;
    @Basic(optional = false)
    @Column(name = "COD_BAR", nullable = false, length = 30)
    private String codBar;
    @Basic(optional = false)
    @Column(name = "DES")
    private String des;
    @Basic(optional = false)
    @Column(name = "QTE_MIN", nullable = false)
    private int qteMin;
    @Basic(optional = false)
    @Column(name = "QTE_MAX", nullable = false)
    private int qteMax;
    @Basic(optional = false)
    @Column(name = "QTE_GLOBAL", nullable = false)
    private double qteGlobal;
    @Basic(optional = false)
    @Column(name = "PMP")
    private BigDecimal pmp;
    @Basic(optional = false)
    @Column(name = "MARGE_DT")
    private double margeDt;
    @Basic(optional = false)
    @Column(name = "MARGE_GR")
    private double margeGr;
    @Basic(optional = false)
    @Column(name = "MARGE_DGR")
    private double margeDgr;
    @Basic(optional = false)
    @Column(name = "MARGE_SGR")
    private double margeSgr;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "produit")
    private List<Quantifier> quantifierList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "produit")
    private List<LigneAch> ligneAchList;

    public Produit() {
        this(0);
    }

    public Produit(Integer id) {
        this.id = id;
        if (id > 0) {
            Produit p = getTableDAO().getObjectByID(id);
            this.codBar = p.codBar;
            this.des = p.des;
            this.qteMin = p.qteMin;
            this.qteMax = p.qteMax;
            this.categ = p.getCateg();
        }
    }

    public Produit(Integer id, String codBar, String des, int qteMin, int qteMax, BigDecimal pmp, double margeDt, double margeGr, double margeDgr, double margeSgr) {
        this.id = id;
        this.codBar = codBar;
        this.des = des;
        this.qteMin = qteMin;
        this.qteMax = qteMax;
        this.pmp = pmp;
        this.margeDt = margeDt;
        this.margeGr = margeGr;
        this.margeDgr = margeDgr;
        this.margeSgr = margeSgr;
    }
    
    

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        Integer oldId = this.id;
        this.id = id;
        changeSupport.firePropertyChange("id", oldId, id);
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        String oldDes = this.des;
        this.des = des;
        changeSupport.firePropertyChange("des", oldDes, des);
    }

    public Categorie getCateg() {
        return categ;
    }

    public void setCateg(Categorie categ) {
        Categorie oldCateg = this.categ;
        this.categ = categ;
        changeSupport.firePropertyChange("categ", oldCateg, categ);
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
        if (!(object instanceof Produit)) {
            return false;
        }
        return super.equals(object);
    }

    @Override
    public String toString() {
        return "Produit[ID:" + id + ", Réf:" + codBar + ", Des:" + des + ", Qte:" + getQteGlobal() + "]";
    }

    public Unite getUnite() {
        for (Quantifier q : getUnitesList()) {
            if (q.getUnite().getQte() == 1) {
                return q.getUnite();
            }
        }
        return null;
    }

    public List<Quantifier> getUnitesList() {
        return QuantifierDAO.getInstance().getUnitesOfProd(this);
    }

    public String getCodBar() {
        return codBar;
    }

    public void setCodBar(String codBar) {
        String oldCodBar = this.codBar;
        this.codBar = codBar;
        changeSupport.firePropertyChange("codBar", oldCodBar, codBar);
    }

    public EnStock getLastLot() {
        return LotEnStockDAO.getInstance().getLastLot(this.getId());
    }

    public int getQteMin() {
        return qteMin;
    }

    public void setQteMin(int qteMin) {
        int oldQteMin = this.qteMin;
        this.qteMin = qteMin;
        changeSupport.firePropertyChange("qteMin", oldQteMin, qteMin);
    }

    public int getQteMax() {
        return qteMax;
    }

    public void setQteMax(int qteMax) {
        int oldQteMax = this.qteMax;
        this.qteMax = qteMax;
        changeSupport.firePropertyChange("qteMax", oldQteMax, qteMax);
    }

    public double getQteGlobal() {
        return getTableDAO().getQteGlobal(this);
    }

    public void setQteGlobal(double qteStk) {
        double oldQteStk = this.qteGlobal;
        this.qteGlobal = qteStk;
        changeSupport.firePropertyChange("qteGlobal", oldQteStk, qteStk);
    }

    @Override
    public String getShortDesc() {
        return des;
    }

    @Override
    public final ProduitDAO getTableDAO() {
        return ProduitDAO.getInstance();
    }

    public List<LigneAch> getLigneAchList() {
        return ligneAchList;
    }

    public void setLigneAchList(List<LigneAch> ligneAchList) {
        this.ligneAchList = ligneAchList;
    }

    public Famille getFamille() {
        return famille;
    }

    public void setFamille(Famille famille) {
        Famille oldFamille = this.famille;
        this.famille = famille;
        changeSupport.firePropertyChange("famille", oldFamille, famille);
    }

    public BigDecimal getPmp() {
        return pmp;
    }

    public void setPmp(BigDecimal pmp) {
        BigDecimal oldPmp = this.pmp;
        this.pmp = pmp;
        changeSupport.firePropertyChange("pmp", oldPmp, pmp);
    }

    public double getMargeDt() {
        return margeDt;
    }

    public void setMargeDt(double margeDt) {
        double oldMargeDt = this.margeDt;
        this.margeDt = margeDt;
        changeSupport.firePropertyChange("margeDt", oldMargeDt, margeDt);
    }

    public double getMargeGr() {
        return margeGr;
    }

    public void setMargeGr(double margeGr) {
        double oldMargeGr = this.margeGr;
        this.margeGr = margeGr;
        changeSupport.firePropertyChange("margeGr", oldMargeGr, margeGr);
    }

    public double getMargeDgr() {
        return margeDgr;
    }

    public void setMargeDgr(double margeDgr) {
        double oldMargeDgr = this.margeDgr;
        this.margeDgr = margeDgr;
        changeSupport.firePropertyChange("margeDgr", oldMargeDgr, margeDgr);
    }

    public double getMargeSgr() {
        return margeSgr;
    }

    public void setMargeSgr(double margeSgr) {
        double oldMargeSgr = this.margeSgr;
        this.margeSgr = margeSgr;
        changeSupport.firePropertyChange("margeSgr", oldMargeSgr, margeSgr);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    public BigDecimal getCump() {
        return cump;
    }

    public void setCump(BigDecimal cump) {
        this.cump = cump;
    }

    public double getQte() {
        return qte;
    }

    public void setQte(double qte) {
        this.qte = qte;
    }

    public BigDecimal getPuAch() {
        return puAch;
    }

    public void setPuAch(BigDecimal puAch) {
        this.puAch = puAch;
    }

    public BigDecimal getPuVnt() {
        return puVnt;
    }

    public void setPuVnt(BigDecimal puVnt) {
        this.puVnt = puVnt;
    }
}
