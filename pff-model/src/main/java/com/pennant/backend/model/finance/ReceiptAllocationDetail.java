package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class ReceiptAllocationDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	private long receiptAllocationid = Long.MIN_VALUE;
	private long receiptID = 0;
	private int allocationID = 0;
	@XmlElement
	private String allocationType;
	private String typeDesc;
	private long allocationTo;
	private BigDecimal totRecv = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal dueAmount = BigDecimal.ZERO;
	private BigDecimal dueGST = BigDecimal.ZERO;
	private BigDecimal totalDue = BigDecimal.ZERO;
	private BigDecimal paidAmount = BigDecimal.ZERO;
	private BigDecimal paidGST = BigDecimal.ZERO;
	private BigDecimal totalPaid = BigDecimal.ZERO;
	private BigDecimal waivedAmount = BigDecimal.ZERO;
	private BigDecimal waivedAvailable = BigDecimal.ZERO;
	private BigDecimal paidAvailable = BigDecimal.ZERO;
	private String waiverAccepted = "";
	private BigDecimal balance = BigDecimal.ZERO;
	private BigDecimal paidCGST = BigDecimal.ZERO;
	private BigDecimal paidSGST = BigDecimal.ZERO;
	private BigDecimal paidIGST = BigDecimal.ZERO;
	private BigDecimal paidUGST = BigDecimal.ZERO;

	private BigDecimal paidNow = BigDecimal.ZERO;
	private BigDecimal waivedNow = BigDecimal.ZERO;

	//In Process Allocation along with GST and TDS
	private BigDecimal inProcess = BigDecimal.ZERO;

	private String taxType = "";
	private String feeTypeCode = "";
	private long feeId;

	//Applicable only for allocation summary in receipt header
	private boolean isSubListAvailable = false;
	private boolean isEditable = false;

	private List<ReceiptAllocationDetail> subList = new ArrayList<>();

	public ReceiptAllocationDetail() {
		super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public int getAllocationID() {
		return allocationID;
	}

	public void setAllocationID(int allocationID) {
		this.allocationID = allocationID;
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

	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}

	public BigDecimal getWaivedAmount() {
		return waivedAmount;
	}

	public void setWaivedAmount(BigDecimal waivedAmount) {
		this.waivedAmount = waivedAmount;
	}

	public long getReceiptID() {
		return receiptID;
	}

	public void setReceiptID(long receiptID) {
		this.receiptID = receiptID;
	}

	public String getTypeDesc() {
		return typeDesc;
	}

	public void setTypeDesc(String typeDesc) {
		this.typeDesc = typeDesc;
	}

	public long getReceiptAllocationid() {
		return receiptAllocationid;
	}

	public void setReceiptAllocationid(long receiptAllocationid) {
		this.receiptAllocationid = receiptAllocationid;
	}

	public boolean isNew() {
		return false;
	}

	public long getId() {
		return receiptID;
	}

	public void setId(long id) {
		this.receiptID = id;
	}

	public BigDecimal getPaidGST() {
		return paidGST;
	}

	public void setPaidGST(BigDecimal paidGST) {
		this.paidGST = paidGST;
	}

	public BigDecimal getDueAmount() {
		return dueAmount;
	}

	public void setDueAmount(BigDecimal dueAmount) {
		this.dueAmount = dueAmount;
	}

	public String getWaiverAccepted() {
		return waiverAccepted;
	}

	public void setWaiverAccepted(String waiverAccepted) {
		this.waiverAccepted = waiverAccepted;
	}

	public BigDecimal getDueGST() {
		return dueGST;
	}

	public void setDueGST(BigDecimal dueGST) {
		this.dueGST = dueGST;
	}

	public BigDecimal getTotalPaid() {
		return totalPaid;
	}

	public void setTotalPaid(BigDecimal totalPaid) {
		this.totalPaid = totalPaid;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public String getTaxType() {
		return taxType;
	}

	public void setTaxType(String taxType) {
		this.taxType = taxType;
	}

	public BigDecimal getTotalDue() {
		return totalDue;
	}

	public void setTotalDue(BigDecimal totalDue) {
		this.totalDue = totalDue;
	}

	public BigDecimal getWaivedAvailable() {
		return waivedAvailable;
	}

	public void setWaivedAvailable(BigDecimal waivedAvailable) {
		this.waivedAvailable = waivedAvailable;
	}

	public BigDecimal getPaidCGST() {
		return paidCGST;
	}

	public void setPaidCGST(BigDecimal paidCGST) {
		this.paidCGST = paidCGST;
	}

	public BigDecimal getPaidSGST() {
		return paidSGST;
	}

	public void setPaidSGST(BigDecimal paidSGST) {
		this.paidSGST = paidSGST;
	}

	public BigDecimal getPaidIGST() {
		return paidIGST;
	}

	public void setPaidIGST(BigDecimal paidIGST) {
		this.paidIGST = paidIGST;
	}

	public BigDecimal getPaidUGST() {
		return paidUGST;
	}

	public void setPaidUGST(BigDecimal paidUGST) {
		this.paidUGST = paidUGST;
	}

	public BigDecimal getPaidNow() {
		return paidNow;
	}

	public void setPaidNow(BigDecimal paidNow) {
		this.paidNow = paidNow;
	}

	public BigDecimal getWaivedNow() {
		return waivedNow;
	}

	public void setWaivedNow(BigDecimal waivedNow) {
		this.waivedNow = waivedNow;
	}

	public BigDecimal getPaidAvailable() {
		return paidAvailable;
	}

	public void setPaidAvailable(BigDecimal paidAvailable) {
		this.paidAvailable = paidAvailable;
	}

	public String getFeeTypeCode() {
		return feeTypeCode;
	}

	public void setFeeTypeCode(String feeTypeCode) {
		this.feeTypeCode = feeTypeCode;
	}

	public long getFeeId() {
		return feeId;
	}

	public void setFeeId(long feeId) {
		this.feeId = feeId;
	}

	public BigDecimal getInProcess() {
		return inProcess;
	}

	public void setInProcess(BigDecimal inProcess) {
		this.inProcess = inProcess;
	}

	public boolean isSubListAvailable() {
		return isSubListAvailable;
	}

	public void setSubListAvailable(boolean isSubListAvailable) {
		this.isSubListAvailable = isSubListAvailable;
	}

	public boolean isEditable() {
		return isEditable;
	}

	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}

	public BigDecimal getTotRecv() {
		return totRecv;
	}

	public void setTotRecv(BigDecimal totRecv) {
		this.totRecv = totRecv;
	}

	public List<ReceiptAllocationDetail> getSubList() {
		return subList;
	}

	public void setSubList(List<ReceiptAllocationDetail> subList) {
		this.subList = subList;
	}

}
