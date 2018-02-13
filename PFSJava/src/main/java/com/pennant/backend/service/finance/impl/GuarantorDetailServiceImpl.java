/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ******************************************************************************************** 
 * FILE HEADER *
 ******************************************************************************************** 
 * * FileName : GuarantorDetailServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 10-09-2013 * *
 * Modified Date : 10-09-2013 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 10-09-2013 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.GuarantorDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceExposure;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.GuarantorDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>GuarantorDetail</b>.<br>
 * 
 */
public class GuarantorDetailServiceImpl extends GenericService<GuarantorDetail> implements  GuarantorDetailService {
	private static final Logger logger = Logger.getLogger(GuarantorDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private GuarantorDetailDAO guarantorDetailDAO;
	private CustomerDAO customerDAO;

	public GuarantorDetailServiceImpl() {
		super();
	}
	
	@Override
	public GuarantorDetail getGuarantorDetail() {
		return getGuarantorDetailDAO().getGuarantorDetail();
	}

	/**
	 * @return the guarantorDetail for New Record
	 */
	@Override
	public GuarantorDetail getNewGuarantorDetail() {
		return getGuarantorDetailDAO().getNewGuarantorDetail();
	}

	
	public CustomerDAO getCustomerDAO() {
    	return customerDAO;
    }

	public void setCustomerDAO(CustomerDAO customerDAO) {
    	this.customerDAO = customerDAO;
    }

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * FinGuarantorsDetails/FinGuarantorsDetails_Temp by using GuarantorDetailDAO's save method b) Update the Record in
	 * the table. based on the module workFlow Configuration. by using GuarantorDetailDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtFinGuarantorsDetails by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		return saveOrUpdate(auditHeader, false);
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * FinGuarantorsDetails/FinGuarantorsDetails_Temp by using GuarantorDetailDAO's save method b) Update the Record in
	 * the table. based on the module workFlow Configuration. by using GuarantorDetailDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtFinGuarantorsDetails by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */

	private AuditHeader saveOrUpdate(AuditHeader auditHeader, boolean online) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "saveOrUpdate", online);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType = "";
		GuarantorDetail guarantorDetail = (GuarantorDetail) auditHeader.getAuditDetail()
		.getModelData();

		if (guarantorDetail.isWorkflow()) {
			tableType = "_Temp";
		}

		if (guarantorDetail.isNew()) {
			guarantorDetail.setId(getGuarantorDetailDAO().save(guarantorDetail, tableType));
			auditHeader.getAuditDetail().setModelData(guarantorDetail);
			auditHeader.setAuditReference(String.valueOf(guarantorDetail.getGuarantorId()));
		} else {
			getGuarantorDetailDAO().update(guarantorDetail, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * FinGuarantorsDetails by using GuarantorDetailDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtFinGuarantorsDetails by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "delete", false);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		GuarantorDetail guarantorDetail = (GuarantorDetail) auditHeader.getAuditDetail()
		.getModelData();
		getGuarantorDetailDAO().delete(guarantorDetail, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getGuarantorDetailById fetch the details by using GuarantorDetailDAO's getGuarantorDetailById method.
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return GuarantorDetail
	 */

	@Override
	public GuarantorDetail getGuarantorDetailById(long id) {
		return getGuarantorDetailDAO().getGuarantorDetailById(id, "_View");
	}

	/**
	 * getApprovedGuarantorDetailById fetch the details by using GuarantorDetailDAO's getGuarantorDetailById method .
	 * with parameter id and type as blank. it fetches the approved records from the FinGuarantorsDetails.
	 * 
	 * @param id
	 *            (int)
	 * @return GuarantorDetail
	 */

	public GuarantorDetail getApprovedGuarantorDetailById(long id) {
		return getGuarantorDetailDAO().getGuarantorDetailById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getGuarantorDetailDAO().delete with
	 * parameters guarantorDetail,"" b) NEW Add new record in to main table by using getGuarantorDetailDAO().save with
	 * parameters guarantorDetail,"" c) EDIT Update record in the main table by using getGuarantorDetailDAO().update
	 * with parameters guarantorDetail,"" 3) Delete the record from the workFlow table by using
	 * getGuarantorDetailDAO().delete with parameters guarantorDetail,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtFinGuarantorsDetails by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to
	 * AuditHeader and AdtFinGuarantorsDetails by using auditHeaderDAO.addAudit(auditHeader) based on the transaction
	 * Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove", false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		GuarantorDetail guarantorDetail = new GuarantorDetail();
		BeanUtils.copyProperties((GuarantorDetail) auditHeader.getAuditDetail().getModelData(),
				guarantorDetail);

		if (guarantorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getGuarantorDetailDAO().delete(guarantorDetail, "");

		} else {
			guarantorDetail.setRoleCode("");
			guarantorDetail.setNextRoleCode("");
			guarantorDetail.setTaskId("");
			guarantorDetail.setNextTaskId("");
			guarantorDetail.setWorkflowId(0);

			if (guarantorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				guarantorDetail.setRecordType("");
				getGuarantorDetailDAO().save(guarantorDetail, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				guarantorDetail.setRecordType("");
				getGuarantorDetailDAO().update(guarantorDetail, "");
			}
		}

		getGuarantorDetailDAO().delete(guarantorDetail, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(guarantorDetail);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getGuarantorDetailDAO().delete with parameters guarantorDetail,"_Temp" 3) Audit the
	 * record in to AuditHeader and AdtFinGuarantorsDetails by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "doApprove", false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		GuarantorDetail guarantorDetail = (GuarantorDetail) auditHeader.getAuditDetail()
		.getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getGuarantorDetailDAO().delete(guarantorDetail, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * getPrimaryExposureList
	 * 
	 * Return the list of primary finances Exposer List (self finances) for the corresponding Guarantor
	 * 
	 * @param GuarantorDetail
	 *            (guarantorDetail)
	 * @return List<FinanceExposure>
	 */
	@Override
	public List<FinanceExposure> getPrimaryExposureList(GuarantorDetail guarantorDetail) {
		
		return getGuarantorDetailDAO().getPrimaryExposureList(guarantorDetail);
	}


	/**
	 * getSecondaryExposureList
	 * 
	 * Return the list of secondary finances Exposer List(where the Customer is having joint finances) for the corresponding Guarantor
	 * 
	 * @param GuarantorDetail
	 *            (guarantorDetail)
	 * @return List<FinanceExposure>
	 */

	@Override
	public List<FinanceExposure> getSecondaryExposureList(GuarantorDetail guarantorDetail) {

		return getGuarantorDetailDAO().getSecondaryExposureList(guarantorDetail);
	}

	/**
	 * getGuarantorExposureList
	 * 
	 * Return the list of secondary  Gurantor Exposure List finances(where the Customer is Gurantor to others) for the corresponding Guarantor
	 * 
	 * @param GuarantorDetail
	 *            (guarantorDetail)
	 * @return List<FinanceExposure>
	 */
	@Override
	public List<FinanceExposure> getGuarantorExposureList(GuarantorDetail guarantorDetail) {
		
		return getGuarantorDetailDAO().getGuarantorExposureList(guarantorDetail);
	}



	/**
	 * getExposureSummaryDetail
	 * 
	 * Sum of all the finances of financeAmount, currentExposer, overDueAmount respectively, For the corresponding
	 * Customer
	 * 
	 * @param List
	 *            <FinanceExposure> (exposerList)
	 * @return exposerSummaryDetail
	 */
	@Override
	public FinanceExposure getExposureSummaryDetail(List<FinanceExposure> exposerList) {
		FinanceExposure exposerSummaryDetail = new FinanceExposure();
		BigDecimal finaceAmout = BigDecimal.ZERO;
		BigDecimal currentExposer = BigDecimal.ZERO;
		BigDecimal overDueAmount = BigDecimal.ZERO;
		
		int dftCurntEdtField = SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT);
		
		if(exposerList != null && !exposerList.isEmpty()) {
			for (FinanceExposure financeExposure : exposerList) {
				finaceAmout = finaceAmout.add(financeExposure.getFinanceAmtinBaseCCY());
				currentExposer = currentExposer.add(financeExposure.getCurrentExpoSureinBaseCCY());
				if (financeExposure.getOverdueAmt() != null) {
					overDueAmount = overDueAmount.add(financeExposure.getOverdueAmtBaseCCY());
				}
			}
		}
		exposerSummaryDetail.setCcyEditField(dftCurntEdtField);
		exposerSummaryDetail.setFinanceAmtinBaseCCY(finaceAmout);
		exposerSummaryDetail.setCurrentExpoSureinBaseCCY(currentExposer);
		exposerSummaryDetail.setOverdueAmtBaseCCY(overDueAmount);

		return exposerSummaryDetail;
	}



	/**
	 * businessValidation method do the following steps. 1) validate the audit detail 2) if any error/Warnings then
	 * assign the to auditHeader 3) identify the nextprocess
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */

	private AuditHeader businessValidation(AuditHeader auditHeader, String method,
			boolean onlineRequest) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),
				auditHeader.getUsrLanguage(), method, onlineRequest);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}
	
	@Override
	public List<AuditDetail> validate(List<GuarantorDetail> guarantorDetailList, long workflowId, String method, String auditTranType, String  usrLanguage){
		return doValidation(guarantorDetailList, workflowId, method, auditTranType, usrLanguage);
	}

	/**
	 * Validation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details from the
	 * tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5) for any
	 * mismatch conditions Fetch the error details from getGuarantorDetailDAO().getErrorDetail with Error ID and
	 * language as parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method,
			boolean onlineRequest) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		GuarantorDetail guarantorDetail = (GuarantorDetail) auditDetail.getModelData();

		GuarantorDetail tempGuarantorDetail = null;
		if (guarantorDetail.isWorkflow()) {
			tempGuarantorDetail = getGuarantorDetailDAO().getGuarantorDetailById(
					guarantorDetail.getId(), "_Temp");
		}
		GuarantorDetail befGuarantorDetail = getGuarantorDetailDAO().getGuarantorDetailById(
				guarantorDetail.getId(), "");

		GuarantorDetail oldGuarantorDetail = guarantorDetail.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(guarantorDetail.getId());
		errParm[0] = PennantJavaUtil.getLabel("label_GuarantorId") + ":" + valueParm[0];

		if (guarantorDetail.isNew()) { // for New record or new record into work flow

			if (!guarantorDetail.isWorkflow()) {// With out Work flow only new records  
				if (befGuarantorDetail != null) { // Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
							PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (guarantorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befGuarantorDetail != null || tempGuarantorDetail != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
								usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befGuarantorDetail == null || tempGuarantorDetail != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
								usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!guarantorDetail.isWorkflow()) { // With out Work flow for update and delete

				if (befGuarantorDetail == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
							PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldGuarantorDetail != null
							&& !oldGuarantorDetail.getLastMntOn().equals(
									befGuarantorDetail.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41003", errParm, valueParm),
									usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41004", errParm, valueParm),
									usrLanguage));
						}
					}
				}
			} else {

				if (tempGuarantorDetail == null) { // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
							PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (oldGuarantorDetail != null
						&& !oldGuarantorDetail.getLastMntOn().equals(
								tempGuarantorDetail.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
							PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(),
				usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !guarantorDetail.isWorkflow()) {
			auditDetail.setBefImage(befGuarantorDetail);
		}

		return auditDetail;
	}

	@Override
	public List<GuarantorDetail> getGuarantorDetailByFinRef(String finReference, String type) {
		return getGuarantorDetailDAO().getGuarantorDetailByFinRef(finReference, type);
	}


	@Override
	public List<AuditDetail> saveOrUpdate(List<GuarantorDetail> guarantorDetailList, String tableType, String auditTranType) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		
		for (GuarantorDetail guarantorDetail : guarantorDetailList) {
			guarantorDetail.setWorkflowId(0);

			if (guarantorDetail.isNewRecord()) {
				getGuarantorDetailDAO().save(guarantorDetail, tableType);
			} else {
				getGuarantorDetailDAO().update(guarantorDetail, tableType);
			}
			String[] fields = PennantJavaUtil.getFieldDetails(guarantorDetail, guarantorDetail.getExcludeFields());
			auditDetails.add(new AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], guarantorDetail.getBefImage(), guarantorDetail));
		}

		logger.debug("Leaving");
		return auditDetails;
	}
	
	
	@Override
	public List<AuditDetail> doApprove(List<GuarantorDetail> guarantorDetailList, String tableType, 
			String auditTranType, String finSourceId) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		
		for (GuarantorDetail guarantorDetail : guarantorDetailList) {
			GuarantorDetail detail = new GuarantorDetail();
			BeanUtils.copyProperties(guarantorDetail, detail);
			if (!guarantorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
				guarantorDetail.setRoleCode("");
				guarantorDetail.setNextRoleCode("");
				guarantorDetail.setTaskId("");
				guarantorDetail.setNextTaskId("");
				guarantorDetail.setWorkflowId(0);
				guarantorDetail.setRecordType("");

				getGuarantorDetailDAO().save(guarantorDetail, tableType);
			}
			if(!StringUtils.equals(finSourceId, PennantConstants.FINSOURCE_ID_API)) {
				getGuarantorDetailDAO().delete(guarantorDetail, "_Temp");
			}
			
			String[] fields = PennantJavaUtil.getFieldDetails(guarantorDetail, guarantorDetail.getExcludeFields());
			auditDetails.add(new  AuditDetail(PennantConstants.TRAN_WF, auditDetails.size()+1, fields[0], fields[1], detail.getBefImage(), detail));
			auditDetails.add(new  AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], guarantorDetail.getBefImage(), guarantorDetail));
		}

		logger.debug("Leaving");
		return auditDetails;
	}
	
	
	@Override
	public List<AuditDetail> delete(List<GuarantorDetail> guarantorDetailList, String tableType, String auditTranType) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		for (GuarantorDetail guarantorDetail : guarantorDetailList) {
			getGuarantorDetailDAO().delete(guarantorDetail, tableType);
			
			String[] fields = PennantJavaUtil.getFieldDetails(guarantorDetail, guarantorDetail.getExcludeFields());
			auditDetails.add(new  AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], guarantorDetail.getBefImage(), guarantorDetail));
		}

		logger.debug("Leaving");
		return auditDetails;
	}


	public AuditHeader doValidation(AuditHeader auditHeader, String method){
		logger.debug("Entering");
		
		AuditDetail auditDetail =   validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		
		logger.debug("Leaving");
		return auditHeader;
	}
	
	public List<AuditDetail> doValidation(List<GuarantorDetail> guarantorDetailList, long workflowId, String method, String auditTranType, String usrLanguage){
		logger.debug("Entering");

		List<AuditDetail> auditDetails = getAuditDetail(guarantorDetailList, auditTranType, method, workflowId);

		for (AuditDetail auditDetail : auditDetails) {
			validate(auditDetail, method, usrLanguage); 
		}

		logger.debug("Leaving");
		return auditDetails ;
	}


	private AuditDetail validate(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());

		GuarantorDetail guarantorDetail = (GuarantorDetail) auditDetail.getModelData();
		GuarantorDetail tempGuarantorDetail = null;
		GuarantorDetail befGuarantorDetail = null;
		GuarantorDetail oldGuarantorDetail = null;

		String finReference = guarantorDetail.getFinReference();
		String guarantorCIF = guarantorDetail.getGuarantorCIF();
		String guarantorIDNumber = guarantorDetail.getGuarantorIDNumber();
		String keyField = "";

		if(guarantorDetail.isBankCustomer()) {		
			keyField = guarantorCIF;
		} else {
			keyField = guarantorIDNumber;
		}


		if (guarantorDetail.isWorkflow()) {
			tempGuarantorDetail = getGuarantorDetailDAO().getGuarantorDetailByRefId(finReference, guarantorDetail.getGuarantorId(), "_Temp");
		}

		befGuarantorDetail = getGuarantorDetailDAO().getGuarantorDetailByRefId(finReference, guarantorDetail.getGuarantorId(), "");
		oldGuarantorDetail = guarantorDetail.getBefImage();

		String[] errParm = new String[2];
		String[] valueParm = new String[2];
		valueParm[0] = finReference;
		valueParm[1] = keyField;

		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_GuarantorCIF") + ":" + valueParm[1];

		if (guarantorDetail.isNew()) { // for New record or new record into work flow

			if (!guarantorDetail.isWorkflow()) {// With out Work flow only new records  
				if (befGuarantorDetail != null) { // Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
							PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (guarantorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befGuarantorDetail != null || tempGuarantorDetail != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
								usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befGuarantorDetail == null || tempGuarantorDetail != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
								usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!guarantorDetail.isWorkflow()) { // With out Work flow for update and delete

				if (befGuarantorDetail == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
							PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldGuarantorDetail != null
							&& !oldGuarantorDetail.getLastMntOn().equals(
									befGuarantorDetail.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41003", errParm, valueParm),
									usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41004", errParm, valueParm),
									usrLanguage));
						}
					}
				}
			} else {

				if (tempGuarantorDetail == null) { // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
							PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempGuarantorDetail != null
						&& oldGuarantorDetail != null
						&& !oldGuarantorDetail.getLastMntOn().equals(
								tempGuarantorDetail.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
							PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(),
				usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !guarantorDetail.isWorkflow()) {
			auditDetail.setBefImage(befGuarantorDetail);
		}
		return auditDetail;
	}

	@Override
	public GuarantorDetail getGuarantorProof(GuarantorDetail guarantorDetail) {
		return getGuarantorDetailDAO().getGuarantorProof(guarantorDetail);
	}

	/**
	 * getGuarantorDetail
	 * 
	 * Return the list of Guarantor financial details and customer details based on the corresponding finance reference
	 * 
	 * @param String
	 *            (finReference)
	 * @param String
	 *            (tableType)
	 * @return List<GuarantorDetail>
	 */
	@Override
	public List<GuarantorDetail> getGuarantorDetail(String finReference, String tableType) {
		List<FinanceExposure> primaryList = null;
		List<FinanceExposure> secoundaryList = null;
		List<FinanceExposure> guarantorList = null;

		List<GuarantorDetail> guarantorDetailList = getGuarantorDetailByFinRef(finReference, tableType);

		if(guarantorDetailList != null && !guarantorDetailList.isEmpty()) {
			for (GuarantorDetail detail : guarantorDetailList) {	
				BigDecimal currentExpoSure = BigDecimal.ZERO;
				detail=setStatus(detail);
				// set the primary exposer details to Joint Account Details
				primaryList = getGuarantorDetailDAO().getPrimaryExposureList(detail);
				currentExpoSure =  doFillExposureDetails(primaryList, detail);
				detail.setPrimaryExposure(String.valueOf(currentExpoSure));

				// set the secondary exposer details to Joint Account 
				secoundaryList = getGuarantorDetailDAO().getSecondaryExposureList(detail);
				currentExpoSure = doFillExposureDetails(secoundaryList, detail);
				detail.setSecondaryExposure(String.valueOf(currentExpoSure));

				// set the exposer details to Joint Account
				guarantorList = getGuarantorDetailDAO().getGuarantorExposureList(detail);
				currentExpoSure =  doFillExposureDetails(guarantorList, detail);
				detail.setGuarantorExposure(String.valueOf(currentExpoSure));

			}

		}

		return guarantorDetailList;
	}
	
	public GuarantorDetail setStatus(GuarantorDetail detail){
		if (detail.isBankCustomer()) {
            Customer customer = getCustomerDAO().getCustomerByCIF(detail.getGuarantorCIF(),"_AView");
            if (customer != null) {
                detail.setStatus(customer.getLovDescCustStsName());
                detail.setWorstStatus(getWorstStaus(customer.getCustID()));
            }
        }
		return detail;
	}
	
	@Override
	public String getWorstStaus(long custID){
		return getCustomerDAO().getCustWorstStsDesc(custID);
	}
	
	@Override
	public BigDecimal doFillExposureDetails(List<FinanceExposure> primaryList, GuarantorDetail detail) {
		BigDecimal currentExpoSure = BigDecimal.ZERO;
		if(primaryList != null && !primaryList.isEmpty() )  {
			for (FinanceExposure exposer : primaryList) {
				if(exposer != null) {
					String toCcy = SysParamUtil.getValueAsString("APP_DFT_CURR");
					String  fromCcy = exposer.getFinCCY();
					currentExpoSure = currentExpoSure.add(CalculationUtil.getConvertedAmount(fromCcy, toCcy, exposer.getCurrentExpoSure()));
				}
			}
		}
		return currentExpoSure;
	}
	
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public GuarantorDetailDAO getGuarantorDetailDAO() {
		return guarantorDetailDAO;
	}
	public void setGuarantorDetailDAO(GuarantorDetailDAO guarantorDetailDAO) {
		this.guarantorDetailDAO = guarantorDetailDAO;
	}
	
	private List<AuditDetail> getAuditDetail(List<GuarantorDetail> gurantorsDetailList, String auditTranType, String method, long workflowId) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		GuarantorDetail object = new GuarantorDetail();
		String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());

		for (int i = 0; i < gurantorsDetailList.size(); i++) {

			GuarantorDetail guarantorDetail = gurantorsDetailList.get(i);
			guarantorDetail.setWorkflowId(workflowId);
			boolean isRcdType = false;

			if (guarantorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				guarantorDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (guarantorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				guarantorDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (guarantorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				guarantorDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				guarantorDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (guarantorDetail.getRecordType().equalsIgnoreCase(
						PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (guarantorDetail.getRecordType().equalsIgnoreCase(
						PennantConstants.RECORD_TYPE_DEL)
						|| guarantorDetail.getRecordType().equalsIgnoreCase(
								PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			if (StringUtils.isNotEmpty(guarantorDetail.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], guarantorDetail.getBefImage(), guarantorDetail));
			}
		}
		
		logger.debug("Leaving");
		return auditDetails;
	}


}