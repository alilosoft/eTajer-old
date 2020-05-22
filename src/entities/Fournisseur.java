/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import dao.FournissDAO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
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

/**
 *
 * @author alilo
 */
@Entity
@Table(name = "FOURNISSEUR", catalog = "", schema = "ALILO", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"CODE"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Fournisseur.findAll", query = "SELECT f FROM Fournisseur f")})
public class Fournisseur extends EntityClass<FournissDAO> implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fournisseur")
    private List<ReglementFr> reglementFrList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fournisseur")
    private List<CreditFr> creditFrList;
    @OneToMany(mappedBy = "fournisseur")
    private List<Achat> achatList;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "CODE", nullable = false, length = 5)
    private String code;
    @Basic(optional = false)
    @Column(name = "NOM", nullable = false, length = 40)
    private String nom;
    @Column(name = "ADR", length = 50)
    private String adr;
    @Column(name = "TEL", length = 15)
    private String tel;
    @Column(name = "MOBILE", length = 15)
    private String mobile;
    @Column(name = "EMAIL", length = 50)
    private String email;
    @Column(name = "NUM_RC", length = 15)
    private String numRc;
    @Column(name = "NUM_FISC", length = 20)
    private String numFisc;
    @Column(name = "NUM_ART", length = 15)
    private String numArt;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "DETTE", nullable = false, precision = 12, scale = 2)
    private BigDecimal dette;

    public static final Fournisseur ANONYME = new Fournisseur(0);
    
    public Fournisseur() {
        this(0);
    }

    public Fournisseur(Integer id) {
        this.id = id;
        if (id == 0) {
            code = "?";
            nom = "Anonyme!";
        } else {
            Fournisseur f = getTableDAO().getObjectByID(id);
            if (f != null) {
                this.code = f.code;
                this.nom = f.nom;
                this.adr = f.adr;
                this.tel = f.tel;
                this.mobile = f.mobile;
                this.email = f.email;
                this.numRc = f.numRc;
                this.numFisc = f.numFisc;
                this.numArt = f.numArt;
                this.dette = f.dette;
            }
        }
    }

    public Fournisseur(Integer id, String code, String nom) {
        this.id = id;
        this.code = code;
        this.nom = nom;
    }

    public Fournisseur(Integer id, String code, String nom, String adr, String tel, String mobile, String email, String numRc, String numFisc, String numArt) {
        this.id = id;
        this.code = code;
        this.nom = nom;
        this.adr = adr;
        this.tel = tel;
        this.mobile = mobile;
        this.email = email;
        this.numRc = numRc;
        this.numFisc = numFisc;
        this.numArt = numArt;
    }

    public boolean isAnonyme() {
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

    public void setCode(String code) {
        this.code = code;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
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

    public BigDecimal getDette() {
        return dette;
    }

    public void setDette(BigDecimal dette) {
        this.dette = dette;
    }

    public CreditFr getCreditInitial() {
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
        if (!(object instanceof Fournisseur)) {
            return false;
        }
        return super.equals(object);
    }

    @Override
    public String toString() {
        return "Fourniss[Code: " + code + ", Nom: " + nom + ", Crédit: " + dette + "]";
    }

    @Override
    public String getShortDesc() {
        return nom + ", Crédit:" + dette;
    }

    @Override
    public final FournissDAO getTableDAO() {
        return FournissDAO.getInstance();
    }

    public List<Achat> getAchatList() {
        return achatList;
    }

    public void setAchatList(List<Achat> achatList) {
        this.achatList = achatList;
    }

    public List<CreditFr> getCreditFrList() {
        return creditFrList;
    }

    public void setCreditFrList(List<CreditFr> creditFrCollection) {
        this.creditFrList = creditFrCollection;
    }

    public List<ReglementFr> getReglementFrList() {
        return reglementFrList;
    }

    public void setReglementFrList(List<ReglementFr> reglementFrList) {
        this.reglementFrList = reglementFrList;
    }
}
