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
 * FileName    		:  InterestRateBasisCodeServiceImpl.java                                                   * 	  
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

package com.pennant.backend.service.staticparms.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.staticparms.InterestRateBasisCodeDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.staticparms.InterestRateBasisCode;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.staticparms.InterestRateBasisCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on
 * <b>InterestRateBasisCode</b>.<br>
 * 
 */
public class InterestRateBasisCodeServiceImpl extends GenericService<InterestRateBasisCode> implements InterestRateBasisCodeService {

	private static Logger logger = Logger.getLogger(InterestRateBasisCodeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private InterestRateBasisCodeDAO interestRateBasisCodeDAO;

	public InterestRateBasisCodeServiceImpl() {
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

	public InterestRateBasisCodeDAO getInterestRateBasisCodeDAO() {
		return interestRateBasisCodeDAO;
	}

	public void setInterestRateBasisCodeDAO(InterestRateBasisCodeDAO interestRateBasisCodeDAO) {
		this.interestRateBasisCodeDAO = interestRateBasisCodeDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTIntRateBasisCodes/BMTIntRateBasisCodes_Temp by using
	 * InterestRateBasisCodeDAO's save method b) Update the Record in the table.
	 * based on the module workFlow Configuration. by using
	 * InterestRateBasisCodeDAO's update method 3) Audit the record in to
	 * AuditHeader and AdtBMTIntRateBasisCodes by using
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
		InterestRateBasisCode interestRateBasisCode = (InterestRateBasisCode) auditHeader.getAuditDetail().getModelData();

		if (interestRateBasisCode.isWorkflow()) {
			tableType = "_Temp";
		}

		if (interestRateBasisCode.isNew()) {
			interestRateBasisCode.setId(getInterestRateBasisCodeDAO().save(interestRateBasisCode, tableType));
			auditHeader.getAuditDetail().setModelData(interestRateBasisCode);
			auditHeader.setAuditReference(interestRateBasisCode.getId());
		} else {
			getInterestRateBasisCodeDAO().update(interestRateBasisCode,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTIntRateBasisCodes by using InterestRateBasisCodeDAO's delete
	 * method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtBMTIntRateBasisCodes by using auditHeaderDAO.addAudit(auditHeader)
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
		InterestRateBasisCode interestRateBasisCode = (InterestRateBasisCode) auditHeader.getAuditDetail().getModelData();

		getInterestRateBasisCodeDAO().delete(interestRateBasisCode, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getInterestRateBasisCodeById fetch the details by using
	 * InterestRateBasisCodeDAO's getInterestRateBasisCodeById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return InterestRateBasisCode
	 */
	@Override
	public InterestRateBasisCode getInterestRateBasisCodeById(String id) {
		return getInterestRateBasisCodeDAO().getInterestRateBasisCodeById(id,"_View");
	}

	/**
	 * getApprovedInterestRateBasisCodeById fetch the details by using
	 * InterestRateBasisCodeDAO's getInterestRateBasisCodeById method . with
	 * parameter id and type as blank. it fetches the approved records from the
	 * BMTIntRateBasisCodes.
	 * 
	 * @param id
	 *            (String)
	 * @return InterestRateBasisCode
	 */
	public InterestRateBasisCode getApprovedInterestRateBasisCodeById(String id) {
		return getInterestRateBasisCodeDAO().getInterestRateBasisCodeById(id,"_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getInterestRateBasisCodeDAO().delete with parameters
	 * interestRateBasisCode,"" b) NEW Add new record in to main table by using
	 * getInterestRateBasisCodeDAO().save with parameters
	 * interestRateBasisCode,"" c) EDIT Update record in the main table by using
	 * getInterestRateBasisCodeDAO().update with parameters
	 * interestRateBasisCode,"" 3) Delete the record from the workFlow table by
	 * using getInterestRateBasisCodeDAO().delete with parameters
	 * interestRateBasisCode,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBMTIntRateBasisCodes by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow 5) Audit the record in to AuditHeader and
	 * AdtBMTIntRateBasisCodes by using auditHeaderDAO.addAudit(auditHeader)
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
		InterestRateBasisCode interestRateBasisCode = new InterestRateBasisCode();
		BeanUtils.copyProperties((InterestRateBasisCode) auditHeader.getAuditDetail().getModelData(), interestRateBasisCode);

		if (interestRateBasisCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getInterestRateBasisCodeDAO().delete(interestRateBasisCode, "");
		} else {
			interestRateBasisCode.setRoleCode("");
			interestRateBasisCode.setNextRoleCode("");
			interestRateBasisCode.setTaskId("");
			interestRateBasisCode.setNextTaskId("");
			interestRateBasisCode.setWorkflowId(0);
			if (interestRateBasisCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				interestRateBasisCode.setRecordType("");
				getInterestRateBasisCodeDAO().save(interestRateBasisCode, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				interestRateBasisCode.setRecordType("");
				getInterestRateBasisCodeDAO().update(interestRateBasisCode, "");
			}
		}

		getInterestRateBasisCodeDAO().delete(interestRateBasisCode, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(interestRateBasisCode);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getInterestRateBasisCodeDAO().delete with
	 * parameters interestRateBasisCode,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtBMTIntRateBasisCodes by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow
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
		InterestRateBasisCode interestRateBasisCode = (InterestRateBasisCode) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getInterestRateBasisCodeDAO().delete(interestRateBasisCode, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from
	 * the auditHeader. 2) fetch the details from the tables 3) Validate the
	 * Record based on the record details. 4) Validate for any business
	 * validation. 5) for any mismatch conditions Fetch the error details from
	 * getInterestRateBasisCodeDAO().getErrorDetail with Error ID and language
	 * as parameters. 6) if any error/Warnings then assign the to auditHeader
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
	 * getInterestRateBasisCodeDAO().getErrorDetail with Error ID and language
	 * as parameters. if any error/Warnings then assign the to auditDeail Object
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

		InterestRateBasisCode interestRateBasisCode = (InterestRateBasisCode) auditDetail.getModelData();
		InterestRateBasisCode tempInterestRateBasisCode = null;

		if (interestRateBasisCode.isWorkflow()) {
			tempInterestRateBasisCode = getInterestRateBasisCodeDAO()
			.getInterestRateBasisCodeById(interestRateBasisCode.getId(), "_Temp");
		}

		InterestRateBasisCode befInterestRateBasisCode = getInterestRateBasisCodeDAO()
		.getInterestRateBasisCodeById(interestRateBasisCode.getId(), "");
		InterestRateBasisCode oldInterestRateBasisCode = interestRateBasisCode
		.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = interestRateBasisCode.getIntRateBasisCode();
		errParm[0] = PennantJavaUtil.getLabel("label_IntRateBasisCode") + ":"
		+ valueParm[0];

		if (interestRateBasisCode.isNew()) { // for New record or new record
			// into work flow

			if (!interestRateBasisCode.isWorkflow()) {// With out Work flow only new records
				if (befInterestRateBasisCode != null) { // Record Already Exists in the table then error
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001",errParm, null));
				}
			} else { // with work flow

				if (interestRateBasisCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befInterestRateBasisCode != null
							|| tempInterestRateBasisCode != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,null));
					}
				} else { // if records not exists in the Main flow table
					if (befInterestRateBasisCode == null
							|| tempInterestRateBasisCode != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!interestRateBasisCode.isWorkflow()) { // With out Work flow for update and delete

				if (befInterestRateBasisCode == null) { // if records not exists in the main table
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002",errParm, null));
				} else {
					if (oldInterestRateBasisCode != null
							&& !oldInterestRateBasisCode.getLastMntOn()
							.equals(befInterestRateBasisCode.getLastMntOn())) {
						if (StringUtils.trimToEmpty(
								auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003",errParm, null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004",errParm, null));
						}
					}
				}
			} else {
				if (tempInterestRateBasisCode == null) { // if records not exists in the Work flow table
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005",errParm, null));
				}
				if (tempInterestRateBasisCode != null
						&& oldInterestRateBasisCode != null
						&& !oldInterestRateBasisCode.getLastMntOn().equals(tempInterestRateBasisCode.getLastMntOn())) {
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005",errParm, null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if ("doApprove".equals(StringUtils.trimToEmpty(method))
				|| !interestRateBasisCode.isWorkflow()) {
			auditDetail.setBefImage(befInterestRateBasisCode);
		}
		logger.debug("Leaving");
		return auditDetail;
	}

}