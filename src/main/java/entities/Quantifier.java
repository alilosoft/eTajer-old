/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import dao.QuantifierDAO;
import java.io.Serializable;
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

/**
 *
 * @author alilo
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "Quantifier.findAll", query = "SELECT q FROM Quantifier q")})
public class Quantifier extends EntityClass<QuantifierDAO> implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "UNITE_DT", nullable = false)
    private Boolean uniteDt = false;
    @Basic(optional = false)
    @Column(name = "UNITE_GR", nullable = false)
    private Boolean uniteGr = false;
    @Basic(optional = false)
    @Column(name = "UNITE_DGR", nullable = false)
    private Boolean uniteDGr = false;
    @Basic(optional = false)
    @Column(name = "UNITE_SGR", nullable = false)
    private Boolean uniteSGr = false;
    @JoinColumn(name = "ID_PROD", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false)
    private Produit produit;
    @JoinColumn(name = "ID_UNITE", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false)
    private Unite unite;

    public Quantifier() {
        this(0);
    }

    public Quantifier(Integer id) {
        this.id = id;
    }

    public Quantifier(Integer id, Boolean uniteDt, Boolean uniteGr, Boolean uniteDGr, Boolean uniteSGr) {
        this.id = id;
        this.uniteDt = uniteDt;
        this.uniteGr = uniteGr;
        this.uniteDGr = uniteDGr;
        this.uniteSGr = uniteSGr;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit idProd) {
        this.produit = idProd;
    }

    public Unite getUnite() {
        return unite;
    }

    public void setUnite(Unite idUnite) {
        this.unite = idUnite;
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
        if (!(object instanceof Quantifier)) {
            return false;
        }
        return super.equals(object);
    }

    @Override
    public String toString() {
        return "Quantifier[ID:" + id + "]";
    }

    @Override
    public QuantifierDAO getTableDAO() {
        return QuantifierDAO.getInstance();
    }

    public Boolean getUniteDt() {
        return uniteDt;
    }

    public void setUniteDt(Boolean uniteDt) {
        this.uniteDt = uniteDt;
    }

    public Boolean getUniteGr() {
        return uniteGr;
    }

    public void setUniteGr(Boolean uniteGr) {
        this.uniteGr = uniteGr;
    }

    public Boolean getUniteDGr() {
        return uniteDGr;
    }

    public void setUniteDGr(Boolean uniteDgr) {
        this.uniteDGr = uniteDgr;
    }

    public Boolean getUniteSGr() {
        return uniteSGr;
    }

    public void setUniteSGr(Boolean uniteSgr) {
        this.uniteSGr = uniteSgr;
    }
}
