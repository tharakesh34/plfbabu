package com.pennanttech.extension.api;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.pennanttech.pennapps.core.FactoryException;

@Component
public class APIServices {
	private static Set<String> services = new HashSet<>();

	@PostConstruct
	public void initialize() {

		IExtensionServices extension = getExtension();

		services.addAll(getDefaultServices());
		services.addAll(extension.getExtended());

		extension.getFilter().forEach(filter -> services.remove(filter));

		Set<String> overrideServices = extension.getOverride();

		if (!overrideServices.isEmpty()) {
			services.clear();

			services.addAll(overrideServices);
		}

	}

	public static boolean isAllowed(String serviceName) {
		return services.contains(serviceName);
	}

	private static IExtensionServices getExtension() {
		IExtensionServices extensionServices;
		String exception = "The IExtensionServices implimentation should be com.pennanttech.extension.api.ExtensionServicese available in the client exetension layer to override the API services list.";
		try {
			Object object = Class.forName("com.pennanttech.extension.api.ExtensionServices").getDeclaredConstructor()
					.newInstance();
			if (object != null) {
				extensionServices = (IExtensionServices) object;
				return extensionServices;
			} else {
				throw new FactoryException(exception);
			}
		} catch (Exception e) {
			throw new FactoryException(exception);

		}
	}

	public Set<String> getDefaultServices() {
		Set<String> services = new HashSet<>();

		services.add("createBeneficiary");
		services.add("updateBeneficiary");
		services.add("deleteBeneficiary");
		services.add("getBeneficiaries");
		services.add("getBeneficiary");
		services.add("updateCollateral");
		services.add("deleteCollateral");
		services.add("createCollateral");
		services.add("getCollaterals");
		services.add("getCollateralType");
		services.add("createFinance");
		services.add("createFinanceWithWIF");
		services.add("getFinanceWithCollateral");
		services.add("getFinance");
		services.add("getFinanceDetails");
		services.add("getFinanceWithCustomer");
		services.add("getPendingFinanceWithCustomer");
		services.add("updateCustomerBankingInformation");
		services.add("deleteCustomerAccountBehaviour");
		services.add("addCustomerEmployment");
		services.add("addCustomerPhoneNumber");
		services.add("createCustomer");
		services.add("updateCustomerPhoneNumber");
		services.add("addCustomerAddress");
		services.add("deleteCustomerDocument");
		services.add("addCustomerAccountBehaviour");
		services.add("updateCustomerDirectorDetail");
		services.add("updateCustomerExternalLiability");
		services.add("deleteCustomerEmployment");
		services.add("addCustomerIncome");
		services.add("addCustomerDocument");
		services.add("deleteCustomerAddress");
		services.add("deleteCustomerEmail");
		services.add("deleteCustomerBankingInformation");
		services.add("updateCustomerAddress");
		services.add("deleteCustomerExternalLiability");
		services.add("updateCustomerEmployment");
		services.add("updateCustomerEmail");
		services.add("updateCustomerIncome");
		services.add("updateCustomer");
		services.add("addCustomerExternalLiability");
		services.add("updateCustomerDocument");
		services.add("updateCustomerPersonalInfo");
		services.add("addCustomerEmail");
		services.add("addCustomerDirectorDetail");
		services.add("deleteCustomerDirectorDetail");
		services.add("addCustomerBankingInformation");
		services.add("deleteCustomerPhoneNumber");
		services.add("addCustomerExtendedFieldDetails");
		services.add("deleteCustomerIncome");
		services.add("updateCustomerAccountBehaviour");
		services.add("getCustomerDirectorDetails");
		services.add("getCustomerBankingInformation");
		services.add("deleteCustomer");
		services.add("getCustomerExternalLiabilities");
		services.add("getCustomerAccountBehaviour");
		services.add("getCustomerDocuments");
		services.add("getCustomerPhoneNumbers");
		services.add("getCustomerPersonalInfo");
		services.add("getCustomerEmails");
		services.add("getCustomerAddresses");
		services.add("getCustomerIncomes");
		services.add("getDocuments");
		services.add("getExtendedFieldDetail");
		services.add("addLoanFlags");
		services.add("deleteLoanFlags");
		services.add("getLoanFlags");
		services.add("createLoanSchedule");
		services.add("getLoanInquiry");
		services.add("getFinanceType");
		services.add("updateLoanPenaltyDetails");
		services.add("partialSettlement");
		services.add("addDisbursement");
		services.add("addRateChangeRequest");
		services.add("changeInterest");
		services.add("addTermsRequest");
		services.add("removeTermsRequest");
		services.add("recalculate");
		services.add("earlySettlement");
		services.add("updateLoanBasicDetails");
		services.add("feePayment");
		services.add("changeRepaymentAmountRequest");
		services.add("changeInstallmentFrq");
		services.add("manualPayment");
		services.add("getStatement");
		services.add("getForeclosureStmt");
		services.add("getStatementOfAcc");
		services.add("getStatementofAccount");
		services.add("getForeclosureLetter");
		services.add("getRepaymentSchedule");
		services.add("getNOC");
		services.add("getInterestCerificate");
		services.add("updateLimitSetup");
		services.add("reserveLimit");
		services.add("createLimitSetup");
		services.add("getLimitSetup");
		services.add("cancelLimitReserve");
		services.add("getCustomerLimitStructure");
		services.add("loanMandateSwapping");
		services.add("createMandate");
		services.add("updateMandate");
		services.add("getMandate");
		services.add("deleteMandate");
		services.add("getMandates");
		services.add("AddFinanceJVPostings");
		services.add("getLoanTypes");
		services.add("getPromotion");
		services.add("getPromotions");
		services.add("updateQueryRequest");
		services.add("createRemarks");
		services.add("getRemarks");
		services.add("getSecRoles");
		services.add("getStepPolicy");
		services.add("pendingRecordVAS");
		services.add("getRecordVAS");
		services.add("cancelVAS");
		services.add("getVASRecordings");
		services.add("recordVAS");
		services.add("getVASProduct");
		services.add("createWorkFlow");
		services.add("updateWorkFlow");
		services.add("updateRelationshipOfficer");
		services.add("createRelationshipOfficer");
		services.add("getWorkFlowDetails");
		services.add("getProcessView");
		services.add("getCustomerDetails");
		services.add("createSecurityUser");
		services.add("updateSecurityUser");
		services.add("addOperation");
		services.add("deleteOperation");
		services.add("enableUser");
		services.add("expireUser");
		services.add("getCustomerEmployment");
		services.add("updateMandateStatus");
		services.add("changeRepaymentMethod");
		services.add("createPaymentInstruction");
		services.add("getPaymentInstruction");
		services.add("updatePaymentInstruction");
		services.add("getFinanceByName");
		services.add("getFinanceByPAN");
		services.add("getFinanceByAccNumber");
		services.add("getFinanceByMobileNumber");
		services.add("getFinanceByNameAndMobileNumber");
		services.add("getFinanceByNameAndDateOfBirth");
		services.add("getFinanceByNameAndEMIAmount");
		services.add("loanAuthentication");
		services.add("getPDCEnquiry");
		services.add("getPDCDetails");
		services.add("getCustomerDetailsByName");
		services.add("getCustomerDetailsByPAN");
		services.add("getCustomerDetailsByAccNumber");
		services.add("getCustomerDetailsByPhoneNumber");
		services.add("getCustomerDetailsByNameAndPhoneNumber");
		services.add("getCustomerDetailsByNameAndDOB");
		services.add("getCustomerDetailsByNameAndEMIAmount");
		services.add("cancelLoan");

		return services;
	}

}
