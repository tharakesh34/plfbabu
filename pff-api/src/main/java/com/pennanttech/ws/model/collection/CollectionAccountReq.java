package com.pennanttech.ws.model.collection;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "collectionAccountDetailList" })
@XmlAccessorType(XmlAccessType.NONE)
public class CollectionAccountReq {

	@XmlElement
	private List<CollectionAccountDetails> collectionAccountDetailList = new ArrayList<>();

	public List<CollectionAccountDetails> getCollectionAccountDetailList() {
		return collectionAccountDetailList;
	}

	public void setCollectionAccountDetailList(List<CollectionAccountDetails> collectionAccountDetailList) {
		this.collectionAccountDetailList = collectionAccountDetailList;
	}
}
