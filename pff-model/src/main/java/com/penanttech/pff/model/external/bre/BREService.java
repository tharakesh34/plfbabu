package com.penanttech.pff.model.external.bre;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BREService implements Serializable {

	private static final long serialVersionUID = 1L;

	DAXMLDocument daXMLDocument;

	@JsonCreator
	public BREService() {
	}

	@JsonProperty("DAXMLDocument")
	public DAXMLDocument getDaXMLDocument() {
		return daXMLDocument;
	}

	@JsonProperty("DAXMLDocument")
	public void setDaXMLDocument(DAXMLDocument daXMLDocument) {
		this.daXMLDocument = daXMLDocument;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class DAXMLDocument implements Serializable {

		private static final long serialVersionUID = 1L;
		@JsonProperty("OCONTROL")
		OControl oControl;
		@JsonProperty("PCHFLIN")
		PchflIn pchflIn;
		@JsonProperty("PCHFLOUT")
		Pchflout PchflOut;

		@JsonCreator
		public DAXMLDocument() {
		}

		public Pchflout getPchflOut() {
			return PchflOut;
		}

		public void setPchflOut(Pchflout pchflOut) {
			PchflOut = pchflOut;
		}

		public OControl getoControl() {
			return oControl;
		}

		public void setoControl(OControl oControl) {
			this.oControl = oControl;
		}

		public PchflIn getPchflIn() {
			return pchflIn;
		}

		public void setPchflIn(PchflIn pchflIn) {
			this.pchflIn = pchflIn;
		}

		@Override
		public String toString() {
			return "DAXMLDocument [oControl=" + oControl + ", pchflIn=" + pchflIn + "]";
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class OControl implements Serializable {

		private static final long serialVersionUID = 1L;
		@JsonProperty("ALIAS")
		private String alias;
		@JsonProperty("SIGNATURE")
		private String signature;
		@JsonProperty("DALOGLEVEL")
		private String daloglevel;
		@JsonProperty("EDITION")
		private String edition;
		@JsonProperty("OBJECTIVE")
		private String objective;
		@JsonProperty("EDITIONDATE")
		private String editiondate;
		@JsonProperty("ERRORCODE")
		private String errorcode;
		@JsonProperty("ERRORMSG")
		private String errormsg;
		@JsonProperty("APPLICATION_ID")
		private String application_id;

		@JsonCreator
		public OControl() {
		}

		// Getter Methods

		@JsonProperty("ALIAS")
		public String getAlias() {
			return alias;
		}

		public String getSignature() {
			return signature;
		}

		public String getDaloglevel() {
			return daloglevel;
		}

		public String getEdition() {
			return edition;
		}

		public String getObjective() {
			return objective;
		}

		public String getEditiondate() {
			return editiondate;
		}

		public String getErrorcode() {
			return errorcode;
		}

		public String getErrormsg() {
			return errormsg;
		}

		public String getApplication_id() {
			return application_id;
		}

		// Setter Methods
		@JsonProperty("ALIAS")
		public void setAlias(String alias) {
			this.alias = alias;
		}

		public void setSignature(String signature) {
			this.signature = signature;
		}

		public void setDaloglevel(String daloglevel) {
			this.daloglevel = daloglevel;
		}

		public void setEdition(String edition) {
			this.edition = edition;
		}

		public void setObjective(String objective) {
			this.objective = objective;
		}

		public void setEditiondate(String editiondate) {
			this.editiondate = editiondate;
		}

		public void setErrorcode(String errorcode) {
			this.errorcode = errorcode;
		}

		public void setErrormsg(String errormsg) {
			this.errormsg = errormsg;
		}

		public void setApplication_id(String application_id) {
			this.application_id = application_id;
		}

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class PchflIn implements Serializable {

		private static final long serialVersionUID = 1L;
		@JsonProperty("APPLICANTIN")
		ApplicantIn applicantIn;
		@JsonProperty("APPLICATIONIN")
		ApplicationIn applicationIn;

		@JsonCreator
		public PchflIn() {

		}

		public ApplicantIn getApplicantIn() {
			return applicantIn;
		}

		public void setApplicantIn(ApplicantIn applicantIn) {
			this.applicantIn = applicantIn;
		}

		public ApplicationIn getApplicationIn() {
			return applicationIn;
		}

		public void setApplicationIn(ApplicationIn applicationIn) {
			this.applicationIn = applicationIn;
		}

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Pchflout implements Serializable {

		private static final long serialVersionUID = 1L;
		@JsonProperty("APPLICANTOUT")
		private ApplicantOut applicantOut;

		@JsonProperty("APPLICATIONOUT")
		private ApplicationOut applicationOut;

		@JsonCreator
		public Pchflout() {
		}

		public ApplicantOut getApplicantOut() {
			return applicantOut;
		}

		public void setApplicantOut(ApplicantOut applicantOut) {
			this.applicantOut = applicantOut;
		}

		public ApplicationOut getApplicationOut() {
			return applicationOut;
		}

		public void setApplicationOut(ApplicationOut applicationOut) {
			this.applicationOut = applicationOut;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ApplicationOut implements Serializable {

		private static final long serialVersionUID = 1L;

		@JsonProperty("CALCULATIONS_LTV")
		CalculationsLtv calculationsLtv;

		@JsonProperty("DECISION_REASON_CODE")
		DecisionReasonCode decisionReasonCode;

		@JsonProperty("DELEGATION_LEVEL")
		private String delegationLevel;

		@JsonProperty("FINAL_ELIGIBILITY")
		private FinalEligibility FinalEligibility;

		@JsonProperty("LOAN_ELIG_LTV")
		private LoanEligLtv loanEligLtv;

		@JsonCreator
		public ApplicationOut() {
		}

		public CalculationsLtv getCalculationsLtv() {
			return calculationsLtv;
		}

		public void setCalculationsLtv(CalculationsLtv calculationsLtv) {
			this.calculationsLtv = calculationsLtv;
		}

		public DecisionReasonCode getDecisionReasonCode() {
			return decisionReasonCode;
		}

		public void setDecisionReasonCode(DecisionReasonCode decisionReasonCode) {
			this.decisionReasonCode = decisionReasonCode;
		}

		public String getDelegationLevel() {
			return delegationLevel;
		}

		public void setDelegationLevel(String delegationLevel) {
			this.delegationLevel = delegationLevel;
		}

		public FinalEligibility getFinalEligibility() {
			return FinalEligibility;
		}

		public void setFinalEligibility(FinalEligibility finalEligibility) {
			FinalEligibility = finalEligibility;
		}

		public LoanEligLtv getLoanEligLtv() {
			return loanEligLtv;
		}

		public void setLoanEligLtv(LoanEligLtv loanEligLtv) {
			this.loanEligLtv = loanEligLtv;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Advantage implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		@JsonProperty("COMBINED_LOAN_SANCTIONED")
		private String combinedLoanSanctioned;
		@JsonProperty("ELIGIBLE_LOAN_ADVANTAGE")
		private String eligibleLoanAdvantage;
		@JsonProperty("ROI_ADVANTAGE")
		private String roiAdvantage;
		@JsonProperty("EMI_AMOUNT")
		private EmiAmount emiAmount;
		@JsonProperty("MONTHS")
		private Months months;

		@JsonCreator
		public Advantage() {
		}

		public String getCombinedLoanSanctioned() {
			return combinedLoanSanctioned;
		}

		public void setCombinedLoanSanctioned(String combinedLoanSanctioned) {
			this.combinedLoanSanctioned = combinedLoanSanctioned;
		}

		public String getEligibleLoanAdvantage() {
			return eligibleLoanAdvantage;
		}

		public void setEligibleLoanAdvantage(String eligibleLoanAdvantage) {
			this.eligibleLoanAdvantage = eligibleLoanAdvantage;
		}

		public String getRoiAdvantage() {
			return roiAdvantage;
		}

		public void setRoiAdvantage(String roiAdvantage) {
			this.roiAdvantage = roiAdvantage;
		}

		public EmiAmount getEmiAmount() {
			return emiAmount;
		}

		public void setEmiAmount(EmiAmount emiAmount) {
			this.emiAmount = emiAmount;
		}

		public Months getMonths() {
			return months;
		}

		public void setMonths(Months months) {
			this.months = months;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class SuperHigherLoan implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@JsonProperty("APPLICABLE_EMI_BALANCE")
		private String applicableEmiBalance;
		@JsonProperty("APPLICABLE_EMI_FIRST_60")
		private String applicableEmiFirst60;
		@JsonProperty("APPLICABLE_EMI_NEXT_60")
		private String applicableEmiNext60;
		@JsonProperty("PERC_CHANGE_EMI_BALANCE")
		private String percChangeEmiBalance;
		@JsonProperty("FOIR_INSURANCE")
		private String foirInsurance;
		@JsonProperty("PERC_INCREASE_ELIG")
		private String percIncreaseElig;
		@JsonProperty("POSSIBLE_HIGHER_LOAN")
		private String possibleHigherLoan;
		@JsonProperty("TOTAL_LOAN_AMOUNT")
		private String totalLoanAmount;

		public SuperHigherLoan() {
		}

		public String getPercChangeEmiBalance() {
			return percChangeEmiBalance;
		}

		public void setPercChangeEmiBalance(String percChangeEmiBalance) {
			this.percChangeEmiBalance = percChangeEmiBalance;
		}

		public String getFoirInsurance() {
			return foirInsurance;
		}

		public void setFoirInsurance(String foirInsurance) {
			this.foirInsurance = foirInsurance;
		}

		public String getPercIncreaseElig() {
			return percIncreaseElig;
		}

		public void setPercIncreaseElig(String percIncreaseElig) {
			this.percIncreaseElig = percIncreaseElig;
		}

		public String getTotalLoanAmount() {
			return totalLoanAmount;
		}

		public void setTotalLoanAmount(String totalLoanAmount) {
			this.totalLoanAmount = totalLoanAmount;
		}

		public String getPossibleHigherLoan() {
			return possibleHigherLoan;
		}

		public void setPossibleHigherLoan(String possibleHigherLoan) {
			this.possibleHigherLoan = possibleHigherLoan;
		}

		public String getApplicableEmiFirst60() {
			return applicableEmiFirst60;
		}

		public void setApplicableEmiFirst60(String applicableEmiFirst60) {
			this.applicableEmiFirst60 = applicableEmiFirst60;
		}

		public String getApplicableEmiNext60() {
			return applicableEmiNext60;
		}

		public void setApplicableEmiNext60(String applicableEmiNext60) {
			this.applicableEmiNext60 = applicableEmiNext60;
		}

		public String getApplicableEmiBalance() {
			return applicableEmiBalance;
		}

		public void setApplicableEmiBalance(String applicableEmiBalance) {
			this.applicableEmiBalance = applicableEmiBalance;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class SuperLowEmi implements Serializable {

		private static final long serialVersionUID = 1L;

		@JsonProperty("APPLICABLE_EMI_BALANCE")
		private String applicable_emi_balance;
		@JsonProperty("APPLICABLE_EMI_FIRST_60")
		private String applicable_emi_first_60;
		@JsonProperty("APPLICABLE_EMI_NEXT_60")
		private String applicable_emi_next_60;
		@JsonProperty("FOIR_INSURANCE")
		private String foir_insurance;
		@JsonProperty("PERC_CHANGE_EMI_BALANCE_60")
		private String perc_change_emi_balance_60;
		@JsonProperty("PERC_LOW_EMI_POSSIBLE")
		private String perc_low_emi_possible;
		@JsonProperty("PROPOSED_LOAN_EFFECTIVE_EMI")
		private String proposed_loan_effective_emi;
		@JsonProperty("TOTAL_LOAN_AMOUNT")
		private String total_loan_amount;

		@JsonCreator
		public SuperLowEmi() {
		}

		// Getter Methods

		public String getApplicable_emi_balance() {
			return applicable_emi_balance;
		}

		public String getApplicable_emi_first_60() {
			return applicable_emi_first_60;
		}

		public String getApplicable_emi_next_60() {
			return applicable_emi_next_60;
		}

		public String getFoir_insurance() {
			return foir_insurance;
		}

		public String getPerc_change_emi_balance_60() {
			return perc_change_emi_balance_60;
		}

		public String getPerc_low_emi_possible() {
			return perc_low_emi_possible;
		}

		public String getProposed_loan_effective_emi() {
			return proposed_loan_effective_emi;
		}

		public String getTotal_loan_amount() {
			return total_loan_amount;
		}

		// Setter Methods

		public void setApplicable_emi_balance(String applicable_emi_balance) {
			this.applicable_emi_balance = applicable_emi_balance;
		}

		public void setApplicable_emi_first_60(String applicable_emi_first_60) {
			this.applicable_emi_first_60 = applicable_emi_first_60;
		}

		public void setApplicable_emi_next_60(String applicable_emi_next_60) {
			this.applicable_emi_next_60 = applicable_emi_next_60;
		}

		public void setFoir_insurance(String foir_insurance) {
			this.foir_insurance = foir_insurance;
		}

		public void setPerc_change_emi_balance_60(String perc_change_emi_balance_60) {
			this.perc_change_emi_balance_60 = perc_change_emi_balance_60;
		}

		public void setPerc_low_emi_possible(String perc_low_emi_possible) {
			this.perc_low_emi_possible = perc_low_emi_possible;
		}

		public void setProposed_loan_effective_emi(String proposed_loan_effective_emi) {
			this.proposed_loan_effective_emi = proposed_loan_effective_emi;
		}

		public void setTotal_loan_amount(String total_loan_amount) {
			this.total_loan_amount = total_loan_amount;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class FinalEligibility implements Serializable {

		private static final long serialVersionUID = 1L;

		@JsonProperty("CONSIDERED_MONTHLY_INCOME")
		private String consideredMonthlyIncome;
		@JsonProperty("ELIGIBLE_LOAN")
		private String eligibleLoan;
		@JsonProperty("EMI")
		private String emi;
		@JsonProperty("EMI_LI")
		private String emiLi;
		@JsonProperty("EMI_GI")
		private String emiGi;
		@JsonProperty("EMI_LIGI")
		private String emiLiGi;
		@JsonProperty("EMI_BT_HL")
		private String emiBtHl;
		@JsonProperty("EMI_HE_TOPUP")
		private String emiHeTopup;
		@JsonProperty("EXISTING_MONTHLY_OBLIGATIONS")
		private String existingMonthlyObligations;
		@JsonProperty("FINAL_ELIGIBLE_LOAN_BT_HL")
		private String finalEligibleLoanBtHl;
		@JsonProperty("FINAL_ELIGIBLE_LOAN_HE_TOPUP")
		private String finalEligibleLoanHeTopup;
		@JsonProperty("FOIR_CALC")
		private String foirCalc;
		@JsonProperty("FOIR_CALC_LIGI")
		private String foirCalcLigi;
		@JsonProperty("LOAN_RECOMMENDED")
		private String loanRecommended;
		@JsonProperty("MAX_POSSIBLE_EMI")
		private String maxPossibleEmi;
		@JsonProperty("TENOR_AS_PER_NORMS")
		private String tenorAsPerNorms;
		@JsonProperty("SUPER_LOW_EMI")
		private SuperLowEmi superLowEmi;
		@JsonProperty("SUPER_HIGHER_LOAN")
		private SuperHigherLoan superHigherLoan;
		@JsonProperty("ADVANTAGE")
		private Advantage advantage;

		@JsonCreator
		public FinalEligibility() {

		}

		public String getConsideredMonthlyIncome() {
			return consideredMonthlyIncome;
		}

		public void setConsideredMonthlyIncome(String consideredMonthlyIncome) {
			this.consideredMonthlyIncome = consideredMonthlyIncome;
		}

		public String getEligibleLoan() {
			return eligibleLoan;
		}

		public void setEligibleLoan(String eligibleLoan) {
			this.eligibleLoan = eligibleLoan;
		}

		public String getEmi() {
			return emi;
		}

		public void setEmi(String emi) {
			this.emi = emi;
		}

		public String getEmiLi() {
			return emiLi;
		}

		public void setEmiLi(String emiLi) {
			this.emiLi = emiLi;
		}

		public String getEmiGi() {
			return emiGi;
		}

		public void setEmiGi(String emiGi) {
			this.emiGi = emiGi;
		}

		public String getEmiLiGi() {
			return emiLiGi;
		}

		public void setEmiLiGi(String emiLiGi) {
			this.emiLiGi = emiLiGi;
		}

		public String getEmiBtHl() {
			return emiBtHl;
		}

		public void setEmiBtHl(String emiBtHl) {
			this.emiBtHl = emiBtHl;
		}

		public String getEmiHeTopup() {
			return emiHeTopup;
		}

		public void setEmiHeTopup(String emiHeTopup) {
			this.emiHeTopup = emiHeTopup;
		}

		public String getExistingMonthlyObligations() {
			return existingMonthlyObligations;
		}

		public void setExistingMonthlyObligations(String existingMonthlyObligations) {
			this.existingMonthlyObligations = existingMonthlyObligations;
		}

		public String getFinalEligibleLoanBtHl() {
			return finalEligibleLoanBtHl;
		}

		public void setFinalEligibleLoanBtHl(String finalEligibleLoanBtHl) {
			this.finalEligibleLoanBtHl = finalEligibleLoanBtHl;
		}

		public String getFinalEligibleLoanHeTopup() {
			return finalEligibleLoanHeTopup;
		}

		public void setFinalEligibleLoanHeTopup(String finalEligibleLoanHeTopup) {
			this.finalEligibleLoanHeTopup = finalEligibleLoanHeTopup;
		}

		public String getFoirCalc() {
			return foirCalc;
		}

		public void setFoirCalc(String foirCalc) {
			this.foirCalc = foirCalc;
		}

		public String getFoirCalcLigi() {
			return foirCalcLigi;
		}

		public void setFoirCalcLigi(String foirCalcLigi) {
			this.foirCalcLigi = foirCalcLigi;
		}

		public String getLoanRecommended() {
			return loanRecommended;
		}

		public void setLoanRecommended(String loanRecommended) {
			this.loanRecommended = loanRecommended;
		}

		public String getMaxPossibleEmi() {
			return maxPossibleEmi;
		}

		public void setMaxPossibleEmi(String maxPossibleEmi) {
			this.maxPossibleEmi = maxPossibleEmi;
		}

		public String getTenorAsPerNorms() {
			return tenorAsPerNorms;
		}

		public void setTenorAsPerNorms(String tenorAsPerNorms) {
			this.tenorAsPerNorms = tenorAsPerNorms;
		}

		public SuperLowEmi getSuperLowEmi() {
			return superLowEmi;
		}

		public void setSuperLowEmi(SuperLowEmi superLowEmi) {
			this.superLowEmi = superLowEmi;
		}

		public SuperHigherLoan getSuperHigherLoan() {
			return superHigherLoan;
		}

		public void setSuperHigherLoan(SuperHigherLoan superHigherLoan) {
			this.superHigherLoan = superHigherLoan;
		}

		public Advantage getAdvantage() {
			return advantage;
		}

		public void setAdvantage(Advantage advantage) {
			this.advantage = advantage;
		}

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class CalculationsLtv implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		@JsonProperty("ALLOWED_HL_LOAN_AMOUNT_CONSOLIDATED")
		private String allowed_hl_loan_amount_consolidated;
		@JsonProperty("ALLOWED_TOPUP_LOAN_AMOUNT_CONSOLIDATED")
		private String allowed_topup_loan_amount_consolidated;
		@JsonProperty("ALLOWED_TOTAL_LOAN_AMOUNT_CONSOLIDATED")
		private String allowed_total_loan_amount_consolidated;
		@JsonProperty("HL_LTV_ON_AV_CONSOLIDATED")
		private String hl_ltv_on_av_consolidated;
		@JsonProperty("HL_LTV_ON_DV_CONSOLIDATED")
		private String hl_ltv_on_dv_consolidated;
		@JsonProperty("HL_LTV_ON_MV_CONSOLIDATED")
		private String hl_ltv_on_mv_consolidated;
		@JsonProperty("INSURANCE_LTV_ON_MV_CONSOLIDATED")
		private String insurance_ltv_on_mv_consolidated;
		@JsonProperty("PERC_OF_AMENITIES_ON_AV_CONSOLIDATED")
		private String perc_of_amenities_on_av_consolidated;
		@JsonProperty("TOTAL_LOAN_LTV_ON_AV_GST_CONSOLIDATED")
		private String total_loan_ltv_on_av_gst_consolidated;
		@JsonProperty("TOTAL_LOAN_LTV_ON_AV_CONSOLIDATED")
		private String total_loan_ltv_on_av_consolidated;
		@JsonProperty("TOTAL_LOAN_LTV_ON_DV_CONSOLIDATED")
		private String total_loan_ltv_on_dv_consolidated;
		@JsonProperty("TOTAL_LOAN_LTV_ON_MV_CONSOLIDATED")
		private String total_loan_ltv_on_mv_consolidated;
		@JsonProperty("LTV_AS_PER_NHB_POLICY")
		private String ltv_as_per_nhb_policy;

		@JsonCreator
		public CalculationsLtv() {
		}

		// Getter Methods

		public String getAllowed_hl_loan_amount_consolidated() {
			return allowed_hl_loan_amount_consolidated;
		}

		public String getAllowed_topup_loan_amount_consolidated() {
			return allowed_topup_loan_amount_consolidated;
		}

		public String getAllowed_total_loan_amount_consolidated() {
			return allowed_total_loan_amount_consolidated;
		}

		public String getHl_ltv_on_av_consolidated() {
			return hl_ltv_on_av_consolidated;
		}

		public String getHl_ltv_on_dv_consolidated() {
			return hl_ltv_on_dv_consolidated;
		}

		public String getHl_ltv_on_mv_consolidated() {
			return hl_ltv_on_mv_consolidated;
		}

		public String getInsurance_ltv_on_mv_consolidated() {
			return insurance_ltv_on_mv_consolidated;
		}

		public String getPerc_of_amenities_on_av_consolidated() {
			return perc_of_amenities_on_av_consolidated;
		}

		public String getTotal_loan_ltv_on_av_gst_consolidated() {
			return total_loan_ltv_on_av_gst_consolidated;
		}

		public String getTotal_loan_ltv_on_av_consolidated() {
			return total_loan_ltv_on_av_consolidated;
		}

		public void setTotal_loan_ltv_on_av_consolidated(String total_loan_ltv_on_av_consolidated) {
			this.total_loan_ltv_on_av_consolidated = total_loan_ltv_on_av_consolidated;
		}

		public String getTotal_loan_ltv_on_dv_consolidated() {
			return total_loan_ltv_on_dv_consolidated;
		}

		public String getTotal_loan_ltv_on_mv_consolidated() {
			return total_loan_ltv_on_mv_consolidated;
		}

		// Setter Methods

		public void setAllowed_hl_loan_amount_consolidated(String allowed_hl_loan_amount_consolidated) {
			this.allowed_hl_loan_amount_consolidated = allowed_hl_loan_amount_consolidated;
		}

		public void setAllowed_topup_loan_amount_consolidated(String allowed_topup_loan_amount_consolidated) {
			this.allowed_topup_loan_amount_consolidated = allowed_topup_loan_amount_consolidated;
		}

		public void setAllowed_total_loan_amount_consolidated(String allowed_total_loan_amount_consolidated) {
			this.allowed_total_loan_amount_consolidated = allowed_total_loan_amount_consolidated;
		}

		public void setHl_ltv_on_av_consolidated(String hl_ltv_on_av_consolidated) {
			this.hl_ltv_on_av_consolidated = hl_ltv_on_av_consolidated;
		}

		public void setHl_ltv_on_dv_consolidated(String hl_ltv_on_dv_consolidated) {
			this.hl_ltv_on_dv_consolidated = hl_ltv_on_dv_consolidated;
		}

		public void setHl_ltv_on_mv_consolidated(String hl_ltv_on_mv_consolidated) {
			this.hl_ltv_on_mv_consolidated = hl_ltv_on_mv_consolidated;
		}

		public void setInsurance_ltv_on_mv_consolidated(String insurance_ltv_on_mv_consolidated) {
			this.insurance_ltv_on_mv_consolidated = insurance_ltv_on_mv_consolidated;
		}

		public void setPerc_of_amenities_on_av_consolidated(String perc_of_amenities_on_av_consolidated) {
			this.perc_of_amenities_on_av_consolidated = perc_of_amenities_on_av_consolidated;
		}

		public void setTotal_loan_ltv_on_av_gst_consolidated(String total_loan_ltv_on_av_gst_consolidated) {
			this.total_loan_ltv_on_av_gst_consolidated = total_loan_ltv_on_av_gst_consolidated;
		}

		public void setTotal_loan_ltv_on_dv_consolidated(String total_loan_ltv_on_dv_consolidated) {
			this.total_loan_ltv_on_dv_consolidated = total_loan_ltv_on_dv_consolidated;
		}

		public void setTotal_loan_ltv_on_mv_consolidated(String total_loan_ltv_on_mv_consolidated) {
			this.total_loan_ltv_on_mv_consolidated = total_loan_ltv_on_mv_consolidated;
		}

		public String getLtv_as_per_nhb_policy() {
			return ltv_as_per_nhb_policy;
		}

		public void setLtv_as_per_nhb_policy(String ltv_as_per_nhb_policy) {
			this.ltv_as_per_nhb_policy = ltv_as_per_nhb_policy;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ApplicationIn implements Serializable {

		private static final long serialVersionUID = 1L;
		@JsonProperty("BUSINESS_DATE")
		private String business_date;
		@JsonProperty("LEAD_ID")
		private String lead_id;
		@JsonProperty("LOAN_TYPE")
		private String loan_type;
		@JsonProperty("TRANSACTION_TYPE")
		private String transaction_type;
		@JsonProperty("TRANSACTION_SUBTYPE")
		private String transaction_subtype;
		@JsonProperty("SOURCE_SYSTEM")
		private String source_system;
		@JsonProperty("PRODUCT")
		private String product;
		@JsonProperty("APPLIED_LOAN_AMOUNT")
		private String applied_loan_amount;
		@JsonProperty("LINSURANCE_LOAN_AMOUNT")
		private String lInsurance_loan_amount;
		@JsonProperty("GINSURANCE_LOAN_AMOUNT")
		private String gInsurance_loan_amount;
		@JsonProperty("LOAN_RECOMMENDED_LI")
		private String loan_recommended_li;
		@JsonProperty("LOAN_RECOMMENDED_GI")
		private String loan_recommended_gi;
		@JsonProperty("LOAN_RECOMMENDED_BTHL")
		private String loan_recommended_bthl;
		@JsonProperty("LOAN_RECOMMENDED_HETOPUP")
		private String loan_recommended_hetopup;
		@JsonProperty("ALLOWED_HL_LOAN_AMOUNT")
		private String allowed_hl_loan_amount;
		@JsonProperty("ALLOWED_TOPUP_LOAN_AMOUNT")
		private String allowed_topup_loan_amount;
		@JsonProperty("ALLOWED_TOTAL_LOAN_AMOUNT")
		private String allowed_total_loan_amount;
		@JsonProperty("INITIAL_TRANSACTION")
		private String initial_transaction;
		@JsonProperty("TENOR")
		private String tenor;
		@JsonProperty("TENOR_CREDIT")
		private String tenor_credit;
		@JsonProperty("TIER")
		private String tier;
		@JsonProperty("RATE_OF_INTEREST")
		private String rate_of_interest;
		@JsonProperty("LOAN_PURPOSE")
		private String loan_purpose;
		@JsonProperty("END_USE")
		private String end_use;
		@JsonProperty("SOURCING_BRANCH")
		private String sourcing_branch;
		@JsonProperty("SOURCING_CHANNEL")
		private String sourcing_channel;
		@JsonProperty("CALL_SEGMENTATION")
		private String call_segmentation;
		@JsonProperty("TOTAL_LOAN_AMOUNT")
		private String total_loan_amount;
		@JsonProperty("HOME_LOAN_AMOUNT")
		private String home_loan_amount;
		@JsonProperty("TOP_UP_LOAN_AMOUNT")
		private String top_up_loan_amount;
		@JsonProperty("LI_GI_INSURANCE_LOAN_AMOUNT")
		private String li_gi_insurance_loan_amount;
		@JsonProperty("STEP_EMI_APPLICABLE")
		private String step_emi_applicable;
		@JsonProperty("STEP_EMI_TYPE")
		private String step_emi_type;
		@JsonProperty("TOTAL_LOAN_INCLUDING_LI_GI")
		private String total_loan_including_li_gi;
		@JsonProperty("PRIMARY_LOAN_AMOUNT")
		private String primary_loan_amount;
		@JsonProperty("LTV_ON_AV_AS_PER_SCHEME")
		private String ltv_on_av_as_per_scheme;
		@JsonProperty("LTV_ON_AV_PLUS_GST_AS_PER_SCHEME")
		private String ltv_on_av_plus_gst_as_per_scheme;
		@JsonProperty("LTV_ON_DV_AS_PER_SCHEME")
		private String ltv_on_dv_as_per_scheme;
		@JsonProperty("LTV_ON_MV_AS_PER_SCHEME")
		private String ltv_on_mv_as_per_scheme;

		@JsonProperty("PROPERTY_MASTER")
		PropertyMaster propertyMaster;

		@JsonProperty("LRD")
		LRD lrd;
		@JsonProperty("PD")
		PD pd;
		@JsonProperty("FCU")
		FCU fcu;
		@JsonProperty("DV")
		DV dv;
		@JsonProperty("SUPERCALC")
		Supercalc superCalc;

		@JsonCreator
		public ApplicationIn() {
		}

		// Getter Methods
		public String getBusiness_date() {
			return business_date;
		}

		public String getLead_id() {
			return lead_id;
		}

		public String getLoan_type() {
			return loan_type;
		}

		public String getTransaction_type() {
			return transaction_type;
		}

		public String getTransaction_subtype() {
			return transaction_subtype;
		}

		public String getSource_system() {
			return source_system;
		}

		public String getProduct() {
			return product;
		}

		public String getApplied_loan_amount() {
			return applied_loan_amount;
		}

		public String getlInsurance_loan_amount() {
			return lInsurance_loan_amount;
		}

		public void setlInsurance_loan_amount(String lInsurance_loan_amount) {
			this.lInsurance_loan_amount = lInsurance_loan_amount;
		}

		public String getgInsurance_loan_amount() {
			return gInsurance_loan_amount;
		}

		public void setgInsurance_loan_amount(String gInsurance_loan_amount) {
			this.gInsurance_loan_amount = gInsurance_loan_amount;
		}

		public String getLoan_recommended_li() {
			return loan_recommended_li;
		}

		public String getLoan_recommended_gi() {
			return loan_recommended_gi;
		}

		public String getLoan_recommended_bthl() {
			return loan_recommended_bthl;
		}

		public String getLoan_recommended_hetopup() {
			return loan_recommended_hetopup;
		}

		public String getAllowed_hl_loan_amount() {
			return allowed_hl_loan_amount;
		}

		public String getAllowed_topup_loan_amount() {
			return allowed_topup_loan_amount;
		}

		public String getAllowed_total_loan_amount() {
			return allowed_total_loan_amount;
		}

		public String getInitial_transaction() {
			return initial_transaction;
		}

		public String getTenor() {
			return tenor;
		}

		public String getTenor_credit() {
			return tenor_credit;
		}

		public String getTier() {
			return tier;
		}

		public String getRate_of_interest() {
			return rate_of_interest;
		}

		public String getLoan_purpose() {
			return loan_purpose;
		}

		public String getEnd_use() {
			return end_use;
		}

		public String getSourcing_branch() {
			return sourcing_branch;
		}

		public String getSourcing_channel() {
			return sourcing_channel;
		}

		public String getCall_segmentation() {
			return call_segmentation;
		}

		public String getTotal_loan_amount() {
			return total_loan_amount;
		}

		public String getHome_loan_amount() {
			return home_loan_amount;
		}

		public String getTop_up_loan_amount() {
			return top_up_loan_amount;
		}

		public String getLi_gi_insurance_loan_amount() {
			return li_gi_insurance_loan_amount;
		}

		public String getStep_emi_applicable() {
			return step_emi_applicable;
		}

		public String getStep_emi_type() {
			return step_emi_type;
		}

		public String getTotal_loan_including_li_gi() {
			return total_loan_including_li_gi;
		}

		public String getPrimary_loan_amount() {
			return primary_loan_amount;
		}

		public String getLtv_on_av_as_per_scheme() {
			return ltv_on_av_as_per_scheme;
		}

		public String getLtv_on_av_plus_gst_as_per_scheme() {
			return ltv_on_av_plus_gst_as_per_scheme;
		}

		public String getLtv_on_dv_as_per_scheme() {
			return ltv_on_dv_as_per_scheme;
		}

		public String getLtv_on_mv_as_per_scheme() {
			return ltv_on_mv_as_per_scheme;
		}

		public PropertyMaster getPropertyMaster() {
			return propertyMaster;
		}

		public LRD getLrd() {
			return lrd;
		}

		public PD getPd() {
			return pd;
		}

		public FCU getFcu() {
			return fcu;
		}

		public DV getDv() {
			return dv;
		}

		public Supercalc getSuperCalc() {
			return superCalc;
		}

		// Setter Methods
		public void setBusiness_date(String business_date) {
			this.business_date = business_date;
		}

		public void setLead_id(String lead_id) {
			this.lead_id = lead_id;
		}

		public void setLoan_type(String loan_type) {
			this.loan_type = loan_type;
		}

		public void setTransaction_type(String transaction_type) {
			this.transaction_type = transaction_type;
		}

		public void setTransaction_subtype(String transaction_subtype) {
			this.transaction_subtype = transaction_subtype;
		}

		public void setSource_system(String source_system) {
			this.source_system = source_system;
		}

		public void setProduct(String product) {
			this.product = product;
		}

		public void setApplied_loan_amount(String applied_loan_amount) {
			this.applied_loan_amount = applied_loan_amount;
		}

		public void setLoan_recommended_li(String loan_recommended_li) {
			this.loan_recommended_li = loan_recommended_li;
		}

		public void setLoan_recommended_gi(String loan_recommended_gi) {
			this.loan_recommended_gi = loan_recommended_gi;
		}

		public void setLoan_recommended_bthl(String loan_recommended_bthl) {
			this.loan_recommended_bthl = loan_recommended_bthl;
		}

		public void setLoan_recommended_hetopup(String loan_recommended_hetopup) {
			this.loan_recommended_hetopup = loan_recommended_hetopup;
		}

		public void setAllowed_hl_loan_amount(String allowed_hl_loan_amount) {
			this.allowed_hl_loan_amount = allowed_hl_loan_amount;
		}

		public void setAllowed_topup_loan_amount(String allowed_topup_loan_amount) {
			this.allowed_topup_loan_amount = allowed_topup_loan_amount;
		}

		public void setAllowed_total_loan_amount(String allowed_total_loan_amount) {
			this.allowed_total_loan_amount = allowed_total_loan_amount;
		}

		public void setInitial_transaction(String initial_transaction) {
			this.initial_transaction = initial_transaction;
		}

		public void setTenor(String tenor) {
			this.tenor = tenor;
		}

		public void setTenor_credit(String tenor_credit) {
			this.tenor_credit = tenor_credit;
		}

		public void setTier(String tier) {
			this.tier = tier;
		}

		public void setRate_of_interest(String rate_of_interest) {
			this.rate_of_interest = rate_of_interest;
		}

		public void setLoan_purpose(String loan_purpose) {
			this.loan_purpose = loan_purpose;
		}

		public void setEnd_use(String end_use) {
			this.end_use = end_use;
		}

		public void setSourcing_branch(String sourcing_branch) {
			this.sourcing_branch = sourcing_branch;
		}

		public void setSourcing_channel(String sourcing_channel) {
			this.sourcing_channel = sourcing_channel;
		}

		public void setCall_segmentation(String call_segmentation) {
			this.call_segmentation = call_segmentation;
		}

		public void setTotal_loan_amount(String total_loan_amount) {
			this.total_loan_amount = total_loan_amount;
		}

		public void setHome_loan_amount(String home_loan_amount) {
			this.home_loan_amount = home_loan_amount;
		}

		public void setTop_up_loan_amount(String top_up_loan_amount) {
			this.top_up_loan_amount = top_up_loan_amount;
		}

		public void setLi_gi_insurance_loan_amount(String li_gi_insurance_loan_amount) {
			this.li_gi_insurance_loan_amount = li_gi_insurance_loan_amount;
		}

		public void setStep_emi_applicable(String step_emi_applicable) {
			this.step_emi_applicable = step_emi_applicable;
		}

		public void setStep_emi_type(String step_emi_type) {
			this.step_emi_type = step_emi_type;
		}

		public void setTotal_loan_including_li_gi(String total_loan_including_li_gi) {
			this.total_loan_including_li_gi = total_loan_including_li_gi;
		}

		public void setPrimary_loan_amount(String primary_loan_amount) {
			this.primary_loan_amount = primary_loan_amount;
		}

		public void setLtv_on_av_as_per_scheme(String ltv_on_av_as_per_scheme) {
			this.ltv_on_av_as_per_scheme = ltv_on_av_as_per_scheme;
		}

		public void setLtv_on_av_plus_gst_as_per_scheme(String ltv_on_av_plus_gst_as_per_scheme) {
			this.ltv_on_av_plus_gst_as_per_scheme = ltv_on_av_plus_gst_as_per_scheme;
		}

		public void setLtv_on_dv_as_per_scheme(String ltv_on_dv_as_per_scheme) {
			this.ltv_on_dv_as_per_scheme = ltv_on_dv_as_per_scheme;
		}

		public void setLtv_on_mv_as_per_scheme(String ltv_on_mv_as_per_scheme) {
			this.ltv_on_mv_as_per_scheme = ltv_on_mv_as_per_scheme;
		}

		public void setPropertyMaster(PropertyMaster propertyMaster) {
			this.propertyMaster = propertyMaster;
		}

		public void setLrd(LRD lrd) {
			this.lrd = lrd;
		}

		public void setPd(PD pd) {
			this.pd = pd;
		}

		public void setFc(FCU fcu) {
			this.fcu = fcu;
		}

		public void setDv(DV dv) {
			this.dv = dv;
		}

		public void setSuperCalc(Supercalc superCalc) {
			this.superCalc = superCalc;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ApplicantDetails implements Serializable {

		private static final long serialVersionUID = 1L;
		@JsonProperty("FIRST_NAME")
		private String firstName;
		@JsonProperty("MIDDLE_NAME")
		private String middleName;
		@JsonProperty("LAST_NAME")
		private String lastName;
		@JsonProperty("CCCID")
		private String cccid;
		@JsonProperty("APPLICANT_CATEGORY")
		private String applicantCategory;
		@JsonProperty("INCOME_CONSIDERED")
		private String incomeConsidered;
		@JsonProperty("PCHFL_EMPLOYEE")
		private String pchflEmployee;
		@JsonProperty("REF_CHECK_LINE_MANAGER_DOCUMENTED")
		private String refCheckLineManagerDocumented;
		@JsonProperty("CAUTION_PROFILE")
		private String cautionProfile;
		@JsonProperty("NEGATIVE_PROFILE")
		private String negativeProfile;
		@JsonProperty("PROPERTY_OWNER")
		private String propertyOwner;
		@JsonProperty("DOB")
		private String dob;
		@JsonProperty("GENDER")
		private String gender;
		@JsonProperty("RETIREMENT_DATE")
		private String retirementDate;
		@JsonProperty("NATIONALITY")
		private String nationality;
		@JsonProperty("RESIDENT_STATUS")
		private String residentStatus;
		@JsonProperty("POA_FLAG")
		private String poaFlag;
		@JsonProperty("RELATIONSHIP_WITH_MAIN_APPLICANT")
		private String relationshipWithMainApplicant;
		@JsonProperty("MARITAL_STATUS")
		private String maritalStatus;
		@JsonProperty("APPLICANT_TYPE")
		private String applicantType;
		@JsonProperty("PERSONAL_EMAIL")
		private String personalEmail;
		@JsonProperty("SEGMENT")
		private String segment;
		@JsonProperty("FOIR")
		private String foir;
		@JsonProperty("ELIGIBILITY_METHOD")
		private String eligibilityMethod;
		@JsonProperty("ROI")
		private String roi;
		@JsonProperty("TENURE_CREDIT")
		private String tenureCredit;
		@JsonProperty("SOURCING_BRANCH")
		private String sourcingBranch;
		@JsonProperty("PENSION_FLAG")
		private String pensionFlag;
		@JsonProperty("RETIREMENT_AGE")
		private String retirementAge;
		@JsonProperty("INSURANCE_FLAG")
		private String insuranceFlag;

		@JsonProperty("DECISION_REASON_CODE")
		private DecisionReasonCode decisionReasonCode;

		@JsonCreator
		public ApplicantDetails() {

		}

		public DecisionReasonCode getDecisionReasonCode() {
			return decisionReasonCode;
		}

		public void setDecisionReasonCode(DecisionReasonCode decisionReasonCode) {
			this.decisionReasonCode = decisionReasonCode;
		}

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getMiddleName() {
			return middleName;
		}

		public void setMiddleName(String middleName) {
			this.middleName = middleName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public String getCccid() {
			return cccid;
		}

		public void setCccid(String cccid) {
			this.cccid = cccid;
		}

		public String getApplicantCategory() {
			return applicantCategory;
		}

		public void setApplicantCategory(String applicantCategory) {
			this.applicantCategory = applicantCategory;
		}

		public String getIncomeConsidered() {
			return incomeConsidered;
		}

		public void setIncomeConsidered(String incomeConsidered) {
			this.incomeConsidered = incomeConsidered;
		}

		public String getPchflEmployee() {
			return pchflEmployee;
		}

		public void setPchflEmployee(String pchflEmployee) {
			this.pchflEmployee = pchflEmployee;
		}

		public String getRefCheckLineManagerDocumented() {
			return refCheckLineManagerDocumented;
		}

		public void setRefCheckLineManagerDocumented(String refCheckLineManagerDocumented) {
			this.refCheckLineManagerDocumented = refCheckLineManagerDocumented;
		}

		public String getCautionProfile() {
			return cautionProfile;
		}

		public void setCautionProfile(String cautionProfile) {
			this.cautionProfile = cautionProfile;
		}

		public String getNegativeProfile() {
			return negativeProfile;
		}

		public void setNegativeProfile(String negativeProfile) {
			this.negativeProfile = negativeProfile;
		}

		public String getPropertyOwner() {
			return propertyOwner;
		}

		public void setPropertyOwner(String propertyOwner) {
			this.propertyOwner = propertyOwner;
		}

		public String getDob() {
			return dob;
		}

		public void setDob(String dob) {
			this.dob = dob;
		}

		public String getGender() {
			return gender;
		}

		public void setGender(String gender) {
			this.gender = gender;
		}

		public String getRetirementDate() {
			return retirementDate;
		}

		public void setRetirementDate(String retirementDate) {
			this.retirementDate = retirementDate;
		}

		public String getNationality() {
			return nationality;
		}

		public void setNationality(String nationality) {
			this.nationality = nationality;
		}

		public String getResidentStatus() {
			return residentStatus;
		}

		public void setResidentStatus(String residentStatus) {
			this.residentStatus = residentStatus;
		}

		public String getPoaFlag() {
			return poaFlag;
		}

		public void setPoaFlag(String poaFlag) {
			this.poaFlag = poaFlag;
		}

		public String getRelationshipWithMainApplicant() {
			return relationshipWithMainApplicant;
		}

		public void setRelationshipWithMainApplicant(String relationshipWithMainApplicant) {
			this.relationshipWithMainApplicant = relationshipWithMainApplicant;
		}

		public String getMaritalStatus() {
			return maritalStatus;
		}

		public void setMaritalStatus(String maritalStatus) {
			this.maritalStatus = maritalStatus;
		}

		public String getApplicantType() {
			return applicantType;
		}

		public void setApplicantType(String applicantType) {
			this.applicantType = applicantType;
		}

		public String getPersonalEmail() {
			return personalEmail;
		}

		public void setPersonalEmail(String personalEmail) {
			this.personalEmail = personalEmail;
		}

		public String getSegment() {
			return segment;
		}

		public void setSegment(String segment) {
			this.segment = segment;
		}

		public String getFoir() {
			return foir;
		}

		public void setFoir(String foir) {
			this.foir = foir;
		}

		public String getEligibilityMethod() {
			return eligibilityMethod;
		}

		public void setEligibilityMethod(String eligibilityMethod) {
			this.eligibilityMethod = eligibilityMethod;
		}

		public String getRoi() {
			return roi;
		}

		public void setRoi(String roi) {
			this.roi = roi;
		}

		public String getTenureCredit() {
			return tenureCredit;
		}

		public void setTenureCredit(String tenureCredit) {
			this.tenureCredit = tenureCredit;
		}

		public String getSourcingBranch() {
			return sourcingBranch;
		}

		public void setSourcingBranch(String sourcingBranch) {
			this.sourcingBranch = sourcingBranch;
		}

		public String getPensionFlag() {
			return pensionFlag;
		}

		public void setPensionFlag(String pensionFlag) {
			this.pensionFlag = pensionFlag;
		}

		public String getRetirementAge() {
			return retirementAge;
		}

		public void setRetirementAge(String retirementAge) {
			this.retirementAge = retirementAge;
		}

		public String getInsuranceFlag() {
			return insuranceFlag;
		}

		public void setInsuranceFlag(String insuranceFlag) {
			this.insuranceFlag = insuranceFlag;
		}

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class EmploymentDetails implements Serializable {

		private static final long serialVersionUID = 1L;
		@JsonProperty("EMPLOYMENT_TYPE")
		private String employmentType;
		@JsonProperty("ENTITY_TYPE")
		private String entityType;
		@JsonProperty("EMPLOYER_CATEGORY")
		private String employerCategory;
		@JsonProperty("EMPLOYER_VINTAGE")
		private String employerVintage;
		@JsonProperty("NO_OF_EMPLOYEES")
		private String noOfEmployees;
		@JsonProperty("WORKING_SINCE")
		private String workingSince;
		@JsonProperty("EMPLOYER_TYPE")
		private String employerType;
		@JsonProperty("WORK_EXP_IN_YEARS_CUR")
		private String workExpInYearsCur;
		@JsonProperty("WORK_EXP_IN_MONTH_CUR")
		private String workExpInMonthCur;
		@JsonProperty("WORK_EXP_IN_YEARS")
		private String workExpInYears;
		@JsonProperty("WORK_EXP_IN_MONTHS")
		private String workExpInMonths;
		@JsonProperty("WORK_EXP_ABROAD_IN_YEARS")
		private String workExpAbroadInYears;
		@JsonProperty("VISA_EXPIRY_DATE")
		private String visaExpiryDate;
		@JsonProperty("EXCHANGE_RATE")
		private String exchangeRate;
		@JsonProperty("MONTHS_OF_SAIL_IN_LAST_3YEARS")
		private String monthsOfSailInLast3years;
		@JsonProperty("PROFESSION")
		private String profession;
		@JsonProperty("DESIGNATION")
		private String designation;
		@JsonProperty("OFFICIAL_EMAIL")
		private String officialEmail;

		@JsonCreator
		public EmploymentDetails() {

		}

		public String getEmploymentType() {
			return employmentType;
		}

		public void setEmploymentType(String employmentType) {
			this.employmentType = employmentType;
		}

		public String getEntityType() {
			return entityType;
		}

		public void setEntityType(String entityType) {
			this.entityType = entityType;
		}

		public String getEmployerCategory() {
			return employerCategory;
		}

		public void setEmployerCategory(String employerCategory) {
			this.employerCategory = employerCategory;
		}

		public String getEmployerVintage() {
			return employerVintage;
		}

		public void setEmployerVintage(String employerVintage) {
			this.employerVintage = employerVintage;
		}

		public String getNoOfEmployees() {
			return noOfEmployees;
		}

		public void setNoOfEmployees(String noOfEmployees) {
			this.noOfEmployees = noOfEmployees;
		}

		public String getWorkingSince() {
			return workingSince;
		}

		public void setWorkingSince(String workingSince) {
			this.workingSince = workingSince;
		}

		public String getEmployerType() {
			return employerType;
		}

		public void setEmployerType(String employerType) {
			this.employerType = employerType;
		}

		public String getWorkExpInYearsCur() {
			return workExpInYearsCur;
		}

		public void setWorkExpInYearsCur(String workExpInYearsCur) {
			this.workExpInYearsCur = workExpInYearsCur;
		}

		public String getWorkExpInMonthCur() {
			return workExpInMonthCur;
		}

		public void setWorkExpInMonthCur(String workExpInMonthCur) {
			this.workExpInMonthCur = workExpInMonthCur;
		}

		public String getWorkExpInYears() {
			return workExpInYears;
		}

		public void setWorkExpInYears(String workExpInYears) {
			this.workExpInYears = workExpInYears;
		}

		public String getWorkExpInMonths() {
			return workExpInMonths;
		}

		public void setWorkExpInMonths(String workExpInMonths) {
			this.workExpInMonths = workExpInMonths;
		}

		public String getWorkExpAbroadInYears() {
			return workExpAbroadInYears;
		}

		public void setWorkExpAbroadInYears(String workExpAbroadInYears) {
			this.workExpAbroadInYears = workExpAbroadInYears;
		}

		public String getVisaExpiryDate() {
			return visaExpiryDate;
		}

		public void setVisaExpiryDate(String visaExpiryDate) {
			this.visaExpiryDate = visaExpiryDate;
		}

		public String getExchangeRate() {
			return exchangeRate;
		}

		public void setExchangeRate(String exchangeRate) {
			this.exchangeRate = exchangeRate;
		}

		public String getMonthsOfSailInLast3years() {
			return monthsOfSailInLast3years;
		}

		public void setMonthsOfSailInLast3years(String monthsOfSailInLast3years) {
			this.monthsOfSailInLast3years = monthsOfSailInLast3years;
		}

		public String getProfession() {
			return profession;
		}

		public void setProfession(String profession) {
			this.profession = profession;
		}

		public String getDesignation() {
			return designation;
		}

		public void setDesignation(String designation) {
			this.designation = designation;
		}

		public String getOfficialEmail() {
			return officialEmail;
		}

		public void setOfficialEmail(String officialEmail) {
			this.officialEmail = officialEmail;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ConsolidatedBanking implements Serializable {

		private static final long serialVersionUID = 1L;

		@JsonProperty("TOTAL_NO_OF_CREDIT_TRANSACTIONS")
		private String totalNoOfCreditTransactions;
		@JsonProperty("TOTAL_AMOUNT_OF_CREDIT_TRANSACTIONS")
		private String totalAmountOfCreditTransactions;
		@JsonProperty("TOTAL_NO_OF_DEBIT_TRANSACTIONS")
		private String totalNoOfDebitTransactions;
		@JsonProperty("TOTAL_AMOUNT_OF_DEBIT_TRANSACTIONS")
		private String totalAmountOfDebitTransactions;
		@JsonProperty("TOTAL_NO_OF_INWARD_CHEQUE_BOUNCE")
		private String totalNoOfInwardChequeBounce;
		@JsonProperty("PERC_INWARD_CHEQUE_BOUNCE")
		private String percInwardChequeBounce;
		@JsonProperty("TOTAL_NO_OF_OUTWARD_CHEQUE_BOUNCE")
		private String totalNoOfOutwardChequeBounce;
		@JsonProperty("PERC_OUTWARD_CHEQUE_BOUNCE")
		private String percOutwardChequeBounce;
		@JsonProperty("MIN_EOD_BALANCE")
		private String minEodBalance;
		@JsonProperty("MAX_EOD_BALANCE")
		private String maxEodBalance;
		@JsonProperty("AVERAGE_EOD_BALANCE")
		private String averageEodBalance;
		@JsonProperty("CL_BALANCE")
		private String clBalance;
		@JsonProperty("ODCC_UTILIZATION_PER_MONTH")
		private String odccUtilizationPerMonth;
		@JsonProperty("ODCC_LIMIT_PER_MONTH")
		private String odccLimitPerMonth;
		@JsonProperty("INTREST")
		private String intrest;
		@JsonProperty("TRF")
		private String trf;
		@JsonProperty("CREDIT_SUMMATION_OF_REPORTED_TURNOVER")
		private String creditSummationOfReportedTurnover;
		@JsonProperty("SUM_OF_CREDITS")
		private String sumOfCredits;
		@JsonProperty("SUM_OF_TRANSFER")
		private String sumOfTransfer;

		@JsonCreator
		public ConsolidatedBanking() {
		}

		public String getTotalNoOfCreditTransactions() {
			return totalNoOfCreditTransactions;
		}

		public void setTotalNoOfCreditTransactions(String totalNoOfCreditTransactions) {
			this.totalNoOfCreditTransactions = totalNoOfCreditTransactions;
		}

		public String getTotalAmountOfCreditTransactions() {
			return totalAmountOfCreditTransactions;
		}

		public void setTotalAmountOfCreditTransactions(String totalAmountOfCreditTransactions) {
			this.totalAmountOfCreditTransactions = totalAmountOfCreditTransactions;
		}

		public String getTotalNoOfDebitTransactions() {
			return totalNoOfDebitTransactions;
		}

		public void setTotalNoOfDebitTransactions(String totalNoOfDebitTransactions) {
			this.totalNoOfDebitTransactions = totalNoOfDebitTransactions;
		}

		public String getTotalAmountOfDebitTransactions() {
			return totalAmountOfDebitTransactions;
		}

		public void setTotalAmountOfDebitTransactions(String totalAmountOfDebitTransactions) {
			this.totalAmountOfDebitTransactions = totalAmountOfDebitTransactions;
		}

		public String getTotalNoOfInwardChequeBounce() {
			return totalNoOfInwardChequeBounce;
		}

		public void setTotalNoOfInwardChequeBounce(String totalNoOfInwardChequeBounce) {
			this.totalNoOfInwardChequeBounce = totalNoOfInwardChequeBounce;
		}

		public String getPercInwardChequeBounce() {
			return percInwardChequeBounce;
		}

		public void setPercInwardChequeBounce(String percInwardChequeBounce) {
			this.percInwardChequeBounce = percInwardChequeBounce;
		}

		public String getTotalNoOfOutwardChequeBounce() {
			return totalNoOfOutwardChequeBounce;
		}

		public void setTotalNoOfOutwardChequeBounce(String totalNoOfOutwardChequeBounce) {
			this.totalNoOfOutwardChequeBounce = totalNoOfOutwardChequeBounce;
		}

		public String getPercOutwardChequeBounce() {
			return percOutwardChequeBounce;
		}

		public void setPercOutwardChequeBounce(String percOutwardChequeBounce) {
			this.percOutwardChequeBounce = percOutwardChequeBounce;
		}

		public String getMinEodBalance() {
			return minEodBalance;
		}

		public void setMinEodBalance(String minEodBalance) {
			this.minEodBalance = minEodBalance;
		}

		public String getMaxEodBalance() {
			return maxEodBalance;
		}

		public void setMaxEodBalance(String maxEodBalance) {
			this.maxEodBalance = maxEodBalance;
		}

		public String getAverageEodBalance() {
			return averageEodBalance;
		}

		public void setAverageEodBalance(String averageEodBalance) {
			this.averageEodBalance = averageEodBalance;
		}

		public String getClBalance() {
			return clBalance;
		}

		public void setClBalance(String clBalance) {
			this.clBalance = clBalance;
		}

		public String getOdccUtilizationPerMonth() {
			return odccUtilizationPerMonth;
		}

		public void setOdccUtilizationPerMonth(String odccUtilizationPerMonth) {
			this.odccUtilizationPerMonth = odccUtilizationPerMonth;
		}

		public String getOdccLimitPerMonth() {
			return odccLimitPerMonth;
		}

		public void setOdccLimitPerMonth(String odccLimitPerMonth) {
			this.odccLimitPerMonth = odccLimitPerMonth;
		}

		public String getIntrest() {
			return intrest;
		}

		public void setIntrest(String intrest) {
			this.intrest = intrest;
		}

		public String getTrf() {
			return trf;
		}

		public void setTrf(String trf) {
			this.trf = trf;
		}

		public String getCreditSummationOfReportedTurnover() {
			return creditSummationOfReportedTurnover;
		}

		public void setCreditSummationOfReportedTurnover(String creditSummationOfReportedTurnover) {
			this.creditSummationOfReportedTurnover = creditSummationOfReportedTurnover;
		}

		public String getSumOfCredits() {
			return sumOfCredits;
		}

		public void setSumOfCredits(String sumOfCredits) {
			this.sumOfCredits = sumOfCredits;
		}

		public String getSumOfTransfer() {
			return sumOfTransfer;
		}

		public void setSumOfTransfer(String sumOfTransfer) {
			this.sumOfTransfer = sumOfTransfer;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Income implements Serializable {

		private static final long serialVersionUID = 1L;

		@JsonProperty("ELIGIBILITY_METHOD")
		private String eligibilityMethod;
		@JsonProperty("GROSS_MONTHLY_INCOME_NRI")
		private String grossMonthlyIncomeNri;
		@JsonProperty("REGISTERED_RENTAL_INCOME_PREVIOUSYR")
		private String registeredRentalIncomePreviousyr;
		@JsonProperty("REGISTERED_RENTAL_INCOME_CURRENTYR")
		private String registeredRentalIncomeCurrentyr;
		@JsonProperty("CASH_PROFIT_PREVIOUSYEAR")
		private String cashProfitPreviousyear;
		@JsonProperty("CASH_PROFIT_CURRENTYEAR")
		private String cashProfitCurrentyear;
		@JsonProperty("OTHER_INCOME_PREVIOUSYEAR")
		private String otherIncomePreviousyear;
		@JsonProperty("OTHER_INCOME_CURRENTYEAR")
		private String otherIncomeCurrentyear;
		@JsonProperty("GROSS_RECIEPT_PREVIOUSYEAR")
		private String grossRecieptPreviousyear;
		@JsonProperty("GROSS_RECIEPT_CURRENTYEAR")
		private String grossRecieptCurrentyear;
		@JsonProperty("GROSS_PROFIT_PREVIOUSYEAR")
		private String grossProfitPreviousyear;
		@JsonProperty("GROSS_PROFIT_CURRENTYEAR")
		private String grossProfitCurrentyear;
		@JsonProperty("INDUSTRY_TURNOVER_PREVIOUSYR")
		private String industryTurnoverPreviousyr;
		@JsonProperty("INDUSTRY_TURNOVER_CURRENTYR")
		private String industryTurnoverCurrentyr;
		@JsonProperty("DEBT_EQUITY_RATIO")
		private String debtEquityRatio;
		@JsonProperty("NET_ANNUAL_TUROVER")
		private String netAnnualTurover;
		@JsonProperty("ANNUALIZED_BANKING_TURNOVER")
		private String annualizedBankingTurnover;
		@JsonProperty("PAT_PREVIOUS_YEAR")
		private String patPreviousYear;
		@JsonProperty("PAT_CURRENT_YEAR")
		private String patCurrentYear;
		@JsonProperty("PBT_PREVIOUS_YEAR")
		private String pbtPreviousYear;
		@JsonProperty("PBT_CURRENT_YEAR")
		private String pbtCurrentYear;
		@JsonProperty("DEPRECIATION_PREVIOUSYEAR")
		private String depreciationPreviousyear;
		@JsonProperty("DEPRECIATION_CURRENTYEAR")
		private String depreciationCurrentyear;
		@JsonProperty("INTEREST_PAID_PARTNER_DIRECTOR_PREVIOUSYEAR")
		private String interestPaidPartnerDirectorPreviousyear;
		@JsonProperty("INTEREST_PAID_PARTNER_DIRECTOR_CURRENTYEAR")
		private String interestPaidPartnerDirectorCurrentyear;
		@JsonProperty("INTEREST_INCOME_CURRENTYR")
		private String interestIncomeCurrentyr;
		@JsonProperty("INTEREST_INCOME_PREVIOUSYR")
		private String interestIncomePreviousyr;
		@JsonProperty("CA_ASSESSED_INCOME")
		private String caAssessedIncome;
		@JsonProperty("SALARY_PARTNER_DIRECTOR_CURRENTYEAR")
		private String salaryPartnerDirectorCurrentyear;
		@JsonProperty("SALARY_PARTNER_DIRECTOR_PREVIOUSYEAR")
		private String salaryPartnerDirectorPreviousyear;
		@JsonProperty("INTEREST_PAID_ON_TERM_LOAN_CURRENTYEAR")
		private String interestPaidOnTermLoanCurrentyear;
		@JsonProperty("INTEREST_PAID_ON_TERM_LOAN_PREVIOUSYEAR")
		private String interestPaidOnTermLoanPreviousyear;
		@JsonProperty("OTHER_PAYMENTS_CURRENTYEAR")
		private String otherPaymentsCurrentyear;
		@JsonProperty("OTHER_PAYMENTS_PREVIOUSYEAR")
		private String otherPaymentsPreviousyear;
		@JsonProperty("ITR_PREVIOUS_YEAR_FILING_DATE")
		private String itrPreviousYearFilingDate;
		@JsonProperty("ITR_CURRENT_YEAR_FILING_DATE")
		private String itrCurrentYearFilingDate;
		@JsonProperty("INTEREST_FD_CURRENTYEAR")
		private String interestFdCurrentyear;
		@JsonProperty("INTEREST_FD_PREVIOUSYEAR")
		private String interestFdPreviousyear;
		@JsonProperty("INTEREST_COMMISION_CURRENTYR")
		private String interestCommisionCurrentyr;
		@JsonProperty("INTEREST_COMMISION_PREVIOUSYR")
		private String interestCommisionPreviousyr;
		@JsonProperty("TOTAL_REVENUE_CURRENTYEAR")
		private String totalRevenueCurrentyear;
		@JsonProperty("TOTAL_REVENUE_PREVIOUSYEAR")
		private String totalRevenuePreviousyear;
		@JsonProperty("BASIC_PAY")
		private String basicPay;
		@JsonProperty("HRA")
		private String hra;
		@JsonProperty("SPECIAL_ALLOWANCE")
		private String specialAllowance;
		@JsonProperty("OTHERS_FIXED_PAY")
		private String othersFixedPay;
		@JsonProperty("MONTHLY_VARIABLE_PAY")
		private String monthlyVariablePay;
		@JsonProperty("QAURTELY_VARIABLE_PAY")
		private String qaurtelyVariablePay;
		@JsonProperty("ANNUAL_VARIABLE_PAY")
		private String annualVariablePay;
		@JsonProperty("PERCENTAGE_CONSIDERED_MONTHLY_VARIABLE_PAY_INCENTIVE")
		private String percentageConsideredMonthlyVariablePayIncentive;
		@JsonProperty("PERCENTAGE_CONSIDERED_QUARTERLY_VARIABLE_PAY_INCENTIVE")
		private String percentageConsideredQuarterlyVariablePayIncentive;
		@JsonProperty("PERCENTAGE_CONSIDERED_ANNUAL_VARIABLE_PAY_INCENTIVE")
		private String percentageConsideredAnnualVariablePayIncentive;
		@JsonProperty("TOTAL_FIXED_PAY")
		private String totalFixedPay;
		@JsonProperty("TOTAL_VARIABLE_PAY")
		private String totalVariablePay;
		@JsonProperty("RENTAL_INCOME_PER_MONTH")
		private String rentalIncomePerMonth;
		@JsonProperty("INTEREST_INCOME_IN_ITR")
		private String interestIncomeInItr;
		@JsonProperty("DIVIDEND_INCOME_IN_ITR")
		private String dividendIncomeInItr;
		@JsonProperty("COMMISION_INCOME_IN_ITR")
		private String commisionIncomeInItr;
		@JsonProperty("PERCENTAGE_CONSIDERED_INTEREST_INCOME_IN_ITR")
		private String percentageConsideredInterestIncomeInItr;
		@JsonProperty("PERCENTAGE_CONSIDERED_DIVIDEND_INCOME_IN_ITR")
		private String percentageConsideredDividendIncomeInItr;
		@JsonProperty("PERCENTAGE_CONSIDERED_COMMISION_INCOME_IN_ITR")
		private String percentageConsideredCommisionIncomeInItr;
		@JsonProperty("OTHER_INCOME_PER_MONTH")
		private String otherIncomePerMonth;
		@JsonProperty("CUSTOMER_DECLARED_INCOME")
		private String customerDeclaredIncome;
		@JsonProperty("PERCENTAGE_CAPPING_LAST_YEAR_BUSINESS_INCOME")
		private String percentageCappingLastYearBusinessIncome;
		@JsonProperty("CONSIDER_CURRENT_YEAR_BUSINESS_INCOME")
		private String considerCurrentYearBusinessIncome;
		@JsonProperty("CAPPING_OF_ANNUAL_TURNOVER_GROSS_PROFIT_METHOD")
		private String cappingOfAnnualTurnoverGrossProfitMethod;
		@JsonProperty("MULTIPLIER_GROSS_RECEIPT_METHOD")
		private String multiplierGrossReceiptMethod;
		@JsonProperty("MULTIPLIER_LIQUID_INCOME_PROGRAM")
		private String multiplierLiquidIncomeProgram;
		@JsonProperty("CONSIDER_CONSOLIDATION_OF_FINANCIALS")
		private String considerConsolidationOfFinancials;
		@JsonProperty("MULTIPLIER_INDUSTRY_MARGIN")
		private String multiplierIndustryMargin;
		@JsonProperty("OTHER_INCOME_CAPPING_PERCENTAGE")
		private String otherIncomeCappingPercentage;
		@JsonProperty("ANNUAL_INTEREST_PAID_ON_ODCC_CURRENTYEAR")
		private String annualInterestPaidOnOdccCurrentyear;
		@JsonProperty("ANNUAL_INTEREST_PAID_ON_ODCC_PREVIOUSYEAR")
		private String annualInterestPaidOnOdccPreviousyear;
		@JsonProperty("PENSION_INCOME")
		private String pensionIncome;
		@JsonProperty("INTEREST_ON_LOAN_TO_BE_ADDED_BACK_CURRENT_YR")
		private String interestOnLoanToBeAddedBackCurrentYr;
		@JsonProperty("INTEREST_ON_LOAN_TO_BE_ADDED_BACK_PREVIOUS_YEAR")
		private String interestOnLoanToBeAddedBackPreviousYear;
		@JsonProperty("LIQUID_ASSESSED_PAT")
		private String liquidAssessedPat;
		@JsonProperty("INTEREST_PAID_ON_OBLIGATED_LOANS_CURRENT_YR")
		private String interestPaidOnObligatedLoansCurrentYr;
		@JsonProperty("INTEREST_PAID_ON_OBLIGATED_LOANS_PREVIOUS_YR")
		private String interestPaidOnObligatedLoansPreviousYr;
		@JsonProperty("TOTAL_OTHER_INCOME")
		private String totalOtherIncome;

		public String getEligibilityMethod() {
			return eligibilityMethod;
		}

		public void setEligibilityMethod(String eligibilityMethod) {
			this.eligibilityMethod = eligibilityMethod;
		}

		public String getGrossMonthlyIncomeNri() {
			return grossMonthlyIncomeNri;
		}

		public void setGrossMonthlyIncomeNri(String grossMonthlyIncomeNri) {
			this.grossMonthlyIncomeNri = grossMonthlyIncomeNri;
		}

		public String getRegisteredRentalIncomePreviousyr() {
			return registeredRentalIncomePreviousyr;
		}

		public void setRegisteredRentalIncomePreviousyr(String registeredRentalIncomePreviousyr) {
			this.registeredRentalIncomePreviousyr = registeredRentalIncomePreviousyr;
		}

		public String getRegisteredRentalIncomeCurrentyr() {
			return registeredRentalIncomeCurrentyr;
		}

		public void setRegisteredRentalIncomeCurrentyr(String registeredRentalIncomeCurrentyr) {
			this.registeredRentalIncomeCurrentyr = registeredRentalIncomeCurrentyr;
		}

		public String getCashProfitPreviousyear() {
			return cashProfitPreviousyear;
		}

		public void setCashProfitPreviousyear(String cashProfitPreviousyear) {
			this.cashProfitPreviousyear = cashProfitPreviousyear;
		}

		public String getCashProfitCurrentyear() {
			return cashProfitCurrentyear;
		}

		public void setCashProfitCurrentyear(String cashProfitCurrentyear) {
			this.cashProfitCurrentyear = cashProfitCurrentyear;
		}

		public String getOtherIncomePreviousyear() {
			return otherIncomePreviousyear;
		}

		public void setOtherIncomePreviousyear(String otherIncomePreviousyear) {
			this.otherIncomePreviousyear = otherIncomePreviousyear;
		}

		public String getOtherIncomeCurrentyear() {
			return otherIncomeCurrentyear;
		}

		public void setOtherIncomeCurrentyear(String otherIncomeCurrentyear) {
			this.otherIncomeCurrentyear = otherIncomeCurrentyear;
		}

		public String getGrossRecieptPreviousyear() {
			return grossRecieptPreviousyear;
		}

		public void setGrossRecieptPreviousyear(String grossRecieptPreviousyear) {
			this.grossRecieptPreviousyear = grossRecieptPreviousyear;
		}

		public String getGrossRecieptCurrentyear() {
			return grossRecieptCurrentyear;
		}

		public void setGrossRecieptCurrentyear(String grossRecieptCurrentyear) {
			this.grossRecieptCurrentyear = grossRecieptCurrentyear;
		}

		public String getGrossProfitPreviousyear() {
			return grossProfitPreviousyear;
		}

		public void setGrossProfitPreviousyear(String grossProfitPreviousyear) {
			this.grossProfitPreviousyear = grossProfitPreviousyear;
		}

		public String getGrossProfitCurrentyear() {
			return grossProfitCurrentyear;
		}

		public void setGrossProfitCurrentyear(String grossProfitCurrentyear) {
			this.grossProfitCurrentyear = grossProfitCurrentyear;
		}

		public String getIndustryTurnoverPreviousyr() {
			return industryTurnoverPreviousyr;
		}

		public void setIndustryTurnoverPreviousyr(String industryTurnoverPreviousyr) {
			this.industryTurnoverPreviousyr = industryTurnoverPreviousyr;
		}

		public String getIndustryTurnoverCurrentyr() {
			return industryTurnoverCurrentyr;
		}

		public void setIndustryTurnoverCurrentyr(String industryTurnoverCurrentyr) {
			this.industryTurnoverCurrentyr = industryTurnoverCurrentyr;
		}

		public String getDebtEquityRatio() {
			return debtEquityRatio;
		}

		public void setDebtEquityRatio(String debtEquityRatio) {
			this.debtEquityRatio = debtEquityRatio;
		}

		public String getNetAnnualTurover() {
			return netAnnualTurover;
		}

		public void setNetAnnualTurover(String netAnnualTurover) {
			this.netAnnualTurover = netAnnualTurover;
		}

		public String getAnnualizedBankingTurnover() {
			return annualizedBankingTurnover;
		}

		public void setAnnualizedBankingTurnover(String annualizedBankingTurnover) {
			this.annualizedBankingTurnover = annualizedBankingTurnover;
		}

		public String getPatPreviousYear() {
			return patPreviousYear;
		}

		public void setPatPreviousYear(String patPreviousYear) {
			this.patPreviousYear = patPreviousYear;
		}

		public String getPatCurrentYear() {
			return patCurrentYear;
		}

		public void setPatCurrentYear(String patCurrentYear) {
			this.patCurrentYear = patCurrentYear;
		}

		public String getPbtPreviousYear() {
			return pbtPreviousYear;
		}

		public void setPbtPreviousYear(String pbtPreviousYear) {
			this.pbtPreviousYear = pbtPreviousYear;
		}

		public String getPbtCurrentYear() {
			return pbtCurrentYear;
		}

		public void setPbtCurrentYear(String pbtCurrentYear) {
			this.pbtCurrentYear = pbtCurrentYear;
		}

		public String getDepreciationPreviousyear() {
			return depreciationPreviousyear;
		}

		public void setDepreciationPreviousyear(String depreciationPreviousyear) {
			this.depreciationPreviousyear = depreciationPreviousyear;
		}

		public String getDepreciationCurrentyear() {
			return depreciationCurrentyear;
		}

		public void setDepreciationCurrentyear(String depreciationCurrentyear) {
			this.depreciationCurrentyear = depreciationCurrentyear;
		}

		public String getInterestPaidPartnerDirectorPreviousyear() {
			return interestPaidPartnerDirectorPreviousyear;
		}

		public void setInterestPaidPartnerDirectorPreviousyear(String interestPaidPartnerDirectorPreviousyear) {
			this.interestPaidPartnerDirectorPreviousyear = interestPaidPartnerDirectorPreviousyear;
		}

		public String getInterestPaidPartnerDirectorCurrentyear() {
			return interestPaidPartnerDirectorCurrentyear;
		}

		public void setInterestPaidPartnerDirectorCurrentyear(String interestPaidPartnerDirectorCurrentyear) {
			this.interestPaidPartnerDirectorCurrentyear = interestPaidPartnerDirectorCurrentyear;
		}

		public String getInterestIncomeCurrentyr() {
			return interestIncomeCurrentyr;
		}

		public void setInterestIncomeCurrentyr(String interestIncomeCurrentyr) {
			this.interestIncomeCurrentyr = interestIncomeCurrentyr;
		}

		public String getInterestIncomePreviousyr() {
			return interestIncomePreviousyr;
		}

		public void setInterestIncomePreviousyr(String interestIncomePreviousyr) {
			this.interestIncomePreviousyr = interestIncomePreviousyr;
		}

		public String getCaAssessedIncome() {
			return caAssessedIncome;
		}

		public void setCaAssessedIncome(String caAssessedIncome) {
			this.caAssessedIncome = caAssessedIncome;
		}

		public String getSalaryPartnerDirectorCurrentyear() {
			return salaryPartnerDirectorCurrentyear;
		}

		public void setSalaryPartnerDirectorCurrentyear(String salaryPartnerDirectorCurrentyear) {
			this.salaryPartnerDirectorCurrentyear = salaryPartnerDirectorCurrentyear;
		}

		public String getSalaryPartnerDirectorPreviousyear() {
			return salaryPartnerDirectorPreviousyear;
		}

		public void setSalaryPartnerDirectorPreviousyear(String salaryPartnerDirectorPreviousyear) {
			this.salaryPartnerDirectorPreviousyear = salaryPartnerDirectorPreviousyear;
		}

		public String getInterestPaidOnTermLoanCurrentyear() {
			return interestPaidOnTermLoanCurrentyear;
		}

		public void setInterestPaidOnTermLoanCurrentyear(String interestPaidOnTermLoanCurrentyear) {
			this.interestPaidOnTermLoanCurrentyear = interestPaidOnTermLoanCurrentyear;
		}

		public String getInterestPaidOnTermLoanPreviousyear() {
			return interestPaidOnTermLoanPreviousyear;
		}

		public void setInterestPaidOnTermLoanPreviousyear(String interestPaidOnTermLoanPreviousyear) {
			this.interestPaidOnTermLoanPreviousyear = interestPaidOnTermLoanPreviousyear;
		}

		public String getOtherPaymentsCurrentyear() {
			return otherPaymentsCurrentyear;
		}

		public void setOtherPaymentsCurrentyear(String otherPaymentsCurrentyear) {
			this.otherPaymentsCurrentyear = otherPaymentsCurrentyear;
		}

		public String getOtherPaymentsPreviousyear() {
			return otherPaymentsPreviousyear;
		}

		public void setOtherPaymentsPreviousyear(String otherPaymentsPreviousyear) {
			this.otherPaymentsPreviousyear = otherPaymentsPreviousyear;
		}

		public String getItrPreviousYearFilingDate() {
			return itrPreviousYearFilingDate;
		}

		public void setItrPreviousYearFilingDate(String itrPreviousYearFilingDate) {
			this.itrPreviousYearFilingDate = itrPreviousYearFilingDate;
		}

		public String getItrCurrentYearFilingDate() {
			return itrCurrentYearFilingDate;
		}

		public void setItrCurrentYearFilingDate(String itrCurrentYearFilingDate) {
			this.itrCurrentYearFilingDate = itrCurrentYearFilingDate;
		}

		public String getInterestFdCurrentyear() {
			return interestFdCurrentyear;
		}

		public void setInterestFdCurrentyear(String interestFdCurrentyear) {
			this.interestFdCurrentyear = interestFdCurrentyear;
		}

		public String getInterestFdPreviousyear() {
			return interestFdPreviousyear;
		}

		public void setInterestFdPreviousyear(String interestFdPreviousyear) {
			this.interestFdPreviousyear = interestFdPreviousyear;
		}

		public String getInterestCommisionCurrentyr() {
			return interestCommisionCurrentyr;
		}

		public void setInterestCommisionCurrentyr(String interestCommisionCurrentyr) {
			this.interestCommisionCurrentyr = interestCommisionCurrentyr;
		}

		public String getInterestCommisionPreviousyr() {
			return interestCommisionPreviousyr;
		}

		public void setInterestCommisionPreviousyr(String interestCommisionPreviousyr) {
			this.interestCommisionPreviousyr = interestCommisionPreviousyr;
		}

		public String getTotalRevenueCurrentyear() {
			return totalRevenueCurrentyear;
		}

		public void setTotalRevenueCurrentyear(String totalRevenueCurrentyear) {
			this.totalRevenueCurrentyear = totalRevenueCurrentyear;
		}

		public String getTotalRevenuePreviousyear() {
			return totalRevenuePreviousyear;
		}

		public void setTotalRevenuePreviousyear(String totalRevenuePreviousyear) {
			this.totalRevenuePreviousyear = totalRevenuePreviousyear;
		}

		public String getBasicPay() {
			return basicPay;
		}

		public void setBasicPay(String basicPay) {
			this.basicPay = basicPay;
		}

		public String getHra() {
			return hra;
		}

		public void setHra(String hra) {
			this.hra = hra;
		}

		public String getSpecialAllowance() {
			return specialAllowance;
		}

		public void setSpecialAllowance(String specialAllowance) {
			this.specialAllowance = specialAllowance;
		}

		public String getOthersFixedPay() {
			return othersFixedPay;
		}

		public void setOthersFixedPay(String othersFixedPay) {
			this.othersFixedPay = othersFixedPay;
		}

		public String getMonthlyVariablePay() {
			return monthlyVariablePay;
		}

		public void setMonthlyVariablePay(String monthlyVariablePay) {
			this.monthlyVariablePay = monthlyVariablePay;
		}

		public String getQaurtelyVariablePay() {
			return qaurtelyVariablePay;
		}

		public void setQaurtelyVariablePay(String qaurtelyVariablePay) {
			this.qaurtelyVariablePay = qaurtelyVariablePay;
		}

		public String getAnnualVariablePay() {
			return annualVariablePay;
		}

		public void setAnnualVariablePay(String annualVariablePay) {
			this.annualVariablePay = annualVariablePay;
		}

		public String getPercentageConsideredMonthlyVariablePayIncentive() {
			return percentageConsideredMonthlyVariablePayIncentive;
		}

		public void setPercentageConsideredMonthlyVariablePayIncentive(
				String percentageConsideredMonthlyVariablePayIncentive) {
			this.percentageConsideredMonthlyVariablePayIncentive = percentageConsideredMonthlyVariablePayIncentive;
		}

		public String getPercentageConsideredQuarterlyVariablePayIncentive() {
			return percentageConsideredQuarterlyVariablePayIncentive;
		}

		public void setPercentageConsideredQuarterlyVariablePayIncentive(
				String percentageConsideredQuarterlyVariablePayIncentive) {
			this.percentageConsideredQuarterlyVariablePayIncentive = percentageConsideredQuarterlyVariablePayIncentive;
		}

		public String getPercentageConsideredAnnualVariablePayIncentive() {
			return percentageConsideredAnnualVariablePayIncentive;
		}

		public void setPercentageConsideredAnnualVariablePayIncentive(
				String percentageConsideredAnnualVariablePayIncentive) {
			this.percentageConsideredAnnualVariablePayIncentive = percentageConsideredAnnualVariablePayIncentive;
		}

		public String getTotalFixedPay() {
			return totalFixedPay;
		}

		public void setTotalFixedPay(String totalFixedPay) {
			this.totalFixedPay = totalFixedPay;
		}

		public String getTotalVariablePay() {
			return totalVariablePay;
		}

		public void setTotalVariablePay(String totalVariablePay) {
			this.totalVariablePay = totalVariablePay;
		}

		public String getRentalIncomePerMonth() {
			return rentalIncomePerMonth;
		}

		public void setRentalIncomePerMonth(String rentalIncomePerMonth) {
			this.rentalIncomePerMonth = rentalIncomePerMonth;
		}

		public String getInterestIncomeInItr() {
			return interestIncomeInItr;
		}

		public void setInterestIncomeInItr(String interestIncomeInItr) {
			this.interestIncomeInItr = interestIncomeInItr;
		}

		public String getDividendIncomeInItr() {
			return dividendIncomeInItr;
		}

		public void setDividendIncomeInItr(String dividendIncomeInItr) {
			this.dividendIncomeInItr = dividendIncomeInItr;
		}

		public String getCommisionIncomeInItr() {
			return commisionIncomeInItr;
		}

		public void setCommisionIncomeInItr(String commisionIncomeInItr) {
			this.commisionIncomeInItr = commisionIncomeInItr;
		}

		public String getPercentageConsideredInterestIncomeInItr() {
			return percentageConsideredInterestIncomeInItr;
		}

		public void setPercentageConsideredInterestIncomeInItr(String percentageConsideredInterestIncomeInItr) {
			this.percentageConsideredInterestIncomeInItr = percentageConsideredInterestIncomeInItr;
		}

		public String getPercentageConsideredDividendIncomeInItr() {
			return percentageConsideredDividendIncomeInItr;
		}

		public void setPercentageConsideredDividendIncomeInItr(String percentageConsideredDividendIncomeInItr) {
			this.percentageConsideredDividendIncomeInItr = percentageConsideredDividendIncomeInItr;
		}

		public String getPercentageConsideredCommisionIncomeInItr() {
			return percentageConsideredCommisionIncomeInItr;
		}

		public void setPercentageConsideredCommisionIncomeInItr(String percentageConsideredCommisionIncomeInItr) {
			this.percentageConsideredCommisionIncomeInItr = percentageConsideredCommisionIncomeInItr;
		}

		public String getOtherIncomePerMonth() {
			return otherIncomePerMonth;
		}

		public void setOtherIncomePerMonth(String otherIncomePerMonth) {
			this.otherIncomePerMonth = otherIncomePerMonth;
		}

		public String getCustomerDeclaredIncome() {
			return customerDeclaredIncome;
		}

		public void setCustomerDeclaredIncome(String customerDeclaredIncome) {
			this.customerDeclaredIncome = customerDeclaredIncome;
		}

		public String getPercentageCappingLastYearBusinessIncome() {
			return percentageCappingLastYearBusinessIncome;
		}

		public void setPercentageCappingLastYearBusinessIncome(String percentageCappingLastYearBusinessIncome) {
			this.percentageCappingLastYearBusinessIncome = percentageCappingLastYearBusinessIncome;
		}

		public String getConsiderCurrentYearBusinessIncome() {
			return considerCurrentYearBusinessIncome;
		}

		public void setConsiderCurrentYearBusinessIncome(String considerCurrentYearBusinessIncome) {
			this.considerCurrentYearBusinessIncome = considerCurrentYearBusinessIncome;
		}

		public String getCappingOfAnnualTurnoverGrossProfitMethod() {
			return cappingOfAnnualTurnoverGrossProfitMethod;
		}

		public void setCappingOfAnnualTurnoverGrossProfitMethod(String cappingOfAnnualTurnoverGrossProfitMethod) {
			this.cappingOfAnnualTurnoverGrossProfitMethod = cappingOfAnnualTurnoverGrossProfitMethod;
		}

		public String getMultiplierGrossReceiptMethod() {
			return multiplierGrossReceiptMethod;
		}

		public void setMultiplierGrossReceiptMethod(String multiplierGrossReceiptMethod) {
			this.multiplierGrossReceiptMethod = multiplierGrossReceiptMethod;
		}

		public String getMultiplierLiquidIncomeProgram() {
			return multiplierLiquidIncomeProgram;
		}

		public void setMultiplierLiquidIncomeProgram(String multiplierLiquidIncomeProgram) {
			this.multiplierLiquidIncomeProgram = multiplierLiquidIncomeProgram;
		}

		public String getConsiderConsolidationOfFinancials() {
			return considerConsolidationOfFinancials;
		}

		public void setConsiderConsolidationOfFinancials(String considerConsolidationOfFinancials) {
			this.considerConsolidationOfFinancials = considerConsolidationOfFinancials;
		}

		public String getMultiplierIndustryMargin() {
			return multiplierIndustryMargin;
		}

		public void setMultiplierIndustryMargin(String multiplierIndustryMargin) {
			this.multiplierIndustryMargin = multiplierIndustryMargin;
		}

		public String getOtherIncomeCappingPercentage() {
			return otherIncomeCappingPercentage;
		}

		public void setOtherIncomeCappingPercentage(String otherIncomeCappingPercentage) {
			this.otherIncomeCappingPercentage = otherIncomeCappingPercentage;
		}

		public String getAnnualInterestPaidOnOdccCurrentyear() {
			return annualInterestPaidOnOdccCurrentyear;
		}

		public void setAnnualInterestPaidOnOdccCurrentyear(String annualInterestPaidOnOdccCurrentyear) {
			this.annualInterestPaidOnOdccCurrentyear = annualInterestPaidOnOdccCurrentyear;
		}

		public String getAnnualInterestPaidOnOdccPreviousyear() {
			return annualInterestPaidOnOdccPreviousyear;
		}

		public void setAnnualInterestPaidOnOdccPreviousyear(String annualInterestPaidOnOdccPreviousyear) {
			this.annualInterestPaidOnOdccPreviousyear = annualInterestPaidOnOdccPreviousyear;
		}

		public String getPensionIncome() {
			return pensionIncome;
		}

		public void setPensionIncome(String pensionIncome) {
			this.pensionIncome = pensionIncome;
		}

		public String getInterestOnLoanToBeAddedBackCurrentYr() {
			return interestOnLoanToBeAddedBackCurrentYr;
		}

		public void setInterestOnLoanToBeAddedBackCurrentYr(String interestOnLoanToBeAddedBackCurrentYr) {
			this.interestOnLoanToBeAddedBackCurrentYr = interestOnLoanToBeAddedBackCurrentYr;
		}

		public String getInterestOnLoanToBeAddedBackPreviousYear() {
			return interestOnLoanToBeAddedBackPreviousYear;
		}

		public void setInterestOnLoanToBeAddedBackPreviousYear(String interestOnLoanToBeAddedBackPreviousYear) {
			this.interestOnLoanToBeAddedBackPreviousYear = interestOnLoanToBeAddedBackPreviousYear;
		}

		public String getLiquidAssessedPat() {
			return liquidAssessedPat;
		}

		public void setLiquidAssessedPat(String liquidAssessedPat) {
			this.liquidAssessedPat = liquidAssessedPat;
		}

		public String getInterestPaidOnObligatedLoansCurrentYr() {
			return interestPaidOnObligatedLoansCurrentYr;
		}

		public void setInterestPaidOnObligatedLoansCurrentYr(String interestPaidOnObligatedLoansCurrentYr) {
			this.interestPaidOnObligatedLoansCurrentYr = interestPaidOnObligatedLoansCurrentYr;
		}

		public String getInterestPaidOnObligatedLoansPreviousYr() {
			return interestPaidOnObligatedLoansPreviousYr;
		}

		public void setInterestPaidOnObligatedLoansPreviousYr(String interestPaidOnObligatedLoansPreviousYr) {
			this.interestPaidOnObligatedLoansPreviousYr = interestPaidOnObligatedLoansPreviousYr;
		}

		public String getTotalOtherIncome() {
			return totalOtherIncome;
		}

		public void setTotalOtherIncome(String totalOtherIncome) {
			this.totalOtherIncome = totalOtherIncome;
		}

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Business implements Serializable {

		private static final long serialVersionUID = 1L;
		@JsonProperty("EMPLOYER_TYPE")
		private String employerType;
		@JsonProperty("INDUSTRY_TYPE")
		private String industryType;
		@JsonProperty("INDUSTRY_NAME")
		private String industryName;
		@JsonProperty("BUSINESS_TYPE")
		private String businessType;
		@JsonProperty("DATE_OF_INCORPORATION")
		private String dateOfIncorporation;
		@JsonProperty("BUSINESS_CONTINUTIY")
		private String businessContinutiy;
		@JsonProperty("PROFIT_SHARING_PERCENTAGE")
		private String profitSharingPercentage;
		@JsonProperty("SHARE_HOLDING_PERCENTAGE")
		private String shareHoldingPercentage;
		@JsonProperty("ORGANIZATION_TYPE")
		private String organizationType;
		@JsonProperty("NET_WORTH_PREVIOUS_YEAR")
		private String netWorthPreviousYear;
		@JsonProperty("NET_WORTH_CURRENT_YEAR")
		private String netWorthCurrentYear;
		@JsonProperty("INDUSTRY_MARGIN")
		private String industryMargin;

		@JsonCreator
		public Business() {
		}

		public String getEmployerType() {
			return employerType;
		}

		public void setEmployerType(String employerType) {
			this.employerType = employerType;
		}

		public String getIndustryType() {
			return industryType;
		}

		public void setIndustryType(String industryType) {
			this.industryType = industryType;
		}

		public String getIndustryName() {
			return industryName;
		}

		public void setIndustryName(String industryName) {
			this.industryName = industryName;
		}

		public String getBusinessType() {
			return businessType;
		}

		public void setBusinessType(String businessType) {
			this.businessType = businessType;
		}

		public String getDateOfIncorporation() {
			return dateOfIncorporation;
		}

		public void setDateOfIncorporation(String dateOfIncorporation) {
			this.dateOfIncorporation = dateOfIncorporation;
		}

		public String getBusinessContinutiy() {
			return businessContinutiy;
		}

		public void setBusinessContinutiy(String businessContinutiy) {
			this.businessContinutiy = businessContinutiy;
		}

		public String getProfitSharingPercentage() {
			return profitSharingPercentage;
		}

		public void setProfitSharingPercentage(String profitSharingPercentage) {
			this.profitSharingPercentage = profitSharingPercentage;
		}

		public String getShareHoldingPercentage() {
			return shareHoldingPercentage;
		}

		public void setShareHoldingPercentage(String shareHoldingPercentage) {
			this.shareHoldingPercentage = shareHoldingPercentage;
		}

		public String getOrganizationType() {
			return organizationType;
		}

		public void setOrganizationType(String organizationType) {
			this.organizationType = organizationType;
		}

		public String getNetWorthPreviousYear() {
			return netWorthPreviousYear;
		}

		public void setNetWorthPreviousYear(String netWorthPreviousYear) {
			this.netWorthPreviousYear = netWorthPreviousYear;
		}

		public String getNetWorthCurrentYear() {
			return netWorthCurrentYear;
		}

		public void setNetWorthCurrentYear(String netWorthCurrentYear) {
			this.netWorthCurrentYear = netWorthCurrentYear;
		}

		public String getIndustryMargin() {
			return industryMargin;
		}

		public void setIndustryMargin(String industryMargin) {
			this.industryMargin = industryMargin;
		}

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Eligibility implements Serializable {

		private static final long serialVersionUID = 1L;

		@JsonProperty("APPLIED_TENOR_ADVANTAGE_LOAN")
		private String appliedTenorAdvantageLoan;
		@JsonProperty("APPLIED_ROI")
		private String appliedRoi;
		@JsonProperty("RECOMMENDED_FOIR")
		private String recommendedFoir;
		@JsonProperty("RECOMMENDED_LOAN_AMOUNT")
		private String recommendedLoanAmount;
		@JsonProperty("RECOMMENDED_LOAN_AMOUNT_ADVANTAGE")
		private String recommendedLoanAmountAdvantage;

		@JsonCreator
		public Eligibility() {
		}

		public String getAppliedTenorAdvantageLoan() {
			return appliedTenorAdvantageLoan;
		}

		public void setAppliedTenorAdvantageLoan(String appliedTenorAdvantageLoan) {
			this.appliedTenorAdvantageLoan = appliedTenorAdvantageLoan;
		}

		public String getAppliedRoi() {
			return appliedRoi;
		}

		public void setAppliedRoi(String appliedRoi) {
			this.appliedRoi = appliedRoi;
		}

		public String getRecommendedFoir() {
			return recommendedFoir;
		}

		public void setRecommendedFoir(String recommendedFoir) {
			this.recommendedFoir = recommendedFoir;
		}

		public String getRecommendedLoanAmount() {
			return recommendedLoanAmount;
		}

		public void setRecommendedLoanAmount(String recommendedLoanAmount) {
			this.recommendedLoanAmount = recommendedLoanAmount;
		}

		public String getRecommendedLoanAmountAdvantage() {
			return recommendedLoanAmountAdvantage;
		}

		public void setRecommendedLoanAmountAdvantage(String recommendedLoanAmountAdvantage) {
			this.recommendedLoanAmountAdvantage = recommendedLoanAmountAdvantage;
		}

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class EducationDetail implements Serializable {

		private static final long serialVersionUID = 1L;

		@JsonProperty("QUALIFICATION")
		private String qualification;
		@JsonProperty("EDUCATION_DUMMY1")
		private String educationDummy1;
		@JsonProperty("EDUCATION_DUMMY2")
		private String educationDummy2;
		@JsonProperty("EDUCATION_DUMMY3")
		private String educationDummy3;
		@JsonProperty("EDUCATION_DUMMY4")
		private String educationDummy4;

		@JsonCreator
		public EducationDetail() {
		}

		public String getQualification() {
			return qualification;
		}

		public void setQualification(String qualification) {
			this.qualification = qualification;
		}

		public String getEducationDummy1() {
			return educationDummy1;
		}

		public void setEducationDummy1(String educationDummy1) {
			this.educationDummy1 = educationDummy1;
		}

		public String getEducationDummy2() {
			return educationDummy2;
		}

		public void setEducationDummy2(String educationDummy2) {
			this.educationDummy2 = educationDummy2;
		}

		public String getEducationDummy3() {
			return educationDummy3;
		}

		public void setEducationDummy3(String educationDummy3) {
			this.educationDummy3 = educationDummy3;
		}

		public String getEducationDummy4() {
			return educationDummy4;
		}

		public void setEducationDummy4(String educationDummy4) {
			this.educationDummy4 = educationDummy4;
		}

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Poa implements Serializable {

		private static final long serialVersionUID = 1L;
		@JsonProperty("POA_HOLDER")
		private String poaHolder;
		@JsonProperty("POA_DUMMY1")
		private String poaDummy1;
		@JsonProperty("POA_DUMMY2")
		private String poaDummy2;
		@JsonProperty("POA_DUMMY3")
		private String poaDummy3;
		@JsonProperty("POA_DUMMY4")
		private String poaDummy4;
		@JsonProperty("POA_DUMMY5")
		private String poaDummy5;

		@JsonCreator
		public Poa() {
		}

		public String getPoaHolder() {
			return poaHolder;
		}

		public void setPoaHolder(String poaHolder) {
			this.poaHolder = poaHolder;
		}

		public String getPoaDummy1() {
			return poaDummy1;
		}

		public void setPoaDummy1(String poaDummy1) {
			this.poaDummy1 = poaDummy1;
		}

		public String getPoaDummy2() {
			return poaDummy2;
		}

		public void setPoaDummy2(String poaDummy2) {
			this.poaDummy2 = poaDummy2;
		}

		public String getPoaDummy3() {
			return poaDummy3;
		}

		public void setPoaDummy3(String poaDummy3) {
			this.poaDummy3 = poaDummy3;
		}

		public String getPoaDummy4() {
			return poaDummy4;
		}

		public void setPoaDummy4(String poaDummy4) {
			this.poaDummy4 = poaDummy4;
		}

		public String getPoaDummy5() {
			return poaDummy5;
		}

		public void setPoaDummy5(String poaDummy5) {
			this.poaDummy5 = poaDummy5;
		}

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Bureau implements Serializable {

		private static final long serialVersionUID = 1L;

		@JsonProperty("SCOREDETAIL")
		ScoreDetail scoreDetail;

		@JsonProperty("ACCOUNTDETAIL")
		AccountDetail accountDetail;

		@JsonProperty("ENQUIRYDETAIL")
		EnquiryDetail enquiryDetail;

		@JsonCreator
		public Bureau() {
		}

		public ScoreDetail getScoreDetail() {
			return scoreDetail;
		}

		public void setScoreDetail(ScoreDetail scoreDetail) {
			this.scoreDetail = scoreDetail;
		}

		public AccountDetail getAccountDetail() {
			return accountDetail;
		}

		public void setAccountDetail(AccountDetail accountDetail) {
			this.accountDetail = accountDetail;
		}

		public EnquiryDetail getEnquiryDetail() {
			return enquiryDetail;
		}

		public void setEnquiryDetail(EnquiryDetail enquiryDetail) {
			this.enquiryDetail = enquiryDetail;
		}

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ScoreDetail implements Serializable {

		private static final long serialVersionUID = 1L;

		@JsonProperty("SCORE")
		private String score;
		@JsonProperty("SCORE_DATE")
		private String scoreDate;
		@JsonProperty("CIBIL_ENQUIRY_DATE")
		private String cibilEnquiryDate;

		@JsonCreator
		public ScoreDetail() {
		}

		public String getScore() {
			return score;
		}

		public void setScore(String score) {
			this.score = score;
		}

		public String getScoreDate() {
			return scoreDate;
		}

		public void setScoreDate(String scoreDate) {
			this.scoreDate = scoreDate;
		}

		public String getCibilEnquiryDate() {
			return cibilEnquiryDate;
		}

		public void setCibilEnquiryDate(String cibilEnquiryDate) {
			this.cibilEnquiryDate = cibilEnquiryDate;
		}

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class PropertyMaster implements Serializable {

		private static final long serialVersionUID = 1L;
		@JsonProperty("element")
		List<Element> element;

		public PropertyMaster() {

		}

		public List<Element> getElement() {
			return element;
		}

		public void setElement(List<Element> element) {
			this.element = element;
		}

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Supercalc implements Serializable {

		private static final long serialVersionUID = 1L;
		@JsonProperty("PERC_CHANGE_EMI_FIRST_60")
		private String perc_change_emi_first_60;
		@JsonProperty("PERC_CHANGE_EMI_NEXT_60")
		private String perc_change_emi_next_60;

		@JsonCreator
		public Supercalc() {
		}

		// Getter Methods
		public String getPerc_change_emi_first_60() {
			return perc_change_emi_first_60;
		}

		public String getPerc_change_emi_next_60() {
			return perc_change_emi_next_60;
		}

		// Setter Methods
		public void setPerc_change_emi_first_60(String perc_change_emi_first_60) {
			this.perc_change_emi_first_60 = perc_change_emi_first_60;
		}

		public void setPerc_change_emi_next_60(String perc_change_emi_next_60) {
			this.perc_change_emi_next_60 = perc_change_emi_next_60;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class DV implements Serializable {

		private static final long serialVersionUID = 1L;

		public DV() {
		}

		@JsonProperty("element")
		List<Element> element;

		public List<Element> getElement() {
			return element;
		}

		public void setElement(List<Element> element) {
			this.element = element;
		}

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class FCU implements Serializable {

		private static final long serialVersionUID = 1L;

		public FCU() {
		}

		@JsonProperty("element")
		List<Element> element;

		public List<Element> getElement() {
			return element;
		}

		public void setElement(List<Element> element) {
			this.element = element;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class PD implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@JsonCreator
		public PD() {
		}

		@JsonProperty("element")
		List<Element> element;

		public List<Element> getElement() {
			return element;
		}

		public void setElement(List<Element> element) {
			this.element = element;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class LRD implements Serializable {

		private static final long serialVersionUID = 1L;
		@JsonProperty("PROPERTY_VACANT_SINCE")
		private String property_vacant_since;
		@JsonProperty("PROPERTY_TYPE")
		private String property_type;
		@JsonProperty("DATE_OF_AGREEMENT")
		private String date_of_agreement;
		@JsonProperty("TENURE_OF_LEASE_FROM")
		private String tenure_of_lease_from;
		@JsonProperty("TENURE_OF_LEASE_TO")
		private String tenure_of_lease_to;
		@JsonProperty("NPV")
		private String npv;
		@JsonProperty("LETTER_OF_INTENT")
		private String letter_of_intent;
		@JsonProperty("LETTER_OF_INTENT_1")
		private String letter_of_intent_1;
		@JsonProperty("ESCALATION_FLAG")
		private String escalation_flag;
		@JsonProperty("NET_CURRENT_MONTHLY_RENTALS")
		private String net_current_monthly_rentals;
		@JsonProperty("RESIDUAL_LEASE_PERIOD_IN_MONTHS")
		private String residual_lease_period_in_months;
		@JsonProperty("LEASE_PERIOD_IN_MONTHS")
		private String lease_period_in_months;
		@JsonProperty("LEASE_MARKET_CAPITALISATION")
		private String lease_market_capitalisation;
		@JsonProperty("COST_OF_PROPERTY")
		private String cost_of_property;
		@JsonProperty("APPLICABLE_LTV")
		private String applicable_ltv;

		@JsonCreator
		public LRD() {
		}

		// Getter Methods
		public String getProperty_vacant_since() {
			return property_vacant_since;
		}

		public String getProperty_type() {
			return property_type;
		}

		public String getDate_of_agreement() {
			return date_of_agreement;
		}

		public String getTenure_of_lease_from() {
			return tenure_of_lease_from;
		}

		public String getTenure_of_lease_to() {
			return tenure_of_lease_to;
		}

		public String getNpv() {
			return npv;
		}

		public String getLetter_of_intent() {
			return letter_of_intent;
		}

		public String getLetter_of_intent_1() {
			return letter_of_intent_1;
		}

		public String getEscalation_flag() {
			return escalation_flag;
		}

		public String getNet_current_monthly_rentals() {
			return net_current_monthly_rentals;
		}

		public String getResidual_lease_period_in_months() {
			return residual_lease_period_in_months;
		}

		public String getLease_period_in_months() {
			return lease_period_in_months;
		}

		public String getLease_market_capitalisation() {
			return lease_market_capitalisation;
		}

		public String getCost_of_property() {
			return cost_of_property;
		}

		public String getApplicable_ltv() {
			return applicable_ltv;
		}

		// Setter Methods

		public void setProperty_vacant_since(String property_vacant_since) {
			this.property_vacant_since = property_vacant_since;
		}

		public void setProperty_type(String property_type) {
			this.property_type = property_type;
		}

		public void setDate_of_agreement(String date_of_agreement) {
			this.date_of_agreement = date_of_agreement;
		}

		public void setTenure_of_lease_from(String tenure_of_lease_from) {
			this.tenure_of_lease_from = tenure_of_lease_from;
		}

		public void setTenure_of_lease_to(String tenure_of_lease_to) {
			this.tenure_of_lease_to = tenure_of_lease_to;
		}

		public void setNpv(String npv) {
			this.npv = npv;
		}

		public void setLetter_of_intent(String letter_of_intent) {
			this.letter_of_intent = letter_of_intent;
		}

		public void setLetter_of_intent_1(String letter_of_intent_1) {
			this.letter_of_intent_1 = letter_of_intent_1;
		}

		public void setEscalation_flag(String escalation_flag) {
			this.escalation_flag = escalation_flag;
		}

		public void setNet_current_monthly_rentals(String net_current_monthly_rentals) {
			this.net_current_monthly_rentals = net_current_monthly_rentals;
		}

		public void setResidual_lease_period_in_months(String residual_lease_period_in_months) {
			this.residual_lease_period_in_months = residual_lease_period_in_months;
		}

		public void setLease_period_in_months(String lease_period_in_months) {
			this.lease_period_in_months = lease_period_in_months;
		}

		public void setLease_market_capitalisation(String lease_market_capitalisation) {
			this.lease_market_capitalisation = lease_market_capitalisation;
		}

		public void setCost_of_property(String cost_of_property) {
			this.cost_of_property = cost_of_property;
		}

		public void setApplicable_ltv(String applicable_ltv) {
			this.applicable_ltv = applicable_ltv;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class TotalMarketValue implements Serializable {

		private static final long serialVersionUID = 1L;
		ArrayList<String> item;

		public ArrayList<String> getItem() {
			return item;
		}

		public void setItem(ArrayList<String> item) {
			this.item = item;
		}
	}

	@Override
	public String toString() {
		return "BREService [daXMLDocument=" + daXMLDocument + "]";
	}

}
