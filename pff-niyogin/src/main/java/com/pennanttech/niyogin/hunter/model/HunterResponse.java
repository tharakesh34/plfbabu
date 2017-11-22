package com.pennanttech.niyogin.hunter.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
@XmlType(propOrder = { "statusCode", "message", "matchSummary", "totalMatchScore", "rules", "matchSchemes" })
@XmlAccessorType(XmlAccessType.FIELD)
public class HunterResponse implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	private String				message;
	private String				statusCode;
	@XmlElement(name = "MatchSummary")
	private int					matchSummary;
	@XmlElement(name = "TotalMatchScore")
	private int					totalMatchScore;
	@XmlElementWrapper(name = "Rules")
	@XmlElement(name = "rule")
	private List<Rule>			rules;
	@XmlElementWrapper(name = "MatchSchemes")
	@XmlElement(name = "matchScheme")
	private List<MatchSchemes>	matchSchemes;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public int getMatchSummary() {
		return matchSummary;
	}

	public void setMatchSummary(int matchSummary) {
		this.matchSummary = matchSummary;
	}

	public int getTotalMatchScore() {
		return totalMatchScore;
	}

	public void setTotalMatchScore(int totalMatchScore) {
		this.totalMatchScore = totalMatchScore;
	}

	public List<MatchSchemes> getMatchSchemes() {
		return matchSchemes;
	}

	public void setMatchSchemes(List<MatchSchemes> matchSchemes) {
		this.matchSchemes = matchSchemes;
	}

	public List<Rule> getRules() {
		return rules;
	}

	public void setRules(List<Rule> rules) {
		this.rules = rules;
	}

}
