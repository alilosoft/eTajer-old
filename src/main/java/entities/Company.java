/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import dao.CompanyDAO;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author alilo
 */
@Entity
@Table(name = "COMPANY")
@NamedQueries({
    @NamedQuery(name = "Company.findAll", query = "SELECT c FROM Company c")})
public class Company extends EntityClass<CompanyDAO> implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "COMPANY")
    private String company;
    @Basic(optional = false)
    @Column(name = "ACTIVITY")
    private String activity;
    @Basic(optional = false)
    @Column(name = "ADRESSE")
    private String adresse;
    @Basic(optional = false)
    @Column(name = "EMAIL")
    private String email;
    @Basic(optional = false)
    @Column(name = "SITE")
    private String site;
    @Basic(optional = false)
    @Column(name = "TEL")
    private String tel;
    @Basic(optional = false)
    @Column(name = "FAX")
    private String fax;
    @Basic(optional = false)
    @Column(name = "NUM_RC")
    private String numRc;
    @Basic(optional = false)
    @Column(name = "NUM_FISC")
    private String numFisc;
    @Basic(optional = false)
    @Column(name = "NUM_ART")
    private String numArt;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "CAPITAL")
    private BigDecimal capital;

    public Company() {
    }

    public Company(Integer id) {
        this.id = id;
    }

    public Company(Integer id, String company, String activity, String adresse, String email, String site, String tel, String fax, String numRc, String numFisc, String numArt, BigDecimal capital) {
        this.id = id;
        this.company = company;
        this.activity = activity;
        this.adresse = adresse;
        this.email = email;
        this.site = site;
        this.tel = tel;
        this.fax = fax;
        this.numRc = numRc;
        this.numFisc = numFisc;
        this.numArt = numArt;
        this.capital = capital;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
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

    public BigDecimal getCapital() {
        return capital;
    }

    public void setCapital(BigDecimal capital) {
        this.capital = capital;
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
        if (!(object instanceof Company)) {
            return false;
        }
        return super.equals(object);
    }

    @Override
    public String toString() {
        return "Company[ID:" + id + "]";
    }
    
}
