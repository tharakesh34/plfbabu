package com.pennanttech.ws.model.finance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class FinanceDedupDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("fields")
	List<FinanceDedupRequest> dedupList = new ArrayList<FinanceDedupRequest>();

	public FinanceDedupDetails() {
		super();
	}

	public List<FinanceDedupRequest> getDedupList() {
		return dedupList;
	}

	public void setDedupList(List<FinanceDedupRequest> dedupList) {
		this.dedupList = dedupList;
	}

}