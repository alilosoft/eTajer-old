/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import dao.CategorieDAO;
import java.io.Serializable;
import java.util.Collection;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author alilo
 */
@Entity
@Table(name = "CATEGORIE")
@XmlRootElement
public class Categorie extends EntityClass<CategorieDAO> implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "DES")
    private String des;
        @OneToMany(cascade = CascadeType.ALL, mappedBy = "categ")
    private Collection<Produit> produitCollection;
    @JoinColumn(name = "ID_FAM", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Famille famille;

    public Categorie() {
    }

    public Categorie(Integer id) {
        this.id = id;
    }

    public Categorie(Integer id, String des, Famille famille) {
        this.id = id;
        this.des = des;
        this.famille = famille;
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

    @XmlTransient
    public Collection<Produit> getProduitCollection() {
        return produitCollection;
    }

    public void setProduitCollection(Collection<Produit> produitCollection) {
        this.produitCollection = produitCollection;
    }

    public Famille getFamille() {
        return famille;
    }

    public void setFamille(Famille famille) {
        this.famille = famille;
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
        if (!(object instanceof Categorie)) {
            return false;
        }
        return super.equals(object);
    }

    @Override
    public String toString() {
        return "Categorie [ Ref: " + id +", Des: "+des+" ]";
    }

    @Override
    public String getShortDesc() {
        return des;
    }

    @Override
    public CategorieDAO getTableDAO() {
        return CategorieDAO.getInstance();
    }
}
