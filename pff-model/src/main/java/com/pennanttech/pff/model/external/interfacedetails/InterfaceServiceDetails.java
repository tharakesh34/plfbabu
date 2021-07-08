/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

package com.pennanttech.pff.model.external.interfacedetails;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@XmlAccessorType(XmlAccessType.NONE)
public class InterfaceServiceDetails implements Serializable {
	private static final long serialVersionUID = 3832850641524383002L;
	private String init_reference;
	@XmlElement
	private String cif;
	@XmlElement
	private String serviceName;
	private long custId;
	@XmlElement
	private String request;
	@XmlElement
	private String response;
	private String custName;
	private String remarks;
	private String adverse_Observed;
	@XmlElement
	private boolean initiateflag;
	@XmlElement
	private String uniqueId;
	@XmlElement
	private String matchType;
	@XmlElement
	private String cibilType;
	@XmlElement
	private String dpdClusterSegment;
	@XmlElement
	private String cibilTuefResponse;
	@XmlElement
	private String cibilScore;
	@XmlElement
	private String status;
	@XmlElement
	private byte[] resoponseAsPdf;
	private String cibilInitateDate;
	@JsonProperty("customer_status")
	private String custStatus;
	private String dedupDecision;
	private String dedupComments;
	@JsonProperty("dedup_result")
	private String dedupResult;
	private String applicantId;
	@JsonProperty("source_target")
	private String sourceOrTarget;
	private String segment;
	@JsonProperty("dedup_lan_matches")
	private String dedupLanMatches;
	@JsonProperty("dedup_target_remarks")
	private String dedupTargetRemarks;
	private String customerName;
	private String customerId;
	private String hash_value;
	private String bankingName;
	private String bankAcctNumber;
	private String bankId;
	private Timestamp initiatedTime;
	private String verificationStatus;
	private String errorDetails;

	public String getErrorDetails() {
		return errorDetails;
	}

	public void setErrorDetails(String errorDetails) {
		this.errorDetails = errorDetails;
	}

	public String getHash_value() {
		return hash_value;
	}

	public void setHash_value(String hash_value) {
		this.hash_value = hash_value;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getAdverse_Observed() {
		return adverse_Observed;
	}

	public void setAdverse_Observed(String adverse_Observed) {
		this.adverse_Observed = adverse_Observed;
	}

	public String getInit_reference() {
		return init_reference;
	}

	public void setInit_reference(String init_reference) {
		this.init_reference = init_reference;
	}

	public String getCif() {
		return cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public long getCustId() {
		return custId;
	}

	public void setCustId(long custId) {
		this.custId = custId;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public boolean isInitiateflag() {
		return initiateflag;
	}

	public void setInitiateflag(boolean initiateflag) {
		this.initiateflag = initiateflag;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getMatchType() {
		return matchType;
	}

	public void setMatchType(String matchType) {
		this.matchType = matchType;
	}

	public String getCibilType() {
		return cibilType;
	}

	public void setCibilType(String cibilType) {
		this.cibilType = cibilType;
	}

	public String getDpdClusterSegment() {
		return dpdClusterSegment;
	}

	public void setDpdClusterSegment(String dpdClusterSegment) {
		this.dpdClusterSegment = dpdClusterSegment;
	}

	public String getCibilTuefResponse() {
		return cibilTuefResponse;
	}

	public void setCibilTuefResponse(String cibilTuefResponse) {
		this.cibilTuefResponse = cibilTuefResponse;
	}

	public String getCibilScore() {
		return cibilScore;
	}

	public void setCibilScore(String cibilScore) {
		this.cibilScore = cibilScore;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public byte[] getResoponseAsPdf() {
		return resoponseAsPdf;
	}

	public void setResoponseAsPdf(byte[] resoponseAsPdf) {
		this.resoponseAsPdf = resoponseAsPdf;
	}

	public String getCibilInitateDate() {
		return cibilInitateDate;
	}

	public void setCibilInitateDate(String cibilInitateDate) {
		this.cibilInitateDate = cibilInitateDate;
	}

	public String getBankingName() {
		return bankingName;
	}

	public void setBankingName(String bankingName) {
		this.bankingName = bankingName;
	}

	public String getBankAcctNumber() {
		return bankAcctNumber;
	}

	public void setBankAcctNumber(String bankAcctNumber) {
		this.bankAcctNumber = bankAcctNumber;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public Timestamp getInitiatedTime() {
		return initiatedTime;
	}

	public void setInitiatedTime(Timestamp initiatedTime) {
		this.initiatedTime = initiatedTime;
	}

	public String getCustStatus() {
		return custStatus;
	}

	public void setCustStatus(String custStatus) {
		this.custStatus = custStatus;
	}

	public String getDedupDecision() {
		return dedupDecision;
	}

	public void setDedupDecision(String dedupDecision) {
		this.dedupDecision = dedupDecision;
	}

	public String getDedupComments() {
		return dedupComments;
	}

	public void setDedupComments(String dedupComments) {
		this.dedupComments = dedupComments;
	}

	public String getDedupResult() {
		return dedupResult;
	}

	public void setDedupResult(String dedupResult) {
		this.dedupResult = dedupResult;
	}

	public String getApplicantId() {
		return applicantId;
	}

	public void setApplicantId(String applicantId) {
		this.applicantId = applicantId;
	}

	public String getSourceOrTarget() {
		return sourceOrTarget;
	}

	public void setSourceOrTarget(String sourceOrTarget) {
		this.sourceOrTarget = sourceOrTarget;
	}

	public String getSegment() {
		return segment;
	}

	public void setSegment(String segment) {
		this.segment = segment;
	}

	public String getDedupLanMatches() {
		return dedupLanMatches;
	}

	public void setDedupLanMatches(String dedupLanMatches) {
		this.dedupLanMatches = dedupLanMatches;
	}

	public String getDedupTargetRemarks() {
		return dedupTargetRemarks;
	}

	public void setDedupTargetRemarks(String dedupTargetRemarks) {
		this.dedupTargetRemarks = dedupTargetRemarks;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getVerificationStatus() {
		return verificationStatus;
	}

	public void setVerificationStatus(String verificationStatus) {
		this.verificationStatus = verificationStatus;
	}

}
