package com.pennanttech.bajaj.model;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonPropertyOrder({"appscore", "appscoreDP"})
@JsonSerialize
public class Appscore {
	@XmlElement
	private int appscore;
	@XmlElement
	private int appscoreDP;
	
	public int getAppscore() {
		return appscore;
	}
	public void setAppscore(int appscore) {
		this.appscore = appscore;
	}
	public int getAppscoreDP() {
		return appscoreDP;
	}
	public void setAppscoreDP(int appscoreDP) {
		this.appscoreDP = appscoreDP;
	}
	@Override
	public String toString() {
		return "Appscore [appscore=" + appscore + ", appscoreDP=" + appscoreDP
				+ "]";
	}
	
	
}
