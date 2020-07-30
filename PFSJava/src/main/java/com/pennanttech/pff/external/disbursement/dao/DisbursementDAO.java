package com.pennanttech.pff.external.disbursement.dao;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.DisbursementDetails;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennanttech.pff.core.disbursement.model.DisbursementRequest;

public interface DisbursementDAO {

	long getNextBatchId();

	int lockFinAdvancePayments(long headerId, long userId, Long[] result);

	int clearBatch(long headerId);

	List<DisbursementRequest> logDisbursementBatch(DisbursementRequest disbursementRequestData);

	int deleteDisbursementBatch(long headerId, long batchId);

	int deleteDisbursementBatch(long headerId);

	List<FinAdvancePayments> getAdvancePayments(long headerId);

	int updateBatchStatus(DisbursementRequest req);

	void logDisbursementMovement(DisbursementRequest request, boolean log);

	List<Long> getMovementList();

	void lockMovement(long requestId);

	DisbursementRequest getMovementRequest(long requestId);

	void updateMovement(DisbursementRequest request, int processFlag);

	void deleteMovement(long requestId);

	void updateMovement(long requestId, int processFlag, String failureReason);

	void updateBatchFailureStatus(DisbursementRequest req);

	int updateRespBatch(DisbursementDetails detail, long respBatchId);

	String isDisbursementExist(DisbursementDetails detail);

	List<FinAdvancePayments> getAutoDisbInstructions(Date llDate);

}
