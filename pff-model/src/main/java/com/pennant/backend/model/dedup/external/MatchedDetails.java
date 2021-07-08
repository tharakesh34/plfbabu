package com.pennant.backend.model.dedup.external;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class MatchedDetails {
	@XmlElement(name = "matchedId")
	private String matchedId;
	@XmlElement(name = "matchType")
	private String matchType;

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