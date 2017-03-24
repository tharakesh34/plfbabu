package com.pennant.backend.model.finance;

import java.util.Date;

public class FinPlanEmiHoliday {

	private String finReference;
	private int planEMIHMonth;
	private Date planEMIHDate;
	
	public FinPlanEmiHoliday() {
		super();
	}

	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public int getPlanEMIHMonth() {
		return planEMIHMonth;
	}
	public void setPlanEMIHMonth(int planEMIHMonth) {
		this.planEMIHMonth = planEMIHMonth;
	}

	public Date getPlanEMIHDate() {
		return planEMIHDate;
	}
	public void setPlanEMIHDate(Date planEMIHDate) {
		this.planEMIHDate = planEMIHDate;
	}
	
}
