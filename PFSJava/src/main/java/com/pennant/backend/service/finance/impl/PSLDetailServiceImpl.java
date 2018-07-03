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
 * FileName    		:  PSLDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-06-2018    														*
 *                                                                  						*
 * Modified Date    :  20-06-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 20-06-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.service.finance.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.psl.PSLDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.psl.PSLDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.PSLDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>PSLDetail</b>.<br>
 */
public class PSLDetailServiceImpl extends GenericService<PSLDetail> implements PSLDetailService {
	private static final Logger logger = Logger.getLogger(PSLDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private PSLDetailDAO pSLDetailDAO;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	/**
	 * @return the auditHeaderDAO
	 */
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	/**
	 * @param auditHeaderDAO
	 *            the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	/**
	 * @return the pSLDetailDAO
	 */
	public PSLDetailDAO getPSLDetailDAO() {
		return pSLDetailDAO;
	}

	/**
	 * @param pSLDetailDAO
	 *            the pSLDetailDAO to set
	 */
	public void setPSLDetailDAO(PSLDetailDAO pSLDetailDAO) {
		this.pSLDetailDAO = pSLDetailDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * PSLDetails/PSLDetails_Temp by using PSLDetailsDAO's save method b) Update
	 * the Record in the table. based on the module workFlow Configuration. by
	 * using PSLDetailsDAO's update method 3) Audit the record in to AuditHeader
	 * and AdtPSLDetails by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		PSLDetail pSLDetail = (PSLDetail) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (pSLDetail.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (pSLDetail.isNew()) {
			getPSLDetailDAO().save(pSLDetail, tableType);
		} else {
			getPSLDetailDAO().update(pSLDetail, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}
	
	@Override
	public AuditDetail saveOrUpdate(PSLDetail pslDetail, TableType tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		String[] fields = PennantJavaUtil.getFieldDetails(pslDetail, pslDetail.getExcludeFields());

		pslDetail.setWorkflowId(0);
		if (pslDetail.isNewRecord()) {
			getPSLDetailDAO().save(pslDetail, tableType);
		} else {
			getPSLDetailDAO().update(pslDetail, tableType);
		}

		logger.debug(Literal.LEAVING);
		return new AuditDetail(auditTranType, 1, fields[0], fields[1], pslDetail.getBefImage(), pslDetail);

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table PSLDetails by using PSLDetailsDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtPSLDetails by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		PSLDetail pSLDetail = (PSLDetail) auditHeader.getAuditDetail().getModelData();
		getPSLDetailDAO().delete(pSLDetail, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	
	@Override
	public AuditDetail doApprove(PSLDetail pslDetail, TableType tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		String[] fields = PennantJavaUtil.getFieldDetails(pslDetail, pslDetail.getExcludeFields());

		pslDetail.setRoleCode("");
		pslDetail.setNextRoleCode("");
		pslDetail.setTaskId("");
		pslDetail.setNextTaskId("");
		pslDetail.setWorkflowId(0);

		getPSLDetailDAO().save(pslDetail, tableType);

		logger.debug(Literal.LEAVING);
		return new  AuditDetail(auditTranType, 1, fields[0], fields[1], pslDetail.getBefImage(), pslDetail);
	}
	
	@Override
	public AuditDetail delete(PSLDetail pslDetail, TableType tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		String[] fields = PennantJavaUtil.getFieldDetails(pslDetail, pslDetail.getExcludeFields());	

		getPSLDetailDAO().delete(pslDetail, tableType);

		logger.debug(Literal.LEAVING);
		return new  AuditDetail(auditTranType, 1, fields[0], fields[1], pslDetail.getBefImage(), pslDetail);
	}
	
	
	/**
	 * getPSLDetails fetch the details by using PSLDetailsDAO's
	 * getPSLDetailsById method.
	 * 
	 * @param finReference
	 *            finReference of the PSLDetail.
	 * @return PSLDetails
	 */
	@Override
	public PSLDetail getPSLDetail(String finReference) {
		return getPSLDetailDAO().getPSLDetail(finReference, "_View");
	}

	/**
	 * getApprovedPSLDetailsById fetch the details by using PSLDetailsDAO's
	 * getPSLDetailsById method . with parameter id and type as blank. it
	 * fetches the approved records from the PSLDetails.
	 * 
	 * @param finReference
	 *            finReference of the PSLDetail. (String)
	 * @return PSLDetails
	 */
	public PSLDetail getApprovedPSLDetail(String finReference) {
		return getPSLDetailDAO().getPSLDetail(finReference, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getPSLDetailDAO().delete with parameters pSLDetail,"" b) NEW Add
	 * new record in to main table by using getPSLDetailDAO().save with
	 * parameters pSLDetail,"" c) EDIT Update record in the main table by using
	 * getPSLDetailDAO().update with parameters pSLDetail,"" 3) Delete the
	 * record from the workFlow table by using getPSLDetailDAO().delete with
	 * parameters pSLDetail,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtPSLDetails by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5) Audit the record in to AuditHeader and AdtPSLDetails by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		PSLDetail pSLDetail = new PSLDetail();
		BeanUtils.copyProperties((PSLDetail) auditHeader.getAuditDetail().getModelData(), pSLDetail);

		getPSLDetailDAO().delete(pSLDetail, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(pSLDetail.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(pSLDetailDAO.getPSLDetail(pSLDetail.getFinReference(), ""));
		}

		if (pSLDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getPSLDetailDAO().delete(pSLDetail, TableType.MAIN_TAB);
		} else {
			pSLDetail.setRoleCode("");
			pSLDetail.setNextRoleCode("");
			pSLDetail.setTaskId("");
			pSLDetail.setNextTaskId("");
			pSLDetail.setWorkflowId(0);

			if (pSLDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				pSLDetail.setRecordType("");
				getPSLDetailDAO().save(pSLDetail, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				pSLDetail.setRecordType("");
				getPSLDetailDAO().update(pSLDetail, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(pSLDetail);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	
	
	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getPSLDetailDAO().delete with parameters
	 * pSLDetail,"_Temp" 3) Audit the record in to AuditHeader and AdtPSLDetails
	 * by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		PSLDetail pSLDetail = (PSLDetail) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getPSLDetailDAO().delete(pSLDetail, TableType.TEMP_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
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
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		auditHeader = doValidation(auditHeader, method);
		auditHeader=nextProcess(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getPSLDetailDAO().getErrorDetail with Error ID and language as
	 * parameters. if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditHeader doValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);
		
		AuditDetail auditDetail =   validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditDetail validate(PSLDetail pslDetail, String method, String auditTranType, String usrLanguage) {
		return doValidation(pslDetail, auditTranType, method, usrLanguage);
	}

	
	public AuditDetail doValidation(PSLDetail pslDetail, String auditTranType, String method,String  usrLanguage){
		logger.debug(Literal.ENTERING);
		String[] fields = PennantJavaUtil.getFieldDetails(pslDetail, pslDetail.getExcludeFields());
		
		AuditDetail auditDetail = new AuditDetail(auditTranType, 1, fields[0], fields[1], pslDetail.getBefImage(), pslDetail);
		
		logger.debug(Literal.LEAVING);
		return validate(auditDetail, usrLanguage, method);
	}
	
	
	
	private AuditDetail validate(AuditDetail auditDetail, String method, String usrLanguage) {
		logger.debug(Literal.ENTERING);
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		PSLDetail pslDetail = (PSLDetail) auditDetail.getModelData();

		PSLDetail tempPslDetails = null;
		if (pslDetail.isWorkflow()) {
			tempPslDetails = getPSLDetailDAO().getPSLDetail(pslDetail.getFinReference(), "_Temp");

		}
		PSLDetail befPSLDetail = getPSLDetailDAO().getPSLDetail(pslDetail.getFinReference(), "");
		PSLDetail oldpslDetail = pslDetail.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(pslDetail.getId());
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (pslDetail.isNew()) { // for New record or new record into work flow

			if (!pslDetail.isWorkflow()) {// With out Work flow only new
											// records
				if (befPSLDetail != null) { // Record Already Exists in the
											// table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (pslDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
																							// records
																							// type
																							// is
																							// new
					if (befPSLDetail != null || tempPslDetails != null) {
						// if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befPSLDetail == null || tempPslDetails != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!pslDetail.isWorkflow()) { // With out Work flow for update
											// and delete

				if (befPSLDetail == null) { // if records not exists in the
											// main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldpslDetail != null && !oldpslDetail.getLastMntOn().equals(befPSLDetail.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, valueParm),
									usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, valueParm),
									usrLanguage));
						}
					}
				}
			} else {

				if (tempPslDetails == null) { // if records not exists in
												// the Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempPslDetails != null && oldpslDetail != null
						&& !oldpslDetail.getLastMntOn().equals(tempPslDetails.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !pslDetail.isWorkflow()) {
			pslDetail.setBefImage(befPSLDetail);
		}
		logger.debug(Literal.LEAVING);
		return auditDetail;
	}
}