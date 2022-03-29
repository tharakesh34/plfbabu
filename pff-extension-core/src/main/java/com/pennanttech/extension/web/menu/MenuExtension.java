package com.pennanttech.extension.web.menu;

import java.util.HashSet;
import java.util.Set;

public class MenuExtension implements IMenuExtension {

	/**
	 * Add all the not required menu items into Set.
	 * 
	 * @return Set of names will be return, When list is empty, new object will return
	 */
	@Override
	public Set<String> getExclude() {
		Set<String> set = new HashSet<>();

		set.add("menu_Item_FinanceReferenceDetail");
		set.add("menu_Item_FieldInvestigation");
		set.add("menu_Item_FieldInvestigation_Enquiry");
		set.add("menu_Item_FieldInvestigation_Initiation");
		set.add("menu_Item_TechnicalVerification_Initiation");
		set.add("menu_Item_LegalVerification_Initiation");
		set.add("menu_Item_TechnicalVerification_Enquiry");
		set.add("menu_Item_RCUInitiation");
		set.add("menu_Item_TechnicalVerification");
		set.add("menu_Item_TechnicalVerification_External");
		set.add("menu_Item_TechnicalVerification_Internal");
		set.add("menu_Item_LegalVerification");
		set.add("menu_Item_RiskContainmentUnit");
		set.add("menu_Item_PersonalDiscussion");
		set.add("menu_Item_ExtendedFieldsConfiguration");
		set.add("menu_Item_DedupParm");
		set.add("menu_Item_BlackListDedupParm");
		set.add("menu_Item_LoanDedupParm");
		set.add("menu_Item_EligibilityRules");
		set.add("menu_Item_ScoringGroup");
		set.add("menu_Item_ScoringMetrics");
		set.add("menu_Item_VerificationRule");
		set.add("menu_Item_StepPolicy");
		set.add("menu_Item_Promotion");
		set.add("menu_Item_PromotionWorkFlow");
		set.add("menu_Item_PromotionReferenceDetail");
		set.add("menu_Item_PromotionServiceWorkFlow");
		set.add("menu_Item_PromotionReferenceService");
		set.add("menu_Item_AssetType");
		set.add("menu_Item_VASProductCategory");
		set.add("menu_Item_VASProductType");
		set.add("menu_Item_VASConfiguration");
		set.add("menu_Item_VASWorkFlow");
		set.add("menu_Item_VASReferenceDetail");
		set.add("menu_Item_VASRecording");
		set.add("menu_Item_VASCancel");
		set.add("menu_Item_CorpCreditAppReview");
		set.add("menu_Item_CreditApplicationReviewMaintinence");
		set.add("menu_Item_LimitRebuild");
		set.add("menu_Item_InstitutionLimitRebuild");
		set.add("menu_Item_LegalVetting_Initiation");
		set.add("menu_Item_LegalVettingVerification");
		set.add("menu_Item_PMAY");
		set.add("menu_Item_PmayEnquiry");
		set.add("menu_Item_PMAYDetails");
		set.add("menu_Item_LinkingDelinking");
		set.add("menu_Item_NewFinanceMain");
		set.add("menu_Item_FinanceDeviations");
		set.add("menu_Item_ReinstateFinance");
		set.add("menu_Item_LegalDetail");
		set.add("menu_Item_QueryDetail");
		set.add("menu_Item_QueueAssignment");
		set.add("menu_Item_FinChangeCustomer");
		set.add("menu_Item_BuilderGroup");
		set.add("menu_Item_BuilderCompany");
		set.add("menu_Item_BuilderCompanyEnquiry");
		set.add("menu_Item_BuilderProjcet");
		set.add("menu_Item_BuilderProjcetEnquiry");
		set.add("menu_Item_VASProviderAccDetail");
		set.add("menu_Item_Dealer");
		set.add("menu_Item_Manufacturer");
		set.add("menu_Item_QueryCategory");
		set.add("menu_Item_Field_Investigation");
		set.add("menu_Item_Legal_Verification");
		set.add("menu_Item_RCU_Verification");
		set.add("menu_Item_Technical_Verification");
		set.add("menu_Item_Pesonal_Discussion");
		set.add("menu_Item_DirectSellingAgent");
		set.add("menu_Item_DirectMarketAgent");
		set.add("menu_Item_Connector");
		set.add("menu_Item_VehicleDealer");
		set.add("menu_Item_Sampling");
		set.add("menu_Item_Sampling_Enquiry");
		set.add("menu_Item_InterfaceConfiguration");
		set.add("menu_Item_InterfaceConfigurationEnquiry");

		return set;
	}

	/**
	 * Add all the required menu items into Set.
	 * 
	 * @return Set of names will be return, When list is empty, new object will return
	 */
	@Override
	public Set<String> getInclude() {
		return new HashSet<>();
	}

}
