/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import dao.CreditClDAO;
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
@Table(name = "CREDIT_CL", catalog = "", schema = "ALILO")
public class CreditCl extends EntityClass<CreditClDAO> implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Integer id;
    @Temporal(TemporalType.DATE)
    private Date dateCr;
    @Basic(optional = false)
    @Column(nullable = false)
    @Temporal(TemporalType.TIME)
    private Date heure;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;
    @Column(length = 200)
    private String comment;
    @Basic(optional = false)
    @Column(name = "INITIAL")
    private Boolean initial;
    @JoinColumn(name = "ID_CL", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false)
    private Client idCl;
    @JoinColumn(name = "ID_VNT", referencedColumnName = "ID")
    @ManyToOne
    private Vente idVnt;

    public CreditCl() {
        this(0);
    }

    public CreditCl(Integer id) {
        this.id = id;
        this.dateCr = new Date(System.currentTimeMillis());
        this.heure = new Date(System.currentTimeMillis());
        this.montant = new BigDecimal(0);
    }

    public CreditCl(Integer id, Date date, Date heure, BigDecimal montant, String comment, boolean initial) {
        this.id = id;
        this.dateCr = date;
        this.heure = heure;
        this.montant = montant;
        this.comment = comment;
        this.initial = initial;
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
        return dateCr;
    }

    public void setDate(Date date) {
        this.dateCr = date;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean isInitial() {
        return initial;
    }

    public void setInitial(Boolean initial) {
        this.initial = initial;
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
        if (!(object instanceof CreditCl)) {
            return false;
        }
        return super.equals(object);
    }

    @Override
    public String toString() {
        return "CreditCl[ID:" + id + ", Client:" + idCl.getNom() + ", Montant:" + montant + ", Le:" + dateCr + " à:" + heure + "]";
    }

    public Client getClient() {
        return idCl;
    }

    public void setClient(Client idCl) {
        this.idCl = idCl;
    }

    public Vente getVente() {
        return idVnt;
    }

    public void setVente(Vente idVnt) {
        this.idVnt = idVnt;
    }

    @Override
    public CreditClDAO getTableDAO() {
        return CreditClDAO.getInstance();
    }
}
