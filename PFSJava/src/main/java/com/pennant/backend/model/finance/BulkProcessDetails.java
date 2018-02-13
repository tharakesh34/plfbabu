package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

public class BulkProcessDetails extends AbstractWorkflowEntity implements Entity {

    private static final long serialVersionUID = 1L;

    private String finReference;
	private String finType; 
	private String finCCY;
	private long custID;
	private String finBranch;
	private String lovDescProductCode;
	private Date lovDescEventFromDate;
	private Date lovDescEventToDate;
	private Date deferedSchdDate;
	private Date reCalStartDate;
	private Date reCalEndDate;
	
	private long bulkProcessId;
	private BigDecimal oldProfitRate = BigDecimal.ZERO;
	private BigDecimal newProfitRate = BigDecimal.ZERO;
	private int profitChange = 0;
	private boolean alwProcess=false;
     
	private boolean newRecord=false;
	private String lovValue;
	private BulkProcessDetails befImage;
	private LoggedInUser userDetails;
	private String custName;
	private String profitDayBasisDesc;
	private String schdMethodDesc;
	private String rcdMaintainSts;
	private boolean schdChangeAlw;
	public boolean isNew() {
		return isNewRecord();
	}

	public BulkProcessDetails() {
		super();
		setWorkflowId(WorkFlowUtil.getWorkFlowID("BulkProcessDetails"));
	}
	
	public BulkProcessDetails(long id) {
		super();
		this.setId(id);
	}
	
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("finType");
		excludeFields.add("finCCY");
		excludeFields.add("custID");
		excludeFields.add("finBranch");
		excludeFields.add("custName");
		excludeFields.add("profitDayBasisDesc");
		excludeFields.add("schdMethodDesc");
		excludeFields.add("rcdMaintainSts");
		excludeFields.add("schdChangeAlw");
		
		return excludeFields;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public String getFinReference() {
    	return finReference;
    }
	public void setFinReference(String finReference) {
    	this.finReference = finReference;
    }
	
	
	 public String getLovDescProductCode() {
    	return lovDescProductCode;
    }
	public void setLovDescProductCode(String lovDescProductCode) {
    	this.lovDescProductCode = lovDescProductCode;
    } 
	
	public Date getLovDescEventFromDate() {
    	return lovDescEventFromDate;
    }
	public void setLovDescEventFromDate(Date lovDescEventFromDate) {
    	this.lovDescEventFromDate = lovDescEventFromDate;
    }
	
	public Date getLovDescEventToDate() {
    	return lovDescEventToDate;
    }
	public void setLovDescEventToDate(Date lovDescEventToDate) {
    	this.lovDescEventToDate = lovDescEventToDate;
    }
	public long getBulkProcessId() {
    	return bulkProcessId;
    }
	public void setBulkProcessId(long bulkProcessId) {
    	this.bulkProcessId = bulkProcessId;
    }
	public BigDecimal getOldProfitRate() {
    	return oldProfitRate;
    }
	public void setOldProfitRate(BigDecimal oldProfitRate) {
    	this.oldProfitRate = oldProfitRate;
    }
	public BigDecimal getNewProfitRate() {
    	return newProfitRate;
    }
	public void setNewProfitRate(BigDecimal newProfitRate) {
    	this.newProfitRate = newProfitRate;
    }
	public int getProfitChange() {
    	return profitChange;
    }
	public void setProfitChange(int profitChange) {
    	this.profitChange = profitChange;
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
	public BulkProcessDetails getBefImage() {
    	return befImage;
    }
	public void setBefImage(BulkProcessDetails befImage) {
    	this.befImage = befImage;
    }
	public LoggedInUser getUserDetails() {
    	return userDetails;
    }
	public void setUserDetails(LoggedInUser userDetails) {
    	this.userDetails = userDetails;
    }
	public long getId() {
	    return bulkProcessId;
    }
	public void setId(long id) {
	    this.bulkProcessId = id;
    }

	public Date getDeferedSchdDate() {
	    return deferedSchdDate;
    }

	public void setDeferedSchdDate(Date deferedSchdDate) {
	    this.deferedSchdDate = deferedSchdDate;
    }

	public boolean isAlwProcess() {
    	return alwProcess;
    }

	public void setAlwProcess(boolean alwProcess) {
    	this.alwProcess = alwProcess;
    }

	public Date getReCalStartDate() {
    	return reCalStartDate;
    }

	public void setReCalStartDate(Date reCalStartDate) {
    	this.reCalStartDate = reCalStartDate;
    }

	public Date getReCalEndDate() {
    	return reCalEndDate;
    }

	public void setReCalEndDate(Date reCalEndDate) {
    	this.reCalEndDate = reCalEndDate;
    }

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getFinCCY() {
		return finCCY;
	}

	public void setFinCCY(String finCCY) {
		this.finCCY = finCCY;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public String getFinBranch() {
		return finBranch;
	}

	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getProfitDayBasisDesc() {
		return profitDayBasisDesc;
	}

	public void setProfitDayBasisDesc(String profitDayBasisDesc) {
		this.profitDayBasisDesc = profitDayBasisDesc;
	}

	public String getSchdMethodDesc() {
		return schdMethodDesc;
	}

	public void setSchdMethodDesc(String schdMethodDesc) {
		this.schdMethodDesc = schdMethodDesc;
	}
	public String getRcdMaintainSts() {
		return rcdMaintainSts;
	}

	public void setRcdMaintainSts(String rcdMaintainSts) {
		this.rcdMaintainSts = rcdMaintainSts;
	}

	public boolean isSchdChangeAlw() {
		return schdChangeAlw;
	}

	public void setSchdChangeAlw(boolean schdChangeAlw) {
		this.schdChangeAlw = schdChangeAlw;
	}



}
