package com.pennant.backend.model.finance;

import java.io.Serializable;

public class TATNotificationLog implements Serializable {

	private static final long serialVersionUID = 8862601771682801641L;

	private String module;
	private String reference;
	private String roleCode;
	private int count;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public long getId() {
		return 0;
	}

	public void setId(long id) {

	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
