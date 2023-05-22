package com.pennanttech.pff.cd.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class MerchantDetails extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long merchantId = Long.MIN_VALUE;
	private String merchantName;
	private Long storeId;
	private String storeName;
	private String storeAddressLine1;
	private String storeAddressLine2;
	private String storeAddressLine3;
	private String storeCity;
	private String storeState;
	private int POSId = 0;
	private BigDecimal avgTranPerMnth;
	private BigDecimal avgTranAmtPerMnth;
	private BigDecimal tranAmtPerTran;
	private BigDecimal tranAmtPerDay;
	private boolean allowRefund;
	private String channel;
	private boolean active;
	private MerchantDetails befImage;
	private LoggedInUser userDetails;
	private String lovValue;
	private boolean newRecord = false;
	private String cityName;
	private String stateName;
	private String storeCountry;
	private String countryName;
	private int peakTransPerDay;
	private String pincode;
	private String merchPAN;
	private String gstInNumber;
	private String merchMobileNo;
	private String merchEmailId;

	public long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(long merchantId) {
		this.merchantId = merchantId;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public Long getStoreId() {
		return storeId;
	}

	public void setStoreId(Long storeId) {
		this.storeId = storeId;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getStoreCity() {
		return storeCity;
	}

	public void setStoreCity(String storeCity) {
		this.storeCity = storeCity;
	}

	public String getStoreState() {
		return storeState;
	}

	public void setStoreState(String storeState) {
		this.storeState = storeState;
	}

	public int getPOSId() {
		return POSId;
	}

	public void setPOSId(int pOSId) {
		POSId = pOSId;
	}

	public BigDecimal getAvgTranPerMnth() {
		return avgTranPerMnth;
	}

	public void setAvgTranPerMnth(BigDecimal avgTranPerMnth) {
		this.avgTranPerMnth = avgTranPerMnth;
	}

	public BigDecimal getAvgTranAmtPerMnth() {
		return avgTranAmtPerMnth;
	}

	public void setAvgTranAmtPerMnth(BigDecimal avgTranAmtPerMnth) {
		this.avgTranAmtPerMnth = avgTranAmtPerMnth;
	}

	public BigDecimal getTranAmtPerTran() {
		return tranAmtPerTran;
	}

	public void setTranAmtPerTran(BigDecimal tranAmtPerTran) {
		this.tranAmtPerTran = tranAmtPerTran;
	}

	public BigDecimal getTranAmtPerDay() {
		return tranAmtPerDay;
	}

	public void setTranAmtPerDay(BigDecimal tranAmtPerDay) {
		this.tranAmtPerDay = tranAmtPerDay;
	}

	public boolean isAllowRefund() {
		return allowRefund;
	}

	public void setAllowRefund(boolean allowRefund) {
		this.allowRefund = allowRefund;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public MerchantDetails getBefImage() {
		return befImage;
	}

	public void setBefImage(MerchantDetails befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public String getStoreAddressLine1() {
		return storeAddressLine1;
	}

	public void setStoreAddressLine1(String storeAddressLine1) {
		this.storeAddressLine1 = storeAddressLine1;
	}

	public String getStoreAddressLine2() {
		return storeAddressLine2;
	}

	public void setStoreAddressLine2(String storeAddressLine2) {
		this.storeAddressLine2 = storeAddressLine2;
	}

	public String getStoreAddressLine3() {
		return storeAddressLine3;
	}

	public void setStoreAddressLine3(String storeAddressLine3) {
		this.storeAddressLine3 = storeAddressLine3;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getStoreCountry() {
		return storeCountry;
	}

	public void setStoreCountry(String storeCountry) {
		this.storeCountry = storeCountry;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public int getPeakTransPerDay() {
		return peakTransPerDay;
	}

	public void setPeakTransPerDay(int peakTransPerDay) {
		this.peakTransPerDay = peakTransPerDay;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getMerchPAN() {
		return merchPAN;
	}

	public void setMerchPAN(String merchPAN) {
		this.merchPAN = merchPAN;
	}

	public String getGstInNumber() {
		return gstInNumber;
	}

	public void setGstInNumber(String gstInNumber) {
		this.gstInNumber = gstInNumber;
	}

	public String getMerchMobileNo() {
		return merchMobileNo;
	}

	public void setMerchMobileNo(String merchMobileNo) {
		this.merchMobileNo = merchMobileNo;
	}

	public String getMerchEmailId() {
		return merchEmailId;
	}

	public void setMerchEmailId(String merchEmailId) {
		this.merchEmailId = merchEmailId;
	}

}
