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
 * FileName    		:  TreasuaryFinanceServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-11-2013    														*
 *                                                                  						*
 * Modified Date    :  04-11-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-11-2013       Pennant	                 0.1                                            * 
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



import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.dao.finance.TreasuaryFinHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.CustomerStatusCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.InvestmentFinHeader;
import com.pennant.backend.model.lmtmasters.CommidityLoanDetail;
import com.pennant.backend.model.lmtmasters.CommidityLoanHeader;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.service.finance.GenericFinanceDetailService;
import com.pennant.backend.service.finance.TreasuaryFinanceService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.coreinterface.exception.AccountNotFoundException;

/**
 * Service implementation for methods that depends on <b>TreasuaryFinance</b>.<br>
 * 
 */
public class TreasuaryFinanceServiceImpl extends GenericFinanceDetailService implements TreasuaryFinanceService {
	private final static Logger logger = Logger.getLogger(TreasuaryFinanceServiceImpl.class);
	
	private TreasuaryFinHeaderDAO treasuaryFinHeaderDAO;

	public TreasuaryFinHeaderDAO getTreasuaryFinHeaderDAO() {
		return treasuaryFinHeaderDAO;
	}
	public void setTreasuaryFinHeaderDAO(TreasuaryFinHeaderDAO treasuaryFinHeaderDAO) {
		this.treasuaryFinHeaderDAO = treasuaryFinHeaderDAO;
	}

	@Override
	public InvestmentFinHeader getTreasuaryFinance() {
		return getTreasuaryFinHeaderDAO().getTreasuaryFinHeader();
	}
	
	@Override
	public InvestmentFinHeader getNewTreasuaryFinance() {
		return getTreasuaryFinHeaderDAO().getNewTreasuaryFinHeader();
	}


	@Override
	public List<FinanceDetail> getFinanceDetails(InvestmentFinHeader investmentFinHeader) {
		List<FinanceDetail> financeDetails = new ArrayList<FinanceDetail>();

		List<FinanceMain>  investmentDeals = getTreasuaryFinHeaderDAO().getInvestmentDealList(investmentFinHeader, "TVIEW");

		for(FinanceMain financeMain : investmentDeals){
			financeMain = getFinanceMainDAO().getFinanceMainById(financeMain.getFinReference(), "_View", false);
			FinanceDetail financeDetail = new FinanceDetail();
			financeDetail.setNewRecord(false);
			financeDetail.getFinScheduleData().setFinReference(financeMain.getFinReference());
			financeDetail.getFinScheduleData().setFinanceMain(financeMain);
			financeDetails.add(financeDetail);
		}

		return financeDetails;
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table TreasuaryFinances/TreasuaryFinances_Temp 
	 * 			by using TreasuaryFinanceDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using TreasuaryFinanceDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtTreasuaryFinances by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");	
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		String tableType="";
		InvestmentFinHeader investmentFinHeader = (InvestmentFinHeader) auditHeader.getAuditDetail().getModelData();
		List<FinanceDetail> finDetailList = investmentFinHeader.getFinanceDetailsList();

		if (investmentFinHeader.isWorkflow()) {
			tableType="_TEMP";
		}

		if (investmentFinHeader.isNew()) {
			getTreasuaryFinHeaderDAO().save(investmentFinHeader, tableType);
		}else{
			getTreasuaryFinHeaderDAO().update(investmentFinHeader, tableType);
		}
		

		if (finDetailList != null && !finDetailList.isEmpty()) {
			List<AuditDetail> details = investmentFinHeader.getAuditDetailMap().get("FinanceMain");
			List<AuditDetail> dealAuditDetails = processingDeals(details, investmentFinHeader, tableType);
			auditDetails.addAll(dealAuditDetails);
			
			auditDetails.addAll(saveOrUpdateDetails(finDetailList, tableType, auditHeader.getAuditTranType()));
		}
		
		for (FinanceDetail financeDetail : finDetailList) {
			saveScheduleDetails(financeDetail, "_TEMP", false);
		}
		
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}
	
	
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table TreasuaryFinances/TreasuaryFinances_Temp 
	 * 			by using TreasuaryFinanceDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using TreasuaryFinanceDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtTreasuaryFinances by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	@Override
	public AuditHeader saveOrUpdateDeal(AuditHeader auditHeader) {
		logger.debug("Entering");	
		auditHeader = dealBusinessValidation(auditHeader, "saveOrUpdate");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		String type="";
		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		List<FinanceDetail> finDetailList = new ArrayList<FinanceDetail>();
		finDetailList.add(financeDetail);

		if (financeMain.isWorkflow()) {
			type = "_TEMP";
		}

		if (financeDetail.isNew()) {
			getFinanceMainDAO().save(financeMain, type, false);
		}else{
			getFinanceMainDAO().update(financeMain, type, false);
		}
		
		saveScheduleDetails(financeDetail, "_TEMP", false);

		auditDetails.addAll(saveOrUpdateDetails(finDetailList, type, auditHeader.getAuditTranType()));

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), excludeFields);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], financeMain.getBefImage(), financeMain));
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	private List<AuditDetail> saveOrUpdateDetails(List<FinanceDetail> financeDetails,  String tableType, String tranType) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		
		List<AuditDetail> auditDetail;
		for (FinanceDetail finDetail : financeDetails) {
			List<AuditDetail> details = finDetail.getAuditDetailMap().get("DocumentDetails");

			if(details != null && !details.isEmpty()) {
				FinanceMain financeMain = finDetail.getFinScheduleData().getFinanceMain();
				auditDetail = processingDocumentDetailsList(details, tableType, financeMain.getFinReference(), financeMain);
				auditDetails.addAll(auditDetail);

			}
			// set Finance Check List audit details to auditDetails
			if (finDetail.getFinanceCheckList() != null && !finDetail.getFinanceCheckList().isEmpty()) {
				auditDetail = getCheckListDetailService().saveOrUpdate(finDetail, tableType);
				auditDetails.addAll(auditDetail);
			}

			// Save Commidity Header & Details
			CommidityLoanHeader commidityLoanHeader = finDetail.getCommidityLoanHeader();
			if (commidityLoanHeader != null) {
				commidityLoanHeader.setCommidityLoanDetails(finDetail.getCommidityLoanDetails());
				auditDetails.addAll(getCommidityLoanDetailService().saveOrUpdate(commidityLoanHeader, tableType, tranType));
			}
		}

		return auditDetails;
	}
	
	
	private List<AuditDetail> doApproveFinanceDetails(FinanceDetail financeDetails,  String tableType, String tranType) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		
		FinanceMain financeMain = financeDetails.getFinScheduleData().getFinanceMain();
		
		List<AuditDetail> auditDetail;

		List<AuditDetail> details = financeDetails.getAuditDetailMap().get("DocumentDetails");
		if(details != null && !details.isEmpty()) {
			auditDetail = processingDocumentDetailsList(details, tableType, financeMain.getFinReference(), financeMain);
			auditDetails.addAll(auditDetail);

		}
		// set Finance Check List audit details to auditDetails
		if (financeDetails.getFinanceCheckList() != null && !financeDetails.getFinanceCheckList().isEmpty()) {
			auditDetail = getCheckListDetailService().doApprove(financeDetails, tableType);
			getCheckListDetailService().delete(financeDetails, "_Temp", tranType);
			auditDetails.addAll(auditDetail);
		}

		// Commidity Header & Details
		CommidityLoanHeader commidityLoanHeader = financeDetails.getCommidityLoanHeader();
		if (commidityLoanHeader != null && financeDetails.getCommidityLoanDetails() !=null) {
			commidityLoanHeader.setCommidityLoanDetails(financeDetails.getCommidityLoanDetails());
			auditDetails.addAll(getCommidityLoanDetailService().doApprove(commidityLoanHeader, "", tranType));
			getCommidityLoanDetailService().delete(commidityLoanHeader, "_Temp", PennantConstants.TRAN_WF);
		}
		
		saveFeeChargeList(financeDetails.getFinScheduleData(), false, tableType);
		getPostingsDAO().deleteChargesBatch(financeMain.getFinReference(), false, "_Temp");

		return auditDetails;
	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table TreasuaryFinances by using TreasuaryFinanceDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtTreasuaryFinances by using auditHeaderDAO.addAudit(auditHeader)    
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		InvestmentFinHeader investmentFinHeader = (InvestmentFinHeader) auditHeader.getAuditDetail().getModelData();
		
		for (FinanceDetail financeDetail : investmentFinHeader.getFinanceDetailsList()) {
			getFinanceMainDAO().delete(financeDetail.getFinScheduleData().getFinanceMain(),"_TEMP", false);
			listDeletion(financeDetail.getFinScheduleData(), "_Temp", false);
		}
		
		getTreasuaryFinHeaderDAO().delete(investmentFinHeader,"");
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getTreasuaryFinanceById fetch the details by using TreasuaryFinanceDAO's getTreasuaryFinanceById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return TreasuaryFinance
	 */

	@Override
	public InvestmentFinHeader getTreasuaryFinanceById(String id) {
		return getTreasuaryFinHeaderDAO().getTreasuaryFinHeaderById(id,"_View");
	}
	/**
	 * getApprovedTreasuaryFinanceById fetch the details by using TreasuaryFinanceDAO's getTreasuaryFinanceById method .
	 * with parameter id and type as blank. it fetches the approved records from the TreasuaryFinances.
	 * @param id (String)
	 * @return TreasuaryFinance
	 */

	public InvestmentFinHeader getApprovedTreasuaryFinanceById(String id) {
		return getTreasuaryFinHeaderDAO().getTreasuaryFinHeaderById(id,"_AView");
	}

	/**
	 * This method refresh the Record.
	 * @param TreasuaryFinance (treasuaryFinance)
	 * @return treasuaryFinance
	 */
	@Override
	public InvestmentFinHeader refresh(InvestmentFinHeader treasuaryFinance) {
		logger.debug("Entering");
		getTreasuaryFinHeaderDAO().refresh(treasuaryFinance);
		getTreasuaryFinHeaderDAO().initialize(treasuaryFinance);
		logger.debug("Leaving");
		return treasuaryFinance;
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getTreasuaryFinHeaderDAO().delete with parameters treasuaryFinance,""
	 * 		b)  NEW		Add new record in to main table by using getTreasuaryFinHeaderDAO().save with parameters treasuaryFinance,""
	 * 		c)  EDIT	Update record in the main table by using getTreasuaryFinHeaderDAO().update with parameters treasuaryFinance,""
	 * 3)	Delete the record from the workFlow table by using getTreasuaryFinHeaderDAO().delete with parameters treasuaryFinance,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtTreasuaryFinances by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtTreasuaryFinances by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) throws AccountNotFoundException{
		logger.debug("Entering");
		String tranType="";
		auditHeader = dealBusinessValidation(auditHeader, "doApprove");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		FinanceDetail financeDetail = new FinanceDetail();
		BeanUtils.copyProperties((FinanceDetail) auditHeader.getAuditDetail().getModelData(), financeDetail);
		
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		
		Date curBDay = (Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");
		financeMain.setFinApprovedDate(curBDay);
		
		auditHeader = executeAccountingProcess(auditHeader, curBDay);
		if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
			return auditHeader;
		}
		
		if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;
			getFinanceMainDAO().delete(financeMain, "", false);

		} else {
			financeMain.setRoleCode("");
			financeMain.setNextRoleCode("");
			financeMain.setTaskId("");
			financeMain.setNextTaskId("");
			financeMain.setWorkflowId(0);

			if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {	
				tranType = PennantConstants.TRAN_ADD;
				financeMain.setRecordType("");
				getFinanceMainDAO().save(financeMain, "", false);
			} else {
				tranType=PennantConstants.TRAN_UPD;
				financeMain.setRecordType("");
				getFinanceMainDAO().update(financeMain, "", false);
			}
			
			saveScheduleDetails(financeDetail, "", false);
		}
		
		auditDetails.addAll(doApproveFinanceDetails(financeDetail, "", tranType));
		
		getFinanceMainDAO().delete(financeMain,"_TEMP", false);
		listDeletion(financeDetail.getFinScheduleData(), "_Temp", false);
		
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), excludeFields);
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.getAuditDetail().setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], financeMain.getBefImage(), financeMain));
		getAuditHeaderDAO().addAudit(auditHeader);
		
		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);		
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], financeMain.getBefImage(), financeMain));
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		
		// update Deal status for the Investment
		getTreasuaryFinHeaderDAO().updateDealsStatus(financeMain.getInvestmentRef());
		
		//Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(financeDetail);
		logger.debug("Leaving");
		
		return auditHeader;
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getTreasuaryFinHeaderDAO().delete with parameters treasuaryFinance,""
	 * 		b)  NEW		Add new record in to main table by using getTreasuaryFinHeaderDAO().save with parameters treasuaryFinance,""
	 * 		c)  EDIT	Update record in the main table by using getTreasuaryFinHeaderDAO().update with parameters treasuaryFinance,""
	 * 3)	Delete the record from the workFlow table by using getTreasuaryFinHeaderDAO().delete with parameters treasuaryFinance,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtTreasuaryFinances by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtTreasuaryFinances by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader doConfirm(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType="";
		auditHeader = businessValidation(auditHeader, "doConfirm");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		InvestmentFinHeader header = new InvestmentFinHeader();
		InvestmentFinHeader aInvFinHeader = new InvestmentFinHeader();
		BeanUtils.copyProperties((InvestmentFinHeader) auditHeader.getAuditDetail().getModelData(), aInvFinHeader);
		BeanUtils.copyProperties((InvestmentFinHeader) auditHeader.getAuditDetail().getModelData(), header);
		List<FinanceDetail> finDetailList = header.getFinanceDetailsList();

		aInvFinHeader.setRoleCode("");
		aInvFinHeader.setNextRoleCode("");
		aInvFinHeader.setTaskId("");
		aInvFinHeader.setNextTaskId("");
		aInvFinHeader.setWorkflowId(0);

		if (aInvFinHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) 
		{	
			tranType=PennantConstants.TRAN_ADD;
			aInvFinHeader.setRecordType("");
			getTreasuaryFinHeaderDAO().save(aInvFinHeader, "");
		} else {
			tranType=PennantConstants.TRAN_UPD;
			aInvFinHeader.setRecordType("");
			getTreasuaryFinHeaderDAO().update(aInvFinHeader,"");
		}


		getTreasuaryFinHeaderDAO().delete(aInvFinHeader,"_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(aInvFinHeader);

		String tableType = "_TEMP";
		if (finDetailList != null && !finDetailList.isEmpty()) {
			List<AuditDetail> details = header.getAuditDetailMap().get("FinanceMain");
			List<AuditDetail> dealAuditDetails = processingDeals(details, header,  tableType);
			auditDetails.addAll(dealAuditDetails);			
		}
		
		for (FinanceDetail financeDetail : finDetailList) {
			saveScheduleDetails(financeDetail, "_TEMP", false);
		}

		auditDetails.addAll(saveOrUpdateDetails(finDetailList, tableType, auditHeader.getAuditTranType()));

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getTreasuaryFinHeaderDAO().delete with parameters treasuaryFinance,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtTreasuaryFinances by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		
		InvestmentFinHeader treasuaryFinance = (InvestmentFinHeader) auditHeader.getAuditDetail().getModelData();
		
		for (FinanceDetail financeDetail : treasuaryFinance.getFinanceDetailsList()) {
			getFinanceMainDAO().delete(financeDetail.getFinScheduleData().getFinanceMain(),"_TEMP", false);
			listDeletion(financeDetail.getFinScheduleData(), "_Temp", false);
		}
	
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getTreasuaryFinHeaderDAO().delete(treasuaryFinance,"_TEMP");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps.
	 * 1)	get the details from the auditHeader. 
	 * 2)	fetch the details from the tables
	 * 3)	Validate the Record based on the record details. 
	 * 4) 	Validate for any business validation.
	 * 5)	for any mismatch conditions Fetch the error details from getTreasuaryFinHeaderDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */


	private AuditHeader businessValidation(AuditHeader auditHeader, String method){
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		AuditDetail auditDetail;
		if("doConfirm".equals(method)) {
			auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), "doApprove");
			auditHeader.setAuditDetail(auditDetail);
			auditHeader.setErrorList(auditDetail.getErrorDetails());
			auditHeader = getAuditDetails(auditHeader,  "doConfirm");
			method = "saveOrUpdate";
		} else {
			auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
			auditHeader.setAuditDetail(auditDetail);
			auditHeader.setErrorList(auditDetail.getErrorDetails());
			auditHeader = getAuditDetails(auditHeader,  method);
		}


		InvestmentFinHeader finHeader = (InvestmentFinHeader) auditHeader.getAuditDetail().getModelData();
		String usrLanguage = finHeader.getUserDetails().getUsrLanguage();


		List<FinanceDetail> dealList =  finHeader.getFinanceDetailsList();

		for (FinanceDetail financeDetail : dealList) {
			 List<CommidityLoanDetail>  commidityLoanDetails = financeDetail.getCommidityLoanDetails();
				if (commidityLoanDetails != null && !commidityLoanDetails.isEmpty()) {
					auditDetails.addAll(getCommidityLoanDetailService().validate(commidityLoanDetails, financeDetail.getFinScheduleData().getFinanceMain().getWorkflowId(), method, auditHeader.getAuditTranType(), usrLanguage));
				}
			
			if (financeDetail.getFinanceCheckList() != null && !financeDetail.getFinanceCheckList().isEmpty()) {
				List<AuditDetail> details;
				details = getCheckListDetailService().validate(financeDetail, method, usrLanguage);
				auditDetails.addAll(details);
			}
		}


		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());	
		
	/*	if(auditDetail.getModelData() instanceof InvestmentFinHeader) {
			return auditDetail;
		}*/
		
		InvestmentFinHeader treasuaryFinance = (InvestmentFinHeader) auditDetail.getModelData();

		InvestmentFinHeader tempTreasuaryFinance= null;

		if (treasuaryFinance.isWorkflow()){
			tempTreasuaryFinance = getTreasuaryFinHeaderDAO().getTreasuaryFinHeaderById(treasuaryFinance.getId(), "_Temp");
		}

		InvestmentFinHeader befTreasuaryFinance= getTreasuaryFinHeaderDAO().getTreasuaryFinHeaderById(treasuaryFinance.getId(), "");

		InvestmentFinHeader old_TreasuaryFinance= treasuaryFinance.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=treasuaryFinance.getId();
		errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

		if (treasuaryFinance.isNew()){ // for New record or new record into work flow

			if (!treasuaryFinance.isWorkflow()){// With out Work flow only new records  
				if (befTreasuaryFinance !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (treasuaryFinance.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befTreasuaryFinance !=null || tempTreasuaryFinance!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befTreasuaryFinance ==null || tempTreasuaryFinance!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!treasuaryFinance.isWorkflow()){	// With out Work flow for update and delete

				if (befTreasuaryFinance ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (old_TreasuaryFinance!=null && !old_TreasuaryFinance.getLastMntOn().equals(befTreasuaryFinance.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempTreasuaryFinance==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
				
				if (old_TreasuaryFinance!=null && !old_TreasuaryFinance.getLastMntOn().equals(tempTreasuaryFinance.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if(StringUtils.trimToEmpty(method).equals("doApprove") || !treasuaryFinance.isWorkflow()){
			treasuaryFinance.setBefImage(befTreasuaryFinance);	
		}

		return auditDetail;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param contributorHeader
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> getDealAuditDetail(InvestmentFinHeader invFinHeader, String auditTranType,  String method) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		FinanceMain object = new FinanceMain();

		String[] fields = PennantJavaUtil.getFieldDetails(object, excludeFields);

		for (int i = 0; i < invFinHeader.getFinanceDetailsList().size(); i++) {

			FinanceDetail financeDetail = invFinHeader.getFinanceDetailsList().get(i);
			FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

			financeMain.setWorkflowId(invFinHeader.getWorkflowId());

			boolean isRcdType = false;

			if (financeMain.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				financeMain.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (financeMain.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				financeMain.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (financeMain.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				financeMain.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}

			if ((method.equals("doConfirm") || method.equals("saveOrUpdate")) && (isRcdType == true)) {
				financeMain.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (financeMain.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (financeMain.getRecordType().equalsIgnoreCase(
						PennantConstants.RECORD_TYPE_DEL)
						|| financeMain.getRecordType().equalsIgnoreCase(
								PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			financeMain.setRecordStatus(invFinHeader.getRecordStatus());
			financeMain.setUserDetails(invFinHeader.getUserDetails());
			financeMain.setLastMntOn(invFinHeader.getLastMntOn());

			if (!financeMain.getRecordType().equals("")) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						financeMain.getBefImage(), financeMain));
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for Contributor Details
	 * 
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
	private List<AuditDetail> processingDeals(List<AuditDetail> auditDetails, InvestmentFinHeader header, String type) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			FinanceMain financeMain = (FinanceMain) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";

			if (type.equals("")) {
				approveRec = true;
				financeMain.setRoleCode("");
				financeMain.setNextRoleCode("");
				financeMain.setTaskId("");
				financeMain.setNextTaskId("");
			} else {
				financeMain.setRoleCode(header.getRoleCode());
				financeMain.setNextRoleCode(header.getNextRoleCode());
				financeMain.setTaskId(header.getTaskId());
				financeMain.setNextTaskId(header.getNextTaskId());
			}

			financeMain.setWorkflowId(header.getWorkflowId());

			if (financeMain.getRecordType()
					.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (financeMain.isNewRecord()) {
				saveRecord = true;
				if (financeMain.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					financeMain.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (financeMain.getRecordType().equalsIgnoreCase(
						PennantConstants.RCD_DEL)) {
					financeMain.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (financeMain.getRecordType().equalsIgnoreCase(
						PennantConstants.RCD_UPD)) {
					financeMain.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (financeMain.getRecordType().equalsIgnoreCase(
					PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (financeMain.getRecordType().equalsIgnoreCase(
					PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (financeMain.getRecordType().equalsIgnoreCase(
					PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (financeMain.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = financeMain.getRecordType();
				recordStatus = financeMain.getRecordStatus();
				financeMain.setRecordType("");
				financeMain.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			financeMain.setLastMntBy(header.getLastMntBy());
			if (saveRecord) {
				getFinanceMainDAO().save(financeMain, type, false);
			}

			if (updateRecord) {
				getFinanceMainDAO().update(financeMain, type, false);
			}

			if (deleteRecord) {
				getFinanceMainDAO().delete(financeMain, type, false);
			}

			if (approveRec) {
				financeMain.setRecordType(rcdType);
				financeMain.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(financeMain);
			
		}

		logger.debug("Leaving");
		return auditDetails;

	}


	/**
	 * Common Method for Retrieving AuditDetails List
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	public AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		InvestmentFinHeader investmentFinHeader = (InvestmentFinHeader) auditHeader.getAuditDetail().getModelData();
		String auditTranType = "";

		if (method.equals("saveOrUpdate") || method.equals("doApprove") || method.equals("doReject") || method.equals("doConfirm")) {
			if (investmentFinHeader.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		List<FinanceDetail> finDetailsList = investmentFinHeader.getFinanceDetailsList();
		
		if (finDetailsList != null  && !finDetailsList.isEmpty()) {
			List<AuditDetail> dealAuditDetails = getDealAuditDetail(investmentFinHeader, auditTranType, method);
			auditDetailMap.put("FinanceMain", dealAuditDetails);

			//Finance Document Details
			if (finDetailsList != null && !finDetailsList.isEmpty()) {
				for (FinanceDetail financeDetail : finDetailsList) {
					auditDetails.addAll(getAuditDetails(financeDetail, method, auditTranType));
				}
			}

		}
		

		investmentFinHeader.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(investmentFinHeader);
		
		processAuditDetail(auditDetails);
		
		auditHeader.setAuditDetails(auditDetails);
		logger.debug("Leaving ");

		return auditHeader;
	}

	private void processAuditDetail(List<AuditDetail> auditDetails) {
		int docAuditSeq = 1;
		int checkListAuditSeq = 1;
		int commudityAuditSeq = 1;
		int commudityHeadeAuditSeq = 1;


		for (AuditDetail auditDetail : auditDetails) {			
			if(auditDetail.getModelData() instanceof DocumentDetails) {
				auditDetail.setAuditSeq(docAuditSeq);
				docAuditSeq++;
			}

			if(auditDetail.getModelData() instanceof FinanceCheckListReference) {
				auditDetail.setAuditSeq(checkListAuditSeq);
				checkListAuditSeq++;
			}

			if(auditDetail.getModelData() instanceof CommidityLoanDetail) {
				auditDetail.setAuditSeq(commudityAuditSeq);
				commudityAuditSeq++;
			}

			if(auditDetail.getModelData() instanceof CommidityLoanHeader) {
				auditDetail.setAuditSeq(commudityHeadeAuditSeq);
				commudityHeadeAuditSeq++;
			}


		}

	}

	private List<AuditDetail> getAuditDetails(FinanceDetail financeDetail, String method, String auditTranType) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		
		List<AuditDetail> auditDetail;
		
		if(financeDetail.getDocumentDetailsList() != null) {
			auditDetail = setDocumentDetailsAuditData(financeDetail, auditTranType, method);
			financeDetail.getAuditDetailMap().put("DocumentDetails", auditDetail);
			auditDetails.addAll(auditDetail);
		}

		//Finance Check List Details
		List<FinanceCheckListReference> financeCheckList = financeDetail.getFinanceCheckList();
		if (financeCheckList != null && !financeCheckList.isEmpty()) {
			auditDetail = getCheckListDetailService().getAuditDetail(financeDetail.getAuditDetailMap(), financeDetail, auditTranType, method);
			auditDetails.addAll(auditDetail);
		}
		
		return auditDetails;
	}
	/**
	 * Method For Screen Level Validations
	 * These Validations Are Moved From DialogCtrl  
	 * 
	 * @param auditHeader
	 * @param usrLanguage
	 * @return
	 */
	@Override
	public ErrorDetails treasuryFinHeaderDialogValidations(InvestmentFinHeader investmentFinHeader , String usrLanguage){
		logger.debug("Entering");

		if (investmentFinHeader.getMaturityDate().before(investmentFinHeader.getStartDate())) {
			return	ErrorUtil.getErrorDetail(new ErrorDetails("StartDate","S0015", 
					new String[] {Labels.getLabel("label_TreasuaryFinHeaderDialog_MaturityDate.value"),
					Labels.getLabel("label_TreasuaryFinHeaderDialog_StartDate.value")},
					new String[] {}), usrLanguage);
		}

		return null;

	}


	/**
	 * Method For Screen Level Validations
	 * These Validations Are Moved From DialogCtrl  
	 * 
	 * @param auditHeader
	 * @param usrLanguage
	 * @return
	 */
	@Override
	public ErrorDetails investmentDealValidations(FinanceDetail aFinanceDetail, InvestmentFinHeader investmentFinHeader , String usrLanguage){
		logger.debug("Entering");

		FinanceMain financeMain = aFinanceDetail.getFinScheduleData().getFinanceMain();


/*		// Validation To Fill Atleast One Item In Document List	
		if(aFinanceDetail.getDocumentDetailsList() == null || 
				aFinanceDetail.getDocumentDetailsList().isEmpty()){
			return	ErrorUtil.getErrorDetail(new ErrorDetails("DocumentDetailsList","S0017", 
					new String[] {Labels.getLabel("window_DocumentDetailDialog.title")},
					new String[] {}),usrLanguage);	
		}

		// Validation To Fill Atleast One Item In Commodity List	
		if(aFinanceDetail.getCommidityLoanDetails() == null || 
				aFinanceDetail.getCommidityLoanDetails().isEmpty()){
			return	ErrorUtil.getErrorDetail(new ErrorDetails("CommidityList","S0017", 
					new String[] {Labels.getLabel("panel_CommidityLoanDetailDialog_BasicDetails.title")},
					new String[] {}),usrLanguage);	
		}*/
		
		
		//TODO the below calculations are not correct.Once verify it	

		BigDecimal availbleAmount = BigDecimal.ZERO; 
		//BigDecimal totInvstd = BigDecimal.ZERO; 
/*			List<FinanceDetail> investmentDealList = investmentFinHeader.getFinanceDetailsList();
		if(investmentDealList != null && !investmentDealList.isEmpty()){
			for (FinanceDetail financeDetailList : investmentDealList) {
				FinanceMain finMain = financeDetailList.getFinScheduleData().getFinanceMain(); 
				if(financeMain.getCustID() != finMain.getCustID()){
					totInvstd = availbleAmount.add(PennantApplicationUtil.unFormateAmount(finMain.getFinAmount(), investmentFinHeader.getLovDescFinFormatter()));
				}
				availbleAmount = investmentFinHeader.getTotPrincipalAmt().subtract(totInvstd);
				totInvstd.add(financeMain.getFinAmount());
			}
		}
		
		if (totInvstd.compareTo(investmentFinHeader.getTotPrincipalAmt()) > 0) {
			return	ErrorUtil.getErrorDetail(new ErrorDetails("FinAmount","E0008", 
					new String[] {availbleAmount.toString()},
					new String[] {Labels.getLabel("label_InvestmentDealDialog_PrincipalAmt.value"),
					Labels.getLabel("label_InvestmentDealDialog_TotPrncpl.value")},
					new String[] {}),usrLanguage);	

		}*/
		
		availbleAmount = availbleAmount.add(financeMain.getFinAmount());
		if(investmentFinHeader.getTotPrincipalAmt() != null && financeMain.getFinAmount() != null){
			if (availbleAmount.compareTo(investmentFinHeader.getTotPrincipalAmt()) > 0) {
				return	ErrorUtil.getErrorDetail(new ErrorDetails("FinAmount","S0012", 
						new String[] {Labels.getLabel("label_InvestmentDealDialog_PrincipalAmt.value"),
						Labels.getLabel("label_InvestmentDealDialog_TotPrncpl.value")},
						new String[] {}),usrLanguage);	

			}}
		
		
		return null;

	}

	@Override
	public void setDocumentDetails(FinanceDetail financeDetail) {
		String finReference = financeDetail.getFinScheduleData().getFinanceMain().getFinReference();
		financeDetail.setDocumentDetailsList(getDocumentDetailsDAO().getDocumentDetailsByRef(finReference, "_View"));
	}
	
	@Override
	public void setFeeCharges(FinanceDetail financeDetail, String type) {
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String finType = financeMain.getFinType();
		financeDetail.getFinScheduleData().setFeeRules(getPostingsDAO().getFeeChargesByFinRef(finType, false, type));
	}

	public void listDocDeletion(FinanceDetail custDetails, String tableType) {
		getDocumentDetailsDAO().deleteList(
				new ArrayList<DocumentDetails>(custDetails.getDocumentDetailsList()), tableType);
	}


	public FinanceDetail getFinanceDetailById(FinanceDetail financeDetail, String finReference) {
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		String finType = financeMain.getFinType();
		String eventCode = "";
		
		FinanceType financeType = finScheduleData.getFinanceType();

		if(financeType == null && finType != null) {
			financeType =  getFinanceTypeDAO().getFinanceTypeByID(finType, "_AView");
			financeDetail.getFinScheduleData().setFinanceType(financeType);
		}
		
		FinanceMain financeMain1;
		
		financeMain1 = getFinanceMainDAO().getFinanceMainById(financeMain.getFinReference(), "_View", false);
		
		if(financeMain1 != null) {
			BeanUtils.copyProperties(financeMain1, financeMain);
		} 
		
		finScheduleData.setFinReference(financeMain.getFinReference());
		finScheduleData.setFinanceMain(financeMain);
		
		Date curBussDate = (Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");
		
		if (financeMain.getFinStartDate() != null && financeMain.getFinStartDate().after(curBussDate)) {
			eventCode = "ADDDBSF";
		} else {
			eventCode = "ADDDBSP";
		}
	
		
		String accSetId = returnAccountingSetid(eventCode, financeType);

		//Finance Fee Charge Details				
		financeDetail.setFeeCharges(getTransactionEntryDAO().getListFeeChargeRules(Long.valueOf(accSetId), eventCode.startsWith("ADDDBS") ?  "ADDDBS" : eventCode, "_AView", 0));
		
		//Finance Overdue Penalty Rate Details
		financeDetail.getFinScheduleData().setFinODPenaltyRate(getFinODPenaltyRateDAO().getFinODPenaltyRateByRef(finReference, "_View"));
		//Finance Schedule Details List
		financeDetail.getFinScheduleData().setFinanceScheduleDetails(getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, "_Temp", false));
		//Finance Disbursement Details
		financeDetail.getFinScheduleData().setDisbursementDetails(getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference, "_Temp", false));

		return financeDetail;
	}
	

	public enum TSR_TABS {
		COMIDITY,
		DOCUMENTS,
		CHECKLIST, 
		AGGREMENTS, 
		FEE, 
		ACCOUNTING,
		DEFAULT
	}
	
	@Override
	public void setFinanceDetails(FinanceDetail financeDetail, String strTab, String userRole) {
		logger.debug("Entering ");
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = scheduleData.getFinanceMain();
		String finType = financeMain.getFinType();
		
		CommidityLoanHeader commidityLoanHeader = financeDetail.getCommidityLoanHeader();
		List<DocumentDetails> documentDetailsList = financeDetail.getDocumentDetailsList();
		List<FinanceCheckListReference> financeCheckList = financeDetail.getFinanceCheckList();
		List<FinanceReferenceDetail> aggrementList = financeDetail.getAggrementList();
		List<TransactionEntry> transactionEntries = financeDetail.getTransactionEntries();
		List<FeeRule> feeRules = scheduleData.getFeeRules();
		
		
		TSR_TABS tab = TSR_TABS.valueOf(strTab.toUpperCase());

		switch(tab) {
		case COMIDITY:
			if(commidityLoanHeader == null) {
				commidityLoanHeader = getCommidityLoanDetailService().getCommidityLoanHeaderById(financeMain.getFinReference());
				if(commidityLoanHeader != null) {
					financeDetail.setCommidityLoanHeader(commidityLoanHeader);
					financeDetail.setCommidityLoanDetails(commidityLoanHeader.getCommidityLoanDetails());
				}
			}
			break;
		case DOCUMENTS:
			if(documentDetailsList == null || documentDetailsList.isEmpty()) {
				setDocumentDetails(financeDetail);
			}
			break;

		case CHECKLIST:
			if(financeCheckList == null || financeCheckList.isEmpty()) {
				getCheckListDetailService().setFinanceCheckListDetails(financeDetail, finType, userRole);
			}
			break;

		case AGGREMENTS:
			if(aggrementList == null || aggrementList.isEmpty()) {
				financeDetail.setAggrementList(getAgreementDetailService().getAggrementDetailList(finType, userRole));
			}
			break;

		case FEE:
			if(feeRules == null || feeRules.isEmpty()) {
				setFeeCharges(financeDetail, "_View");
			}
		case ACCOUNTING:
			if(transactionEntries == null || transactionEntries.isEmpty()) {
				transactionEntries = getAccountingDetails(financeDetail);
				financeDetail.setTransactionEntries(transactionEntries);
			}
			break;
		default :
			if(commidityLoanHeader == null) {
				commidityLoanHeader = getCommidityLoanDetailService().getCommidityLoanHeaderById(financeMain.getFinReference());
				if(commidityLoanHeader != null) {
					financeDetail.setCommidityLoanHeader(commidityLoanHeader);
					financeDetail.setCommidityLoanDetails(commidityLoanHeader.getCommidityLoanDetails());
				}
			}
			if(documentDetailsList == null || documentDetailsList.isEmpty()) {
				setDocumentDetails(financeDetail);
			}
			if(financeCheckList == null || financeCheckList.isEmpty()) {
				getCheckListDetailService().setFinanceCheckListDetails(financeDetail, finType, userRole);
			}
			if(aggrementList == null || aggrementList.isEmpty()) {
				financeDetail.setAggrementList(getAgreementDetailService().getAggrementDetailList(finType, userRole));
			}
			if(feeRules == null || feeRules.isEmpty()) {
				setFeeCharges(financeDetail, "_View");
			}
			
			if(transactionEntries == null || transactionEntries.isEmpty()) {
				transactionEntries = getAccountingDetails(financeDetail);
				financeDetail.setTransactionEntries(transactionEntries);
			}
		}
		logger.debug("Leaving ");
	}

	private List<TransactionEntry> getAccountingDetails(FinanceDetail financeDetail) {
		logger.debug("Entering ");
		List<TransactionEntry> transactionEntries = financeDetail.getTransactionEntries();
		
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = scheduleData.getFinanceMain();

		String eventCode = "";
		if (StringUtils.trimToEmpty(eventCode).equals("")) {
			Date curBussDate = (Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");

			if (financeMain.getFinStartDate() != null && financeMain.getFinStartDate().after(curBussDate)) {
				eventCode = "ADDDBSF";
			} else {
				eventCode = "ADDDBSP";
			}
		}
		String accSetId = returnAccountingSetid(eventCode, scheduleData.getFinanceType());

		transactionEntries = getTransactionEntryDAO().getListTransactionEntryById(Long.valueOf(accSetId), "_AEView", true);
		
		logger.debug("Leaving "); 
		
		return transactionEntries;
	}
	
	
	
	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param detail
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	public List<AuditDetail> setDocumentDetailsAuditData(FinanceDetail detail, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();		
		DocumentDetails object = new DocumentDetails();
		String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());

		for (int i = 0; i < detail.getDocumentDetailsList().size(); i++) {
			DocumentDetails documentDetails = detail.getDocumentDetailsList().get(i);
 				documentDetails.setWorkflowId(detail.getWorkflowId());
				boolean isRcdType = false;

				if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					documentDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					isRcdType = true;
				} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					documentDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					isRcdType = true;
				} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					documentDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					isRcdType = true;
				}

				if (method.equals("saveOrUpdate") && (isRcdType == true)) {
					documentDetails.setNewRecord(true);
				}

				if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
					if (documentDetails.getRecordType().equalsIgnoreCase(
							PennantConstants.RECORD_TYPE_NEW)) {
						auditTranType = PennantConstants.TRAN_ADD;
					} else if (documentDetails.getRecordType().equalsIgnoreCase(
							PennantConstants.RECORD_TYPE_DEL)
							|| documentDetails.getRecordType().equalsIgnoreCase(
									PennantConstants.RECORD_TYPE_CAN)) {
						auditTranType = PennantConstants.TRAN_DEL;
					} else {
						auditTranType = PennantConstants.TRAN_UPD;
					}
				}

				documentDetails.setRecordStatus(detail.getFinScheduleData().getFinanceMain()
						.getRecordStatus());
				documentDetails.setUserDetails(detail.getFinScheduleData().getFinanceMain()
						.getUserDetails());
				documentDetails.setLastMntOn(detail.getFinScheduleData().getFinanceMain()
						.getLastMntOn());

				if (!documentDetails.getRecordType().equals("")) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							documentDetails.getBefImage(), documentDetails));
				}
			}
 		logger.debug("Leaving");
		return auditDetails;
	}
	
	
	/**
	 * Get AccountingSet Id based on event code.<br>
	 * 
	 * @param eventCode
	 * @param financeType
	 * @return
	 */
	private String returnAccountingSetid(String eventCode, FinanceType financeType) {
		logger.debug("Entering ");

		// Execute entries depend on Finance Event
		String accountingSetId = "";
		if (eventCode.equals("ADDDBSF")) {
			accountingSetId = financeType.getFinAEAddDsbFD();
		} else if (eventCode.equals("ADDDBSN")) {
			accountingSetId = financeType.getFinAEAddDsbFDA();
		} else if (eventCode.equals("ADDDBSP")) {
			accountingSetId = financeType.getFinAEAddDsbOD();
		} else if (eventCode.equals("AMZ")) {
			accountingSetId = financeType.getFinAEAmzNorm();
		} else if (eventCode.equals("AMZSUSP")) {
			accountingSetId = financeType.getFinAEAmzSusp();
		} else if (eventCode.equals("DEFRPY")) {
			accountingSetId = financeType.getFinDefRepay();
		} else if (eventCode.equals("DEFFRQ")) {
			accountingSetId = financeType.getFinDeffreq();
		} else if (eventCode.equals("EARLYPAY")) {
			accountingSetId = financeType.getFinAEEarlyPay();
		} else if (eventCode.equals("EARLYSTL")) {
			accountingSetId = financeType.getFinAEEarlySettle();
		} else if (eventCode.equals("LATEPAY")) {
			accountingSetId = financeType.getFinLatePayRule();
		} else if (eventCode.equals("M_AMZ")) {
			accountingSetId = financeType.getFinToAmz();
		} else if (eventCode.equals("M_NONAMZ")) {
			accountingSetId = financeType.getFinAEToNoAmz();
		} else if (eventCode.equals("RATCHG")) {
			accountingSetId = financeType.getFinAERateChg();
		} else if (eventCode.equals("REPAY")) {
			accountingSetId = financeType.getFinAERepay();
		} else if (eventCode.equals("WRITEOFF")) {
			accountingSetId = financeType.getFinAEWriteOff();
		} else if (eventCode.equals("SCDCHG")) {
			accountingSetId = financeType.getFinSchdChange();
		}

		logger.debug("Leaving");
		return accountingSetId;
	}
	
	

	public InvestmentFinHeader getTreasuaryFinHeader(String finReference, String tableType) {
		return getTreasuaryFinHeaderDAO().getTreasuaryFinHeader(finReference, tableType);
	}
	
	
	
	
	/**
	 * businessValidation method do the following steps.
	 * 1)	get the details from the auditHeader. 
	 * 2)	fetch the details from the tables
	 * 3)	Validate the Record based on the record details. 
	 * 4) 	Validate for any business validation.
	 * 5)	for any mismatch conditions Fetch the error details from getFinanceMainDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */


	private AuditHeader dealBusinessValidation(AuditHeader auditHeader, String method){
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		AuditDetail auditDetail = dealValidation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getDealAuditDetails(auditHeader, method);
		
		FinanceDetail financeDetail = (FinanceDetail)auditHeader.getAuditDetail().getModelData();
		
		 List<CommidityLoanDetail>  commidityLoanDetails = financeDetail.getCommidityLoanDetails();
			if (commidityLoanDetails != null && !commidityLoanDetails.isEmpty()) {
				auditDetails.addAll(getCommidityLoanDetailService().validate(commidityLoanDetails, financeDetail.getFinScheduleData().getFinanceMain().getWorkflowId(), method, auditHeader.getAuditTranType(), auditHeader.getUsrLanguage()));
			}
		
		if (financeDetail.getFinanceCheckList() != null && !financeDetail.getFinanceCheckList().isEmpty()) {
			auditDetails.addAll(getCheckListDetailService().validate(financeDetail, method, auditHeader.getUsrLanguage()));
		}
		
		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}
		
		auditHeader=nextProcess(auditHeader);
		
		logger.debug("Leaving");
		return auditHeader;
	}
	
	/**
	 * Validation
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @param isWIF
	 * @return
	 */
	private AuditDetail dealValidation(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
		FinanceDetail financeDetail = (FinanceDetail) auditDetail.getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		FinanceMain tempFinanceMain= null;
		if (financeMain.isWorkflow()){
			tempFinanceMain = getFinanceMainDAO().getFinanceMainById(financeMain.getId(), "_Temp", false);
		}
		FinanceMain befFinanceMain= getFinanceMainDAO().getFinanceMainById(financeMain.getId(), "", false);

		FinanceMain old_FinanceMain= financeMain.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=financeMain.getId();
		errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

		if (financeMain.isNew()){ // for New record or new record into work flow

			if (!financeMain.isWorkflow()){// With out Work flow only new records  
				if (befFinanceMain !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befFinanceMain !=null || tempFinanceMain!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befFinanceMain ==null || tempFinanceMain!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!financeMain.isWorkflow()){	// With out Work flow for update and delete

				if (befFinanceMain ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (old_FinanceMain!=null && !old_FinanceMain.getLastMntOn().equals(befFinanceMain.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempFinanceMain==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (old_FinanceMain!=null && !old_FinanceMain.getLastMntOn().equals(tempFinanceMain.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if(StringUtils.trimToEmpty(method).equals("doApprove") || !financeMain.isWorkflow()){
			financeMain.setBefImage(befFinanceMain);	
		}

		return auditDetail;
	}

	/**
	 * Common Method for Retrieving AuditDetails List
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private AuditHeader getDealAuditDetails(AuditHeader auditHeader,String method ){
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FinanceDetail  financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain finMain =  financeDetail.getFinScheduleData().getFinanceMain();

		String auditTranType="";

		if(method.equals("saveOrUpdate") || method.equals("doApprove") || method.equals("doReject") ){
			if (finMain.isWorkflow()) {
				auditTranType= PennantConstants.TRAN_WF;
			}
		}

		auditDetails.addAll(getAuditDetails(financeDetail, method, auditTranType));

		auditHeader.getAuditDetail().setModelData(financeDetail);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug("Leaving ");
		return auditHeader;
	}
	
	
	
	// Save schedule details
	public void saveScheduleDetails(FinanceDetail financeDetail, String tableType, boolean isWIF) {
		logger.debug("Entering ");
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		
		if (!financeDetail.isNewRecord()) {
			//TODO-- Check it is needed or not
			if(tableType.equals("") && financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
				
				Date curBDay = (Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");
				
				//Fetch Existing data before Modification
				FinScheduleData old_finSchdData = getFinSchDataByFinRef(financeDetail.getFinScheduleData().getFinReference(), "", -1);
				old_finSchdData.setFinReference(financeDetail.getFinScheduleData().getFinReference());

				//Create log entry for Action for Schedule Modification
				FinLogEntryDetail entryDetail = new FinLogEntryDetail();
				entryDetail.setFinReference(financeDetail.getFinScheduleData().getFinReference());
				entryDetail.setEventAction(financeDetail.getAccountingEventCode());
				entryDetail.setSchdlRecal(financeDetail.getFinScheduleData().getFinanceMain().isScheduleRegenerated());
				entryDetail.setPostDate(curBDay);
				entryDetail.setReversalCompleted(false);
				long logKey = getFinLogEntryDetailDAO().save(entryDetail);

				//Save Schedule Details For Future Modifications
				listSave(old_finSchdData, "_Log", isWIF, logKey);
			}

			listDeletion(financeDetail.getFinScheduleData(), tableType, isWIF);
			listSave(financeDetail.getFinScheduleData(), tableType, isWIF, 0);
			saveFeeChargeList(financeDetail.getFinScheduleData(), isWIF, tableType);
		} else {
			listSave(financeDetail.getFinScheduleData(), tableType, isWIF, 0);
			saveFeeChargeList(financeDetail.getFinScheduleData(), isWIF, tableType);
		}
		
		logger.debug("Leaving ");
	}
	
	@Override
	public String getCustStatusByMinDueDays() {
		CustomerStatusCode customerStatusCode = getCustomerStatusCodeDAO()
		.getCustStatusByMinDueDays("");
		if (customerStatusCode != null) {
			return customerStatusCode.getCustStsCode();
		}
		return "";
	}
	
	}