package com.pennanttech.pennapps.pff.sampling.model;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class SamplingCollateral extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private String collateralRef;
	private int seqNo;
	private String depositorCif;
	private String depositorName;
	private String collateralType;
	private String collateralTypeName;
	private String linkId;
	
	public SamplingCollateral() {
		super();
	}

	public String getCollateralRef() {
		return collateralRef;
	}

	public void setCollateralRef(String collateralRef) {
		this.collateralRef = collateralRef;
	}

	public int getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}

	public String getDepositorCif() {
		return depositorCif;
	}

	public void setDepositorCif(String depositorCif) {
		this.depositorCif = depositorCif;
	}

	public String getDepositorName() {
		return depositorName;
	}

	public void setDepositorName(String depositorName) {
		this.depositorName = depositorName;
	}

	public String getCollateralType() {
		return collateralType;
	}

	public void setCollateralType(String collateralType) {
		this.collateralType = collateralType;
	}

	public String getCollateralTypeName() {
		return collateralTypeName;
	}

	public void setCollateralTypeName(String collateralTypeName) {
		this.collateralTypeName = collateralTypeName;
	}

	public String getLinkId() {
		return linkId;
	}

	public void setLinkId(String linkId) {
		this.linkId = linkId;
	}
}
