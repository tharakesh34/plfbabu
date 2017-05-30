package com.pennant.interfaces.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Drawee")
public class Drawee {
	
	private String draweeID;
	private String draweeName;
	private String limitRef;
	
	@XmlElement(name="DraweeID")
	public String getDraweeID() {
		return draweeID;
	}
	public void setDraweeID(String draweeID) {
		this.draweeID = draweeID;
	}
	
	@XmlElement(name="DraweeName")
	public String getDraweeName() {
		return draweeName;
	}
	public void setDraweeName(String draweeName) {
		this.draweeName = draweeName;
	}
	
	public String getLimitRef() {
		return limitRef;
	}
	public void setLimitRef(String limitRef) {
		this.limitRef = limitRef;
	}
	
	
}
