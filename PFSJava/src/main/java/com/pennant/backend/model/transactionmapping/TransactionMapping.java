package com.pennant.backend.model.transactionmapping;

import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class TransactionMapping extends AbstractWorkflowEntity implements Entity {

	private static final long serialVersionUID = 221505141217792488L;

	private long id = Long.MIN_VALUE;
	private String posId;
	private long dealerCode = Long.MIN_VALUE;
	private String dealerName;
	private long mid = Long.MIN_VALUE;
	private long tid = Long.MIN_VALUE;
	private String storeName;
	private String storeId;
	private boolean newRecord;
	private String lovValue;
	private TransactionMapping befImage;
	private LoggedInUser userDetails;
	private boolean active;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("storeName");
		excludeFields.add("storeId");
		return excludeFields;
	}

	public String getPosId() {
		return posId;
	}

	public void setPosId(String posId) {
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

	public long getTid() {
		return tid;
	}

	public void setTid(long tid) {
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

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
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

}
