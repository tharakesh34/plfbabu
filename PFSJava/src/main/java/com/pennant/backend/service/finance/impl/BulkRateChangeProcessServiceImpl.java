package com.pennant.backend.service.finance.impl;

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
 * FileName    		:  BulkRateChangeProcessServiceImpl.java                                * 	  
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.dao.finance.BulkRateChangeProcessDAO;
import com.pennant.backend.dao.finance.BulkRateChangeProcessDetailsDAO;
import com.pennant.backend.dao.solutionfactory.ExtendedFieldDetailDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.BulkRateChangeDetails;
import com.pennant.backend.model.finance.BulkRateChangeHeader;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ScheduleMapDetails;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.service.finance.BulkRateChangeProcessService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.GenericFinanceDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.TableType;
import com.rits.cloning.Cloner;

/**
 * Service implementation for methods that depends on <b>BulkRateChangeHeader</b>.<br>
 * 
 */
public class BulkRateChangeProcessServiceImpl extends GenericFinanceDetailService implements BulkRateChangeProcessService {

	private static Logger logger = Logger.getLogger(BulkRateChangeProcessServiceImpl.class);

	// DAO Classes
	private BulkRateChangeProcessDAO bulkRateChangeProcessDAO;
	private BulkRateChangeProcessDetailsDAO bulkRateChangeProcessDetailsDAO;
	private ExtendedFieldDetailDAO extendedFieldDetailDAO;

	private FinanceDetailService financeDetailService;
	private LoggedInUser loggedInUser;


	public BulkRateChangeProcessServiceImpl() {
		super();
	}


	public BulkRateChangeHeader getBulkRateChangeHeader() {
		return getBulkRateChangeProcessDAO().getBulkRateChangeHeader();
	}

	public BulkRateChangeHeader getNewBulkRateChangeHeader() {
		return getBulkRateChangeProcessDAO().getNewBulkRateChangeHeader();
	}

	/**
	 * getAcademicById fetch the details by using BulkRateChangeProcessDAO's
	 * method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return BulkRateChangeHeader
	 */
	@Override
	public BulkRateChangeHeader getBulkRateChangeHeaderByRef(String bulkRateChangeRef) {

		BulkRateChangeHeader bulkRateChangeHeader = getBulkRateChangeProcessDAO().getBulkRateChangeHeaderByRef(bulkRateChangeRef, "_View");
		if (bulkRateChangeHeader != null) {
			bulkRateChangeHeader.setBulkRateChangeDetailsList(getBulkRateChangeProcessDetailsDAO().getBulkRateChangeDetailsListByRef(bulkRateChangeHeader.getBulkRateChangeRef(), "_View"));
		}
		return bulkRateChangeHeader;
	}

	/**
	 * This method is used  for fetch the details by using BulkRateChangeProcessDAO's
	 * getAcademicById method . with parameter id and type as blank. it fetches
	 * the approved records from the BulkRateChangeHeader.
	 * 
	 * @param id
	 *            (String)
	 * @return BulkRateChangeHeader
	 */
	public BulkRateChangeHeader getApprovedBulkRateChangeHeaderByRef(String bulkRateChangeRef) {
		BulkRateChangeHeader bulkRateChangeHeader = getBulkRateChangeProcessDAO().getBulkRateChangeHeaderByRef(bulkRateChangeRef, "_AView");
		if (bulkRateChangeHeader != null) {
			bulkRateChangeHeader.setBulkRateChangeDetailsList(getBulkRateChangeProcessDetailsDAO().getBulkRateChangeDetailsListByRef(bulkRateChangeHeader.getBulkRateChangeRef(), "_AView"));
		}
		return bulkRateChangeHeader;
	}

	@Override
	public List<BulkRateChangeDetails> getBulkRateChangeFinList(String frinType, Date schFromDate, String whereClause) {
		return getBulkRateChangeProcessDetailsDAO().getBulkRateChangeFinList(frinType, schFromDate, whereClause);
	}

	@Override
	public String getBulkRateChangeReference() {
		return getBulkRateChangeProcessDAO().getBulkRateChangeReference();	
	}


	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BulkRateChangeHeader/BulkRateChangeHeader_Temp by using BulkRateChangeProcessDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using BulkRateChangeProcessDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtBulkRateChangeHeader by using
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
		BulkRateChangeHeader bulkRateChangeHeader = (BulkRateChangeHeader) auditHeader.getAuditDetail().getModelData();

		if(bulkRateChangeHeader != null && bulkRateChangeHeader.getUserDetails() != null){
			setLoggedInUser(bulkRateChangeHeader.getUserDetails());
		}

		/*if (bulkRateChangeHeader.isWorkflow()) {
			tableType = "_TEMP";
		}*/

		bulkRateChangeHeader.setStatus("Rate Change Process Started");

		if (bulkRateChangeHeader.isNew()) {
			bulkRateChangeHeader.setBulkRateChangeRef(getBulkRateChangeProcessDAO().save(bulkRateChangeHeader, tableType));
			auditHeader.getAuditDetail().setModelData(bulkRateChangeHeader);
			auditHeader.setAuditReference(bulkRateChangeHeader.getBulkRateChangeRef());
		} else {
			getBulkRateChangeProcessDAO().update(bulkRateChangeHeader, tableType);
			//getBulkRateChangeProcessDetailsDAO().updateList(bulkRateChangeHeader.getBulkRateChangeDetailsList(), tableType);
		}

		/*if (bulkRateChangeHeader.isNew() || bulkRateChangeHeader.isLovDescIsOlddataChanged()) { //TODO
			getBulkRateChangeProcessDetailsDAO().deleteBulkRateChangeDetailsByRef(bulkRateChangeHeader.getBulkRateChangeRef(), tableType);
			getBulkRateChangeProcessDetailsDAO().saveList(bulkRateChangeHeader.getBulkRateChangeDetailsList(), tableType);
		} else {
			getBulkRateChangeProcessDetailsDAO().updateList(bulkRateChangeHeader.getBulkRateChangeDetailsList(), tableType);
		}*/

		if (bulkRateChangeHeader.getBulkRateChangeDetailsList() != null && bulkRateChangeHeader.getBulkRateChangeDetailsList().size() > 0) { //TODO Total if part

			getBulkRateChangeProcessDetailsDAO().deleteBulkRateChangeDetailsByRef(bulkRateChangeHeader.getBulkRateChangeRef(), tableType);

			//Set Header Properties to the Child details
			setHeaderPropertiesToDetailsList(bulkRateChangeHeader);
			getBulkRateChangeProcessDetailsDAO().saveList(bulkRateChangeHeader.getBulkRateChangeDetailsList(), tableType);

			//Restrict Finance for other servicing activity
			for (BulkRateChangeDetails bulkRateChangeDetail : bulkRateChangeHeader.getBulkRateChangeDetailsList()) {
				FinanceMain financeMain = getFinanceMainDAO().getFinanceMainById(bulkRateChangeDetail.getFinReference(), "", false);
				
				if(!bulkRateChangeHeader.isNewRecord()){
					getFinanceMainDAO().delete(financeMain, TableType.TEMP_TAB, false, true);
				}
				
				financeMain.setRcdMaintainSts(FinanceConstants.BULK_RATE_CHG);
				financeMain.setEffectiveRateOfReturn(bulkRateChangeDetail.getNewProfitRate());
				financeMain.setTotalProfit(bulkRateChangeDetail.getNewProfit());
				getFinanceMainDAO().save(financeMain, TableType.TEMP_TAB, false);
			}
		}

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * 
	 * @param bulkRateChangeHeader
	 */
	private void setHeaderPropertiesToDetailsList(BulkRateChangeHeader bulkRateChangeHeader) {
		logger.debug("Entering");

		for (BulkRateChangeDetails rateChangeDetails : bulkRateChangeHeader.getBulkRateChangeDetailsList()) {
			/*if (bulkRateChangeHeader.isNew() || bulkRateChangeHeader.isLovDescIsOlddataChanged()) { //TODO Delete and Save
				rateChangeDetails.setBulkRateChangeRef(bulkRateChangeHeader.getBulkRateChangeRef());
			}*/
			rateChangeDetails.setBulkRateChangeRef(bulkRateChangeHeader.getBulkRateChangeRef());
			rateChangeDetails.setStatus("P");//Pending
			rateChangeDetails.setVersion(bulkRateChangeHeader.getVersion());
			rateChangeDetails.setLastMntBy(bulkRateChangeHeader.getLastMntBy());
			rateChangeDetails.setLastMntOn(bulkRateChangeHeader.getLastMntOn());
			rateChangeDetails.setRecordType(bulkRateChangeHeader.getRecordType());
			rateChangeDetails.setRecordStatus(bulkRateChangeHeader.getRecordStatus());
			rateChangeDetails.setRoleCode(bulkRateChangeHeader.getRoleCode());
			rateChangeDetails.setNextRoleCode(bulkRateChangeHeader.getNextRoleCode());
			rateChangeDetails.setTaskId(bulkRateChangeHeader.getTaskId());
			rateChangeDetails.setNextTaskId(bulkRateChangeHeader.getNextTaskId());
		}

		logger.debug("Leaving");
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BulkRateChangeHeader by using BulkRateChangeProcessDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtBulkRateChangeHeader by using
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

		BulkRateChangeHeader bulkRateChangeHeader = (BulkRateChangeHeader) auditHeader.getAuditDetail().getModelData();
		getBulkRateChangeProcessDAO().delete(bulkRateChangeHeader, "");

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws Exception 
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) throws Exception {
		logger.debug("Entering");

		logger.debug("Leaving");
		return businessValidation(auditHeader, "doApprove");
	}

	/**
	 * @param BulkRateChangeHeader
	 *            (bulkRateChangeHeader)
	 * @param success
	 * @param failure
	 * @throws Exception 
	 */
	public void doApproveBulkRateChangeHeader(BulkRateChangeHeader bulkRateChangeHeader, long success, long failure) {
		logger.debug("Entering");

		String tranType = PennantConstants.TRAN_WF;

		AuditDetail auditDetail = new AuditDetail(tranType, 1, bulkRateChangeHeader.getBefImage(), bulkRateChangeHeader);   
		AuditHeader auditHeader = new AuditHeader(bulkRateChangeHeader.getBulkRateChangeRef(), null, null, null, auditDetail, bulkRateChangeHeader.getUserDetails(),
				new HashMap<String, ArrayList<ErrorDetail>>());

		BulkRateChangeHeader aBulkRateChangeHeader = new BulkRateChangeHeader();
		BeanUtils.copyProperties((BulkRateChangeHeader) auditHeader.getAuditDetail().getModelData(), aBulkRateChangeHeader);

		if (failure == 0) {
			aBulkRateChangeHeader.setRoleCode("");
			aBulkRateChangeHeader.setNextRoleCode("");
			aBulkRateChangeHeader.setTaskId("");
			aBulkRateChangeHeader.setNextTaskId("");
			aBulkRateChangeHeader.setWorkflowId(0);

			aBulkRateChangeHeader.setRecordType("");

			aBulkRateChangeHeader.setStatus("Rate Change Process Completed Successfully. Total Finances : " + (failure + success));
			getBulkRateChangeProcessDAO().update(aBulkRateChangeHeader, "");

			auditHeader.setAuditTranType(tranType);
			getAuditHeaderDAO().addAudit(auditHeader);

			tranType = PennantConstants.TRAN_ADD;
			auditHeader.setAuditTranType(tranType);
			auditHeader.getAuditDetail().setAuditTranType(tranType);
			auditHeader.getAuditDetail().setModelData(aBulkRateChangeHeader);

			getAuditHeaderDAO().addAudit(auditHeader);
		} else {
			aBulkRateChangeHeader.setStatus("Rate Change Process Completed With Errors. Success : " + success + " , Failed : " + failure);
			getBulkRateChangeProcessDAO().update(aBulkRateChangeHeader, "");

			auditHeader.setAuditTranType(tranType);
			auditHeader.getAuditDetail().setModelData(aBulkRateChangeHeader);

			getAuditHeaderDAO().addAudit(auditHeader);
		}

		logger.debug("Leaving");
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getBulkRateChangeProcessDAO().delete with parameters
	 * bulkRateChangeHeader,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtBulkRateChangeHeader by using auditHeaderDAO.addAudit(auditHeader) for Work
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

		BulkRateChangeHeader bulkRateChangeHeader = (BulkRateChangeHeader) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getBulkRateChangeProcessDAO().delete(bulkRateChangeHeader, "");
		getBulkRateChangeProcessDetailsDAO().deleteBulkRateChangeDetailsByRef(bulkRateChangeHeader.getBulkRateChangeRef(), "");

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
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getBulkRateChangeProcessDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		BulkRateChangeHeader bulkRateChangeHeader = (BulkRateChangeHeader) auditDetail.getModelData();

		BulkRateChangeHeader tempBulkRateChangeHeader = null;
		if (bulkRateChangeHeader.isWorkflow()) {
			tempBulkRateChangeHeader = getBulkRateChangeProcessDAO().getBulkRateChangeHeaderByRef(bulkRateChangeHeader.getBulkRateChangeRef(), "");
		}

		BulkRateChangeHeader befBulkRateChangeHeader = getBulkRateChangeProcessDAO().getBulkRateChangeHeaderByRef(bulkRateChangeHeader.getBulkRateChangeRef(), " ");
		BulkRateChangeHeader oldBulkRateChangeHeader = bulkRateChangeHeader.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = bulkRateChangeHeader.getBulkRateChangeRef();

		/*errParm[0] = PennantJavaUtil.getLabel("label_BulkRateChangeSearch_fromDate.value") + ":"+ valueParm[0]; //TODO
		errParm[1] = PennantJavaUtil.getLabel("label_BulkRateChangeSearch_toDate.value") + ":"+valueParm[1];*/

		errParm[0] = PennantJavaUtil.getLabel("label_BulkRateChangeRef") + " : "+ valueParm[0];

		if (bulkRateChangeHeader.isNew()) { // for New record or new record into work flow

			if (!bulkRateChangeHeader.isWorkflow()) {// With out Work flow only new records
				if (befBulkRateChangeHeader != null) { // Record Already Exists in the table
					// then error
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41001",errParm,null));
				}
			} else { // with work flow
				if (bulkRateChangeHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type
					// is new
					if (befBulkRateChangeHeader != null || tempBulkRateChangeHeader != null) { // if records already exists in
						// the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41001",errParm,null));
					}
				} else { // if records not exists in the Main flow table
					if (befBulkRateChangeHeader == null || tempBulkRateChangeHeader != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!bulkRateChangeHeader.isWorkflow()) { // With out Work flow for update and
				// delete
				if (befBulkRateChangeHeader == null) { // if records not exists in the main
					// table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41002",errParm,null));
				} else {
					if (oldBulkRateChangeHeader != null
							&& !oldBulkRateChangeHeader.getLastMntOn().equals(
									befBulkRateChangeHeader.getLastMntOn())) {
						if (StringUtils.trimToEmpty(
								auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41003",errParm,null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41004",errParm,null));
						}
					}
				}
			} else {
				if (tempBulkRateChangeHeader == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

				if ( tempBulkRateChangeHeader != null &&  oldBulkRateChangeHeader != null
						&& !oldBulkRateChangeHeader.getLastMntOn().equals(
								tempBulkRateChangeHeader.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if (StringUtils.trimToEmpty(method).equals("doApprove")	|| !bulkRateChangeHeader.isWorkflow()) {
			auditDetail.setBefImage(befBulkRateChangeHeader);
		}

		logger.debug("Leaving");
		return auditDetail;
	}

	/**
	 * 
	 * @param bulkRateChangeHeader
	 * @param bulkRateChangeDetail
	 * @return boolean success
	 * @throws Exception
	 */
	@Override
	public boolean processBulkRateChangeDetail(BulkRateChangeHeader bulkRateChangeHeader, BulkRateChangeDetails bulkRateChangeDetail) throws Exception {
		logger.debug("Entering");

		boolean success = false;
		String errorMsg = "";

		try {
			//bulkRateChangeDetail.setStatus("P"); //Pending
			//getBulkRateChangeProcessDetailsDAO().update(bulkRateChangeDetail, "");

			success = processRateChange(bulkRateChangeHeader, bulkRateChangeDetail);

			if (success) {
				bulkRateChangeDetail.setRoleCode("");
				bulkRateChangeDetail.setNextRoleCode("");
				bulkRateChangeDetail.setTaskId("");
				bulkRateChangeDetail.setNextTaskId("");
				bulkRateChangeDetail.setWorkflowId(0);
				bulkRateChangeDetail.setRecordType("");
				bulkRateChangeDetail.setRecordStatus(bulkRateChangeHeader.getRecordStatus());

				bulkRateChangeDetail.setStatus("S"); //Success
				bulkRateChangeDetail.setErrorMsg(errorMsg);
				getBulkRateChangeProcessDetailsDAO().update(bulkRateChangeDetail, "");
			}
		} catch (Exception e) {
			success = false;
			errorMsg = e.getMessage();
			logger.debug(e);
		}

		if (!success) {
			bulkRateChangeDetail.setStatus("F"); //Failed
			bulkRateChangeDetail.setErrorMsg(errorMsg);
			getBulkRateChangeProcessDetailsDAO().update(bulkRateChangeDetail, "");
		}

		logger.debug("Leaving");
		return success;
	}

	/**
	 * 
	 * @param bulkRateChangeHeader
	 * @param bulkRateChangeDetail
	 */
	public boolean processRateChange(BulkRateChangeHeader bulkRateChangeHeader, BulkRateChangeDetails bulkRateChangeDetail) {
		logger.debug("Entering");

		//Get Total Finance Details to particular Finance
		FinanceDetail financeDetail = getFinanceDetailService().getFinSchdDetailById(bulkRateChangeDetail.getFinReference(), "_AView", false);
		financeDetail.setUserDetails(bulkRateChangeHeader.getUserDetails());

		//Reset Before Image for Auditing
		FinanceDetail befImage = new FinanceDetail();
		BeanUtils.copyProperties(financeDetail, befImage);
		financeDetail.setBefImage(befImage);

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		financeMain.setUserDetails(bulkRateChangeHeader.getUserDetails());
		financeMain.setRecalType(bulkRateChangeHeader.getReCalType());
		financeMain.setEventFromDate(bulkRateChangeHeader.getFromDate());
		financeMain.setRecalFromDate(bulkRateChangeHeader.getFromDate());

		if (bulkRateChangeHeader.getToDate() == null) {
			financeMain.setEventToDate(financeMain.getMaturityDate());
		} else {
			financeMain.setEventToDate(bulkRateChangeHeader.getToDate());
		}

		BigDecimal newProfitRate = financeMain.getEffectiveRateOfReturn().add(bulkRateChangeHeader.getRateChange());
		
		//Schedule Re-calculation based on Applied parameters
		financeDetail.setFinScheduleData(ScheduleCalculator.changeRate(financeDetail.getFinScheduleData(), financeMain.getRepayBaseRate(), "", BigDecimal.ZERO,
				newProfitRate == null ? BigDecimal.ZERO : newProfitRate, true));

		bulkRateChangeDetail.setNewProfitRate(financeMain.getEffectiveRateOfReturn());
		bulkRateChangeDetail.setNewProfit(financeMain.getTotalProfit());

		//Record proceed through WorkFlow defined Process
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), "");
		AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_UPD, 1, fields[0], fields[1], financeDetail.getBefImage(), financeDetail);
		AuditHeader auditHeader = new AuditHeader(financeDetail.getFinScheduleData().getFinReference(), null, null, null, auditDetail,
				financeDetail.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());

		//Changed Finance Save in Database
		saveOrUpdate(auditHeader, false);

		logger.debug("Leaving");
		return true;
	}

	/**
	 * @param AuditHeader
	 *            (auditHeader)
	 * @param isWIF
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader aAuditHeader, boolean isWIF) {
		logger.debug("Entering");

		aAuditHeader = businessValidation(aAuditHeader, "saveOrUpdate", isWIF);
		if (!aAuditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);

		String tableType = "";
		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		financeDetail.setUserDetails(getLoggedInUser());
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		financeMain.setUserDetails(getLoggedInUser());
		Date curBDay = DateUtility.getAppDate();

		if (financeMain.isWorkflow()) {
			tableType = "_TEMP";
		}

		// Save schedule details
		//=======================================
		if (!financeDetail.isNewRecord()) {

			if(!isWIF && tableType.equals("") && financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
				//Fetch Existing data before Modification

				FinScheduleData old_finSchdData = null;
				if(financeDetail.getFinScheduleData().getFinanceMain().isScheduleRegenerated()){
					old_finSchdData = getFinSchDataByFinRef(financeDetail.getFinScheduleData().getFinReference(), "", -1);
					old_finSchdData.setFinReference(financeDetail.getFinScheduleData().getFinReference());
				}

				//Create log entry for Action for Schedule Modification
				FinLogEntryDetail entryDetail = new FinLogEntryDetail();
				entryDetail.setFinReference(financeDetail.getFinScheduleData().getFinReference());
				entryDetail.setEventAction(financeDetail.getAccountingEventCode());
				entryDetail.setSchdlRecal(financeDetail.getFinScheduleData().getFinanceMain().isScheduleRegenerated());
				entryDetail.setPostDate(curBDay);
				entryDetail.setReversalCompleted(false);
				long logKey = getFinLogEntryDetailDAO().save(entryDetail);

				//Save Schedule Details For Future Modifications
				if(financeDetail.getFinScheduleData().getFinanceMain().isScheduleRegenerated()){
					listSave(old_finSchdData, "_Log", false, logKey);
				}
			}
			
			listDeletion(financeDetail.getFinScheduleData(), tableType, isWIF);
			listSave(financeDetail.getFinScheduleData(), tableType, isWIF, 0);
	//		saveFeeChargeList(financeDetail.getFinScheduleData(), isWIF,tableType);
		} else {
			listSave(financeDetail.getFinScheduleData(), tableType, isWIF, 0);
		//	saveFeeChargeList(financeDetail.getFinScheduleData(), isWIF,tableType);
		}

		// Save asset details
		//=======================================
		if(!isWIF){
			getFinanceDetailService().doSaveAddlFieldDetails(financeDetail, tableType);
		}

		if(!isWIF){
			String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), "");
			auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], financeMain.getBefImage(), financeMain));

			AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_UPD, 1, fields[0], fields[1], financeMain.getBefImage(), financeMain);
			auditHeader = new AuditHeader(financeDetail.getFinScheduleData().getFinReference(), null, null, null, auditDetail,
					financeDetail.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());

			getAuditHeaderDAO().addAudit(auditHeader);
		}

		logger.debug("Leaving");
		return auditHeader;
	}




	/**
	 * @param AuditHeader
	 *            (auditHeader)
	 * @param method
	 * @param isWIF
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method, boolean isWIF) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method, isWIF);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (!isWIF) {
			auditHeader = getAuditDetails(auditHeader, method);
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Method for Validate Finance Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @param isWIF
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method, boolean isWIF) {
		logger.debug("Entering");

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FinanceDetail financeDetail = (FinanceDetail) auditDetail.getModelData();
		financeDetail.setUserDetails(getLoggedInUser());
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		financeMain.setUserDetails(getLoggedInUser());

		FinanceMain tempFinanceMain = null;
		if (financeMain.isWorkflow()) {
			tempFinanceMain = getFinanceMainDAO().getFinanceMainById(financeMain.getId(), "_Temp", isWIF);
		}
		FinanceMain befFinanceMain = getFinanceMainDAO().getFinanceMainById(financeMain.getId(), "", isWIF);
		FinanceMain oldFinanceMain = financeMain.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = financeMain.getId();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];


		// for work flow process records or (Record to update or Delete with
		// out work flow)
		if (!financeMain.isWorkflow()) { // With out Work flow for update
			// and delete

			if (befFinanceMain == null) { // if records not exists in the
				// main table
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
						PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
			} else {
				if (oldFinanceMain != null && !oldFinanceMain.getLastMntOn()
						.equals(befFinanceMain.getLastMntOn())) {
					if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
							.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41003", errParm, valueParm), usrLanguage));
					} else {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41004", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {

			if (tempFinanceMain == null) { // if records not exists in the
				// Work flow table
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
						PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
			}

			if (tempFinanceMain != null && oldFinanceMain != null
					&& !oldFinanceMain.getLastMntOn().equals(tempFinanceMain.getLastMntOn())) {
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
						PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if (StringUtils.trimToEmpty(method).equals("doApprove") || !financeMain.isWorkflow()) {
			auditDetail.setBefImage(befFinanceMain);
		}

		return auditDetail;
	}

	/**
	 * Common Method for Retrieving AuditDetails List
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
		BulkRateChangeHeader bulkRateChangeHeader =null;
		FinanceDetail financeDetail = null;
		FinanceMain financeMain = null;
		if(auditHeader.getAuditDetail().getModelData() instanceof BulkRateChangeHeader){
			bulkRateChangeHeader = (BulkRateChangeHeader) auditHeader.getAuditDetail().getModelData();
			setLoggedInUser(bulkRateChangeHeader.getUserDetails());
		} else {
			financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
			financeDetail.setUserDetails(getLoggedInUser());
			financeMain = financeDetail.getFinScheduleData().getFinanceMain();
			financeMain.setUserDetails(getLoggedInUser());
		}

		String auditTranType = "";
		if (bulkRateChangeHeader != null) {
			if (method.equals("saveOrUpdate") || method.equals("doApprove") || method.equals("doReject")) {
				if (bulkRateChangeHeader.isWorkflow()) {
					auditTranType = PennantConstants.TRAN_WF;
				}
			}

			if (bulkRateChangeHeader.getBulkRateChangeDetailsList() != null    && bulkRateChangeHeader.getBulkRateChangeDetailsList().size() > 0) {
				auditDetailMap.put("BulkRateChangeDetail",  setBulkRateChangeDetailsData(bulkRateChangeHeader, auditTranType, method));
				auditDetails.addAll(auditDetailMap.get("BulkRateChangeDetail"));
			}

			bulkRateChangeHeader.setAuditDetailMap(auditDetailMap);
			auditHeader.getAuditDetail().setModelData(bulkRateChangeHeader);
			auditHeader.setAuditDetails(auditDetails);
		} else {

			if (method.equals("saveOrUpdate") || method.equals("doApprove") || method.equals("doReject")) {
				if (financeMain.isWorkflow()) {
					auditTranType = PennantConstants.TRAN_WF;
				}
			}

			financeDetail.setAuditDetailMap(auditDetailMap);
			auditHeader.getAuditDetail().setModelData(financeDetail);
			auditHeader.setAuditDetails(auditDetails);

		}
		logger.debug("Leaving");
		return auditHeader;

	}


	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param customerDetails
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setBulkRateChangeDetailsData(BulkRateChangeHeader bulkRateChangeHeader, 
			String auditTranType, String method) { //TODO
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		BulkRateChangeDetails object = new BulkRateChangeDetails();
		String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());

		for (int i = 0; i < bulkRateChangeHeader.getBulkRateChangeDetailsList().size(); i++) {
			BulkRateChangeDetails rateChangeDetails = bulkRateChangeHeader.getBulkRateChangeDetailsList().get(i);

			if (StringUtils.isEmpty(rateChangeDetails.getRecordType())) {
				continue;
			}

			boolean isRcdType = false;
			rateChangeDetails.setWorkflowId(bulkRateChangeHeader.getWorkflowId());
			//rateChangeDetails.setRecordType(bulkRateChangeHeader.getRecordType());
			//rateChangeDetails.setNewRecord(bulkRateChangeHeader.isNew());

			if (rateChangeDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				rateChangeDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (rateChangeDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				rateChangeDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (rateChangeDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				rateChangeDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}

			if (method.equals("saveOrUpdate") && (isRcdType == true)) {
				rateChangeDetails.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (rateChangeDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (rateChangeDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| rateChangeDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}
			rateChangeDetails.setRecordStatus(bulkRateChangeHeader.getRecordStatus());
			rateChangeDetails.setUserDetails(bulkRateChangeHeader.getUserDetails());
			rateChangeDetails.setLastMntOn(bulkRateChangeHeader.getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], 
					rateChangeDetails.getBefImage(), rateChangeDetails));

		}
		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Method deletion of creditReviewSummary list with existing fee type
	 * 
	 * @param bulkRateChangeDetail
	 * @param tableType
	 * 
	 */
	public List<AuditDetail> bulkRateChangeDetailsListDeletion(BulkRateChangeHeader bulkRateChangeHeader, String tableType, String auditTranType) {
		logger.debug("Entering");
		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		if (bulkRateChangeHeader.getBulkRateChangeDetailsList() != null && bulkRateChangeHeader.getBulkRateChangeDetailsList().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new BulkRateChangeDetails());
			for (int i = 0; i < bulkRateChangeHeader.getBulkRateChangeDetailsList().size(); i++) {
				BulkRateChangeDetails bulkRateChangeDetail = bulkRateChangeHeader.getBulkRateChangeDetailsList().get(i);
				if (!StringUtils.trimToEmpty(bulkRateChangeDetail.getRecordType()).equals("") || tableType.equals("")) {
					auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], bulkRateChangeDetail.getBefImage(), bulkRateChangeDetail));
				}
			}
			getBulkRateChangeProcessDetailsDAO().deleteBulkRateChangeDetailsByRef(bulkRateChangeHeader.getBulkRateChangeRef(), tableType);
		}

		logger.debug("Leaving");
		return auditList;
	}

	/**
	 * Method deletion of BulkRateChangeDetails list with existing fee type
	 * 
	 * @param bulkRateChangeHeader
	 * @param tableType
	 */
	public List<AuditDetail> listDeletion(BulkRateChangeHeader bulkRateChangeHeader, String tableType, String auditTranType) {
		logger.debug("Entering");

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		if (bulkRateChangeHeader.getBulkRateChangeDetailsList() != null && bulkRateChangeHeader.getBulkRateChangeDetailsList().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new BulkRateChangeDetails());
			for (int i = 0; i < bulkRateChangeHeader.getBulkRateChangeDetailsList().size(); i++) {
				BulkRateChangeDetails bulkRateChangeDetail = bulkRateChangeHeader.getBulkRateChangeDetailsList().get(i);
				if (!bulkRateChangeDetail.getRecordType().equals("") || tableType.equals("")) {
					auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], bulkRateChangeDetail.getBefImage(), bulkRateChangeDetail));
				}
			}
			getBulkRateChangeProcessDetailsDAO().deleteBulkRateChangeDetailsByRef(bulkRateChangeHeader.getBulkRateChangeRef(), tableType);
		}

		logger.debug("Leaving");
		return auditList;
	}

	public List<AuditDetail> bulkRateChangeDetailsListValidation(List<AuditDetail> auditDetails, String method,String  usrLanguage){
		logger.debug("Entering");
		if(auditDetails!=null && auditDetails.size()>0){
			List<AuditDetail> details = new ArrayList<AuditDetail>();
			for (int i = 0; i < auditDetails.size(); i++) {
				AuditDetail auditDetail =   bulkRateChangeDetailsValidation(auditDetails.get(i), method, usrLanguage);
				details.add(auditDetail); 		
			}
			return details;
		}
		logger.debug("Leaving");
		return new ArrayList<AuditDetail>();
	}

	private AuditDetail bulkRateChangeDetailsValidation(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
		BulkRateChangeDetails bulkRateChangeDetail= (BulkRateChangeDetails) auditDetail.getModelData();

		BulkRateChangeDetails tempBulkRateChangeDetails= null;
		if (bulkRateChangeDetail.isWorkflow()){
			tempBulkRateChangeDetails = getBulkRateChangeProcessDetailsDAO().getDetailsByRateChangeRefAndFinRef(bulkRateChangeDetail.getBulkRateChangeRef(), bulkRateChangeDetail.getFinReference(), "");

		}
		BulkRateChangeDetails befBulkRateChangeDetails= getBulkRateChangeProcessDetailsDAO().getDetailsByRateChangeRefAndFinRef(bulkRateChangeDetail.getBulkRateChangeRef(), bulkRateChangeDetail.getFinReference(), "");

		BulkRateChangeDetails oldBulkRateChangeDetails= bulkRateChangeDetail.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=String.valueOf(bulkRateChangeDetail.getId());
		errParm[0]=PennantJavaUtil.getLabel("label_SubCategoryCode")+":"+valueParm[0];
		//bulkRateChangeDetail.setWorkflowId(0);
		if (bulkRateChangeDetail.isNew()){ // for New record or new record into work flow

			if (!bulkRateChangeDetail.isWorkflow()){// With out Work flow only new records  
				if (befBulkRateChangeDetails !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (bulkRateChangeDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befBulkRateChangeDetails !=null || tempBulkRateChangeDetails!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befBulkRateChangeDetails ==null || tempBulkRateChangeDetails!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!bulkRateChangeDetail.isWorkflow()){	// With out Work flow for update and delete

				if (befBulkRateChangeDetails ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldBulkRateChangeDetails!=null && !oldBulkRateChangeDetails.getLastMntOn().equals(befBulkRateChangeDetails.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempBulkRateChangeDetails==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (oldBulkRateChangeDetails!=null && !oldBulkRateChangeDetails.getLastMntOn().equals(tempBulkRateChangeDetails.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if(StringUtils.trimToEmpty(method).equals("doApprove") || !bulkRateChangeDetail.isWorkflow()){
			bulkRateChangeDetail.setBefImage(befBulkRateChangeDetails);	
		}

		logger.debug("Leaving");
		return auditDetail;
	}

	/**
	 * Method to save what if inquiry lists
	 */
	public void listSave(FinScheduleData finDetail, String tableType, boolean isWIF , long logKey) {
		logger.debug("Entering ");
		HashMap<Date, Integer> mapDateSeq = new HashMap<Date, Integer>();

		// Finance Schedule Details
		for (int i = 0; i < finDetail.getFinanceScheduleDetails().size(); i++) {
			finDetail.getFinanceScheduleDetails().get(i).setLastMntBy(finDetail.getFinanceMain().getLastMntBy());
			finDetail.getFinanceScheduleDetails().get(i).setFinReference(finDetail.getFinReference());
			int seqNo = 0;

			if (mapDateSeq.containsKey(finDetail.getFinanceScheduleDetails().get(i).getSchDate())) {
				seqNo = mapDateSeq.get(finDetail.getFinanceScheduleDetails().get(i).getSchDate());
				mapDateSeq.remove(finDetail.getFinanceScheduleDetails().get(i).getSchDate());
			}
			seqNo = seqNo + 1;
			mapDateSeq.put(finDetail.getFinanceScheduleDetails().get(i).getSchDate(), seqNo);
			finDetail.getFinanceScheduleDetails().get(i).setSchSeq(seqNo);
			finDetail.getFinanceScheduleDetails().get(i).setLogKey(logKey);
		}

		getFinanceScheduleDetailDAO().saveList(finDetail.getFinanceScheduleDetails(), tableType, isWIF);

		/*//Finance Deferment Header Details
		for (int i = 0; i < finDetail.getDefermentHeaders().size(); i++) {
			finDetail.getDefermentHeaders().get(i).setFinReference(finDetail.getFinReference());
			finDetail.getDefermentHeaders().get(i).setLogKey(logKey);
		}
		getDefermentHeaderDAO().saveList(finDetail.getDefermentHeaders(), tableType, isWIF);

		//Finance Deferment Details
		for (int i = 0; i < finDetail.getDefermentDetails().size(); i++) {
			finDetail.getDefermentDetails().get(i).setFinReference(finDetail.getFinReference());
			finDetail.getDefermentDetails().get(i).setLogKey(logKey);
		}
		getDefermentDetailDAO().saveList(finDetail.getDefermentDetails(), tableType, isWIF);*/

		//Finance Repay Instruction Details
		for (int i = 0; i < finDetail.getRepayInstructions().size(); i++) {
			finDetail.getRepayInstructions().get(i).setFinReference(finDetail.getFinReference());
			finDetail.getRepayInstructions().get(i).setLogKey(logKey);
		}
		getRepayInstructionDAO().saveList(finDetail.getRepayInstructions(), tableType, isWIF);

		FinanceMain aFinanceMain = finDetail.getFinanceMain();
		aFinanceMain.setAvailedDefRpyChange(aFinanceMain.getAvailedDefRpyChange()+1);
		aFinanceMain.setVersion(aFinanceMain.getVersion()+1);
		getFinanceMainDAO().update(aFinanceMain, TableType.MAIN_TAB, false);

		logger.debug("Leaving ");
	}

	/**
	 * Method to delete schedule, disbursement lists.
	 * 
	 * @param finDetail
	 * @param tableType
	 * @param isWIF
	 */
	public void listDeletion(FinScheduleData finDetail, String tableType, boolean isWIF) {
		logger.debug("Entering ");

		getFinanceScheduleDetailDAO().deleteByFinReference(finDetail.getFinReference(), tableType, isWIF, 0);
		/*getDefermentHeaderDAO().deleteByFinReference(finDetail.getFinReference(), tableType, isWIF, 0);
		getDefermentDetailDAO().deleteByFinReference(finDetail.getFinReference(), tableType, isWIF, 0);*/
		getRepayInstructionDAO().deleteByFinReference(finDetail.getFinReference(), tableType, isWIF, 0);

		logger.debug("Leaving ");
	}

	/**
	 * Method to get Schedule related data.
	 * 
	 * @param finReference
	 *            (String)
	 * @param isWIF
	 *            (boolean)
	 * **/
	public FinScheduleData getFinSchDataByFinRef(String finReference, String type, long logKey) {
		logger.debug("Entering");

		FinScheduleData finSchData = new FinScheduleData();
		finSchData.setFinanceMain(getFinanceMainDAO().getFinanceMainById(finReference, type, false));
		finSchData.setFinanceScheduleDetails(getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, type, false));
		if(logKey != 0){
			finSchData.setDisbursementDetails(getFinanceDisbursementDAO().getFinanceDisbursementDetails( finReference, type, false));
		}
		finSchData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(finReference, type, false));
		/*finSchData.setDefermentHeaders(getDefermentHeaderDAO().getDefermentHeaders(finReference, type, false));
		finSchData.setDefermentDetails(getDefermentDetailDAO().getDefermentDetails(finReference, type, false));*/

		if(logKey == 0){
			finSchData.setFinanceType(getFinanceTypeDAO().getFinanceTypeByID(finSchData.getFinanceMain().getFinType(), type));
			//finSchData.setFeeRules(getFinFeeChargesDAO().getFeeChargesByFinRef(finReference, false, ""));
			finSchData = getFinMaintainenceDetails(finSchData);
			finSchData.setAccrueValue(getAccrueAmount(finReference));
		}
		logger.debug("Leaving");
		return finSchData;
	}

	/**
	 * Method for Get the Accrue Details
	 * @param finReference
	 * @return
	 */
	@Override
	public BigDecimal getAccrueAmount(String finReference){
		return getProfitDetailsDAO().getAccrueAmount(finReference);
	}

	@Override
	public FinScheduleData getFinMaintainenceDetails(FinScheduleData finSchData){
		logger.debug("Entering");
		String finReference = finSchData.getFinanceMain().getFinReference();
		finSchData.setRepayDetails(getFinanceRepaymentsByFinRef(finReference, false));
		finSchData.setPenaltyDetails(getFinancePenaltysByFinRef(finReference));
		logger.debug("Leaving");
		return finSchData;
	}

	/**
	 * Method to get FinanceRepayments By FinReference
	 * 
	 * @param id
	 * @param type
	 * @return
	 */
	public List<OverdueChargeRecovery> getFinancePenaltysByFinRef(final String id) {
		return getRecoveryDAO().getFinancePenaltysByFinRef(id, "");
	}


	/**
	 * Method to get FinanceRepayments By FinReference
	 * 
	 * @param id
	 * @param type
	 * @return
	 */
	public List<FinanceRepayments> getFinanceRepaymentsByFinRef(final String id, boolean isRpyCancelProc) {
		return getFinanceRepaymentsDAO().getFinRepayListByFinRef(id, isRpyCancelProc, "");
	}




	/**
	 * Method For Preparing List of AuditDetails for Customer Ratings
	 * 
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
	/*private List<AuditDetail> processBulkRateChangeDetails(List<AuditDetail> auditDetails, long detailId, String type) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			BulkRateChangeDetails bulkRateChangeDetail = (BulkRateChangeDetails) auditDetails.get(i).getModelData();
			bulkRateChangeDetail.setBulkProcessId(detailId);
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (type.equals("")) {
				approveRec = true;
				bulkRateChangeDetail.setRoleCode("");
				bulkRateChangeDetail.setNextRoleCode("");
				bulkRateChangeDetail.setTaskId("");
				bulkRateChangeDetail.setNextTaskId("");
			}

			if (bulkRateChangeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (bulkRateChangeDetail.isNewRecord()) {
				saveRecord = true;
				if (bulkRateChangeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					bulkRateChangeDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (bulkRateChangeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					bulkRateChangeDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (bulkRateChangeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					bulkRateChangeDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (bulkRateChangeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (bulkRateChangeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
					updateRecord = true;
			} else if (bulkRateChangeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (bulkRateChangeDetail.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = bulkRateChangeDetail.getRecordType();
				recordStatus = bulkRateChangeDetail.getRecordStatus();
				bulkRateChangeDetail.setRecordType("");
				bulkRateChangeDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			if (saveRecord) {
				getBulkRateChangeProcessDetailsDAO().save(bulkRateChangeDetail, type);
			}

			if (updateRecord) {
				getBulkRateChangeProcessDetailsDAO().update(bulkRateChangeDetail, type);
			}

			if (deleteRecord) {
				getBulkRateChangeProcessDetailsDAO().delete(bulkRateChangeDetail, type);
			}

			if (approveRec) {
				bulkRateChangeDetail.setRecordType(rcdType);
				bulkRateChangeDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(bulkRateChangeDetail);
		}

		return auditDetails;

	}*/

	/**
	 * Method for Processing Bulk Finance for Deferment Process
	 * @throws Exception 
	 */
	@Override
	public List<ScheduleMapDetails> getDeferedDates(
			List<BulkRateChangeDetails> defermentChangeFinances, String cbRecalType,
			Date reCalFromDate, Date reCalToDate) {
		logger.debug("Entering");

		Map<String, String> referencesMap = new HashMap<String, String>();
		for (BulkRateChangeDetails bulkProcessDetail : defermentChangeFinances) {
			if (!referencesMap.containsKey(bulkProcessDetail.getFinReference())) {
				referencesMap.put(bulkProcessDetail.getFinReference(),
						bulkProcessDetail.getFinReference());
			}
		}

		List<String> referencesList = new ArrayList<String>(referencesMap.values());
		referencesMap = null;

		logger.debug("Leaving");
		return getFinSchdDetailTermByDates(referencesList, reCalFromDate, reCalToDate);
	}	

	public List<ScheduleMapDetails> getFinSchdDetailTermByDates(List<String> referencesList, Date reCalFromDate, Date reCalToDate){
		logger.debug("Entering");

		List<String> subRefList = null;
		List<ScheduleMapDetails> scheDetails = null;
		int referencesListSize = referencesList.size();
		int startIndex = 0, endIndex = 0;

		if (referencesListSize >= 500) {
			endIndex = 500;
		} else {
			endIndex = referencesListSize;
		}

		scheDetails = new ArrayList<ScheduleMapDetails>();
		while (referencesListSize > 0) {
			subRefList = referencesList.subList(startIndex, endIndex);
			scheDetails.addAll(getFinanceScheduleDetailDAO().getFinSchdDetailTermByDates(subRefList, reCalFromDate, reCalToDate));
			referencesListSize = referencesListSize - subRefList.size();

			startIndex = endIndex;
			if (referencesListSize > 500) {
				endIndex = endIndex + 500;
			} else {
				endIndex = endIndex + referencesListSize;
			}
		}

		logger.debug("Leaving");
		return scheDetails;

	}

	//getter / setter

	public BulkRateChangeProcessDAO getBulkRateChangeProcessDAO() {
		return bulkRateChangeProcessDAO;
	}

	public void setBulkRateChangeProcessDAO(BulkRateChangeProcessDAO bulkRateChangeProcessDAO) {
		this.bulkRateChangeProcessDAO = bulkRateChangeProcessDAO;
	}

	public BulkRateChangeProcessDetailsDAO getBulkRateChangeProcessDetailsDAO() {
		return bulkRateChangeProcessDetailsDAO;
	}

	public void setBulkRateChangeProcessDetailsDAO(BulkRateChangeProcessDetailsDAO bulkRateChangeProcessDetailsDAO) {
		this.bulkRateChangeProcessDetailsDAO = bulkRateChangeProcessDetailsDAO;
	}

	public ExtendedFieldDetailDAO getExtendedFieldDetailDAO() {
		return extendedFieldDetailDAO;
	}

	public void setExtendedFieldDetailDAO(ExtendedFieldDetailDAO extendedFieldDetailDAO) {
		this.extendedFieldDetailDAO = extendedFieldDetailDAO;
	}

	public LoggedInUser getLoggedInUser() {
		return loggedInUser;
	}


	public void setLoggedInUser(LoggedInUser loggedInUser) {
		this.loggedInUser = loggedInUser;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}


	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	//******************************************* Bulk Rate Change *******************************


}