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
 * FileName    		:  SalesOfficerServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-09-2011    														*
 *                                                                  						*
 * Modified Date    :  12-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-09-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.applicationmaster.SalesOfficerDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.applicationmaster.SalesOfficer;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.SalesOfficerService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>SalesOfficer</b>.<br>
 * 
 */
public class SalesOfficerServiceImpl extends GenericService<SalesOfficer>
		implements SalesOfficerService {
	
	private static final Logger logger = Logger.getLogger(SalesOfficerServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private SalesOfficerDAO salesOfficerDAO;

	public SalesOfficerServiceImpl() {
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

	public SalesOfficerDAO getSalesOfficerDAO() {
		return salesOfficerDAO;
	}
	public void setSalesOfficerDAO(SalesOfficerDAO salesOfficerDAO) {
		this.salesOfficerDAO = salesOfficerDAO;
	}


	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * SalesOfficers/SalesOfficers_Temp by using SalesOfficerDAO's save method
	 * b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using SalesOfficerDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtSalesOfficers by using
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
		SalesOfficer salesOfficer = (SalesOfficer) auditHeader.getAuditDetail()
				.getModelData();

		if (salesOfficer.isWorkflow()) {
			tableType = "_Temp";
		}

		if (salesOfficer.isNew()) {
			getSalesOfficerDAO().save(salesOfficer, tableType);
		} else {
			getSalesOfficerDAO().update(salesOfficer, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table SalesOfficers by using SalesOfficerDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtSalesOfficers by using
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

		SalesOfficer salesOfficer = (SalesOfficer) auditHeader.getAuditDetail()
				.getModelData();
		getSalesOfficerDAO().delete(salesOfficer, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getSalesOfficerById fetch the details by using SalesOfficerDAO's
	 * getSalesOfficerById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return SalesOfficer
	 */

	@Override
	public SalesOfficer getSalesOfficerById(String id) {
		return getSalesOfficerDAO().getSalesOfficerById(id, "_View");
	}

	/**
	 * getApprovedSalesOfficerById fetch the details by using SalesOfficerDAO's
	 * getSalesOfficerById method . with parameter id and type as blank. it
	 * fetches the approved records from the SalesOfficers.
	 * 
	 * @param id
	 *            (String)
	 * @return SalesOfficer
	 */

	public SalesOfficer getApprovedSalesOfficerById(String id) {
		return getSalesOfficerDAO().getSalesOfficerById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getSalesOfficerDAO().delete with parameters salesOfficer,"" b) NEW
	 * Add new record in to main table by using getSalesOfficerDAO().save with
	 * parameters salesOfficer,"" c) EDIT Update record in the main table by
	 * using getSalesOfficerDAO().update with parameters salesOfficer,"" 3)
	 * Delete the record from the workFlow table by using
	 * getSalesOfficerDAO().delete with parameters salesOfficer,"_Temp" 4) Audit
	 * the record in to AuditHeader and AdtSalesOfficers by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in
	 * to AuditHeader and AdtSalesOfficers by using
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
			return auditHeader;
		}

		SalesOfficer salesOfficer = new SalesOfficer();
		BeanUtils.copyProperties((SalesOfficer) auditHeader.getAuditDetail()
				.getModelData(), salesOfficer);

		if (salesOfficer.getRecordType().equals(
				PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getSalesOfficerDAO().delete(salesOfficer, "");

		} else {
			salesOfficer.setRoleCode("");
			salesOfficer.setNextRoleCode("");
			salesOfficer.setTaskId("");
			salesOfficer.setNextTaskId("");
			salesOfficer.setWorkflowId(0);

			if (salesOfficer.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				salesOfficer.setRecordType("");
				getSalesOfficerDAO().save(salesOfficer, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				salesOfficer.setRecordType("");
				getSalesOfficerDAO().update(salesOfficer, "");
			}
		}

		getSalesOfficerDAO().delete(salesOfficer, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(salesOfficer);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getSalesOfficerDAO().delete with parameters
	 * salesOfficer,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtSalesOfficers by using auditHeaderDAO.addAudit(auditHeader) for Work
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

		SalesOfficer salesOfficer = (SalesOfficer) auditHeader.getAuditDetail()
				.getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getSalesOfficerDAO().delete(salesOfficer, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from
	 * the auditHeader. 2) fetch the details from the tables 3) Validate the
	 * Record based on the record details. 4) Validate for any business
	 * validation. 5) for any mismatch conditions Fetch the error details from
	 * getSalesOfficerDAO().getErrorDetail with Error ID and language as
	 * parameters. 6) if any error/Warnings then assign the to auditHeader
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
	 * getSalesOfficerDAO().getErrorDetail with Error ID and language as
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
		
		SalesOfficer salesOfficer = (SalesOfficer) auditDetail.getModelData();
		SalesOfficer tempSalesOfficer = null;
		
		if (salesOfficer.isWorkflow()) {
			tempSalesOfficer = getSalesOfficerDAO().getSalesOfficerById(
					salesOfficer.getId(), "_Temp");
		}
		
		SalesOfficer befSalesOfficer = getSalesOfficerDAO()
				.getSalesOfficerById(salesOfficer.getId(), "");
		SalesOfficer oldSalesOfficer = salesOfficer.getBefImage();

		String[] errParm = new String[2];
		String[] valueParm = new String[2];
		
		valueParm[0] = salesOfficer.getSalesOffCode();
		errParm[0] = PennantJavaUtil.getLabel("label_SalesOffCode") + ":"
				+ valueParm[0];

		if (salesOfficer.isNew()) { // for New record or new record into work
									// flow

			if (!salesOfficer.isWorkflow()) {// With out Work flow only new
												// records
				if (befSalesOfficer != null) { // Record Already Exists in the
												// table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD,
									"41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (salesOfficer.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
																// is new
					if (befSalesOfficer != null || tempSalesOfficer != null) { // if records
																				// already exists
																				// in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD,
										"41001", errParm, valueParm),
								usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befSalesOfficer == null || tempSalesOfficer != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD,
										"41005", errParm, valueParm),
								usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!salesOfficer.isWorkflow()) { // With out Work flow for update
												// and delete

				if (befSalesOfficer == null) { // if records not exists in the
												// main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD,
									"41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldSalesOfficer != null
							&& !oldSalesOfficer.getLastMntOn().equals(
									befSalesOfficer.getLastMntOn())) {
						if (StringUtils.trimToEmpty(
								auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil
									.getErrorDetail(new ErrorDetail(
											PennantConstants.KEY_FIELD,
											"41003", errParm, valueParm),
											usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil
									.getErrorDetail(new ErrorDetail(
											PennantConstants.KEY_FIELD,
											"41004", errParm, valueParm),
											usrLanguage));
						}
					}
				}
			} else {

				if (tempSalesOfficer == null) { // if records not exists in the
												// Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD,
									"41005", errParm, valueParm), usrLanguage));
				}

				if (tempSalesOfficer != null && oldSalesOfficer != null
						&& !oldSalesOfficer.getLastMntOn().equals(
								tempSalesOfficer.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD,
									"41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(
				auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method))
				|| !salesOfficer.isWorkflow()) {
			salesOfficer.setBefImage(befSalesOfficer);
		}
		logger.debug("Leaving");
		return auditDetail;
	}

}