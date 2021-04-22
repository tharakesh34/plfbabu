package com.pennanttech.ws.model.customer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class CustDedupDetails implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "fields")
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
