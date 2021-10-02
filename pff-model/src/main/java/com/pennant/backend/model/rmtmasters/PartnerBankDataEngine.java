package com.pennant.backend.model.rmtmasters;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class PartnerBankDataEngine extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -8491119721104361260L;

	private String type;
	private String partnerBankId;
	private String payMode;
	private String requestType;
	private String configName;

	public PartnerBankDataEngine() {
		super();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPartnerBankId() {
		return partnerBankId;
	}

	public void setPartnerBankId(String partnerBankId) {
		this.partnerBankId = partnerBankId;
	}

	public String getPayMode() {
		return payMode;
	}

	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}
}
