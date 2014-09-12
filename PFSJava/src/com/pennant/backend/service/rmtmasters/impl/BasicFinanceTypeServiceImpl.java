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
 * FileName    		:  BasicFinanceTypeServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.rmtmasters.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.rmtmasters.BasicFinanceTypeDAO;
import com.pennant.backend.dao.rmtmasters.impl.BasicFinanceTypeDAOImpl;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.BasicFinanceType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.rmtmasters.BasicFinanceTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>BasicFinanceType</b>.<br>
 * 
 */
public class BasicFinanceTypeServiceImpl extends GenericService<BasicFinanceType> implements BasicFinanceTypeService {

	private static Logger logger = Logger
			.getLogger(BasicFinanceTypeDAOImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private BasicFinanceTypeDAO basicFinanceTypeDAO;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public BasicFinanceTypeDAO getBasicFinanceTypeDAO() {
		return basicFinanceTypeDAO;
	}
	public void setBasicFinanceTypeDAO(BasicFinanceTypeDAO basicFinanceTypeDAO) {
		this.basicFinanceTypeDAO = basicFinanceTypeDAO;
	}

	@Override
	public BasicFinanceType getBasicFinanceType() {
		return getBasicFinanceTypeDAO().getBasicFinanceType();
	}

	@Override
	public BasicFinanceType getNewBasicFinanceType() {
		return getBasicFinanceTypeDAO().getNewBasicFinanceType();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * RMTBasicFinanceTypes/RMTBasicFinanceTypes_Temp by using
	 * BasicFinanceTypeDAO's save method b) Update the Record in the table.
	 * based on the module workFlow Configuration. by using
	 * BasicFinanceTypeDAO's update method 3) Audit the record in to AuditHeader
	 * and AdtRMTBasicFinanceTypes by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType = "";
		BasicFinanceType basicFinanceType = (BasicFinanceType) auditHeader.getAuditDetail()
						.getModelData();

		if (basicFinanceType.isWorkflow()) {
			tableType = "_TEMP";
		} 

		if (basicFinanceType.isNew()) {
			basicFinanceType.setId(getBasicFinanceTypeDAO().save(basicFinanceType, tableType));
			auditHeader.getAuditDetail().setModelData(basicFinanceType);
			auditHeader.setAuditReference(String.valueOf(basicFinanceType.getId()));
		} else {
			getBasicFinanceTypeDAO().update(basicFinanceType, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table RMTBasicFinanceTypes by using BasicFinanceTypeDAO's delete method
	 * with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtRMTBasicFinanceTypes by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}
		BasicFinanceType basicFinanceType = (BasicFinanceType) auditHeader.getAuditDetail()
				.getModelData();

		getBasicFinanceTypeDAO().delete(basicFinanceType, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getBasicFinanceTypeById fetch the details by using BasicFinanceTypeDAO's
	 * getBasicFinanceTypeById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return BasicFinanceType
	 */
	@Override
	public BasicFinanceType getBasicFinanceTypeById(String id) {
		return getBasicFinanceTypeDAO().getBasicFinanceTypeById(id, "_View");
	}

	/**
	 * getApprovedBasicFinanceTypeById fetch the details by using
	 * BasicFinanceTypeDAO's getBasicFinanceTypeById method . with parameter id
	 * and type as blank. it fetches the approved records from the
	 * RMTBasicFinanceTypes.
	 * 
	 * @param id
	 *            (String)
	 * @return BasicFinanceType
	 */
	public BasicFinanceType getApprovedBasicFinanceTypeById(String id) {
		return getBasicFinanceTypeDAO().getBasicFinanceTypeById(id, "_AView");
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param BasicFinanceType
	 *            (basicFinanceType)
	 * @return basicFinanceType
	 */
	@Override
	public BasicFinanceType refresh(BasicFinanceType basicFinanceType) {
		logger.debug("Entering");
		getBasicFinanceTypeDAO().refresh(basicFinanceType);
		getBasicFinanceTypeDAO().initialize(basicFinanceType);
		logger.debug("Leaving");
		return basicFinanceType;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getBasicFinanceTypeDAO().delete with parameters basicFinanceType,""
	 * b) NEW Add new record in to main table by using
	 * getBasicFinanceTypeDAO().save with parameters basicFinanceType,"" c) EDIT
	 * Update record in the main table by using getBasicFinanceTypeDAO().update
	 * with parameters basicFinanceType,"" 3) Delete the record from the
	 * workFlow table by using getBasicFinanceTypeDAO().delete with parameters
	 * basicFinanceType,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtRMTBasicFinanceTypes by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow 5) Audit the record in to AuditHeader and
	 * AdtRMTBasicFinanceTypes by using auditHeaderDAO.addAudit(auditHeader)
	 * based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType = "";

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		BasicFinanceType basicFinanceType = new BasicFinanceType();
		BeanUtils.copyProperties((BasicFinanceType) auditHeader.getAuditDetail().getModelData(),
				basicFinanceType);

		if (basicFinanceType.getRecordType().equals(
				PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getBasicFinanceTypeDAO().delete(basicFinanceType, "");

		} else {
			basicFinanceType.setRoleCode("");
			basicFinanceType.setNextRoleCode("");
			basicFinanceType.setTaskId("");
			basicFinanceType.setNextTaskId("");
			basicFinanceType.setWorkflowId(0);

			if (basicFinanceType.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				basicFinanceType.setRecordType("");
				getBasicFinanceTypeDAO().save(basicFinanceType, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				basicFinanceType.setRecordType("");
				getBasicFinanceTypeDAO().update(basicFinanceType, "");
			}
		}

		getBasicFinanceTypeDAO().delete(basicFinanceType, "_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(basicFinanceType);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getBasicFinanceTypeDAO().delete with
	 * parameters basicFinanceType,"_Temp" 3) Audit the record in to AuditHeader
	 * and AdtRMTBasicFinanceTypes by using auditHeaderDAO.addAudit(auditHeader)
	 * for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}
		
		BasicFinanceType basicFinanceType = (BasicFinanceType) auditHeader.getAuditDetail()
				.getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getBasicFinanceTypeDAO().delete(basicFinanceType, "_TEMP");

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
	private AuditHeader businessValidation(AuditHeader auditHeader,
			String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),
				auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getAccountEngineRuleDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method) {
		logger.debug("Entering");

		BasicFinanceType basicFinanceType = (BasicFinanceType) auditDetail
				.getModelData();
		BasicFinanceType tempBasicFinanceType = null;
		if (basicFinanceType.isWorkflow()) {
			tempBasicFinanceType = getBasicFinanceTypeDAO()
					.getBasicFinanceTypeById(basicFinanceType.getId(), "_Temp");
		}
		BasicFinanceType befBasicFinanceType = getBasicFinanceTypeDAO()
				.getBasicFinanceTypeById(basicFinanceType.getId(), "");

		BasicFinanceType oldBasicFinanceType = basicFinanceType.getBefImage();

		String[] valueParm = new String[1];
		String[] errParm= new String[1];

		valueParm[0] = basicFinanceType.getFinBasicType();
		errParm[0] = PennantJavaUtil.getLabel("label_FinBasicType") + ":"+ valueParm[0];
		
		if (basicFinanceType.isNew()) { // for New record or new record into
										// work flow

			if (!basicFinanceType.isWorkflow()) {// With out Work flow only new
													// records
				if (befBasicFinanceType != null) { // Record Already Exists in
													// the table then error
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
				}
			} else { // with work flow
				
				if (basicFinanceType.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
																// is new
					if (befBasicFinanceType != null || tempBasicFinanceType != null) { // if records already
														// exists in the main
														// table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
					}
				} else { // if records not exists in the Main flow table
					if (befBasicFinanceType == null || tempBasicFinanceType != null) {
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}

			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!basicFinanceType.isWorkflow()) { // With out Work flow for
													// update and delete

				if (befBasicFinanceType == null) { // if records not exists in
													// the main table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41002",errParm,null));
				}else{

					if (oldBasicFinanceType != null
							&& !oldBasicFinanceType.getLastMntOn().equals(
									befBasicFinanceType.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41003",errParm,null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41004",errParm,null));
						}
					}
				}

			} else {

				if (tempBasicFinanceType == null) { // if records not exists in
													// the Work flow table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

				if (tempBasicFinanceType != null && oldBasicFinanceType != null
						&& !oldBasicFinanceType.getLastMntOn().equals(
								tempBasicFinanceType.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		
		if (StringUtils.trimToEmpty(method).equals("doApprove") || !basicFinanceType.isWorkflow()) {
			auditDetail.setBefImage(befBasicFinanceType);
		}
		logger.debug("Leaving");
		return auditDetail;
	}

}