package com.pennant.backend.model.inventorysettlement;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class InventorySettlementDetails implements Serializable {

	private static final long serialVersionUID = -8739594888779797571L;

	private long id = Long.MIN_VALUE;
	private String brokerCode;
	private String holdCertificateNo;
	private long unsoldQty;
	private BigDecimal unsoldFee;
	
	//other fields
	private long quantity;
	private long saleQuantity;
	private String commodityCode;
	private BigDecimal unitPrice;
	private String commodityCcy;
	private Date finalSettlementDate;
	private BigDecimal feeOnUnsold;
	private long brokerCustID;
	private String accountNumber;	

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getBrokerCode() {
		return brokerCode;
	}

	public void setBrokerCode(String brokerCode) {
		this.brokerCode = brokerCode;
	}

	public String getHoldCertificateNo() {
		return holdCertificateNo;
	}

	public void setHoldCertificateNo(String holdCertificateNo) {
		this.holdCertificateNo = holdCertificateNo;
	}
	public String getCommodityCode() {
		return commodityCode;
	}

	public void setCommodityCode(String commodityCode) {
		this.commodityCode = commodityCode;
	}

	public String getCommodityCcy() {
		return commodityCcy;
	}

	public void setCommodityCcy(String commodityCcy) {
		this.commodityCcy = commodityCcy;
	}


	public Date getFinalSettlementDate() {
		return finalSettlementDate;
	}

	public void setFinalSettlementDate(Date finalSettlementDate) {
		this.finalSettlementDate = finalSettlementDate;
	}

	public long getUnsoldQty() {
		return unsoldQty;
	}

	public void setUnsoldQty(long unsoldQty) {
		this.unsoldQty = unsoldQty;
	}

	public BigDecimal getFeeOnUnsold() {
		return feeOnUnsold;
	}

	public void setFeeOnUnsold(BigDecimal feeOnUnsold) {
		this.feeOnUnsold = feeOnUnsold;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
	
	public BigDecimal getUnsoldFee() {
		return unsoldFee;
	}

	public void setUnsoldFee(BigDecimal unsoldFee) {
		this.unsoldFee = unsoldFee;
	}

	public long getSaleQuantity() {
		return saleQuantity;
	}

	public void setSaleQuantity(long soldQuantity) {
		this.saleQuantity = soldQuantity;
	}

	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

	public long getBrokerCustID() {
		return brokerCustID;
	}

	public void setBrokerCustID(long brokerCustID) {
		this.brokerCustID = brokerCustID;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

}
