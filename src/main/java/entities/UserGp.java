/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import dao.UGroupDAO;
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
@Table(name = "USER_GP")
@NamedQueries({
    @NamedQuery(name = "UserGp.findAll", query = "SELECT u FROM UserGp u")})
public class UserGp extends EntityClass<UGroupDAO> implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "DES")
    private String des;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userGp")
    private List<AppUser> appUserList;

    public UserGp() {
    }

    public UserGp(Integer id) {
        this.id = id;
    }

    public UserGp(Integer id, String des) {
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

    public List<AppUser> getAppUserList() {
        return appUserList;
    }

    public void setAppUserList(List<AppUser> appUserList) {
        this.appUserList = appUserList;
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
        if (!(object instanceof UserGp)) {
            return false;
        }
        UserGp other = (UserGp) object;
        return true;
    }

    @Override
    public String toString() {
        return "Groupe[ID:" + id +", Des: "+ des+ "]";
    }
    
    @Override
    public String getShortDesc() {
        return des;
    }

    @Override
    public UGroupDAO getTableDAO() {
        return UGroupDAO.getInstance();
    }
}
