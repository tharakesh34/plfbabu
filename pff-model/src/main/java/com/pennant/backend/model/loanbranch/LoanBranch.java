package com.pennant.backend.model.loanbranch;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "applicantDetails")
@XmlAccessorType(XmlAccessType.NONE)
public class LoanBranch {

	@XmlElement(name = "branchCode")
	private String branchCode;
	@XmlElement
	private String branchAddrLine1;
	@XmlElement
	private String branchAddrLine2;
	@XmlElement
	private String branchPOBox;
	@XmlElement(name = "cityCode")
	private String branchCity;
	@XmlElement(name = "cityName")
	private String lovDescBranchCityName;
	@XmlElement(name = "state")
	private String branchProvince;
	@XmlElement(name = "stateName")
	private String lovDescBranchProvinceName;
	@XmlElement(name = "countryCode")
	private String branchCountry;
	@XmlElement(name = "countryName")
	private String lovDescBranchCountryName;
	@XmlElement(name = "fax")
	private String branchFax;
	@XmlElement(name = "telephoneNumber")
	private String branchTel;
	@XmlElement(name = "mail")
	private String branchMail;

	public LoanBranch() {
		super();
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getBranchAddrLine1() {
		return branchAddrLine1;
	}

	public void setBranchAddrLine1(String branchAddrLine1) {
		this.branchAddrLine1 = branchAddrLine1;
	}

	public String getBranchAddrLine2() {
		return branchAddrLine2;
	}

	public void setBranchAddrLine2(String branchAddrLine2) {
		this.branchAddrLine2 = branchAddrLine2;
	}

	public String getBranchPOBox() {
		return branchPOBox;
	}

	public void setBranchPOBox(String branchPOBox) {
		this.branchPOBox = branchPOBox;
	}

	public String getBranchCity() {
		return branchCity;
	}

	public void setBranchCity(String branchCity) {
		this.branchCity = branchCity;
	}

	public String getLovDescBranchCityName() {
		return lovDescBranchCityName;
	}

	public void setLovDescBranchCityName(String lovDescBranchCityName) {
		this.lovDescBranchCityName = lovDescBranchCityName;
	}

	public String getBranchProvince() {
		return branchProvince;
	}

	public void setBranchProvince(String branchProvince) {
		this.branchProvince = branchProvince;
	}

	public String getLovDescBranchProvinceName() {
		return lovDescBranchProvinceName;
	}

	public void setLovDescBranchProvinceName(String lovDescBranchProvinceName) {
		this.lovDescBranchProvinceName = lovDescBranchProvinceName;
	}

	public String getBranchCountry() {
		return branchCountry;
	}

	public void setBranchCountry(String branchCountry) {
		this.branchCountry = branchCountry;
	}

	public String getLovDescBranchCountryName() {
		return lovDescBranchCountryName;
	}

	public void setLovDescBranchCountryName(String lovDescBranchCountryName) {
		this.lovDescBranchCountryName = lovDescBranchCountryName;
	}

	public String getBranchFax() {
		return branchFax;
	}

	public void setBranchFax(String branchFax) {
		this.branchFax = branchFax;
	}

	public String getBranchTel() {
		return branchTel;
	}

	public void setBranchTel(String branchTel) {
		this.branchTel = branchTel;
	}

	public String getBranchMail() {
		return branchMail;
	}

	public void setBranchMail(String branchMail) {
		this.branchMail = branchMail;
	}
}
