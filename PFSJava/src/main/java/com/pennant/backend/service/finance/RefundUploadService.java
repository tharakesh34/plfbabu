/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : RefundUploadService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-10-2018 * * Modified
 * Date : 05-10-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-10-2018 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.refundupload.RefundUpload;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service Declaration for methods that depends on <b>FinanceType</b>.<br>
 * 
 */
public interface RefundUploadService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	List<RefundUpload> getRefundUploadsByUploadId(long uploadId);

	List<RefundUpload> getApprovedRefundUploadsByUploadId(long uploadId);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	List<AuditDetail> setRefundUploadsAuditData(List<RefundUpload> refundUploadsList, String auditTranType,
			String method);

	List<AuditDetail> processRefundUploadsDetails(List<AuditDetail> auditDetails, long uploadId, String type);

	List<AuditDetail> delete(List<RefundUpload> refundUploadsList, String tableType, String auditTranType,
			long uploadId);

	List<ErrorDetail> validateRefundUploads(AuditHeader auditHeader, String usrLanguage, String method);
}
