package com.pennanttech.ws.model.finance;

import java.io.Serializable;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

import com.pennant.backend.model.Notes;

@XmlType(propOrder = { "finReference", "currentStage", "action", "remarks" })
@XmlAccessorType(XmlAccessType.FIELD)
public class MoveLoanStageRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private String finReference;
	private String currentStage;
	private String action;
	private List<Notes> remarks;

	public MoveLoanStageRequest() {
		super();
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getCurrentStage() {
		return currentStage;
	}

	public void setCurrentStage(String currentStage) {
		this.currentStage = currentStage;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public List<Notes> getRemarks() {
		return remarks;
	}

	public void setRemarks(List<Notes> remarks) {
		this.remarks = remarks;
	}

}
