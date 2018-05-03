package com.pennant.app.util;

import java.io.Serializable;
import java.util.Date;

public class MailLog implements Serializable {

    private static final long serialVersionUID = -6410299021526884222L;
    
    public MailLog() {
    	super();
    }
    
	public long mailReference;
	public String module;
	public String reference;
	public String mailType;
	public Date valueDate;
	public long reqUser;
	public String reqUserRole;
	public int uniqueRef;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getMailReference() {
		return mailReference;
	}

	public void setMailReference(long mailReference) {
		this.mailReference = mailReference;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getMailType() {
		return mailType;
	}

	public void setMailType(String mailType) {
		this.mailType = mailType;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public long getReqUser() {
		return reqUser;
	}

	public void setReqUser(long reqUser) {
		this.reqUser = reqUser;
	}

	public String getReqUserRole() {
		return reqUserRole;
	}

	public void setReqUserRole(String reqUserRole) {
		this.reqUserRole = reqUserRole;
	}

	public int getUniqueRef() {
		return uniqueRef;
	}

	public void setUniqueRef(int uniqueRef) {
		this.uniqueRef = uniqueRef;
	}
}
