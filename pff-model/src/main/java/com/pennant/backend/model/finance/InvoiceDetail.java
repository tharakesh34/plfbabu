package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennanttech.pennapps.core.model.AbstractEntity;

public class InvoiceDetail extends AbstractEntity {
	private static final long serialVersionUID = 1L;
	private Long invoiceID;
	private long linkedTranId;
	private BigDecimal pftAmount = BigDecimal.ZERO;
	private BigDecimal priAmount = BigDecimal.ZERO;
	private BigDecimal fpftAmount = BigDecimal.ZERO;
	private BigDecimal fpriAmount = BigDecimal.ZERO;
	private String invoiceType;
	private FinanceDetail financeDetail;
	private List<FinFeeDetail> finFeeDetailsList = new ArrayList<>();
	private List<ManualAdviseMovements> movements = new ArrayList<>();
	private Long dbInvoiceID;
	private boolean origination;
	private boolean isWaiver;
	private boolean dbInvSetReq;
	private EventProperties eventProperties = new EventProperties();

	public InvoiceDetail() {
		super();
	}

	public Long getInvoiceID() {
		return invoiceID;
	}

	public void setInvoiceID(Long invoiceID) {
		this.invoiceID = invoiceID;
	}

	public long getLinkedTranId() {
		return linkedTranId;
	}

	public void setLinkedTranId(long linkedTranId) {
		this.linkedTranId = linkedTranId;
	}

	public BigDecimal getPftAmount() {
		return pftAmount;
	}

	public void setPftAmount(BigDecimal pftAmount) {
		this.pftAmount = pftAmount;
	}

	public BigDecimal getPriAmount() {
		return priAmount;
	}

	public void setPriAmount(BigDecimal priAmount) {
		this.priAmount = priAmount;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public List<FinFeeDetail> getFinFeeDetailsList() {
		return finFeeDetailsList;
	}

	public void setFinFeeDetailsList(List<FinFeeDetail> finFeeDetailsList) {
		this.finFeeDetailsList = finFeeDetailsList;
	}

	public List<ManualAdviseMovements> getMovements() {
		return movements;
	}

	public void setMovements(List<ManualAdviseMovements> movements) {
		this.movements = movements;
	}

	public Long getDbInvoiceID() {
		return dbInvoiceID;
	}

	public void setDbInvoiceID(Long dbInvoiceID) {
		this.dbInvoiceID = dbInvoiceID;
	}

	public boolean isOrigination() {
		return origination;
	}

	public void setOrigination(boolean origination) {
		this.origination = origination;
	}

	public boolean isWaiver() {
		return isWaiver;
	}

	public void setWaiver(boolean isWaiver) {
		this.isWaiver = isWaiver;
	}

	public boolean isDbInvSetReq() {
		return dbInvSetReq;
	}

	public void setDbInvSetReq(boolean dbInvSetReq) {
		this.dbInvSetReq = dbInvSetReq;
	}

	public BigDecimal getFpftAmount() {
		return fpftAmount;
	}

	public void setFpftAmount(BigDecimal fpftAmount) {
		this.fpftAmount = fpftAmount;
	}

	public BigDecimal getFpriAmount() {
		return fpriAmount;
	}

	public void setFpriAmount(BigDecimal fpriAmount) {
		this.fpriAmount = fpriAmount;
	}

	public EventProperties getEventProperties() {
		return eventProperties;
	}

	public void setEventProperties(EventProperties eventProperties) {
		this.eventProperties = eventProperties;
	}

}
