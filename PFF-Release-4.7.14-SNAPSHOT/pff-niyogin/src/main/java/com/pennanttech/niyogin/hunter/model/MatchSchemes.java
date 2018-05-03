package com.pennanttech.niyogin.hunter.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "schemeID", "score" })
@XmlRootElement(name = "MatchSchemes")
@XmlAccessorType(XmlAccessType.FIELD)
public class MatchSchemes implements Serializable {
	
	private static final long serialVersionUID = -2584179719768711687L;
	
	private int schemeID;
	private int score;

	public int getSchemeID() {
		return schemeID;
	}

	public void setSchemeID(int schemeID) {
		this.schemeID = schemeID;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	@Override
	public String toString() {
		return "MatchSchemes [schemeID=" + schemeID + ", score=" + score + "]";
	}

}
