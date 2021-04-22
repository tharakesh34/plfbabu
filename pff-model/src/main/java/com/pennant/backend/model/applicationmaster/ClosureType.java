package com.pennant.backend.model.applicationmaster;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class ClosureType extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -2065817593651934568L;

	private Long id;
	private String code;
	private String description;
	private boolean active;
	private String lovValue;

	public ClosureType() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

}
