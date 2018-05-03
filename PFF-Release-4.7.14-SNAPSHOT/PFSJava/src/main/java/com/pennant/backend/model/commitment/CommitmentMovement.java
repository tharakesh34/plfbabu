package com.pennant.backend.model.commitment;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class CommitmentMovement extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -2091525494536161888L;
	
	private String cmtReference;
	private String finReference;
	private String finBranch;
	private String finType;
	private Date movementDate;
	private long movementOrder;
	private String movementType;
	private BigDecimal cmtCharges;
	private BigDecimal movementAmount;
	private BigDecimal cmtAmount;
	private BigDecimal cmtUtilizedAmount;
	private BigDecimal cmtAvailable;
	private long linkedTranId;
	private boolean newRecord=false;
	private String lovValue;
	private CommitmentMovement befImage;
	private LoggedInUser userDetails;
	private HashMap<String, AuditDetail> lovDescAuditDetailMap = new HashMap<String, AuditDetail>();

	public boolean isNew() {
		return isNewRecord();
	}
	public CommitmentMovement() {
		super();
	}


	public CommitmentMovement(String id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields(){
		return new HashSet<String>();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return finReference;
	}

	public void setId (String id) {
		this.finReference = id;
	}
	public String getCmtReference() {
    	return cmtReference;
    }
	public void setCmtReference(String cmtReference) {
    	this.cmtReference = cmtReference;
    }
	public String getFinReference() {
    	return finReference;
    }
	public void setFinReference(String finReference) {
    	this.finReference = finReference;
    }
	public String getFinBranch() {
    	return finBranch;
    }
	public void setFinBranch(String finBranch) {
    	this.finBranch = finBranch;
    }
	public String getFinType() {
    	return finType;
    }
	public void setFinType(String finType) {
    	this.finType = finType;
    }
	public Date getMovementDate() {
    	return movementDate;
    }
	public void setMovementDate(Date movementDate) {
    	this.movementDate = movementDate;
    }
	public long getMovementOrder() {
    	return movementOrder;
    }
	public void setMovementOrder(long movementOrder) {
    	this.movementOrder = movementOrder;
    }
	public String getMovementType() {
    	return movementType;
    }
	public void setMovementType(String movementType) {
    	this.movementType = movementType;
    }
	public BigDecimal getMovementAmount() {
    	return movementAmount;
    }
	public void setMovementAmount(BigDecimal movementAmount) {
    	this.movementAmount = movementAmount;
    }
	public BigDecimal getCmtAmount() {
    	return cmtAmount;
    }
	public void setCmtAmount(BigDecimal cmtAmount) {
    	this.cmtAmount = cmtAmount;
    }
	public BigDecimal getCmtUtilizedAmount() {
    	return cmtUtilizedAmount;
    }
	public void setCmtUtilizedAmount(BigDecimal cmtUtilizedAmount) {
    	this.cmtUtilizedAmount = cmtUtilizedAmount;
    }
	public BigDecimal getCmtAvailable() {
    	return cmtAvailable;
    }
	public void setCmtAvailable(BigDecimal cmtAvailable) {
    	this.cmtAvailable = cmtAvailable;
    }
	public long getLinkedTranId() {
    	return linkedTranId;
    }
	public void setLinkedTranId(long linkedTranId) {
    	this.linkedTranId = linkedTranId;
    }
	
	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
	public String getLovValue() {
		return lovValue;
	}
	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}
	
	public CommitmentMovement getBefImage() {
    	return befImage;
    }
	public void setBefImage(CommitmentMovement befImage) {
    	this.befImage = befImage;
    }
	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}	

	public HashMap<String, AuditDetail> getLovDescAuditDetailMap() {
    	return lovDescAuditDetailMap;
    }
	public void setLovDescAuditDetailMap(HashMap<String, AuditDetail> lovDescAuditDetailMap) {
    	this.lovDescAuditDetailMap = lovDescAuditDetailMap;
    }
	
	public void setCmtCharges(BigDecimal cmtCharges) {
	    this.cmtCharges = cmtCharges;
    }
	public BigDecimal getCmtCharges() {
	    return cmtCharges;
    }
}
