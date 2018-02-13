package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

public class BulkRateChangeHeader extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 1L;

	private String bulkRateChangeRef;
	private String finType; 
	private Date fromDate;
	private Date toDate;
 	private BigDecimal rateChange = BigDecimal.ZERO;
	private String reCalType;
	private String ruleType;
	private String lovDescReCalType = "";
	private String lovDescSqlQuery;
	private String lovDescFinTypeDesc;
	private String lovDescQueryDesc;
	private boolean lovDescIsOlddataChanged;
	private boolean oldFinDataChanged;
	private boolean oldRateChangeDataChanged;

	//Common Fields
	private boolean newRecord = false;
 	private BulkRateChangeHeader befImage;
	private LoggedInUser userDetails;
	private String status;
	
	private List<BulkRateChangeDetails> bulkRateChangeDetailsList;
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();


	public BulkRateChangeHeader() {
		super();
		setWorkflowId(WorkFlowUtil.getWorkFlowID("BulkRateChangeHeader"));
	}

	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
		excludeFields.add("oldFinDataChanged");
		excludeFields.add("oldRateChangeDataChanged");
		return excludeFields;
	}
	
	public boolean isNew() {
		return isNewRecord();
	}

	public BulkRateChangeHeader(String id) {
		super();
		this.setId(id);
	}

	public String getId() {
		return bulkRateChangeRef;
	}

	public void setId (String id) {
		this.bulkRateChangeRef = id;
	}

	public String getBulkRateChangeRef() {
		return bulkRateChangeRef;
	}


	public void setBulkRateChangeRef(String bulkRateChangeRef) {
		this.bulkRateChangeRef = bulkRateChangeRef;
	}


	public String getFinType() {
		return finType;
	}


	public void setFinType(String finType) {
		this.finType = finType;
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

	public BigDecimal getRateChange() {
		return rateChange;
	}

	public void setRateChange(BigDecimal rateChange) {
		this.rateChange = rateChange;
	}
	
	public String getReCalType() {
		return reCalType;
	}


	public void setReCalType(String reCalType) {
		this.reCalType = reCalType;
	}


	public String getRuleType() {
		return ruleType;
	}


	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}


	public String getLovDescReCalType() {
		return lovDescReCalType;
	}


	public void setLovDescReCalType(String lovDescReCalType) {
		this.lovDescReCalType = lovDescReCalType;
	}


	public String getLovDescSqlQuery() {
		return lovDescSqlQuery;
	}


	public void setLovDescSqlQuery(String lovDescSqlQuery) {
		this.lovDescSqlQuery = lovDescSqlQuery;
	}


	public String getLovDescFinTypeDesc() {
		return lovDescFinTypeDesc;
	}

	public void setLovDescFinTypeDesc(String lovDescFinTypeDesc) {
		this.lovDescFinTypeDesc = lovDescFinTypeDesc;
	}

	public boolean isNewRecord() {
		return newRecord;
	}


	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public BulkRateChangeHeader getBefImage() {
		return befImage;
	}


	public void setBefImage(BulkRateChangeHeader befImage) {
		this.befImage = befImage;
	}


	public LoggedInUser getUserDetails() {
		return userDetails;
	}


	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}


	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public List<BulkRateChangeDetails> getBulkRateChangeDetailsList() {
		return bulkRateChangeDetailsList;
	}

	public void setBulkRateChangeDetailsList(
			List<BulkRateChangeDetails> bulkRateChangeDetailsList) {
		this.bulkRateChangeDetailsList = bulkRateChangeDetailsList;
	}

	public boolean isOldFinDataChanged() {
		return oldFinDataChanged;
	}

	public void setOldFinDataChanged(boolean oldFinDataChanged) {
		this.oldFinDataChanged = oldFinDataChanged;
	}

	public boolean isOldRateChangeDataChanged() {
		return oldRateChangeDataChanged;
	}

	public void setOldRateChangeDataChanged(boolean oldRateChangeDataChanged) {
		this.oldRateChangeDataChanged = oldRateChangeDataChanged;
	}

	public boolean isLovDescIsOlddataChanged() {
		return lovDescIsOlddataChanged;
	}

	public void setLovDescIsOlddataChanged(boolean lovDescIsOlddataChanged) {
		this.lovDescIsOlddataChanged = lovDescIsOlddataChanged;
	}

	public String getLovDescQueryDesc() {
		return lovDescQueryDesc;
	}

	public void setLovDescQueryDesc(String lovDescQueryDesc) {
		this.lovDescQueryDesc = lovDescQueryDesc;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
