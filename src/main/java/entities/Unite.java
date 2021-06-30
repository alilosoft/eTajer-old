/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import dao.UniteDAO;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author alilo
 */
@Entity
public class Unite extends EntityClass<UniteDAO> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "QTE")
    private double qte;
    @Basic(optional = false)
    @Column(name = "DES")
    private String des;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "unite")
    private List<LigneAch> ligneAchList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "unite")
    private List<Quantifier> quantifierCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "uniteVnt")
    private List<LigneVnt> ligneVntList;

    public Unite() {
    }

    public Unite(Integer id) {
        this.id = id;
    }

    public Unite(Integer id, String des, double qte) {
        this.id = id;
        this.des = des;
        this.qte = qte;
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

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
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
        if (!(object instanceof Unite)) {
            return false;
        }
        return super.equals(object);
    }

    @Override
    public String toString() {
        return des;
    }

    public List<Quantifier> getQuantifierCollection() {
        return quantifierCollection;
    }

    public void setQuantifierCollection(List<Quantifier> quantifierCollection) {
        this.quantifierCollection = quantifierCollection;
    }

    public List<LigneVnt> getLigneVntList() {
        return ligneVntList;
    }

    public void setLigneVntList(List<LigneVnt> ligneVntList) {
        this.ligneVntList = ligneVntList;
    }

    @Override
    public String getShortDesc() {
        return des;
    }

    @Override
    public UniteDAO getTableDAO() {
        return UniteDAO.getInstance();
    }

    public List<LigneAch> getLigneAchList() {
        return ligneAchList;
    }

    public void setLigneAchList(List<LigneAch> ligneAchList) {
        this.ligneAchList = ligneAchList;
    }
}
