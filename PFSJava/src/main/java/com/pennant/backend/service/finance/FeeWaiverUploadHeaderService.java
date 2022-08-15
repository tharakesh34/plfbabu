package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.expenses.FeeWaiverUploadHeader;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FeeWaiverUpload;

public interface FeeWaiverUploadHeaderService {

	boolean isFileNameExist(String fileName);

	FeeType getApprovedFeeTypeByFeeCode(String finTypeCode);

	FeeWaiverUploadHeader getUploadHeader();

	AuditHeader saveOrUpdate(AuditHeader auditHeader) throws Exception;

	AuditHeader doApprove(AuditHeader auditHeader) throws Exception;

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	boolean isFileDownload(long uploadID, String tableType);

	FeeWaiverUploadHeader getUploadHeaderById(long uploadId, String type);

	List<FeeWaiverUpload> getFeeWaiverListByUploadId(long uploadId);

	void updateFileDownload(long uploadId, boolean fileDownload, String type);

	String getFinanceMainByRcdMaintenance(long finID);
}
