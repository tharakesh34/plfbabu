package com.pennant.backend.model.finance;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class FinanceMainExtension extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -3026443763391506067L;
	private long id = Long.MIN_VALUE;
	private long finId = Long.MIN_VALUE;
	private String finreference;
	private String hostreference;
	private String oldhostreference;

	public long getId() {
		return id;
	}

	public long getFinId() {
		return finId;
	}

	public String getFinreference() {
		return finreference;
	}

	public String getHostreference() {
		return hostreference;
	}

	public String getOldhostreference() {
		return oldhostreference;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setFinId(long finId) {
		this.finId = finId;
	}

	public void setFinreference(String finreference) {
		this.finreference = finreference;
	}

	public void setHostreference(String hostreference) {
		this.hostreference = hostreference;
	}

	public void setOldhostreference(String oldhostreference) {
		this.oldhostreference = oldhostreference;
	}

}
