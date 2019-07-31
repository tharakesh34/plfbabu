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
 * FileName    		:  InstrumentwiseLimitServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  18-01-2018    														*
 *                                                                  						*
 * Modified Date    :  18-01-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-01-2018       PENNANT	                 0.1                                            * 
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
import com.pennant.backend.dao.applicationmaster.InstrumentwiseLimitDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.applicationmaster.InstrumentwiseLimit;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.InstrumentwiseLimitService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>InstrumentwiseLimit</b>.<br>
 */
public class InstrumentwiseLimitServiceImpl extends GenericService<InstrumentwiseLimit>
		implements InstrumentwiseLimitService {
	private static final Logger logger = Logger.getLogger(InstrumentwiseLimitServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private InstrumentwiseLimitDAO instrumentwiseLimitDAO;

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * InstrumentwiseLimit/InstrumentwiseLimit_Temp by using InstrumentwiseLimitDAO's save method b) Update the Record
	 * in the table. based on the module workFlow Configuration. by using InstrumentwiseLimitDAO's update method 3)
	 * Audit the record in to AuditHeader and AdtInstrumentwiseLimit by using auditHeaderDAO.addAudit(auditHeader)
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

		InstrumentwiseLimit instrumentwiseLimit = (InstrumentwiseLimit) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (instrumentwiseLimit.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (instrumentwiseLimit.isNew()) {
			instrumentwiseLimit.setId(Long.parseLong(getInstrumentwiseLimitDAO().save(instrumentwiseLimit, tableType)));
			auditHeader.getAuditDetail().setModelData(instrumentwiseLimit);
			auditHeader.setAuditReference(String.valueOf(instrumentwiseLimit.getId()));
		} else {
			getInstrumentwiseLimitDAO().update(instrumentwiseLimit, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * InstrumentwiseLimit by using InstrumentwiseLimitDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtInstrumentwiseLimit by using auditHeaderDAO.addAudit(auditHeader)
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

		InstrumentwiseLimit instrumentwiseLimit = (InstrumentwiseLimit) auditHeader.getAuditDetail().getModelData();
		getInstrumentwiseLimitDAO().delete(instrumentwiseLimit, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getInstrumentwiseLimit fetch the details by using InstrumentwiseLimitDAO's getInstrumentwiseLimitById method.
	 * 
	 * @param id
	 *            id of the InstrumentwiseLimit.
	 * @return InstrumentwiseLimit
	 */
	@Override
	public InstrumentwiseLimit getInstrumentwiseLimit(long id) {
		return getInstrumentwiseLimitDAO().getInstrumentwiseLimit(id, "_View");
	}

	/**
	 * getApprovedInstrumentwiseLimitById fetch the details by using InstrumentwiseLimitDAO's getInstrumentwiseLimitById
	 * method . with parameter id and type as blank. it fetches the approved records from the InstrumentwiseLimit.
	 * 
	 * @param id
	 *            id of the InstrumentwiseLimit. (String)
	 * @return InstrumentwiseLimit
	 */
	public InstrumentwiseLimit getApprovedInstrumentwiseLimit(long id) {
		return getInstrumentwiseLimitDAO().getInstrumentwiseLimit(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getInstrumentwiseLimitDAO().delete
	 * with parameters instrumentwiseLimit,"" b) NEW Add new record in to main table by using
	 * getInstrumentwiseLimitDAO().save with parameters instrumentwiseLimit,"" c) EDIT Update record in the main table
	 * by using getInstrumentwiseLimitDAO().update with parameters instrumentwiseLimit,"" 3) Delete the record from the
	 * workFlow table by using getInstrumentwiseLimitDAO().delete with parameters instrumentwiseLimit,"_Temp" 4) Audit
	 * the record in to AuditHeader and AdtInstrumentwiseLimit by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtInstrumentwiseLimit by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		InstrumentwiseLimit instrumentwiseLimit = new InstrumentwiseLimit();
		BeanUtils.copyProperties((InstrumentwiseLimit) auditHeader.getAuditDetail().getModelData(),
				instrumentwiseLimit);

		getInstrumentwiseLimitDAO().delete(instrumentwiseLimit, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(instrumentwiseLimit.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(instrumentwiseLimitDAO.getInstrumentwiseLimit(instrumentwiseLimit.getId(), ""));
		}

		if (instrumentwiseLimit.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getInstrumentwiseLimitDAO().delete(instrumentwiseLimit, TableType.MAIN_TAB);
		} else {
			instrumentwiseLimit.setRoleCode("");
			instrumentwiseLimit.setNextRoleCode("");
			instrumentwiseLimit.setTaskId("");
			instrumentwiseLimit.setNextTaskId("");
			instrumentwiseLimit.setWorkflowId(0);

			if (instrumentwiseLimit.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				instrumentwiseLimit.setRecordType("");
				getInstrumentwiseLimitDAO().save(instrumentwiseLimit, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				instrumentwiseLimit.setRecordType("");
				getInstrumentwiseLimitDAO().update(instrumentwiseLimit, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(instrumentwiseLimit);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getInstrumentwiseLimitDAO().delete with parameters instrumentwiseLimit,"_Temp" 3) Audit
	 * the record in to AuditHeader and AdtInstrumentwiseLimit by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow
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

		InstrumentwiseLimit instrumentwiseLimit = (InstrumentwiseLimit) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getInstrumentwiseLimitDAO().delete(instrumentwiseLimit, TableType.TEMP_TAB);

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
	 * from getInstrumentwiseLimitDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings
	 * then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		InstrumentwiseLimit instrumentwiseLimit = (InstrumentwiseLimit) auditDetail.getModelData();

		// Check the unique keys.
		if (instrumentwiseLimit.isNew() && instrumentwiseLimitDAO.isDuplicateKey(instrumentwiseLimit.getId(),
				instrumentwiseLimit.getInstrumentMode(),
				instrumentwiseLimit.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];

			parameters[0] = PennantJavaUtil.getLabel("label_InstrumentMode") + ": "
					+ instrumentwiseLimit.getInstrumentMode();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}
	
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
	 * @return the instrumentwiseLimitDAO
	 */
	public InstrumentwiseLimitDAO getInstrumentwiseLimitDAO() {
		return instrumentwiseLimitDAO;
	}

	/**
	 * @param instrumentwiseLimitDAO
	 *            the instrumentwiseLimitDAO to set
	 */
	public void setInstrumentwiseLimitDAO(InstrumentwiseLimitDAO instrumentwiseLimitDAO) {
		this.instrumentwiseLimitDAO = instrumentwiseLimitDAO;
	}

	@Override
	public InstrumentwiseLimit getInstrumentWiseModeLimit(String paymentMode) {
		// TODO Auto-generated method stub
		return getInstrumentwiseLimitDAO().getInstrumentWiseModeLimit(paymentMode, "_AView");
	}

}