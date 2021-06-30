/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import dao.ReglementFrDAO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author alilo
 */
@Entity
@Table(name = "REGLEMENT_FR")
@NamedQueries({
    @NamedQuery(name = "ReglementFr.findAll", query = "SELECT r FROM ReglementFr r")})
public class ReglementFr extends EntityClass<ReglementFrDAO> implements Serializable {
    

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "DATE")
    @Temporal(TemporalType.DATE)
    private Date date;
    @Basic(optional = false)
    @Column(name = "HEURE")
    @Temporal(TemporalType.TIME)
    private Date heure;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "MONTANT")
    private BigDecimal montant;
    @Column(name = "COMMENT")
    private String comment;
    @JoinColumn(name = "ID_FR", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Fournisseur fournisseur;
    @JoinColumn(name = "ID_ACH", referencedColumnName = "ID")
    @ManyToOne
    private Achat achat;
    @JoinColumn(name = "MODE_PAY", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private ModePaye modePaye;

    public ReglementFr() {
        this(0);
    }

    public ReglementFr(Integer id) {
        this.id = id;
        if (id > 0) {
            ReglementFr rf = getTableDAO().getObjectByID(id);
            this.date = rf.date;
            this.heure = rf.heure;
            this.montant = rf.montant;
            this.modePaye = rf.modePaye;
            this.comment = rf.comment;
            this.fournisseur = rf.fournisseur;
            this.achat = rf.achat;
        } else {
            this.date = new Date(System.currentTimeMillis());
            this.heure = new Date(System.currentTimeMillis());
            this.montant = new BigDecimal(0);
        }

    }

    public ReglementFr(Integer id, Date date, Date heure, BigDecimal montant, ModePaye mode, String comment) {
        this.id = id;
        this.date = date;
        this.heure = heure;
        this.montant = montant;
        this.modePaye = mode;
        this.comment = comment;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
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

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }
    
    public ModePaye getModePaye() {
        return modePaye;
    }

    public void setModePaye(ModePaye modePaye) {
        this.modePaye = modePaye;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Fournisseur getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(Fournisseur fournisseur) {
        this.fournisseur = fournisseur;
    }

    public Achat getAchat() {
        return achat;
    }

    public void setAchat(Achat achat) {
        this.achat = achat;
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
        if (!(object instanceof ReglementFr)) {
            return false;
        }
        return super.equals(object);
    }

    @Override
    public String toString() {
        return "ReglementFr[ID:" + id + ", Le:" + date + " à:" + heure + ", Montant:" + montant + ", Pour:" + fournisseur.getNom() + "]";
    }

    @Override
    public final ReglementFrDAO getTableDAO() {
        return ReglementFrDAO.getInstance();
    }
}
