package com.pennanttech.ws.model.finance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class FinanceDedupDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlElement(name = "fields")
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