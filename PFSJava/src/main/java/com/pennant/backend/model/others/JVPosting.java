/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 * FILE HEADER *
 ******************************************************************************************** 
 * * FileName : JVPosting.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-06-2013 * * Modified Date :
 ******************************************************************************************** 
 * 21-06-2013 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 21-06-2013 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */
package com.pennant.backend.model.others;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.RequestSource;

/**
 * Model class for the <b>JVPosting table</b>.<br>
 * 
 */
@XmlRootElement(name = "posting")
@XmlType(propOrder = { "branch", "batch", "currency", "reference", "JVPostingEntrysList" })
public class JVPosting extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	@XmlElement
	private String batch;
	private String filename;
	private long batchReference;
	@XmlElement
	private String currency;
	private String exchangeRateType;
	private String RateTypeDescription;
	private BigDecimal totDebitsByBatchCcy;
	private BigDecimal totCreditsByBatchCcy;
	private int debitCount;
	private int creditsCount;
	private String batchPurpose;
	private String validationStatus = "";
	private String batchPostingStatus = "";
	private String txnId = "";
	private Date postingDate;
	private String finType = "";
	@XmlElement
	private String branch = "";
	private String branchDesc = "";
	private boolean rePostingModule = false;
	@XmlElementWrapper(name = "postingEntry")
	@XmlElement(name = "postingEntry")
	private List<JVPostingEntry> JVPostingEntrysList = new ArrayList<JVPostingEntry>();
	private List<JVPostingEntry> postingEntryList = new ArrayList<JVPostingEntry>();
	private List<JVPostingEntry> deletedJVPostingEntryList = new ArrayList<JVPostingEntry>();
	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
	private String expReference;
	private String lovValue;
	private JVPosting befImage;
	private LoggedInUser userDetails;
	private String postAgainst;
	@XmlElement
	private String reference;
	private String postingDivision;
	private String divisionCodeDesc;
	private String finSourceID;
	private RequestSource requestSource = RequestSource.UI;
	private long uploadID;

	public JVPosting() {
		super();
	}

	public JVPosting(String id) {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("branchDesc");
		excludeFields.add("RateTypeDescription");
		excludeFields.add("JVPostingEntrysList");
		excludeFields.add("rePostingModule");
		excludeFields.add("txnId");
		excludeFields.add("finType");
		excludeFields.add("divisionCodeDesc");
		excludeFields.add("finSourceID");
		excludeFields.add("requestSource");
		excludeFields.add("uploadID");
		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getId() {
		return batchReference;
	}

	public void setId(long id) {
		this.batchReference = id;
	}

	public long getBatchReference() {
		return batchReference;
	}

	public void setBatchReference(long batchReference) {
		this.batchReference = batchReference;
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
	}

	public int getDebitCount() {
		return debitCount;
	}

	public void setDebitCount(int debitCount) {
		this.debitCount = debitCount;
	}

	public int getCreditsCount() {
		return creditsCount;
	}

	public void setCreditsCount(int creditsCount) {
		this.creditsCount = creditsCount;
	}

	public BigDecimal getTotDebitsByBatchCcy() {
		return totDebitsByBatchCcy;
	}

	public void setTotDebitsByBatchCcy(BigDecimal totDebitsByBatchCcy) {
		this.totDebitsByBatchCcy = totDebitsByBatchCcy;
	}

	public BigDecimal getTotCreditsByBatchCcy() {
		return totCreditsByBatchCcy;
	}

	public void setTotCreditsByBatchCcy(BigDecimal totCreditsByBatchCcy) {
		this.totCreditsByBatchCcy = totCreditsByBatchCcy;
	}

	public String getBatchPurpose() {
		return batchPurpose;
	}

	public void setBatchPurpose(String batchPurpose) {
		this.batchPurpose = batchPurpose;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public JVPosting getBefImage() {
		return this.befImage;
	}

	public void setBefImage(JVPosting beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public void setJVPostingEntrysList(List<JVPostingEntry> jVPostingEntrys) {
		JVPostingEntrysList = jVPostingEntrys;
	}

	public List<JVPostingEntry> getJVPostingEntrysList() {
		return JVPostingEntrysList;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public String getValidationStatus() {
		return validationStatus;
	}

	public void setValidationStatus(String validationStatus) {
		this.validationStatus = validationStatus;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getExchangeRateType() {
		return exchangeRateType;
	}

	public void setExchangeRateType(String exchangeRateType) {
		this.exchangeRateType = exchangeRateType;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getRateTypeDescription() {
		return RateTypeDescription;
	}

	public void setRateTypeDescription(String rateTypeDescription) {
		RateTypeDescription = rateTypeDescription;
	}

	public List<JVPostingEntry> getDeletedJVPostingEntryList() {
		return deletedJVPostingEntryList;
	}

	public void setDeletedJVPostingEntryList(List<JVPostingEntry> deletedJVPostingEntryList) {
		this.deletedJVPostingEntryList = deletedJVPostingEntryList;
	}

	public String getBatchPostingStatus() {
		return batchPostingStatus;
	}

	public void setBatchPostingStatus(String batchPostingStatus) {
		this.batchPostingStatus = batchPostingStatus;
	}

	public boolean isRePostingModule() {
		return rePostingModule;
	}

	public void setRePostingModule(boolean rePostingModule) {
		this.rePostingModule = rePostingModule;
	}

	public String getTxnId() {
		return txnId;
	}

	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	public Date getPostingDate() {
		return postingDate;
	}

	public void setPostingDate(Date postingDate) {
		this.postingDate = postingDate;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public List<JVPostingEntry> getPostingEntryList() {
		return postingEntryList;
	}

	public void setPostingEntryList(List<JVPostingEntry> postingEntryList) {
		this.postingEntryList = postingEntryList;
	}

	public String getBranchDesc() {
		return branchDesc;
	}

	public void setBranchDesc(String branchDesc) {
		this.branchDesc = branchDesc;
	}

	public String getExpReference() {
		return expReference;
	}

	public void setExpReference(String expReference) {
		this.expReference = expReference;
	}

	public String getPostAgainst() {
		return postAgainst;
	}

	public void setPostAgainst(String postAgainst) {
		this.postAgainst = postAgainst;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getPostingDivision() {
		return postingDivision;
	}

	public void setPostingDivision(String postingDivision) {
		this.postingDivision = postingDivision;
	}

	public String getDivisionCodeDesc() {
		return divisionCodeDesc;
	}

	public void setDivisionCodeDesc(String divisionCodeDesc) {
		this.divisionCodeDesc = divisionCodeDesc;
	}

	public String getFinSourceID() {
		return finSourceID;
	}

	public void setFinSourceID(String finSourceID) {
		this.finSourceID = finSourceID;
	}

	public RequestSource getRequestSource() {
		return requestSource;
	}

	public void setRequestSource(RequestSource requestSource) {
		this.requestSource = requestSource;
	}

	public long getUploadID() {
		return uploadID;
	}

	public void setUploadID(long uploadID) {
		this.uploadID = uploadID;
	}

}
