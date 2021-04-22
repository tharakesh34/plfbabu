package com.pennant.backend.dao.insurance;

import com.pennant.backend.model.insurance.InsuranceDetails;
import com.pennant.backend.model.insurance.InsurancePaymentInstructions;
import com.pennanttech.pff.core.TableType;

public interface InsuranceDetailDAO {

	InsuranceDetails getInsurenceDetailsByRef(String reference, String tableType);

	void updateInsuranceDetails(InsuranceDetails insuranceDetail, String tableType);

	long saveInsuranceDetails(InsuranceDetails insuranceDetail, String tableType);

	InsuranceDetails getInsurenceDetailsById(long id, String tableType);

	boolean isDuplicateKey(long id, String reference, TableType tableType);

	void delete(InsuranceDetails insuranceDetails, String tableType);

	long saveInsurancePayments(InsurancePaymentInstructions paymentHeader, TableType tableType);

	int updatePaymentStatus(InsurancePaymentInstructions instruction);

	long getSeqNumber();

	void updateInsuranceDetails(String reference, String status);

	void updatePaymentLinkedTranId(String vasReference, long linkedTranId);

	void updateLinkTranId(long id, long linkTranId);

	InsurancePaymentInstructions getInsurancePaymentInstructionStatus(long id);

	InsurancePaymentInstructions getInsurancePaymentInstructionById(long id);

}
