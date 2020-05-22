/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import dao.UserDAO;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author lenovooo
 */
@Entity
@Table(name = "APP_USER")
@NamedQueries({
    @NamedQuery(name = "AppUser.findAll", query = "SELECT a FROM AppUser a")})
public class AppUser extends  EntityClass<UserDAO> implements Serializable {
    @OneToMany(mappedBy = "appUser")
    private List<Session> sessionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "appUser")
    private List<Vente> venteList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "LOGIN")
    private String login;
    @Basic(optional = false)
    @Column(name = "PW")
    private String pw;
    @JoinColumn(name = "ID_GROUP", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private UserGp userGp;

    public AppUser() {
        this(1);
    }

    public AppUser(Integer id) {
        this.id = id;
        if(id == 1){
            userGp = new UserGp(1, "Superviseur");
        }
    }

    public AppUser(Integer id, String login, String pw) {
        this.id = id;
        this.login = login;
        this.pw = pw;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public UserGp getUserGp() {
        return userGp;
    }

    public void setUserGp(UserGp userGp) {
        this.userGp = userGp;
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
        if (!(object instanceof AppUser)) {
            return false;
        }
        AppUser othe = (AppUser) object;
        return Objects.equals(othe.getId(), this.getId());
    }

    @Override
    public String toString() {
        return "Utilisateur[ID:" + id + ", Nom:"+ login + ", Groupe:"+ userGp.getDes()+ "]";
    }
    
    @Override
    public String getShortDesc() {
        return login + "("+ userGp.getDes()+")";
    }

    @Override
    public UserDAO getTableDAO() {
        return UserDAO.getInstance();
    }

    public boolean isAdmin() {
        return getUserGp().getId() == 1;
    }

    public List<Vente> getVenteList() {
        return venteList;
    }

    public void setVenteList(List<Vente> venteList) {
        this.venteList = venteList;
    }

    public List<Session> getSessionList() {
        return sessionList;
    }

    public void setSessionList(List<Session> sessionList) {
        this.sessionList = sessionList;
    }
}
