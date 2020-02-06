package com.pennanttech.ws.model.deviation;

import java.io.Serializable;
import java.util.List;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WSReturnStatus;

public class ManualDeviationAuthorities implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<ValueLabel> authoritiesList;
	private WSReturnStatus returnStatus;

	public ManualDeviationAuthorities() {
		super();
	}

	public List<ValueLabel> getAuthoritiesList() {
		return authoritiesList;
	}

	public void setAuthoritiesList(List<ValueLabel> authoritiesList) {
		this.authoritiesList = authoritiesList;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

}
