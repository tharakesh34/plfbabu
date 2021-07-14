package com.pennant.backend.model.dedup.external;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.NONE)
public class MatchedDetails {
	@JsonProperty("matchedId")
	private String matchedId;
	@JsonProperty("matchType")
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