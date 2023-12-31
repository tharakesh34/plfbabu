package com.pennant.backend.model.dedup.external;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonPropertyOrder({ "appscore", "appscoreDP" })
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
		return "Appscore [appscore=" + appscore + ", appscoreDP=" + appscoreDP + "]";
	}

}
