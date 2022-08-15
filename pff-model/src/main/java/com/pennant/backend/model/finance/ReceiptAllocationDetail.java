package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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
	private BigDecimal paidCESS = BigDecimal.ZERO;

	private BigDecimal percCGST = BigDecimal.ZERO;
	private BigDecimal percSGST = BigDecimal.ZERO;
	private BigDecimal percIGST = BigDecimal.ZERO;
	private BigDecimal percUGST = BigDecimal.ZERO;
	private BigDecimal percCESS = BigDecimal.ZERO;

	private BigDecimal paidNow = BigDecimal.ZERO;
	private BigDecimal waivedNow = BigDecimal.ZERO;

	// Waiver GST fields
	private BigDecimal waivedCGST = BigDecimal.ZERO;
	private BigDecimal waivedSGST = BigDecimal.ZERO;
	private BigDecimal waivedIGST = BigDecimal.ZERO;
	private BigDecimal waivedUGST = BigDecimal.ZERO;
	private BigDecimal waivedCESS = BigDecimal.ZERO;
	private BigDecimal waivedGST = BigDecimal.ZERO;

	// Waiver GST fields
	private BigDecimal dueCGST = BigDecimal.ZERO;
	private BigDecimal dueSGST = BigDecimal.ZERO;
	private BigDecimal dueIGST = BigDecimal.ZERO;
	private BigDecimal dueUGST = BigDecimal.ZERO;
	private BigDecimal dueCESS = BigDecimal.ZERO;

	// In Process Allocation along with GST and TDS
	private BigDecimal inProcess = BigDecimal.ZERO;

	private String taxType = "";
	@XmlElement
	private String feeTypeCode = "";
	private long feeId;
	private Long taxHeaderId;
	private TaxHeader taxHeader;

	// Applicable only for allocation summary in receipt header
	private boolean isSubListAvailable = false;
	private boolean isEditable = false;

	private List<ReceiptAllocationDetail> subList = new ArrayList<>();

	private int dispayOrder = 0;
	private BigDecimal tdsPaidNow = BigDecimal.ZERO;
	private BigDecimal tdsWaivedNow = BigDecimal.ZERO;

	private BigDecimal tdsDue = BigDecimal.ZERO;
	private BigDecimal tdsPaid = BigDecimal.ZERO;
	private BigDecimal tdsWaived = BigDecimal.ZERO;

	private boolean isTdsReq = false;
	private BigDecimal percTds = BigDecimal.ZERO;

	private Date valueDate;

	public ReceiptAllocationDetail() {
		super();
	}

	public ReceiptAllocationDetail copyEntity() {
		ReceiptAllocationDetail entity = new ReceiptAllocationDetail();
		entity.setReceiptAllocationid(this.receiptAllocationid);
		entity.setReceiptID(this.receiptID);
		entity.setAllocationID(this.allocationID);
		entity.setAllocationType(this.allocationType);
		entity.setTypeDesc(this.typeDesc);
		entity.setAllocationTo(this.allocationTo);
		entity.setTotRecv(this.totRecv);
		entity.setDueAmount(this.dueAmount);
		entity.setDueGST(this.dueGST);
		entity.setTotalDue(this.totalDue);
		entity.setPaidAmount(this.paidAmount);
		entity.setPaidGST(this.paidGST);
		entity.setTotalPaid(this.totalPaid);
		entity.setWaivedAmount(this.waivedAmount);
		entity.setWaivedAvailable(this.waivedAvailable);
		entity.setPaidAvailable(this.paidAvailable);
		entity.setWaiverAccepted(this.waiverAccepted);
		entity.setBalance(this.balance);
		entity.setPaidCGST(this.paidCGST);
		entity.setPaidSGST(this.paidSGST);
		entity.setPaidIGST(this.paidIGST);
		entity.setPaidUGST(this.paidUGST);
		entity.setPaidCESS(this.paidCESS);
		entity.setPercCGST(this.percCGST);
		entity.setPercSGST(this.percSGST);
		entity.setPercIGST(this.percIGST);
		entity.setPercUGST(this.percUGST);
		entity.setPercCESS(this.percCESS);
		entity.setPaidNow(this.paidNow);
		entity.setWaivedNow(this.waivedNow);
		entity.setWaivedCGST(this.waivedCGST);
		entity.setWaivedSGST(this.waivedSGST);
		entity.setWaivedIGST(this.waivedIGST);
		entity.setWaivedUGST(this.waivedUGST);
		entity.setWaivedCESS(this.waivedCESS);
		entity.setWaivedGST(this.waivedGST);
		entity.setDueCGST(this.dueCGST);
		entity.setDueSGST(this.dueSGST);
		entity.setDueIGST(this.dueIGST);
		entity.setDueUGST(this.dueUGST);
		entity.setDueCESS(this.dueCESS);
		entity.setInProcess(this.inProcess);
		entity.setTaxType(this.taxType);
		entity.setFeeTypeCode(this.feeTypeCode);
		entity.setFeeId(this.feeId);
		entity.setTaxHeaderId(this.taxHeaderId);
		entity.setTaxHeader(this.taxHeader == null ? null : this.taxHeader.copyEntity());
		entity.setSubListAvailable(this.isSubListAvailable);
		entity.setEditable(this.isEditable);
		this.subList.stream().forEach(e -> entity.getSubList().add(e.copyEntity()));
		entity.setDispayOrder(this.dispayOrder);
		entity.setTdsPaidNow(this.tdsPaidNow);
		entity.setTdsWaivedNow(this.tdsWaivedNow);
		entity.setTdsDue(this.tdsDue);
		entity.setTdsPaid(this.tdsPaid);
		entity.setTdsWaived(this.tdsWaived);
		entity.setTdsReq(this.isTdsReq);
		entity.setPercTds(this.percTds);
		entity.setValueDate(this.valueDate);
		return entity;
	}

	public long getReceiptAllocationid() {
		return receiptAllocationid;
	}

	public void setReceiptAllocationid(long receiptAllocationid) {
		this.receiptAllocationid = receiptAllocationid;
	}

	public long getReceiptID() {
		return receiptID;
	}

	public void setReceiptID(long receiptID) {
		this.receiptID = receiptID;
	}

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

	public String getTypeDesc() {
		return typeDesc;
	}

	public void setTypeDesc(String typeDesc) {
		this.typeDesc = typeDesc;
	}

	public long getAllocationTo() {
		return allocationTo;
	}

	public void setAllocationTo(long allocationTo) {
		this.allocationTo = allocationTo;
	}

	public BigDecimal getTotRecv() {
		return totRecv;
	}

	public void setTotRecv(BigDecimal totRecv) {
		this.totRecv = totRecv;
	}

	public BigDecimal getDueAmount() {
		return dueAmount;
	}

	public void setDueAmount(BigDecimal dueAmount) {
		this.dueAmount = dueAmount;
	}

	public BigDecimal getDueGST() {
		return dueGST;
	}

	public void setDueGST(BigDecimal dueGST) {
		this.dueGST = dueGST;
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

	public BigDecimal getTotalPaid() {
		return totalPaid;
	}

	public void setTotalPaid(BigDecimal totalPaid) {
		this.totalPaid = totalPaid;
	}

	public BigDecimal getWaivedAmount() {
		return waivedAmount;
	}

	public void setWaivedAmount(BigDecimal waivedAmount) {
		this.waivedAmount = waivedAmount;
	}

	public BigDecimal getWaivedAvailable() {
		return waivedAvailable;
	}

	public void setWaivedAvailable(BigDecimal waivedAvailable) {
		this.waivedAvailable = waivedAvailable;
	}

	public BigDecimal getPaidAvailable() {
		return paidAvailable;
	}

	public void setPaidAvailable(BigDecimal paidAvailable) {
		this.paidAvailable = paidAvailable;
	}

	public String getWaiverAccepted() {
		return waiverAccepted;
	}

	public void setWaiverAccepted(String waiverAccepted) {
		this.waiverAccepted = waiverAccepted;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
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

	public BigDecimal getWaivedCGST() {
		return waivedCGST;
	}

	public void setWaivedCGST(BigDecimal waivedCGST) {
		this.waivedCGST = waivedCGST;
	}

	public BigDecimal getWaivedSGST() {
		return waivedSGST;
	}

	public void setWaivedSGST(BigDecimal waivedSGST) {
		this.waivedSGST = waivedSGST;
	}

	public BigDecimal getWaivedIGST() {
		return waivedIGST;
	}

	public void setWaivedIGST(BigDecimal waivedIGST) {
		this.waivedIGST = waivedIGST;
	}

	public BigDecimal getWaivedUGST() {
		return waivedUGST;
	}

	public void setWaivedUGST(BigDecimal waivedUGST) {
		this.waivedUGST = waivedUGST;
	}

	public BigDecimal getWaivedGST() {
		return waivedGST;
	}

	public void setWaivedGST(BigDecimal waivedGST) {
		this.waivedGST = waivedGST;
	}

	public BigDecimal getInProcess() {
		return inProcess;
	}

	public void setInProcess(BigDecimal inProcess) {
		this.inProcess = inProcess;
	}

	public String getTaxType() {
		return taxType;
	}

	public void setTaxType(String taxType) {
		this.taxType = taxType;
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

	public Long getTaxHeaderId() {
		return taxHeaderId;
	}

	public void setTaxHeaderId(Long taxHeaderId) {
		this.taxHeaderId = taxHeaderId;
	}

	public TaxHeader getTaxHeader() {
		return taxHeader;
	}

	public void setTaxHeader(TaxHeader taxHeader) {
		this.taxHeader = taxHeader;
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

	public List<ReceiptAllocationDetail> getSubList() {
		return subList;
	}

	public void setSubList(List<ReceiptAllocationDetail> subList) {
		this.subList = subList;
	}

	public int getDispayOrder() {
		return dispayOrder;
	}

	public void setDispayOrder(int dispayOrder) {
		this.dispayOrder = dispayOrder;
	}

	public BigDecimal getTdsPaidNow() {
		return tdsPaidNow;
	}

	public void setTdsPaidNow(BigDecimal tdsPaidNow) {
		this.tdsPaidNow = tdsPaidNow;
	}

	public BigDecimal getTdsWaivedNow() {
		return tdsWaivedNow;
	}

	public void setTdsWaivedNow(BigDecimal tdsWaivedNow) {
		this.tdsWaivedNow = tdsWaivedNow;
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

	public BigDecimal getPercCGST() {
		return percCGST;
	}

	public void setPercCGST(BigDecimal percCGST) {
		this.percCGST = percCGST;
	}

	public BigDecimal getPercSGST() {
		return percSGST;
	}

	public void setPercSGST(BigDecimal percSGST) {
		this.percSGST = percSGST;
	}

	public BigDecimal getPercIGST() {
		return percIGST;
	}

	public void setPercIGST(BigDecimal percIGST) {
		this.percIGST = percIGST;
	}

	public BigDecimal getPercUGST() {
		return percUGST;
	}

	public void setPercUGST(BigDecimal percUGST) {
		this.percUGST = percUGST;
	}

	public boolean isTdsReq() {
		return isTdsReq;
	}

	public void setTdsReq(boolean isTdsReq) {
		this.isTdsReq = isTdsReq;
	}

	public BigDecimal getPercTds() {
		return percTds;
	}

	public void setPercTds(BigDecimal percTds) {
		this.percTds = percTds;
	}

	public BigDecimal getPaidCESS() {
		return paidCESS;
	}

	public void setPaidCESS(BigDecimal paidCESS) {
		this.paidCESS = paidCESS;
	}

	public BigDecimal getWaivedCESS() {
		return waivedCESS;
	}

	public void setWaivedCESS(BigDecimal waivedCESS) {
		this.waivedCESS = waivedCESS;
	}

	public BigDecimal getPercCESS() {
		return percCESS;
	}

	public void setPercCESS(BigDecimal percCESS) {
		this.percCESS = percCESS;
	}

	public BigDecimal getDueCGST() {
		return dueCGST;
	}

	public void setDueCGST(BigDecimal dueCGST) {
		this.dueCGST = dueCGST;
	}

	public BigDecimal getDueSGST() {
		return dueSGST;
	}

	public void setDueSGST(BigDecimal dueSGST) {
		this.dueSGST = dueSGST;
	}

	public BigDecimal getDueIGST() {
		return dueIGST;
	}

	public void setDueIGST(BigDecimal dueIGST) {
		this.dueIGST = dueIGST;
	}

	public BigDecimal getDueUGST() {
		return dueUGST;
	}

	public void setDueUGST(BigDecimal dueUGST) {
		this.dueUGST = dueUGST;
	}

	public BigDecimal getDueCESS() {
		return dueCESS;
	}

	public void setDueCESS(BigDecimal dueCESS) {
		this.dueCESS = dueCESS;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

}
