package com.pennanttech.pennapps.pff.sampling.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class SamplingDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	private String parameter = StringUtils.EMPTY;
	private String branchCam = StringUtils.EMPTY;
	private String creditCam = StringUtils.EMPTY;
	private String variance = StringUtils.EMPTY;
	private String remarks = StringUtils.EMPTY;
	private String caption = StringUtils.EMPTY;
	private String remarksId = StringUtils.EMPTY;
	private boolean alignLeft;

	public SamplingDetails() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		return excludeFields;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public String getBranchCam() {
		return branchCam;
	}

	public void setBranchCam(String branchCam) {
		this.branchCam = branchCam;
	}

	public String getCreditCam() {
		return creditCam;
	}

	public void setCreditCam(String creditCam) {
		this.creditCam = creditCam;
	}

	public String getVariance() {
		return variance;
	}

	public void setVariance(String variance) {
		this.variance = variance;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public boolean isAlignLeft() {
		return alignLeft;
	}

	public void setAlignLeft(boolean alignLeft) {
		this.alignLeft = alignLeft;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getRemarksId() {
		return remarksId;
	}

	public void setRemarksId(String remarksId) {
		this.remarksId = remarksId;
	}
}
