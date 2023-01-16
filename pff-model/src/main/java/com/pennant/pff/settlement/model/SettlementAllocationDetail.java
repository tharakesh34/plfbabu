package com.pennant.pff.settlement.model;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class SettlementAllocationDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private long headerID;
	@XmlElement
	private String allocationType;
	private long allocationTo;
	private BigDecimal totalDue = BigDecimal.ZERO;
	private BigDecimal paidAmount = BigDecimal.ZERO;
	private BigDecimal paidGST = BigDecimal.ZERO;
	private BigDecimal waivedAmount = BigDecimal.ZERO;
	private String waiverAccepted = "";
	private BigDecimal waivedGST = BigDecimal.ZERO;
	private long taxHeaderID;
	private BigDecimal tdsDue = BigDecimal.ZERO;
	private BigDecimal tdsPaid = BigDecimal.ZERO;
	private BigDecimal tdsWaived = BigDecimal.ZERO;
	private String typeDesc;
	private boolean isSubListAvailable = false;
	private BigDecimal balance = BigDecimal.ZERO;
	private long allocationID;
	private BigDecimal waivedReqAmount;

	public SettlementAllocationDetail() {
		super();
	}

	public SettlementAllocationDetail copyEntity() {
		SettlementAllocationDetail entity = new SettlementAllocationDetail();
		entity.setHeaderID(this.headerID);
		entity.setId(this.id);
		entity.setAllocationType(this.allocationType);
		entity.setAllocationTo(this.allocationTo);
		entity.setTotalDue(this.totalDue);
		entity.setPaidAmount(this.paidAmount);
		entity.setPaidGST(this.paidGST);
		entity.setWaivedAmount(this.waivedAmount);
		entity.setWaiverAccepted(this.waiverAccepted);
		entity.setWaivedGST(this.waivedGST);
		entity.setTaxHeaderID(this.taxHeaderID);
		entity.setTdsDue(this.tdsDue);
		entity.setTdsPaid(this.tdsPaid);
		entity.setTdsWaived(this.tdsWaived);
		entity.setTypeDesc(this.typeDesc);
		entity.setSubListAvailable(this.isSubListAvailable);
		entity.setBalance(this.balance);
		entity.setAllocationID(this.allocationID);
		entity.setWaivedReqAmount(this.waivedReqAmount);

		return entity;
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("typeDesc");
		excludeFields.add("isSubListAvailable");
		excludeFields.add("balance");
		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getHeaderID() {
		return headerID;
	}

	public void setHeaderID(long headerID) {
		this.headerID = headerID;
	}

	public String getAllocationType() {
		return allocationType;
	}

	public void setAllocationType(String allocationType) {
		this.allocationType = allocationType;
	}

	public long getAllocationTo() {
		return allocationTo;
	}

	public void setAllocationTo(long allocationTo) {
		this.allocationTo = allocationTo;
	}

	public BigDecimal getTotalDue() {
		return totalDue;
	}

	public void setTotalDue(BigDecimal totalDue) {
		this.totalDue = totalDue;
	}

	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}

	public BigDecimal getPaidGST() {
		return paidGST;
	}

	public void setPaidGST(BigDecimal paidGST) {
		this.paidGST = paidGST;
	}

	public BigDecimal getWaivedAmount() {
		return waivedAmount;
	}

	public void setWaivedAmount(BigDecimal waivedAmount) {
		this.waivedAmount = waivedAmount;
	}

	public String getWaiverAccepted() {
		return waiverAccepted;
	}

	public void setWaiverAccepted(String waiverAccepted) {
		this.waiverAccepted = waiverAccepted;
	}

	public BigDecimal getWaivedGST() {
		return waivedGST;
	}

	public void setWaivedGST(BigDecimal waivedGST) {
		this.waivedGST = waivedGST;
	}

	public long getTaxHeaderID() {
		return taxHeaderID;
	}

	public void setTaxHeaderID(long taxHeaderID) {
		this.taxHeaderID = taxHeaderID;
	}

	public BigDecimal getTdsDue() {
		return tdsDue;
	}

	public void setTdsDue(BigDecimal tdsDue) {
		this.tdsDue = tdsDue;
	}

	public BigDecimal getTdsPaid() {
		return tdsPaid;
	}

	public void setTdsPaid(BigDecimal tdsPaid) {
		this.tdsPaid = tdsPaid;
	}

	public BigDecimal getTdsWaived() {
		return tdsWaived;
	}

	public void setTdsWaived(BigDecimal tdsWaived) {
		this.tdsWaived = tdsWaived;
	}

	public String getTypeDesc() {
		return typeDesc;
	}

	public void setTypeDesc(String typeDesc) {
		this.typeDesc = typeDesc;
	}

	public boolean isSubListAvailable() {
		return isSubListAvailable;
	}

	public void setSubListAvailable(boolean isSubListAvailable) {
		this.isSubListAvailable = isSubListAvailable;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public long getAllocationID() {
		return allocationID;
	}

	public void setAllocationID(long allocationID) {
		this.allocationID = allocationID;
	}

	public BigDecimal getWaivedReqAmount() {
		return waivedReqAmount;
	}

	public void setWaivedReqAmount(BigDecimal waivedReqAmount) {
		this.waivedReqAmount = waivedReqAmount;
	}

}
