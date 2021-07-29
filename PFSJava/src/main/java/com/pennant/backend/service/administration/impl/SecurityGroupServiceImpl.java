/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  SecurityGroupServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  27-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-05-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.service.administration.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.administration.SecurityGroupDAO;
import com.pennant.backend.dao.administration.SecurityGroupRightsDAO;
import com.pennant.backend.dao.administration.SecurityRoleGroupsDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.administration.SecurityGroup;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.administration.SecurityGroupService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>SecurityGroup</b>.<br>
 * 
 */
public class SecurityGroupServiceImpl extends GenericService<SecurityGroup> implements SecurityGroupService {
	private static Logger logger = LogManager.getLogger(SecurityGroupServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private SecurityGroupDAO securityGroupDAO;
	private SecurityRoleGroupsDAO securityRoleGroupsDAO;
	private SecurityGroupRightsDAO securityGroupRightsDAO;

	public SecurityGroupServiceImpl() {
		super();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table SecGroups/SecGroups_Temp by
	 * using SecurityGroupDAO's save method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using SecurityGroupDAO's update method 3) Audit the record in to AuditHeader and AdtSecGroups
	 * by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
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
		SecurityGroup securityGroup = (SecurityGroup) auditHeader.getAuditDetail().getModelData();
		if (securityGroup.isWorkflow()) {
			tableType = "_Temp";
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		} else {

			if (securityGroup.isNewRecord()) {
				auditHeader.setAuditTranType(PennantConstants.TRAN_ADD);
			} else {
				auditHeader.setAuditTranType(PennantConstants.TRAN_UPD);
			}

			securityGroup.setRecordStatus("");
			securityGroup.setRoleCode("");
			securityGroup.setNextRoleCode("");
			securityGroup.setTaskId("");
			securityGroup.setNextTaskId("");
			securityGroup.setRecordType("");
			securityGroup.setWorkflowId(0);

		}

		if (securityGroup.isNewRecord()) {
			securityGroup.setId(getSecurityGroupDAO().save(securityGroup, tableType));
			auditHeader.setModelData(securityGroup);
			auditHeader.setAuditReference(String.valueOf(securityGroup.getGrpID()));
		} else {
			getSecurityGroupDAO().update(securityGroup, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * SecGroups by using SecurityGroupDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtSecGroups by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering ");

		SecurityGroup securityGroup = (SecurityGroup) auditHeader.getAuditDetail().getModelData();

		auditHeader = businessValidation(auditHeader, "delete");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		getSecurityGroupDAO().delete(securityGroup, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * getSecurityGroupById fetch the details by using SecurityGroupDAO's getSecurityGroupById method.
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return SecurityGroup
	 */

	@Override
	public SecurityGroup getSecurityGroupById(long id) {
		logger.debug("Entering ");
		return getSecurityGroupDAO().getSecurityGroupById(id, "_View");
	}

	/**
	 * getApprovedSecurityGroupById fetch the details by using SecurityGroupDAO's getSecurityGroupById method . with
	 * parameter id and type as blank. it fetches the approved records from the SecGroups.
	 * 
	 * @param id
	 *            (int)
	 * @return SecurityGroup
	 */

	public SecurityGroup getApprovedSecurityGroupById(long id) {
		logger.debug("Entering ");
		return getSecurityGroupDAO().getSecurityGroupById(id, "");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getSecurityGroupDAO().delete with
	 * parameters securityGroup,"" b) NEW Add new record in to main table by using getSecurityGroupDAO().save with
	 * parameters securityGroup,"" c) EDIT Update record in the main table by using getSecurityGroupDAO().update with
	 * parameters securityGroup,"" 3) Delete the record from the workFlow table by using getSecurityGroupDAO().delete
	 * with parameters securityGroup,"_Temp" 4) Audit the record in to AuditHeader and AdtSecGroups by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtSecGroups by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering ");
		SecurityGroup securityGroup = new SecurityGroup();
		BeanUtils.copyProperties((SecurityGroup) auditHeader.getAuditDetail().getModelData(), securityGroup);

		String tranType = "";

		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		if (securityGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getSecurityGroupDAO().delete(securityGroup, "");

		} else {
			securityGroup.setRoleCode("");
			securityGroup.setNextRoleCode("");
			securityGroup.setTaskId("");
			securityGroup.setNextTaskId("");
			securityGroup.setWorkflowId(0);

			if (securityGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				securityGroup.setRecordType("");
				getSecurityGroupDAO().save(securityGroup, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				securityGroup.setRecordType("");
				getSecurityGroupDAO().update(securityGroup, "");
			}
		}

		getSecurityGroupDAO().delete(securityGroup, "_Temp");
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
	 * workFlow table by using getSecurityGroupDAO().delete with parameters securityGroup,"_Temp" 3) Audit the record in
	 * to AuditHeader and AdtSecGroups by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering ");
		SecurityGroup securityGroup = (SecurityGroup) auditHeader.getAuditDetail().getModelData();

		auditHeader = businessValidation(auditHeader, "doReject");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getSecurityGroupDAO().delete(securityGroup, "_Temp");
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering ");

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
	 * from getSecurityGroupDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);
		SecurityGroup securityGroup = (SecurityGroup) auditDetail.getModelData();
		// Check the unique keys.
		if (securityGroup.isNewRecord() && StringUtils.equals(PennantConstants.RECORD_TYPE_NEW, securityGroup.getRecordType())
				&& securityGroupDAO.isDuplicateKey(securityGroup.getGrpCode(),
						securityGroup.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_GrpCode") + ": " + securityGroup.getGrpCode();
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public SecurityGroupDAO getSecurityGroupDAO() {
		return securityGroupDAO;
	}

	public void setSecurityGroupDAO(SecurityGroupDAO securityGroupDAO) {
		this.securityGroupDAO = securityGroupDAO;
	}

	public void setSecurityRoleGroupsDAO(SecurityRoleGroupsDAO securityRoleGroupsDAO) {
		this.securityRoleGroupsDAO = securityRoleGroupsDAO;
	}

	public SecurityRoleGroupsDAO getSecurityRoleGroupsDAO() {
		return securityRoleGroupsDAO;
	}

	public SecurityGroupRightsDAO getSecurityGroupRightsDAO() {
		return securityGroupRightsDAO;
	}

	public void setSecurityGroupRightsDAO(SecurityGroupRightsDAO securityGroupRightsDAO) {
		this.securityGroupRightsDAO = securityGroupRightsDAO;
	}

}