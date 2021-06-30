/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import dao.LigneAchatDAO;
import java.io.Serializable;
import java.math.BigDecimal;
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

/**
 *
 * @author alilo
 */
@Entity
@Table(name = "LIGNE_ACH")
@NamedQueries({
    @NamedQuery(name = "LigneAch.findAll", query = "SELECT l FROM LigneAch l")})
public class LigneAch extends EntityClass<LigneAchatDAO> implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "PU_ACH")
    private BigDecimal puAch;
    @Basic(optional = false)
    @Column(name = "QTE")
    private int qte;
    @Basic(optional = false)
    @Column(name = "VALIDEE")
    private Boolean validee;
    @Basic(optional = false)
    @Column(name = "QTE_UNIT")
    private double qteUnit;
    @Basic(optional = false)
    @Column(name = "TOTAL_LACH")
    private BigDecimal totalLach;
    @JoinColumn(name = "UNITE", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Unite unite;
    @JoinColumn(name = "ID_PROD", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Produit produit;
    @JoinColumn(name = "ID_EN_STK", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private EnStock enStock;
    @JoinColumn(name = "ID_ACH", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Achat achat;

    public LigneAch() {
        this(0);
    }

    public LigneAch(Integer id) {
        this.id = id;
    }

    public LigneAch(Integer id, BigDecimal puAch, int qte, double qteUnit, BigDecimal totalLach) {
        this.id = id;
        this.puAch = puAch;
        this.qte = qte;
        this.qteUnit = qteUnit;
        this.totalLach = totalLach;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getPuAch() {
        return puAch;
    }

    public void setPuAch(BigDecimal puAch) {
        this.puAch = puAch;
    }

    public int getQte() {
        return qte;
    }

    public void setQte(int qte) {
        this.qte = qte;
    }

    public Boolean getValidee() {
        return validee;
    }

    public void setValidee(Boolean validee) {
        this.validee = validee;
    }

    public double getQteUnit() {
        return qteUnit;
    }

    public void setQteUnit(double qteUnit) {
        this.qteUnit = qteUnit;
    }

    public BigDecimal getTotalLach() {
        return totalLach;
    }

    public void setTotalLach(BigDecimal totalLach) {
        this.totalLach = totalLach;
    }

    public Unite getUnite() {
        return unite;
    }

    public void setUnite(Unite unite) {
        this.unite = unite;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public EnStock getEnStock() {
        return enStock;
    }

    public void setEnStock(EnStock enStock) {
        this.enStock = enStock;
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
        if (!(object instanceof LigneAch)) {
            return false;
        }
        return super.equals(object);
    }

    @Override
    public String toString() {
        return "LigneAch[ID: " + id + ", ID_STK: "+ enStock.getId()+ ", ID_ACH: "+ achat.getId()+"]";
    }
}
