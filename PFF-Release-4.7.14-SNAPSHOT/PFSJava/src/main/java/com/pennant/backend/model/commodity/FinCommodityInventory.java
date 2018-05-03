package com.pennant.backend.model.commodity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.pennant.backend.model.Entity;

public class FinCommodityInventory implements Serializable,Entity {

	private static final long serialVersionUID = -8617777212368059076L;

	private long finInventoryID;
	private String finreference;
	private String brokerCode;
	private String holdCertificateNo;
	private long quantity ;
	private long saleQuantity;
	private BigDecimal salePrice = BigDecimal.ZERO;
	private BigDecimal unitSalePrice = BigDecimal.ZERO;
	private String commodityStatus;
	private Date dateOfAllocation;
	private Date dateOfSelling;
	private Date dateCancelled;
	private BigDecimal feeCalculated = BigDecimal.ZERO;
	private Date feePayableDate;
	private BigDecimal feeBalance = BigDecimal.ZERO;

	public FinCommodityInventory() {

	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	@Override
	public long getId() {
		return finInventoryID;
	}

	@Override
    public void setId(long id) {
		this.finInventoryID = id;
    }
	
	public long getFinInventoryID() {
		return finInventoryID;
	}

	public void setFinInventoryID(long finInventoryID) {
		this.finInventoryID = finInventoryID;
	}

	public String getFinreference() {
		return finreference;
	}

	public void setFinreference(String finreference) {
		this.finreference = finreference;
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
	
	public BigDecimal getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(BigDecimal salePrice) {
		this.salePrice = salePrice;
	}

	public BigDecimal getUnitSalePrice() {
		return unitSalePrice;
	}

	public void setUnitSalePrice(BigDecimal unitSalePrice) {
		this.unitSalePrice = unitSalePrice;
	}

	public String getCommodityStatus() {
		return commodityStatus;
	}

	public void setCommodityStatus(String commodityStatus) {
		this.commodityStatus = commodityStatus;
	}

	public Date getDateOfAllocation() {
		return dateOfAllocation;
	}

	public void setDateOfAllocation(Date dateOfAllocation) {
		this.dateOfAllocation = dateOfAllocation;
	}

	public Date getDateOfSelling() {
		return dateOfSelling;
	}

	public void setDateOfSelling(Date dateOfSelling) {
		this.dateOfSelling = dateOfSelling;
	}

	public Date getDateCancelled() {
		return dateCancelled;
	}

	public void setDateCancelled(Date dateCancelled) {
		this.dateCancelled = dateCancelled;
	}

	public BigDecimal getFeeCalculated() {
		return feeCalculated;
	}

	public void setFeeCalculated(BigDecimal feeCalculated) {
		this.feeCalculated = feeCalculated;
	}

	public Date getFeePayableDate() {
		return feePayableDate;
	}

	public void setFeePayableDate(Date feePayableDate) {
		this.feePayableDate = feePayableDate;
	}

	public BigDecimal getFeeBalance() {
		return feeBalance;
	}

	public void setFeeBalance(BigDecimal feeBalance) {
		this.feeBalance = feeBalance;
	}

	@Override
    public boolean isNew() {
	    return false;
    }

	public long getQuantity() {
	    return quantity;
    }

	public void setQuantity(long quantity) {
	    this.quantity = quantity;
    }

	public long getSaleQuantity() {
	    return saleQuantity;
    }

	public void setSaleQuantity(long saleQuantity) {
	    this.saleQuantity = saleQuantity;
    }
}
