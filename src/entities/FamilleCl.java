/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author lenovooo
 */
@Entity
@Table(name = "FAMILLE_CL")
@NamedQueries({
    @NamedQuery(name = "FamilleCl.findAll", query = "SELECT f FROM FamilleCl f")})
public class FamilleCl implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "DES")
    private String des;
    @Basic(optional = false)
    @Column(name = "SOUMIS_TVA")
    private Boolean soumisTva;
    @Basic(optional = false)
    @Column(name = "MARGE")
    private double marge;
    @Basic(optional = false)
    @Column(name = "REMISE")
    private double remise;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "familleCl")
    private List<Client> clientList;

    public FamilleCl() {
    }

    public FamilleCl(Integer id) {
        this.id = id;
    }

    public FamilleCl(Integer id, String des, Boolean soumisTva, double marge, double remise) {
        this.id = id;
        this.des = des;
        this.soumisTva = soumisTva;
        this.marge = marge;
        this.remise = remise;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public Boolean getSoumisTva() {
        return soumisTva;
    }

    public void setSoumisTva(Boolean soumisTva) {
        this.soumisTva = soumisTva;
    }

    public double getMarge() {
        return marge;
    }

    public void setMarge(double marge) {
        this.marge = marge;
    }

    public double getRemise() {
        return remise;
    }

    public void setRemise(double remise) {
        this.remise = remise;
    }

    public List<Client> getClientList() {
        return clientList;
    }

    public void setClientList(List<Client> clientList) {
        this.clientList = clientList;
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
        if (!(object instanceof FamilleCl)) {
            return false;
        }
        FamilleCl other = (FamilleCl) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.FamilleCl[ id=" + id + " ]";
    }
    
}
