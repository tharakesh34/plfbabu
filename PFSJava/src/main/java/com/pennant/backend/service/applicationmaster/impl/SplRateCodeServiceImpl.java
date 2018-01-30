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
 * FileName    		:  SplRateCodeServiceImpl.java                                                   * 	  
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

package com.pennant.backend.service.applicationmaster.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.SplRateCodeDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.applicationmaster.SplRateCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.SplRateCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>SplRateCode</b>.<br>
 * 
 */
public class SplRateCodeServiceImpl extends GenericService<SplRateCode> implements SplRateCodeService {

	private static final Logger logger = Logger.getLogger(SplRateCodeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private SplRateCodeDAO splRateCodeDAO;

	public SplRateCodeServiceImpl() {
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

	public SplRateCodeDAO getSplRateCodeDAO() {
		return splRateCodeDAO;
	}
	public void setSplRateCodeDAO(SplRateCodeDAO splRateCodeDAO) {
		this.splRateCodeDAO = splRateCodeDAO;
	}


	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * RMTSplRateCodes/RMTSplRateCodes_Temp by using SplRateCodeDAO's save
	 * method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using SplRateCodeDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtRMTSplRateCodes by using
	 * auditHeaderDAO.addAudit(auditHeader)
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
		SplRateCode splRateCode = (SplRateCode) auditHeader.getAuditDetail()
				.getModelData();

		if (splRateCode.isWorkflow()) {
			tableType = "_Temp";
		}

		if (splRateCode.isNew()) {
			splRateCode.setSRType(getSplRateCodeDAO().save(splRateCode,
					tableType));
			auditHeader.getAuditDetail().setModelData(splRateCode);
			auditHeader.setAuditReference(splRateCode
					.getSRType());
		} else {
			getSplRateCodeDAO().update(splRateCode, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table RMTSplRateCodes by using SplRateCodeDAO's delete method with type
	 * as Blank 3) Audit the record in to AuditHeader and AdtRMTSplRateCodes by
	 * using auditHeaderDAO.addAudit(auditHeader)
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

		SplRateCode splRateCode = (SplRateCode) auditHeader.getAuditDetail()
				.getModelData();
		getSplRateCodeDAO().delete(splRateCode, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getSplRateCodeById fetch the details by using SplRateCodeDAO's
	 * getSplRateCodeById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return SplRateCode
	 */
	@Override
	public SplRateCode getSplRateCodeById(String id) {
		return getSplRateCodeDAO().getSplRateCodeById(id, "_View");
	}

	/**
	 * getApprovedSplRateCodeById fetch the details by using SplRateCodeDAO's
	 * getSplRateCodeById method . with parameter id and type as blank. it
	 * fetches the approved records from the RMTSplRateCodes.
	 * 
	 * @param id
	 *            (String)
	 * @return SplRateCode
	 */
	public SplRateCode getApprovedSplRateCodeById(String id) {
		return getSplRateCodeDAO().getSplRateCodeById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getSplRateCodeDAO().delete with parameters splRateCode,"" b) NEW
	 * Add new record in to main table by using getSplRateCodeDAO().save with
	 * parameters splRateCode,"" c) EDIT Update record in the main table by
	 * using getSplRateCodeDAO().update with parameters splRateCode,"" 3) Delete
	 * the record from the workFlow table by using getSplRateCodeDAO().delete
	 * with parameters splRateCode,"_Temp" 4) Audit the record in to AuditHeader
	 * and AdtRMTSplRateCodes by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow 5) Audit the record in to AuditHeader and AdtRMTSplRateCodes by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		SplRateCode splRateCode = new SplRateCode();
		BeanUtils.copyProperties((SplRateCode) auditHeader.getAuditDetail()
				.getModelData(), splRateCode);

		if (splRateCode.getRecordType()
				.equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getSplRateCodeDAO().delete(splRateCode, "");
		} else {
			splRateCode.setRoleCode("");
			splRateCode.setNextRoleCode("");
			splRateCode.setTaskId("");
			splRateCode.setNextTaskId("");
			splRateCode.setWorkflowId(0);

			if (splRateCode.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				splRateCode.setRecordType("");
				getSplRateCodeDAO().save(splRateCode, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				splRateCode.setRecordType("");
				getSplRateCodeDAO().update(splRateCode, "");
			}
		}

		getSplRateCodeDAO().delete(splRateCode, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(splRateCode);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getSplRateCodeDAO().delete with parameters
	 * splRateCode,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtRMTSplRateCodes by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow
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

		SplRateCode splRateCode = (SplRateCode) auditHeader.getAuditDetail()
				.getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getSplRateCodeDAO().delete(splRateCode, "_Temp");

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
	 * getSplRateCodeDAO().getErrorDetail with Error ID and language as
	 * parameters. if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage,
			String method) {
		logger.debug("Entering");

		SplRateCode splRateCode = (SplRateCode) auditDetail.getModelData();

		SplRateCode tempSplRateCode = null;
		if (splRateCode.isWorkflow()) {
			tempSplRateCode = getSplRateCodeDAO().getSplRateCodeById(
					splRateCode.getId(), "_Temp");
		}

		SplRateCode befSplRateCode = getSplRateCodeDAO().getSplRateCodeById(
				splRateCode.getId(), "");
		SplRateCode oldSplRateCode = splRateCode.getBefImage();

		String[] valueParm = new String[1];
		String[] errParm= new String[1];

		valueParm[0] = splRateCode.getSRType();
		errParm[0] = PennantJavaUtil.getLabel("label_SRType") + ":"+ valueParm[0];

		if (splRateCode.isNew()) { // for New record or new record into work flow

			if (!splRateCode.isWorkflow()) {// With out Work flow only new
											// records
				if (befSplRateCode != null) { // Record Already Exists in the
												// table then error
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41001",errParm,null));
				}
			} else { // with work flow
				
				if (splRateCode.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befSplRateCode != null || tempSplRateCode != null) { // if records already exists
													// in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41001",errParm,null));
					}
				} else { // if records not exists in the Main flow table
					if (befSplRateCode == null || tempSplRateCode != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!splRateCode.isWorkflow()) { // With out Work flow for update
												// and delete

				if (befSplRateCode == null) { // if records not exists in the
												// main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41002",errParm,null));
				}else{

					if (oldSplRateCode != null
							&& !oldSplRateCode.getLastMntOn().equals(
									befSplRateCode.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41003",errParm,null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41004",errParm,null));
						}
					}
				}

			} else {

				if (tempSplRateCode == null) { // if records not exists in the
												// Work flow table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}
				if (tempSplRateCode != null && oldSplRateCode != null
						&& !oldSplRateCode.getLastMntOn().equals(
								tempSplRateCode.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		
		if ("doApprove".equals(StringUtils.trimToEmpty(method))
				|| !splRateCode.isWorkflow()) {
			auditDetail.setBefImage(befSplRateCode);
		}
		logger.debug("Leaving");
		return auditDetail;
	}

}