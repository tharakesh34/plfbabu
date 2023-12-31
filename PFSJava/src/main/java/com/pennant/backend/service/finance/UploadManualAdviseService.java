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
 * * FileName : UploadManualAdviseService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 06-05-2018 * *
 * Modified Date : 06-05-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 06-05-2018 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.UploadManualAdvise;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public interface UploadManualAdviseService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	List<AuditDetail> processAdviseUploadsDetails(List<AuditDetail> auditDetails, long uploadId, String type);

	List<AuditDetail> delete(List<UploadManualAdvise> adviseUploadsList, String tableType, String auditTranType,
			long uploadId);

	List<ErrorDetail> validateAdviseUploads(AuditHeader auditHeader, String usrLanguage, String method);

	List<AuditDetail> setAdviseUploadsAuditData(List<UploadManualAdvise> adviseUploadList, String auditTranType,
			String method);

	List<UploadManualAdvise> getManualAdviseListByUploadId(long uploadId);
}
