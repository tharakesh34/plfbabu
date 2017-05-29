package com.pennant.backend.model.finance;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
@XmlType(propOrder = { "planEMIHMonth", "planEMIHDate"})
@XmlAccessorType(XmlAccessType.NONE)
public class FinPlanEmiHoliday {

	private String finReference;
	@XmlElement(name="holidayMonth")
	private int planEMIHMonth;
	@XmlElement(name="holidayDate")
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
