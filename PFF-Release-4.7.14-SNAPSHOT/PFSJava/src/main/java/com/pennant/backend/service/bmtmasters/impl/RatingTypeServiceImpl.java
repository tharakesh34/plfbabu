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
 * FileName    		:  RatingTypeServiceImpl.java                                                   * 	  
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
import com.pennant.backend.dao.bmtmasters.RatingTypeDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.RatingType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.bmtmasters.RatingTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>RatingType</b>.<br>
 * 
 */
public class RatingTypeServiceImpl extends GenericService<RatingType> implements RatingTypeService {

	private static Logger logger = Logger.getLogger(RatingTypeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private RatingTypeDAO ratingTypeDAO;

	public RatingTypeServiceImpl() {
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

	public RatingTypeDAO getRatingTypeDAO() {
		return ratingTypeDAO;
	}

	public void setRatingTypeDAO(RatingTypeDAO ratingTypeDAO) {
		this.ratingTypeDAO = ratingTypeDAO;
	}


	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTRatingTypes/BMTRatingTypes_Temp by using RatingTypeDAO's save method
	 * b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using RatingTypeDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtBMTRatingTypes by using
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
		RatingType ratingType = (RatingType) auditHeader.getAuditDetail().getModelData();
		if (ratingType.isWorkflow()) {
			tableType = "_Temp";
		}
		if (ratingType.isNew()) {
			ratingType.setId(getRatingTypeDAO().save(ratingType, tableType));
			auditHeader.getAuditDetail().setModelData(ratingType);
			auditHeader.setAuditReference(ratingType.getRatingType());
		} else {
			getRatingTypeDAO().update(ratingType, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTRatingTypes by using RatingTypeDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtBMTRatingTypes by
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
		RatingType ratingType = (RatingType) auditHeader.getAuditDetail().getModelData();
		getRatingTypeDAO().delete(ratingType, "");
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getRatingTypeById fetch the details by using RatingTypeDAO's
	 * getRatingTypeById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return RatingType
	 */
	@Override
	public RatingType getRatingTypeById(String id) {
		return getRatingTypeDAO().getRatingTypeById(id, "_View");
	}

	/**
	 * getApprovedRatingTypeById fetch the details by using RatingTypeDAO's
	 * getRatingTypeById method . with parameter id and type as blank. it
	 * fetches the approved records from the BMTRatingTypes.
	 * 
	 * @param id
	 *            (String)
	 * @return RatingType
	 */
	public RatingType getApprovedRatingTypeById(String id) {
		return getRatingTypeDAO().getRatingTypeById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getRatingTypeDAO().delete with parameters ratingType,"" b) NEW Add
	 * new record in to main table by using getRatingTypeDAO().save with
	 * parameters ratingType,"" c) EDIT Update record in the main table by using
	 * getRatingTypeDAO().update with parameters ratingType,"" 3) Delete the
	 * record from the workFlow table by using getRatingTypeDAO().delete with
	 * parameters ratingType,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBMTRatingTypes by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtBMTRatingTypes by using
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
		RatingType ratingType = new RatingType();
		BeanUtils.copyProperties((RatingType) auditHeader.getAuditDetail().getModelData(), ratingType);
		if (ratingType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getRatingTypeDAO().delete(ratingType, "");
		} else {
			ratingType.setRoleCode("");
			ratingType.setNextRoleCode("");
			ratingType.setTaskId("");
			ratingType.setNextTaskId("");
			ratingType.setWorkflowId(0);

			if (ratingType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				ratingType.setRecordType("");
				getRatingTypeDAO().save(ratingType, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				ratingType.setRecordType("");
				getRatingTypeDAO().update(ratingType, "");
			}
		}

		getRatingTypeDAO().delete(ratingType, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(ratingType);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getRatingTypeDAO().delete with parameters
	 * ratingType,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtBMTRatingTypes by using auditHeaderDAO.addAudit(auditHeader) for Work
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
		RatingType ratingType = (RatingType) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getRatingTypeDAO().delete(ratingType, "_Temp");

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

		RatingType ratingType = (RatingType) auditDetail.getModelData();
		RatingType tempRatingType = null;

		if (ratingType.isWorkflow()) {
			tempRatingType = getRatingTypeDAO().getRatingTypeById(ratingType.getId(), "_Temp");
		}

		RatingType befRatingType = getRatingTypeDAO().getRatingTypeById(ratingType.getId(), "");
		RatingType oldRatingType = ratingType.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = ratingType.getRatingType();
		errParm[0] = PennantJavaUtil.getLabel("label_RatingType") + ":"+ valueParm[0];

		if (ratingType.isNew()) { // for New record or new record into work flow

			if (!ratingType.isWorkflow()) {// With out Work flow only new records
				if (befRatingType != null) { // Record Already Exists in the table then error
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001",errParm, null));
				}
			} else { // with work flow
				if (ratingType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befRatingType != null || tempRatingType != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,null));
					}
				} else { // if records not exists in the Main flow table
					if (befRatingType == null || tempRatingType != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!ratingType.isWorkflow()) { // With out Work flow for update and delete

				if (befRatingType == null) { // if records not exists in the main table
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002",errParm, null));
				} else {
					if (oldRatingType != null
							&& !oldRatingType.getLastMntOn().equals(befRatingType.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003",errParm, null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004",errParm, null));
						}
					}
				}
			} else {
				if (tempRatingType == null) { // if records not exists in the Work flow table
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005",errParm, null));
				}

				if (tempRatingType != null
						&& oldRatingType != null
						&& !oldRatingType.getLastMntOn().equals(tempRatingType.getLastMntOn())) {
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005",errParm, null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if ("doApprove".equals(StringUtils.trimToEmpty(method))|| !ratingType.isWorkflow()) {
			auditDetail.setBefImage(befRatingType);
		}
		logger.debug("Leaving");
		return auditDetail;
	}
}