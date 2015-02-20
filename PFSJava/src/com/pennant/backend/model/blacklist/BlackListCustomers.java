package com.pennant.backend.model.blacklist;

import java.util.Date;

public class BlackListCustomers {

	private String custCIF;
	private String custFName;
	private String custLName;
	private String custShrtName;
	private Date custDOB;
	private String custCRCPR;
	private String custPassportNo;
	private String phoneNumber;
	private String custNationality;
	private String employer;
	private String watchListRule;
	private boolean override;
	private String custCtgCode;
	private String custCtgType;
	private long CustIDList;

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getCustFName() {
		return custFName;
	}

	public void setCustFName(String custFName) {
		this.custFName = custFName;
	}

	public String getCustLName() {
		return custLName;
	}

	public void setCustLName(String custLName) {
		this.custLName = custLName;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public Date getCustDOB() {
		return custDOB;
	}

	public void setCustDOB(Date custDOB) {
		this.custDOB = custDOB;
	}

	public String getCustCRCPR() {
		return custCRCPR;
	}

	public void setCustCRCPR(String custCRCPR) {
		this.custCRCPR = custCRCPR;
	}

	public String getCustPassportNo() {
		return custPassportNo;
	}

	public void setCustPassportNo(String custPassportNo) {
		this.custPassportNo = custPassportNo;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getCustNationality() {
		return custNationality;
	}

	public void setCustNationality(String custNationality) {
		this.custNationality = custNationality;
	}

	public String getEmployer() {
		return employer;
	}

	public void setEmployer(String employer) {
		this.employer = employer;
	}

	public String getWatchListRule() {
		return watchListRule;
	}

	public void setWatchListRule(String watchListRule) {
		this.watchListRule = watchListRule;
	}

	public boolean getOverride() {
		return override;
	}
	
	public boolean isOverride() {
		return override;
	}

	public void setOverride(boolean override) {
		this.override = override;
	}

	public String getCustCtgCode() {
		return custCtgCode;
	}

	public void setCustCtgCode(String custCtgCode) {
		this.custCtgCode = custCtgCode;
	}

	public long getCustIDList() {
		return CustIDList;
	}

	public void setCustIDList(long custIDList) {
		CustIDList = custIDList;
	}

	public String getCustCtgType() {
	    return custCtgType;
    }

	public void setCustCtgType(String custCtgType) {
	    this.custCtgType = custCtgType;
    }
}
