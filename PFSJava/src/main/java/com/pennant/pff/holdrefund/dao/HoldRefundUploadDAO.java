package com.pennant.pff.holdrefund.dao;

import java.util.Date;
import java.util.List;

import com.pennant.pff.holdrefund.model.FinanceHoldDetail;
import com.pennant.pff.holdrefund.model.HoldRefundUploadDetail;
import com.pennant.pff.upload.model.FileUploadHeader;

public interface HoldRefundUploadDAO {
	List<HoldRefundUploadDetail> getDetails(long headerID);

	List<String> isDuplicateExists(String reference, Date dueDate, long headerID);

	void update(List<HoldRefundUploadDetail> detailsList);

	void update(List<Long> headerIds, String errorCode, String errorDesc, int progress);

	String getSqlQuery();

	long save(HoldRefundUploadDetail detail);

	long saveLog(HoldRefundUploadDetail detail, FileUploadHeader header);

	String getHoldRefundStatus(long finId);

	String getStatus(String finReference);

	int updateFinHoldDetail(HoldRefundUploadDetail detail);

	boolean isFinIDExists(long finId);

	FinanceHoldDetail getFinanceHoldDetails(long finID, String type, boolean isWIF);

	void releaseHoldOnLoans(Date closureDate);

	boolean isInProgress(String finReference, long headerID);
}
