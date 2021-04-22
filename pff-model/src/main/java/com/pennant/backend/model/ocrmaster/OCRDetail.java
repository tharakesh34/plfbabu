package com.pennant.backend.model.ocrmaster;

import java.math.BigDecimal;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class OCRDetail extends AbstractWorkflowEntity implements Comparable<OCRDetail> {
	private static final long serialVersionUID = 1L;
	private long detailID = Long.MIN_VALUE;
	private long headerID = Long.MIN_VALUE;
	private int stepSequence;
	private BigDecimal customerContribution = BigDecimal.ZERO;
	private BigDecimal financerContribution = BigDecimal.ZERO;
	private String contributor;
	private boolean newRecord = false;
	private String lovValue;
	private OCRDetail befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public OCRDetail() {
		super();
	}

	public OCRDetail(int id) {
		super();
		this.setId(id);
	}

	public long getId() {
		return detailID;
	}

	public void setId(long id) {
		this.detailID = id;
	}

	public int getStepSequence() {
		return stepSequence;
	}

	public void setStepSequence(int stepSequence) {
		this.stepSequence = stepSequence;
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

	public OCRDetail getBefImage() {
		return befImage;
	}

	public void setBefImage(OCRDetail befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public long getHeaderID() {
		return headerID;
	}

	public void setHeaderID(long headerID) {
		this.headerID = headerID;
	}

	public long getDetailID() {
		return detailID;
	}

	public void setDetailID(long detailID) {
		this.detailID = detailID;
	}

	@Override
	public int compareTo(OCRDetail ocrDetail) {
		return this.stepSequence > ocrDetail.stepSequence ? 1 : this.stepSequence < ocrDetail.stepSequence ? -1 : 0;
	}

}
