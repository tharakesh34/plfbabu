package com.pennant.backend.model.hunter;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class HunterUpload extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;
	private String leadId;
	private String category;
	private String reasons;

	public HunterUpload() {
		super();
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getReasons() {
		return reasons;
	}

	public void setReasons(String reasons) {
		this.reasons = reasons;
	}

	public String getLeadId() {
		return leadId;
	}

	public void setLeadId(String leadId) {
		this.leadId = leadId;
	}

}
