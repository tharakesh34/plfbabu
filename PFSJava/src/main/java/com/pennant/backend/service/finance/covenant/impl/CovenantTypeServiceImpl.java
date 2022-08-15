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
 * * FileName : CovenantTypeServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 06-02-2019 * *
 * Modified Date : 06-02-2019 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 06-02-2019 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.finance.covenant.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.covenant.CovenantTypeDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.covenant.CovenantType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.covenant.CovenantTypeService;
// import com.pennanttech.pennapps.core.model.ErrorDetails;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
// import com.pennanttech.pfs.core.TableType;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>CovenantType</b>.<br>
 */
public class CovenantTypeServiceImpl extends GenericService<CovenantType> implements CovenantTypeService {
	private static final Logger logger = LogManager.getLogger(CovenantTypeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private CovenantTypeDAO covenantTypeDAO;

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * COVENANT_TYPES/COVENANT_TYPES_Temp by using COVENANT_TYPESDAO's save method b) Update the Record in the table.
	 * based on the module workFlow Configuration. by using COVENANT_TYPESDAO's update method 3) Audit the record in to
	 * AuditHeader and AdtCOVENANT_TYPES by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		CovenantType covenantType = (CovenantType) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (covenantType.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (covenantType.isNewRecord()) {
			covenantType.setId(Long.parseLong(covenantTypeDAO.save(covenantType, tableType)));
			auditHeader.getAuditDetail().setModelData(covenantType);
			auditHeader.setAuditReference(String.valueOf(covenantType.getId()));
		} else {
			covenantTypeDAO.update(covenantType, tableType);
		}

		auditHeaderDAO.addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * COVENANT_TYPES by using COVENANT_TYPESDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtCOVENANT_TYPES by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
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

		CovenantType covenantType = (CovenantType) auditHeader.getAuditDetail().getModelData();
		covenantTypeDAO.delete(covenantType, TableType.MAIN_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getCOVENANT_TYPES fetch the details by using COVENANT_TYPESDAO's getCOVENANT_TYPESById method.
	 * 
	 * @param id id of the CovenantType.
	 * @return COVENANT_TYPES
	 */
	@Override
	public CovenantType getCovenantType(long id) {
		return covenantTypeDAO.getCovenantType(id, "_View");
	}

	/**
	 * getApprovedCOVENANT_TYPESById fetch the details by using COVENANT_TYPESDAO's getCOVENANT_TYPESById method . with
	 * parameter id and type as blank. it fetches the approved records from the COVENANT_TYPES.
	 * 
	 * @param id id of the CovenantType. (String)
	 * @return COVENANT_TYPES
	 */
	public CovenantType getApprovedCovenantType(long id) {
		return covenantTypeDAO.getCovenantType(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using covenantTypeDAO.delete with parameters
	 * covenantType,"" b) NEW Add new record in to main table by using covenantTypeDAO.save with parameters
	 * covenantType,"" c) EDIT Update record in the main table by using covenantTypeDAO.update with parameters
	 * covenantType,"" 3) Delete the record from the workFlow table by using covenantTypeDAO.delete with parameters
	 * covenantType,"_Temp" 4) Audit the record in to AuditHeader and AdtCOVENANT_TYPES by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtCOVENANT_TYPES by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
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

		CovenantType covenantType = new CovenantType();
		BeanUtils.copyProperties((CovenantType) auditHeader.getAuditDetail().getModelData(), covenantType);

		covenantTypeDAO.delete(covenantType, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(covenantType.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(covenantTypeDAO.getCovenantType(covenantType.getId(), ""));
		}

		if (covenantType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			covenantTypeDAO.delete(covenantType, TableType.MAIN_TAB);
		} else {
			covenantType.setRoleCode("");
			covenantType.setNextRoleCode("");
			covenantType.setTaskId("");
			covenantType.setNextTaskId("");
			covenantType.setWorkflowId(0);

			if (covenantType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				covenantType.setRecordType("");
				covenantTypeDAO.save(covenantType, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				covenantType.setRecordType("");
				covenantTypeDAO.update(covenantType, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(covenantType);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using covenantTypeDAO.delete with parameters covenantType,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtCOVENANT_TYPES by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader (auditHeader)
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

		CovenantType covenantType = (CovenantType) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		covenantTypeDAO.delete(covenantType, TableType.TEMP_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader (auditHeader)
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
	 * from covenantTypeDAO.getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
	 * the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		CovenantType covenantType = (CovenantType) auditDetail.getModelData();
		String code = covenantType.getCode();
		String category = covenantType.getCategory();

		// Check the unique keys.
		if (covenantType.isNewRecord() && PennantConstants.RECORD_TYPE_NEW.equals(covenantType.getRecordType())
				&& covenantTypeDAO.isDuplicateKey(covenantType,
						covenantType.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];
			parameters[0] = PennantJavaUtil.getLabel("label_Covenant_Code.value") + ": " + code;
			parameters[1] = PennantJavaUtil.getLabel("label_CovenantTypeDialog_Category.value") + ": " + category;

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41014", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	/**
	 * @param auditHeaderDAO the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	/**
	 * @param covenantTypeDAO the covenantTypeDAO to set
	 */
	public void setCovenantTypeDAO(CovenantTypeDAO covenantTypeDAO) {
		this.covenantTypeDAO = covenantTypeDAO;
	}

}