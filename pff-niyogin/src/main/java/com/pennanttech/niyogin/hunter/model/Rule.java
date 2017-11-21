package com.pennanttech.niyogin.hunter.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "ruleID", "ruleCount", "score" })
@XmlAccessorType(XmlAccessType.NONE)
public class Rule implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	@XmlElement(name = "RuleID")
	private String				ruleID;
	private int					ruleCount;
	@XmlElement(name = "Score")
	private int					score;

	public String getRuleID() {
		return ruleID;
	}

	public void setRuleID(String ruleID) {
		this.ruleID = ruleID;
	}

	public int getRuleCount() {
		return ruleCount;
	}

	public void setRuleCount(int ruleCount) {
		this.ruleCount = ruleCount;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

}