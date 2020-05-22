/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import dao.LigneVenteDAO;
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
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author alilo
 */
@Entity
@Table(name = "LIGNE_VNT", catalog = "", schema = "ALILO")
@XmlRootElement
public class LigneVnt extends EntityClass<LigneVenteDAO> implements Serializable {
    @Basic(optional = false)
    @Column(name = "QTE")
    private double qte;
    @Basic(optional = false)
    @Column(name = "COMMAND")
    private Boolean command;
    @Basic(optional = false)
    @Column(name = "RESERV")
    private Boolean reserv;
    @Basic(optional = false)
    @Column(name = "LIVRAIS")
    private Boolean livrais;
    @JoinColumn(name = "ID_USER", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "PU_VNT", nullable = false, precision = 10, scale = 2)
    private BigDecimal puVnt;
    @Basic(optional = false)
    @Column(name = "PU_ACH")
    private BigDecimal puAch;
    @Basic(optional = false)
    @Column(name = "VALIDEE")
    private Boolean validee;
    @Basic(optional = false)
    @Column(name = "QTE_UNIT", nullable = false)
    private double qteUnit;
    @Basic(optional = false)
    @Column(name = "TOTAL_LVNT", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalLvnt;
    @JoinColumn(name = "UNITE_VNT", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false)
    private Unite uniteVnt;
    @JoinColumn(name = "ID_VNT", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false)
    private Vente vente;
    @JoinColumn(name = "ID_EN_STK", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private EnStock enStock;

    public LigneVnt() {
        this(0);
    }

    public LigneVnt(Integer id) {
        this.id = id;
    }

    public LigneVnt(Integer id, BigDecimal puVnt, BigDecimal puAch, double qteVendu, double qteUnitairVendu, BigDecimal totalLvnt) {
        this.id = id;
        this.puVnt = puVnt;
        this.puAch = puAch;
        this.qte = qteVendu;
        this.qteUnit = qteUnitairVendu;
        this.totalLvnt = totalLvnt;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getPuVnt() {
        return puVnt;
    }

    public void setPuVnt(BigDecimal puVnt) {
        this.puVnt = puVnt;
    }

    public double getQte() {
        return qte;
    }

    public void setQte(double qteVendu) {
        this.qte = qteVendu;
    }

    public boolean getValidee() {
        return validee;
    }

    public void setValidee(boolean validee) {
        this.validee = validee;
    }

    public double getQteUnitaire() {
        return qteUnit;
    }

    public void setQteUnitair(double qteUnitairVendu) {
        this.qteUnit = qteUnitairVendu;
    }

    public BigDecimal getTotalLvnt() {
        return totalLvnt;
    }

    public void setTotalLvnt(BigDecimal totalLvnt) {
        this.totalLvnt = totalLvnt;
    }

    public Unite getUniteVnt() {
        return uniteVnt;
    }

    public void setUniteVnt(Unite uniteVnt) {
        this.uniteVnt = uniteVnt;
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
        if (!(object instanceof LigneVnt)) {
            return false;
        }
        return super.equals(object);
    }

    @Override
    public String toString() {
        return "LigneVnt[ID:" + id + "]";
    }

    @Override
    public LigneVenteDAO getTableDAO() {
        return LigneVenteDAO.getInstance();
    }


    public BigDecimal getPuAch() {
        return puAch;
    }

    public void setPuAch(BigDecimal puAch) {
        this.puAch = puAch;
    }

    public EnStock getEnStock() {
        return enStock;
    }

    public void setEnStock(EnStock enStock) {
        this.enStock = enStock;
    }

    public Boolean getCommand() {
        return command;
    }

    public void setCommand(Boolean command) {
        this.command = command;
    }

    public Boolean getReserv() {
        return reserv;
    }

    public void setReserv(Boolean reserv) {
        this.reserv = reserv;
    }

    public Boolean getLivrais() {
        return livrais;
    }

    public void setLivrais(Boolean livrais) {
        this.livrais = livrais;
    }
}
