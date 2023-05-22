package com.pennant.backend.model.reports;

public class AvailAccount {

	private String accountNum = "";
	private String acType = "";
	private String accountDesc = "";
	private String accountCcy = "";
	private String acBalance = "";
	private String acBalBHD = "";
	private String convertCcy = "";

	public AvailAccount() {
	    super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getAccountNum() {
		return accountNum;
	}

	public void setAccountNum(String accountNum) {
		this.accountNum = accountNum;
	}

	public String getAcType() {
		return acType;
	}

	public void setAcType(String acType) {
		this.acType = acType;
	}

	public String getAccountDesc() {
		return accountDesc;
	}

	public void setAccountDesc(String accountDesc) {
		this.accountDesc = accountDesc;
	}

	public String getAccountCcy() {
		return accountCcy;
	}

	public void setAccountCcy(String accountCcy) {
		this.accountCcy = accountCcy;
	}

	public String getAcBalance() {
		return acBalance;
	}

	public void setAcBalance(String acBalance) {
		this.acBalance = acBalance;
	}

	public String getAcBalBHD() {
		return acBalBHD;
	}

	public void setAcBalBHD(String acBalBHD) {
		this.acBalBHD = acBalBHD;
	}

	public String getConvertCcy() {
		return convertCcy;
	}

	public void setConvertCcy(String convertCcy) {
		this.convertCcy = convertCcy;
	}

}
