package com.pennant.backend.model.finance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

@XmlAccessorType(XmlAccessType.NONE)
public class FinReqParams extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 1L;
	@XmlElement(name = "reqParam")
	private String reqParam;
	@XmlElement(name = "reqType")
	private String reqType;
	@XmlElement(name = "loggedInUser")
	private String loggedInUser;
	@XmlElement(name = "stageCodes")
	private String stageCodes;

	public FinReqParams() {
		super();
	}

	public String getReqType() {
		return reqType;
	}

	public void setReqType(String reqType) {
		this.reqType = reqType;
	}

	public String getReqParam() {
		return reqParam;
	}

	public void setReqParam(String reqParam) {
		this.reqParam = reqParam;
	}

	public String getLoggedInUser() {
		return loggedInUser;
	}

	public void setLoggedInUser(String loggedInUser) {
		this.loggedInUser = loggedInUser;
	}

	public String getStageCodes() {
		return stageCodes;
	}

	public void setStageCodes(String stageCodes) {
		this.stageCodes = stageCodes;
	}
}
