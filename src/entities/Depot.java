/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import dao.DepotDAO;
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
@Table(name = "DEPOT")
@NamedQueries({
    @NamedQuery(name = "Depot.findAll", query = "SELECT d FROM Depot d")})
public class Depot extends EntityClass<DepotDAO> implements Serializable {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "depot")
    private List<LigneAch> ligneAchList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "depot")
    private List<LigneVnt> ligneVntList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "depot")
    private List<EnStock> stockerList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "ADR")
    private String adr;
    @Basic(optional = false)
    @Column(name = "DE_VENTE")
    private Boolean deVente;
    @Basic(optional = false)
    @Column(name = "DE_RESERVE")
    private Boolean deReserve;
    @Basic(optional = false)
    @Column(name = "DE_STOCKAGE")
    private Boolean deStockage;
    @Basic(optional = false)
    @Column(name = "DE_PERTES")
    private Boolean dePertes;
    @Basic(optional = false)
    @Column(name = "DE_CMND_CL")
    private Boolean deCmndCl;
    @Basic(optional = false)
    @Column(name = "DE_CMND_FR")
    private Boolean deCmndFr;

    public Depot() {
        this(0);
    }

    public Depot(Integer id) {
        this.id = id;
        if (id > 0) {
            Depot d = DepotDAO.getInstance().getObjectByID(id);
            this.adr = d.adr;
            this.deVente = d.deVente;
            this.deReserve = d.deReserve;
            this.deStockage = d.deStockage;
            this.dePertes = d.dePertes;
            this.deCmndCl = d.deCmndCl;
            this.deCmndFr = d.deCmndFr;
        }
    }

    public Depot(Integer id, String adr, Boolean deVente, Boolean deReserve, Boolean deStock, Boolean dePertes, Boolean deCmdCl, Boolean deCmdFr) {
        this.id = id;
        this.adr = adr;
        this.deVente = deVente;
        this.deReserve = deReserve;
        this.deStockage = deStock;
        this.dePertes = dePertes;
        this.deCmndCl = deCmdCl;
        this.deCmndFr = deCmdFr;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getAdr() {
        return adr;
    }

    public void setAdr(String adr) {
        this.adr = adr;
    }

    public Boolean getDeVente() {
        return deVente;
    }

    public void setDeVente(Boolean deVente) {
        this.deVente = deVente;
    }

    public Boolean getDeReserve() {
        return deReserve;
    }

    public void setDeReserve(Boolean deReserve) {
        this.deReserve = deReserve;
    }

    public Boolean getDeStockage() {
        return deStockage;
    }

    public void setDeStockage(Boolean deStockage) {
        this.deStockage = deStockage;
    }

    public Boolean getDePertes() {
        return dePertes;
    }

    public void setDePertes(Boolean dePertes) {
        this.dePertes = dePertes;
    }

    public Boolean getDeCmndCl() {
        return deCmndCl;
    }

    public void setDeCmndCl(Boolean deCmndCl) {
        this.deCmndCl = deCmndCl;
    }

    public Boolean getDeCmndFr() {
        return deCmndFr;
    }

    public void setDeCmndFr(Boolean deCmndFr) {
        this.deCmndFr = deCmndFr;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Depot)) {
            return false;
        }
        return super.equals(object);
    }

    @Override
    public String toString() {
        return "Dépôt[ID:" + id + ", Adresse:" + adr + " ]";
    }

    @Override
    public String getShortDesc() {
        String desc = "";
        if (deVente) {
            desc = " (Vente)";
        }
        if (deReserve) {
            desc = " (Réserve)";
        }
        if (deStockage) {
            desc = "(Stockage)";
        }
        return adr + desc;
    }

    @Override
    public DepotDAO getTableDAO() {
        return DepotDAO.getInstance();
    }

    public List<EnStock> getStockerList() {
        return stockerList;
    }

    public void setStockerList(List<EnStock> stockerList) {
        this.stockerList = stockerList;
    }

    public List<LigneVnt> getLigneVntList() {
        return ligneVntList;
    }

    public void setLigneVntList(List<LigneVnt> ligneVntList) {
        this.ligneVntList = ligneVntList;
    }

    public List<LigneAch> getLigneAchList() {
        return ligneAchList;
    }

    public void setLigneAchList(List<LigneAch> ligneAchList) {
        this.ligneAchList = ligneAchList;
    }
}
