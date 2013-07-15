package com.pennant.backend.model.finance;

import java.util.List;

import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.Rule;

public class PaymentHeader {

	private PaymentDetails paymentDetails;
	private List<FinanceScheduleDetail>	 unPaidFinSchdDetails;
	private List<FinanceScheduleDetail>	 paidFinSchdDetails;
	private List<TransactionEntry> listLatePayTransEntries;
	private List<TransactionEntry> listEarlyPayTransEntries;
	private List<Rule> latePayRule;
	private List<Rule> earlyPayRule;

	public PaymentDetails getPaymentDetails() {
    	return paymentDetails;
    }
	public void setPaymentDetails(PaymentDetails paymentDetails) {
    	this.paymentDetails = paymentDetails;
    }
	public List<FinanceScheduleDetail> getUnPaidFinSchdDetails() {
    	return unPaidFinSchdDetails;
    }
	public void setUnPaidFinSchdDetails(List<FinanceScheduleDetail> listFinanceScheduleDetails) {
    	this.unPaidFinSchdDetails = listFinanceScheduleDetails;
    }
	
	public List<FinanceScheduleDetail> getPaidFinSchdDetails() {
    	return paidFinSchdDetails;
    }
	public void setPaidFinSchdDetails(List<FinanceScheduleDetail> paidFinSchdDetails) {
    	this.paidFinSchdDetails = paidFinSchdDetails;
    }
	public List<TransactionEntry> getListLatePayTransEntries() {
    	return listLatePayTransEntries;    	
    }
	public void setListLatePayTransEntries(List<TransactionEntry> listLatePayTransEntries) {
    	this.listLatePayTransEntries = listLatePayTransEntries;
    }
	
	public List<TransactionEntry> getListEarlyPayTransEntries() {
    	return listEarlyPayTransEntries;
    }
	public void setListEarlyPayTransEntries(List<TransactionEntry> listEarlyPayTransEntries) {
    	this.listEarlyPayTransEntries = listEarlyPayTransEntries;
    }
	public List<Rule> getLatePayRule() {
    	return latePayRule;
    }
	public void setLatePayRule(List<Rule> latePayRule) {
    	this.latePayRule = latePayRule;
    }
	public List<Rule> getEarlyPayRule() {
    	return earlyPayRule;
    }
	public void setEarlyPayRule(List<Rule> earlyPayRule) {
    	this.earlyPayRule = earlyPayRule;
    }

	
	
}
