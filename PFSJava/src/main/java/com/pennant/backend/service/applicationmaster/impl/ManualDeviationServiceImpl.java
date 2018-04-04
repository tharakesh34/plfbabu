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
 * FileName    		:  ManualDeviationServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-04-2018    														*
 *                                                                  						*
 * Modified Date    :  03-04-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-04-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.service.applicationmaster.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.ManualDeviationDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.applicationmaster.ManualDeviation;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.ManualDeviationService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>ManualDeviation</b>.<br>
 */
public class ManualDeviationServiceImpl extends GenericService<ManualDeviation> implements ManualDeviationService {
	private static final Logger	logger	= Logger.getLogger(ManualDeviationServiceImpl.class);

	private AuditHeaderDAO		auditHeaderDAO;
	private ManualDeviationDAO	manualDeviationDAO;

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
	 * @return the manualDeviationDAO
	 */
	public ManualDeviationDAO getManualDeviationDAO() {
		return manualDeviationDAO;
	}

	/**
	 * @param manualDeviationDAO
	 *            the manualDeviationDAO to set
	 */
	public void setManualDeviationDAO(ManualDeviationDAO manualDeviationDAO) {
		this.manualDeviationDAO = manualDeviationDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * ManualDeviations/ManualDeviations_Temp by using ManualDeviationsDAO's save method b) Update the Record in the
	 * table. based on the module workFlow Configuration. by using ManualDeviationsDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtManualDeviations by using auditHeaderDAO.addAudit(auditHeader)
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

		ManualDeviation manualDeviation = (ManualDeviation) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (manualDeviation.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (manualDeviation.isNew()) {
			manualDeviation.setId(Long.parseLong(getManualDeviationDAO().save(manualDeviation, tableType)));
			auditHeader.getAuditDetail().setModelData(manualDeviation);
			auditHeader.setAuditReference(String.valueOf(manualDeviation.getDeviationID()));
		} else {
			getManualDeviationDAO().update(manualDeviation, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * ManualDeviations by using ManualDeviationsDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtManualDeviations by using auditHeaderDAO.addAudit(auditHeader)
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

		ManualDeviation manualDeviation = (ManualDeviation) auditHeader.getAuditDetail().getModelData();
		getManualDeviationDAO().delete(manualDeviation, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getManualDeviations fetch the details by using ManualDeviationsDAO's getManualDeviationsById method.
	 * 
	 * @param deviationID
	 *            deviationID of the ManualDeviation.
	 * @return ManualDeviations
	 */
	@Override
	public ManualDeviation getManualDeviation(long deviationID) {
		return getManualDeviationDAO().getManualDeviation(deviationID, "_View");
	}

	/**
	 * getApprovedManualDeviationsById fetch the details by using ManualDeviationsDAO's getManualDeviationsById method .
	 * with parameter id and type as blank. it fetches the approved records from the ManualDeviations.
	 * 
	 * @param deviationID
	 *            deviationID of the ManualDeviation. (String)
	 * @return ManualDeviations
	 */
	public ManualDeviation getApprovedManualDeviation(long deviationID) {
		return getManualDeviationDAO().getManualDeviation(deviationID, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getManualDeviationDAO().delete with
	 * parameters manualDeviation,"" b) NEW Add new record in to main table by using getManualDeviationDAO().save with
	 * parameters manualDeviation,"" c) EDIT Update record in the main table by using getManualDeviationDAO().update
	 * with parameters manualDeviation,"" 3) Delete the record from the workFlow table by using
	 * getManualDeviationDAO().delete with parameters manualDeviation,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtManualDeviations by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to
	 * AuditHeader and AdtManualDeviations by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		ManualDeviation manualDeviation = new ManualDeviation();
		BeanUtils.copyProperties((ManualDeviation) auditHeader.getAuditDetail().getModelData(), manualDeviation);

		getManualDeviationDAO().delete(manualDeviation, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(manualDeviation.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(manualDeviationDAO.getManualDeviation(manualDeviation.getDeviationID(), ""));
		}

		if (manualDeviation.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getManualDeviationDAO().delete(manualDeviation, TableType.MAIN_TAB);
		} else {
			manualDeviation.setRoleCode("");
			manualDeviation.setNextRoleCode("");
			manualDeviation.setTaskId("");
			manualDeviation.setNextTaskId("");
			manualDeviation.setWorkflowId(0);

			if (manualDeviation.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				manualDeviation.setRecordType("");
				getManualDeviationDAO().save(manualDeviation, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				manualDeviation.setRecordType("");
				getManualDeviationDAO().update(manualDeviation, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(manualDeviation);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getManualDeviationDAO().delete with parameters manualDeviation,"_Temp" 3) Audit the
	 * record in to AuditHeader and AdtManualDeviations by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		ManualDeviation manualDeviation = (ManualDeviation) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getManualDeviationDAO().delete(manualDeviation, TableType.TEMP_TAB);

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
	 * from getManualDeviationDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		ManualDeviation manualDeviation = (ManualDeviation) auditDetail.getModelData();

		// Check the unique keys.
		if (manualDeviation.isNew() && manualDeviationDAO.isDuplicateKey(manualDeviation.getDeviationID(),
				manualDeviation.getCode(), manualDeviation.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];

			parameters[0] = PennantJavaUtil.getLabel("label_Code") + ": " + manualDeviation.getCode();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

}