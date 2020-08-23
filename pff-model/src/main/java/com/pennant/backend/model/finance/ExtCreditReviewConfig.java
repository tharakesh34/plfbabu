package com.pennant.backend.model.finance;

import java.io.Serializable;

public class ExtCreditReviewConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String creditReviewType;
	private String templateName;
	private int templateVersion;
	private String salFields;
	private String sepFields;
	private String consolidatedBankingFields;
	private String consolidatedObligationsFields;
	private String finalEligIncomeDetailsFields;
	private String finalEligIncomeFoirFields;
	private String finalEligIncomeUserEntryFields;
	private String finalEligLowLtvUserEntryFields;
	private String finalEligLRDUserEntryFields;
	private String finalEligibilityFields;
	private String finalEligSuperLowerFields;
	private String FinalEligSuperLowerEntryFields;
	private String finalEligSuperHigherFields;
	private String FinalEligSuperHigherEntryField;
	private String finalEligAdvantageFields;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCreditReviewType() {
		return creditReviewType;
	}

	public void setCreditReviewType(String creditReviewType) {
		this.creditReviewType = creditReviewType;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public int getTemplateVersion() {
		return templateVersion;
	}

	public void setTemplateVersion(int templateVersion) {
		this.templateVersion = templateVersion;
	}

	public String getSalFields() {
		return salFields;
	}

	public void setSalFields(String salFields) {
		this.salFields = salFields;
	}

	public String getSepFields() {
		return sepFields;
	}

	public void setSepFields(String sepFields) {
		this.sepFields = sepFields;
	}

	public String getConsolidatedBankingFields() {
		return consolidatedBankingFields;
	}

	public void setConsolidatedBankingFields(String consolidatedBankingFields) {
		this.consolidatedBankingFields = consolidatedBankingFields;
	}

	public String getConsolidatedObligationsFields() {
		return consolidatedObligationsFields;
	}

	public void setConsolidatedObligationsFields(String consolidatedObligationsFields) {
		this.consolidatedObligationsFields = consolidatedObligationsFields;
	}

	public String getFinalEligIncomeDetailsFields() {
		return finalEligIncomeDetailsFields;
	}

	public void setFinalEligIncomeDetailsFields(String finalEligIncomeDetailsFields) {
		this.finalEligIncomeDetailsFields = finalEligIncomeDetailsFields;
	}

	public String getFinalEligIncomeFoirFields() {
		return finalEligIncomeFoirFields;
	}

	public void setFinalEligIncomeFoirFields(String finalEligIncomeFoirFields) {
		this.finalEligIncomeFoirFields = finalEligIncomeFoirFields;
	}

	public String getFinalEligIncomeUserEntryFields() {
		return finalEligIncomeUserEntryFields;
	}

	public void setFinalEligIncomeUserEntryFields(String finalEligIncomeUserEntryFields) {
		this.finalEligIncomeUserEntryFields = finalEligIncomeUserEntryFields;
	}

	public String getFinalEligLowLtvUserEntryFields() {
		return finalEligLowLtvUserEntryFields;
	}

	public void setFinalEligLowLtvUserEntryFields(String finalEligLowLtvUserEntryFields) {
		this.finalEligLowLtvUserEntryFields = finalEligLowLtvUserEntryFields;
	}

	public String getFinalEligLRDUserEntryFields() {
		return finalEligLRDUserEntryFields;
	}

	public void setFinalEligLRDUserEntryFields(String finalEligLRDUserEntryFields) {
		this.finalEligLRDUserEntryFields = finalEligLRDUserEntryFields;
	}

	public String getFinalEligibilityFields() {
		return finalEligibilityFields;
	}

	public void setFinalEligibilityFields(String finalEligibilityFields) {
		this.finalEligibilityFields = finalEligibilityFields;
	}

	public String getFinalEligSuperLowerFields() {
		return finalEligSuperLowerFields;
	}

	public void setFinalEligSuperLowerFields(String finalEligSuperLowerFields) {
		this.finalEligSuperLowerFields = finalEligSuperLowerFields;
	}

	public String getFinalEligSuperLowerEntryFields() {
		return FinalEligSuperLowerEntryFields;
	}

	public void setFinalEligSuperLowerEntryFields(String finalEligSuperLowerEntryFields) {
		FinalEligSuperLowerEntryFields = finalEligSuperLowerEntryFields;
	}

	public String getFinalEligSuperHigherFields() {
		return finalEligSuperHigherFields;
	}

	public void setFinalEligSuperHigherFields(String finalEligSuperHigherFields) {
		this.finalEligSuperHigherFields = finalEligSuperHigherFields;
	}

	public String getFinalEligSuperHigherEntryField() {
		return FinalEligSuperHigherEntryField;
	}

	public void setFinalEligSuperHigherEntryField(String finalEligSuperHigherEntryField) {
		FinalEligSuperHigherEntryField = finalEligSuperHigherEntryField;
	}

	public String getFinalEligAdvantageFields() {
		return finalEligAdvantageFields;
	}

	public void setFinalEligAdvantageFields(String finalEligAdvantageFields) {
		this.finalEligAdvantageFields = finalEligAdvantageFields;
	}

}
