package com.pennant.backend.model.perfios;

import java.io.Serializable;

public class PerfiosInstitutions implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int instituteId;
	private String instituteType;
	private String instituteName;

	public int getInstituteId() {
		return instituteId;
	}

	public void setInstituteId(int instituteId) {
		this.instituteId = instituteId;
	}

	public String getInstituteType() {
		return instituteType;
	}

	public void setInstituteType(String instituteType) {
		this.instituteType = instituteType;
	}

	public String getInstituteName() {
		return instituteName;
	}

	public void setInstituteName(String instituteName) {
		this.instituteName = instituteName;
	}

}
