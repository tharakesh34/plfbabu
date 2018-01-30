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
 * FileName    		:  InterestRateTypeServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-05-2011    														*
 *                                                                  						*
 * Modified Date    :  06-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-05-2011       Pennant	                 0.1                                            * 
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

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.InterestRateTypeDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.applicationmaster.InterestRateType;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.InterestRateTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;

/**
 * Service implementation for methods that depends on <b>InterestRateType</b>.<br>
 * 
 */
public class InterestRateTypeServiceImpl extends GenericService<InterestRateType> implements InterestRateTypeService {

	private static Logger logger = Logger.getLogger(InterestRateTypeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private InterestRateTypeDAO interestRateTypeDAO;

	public InterestRateTypeServiceImpl() {
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

	public InterestRateTypeDAO getInterestRateTypeDAO() {
		return interestRateTypeDAO;
	}

	public void setInterestRateTypeDAO(InterestRateTypeDAO interestRateTypeDAO) {
		this.interestRateTypeDAO = interestRateTypeDAO;
	}


	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTInterestRateTypes/BMTInterestRateTypes_Temp by using
	 * InterestRateTypeDAO's save method b) Update the Record in the table.
	 * based on the module workFlow Configuration. by using
	 * InterestRateTypeDAO's update method 3) Audit the record in to AuditHeader
	 * and AdtBMTInterestRateTypes by using auditHeaderDAO.addAudit(auditHeader)
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
		InterestRateType interestRateType = (InterestRateType) auditHeader
		.getAuditDetail().getModelData();

		if (interestRateType.isWorkflow()) {
			tableType = "_Temp";
		}

		if (interestRateType.isNew()) {
			interestRateType.setId(getInterestRateTypeDAO().save(interestRateType, tableType));
			auditHeader.getAuditDetail().setModelData(interestRateType);
			auditHeader.setAuditReference(interestRateType.getId());
		} else {
			getInterestRateTypeDAO().update(interestRateType, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTInterestRateTypes by using InterestRateTypeDAO's delete method
	 * with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtBMTInterestRateTypes by using auditHeaderDAO.addAudit(auditHeader)
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
		InterestRateType interestRateType = (InterestRateType) auditHeader.getAuditDetail().getModelData();

		getInterestRateTypeDAO().delete(interestRateType, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getInterestRateTypeById fetch the details by using InterestRateTypeDAO's
	 * getInterestRateTypeById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return InterestRateType
	 */
	@Override
	public InterestRateType getInterestRateTypeById(String id) {
		return getInterestRateTypeDAO().getInterestRateTypeById(id, "_View");
	}

	/**
	 * getApprovedInterestRateTypeById fetch the details by using
	 * InterestRateTypeDAO's getInterestRateTypeById method . with parameter id
	 * and type as blank. it fetches the approved records from the
	 * BMTInterestRateTypes.
	 * 
	 * @param id
	 *            (String)
	 * @return InterestRateType
	 */
	public InterestRateType getApprovedInterestRateTypeById(String id) {
		return getInterestRateTypeDAO().getInterestRateTypeById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getInterestRateTypeDAO().delete with parameters interestRateType,""
	 * b) NEW Add new record in to main table by using
	 * getInterestRateTypeDAO().save with parameters interestRateType,"" c) EDIT
	 * Update record in the main table by using getInterestRateTypeDAO().update
	 * with parameters interestRateType,"" 3) Delete the record from the
	 * workFlow table by using getInterestRateTypeDAO().delete with parameters
	 * interestRateType,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBMTInterestRateTypes by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow 5) Audit the record in to AuditHeader and
	 * AdtBMTInterestRateTypes by using auditHeaderDAO.addAudit(auditHeader)
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
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		InterestRateType interestRateType = new InterestRateType();
		BeanUtils.copyProperties((InterestRateType) auditHeader.getAuditDetail().getModelData(), interestRateType);

		if (interestRateType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getInterestRateTypeDAO().delete(interestRateType, "");
		} else {
			interestRateType.setRoleCode("");
			interestRateType.setNextRoleCode("");
			interestRateType.setTaskId("");
			interestRateType.setNextTaskId("");
			interestRateType.setWorkflowId(0);

			if (interestRateType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				interestRateType.setRecordType("");
				getInterestRateTypeDAO().save(interestRateType, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				interestRateType.setRecordType("");
				getInterestRateTypeDAO().update(interestRateType, "");
			}
		}

		getInterestRateTypeDAO().delete(interestRateType, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(interestRateType);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getInterestRateTypeDAO().delete with
	 * parameters interestRateType,"_Temp" 3) Audit the record in to AuditHeader
	 * and AdtBMTInterestRateTypes by using auditHeaderDAO.addAudit(auditHeader)
	 * for Work flow
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
		InterestRateType interestRateType = (InterestRateType) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getInterestRateTypeDAO().delete(interestRateType, "_Temp");

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
	 * getInterestRateTypeDAO().getErrorDetail with Error ID and language as
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
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());

		InterestRateType interestRateType = (InterestRateType) auditDetail.getModelData();
		InterestRateType tempInterestRateType = null;

		if (interestRateType.isWorkflow()) {
			tempInterestRateType = getInterestRateTypeDAO().getInterestRateTypeById(interestRateType.getId(), "_Temp");
		}

		InterestRateType befInterestRateType = getInterestRateTypeDAO()
		.getInterestRateTypeById(interestRateType.getId(), "");
		InterestRateType oldInterestRateType = interestRateType.getBefImage();

		String[] valueParm = new String[1];
		String[] errParm = new String[1];
		 
		valueParm[0]= PennantStaticListUtil.getlabelDesc(
				StringUtils.trimToEmpty(interestRateType.getIntRateTypeCode()),
				PennantStaticListUtil.getInterestRateType(true));
		errParm[0] = PennantJavaUtil.getLabel("label_IntRateTypeCode") + ":"+ valueParm[0];

		if (interestRateType.isNew()) { // for New record or new record into work flow

			if (!interestRateType.isWorkflow()) {// With out Work flow only new records
				if (befInterestRateType != null) { // Record Already Exists in the table then error
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001",errParm, null));
				}
			} else { // with work flow
				if (interestRateType.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befInterestRateType != null
							|| tempInterestRateType != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,null));
					}
				} else { // if records not exists in the Main flow table
					if (befInterestRateType == null
							|| tempInterestRateType != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}

			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!interestRateType.isWorkflow()) { // With out Work flow for update and delete

				if (befInterestRateType == null) { // if records not exists in the main table
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002",errParm, null));
				} else {

					if (oldInterestRateType != null
							&& !oldInterestRateType.getLastMntOn().equals(befInterestRateType.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003",errParm, null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004",errParm, null));
						}
					}
				}
			} else {
				if (tempInterestRateType == null) { // if records not exists in the Work flow table
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005",errParm, null));
				}
				if (tempInterestRateType != null
						&& oldInterestRateType != null
						&& !oldInterestRateType.getLastMntOn().equals(tempInterestRateType.getLastMntOn())) {
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005",errParm, null));
				}

			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if ("doApprove".equals(StringUtils.trimToEmpty(method))
				|| !interestRateType.isWorkflow()) {
			auditDetail.setBefImage(befInterestRateType);
		}
		logger.debug("Leaving");
		return auditDetail;
	}
}