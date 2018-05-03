package com.pennant.webservice.model;

public class ReferenceData {

	String mdmCode; 		// MDM Code
	String t24Code; 		// T24 Code
	String desc; 			// Description

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public ReferenceData() {

	}

	public String getMdmCode() {
		return mdmCode;
	}

	public void setMdmCode(String mdmCode) {
		this.mdmCode = mdmCode;
	}

	public String getT24Code() {
		return t24Code;
	}

	public void setT24Code(String t24Code) {
		this.t24Code = t24Code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
