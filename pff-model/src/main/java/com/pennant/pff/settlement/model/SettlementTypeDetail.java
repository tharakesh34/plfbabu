package com.pennant.pff.settlement.model;

import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class SettlementTypeDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id;
	private String settlementCode;
	private String settlementDesc;
	private boolean alwGracePeriod;
	private boolean active;
	private String lovValue;
	private SettlementTypeDetail befImage;
	private LoggedInUser userDetails;

	public SettlementTypeDetail() {
		super();
	}

	public Set<String> getExcludeFields() {
		return new HashSet<>();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSettlementCode() {
		return settlementCode;
	}

	public void setSettlementCode(String settlementCode) {
		this.settlementCode = settlementCode;
	}

	public String getSettlementDesc() {
		return settlementDesc;
	}

	public void setSettlementDesc(String settlementDesc) {
		this.settlementDesc = settlementDesc;
	}

	public boolean isAlwGracePeriod() {
		return alwGracePeriod;
	}

	public void setAlwGracePeriod(boolean alwGracePeriod) {
		this.alwGracePeriod = alwGracePeriod;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public SettlementTypeDetail getBefImage() {
		return befImage;
	}

	public void setBefImage(SettlementTypeDetail befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

}
