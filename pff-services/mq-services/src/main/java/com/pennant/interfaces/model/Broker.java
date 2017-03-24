package com.pennant.interfaces.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Broker")
public class Broker {
	
	private String brokerID;
	private String brokerName;
	private String limitRef;
	
	@XmlElement(name="BrokerID")
	public String getBrokerID() {
		return brokerID;
	}
	public void setBrokerID(String brokerID) {
		this.brokerID = brokerID;
	}
	
	@XmlElement(name="BrokerName")
	public String getBrokerName() {
		return brokerName;
	}
	public void setBrokerName(String brokerName) {
		this.brokerName = brokerName;
	}
	public String getLimitRef() {
		return limitRef;
	}
	public void setLimitRef(String limitRef) {
		this.limitRef = limitRef;
	}
	

}
