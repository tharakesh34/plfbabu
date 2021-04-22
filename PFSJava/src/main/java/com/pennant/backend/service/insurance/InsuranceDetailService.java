package com.pennant.backend.service.insurance;

import java.util.List;

import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.configuration.VasCustomer;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.insurance.InsuranceDetails;
import com.pennant.backend.model.insurance.InsurancePaymentInstructions;
import com.pennant.backend.model.systemmasters.VASProviderAccDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public interface InsuranceDetailService {

	VASRecording getVASRecordingByRef(String vasReference);

	InsuranceDetails getInsurenceDetailsByRef(String reference, String tableType);

	VASProviderAccDetail getVASProviderAccDetByPRoviderId(long providerId, String entityCode, String tableType);

	VASProviderAccDetail getVASProviderAccDetByPRoviderId(long providerId, String tableType);

	void updateVasStatus(String status, String vasReference);

	void updateInsuranceDetails(InsuranceDetails insuranceDetail, String tableType);

	void saveInsuranceDetails(InsuranceDetails insuranceDetail, String tableType);

	InsuranceDetails getInsurenceDetailsById(long id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader) throws Exception;

	AuditHeader doReject(AuditHeader auditHeader);

	VasCustomer getVasCustomerDetails(String finReference, String postingAgainst);

	void saveInsurancePayments(InsurancePaymentInstructions paymentHeader);

	BankBranch getBankBranchById(long bankBranchID, String tableType);

	long executeInsPartnerAccountingProcess(InsuranceDetails details, VASRecording vASRecording) throws Exception;

	VehicleDealer getProviderDetails(long providerId, String tableType);

	int updatePaymentStatus(InsurancePaymentInstructions instruction);

	VASRecording getVASRecording(String vasReference, String vasStatus);

	List<ManualAdvise> getManualAdviseByRefAndFeeId(int manualAdvisePayable, long feeTypeId);

	VASConfiguration getVASConfigurationByCode(String productCode);

	void doApproveVASInsurance(List<VASRecording> vasRecording, LoggedInUser loggedInUser, FinanceDetail financeDetail);

	void executeVasPaymentsAccountingProcess(InsurancePaymentInstructions insurancePaymentInstructions);

}
