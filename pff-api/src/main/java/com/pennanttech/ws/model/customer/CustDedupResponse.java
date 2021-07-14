package com.pennanttech.ws.model.customer;

import java.io.Serializable;
import java.util.List;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.blacklist.BlackListCustomers;
import com.pennant.backend.model.customermasters.CustomerDedup;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class CustDedupResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<CustomerDedup> dedupList = null;
	private List<BlackListCustomers> blackList = null;
	private WSReturnStatus returnStatus;

	public CustDedupResponse() {
		super();
	}

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
