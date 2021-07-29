package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.pennant.backend.model.WSReturnStatus;

@XmlAccessorType(XmlAccessType.FIELD)
public class UserPendingCasesResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<UserPendingCases> userPendingList = null;

	private WSReturnStatus returnStatus;

	public List<UserPendingCases> getUserPendingList() {
		return userPendingList;
	}

	public void setUserPendingList(List<UserPendingCases> userPendingList) {
		this.userPendingList = userPendingList;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}
}
