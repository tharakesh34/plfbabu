package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class FinExcessAmount implements Serializable {
	private static final long serialVersionUID = 1L;

	private long excessID = 0;
	private long finID;
	private String finReference;
	private String amountType;
	private BigDecimal amount = BigDecimal.ZERO;
	private BigDecimal utilisedAmt = BigDecimal.ZERO;
	private BigDecimal reservedAmt = BigDecimal.ZERO;
	private BigDecimal balanceAmt = BigDecimal.ZERO;
	private String rcdAction;
	private FinExcessMovement excessMovement;
	private Long receiptID;
	private Date valueDate;
	private Date postDate;
	private boolean expand;
	private boolean collapse;

	public FinExcessAmount() {
		super();
	}

	public FinExcessAmount copyEntity() {
		FinExcessAmount entity = new FinExcessAmount();
		entity.setExcessID(this.excessID);
		entity.setFinID(this.finID);
		entity.setFinReference(this.finReference);
		entity.setAmountType(this.amountType);
		entity.setAmount(this.amount);
		entity.setUtilisedAmt(this.utilisedAmt);
		entity.setReservedAmt(this.reservedAmt);
		entity.setBalanceAmt(this.balanceAmt);
		entity.setRcdAction(this.rcdAction);
		entity.setExcessMovement(this.excessMovement == null ? null : this.excessMovement.copyEntity());
		return entity;
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("rcdAction");
		excludeFields.add("excessMovement");
		excludeFields.add("expand");
		excludeFields.add("collapse");

		return excludeFields;
	}

	public long getId() {
		return excessID;
	}

	public void setId(long id) {
		this.excessID = id;
	}

	public long getExcessID() {
		return getId();
	}

	public void setExcessID(long excessID) {
		this.excessID = excessID;
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

	public String getAmountType() {
		return amountType;
	}

	public void setAmountType(String amountType) {
		this.amountType = amountType;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getUtilisedAmt() {
		return utilisedAmt;
	}

	public void setUtilisedAmt(BigDecimal utilisedAmt) {
		this.utilisedAmt = utilisedAmt;
	}

	public BigDecimal getReservedAmt() {
		return reservedAmt;
	}

	public void setReservedAmt(BigDecimal reservedAmt) {
		this.reservedAmt = reservedAmt;
	}

	public BigDecimal getBalanceAmt() {
		return balanceAmt;
	}

	public void setBalanceAmt(BigDecimal balanceAmt) {
		this.balanceAmt = balanceAmt;
	}

	public String getRcdAction() {
		return rcdAction;
	}

	public void setRcdAction(String rcdAction) {
		this.rcdAction = rcdAction;
	}

	public FinExcessMovement getExcessMovement() {
		return excessMovement;
	}

	public void setExcessMovement(FinExcessMovement excessMovement) {
		this.excessMovement = excessMovement;
	}

	public Long getReceiptID() {
		return receiptID;
	}

	public void setReceiptID(Long receiptID) {
		this.receiptID = receiptID;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public boolean isExpand() {
		return expand;
	}

	public void setExpand(boolean expand) {
		this.expand = expand;
	}

	public boolean isCollapse() {
		return collapse;
	}

	public void setCollapse(boolean collapse) {
		this.collapse = collapse;
	}

}
