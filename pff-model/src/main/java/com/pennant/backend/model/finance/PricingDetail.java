package com.pennant.backend.model.finance;

import java.util.ArrayList;
import java.util.List;

import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class PricingDetail extends AbstractWorkflowEntity {

	private static final long serialVersionUID = -4098586745401583126L;

	private List<FinanceMain> financeMains = new ArrayList<FinanceMain>();
	private FinanceMain financeMain;
	// private FinFeeDetail finFeeDetail;
	private List<FinFeeDetail> topUpFinFeeDetails = new ArrayList<FinFeeDetail>();
	private List<FinFeeDetail> actualFinFeeDetails = new ArrayList<FinFeeDetail>();
	private List<VASRecording> topUpVasDetails = new ArrayList<VASRecording>();
	private List<VASRecording> actualVasDetails = new ArrayList<VASRecording>();
	private List<CollateralAssignment> collateralAssignments = new ArrayList<CollateralAssignment>();
	private boolean split;

	private boolean newRecord;

	public List<FinanceMain> getFinanceMains() {
		return financeMains;
	}

	public void setFinanceMains(List<FinanceMain> financeMains) {
		this.financeMains = financeMains;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public List<FinFeeDetail> getTopUpFinFeeDetails() {
		return topUpFinFeeDetails;
	}

	public void setTopUpFinFeeDetails(List<FinFeeDetail> topUpFinFeeDetails) {
		this.topUpFinFeeDetails = topUpFinFeeDetails;
	}

	public List<FinFeeDetail> getActualFinFeeDetails() {
		return actualFinFeeDetails;
	}

	public void setActualFinFeeDetails(List<FinFeeDetail> actualFinFeeDetails) {
		this.actualFinFeeDetails = actualFinFeeDetails;
	}

	public List<VASRecording> getActualVasDetails() {
		return actualVasDetails;
	}

	public void setActualVasDetails(List<VASRecording> actualVasDetails) {
		this.actualVasDetails = actualVasDetails;
	}

	public List<VASRecording> getTopUpVasDetails() {
		return topUpVasDetails;
	}

	public void setTopUpVasDetails(List<VASRecording> topUpVasDetails) {
		this.topUpVasDetails = topUpVasDetails;
	}

	public List<CollateralAssignment> getCollateralAssignments() {
		return collateralAssignments;
	}

	public void setCollateralAssignments(List<CollateralAssignment> collateralAssignments) {
		this.collateralAssignments = collateralAssignments;
	}

	public boolean isSplit() {
		return split;
	}

	public void setSplit(boolean split) {
		this.split = split;
	}

}
