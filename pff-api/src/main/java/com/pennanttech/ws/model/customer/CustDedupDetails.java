package com.pennanttech.ws.model.customer;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
@XmlAccessorType(XmlAccessType.FIELD)
public class CustDedupDetails {
	
	@XmlElement(name="fields")
	List<CustDedupRequest> dedupList=new ArrayList<CustDedupRequest>();

	public List<CustDedupRequest> getDedupList() {
		return dedupList;
	}

	public void setDedupList(List<CustDedupRequest> dedupList) {
		this.dedupList = dedupList;
	}

}
