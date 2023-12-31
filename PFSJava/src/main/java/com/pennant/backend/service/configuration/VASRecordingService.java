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
 * * FileName : VASRecordingService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 02-12-2016 * * Modified
 * Date : 02-12-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 02-12-2016 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.configuration;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.configuration.VASPremiumCalcDetails;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.configuration.VasCustomer;

public interface VASRecordingService {
	VASRecording getVASRecording();

	VASRecording getNewVASRecording();

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	VASRecording getVASRecordingByRef(String vasReference, String userRole, boolean isEnquiry);

	VASRecording getVASRecordingByReference(String vasReference);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	VASRecording getProcessEditorDetails(VASRecording vASRecording, String role, String finserEventOrg);

	AuditDetail doValidations(VASRecording vasRecording, boolean isPending);

	VasCustomer getVasCustomerDetails(String primaryLinkRef, String postingAgainst);

	List<VASRecording> getVasRecordingsByPrimaryLinkRef(String primaryLinkRef);

	VASRecording getVASRecordingForInsurance(String vasReference, String nextRoleCode, String event, boolean isEnquiry);

	void updateVasStatus(String status, String vasReference);

	VASRecording getVASRecording(String vasRefrence, String vasStatus);

	void updateVasPaymentId(String reference, long paymentInsId);

	List<VASPremiumCalcDetails> getPremiumCalcDeatils(VASPremiumCalcDetails premiumCalcDetails);

	VASRecording getVASRecordingDetails(VASRecording vasRecording);

	List<VASRecording> getLoanReportVasRecordingByRef(String reference);

	String getVasInsStatus(long paymentInsId);

}