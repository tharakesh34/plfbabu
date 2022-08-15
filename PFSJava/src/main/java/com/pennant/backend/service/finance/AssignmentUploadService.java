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
 * * FileName : AssignmentUploadService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 19-11-2018 * *
 * Modified Date : 19-11-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 19-11-2018 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.assignmentupload.AssignmentUpload;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service Declaration for methods that depends on <b>AssignmentUpload</b>.<br>
 * 
 */
public interface AssignmentUploadService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	List<AssignmentUpload> getAssignmentUploadsByUploadId(long uploadId);

	List<AssignmentUpload> getApprovedAssignmentUploadsByUploadId(long uploadId);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	List<AuditDetail> setAssignmentUploadsAuditData(List<AssignmentUpload> assignmentUploadsList, String auditTranType,
			String method);

	List<AuditDetail> processAssignmentUploadsDetails(List<AuditDetail> auditDetails, long uploadId, String type,
			String postingBranch);

	List<AuditDetail> delete(List<AssignmentUpload> assignmentUploadsList, String tableType, String auditTranType,
			long uploadId);

	List<ErrorDetail> validateAssignmentUploads(AuditHeader auditHeader, String usrLanguage, String method);

	void validateAssignmentScreenLevel(AssignmentUpload assignmentUpload, String entityCode);
}
