package com.pennanttech.extension.api;

import java.util.HashSet;
import java.util.Set;

public class ExtensionServices implements IExtensionServices {

	/**
	 * Add all the client specific API services into Set.
	 * 
	 * @return Set of names will be return, When list is empty, new object will return
	 */
	@Override
	public Set<String> getExtended() {
		Set<String> services = new HashSet<>();

		services.add("updateDisbursementInstructionStatus");
		services.add("getDisbursementInstructions");
		services.add("downloadDisbursementInstructions");
		services.add("getManualDeviationAuthorities");
		services.add("deleteBeneficiary");
		services.add("getBeneficiaries");
		services.add("getBeneficiary");
		services.add("getCollaterals");
		services.add("getCollateralType");
		services.add("getLoansStatusEnquiry");
		services.add("updateLoan");
		services.add("getAgreements");
		services.add("cancelLoan");
		services.add("getLoansStatus");
		services.add("getLoanDeviations");
		services.add("financeDedup");
		services.add("UpdateLoanDeviation");
		services.add("moveLoanStage");
		services.add("reinitiateLoan");
		services.add("rejectLoan");
		services.add("getLoansByStage");
		services.add("approveLoan");
		services.add("getFinanceWithCollateral");
		services.add("getActivityLogs");
		services.add("getUserActions");
		services.add("getFinance");
		services.add("getFinanceDetails");
		services.add("getFinanceWithCustomer");
		services.add("getDeviations");
		services.add("getPendingFinanceWithCustomer");
		services.add("deleteCustomerGstInformation");
		services.add("getCustomerAgreement");
		services.add("addCustomerGstInformation");
		services.add("updateCustomerGstInformation");
		services.add("dedupCustomer");
		services.add("getSRMCustDetails");
		services.add("addCardSalesInformation");
		services.add("deleteCardSaleInformation");
		services.add("addCreditReviewDetails");
		services.add("updatecustCardSalesInformation");
		services.add("getNegativeListCustomer");
		services.add("getCustDedup");
		services.add("addCustomerExtendedFieldDetails");
		services.add("doCustomerValidation");
		services.add("getCustomerDirectorDetails");
		services.add("getCustomerBankingInformation");
		services.add("deleteCustomer");
		services.add("getCustomerExternalLiabilities");
		services.add("getCustomerAccountBehaviour");
		services.add("getCustomerDocuments");
		services.add("getCustomerPhoneNumbers");
		services.add("getCardSalesInformation");
		services.add("getCustomerPersonalInfo");
		services.add("getCustomerEmails");
		services.add("getCustomerGstInformation");
		services.add("getCustomerAddresses");
		services.add("getCustomerIncomes");
		services.add("createDealer");
		services.add("getDealer");
		services.add("addDocument");
		services.add("getFinDocument");
		services.add("getDocuments");
		services.add("getExtendedFieldDetail");
		services.add("createAdvise");
		services.add("feePosting");
		services.add("getLoanFlags");
		services.add("getLoanInquiry");
		services.add("deleteFinCovenant");
		services.add("addFinCovenant");
		services.add("updateFinCovenant");
		services.add("getFinCovenants");
		services.add("getManualDeviationList");
		services.add("getReasonCodeDetails");
		services.add("defermentsRequest");
		services.add("approveDisbursementResponse");
		services.add("updateChequeDetailsInMaintainence");
		services.add("addGSTDetails");
		services.add("chequeDetailsMaintainence");
		services.add("updateGSTDetails");
		services.add("subventionKnockOff");
		services.add("getCovenantAggrement");
		services.add("reScheduling");
		services.add("updateCovenants");
		services.add("processFeeWaiver");
		services.add("createChequeDetails");
		services.add("changeGestationPeriod");
		services.add("updateChequeDetails");
		services.add("cancelDisbursementInstructions");
		services.add("restructuring");
		services.add("scheduleMethodChange");
		services.add("feePayment");
		services.add("getLoanPendingDetailsByUserName");
		services.add("getGSTDetails");
		services.add("getChequeDetails");
		services.add("getDisbursmentDetails");
		services.add("getCovenantDocumentStatus");
		services.add("getForeclosureStmtV1");
		services.add("blockLimit");
		services.add("unBlockLimit");
		services.add("getCustomerLimitStructure");
		services.add("userValidate");
		services.add("getUserRoles");
		services.add("updateMandateStatus");
		services.add("approveMandate");
		services.add("getMandate");
		services.add("deleteMandate");
		services.add("getMandates");
		services.add("getEligibility");
		services.add("createDashboard");
		services.add("getCovenants");
		services.add("getApprovedPresentment");
		services.add("extractPresentmentDetails");
		services.add("approvePresentmentDetails");
		services.add("uploadPresentment");
		services.add("getPresentmentStatus");
		services.add("getLoanTypes");
		services.add("getPromotions");
		services.add("createRefund");
		services.add("getRemarks");
		services.add("getSecRoles");
		services.add("getStepPolicy");
		services.add("getSystemDate");
		services.add("doAuthentication");
		services.add("mobileAuthentication");
		services.add("getVASProduct");
		services.add("createWorkFlow");
		services.add("updateWorkFlow");
		services.add("getDetailsByOfferID");
		services.add("updatePerfiosStatus");
		services.add("partCancellation");
		services.add("nonLanReceipt");
		services.add("getEMIAmount");
		services.add("pushLeadsForDMS");
		services.add("getScore");
		services.add("checkEligibility");
		services.add("calculateEligibility");
		services.add("getProductOffers");
		services.add("updateRelationshipOfficer");
		services.add("createRelationshipOfficer");
		services.add("initiateRCUVerification");
		services.add("initiateFIVerification");
		services.add("recordTVVerification");
		services.add("initiateLVVerification");
		services.add("recordPDVerification");
		services.add("getVerificationDetails");
		services.add("initiatePDVerification");
		services.add("recordRCUVerification");
		services.add("recordLVVerification");
		services.add("recordFIVerification");
		services.add("getVerificationIds");
		services.add("initiateTVVerification");

		return services;
	}

	/**
	 * Add not required API services to particular client.
	 * 
	 * Added names will removed with the help of returned object.
	 * 
	 * @return Set of names will be return, When list is empty, new object will return
	 */
	@Override
	public Set<String> getFilter() {
		return new HashSet<>();
	}

	/**
	 * Add whatever the API services required for particular client.
	 * 
	 * This method will override the existing Services.
	 * 
	 * @return Set of names will be return, When list is empty, new object will return
	 */
	@Override
	public Set<String> getOverride() {
		return new HashSet<>();
	}

}
