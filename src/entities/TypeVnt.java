/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import dao.TypeVenteDAO;
import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author alilo
 */
@Entity
@Table(name = "TYPE_VNT", catalog = "", schema = "ALILO", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"DES"})})
@XmlRootElement
public class TypeVnt extends EntityClass<TypeVenteDAO> implements Serializable {

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
    @Column(name = "DEF_MARGE", nullable = false, precision = 9, scale = 2)
    private BigDecimal defMarge;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "typeVnt")
    private List<Vente> venteList;

    public static final TypeVnt DETAIL = new TypeVnt(1);
    public static final TypeVnt GROS = new TypeVnt(2);
    public static final TypeVnt DEMI_GROS = new TypeVnt(3);
    public static final TypeVnt SUPER_GROS = new TypeVnt(4);

    public TypeVnt() {
    }

    public TypeVnt(Integer id) {
        this.id = id;
        defMarge = BigDecimal.ZERO;
        switch (id) {
            case 1:
                des = "Détail";
                break;
            case 2:
                des = "Gros";
                break;
            case 3:
                des = "Demi-Gros";
                break;
            case 4:
                des = "Super-Gros";
                break;
        }
    }

    public TypeVnt(Integer id, String des, BigDecimal defMarge) {
        this.id = id;
        this.des = des;
        this.defMarge = defMarge;
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

    public BigDecimal getDefMarge() {
        return defMarge;
    }

    public void setDefMarge(BigDecimal defMarge) {
        this.defMarge = defMarge;
    }

    @XmlTransient
    public List<Vente> getVenteList() {
        return venteList;
    }

    public void setVenteList(List<Vente> venteList) {
        this.venteList = venteList;
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
        if (!(object instanceof TypeVnt)) {
            return false;
        }
        return super.equals(object);
    }

    @Override
    public String toString() {
        return "TypeVnt[ID:" + id + ", Des:" + des + "]";
    }

    @Override
    public String getShortDesc() {
        return des;
    }

    @Override
    public TypeVenteDAO getTableDAO() {
        return TypeVenteDAO.getInstance();
    }

}
