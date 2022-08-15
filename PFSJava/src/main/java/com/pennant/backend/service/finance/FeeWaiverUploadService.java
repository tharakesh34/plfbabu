package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FeeWaiverUpload;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public interface FeeWaiverUploadService {

	List<AuditDetail> processWaiverUploadsDetails(List<AuditDetail> adList, long uploadId, String type)
			throws Exception;

	List<AuditDetail> delete(List<FeeWaiverUpload> fwuList, String tableType, String tranType, long uploadId);

	List<AuditDetail> setWaiverUploadsAuditData(List<FeeWaiverUpload> fwuList, String tranType, String method);

	List<ErrorDetail> validateWaiverUploads(AuditHeader auditHeader, String method);

	List<FeeWaiverUpload> getFeeWaiverListByUploadId(long uploadId);
}
