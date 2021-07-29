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
 * FileName    		:  ExtInterfaceConfigurationServiceImpl.java                                                   * 	  
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
import com.pennant.backend.dao.externalinterface.ExtInterfaceConfigurationDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.externalinterface.InterfaceConfiguration;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.externalinterface.ExtInterfaceConfigurationService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>InterfaceConfiguration</b>.<br>
 */
public class ExtInterfaceConfigurationServiceImpl extends GenericService<InterfaceConfiguration>
		implements ExtInterfaceConfigurationService {
	private static final Logger logger = LogManager.getLogger(ExtInterfaceConfigurationServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private ExtInterfaceConfigurationDAO ExtInterfaceConfigurationDAO;

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
	 * @return the ExtInterfaceConfigurationDAO
	 */
	public ExtInterfaceConfigurationDAO getExtInterfaceConfigurationDAO() {
		return ExtInterfaceConfigurationDAO;
	}

	/**
	 * @param ExtInterfaceConfigurationDAO
	 *            the ExtInterfaceConfigurationDAO to set
	 */
	public void setExtInterfaceConfigurationDAO(ExtInterfaceConfigurationDAO ExtInterfaceConfigurationDAO) {
		this.ExtInterfaceConfigurationDAO = ExtInterfaceConfigurationDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * InterfaceConfiguration/ExtInterfaceConfiguration_Temp by using ExtInterfaceConfigurationDAO's save method b)
	 * Update the Record in the table. based on the module workFlow Configuration. by using
	 * ExtInterfaceConfigurationDAO's update method 3) Audit the record in to AuditHeader and
	 * AdtExtInterfaceConfiguration by using auditHeaderDAO.addAudit(auditHeader)
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

		InterfaceConfiguration configuration = (InterfaceConfiguration) auditHeader.getAuditDetail().getModelData();

		configuration.setEodDate(SysParamUtil.getAppDate());
		TableType tableType = TableType.MAIN_TAB;
		if (configuration.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (configuration.isNewRecord()) {
			configuration.setId(Long.parseLong(getExtInterfaceConfigurationDAO().save(configuration, tableType)));
			auditHeader.getAuditDetail().setModelData(configuration);
			auditHeader.setAuditReference(String.valueOf(configuration.getId()));
		} else {
			getExtInterfaceConfigurationDAO().update(configuration, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * InterfaceConfiguration by using ExtInterfaceConfigurationDAO's delete method with type as Blank 3) Audit the
	 * record in to AuditHeader and AdtExtInterfaceConfiguration by using auditHeaderDAO.addAudit(auditHeader)
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

		InterfaceConfiguration InterfaceConfiguration = (InterfaceConfiguration) auditHeader.getAuditDetail()
				.getModelData();
		getExtInterfaceConfigurationDAO().delete(InterfaceConfiguration, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getExtInterfaceConfiguration fetch the details by using ExtInterfaceConfigurationDAO's
	 * getExtInterfaceConfigurationById method.
	 * 
	 * @param id
	 *            id of the InterfaceConfiguration.
	 * @return InterfaceConfiguration
	 */
	@Override
	public InterfaceConfiguration getExtInterfaceConfiguration(long id) {
		return getExtInterfaceConfigurationDAO().getExtInterfaceConfiguration(id, "_View");
	}

	/**
	 * getApprovedExtInterfaceConfigurationById fetch the details by using ExtInterfaceConfigurationDAO's
	 * getExtInterfaceConfigurationById method . with parameter id and type as blank. it fetches the approved records
	 * from the InterfaceConfiguration.
	 * 
	 * @param id
	 *            id of the InterfaceConfiguration. (String)
	 * @return InterfaceConfiguration
	 */
	public InterfaceConfiguration getApprovedExtInterfaceConfiguration(long id) {
		return getExtInterfaceConfigurationDAO().getExtInterfaceConfiguration(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using
	 * getExtInterfaceConfigurationDAO().delete with parameters InterfaceConfiguration,"" b) NEW Add new record in to
	 * main table by using getExtInterfaceConfigurationDAO().save with parameters InterfaceConfiguration,"" c) EDIT
	 * Update record in the main table by using getExtInterfaceConfigurationDAO().update with parameters
	 * InterfaceConfiguration,"" 3) Delete the record from the workFlow table by using
	 * getExtInterfaceConfigurationDAO().delete with parameters InterfaceConfiguration,"_Temp" 4) Audit the record in to
	 * AuditHeader and AdtExtInterfaceConfiguration by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit
	 * the record in to AuditHeader and AdtExtInterfaceConfiguration by using auditHeaderDAO.addAudit(auditHeader) based
	 * on the transaction Type.
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
		getExtInterfaceConfigurationDAO().delete(configuration, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(configuration.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(ExtInterfaceConfigurationDAO.getExtInterfaceConfiguration(configuration.getId(), ""));
		}

		if (configuration.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getExtInterfaceConfigurationDAO().delete(configuration, TableType.MAIN_TAB);
		} else {
			configuration.setRoleCode("");
			configuration.setNextRoleCode("");
			configuration.setTaskId("");
			configuration.setNextTaskId("");
			configuration.setWorkflowId(0);

			if (configuration.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				configuration.setRecordType("");
				getExtInterfaceConfigurationDAO().save(configuration, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				configuration.setRecordType("");
				getExtInterfaceConfigurationDAO().update(configuration, TableType.MAIN_TAB);
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
	 * workFlow table by using getExtInterfaceConfigurationDAO().delete with parameters InterfaceConfiguration,"_Temp"
	 * 3) Audit the record in to AuditHeader and AdtExtInterfaceConfiguration by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		InterfaceConfiguration InterfaceConfiguration = (InterfaceConfiguration) auditHeader.getAuditDetail()
				.getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getExtInterfaceConfigurationDAO().delete(InterfaceConfiguration, TableType.TEMP_TAB);

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
	 * from getExtInterfaceConfigurationDAO().getErrorDetail with Error ID and language as parameters. if any
	 * error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		InterfaceConfiguration InterfaceConfiguration = (InterfaceConfiguration) auditDetail.getModelData();

		// Check the unique keys.
		if (InterfaceConfiguration.isNewRecord() && ExtInterfaceConfigurationDAO.isDuplicateKey(
				InterfaceConfiguration.getId(), InterfaceConfiguration.getCode(),
				InterfaceConfiguration.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];

			parameters[0] = PennantJavaUtil.getLabel("label_Code") + ": " + InterfaceConfiguration.getCode();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

}