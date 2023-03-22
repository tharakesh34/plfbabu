package com.pennanttech.pff.cd.model;

import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class TransactionMapping extends AbstractWorkflowEntity implements Entity {

	private static final long serialVersionUID = 221505141217792488L;

	private long id = Long.MIN_VALUE;
	private int posId;
	private long dealerCode = Long.MIN_VALUE;
	private String dealerName;
	private long mid = Long.MIN_VALUE;
	private String tid;
	private String storeName;
	private int storeId;
	private boolean newRecord;
	private String lovValue;
	private TransactionMapping befImage;
	private LoggedInUser userDetails;
	private boolean active;
	private String mobileNumber1;
	private String mobileNumber2;
	private String mobileNumber3;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("storeName");
		excludeFields.add("storeId");
		return excludeFields;
	}

	public int getPosId() {
		return posId;
	}

	public void setPosId(int posId) {
		this.posId = posId;
	}

	public long getDealerCode() {
		return dealerCode;
	}

	public void setDealerCode(long dealerCode) {
		this.dealerCode = dealerCode;
	}

	public String getDealerName() {
		return dealerName;
	}

	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}

	public long getMid() {
		return mid;
	}

	public void setMid(long mid) {
		this.mid = mid;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
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

	public int getStoreId() {
		return storeId;
	}

	public void setStoreId(int storeId) {
		this.storeId = storeId;
	}

	public TransactionMapping getBefImage() {
		return befImage;
	}

	public void setBefImage(TransactionMapping befImage) {
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

	@Override
	public boolean isNew() {
		return newRecord;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getMobileNumber1() {
		return mobileNumber1;
	}

	public String getMobileNumber2() {
		return mobileNumber2;
	}

	public String getMobileNumber3() {
		return mobileNumber3;
	}

	public void setMobileNumber1(String mobileNumber1) {
		this.mobileNumber1 = mobileNumber1;
	}

	public void setMobileNumber2(String mobileNumber2) {
		this.mobileNumber2 = mobileNumber2;
	}

	public void setMobileNumber3(String mobileNumber3) {
		this.mobileNumber3 = mobileNumber3;
	}

}
