package com.pennant.coreinterface.model.accountposting;

import java.io.Serializable;
import java.util.Date;

public class SecondaryDebitAccount implements Serializable {

	private static final long serialVersionUID = 541486821922538453L;

	public SecondaryDebitAccount() {
		super();
	}

	private String secondaryDebitAccount;
	private Date scheduleDate;
	private String custCIF;
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getSecondaryDebitAccount() {
		return secondaryDebitAccount;
	}

	public void setSecondaryDebitAccount(String secondaryDebitAccount) {
		this.secondaryDebitAccount = secondaryDebitAccount;
	}

	public Date getScheduleDate() {
		return scheduleDate;
	}

	public void setScheduleDate(Date scheduleDate) {
		this.scheduleDate = scheduleDate;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}
}
