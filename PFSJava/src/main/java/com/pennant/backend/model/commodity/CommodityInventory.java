/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ******************************************************************************************** 
 * FILE HEADER *
 ******************************************************************************************** 
 * * FileName : CommodityInventory.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 23-04-2015 * * Modified
 * Date : 23-04-2015 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 23-04-2015 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */
package com.pennant.backend.model.commodity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>CommodityInventory table</b>.<br>
 * 
 */
public class CommodityInventory extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;

	private long commodityInvId = Long.MIN_VALUE;
	private String brokerCode;
	private String brokerCodeName;
	private String holdCertificateNo;
	private String commodityCode;
	private String commodityCodeName;
	private Date purchaseDate;
	private Date finalSettlementDate;
	private BigDecimal purchaseAmount;
	private BigDecimal unitPrice;
	private String units;
	private String unitsName;
	private long quantity;
	private String location;
	private boolean bulkPurchase;
	private String commodityCcy;
	private boolean newRecord;
	private String lovValue;
	private CommodityInventory befImage;
	private LoggedInUser userDetails;

	private String brokerShrtName;
	private String lovDescRequestStage;
	private BigDecimal feeOnUnsold = BigDecimal.ZERO;
	private String locationCode;
	private String locationDesc;
	private long brokerCustID;
	private String accountNumber;
	private String lovDescCommodityDesc;
	
	//Enquiry Fields
	private long totalQuantityAllocated;
	private long soldQuantity;
	private long allocatedQuantity;
	private long cancelledQuantity;

	public boolean isNew() {
		return isNewRecord();
	}

	public CommodityInventory() {
		super();
	}

	public CommodityInventory(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("brokerCodeName");
		excludeFields.add("commodityCodeName");
		excludeFields.add("unitsName");
		excludeFields.add("brokerShrtName");
		excludeFields.add("lovDescRequestStage");
		excludeFields.add("totalQuantityAllocated");
		excludeFields.add("soldQuantity");
		excludeFields.add("allocatedQuantity");
		excludeFields.add("cancelledQuantity");
		excludeFields.add("feeOnUnsold");
		excludeFields.add("ccyEditField");
		excludeFields.add("locationCode");
		excludeFields.add("locationDesc");
		excludeFields.add("commodityCcyDesc");
		excludeFields.add("brokerCustID");
		excludeFields.add("accountNumber");
		excludeFields.add("lovDescCommodityDesc");
		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getId() {
		return commodityInvId;
	}

	public void setId(long id) {
		this.commodityInvId = id;
	}

	public long getCommodityInvId() {
		return commodityInvId;
	}

	public void setCommodityInvId(long commodityInvId) {
		this.commodityInvId = commodityInvId;
	}

	public String getBrokerCode() {
		return brokerCode;
	}

	public void setBrokerCode(String brokerCode) {
		this.brokerCode = brokerCode;
	}

	public String getBrokerCodeName() {
		return this.brokerCodeName;
	}

	public void setBrokerCodeName(String brokerCodeName) {
		this.brokerCodeName = brokerCodeName;
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

	public String getCommodityCodeName() {
		return this.commodityCodeName;
	}

	public void setCommodityCodeName(String commodityCodeName) {
		this.commodityCodeName = commodityCodeName;
	}

	public Date getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public Date getFinalSettlementDate() {
		return finalSettlementDate;
	}

	public void setFinalSettlementDate(Date finalSettlementDate) {
		this.finalSettlementDate = finalSettlementDate;
	}

	public BigDecimal getPurchaseAmount() {
		return purchaseAmount;
	}

	public void setPurchaseAmount(BigDecimal purchaseAmount) {
		this.purchaseAmount = purchaseAmount;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public String getUnitsName() {
		return this.unitsName;
	}

	public void setUnitsName(String unitsName) {
		this.unitsName = unitsName;
	}

	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public boolean isBulkPurchase() {
		return bulkPurchase;
	}

	public void setBulkPurchase(boolean bulkPurchase) {
		this.bulkPurchase = bulkPurchase;
	}

	public long getTotalQuantityAllocated() {
		return totalQuantityAllocated;
	}

	public void setTotalQuantityAllocated(long totalQuantityAllocated) {
		this.totalQuantityAllocated = totalQuantityAllocated;
	}

	public long getSoldQuantity() {
		return soldQuantity;
	}

	public void setSoldQuantity(long soldQuantity) {
		this.soldQuantity = soldQuantity;
	}

	public long getAllocatedQuantity() {
		return allocatedQuantity;
	}

	public void setAllocatedQuantity(long allocatedQuantity) {
		this.allocatedQuantity = allocatedQuantity;
	}

	public long getCancelledQuantity() {
		return cancelledQuantity;
	}

	public void setCancelledQuantity(long cancelledQuantity) {
		this.cancelledQuantity = cancelledQuantity;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public CommodityInventory getBefImage() {
		return this.befImage;
	}

	public void setBefImage(CommodityInventory beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getBrokerShrtName() {
		return brokerShrtName;
	}

	public void setBrokerShrtName(String brokerShrtName) {
		this.brokerShrtName = brokerShrtName;
	}

	public String getLovDescRequestStage() {
		return lovDescRequestStage;
	}

	public void setLovDescRequestStage(String lovDescRequestStage) {
		this.lovDescRequestStage = lovDescRequestStage;
	}

	public BigDecimal getFeeOnUnsold() {
	    return feeOnUnsold;
    }

	public void setFeeOnUnsold(BigDecimal feeOnUnsold) {
	    this.feeOnUnsold = feeOnUnsold;
    }

	public String getCommodityCcy() {
	    return commodityCcy;
    }

	public void setCommodityCcy(String commodityCcy) {
	    this.commodityCcy = commodityCcy;
    }

	public String getLocationCode() {
	    return locationCode;
    }

	public void setLocationCode(String locationCode) {
	    this.locationCode = locationCode;
    }

	public String getLocationDesc() {
	    return locationDesc;
    }

	public void setLocationDesc(String locationDesc) {
	    this.locationDesc = locationDesc;
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

	public String getLovDescCommodityDesc() {
		return lovDescCommodityDesc;
	}

	public void setLovDescCommodityDesc(String lovDescCommodityDesc) {
		this.lovDescCommodityDesc = lovDescCommodityDesc;
	}
}
