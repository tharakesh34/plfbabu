package com.pennanttech.bajaj.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class MatchedDetails {
	@XmlElement(name = "matchedId")
	private String	matchedId;
	@XmlElement(name = "matchType")
	private String	matchType;

	public String getMatchType() {
		return matchType;
	}

	public void setMatchType(String matchType) {
		this.matchType = matchType;
	}

	public String getMatchedId() {
		return matchedId;
	}

	public void setMatchedId(String matchedId) {
		this.matchedId = matchedId;
	}

	@Override
	public String toString() {
		return "ClassPojo [matchType = " + matchType + ", matchedId = " + matchedId + "]";
	}
}