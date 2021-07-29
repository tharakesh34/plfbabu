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
 * FileName    		:  InterfaceConfigurationServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-08-2019    														*
 *                                                                  						*
 * Modified Date    :  10-08-2019    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-08-2019       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.service.externalinterface.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.externalinterface.InterfaceConfigurationDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.externalinterface.InterfaceConfiguration;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.externalinterface.InterfaceConfigurationService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>InterfaceConfiguration</b>.<br>
 */
public class InterfaceConfigurationServiceImpl extends GenericService<InterfaceConfiguration>
		implements InterfaceConfigurationService {
	private static final Logger logger = LogManager.getLogger(InterfaceConfigurationServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private InterfaceConfigurationDAO interfaceConfigurationDAO;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	/**
	 * @return the auditHeaderDAO
	 */
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	/**
	 * @param auditHeaderDAO
	 *            the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	/**
	 * @return the interfaceConfigurationDAO
	 */

	/**
	 * @param interfaceConfigurationDAO
	 *            the interfaceConfigurationDAO to set
	 */

	public InterfaceConfigurationDAO getInterfaceConfigurationDAO() {
		return interfaceConfigurationDAO;
	}

	public void setInterfaceConfigurationDAO(InterfaceConfigurationDAO interfaceConfigurationDAO) {
		this.interfaceConfigurationDAO = interfaceConfigurationDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * InterfaceConfiguration/InterfaceConfiguration_Temp by using InterfaceConfigurationDAO's save method b) Update the
	 * Record in the table. based on the module workFlow Configuration. by using InterfaceConfigurationDAO's update
	 * method 3) Audit the record in to AuditHeader and AdtInterfaceConfiguration by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		InterfaceConfiguration interfaceConfiguration = (InterfaceConfiguration) auditHeader.getAuditDetail()
				.getModelData();
		interfaceConfiguration.setEodDate(SysParamUtil.getAppDate());
		TableType tableType = TableType.MAIN_TAB;
		if (interfaceConfiguration.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (interfaceConfiguration.isNewRecord()) {
			interfaceConfiguration
					.setId(Long.parseLong(getInterfaceConfigurationDAO().save(interfaceConfiguration, tableType)));
			auditHeader.getAuditDetail().setModelData(interfaceConfiguration);
			auditHeader.setAuditReference(String.valueOf(interfaceConfiguration.getId()));
		} else {
			getInterfaceConfigurationDAO().update(interfaceConfiguration, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * InterfaceConfiguration by using InterfaceConfigurationDAO's delete method with type as Blank 3) Audit the record
	 * in to AuditHeader and AdtInterfaceConfiguration by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		InterfaceConfiguration interfaceConfiguration = (InterfaceConfiguration) auditHeader.getAuditDetail()
				.getModelData();
		getInterfaceConfigurationDAO().delete(interfaceConfiguration, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getInterfaceConfiguration fetch the details by using InterfaceConfigurationDAO's getInterfaceConfigurationById
	 * method.
	 * 
	 * @param id
	 *            id of the InterfaceConfiguration.
	 * @return InterfaceConfiguration
	 */
	@Override
	public InterfaceConfiguration getInterfaceConfiguration(long id) {
		return getInterfaceConfigurationDAO().getInterfaceConfiguration(id, "_View");
	}

	/**
	 * getApprovedInterfaceConfigurationById fetch the details by using InterfaceConfigurationDAO's
	 * getInterfaceConfigurationById method . with parameter id and type as blank. it fetches the approved records from
	 * the InterfaceConfiguration.
	 * 
	 * @param id
	 *            id of the InterfaceConfiguration. (String)
	 * @return InterfaceConfiguration
	 */
	public InterfaceConfiguration getApprovedInterfaceConfiguration(long id) {
		return getInterfaceConfigurationDAO().getInterfaceConfiguration(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getInterfaceConfigurationDAO().delete
	 * with parameters interfaceConfiguration,"" b) NEW Add new record in to main table by using
	 * getInterfaceConfigurationDAO().save with parameters interfaceConfiguration,"" c) EDIT Update record in the main
	 * table by using getInterfaceConfigurationDAO().update with parameters interfaceConfiguration,"" 3) Delete the
	 * record from the workFlow table by using getInterfaceConfigurationDAO().delete with parameters
	 * interfaceConfiguration,"_Temp" 4) Audit the record in to AuditHeader and AdtInterfaceConfiguration by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and
	 * AdtInterfaceConfiguration by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		InterfaceConfiguration configuration = new InterfaceConfiguration();
		BeanUtils.copyProperties((InterfaceConfiguration) auditHeader.getAuditDetail().getModelData(), configuration);

		configuration.setEodDate(SysParamUtil.getAppDate());
		getInterfaceConfigurationDAO().delete(configuration, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(configuration.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(interfaceConfigurationDAO.getInterfaceConfiguration(configuration.getId(), ""));
		}

		if (configuration.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getInterfaceConfigurationDAO().delete(configuration, TableType.MAIN_TAB);
		} else {
			configuration.setRoleCode("");
			configuration.setNextRoleCode("");
			configuration.setTaskId("");
			configuration.setNextTaskId("");
			configuration.setWorkflowId(0);

			if (configuration.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				configuration.setRecordType("");
				getInterfaceConfigurationDAO().save(configuration, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				configuration.setRecordType("");
				getInterfaceConfigurationDAO().update(configuration, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(configuration);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getInterfaceConfigurationDAO().delete with parameters interfaceConfiguration,"_Temp" 3)
	 * Audit the record in to AuditHeader and AdtInterfaceConfiguration by using auditHeaderDAO.addAudit(auditHeader)
	 * for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		InterfaceConfiguration interfaceConfiguration = (InterfaceConfiguration) auditHeader.getAuditDetail()
				.getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getInterfaceConfigurationDAO().delete(interfaceConfiguration, TableType.TEMP_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getInterfaceConfigurationDAO().getErrorDetail with Error ID and language as parameters. if any
	 * error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		InterfaceConfiguration interfaceConfiguration = (InterfaceConfiguration) auditDetail.getModelData();

		// Check the unique keys.
		if (interfaceConfiguration.isNewRecord() && interfaceConfigurationDAO.isDuplicateKey(interfaceConfiguration.getId(),
				interfaceConfiguration.getCode(),
				interfaceConfiguration.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];

			parameters[0] = PennantJavaUtil.getLabel("label_Code") + ": " + interfaceConfiguration.getCode();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

}