/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import dao.AlertExpDAO;
import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author lenovooo
 */
@Entity
@Table(name = "ALERTE_EXP")
@NamedQueries({
    @NamedQuery(name = "AlerteExp.findAll", query = "SELECT a FROM AlerteExp a")})
public class AlerteExp extends EntityClass<AlertExpDAO> implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "DATE_ALERT")
    @Temporal(TemporalType.DATE)
    private Date dateAlert;
    @JoinColumn(name = "ID_LOT", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private EnStock enStock;

    public AlerteExp() {
    }

    public AlerteExp(Integer id) {
        this.id = id;
    }

    public AlerteExp(Integer id, Date dateAlert) {
        this.id = id;
        this.dateAlert = dateAlert;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDateAlert() {
        return dateAlert;
    }

    public void setDateAlert(Date dateAlert) {
        this.dateAlert = dateAlert;
    }

    public EnStock getEnStock() {
        return enStock;
    }

    public void setEnStock(EnStock enStock) {
        this.enStock = enStock;
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
        if (!(object instanceof AlerteExp)) {
            return false;
        }
        AlerteExp other = (AlerteExp) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AlerteExp[ id=" + id + " ]";
    }

    @Override
    public AlertExpDAO getTableDAO() {
        return AlertExpDAO.getInstance();
    }
}
