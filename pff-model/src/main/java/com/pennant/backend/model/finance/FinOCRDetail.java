package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "stepSequence", "customerContribution", "financerContribution", "contributor" })
@XmlRootElement(name = "finOCRDetail")
@XmlAccessorType(XmlAccessType.NONE)
public class FinOCRDetail extends AbstractWorkflowEntity implements Comparable<FinOCRDetail> {

	private static final long serialVersionUID = 1L;
	private long detailID = Long.MIN_VALUE;
	@XmlElement
	private int stepSequence;
	@XmlElement
	private BigDecimal customerContribution;
	@XmlElement
	private BigDecimal financerContribution;
	private boolean newRecord = false;
	private FinOCRDetail befImage;
	private LoggedInUser userDetails;
	@XmlElement
	private String contributor;
	private long headerID;

	public FinOCRDetail() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		return excludeFields;
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public int getStepSequence() {
		return stepSequence;
	}

	public void setStepSequence(int stepSequence) {
		this.stepSequence = stepSequence;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public FinOCRDetail getBefImage() {
		return befImage;
	}

	public void setBefImage(FinOCRDetail befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public long getDetailID() {
		return detailID;
	}

	public void setDetailID(long ocrDetailID) {
		this.detailID = ocrDetailID;
	}

	public BigDecimal getCustomerContribution() {
		return customerContribution;
	}

	public void setCustomerContribution(BigDecimal customerContribution) {
		this.customerContribution = customerContribution;
	}

	public BigDecimal getFinancerContribution() {
		return financerContribution;
	}

	public void setFinancerContribution(BigDecimal financerContribution) {
		this.financerContribution = financerContribution;
	}

	public String getContributor() {
		return contributor;
	}

	public void setContributor(String contributor) {
		this.contributor = contributor;
	}

	@Override
	public int compareTo(FinOCRDetail ocrDetail) {
		if (this.stepSequence == ocrDetail.stepSequence) {
			return 0;
		} else if (this.stepSequence > ocrDetail.stepSequence) {
			return 1;
		} else {
			return -1;
		}
	}

	public long getHeaderID() {
		return headerID;
	}

	public void setHeaderID(long headerID) {
		this.headerID = headerID;
	}

}
