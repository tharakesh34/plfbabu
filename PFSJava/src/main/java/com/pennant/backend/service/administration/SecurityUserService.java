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
 * * FileName : SecurityUserService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2011 * * Modified
 * Date : 27-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.administration;

import java.util.List;

import com.pennant.backend.model.administration.ReportingManager;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.administration.SecurityUserDivBranch;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.lic.exception.LicenseException;

public interface SecurityUserService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	SecurityUser getSecurityUserById(long id);

	SecurityUser getApprovedSecurityUserById(long id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	AuditHeader changePassword(AuditHeader auditHeader);

	List<SecurityUserDivBranch> getSecUserDivBrList(long usrID, String type);

	List<AuditDetail> deleteDivBranchs(List<SecurityUserDivBranch> securityUserDivBranchList, String tableType,
			String auditTranType);

	public List<AuditDetail> deleteReportinManagers(List<ReportingManager> reportingManagers, String tableType,
			String auditTranType);

	// Security Work flow changes
	SecurityUser getSecurityUserOperationsById(long id);

	SecurityUser getApprovedSecurityUserOperationsById(long id);

	void validateLicensedUsers() throws LicenseException;

	SecurityUser getSecurityUserByLogin(String userLogin);

	public List<Entity> getEntityList(String entity);

	long getSecuredUserDetails(String username);

	void disableUserAccount();

	AuditDetail doUserValidation(AuditHeader auditHeader, boolean isAllowCluster, boolean isUpdate,
			LoggedInUser logUsrDtls);

	AuditDetail doValidation(AuditHeader auditHeader, LoggedInUser logUserDtls, boolean isFromUserExpire);

	AuditHeader updateUserStatus(AuditHeader auditHeader);

	SecurityUser getSecurityUserByLoginId(String userLogin);

	List<SecurityUserDivBranch> prepareSecurityBranch(List<SecurityUserDivBranch> divBranchList);
}