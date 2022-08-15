package com.pennattech.pff.receipt.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennanttech.pff.presentment.model.PresentmentDetail;
import com.pennanttech.pff.presentment.model.PresentmentHeader;

public class ReceiptDTO implements Serializable {
	private static final long serialVersionUID = 3276282747331284700L;

	private FinanceMain financeMain;
	private Customer customer;
	private List<FinanceScheduleDetail> schedules = new ArrayList<>();
	private List<FinFeeDetail> fees = new ArrayList<>();
	private FinanceProfitDetail profitDetail;
	private FinReceiptHeader finReceiptHeader;
	private PresentmentHeader presentmentHeader;
	private PresentmentDetail presentmentDetail;
	private String repayHierarchy;
	private Date valuedate;
	private Date postDate;

	public ReceiptDTO() {
		super();
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<FinanceScheduleDetail> getSchedules() {
		return schedules;
	}

	public void setSchedules(List<FinanceScheduleDetail> schedules) {
		this.schedules = schedules;
	}

	public List<FinFeeDetail> getFees() {
		return fees;
	}

	public void setFees(List<FinFeeDetail> fees) {
		this.fees = fees;
	}

	public FinanceProfitDetail getProfitDetail() {
		return profitDetail;
	}

	public void setProfitDetail(FinanceProfitDetail profitDetail) {
		this.profitDetail = profitDetail;
	}

	public FinReceiptHeader getFinReceiptHeader() {
		return finReceiptHeader;
	}

	public void setFinReceiptHeader(FinReceiptHeader finReceiptHeader) {
		this.finReceiptHeader = finReceiptHeader;
	}

	public PresentmentHeader getPresentmentHeader() {
		return presentmentHeader;
	}

	public void setPresentmentHeader(PresentmentHeader presentmentHeader) {
		this.presentmentHeader = presentmentHeader;
	}

	public PresentmentDetail getPresentmentDetail() {
		return presentmentDetail;
	}

	public void setPresentmentDetail(PresentmentDetail presentmentDetail) {
		this.presentmentDetail = presentmentDetail;
	}

	public String getRepayHierarchy() {
		return repayHierarchy;
	}

	public void setRepayHierarchy(String repayHierarchy) {
		this.repayHierarchy = repayHierarchy;
	}

	public Date getValuedate() {
		return valuedate;
	}

	public void setValuedate(Date valuedate) {
		this.valuedate = valuedate;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

}
