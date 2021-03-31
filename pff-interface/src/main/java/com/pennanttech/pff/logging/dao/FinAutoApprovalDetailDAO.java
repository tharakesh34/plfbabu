package com.pennanttech.pff.logging.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.finance.FinAutoApprovalDetails;
import com.pennant.backend.model.rmtmasters.FinanceType;

public interface FinAutoApprovalDetailDAO {

	void logFinAutoApprovalDetails(List<FinAutoApprovalDetails> autoAppList);

	List<FinAutoApprovalDetails> getUploadedDisbursementsWithBatchId(long batchId);

	Map<String, Integer> loadQDPValidityDays();

	void updateFinAutoApprovals(FinAutoApprovalDetails finAutoApprovalDetails);

	void deleteNonQDPRecords(List<FinAutoApprovalDetails> nonQDPList);

	boolean getFinanceIfApproved(String finReference);

	int getProcessingCount(Date appDate);

	boolean getFinanceServiceInstruction(String finReference);

	boolean isFinQdpIsInProgress(String finReference);

	boolean CheckDisbForQDP(String finReference);

	FinanceType getQDPflagByFinref(String finReference);

	boolean getAutoApprovalFlag(String finType);

	List<FinAutoApprovalDetails> getUploadedDisbursementsFinRefBatchId(long batchId);
	
	boolean isQDPCase(String finReference, String type);

}
