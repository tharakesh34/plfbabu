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
 * FileName    		:  RatingCodeServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.bmtmasters.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.bmtmasters.RatingCodeDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.RatingCode;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.bmtmasters.RatingCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>RatingCode</b>.<br>
 * 
 */
public class RatingCodeServiceImpl extends GenericService<RatingCode> implements RatingCodeService {

	private static Logger logger = Logger.getLogger(RatingCodeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private RatingCodeDAO ratingCodeDAO;

	public RatingCodeServiceImpl() {
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

	public RatingCodeDAO getRatingCodeDAO() {
		return ratingCodeDAO;
	}

	public void setRatingCodeDAO(RatingCodeDAO ratingCodeDAO) {
		this.ratingCodeDAO = ratingCodeDAO;
	}
	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTRatingCodes/BMTRatingCodes_Temp by using RatingCodeDAO's save method
	 * b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using RatingCodeDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtBMTRatingCodes by using
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
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType = "";
		RatingCode ratingCode = (RatingCode) auditHeader.getAuditDetail().getModelData();

		if (ratingCode.isWorkflow()) {
			tableType = "_Temp";
		}
		if (ratingCode.isNew()) {
			ratingCode.setRatingCode(getRatingCodeDAO().save(ratingCode,tableType));
			auditHeader.getAuditDetail().setModelData(ratingCode);
			auditHeader.setAuditReference(ratingCode.getRatingCode());
		} else {
			getRatingCodeDAO().update(ratingCode, tableType);
		}
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTRatingCodes by using RatingCodeDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtBMTRatingCodes by
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
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		RatingCode ratingCode = (RatingCode) auditHeader.getAuditDetail().getModelData();
		getRatingCodeDAO().delete(ratingCode, "");
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getRatingCodeById fetch the details by using RatingCodeDAO's
	 * getRatingCodeById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return RatingCode
	 */
	@Override
	public RatingCode getRatingCodeById(String ratingType, String ratingCode) {
		return getRatingCodeDAO().getRatingCodeById(ratingType, ratingCode, "_View");
	}

	/**
	 * getApprovedRatingCodeById fetch the details by using RatingCodeDAO's
	 * getRatingCodeById method . with parameter id and type as blank. it
	 * fetches the approved records from the BMTRatingCodes.
	 * 
	 * @param id
	 *            (String)
	 * @return RatingCode
	 */
	public RatingCode getApprovedRatingCodeById(String ratingType,String ratingCode) {
		return getRatingCodeDAO().getRatingCodeById(ratingType, ratingCode, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getRatingCodeDAO().delete with parameters ratingCode,"" b) NEW Add
	 * new record in to main table by using getRatingCodeDAO().save with
	 * parameters ratingCode,"" c) EDIT Update record in the main table by using
	 * getRatingCodeDAO().update with parameters ratingCode,"" 3) Delete the
	 * record from the workFlow table by using getRatingCodeDAO().delete with
	 * parameters ratingCode,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBMTRatingCodes by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtBMTRatingCodes by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		RatingCode ratingCode = new RatingCode();
		BeanUtils.copyProperties((RatingCode) auditHeader.getAuditDetail().getModelData(), ratingCode);
		if (ratingCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getRatingCodeDAO().delete(ratingCode, "");

		} else {
			ratingCode.setRoleCode("");
			ratingCode.setNextRoleCode("");
			ratingCode.setTaskId("");
			ratingCode.setNextTaskId("");
			ratingCode.setWorkflowId(0);

			if (ratingCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				ratingCode.setRecordType("");
				getRatingCodeDAO().save(ratingCode, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				ratingCode.setRecordType("");
				getRatingCodeDAO().update(ratingCode, "");
			}
		}

		getRatingCodeDAO().delete(ratingCode, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(ratingCode);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getRatingCodeDAO().delete with parameters
	 * ratingCode,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtBMTRatingCodes by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		RatingCode ratingCode = (RatingCode) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getRatingCodeDAO().delete(ratingCode, "_Temp");

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
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getAcademicDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage,
			String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());

		RatingCode ratingCode = (RatingCode) auditDetail.getModelData();
		RatingCode tempRatingCode = null;

		if (ratingCode.isWorkflow()) {
			tempRatingCode = getRatingCodeDAO().getRatingCodeById(ratingCode.getRatingType(), ratingCode.getRatingCode(),"_Temp");
		}

		RatingCode befRatingCode = getRatingCodeDAO().getRatingCodeById(ratingCode.getRatingType(), ratingCode.getRatingCode(), "");
		RatingCode oldRatingCode = ratingCode.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = ratingCode.getRatingType();
		valueParm[1] = ratingCode.getRatingCode();

		errParm[0] = PennantJavaUtil.getLabel("label_RatingType") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_RatingCode") + ":"+ valueParm[1];

		if (ratingCode.isNew()) { // for New record or new record into work flow

			if (!ratingCode.isWorkflow()) {// With out Work flow only new records
				if (befRatingCode != null) { // Record Already Exists in the table then error
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001",errParm, null));
				}
			} else { // with work flow

				if (ratingCode.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befRatingCode != null || tempRatingCode != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,null));
					}
				} else { // if records not exists in the Main flow table
					if (befRatingCode == null || tempRatingCode != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!ratingCode.isWorkflow()) { // With out Work flow for update and delete

				if (befRatingCode == null) { // if records not exists in the main table
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002",errParm, null));
				} else {
					if (oldRatingCode != null
							&& !oldRatingCode.getLastMntOn().equals(befRatingCode.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003",errParm, null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004",errParm, null));
						}
					}
				}
			} else {

				if (tempRatingCode == null) { // if records not exists in the Work flow table
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005",errParm, null));
				}

				if (tempRatingCode != null
						&& oldRatingCode != null
						&& !oldRatingCode.getLastMntOn().equals(tempRatingCode.getLastMntOn())) {
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005",errParm, null));
				}

			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if ("doApprove".equals(StringUtils.trimToEmpty(method))|| !ratingCode.isWorkflow()) {
			auditDetail.setBefImage(befRatingCode);
		}
		logger.debug("Leaving");
		return auditDetail;
	}
}