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
 * FileName    		:  AcademicServiceImpl.java                                                   * 	  
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
import org.zkoss.zk.ui.WrongValueException;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.BulkProcessDetailsDAO;
import com.pennant.backend.dao.finance.BulkRateChangeProcessDAO;
import com.pennant.backend.dao.finance.DefermentDetailDAO;
import com.pennant.backend.dao.finance.DefermentHeaderDAO;
import com.pennant.backend.dao.finance.FinLogEntryDetailDAO;
import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinancePremiumDetailDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.dao.financemanagement.OverdueChargeRecoveryDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.dao.solutionfactory.ExtendedFieldDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.BulkProcessDetails;
import com.pennant.backend.model.finance.BulkProcessHeader;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ScheduleMapDetails;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.BulkRateChangeProcessService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.rits.cloning.Cloner;

/**
 * Service implementation for methods that depends on <b>BulkProcessHeader</b>.<br>
 * 
 */
public class BulkRateChangeProcessServiceImpl extends GenericService<BulkProcessHeader> implements BulkRateChangeProcessService {

	private static Logger logger = Logger.getLogger(BulkRateChangeProcessServiceImpl.class);
	
	// DAO Classes
	private AuditHeaderDAO auditHeaderDAO;
	private BulkRateChangeProcessDAO bulkRateChangeProcessDAO;
	private BulkProcessDetailsDAO bulkProcessDetailsDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceMainDAO financeMainDAO;
	private FinanceTypeDAO financeTypeDAO;
	private FinanceDisbursementDAO financeDisbursementDAO;
	private DefermentHeaderDAO defermentHeaderDAO;
	private DefermentDetailDAO defermentDetailDAO;
	private RepayInstructionDAO repayInstructionDAO;
	private FinODPenaltyRateDAO finODPenaltyRateDAO;	
	private FinancePremiumDetailDAO financePremiumDetailDAO;
	private PostingsDAO postingsDAO;
	private FinanceProfitDetailDAO profitDetailsDAO;
	private FinLogEntryDetailDAO finLogEntryDetailDAO;
	private ExtendedFieldDetailDAO extendedFieldDetailDAO;
	private FinanceRepaymentsDAO financeRepaymentsDAO;
	private OverdueChargeRecoveryDAO recoveryDAO;

	private LoginUserDetails loginUserDetails;
	
	protected String excludeFields = "calculateRepay,equalRepay,eventFromDate,eventToDate,increaseTerms,"
			+ "allowedDefRpyChange,availedDefRpyChange,allowedDefFrqChange,availedDefFrqChange,recalFromDate,recalToDate,excludeDeferedDates,"
			+ "financeScheduleDetails,disbDate, disbursementDetails,repayInstructions, rateChanges, defermentHeaders,addTermAfter,"
			+ "defermentDetails,scheduleMap,reqTerms,errorDetails,carLoanDetail,educationalLoan,homeLoanDetail,"
			+ "mortgageLoanDetail,proceedDedup,actionSave, finRvwRateApplFor,finGrcRvwRateApplFor,curDisbursementAmt,amount,"
			+ "exception,amountBD,amountUSD,maturity,availCommitAmount,guarantorIDTypeName,curFeeChargeAmt,"
			+ "name,lovCustCIFName,primaryExposure,secondaryExposure,guarantorExposure,worstStatus,status,sumPrimaryDetails,sumSecondaryDetails,"
			+ "sumGurantorDetails, isExtSource, commidityLoanDetails, limitStatus,fundsAvailConfirmed";
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public BulkRateChangeProcessDAO getBulkRateChangeProcessDAO() {
		return bulkRateChangeProcessDAO;
	}

	public void setBulkRateChangeProcessDAO(BulkRateChangeProcessDAO bulkRateChangeProcessDAO) {
		this.bulkRateChangeProcessDAO = bulkRateChangeProcessDAO;
	}

	public BulkProcessDetailsDAO getBulkProcessDetailsDAO() {
	    return bulkProcessDetailsDAO;
    }

	public void setBulkProcessDetailsDAO(BulkProcessDetailsDAO bulkProcessDetailsDAO) {
	    this.bulkProcessDetailsDAO = bulkProcessDetailsDAO;
    }

	public BulkProcessHeader getBulkProcessHeader() {
		return getBulkRateChangeProcessDAO().getBulkProcessHeader();
	}

	public BulkProcessHeader getNewBulkProcessHeader() {
		return getBulkRateChangeProcessDAO().getNewBulkProcessHeader();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTAcademics/BMTAcademics_Temp by using BulkRateChangeProcessDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using BulkRateChangeProcessDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtBMTAcademics by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		String tableType = "";
        BulkProcessHeader bulkProcessHeader = (BulkProcessHeader) auditHeader.getAuditDetail()
				.getModelData();
        if(bulkProcessHeader != null && bulkProcessHeader.getUserDetails() != null){
           setLoginUserDetails(bulkProcessHeader.getUserDetails());
        }
		if (bulkProcessHeader.isWorkflow()) {
			tableType = "_TEMP";
		}

		if (bulkProcessHeader.isNew()) {
			bulkProcessHeader.setBulkProcessId(getBulkRateChangeProcessDAO().save(bulkProcessHeader, tableType));
			auditHeader.getAuditDetail().setModelData(bulkProcessHeader);
			auditHeader.setAuditReference(String.valueOf(bulkProcessHeader.getBulkProcessId()));
		} else {
			getBulkRateChangeProcessDAO().update(bulkProcessHeader, tableType);
			getBulkProcessDetailsDAO().updateList(bulkProcessHeader.getBulkProcessDetailsList(), tableType);
		}

		if (bulkProcessHeader.getBulkProcessDetailsList() != null
		        && bulkProcessHeader.getBulkProcessDetailsList().size() > 0) {
			for (BulkProcessDetails bulkProDetails : bulkProcessHeader.getBulkProcessDetailsList()) {
				if(bulkProcessHeader.isNew() || bulkProcessHeader.isLovDescIsOlddataChanged()){
					bulkProDetails.setBulkProcessId(bulkProcessHeader.getBulkProcessId());
				}
				bulkProDetails.setRecordType(bulkProcessHeader.getRecordType());
				bulkProDetails.setRecordStatus(bulkProcessHeader.getRecordStatus());
				bulkProDetails.setRoleCode(bulkProcessHeader.getRoleCode());
				bulkProDetails.setNextRoleCode(bulkProcessHeader.getNextRoleCode());
				bulkProDetails.setTaskId(bulkProcessHeader.getTaskId());
				bulkProDetails.setNextTaskId(bulkProcessHeader.getNextTaskId());
			}
		}
		
		if (bulkProcessHeader.isNew() || bulkProcessHeader.isLovDescIsOlddataChanged()) {
			getBulkProcessDetailsDAO().saveList(bulkProcessHeader.getBulkProcessDetailsList(), tableType);
		} else {
			getBulkProcessDetailsDAO().updateList(bulkProcessHeader.getBulkProcessDetailsList(), tableType);
		}
		
		/*if (bulkProcessHeader.getBulkProcessDetailsList() != null && bulkProcessHeader.getBulkProcessDetailsList().size() > 0) {
			List<AuditDetail> details = bulkProcessHeader.getAuditDetailMap().get("BulkProcessDetails");
			
			for(BulkProcessDetails bulkProDetails : bulkProcessHeader.getBulkProcessDetailsList()){
				bulkProDetails.setRecordType(bulkProcessHeader.getRecordType());
				bulkProDetails.setRecordStatus(bulkProcessHeader.getRecordStatus());
				bulkProDetails.setRoleCode(bulkProcessHeader.getRoleCode());
				bulkProDetails.setNextRoleCode(bulkProcessHeader.getNextRoleCode());
				bulkProDetails.setTaskId(bulkProcessHeader.getTaskId());
				bulkProDetails.setNextTaskId(bulkProcessHeader.getNextTaskId());
			}
			
			getBulkProcessDetailsDAO().saveList(bulkProcessHeader.getBulkProcessDetailsList(), tableType);
			
			
			details = processBulkProcessDetails(details, bulkProcessHeader.getBulkProcessId(), tableType);
			auditDetails.addAll(details);
		}
		*/
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}
	
	

	/**
	 * Method for Rate changes for IJARAH Finances by Applying Actual rates
	 * 
	 * @param bulkRateChangeFinances
	 * @param fromDate
	 * @param toDate
	 * @param recalType
	 * @param rateChange
	 * @throws AccountNotFoundException
	 */
	@Override
	public boolean bulkRateChangeFinances(List<BulkProcessDetails> bulkRateChangeFinances,
			String recalType, BigDecimal rateChange) throws AccountNotFoundException {
		logger.debug("Entering");

		//Bulk Rate Changes applied for fetched list
		for (BulkProcessDetails rateChangeFinance : bulkRateChangeFinances) {

			//Get Total Finance Details to particular Finance
			FinanceDetail financeDetail = getFinSchdDetailById(rateChangeFinance.getFinReference(), "_AView", false);
			financeDetail.setUserDetails(rateChangeFinance.getUserDetails());
			//Reset Before Image for Auditing
			FinanceDetail befImage = new FinanceDetail();
			BeanUtils.copyProperties(financeDetail, befImage);
			financeDetail.setBefImage(befImage);

			FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
			financeMain.setUserDetails(rateChangeFinance.getUserDetails());
			financeMain.setEventFromDate(rateChangeFinance.getLovDescEventFromDate());
			financeMain.setEventToDate(rateChangeFinance.getLovDescEventToDate());
			financeMain.setRecalType(recalType);
			 
			 //Schedule Re-calculation based on Applied parameters
			financeDetail.setFinScheduleData(ScheduleCalculator.changeRate(financeDetail.getFinScheduleData(), "", "", BigDecimal.ZERO,
					rateChange == null ? BigDecimal.ZERO : rateChange, true));

			//Record proceed through WorkFlow defined Process
			
			String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), excludeFields);
			AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_UPD, 1, fields[0],
					fields[1], financeMain.getBefImage(), financeMain);
			AuditHeader auditHeader = new AuditHeader(financeDetail.getFinScheduleData().getFinReference(), null, null, null, auditDetail,
					financeDetail.getUserDetails(), new HashMap<String, ArrayList<ErrorDetails>>());

			//Changed Finance Save in Database
			saveOrUpdate(auditHeader, false);
		}

		logger.debug("Leaving");
		return true;
	}
	
	
	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table FinanceMain/FinanceMain_Temp by
	 * using FinanceMainDAO's save method b) Update the Record in the table. based on the module workFlow Configuration.
	 * by using FinanceMainDAO's update method 3) Audit the record in to AuditHeader and AdtFinanceMain by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws AccountNotFoundException
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader aAuditHeader, boolean isWIF)
	throws AccountNotFoundException {
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
		financeDetail.setUserDetails(getLoginUserDetails());
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		financeMain.setUserDetails(getLoginUserDetails());
		Date curBDay = (Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");

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
			saveFeeChargeList(financeDetail.getFinScheduleData(), isWIF,tableType);
		} else {
			listSave(financeDetail.getFinScheduleData(), tableType, isWIF, 0);
			saveFeeChargeList(financeDetail.getFinScheduleData(), isWIF,tableType);
		}

		// Save asset details
		//=======================================
		if(!isWIF){
			doSaveAddlFieldDetails(financeDetail, tableType);
		}

		if(!isWIF){
			String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), excludeFields);
			auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0],
					fields[1], financeDetail.getBefImage(), financeDetail));
			
		//	AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_UPD, 1, financeDetail.getBefImage(), financeDetail);
			AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_UPD, 1, fields[0],
					fields[1], financeMain.getBefImage(), financeMain);
			
			 auditHeader = new AuditHeader(financeDetail.getFinScheduleData().getFinReference(), null, null, null, auditDetail,
			financeDetail.getUserDetails(), new HashMap<String, ArrayList<ErrorDetails>>());
			
			getAuditHeaderDAO().addAudit(auditHeader);
		}

		logger.debug("Leaving");
		return auditHeader;
	}
	
	
	/**
	 * Method for Save/ Update Additional Field Details
	 */
	private void doSaveAddlFieldDetails(FinanceDetail financeDetail, String tableType) {
		logger.debug("Entering");

		if (financeDetail.getLovDescExtendedFieldValues() != null
				&& financeDetail.getLovDescExtendedFieldValues().size() > 0) {

			String tableName = "";
			if (PennantStaticListUtil.getModuleName().containsKey("FASSET")) {
				tableName = PennantStaticListUtil.getModuleName().get("FASSET").get(
						financeDetail.getFinScheduleData().getFinanceType().getLovDescAssetCodeName());
			}

			if (!getExtendedFieldDetailDAO().isExist(tableName,
					financeDetail.getFinScheduleData().getFinReference(), tableType)) {
				getExtendedFieldDetailDAO().saveAdditional(financeDetail.getFinScheduleData().getFinReference(),
						financeDetail.getLovDescExtendedFieldValues(), tableType, tableName);
			} else {
				getExtendedFieldDetailDAO().updateAdditional(financeDetail.getLovDescExtendedFieldValues(),
						financeDetail.getFinScheduleData().getFinReference(), tableType, tableName);
			}
		}
		logger.debug("Leaving");
	}

	
	/**
	 * Method for saving List of Fee Charge details
	 * 
	 * @param finDetail
	 * @param tableType
	 */
	public void saveFeeChargeList(FinScheduleData finScheduleData, boolean isWIF, String tableType) {
		logger.debug("Entering");

		if (finScheduleData.getFeeRules() != null && finScheduleData.getFeeRules().size() > 0) {
			//Finance Fee Charge Details
			for (int i = 0; i < finScheduleData.getFeeRules().size(); i++) {
				finScheduleData.getFeeRules().get(i)
				.setFinReference(finScheduleData.getFinReference());
			}
			getPostingsDAO().saveChargesBatch(finScheduleData.getFeeRules(),isWIF, tableType);
		}

		logger.debug("Leaving");
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

		//Finance Defferment Header Details
		for (int i = 0; i < finDetail.getDefermentHeaders().size(); i++) {
			finDetail.getDefermentHeaders().get(i).setFinReference(finDetail.getFinReference());
			finDetail.getDefermentHeaders().get(i).setLogKey(logKey);
		}
		getDefermentHeaderDAO().saveList(finDetail.getDefermentHeaders(), tableType, isWIF);

		//Finance Defferment Details
		for (int i = 0; i < finDetail.getDefermentDetails().size(); i++) {
			finDetail.getDefermentDetails().get(i).setFinReference(finDetail.getFinReference());
			finDetail.getDefermentDetails().get(i).setLogKey(logKey);
		}
		getDefermentDetailDAO().saveList(finDetail.getDefermentDetails(), tableType, isWIF);

		//Finance Repay Instruction Details
		for (int i = 0; i < finDetail.getRepayInstructions().size(); i++) {
			finDetail.getRepayInstructions().get(i).setFinReference(finDetail.getFinReference());
			finDetail.getRepayInstructions().get(i).setLogKey(logKey);
		}
		getRepayInstructionDAO().saveList(finDetail.getRepayInstructions(), tableType, isWIF);

		FinanceMain aFinanceMain = finDetail.getFinanceMain();
		aFinanceMain.setAvailedDefRpyChange(aFinanceMain.getAvailedDefRpyChange()+1);
		aFinanceMain.setVersion(aFinanceMain.getVersion()+1);
		getFinanceMainDAO().update(aFinanceMain, "", false);
		
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
		getDefermentHeaderDAO().deleteByFinReference(finDetail.getFinReference(), tableType, isWIF, 0);
		getDefermentDetailDAO().deleteByFinReference(finDetail.getFinReference(), tableType, isWIF, 0);
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
		finSchData.setDefermentHeaders(getDefermentHeaderDAO().getDefermentHeaders(finReference, type, false));
		finSchData.setDefermentDetails(getDefermentDetailDAO().getDefermentDetails(finReference, type, false));

		if(logKey == 0){
			finSchData.setFinanceType(getFinanceTypeDAO().getFinanceTypeByID(finSchData.getFinanceMain().getFinType(), type));
			finSchData.setFeeRules(getPostingsDAO().getFeeChargesByFinRef(finReference,false, ""));
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
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5)
	 * for any mismatch conditions Fetch the error details from getFinanceMainDAO().getErrorDetail with Error ID and
	 * language as parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
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

		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());
		FinanceDetail financeDetail = (FinanceDetail) auditDetail.getModelData();
		financeDetail.setUserDetails(getLoginUserDetails());
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		financeMain.setUserDetails(getLoginUserDetails());

		FinanceMain tempFinanceMain = null;
		if (financeMain.isWorkflow()) {
			tempFinanceMain = getFinanceMainDAO().getFinanceMainById(financeMain.getId(), "_Temp", isWIF);
		}
		FinanceMain befFinanceMain = getFinanceMainDAO().getFinanceMainById(financeMain.getId(), "", isWIF);
		FinanceMain old_FinanceMain = financeMain.getBefImage();

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
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
						PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
			} else {
				if (old_FinanceMain != null && !old_FinanceMain.getLastMntOn()
						.equals(befFinanceMain.getLastMntOn())) {
					if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
							.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41003", errParm, valueParm), usrLanguage));
					} else {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41004", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {

			if (tempFinanceMain == null) { // if records not exists in the
				// Work flow table
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
						PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
			}

			if (tempFinanceMain != null && old_FinanceMain != null
					&& !old_FinanceMain.getLastMntOn().equals(tempFinanceMain.getLastMntOn())) {
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
						PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
			}
		}
	
		
		//Fee Details Waiver Validations TODO
		/*if(financeDetail.getFinScheduleData().getFeeRules() != null && 
				!financeDetail.getFinScheduleData().getFeeRules().isEmpty()){
			
			List<FeeRule> feeRuleList = financeDetail.getFinScheduleData().getFeeRules();
			for (FeeRule feeRule : feeRuleList) {
				
				String[] errParm1 = new String[1];
				String[] valueParm1 = new String[1];
				valueParm[0] = feeRule.getFeeCodeDesc();
				errParm[0] = PennantJavaUtil.getLabel("label_FeeCode") + ":" + valueParm[0];
				
				BigDecimal maxWaiverAmt = feeRule.getFeeAmount().multiply(feeRule.getWaiverPerc()).divide(
						new BigDecimal(100), 0, RoundingMode.HALF_DOWN);
				if(feeRule.getWaiverAmount().compareTo(maxWaiverAmt) > 0){
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
							PennantConstants.KEY_FIELD, "E0040", errParm1, valueParm1), usrLanguage));
				}
            }
		}*/ 

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if (StringUtils.trimToEmpty(method).equals("doApprove") || !financeMain.isWorkflow()) {
			auditDetail.setBefImage(befFinanceMain);
		}

		return auditDetail;
	}
	
	/**
	 * Method to fetch finance details by id from given table type
	 * 
	 * @param finReference
	 *            (String)
	 * @param type
	 *            (String)
	 * @return FinanceDetail
	 * */
	private FinanceDetail getFinSchdDetailById(String finReference, String type, boolean isWIF) {
		logger.debug("Entering");

		//Finance Details
		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		scheduleData.setFinReference(finReference);
		scheduleData.setFinanceMain(getFinanceMainDAO().getFinanceMainById(finReference, type, isWIF));

		if (scheduleData.getFinanceMain() != null) {

			//Finance Type Details
			scheduleData.setFinanceType(getFinanceTypeDAO().getFinanceTypeByID(scheduleData.getFinanceMain().getFinType(), "_AView"));

			//Finance Schedule Details
			scheduleData.setFinanceScheduleDetails(getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, type, isWIF));

			//Finance Disbursement Details
			scheduleData.setDisbursementDetails(getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference, type, isWIF));
			
			String tableType = "";
			if(!StringUtils.trimToEmpty(scheduleData.getFinanceMain().getRecordType()).equals("")){
				tableType = "_Temp";
			}

			//Finance Deferment Header Details
			scheduleData.setDefermentHeaders(getDefermentHeaderDAO().getDefermentHeaders(finReference, tableType, isWIF));

			//Finance Deferment Details
			scheduleData.setDefermentDetails(getDefermentDetailDAO().getDefermentDetails(finReference, tableType, isWIF));

			//Finance Repayment Instruction Details
			scheduleData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(finReference, type, isWIF));

			//Finance Overdue Penalty Rate Details
			if (!isWIF) {
				scheduleData.setFinODPenaltyRate(getFinODPenaltyRateDAO().getFinODPenaltyRateByRef(finReference, type));
			}
			
			//Fetch Finance Premium Details
			String productCode = scheduleData.getFinanceType().getFinCategory();
			if(productCode.equals(PennantConstants.FINANCE_PRODUCT_SUKUK)){
				financeDetail.setPremiumDetail(getFinancePremiumDetailDAO().getFinPremiumDetailsById(finReference, type));
			}
		}

		logger.debug("Leaving");
		return financeDetail;
	}
	
	/**
	 * Method For Preparing List of AuditDetails for Customer Ratings
	 * 
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
	private List<AuditDetail> processBulkProcessDetails(List<AuditDetail> auditDetails, long detailId, String type) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			BulkProcessDetails bulkProcessDetails = (BulkProcessDetails) auditDetails.get(i).getModelData();
			bulkProcessDetails.setBulkProcessId(detailId);
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (type.equals("")) {
				approveRec = true;
				bulkProcessDetails.setRoleCode("");
				bulkProcessDetails.setNextRoleCode("");
				bulkProcessDetails.setTaskId("");
				bulkProcessDetails.setNextTaskId("");
			}

			if (bulkProcessDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (bulkProcessDetails.isNewRecord()) {
				saveRecord = true;
				if (bulkProcessDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					bulkProcessDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (bulkProcessDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					bulkProcessDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (bulkProcessDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					bulkProcessDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (bulkProcessDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (bulkProcessDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
					updateRecord = true;
			} else if (bulkProcessDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (bulkProcessDetails.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = bulkProcessDetails.getRecordType();
				recordStatus = bulkProcessDetails.getRecordStatus();
				bulkProcessDetails.setRecordType("");
				bulkProcessDetails.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			if (saveRecord) {
				getBulkProcessDetailsDAO().save(bulkProcessDetails, type);
			}

			if (updateRecord) {
				getBulkProcessDetailsDAO().update(bulkProcessDetails, type);
			}

			if (deleteRecord) {
				getBulkProcessDetailsDAO().delete(bulkProcessDetails, type);
			}

			if (approveRec) {
				bulkProcessDetails.setRecordType(rcdType);
				bulkProcessDetails.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(bulkProcessDetails);
		}

		return auditDetails;

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
		BulkProcessHeader bulkProcessHeader =null;
		FinanceDetail financeDetail = null;
		FinanceMain financeMain = null;
		if(auditHeader.getAuditDetail().getModelData() instanceof BulkProcessHeader){
		     bulkProcessHeader = (BulkProcessHeader) auditHeader.getAuditDetail().getModelData();
		     setLoginUserDetails(bulkProcessHeader.getUserDetails());
		} else {
			financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
			financeDetail.setUserDetails(getLoginUserDetails());
			financeMain = financeDetail.getFinScheduleData().getFinanceMain();
			financeMain.setUserDetails(getLoginUserDetails());
		}

		String auditTranType = "";
		if (bulkProcessHeader != null) {
			if (method.equals("saveOrUpdate") || method.equals("doApprove")
			        || method.equals("doReject")) {
				if (bulkProcessHeader.isWorkflow()) {
					auditTranType = PennantConstants.TRAN_WF;
				}
			}

			if (bulkProcessHeader.getBulkProcessDetailsList() != null
			        && bulkProcessHeader.getBulkProcessDetailsList().size() > 0) {
				auditDetailMap.put("BulkProcessDetails",
				        setBulkProcessDetailsData(bulkProcessHeader, auditTranType, method));
				auditDetails.addAll(auditDetailMap.get("BulkProcessDetails"));
			}

			bulkProcessHeader.setAuditDetailMap(auditDetailMap);
			auditHeader.getAuditDetail().setModelData(bulkProcessHeader);
			auditHeader.setAuditDetails(auditDetails);
		} else {

			if (method.equals("saveOrUpdate") || method.equals("doApprove")
			        || method.equals("doReject")) {
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
	private List<AuditDetail> setBulkProcessDetailsData(BulkProcessHeader bulkProcessHeader, String auditTranType, String method) {
		logger.debug("Entering");
		
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new BulkProcessDetails(), "");

		for (int i = 0; i < bulkProcessHeader.getBulkProcessDetailsList().size(); i++) {
			
			BulkProcessDetails bulkProcessDetails = bulkProcessHeader.getBulkProcessDetailsList().get(i);
			bulkProcessDetails.setWorkflowId(bulkProcessHeader.getWorkflowId());
			bulkProcessDetails.setRecordType(bulkProcessHeader.getRecordType());
			bulkProcessDetails.setNewRecord(bulkProcessHeader.isNew());
			
			boolean isRcdType = false;
			if(bulkProcessDetails.getRecordType()!=null){
				if (bulkProcessDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					bulkProcessDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					isRcdType = true;
				} else if (bulkProcessDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					bulkProcessDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					isRcdType = true;
				} else if (bulkProcessDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					bulkProcessDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					//isRcdType = true;
				}
				
				if (method.equals("saveOrUpdate") && (isRcdType == true)) {
					bulkProcessDetails.setNewRecord(true);
				}
				
				if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
					if (bulkProcessDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
						auditTranType = PennantConstants.TRAN_ADD;
					} else if (bulkProcessDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
							|| bulkProcessDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
						auditTranType = PennantConstants.TRAN_DEL;
					} else {
						auditTranType = PennantConstants.TRAN_UPD;
					}
				}
				bulkProcessDetails.setRecordStatus(bulkProcessHeader.getRecordStatus());
				bulkProcessDetails.setUserDetails(bulkProcessHeader.getUserDetails());
				bulkProcessDetails.setLastMntOn(bulkProcessHeader.getLastMntOn());

				if (!StringUtils.trimToEmpty(bulkProcessDetails.getRecordType()).equals("")) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], bulkProcessDetails.getBefImage(), bulkProcessDetails));
				}
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}
	
	
	

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTAcademics by using BulkRateChangeProcessDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtBMTAcademics by using
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

		BulkProcessHeader bulkProcessHeader = (BulkProcessHeader) auditHeader.getAuditDetail()
				.getModelData();
		getBulkRateChangeProcessDAO().delete(bulkProcessHeader, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getAcademicById fetch the details by using BulkRateChangeProcessDAO's getAcademicById
	 * method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Academic
	 */
	@Override
	public BulkProcessHeader getBulkProcessHeaderById(long bulkProcessId, String bulkProcessFor) {
		BulkProcessHeader bulkProcessHeader = getBulkRateChangeProcessDAO().getBulkProcessHeaderById(bulkProcessId, "_View", bulkProcessFor);
		if (bulkProcessHeader!=null) {
			bulkProcessHeader.setBulkProcessDetailsList(getBulkProcessDetailsDAO().getBulkProcessDetailsListById(bulkProcessHeader.getBulkProcessId(), "_View"));
        }
		return bulkProcessHeader;
	}

	/**
	 * getApprovedAcademicById fetch the details by using BulkRateChangeProcessDAO's
	 * getAcademicById method . with parameter id and type as blank. it fetches
	 * the approved records from the BMTAcademics.
	 * 
	 * @param id
	 *            (String)
	 * @return Academic
	 */
	public BulkProcessHeader getApprovedBulkProcessHeaderById(long bulkProcessId) {
		BulkProcessHeader bulkProcessHeader = getBulkRateChangeProcessDAO().getBulkProcessHeaderById(bulkProcessId, "_AView", null);
		if (bulkProcessHeader!=null) {
			bulkProcessHeader.setBulkProcessDetailsList(getBulkProcessDetailsDAO().getBulkProcessDetailsListById(bulkProcessHeader.getBulkProcessId(), "_AView"));
        }
		return bulkProcessHeader;
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param Academic
	 *            (academic)
	 * @return academic
	 */
	@Override
	public BulkProcessHeader refresh(BulkProcessHeader bulkProcessHeader) {
		logger.debug("Entering");
		getBulkRateChangeProcessDAO().refresh(bulkProcessHeader);
		getBulkRateChangeProcessDAO().initialize(bulkProcessHeader);
		logger.debug("Leaving");
		return bulkProcessHeader;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getBulkRateChangeProcessDAO().delete with parameters academic,"" b) NEW Add new
	 * record in to main table by using getBulkRateChangeProcessDAO().save with parameters
	 * academic,"" c) EDIT Update record in the main table by using
	 * getBulkRateChangeProcessDAO().update with parameters academic,"" 3) Delete the record
	 * from the workFlow table by using getBulkRateChangeProcessDAO().delete with parameters
	 * academic,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBMTAcademics by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtBMTAcademics by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws Exception 
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) throws Exception {
		logger.debug("Entering");
		boolean success = false;
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		BulkProcessHeader bulkProcessHeader = new BulkProcessHeader();
		BeanUtils.copyProperties((BulkProcessHeader) auditHeader.getAuditDetail().getModelData(),
		        bulkProcessHeader);
		//Processing Fetch Finance Details for IJARAH Bulk Rate Changes
		try {
			if (bulkProcessHeader.getBulkProcessFor().equals("R")) {
				if (bulkProcessHeader.getBulkProcessDetailsList() != null) {
					for (BulkProcessDetails bulkProcessDetails : bulkProcessHeader
					        .getBulkProcessDetailsList()) {
						bulkProcessDetails.setLovDescEventFromDate(bulkProcessHeader.getFromDate());
						bulkProcessDetails.setLovDescEventToDate(bulkProcessHeader.getToDate());
					}
				}
				success = bulkRateChangeFinances(bulkProcessHeader.getBulkProcessDetailsList(),
				        bulkProcessHeader.getReCalType(), bulkProcessHeader.getNewProcessedRate());
			} else if (bulkProcessHeader.getBulkProcessFor().equals("D")) {
				if (bulkProcessHeader.getBulkProcessDetailsList() != null) {
					for (BulkProcessDetails bulkProcessDetails : bulkProcessHeader
					        .getBulkProcessDetailsList()) {
						bulkProcessDetails.setLovDescEventFromDate(bulkProcessHeader.getFromDate());
						bulkProcessDetails.setLovDescEventToDate(bulkProcessHeader.getToDate());
					}
				}
				success = bulkDefermentChanges(bulkProcessHeader.getBulkProcessDetailsList(),
				        bulkProcessHeader.getReCalType(), bulkProcessHeader.isExcludeDeferement(),
				        bulkProcessHeader.getAddTermAfter(), bulkProcessHeader.getReCalFromDate(),
				        bulkProcessHeader.getReCalToDate(), bulkProcessHeader.getReCalType());
			}
		} catch (Exception e) {
			success = false;
			logger.debug(e);
			throw e;
		}
		
		if (success) {
			if (bulkProcessHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
				tranType = PennantConstants.TRAN_DEL;
				getBulkRateChangeProcessDAO().delete(bulkProcessHeader, "");
				getBulkProcessDetailsDAO().deleteBulkProcessDetailsById(bulkProcessHeader.getBulkProcessId(), "");
			} else {
				bulkProcessHeader.setRoleCode("");
				bulkProcessHeader.setNextRoleCode("");
				bulkProcessHeader.setTaskId("");
				bulkProcessHeader.setNextTaskId("");
				bulkProcessHeader.setWorkflowId(0);
				
				if (bulkProcessHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_ADD;
					bulkProcessHeader.setRecordType("");
					getBulkRateChangeProcessDAO().save(bulkProcessHeader, "");
					setHeaderPropertiesToList(bulkProcessHeader);
					getBulkProcessDetailsDAO().deleteBulkProcessDetailsById(bulkProcessHeader.getBulkProcessId(), "_Temp");
					getBulkProcessDetailsDAO().saveList(bulkProcessHeader.getBulkProcessDetailsList(), "");
				} else {
					tranType = PennantConstants.TRAN_UPD;
					bulkProcessHeader.setRecordType("");
					setHeaderPropertiesToList(bulkProcessHeader);
					getBulkRateChangeProcessDAO().update(bulkProcessHeader, "");
					getBulkProcessDetailsDAO().updateList(bulkProcessHeader.getBulkProcessDetailsList(), "");
				}

				/*if (bulkProcessHeader.getBulkProcessDetailsList() != null
				        && bulkProcessHeader.getBulkProcessDetailsList().size() > 0) {
					List<AuditDetail> details = bulkProcessHeader.getAuditDetailMap().get(
					        "BulkProcessDetails");
					processBulkProcessDetails(details, bulkProcessHeader.getBulkProcessId(), "");
					auditDetails.addAll(details);
				}*/

			}

			getBulkRateChangeProcessDAO().delete(bulkProcessHeader, "_TEMP");
			auditHeader.setAuditDetails(getListAuditDetails(bulkProcessDetailsListDeletion(
			        bulkProcessHeader, "_TEMP", auditHeader.getAuditTranType())));
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getAuditHeaderDAO().addAudit(auditHeader);
			auditHeader.setAuditDetails(auditDetails);
			auditHeader.setAuditTranType(tranType);
			auditHeader.getAuditDetail().setAuditTranType(tranType);
			auditHeader.getAuditDetail().setModelData(bulkProcessHeader);
			getAuditHeaderDAO().addAudit(auditHeader);
		}
		logger.debug("Leaving");
		return auditHeader;
	}

	public void setHeaderPropertiesToList(BulkProcessHeader bulkProcessHeader){
		logger.debug("Entering");
		for (BulkProcessDetails bulkProDetails : bulkProcessHeader.getBulkProcessDetailsList()) {
			bulkProDetails.setRecordType(bulkProcessHeader.getRecordType());
			bulkProDetails.setRecordStatus(bulkProcessHeader.getRecordStatus());
			bulkProDetails.setRoleCode(bulkProcessHeader.getRoleCode());
			bulkProDetails.setNextRoleCode(bulkProcessHeader.getNextRoleCode());
			bulkProDetails.setTaskId(bulkProcessHeader.getTaskId());
			bulkProDetails.setNextTaskId(bulkProcessHeader.getNextTaskId());
		}
		logger.debug("Leaving");
	}
	
	
	/**
	 * Method deletion of creditReviewSummary list with existing fee type
	 * 
	 * @param bulkProcessDetails
	 * @param tableType
	 * 
	 */
	public List<AuditDetail> bulkProcessDetailsListDeletion(BulkProcessHeader bulkProcessHeader, String tableType, String auditTranType) {
		logger.debug("Entering");
		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		if (bulkProcessHeader.getBulkProcessDetailsList() != null && bulkProcessHeader.getBulkProcessDetailsList().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new BulkProcessDetails());
			for (int i = 0; i < bulkProcessHeader.getBulkProcessDetailsList().size(); i++) {
				BulkProcessDetails bulkProcessDetails = bulkProcessHeader.getBulkProcessDetailsList().get(i);
				if (!StringUtils.trimToEmpty(bulkProcessDetails.getRecordType()).equals("") || tableType.equals("")) {
					auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], bulkProcessDetails.getBefImage(), bulkProcessDetails));
				}
			}
			getBulkProcessDetailsDAO().deleteBulkProcessDetailsById(bulkProcessHeader.getBulkProcessId(), tableType);
		}
		
		logger.debug("Leaving");
		return auditList;
	}
	
	
	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getBulkRateChangeProcessDAO().delete with parameters
	 * academic,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtBMTAcademics by using auditHeaderDAO.addAudit(auditHeader) for Work
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

		BulkProcessHeader bulkProcessHeader = (BulkProcessHeader) auditHeader.getAuditDetail()
				.getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getBulkRateChangeProcessDAO().delete(bulkProcessHeader, "_TEMP");
		getBulkProcessDetailsDAO().deleteBulkProcessDetailsById(bulkProcessHeader.getBulkProcessId(), "_TEMP");
		//auditHeader.setAuditDetails(getListAuditDetails(listDeletion(bulkProcessHeader, "_TEMP", auditHeader.getAuditTranType())));
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}
	
	
	/**
	 * Method for Processing Bulk Finance for Deferment Process
	 * @throws Exception 
	 */
	@Override
	public boolean bulkDefermentChanges(List<BulkProcessDetails> defermentChangeFinances,
			String recalType, boolean excludeDeferment, String addTermAfter, Date calFromDate,
	        Date calToDate, String cbRecalType) throws Exception {
		logger.debug("Entering");

		List<String> referencesList = new ArrayList<String>();
		for (BulkProcessDetails bulkProcessDetail : defermentChangeFinances) {
			referencesList.add(bulkProcessDetail.getFinReference());
		}

		List<ScheduleMapDetails> scheduleMapDetails = null;
		if (cbRecalType.equals(CalculationConstants.RPYCHG_TILLDATE)) {
			scheduleMapDetails = getFinSchdDetailTermByDates(referencesList, calFromDate, calToDate);
		}
		
		//Bulk Deferment Changes applied for fetched list
		for (BulkProcessDetails defermentFinance : defermentChangeFinances) {
			if (defermentFinance.isAlwProcess()) {
				//Get Total Finance Details to particular Finance
				FinanceDetail financeDetail = getFinSchdDetailById(
				        defermentFinance.getFinReference(), "_AView", false);

				//Reset Before Image for Auditing
				FinanceDetail befImage = new FinanceDetail();
				BeanUtils.copyProperties(financeDetail, befImage);
				financeDetail.setBefImage(befImage);

				FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

				financeMain.setEventFromDate(defermentFinance.getDeferedSchdDate());
				Date eventToDate = null;
				for (BulkProcessDetails aDefermentFinance : defermentChangeFinances) {
					if (aDefermentFinance.getFinReference().equals(
					        defermentFinance.getFinReference())) {
						if (financeMain.getMaturityDate().compareTo(
						        defermentFinance.getDeferedSchdDate()) == 0) {
							throw new WrongValueException("Maturity Date is not allowed to Defer");
						} else if (aDefermentFinance.getDeferedSchdDate().compareTo(
						        defermentFinance.getDeferedSchdDate()) == 1) {
							eventToDate = aDefermentFinance.getDeferedSchdDate();
						}
					}
				}

				financeMain.setEventToDate(eventToDate != null ? eventToDate : defermentFinance
				        .getDeferedSchdDate());
				financeMain.setRecalType(recalType);
				financeMain.setExcludeDeferedDates(excludeDeferment);
				financeMain.setAddTermAfter(StringUtils.trimToEmpty(addTermAfter));
				
				if(cbRecalType.equals(CalculationConstants.RPYCHG_TILLDATE)){
					for(ScheduleMapDetails scheduleMap: scheduleMapDetails){
						if(scheduleMap.getFinReference().equals(financeMain.getFinReference())){
							financeMain.setRecalFromDate(scheduleMap.getSchdFromDate());
							financeMain.setRecalToDate(scheduleMap.getSchdToDate());
						}
					}
				} else if(cbRecalType.equals(CalculationConstants.RPYCHG_TILLMDT)){
					if(scheduleMapDetails == null){
						scheduleMapDetails = getFinSchdDetailTermByDates(referencesList, calFromDate, financeMain.getMaturityDate());
					}
					for(ScheduleMapDetails scheduleMap: scheduleMapDetails){
						if(scheduleMap.getFinReference().equals(financeMain.getFinReference())){
							financeMain.setRecalFromDate(scheduleMap.getSchdFromDate());
							financeMain.setRecalToDate(financeMain.getMaturityDate());
						}
					}
				} else {
					financeMain.setRecalFromDate(calFromDate);
					financeMain.setRecalToDate(calToDate);
				}
				
				
				//Schedule Re-calculation based on Applied parameters
				financeDetail.setFinScheduleData(ScheduleCalculator.addDeferment(financeDetail
				        .getFinScheduleData()));

				//Record proceed through WorkFlow defined Process
				AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_UPD, 1,
				        financeDetail.getBefImage(), financeDetail);
				AuditHeader auditHeader = new AuditHeader(financeDetail.getFinScheduleData()
				        .getFinReference(), null, null, null, auditDetail,
				        financeDetail.getUserDetails(),
				        new HashMap<String, ArrayList<ErrorDetails>>());

				//Changed Finance Save in Database
				saveOrUpdate(auditHeader, false);
			}
		}
		logger.debug("Leaving");
		return true;
	}
	
	
	/**
	 * Method for Processing Bulk Finance for Deferment Process
	 * @throws Exception 
	 */
	@Override
	public List<ScheduleMapDetails> getDeferedDates(
	        List<BulkProcessDetails> defermentChangeFinances, String cbRecalType,
	        Date reCalFromDate, Date reCalToDate) {
		logger.debug("Entering");

		Map<String, String> referencesMap = new HashMap<String, String>();
		for (BulkProcessDetails bulkProcessDetail : defermentChangeFinances) {
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
	
	/**
	 * Common Method for Customers list validation
	 * 
	 * @param list
	 * @param method
	 * @param userDetails
	 * @param lastMntON
	 * @return
	 */
	private List<AuditDetail> getListAuditDetails(List<AuditDetail> list) {
		logger.debug("Entering");
		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();

		if (list != null & list.size() > 0) {

			String[] fields = PennantJavaUtil.getFieldDetails(new BulkProcessDetails(), "");
			for (int i = 0; i < list.size(); i++) {

				String transType = "";
				String rcdType = "";
				
		      if(list.get(i).getModelData()instanceof BulkProcessDetails) {
		    	  BulkProcessDetails bulkProcessDetails = (BulkProcessDetails) ((AuditDetail) list.get(i)).getModelData();
					rcdType = bulkProcessDetails.getRecordType();

					if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
						transType = PennantConstants.TRAN_ADD;
					} else if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL) || rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
						transType = PennantConstants.TRAN_DEL;
					} else {
						transType = PennantConstants.TRAN_UPD;
					}
					
					if (!(transType.equals(""))) {
						// check and change below line for Complete code
						auditDetailsList.add(new AuditDetail(transType, ((AuditDetail) list.get(i)).getAuditSeq(),fields[0],fields[1], bulkProcessDetails.getBefImage(), bulkProcessDetails));
					}
				} 
			}
		}
		logger.debug("Leaving");
		return auditDetailsList;
	}
	
	
	/**
	 * Method deletion of BulkProcessDetails list with existing fee type
	 * 
	 * @param bulkProcessHeader
	 * @param tableType
	 */
	public List<AuditDetail> listDeletion(BulkProcessHeader bulkProcessHeader, String tableType, String auditTranType) {
		logger.debug("Entering");

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		if (bulkProcessHeader.getBulkProcessDetailsList() != null && bulkProcessHeader.getBulkProcessDetailsList().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new BulkProcessDetails());
			for (int i = 0; i < bulkProcessHeader.getBulkProcessDetailsList().size(); i++) {
				BulkProcessDetails bulkProcessDetails = bulkProcessHeader.getBulkProcessDetailsList().get(i);
				if (!bulkProcessDetails.getRecordType().equals("") || tableType.equals("")) {
					auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], bulkProcessDetails.getBefImage(), bulkProcessDetails));
				}
			}
			getBulkProcessDetailsDAO().deleteBulkProcessDetailsById(bulkProcessHeader.getBulkProcessId(), tableType);
		}
		
		logger.debug("Leaving");
		return auditList;
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
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),
				auditHeader.getUsrLanguage(), method);
		
		auditHeader = getAuditDetails(auditHeader, method);
		BulkProcessHeader bulkProcessHeader = (BulkProcessHeader) auditHeader.getAuditDetail().getModelData();
		String usrLanguage = bulkProcessHeader.getUserDetails().getUsrLanguage();
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		/*if (bulkProcessHeader.getBulkProcessDetailsList() != null && bulkProcessHeader.getBulkProcessDetailsList().size() > 0) {
			List<AuditDetail> details = bulkProcessHeader.getAuditDetailMap().get("BulkProcessDetails");
			details =  bulkProcessDetailsListValidation(details, method, usrLanguage);
			auditDetails.addAll(details);
		}*/
		
		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}
		
		auditHeader.setAuditDetail(auditDetail);
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	public List<AuditDetail> bulkProcessDetailsListValidation(List<AuditDetail> auditDetails, String method,String  usrLanguage){
		logger.debug("Entering");
		if(auditDetails!=null && auditDetails.size()>0){
			List<AuditDetail> details = new ArrayList<AuditDetail>();
			for (int i = 0; i < auditDetails.size(); i++) {
				AuditDetail auditDetail =   bulkProcessDetailsValidation(auditDetails.get(i), method, usrLanguage);
				details.add(auditDetail); 		
			}
			return details;
		}
		logger.debug("Leaving");
		return new ArrayList<AuditDetail>();
	}
	
	private AuditDetail bulkProcessDetailsValidation(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
		BulkProcessDetails bulkProcessDetails= (BulkProcessDetails) auditDetail.getModelData();
		
		BulkProcessDetails tempBulkProcessDetails= null;
		if (bulkProcessDetails.isWorkflow()){
			tempBulkProcessDetails = getBulkProcessDetailsDAO().getBulkProcessDetailsById(bulkProcessDetails.getId(), bulkProcessDetails.getFinReference(), bulkProcessDetails.getDeferedSchdDate(), "_Temp");
			
		}
		BulkProcessDetails befBulkProcessDetails= getBulkProcessDetailsDAO().getBulkProcessDetailsById(bulkProcessDetails.getId(), bulkProcessDetails.getFinReference(), bulkProcessDetails.getDeferedSchdDate(), "");
		
		BulkProcessDetails old_BulkProcessDetails= bulkProcessDetails.getBefImage();
		
		
		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=String.valueOf(bulkProcessDetails.getId());
		errParm[0]=PennantJavaUtil.getLabel("label_SubCategoryCode")+":"+valueParm[0];
		//bulkProcessDetails.setWorkflowId(0);
		if (bulkProcessDetails.isNew()){ // for New record or new record into work flow
			
			if (!bulkProcessDetails.isWorkflow()){// With out Work flow only new records  
				if (befBulkProcessDetails !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (bulkProcessDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befBulkProcessDetails !=null || tempBulkProcessDetails!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befBulkProcessDetails ==null || tempBulkProcessDetails!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!bulkProcessDetails.isWorkflow()){	// With out Work flow for update and delete
			
				if (befBulkProcessDetails ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (old_BulkProcessDetails!=null && !old_BulkProcessDetails.getLastMntOn().equals(befBulkProcessDetails.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{
			
				if (tempBulkProcessDetails==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
				
				if (old_BulkProcessDetails!=null && !old_BulkProcessDetails.getLastMntOn().equals(tempBulkProcessDetails.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		
		if(StringUtils.trimToEmpty(method).equals("doApprove") || !bulkProcessDetails.isWorkflow()){
			bulkProcessDetails.setBefImage(befBulkProcessDetails);	
		}
		
		logger.debug("Leaving");
		return auditDetail;
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
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage,
			String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());
		
		BulkProcessHeader bulkProcessHeader = (BulkProcessHeader) auditDetail.getModelData();

		BulkProcessHeader tempBulkProcessHeader = null;
		if (bulkProcessHeader.isWorkflow()) {
			tempBulkProcessHeader = getBulkRateChangeProcessDAO().getBulkProcessHeader(
					bulkProcessHeader.getBulkProcessId(),
					bulkProcessHeader.getFromDate(), "_Temp");
		}

		BulkProcessHeader befBulkProcessHeader = getBulkRateChangeProcessDAO().getBulkProcessHeader(
				bulkProcessHeader.getBulkProcessId(), bulkProcessHeader.getFromDate(),
				" ");
		BulkProcessHeader old_BulkProcessHeader = bulkProcessHeader.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];
		
		valueParm[0] = String.valueOf(bulkProcessHeader.getBulkProcessId());
		valueParm[1] = bulkProcessHeader.getFromDate().toString();

		errParm[0] = PennantJavaUtil.getLabel("label_BulkRateChangeSearch_fromDate.value") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_BulkRateChangeSearch_toDate.value") + ":"+valueParm[1];

		if (bulkProcessHeader.isNew()) { // for New record or new record into work flow

			if (!bulkProcessHeader.isWorkflow()) {// With out Work flow only new records
				if (befBulkProcessHeader != null) { // Record Already Exists in the table
											// then error
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
				}
			} else { // with work flow
				if (bulkProcessHeader.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
																// is new
					if (befBulkProcessHeader != null || tempBulkProcessHeader != null) { // if records already exists in
												// the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
					}
				} else { // if records not exists in the Main flow table
					if (befBulkProcessHeader == null || tempBulkProcessHeader != null) {
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!bulkProcessHeader.isWorkflow()) { // With out Work flow for update and
											// delete
				if (befBulkProcessHeader == null) { // if records not exists in the main
											// table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41002",errParm,null));
				} else {
					if (old_BulkProcessHeader != null
							&& !old_BulkProcessHeader.getLastMntOn().equals(
									befBulkProcessHeader.getLastMntOn())) {
						if (StringUtils.trimToEmpty(
								auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41003",errParm,null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41004",errParm,null));
						}
					}
				}
			} else {
				if (tempBulkProcessHeader == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}
				
				if ( tempBulkProcessHeader != null &&  old_BulkProcessHeader != null
						&& !old_BulkProcessHeader.getLastMntOn().equals(
								tempBulkProcessHeader.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		
		if (StringUtils.trimToEmpty(method).equals("doApprove")
				|| !bulkProcessHeader.isWorkflow()) {
			auditDetail.setBefImage(befBulkProcessHeader);
		}

		logger.debug("Leaving");
		return auditDetail;
	}
	
	@ Override
	public BulkProcessHeader getBulkProcessHeaderByFromAndToDates( Date fromDate, Date toDate, String type) {
		logger.debug("Entering");
		return getBulkRateChangeProcessDAO().getBulkProcessHeaderByFromAndToDates(fromDate, toDate, type);

	}
	
	
	@Override
	public List<BulkProcessDetails> getIjaraBulkRateFinList(Date fromDate, Date toDate) {
		return getBulkProcessDetailsDAO().getIjaraBulkRateFinList(fromDate, toDate);
	}
	
	@Override
	public List<BulkProcessDetails> getBulkDefermentFinList(Date fromDate, Date toDate, String whereClause) {
		return getBulkProcessDetailsDAO().getBulkDefermentFinList(fromDate, toDate, whereClause);
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ Getters & Setters +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
	    return financeScheduleDetailDAO;
    }

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
	    this.financeScheduleDetailDAO = financeScheduleDetailDAO;
    }

	public FinanceMainDAO getFinanceMainDAO() {
	    return financeMainDAO;
    }

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
	    this.financeMainDAO = financeMainDAO;
    }

	public FinanceTypeDAO getFinanceTypeDAO() {
	    return financeTypeDAO;
    }

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
	    this.financeTypeDAO = financeTypeDAO;
    }

	public FinanceDisbursementDAO getFinanceDisbursementDAO() {
	    return financeDisbursementDAO;
    }

	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
	    this.financeDisbursementDAO = financeDisbursementDAO;
    }

	public DefermentHeaderDAO getDefermentHeaderDAO() {
	    return defermentHeaderDAO;
    }

	public void setDefermentHeaderDAO(DefermentHeaderDAO defermentHeaderDAO) {
	    this.defermentHeaderDAO = defermentHeaderDAO;
    }

	public DefermentDetailDAO getDefermentDetailDAO() {
	    return defermentDetailDAO;
    }

	public void setDefermentDetailDAO(DefermentDetailDAO defermentDetailDAO) {
	    this.defermentDetailDAO = defermentDetailDAO;
    }

	public RepayInstructionDAO getRepayInstructionDAO() {
	    return repayInstructionDAO;
    }

	public void setRepayInstructionDAO(RepayInstructionDAO repayInstructionDAO) {
	    this.repayInstructionDAO = repayInstructionDAO;
    }

	public FinODPenaltyRateDAO getFinODPenaltyRateDAO() {
	    return finODPenaltyRateDAO;
    }

	public void setFinODPenaltyRateDAO(FinODPenaltyRateDAO finODPenaltyRateDAO) {
	    this.finODPenaltyRateDAO = finODPenaltyRateDAO;
    }

	public FinancePremiumDetailDAO getFinancePremiumDetailDAO() {
	    return financePremiumDetailDAO;
    }

	public void setFinancePremiumDetailDAO(FinancePremiumDetailDAO financePremiumDetailDAO) {
	    this.financePremiumDetailDAO = financePremiumDetailDAO;
    }

	public PostingsDAO getPostingsDAO() {
	    return postingsDAO;
    }

	public void setPostingsDAO(PostingsDAO postingsDAO) {
	    this.postingsDAO = postingsDAO;
    }

	public FinanceProfitDetailDAO getProfitDetailsDAO() {
	    return profitDetailsDAO;
    }

	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
	    this.profitDetailsDAO = profitDetailsDAO;
    }

	public FinLogEntryDetailDAO getFinLogEntryDetailDAO() {
	    return finLogEntryDetailDAO;
    }

	public void setFinLogEntryDetailDAO(FinLogEntryDetailDAO finLogEntryDetailDAO) {
	    this.finLogEntryDetailDAO = finLogEntryDetailDAO;
    }

	public ExtendedFieldDetailDAO getExtendedFieldDetailDAO() {
	    return extendedFieldDetailDAO;
    }

	public void setExtendedFieldDetailDAO(ExtendedFieldDetailDAO extendedFieldDetailDAO) {
	    this.extendedFieldDetailDAO = extendedFieldDetailDAO;
    }

	public FinanceRepaymentsDAO getFinanceRepaymentsDAO() {
	    return financeRepaymentsDAO;
    }

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
	    this.financeRepaymentsDAO = financeRepaymentsDAO;
    }

	public OverdueChargeRecoveryDAO getRecoveryDAO() {
	    return recoveryDAO;
    }

	public void setRecoveryDAO(OverdueChargeRecoveryDAO recoveryDAO) {
	    this.recoveryDAO = recoveryDAO;
    }

	public LoginUserDetails getLoginUserDetails() {
    	return loginUserDetails;
    }

	public void setLoginUserDetails(LoginUserDetails loginUserDetails) {
    	this.loginUserDetails = loginUserDetails;
    }
}