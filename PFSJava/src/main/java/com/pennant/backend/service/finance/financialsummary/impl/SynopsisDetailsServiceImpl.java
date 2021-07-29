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
package com.pennant.backend.service.finance.financialsummary.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.financialSummary.SynopsisDetailsDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.financialsummary.SynopsisDetails;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.financialsummary.SynopsisDetailsService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>PSLDetail</b>.<br>
 */
public class SynopsisDetailsServiceImpl extends GenericService<SynopsisDetails> implements SynopsisDetailsService {
	private static final Logger logger = LogManager.getLogger(SynopsisDetailsServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private SynopsisDetailsDAO synopsisDetailsDAO;

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
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table PSLDetails/PSLDetails_Temp by
	 * using PSLDetailsDAO's save method b) Update the Record in the table. based on the module workFlow Configuration.
	 * by using PSLDetailsDAO's update method 3) Audit the record in to AuditHeader and AdtPSLDetails by using
	 * auditHeaderDAO.addAudit(auditHeader)
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

		SynopsisDetails synopsisDetails = (SynopsisDetails) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (synopsisDetails.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (synopsisDetails.isNewRecord()) {
			getSynopsisDetailsDAO().save(synopsisDetails, tableType);
		} else {
			getSynopsisDetailsDAO().update(synopsisDetails, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public AuditDetail saveOrUpdate(SynopsisDetails synopsisDetails, TableType tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		String[] fields = PennantJavaUtil.getFieldDetails(synopsisDetails, synopsisDetails.getExcludeFields());

		synopsisDetails.setWorkflowId(0);
		if (synopsisDetails.isNewRecord()) {
			getSynopsisDetailsDAO().save(synopsisDetails, tableType);
		} else {
			getSynopsisDetailsDAO().update(synopsisDetails, tableType);
		}

		logger.debug(Literal.LEAVING);
		return new AuditDetail(auditTranType, 1, fields[0], fields[1], synopsisDetails.getBefImage(), synopsisDetails);

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * PSLDetails by using PSLDetailsDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtPSLDetails by using auditHeaderDAO.addAudit(auditHeader)
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

		SynopsisDetails synopsisDetails = (SynopsisDetails) auditHeader.getAuditDetail().getModelData();
		getSynopsisDetailsDAO().delete(synopsisDetails, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditDetail doApprove(SynopsisDetails synopsisDetails, TableType tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		String[] fields = PennantJavaUtil.getFieldDetails(synopsisDetails, synopsisDetails.getExcludeFields());

		synopsisDetails.setRoleCode("");
		synopsisDetails.setNextRoleCode("");
		synopsisDetails.setTaskId("");
		synopsisDetails.setNextTaskId("");
		synopsisDetails.setWorkflowId(0);

		getSynopsisDetailsDAO().save(synopsisDetails, tableType);

		logger.debug(Literal.LEAVING);
		return new AuditDetail(auditTranType, 1, fields[0], fields[1], synopsisDetails.getBefImage(), synopsisDetails);
	}

	@Override
	public AuditDetail delete(SynopsisDetails synopsisDetails, TableType tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		String[] fields = PennantJavaUtil.getFieldDetails(synopsisDetails, synopsisDetails.getExcludeFields());

		getSynopsisDetailsDAO().delete(synopsisDetails, tableType);

		logger.debug(Literal.LEAVING);
		return new AuditDetail(auditTranType, 1, fields[0], fields[1], synopsisDetails.getBefImage(), synopsisDetails);
	}

	/**
	 * getPSLDetails fetch the details by using PSLDetailsDAO's getPSLDetailsById method.
	 * 
	 * @param finReference
	 *            finReference of the PSLDetail.
	 * @return PSLDetails
	 */
	@Override
	public SynopsisDetails getSynopsisDetails(String finReference) {
		return getSynopsisDetailsDAO().getSynopsisDetails(finReference);
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getPSLDetailDAO().delete with
	 * parameters pSLDetail,"" b) NEW Add new record in to main table by using getPSLDetailDAO().save with parameters
	 * pSLDetail,"" c) EDIT Update record in the main table by using getPSLDetailDAO().update with parameters
	 * pSLDetail,"" 3) Delete the record from the workFlow table by using getPSLDetailDAO().delete with parameters
	 * pSLDetail,"_Temp" 4) Audit the record in to AuditHeader and AdtPSLDetails by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtPSLDetails by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		SynopsisDetails synopsisDetails = new SynopsisDetails();
		BeanUtils.copyProperties((SynopsisDetails) auditHeader.getAuditDetail().getModelData(), synopsisDetails);

		getSynopsisDetailsDAO().delete(synopsisDetails, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(synopsisDetails.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(getSynopsisDetailsDAO().getSynopsisDetails(synopsisDetails.getFinReference()));
		}

		if (synopsisDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getSynopsisDetailsDAO().delete(synopsisDetails, TableType.MAIN_TAB);
		} else {
			synopsisDetails.setRoleCode("");
			synopsisDetails.setNextRoleCode("");
			synopsisDetails.setTaskId("");
			synopsisDetails.setNextTaskId("");
			synopsisDetails.setWorkflowId(0);

			if (synopsisDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				synopsisDetails.setRecordType("");
				getSynopsisDetailsDAO().save(synopsisDetails, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				synopsisDetails.setRecordType("");
				getSynopsisDetailsDAO().update(synopsisDetails, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(synopsisDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getPSLDetailDAO().delete with parameters pSLDetail,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtPSLDetails by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		SynopsisDetails synopsisDetails = (SynopsisDetails) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getSynopsisDetailsDAO().delete(synopsisDetails, TableType.TEMP_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		auditHeader = doValidation(auditHeader, method);
		auditHeader = nextProcess(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getPSLDetailDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
	 * the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditHeader doValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditDetail validate(SynopsisDetails synopsisDetails, String method, String auditTranType,
			String usrLanguage) {
		return doValidation(synopsisDetails, auditTranType, method, usrLanguage);
	}

	public AuditDetail doValidation(SynopsisDetails synopsisDetails, String auditTranType, String method,
			String usrLanguage) {
		logger.debug(Literal.ENTERING);
		String[] fields = PennantJavaUtil.getFieldDetails(synopsisDetails, synopsisDetails.getExcludeFields());

		AuditDetail auditDetail = new AuditDetail(auditTranType, 1, fields[0], fields[1], synopsisDetails.getBefImage(),
				synopsisDetails);

		logger.debug(Literal.LEAVING);
		return validate(auditDetail, usrLanguage, method);
	}

	private AuditDetail validate(AuditDetail auditDetail, String method, String usrLanguage) {
		logger.debug(Literal.ENTERING);
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		SynopsisDetails synopsisDetails = (SynopsisDetails) auditDetail.getModelData();

		SynopsisDetails tempSynopsisDetails = null;
		if (synopsisDetails.isWorkflow()) {
			tempSynopsisDetails = getSynopsisDetailsDAO().getSynopsisDetails(synopsisDetails.getFinReference());

		}
		SynopsisDetails befSynopsisDetails = getSynopsisDetailsDAO()
				.getSynopsisDetails(synopsisDetails.getFinReference());
		SynopsisDetails oldSynopsisDetails = synopsisDetails.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(synopsisDetails.getId());
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (synopsisDetails.isNewRecord()) { // for New record or new record into work flow

			if (!synopsisDetails.isWorkflow()) {// With out Work flow only new
													// records
				if (befSynopsisDetails != null) { // Record Already Exists in the
														// table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (synopsisDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
																									// records
																								// type
																								// is
																								// new
					if (befSynopsisDetails != null || tempSynopsisDetails != null) {
						// if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befSynopsisDetails == null || tempSynopsisDetails != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!synopsisDetails.isWorkflow()) { // With out Work flow for update
														// and delete

				if (befSynopsisDetails == null) { // if records not exists in the
														// main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldSynopsisDetails != null
							&& !oldSynopsisDetails.getLastMntOn().equals(befSynopsisDetails.getLastMntOn())) {
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

				if (tempSynopsisDetails == null) { // if records not exists in
														// the Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempSynopsisDetails != null && oldSynopsisDetails != null
						&& !oldSynopsisDetails.getLastMntOn().equals(tempSynopsisDetails.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !synopsisDetails.isWorkflow()) {
			synopsisDetails.setBefImage(befSynopsisDetails);
		}
		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	public SynopsisDetailsDAO getSynopsisDetailsDAO() {
		return synopsisDetailsDAO;
	}

	public void setSynopsisDetailsDAO(SynopsisDetailsDAO synopsisDetailsDAO) {
		this.synopsisDetailsDAO = synopsisDetailsDAO;
	}
}