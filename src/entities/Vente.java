/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import dao.ReglementClDAO;
import dao.VenteDAO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author alilo
 */
@Entity
@Table(catalog = "", schema = "ALILO")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Vente.findAll", query = "SELECT v FROM Vente v")})
public class Vente extends EntityClass<VenteDAO> implements Serializable {
    @JoinColumn(name = "ID_USER", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    @Basic(optional = false)
    @Column(name = "COMMAND")
    private Boolean command;
    @Basic(optional = false)
    @Column(name = "RESERV")
    private Boolean reserv;
    @Basic(optional = false)
    @Column(name = "LIVRAIS")
    private Boolean livrais;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "NUM")
    private int num;
    @Column(name = "DATE")
    @Temporal(TemporalType.DATE)
    private Date date;
    @Basic(optional = false)
    @Column(name = "HEURE")
    @Temporal(TemporalType.TIME)
    private Date heure;
    @Basic(optional = false)
    @Column(name = "VALIDEE")
    private Boolean validee;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "TOTAL")
    private BigDecimal total;
    @JoinColumn(name = "ID_CL", referencedColumnName = "ID")
    @ManyToOne
    private Client client;
    @JoinColumn(name = "ID_TYPE", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false)
    private TypeVnt typeVnt;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vente")
    private List<LigneVnt> ligneVntList;
    @OneToMany(mappedBy = "vente")
    private List<ReglementCl> reglementClList;

    public Vente() {
        this(0);
    }

    public Vente(Integer id) {
        if (id > 0) {
            this.id = id;
            Vente v = VenteDAO.getInstance().getObjectByID(id);
            this.client = v.client;
            this.typeVnt = v.typeVnt;

            this.num = v.num;
            this.date = v.date;
            this.heure = v.heure;
            this.validee = v.validee;
        } else {
            this.client = new Client(0);
            this.typeVnt = TypeVnt.GROS;

            this.id = id;
            this.num = 0;
            this.date = new Date();
            this.heure = new Date();
            this.validee = false;
        }
    }

    public Vente(Integer id, int num, Date dateVnt, Date heure, boolean validee) {
        this.id = id;
        this.num = num;
        this.date = dateVnt;
        this.heure = heure;
        this.validee = validee;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getHeure() {
        return heure;
    }

    public void setHeure(Date heure) {
        this.heure = heure;
    }

    public boolean isValidee() {
        return validee;
    }

    public void setValidee(boolean validee) {
        this.validee = validee;
    }

    public boolean validate() {
        if (isValidee()) {
            return true;
        }
        boolean validated = getTableDAO().validate(this);
        setValidee(validated);
        return validated;
    }

    public boolean invalidate() {
        if (!isValidee()) {
            return true;
        }
        boolean invalidated = getTableDAO().invalidate(this);
        setValidee(!invalidated);
        return invalidated;
    }

    public BigDecimal getTotal() {
        this.total = getTableDAO().getTotalVente(this);
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getBenifice() {
        return VenteDAO.getInstance().getBenificeVente(this);
    }

    public ReglementCl getReglement() {
        return ReglementClDAO.getInstance().getReglementOfVnt(this);
    }

    public Client getClient() {
        return (client != null) ? client : new Client();
    }

    public void setClient(Client idCl) {
        this.client = idCl;
    }

    public TypeVnt getTypeVnt() {
        return typeVnt;
    }

    public void setTypeVnt(TypeVnt idType) {
        this.typeVnt = idType;
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
        if (!(object instanceof Vente)) {
            return false;
        }
        return super.equals(object);
    }

    @Override
    public String toString() {
        return "Vente[ID:" + id +", N°:"+ num+", Le:"+ date +"]";
    }

    @XmlTransient
    public List<LigneVnt> getLigneVntList() {
        return ligneVntList;
    }

    public void setLigneVntList(List<LigneVnt> ligneVntList) {
        this.ligneVntList = ligneVntList;
    }

    @XmlTransient
    public List<ReglementCl> getReglementClList() {
        return reglementClList;
    }

    public void setReglementClList(List<ReglementCl> reglementClList) {
        this.reglementClList = reglementClList;
    }

    @Override
    public VenteDAO getTableDAO() {
        return VenteDAO.getInstance();
    }

    public Boolean getCommand() {
        return command;
    }

    public void setCommand(Boolean command) {
        this.command = command;
    }

    public Boolean getReserv() {
        return reserv;
    }

    public void setReserv(Boolean reserv) {
        this.reserv = reserv;
    }

    public Boolean getLivrais() {
        return livrais;
    }

    public void setLivrais(Boolean livrais) {
        this.livrais = livrais;
    }
}
