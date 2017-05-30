package com.pennant.interfaces.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Brokers")
public class Brokers {
	
	private List<Broker> broker;

	@XmlElement(name="Broker")
	public List<Broker> getBroker() {
		return broker;
	}

	public void setBroker(List<Broker> broker) {
		this.broker = broker;
	}	
}
