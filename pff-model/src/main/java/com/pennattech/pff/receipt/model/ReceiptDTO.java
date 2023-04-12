package com.pennattech.pff.receipt.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.presentment.model.PresentmentDetail;
import com.pennanttech.pff.presentment.model.PresentmentHeader;

public class ReceiptDTO implements Serializable {
	private static final long serialVersionUID = 3276282747331284700L;

	private FinanceType finType;
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
	private Date bussinessDate;
	private boolean noReserve;
	private boolean pdDetailsExits;
	private List<FinExcessAmount> emiInAdvance = new ArrayList<>();
	private RequestSource requestSource;
	private boolean createPrmntReceipt;
	private List<FinODDetails> odDetails = new ArrayList<>();
	private List<ManualAdvise> manualAdvises = new ArrayList<>();
	private String roundAdjMth;
	private FeeType lppFeeType;
	private FinReceiptData finReceiptData;
	private Map<String, String> bounceForPD = new HashMap<String, String>();

	public ReceiptDTO() {
		super();
	}

	public FinanceType getFinType() {
		return finType;
	}

	public void setFinType(FinanceType finType) {
		this.finType = finType;
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

	public Date getBussinessDate() {
		return bussinessDate;
	}

	public void setBussinessDate(Date bussinessDate) {
		this.bussinessDate = bussinessDate;
	}

	public boolean isNoReserve() {
		return noReserve;
	}

	public void setNoReserve(boolean noReserve) {
		this.noReserve = noReserve;
	}

	public boolean isPdDetailsExits() {
		return pdDetailsExits;
	}

	public void setPdDetailsExits(boolean pdDetailsExits) {
		this.pdDetailsExits = pdDetailsExits;
	}

	public List<FinExcessAmount> getEmiInAdvance() {
		return emiInAdvance;
	}

	public void setEmiInAdvance(List<FinExcessAmount> emiInAdvance) {
		this.emiInAdvance = emiInAdvance;
	}

	public RequestSource getRequestSource() {
		return requestSource;
	}

	public void setRequestSource(RequestSource requestSource) {
		this.requestSource = requestSource;
	}

	public boolean isCreatePrmntReceipt() {
		return createPrmntReceipt;
	}

	public void setCreatePrmntReceipt(boolean createPrmntReceipt) {
		this.createPrmntReceipt = createPrmntReceipt;
	}

	public List<FinODDetails> getOdDetails() {
		return odDetails;
	}

	public void setOdDetails(List<FinODDetails> odDetails) {
		this.odDetails = odDetails;
	}

	public List<ManualAdvise> getManualAdvises() {
		return manualAdvises;
	}

	public void setManualAdvises(List<ManualAdvise> manualAdvises) {
		this.manualAdvises = manualAdvises;
	}

	public String getRoundAdjMth() {
		return roundAdjMth;
	}

	public void setRoundAdjMth(String roundAdjMth) {
		this.roundAdjMth = roundAdjMth;
	}

	public FeeType getLppFeeType() {
		return lppFeeType;
	}

	public void setLppFeeType(FeeType lppFeeType) {
		this.lppFeeType = lppFeeType;
	}

	public FinReceiptData getFinReceiptData() {
		return finReceiptData;
	}

	public void setFinReceiptData(FinReceiptData finReceiptData) {
		this.finReceiptData = finReceiptData;
	}

	public Map<String, String> getBounceForPD() {
		return bounceForPD;
	}

	public void setBounceForPD(Map<String, String> bounceForPD) {
		this.bounceForPD = bounceForPD;
	}

}