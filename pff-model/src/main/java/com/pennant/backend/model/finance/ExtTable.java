package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.util.Date;

public class ExtTable implements Serializable {
	private static final long serialVersionUID = -4823984437205894855L;

	private String Id;
	private String accountBalance;

	// Control table fields.
	private String sys_Code;
	private Date cob_Date;
	private String target_Sys_Code;
	private String status;

	// Bench marking information
	private String key_Code;
	private String key_desc;
	private String key_Value;

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		this.Id = id;
	}

	public String getAccountBalance() {
		return accountBalance;
	}

	public void setAccountBalance(String accountBalance) {
		this.accountBalance = accountBalance;
	}

	public String getSys_Code() {
		return sys_Code;
	}

	public void setSys_Code(String sysCode) {
		this.sys_Code = sysCode;
	}

	public Date getCob_Date() {
		return cob_Date;
	}

	public void setCob_Date(Date cobDate) {
		this.cob_Date = cobDate;
	}

	public String getTarget_Sys_Code() {
		return target_Sys_Code;
	}

	public void setTarget_Sys_Code(String targetSysCode) {
		this.target_Sys_Code = targetSysCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getKey_Code() {
		return key_Code;
	}

	public void setKey_Code(String keyCode) {
		this.key_Code = keyCode;
	}

	public String getKey_desc() {
		return key_desc;
	}

	public void setKey_desc(String keyDesc) {
		this.key_desc = keyDesc;
	}

	public String getKey_Value() {
		return key_Value;
	}

	public void setKey_Value(String keyValue) {
		this.key_Value = keyValue;
	}
}
