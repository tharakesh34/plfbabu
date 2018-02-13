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
 * FileName    		:  FinanceApplicationCodeServiceImpl.java                                                   * 	  
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

package com.pennant.backend.service.applicationmaster.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.FinanceApplicationCodeDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.applicationmaster.FinanceApplicationCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.AddressType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.FinanceApplicationCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on
 * <b>FinanceApplicationCode</b>.<br>
 * 
 */
public class FinanceApplicationCodeServiceImpl extends
		GenericService<AddressType> implements FinanceApplicationCodeService {

	private static Logger logger = Logger.getLogger(FinanceApplicationCodeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private FinanceApplicationCodeDAO financeApplicationCodeDAO;

	public FinanceApplicationCodeServiceImpl() {
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

	public FinanceApplicationCodeDAO getFinanceApplicationCodeDAO() {
		return financeApplicationCodeDAO;
	}

	public void setFinanceApplicationCodeDAO(
			FinanceApplicationCodeDAO financeApplicationCodeDAO) {
		this.financeApplicationCodeDAO = financeApplicationCodeDAO;
	}


	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTFinAppCodes/BMTFinAppCodes_Temp by using
	 * FinanceApplicationCodeDAO's save method b) Update the Record in the
	 * table. based on the module workFlow Configuration. by using
	 * FinanceApplicationCodeDAO's update method 3) Audit the record in to
	 * AuditHeader and AdtBMTFinAppCodes by using
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
		FinanceApplicationCode financeApplicationCode = (FinanceApplicationCode) auditHeader
				.getAuditDetail().getModelData();

		if (financeApplicationCode.isWorkflow()) {
			tableType = "_Temp";
		}

		if (financeApplicationCode.isNew()) {
			financeApplicationCode.setId(getFinanceApplicationCodeDAO().save(
					financeApplicationCode, tableType));
			auditHeader.getAuditDetail().setModelData(financeApplicationCode);
			auditHeader.setAuditReference(financeApplicationCode.getId());
		} else {
			getFinanceApplicationCodeDAO().update(financeApplicationCode,
					tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTFinAppCodes by using FinanceApplicationCodeDAO's
	 * delete method with type as Blank 3) Audit the record in to AuditHeader
	 * and AdtBMTFinAppCodes by using
	 * auditHeaderDAO.addAudit(auditHeader)
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
		FinanceApplicationCode financeApplicationCode = (FinanceApplicationCode) auditHeader
				.getAuditDetail().getModelData();

		getFinanceApplicationCodeDAO().delete(financeApplicationCode, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getFinanceApplicationCodeById fetch the details by using
	 * FinanceApplicationCodeDAO's getFinanceApplicationCodeById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceApplicationCode
	 */
	@Override
	public FinanceApplicationCode getFinanceApplicationCodeById(String id) {
		return getFinanceApplicationCodeDAO().getFinanceApplicationCodeById(id,
				"_View");
	}

	/**
	 * getApprovedFinanceApplicationCodeById fetch the details by using
	 * FinanceApplicationCodeDAO's getFinanceApplicationCodeById method . with
	 * parameter id and type as blank. it fetches the approved records from the
	 * BMTFinAppCodes.
	 * 
	 * @param id
	 *            (String)
	 * @return FinanceApplicationCode
	 */

	public FinanceApplicationCode getApprovedFinanceApplicationCodeById(
			String id) {
		return getFinanceApplicationCodeDAO().getFinanceApplicationCodeById(id,	"_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getFinanceApplicationCodeDAO().delete with parameters
	 * financeApplicationCode,"" b) NEW Add new record in to main table by using
	 * getFinanceApplicationCodeDAO().save with parameters
	 * financeApplicationCode,"" c) EDIT Update record in the main table by
	 * using getFinanceApplicationCodeDAO().update with parameters
	 * financeApplicationCode,"" 3) Delete the record from the workFlow table by
	 * using getFinanceApplicationCodeDAO().delete with parameters
	 * financeApplicationCode,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBMTFinAppCodes by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in
	 * to AuditHeader and AdtBMTFinAppCodes by using
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
		FinanceApplicationCode financeApplicationCode = new FinanceApplicationCode();
		BeanUtils.copyProperties((FinanceApplicationCode) auditHeader
				.getAuditDetail().getModelData(), financeApplicationCode);

		if (financeApplicationCode.getRecordType().equals(
				PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getFinanceApplicationCodeDAO().delete(financeApplicationCode, "");

		} else {
			financeApplicationCode.setRoleCode("");
			financeApplicationCode.setNextRoleCode("");
			financeApplicationCode.setTaskId("");
			financeApplicationCode.setNextTaskId("");
			financeApplicationCode.setWorkflowId(0);

			if (financeApplicationCode.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				financeApplicationCode.setRecordType("");
				getFinanceApplicationCodeDAO().save(financeApplicationCode, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				financeApplicationCode.setRecordType("");
				getFinanceApplicationCodeDAO().update(financeApplicationCode,
						"");
			}
		}

		getFinanceApplicationCodeDAO().delete(financeApplicationCode, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(financeApplicationCode);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getFinanceApplicationCodeDAO().delete with
	 * parameters financeApplicationCode,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtBMTFinAppCodes by using
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
		FinanceApplicationCode financeApplicationCode = (FinanceApplicationCode) auditHeader
				.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getFinanceApplicationCodeDAO().delete(financeApplicationCode, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from
	 * the auditHeader. 2) fetch the details from the tables 3) Validate the
	 * Record based on the record details. 4) Validate for any business
	 * validation. 5) for any mismatch conditions Fetch the error details from
	 * getFinanceApplicationCodeDAO().getErrorDetail with Error ID and language
	 * as parameters. 6) if any error/Warnings then assign the to auditHeader
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
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getFinanceApplicationCodeDAO().getErrorDetail with Error ID and language
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

		FinanceApplicationCode financeApplicationCode = (FinanceApplicationCode) auditDetail
				.getModelData();
		FinanceApplicationCode tempFinanceApplicationCode = null;

		if (financeApplicationCode.isWorkflow()) {
			tempFinanceApplicationCode = getFinanceApplicationCodeDAO()
					.getFinanceApplicationCodeById(
							financeApplicationCode.getId(), "_Temp");
		}

		FinanceApplicationCode befFinanceApplicationCode = getFinanceApplicationCodeDAO()
				.getFinanceApplicationCodeById(financeApplicationCode.getId(),
						"");
		FinanceApplicationCode oldFinanceApplicationCode = financeApplicationCode
				.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = financeApplicationCode.getFinAppType();
		errParm[0] = PennantJavaUtil.getLabel("label_FinAppType") + ":"
				+ valueParm[0];

		if (financeApplicationCode.isNew()) { // for New record or new record
			// into work flow

			if (!financeApplicationCode.isWorkflow()) {// With out Work flow
				// only new records
				if (befFinanceApplicationCode != null) { // Record Already
					// Exists in the
					// table then error
					auditDetail
							.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41001",
									errParm, null));
				}
			} else { // with work flow

				if (financeApplicationCode.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
					// is new
					if (befFinanceApplicationCode != null
							|| tempFinanceApplicationCode != null) { // if
						  						// records already exists
							 					// in the main table
						auditDetail.setErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41001", errParm,
								null));
					}
				} else { // if records not exists in the Main flow table
					if (befFinanceApplicationCode == null
							|| tempFinanceApplicationCode != null) {
						auditDetail.setErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41005", errParm,
								null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!financeApplicationCode.isWorkflow()) { // With out Work flow
				// for update and delete

				if (befFinanceApplicationCode == null) { // if records not
					// exists in the
					// main table
					auditDetail
							.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41002",
									errParm, null));
				} else {
					if (oldFinanceApplicationCode != null
							&& !oldFinanceApplicationCode.getLastMntOn()
									.equals(befFinanceApplicationCode
											.getLastMntOn())) {
						if (StringUtils.trimToEmpty(
								auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41003",
									errParm, null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41004",
									errParm, null));
						}
					}
				}

			} else {

				if (tempFinanceApplicationCode == null) { // if records not
					// exists in the
					// Work flow table
					auditDetail
							.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41005",
									errParm, null));
				}

				if (tempFinanceApplicationCode != null
						&& oldFinanceApplicationCode != null
						&& !oldFinanceApplicationCode.getLastMntOn().equals(
								tempFinanceApplicationCode.getLastMntOn())) {
					auditDetail
							.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41005",
									errParm, null));
				}
			}
		}
		
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(
				auditDetail.getErrorDetails(), usrLanguage));
		if ("doApprove".equals(StringUtils.trimToEmpty(method))
				|| !financeApplicationCode.isWorkflow()) {
			auditDetail.setBefImage(befFinanceApplicationCode);
		}
		logger.debug("Leaving");
		return auditDetail;
	}

}