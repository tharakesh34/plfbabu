package com.pennant.app.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.customermasters.Customer;
import com.pennanttech.pff.core.model.AbstractEntity;

public class CustEODEvent extends AbstractEntity {
	private static final long	serialVersionUID	= -8270026465500688782L;
	private Customer customer;
	private Date						eodDate;
	private Date						eodValueDate;
	private List<FinEODEvent> finEODEvents = new ArrayList<FinEODEvent>(1);
	private boolean updCustomer = false;
	
	
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public Date getEodDate() {
		return eodDate;
	}
	public void setEodDate(Date eodDate) {
		this.eodDate = eodDate;
	}
	public Date getEodValueDate() {
		return eodValueDate;
	}
	public void setEodValueDate(Date eodValueDate) {
		this.eodValueDate = eodValueDate;
	}
	public List<FinEODEvent> getFinEODEvents() {
		return finEODEvents;
	}
	public void setFinEODEvents(List<FinEODEvent> finEODEvents) {
		this.finEODEvents = finEODEvents;
	}
	public boolean isUpdCustomer() {
		return updCustomer;
	}
	public void setUpdCustomer(boolean updCustomer) {
		this.updCustomer = updCustomer;
	}

}
