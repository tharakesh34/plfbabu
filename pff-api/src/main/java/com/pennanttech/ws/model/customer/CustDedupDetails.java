package com.pennanttech.ws.model.customer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class CustDedupDetails implements Serializable {
	private static final long serialVersionUID = 1L;

	@JsonProperty("fields")
	List<CustDedupRequest> dedupList = new ArrayList<CustDedupRequest>();

	public CustDedupDetails() {
		super();
	}

	public List<CustDedupRequest> getDedupList() {
		return dedupList;
	}

	public void setDedupList(List<CustDedupRequest> dedupList) {
		this.dedupList = dedupList;
	}

}
