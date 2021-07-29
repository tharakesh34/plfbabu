package com.pennant.backend.model.systemmasters;

import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class DealerMapping extends AbstractWorkflowEntity {

	private static final long serialVersionUID = -1103892861574957994L;

	private long dealerMapId = Long.MIN_VALUE;
	private long merchantId;
	private String merchantName;
	private String storeName;
	private String storeAddress;
	private String storeCity;
	private String storeId;
	private long dealerCode;
	private String lovValue;
	private DealerMapping befImage;
	private LoggedInUser userDetails;
	private boolean active;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("merchantName");
		excludeFields.add("storeName");
		excludeFields.add("storeAddress");
		excludeFields.add("storeCity");
		return excludeFields;
	}

	public DealerMapping() {
		super();
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public DealerMapping getBefImage() {
		return befImage;
	}

	public void setBefImage(DealerMapping befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(long merchantId) {
		this.merchantId = merchantId;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getStoreAddress() {
		return storeAddress;
	}

	public void setStoreAddress(String storeAddress) {
		this.storeAddress = storeAddress;
	}

	public String getStoreCity() {
		return storeCity;
	}

	public void setStoreCity(String storeCity) {
		this.storeCity = storeCity;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public long getDealerCode() {
		return dealerCode;
	}

	public void setDealerCode(long dealerCode) {
		this.dealerCode = dealerCode;
	}

	public long getDealerMapId() {
		return dealerMapId;
	}

	public void setDealerMapId(long dealerMapId) {
		this.dealerMapId = dealerMapId;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public long getId() {
		return dealerMapId;
	}

	public void setId(long id) {
		this.dealerMapId = id;
	}

}
