package com.pennant.backend.model.finance.covenant;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.pennant.backend.model.documentdetails.DocumentDetails;

@XmlAccessorType(XmlAccessType.NONE)
public class Covenant extends CovenantType {
	private static final long serialVersionUID = 1L;

	private String keyReference;
	@XmlElement
	private Date receivableDate;
	private boolean documentReceived;
	@XmlElement
	private Date documentReceivedDate;
	private Date nextFrequencyDate;
	private Date graceDueDate;
	@XmlElement
	private boolean internalUse;
	@XmlElement
	private Date extendedDate;
	@XmlElement(name = "description")
	private String remarks;
	private byte[] remarks1;
	@XmlElement(name = "remarks")
	private String additionalField1;
	@XmlElement(name = "standardValue")
	private String additionalField2;
	@XmlElement(name = "actualValue")
	private String additionalField3;
	private String additionalField4;
	@XmlElement
	private String mandatoryRole;
	private String mandRoleDescription;
	@XmlElement
	private boolean allowWaiver;
	@XmlElement
	private String notifyTo;
	private String module;
	private String covenantTypeDescription;
	@XmlElement
	private long covenantTypeId;
	private boolean los;
	private boolean otc;
	private boolean pdd;
	private String covenantTypeCode;
	@XmlElement
	private List<CovenantDocument> covenantDocuments = new ArrayList<>();
	private List<DocumentDetails> documentDetails = new ArrayList<>();
	private Date alertsentOn;

	@XmlElement(name = "pdd")
	private String strPdd;
	@XmlElement(name = "otc")
	private String strOtc;
	@XmlElement(name = "documentReceived")
	private String strDocumentReceived;

	public Covenant() {
		super();
	}

	public Covenant(long id) {
		super();
		this.setId(id);
	}

	@Override
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("mandRoleDescription");
		excludeFields.add("module");
		excludeFields.add("standardValue");
		excludeFields.add("actualValue");
		excludeFields.add("notifyTo");
		excludeFields.add("code");
		excludeFields.add("description");
		excludeFields.add("category");
		excludeFields.add("categoryName");
		excludeFields.add("docType");
		excludeFields.add("docTypeName");
		excludeFields.add("alertsRequired");
		excludeFields.add("alertType");
		excludeFields.add("alertTypeName");
		excludeFields.add("alertToRolesName");
		excludeFields.add("userTemplate");
		excludeFields.add("userTemplateCode");
		excludeFields.add("userTemplateName");
		excludeFields.add("customerTemplate");
		excludeFields.add("customerTemplateCode");
		excludeFields.add("customerTemplateName");
		excludeFields.add("CovenantTypeDescription");
		excludeFields.add("covenantType");
		excludeFields.add("alertRoleCode");
		excludeFields.add("alertRoleDesc");
		excludeFields.add("covenantTypeDescription");
		excludeFields.add("covenantDocuments");
		excludeFields.add("covenantTypeCode");
		excludeFields.add("alertsentOn");
		excludeFields.add("documentDetails");
		excludeFields.add("strPdd");
		excludeFields.add("strOtc");
		excludeFields.add("strDocumentReceived");
		excludeFields.add("strAlertsRequired");
		excludeFields.add("lGraceDays");
		excludeFields.add("lAlertDays");
		excludeFields.add("strAllowPostPonement");
		excludeFields.add("remarks1");
		excludeFields.add("returnStatus");
		return excludeFields;
	}

	public Date getReceivableDate() {
		return receivableDate;
	}

	public void setReceivableDate(Date receivableDate) {
		this.receivableDate = receivableDate;
	}

	public boolean isDocumentReceived() {
		return documentReceived;
	}

	public void setDocumentReceived(boolean documentReceived) {
		this.documentReceived = documentReceived;
	}

	public Date getDocumentReceivedDate() {
		return documentReceivedDate;
	}

	public void setDocumentReceivedDate(Date documentReceivedDate) {
		this.documentReceivedDate = documentReceivedDate;
	}

	public Date getNextFrequencyDate() {
		return nextFrequencyDate;
	}

	public void setNextFrequencyDate(Date nextFrequencyDate) {
		this.nextFrequencyDate = nextFrequencyDate;
	}

	public Date getGraceDueDate() {
		return graceDueDate;
	}

	public void setGraceDueDate(Date graceDueDate) {
		this.graceDueDate = graceDueDate;
	}

	public boolean isInternalUse() {
		return internalUse;
	}

	public void setInternalUse(boolean internalUse) {
		this.internalUse = internalUse;
	}

	public Date getExtendedDate() {
		return extendedDate;
	}

	public void setExtendedDate(Date extendedDate) {
		this.extendedDate = extendedDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public byte[] getRemarks1() {
		return remarks1;
	}

	public void setRemarks1(byte[] remarks1) {
		this.remarks1 = remarks1;
	}

	public String getAdditionalField1() {
		return additionalField1;
	}

	public void setAdditionalField1(String additionalField1) {
		this.additionalField1 = additionalField1;
	}

	public String getAdditionalField2() {
		return additionalField2;
	}

	public void setAdditionalField2(String additionalField2) {
		this.additionalField2 = additionalField2;
	}

	public String getAdditionalField3() {
		return additionalField3;
	}

	public void setAdditionalField3(String additionalField3) {
		this.additionalField3 = additionalField3;
	}

	public String getAdditionalField4() {
		return additionalField4;
	}

	public void setAdditionalField4(String additionalField4) {
		this.additionalField4 = additionalField4;
	}

	public String getMandatoryRole() {
		return mandatoryRole;
	}

	public void setMandatoryRole(String mandatoryRole) {
		this.mandatoryRole = mandatoryRole;
	}

	public String getMandRoleDescription() {
		return mandRoleDescription;
	}

	public void setMandRoleDescription(String mandRoleDescription) {
		this.mandRoleDescription = mandRoleDescription;
	}

	public boolean isAllowWaiver() {
		return allowWaiver;
	}

	public void setAllowWaiver(boolean allowWaiver) {
		this.allowWaiver = allowWaiver;
	}

	public String getNotifyTo() {
		return notifyTo;
	}

	public void setNotifyTo(String notifyTo) {
		this.notifyTo = notifyTo;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getCovenantTypeDescription() {
		return covenantTypeDescription;
	}

	public void setCovenantTypeDescription(String covenantTypeDescription) {
		this.covenantTypeDescription = covenantTypeDescription;
	}

	public long getCovenantTypeId() {
		return covenantTypeId;
	}

	public void setCovenantTypeId(long covenantTypeId) {
		this.covenantTypeId = covenantTypeId;
	}

	public boolean isLos() {
		return los;
	}

	public void setLos(boolean los) {
		this.los = los;
	}

	public boolean isOtc() {
		return otc;
	}

	public void setOtc(boolean otc) {
		this.otc = otc;
	}

	public boolean isPdd() {
		return pdd;
	}

	public void setPdd(boolean pdd) {
		this.pdd = pdd;
	}

	public List<CovenantDocument> getCovenantDocuments() {
		return covenantDocuments;
	}

	public void setCovenantDocuments(List<CovenantDocument> covenantDocuments) {
		this.covenantDocuments = covenantDocuments;
	}

	public List<DocumentDetails> getDocumentDetails() {
		return documentDetails;
	}

	public void setDocumentDetails(List<DocumentDetails> documentDetails) {
		this.documentDetails = documentDetails;
	}

	public String getCovenantTypeCode() {
		return covenantTypeCode;
	}

	public void setCovenantTypeCode(String covenantTypeCode) {
		this.covenantTypeCode = covenantTypeCode;
	}

	public String getKeyReference() {
		return keyReference;
	}

	public void setKeyReference(String keyReference) {
		this.keyReference = keyReference;
	}

	public Date getAlertsentOn() {
		return alertsentOn;
	}

	public void setAlertsentOn(Date alertsentOn) {
		this.alertsentOn = alertsentOn;
	}

	public String getStrPdd() {
		return strPdd;
	}

	public void setStrPdd(String strPdd) {
		this.strPdd = strPdd;
	}

	public String getStrOtc() {
		return strOtc;
	}

	public void setStrOtc(String strOtc) {
		this.strOtc = strOtc;
	}

	public String getStrDocumentReceived() {
		return strDocumentReceived;
	}

	public void setStrDocumentReceived(String strDocumentReceived) {
		this.strDocumentReceived = strDocumentReceived;
	}
}
