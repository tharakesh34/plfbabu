package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pennant.backend.model.Entity;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "finReference", "linkedTranId", "finStartDate", "maturityDate", "finCurrAssetValue",
		"finAdvancePayments", "postingList", "returnStatus" })
@XmlAccessorType(XmlAccessType.NONE)
public class DisbursementServiceReq extends AbstractWorkflowEntity implements Entity {

	private static final long serialVersionUID = -8968957488386061313L;

	@XmlElement
	private String finReference;
	@XmlElement
	private long linkedTranId;
	@XmlElement
	private Date finStartDate;
	@XmlElement
	private Date maturityDate;
	@JsonProperty("totalDisbAmount")
	private BigDecimal finCurrAssetValue;
	@JsonProperty("disbursement")
	private List<FinAdvancePayments> finAdvancePayments;
	@JsonProperty("postings")
	private List<ReturnDataSet> postingList;
	@XmlElement
	private WSReturnStatus returnStatus;
	@XmlElement
	private String serviceReqNo;

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finRefernce) {
		this.finReference = finRefernce;
	}

	public long getLinkedTranId() {
		return linkedTranId;
	}

	public void setLinkedTranId(long linkedTranId) {
		this.linkedTranId = linkedTranId;
	}

	public Date getFinStartDate() {
		return finStartDate;
	}

	public void setFinStartDate(Date finStartDate) {
		this.finStartDate = finStartDate;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public BigDecimal getFinCurrAssetValue() {
		return finCurrAssetValue;
	}

	public void setFinCurrAssetValue(BigDecimal finCurrAssetValue) {
		this.finCurrAssetValue = finCurrAssetValue;
	}

	public List<FinAdvancePayments> getFinAdvancePayments() {
		return finAdvancePayments;
	}

	public void setFinAdvancePayments(List<FinAdvancePayments> finAdvancePayments) {
		this.finAdvancePayments = finAdvancePayments;
	}

	public List<ReturnDataSet> getPostingList() {
		return postingList;
	}

	public void setPostingList(List<ReturnDataSet> postingList) {
		this.postingList = postingList;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
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

	public String getServiceReqNo() {
		return serviceReqNo;
	}

	public void setServiceReqNo(String serviceReqNo) {
		this.serviceReqNo = serviceReqNo;
	}

}
