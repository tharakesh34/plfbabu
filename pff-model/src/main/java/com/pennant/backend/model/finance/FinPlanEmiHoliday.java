package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "planEMIHMonth", "planEMIHDate" })
@XmlAccessorType(XmlAccessType.NONE)
public class FinPlanEmiHoliday implements Serializable {

	private static final long serialVersionUID = 1L;
	private long finID;
	private String finReference;
	@XmlElement(name = "holidayMonth")
	private int planEMIHMonth;
	@XmlElement(name = "holidayDate")
	private Date planEMIHDate;

	public FinPlanEmiHoliday() {
		super();
	}

	public FinPlanEmiHoliday copyEntity() {
		FinPlanEmiHoliday entity = new FinPlanEmiHoliday();
		entity.setFinID(this.finID);
		entity.setFinReference(this.finReference);
		entity.setPlanEMIHMonth(this.planEMIHMonth);
		entity.setPlanEMIHDate(this.planEMIHDate);
		return entity;
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
