package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

@XmlType(propOrder = { "gstType", "adviseAmount", "netCGST", "netSGST", "netIGST", "netUGST", "netTGST", "total" })
@XmlRootElement(name = "FinTaxDetails")
@XmlAccessorType(XmlAccessType.NONE)
public class FinTaxDetails extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long finTaxID = Long.MIN_VALUE;
	private long feeID;

	//Actual GST
	private BigDecimal actualCGST = BigDecimal.ZERO;
	private BigDecimal actualIGST = BigDecimal.ZERO;
	private BigDecimal actualUGST = BigDecimal.ZERO;
	private BigDecimal actualSGST = BigDecimal.ZERO;
	private BigDecimal actualTGST = BigDecimal.ZERO;

	//Paid GST
	private BigDecimal paidCGST = BigDecimal.ZERO;
	private BigDecimal paidIGST = BigDecimal.ZERO;
	private BigDecimal paidUGST = BigDecimal.ZERO;
	private BigDecimal paidSGST = BigDecimal.ZERO;
	private BigDecimal paidTGST = BigDecimal.ZERO;

	//NET GST
	@XmlElement
	private BigDecimal netCGST = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal netIGST = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal netUGST = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal netSGST = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal netTGST = BigDecimal.ZERO;

	//API Specific Field
	@XmlElement
	private BigDecimal adviseAmount = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal total = BigDecimal.ZERO;
	@XmlElement
	private String gstType;

	//Remaining Fee GST
	private BigDecimal remFeeCGST = BigDecimal.ZERO;
	private BigDecimal remFeeIGST = BigDecimal.ZERO;
	private BigDecimal remFeeUGST = BigDecimal.ZERO;
	private BigDecimal remFeeSGST = BigDecimal.ZERO;
	private BigDecimal remFeeTGST = BigDecimal.ZERO;

	private boolean newRecord;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("adviseAmount");
		excludeFields.add("total");
		excludeFields.add("gstType");

		return excludeFields;
	}

	public FinTaxDetails() {
		super();
	}

	public long getFinTaxID() {
		return finTaxID;
	}

	public void setFinTaxID(long finTaxID) {
		this.finTaxID = finTaxID;
	}

	public BigDecimal getPaidCGST() {
		return paidCGST;
	}

	public void setPaidCGST(BigDecimal paidCGST) {
		this.paidCGST = paidCGST;
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

	public BigDecimal getPaidSGST() {
		return paidSGST;
	}

	public void setPaidSGST(BigDecimal paidSGST) {
		this.paidSGST = paidSGST;
	}

	public BigDecimal getPaidTGST() {
		return paidTGST;
	}

	public void setPaidTGST(BigDecimal paidTGST) {
		this.paidTGST = paidTGST;
	}

	public BigDecimal getNetCGST() {
		return netCGST;
	}

	public void setNetCGST(BigDecimal netCGST) {
		this.netCGST = netCGST;
	}

	public BigDecimal getNetIGST() {
		return netIGST;
	}

	public void setNetIGST(BigDecimal netIGST) {
		this.netIGST = netIGST;
	}

	public BigDecimal getNetUGST() {
		return netUGST;
	}

	public void setNetUGST(BigDecimal netUGST) {
		this.netUGST = netUGST;
	}

	public BigDecimal getNetSGST() {
		return netSGST;
	}

	public void setNetSGST(BigDecimal netSGST) {
		this.netSGST = netSGST;
	}

	public BigDecimal getNetTGST() {
		return netTGST;
	}

	public void setNetTGST(BigDecimal netTGST) {
		this.netTGST = netTGST;
	}

	public BigDecimal getRemFeeCGST() {
		return remFeeCGST;
	}

	public void setRemFeeCGST(BigDecimal remFeeCGST) {
		this.remFeeCGST = remFeeCGST;
	}

	public BigDecimal getRemFeeIGST() {
		return remFeeIGST;
	}

	public void setRemFeeIGST(BigDecimal remFeeIGST) {
		this.remFeeIGST = remFeeIGST;
	}

	public BigDecimal getRemFeeUGST() {
		return remFeeUGST;
	}

	public void setRemFeeUGST(BigDecimal remFeeUGST) {
		this.remFeeUGST = remFeeUGST;
	}

	public BigDecimal getRemFeeSGST() {
		return remFeeSGST;
	}

	public void setRemFeeSGST(BigDecimal remFeeSGST) {
		this.remFeeSGST = remFeeSGST;
	}

	public BigDecimal getRemFeeTGST() {
		return remFeeTGST;
	}

	public void setRemFeeTGST(BigDecimal remFeeTGST) {
		this.remFeeTGST = remFeeTGST;
	}

	public long getFeeID() {
		return feeID;
	}

	public void setFeeID(long feeID) {
		this.feeID = feeID;
	}

	public BigDecimal getActualCGST() {
		return actualCGST;
	}

	public void setActualCGST(BigDecimal actualCGST) {
		this.actualCGST = actualCGST;
	}

	public BigDecimal getActualIGST() {
		return actualIGST;
	}

	public void setActualIGST(BigDecimal actualIGST) {
		this.actualIGST = actualIGST;
	}

	public BigDecimal getActualUGST() {
		return actualUGST;
	}

	public void setActualUGST(BigDecimal actualUGST) {
		this.actualUGST = actualUGST;
	}

	public BigDecimal getActualSGST() {
		return actualSGST;
	}

	public void setActualSGST(BigDecimal actualSGST) {
		this.actualSGST = actualSGST;
	}

	public BigDecimal getActualTGST() {
		return actualTGST;
	}

	public void setActualTGST(BigDecimal actualTGST) {
		this.actualTGST = actualTGST;
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public long getId() {
		return this.finTaxID;
	}

	public void setId(long id) {
		this.finTaxID = id;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public BigDecimal getAdviseAmount() {
		return adviseAmount;
	}

	public void setAdviseAmount(BigDecimal adviseAmount) {
		this.adviseAmount = adviseAmount;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public String getGstType() {
		return gstType;
	}

	public void setGstType(String gstType) {
		this.gstType = gstType;
	}
}
