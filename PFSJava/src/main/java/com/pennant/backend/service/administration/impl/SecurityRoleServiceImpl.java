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
 * * FileName : SecurityRoleServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2011 * *
 * Modified Date : 03-08-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-08-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.administration.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.administration.SecurityRoleDAO;
import com.pennant.backend.dao.administration.SecurityRoleGroupsDAO;
import com.pennant.backend.dao.administration.SecurityUserOperationsDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.administration.SecurityRoleService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>SecurityRole</b>.<br>
 * 
 */
public class SecurityRoleServiceImpl extends GenericService<SecurityRole> implements SecurityRoleService {
	private static final Logger logger = LogManager.getLogger(SecurityRoleServiceImpl.class);
	private AuditHeaderDAO auditHeaderDAO;

	private SecurityRoleDAO securityRoleDAO;
	private SecurityUserOperationsDAO securityUserOperationsDAO;
	private SecurityRoleGroupsDAO securityRoleGroupsDAO;

	public SecurityRoleServiceImpl() {
		super();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table SecRoles/SecRoles_Temp by using
	 * SecurityRoleDAO's save method b) Update the Record in the table. based on the module workFlow Configuration. by
	 * using SecurityRoleDAO's update method 3) Audit the record in to AuditHeader and AdtSecRoles by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering ");

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		String tableType = "";
		SecurityRole securityRole = (SecurityRole) auditHeader.getAuditDetail().getModelData();
		if (securityRole.isWorkflow()) {
			tableType = "_Temp";
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		} else {

			if (securityRole.isNewRecord()) {
				auditHeader.setAuditTranType(PennantConstants.TRAN_ADD);
			} else {
				auditHeader.setAuditTranType(PennantConstants.TRAN_UPD);
			}

			securityRole.setRecordStatus("");
			securityRole.setRoleCode("");
			securityRole.setNextRoleCode("");
			securityRole.setTaskId("");
			securityRole.setNextTaskId("");
			securityRole.setRecordType("");
			securityRole.setWorkflowId(0);

		}

		if (securityRole.isNewRecord()) {
			securityRole.setId(getSecurityRoleDAO().save(securityRole, tableType));
			auditHeader.setModelData(securityRole);
			auditHeader.setAuditReference(String.valueOf(securityRole.getRoleID()));
		} else {
			getSecurityRoleDAO().update(securityRole, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Checks whether Role assigned for
	 * any user by calling SecurityUsersRolesDAO's getAllAssignedRoleIds() 3) Checks whether any Group assigned to this
	 * role by calling SecurityRoleGroupsDAO's getAllAssignedRoleIds() 4) delete Record for the DB table SecRoles by
	 * using SecurityRoleDAO's delete method with type as Blank 5) Audit the record in to AuditHeader and AdtSecRoles by
	 * using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering ");
		SecurityRole secRoles = (SecurityRole) auditHeader.getAuditDetail().getModelData();

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		getSecurityRoleDAO().delete(secRoles, "");
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * getSecurityRoleById fetch the details by using SecurityRoleDAO's getSecurityRoleById method.
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
	 * @return SecurityRole
	 */

	@Override
	public SecurityRole getSecurityRoleById(long id) {
		logger.debug("Entering ");
		return getSecurityRoleDAO().getSecurityRoleById(id, "_View");
	}

	/**
	 * getApprovedSecurityRoleById fetch the details by using SecurityRoleDAO's getSecurityRoleById method . with
	 * parameter id and type as blank. it fetches the approved records from the SecRoles.
	 * 
	 * @param id (int)
	 * @return SecurityRole
	 */

	public SecurityRole getApprovedSecurityRoleById(long id) {
		logger.debug("Entering ");
		return getSecurityRoleDAO().getSecurityRoleById(id, "");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getSecurityRoleDAO().delete with
	 * parameters securityRole,"" b) NEW Add new record in to main table by using getSecurityRoleDAO().save with
	 * parameters securityRole,"" c) EDIT Update record in the main table by using getSecurityRoleDAO().update with
	 * parameters securityRole,"" 3) Delete the record from the workFlow table by using getSecurityRoleDAO().delete with
	 * parameters securityRole,"_Temp" 4) Audit the record in to AuditHeader and AdtSecRoles by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtSecRoles by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering ");
		SecurityRole secRoles = new SecurityRole();
		BeanUtils.copyProperties((SecurityRole) auditHeader.getAuditDetail().getModelData(), secRoles);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		if (secRoles.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getSecurityRoleDAO().delete(secRoles, "");

		} else {
			secRoles.setRoleCode("");
			secRoles.setNextRoleCode("");
			secRoles.setTaskId("");
			secRoles.setNextTaskId("");
			secRoles.setWorkflowId(0);

			if (secRoles.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				secRoles.setRecordType("");
				getSecurityRoleDAO().save(secRoles, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				secRoles.setRecordType("");
				getSecurityRoleDAO().update(secRoles, "");
			}
		}

		getSecurityRoleDAO().delete(secRoles, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getSecurityRoleDAO().delete with parameters securityRole,"_Temp" 3) Audit the record in
	 * to AuditHeader and AdtSecRoles by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering ");
		SecurityRole secRoles = (SecurityRole) auditHeader.getAuditDetail().getModelData();
		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getSecurityRoleDAO().delete(secRoles, "_Temp");
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;
	}

	@Override
	public List<SecurityRole> getSecRoleCodeDesc(String roleCode) {
		return getSecurityRoleDAO().getSecurityRole(roleCode);
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering ");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getSecurityRoleDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);
		// Get the model object.
		SecurityRole securityRole = (SecurityRole) auditDetail.getModelData();
		// Check the unique keys.
		if (securityRole.isNewRecord() && PennantConstants.RECORD_TYPE_NEW.equals(securityRole.getRecordType())
				&& securityRoleDAO.isDuplicateKey(securityRole.getRoleApp(), securityRole.getRoleCd(),
						securityRole.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_RoleCode") + ": " + securityRole.getRoleCd();
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	/**
	 * getApprovedSecurityRoleById fetch the details by using SecurityRoleDAO's getSecurityRoleById method . it fetches
	 * the approved records from the SecRoles.
	 * 
	 * @param id (int)
	 * @return SecurityRole
	 */
	@Override
	public List<SecurityRole> getApprovedSecurityRoles() {
		return getSecurityRoleDAO().getApprovedSecurityRoles();

	}

	@Override
	public List<SecurityRole> getApprovedRoles() {
		return getSecurityRoleDAO().getApprovedSecurityRole();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setSecurityRoleGroupsDAO(SecurityRoleGroupsDAO securityRoleGroupsDAO) {
		this.securityRoleGroupsDAO = securityRoleGroupsDAO;
	}

	public SecurityRoleGroupsDAO getSecurityRoleGroupsDAO() {
		return securityRoleGroupsDAO;
	}

	public SecurityUserOperationsDAO getSecurityUserOperationsDAO() {
		return securityUserOperationsDAO;
	}

	public void setSecurityUserOperationsDAO(SecurityUserOperationsDAO securityUserOperationsDAO) {
		this.securityUserOperationsDAO = securityUserOperationsDAO;
	}

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public SecurityRoleDAO getSecurityRoleDAO() {
		return securityRoleDAO;
	}

	public void setSecurityRoleDAO(SecurityRoleDAO securityRoleDAO) {
		this.securityRoleDAO = securityRoleDAO;
	}

}