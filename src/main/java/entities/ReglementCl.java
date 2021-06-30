/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import dao.ReglementClDAO;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author alilo
 */
@Entity
@Table(name = "REGLEMENT_CL", catalog = "", schema = "ALILO")
public class ReglementCl extends EntityClass<ReglementClDAO> implements Serializable {
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
    @JoinColumn(name = "MODE_PAY", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private ModePaye modePaye;
    @JoinColumn(name = "ID_CL", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false)
    private Client client;
    @JoinColumn(name = "ID_VNT", referencedColumnName = "ID")
    @ManyToOne
    private Vente vente;

    public ReglementCl() {
        this(0);
    }

    public ReglementCl(Integer id) {
        this.id = id;
        this.date = new Date(System.currentTimeMillis());
        this.heure = new Date(System.currentTimeMillis());
        this.montant = new BigDecimal(0);
    }

    public ReglementCl(Integer id, Date date, Date heure, BigDecimal montant, ModePaye mode, String comment) {
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

    public Client getClient() {
        return client;
    }

    public void setClient(Client idCl) {
        this.client = idCl;
    }

    public Vente getVente() {
        return vente;
    }

    public void setVente(Vente idVnt) {
        this.vente = idVnt;
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
        if (!(object instanceof ReglementCl)) {
            return false;
        }
        return super.equals(object);
    }

    @Override
    public String toString() {
        return "Reglement.Cl[ID:" + id + ", Le:" + date + " à:" + heure + ", Montant:" + montant + ", Par:"+ client.getNom() + "]";
    }

    @Override
    public ReglementClDAO getTableDAO() {
        return ReglementClDAO.getInstance();
    }
}