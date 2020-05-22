/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import dao.TableDAO;
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
 * @author alilo
 */
@Entity
@Table(name = "MODE_PAYE")
@NamedQueries({
    @NamedQuery(name = "ModePaye.findAll", query = "SELECT m FROM ModePaye m")})
public class ModePaye extends EntityClass<TableDAO> implements Serializable {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "modePaye")
    private List<ReglementFr> reglementFrList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "modePaye")
    private List<ReglementCl> reglementClList;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "DES")
    private String des;

    public static final ModePaye ESPECE = new ModePaye(1);
    public static final ModePaye CHEQUE = new ModePaye(2);
    public static final ModePaye VIREMENT = new ModePaye(3);
    public static final ModePaye CREDIT = new ModePaye(4);
    

    public ModePaye() {
    }

    public ModePaye(Integer id) {
        this.id = id;
        switch (id) {
            case 1:
                this.des = "Espèce";
                break;
            case 2:
                this.des = "Chèque";
                break;
            case 3:
                this.des = "Virement";
                break;
            case 4:
                this.des = "Crédit";
                break;
        }
    }

    public ModePaye(Integer id, String des) {
        this.id = id;
        this.des = des;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ModePaye)) {
            return false;
        }
        return super.equals(object);
    }

    @Override
    public String toString() {
        return "Mode.Règl[ID:" + id + ", Des:" + des + "]";
    }

    @Override
    public String getShortDesc() {
        return des; 
    }

    public List<ReglementCl> getReglementClList() {
        return reglementClList;
    }

    public void setReglementClList(List<ReglementCl> reglementClList) {
        this.reglementClList = reglementClList;
    }

    public List<ReglementFr> getReglementFrList() {
        return reglementFrList;
    }

    public void setReglementFrList(List<ReglementFr> reglementFrList) {
        this.reglementFrList = reglementFrList;
    }

}
