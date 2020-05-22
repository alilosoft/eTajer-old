/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import dao.ClientDAO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author alilo
 */
@Entity
@Table(name = "CLIENT")
@XmlRootElement
public class Client extends EntityClass<ClientDAO> implements Serializable {
    @JoinColumn(name = "ID_FAM", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private FamilleCl familleCl;
    @Basic(optional = false)
    @Column(name = "FELLAH")
    private Boolean fellah;
    @Basic(optional = false)
    @Column(name = "NOM")
    private String nom;
    @Column(name = "MOBILE")
    private String mobile;
    @Column(name = "EMAIL")
    private String email;
    @Basic(optional = false)
    @Column(name = "APP_TVA")
    private Boolean appTva;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "CODE", nullable = false, length = 5)
    private String code;
    @Column(name = "ADR", length = 50)
    private String adr;
    @Column(name = "TEL", length = 10)
    private String tel;
    @Column(name = "NUM_RC", length = 10)
    private String numRc;
    @Column(name = "NUM_FISC", length = 15)
    private String numFisc;
    @Column(name = "NUM_ART", length = 11)
    private String numArt;
    @Basic(optional = false)
    @Column(name = "DETTE", nullable = false, precision = 10, scale = 2)
    private BigDecimal dette;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idCl")
    private List<ReglementCl> reglementClList;
    @OneToMany(mappedBy = "client")
    private List<Vente> venteList;
    
    public static final Client ANONYME = new Client(0);
    
    public Client() {
       this(0);
    }

    /**
     * If the id param is 0, create new anonymous client.
     *
     * @param id
     */
    public Client(Integer id) {
        this.id = id;
        if (id == 0) {
            code = "?";
            nom = "Anonyme!";
        }
    }

    public Client(Integer id, String code, String nom, String adr, String tel, String mobile, String email, String numRC, String numFisc, String numArt, boolean appTVA) {
        this.id = id;
        this.code = code;
        this.nom = nom;
        this.adr = adr;
        this.tel = tel;
        this.mobile = mobile;
        this.email = email;
        this.numRc = numRC;
        this.numFisc = numFisc;
        this.numArt = numArt;
        this.appTva = appTVA;
    }
    
    public boolean isAnonyme(){
        return getId() == 0;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String refer) {
        this.code = refer;
    }

    public String getAdr() {
        return adr;
    }

    public void setAdr(String adr) {
        this.adr = adr;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getNumRc() {
        return numRc;
    }

    public void setNumRc(String numRc) {
        this.numRc = numRc;
    }

    public String getNumFisc() {
        return numFisc;
    }

    public void setNumFisc(String numFisc) {
        this.numFisc = numFisc;
    }

    public String getNumArt() {
        return numArt;
    }

    public void setNumArt(String numArt) {
        this.numArt = numArt;
    }

    public boolean isAppTva() {
        return appTva;
    }

    public void setAppTva(boolean appTva) {
        this.appTva = appTva;
    }

    public BigDecimal getDette() {
        return dette;
    }

    public void setDette(BigDecimal dette) {
        this.dette = dette;
    }
    
    public CreditCl getCreditInitial(){
        return getTableDAO().getCreditInitial(this);
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
        if (!(object instanceof Client)) {
            return false;
        }
        return super.equals(object);
    }

    @Override
    public String toString() {
        return "Client[Code: " + code + ", Nom: " + nom + ", Dettes: " + dette + "]";
    }

    @Override
    public String getShortDesc() {
        return nom + ", Dettes: " + dette;
    }

    @XmlTransient
    public List<Vente> getVenteList() {
        return venteList;
    }

    public void setVenteList(List<Vente> venteList) {
        this.venteList = venteList;
    }

    @XmlTransient
    public List<ReglementCl> getReglementClList() {
        return reglementClList;
    }

    public void setReglementClList(List<ReglementCl> reglementClList) {
        this.reglementClList = reglementClList;
    }

    @Override
    public ClientDAO getTableDAO() {
        return ClientDAO.getInstance();
    }

    public Boolean getAppTva() {
        return appTva;
    }

    public void setAppTva(Boolean appTva) {
        this.appTva = appTva;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getFellah() {
        return fellah;
    }

    public void setFellah(Boolean fellah) {
        this.fellah = fellah;
    }
    
    public boolean isFellah(){
        return fellah;
    }
    
    public boolean isCommercant(){
        return !fellah;
    }

    public FamilleCl getFamilleCl() {
        return familleCl;
    }

    public void setFamilleCl(FamilleCl familleCl) {
        this.familleCl = familleCl;
    }
}
