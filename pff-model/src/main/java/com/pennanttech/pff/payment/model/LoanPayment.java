package com.pennanttech.pff.payment.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinanceScheduleDetail;

public class LoanPayment implements Serializable {
	private static final long serialVersionUID = 1L;
	private long finID;
	private String finReference;
	private List<FinanceScheduleDetail> schedules = new ArrayList<>();
	private Date valueDate;

	public LoanPayment() {
		super();
	}

	public LoanPayment(long finID, String finReference, List<FinanceScheduleDetail> schedules, Date valueDate) {
		super();
		this.finID = finID;
		this.finReference = finReference;
		this.schedules = schedules;
		this.valueDate = valueDate;
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

	public List<FinanceScheduleDetail> getSchedules() {
		return schedules;
	}

	public void setSchedules(List<FinanceScheduleDetail> schedules) {
		this.schedules = schedules;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

}
