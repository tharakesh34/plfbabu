package com.pennant.backend.model.finance;

import java.sql.Timestamp;

import com.pennant.backend.model.Entity;

public class TATDetail implements java.io.Serializable,Entity  {

	private static final long serialVersionUID = 5429008289209512218L;
	
	private String 		module;
	private String 		reference;
	private long 		serialNo;
	private Timestamp 	tATStartTime;
	private Timestamp 	tATEndTime;
	private String 		roleCode;
	private String		finType;
	private boolean		newrecord;
	private Timestamp   triggerTime;
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
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
		
	public long getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(long serialNo) {
		this.serialNo = serialNo;
	}
	
	public Timestamp gettATStartTime() {
		return tATStartTime;
	}
	public void settATStartTime(Timestamp tATStartTime) {
		this.tATStartTime = tATStartTime;
	}
	
	public Timestamp gettATEndTime() {
		return tATEndTime;
	}
	public void settATEndTime(Timestamp tATEndTime) {
		this.tATEndTime = tATEndTime;
	}
	
	public boolean isNewrecord() {
		return newrecord;
	}
	public void setNewrecord(boolean newrecord) {
		this.newrecord = newrecord;
	}
	
	public String getFinType() {
		return finType;
	}
	public void setFinType(String finType) {
		this.finType = finType;
	}
	
	@Override
    public boolean isNew() {
	    return false;
    }
	@Override
    public long getId() {
	    return 0;
    }
	@Override
    public void setId(long id) {
	    
    }
	
	public String getRoleCode() {
		return roleCode;
	}
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	
	public Timestamp getTriggerTime() {
		return triggerTime;
	}
	public void setTriggerTime(Timestamp triggerTime) {
		this.triggerTime = triggerTime;
	}
	
	
}

