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

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.finance.TreasuaryFinHeaderDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.applicationmaster.CustomerStatusCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.InvestmentFinHeader;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.service.finance.GenericFinanceDetailService;
import com.pennant.backend.service.finance.TreasuaryFinanceService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>TreasuaryFinance</b>.<br>
 * 
 */
public class TreasuaryFinanceServiceImpl extends GenericFinanceDetailService implements TreasuaryFinanceService {
	private static final Logger logger = Logger.getLogger(TreasuaryFinanceServiceImpl.class);
	
	private TreasuaryFinHeaderDAO treasuaryFinHeaderDAO;
	private FinanceReferenceDetailDAO financeReferenceDetailDAO;

	public TreasuaryFinanceServiceImpl() {
		super();
	}
	
	public TreasuaryFinHeaderDAO getTreasuaryFinHeaderDAO() {
		return treasuaryFinHeaderDAO;
	}
	public void setTreasuaryFinHeaderDAO(TreasuaryFinHeaderDAO treasuaryFinHeaderDAO) {
		this.treasuaryFinHeaderDAO = treasuaryFinHeaderDAO;
	}

	public FinanceReferenceDetailDAO getFinanceReferenceDetailDAO() {
	    return financeReferenceDetailDAO;
    }
	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
	    this.financeReferenceDetailDAO = financeReferenceDetailDAO;
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
			tableType="_Temp";
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
			saveScheduleDetails(financeDetail, "_Temp", false);
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

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		List<FinanceDetail> finDetailList = new ArrayList<FinanceDetail>();
		finDetailList.add(financeDetail);

		TableType tableType = TableType.MAIN_TAB;
		if (financeMain.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (financeDetail.isNew()) {
			getFinanceMainDAO().save(financeMain, tableType, false);
		}else{
			getFinanceMainDAO().update(financeMain, tableType, false);
		}
		
		saveScheduleDetails(financeDetail, "_Temp", false);

		auditDetails.addAll(saveOrUpdateDetails(finDetailList, tableType.getSuffix(), auditHeader.getAuditTranType()));

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());
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
				auditDetail = processingDocumentDetailsList(details, tableType, financeMain,FinanceConstants.FINSER_EVENT_ORG);
				auditDetails.addAll(auditDetail);

			}
			// set Finance Check List audit details to auditDetails
			if (finDetail.getFinanceCheckList() != null && !finDetail.getFinanceCheckList().isEmpty()) {
				auditDetail = getCheckListDetailService().saveOrUpdate(finDetail, tableType);
				auditDetails.addAll(auditDetail);
			}

		}

		return auditDetails;
	}
	
	
	private List<AuditDetail> doApproveFinanceDetails(FinanceDetail financeDetail,  String tableType, String tranType) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		
		List<AuditDetail> auditDetail;

		List<AuditDetail> details = financeDetail.getAuditDetailMap().get("DocumentDetails");
		if(details != null && !details.isEmpty()) {
			auditDetail = processingDocumentDetailsList(details, tableType,financeMain,FinanceConstants.FINSER_EVENT_ORG);
			auditDetails.addAll(auditDetail);

		}
		// set Finance Check List audit details to auditDetails
		if (financeDetail.getFinanceCheckList() != null && !financeDetail.getFinanceCheckList().isEmpty()) {
			auditDetail = getCheckListDetailService().doApprove(financeDetail, tableType);
			getCheckListDetailService().delete(financeDetail, "_Temp", tranType);
			auditDetails.addAll(auditDetail);
		}

		
		saveFeeChargeList(financeDetail.getFinScheduleData(), financeDetail.getModuleDefiner(),false, tableType);
		getFinFeeChargesDAO().deleteChargesBatch(financeMain.getFinReference(),financeDetail.getModuleDefiner(), false, "_Temp");

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
			getFinanceMainDAO().delete(financeDetail.getFinScheduleData().getFinanceMain(),TableType.TEMP_TAB, false, true);
			listDeletion(financeDetail.getFinScheduleData(),financeDetail.getModuleDefiner(), "_Temp", false);
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
	public AuditHeader doApprove(AuditHeader auditHeader) throws InterfaceException{
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
		
		Date curBDay = DateUtility.getAppDate();
		financeMain.setFinApprovedDate(curBDay);
		
		auditHeader = executeAccountingProcess(auditHeader, curBDay);
		if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
			return auditHeader;
		}
		
		if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;
			getFinanceMainDAO().delete(financeMain, TableType.MAIN_TAB, false, true);

		} else {
			financeMain.setRoleCode("");
			financeMain.setNextRoleCode("");
			financeMain.setTaskId("");
			financeMain.setNextTaskId("");
			financeMain.setWorkflowId(0);

			if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {	
				tranType = PennantConstants.TRAN_ADD;
				financeMain.setRecordType("");
				getFinanceMainDAO().save(financeMain, TableType.MAIN_TAB, false);
			} else {
				tranType=PennantConstants.TRAN_UPD;
				financeMain.setRecordType("");
				getFinanceMainDAO().update(financeMain, TableType.MAIN_TAB, false);
			}
			
			saveScheduleDetails(financeDetail, "", false);
		}
		
		auditDetails.addAll(doApproveFinanceDetails(financeDetail, "", tranType));
		
		getFinanceMainDAO().delete(financeMain,TableType.TEMP_TAB, false, true);
		listDeletion(financeDetail.getFinScheduleData(), financeDetail.getModuleDefiner(), "_Temp", false);
		
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());
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

		if (aInvFinHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
			tranType=PennantConstants.TRAN_ADD;
			aInvFinHeader.setRecordType("");
			getTreasuaryFinHeaderDAO().save(aInvFinHeader, "");
		} else {
			tranType=PennantConstants.TRAN_UPD;
			aInvFinHeader.setRecordType("");
			getTreasuaryFinHeaderDAO().update(aInvFinHeader,"");
		}


		getTreasuaryFinHeaderDAO().delete(aInvFinHeader,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(aInvFinHeader);

		String tableType = "_Temp";
		if (finDetailList != null && !finDetailList.isEmpty()) {
			List<AuditDetail> details = header.getAuditDetailMap().get("FinanceMain");
			List<AuditDetail> dealAuditDetails = processingDeals(details, header,  tableType);
			auditDetails.addAll(dealAuditDetails);			
		}
		
		for (FinanceDetail financeDetail : finDetailList) {
			saveScheduleDetails(financeDetail, "_Temp", false);
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
			getFinanceMainDAO().delete(financeDetail.getFinScheduleData().getFinanceMain(),TableType.TEMP_TAB, false, false);
			listDeletion(financeDetail.getFinScheduleData(), financeDetail.getModuleDefiner(), "_Temp", false);
		}
	
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getTreasuaryFinHeaderDAO().delete(treasuaryFinance,"_Temp");

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
		String usrLanguage = finHeader.getUserDetails().getLanguage();


		List<FinanceDetail> dealList =  finHeader.getFinanceDetailsList();

		for (FinanceDetail financeDetail : dealList) {
			if (financeDetail.getFinanceCheckList() != null && !financeDetail.getFinanceCheckList().isEmpty()) {
				List<AuditDetail> details;
				details = getCheckListDetailService().validate(financeDetail.getAuditDetailMap().get("checkListDetails"), method, usrLanguage);
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
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());	
		
	/*	if(auditDetail.getModelData() instanceof InvestmentFinHeader) {
			return auditDetail;
		}*/
		
		InvestmentFinHeader treasuaryFinance = (InvestmentFinHeader) auditDetail.getModelData();

		InvestmentFinHeader tempTreasuaryFinance= null;

		if (treasuaryFinance.isWorkflow()){
			tempTreasuaryFinance = getTreasuaryFinHeaderDAO().getTreasuaryFinHeaderById(treasuaryFinance.getId(), "_Temp");
		}

		InvestmentFinHeader befTreasuaryFinance= getTreasuaryFinHeaderDAO().getTreasuaryFinHeaderById(treasuaryFinance.getId(), "");

		InvestmentFinHeader oldTreasuaryFinance= treasuaryFinance.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=treasuaryFinance.getId();
		errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

		if (treasuaryFinance.isNew()){ // for New record or new record into work flow

			if (!treasuaryFinance.isWorkflow()){// With out Work flow only new records  
				if (befTreasuaryFinance !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (treasuaryFinance.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befTreasuaryFinance !=null || tempTreasuaryFinance!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befTreasuaryFinance ==null || tempTreasuaryFinance!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!treasuaryFinance.isWorkflow()){	// With out Work flow for update and delete

				if (befTreasuaryFinance ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldTreasuaryFinance!=null && !oldTreasuaryFinance.getLastMntOn().equals(befTreasuaryFinance.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempTreasuaryFinance==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
				
				if (oldTreasuaryFinance!=null && !oldTreasuaryFinance.getLastMntOn().equals(tempTreasuaryFinance.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !treasuaryFinance.isWorkflow()){
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

		FinanceMain finMain = new FinanceMain();
		String[] fields = PennantJavaUtil.getFieldDetails(finMain, finMain.getExcludeFields());

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

			if (("doConfirm".equals(method) || "saveOrUpdate".equals(method)) && (isRcdType)) {
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

			if (StringUtils.isNotEmpty(financeMain.getRecordType())) {
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

			if (StringUtils.isEmpty(type)) {
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
				getFinanceMainDAO().save(financeMain, TableType.valueOf(type), false);
			}

			if (updateRecord) {
				getFinanceMainDAO().update(financeMain, TableType.valueOf(type), false);
			}

			if (deleteRecord) {
				getFinanceMainDAO().delete(financeMain, TableType.valueOf(type), false, true);
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

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method) || "doConfirm".equals(method)) {
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

		for (AuditDetail auditDetail : auditDetails) {			
			if(auditDetail.getModelData() instanceof DocumentDetails) {
				auditDetail.setAuditSeq(docAuditSeq);
				docAuditSeq++;
			}

			if(auditDetail.getModelData() instanceof FinanceCheckListReference) {
				auditDetail.setAuditSeq(checkListAuditSeq);
				checkListAuditSeq++;
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
	public ErrorDetail treasuryFinHeaderDialogValidations(InvestmentFinHeader investmentFinHeader , String usrLanguage){
		logger.debug("Entering");

		if (investmentFinHeader.getMaturityDate().before(investmentFinHeader.getStartDate())) {
			return	ErrorUtil.getErrorDetail(new ErrorDetail("StartDate","S0015", 
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
	public ErrorDetail investmentDealValidations(FinanceDetail aFinanceDetail, InvestmentFinHeader investmentFinHeader , String usrLanguage){
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
			return	errorList.add(new ErrorDetails(Labels.getLabel("label_FinAmount"), "30508", new String[] {
							Labels.getLabel("label_FinAmount"),
							PennantAppUtil.amountFormate(getFinanceDetail().getFinScheduleData().getFinanceType()
									.getFinMaxAmount(), getFinanceDetail().getFinScheduleData().getFinanceMain()
									.getLovDescFinFormatter()) }, new String[] {}));	

		}*/
		
		availbleAmount = availbleAmount.add(financeMain.getFinAmount());
		if(investmentFinHeader.getTotPrincipalAmt() != null && financeMain.getFinAmount() != null){
			if (availbleAmount.compareTo(investmentFinHeader.getTotPrincipalAmt()) > 0) {
				return	ErrorUtil.getErrorDetail(new ErrorDetail("FinAmount","30568", 
						new String[] {Labels.getLabel("label_InvestmentDealDialog_PrincipalAmt.value"),
						Labels.getLabel("label_InvestmentDealDialog_TotPrncpl.value")},
						new String[] {}),usrLanguage);	

			}
		}
		
		return null;
	}

	@Override
	public void setDocumentDetails(FinanceDetail financeDetail) {
		String finReference = financeDetail.getFinScheduleData().getFinanceMain().getFinReference();
		financeDetail.setDocumentDetailsList(getDocumentDetailsDAO().getDocumentDetailsByRef(finReference,
				FinanceConstants.MODULE_NAME, FinanceConstants.FINSER_EVENT_ORG, "_View"));
	}
	
	@Override
	public void setFeeCharges(FinanceDetail financeDetail, String type) {
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String finType = financeMain.getFinType();
		financeDetail.getFinScheduleData().setFeeRules(getFinFeeChargesDAO().getFeeChargesByFinRef(finType,financeDetail.getModuleDefiner(), false,"_TView"));
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
		
		eventCode = PennantApplicationUtil.getEventCode(financeMain.getFinStartDate());
		String promotionCode = financeMain.getPromotionCode();

		Long accSetId;
		if (StringUtils.isNotBlank(promotionCode)) {
			accSetId = getFinTypeAccountingDAO().getAccountSetID(promotionCode, eventCode,
					FinanceConstants.MODULEID_PROMOTION);
		} else {
			accSetId = getFinTypeAccountingDAO().getAccountSetID(financeType.getFinType(), eventCode,
					FinanceConstants.MODULEID_FINTYPE);
		}
		
		//Fetch Stage Accounting AccountingSetId List 
		List<Long> accSetIdList = new ArrayList<Long>();
		if(eventCode.startsWith(AccountEventConstants.ACCEVENT_ADDDBS)){
			accSetIdList.addAll(getFinanceReferenceDetailDAO().getRefIdListByFinType(finType,FinanceConstants.FINSER_EVENT_ORG, null, "_ACView"));
		}
		accSetIdList.add(accSetId);

		//Finance Fee Charge Details				
		if(!accSetIdList.isEmpty()){
			financeDetail.setFeeCharges(getTransactionEntryDAO().getListFeeChargeRules(accSetIdList,
					eventCode.startsWith(AccountEventConstants.ACCEVENT_ADDDBS) ? AccountEventConstants.ACCEVENT_ADDDBS : eventCode, "_AView", 0));
		}
		
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
		
		List<DocumentDetails> documentDetailsList = financeDetail.getDocumentDetailsList();
		List<FinanceCheckListReference> financeCheckList = financeDetail.getFinanceCheckList();
		List<FinanceReferenceDetail> aggrementList = financeDetail.getAggrementList();
		List<FeeRule> feeRules = scheduleData.getFeeRules();
		
		
		TSR_TABS tab = TSR_TABS.valueOf(strTab.toUpperCase());

		switch(tab) {
		case DOCUMENTS:
			if(documentDetailsList == null || documentDetailsList.isEmpty()) {
				setDocumentDetails(financeDetail);
			}
			break;

		case CHECKLIST:
			if(financeCheckList == null || financeCheckList.isEmpty()) {
				getCheckListDetailService().setFinanceCheckListDetails(financeDetail, finType,FinanceConstants.FINSER_EVENT_ORG, userRole);
			}
			break;

		case AGGREMENTS:
			if(aggrementList == null || aggrementList.isEmpty()) {
				financeDetail.setAggrementList(getAgreementDetailService().getAggrementDetailList(finType, 
						FinanceConstants.FINSER_EVENT_ORG,userRole));
			}
			break;

		case FEE:
			if(feeRules == null || feeRules.isEmpty()) {
				setFeeCharges(financeDetail, "_View");
			}
		case ACCOUNTING:
			break;
		default :
			if(documentDetailsList == null || documentDetailsList.isEmpty()) {
				setDocumentDetails(financeDetail);
			}
			if(financeCheckList == null || financeCheckList.isEmpty()) {
				getCheckListDetailService().setFinanceCheckListDetails(financeDetail, finType, FinanceConstants.FINSER_EVENT_ORG,userRole);
			}
			if(aggrementList == null || aggrementList.isEmpty()) {
				financeDetail.setAggrementList(getAgreementDetailService().getAggrementDetailList(finType,FinanceConstants.FINSER_EVENT_ORG, userRole));
			}
			if(feeRules == null || feeRules.isEmpty()) {
				setFeeCharges(financeDetail, "_View");
			}
		}
		logger.debug("Leaving ");
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
 				documentDetails.setWorkflowId(detail.getFinScheduleData().getFinanceMain().getWorkflowId());
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

				if ("saveOrUpdate".equals(method) && (isRcdType)) {
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

				if (StringUtils.isNotEmpty(documentDetails.getRecordType())) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							documentDetails.getBefImage(), documentDetails));
				}
			}
 		logger.debug("Leaving");
		return auditDetails;
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
		
		if (financeDetail.getFinanceCheckList() != null && !financeDetail.getFinanceCheckList().isEmpty()) {
			auditDetails.addAll(getCheckListDetailService().validate(financeDetail.getAuditDetailMap().get("checkListDetails"), method, auditHeader.getUsrLanguage()));
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
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
		FinanceDetail financeDetail = (FinanceDetail) auditDetail.getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		FinanceMain tempFinanceMain= null;
		if (financeMain.isWorkflow()){
			tempFinanceMain = getFinanceMainDAO().getFinanceMainById(financeMain.getId(), "_Temp", false);
		}
		FinanceMain befFinanceMain= getFinanceMainDAO().getFinanceMainById(financeMain.getId(), "", false);

		FinanceMain oldFinanceMain= financeMain.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=financeMain.getId();
		errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

		if (financeMain.isNew()){ // for New record or new record into work flow

			if (!financeMain.isWorkflow()){// With out Work flow only new records  
				if (befFinanceMain !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befFinanceMain !=null || tempFinanceMain!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befFinanceMain ==null || tempFinanceMain!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!financeMain.isWorkflow()){	// With out Work flow for update and delete

				if (befFinanceMain ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldFinanceMain!=null && !oldFinanceMain.getLastMntOn().equals(befFinanceMain.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempFinanceMain==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (oldFinanceMain!=null && !oldFinanceMain.getLastMntOn().equals(tempFinanceMain.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !financeMain.isWorkflow()){
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

		if("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method) ){
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
			if(StringUtils.isEmpty(tableType) && financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
				
				Date curBDay = DateUtility.getAppDate();
				
				//Fetch Existing data before Modification
				FinScheduleData oldFinSchdData = getFinSchDataByFinRef(financeDetail.getFinScheduleData().getFinReference(), "", -1);
				oldFinSchdData.setFinReference(financeDetail.getFinScheduleData().getFinReference());

				//Create log entry for Action for Schedule Modification
				FinLogEntryDetail entryDetail = new FinLogEntryDetail();
				entryDetail.setFinReference(financeDetail.getFinScheduleData().getFinReference());
				entryDetail.setEventAction(financeDetail.getAccountingEventCode());
				entryDetail.setSchdlRecal(financeDetail.getFinScheduleData().getFinanceMain().isScheduleRegenerated());
				entryDetail.setPostDate(curBDay);
				entryDetail.setReversalCompleted(false);
				long logKey = getFinLogEntryDetailDAO().save(entryDetail);

				//Save Schedule Details For Future Modifications
				listSave(oldFinSchdData, "_Log", isWIF, logKey);
			}

			listDeletion(financeDetail.getFinScheduleData(),financeDetail.getModuleDefiner(), tableType, isWIF);
			listSave(financeDetail.getFinScheduleData(), tableType, isWIF, 0);
			saveFeeChargeList(financeDetail.getFinScheduleData(), financeDetail.getModuleDefiner(),isWIF, tableType);
		} else {
			listSave(financeDetail.getFinScheduleData(), tableType, isWIF, 0);
			saveFeeChargeList(financeDetail.getFinScheduleData(), financeDetail.getModuleDefiner(),isWIF, tableType);
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