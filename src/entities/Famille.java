/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import dao.FamilleDAO;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author alilo
 */
@Entity
@Table(name = "FAMILLE")
@XmlRootElement
public class Famille extends EntityClass<FamilleDAO> implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "DES")
    private String des;
    @Column(name = "TVA")
    private Short tva;
    @Basic(optional = false)
    @Column(name = "SERVICE")
    private Boolean service;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "famille")
    private Collection<Categorie> categorieCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "famille")
    private List<Produit> produitList;

    public Famille() {
    }

    public Famille(Integer id) {
        this.id = id;
    }

    public Famille(Integer id, String des, Short tva, boolean serv) {
        this.id = id;
        this.des = des;
        this.tva = tva;
        this.service = serv;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public Short getTva() {
        return tva;
    }

    public void setTva(Short tva) {
        this.tva = tva;
    }
    
    public Boolean getService() {
        return service;
    }
    
    public boolean isServices(){
        return service;
    }

    public void setService(Boolean service) {
        this.service = service;
    }

    @XmlTransient
    public Collection<Categorie> getCategorieCollection() {
        return categorieCollection;
    }

    public void setCategorieCollection(Collection<Categorie> categorieCollection) {
        this.categorieCollection = categorieCollection;
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
        if (!(object instanceof Famille)) {
            return false;
        }

        return super.equals(object);
    }

    @Override
    public String toString() {
        return "Famille[ Ref:" + id + ", Des: " + des + " ]";
    }

    @Override
    public String getShortDesc() {
        return des;
    }

    @Override
    public FamilleDAO getTableDAO() {
        return FamilleDAO.getInstance();
    }

    public List<Produit> getProduitList() {
        return produitList;
    }

    public void setProduitList(List<Produit> produitList) {
        this.produitList = produitList;
    }
}
