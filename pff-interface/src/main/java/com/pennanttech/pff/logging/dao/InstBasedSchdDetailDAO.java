package com.pennanttech.pff.logging.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.InstBasedSchdDetails;

public interface InstBasedSchdDetailDAO {

	List<InstBasedSchdDetails> getUploadedDisbursementsWithBatchId(long batchId);

	Map<String, Integer> loadQDPValidityDays();

	void updateFinAutoApprovals(InstBasedSchdDetails InstBasedSchdDetails);

	boolean getFinanceIfApproved(String finReference);

	int getProcessingCount(Date appDate);

	boolean getFinanceServiceInstruction(String finReference);

	boolean CheckDisbForQDP(String finReference);

	boolean getAutoApprovalFlag(String finType);

	List<InstBasedSchdDetails> getUploadedDisbursementsFinRefBatchId(long batchId);

	String getDisbType(String id);

	FinAdvancePayments getPaymentInstructionDetails(String id);

	boolean isPaymentDateExist(String finReference, int disbSeq, Date paymentDate);

	FinAdvancePayments getFinAdvancePaymentDetails(String id);

	void saveInstBasedSchdDetails(List<InstBasedSchdDetails> instBasedSchdList);

	boolean checkReBuildSchd(long paymentId);

	boolean checkInstBasedSchd(String finReference);

	boolean isDisbRecordProceed(long paymentId);

}
