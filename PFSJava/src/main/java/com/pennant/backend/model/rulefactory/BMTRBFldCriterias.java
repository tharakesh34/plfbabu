package com.pennant.backend.model.rulefactory;

/**
 * Model class for the <b>BMTRBFldCriterias table</b>.<br>
 *
 */
public class BMTRBFldCriterias {

	private String rbFldType;
	private String rbSTFld;
	private String rbFldCriteriaNames;
	private String rbFldCriteriaValues;

	public BMTRBFldCriterias() {
	    super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getRbFldType() {
		return rbFldType;
	}

	public void setRbFldType(String rbFldType) {
		this.rbFldType = rbFldType;
	}

	public String getRbSTFld() {
		return rbSTFld;
	}

	public void setRbSTFld(String rbSTFld) {
		this.rbSTFld = rbSTFld;
	}

	public String getRbFldCriteriaNames() {
		return rbFldCriteriaNames;
	}

	public void setRbFldCriteriaNames(String rbFldCriteriaNames) {
		this.rbFldCriteriaNames = rbFldCriteriaNames;
	}

	public String getRbFldCriteriaValues() {
		return rbFldCriteriaValues;
	}

	public void setRbFldCriteriaValues(String rbFldCriteriaValues) {
		this.rbFldCriteriaValues = rbFldCriteriaValues;
	}

}
