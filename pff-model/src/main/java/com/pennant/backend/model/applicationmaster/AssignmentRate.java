package com.pennant.backend.model.applicationmaster;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class AssignmentRate extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private long assignmentId;
	private Date effectiveDate;
	private BigDecimal mclrRate = BigDecimal.ZERO;
	private BigDecimal bankSpreadRate = BigDecimal.ZERO;
	private BigDecimal opexRate = BigDecimal.ZERO;
	private String resetFrequency;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private AssignmentRate befImage;
	@XmlTransient
	private LoggedInUser userDetails;

	public AssignmentRate() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(long assignmentId) {
		this.assignmentId = assignmentId;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public BigDecimal getMclrRate() {
		return mclrRate;
	}

	public void setMclrRate(BigDecimal mclrRate) {
		this.mclrRate = mclrRate;
	}

	public BigDecimal getBankSpreadRate() {
		return bankSpreadRate;
	}

	public void setBankSpreadRate(BigDecimal bankSpreadRate) {
		this.bankSpreadRate = bankSpreadRate;
	}

	public BigDecimal getOpexRate() {
		return opexRate;
	}

	public void setOpexRate(BigDecimal opexRate) {
		this.opexRate = opexRate;
	}

	public String getResetFrequency() {
		return resetFrequency;
	}

	public void setResetFrequency(String resetFrequency) {
		this.resetFrequency = resetFrequency;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public AssignmentRate getBefImage() {
		return befImage;
	}

	public void setBefImage(AssignmentRate befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}
}
