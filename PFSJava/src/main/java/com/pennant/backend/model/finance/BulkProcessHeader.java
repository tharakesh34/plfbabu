package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class BulkProcessHeader extends AbstractWorkflowEntity implements Entity {

    private static final long serialVersionUID = 1L;

	private long bulkProcessId= Long.MIN_VALUE;
	private Date fromDate;
	private Date toDate;
	private Date reCalFromDate;
	private Date reCalToDate;
	private boolean excludeDeferement = false;
	private String addTermAfter;
	private BigDecimal newProcessedRate;
	private String reCalType;
	private String lovDescReCalType="";
	private String bulkProcessFor; 
	private String ruleType;
	private String lovDescSqlQuery;
	private boolean lovDescIsOlddataChanged;
	private boolean newRecord=false;
	private String lovValue;
	private BulkProcessHeader befImage;
	private LoggedInUser userDetails;

	private List<BulkProcessDetails> bulkProcessDetailsList;
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();


	public boolean isNew() {
		return isNewRecord();
	}

	public BulkProcessHeader() {
		super();
		setWorkflowId(WorkFlowUtil.getWorkFlowID("BulkProcessHeader"));
	}
	
	public BulkProcessHeader(long id) {
		super();
		this.setId(id);
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public long getBulkProcessId() {
    	return bulkProcessId;
    }
	public void setBulkProcessId(long bulkProcessId) {
    	this.bulkProcessId = bulkProcessId;
    }
	public Date getFromDate() {
    	return fromDate;
    }
	public void setFromDate(Date fromDate) {
    	this.fromDate = fromDate;
    }
	public Date getToDate() {
    	return toDate;
    }
	public void setToDate(Date toDate) {
    	this.toDate = toDate;
    }
	public BigDecimal getNewProcessedRate() {
    	return newProcessedRate;
    }
	public void setNewProcessedRate(BigDecimal newProcessedRate) {
    	this.newProcessedRate = newProcessedRate;
    }
	public String getReCalType() {
    	return reCalType;
    }
	public void setReCalType(String reCalType) {
    	this.reCalType = reCalType;
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
	public BulkProcessHeader getBefImage() {
    	return befImage;
    }
	public void setBefImage(BulkProcessHeader befImage) {
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
	
	public List<BulkProcessDetails> getBulkProcessDetailsList() {
    	return bulkProcessDetailsList;
    }

	public void setBulkProcessDetailsList(List<BulkProcessDetails> lovDescBulkProcessDetails) {
    	this.bulkProcessDetailsList = lovDescBulkProcessDetails;
    }

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
	    return auditDetailMap;
    }

	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
	    this.auditDetailMap = auditDetailMap;
    }

	public String getBulkProcessFor() {
	    return bulkProcessFor;
    }

	public void setBulkProcessFor(String bulkProcessFor) {
	    this.bulkProcessFor = bulkProcessFor;
    }

	public Date getReCalFromDate() {
	    return reCalFromDate;
    }

	public void setReCalFromDate(Date reCalFromDate) {
	    this.reCalFromDate = reCalFromDate;
    }

	public Date getReCalToDate() {
	    return reCalToDate;
    }

	public void setReCalToDate(Date reCalToDate) {
	    this.reCalToDate = reCalToDate;
    }

	public boolean isExcludeDeferement() {
	    return excludeDeferement;
    }

	public void setExcludeDeferement(boolean excludeDeferement) {
	    this.excludeDeferement = excludeDeferement;
    }

	public String getAddTermAfter() {
	    return addTermAfter;
    }

	public void setAddTermAfter(String addTermAfter) {
	    this.addTermAfter = addTermAfter;
    }

	public String getRuleType() {
	    return ruleType;
    }

	public void setRuleType(String ruleType) {
	    this.ruleType = ruleType;
    }

	public String getLovDescSqlQuery() {
	    return lovDescSqlQuery;
    }

	public void setLovDescSqlQuery(String lovDescSqlQuery) {
	    this.lovDescSqlQuery = lovDescSqlQuery;
    }

	public boolean isLovDescIsOlddataChanged() {
    	return lovDescIsOlddataChanged;
    }

	public void setLovDescIsOlddataChanged(boolean lovDescIsOlddataChanged) {
    	this.lovDescIsOlddataChanged = lovDescIsOlddataChanged;
    }

	public String getLovDescReCalType() {
		return lovDescReCalType;
	}
	public void setLovDescReCalType(String lovDescReCalType) {
		this.lovDescReCalType = lovDescReCalType;
	}

}
