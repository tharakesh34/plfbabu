package com.pennant.backend.model.finance;

import java.util.Date;

import com.pennant.backend.model.Entity;

public class FinanceRejectDetail implements java.io.Serializable, Entity {
	private static final long serialVersionUID = 1L;
	private long 		rejectId = Long.MIN_VALUE;
	private String 		finReference;
	private String 		rejectStatus;
	private String		rejectReason;
	private String		roleCode;
	private long 		rejectedUser;
	private Date		rejectedDate;
	

	public FinanceRejectDetail(long id) {
		this.setId(id);
	}

	public FinanceRejectDetail() {
	    
    }

	//Getter and Setter methods
	
	public long getId() {
		return rejectId;
	}
	public void setId (long id) {
		this.rejectId = id;
	}

	public long getRejectId() {
		return rejectId;
	}
	public void setRejectId(long rejectId) {
		this.rejectId = rejectId;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getRejectStatus() {
		return rejectStatus;
	}

	public void setRejectStatus(String rejectStatus) {
		this.rejectStatus = rejectStatus;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public long getRejectedUser() {
		return rejectedUser;
	}

	public void setRejectedUser(long rejectedUser) {
		this.rejectedUser = rejectedUser;
	}

	public Date getRejectedDate() {
		return rejectedDate;
	}

	public void setRejectedDate(Date rejectedDate) {
		this.rejectedDate = rejectedDate;
	}

	@Override
    public boolean isNew() {
	    return false;
    }
	
}
