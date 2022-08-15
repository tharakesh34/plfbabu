package com.pennant.backend.model.systemmasters;

import java.io.Serializable;
import java.util.Date;

public class BalanceConfirmation implements Serializable {
	private static final long serialVersionUID = -539553688055843877L;

	private long finID;
	private String finReference;
	private String appDate;
	private String custShrtName;
	private String address1;
	private String address2;
	private String pinCode;
	private String sanctionRefNo;
	private String principalOS;
	private String profitOS;
	private String otherCharges;
	private String totalOSBalance;
	private Date disbursementDate;
	private String disbDate;

	public BalanceConfirmation() {
		super();
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getAppDate() {
		return appDate;
	}

	public void setAppDate(String appDate) {
		this.appDate = appDate;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getPinCode() {
		return pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	public String getSanctionRefNo() {
		return sanctionRefNo;
	}

	public void setSanctionRefNo(String sanctionRefNo) {
		this.sanctionRefNo = sanctionRefNo;
	}

	public String getPrincipalOS() {
		return principalOS;
	}

	public void setPrincipalOS(String principalOS) {
		this.principalOS = principalOS;
	}

	public String getProfitOS() {
		return profitOS;
	}

	public void setProfitOS(String profitOS) {
		this.profitOS = profitOS;
	}

	public String getOtherCharges() {
		return otherCharges;
	}

	public void setOtherCharges(String otherCharges) {
		this.otherCharges = otherCharges;
	}

	public String getTotalOSBalance() {
		return totalOSBalance;
	}

	public void setTotalOSBalance(String totalOSBalance) {
		this.totalOSBalance = totalOSBalance;
	}

	public Date getDisbursementDate() {
		return disbursementDate;
	}

	public void setDisbursementDate(Date disbursementDate) {
		this.disbursementDate = disbursementDate;
	}

	public String getDisbDate() {
		return disbDate;
	}

	public void setDisbDate(String disbDate) {
		this.disbDate = disbDate;
	}

}
