package com.pennanttech.ws.model.finance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

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