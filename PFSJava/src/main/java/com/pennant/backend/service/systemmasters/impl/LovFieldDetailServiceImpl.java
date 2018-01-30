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
 * FileName    		:  LovFieldDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-10-2011    														*
 *                                                                  						*
 * Modified Date    :  04-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.systemmasters.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.systemmasters.LovFieldDetailDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.LovFieldDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>LovFieldDetail</b>.<br>
 * 
 */
public class LovFieldDetailServiceImpl extends GenericService<LovFieldDetail> 
		implements LovFieldDetailService {
	
	private static final Logger logger = Logger.getLogger(LovFieldDetailServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	private LovFieldDetailDAO lovFieldDetailDAO;

	public LovFieldDetailServiceImpl() {
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
	
	public LovFieldDetailDAO getLovFieldDetailDAO() {
		return lovFieldDetailDAO;
	}
	public void setLovFieldDetailDAO(LovFieldDetailDAO lovFieldDetailDAO) {
		this.lovFieldDetailDAO = lovFieldDetailDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * RMTLovFieldDetail/RMTLovFieldDetail_Temp by using LovFieldDetailDAO's
	 * save method b) Update the Record in the table. based on the module
	 * workFlow Configuration. by using LovFieldDetailDAO's update method 3)
	 * Audit the record in to AuditHeader and AdtRMTLovFieldDetail by using
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
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		TableType tableType = TableType.MAIN_TAB;
		LovFieldDetail lovFieldDetail = (LovFieldDetail) auditHeader.getAuditDetail().getModelData();
		
		if (lovFieldDetail.isWorkflow()) {
			tableType=TableType.TEMP_TAB;
		}

		if (lovFieldDetail.isNew()) {
			lovFieldDetail.setId(Long.valueOf(getLovFieldDetailDAO().save(lovFieldDetail,tableType)));
			auditHeader.getAuditDetail().setModelData(lovFieldDetail);
			auditHeader.setAuditReference(String.valueOf(lovFieldDetail.getFieldCodeId()));
		}else{
			getLovFieldDetailDAO().update(lovFieldDetail,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table RMTLovFieldDetail by using LovFieldDetailDAO's delete method with
	 * type as Blank 3) Audit the record in to AuditHeader and
	 * AdtRMTLovFieldDetail by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		
		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		
		LovFieldDetail lovFieldDetail = (LovFieldDetail) auditHeader.getAuditDetail().getModelData();
		getLovFieldDetailDAO().delete(lovFieldDetail, TableType.MAIN_TAB);
		
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getLovFieldDetailById fetch the details by using LovFieldDetailDAO's
	 * getLovFieldDetailById method.
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return LovFieldDetail
	 */
	@Override
	public LovFieldDetail getLovFieldDetailById(String fieldCode,String fieldCodeValue) {
		return getLovFieldDetailDAO().getLovFieldDetailById(fieldCode,fieldCodeValue,"_View");
	}
	
	/**
	 * getApprovedLovFieldDetailById fetch the details by using
	 * LovFieldDetailDAO's getLovFieldDetailById method . with parameter id and
	 * type as blank. it fetches the approved records from the
	 * RMTLovFieldDetail.
	 * 
	 * @param id
	 *            (int)
	 * @return LovFieldDetail
	 */
	public LovFieldDetail getApprovedLovFieldDetailById(String fieldCode,String fieldCodeValue) {
		return getLovFieldDetailDAO().getLovFieldDetailById(fieldCode,fieldCodeValue,"_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getLovFieldDetailDAO().delete with parameters lovFieldDetail,"" b)
	 * NEW Add new record in to main table by using getLovFieldDetailDAO().save
	 * with parameters lovFieldDetail,"" c) EDIT Update record in the main table
	 * by using getLovFieldDetailDAO().update with parameters lovFieldDetail,""
	 * 3) Delete the record from the workFlow table by using
	 * getLovFieldDetailDAO().delete with parameters lovFieldDetail,"_Temp" 4)
	 * Audit the record in to AuditHeader and AdtRMTLovFieldDetail by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in
	 * to AuditHeader and AdtRMTLovFieldDetail by using
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
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		LovFieldDetail lovFieldDetail = new LovFieldDetail();
		BeanUtils.copyProperties((LovFieldDetail) auditHeader.getAuditDetail().getModelData(),
				lovFieldDetail);

		getLovFieldDetailDAO().delete(lovFieldDetail,TableType.TEMP_TAB);
		if (!PennantConstants.RECORD_TYPE_NEW.equals(lovFieldDetail.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(
					lovFieldDetailDAO.getLovFieldDetailById(lovFieldDetail.getFieldCode(),
							lovFieldDetail.getFieldCodeValue(), ""));
		}
		
		if (lovFieldDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getLovFieldDetailDAO().delete(lovFieldDetail, TableType.MAIN_TAB);

		} else {
			lovFieldDetail.setRoleCode("");
			lovFieldDetail.setNextRoleCode("");
			lovFieldDetail.setTaskId("");
			lovFieldDetail.setNextTaskId("");
			lovFieldDetail.setWorkflowId(0);

			if (lovFieldDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){	
				tranType=PennantConstants.TRAN_ADD;
				lovFieldDetail.setRecordType("");
				getLovFieldDetailDAO().save(lovFieldDetail, TableType.MAIN_TAB);
			} else {
				tranType=PennantConstants.TRAN_UPD;
				lovFieldDetail.setRecordType("");
				getLovFieldDetailDAO().update(lovFieldDetail, TableType.MAIN_TAB);
			}
		}

		
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(lovFieldDetail);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getLovFieldDetailDAO().delete with parameters
	 * lovFieldDetail,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtRMTLovFieldDetail by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		LovFieldDetail lovFieldDetail = (LovFieldDetail) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getLovFieldDetailDAO().delete(lovFieldDetail, TableType.TEMP_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps.
	 * 1)	get the details from the auditHeader. 
	 * 2)	fetch the details from the tables
	 * 3)	Validate the Record based on the record details. 
	 * 4) 	Validate for any business validation.
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader){
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
	 * getLovFieldDetailDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */
	
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug("Entering");

		// Get the model object.
		LovFieldDetail lovFieldDetail= (LovFieldDetail) auditDetail.getModelData();
		// Check the unique keys.
		if (lovFieldDetail.isNew()
				&& PennantConstants.RECORD_TYPE_NEW.equals(lovFieldDetail.getRecordType())
				&& lovFieldDetailDAO.isDuplicateKey(lovFieldDetail.getFieldCode(), lovFieldDetail.getFieldCodeValue(),
						lovFieldDetail.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];

			parameters[0] = PennantJavaUtil.getLabel("label_FieldCode")+":"+lovFieldDetail.getFieldCode();
			parameters[1] = PennantJavaUtil.getLabel("label_FieldCodeValue")+":"+lovFieldDetail.getFieldCodeValue();
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41001", parameters, null));
		}
		
		if (lovFieldDetail.isSystemDefault()) {
			int count = getLovFieldDetailDAO().getSystemDefaultCount(lovFieldDetail.getFieldCode(),lovFieldDetail.getFieldCodeValue());
			if (count > 0) {
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41010",
				        new String[]{PennantJavaUtil.getLabel("label_FieldCodeId")+":"+lovFieldDetail.getFieldCodeValue()}, null));
			}
        }

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	}
}