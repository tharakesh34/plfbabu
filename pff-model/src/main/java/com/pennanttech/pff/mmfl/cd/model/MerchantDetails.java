package com.pennanttech.pff.mmfl.cd.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class MerchantDetails extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long merchantId = Long.MIN_VALUE;
	private String merchantName;
	private long storeId;
	private String storeName;
	private String storeAddress1;
	private String storeAddress2;
	private String storeAddress3;
	private String storeCity;
	private String storeState;
	private String POSId;
	private BigDecimal avgTranPerMnth;
	private BigDecimal avgTranAmtPerMnth;
	private BigDecimal tranAmtPerTran;
	private BigDecimal tranAmtPerDay;
	private boolean allowRefund;
	private BigDecimal peakTransPerDay;
	private String channel;
	private boolean active;
	private MerchantDetails befImage;
	private LoggedInUser userDetails;
	private String lovValue;
	private boolean newRecord = false;

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

	public long getStoreId() {
		return storeId;
	}

	public void setStoreId(long storeId) {
		this.storeId = storeId;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getStoreAddress1() {
		return storeAddress1;
	}

	public void setStoreAddress1(String storeAddress1) {
		this.storeAddress1 = storeAddress1;
	}

	public String getStoreAddress2() {
		return storeAddress2;
	}

	public void setStoreAddress2(String storeAddress2) {
		this.storeAddress2 = storeAddress2;
	}

	public String getStoreAddress3() {
		return storeAddress3;
	}

	public void setStoreAddress3(String storeAddress3) {
		this.storeAddress3 = storeAddress3;
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

	public String getPOSId() {
		return POSId;
	}

	public void setPOSId(String pOSId) {
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

	public BigDecimal getPeakTransPerDay() {
		return peakTransPerDay;
	}

	public void setPeakTransPerDay(BigDecimal peakTransPerDay) {
		this.peakTransPerDay = peakTransPerDay;
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

}
