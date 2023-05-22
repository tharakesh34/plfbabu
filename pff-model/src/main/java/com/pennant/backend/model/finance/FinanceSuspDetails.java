package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class FinanceSuspDetails implements Serializable {

	private static final long serialVersionUID = 5778056251513895432L;

	private long finID;
	private String finReference;
	private String finBranch;
	private String finType;
	private long custId = Long.MIN_VALUE;
	private int finSuspSeq;
	private Date finTrfDate;
	private String finTrfMvt;
	private BigDecimal finTrfAmt = BigDecimal.ZERO;
	private Date finODDate;
	private Date finTrfFromDate;
	private long linkedTranId = Long.MIN_VALUE;

	public FinanceSuspDetails() {
	    super();
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

	public String getFinBranch() {
		return finBranch;
	}

	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public long getCustId() {
		return custId;
	}

	public void setCustId(long custId) {
		this.custId = custId;
	}

	public int getFinSuspSeq() {
		return finSuspSeq;
	}

	public void setFinSuspSeq(int finSuspSeq) {
		this.finSuspSeq = finSuspSeq;
	}

	public Date getFinTrfDate() {
		return finTrfDate;
	}

	public void setFinTrfDate(Date finTrfDate) {
		this.finTrfDate = finTrfDate;
	}

	public String getFinTrfMvt() {
		return finTrfMvt;
	}

	public void setFinTrfMvt(String finTrfMvt) {
		this.finTrfMvt = finTrfMvt;
	}

	public BigDecimal getFinTrfAmt() {
		return finTrfAmt;
	}

	public void setFinTrfAmt(BigDecimal finTrfAmt) {
		this.finTrfAmt = finTrfAmt;
	}

	public Date getFinTrfFromDate() {
		return finTrfFromDate;
	}

	public void setFinTrfFromDate(Date finTrfFromDate) {
		this.finTrfFromDate = finTrfFromDate;
	}

	public Date getFinODDate() {
		return finODDate;
	}

	public void setFinODDate(Date finODDate) {
		this.finODDate = finODDate;
	}

	public long getLinkedTranId() {
		return linkedTranId;
	}

	public void setLinkedTranId(long linkedTranId) {
		this.linkedTranId = linkedTranId;
	}

}
