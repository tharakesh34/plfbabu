package com.pennant.backend.model.finance;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScheduleMapDetails implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private String finReference = null;
	private Date schdFromDate;
	private Date schdToDate;
	private Date schdRecalFromDate;
	private Date schdRecalToDate;
	private List<String> lovDescFinRefences;

	public ScheduleMapDetails() {
	    super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("schdRecalFromDate");
		excludeFields.add("schdRecalToDate");
		return excludeFields;
	}

	// Getter and Setter methods
	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public Date getSchdFromDate() {
		return schdFromDate;
	}

	public void setSchdFromDate(Date schdFromdate) {
		this.schdFromDate = schdFromdate;
	}

	public Date getSchdToDate() {
		return schdToDate;
	}

	public void setSchdToDate(Date schdTodate) {
		this.schdToDate = schdTodate;
	}

	public List<String> getLovDescFinRefences() {
		return lovDescFinRefences;
	}

	public void setLovDescFinRefences(List<String> lovDescFinRefences) {
		this.lovDescFinRefences = lovDescFinRefences;
	}

	public Date getSchdRecalToDate() {
		return schdRecalToDate;
	}

	public void setSchdRecalToDate(Date schdRecalToDate) {
		this.schdRecalToDate = schdRecalToDate;
	}

	public Date getSchdRecalFromDate() {
		return schdRecalFromDate;
	}

	public void setSchdRecalFromDate(Date schdRecalFromDate) {
		this.schdRecalFromDate = schdRecalFromDate;
	}

}
