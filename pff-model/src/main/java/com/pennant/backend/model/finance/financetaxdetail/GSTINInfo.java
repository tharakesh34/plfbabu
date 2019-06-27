package com.pennant.backend.model.finance.financetaxdetail;

import java.util.Date;

public class GSTINInfo {

	private String gSTNNumber; // gstin
	private String legelName; // lgnm
	private String tradeName; // tradeNam
	private Date regesterDate; // rgdt
	private Date lastUpdatedDate; // lstupdt
	private String gSTNStatus; // sts
	private String zone; // stj
	private String orgType; // ctb
	private String city; // ctj
	private String orgCategory; // ctjCd
	private String cxdt;
	private String cityCode; // ctjCd
	private String statusCode;
	private String statusDesc;

	public String getgSTNNumber() {
		return gSTNNumber;
	}

	public void setgSTNNumber(String gSTNNumber) {
		this.gSTNNumber = gSTNNumber;
	}

	public String getLegelName() {
		return legelName;
	}

	public void setLegelName(String legelName) {
		this.legelName = legelName;
	}

	public String getTradeName() {
		return tradeName;
	}

	public void setTradeName(String tradeName) {
		this.tradeName = tradeName;
	}

	public Date getRegesterDate() {
		return regesterDate;
	}

	public void setRegesterDate(Date regesterDate) {
		this.regesterDate = regesterDate;
	}

	public Date getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public void setLastUpdatedDate(Date lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}

	public String getGSTNStatus() {
		return gSTNStatus;
	}

	public void setGSTNStatus(String gSTNStatus) {
		this.gSTNStatus = gSTNStatus;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getOrgType() {
		return orgType;
	}

	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getOrgCategory() {
		return orgCategory;
	}

	public void setOrgCategory(String orgCategory) {
		this.orgCategory = orgCategory;
	}

	public String getCxdt() {
		return cxdt;
	}

	public void setCxdt(String cxdt) {
		this.cxdt = cxdt;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}
}
