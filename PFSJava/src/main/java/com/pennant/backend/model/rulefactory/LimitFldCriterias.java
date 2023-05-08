package com.pennant.backend.model.rulefactory;

/**
 * Model class for the <b>LimitFldCriterias table</b>.<br>
 *
 */
public class LimitFldCriterias {

	private String qbFldType;
	private String qbSTFld;
	private String qbFldCriteriaNames;
	private String qbFldCriteriaValues;

	public LimitFldCriterias() {
	    super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getQbFldType() {
		return qbFldType;
	}

	public void setQbFldType(String qbFldType) {
		this.qbFldType = qbFldType;
	}

	public String getQbSTFld() {
		return qbSTFld;
	}

	public void setQbSTFld(String qbSTFld) {
		this.qbSTFld = qbSTFld;
	}

	public String getQbFldCriteriaNames() {
		return qbFldCriteriaNames;
	}

	public void setQbFldCriteriaNames(String qbFldCriteriaNames) {
		this.qbFldCriteriaNames = qbFldCriteriaNames;
	}

	public String getQbFldCriteriaValues() {
		return qbFldCriteriaValues;
	}

	public void setQbFldCriteriaValues(String qbFldCriteriaValues) {
		this.qbFldCriteriaValues = qbFldCriteriaValues;
	}

}
