package com.pennant.backend.model.reason.details;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import jakarta.xml.bind.annotation.XmlElement;

public class ReasonDetails implements Serializable {
	private static final long serialVersionUID = 1L;

	private long headerId;
	private long reasonId;
	@XmlElement
	private String reasonCode;

	public ReasonDetails copyEntity() {
		ReasonDetails entity = new ReasonDetails();
		entity.setHeaderId(this.headerId);
		entity.setReasonId(this.reasonId);
		entity.setReasonCode(this.reasonCode);
		return entity;
	}

	public boolean isNew() {
		return false;
	}

	public long getHeaderId() {
		return headerId;
	}

	public void setHeaderId(long headerId) {
		this.headerId = headerId;
	}

	public long getReasonId() {
		return reasonId;
	}

	public void setReasonId(long reasonId) {
		this.reasonId = reasonId;
	}

	public long getId() {
		return 0;
	}

	public void setId(long id) {

	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("reasonCode");
		return excludeFields;
	}

	public String getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}

}
