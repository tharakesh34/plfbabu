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
 * FileName    		:  BaseRateCodeServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.applicationmaster.BaseRateCodeDAO;
import com.pennant.backend.dao.applicationmaster.impl.BaseRateCodeDAOImpl;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.applicationmaster.BaseRateCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.BaseRateCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>BaseRateCode</b>.<br>
 * 
 */
public class BaseRateCodeServiceImpl extends GenericService<BaseRateCode> implements BaseRateCodeService {
	private static Logger logger = Logger.getLogger(BaseRateCodeDAOImpl.class);

	private AuditHeaderDAO auditHeaderDAO;	
	private BaseRateCodeDAO baseRateCodeDAO;

	public BaseRateCodeServiceImpl() {
		super();
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

	public BaseRateCodeDAO getBaseRateCodeDAO() {
		return baseRateCodeDAO;
	}
	public void setBaseRateCodeDAO(BaseRateCodeDAO baseRateCodeDAO) {
		this.baseRateCodeDAO = baseRateCodeDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * RMTBaseRateCodes/RMTBaseRateCodes_Temp by using BaseRateCodeDAO's save
	 * method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using BaseRateCodeDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtRMTBaseRateCodes by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}
		
		BaseRateCode baseRateCode = (BaseRateCode) auditHeader.getAuditDetail().getModelData();
		TableType tableType = TableType.MAIN_TAB;
		
		if (baseRateCode.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (baseRateCode.isNew()) {
			baseRateCode.setBRType(getBaseRateCodeDAO().save(baseRateCode,tableType));
			auditHeader.getAuditDetail().setModelData(baseRateCode);
			auditHeader.setAuditReference(baseRateCode.getBRType());
		}else{
			getBaseRateCodeDAO().update(baseRateCode,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table RMTBaseRateCodes by using BaseRateCodeDAO's delete method with type
	 * as Blank 3) Audit the record in to AuditHeader and AdtRMTBaseRateCodes by
	 * using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		BaseRateCode baseRateCode = (BaseRateCode) auditHeader.getAuditDetail().getModelData();
		getBaseRateCodeDAO().delete(baseRateCode, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getBaseRateCodeById fetch the details by using BaseRateCodeDAO's
	 * getBaseRateCodeById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return BaseRateCode
	 */
	@Override
	public BaseRateCode getBaseRateCodeById(String id) {
		return getBaseRateCodeDAO().getBaseRateCodeById(id,"_View");
	}

	/**
	 * getApprovedBaseRateCodeById fetch the details by using BaseRateCodeDAO's
	 * getBaseRateCodeById method . with parameter id and type as blank. it
	 * fetches the approved records from the RMTBaseRateCodes.
	 * 
	 * @param id
	 *            (String)
	 * @return BaseRateCode
	 */
	public BaseRateCode getApprovedBaseRateCodeById(String id) {
		return getBaseRateCodeDAO().getBaseRateCodeById(id,"_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getBaseRateCodeDAO().delete with parameters baseRateCode,"" b) NEW
	 * Add new record in to main table by using getBaseRateCodeDAO().save with
	 * parameters baseRateCode,"" c) EDIT Update record in the main table by
	 * using getBaseRateCodeDAO().update with parameters baseRateCode,"" 3)
	 * Delete the record from the workFlow table by using
	 * getBaseRateCodeDAO().delete with parameters baseRateCode,"_Temp" 4) Audit
	 * the record in to AuditHeader and AdtRMTBaseRateCodes by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in
	 * to AuditHeader and AdtRMTBaseRateCodes by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType="";		
		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		BaseRateCode baseRateCode = new BaseRateCode();
		BeanUtils.copyProperties((BaseRateCode) auditHeader.getAuditDetail().getModelData(), baseRateCode);
		
		getBaseRateCodeDAO().delete(baseRateCode, TableType.TEMP_TAB);
		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(baseRateCode.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(baseRateCodeDAO.getBaseRateCodeById(baseRateCode.getBRType(), ""));
		}
		
		if (baseRateCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;
			getBaseRateCodeDAO().delete(baseRateCode, TableType.MAIN_TAB);

		} else {
			baseRateCode.setRoleCode("");
			baseRateCode.setNextRoleCode("");
			baseRateCode.setTaskId("");
			baseRateCode.setNextTaskId("");
			baseRateCode.setWorkflowId(0);

			if (baseRateCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType=PennantConstants.TRAN_ADD;
				baseRateCode.setRecordType("");
				getBaseRateCodeDAO().save(baseRateCode,TableType.MAIN_TAB);
			} else {
				tranType=PennantConstants.TRAN_UPD;
				baseRateCode.setRecordType("");
				getBaseRateCodeDAO().update(baseRateCode, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(baseRateCode);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getBaseRateCodeDAO().delete with parameters
	 * baseRateCode,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtRMTBaseRateCodes by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		BaseRateCode baseRateCode= (BaseRateCode) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getBaseRateCodeDAO().delete(baseRateCode, TableType.TEMP_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from
	 * the auditHeader. 2) fetch the details from the tables 3) Validate the
	 * Record based on the record details. 4) Validate for any business
	 * validation. 
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),
				auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getBaseRateCodeDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage){
		logger.debug(Literal.ENTERING);

		// Get the model object.
		BaseRateCode baseRateCode = (BaseRateCode) auditDetail.getModelData();
	
		// Check the unique keys.
		if (baseRateCode.isNew()
				&& PennantConstants.RECORD_TYPE_NEW.equals(baseRateCode.getRecordType())
				&& baseRateCodeDAO.isDuplicateKey(baseRateCode.getBRType(), baseRateCode.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_BRType") + ": " + baseRateCode.getBRType();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}
		
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}
}