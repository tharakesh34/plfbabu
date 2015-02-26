/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  CarLoanDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-10-2011    														*
 *                                                                  						*
 * Modified Date    :  08-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-10-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/

package com.pennant.backend.model.lmtmasters;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>CarLoanDetail table</b>.<br>
 *
 */
public class CarLoanDetail implements java.io.Serializable {
	
	private static final long serialVersionUID = -6896226621927563737L;
	
	private String loanRefNumber;
	private int itemNumber;
	private boolean loanRefType;
	private long carLoanFor;
	private String lovDescCarLoanForName;
	private String lovDescLoanForValue;
	private long carUsage;
	private String lovDescCarUsageName;
	private String lovDescCarUsageValue;
	private long vehicleModelId;
	private String lovDescModelDesc;
	private long manufacturerId;
	private String lovDescManufacturerName;
	private long carVersion;
	private String lovDescCarVersionName;
	private String lovDescVehicleVersionCode;
	private int carMakeYear;
	private int carCapacity;
	private long carDealer;
	private String lovDescCarDealerName;
	private String lovDescCarDealerPhone;
	private String lovDescCarDealerFax;
	private int carCc;
	private String carChasisNo;

	private String carRegNo;
	private String carColor;
	private String lovDescCarColorName;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private CarLoanDetail befImage;
	private LoginUserDetails userDetails;
	
	private String engineNumber;
	
	private String paymentMode;
	private String purchageOdrNumber;
	private String quoationNbr;
	private Date quoationDate;
	private String dealerPhone;
	private Date purchaseDate;
	private String lovDescThirdPartyNatName;
	
	private boolean thirdPartyReg=false;
	private String thirdPartyName;
	private String passportNum;
	private String thirdPartyNat;
	private String emiratesRegNum;
	private String dealerOrSellerAcc;
	private String sellerType;
	private BigDecimal vehicleValue=BigDecimal.ZERO;
	private String privateDealerName;
	private String salesPersonName;
	
	
	private String recordStatus;
	private String roleCode="";
	private String nextRoleCode= "";
	private String taskId="";
	private String nextTaskId= "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;

	private List<CarLoanDetail> vehicleLoanDetailList = new ArrayList<CarLoanDetail>();
	
	public boolean isNew() {
		return isNewRecord();
	}

	public CarLoanDetail() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("CarLoanDetail");
	}
	
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("vehicleLoanDetailList");
		return excludeFields;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public CarLoanDetail(String loanRefNumber) {
		super();
		this.loanRefNumber = loanRefNumber;
	}

	public String getLoanRefNumber() {
		return loanRefNumber;
	}
	public void setLoanRefNumber(String loanRefNumber) {
		this.loanRefNumber = loanRefNumber;
	}

	public int getItemNumber() {
		return itemNumber;
	}
	public void setItemNumber(int itemNumber) {
		this.itemNumber = itemNumber;
	}

	public boolean isLoanRefType() {
		return loanRefType;
	}
	public void setLoanRefType(boolean loanRefType) {
		this.loanRefType = loanRefType;
	}
	
	public long getCarLoanFor() {
		return carLoanFor;
	}
	public void setCarLoanFor(long carLoanFor) {
		this.carLoanFor = carLoanFor;
	}

	public String getLovDescCarLoanForName() {
		return this.lovDescCarLoanForName;
	}
	public void setLovDescCarLoanForName (String lovDescCarLoanForName) {
		this.lovDescCarLoanForName = lovDescCarLoanForName;
	}
	
	public String getLovDescLoanForValue() {
		return lovDescLoanForValue;
	}
	public void setLovDescLoanForValue(String lovDescLoanForValue) {
		this.lovDescLoanForValue = lovDescLoanForValue;
	}

	public long getCarUsage() {
		return carUsage;
	}
	public void setCarUsage(long carUsage) {
		this.carUsage = carUsage;
	}

	public String getLovDescCarUsageName() {
		return this.lovDescCarUsageName;
	}
	public void setLovDescCarUsageName (String lovDescCarUsageName) {
		this.lovDescCarUsageName = lovDescCarUsageName;
	}
	
	public String getLovDescCarUsageValue() {
		return lovDescCarUsageValue;
	}
	public void setLovDescCarUsageValue(String lovDescCarUsageValue) {
		this.lovDescCarUsageValue = lovDescCarUsageValue;
	}

	public long getCarVersion() {
		return carVersion;
	}
	public void setCarVersion(long carVersion) {
		this.carVersion = carVersion;
	}

	public String getLovDescCarVersionName() {
		return this.lovDescCarVersionName;
	}
	public void setLovDescCarVersionName (String lovDescCarVersionName) {
		this.lovDescCarVersionName = lovDescCarVersionName;
	}
	
	public String getLovDescVehicleVersionCode() {
		return lovDescVehicleVersionCode;
	}
	public void setLovDescVehicleVersionCode(String lovDescVehicleVersionCode) {
		this.lovDescVehicleVersionCode = lovDescVehicleVersionCode;
	}

	public int getCarMakeYear() {
		return carMakeYear;
	}
	public void setCarMakeYear(int carMakeYear) {
		this.carMakeYear = carMakeYear;
	}
	
	public int getCarCapacity() {
		return carCapacity;
	}
	public void setCarCapacity(int carCapacity) {
		this.carCapacity = carCapacity;
	}
	
	public long getCarDealer() {
		return carDealer;
	}
	public void setCarDealer(long carDealer) {
		this.carDealer = carDealer;
	}

	public String getLovDescCarDealerName() {
		return this.lovDescCarDealerName;
	}
	public void setLovDescCarDealerName (String lovDescCarDealerName) {
		this.lovDescCarDealerName = lovDescCarDealerName;
	}
	
	public int getCarCc() {
		return carCc;
	}

	public void setCarCc(int carCc) {
		this.carCc = carCc;
	}

	public String getCarChasisNo() {
		return carChasisNo;
	}

	public void setCarChasisNo(String carChasisNo) {
		this.carChasisNo = carChasisNo;
	}

	

	public String getCarRegNo() {
		return carRegNo;
	}

	public void setCarRegNo(String carRegNo) {
		this.carRegNo = carRegNo;
	}

	public String getCarColor() {
		return carColor;
	}

	public void setCarColor(String carColor) {
		this.carColor = carColor;
	}

	public String getLovDescCarColorName() {
		return lovDescCarColorName;
	}

	public void setLovDescCarColorName(String lovDescCarColorName) {
		this.lovDescCarColorName = lovDescCarColorName;
	}

	public String getLovDescModelDesc() {
		return lovDescModelDesc;
	}
	public void setLovDescModelDesc(String lovDescModelDesc) {
		this.lovDescModelDesc = lovDescModelDesc;
	}

	public String getLovDescManufacturerName() {
		return lovDescManufacturerName;
	}
	public void setLovDescManufacturerName(String lovDescManufacturerName) {
		this.lovDescManufacturerName = lovDescManufacturerName;
	}

	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	
	public long getLastMntBy() {
		return lastMntBy;
	}
	public void setLastMntBy(long lastMntBy) {
		this.lastMntBy = lastMntBy;
	}

	public Timestamp getLastMntOn() {
		return lastMntOn;
	}
	public void setLastMntOn(Timestamp lastMntON) {
		this.lastMntOn = lastMntON;
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

	public CarLoanDetail getBefImage(){
		return this.befImage;
	}
	public void setBefImage(CarLoanDetail beforeImage){
		this.befImage=beforeImage;
	}

	public LoginUserDetails getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoginUserDetails userDetails) {
		this.userDetails = userDetails;
	}

	public String getRecordStatus() {
		return recordStatus;
	}
	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}
	
	public String getRoleCode() {
		return roleCode;
	}
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	
	public String getNextRoleCode() {
		return nextRoleCode;
	}
	public void setNextRoleCode(String nextRoleCode) {
		this.nextRoleCode = nextRoleCode;
	}
	
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getNextTaskId() {
		return nextTaskId;
	}
	public void setNextTaskId(String nextTaskId) {
		this.nextTaskId = nextTaskId;
	}
	
	public String getRecordType() {
		return recordType;
	}
	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public String getUserAction() {
		return userAction;
	}
	public void setUserAction(String userAction) {
		this.userAction = userAction;
	}

	public boolean isWorkflow() {
		if (this.workflowId==0){
			return false;
		}
		return true;
	}
	
	public long getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}

	// Overridden Equals method to handle the comparison
	public boolean equals(CarLoanDetail carLoanDetail) {
		return getLoanRefNumber() == carLoanDetail.getLoanRefNumber();
	}
	
	/**
	 * Check object is equal or not with Other object
	 * 
	 *  @return boolean
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof CarLoanDetail) {
			CarLoanDetail carLoanDetail = (CarLoanDetail) obj;
			return equals(carLoanDetail);
		}
		return false;
	}

	public String getEngineNumber() {
    	return engineNumber;
    }

	public void setEngineNumber(String engineNumber) {
    	this.engineNumber = engineNumber;
    }
	public String getPaymentMode() {
    	return paymentMode;
    }

	public void setPaymentMode(String paymentMode) {
    	this.paymentMode = paymentMode;
    }

	public String getPurchageOdrNumber() {
    	return purchageOdrNumber;
    }

	public void setPurchageOdrNumber(String purchageOdrNumber) {
    	this.purchageOdrNumber = purchageOdrNumber;
    }

	public String getQuoationNbr() {
    	return quoationNbr;
    }

	public void setQuoationNbr(String quoationNbr) {
    	this.quoationNbr = quoationNbr;
    }

	public Date getQuoationDate() {
    	return quoationDate;
    }

	public void setQuoationDate(Date quoationDate) {
    	this.quoationDate = quoationDate;
    }
	
	public String getDealerPhone() {
    	return dealerPhone;
    }

	public void setDealerPhone(String dealerPhone) {
    	this.dealerPhone = dealerPhone;
    }

	public Date getPurchaseDate() {
    	return purchaseDate;
    }

	public long getVehicleModelId() {
    	return vehicleModelId;
    }

	public void setVehicleModelId(long vehicleModelId) {
    	this.vehicleModelId = vehicleModelId;
    }

	public void setPurchaseDate(Date purchaseDate) {
    	this.purchaseDate = purchaseDate;
    }
	
	public long getManufacturerId() {
    	return manufacturerId;
    }

	public void setManufacturerId(long manufacturerId) {
    	this.manufacturerId = manufacturerId;
    }

	public void setLovDescCarDealerPhone(String lovDescCarDealerPhone) {
	    this.lovDescCarDealerPhone = lovDescCarDealerPhone;
    }

	public String getLovDescCarDealerPhone() {
	    return lovDescCarDealerPhone;
    }

	public void setLovDescCarDealerFax(String lovDescCarDealerFax) {
	    this.lovDescCarDealerFax = lovDescCarDealerFax;
    }

	public String getLovDescCarDealerFax() {
	    return lovDescCarDealerFax;
    }
	public boolean isThirdPartyReg() {
		return thirdPartyReg;
	}

	public void setThirdPartyReg(boolean thirdPartyReg) {
		this.thirdPartyReg = thirdPartyReg;
	}

	public String getThirdPartyName() {
		return thirdPartyName;
	}

	public void setThirdPartyName(String thirdPartyName) {
		this.thirdPartyName = thirdPartyName;
	}

	public String getPassportNum() {
		return passportNum;
	}

	public void setPassportNum(String passportNum) {
		this.passportNum = passportNum;
	}

	public String getThirdPartyNat() {
		return thirdPartyNat;
	}

	public void setThirdPartyNat(String thirdPartyNat) {
		this.thirdPartyNat = thirdPartyNat;
	}

	

	public String getDealerOrSellerAcc() {
		return dealerOrSellerAcc;
	}

	public void setDealerOrSellerAcc(String dealerOrSellerAcc) {
		this.dealerOrSellerAcc = dealerOrSellerAcc;
	}

	public String getSellerType() {
		return sellerType;
	}

	public void setSellerType(String sellerType) {
		this.sellerType = sellerType;
	}

	public BigDecimal getVehicleValue() {
		return vehicleValue;
	}

	public void setVehicleValue(BigDecimal vehicleValue) {
		this.vehicleValue = vehicleValue;
	}

	public String getEmiratesRegNum() {
	    return emiratesRegNum;
    }

	public void setEmiratesRegNum(String emiratesRegNum) {
	    this.emiratesRegNum = emiratesRegNum;
    }

	public List<CarLoanDetail> getVehicleLoanDetailList() {
		return vehicleLoanDetailList;
	}
	public void setVehicleLoanDetailList(List<CarLoanDetail> vehicleLoanDetailList) {
		this.vehicleLoanDetailList = vehicleLoanDetailList;
	}

	public String getLovDescThirdPartyNatName() {
	    return lovDescThirdPartyNatName;
    }

	public void setLovDescThirdPartyNatName(String lovDescThirdPartyNatName) {
	    this.lovDescThirdPartyNatName = lovDescThirdPartyNatName;
    }

	public String getPrivateDealerName() {
	    return privateDealerName;
    }

	public void setPrivateDealerName(String privateDealerName) {
	    this.privateDealerName = privateDealerName;
    }

	public String getSalesPersonName() {
	    return salesPersonName;
    }

	public void setSalesPersonName(String salesPersonName) {
	    this.salesPersonName = salesPersonName;
    }

	

	
}
