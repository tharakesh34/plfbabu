package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class BulkRateChangeDetails extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private String bulkRateChangeRef;
	private String finReference;
	private String finBranch;
	private String finCCY;
	private String custCIF;
	private boolean allowRateChange = false;
	private BigDecimal finAmount 	= BigDecimal.ZERO;
	private BigDecimal oldProfitRate 	= BigDecimal.ZERO;
	private BigDecimal newProfitRate 	= BigDecimal.ZERO;
	private BigDecimal oldProfit 	= BigDecimal.ZERO;
	private BigDecimal newProfit 	= BigDecimal.ZERO;

	// View Fields
	private int lovDescFinFormatter;

	//Rule Related Fields
	private Date lovDescEventFromDate;
	private Date lovDescEventToDate;
	private String lovDescEventFinType;

	//Common Fields
	private boolean newRecord = false;
	private BulkRateChangeDetails befImage;
	private LoggedInUser userDetails;

	private String status;
	private String errorMsg;
	
	public BulkRateChangeDetails() {
		super();
		setWorkflowId(WorkFlowUtil.getWorkFlowID("BulkRateChangeDetails"));
	}

	public Set<String> getExcludeFields(){
		Set<String> excludeFields = new HashSet<String>();
		return excludeFields;
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public BulkRateChangeDetails(String id) {
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

	public String getFinCCY() {
		return finCCY;
	}

	public void setFinCCY(String finCCY) {
		this.finCCY = finCCY;
	}

	public BigDecimal getFinAmount() {
		return finAmount;
	}

	public void setFinAmount(BigDecimal finAmount) {
		this.finAmount = finAmount;
	}

	public int getLovDescFinFormatter() {
		return lovDescFinFormatter;
	}

	public void setLovDescFinFormatter(int lovDescFinFormatter) {
		this.lovDescFinFormatter = lovDescFinFormatter;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public boolean isAllowRateChange() {
		return allowRateChange;
	}

	public void setAllowRateChange(boolean allowRateChange) {
		this.allowRateChange = allowRateChange;
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

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public BulkRateChangeDetails getBefImage() {
		return befImage;
	}

	public void setBefImage(BulkRateChangeDetails befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public BigDecimal getNewProfit() {
		return newProfit;
	}

	public void setNewProfit(BigDecimal newProfit) {
		this.newProfit = newProfit;
	}

	public BigDecimal getOldProfit() {
		return oldProfit;
	}

	public void setOldProfit(BigDecimal oldProfit) {
		this.oldProfit = oldProfit;
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

	public String getLovDescEventFinType() {
		return lovDescEventFinType;
	}

	public void setLovDescEventFinType(String lovDescEventFinType) {
		this.lovDescEventFinType = lovDescEventFinType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

}