/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import dao.AchatDAO;
import dao.ReglementFrDAO;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author alilo
 */
@Entity
@Table(name = "ACHAT")
@NamedQueries({
    @NamedQuery(name = "Achat.findAll", query = "SELECT a FROM Achat a")})
public class Achat extends EntityClass<AchatDAO> implements Serializable {
    @Basic(optional = false)
    @Column(name = "COMMAND")
    private Boolean command;
    @Basic(optional = false)
    @Column(name = "RECEPTION")
    private Boolean reception;
    @Column(name = "DATE")
    @Temporal(TemporalType.DATE)
    private java.util.Date date;
    @Basic(optional = false)
    @Column(name = "HEURE")
    @Temporal(TemporalType.TIME)
    private java.util.Date heure;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "NUM")
    private int num;
    @Basic(optional = false)
    @Column(name = "VALIDE")
    private Boolean valide;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "TOTAL")
    private BigDecimal total;
    @JoinColumn(name = "ID_FR", referencedColumnName = "ID")
    @ManyToOne
    private Fournisseur fournisseur;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "achat")
    private List<LigneAch> ligneAchList;
    @OneToMany(mappedBy = "achat")
    private List<CreditFr> creditFrList;
    @OneToMany(mappedBy = "achat")
    private List<ReglementFr> reglementFrList;

    public Achat() {
        this(0);
    }

    public Achat(Integer id) {
        this.id = id;
        if (id > 0) {
            Achat ach = getTableDAO().getObjectByID(id);
            if (ach != null) {
                this.fournisseur = ach.getFournisseur();
                this.num = ach.num;
                this.date = ach.getDate();
                this.heure = ach.getHeure();
                this.valide = ach.isValide();
            }
        } else {
            this.fournisseur = new Fournisseur(0);
            this.num = 0;
            this.date = new Date(0);
            this.heure = new Date(0);
            this.valide = false;
        }
    }

    public Achat(Integer id, int num, Date date, Date heure, Boolean valide) {
        this.id = id;
        this.num = num;
        this.date = date;
        this.heure = heure;
        this.valide = valide;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }


    public Boolean isValide() {
        return valide;
    }

    public void setValide(Boolean valide) {
        this.valide = valide;
    }

    public boolean validate() {
        if (isValide()) {
            return true;
        }
        boolean validated = getTableDAO().validate(this);
        setValide(validated);
        return validated;
    }

    public boolean invalidate() {
        if (!isValide()) {
            return true;
        }
        boolean invalidated = getTableDAO().invalidate(this);
        setValide(!invalidated);
        return invalidated;
    }

    public BigDecimal getTotal() {
        this.total = getTableDAO().getTotalAchat(this);
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    
    public ReglementFr getReglement(){
        return ReglementFrDAO.getInstance().getReglementOfAch(this);
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
        if (!(object instanceof Achat)) {
            return false;
        }
        return super.equals(object);
    }

    @Override
    public String toString() {
        return "Achat[ID:"+ id +"N°:" + num + ", Le:" + date + "]";
    }

    public Fournisseur getFournisseur() {
        return fournisseur != null ? fournisseur : new Fournisseur();
    }

    public void setFournisseur(Fournisseur fournisseur) {
        this.fournisseur = fournisseur;
    }

    @Override
    public final AchatDAO getTableDAO() {
        return AchatDAO.getInstance();
    }

    public List<LigneAch> getLigneAchList() {
        return ligneAchList;
    }

    public void setLigneAchList(List<LigneAch> ligneAchList) {
        this.ligneAchList = ligneAchList;
    }

    public List<ReglementFr> getReglementFrList() {
        return reglementFrList;
    }

    public void setReglementFrList(List<ReglementFr> reglementFrList) {
        this.reglementFrList = reglementFrList;
    }

    public List<CreditFr> getCreditFrList() {
        return creditFrList;
    }

    public void setCreditFrList(List<CreditFr> creditFrCollection) {
        this.creditFrList = creditFrCollection;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getHeure() {
        return heure;
    }

    public void setHeure(Date heure) {
        this.heure = heure;
    }

    public Boolean getCommand() {
        return command;
    }

    public void setCommand(Boolean command) {
        this.command = command;
    }

    public Boolean getReception() {
        return reception;
    }

    public void setReception(Boolean reception) {
        this.reception = reception;
    }
}
