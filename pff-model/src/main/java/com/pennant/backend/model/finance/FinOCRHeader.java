package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

@XmlType(propOrder = { "ocrID", "ocrDescription", "customerPortion", "ocrType", "totalDemand", "ocrDetailList",
		"finOCRCapturesList" })
@XmlRootElement(name = "finOCRHeader")
@XmlAccessorType(XmlAccessType.NONE)
public class FinOCRHeader extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 1L;
	private long headerID = Long.MIN_VALUE;
	@XmlElement
	private String ocrID;
	@XmlElement
	private String ocrDescription;
	@XmlElement
	private BigDecimal customerPortion = BigDecimal.ZERO;
	@XmlElement
	private String ocrType;
	@XmlElement
	private BigDecimal totalDemand = BigDecimal.ZERO;
	private BigDecimal totalReceivable = BigDecimal.ZERO;
	private long finID;
	private String finReference;
	private BigDecimal ocrCprTotReceivble;
	private BigDecimal ocrTotalDemand;
	private BigDecimal ocrTotalPaid;
	@XmlElement
	private List<FinOCRDetail> ocrDetailList = new ArrayList<FinOCRDetail>();
	@XmlElement
	private List<FinOCRCapture> finOCRCapturesList = new ArrayList<FinOCRCapture>();
	private FinOCRHeader befImage;
	private LoggedInUser userDetails;
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
	private boolean definitionApproved = false;

	public FinOCRHeader() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("definitionApproved");
		excludeFields.add("ocrDetailList");
		excludeFields.add("finOCRCapturesList");
		excludeFields.add("splitApplicable");
		excludeFields.add("totalReceivable");
		excludeFields.add("ocrCprTotReceivble");
		excludeFields.add("ocrTotalDemand");
		excludeFields.add("ocrTotalPaid");
		return excludeFields;
	}

	public long getHeaderID() {
		return headerID;
	}

	public void setHeaderID(long headerID) {
		this.headerID = headerID;
	}

	public String getOcrID() {
		return ocrID;
	}

	public void setOcrID(String ocrID) {
		this.ocrID = ocrID;
	}

	public String getOcrDescription() {
		return ocrDescription;
	}

	public void setOcrDescription(String ocrDescription) {
		this.ocrDescription = ocrDescription;
	}

	public BigDecimal getCustomerPortion() {
		return customerPortion;
	}

	public void setCustomerPortion(BigDecimal customerPortion) {
		this.customerPortion = customerPortion;
	}

	public String getOcrType() {
		return ocrType;
	}

	public void setOcrType(String ocrType) {
		this.ocrType = ocrType;
	}

	public FinOCRHeader getBefImage() {
		return befImage;
	}

	public void setBefImage(FinOCRHeader befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public List<FinOCRDetail> getOcrDetailList() {
		return ocrDetailList;
	}

	public void setOcrDetailList(List<FinOCRDetail> ocrDetailList) {
		this.ocrDetailList = ocrDetailList;
	}

	public List<FinOCRCapture> getFinOCRCapturesList() {
		return finOCRCapturesList;
	}

	public void setFinOCRCapturesList(List<FinOCRCapture> finOCRCapturesList) {
		this.finOCRCapturesList = finOCRCapturesList;
	}

	public boolean isDefinitionApproved() {
		return definitionApproved;
	}

	public void setDefinitionApproved(boolean definitionApproved) {
		this.definitionApproved = definitionApproved;
	}

	public BigDecimal getTotalDemand() {
		return totalDemand;
	}

	public void setTotalDemand(BigDecimal totalDemand) {
		this.totalDemand = totalDemand;
	}

	public BigDecimal getTotalReceivable() {
		return totalReceivable;
	}

	public void setTotalReceivable(BigDecimal totalReceivable) {
		this.totalReceivable = totalReceivable;
	}

	public BigDecimal getOcrCprTotReceivble() {
		return ocrCprTotReceivble;
	}

	public void setOcrCprTotReceivble(BigDecimal ocrCprTotReceivble) {
		this.ocrCprTotReceivble = ocrCprTotReceivble;
	}

	public BigDecimal getOcrTotalDemand() {
		return ocrTotalDemand;
	}

	public void setOcrTotalDemand(BigDecimal ocrTotalDemand) {
		this.ocrTotalDemand = ocrTotalDemand;
	}

	public BigDecimal getOcrTotalPaid() {
		return ocrTotalPaid;
	}

	public void setOcrTotalPaid(BigDecimal ocrTotalPaid) {
		this.ocrTotalPaid = ocrTotalPaid;
	}

}
