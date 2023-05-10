package com.pennant.backend.model.finance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.AutoRefundLoan;
import com.pennanttech.pennapps.core.model.AbstractEntity;

public class CustEODEvent extends AbstractEntity {
	private static final long serialVersionUID = -8270026465500688782L;
	private Customer customer;
	private Date eodDate;
	private Date eodValueDate;
	private String provisionRule;
	private String amzMethodRule;

	private List<FinEODEvent> finEODEvents = new ArrayList<FinEODEvent>(1);
	private boolean updCustomer = false;
	private boolean pastDueExist = false;
	private boolean dateRollover = false;
	private boolean rateRvwExist = false;
	private boolean disbExist = false;
	private boolean dueExist = false;
	private boolean checkPresentment = false;
	private boolean eodSuccess = true;

	/* NPA and Provision fields */
	private boolean customerProvision;
	private String provisionBooks;
	private Date provisionEffectiveDate;
	private EventProperties eventProperties = new EventProperties();
	private List<AutoRefundLoan> autoRefundLoans = new ArrayList<>();

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

	public boolean isPastDueExist() {
		return pastDueExist;
	}

	public void setPastDueExist(boolean pastDueExist) {
		this.pastDueExist = pastDueExist;
	}

	public boolean isDateRollover() {
		return dateRollover;
	}

	public void setDateRollover(boolean dateRollover) {
		this.dateRollover = dateRollover;
	}

	public boolean isRateRvwExist() {
		return rateRvwExist;
	}

	public void setRateRvwExist(boolean rateRvwExist) {
		this.rateRvwExist = rateRvwExist;
	}

	public boolean isDisbExist() {
		return disbExist;
	}

	public void setDisbExist(boolean disbExist) {
		this.disbExist = disbExist;
	}

	public boolean isDueExist() {
		return dueExist;
	}

	public void setDueExist(boolean dueExist) {
		this.dueExist = dueExist;
	}

	public boolean isCheckPresentment() {
		return checkPresentment;
	}

	public void setCheckPresentment(boolean checkPresentment) {
		this.checkPresentment = checkPresentment;
	}

	public boolean isEodSuccess() {
		return eodSuccess;
	}

	public void setEodSuccess(boolean eodSuccess) {
		this.eodSuccess = eodSuccess;
	}

	public void destroy() {
		if (this.finEODEvents != null) {
			for (FinEODEvent eodEvent : finEODEvents) {
				eodEvent.destroy();
			}
			this.finEODEvents.clear();
		}
	}

	public String getProvisionRule() {
		return provisionRule;
	}

	public void setProvisionRule(String provisionRule) {
		this.provisionRule = provisionRule;
	}

	public String getAmzMethodRule() {
		return amzMethodRule;
	}

	public void setAmzMethodRule(String amzMethodRule) {
		this.amzMethodRule = amzMethodRule;
	}

	public boolean isCustomerProvision() {
		return customerProvision;
	}

	public void setCustomerProvision(boolean customerProvision) {
		this.customerProvision = customerProvision;
	}

	public String getProvisionBooks() {
		return provisionBooks;
	}

	public void setProvisionBooks(String provisionBooks) {
		this.provisionBooks = provisionBooks;
	}

	public Date getProvisionEffectiveDate() {
		return provisionEffectiveDate;
	}

	public void setProvisionEffectiveDate(Date provisionEffectiveDate) {
		this.provisionEffectiveDate = provisionEffectiveDate;
	}

	public String getCustIdAsString() {
		return customer != null ? String.valueOf(customer.getCustID()) : "";
	}

	public EventProperties getEventProperties() {
		return eventProperties;
	}

	public void setEventProperties(EventProperties eventProperties) {
		this.eventProperties = eventProperties;
	}

	public List<AutoRefundLoan> getAutoRefundLoans() {
		return autoRefundLoans;
	}

	public void setAutoRefundLoans(List<AutoRefundLoan> autoRefundLoans) {
		this.autoRefundLoans = autoRefundLoans;
	}

}
