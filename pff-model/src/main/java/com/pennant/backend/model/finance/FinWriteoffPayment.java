package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

public class FinWriteoffPayment extends AbstractWorkflowEntity{
	private static final long serialVersionUID = -1477748770396649402L;

	private String  finReference;
	private long seqNo = Long.MIN_VALUE;
	private BigDecimal writeoffPayAmount = BigDecimal.ZERO;
	private String 		writeoffPayAccount;
	private long linkedTranId = 0;

	private BigDecimal writeoffAmount = BigDecimal.ZERO;
	private BigDecimal writeoffPaidAmount = BigDecimal.ZERO;
	private Date  writeoffDate;
	
	public FinWriteoffPayment() {
         super();
	}
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("writeoffAmount");
		excludeFields.add("writeoffPaidAmount");
		excludeFields.add("writeoffDate");
		return excludeFields;
	}
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}
	public BigDecimal getWriteoffPayAmount() {
		return writeoffPayAmount;
	}
	public void setWriteoffPayAmount(BigDecimal writeoffPayAmount) {
		this.writeoffPayAmount = writeoffPayAmount;
	}
	public String getWriteoffPayAccount() {
		return writeoffPayAccount;
	}
	public void setWriteoffPayAccount(String writeoffPayAccount) {
		this.writeoffPayAccount = writeoffPayAccount;
	}
	
	public long getLinkedTranId() {
		return linkedTranId;
	}

	public void setLinkedTranId(long linkedTranId) {
		this.linkedTranId = linkedTranId;
	}

	public BigDecimal getWriteoffAmount() {
		return writeoffAmount;
	}

	public void setWriteoffAmount(BigDecimal writeoffAmount) {
		this.writeoffAmount = writeoffAmount;
	}

	public BigDecimal getWriteoffPaidAmount() {
		return writeoffPaidAmount;
	}

	public void setWriteoffPaidAmount(BigDecimal writeoffPaidAmount) {
		this.writeoffPaidAmount = writeoffPaidAmount;
	}

	public Date getWriteoffDate() {
		return writeoffDate;
	}

	public void setWriteoffDate(Date writeoffDate) {
		this.writeoffDate = writeoffDate;
	}
	public long getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(long seqNo) {
		this.seqNo = seqNo;
	}

}
