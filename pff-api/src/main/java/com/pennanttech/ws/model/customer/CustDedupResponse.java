package com.pennanttech.ws.model.customer;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.blacklist.BlackListCustomers;
import com.pennant.backend.model.customermasters.CustomerDedup;

@XmlAccessorType(XmlAccessType.FIELD)
public class CustDedupResponse {

	private List<CustomerDedup> dedupList = null;
	private List<BlackListCustomers> blackList = null;
	private WSReturnStatus returnStatus;

	public List<CustomerDedup> getDedupList() {
		return dedupList;
	}

	public void setDedupList(List<CustomerDedup> dedupList) {
		this.dedupList = dedupList;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public List<BlackListCustomers> getBlackList() {
		return blackList;
	}

	public void setBlackList(List<BlackListCustomers> blackList) {
		this.blackList = blackList;
	}

}
