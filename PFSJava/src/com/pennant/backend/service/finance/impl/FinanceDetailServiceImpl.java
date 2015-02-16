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
 * * FileName : FinanceDetailServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 15-11-2011 * *
 * Modified Date : 15-11-2011 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 15-11-2011 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.backend.service.finance.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.Interface.service.CustomerLimitIntefaceService;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.dao.NotesDAO;
import com.pennant.backend.dao.applicationmaster.CurrencyDAO;
import com.pennant.backend.dao.customermasters.CustomerIncomeDAO;
import com.pennant.backend.dao.finance.FinContributorDetailDAO;
import com.pennant.backend.dao.finance.FinContributorHeaderDAO;
import com.pennant.backend.dao.finance.FinancePremiumDetailDAO;
import com.pennant.backend.dao.finance.FinanceWriteoffDAO;
import com.pennant.backend.dao.finance.IndicativeTermDetailDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.dao.rmtmasters.AccountTypeDAO;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.dao.solutionfactory.ExtendedFieldDetailDAO;
import com.pennant.backend.dao.staticparms.ExtendedFieldHeaderDAO;
import com.pennant.backend.dao.systemmasters.IncomeTypeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.GlobalVariable;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.applicationmaster.CustomerStatusCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.customermasters.CustomerScoringCheck;
import com.pennant.backend.model.customermasters.WIFCustomer;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.BulkDefermentChange;
import com.pennant.backend.model.finance.BulkProcessDetails;
import com.pennant.backend.model.finance.CustomerFinanceDetail;
import com.pennant.backend.model.finance.FinContributorDetail;
import com.pennant.backend.model.finance.FinContributorHeader;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.finance.IndicativeTermDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.finance.ProspectCustomer;
import com.pennant.backend.model.finance.contractor.ContractorAssetDetail;
import com.pennant.backend.model.lmtmasters.CarLoanDetail;
import com.pennant.backend.model.lmtmasters.CommidityLoanDetail;
import com.pennant.backend.model.lmtmasters.CommidityLoanHeader;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.GenGoodsLoanDetail;
import com.pennant.backend.model.lmtmasters.GoodsLoanDetail;
import com.pennant.backend.model.lmtmasters.HomeLoanDetail;
import com.pennant.backend.model.lmtmasters.MortgageLoanDetail;
import com.pennant.backend.model.reports.AvailFinance;
import com.pennant.backend.model.rmtmasters.AccountType;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.model.staticparms.ExtendedFieldHeader;
import com.pennant.backend.model.systemmasters.IncomeType;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.GenericFinanceDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.coreinterface.model.CustomerLimit;
import com.rits.cloning.Cloner;

/**
 * Service implementation for methods that depends on <b>FinanceMain</b>.<br>
 * 
 */
public class FinanceDetailServiceImpl extends GenericFinanceDetailService implements  FinanceDetailService {
	private final static Logger logger = Logger.getLogger(FinanceDetailServiceImpl.class);

	private CustomerIncomeDAO customerIncomeDAO;
	private IncomeTypeDAO incomeTypeDAO;
	private FinContributorHeaderDAO finContributorHeaderDAO;
	private FinContributorDetailDAO finContributorDetailDAO;
	private FinanceReferenceDetailDAO financeReferenceDetailDAO;
	private RuleDAO ruleDAO;
	private ExtendedFieldHeaderDAO extendedFieldHeaderDAO;
	private ExtendedFieldDetailDAO extendedFieldDetailDAO;
	private AccountingSetDAO accountingSetDAO;
	private RuleExecutionUtil ruleExecutionUtil;
	private AccountTypeDAO accountTypeDAO;
	private CurrencyDAO currencyDAO;
	private CustomerLimitIntefaceService custLimitIntefaceService;
	private FinanceWriteoffDAO financeWriteoffDAO;
	private IndicativeTermDetailDAO indicativeTermDetailDAO;
	private FinancePremiumDetailDAO financePremiumDetailDAO;
	private NotesDAO notesDAO;
	
	@Override
	public FinanceDetail getFinanceDetail(boolean isWIF) {
		logger.debug("Entering");
		FinanceDetail financeDetail = new FinanceDetail();
		financeDetail.getFinScheduleData().setFinanceMain(getFinanceMainDAO().getFinanceMain(isWIF));
		logger.debug("Leaving");
		return financeDetail;
	}

	@Override
	public FinanceDetail getNewFinanceDetail(boolean isWIF) {
		logger.debug("Entering");
		FinanceDetail financeDetail = getFinanceDetail(isWIF);
		financeDetail.setNewRecord(true);
		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * Method for Check Have to Creating New Finance Accessibility for User or not
	 */
	@Override
	public boolean checkFirstTaskOwnerAccess(String productCode, long usrLogin) {
		return getFinanceMainDAO().checkFirstTaskOwnerAccess(productCode, usrLogin);
	}

	/**
	 * getFinanceDetailById fetch the details by using FinanceMainDAO's getFinanceDetailById method.
	 * 
	 * @param finReference
	 *            (String)
	 * @return FinanceDetail
	 */
	public FinanceDetail getFinanceDetailById(String finReference, boolean isWIF, String eventCodeRef, boolean reqCustDetail) {
		logger.debug("Entering");

		//Finance Details
		FinanceDetail financeDetail = getFinSchdDetailById(finReference, "_View", isWIF);
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = scheduleData.getFinanceMain();

		//Finance Accounting Fee Charge Details
		scheduleData.setFeeRules(getPostingsDAO().getFeeChargesByFinRef(finReference, isWIF, "_View"));

		if(isWIF && reqCustDetail && scheduleData.getFinanceMain() != null){

			if(PennantConstants.FIN_DIVISION_RETAIL.equals(scheduleData.getFinanceType().getFinDivision())){

				long custId = scheduleData.getFinanceMain().getCustID();
				if(custId != 0){
					financeDetail.setCustomer(getCustomerDAO().getWIFCustomerByID(custId, null, "_AView"));
					
					if(financeDetail.getCustomer() == null){
						Customer customer = getCustomerDAO().getCustomerByID(custId, "");
						WIFCustomer wifcustomer = new WIFCustomer();
						BeanUtils.copyProperties(customer, wifcustomer);
						wifcustomer.setExistCustID(wifcustomer.getCustID());
						wifcustomer.setCustID(0);
						wifcustomer.setNewRecord(true);
						financeDetail.setCustomer(wifcustomer);
					}
				}else{
					WIFCustomer wifcustomer = new WIFCustomer();
					wifcustomer.setNewRecord(true);
					PFSParameter parameter = SystemParameterDetails.getSystemParameterObject("APP_DFT_CURR");
					wifcustomer.setCustBaseCcy(parameter.getSysParmValue().trim());
					wifcustomer.setLovDescCustBaseCcyName(parameter.getSysParmDescription());
					parameter = SystemParameterDetails.getSystemParameterObject("APP_DFT_NATION");
					wifcustomer.setCustNationality(parameter.getSysParmValue().trim());
					wifcustomer.setLovDescCustNationalityName(parameter.getSysParmDescription());
					wifcustomer.setCustTypeCode("EA");
					wifcustomer.setLovDescCustTypeCodeName("Individual");
					wifcustomer.setCustCtgCode("INDV");
					wifcustomer.setLovDescCustCtgCodeName("Individual");
					financeDetail.setCustomer(wifcustomer);
				}

				financeDetail.getCustomer().setCustomerIncomeList(prepareIncomeDetails());
				String finType = scheduleData.getFinanceType().getFinType();

				financeDetail.setElgRuleList(getEligibilityDetailService().setFinanceEligibilityDetails(finReference, financeMain.getFinCcy(),
						financeMain.getFinAmount(), financeMain.isNewRecord(), finType, null));

				financeDetail = getScoringDetailService().setFinanceScoringDetails(financeDetail, finType, null, 
						PennantConstants.CUST_CAT_INDIVIDUAL);
			}else{
				IndicativeTermDetail termDetail = getIndicativeTermDetailDAO().getIndicateTermByRef(finReference, "_View", true);
				if(termDetail == null){
					termDetail = new IndicativeTermDetail();
					termDetail.setCustId(financeMain.getCustID());
					termDetail.setLovDescCustShrtName(financeMain.getLovDescCustShrtName());
					termDetail.setNewRecord(true);
					termDetail.setWorkflowId(0);
				}
				financeDetail.setIndicativeTermDetail(termDetail);
			}

		}else if(isWIF && PennantConstants.FIN_DIVISION_RETAIL.equals(scheduleData.getFinanceType().getFinDivision())){

			ProspectCustomer propCustomer = getCustomerDAO().getProspectCustomer(finReference,"_View");
			if(propCustomer != null){
				financeMain.setCustID(propCustomer.getCustId());
				financeMain.setLovDescCustCIF(propCustomer.getCustCIF());
				financeMain.setLovDescCustShrtName(propCustomer.getCustShrtName());
				financeMain.setLovDescCustCtgTypeName(propCustomer.getCustTypeCtg());
				financeMain.setFinBranch(propCustomer.getCustDftBranch());
			}else{
				financeMain.setCustID(0);
				financeMain.setLovDescCustCIF("");
				financeMain.setLovDescCustShrtName("");
				financeMain.setLovDescCustCtgTypeName("");
				financeMain.setFinBranch("");
			}
		}

		if (!isWIF && financeMain != null) {

			//Finance Reference Details List
			financeDetail = getFinanceReferenceDetails(financeDetail, financeMain.getNextRoleCode(), "DDE", eventCodeRef);

			//Finance Contributor Details
			if (scheduleData.getFinanceType().isAllowRIAInvestment()) {
				financeDetail.setFinContributorHeader(getFinContributorHeaderDAO().getFinContributorHeaderById(finReference, "_View"));
				if (financeDetail.getFinContributorHeader() != null) {
					financeDetail.getFinContributorHeader().setContributorDetailList(
							getFinContributorDetailDAO().getFinContributorDetailByFinRef(finReference, "_View"));
				}
			}

			//Finance Overdue Penalty Rate Details
			scheduleData.setFinODPenaltyRate(getFinODPenaltyRateDAO().getFinODPenaltyRateByRef(finReference, "_View"));

			//Finance Document Details
			List<DocumentDetails> documentList = getDocumentDetailsDAO().getDocumentDetailsByRef(finReference, "_View");
			if(financeDetail.getDocumentDetailsList() != null && !financeDetail.getDocumentDetailsList().isEmpty()){
				financeDetail.getDocumentDetailsList().addAll(documentList);
			}else{
				financeDetail.setDocumentDetailsList(documentList);
			}

			//Finance Guaranteer Details			
			financeDetail.setGurantorsDetailList(getGuarantorDetailService().getGuarantorDetail(finReference, "_View"));

			//Finance Joint Account Details
			financeDetail.setJountAccountDetailList(getJointAccountDetailService().getJoinAccountDetail(finReference, "_View"));

			// finance Contract Asset Details
			if(PennantConstants.FINANCE_PRODUCT_ISTISNA.equals(financeDetail.getFinScheduleData().getFinanceType().getLovDescProductCodeName())){
				getContractorAssetDetailService().setContractorAssetDetails(financeDetail, "_View");
			}else{
				financeDetail.setContractorAssetDetails(null);
			}
		}

		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * Method for Fetching List of Fee Charge Details depends on Event Code
	 * @param finType
	 * @param startDate
	 * @param isWIF
	 * @return
	 */
	@Override
	public List<Rule> getFeeRuleDetails(FinanceType finType, Date startDate, boolean isWIF){
		logger.debug("Entering");

		//Finance Accounting Fee Charge Details
		String eventCode = "";
		Date curBussDate = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);
		if (startDate.after(curBussDate)) {
			eventCode = "ADDDBSF";
		} else {
			eventCode = "ADDDBSP";
		}
		String accSetId = returnAccountingSetid(eventCode, finType);

		//Finance Fee Charge Details
		List<Rule> feeChargeList = getTransactionEntryDAO().getListFeeChargeRules(Long.valueOf(accSetId), 
				(eventCode.startsWith("ADDDBS") ? "ADDDBS" : eventCode),"_AView", 0);
		logger.debug("Leaving");
		return feeChargeList;
	}
	
	/**
	 * Method for Fetch takaful insurance Details Object for Early Settlement Refund
	 * @param finReference
	 * @return
	 */
	@Override
	public FeeRule getTakafulFee(String finReference){
		return getPostingsDAO().getTakafulFee(finReference,"");
	}

	/**
	 * Method for Fetching List of Fee Rules From Approved Finance
	 */
	@Override
	public List<FeeRule> getApprovedFeeRules(String finReference, boolean isWIF) {
		logger.debug("Entering");
		List<FeeRule> feeRuleList = getPostingsDAO().getFeeChargesByFinRef(finReference, isWIF, "");
		logger.debug("Leaving");
		return feeRuleList;
	}

	@Override
	public List<ContractorAssetDetail> getContractorAssetDetailList(String finReference){
		logger.debug("Entering");
		List<ContractorAssetDetail> assetDetails;
		assetDetails = getContractorAssetDetailService().getContractorAssetDetailList(finReference, "_AView");
		logger.debug("Leaving");
		return assetDetails;
	}

	/**
	 * Method for get Finance Details
	 */
	@Override
	public FinanceDetail getStaticFinanceDetailById(String finReference, String type) {
		logger.debug("Entering");

		//Finance Details
		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		scheduleData.setFinReference(finReference);
		scheduleData.setFinanceMain(getFinanceMainDAO().getFinanceMainById(finReference, type, false));

		if (scheduleData.getFinanceMain() != null) {

			//Finance Type Details
			scheduleData.setFinanceType(getFinanceTypeDAO().getFinanceTypeByID(scheduleData.getFinanceMain().getFinType(), type));

			//Finance Schedule Details List
			scheduleData.setFinanceScheduleDetails(getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, type, false));

			//Finance Disbursement Details List
			scheduleData.setDisbursementDetails(getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference, type, false));

			//Finance Deferment Header Details List
			scheduleData.setDefermentHeaders(getDefermentHeaderDAO().getDefermentHeaders(finReference, type, false));

			//Finance Deferment Details List
			scheduleData.setDefermentDetails(getDefermentDetailDAO().getDefermentDetails(finReference, type, false));

			//Finance Repayments Instruction Details List
			scheduleData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(finReference, type, false));

			//Finance Overdue Penalty Rate Details
			scheduleData.setFinODPenaltyRate(getFinODPenaltyRateDAO().getFinODPenaltyRateByRef(finReference, type));

			// Retrieving Finance Asset details based upon 'AssetCode'
			FinanceType financeType = scheduleData.getFinanceType();
			if (financeType != null) {

				setAssetDetail(financeDetail, type);

				// Fetching the Additional Field Details
				ExtendedFieldHeader fieldHeader = getExtendedFieldHeaderDAO().getExtendedFieldHeaderByModuleName(
						"FASSET", scheduleData.getFinanceType().getLovDescAssetCodeName(), "_AView");

				if (fieldHeader != null) {
					financeDetail.setExtendedFieldHeader(fieldHeader);
					financeDetail.getExtendedFieldHeader().setExtendedFieldDetails(
							getExtendedFieldDetailDAO().getExtendedFieldDetailBySubModule(
									scheduleData.getFinanceType().getLovDescAssetCodeName(), "_AView"));

					if (financeDetail.getExtendedFieldHeader().getExtendedFieldDetails().size() > 0) {

						String tableName = "";
						if (PennantStaticListUtil.getModuleName().containsKey("FASSET")) {
							tableName = PennantStaticListUtil.getModuleName().get("FASSET").get(
									scheduleData.getFinanceType().getLovDescAssetCodeName());
						}

						HashMap<String, Object> map = (HashMap<String, Object>) getExtendedFieldDetailDAO().retrive(tableName,
								scheduleData.getFinReference(), "_Temp");

						if (map != null) {
							financeDetail.getLovDescExtendedFieldValues().putAll(map);
						}
					}
				}
			}
		}

		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * getApprovedFinanceDetailById fetch the details by using FinanceMainDAO's getFinanceDetailById method . with
	 * parameter id and type as blank. it fetches the approved records from the FinanceMain.
	 * 
	 * @param finReference
	 *            (String)
	 * @return FinanceDetail
	 */
	@Override
	public FinanceDetail getApprovedFinanceDetailById(String finReference, boolean isWIF) {
		return getFinSchdDetailById(finReference, "_AView", isWIF);
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
			
			//Step Policy Details List
			if(scheduleData.getFinanceMain().isStepFinance()){
				scheduleData.setStepPolicyDetails(getFinanceStepDetailDAO().getFinStepDetailListByFinRef(finReference, type, isWIF));
			}

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

			//Finance Repayments Instruction Details
			scheduleData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(finReference, type, isWIF));

			//Finance Overdue Penalty Rate Details
			if (!isWIF) {
				scheduleData.setFinODPenaltyRate(getFinODPenaltyRateDAO().getFinODPenaltyRateByRef(finReference, type));

				// Retrieving asset details
				if (scheduleData.getFinanceType() != null && 
						PennantConstants.RECORD_TYPE_NEW.equals(scheduleData.getFinanceMain().getRecordType())) {
					setAssetDetail(financeDetail, type);
				}
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
	 * Method for Fetching Finance Reference Details List by using FinReference
	 */
	@Override
	public FinanceDetail getFinanceReferenceDetails(FinanceDetail financeDetail, String userRole,  String screenCode, String eventCode) {
		logger.debug("Entering");

		//Finance Reference Details
		String finType = financeDetail.getFinScheduleData().getFinanceMain().getFinType();
		FinanceType financeType = financeDetail.getFinScheduleData().getFinanceType();
		FinanceMain financeMain =  financeDetail.getFinScheduleData().getFinanceMain();		
		String finReference = financeDetail.getFinScheduleData().getFinReference();

		String nextRoleCode = userRole;

		if (financeMain.isNewRecord() || PennantConstants.RECORD_TYPE_NEW.equals(financeMain.getRecordType())) {

			if (!financeMain.isNewRecord()) {
				nextRoleCode = financeMain.getNextRoleCode();
			}

			//Finance Agreement Details	
			//=======================================
			financeDetail.setAggrementList(getAgreementDetailService().getAggrementDetailList(finType, nextRoleCode));

		}

		// Accounting Set Details
		//=======================================

		boolean isCustExist = true;
		String ctgType = StringUtils.trimToEmpty(financeMain.getLovDescCustCtgTypeName());

		if ("".equals(ctgType)) {
			isCustExist = false;
		}

		if (StringUtils.trimToEmpty(eventCode).equals("")) {
			Date curBussDate = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);

			if (financeMain.getFinStartDate().after(curBussDate)) {
				eventCode = "ADDDBSF";
			} else {
				eventCode = "ADDDBSP";
			}
		}

		String accSetId = returnAccountingSetid(eventCode, financeType);
		
		if(!accSetId.equals("")){

			//Finance Accounting Posting Details
			//=======================================
			if (isCustExist) {

				financeDetail.setTransactionEntries(getTransactionEntryDAO().getListTransactionEntryById(Long.valueOf(accSetId), "_AEView", true));

				//Finance Commitment Accounting Posting Details
				//=======================================
				if (PennantConstants.RECORD_TYPE_NEW.equals(financeMain.getRecordType())) {
					if (financeType.isFinCommitmentReq()
							&& !StringUtils.trimToEmpty(financeMain.getFinCommitmentRef()).equals("")) {

						long accountingSetId = getAccountingSetDAO().getAccountingSetId("CMTDISB","CMTDISB");

						if (accountingSetId != 0) {
							financeDetail.setCmtFinanceEntries(getTransactionEntryDAO().getListTransactionEntryById(
									accountingSetId, "_AEView", true));
						}
					}
				}
			}

			//Finance Fee Charge Details
			//=======================================
			financeDetail.setFeeCharges(getTransactionEntryDAO().getListFeeChargeRules(
					Long.valueOf(accSetId), (eventCode.startsWith("ADDDBS") ? "ADDDBS" : eventCode), "_AView",0));
		}

		if (financeMain.isNewRecord()
				|| PennantConstants.RECORD_TYPE_NEW.equals(financeMain.getRecordType())) { 

			//Finance Stage Accounting Posting Details
			//=======================================
			if (isCustExist) {
				financeDetail.setStageTransactionEntries(getTransactionEntryDAO().getListTransactionEntryByRefType(
						finType, PennantConstants.Accounting, nextRoleCode, "_AEView", true));
			}

			if (isCustExist) {

				//Finance Eligibility Rule Details List
				//=======================================
				financeDetail.setElgRuleList(getEligibilityDetailService().setFinanceEligibilityDetails(finReference, financeMain.getFinCcy(),
						financeMain.getFinAmount(), financeMain.isNewRecord(), finType, userRole));

				//Finance Scoring Rule Details List
				//=======================================
				getScoringDetailService().setFinanceScoringDetails(financeDetail, finType, userRole, ctgType);

				if ((financeDetail.getElgRuleList() != null && !financeDetail.getElgRuleList().isEmpty())
						|| financeDetail.getScoringGroupList() != null && !financeDetail.getScoringGroupList().isEmpty()) {
					doFillCustEligibilityData(financeDetail);
				}

				// set Check List Details
				//=======================================
				getCheckListDetailService().setFinanceCheckListDetails(financeDetail, finType, userRole);

			}

			// Fetching Finance Asset Additional Fields
			//=======================================
			if (!screenCode.equals("QDE")) {

				ExtendedFieldHeader fieldHeader = null;
				List<ExtendedFieldDetail> extendedFieldDetails = null;
				String assetCode = financeType.getLovDescAssetCodeName();

				fieldHeader = getExtendedFieldHeaderDAO().getExtendedFieldHeaderByModuleName("FASSET", assetCode, "_View");

				if (fieldHeader != null) {
					extendedFieldDetails =  getExtendedFieldDetailDAO().getExtendedFieldDetailBySubModule(assetCode, "_View");
					fieldHeader.setExtendedFieldDetails(extendedFieldDetails);
					financeDetail.setExtendedFieldHeader(fieldHeader);

					finReference = StringUtils.trimToEmpty(financeDetail.getFinScheduleData().getFinReference());

					if (extendedFieldDetails != null && !extendedFieldDetails.isEmpty() && !finReference.equals("")) {

						String tableName = "";
						if (PennantStaticListUtil.getModuleName().containsKey("FASSET")) {
							tableName = PennantStaticListUtil.getModuleName().get("FASSET").get(assetCode);
						}

						HashMap<String, Object> map = (HashMap<String, Object>) getExtendedFieldDetailDAO().retrive(tableName, finReference, "_Temp");
						if (map != null) {
							financeDetail.getLovDescExtendedFieldValues().putAll(map);
						}
					}
				}
			}
		}

		logger.debug("Leaving");
		return financeDetail;
	}


	/**
	 * Method for testing Finance Reference is Already Exist or not
	 */
	@Override
	public boolean isFinReferenceExits(String financeReference, String tableType, boolean isWIF) {
		logger.debug("Entering");
		if (isWIF) {
			tableType = "";
		}
		logger.debug("Leaving");
		return getFinanceMainDAO().isFinReferenceExists(financeReference, tableType, isWIF);
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
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (!aAuditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);

		String tableType = "";
		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String finReference = financeMain.getFinReference();
		Date curBDay = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);

		if (financeMain.isWorkflow()) {
			tableType = "_TEMP";
		}

		//Accounting (Stage/Posting) Execution Process
		//=======================================
		if(!isWIF){
			if(financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){

				//Finance Stage Accounting Process
				//=======================================
				if (financeDetail.getStageAccountingList() != null && financeDetail.getStageAccountingList().size() > 0) {

					List<ReturnDataSet> list = new ArrayList<ReturnDataSet>();
					auditHeader = executeStageAccounting(auditHeader, list);
					if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
						return auditHeader;
					}
					// save Postings
					getPostingsDAO().saveBatch(list, "", false);
				}

			}else{

				//Accounting Execution Process on Maintaiance
				//=======================================
				if(tableType.equals("") && financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){

					auditHeader = executeAccountingProcess(auditHeader, curBDay);
					if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
						return auditHeader;
					}
				}
			}
		}

		if(isWIF){

			//Customer Basic Details Maintenance
			//=======================================
			WIFCustomer customer = financeDetail.getCustomer();
			if(customer != null){
				long custId = customer.getCustID();
				if (customer.isNewRecord()) {
					custId = getCustomerDAO().saveWIFCustomer(customer);
				}else{
					getCustomerDAO().updateWIFCustomer(customer);
					getCustomerIncomeDAO().deleteByCustomer(customer.getCustID(), "", true);
				}

				if(customer.getCustomerIncomeList() != null && !customer.getCustomerIncomeList().isEmpty()){
					for (CustomerIncome income : customer.getCustomerIncomeList()) {
						income.setCustID(custId);
					}
					getCustomerIncomeDAO().saveBatch(customer.getCustomerIncomeList(), "", true);
				}
				financeMain.setCustID(custId);
			}

			//Indicative Term Sheet Details Maintenance
			//=======================================
			IndicativeTermDetail termDetail = financeDetail.getIndicativeTermDetail();
			if(termDetail != null){
				termDetail.setFinReference(finReference);
				if (termDetail.isNewRecord()) {
					getIndicativeTermDetailDAO().save(termDetail, tableType, true);
				}else{
					getIndicativeTermDetailDAO().update(termDetail, tableType, true);
				}
			}
		}

		// Finance Main Details Save And Update
		//=======================================
		if (financeMain.isNew()) {
			getFinanceMainDAO().save(financeMain, tableType, isWIF);
			
			//Save Finance Premium Details
			String productCode = financeDetail.getFinScheduleData().getFinanceType().getFinCategory();
			if(productCode.equals(PennantConstants.FINANCE_PRODUCT_SUKUK)){
				financeDetail.getPremiumDetail().setFinReference(finReference);
				getFinancePremiumDetailDAO().save(financeDetail.getPremiumDetail(), tableType);
			}
			
		} else {
			getFinanceMainDAO().update(financeMain, tableType, isWIF);
			
			//Update Finance Premium Details
			String productCode = financeDetail.getFinScheduleData().getFinanceType().getFinCategory();
			if(productCode.equals(PennantConstants.FINANCE_PRODUCT_SUKUK)){
				getFinancePremiumDetailDAO().update(financeDetail.getPremiumDetail(), tableType);
			}
		}

		// Save Contributor Header Details
		//=======================================
		if (financeDetail.getFinContributorHeader() != null) {

			FinContributorHeader contributorHeader = financeDetail.getFinContributorHeader();
			contributorHeader.setWorkflowId(0);
			String[] fields = PennantJavaUtil.getFieldDetails(new FinContributorHeader());
			if (contributorHeader.isNewRecord()) {
				getFinContributorHeaderDAO().save(contributorHeader, tableType);
			} else {
				getFinContributorHeaderDAO().update(contributorHeader, tableType);
			}

			auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0],
					fields[1], financeDetail.getFinContributorHeader().getBefImage(), financeDetail.getFinContributorHeader()));

			if (contributorHeader.getContributorDetailList() != null
					&& contributorHeader.getContributorDetailList().size() > 0) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("Contributor");
				details = processingContributorList(details, tableType,
						contributorHeader.getFinReference());
				auditDetails.addAll(details);
			}
		}

		// Save schedule details
		//=======================================
		if (!financeDetail.isNewRecord()) {

			if(!isWIF && tableType.equals("") && financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
				//Fetch Existing data before Modification
				
				FinScheduleData oldFinSchdData = null;
				if(financeDetail.getFinScheduleData().getFinanceMain().isScheduleRegenerated()){
					oldFinSchdData = getFinSchDataByFinRef(financeDetail.getFinScheduleData().getFinReference(), "", -1);
					oldFinSchdData.setFinReference(financeDetail.getFinScheduleData().getFinReference());
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
					listSave(oldFinSchdData, "_Log", false, logKey);
				}
			}

			listDeletion(financeDetail.getFinScheduleData(), tableType, isWIF);
			listSave(financeDetail.getFinScheduleData(), tableType, isWIF, 0);
			saveFeeChargeList(financeDetail.getFinScheduleData(), isWIF,tableType);
		} else {
			listSave(financeDetail.getFinScheduleData(), tableType, isWIF, 0);
			saveFeeChargeList(financeDetail.getFinScheduleData(), isWIF,tableType);
		}
		
		// Save Finance Step Policy Details
		//=======================================
		if (isWIF || financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
			getFinanceStepDetailDAO().deleteList(finReference, isWIF, tableType);
			saveStepDetailList(financeDetail.getFinScheduleData(), isWIF, tableType);
		}

		if(!isWIF){

			//Finance Eligibility Rule Details
			//=======================================
			getEligibilityDetailService().saveOrUpdate(financeDetail);

			// Finance Scoring Module Details List Saving 
			//=======================================
			getScoringDetailService().saveOrUpdate(financeDetail);
		}

		// Save asset details
		//=======================================
		if(!isWIF){
			
			String auditTranType = auditHeader.getAuditTranType();
			if (financeDetail.getCarLoanDetail() != null) {
				auditDetails.add(getCarLoanDetailService().saveOrUpdate(financeDetail.getCarLoanDetail(), tableType, auditTranType));
			}

			if(financeDetail.getEducationalLoan() != null) {
				auditDetails.addAll(getEducationalLoanService().saveOrUpdate(financeDetail, tableType, auditTranType));
			}

			if (financeDetail.getHomeLoanDetail() != null) {
				auditDetails.add(getHomeLoanDetailService().saveOrUpdate(financeDetail.getHomeLoanDetail(), tableType, auditTranType));
			}

			if (financeDetail.getMortgageLoanDetail() != null) {
				auditDetails.add(getMortgageLoanDetailService().saveOrUpdate(financeDetail.getMortgageLoanDetail(), tableType, auditTranType));
			}

			// Save GOODS Details
			if (financeDetail.getGoodsLoanDetails() != null && !financeDetail.getGoodsLoanDetails().isEmpty()) {
				auditDetails.addAll(getGoodsLoanDetailService().saveOrUpdate(financeDetail.getGoodsLoanDetails(), tableType, auditTranType));
			}
			// Save General GOODS Details
			if (financeDetail.getGenGoodsLoanDetails() != null && !financeDetail.getGenGoodsLoanDetails().isEmpty()) {
				auditDetails.addAll(getGenGoodsLoanDetailService().saveOrUpdate(financeDetail.getGenGoodsLoanDetails(), tableType, auditTranType));
			}
			// Save Fleet Vehicle Details
			if (financeDetail.getVehicleLoanDetails() != null && !financeDetail.getVehicleLoanDetails().isEmpty()) {
				auditDetails.addAll(getCarLoanDetailService().saveOrUpdate(financeDetail.getVehicleLoanDetails(), tableType, auditTranType));
			}
			
			// Save Commidity Header & Details
			CommidityLoanHeader commidityLoanHeader = financeDetail.getCommidityLoanHeader();
			if (commidityLoanHeader != null) {
				commidityLoanHeader.setCommidityLoanDetails(financeDetail.getCommidityLoanDetails());
				auditDetails.addAll(getCommidityLoanDetailService().saveOrUpdate(commidityLoanHeader, tableType, auditTranType));
			}

			// set Sharing Details Audit
			if (financeDetail.getSharesDetails() != null  && !financeDetail.getSharesDetails().isEmpty()) {
				auditDetails.addAll(getSharesDetailService().saveOrUpdate(financeDetail, tableType));
			}

			// Save Document Details
			if (financeDetail.getDocumentDetailsList() != null
					&& financeDetail.getDocumentDetailsList().size() > 0) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("DocumentDetails");
				details = processingDocumentDetailsList(details, tableType,
						financeMain.getFinReference(),financeDetail.getFinScheduleData().getFinanceMain());
				auditDetails.addAll(details);
			}

			// set Finance Check List audit details to auditDetails
			if (financeDetail.getFinanceCheckList() != null && !financeDetail.getFinanceCheckList().isEmpty()) {
				auditDetails.addAll(getCheckListDetailService().saveOrUpdate(financeDetail, tableType));
			}

			// set contract Details Audit
			if (financeDetail.getContractorAssetDetails() != null  && !financeDetail.getContractorAssetDetails().isEmpty()) {
				auditDetails.addAll(getContractorAssetDetailService().saveOrUpdate(finReference, financeDetail.getContractorAssetDetails(), tableType, auditTranType));
			}

			// set Guarantor Details Audit
			if (financeDetail.getGurantorsDetailList() != null && !financeDetail.getGurantorsDetailList().isEmpty()) {
				auditDetails.addAll(getGuarantorDetailService().saveOrUpdate(financeDetail.getGurantorsDetailList(), tableType, auditTranType));
			}

			// set JountAccount Details Audit
			if (financeDetail.getJountAccountDetailList() != null && !financeDetail.getJountAccountDetailList().isEmpty()) {
				auditDetails.addAll(getJointAccountDetailService().saveOrUpdate(financeDetail.getJountAccountDetailList(), tableType, auditTranType));
			}

			//Additional Field Details Save / Update
			doSaveAddlFieldDetails(financeDetail, tableType);

		}

		if(!isWIF){
			String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), excludeFields);
			auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0],
					fields[1], financeMain.getBefImage(), financeMain));

			auditHeader.setAuditDetails(auditDetails);
			getAuditHeaderDAO().addAudit(auditHeader);
		}

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param FinanceDetail
	 *            (financeDetail)
	 * @return financeDetail
	 */
	@Override
	public FinanceDetail refresh(FinanceDetail financeDetail) {
		logger.debug("Entering");
		getFinanceMainDAO().refresh(financeDetail.getFinScheduleData().getFinanceMain());
		getFinanceMainDAO().initialize(financeDetail.getFinScheduleData().getFinanceMain());
		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * FinanceMain by using FinanceMainDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtFinanceMain by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader, boolean isWIF) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "delete", isWIF);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String productCode = StringUtils.trimToEmpty(financeDetail.getFinScheduleData().getFinanceType().getFinCategory());

		//Finance SubChild List And Reference Details List Deletion
		listDeletion(financeDetail.getFinScheduleData(), "", isWIF);

		if (!isWIF) {
			//Additional Field Details Deletion
			doDeleteAddlFieldDetails(financeDetail, "");
		}

		//Indicative Term Sheet Details deletion on WIF FinanceMain Deletion 
		if(isWIF && financeDetail.getIndicativeTermDetail() != null){
			getIndicativeTermDetailDAO().delete(financeDetail.getIndicativeTermDetail(), "", true);
		}

		if (!isWIF) {
			auditDetails.addAll(assetDeletion(financeDetail, "", auditHeader.getAuditTranType()));	
			auditDetails.addAll(jointGuarantorDeletion(financeDetail, "", auditHeader.getAuditTranType()));
			auditDetails.addAll(getCheckListDetailService().delete(financeDetail, "", auditHeader.getAuditTranType()));
		}
		
		//Delete Finance Premium Details 
		if(productCode.equals(PennantConstants.FINANCE_PRODUCT_SUKUK)){
			getFinancePremiumDetailDAO().delete(financeDetail.getPremiumDetail(), "");
		}
		
		//Finance Deletion
		getFinanceMainDAO().delete(financeMain, "", isWIF);
		
		//Step Details Deletion
		getFinanceStepDetailDAO().deleteList(financeMain.getFinReference(),isWIF ,"");
		
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), excludeFields);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], financeMain.getBefImage(), financeMain));
		auditHeader.setAuditDetails(auditDetails);
		if(!isWIF){
		getAuditHeaderDAO().addAudit(auditHeader);
		}
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getFinanceMainDAO().delete with
	 * parameters financeMain,"" b) NEW Add new record in to main table by using getFinanceMainDAO().save with
	 * parameters financeMain,"" c) EDIT Update record in the main table by using getFinanceMainDAO().update with
	 * parameters financeMain,"" 3) Delete the record from the workFlow table by using getFinanceMainDAO().delete with
	 * parameters financeMain,"_Temp" 4) Audit the record in to AuditHeader and AdtFinanceMain by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtFinanceMain by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws AccountNotFoundException
	 */
	@Override
	public AuditHeader doApprove(AuditHeader aAuditHeader, boolean isWIF) throws AccountNotFoundException {
		logger.debug("Entering");

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		aAuditHeader = businessValidation(aAuditHeader, "doApprove", isWIF);
		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);
		
		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		Date curBDay = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);

		//Execute Accounting Details Process
		//=======================================
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String productCode = financeDetail.getFinScheduleData().getFinanceType().getFinCategory();
		if(financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
			financeMain.setFinApprovedDate(curBDay);
			
			if (financeMain.getFinContractDate() == null) {
				financeMain.setFinContractDate(financeMain.getFinStartDate());
			}
			
			if (financeMain.getFeeChargeAmt() == null) {
				financeMain.setFeeChargeAmt(BigDecimal.ZERO);
			}
		}	
		if (!isWIF) {
			auditHeader = executeAccountingProcess(auditHeader, curBDay);
        }
		if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
			return auditHeader;
		}
		
		//Re-Prepare of Finance Overdue Details with Existing Data
		if(StringUtils.trimToEmpty(financeMain.getRcdMaintainSts()).equals(PennantConstants.CHGFRQ)){

			List<FinanceScheduleDetail> schdList = financeDetail.getFinScheduleData().getFinanceScheduleDetails();
			for (int i = 1; i < schdList.size(); i++) {

				FinanceScheduleDetail curSchd = schdList.get(i);
				if(!(curSchd.isRepayOnSchDate() ||
						(curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0))){
					continue;
				}

				if(curSchd.isDeferedPay()){
					continue;
				}

				if(curSchd.isSchPftPaid() || curSchd.isSchPriPaid()){
					continue;
				}

				if(curSchd.getSchDate().compareTo(curBDay) > 0){
					continue;
				}

				FinRepayQueue finRepayQueue = new FinRepayQueue();
				finRepayQueue.setFinReference(financeMain.getFinReference());
				finRepayQueue.setBranch(financeMain.getFinBranch());
				finRepayQueue.setFinType(financeMain.getFinType());
				finRepayQueue.setCustomerID(financeMain.getCustID());
				finRepayQueue.setRpyDate(curSchd.getSchDate());
				finRepayQueue.setFinPriority(9999);
				finRepayQueue.setFinRpyFor("S");
				finRepayQueue.setSchdPft(curSchd.getProfitSchd());
				finRepayQueue.setSchdPri(curSchd.getPrincipalSchd());
				finRepayQueue.setSchdPftPaid(curSchd.getSchdPftPaid());
				finRepayQueue.setSchdPriPaid(curSchd.getSchdPriPaid());
				finRepayQueue.setSchdPftBal(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()));
				finRepayQueue.setSchdPriBal(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()));
				finRepayQueue.setSchdIsPftPaid(false);
				finRepayQueue.setSchdIsPriPaid(false);

				try {
					getRecoveryPostingsUtil().overDueDetailPreparation(finRepayQueue, financeMain.getProfitDaysBasis(), curBDay, false, false);
				} catch (IllegalAccessException e) {
					logger.error(e.getMessage());
				} catch (InvocationTargetException e) {
					logger.error(e.getMessage());
				}
			}
			
			//Recalculate Status of Finance using Overdue
			String curFinStatus = getCustomerStatusCodeDAO().getFinanceStatus(financeMain.getFinReference(), true);
			financeMain.setFinStatus(curFinStatus);

			//Suspense Process Check after Overdue Details Recalculation
			suspenseCheckProcess(financeMain, PennantConstants.CHGFRQ, curBDay, 
					financeDetail.getFinScheduleData().getFinanceType().isAllowRIAInvestment(), financeMain.getFinStatus(), 0);
			
		}

		if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getFinanceMainDAO().delete(financeMain, "", isWIF);
			listDeletion(financeDetail.getFinScheduleData(), "", isWIF);
			
			//Delete Finance Premium Details 
			if(productCode.equals(PennantConstants.FINANCE_PRODUCT_SUKUK)){
				getFinancePremiumDetailDAO().delete(financeDetail.getPremiumDetail(), "");
			}
			
			//Step Details Deletion
			getFinanceStepDetailDAO().deleteList(financeMain.getFinReference(),isWIF ,"_Temp");

			if(!isWIF){
				//Additional Field Details Deletion
				//=======================================
				doDeleteAddlFieldDetails(financeDetail, "");
				auditDetails.addAll(assetDeletion(financeDetail, "", tranType));
				auditDetails.addAll(jointGuarantorDeletion(financeDetail, "", tranType));
				auditDetails.addAll(getCheckListDetailService().delete(financeDetail, "", auditHeader.getAuditTranType()));
				auditDetails.addAll(getListAuditDetails(listDeletion_FinContributor(financeDetail, "", auditHeader.getAuditTranType())));
			}

		} else {

			financeMain.setRcdMaintainSts("");
			financeMain.setRoleCode("");
			financeMain.setNextRoleCode("");
			financeMain.setTaskId("");
			financeMain.setNextTaskId("");
			financeMain.setWorkflowId(0);

			if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				
				tranType = PennantConstants.TRAN_ADD;
				financeMain.setRecordType("");
				getFinanceMainDAO().save(financeMain, "", isWIF);

				//Schedule Details
				//=======================================
				listSave(financeDetail.getFinScheduleData(), "", isWIF, 0);
				
				// Save Finance Step Policy Details
				//=======================================
				saveStepDetailList(financeDetail.getFinScheduleData(), isWIF, "");

				//Fee Charge Details
				//=======================================
				saveFeeChargeList(financeDetail.getFinScheduleData(),isWIF, "");

				//Indicative Term Sheet Details Maintenance
				//=======================================
				IndicativeTermDetail termDetail = financeDetail.getIndicativeTermDetail();
				if(termDetail != null){
					termDetail.setFinReference(financeMain.getFinReference());
					getIndicativeTermDetailDAO().save(termDetail, "", true);
					getIndicativeTermDetailDAO().delete(termDetail, "_Temp", true);
				}
				
				//Save Finance Premium Details
				//=======================================
				if(productCode.equals(PennantConstants.FINANCE_PRODUCT_SUKUK)){
					getFinancePremiumDetailDAO().save(financeDetail.getPremiumDetail(), "");
				}
				
				// Save Contributor Header Details
				//=======================================
				if (financeDetail.getFinContributorHeader() != null) {

					FinContributorHeader contributorHeader = financeDetail.getFinContributorHeader();
					String[] fields = PennantJavaUtil.getFieldDetails(new FinContributorHeader());
					getFinContributorHeaderDAO().save(contributorHeader, "");
					auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0],
							fields[1], financeDetail.getFinContributorHeader().getBefImage(),
							financeDetail.getFinContributorHeader()));

					// Save Contributor Header Details
					//=======================================
					if (contributorHeader.getContributorDetailList() != null
							&& contributorHeader.getContributorDetailList().size() > 0) {
						List<AuditDetail> details = financeDetail.getAuditDetailMap().get("Contributor");
						details = processingContributorList(details, "", contributorHeader.getFinReference());
						auditDetails.addAll(details);
					}
				}
				
			} else {

				tranType = PennantConstants.TRAN_UPD;
				financeMain.setRecordType("");
				getFinanceMainDAO().update(financeMain, "", isWIF);

				if(!isWIF){
					
					//Fetch Existing data before Modification
					FinScheduleData oldFinSchdData = null;
					if(financeDetail.getFinScheduleData().getFinanceMain().isScheduleRegenerated()){
						oldFinSchdData = getFinSchDataByFinRef(financeDetail.getFinScheduleData().getFinReference(), "", -1);
						oldFinSchdData.setFinReference(financeDetail.getFinScheduleData().getFinReference());
					}

					//Create log entry for Action for Schedule Modification
					//=======================================
					FinLogEntryDetail entryDetail = new FinLogEntryDetail();
					entryDetail.setFinReference(financeDetail.getFinScheduleData().getFinReference());
					entryDetail.setEventAction(financeDetail.getAccountingEventCode());
					entryDetail.setSchdlRecal(financeDetail.getFinScheduleData().getFinanceMain().isScheduleRegenerated());
					entryDetail.setPostDate(curBDay);
					entryDetail.setReversalCompleted(false);
					long logKey = getFinLogEntryDetailDAO().save(entryDetail);

					//Save Schedule Details For Future Modifications
					if(financeDetail.getFinScheduleData().getFinanceMain().isScheduleRegenerated()){
						listSave(oldFinSchdData, "_Log", isWIF, logKey);
					}
				}
				
				// ScheduleDetails delete and save
				//=======================================
				listDeletion(financeDetail.getFinScheduleData(), "", isWIF);
				listSave(financeDetail.getFinScheduleData(), "", isWIF, 0);

				//Fee Charge Details
				//=======================================
				saveFeeChargeList(financeDetail.getFinScheduleData(),isWIF, "");
				
				//Update Finance Premium Details
				//=======================================
				if(productCode.equals(PennantConstants.FINANCE_PRODUCT_SUKUK)){
					getFinancePremiumDetailDAO().update(financeDetail.getPremiumDetail(), "");
				}
				
			}

			if (!financeDetail.isExtSource() && !isWIF) {

				// Car Loan Detail
				if (financeDetail.getCarLoanDetail() != null) {					
					auditDetails.add(getCarLoanDetailService().doApprove(financeDetail.getCarLoanDetail(), "", tranType));
				}

				// Education Loan Detail
				if (financeDetail.getEducationalLoan() != null) {
					auditDetails.addAll(getEducationalLoanService().doApprove(financeDetail, "", auditHeader.getAuditTranType()));
				}

				//Home Loan Detail
				if (financeDetail.getHomeLoanDetail() != null) {
					auditDetails.add(getHomeLoanDetailService().doApprove(financeDetail.getHomeLoanDetail(), "", tranType));
				}

				//mortgage laon Detail
				if (financeDetail.getMortgageLoanDetail() != null) {
					auditDetails.add(getMortgageLoanDetailService().doApprove(financeDetail.getMortgageLoanDetail(), "", tranType));
				}

				// Goods Loan Details
				if (financeDetail.getGoodsLoanDetails() != null && !financeDetail.getGoodsLoanDetails().isEmpty()) {
					auditDetails.addAll(getGoodsLoanDetailService().doApprove(financeDetail.getGoodsLoanDetails() , "", tranType));
				}

				// General Goods loan Details
				if (financeDetail.getGenGoodsLoanDetails() != null && !financeDetail.getGenGoodsLoanDetails().isEmpty()) {
					auditDetails.addAll(getGenGoodsLoanDetailService().doApprove(financeDetail.getGenGoodsLoanDetails() , "", tranType));
					}

				// Fleet Vehicle Loan Details
				if (financeDetail.getVehicleLoanDetails() != null && !financeDetail.getVehicleLoanDetails().isEmpty()) {
					auditDetails.addAll(getCarLoanDetailService().doApprove(financeDetail.getVehicleLoanDetails() , "", tranType));
				}
				
				// Commidity Header & Details
				CommidityLoanHeader commidityLoanHeader = financeDetail.getCommidityLoanHeader();
				if (commidityLoanHeader != null) {
					commidityLoanHeader.setCommidityLoanDetails(financeDetail.getCommidityLoanDetails());
					auditDetails.addAll(getCommidityLoanDetailService().doApprove(commidityLoanHeader, "", tranType));
				}

				// Share Details
				if (financeDetail.getSharesDetails() != null  && financeDetail.getSharesDetails().size() > 0) {
					auditDetails.addAll(getSharesDetailService().doApprove(financeDetail, ""));
				}

				// Svae Contractor Detaila
				List<ContractorAssetDetail> contractorAssetDetails = financeDetail.getContractorAssetDetails();
				if (contractorAssetDetails != null && !contractorAssetDetails.isEmpty()) {
					auditDetails.addAll(getContractorAssetDetailService().doApprove(contractorAssetDetails, "", tranType));
				}

				// Save Document Details
				if (financeDetail.getDocumentDetailsList() != null && financeDetail.getDocumentDetailsList().size() > 0) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("DocumentDetails");
					details = processingDocumentDetailsList(details, "",
							financeMain.getFinReference(),financeDetail.getFinScheduleData().getFinanceMain());
					auditDetails.addAll(details);
					listDocDeletion(financeDetail, "_Temp");
				}

				// set Check list details Audit
				if (financeDetail.getFinanceCheckList() != null && !financeDetail.getFinanceCheckList().isEmpty()) {					
					auditDetails.addAll(getCheckListDetailService().doApprove(financeDetail, ""));
				}

				// set the Audit Details & Save / Update Guarantor Details
				if (financeDetail.getGurantorsDetailList() != null  && !financeDetail.getGurantorsDetailList().isEmpty()) {
					auditDetails.addAll(getGuarantorDetailService().doApprove(financeDetail.getGurantorsDetailList(), "", tranType));
				}

				// set the Audit Details & Save / Update JountAccount Details 
				if (financeDetail.getJountAccountDetailList() != null && !financeDetail.getJountAccountDetailList().isEmpty()) {
					auditDetails.addAll(getJointAccountDetailService().doApprove(financeDetail.getJountAccountDetailList(), "", tranType));
				}

				//Additional Field Details Save / Update
				doSaveAddlFieldDetails(financeDetail, "");
			}

		}

		List<AuditDetail> auditDetailList = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), excludeFields);

		if (!financeDetail.isExtSource()) {

			// ScheduleDetails delete
			//=======================================
			listDeletion(financeDetail.getFinScheduleData(), "_Temp", isWIF);

			if(!isWIF){
				//Additional Field Details Deletion in _Temp Table
				//=======================================
				doDeleteAddlFieldDetails(financeDetail, "_Temp");

				auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
				auditDetailList.addAll(assetDeletion(financeDetail, "_Temp", auditHeader.getAuditTranType()));
				auditDetailList.addAll(getCheckListDetailService().delete(financeDetail, "_Temp", auditHeader.getAuditTranType()));
				auditDetailList.addAll(getListAuditDetails(listDeletion_FinContributor(financeDetail, "_Temp", auditHeader.getAuditTranType())));
				auditDetailList.addAll(getContractorAssetDetailService().delete(financeDetail.getContractorAssetDetails(), "_Temp", auditHeader.getAuditTranType()));
			}
			
			//Step Details Deletion
			getFinanceStepDetailDAO().deleteList(financeMain.getFinReference(),isWIF ,"_Temp");
			
			//Delete Finance Premium Details
			//=======================================
			if(productCode.equals(PennantConstants.FINANCE_PRODUCT_SUKUK)){
				getFinancePremiumDetailDAO().delete(financeDetail.getPremiumDetail(), "_Temp");
			}
			
			getFinanceMainDAO().delete(financeMain, "_Temp", isWIF);
			
			FinanceDetail tempfinanceDetail = (FinanceDetail) aAuditHeader.getAuditDetail().getModelData();
			FinanceMain tempfinanceMain = tempfinanceDetail.getFinScheduleData().getFinanceMain();
			auditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1], tempfinanceMain.getBefImage(), tempfinanceMain));
			auditHeader.setAuditDetails(auditDetailList);

			// Adding audit as deleted from TEMP table
			if(!isWIF){
				getAuditHeaderDAO().addAudit(auditHeader);
			}
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0],
				fields[1], financeMain.getBefImage(), financeMain));
		auditHeader.setAuditDetails(getListAuditDetails(auditDetails));

		// Adding audit as Insert/Update/deleted into main table
		if(!isWIF){
			getAuditHeaderDAO().addAudit(auditHeader);
		}

		//Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(financeDetail);

		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getFinanceMainDAO().delete with parameters financeMain,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtFinanceMain by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader, boolean isWIF) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doReject", isWIF);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String productCode = StringUtils.trimToEmpty(financeDetail.getFinScheduleData().getFinanceType().getFinCategory());

		getFinanceMainDAO().saveRejectFinanceDetails(financeMain);

		// ScheduleDetails deletion
		listDeletion(financeDetail.getFinScheduleData(), "_TEMP", isWIF);
		
		//Document Details 
		getDocumentDetailsDAO().deleteList(financeDetail.getDocumentDetailsList(), "_TEMP");

		if(!isWIF){
			//Additional Field Details Deletion
			doDeleteAddlFieldDetails(financeDetail, "_TEMP");
		}
		
		//Indicative Term Sheet Details Maintenance
		//=======================================
		IndicativeTermDetail termDetail = financeDetail.getIndicativeTermDetail();
		if(termDetail != null){
			termDetail.setFinReference(financeMain.getFinReference());
			getIndicativeTermDetailDAO().delete(termDetail, "_TEMP", true);
		}
		
		//Delete Finance Premium Details 
		if(productCode.equals(PennantConstants.FINANCE_PRODUCT_SUKUK)){
			getFinancePremiumDetailDAO().delete(financeDetail.getPremiumDetail(), "");
		}

		getFinanceMainDAO().delete(financeMain, "_TEMP", isWIF);
		
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), excludeFields);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], financeMain.getBefImage(), financeMain));

		//Step Details
		getFinanceStepDetailDAO().deleteList(financeMain.getFinReference(),isWIF ,"_Temp");
		
		// Asset deletion
		if (!isWIF) {
			
			auditDetails.addAll(assetDeletion(financeDetail, "_TEMP", auditHeader.getAuditTranType()));
			auditDetails.addAll(jointGuarantorDeletion(financeDetail, "_TEMP", auditHeader.getAuditTranType()));
			auditDetails.addAll(getCheckListDetailService().delete(financeDetail, "_TEMP", auditHeader.getAuditTranType()));
			// auditDetails.addAll(getListAuditDetails(listDeletion_FinAgr(financeDetail, "_TEMP",  auditHeader.getAuditTranType())));
			auditDetails.addAll(getListAuditDetails(listDeletion_FinContributor(financeDetail, "_TEMP", auditHeader.getAuditTranType())));
			auditHeader.setAuditDetails(auditDetails);
		}
		
		if(!isWIF){
			getAuditHeaderDAO().addAudit(auditHeader);
		}

		//Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(financeDetail);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Method for Update Finance into BlackList Condition (WRITEOFF)
	 */
	@Override
	public void updateFinBlackListStatus(String finReference) {
		getFinanceMainDAO().updateFinBlackListStatus(finReference);//FIXME === Add writeoff field rather than BlockList
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

		String auditTranType = auditHeader.getAuditTranType();
		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String usrLanguage = financeMain.getUserDetails() .getUsrLanguage();

		if (!financeDetail.isExtSource()) {
			
			String rcdType = financeMain.getRecordType();
			if (!isWIF && !financeDetail.isLovDescIsQDE() && rcdType.equals(PennantConstants.RECORD_TYPE_NEW)) {

				String assetCode = financeDetail.getFinScheduleData().getFinanceType().getLovDescAssetCodeName();

				if (assetCode.equalsIgnoreCase(PennantConstants.CARLOAN)) {
					financeDetail.getCarLoanDetail().setWorkflowId(financeMain.getWorkflowId());
					auditDetails.add(getCarLoanDetailService().validate(financeDetail.getCarLoanDetail(), method, auditTranType, usrLanguage));
				}

				if (assetCode.equalsIgnoreCase(PennantConstants.EDUCATON)) {
					List<AuditDetail> details;
					details = getEducationalLoanService().validate(financeDetail, method, usrLanguage);
					auditDetails.addAll(details);
				}

				if (assetCode.equalsIgnoreCase(PennantConstants.HOMELOAN)) {
					financeDetail.getHomeLoanDetail().setWorkflowId(financeMain.getWorkflowId());
					auditDetails.add(getHomeLoanDetailService().validate(financeDetail.getHomeLoanDetail(), method, auditTranType, usrLanguage));
				}

				if (assetCode.equalsIgnoreCase(PennantConstants.MORTLOAN)) {
					financeDetail.getMortgageLoanDetail().setWorkflowId(financeMain.getWorkflowId());
					auditDetails.add(getMortgageLoanDetailService().validate(financeDetail.getMortgageLoanDetail(), method, auditTranType, usrLanguage));
				}

				List<GoodsLoanDetail>  goodLoanDetailList = financeDetail.getGoodsLoanDetails();
				if (goodLoanDetailList != null && !goodLoanDetailList.isEmpty()) {
					auditDetails.addAll(getGoodsLoanDetailService().validate(goodLoanDetailList, financeMain.getWorkflowId(), method, auditTranType, usrLanguage));
				}

				List<GenGoodsLoanDetail>  genGoodLoanDetailList = financeDetail.getGenGoodsLoanDetails();
				if (genGoodLoanDetailList != null && !genGoodLoanDetailList.isEmpty()) {
					auditDetails.addAll(getGenGoodsLoanDetailService().validate(genGoodLoanDetailList, financeMain.getWorkflowId(), method, auditTranType, usrLanguage));
				}

				List<CarLoanDetail>  vehicleLoanDetailList = financeDetail.getVehicleLoanDetails();
				if (vehicleLoanDetailList != null && !vehicleLoanDetailList.isEmpty()) {
					auditDetails.addAll(getCarLoanDetailService().validate(vehicleLoanDetailList, financeMain.getWorkflowId(), method, auditTranType, usrLanguage));
				}
				
				List<CommidityLoanDetail>  commidityLoanDetails = financeDetail.getCommidityLoanDetails();
				if (commidityLoanDetails != null && !commidityLoanDetails.isEmpty()) {
					auditDetails.addAll(getCommidityLoanDetailService().validate(commidityLoanDetails, financeMain.getWorkflowId(), method, auditTranType, usrLanguage));
				}

				List<ContractorAssetDetail> contractorAssetDetails = financeDetail.getContractorAssetDetails();
				if (contractorAssetDetails != null  && !contractorAssetDetails.isEmpty()) {
					auditDetails.addAll(getContractorAssetDetailService().validate(financeDetail, method, usrLanguage));
				}
			}

			//Finance Check List Details
			List<FinanceCheckListReference> financeCheckList = financeDetail.getFinanceCheckList();
			if (financeCheckList != null && !financeCheckList.isEmpty()) {
				List<AuditDetail> auditDetailList;
				auditDetailList = getCheckListDetailService().validate(financeDetail, method, usrLanguage);
				auditDetails.addAll(auditDetailList);
			}

			//Guarantor Details Validation
			List<GuarantorDetail> gurantorsDetailList = financeDetail.getGurantorsDetailList();
			if (gurantorsDetailList != null  && !gurantorsDetailList.isEmpty()) {
				auditDetails.addAll(getGuarantorDetailService().validate(gurantorsDetailList, financeMain.getWorkflowId(), method, auditTranType, usrLanguage));
			}

			//Joint Account Details Validation
			List<JointAccountDetail> jountAccountDetailList  = financeDetail.getJountAccountDetailList();
			if (jountAccountDetailList != null && !jountAccountDetailList.isEmpty()) {
				auditDetails.addAll(getJointAccountDetailService().validate(jountAccountDetailList, financeMain.getWorkflowId(), method, auditTranType, usrLanguage));
			}

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
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

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

		if (financeMain.isNew()) { // for New record or new record into work flow

			if (!financeMain.isWorkflow()) {// With out Work flow only new
				// records
				if (befFinanceMain != null) { // Record Already Exists in the
					// table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
							PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
					// records type is new
					if (befFinanceMain != null || tempFinanceMain != null) { // if
						// records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befFinanceMain == null || tempFinanceMain != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!financeMain.isWorkflow()) { // With out Work flow for update
				// and delete

				if (befFinanceMain == null) { // if records not exists in the
					// main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
							PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldFinanceMain != null && !oldFinanceMain.getLastMntOn()
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

				if (tempFinanceMain != null && oldFinanceMain != null
						&& !oldFinanceMain.getLastMntOn().equals(tempFinanceMain.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
							PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		if (StringUtils.trimToEmpty(financeMain.getRecordStatus()).startsWith("Submit")) {

			// Eligibility
			getEligibilityDetailService().validate(financeDetail.getElgRuleList(), auditDetail, errParm, valueParm, usrLanguage);

			// Scoring 
			getScoringDetailService().validate(financeDetail, auditDetail, errParm, valueParm, usrLanguage);

		}

		//Fee Details Waiver Validations
		if(financeDetail.getFinScheduleData().getFeeRules() != null && 
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
		}
		
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if (StringUtils.trimToEmpty(method).equals("doApprove") || !financeMain.isWorkflow()) {
			financeMain.setBefImage(befFinanceMain);
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
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		String auditTranType = "";
		if (method.equals("saveOrUpdate") || method.equals("doApprove") || method.equals("doReject")) {
			if (financeMain.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		if (!financeDetail.isExtSource()) {
			
			//Finance Contribution Details
			if (financeDetail.getFinContributorHeader() != null
					&& financeDetail.getFinContributorHeader().getContributorDetailList() != null
					&& financeDetail.getFinContributorHeader().getContributorDetailList().size() > 0) {
				auditDetailMap.put( "Contributor", setContributorAuditData(financeDetail.getFinContributorHeader(), auditTranType, method));
				auditDetails.addAll(auditDetailMap.get("Contributor"));
			}

			//Finance Document Details
			if (financeDetail.getDocumentDetailsList() != null && financeDetail.getDocumentDetailsList().size() > 0) {
				auditDetailMap.put("DocumentDetails", setDocumentDetailsAuditData(financeDetail, auditTranType, method));
				auditDetails.addAll(auditDetailMap.get("DocumentDetails"));
			}

			//Finance Asset Details
			if (financeDetail.getEducationalLoan() != null) {
				List<AuditDetail> auditDetail;
				auditDetail = getEducationalLoanService().getAuditDetail(auditDetailMap, financeDetail, auditTranType, method);
				auditDetails.addAll(auditDetail);
			}

			//Shares
			if (financeDetail.getSharesDetails() != null && !financeDetail.getSharesDetails().isEmpty()) {
				List<AuditDetail> auditDetail;
				auditDetail = getSharesDetailService().getAuditDetail(auditDetailMap, financeDetail, auditTranType, method);
				auditDetails.addAll(auditDetail);
			}


			//Finance Check List Details
			List<FinanceCheckListReference> financeCheckList = financeDetail.getFinanceCheckList();

			if (StringUtils.equals(method, "saveOrUpdate")) {
				if (financeCheckList != null && !financeCheckList.isEmpty()) {
					List<AuditDetail> auditDetail;
					auditDetail = getCheckListDetailService().getAuditDetail(auditDetailMap, financeDetail, auditTranType, method);
					auditDetails.addAll(auditDetail);
				}
			} else {
				String tableType = "_Temp";
				if (financeDetail.getFinScheduleData().getFinanceMain().getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
					tableType = "";
				}

				String finReference = financeDetail.getFinScheduleData().getFinReference();
				financeCheckList = getCheckListDetailService().getCheckListByFinRef(finReference, tableType);				
				financeDetail.setFinanceCheckList(financeCheckList);

				if (financeCheckList != null && !financeCheckList.isEmpty()) {
					List<AuditDetail> auditDetail;
					auditDetail = getCheckListDetailService().getAuditDetail(auditDetailMap, financeDetail, auditTranType, method);
					auditDetails.addAll(auditDetail);
				}
			}

			//Contract Details
			List<ContractorAssetDetail> contractorAssetDetails = financeDetail.getContractorAssetDetails();
			if (contractorAssetDetails != null && contractorAssetDetails.isEmpty()) {
				List<AuditDetail> auditDetail;
				auditDetail = getContractorAssetDetailService().getAuditDetail(contractorAssetDetails, financeMain, auditTranType, method);
				auditDetailMap.put("ContractorAssetDetail", auditDetails);
				auditDetails.addAll(auditDetail);
			}
		}

		financeDetail.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(financeDetail);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug("Leaving ");

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
			FinanceDetail financeDetail = getFinSchdDetailById(rateChangeFinance.getFinReference(), "_AView", true);

			//Reset Before Image for Auditing
			FinanceDetail befImage = new FinanceDetail();
			BeanUtils.copyProperties(financeDetail, befImage);
			financeDetail.setBefImage(befImage);

			FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

			financeMain.setEventFromDate(rateChangeFinance.getLovDescEventFromDate());
			financeMain.setEventToDate(rateChangeFinance.getLovDescEventToDate());
			financeMain.setRecalType(recalType);

			//Schedule Details Log Maintenance
			final List<FinanceScheduleDetail> oldScheduleDetails = financeDetail.getFinScheduleData().getFinanceScheduleDetails();
			getFinanceScheduleDetailDAO().saveList(oldScheduleDetails, "_Log", false);

			//Schedule Re-calculation based on Applied parameters
			financeDetail.setFinScheduleData(ScheduleCalculator.changeRate(financeDetail.getFinScheduleData(), "", "", BigDecimal.ZERO,
					rateChange == null ? BigDecimal.ZERO : rateChange, true,null));

			//Record proceed through WorkFlow defined Process
			AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_UPD, 1, financeDetail.getBefImage(), financeDetail);
			AuditHeader auditHeader = new AuditHeader(financeDetail.getFinScheduleData().getFinReference(), null, null, null, auditDetail,
					financeDetail.getUserDetails(), new HashMap<String, ArrayList<ErrorDetails>>());

			//Changed Finance Save in Database
			saveOrUpdate(auditHeader, false);
		}

		logger.debug("Leaving");
		return true;
	}

	/**
	 * Method for Processing Bulk Finance for Deferment Process
	 * 
	 * @throws AccountNotFoundException
	 */
	@Override
	public boolean bulkDefermentChanges(List<BulkDefermentChange> defermentChangeFinances,
			String recalType, boolean excludeDeferment, String addTermAfter, Date calFromDate,
			Date calToDate) throws AccountNotFoundException {
		logger.debug("Entering");

		//Bulk Deferment Changes applied for fetched list
		for (BulkDefermentChange defermentFinance : defermentChangeFinances) {

			//Get Total Finance Details to particular Finance
			FinanceDetail financeDetail = getFinSchdDetailById(defermentFinance.getFinReference(), "_AView", true);

			//Reset Before Image for Auditing
			FinanceDetail befImage = new FinanceDetail();
			BeanUtils.copyProperties(financeDetail, befImage);
			financeDetail.setBefImage(befImage);

			FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

			financeMain.setEventFromDate(defermentFinance.getEventFromDate());
			financeMain.setEventToDate(defermentFinance.getEventFromDate());
			financeMain.setRecalType(recalType);
			financeMain.setExcludeDeferedDates(excludeDeferment);
			financeMain.setAddTermAfter(addTermAfter);
			financeMain.setRecalFromDate(calFromDate);
			financeMain.setRecalToDate(calToDate);

			//Schedule Details Log Maintenance
			final List<FinanceScheduleDetail> oldScheduleDetails = befImage.getFinScheduleData().getFinanceScheduleDetails();
			getFinanceScheduleDetailDAO().saveList(oldScheduleDetails, "_Log", false);

			//Schedule Re-calculation based on Applied parameters
			financeDetail.setFinScheduleData(ScheduleCalculator.addDeferment(financeDetail.getFinScheduleData()));

			//Record proceed through WorkFlow defined Process
			AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_UPD, 1, financeDetail.getBefImage(), financeDetail);
			AuditHeader auditHeader = new AuditHeader(financeDetail.getFinScheduleData().getFinReference(), null, null, null, auditDetail,
					financeDetail.getUserDetails(), new HashMap<String, ArrayList<ErrorDetails>>());

			//Changed Finance Save in Database
			saveOrUpdate(auditHeader, false);
		}
		logger.debug("Leaving");
		return false;
	}

	/**
	 * Method for Preparing Customer Eligibility Execution Data
	 * @param financeDetail
	 */
	private void doFillCustEligibilityData(FinanceDetail financeDetail) {
		logger.debug("Entering ");

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		Customer customer = getCustomerDAO().getCustomerByID(financeMain.getCustID(), "_AView");

		if (customer != null) {

			String productCode = financeDetail.getFinScheduleData().getFinanceType().getLovDescProductCodeName();

			//Current Finance Monthly Installment Calculation
			BigDecimal totalRepayAmount = financeMain.getTotalRepayAmt();
			int installmentMnts = DateUtility.getMonthsBetween(financeMain.getFinStartDate(), financeMain.getMaturityDate(), true);

			BigDecimal curFinRepayAmt = totalRepayAmount.divide(new BigDecimal(installmentMnts), 0, RoundingMode.HALF_DOWN);
			int months = DateUtility.getMonthsBetween(financeMain.getFinStartDate(), financeMain.getMaturityDate());

			//Customer Eligibility Amounts Calculation
			financeDetail.setCustomerEligibilityCheck(getCustEligibilityDetail(customer, productCode,
					financeMain.getFinCcy(), curFinRepayAmt, months, financeMain.getFinAmount(), financeMain.getCustDSR()));

			//Reset Customer Debt Service Ratio
			financeMain.setCustDSR(financeDetail.getCustomerEligibilityCheck().getDSCR());

			// Scoring object
			financeDetail.setCustomerScoringCheck(new CustomerScoringCheck());
			BeanUtils.copyProperties(financeDetail.getCustomerEligibilityCheck(), financeDetail.getCustomerScoringCheck());
		}
		logger.debug("Leaving ");
	}

	/**
	 * Method for Preparing Customer Eligibility Amount Details in Base(BHD) Currency
	 */
	@Override
	public CustomerEligibilityCheck getCustEligibilityDetail(Customer customer, String productCode,
			String finCcy, BigDecimal curFinRpyAmount, int months, BigDecimal finAmount,
			BigDecimal custDSR) {
		logger.debug("Entering");

		CustomerEligibilityCheck eligibilityCheck = new CustomerEligibilityCheck();
		if (customer != null) {

			// Eligibility object
			BeanUtils.copyProperties(customer, eligibilityCheck);
			int age = DateUtility.getYearsBetween(customer.getCustDOB(), DateUtility.today());
			eligibilityCheck.setCustAge(age);

			//Minor Age Calculation
			int minorAge = Integer.parseInt(SystemParameterDetails.getSystemParameterValue("MINOR_AGE").toString());
			if (age < minorAge) {
				eligibilityCheck.setCustIsMinor(true);
			} else {
				eligibilityCheck.setCustIsMinor(false);
			}

			Currency finCurrency = null;
			//Customer Total Income & Expense Conversion
			if (!StringUtils.trimToEmpty(SystemParameterDetails.getSystemParameterValue("APP_DFT_CURR").toString())
					.equals(customer.getCustBaseCcy())) {
				finCurrency = getCurrencyDAO().getCurrencyById(customer.getCustBaseCcy(), "");
				eligibilityCheck.setCustTotalIncome(calculateExchangeRate(customer.getCustTotalIncome(), finCurrency));
				eligibilityCheck.setCustTotalExpense(calculateExchangeRate(customer.getCustTotalExpense(), finCurrency));
			}

			if (months > 0) {
				eligibilityCheck.setTenure(new BigDecimal((months / 12) + "." + (months % 12)));
			}
			eligibilityCheck.setReqFinAmount(finAmount);

			Date curBussDate = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);
			eligibilityCheck.setBlackListExpPeriod(DateUtility.getMonthsBetween(curBussDate,
					customer.getCustBlackListDate()));

			eligibilityCheck.setCustCtgType(customer.getLovDescCustCtgType());
			eligibilityCheck.setReqProduct(productCode);

			//Currently
			if (curFinRpyAmount != null && curFinRpyAmount.compareTo(BigDecimal.ZERO) > 0) {
				if (!StringUtils.trimToEmpty(SystemParameterDetails.getSystemParameterValue("APP_DFT_CURR").toString())
						.equals(finCcy)) {

					if (finCurrency != null && finCurrency.getCcyCode().equals(finCcy)) {
						eligibilityCheck.setCurFinRepayAmt(calculateExchangeRate(curFinRpyAmount, finCurrency));
					} else {
						finCurrency = getCurrencyDAO().getCurrencyById(finCcy, "");
						eligibilityCheck.setCurFinRepayAmt(calculateExchangeRate(curFinRpyAmount, finCurrency));
					}
				} else {
					eligibilityCheck.setCurFinRepayAmt(curFinRpyAmount);
				}
			}

			//Finance Amount Calculations
			List<FinanceProfitDetail> financeProfitDetailsList = 
				getCustomerDAO().getCustFinAmtDetails(customer.getCustID(),eligibilityCheck);

			BigDecimal custFinAmount = BigDecimal.ZERO;
			BigDecimal custODAmount = BigDecimal.ZERO;

			for (FinanceProfitDetail financeProfitDetail : financeProfitDetailsList) {
				custFinAmount=custFinAmount.add(CalculationUtil.getConvertedAmount(financeProfitDetail.getFinCcy(), finCcy, 
						financeProfitDetail.getTotalPriBal()));
				custODAmount = custODAmount.add(CalculationUtil.getConvertedAmount(financeProfitDetail.getFinCcy(), finCcy, 
						financeProfitDetail.getODPrincipal()));
			}

			eligibilityCheck.setCustLiveFinAmount(custFinAmount);
			eligibilityCheck.setCustPastDueAmt(custODAmount);				

			//get Customer Designation if customer status is Employed
			eligibilityCheck.setCustEmpDesg(getCustomerDAO().getCustEmpDesg(customer.getCustID()));

			//get Customer Employee Allocation Type if customer status is Employed
			eligibilityCheck.setCustEmpAloc(getCustomerDAO().getCustCurEmpAlocType(customer.getCustID()));

			//Get Customer Repay Totals On Bank
			eligibilityCheck.setCustRepayBank(getCustomerDAO().getCustRepayBankTotal(customer.getCustID()));

			//Get Customer Repay Totals by Other Commitments
			eligibilityCheck.setCustRepayOther(getCustomerDAO().getCustRepayOtherTotal(customer.getCustID()));

			//Get Customer Worst Status From Finances
			eligibilityCheck.setCustWorstSts(getCustomerDAO().getCustWorstSts(customer.getCustID()));

			//DSR Calculation
			if (custDSR == null || custDSR.compareTo(BigDecimal.ZERO) == 0) {
				Rule rule = getRuleDAO().getRuleByID("DSRCAL", "ELGRULE", "", "");
				if (rule != null) {
					List<GlobalVariable> globalVariableList = SystemParameterDetails.getGlobaVariableList();
					Object dscr = getRuleExecutionUtil().executeRule(rule.getSQLRule(), eligibilityCheck, globalVariableList,finCcy);

					if(dscr == null){
						dscr = BigDecimal.ZERO;
					}else if(new BigDecimal(dscr.toString()).intValue() > 9999){
						dscr = 9999;
					}

					eligibilityCheck.setDSCR(new BigDecimal(dscr.toString()));
				}
			} else {
				eligibilityCheck.setDSCR(custDSR);
			}

		}
		logger.debug("Leaving");
		return eligibilityCheck;
	}

	/**
	 * Method for Preparing Customer Eligibility Amount Details in Base(BHD) Currency
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	@Override
	public CustomerEligibilityCheck getWIFCustEligibilityDetail(WIFCustomer customer, String productCode,
			String finCcy, BigDecimal curFinRpyAmount, int months, BigDecimal finAmount) throws IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		CustomerEligibilityCheck eligibilityCheck = new CustomerEligibilityCheck();
		if (customer != null) {

			// Eligibility object
			org.apache.commons.beanutils.BeanUtils.copyProperties(eligibilityCheck,customer);
			int age = DateUtility.getYearsBetween(customer.getCustDOB(), DateUtility.today());
			eligibilityCheck.setCustAge(age);

			//Minor Age Calculation
			int minorAge = Integer.parseInt(SystemParameterDetails.getSystemParameterValue("MINOR_AGE").toString());
			if (age < minorAge) {
				eligibilityCheck.setCustIsMinor(true);
			} else {
				eligibilityCheck.setCustIsMinor(false);
			}

			if (months > 0) {
				eligibilityCheck.setTenure(new BigDecimal((months / 12) + "." + (months % 12)));
			}
			eligibilityCheck.setReqFinAmount(finAmount);

			Date curBussDate = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);
			eligibilityCheck.setBlackListExpPeriod(DateUtility.getMonthsBetween(curBussDate,
					customer.getCustBlackListDate()));

			eligibilityCheck.setCustCtgType(customer.getLovDescCustCtgType());
			eligibilityCheck.setReqProduct(productCode);
			eligibilityCheck.setCurFinRepayAmt(curFinRpyAmount);

			if(customer.getExistCustID() != 0){

				//Finance Amount Calculations
				List<FinanceProfitDetail> financeProfitDetailsList = 
					getCustomerDAO().getCustFinAmtDetails(customer.getExistCustID(),eligibilityCheck);

				BigDecimal custFinAmount = BigDecimal.ZERO;
				BigDecimal custODAmount = BigDecimal.ZERO;

				for (FinanceProfitDetail financeProfitDetail : financeProfitDetailsList) {
					custFinAmount.add(CalculationUtil.getConvertedAmount(financeProfitDetail.getFinCcy(), finCcy, 
							financeProfitDetail.getTotalPriBal().add(financeProfitDetail.getTotalPftBal())));
					custODAmount.add(CalculationUtil.getConvertedAmount(financeProfitDetail.getFinCcy(), finCcy, 
							financeProfitDetail.getODPrincipal().add(financeProfitDetail.getODProfit())));
				}

				eligibilityCheck.setCustLiveFinAmount(custFinAmount);
				eligibilityCheck.setCustPastDueAmt(custODAmount);

				//get Customer Designation if customer status is Employed
				eligibilityCheck.setCustEmpDesg(getCustomerDAO().getCustEmpDesg(customer.getExistCustID()));

				//get Customer Employee Allocation Type if customer status is Employed
				eligibilityCheck.setCustEmpAloc(getCustomerDAO().getCustCurEmpAlocType(customer.getExistCustID()));

				//Get Customer Worst Status From Finances
				eligibilityCheck.setCustWorstSts(getCustomerDAO().getCustWorstSts(customer.getExistCustID()));
			}else{

				eligibilityCheck.setCustEmpAloc(customer.getLovDescCustEmpAlocName());
				//Get Customer Worst Status From Finances
				eligibilityCheck.setCustWorstSts(getCustStatusByMinDueDays());
			}

			//DSR Calculation
			Rule rule = getRuleDAO().getRuleByID("DSRCAL", "ELGRULE", "", "");
			if (rule != null) {
				List<GlobalVariable> globalVariableList = SystemParameterDetails.getGlobaVariableList();
				Object dscr = getRuleExecutionUtil().executeRule(rule.getSQLRule(),
						eligibilityCheck, globalVariableList,finCcy);
				eligibilityCheck.setDSCR(dscr == null ? BigDecimal.ZERO : new BigDecimal(dscr.toString()));
			}
		}
		logger.debug("Leaving");
		return eligibilityCheck;
	}

	private List<AuditDetail> listDeletion_FinContributor(FinanceDetail finDetail,
			String tableType, String auditTranType) {
		logger.debug("Entering ");

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		if (finDetail.getFinContributorHeader() != null) {
			String[] fields = PennantJavaUtil.getFieldDetails(new FinContributorHeader(), "");
			FinContributorHeader contributorHeader = finDetail.getFinContributorHeader();
			auditList.add(new AuditDetail(auditTranType, 1, fields[0], fields[1], contributorHeader
					.getBefImage(), contributorHeader));

			getFinContributorHeaderDAO().delete(contributorHeader.getFinReference(), tableType);

			String[] fields1 = PennantJavaUtil.getFieldDetails(new FinContributorDetail(), "");
			if (contributorHeader.getContributorDetailList() != null
					&& contributorHeader.getContributorDetailList().size() > 0) {

				for (int i = 0; i < contributorHeader.getContributorDetailList().size(); i++) {
					FinContributorDetail contributorDetail = contributorHeader.getContributorDetailList().get(i);
					auditList.add(new AuditDetail(auditTranType, i + 1, fields1[0], fields1[1],
							contributorDetail.getBefImage(), contributorDetail));
				}
				getFinContributorDetailDAO().deleteByFinRef(
						contributorHeader.getContributorDetailList().get(0).getFinReference(),tableType);
			}
		}
		logger.debug("Leaving ");
		return auditList;
	}

	/**
	 * Method for Maintaining List of Schedule Details in Work table by User
	 * 
	 * @param financeScheduleDetail
	 * @param financeScheduleDetails
	 */
	public void maintainWorkSchedules(String finReference, long userId,
			List<FinanceScheduleDetail> financeScheduleDetails) {
		logger.debug("Entering ");
		getFinanceScheduleDetailDAO().deleteFromWork(finReference, userId);
		for (int i = 0; i < financeScheduleDetails.size(); i++) {
			FinanceScheduleDetail detail = financeScheduleDetails.get(i);
			detail.setFinReference(finReference);
			detail.setLastMntBy(userId);
			getFinanceScheduleDetailDAO().save(detail, "_Work", false);
		}
		logger.debug("Leaving ");
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
			accountingSetId = financeType.getFinAEPlanDef();
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
		} else if (eventCode.equals("COMPOUND")) {
			accountingSetId = financeType.getFinAECapitalize();
		}

		logger.debug("Leaving");
		return accountingSetId;
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
	 * Method to get Schedule related data.
	 * 
	 * @param finReference
	 *            (String)
	 * @param isWIF
	 *            (boolean)
	 * **/
	public FinScheduleData getFinSchDataByFinRef(String finReference) {
		logger.debug("Entering");

		FinScheduleData finSchData = new FinScheduleData();

		finSchData.setFinanceMain(getFinanceMainDAO().getFinanceMainById(finReference, "", false));

		finSchData.setFinanceScheduleDetails(getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, "", false));

		finSchData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(finReference, "", false));
		finSchData.setDefermentHeaders(getDefermentHeaderDAO().getDefermentHeaders(finReference, "", false));
		finSchData.setDefermentDetails(getDefermentDetailDAO().getDefermentDetails(finReference, "", false));

		finSchData.setFinanceType(getFinanceTypeDAO().getFinanceTypeByID(finSchData.getFinanceMain().getFinType(), ""));
		finSchData.setFeeRules(getPostingsDAO().getFeeChargesByFinRef(finReference,false, ""));
		finSchData.setRepayDetails(getFinanceRepaymentsByFinRef(finReference, false));
		finSchData.setPenaltyDetails(getFinancePenaltysByFinRef(finReference));
		finSchData.setAccrueValue(getAccrueAmount(finReference));

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

	/**
	 * Method to get Schedule related data.
	 * 
	 * @param finReference
	 *            (String)
	 * @param isWIF
	 *            (boolean)
	 * **/
	public FinScheduleData getFinSchDataById(String finReference, String type,
			boolean summaryRequired) {
		logger.debug("Entering");

		FinScheduleData finSchData = new FinScheduleData();
		FinanceMain financeMain = getFinanceMainDAO().getFinanceMainById(finReference, type, false);

		finSchData.setFinanceMain(financeMain);
		finSchData.setFinanceScheduleDetails(getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, type, false));

		if(PennantConstants.FINANCE_PRODUCT_ISTISNA.equals(financeMain.getLovDescProductCodeName())){
			finSchData.setDisbursementDetails(getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference, type, false));
		}

		finSchData.setFinPftSuspended(false);
		FinanceSuspHead financeSuspHead = getFinanceSuspHeadDAO().getFinanceSuspHeadById(finReference, "");
		if (financeSuspHead != null && financeSuspHead.isFinIsInSusp()) {
			finSchData.setFinPftSuspended(true);
			finSchData.setFinSuspDate(financeSuspHead.getFinSuspDate());
		}

		if (summaryRequired) {
			
			//If Penalty Rates Required to show on Finance Inquiry Screen
			//finSchData.setFinODPenaltyRate(getFinODPenaltyRateDAO().getFinODPenaltyRateByRef(finReference, ""));

			//Finance Summary Details Preparation
			final Date curBussDate = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);
			FinanceSummary summary = new FinanceSummary();
			summary.setFinReference(financeMain.getFinReference());
			summary.setSchDate(curBussDate);

			if (financeMain.isAllowGrcPeriod() && curBussDate.compareTo(financeMain.getNextGrcPftDate()) <= 0) {
				summary.setNextSchDate(financeMain.getNextGrcPftDate());
			} else if (financeMain.getNextRepayDate().compareTo(financeMain.getNextRepayPftDate()) < 0) {
				summary.setNextSchDate(financeMain.getNextRepayDate());
			} else {
				summary.setNextSchDate(financeMain.getNextRepayPftDate());
			}

			summary = getFinanceScheduleDetailDAO().getFinanceSummaryDetails(summary);
			summary = getPostingsDAO().getTotalFeeCharges(summary);
			finSchData.setFinanceSummary(summary);
			summary.setFinCurODDays(getFinODDetailsDAO().getFinODDays(finReference, ""));

			FinODDetails finODDetails = getFinODDetailsDAO().getFinODSummary(finReference, 0, false, "");
			if (finODDetails != null) {
				summary.setFinODTotPenaltyAmt(finODDetails.getTotPenaltyAmt());
				summary.setFinODTotWaived(finODDetails.getTotWaived());
				summary.setFinODTotPenaltyPaid(finODDetails.getTotPenaltyPaid());
				summary.setFinODTotPenaltyBal(finODDetails.getTotPenaltyBal());
			}
		}

		logger.debug("Leaving");
		return finSchData;
	}

	/**
	 * Method for Fetching Profit Details for Particular Finance Reference
	 * 
	 * @param finReference
	 * @return
	 */
	public FinanceProfitDetail getFinProfitDetailsById(String finReference) {
		return getProfitDetailsDAO().getFinProfitDetailsById(finReference);
	}

	/**
	 * Method for Fetching Profit Details 
	 */
	public FinanceSummary getFinanceProfitDetails(String finRef) {
		return getFinanceMainDAO().getFinanceProfitDetails(finRef);
	}

	/**
	 * Method for getting Fiannce Contributo Header Details
	 */
	public FinContributorHeader getFinContributorHeaderById(String finReference) {
		logger.debug("Entering");
		FinContributorHeader header = getFinContributorHeaderDAO().getFinContributorHeaderById(finReference, "_AView");
		if (header != null) {
			header.setContributorDetailList(getFinContributorDetailDAO().getFinContributorDetailByFinRef(finReference, "_AView"));
		}
		logger.debug("Leaving");
		return header;
	}

	@Override
	public List<ReturnDataSet> getPostingsByFinRefAndEvent(String finReference, String finEvent,
			boolean showZeroBal) {
		return getPostingsDAO().getPostingsByFinRefAndEvent(finReference, finEvent, showZeroBal);
	}

	/**
	 * Method to CheckLimits
	 * 
	 * @param AuditHeader
	 * 
	 *  1. Check limit category exists or not for the account type, if not exists set limitValid = true other wise goto next step. 
	 *  2. Fetch customer limits from core banking. 
	 *  3. If the limits not available set the ErrMessage. 
	 *  4. If available limit is less than finance amount, set warning message if the user have the permission 'override Limits' otherwise set Error message.
	 * 
	 * */
	public AuditHeader doCheckLimits(AuditHeader auditHeader) {
		logger.debug("Entering");

		CustomerLimit custLimit = null;
		String[] errParm = new String[2];
		String[] valueParm = new String[2];

		List<CustomerLimit> list = null;

		FinanceDetail finDetails = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = finDetails.getFinScheduleData().getFinanceMain();
		AccountType accountType = getAccountTypeDAO().getAccountTypeById(
				finDetails.getFinScheduleData().getFinanceType().getFinAcType(), "");

		if (!StringUtils.trimToEmpty(accountType.getAcLmtCategory()).equals("")) {
			custLimit = new CustomerLimit();
			custLimit.setCustMnemonic(financeMain.getLovDescCustCIF());
			custLimit.setLimitCategory(accountType.getAcLmtCategory());
			custLimit.setCustLocation(" ");
		} else {
			financeMain.setLimitValid(true);
			auditHeader.getAuditDetail().setModelData(finDetails);
			logger.debug("Leaving");
			return auditHeader;
		}

		try {
			list = getCustLimitIntefaceService().fetchLimitDetails(custLimit);
		} catch (Exception e) {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, e.getMessage(),
					null));
			logger.debug("Exception " + e.getMessage());
			logger.debug("Leaving");
			return auditHeader;
		}

		if (list != null && list.size() == 0) {

			valueParm[0] = accountType.getAcLmtCategory();
			errParm[0] = PennantJavaUtil.getLabel("label_LimtCategorys") + ":" + valueParm[0];

			valueParm[1] = financeMain.getLovDescCustCIF();
			errParm[1] = "For " + PennantJavaUtil.getLabel("label_IdCustID") + ":" + valueParm[1];

			auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails("Limit", "41002",
					errParm, valueParm), finDetails.getUserDetails().getUsrLanguage()));
			logger.debug("Leaving");
			return auditHeader;

		} else {

			for (CustomerLimit customerLimit : list) {

				if (customerLimit.getLimitAmount() == null || customerLimit.getLimitAmount().equals(BigDecimal.ZERO)) {

					valueParm[0] = customerLimit.getLimitCategory();
					errParm[0] = PennantJavaUtil.getLabel("label_LimtCategory") + ":" + valueParm[0];
					valueParm[1] =financeMain.getLovDescCustCIF();
					errParm[1] = "For " + PennantJavaUtil.getLabel("label_IdCustID") + ":" + valueParm[1];

					auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails("Limit",
							"41002", errParm, valueParm), finDetails.getUserDetails().getUsrLanguage()));

					return auditHeader;

				} else {

					Currency fCurrency = getCurrencyDAO().getCurrencyById(financeMain.getFinCcy(), "");
					BigDecimal finAmount = calculateExchangeRate(financeMain.getFinAmount(), fCurrency);

					Currency lCurrency = null;
					if (!StringUtils.trimToEmpty(customerLimit.getLimitCurrency()).equals(fCurrency.getCcyCode())) {
						lCurrency = getCurrencyDAO().getCurrencyById(customerLimit.getLimitCurrency(), "");
					} else {
						lCurrency = fCurrency;
					}

					BigDecimal availAmount = calculateExchangeRate(customerLimit.getAvailAmount(), lCurrency);

					if (availAmount != null && availAmount.compareTo(finAmount) < 0) {

						valueParm[0] = PennantApplicationUtil.amountFormate(financeMain.getFinAmount(), lCurrency
								.getCcyEditField() == 0 ? PennantConstants.defaultCCYDecPos : lCurrency.getCcyEditField());
						errParm[0] = valueParm[0];

						valueParm[1] = PennantApplicationUtil.amountFormate(customerLimit.getLimitAmount(), fCurrency
								.getCcyEditField() == 0 ? PennantConstants.defaultCCYDecPos : fCurrency.getCcyEditField());
						errParm[1] = valueParm[1];

						String errorCode = "E0035";
						if (finDetails.getFinScheduleData().getFinanceType().isOverrideLimit()) {
							errorCode = "W0035";
						}

						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails(
								"Limit", errorCode, errParm, valueParm), finDetails.getUserDetails().getUsrLanguage()));
						logger.debug("Leaving");
						return auditHeader;
					}
				}
			}
			financeMain.setLimitValid(true);
			auditHeader.getAuditDetail().setModelData(finDetails);
		}
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Method for Checking Black List Abuser data Against Customer Included in Finance
	 * @param financeDetail
	 * @return
	 */
	@Override
	public boolean doCheckBlackListedCustomer(AuditHeader auditHeader){
		logger.debug("Entering");

		FinanceDetail finDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		long custId = finDetail.getFinScheduleData().getFinanceMain().getCustID();

		if(custId != Long.MIN_VALUE && custId != 0){
			String custCRCPR = getCustomerDAO().getCustCRCPRById(custId,"");
			if(!StringUtils.trimToEmpty(custCRCPR).equals("")){
				Date blackListDate = getCustomerDAO().getCustBlackListedDate(custCRCPR, "");
				if(blackListDate != null){
					logger.debug("Leaving");
					return true;
				}
			}
		}
		logger.debug("Leaving");
		return false;
	}

	/**
	 * Method for Filling Amounts in USD & BHD Format to FinanceMain Object
	 * 
	 * @param financeMain
	 * @return
	 */
	@Override
	public FinanceMain fetchConvertedAmounts(FinanceMain financeMain, boolean calAllAmounts) {
		logger.debug("Entering");
		String dftCcy = SystemParameterDetails.getSystemParameterValue("APP_DFT_CURR").toString();

		if(calAllAmounts){
			if (dftCcy.equals(financeMain.getFinCcy())) {
				financeMain.setAmountBD(PennantApplicationUtil.formateAmount(
						financeMain.getFinAmount(), financeMain.getLovDescFinFormatter()));
			} else {
				Currency fCurrency = getCurrencyDAO().getCurrencyById(financeMain.getFinCcy(), "");
				BigDecimal actualAmount = calculateExchangeRate(financeMain.getFinAmount(), fCurrency);
				financeMain.setAmountBD(actualAmount);
			}
		}

		if ("USD".equals(financeMain.getFinCcy())) {
			financeMain.setAmountUSD(PennantApplicationUtil.formateAmount(
					financeMain.getFinAmount(), financeMain.getLovDescFinFormatter()));
		} else {
			Currency fCurrency = getCurrencyDAO().getCurrencyById("USD", "");
			BigDecimal actualAmount = calculateExchangeRate(financeMain.getFinAmount(), fCurrency);
			financeMain.setAmountUSD(actualAmount);
		}

		logger.debug("Leaving");
		return financeMain;
	}
	

	public List<ErrorDetails> getDiscrepancies(FinanceDetail financeDetail){
		logger.debug("Entering");

        List<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
		financeDetail.getFinScheduleData().getFinanceMain().setLimitStatus("");
		financeDetail.getFinScheduleData().getFinanceMain().setDiscrepancy("");
		
		long ODDays = getFinODDetailsDAO().checkCustPastDue(financeDetail.getFinScheduleData().getFinanceMain().getCustID());
		int allowedDays = Integer.parseInt(SystemParameterDetails.getSystemParameterValue("MAX_ALLOW_ODDAYS").toString());
		if(ODDays >  0 ){
			if(ODDays <= allowedDays){
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "DS001",
						new String[]{String.valueOf(ODDays)}, null), ""));
			}else{
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "DS002",
						new String[]{String.valueOf(ODDays)},null), ""));
			}
		}
		
		//Check Limit Status of Customer
		CustomerLimit custLimit = null;
		List<CustomerLimit> customerLimitList = null;
		AccountType accountType = getAccountTypeDAO().getAccountTypeById(
				financeDetail.getFinScheduleData().getFinanceType().getFinAcType(), "");
		if (!StringUtils.trimToEmpty(accountType.getAcLmtCategory()).equals("")) {
			custLimit = new CustomerLimit();
			custLimit.setCustMnemonic(financeDetail.getFinScheduleData().getFinanceMain()
					.getLovDescCustCIF());
			custLimit.setLimitCategory(accountType.getAcLmtCategory());
			custLimit.setCustLocation(" ");
		} else {
			logger.debug("Leaving");
			return errorDetails;
		}

		try {
			customerLimitList = getCustLimitIntefaceService().fetchLimitDetails(custLimit);
		} catch (Exception e) {
			logger.debug("Exception " + e.getMessage());
			logger.debug("Leaving");
			return errorDetails;
		}

		if (customerLimitList != null && customerLimitList.size() > 0) {

			Date curBussDate = (Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");
			String limitType = "";
			
				//###Release PFFV1.0.6 - Changed Finance Amount to Finance Amount - Downpay Amount.
			BigDecimal finAmount =  financeDetail.getFinScheduleData().getFinanceMain().getFinAmount();
			BigDecimal downpaybank = financeDetail.getFinScheduleData().getFinanceMain().getDownPayBank();						
			BigDecimal downpaySuppl = financeDetail.getFinScheduleData().getFinanceMain().getDownPaySupl();
			finAmount = finAmount.subtract(downpaybank==null?BigDecimal.ZERO:downpaybank).subtract(downpaySuppl==null?BigDecimal.ZERO:downpaySuppl);
			BigDecimal calcFinAmount = BigDecimal.ZERO;
			for (CustomerLimit limit : customerLimitList) {
				if(!limit.getCustCountry().equals("")){
					if(limit.getCustCountry().equalsIgnoreCase(PennantConstants.COUNTRY_BEHRAIN)){
						continue;
					}
					limitType = " ( " + limit.getCustCountry() + " - " + limit.getCustCountryDesc() + " )";
				}else if(!limit.getCustGrpCode().equals("")){
					limitType = " ( " + limit.getCustGrpCode() + " - " + limit.getCustGrpDesc() + " ) ";
				}else {
					limitType = "Customer";
				}
				
				if(limit.getLimitCategory().equals("")){
					if(!limit.getCustGrpCode().equals("")){
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "DS006",
								new String[]{limitType},null), ""));
					}else if(!limit.getCustCountry().equals("")){
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "DS009",
								new String[]{limitType},null), ""));
					}else{
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "DS003",
								null, null), ""));
					}
						continue;
				}
				
				if(limit.getLimitExpiry() != null && limit.getLimitExpiry().compareTo(curBussDate) < 0){
					
					if(!limit.getCustGrpCode().equals("")){
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "DS007",
								new String[]{limitType,limit.getLimitCategoryDesc()},null), ""));
					}else if(!limit.getCustCountry().equals("")){
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "DS010",
								new String[]{limitType,limit.getLimitCategoryDesc()},null), ""));
					}else{
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "DS004",
								new String[]{limit.getLimitCategoryDesc()},null), ""));
					}
					logger.debug("Leaving");
				}

				Currency lCurrency = null;
				if (!StringUtils.trimToEmpty(limit.getLimitCurrency()).equals("") ){
					if(!StringUtils.trimToEmpty(limit.getLimitCurrency()).equals(financeDetail.getFinScheduleData().getFinanceMain().getFinCcy())) {
						lCurrency = getCurrencyDAO().getCurrencyById(limit.getLimitCurrency(), "");
						Currency fCurrency = getCurrencyDAO().getCurrencyById(
								financeDetail.getFinScheduleData().getFinanceMain().getFinCcy(), "");
						calcFinAmount = CalculationUtil.getConvertedAmount(fCurrency, lCurrency, finAmount);
					}  else{
						calcFinAmount = finAmount;
					}
				}
				BigDecimal excessAmount = limit.getRiskAmount().add(calcFinAmount).subtract(limit.getLimitAmount());
				if (excessAmount.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal excessPerc  ;
					if(limit.getLimitAmount().compareTo(BigDecimal.ZERO) == 0){
						excessPerc = new BigDecimal(100);
						/*
						if(!limit.getCustGrpCode().equals("")){
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "DS007",
									new String[]{limitType,limit.getLimitCategoryDesc()},null), ""));
						}else if(!limit.getCustCountry().equals("")){
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "DS008",
									new String[]{limitType,limit.getLimitCategoryDesc()},null), ""));
						}else{
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "DS006",
									new String[]{limit.getLimitCategoryDesc()}, null), ""));
						}
					*/}else{
						excessPerc = excessAmount.multiply(new BigDecimal(100)).divide(limit.getLimitAmount(), 2, RoundingMode.HALF_DOWN);
					}
						if(!limit.getCustCountry().equals("")){
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "DS011",
									new String[]{excessPerc.toString(), PennantApplicationUtil.amountFormate(
											excessAmount, financeDetail.getFinScheduleData().getFinanceMain().getLovDescFinFormatter()).toString(),limitType,limit.getLimitCategoryDesc()},null), ""));
						}else if(!limit.getCustGrpCode().equals("")){
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "DS008",
									new String[]{excessPerc.toString(), PennantApplicationUtil.amountFormate(
											excessAmount, financeDetail.getFinScheduleData().getFinanceMain().getLovDescFinFormatter()).toString(),limitType,limit.getLimitCategoryDesc()},null), ""));
						}else{
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "DS005",
									new String[]{excessPerc.toString(), PennantApplicationUtil.amountFormate(
											excessAmount, financeDetail.getFinScheduleData().getFinanceMain().getLovDescFinFormatter()).toString(),limit.getLimitCategoryDesc()},null), ""));
						}
				
				}
			}
		}
		logger.debug("Leaving");
		return errorDetails;
	
	}
	
	/**
	 * Method for Checking exception List based upon Requirements
	 */
	public AuditHeader doCheckExceptions(AuditHeader auditHeader) {
		logger.debug("Entering");

		FinanceDetail aFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		//Check for Exception 
		aFinanceMain.setException(false);

		//*** Case 1 : Amount Case Check Exception for 100K BHD ***
		String dftCcy = SystemParameterDetails.getSystemParameterValue("APP_DFT_CURR").toString();
		final BigDecimal finAmount = PennantApplicationUtil.formateAmount(
				aFinanceMain.getFinAmount(), aFinanceMain.getLovDescFinFormatter());
		if (dftCcy.equals(aFinanceMain.getFinCcy())) {
			aFinanceMain.setAmount(finAmount);
		} else {
			//Covert Amount into BHD Format 
			Currency fCurrency = getCurrencyDAO().getCurrencyById(aFinanceMain.getFinCcy(), "");
			aFinanceMain.setAmount(finAmount.multiply(fCurrency.getCcySpotRate()));
		}

		if (aFinanceMain.getAmount().compareTo(BigDecimal.valueOf(100000.000)) > 0) {
			aFinanceMain.setException(true);
		}
		aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);
		auditHeader.getAuditDetail().setModelData(aFinanceDetail);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Method for Calculating Exchange Rate for Fiannce Schedule Calculation
	 * 
	 * @param amount
	 * @param aCurrency
	 * @return
	 */
	private BigDecimal calculateExchangeRate(BigDecimal amount, Currency aCurrency) {
		if (SystemParameterDetails.getSystemParameterValue("APP_DFT_CURR").equals(
				aCurrency.getCcyCode())) {
			return amount;
		} else {
			if (amount == null) {
				amount = BigDecimal.ZERO;
			}

			if (aCurrency != null) {
				amount = amount.multiply(aCurrency.getCcySpotRate());
			}
		}
		return amount;
	}

	/**
	 * Method For Preparing List of AuditDetails for Contributor Details
	 * 
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
	private List<AuditDetail> processingContributorList(List<AuditDetail> auditDetails,
			String type, String finReference) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			FinContributorDetail contributorDetail = (FinContributorDetail) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";

			if (type.equals("")) {
				approveRec = true;
				contributorDetail.setRoleCode("");
				contributorDetail.setNextRoleCode("");
				contributorDetail.setTaskId("");
				contributorDetail.setNextTaskId("");
			}

			contributorDetail.setWorkflowId(0);

			if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (contributorDetail.isNewRecord()) {
				saveRecord = true;
				if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					contributorDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (contributorDetail.getRecordType().equalsIgnoreCase(
						PennantConstants.RCD_DEL)) {
					contributorDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (contributorDetail.getRecordType().equalsIgnoreCase(
						PennantConstants.RCD_UPD)) {
					contributorDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (contributorDetail.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = contributorDetail.getRecordType();
				recordStatus = contributorDetail.getRecordStatus();
				contributorDetail.setRecordType("");
				contributorDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				if (StringUtils.trimToEmpty(contributorDetail.getFinReference()).equals("")) {
					contributorDetail.setFinReference(finReference);
				}
				finContributorDetailDAO.save(contributorDetail, type);
			}

			if (updateRecord) {
				finContributorDetailDAO.update(contributorDetail, type);
			}

			if (deleteRecord) {
				finContributorDetailDAO.delete(contributorDetail, type);
			}

			if (approveRec) {
				contributorDetail.setRecordType(rcdType);
				contributorDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(contributorDetail);
		}

		logger.debug("Leaving");
		return auditDetails;

	}
	
	@Override
	public void updateCustCIF(long custID, String finReference) {
		getFinanceMainDAO().updateCustCIF(custID, finReference);

	}

	//Document Details List Maintainance
	public void listDocDeletion(FinanceDetail custDetails, String tableType) {
		getDocumentDetailsDAO().deleteList(
				new ArrayList<DocumentDetails>(custDetails.getDocumentDetailsList()), tableType);
	}

	@Override
	public List<DocumentDetails> getFinDocByFinRef(String finReference, String type) {
		return getDocumentDetailsDAO().getDocumentDetailsByRef(finReference, type);
	}

	@Override
	public DocumentDetails getFinDocDetailByDocId(long docId) {
		return getDocumentDetailsDAO().getDocumentDetailsById(docId, "");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++ Agreement Details ++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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
	 * Method for Delete Additional Field Details
	 */
	private void doDeleteAddlFieldDetails(FinanceDetail financeDetail, String tableType) {
		logger.debug("Entering");

		if (financeDetail.getLovDescExtendedFieldValues() != null
				&& financeDetail.getLovDescExtendedFieldValues().size() > 0) {

			String tableName = "";
			if (PennantStaticListUtil.getModuleName().containsKey("FASSET")) {
				tableName = PennantStaticListUtil.getModuleName().get("FASSET").get(
						financeDetail.getFinScheduleData().getFinanceType().getLovDescAssetCodeName());
			}

			getExtendedFieldDetailDAO().deleteAdditional(
					financeDetail.getFinScheduleData().getFinReference(), tableName, tableType);
		}
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++ Cust Related Finance Details ++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public FinanceDetail fetchFinCustDetails(FinanceDetail financeDetail, String ctgType,
			String finType, String userRole) {
		logger.debug("Entering");

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		String eventCode = "";
		Date curBussDate = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);
		if (financeDetail.getFinScheduleData().getFinanceMain().getFinStartDate().after(curBussDate)) {
			eventCode = "ADDDBSF";
		} else {
			eventCode = "ADDDBSP";
		}
		String accSetId = returnAccountingSetid(eventCode, financeDetail.getFinScheduleData()
				.getFinanceType());

		//Finance Accounting Posting Details
		financeDetail.setTransactionEntries(getTransactionEntryDAO().getListTransactionEntryById(
				Long.valueOf(accSetId), "_AEView", true));

		//Finance Commitment Accounting Posting Details
		if (PennantConstants.RECORD_TYPE_NEW.equals(financeDetail.getFinScheduleData().getFinanceMain().getRecordType())) {
			if (financeDetail.getFinScheduleData().getFinanceType().isFinCommitmentReq()
					&& !StringUtils.trimToEmpty(financeDetail.getFinScheduleData().getFinanceMain().getFinCommitmentRef()).equals("")) {

				long accountingSetId = getAccountingSetDAO().getAccountingSetId("CMTDISB","CMTDISB");
				if (accountingSetId != 0) {
					financeDetail.setCmtFinanceEntries(getTransactionEntryDAO()
							.getListTransactionEntryById(accountingSetId, "_AEView", true));
				}
			}
		}

		//Finance Stage Accounting Posting Details
		financeDetail.setStageTransactionEntries(getTransactionEntryDAO()
				.getListTransactionEntryByRefType(finType, PennantConstants.Accounting, userRole, "_AEView", true));

		// Set Eligibility Details to finaceDetail
		financeDetail.setElgRuleList(getEligibilityDetailService().setFinanceEligibilityDetails(financeMain.getFinReference(),
				financeMain.getFinCcy(), financeMain.getFinAmount(), financeMain.isNewRecord(), finType, null));

		// Set Scoring Details to finaceDetail
		getScoringDetailService().setFinanceScoringDetails(financeDetail, finType, userRole, ctgType);
		
		//Reset Finance Document Details
		financeDetail.setDocumentDetailsList(new ArrayList<DocumentDetails>(1));

		//Set Check List Details to finaceDetail
		getCheckListDetailService().setFinanceCheckListDetails(financeDetail, finType, userRole);

		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * getAssetEnqDetailById fetch the details by using FinanceMainDAO's And getFinanceTypeDAO's .
	 * 
	 * @param finReference
	 *            (String)
	 * @return FinanceDetail
	 */
	public FinanceDetail getFinAssetDetails(String finReference, String type) {
		logger.debug("Entering");
		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		scheduleData.setFinReference(finReference);
		scheduleData.setFinanceMain(getFinanceMainDAO().getFinanceMainById(finReference, type, false));
		scheduleData.setFinanceType(getFinanceTypeDAO().getFinanceTypeByID(scheduleData.getFinanceMain().getFinType(), "_AView"));

		String assetCode = financeDetail.getFinScheduleData().getFinanceType().getLovDescAssetCodeName();
		Assets assest = Assets.valueOf(assetCode.toUpperCase());

		switch(assest) {
		case VEHICLE:
			CarLoanDetail  carLoanDetail;
			carLoanDetail = getCarLoanDetailService().getCarLoanDetailById(finReference);			
			financeDetail.setCarLoanDetail(carLoanDetail);
			break;
		case EDUCATON:
			getEducationalLoanService().setEducationalLoanDetails(financeDetail, type);
			break;
		case HOME:
			HomeLoanDetail homeLoanDetail;
			homeLoanDetail = getHomeLoanDetailService().getHomeLoanDetailById(finReference);
			financeDetail.setHomeLoanDetail(homeLoanDetail);
			break;
		case EQPMENT:
			MortgageLoanDetail mortgageLoanDetail;
			mortgageLoanDetail =  getMortgageLoanDetailService().getMortgageLoanDetailById(finReference);
			financeDetail.setMortgageLoanDetail(mortgageLoanDetail);
			break;
		case GOODS:
			List<GoodsLoanDetail> goodsLoanDetails;
			goodsLoanDetails =  getGoodsLoanDetailService().getGoodsLoanDetailById(finReference).getGoodsLoanDetailList();
			financeDetail.setGoodsLoanDetails(goodsLoanDetails);
			break;
		case GENGOODS:
			List<GenGoodsLoanDetail> genGoodsLoanDetails;
			genGoodsLoanDetails = getGenGoodsLoanDetailService().getGenGoodsLoanDetailById(finReference).getGenGoodsLoanDetailList();
			financeDetail.setGenGoodsLoanDetails(genGoodsLoanDetails);
			break;
		case FLEETVEH:
			List<CarLoanDetail> vehicleLoanDetails;
			vehicleLoanDetails =  getCarLoanDetailService().getVehicleLoanDetailById(finReference).getVehicleLoanDetailList();
			financeDetail.setVehicleLoanDetails(vehicleLoanDetails);
			break;	
		case COMIDITY:
			CommidityLoanHeader commidityLoanHeader = null; 
			commidityLoanHeader = getCommidityLoanDetailService().getCommidityLoanHeaderById(finReference);
			if(commidityLoanHeader != null) {
				financeDetail.setCommidityLoanHeader(commidityLoanHeader);
				financeDetail.setCommidityLoanDetails(commidityLoanHeader.getCommidityLoanDetails());
			}
			break;
		case SHARES:
			getSharesDetailService().setSharesDetails(financeDetail, type);
			break;
		default:
			break;
		}
		logger.debug("Leaving");
		return financeDetail;

	}
	
	@Override
	public List<String> getFinanceReferenceList() {
		return getFinanceMainDAO().getFinanceReferenceList();
	}

	@Override
	public List<BulkProcessDetails> getIjaraBulkRateFinList(Date fromDate, Date toDate) {
		return getFinanceMainDAO().getIjaraBulkRateFinList(fromDate, toDate);
	}

	@Override
	public List<BulkDefermentChange> getBulkDefermentFinList(Date fromDate, Date toDate) {
		return getFinanceMainDAO().getBulkDefermentFinList(fromDate, toDate);
	}

	@Override
	public String getCustStatusByMinDueDays() {
		CustomerStatusCode customerStatusCode = getCustomerStatusCodeDAO().getCustStatusByMinDueDays("");
		if (customerStatusCode != null) {
			return customerStatusCode.getCustStsCode();
		}
		return "";
	}

	/**
	 * Get approved Customer Finance Details By ID 
	 */
	public CustomerFinanceDetail getApprovedCustomerFinanceById(String finReference){
		CustomerFinanceDetail customerFinanceDetail = getFinanceMainDAO().getCustomerFinanceMainById(finReference, "_AView");
		customerFinanceDetail.setAuditTransactionsList(getFinanceMainDAO().getFinTransactionsList(finReference, true));
		return customerFinanceDetail;
	}

	/**
	 * Get Customer Finance Details By ID 
	 */
	public CustomerFinanceDetail getCustomerFinanceById(String finReference) {
		CustomerFinanceDetail customerFinanceDetail = getFinanceMainDAO().getCustomerFinanceMainById(finReference, "_View");
		customerFinanceDetail.setAuditTransactionsList(getFinanceMainDAO().getFinTransactionsList(finReference, false));
		customerFinanceDetail.setNotesList(getNotesDAO().getNotesListAsc(getNotes(finReference), true));
		return customerFinanceDetail;
	}

	/**
	 * Method for retrieving Notes Details
	 */
	private Notes getNotes(String finReference) {
		logger.debug("Entering ");
		Notes notes = new Notes();
		notes.setModuleName("financeMain");
		notes.setReference(finReference);
		notes.setVersion(0);
		logger.debug("Leaving ");
		return notes;
	}

	@Override
	public List<CustomerIncome> prepareIncomeDetails() {

		List<IncomeType> incomeTypeList = getIncomeTypeDAO().getIncomeTypeList();
		List<CustomerIncome> customerIncomes = new ArrayList<CustomerIncome>();
		for (IncomeType incomeType : incomeTypeList) {
			CustomerIncome income = new CustomerIncome();
			income.setIncomeExpense(incomeType.getIncomeExpense().trim());
			income.setCustIncomeType(incomeType.getIncomeTypeCode().trim());
			income.setJointCust(false);
			income.setMargin(BigDecimal.ZERO);
			income.setCategory(incomeType.getCategory().trim());
			income.setCustIncome(BigDecimal.ZERO);
			income.setVersion(1);
			income.setRecordType(PennantConstants.RCD_ADD);
			income.setWorkflowId(0);
			income.setLovDescCategoryName(incomeType.getLovDescCategoryName().trim());
			income.setLovDescCustIncomeTypeName(incomeType.getIncomeTypeDesc().trim());

			customerIncomes.add(income);
		}
		return customerIncomes;
	}

	@Override
	public List<AvailFinance> getFinanceDetailByCmtRef(String cmtRef, long custId) {
		return getFinanceMainDAO().getFinanceDetailByCmtRef(cmtRef, custId);
	}	
	
	/**
	 * Method for Fetching Template ID List using Finance Type & Role Code
	 */
	@Override
	public List<Long> getMailTemplatesByFinType(String financeType, String roleCode){
		return getFinanceReferenceDetailDAO().getMailTemplatesByFinType(financeType, roleCode);
	}	
	
	@Override
	public BigDecimal getCustRepayBankTotal(long custId) {
		return getCustomerDAO().getCustRepayBankTotal(custId);
	}	

	@Override
	public List<FeeRule> getFeeChargesByFeeCode(String feeCode, String tableType){
		return getFinFeeChargesDAO().getFeeChargesByFeeCode(feeCode, tableType);
	}
	
	@Override
	public FeeRule getFeeChargesByFinRefAndFeeCode(String finReference, String feeCode, String tableType){
		return getFinFeeChargesDAO().getFeeChargesByFinRefAndFee(finReference, feeCode, tableType);
	}
	
	@Override
	public boolean updateFeeChargesByFinRefAndFeeCode(FeeRule feeRule, String tableType){
		return getFinFeeChargesDAO().updateFeeChargesByFinRefAndFee(feeRule, tableType);
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public CustomerIncomeDAO getCustomerIncomeDAO() {
		return customerIncomeDAO;
	}
	public void setCustomerIncomeDAO(CustomerIncomeDAO customerIncomeDAO) {
		this.customerIncomeDAO = customerIncomeDAO;
	}

	public String getExcludeFields() {
		return excludeFields;
	}
	public void setExcludeFields(String excludeFields) {
		this.excludeFields = excludeFields;
	}

	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
		this.financeReferenceDetailDAO = financeReferenceDetailDAO;
	}
	public FinanceReferenceDetailDAO getFinanceReferenceDetailDAO() {
		return financeReferenceDetailDAO;
	}

	public AccountingSetDAO getAccountingSetDAO() {
		return accountingSetDAO;
	}
	public void setAccountingSetDAO(AccountingSetDAO accountingSetDAO) {
		this.accountingSetDAO = accountingSetDAO;
	}

	public ExtendedFieldHeaderDAO getExtendedFieldHeaderDAO() {
		return extendedFieldHeaderDAO;
	}
	public void setExtendedFieldHeaderDAO(ExtendedFieldHeaderDAO extendedFieldHeaderDAO) {
		this.extendedFieldHeaderDAO = extendedFieldHeaderDAO;
	}

	public ExtendedFieldDetailDAO getExtendedFieldDetailDAO() {
		return extendedFieldDetailDAO;
	}
	public void setExtendedFieldDetailDAO(ExtendedFieldDetailDAO extendedFieldDetailDAO) {
		this.extendedFieldDetailDAO = extendedFieldDetailDAO;
	}

	public RuleDAO getRuleDAO() {
		return ruleDAO;
	}
	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

	public RuleExecutionUtil getRuleExecutionUtil() {
		return ruleExecutionUtil;
	}
	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
	}

	public CustomerLimitIntefaceService getCustLimitIntefaceService() {
		return custLimitIntefaceService;
	}
	public void setCustLimitIntefaceService(CustomerLimitIntefaceService custLimitIntefaceService) {
		this.custLimitIntefaceService = custLimitIntefaceService;
	}

	public FinanceWriteoffDAO getFinanceWriteoffDAO() {
		return financeWriteoffDAO;
	}
	public void setFinanceWriteoffDAO(FinanceWriteoffDAO financeWriteoffDAO) {
		this.financeWriteoffDAO = financeWriteoffDAO;
	}

	public AccountTypeDAO getAccountTypeDAO() {
		return accountTypeDAO;
	}
	public void setAccountTypeDAO(AccountTypeDAO accountTypeDAO) {
		this.accountTypeDAO = accountTypeDAO;
	}

	public CurrencyDAO getCurrencyDAO() {
		return currencyDAO;
	}
	public void setCurrencyDAO(CurrencyDAO currencyDAO) {
		this.currencyDAO = currencyDAO;
	}

	public void setFinContributorHeaderDAO(FinContributorHeaderDAO finContributorHeaderDAO) {
		this.finContributorHeaderDAO = finContributorHeaderDAO;
	}
	public FinContributorHeaderDAO getFinContributorHeaderDAO() {
		return finContributorHeaderDAO;
	}

	public void setFinContributorDetailDAO(FinContributorDetailDAO finContributorDetailDAO) {
		this.finContributorDetailDAO = finContributorDetailDAO;
	}
	public FinContributorDetailDAO getFinContributorDetailDAO() {
		return finContributorDetailDAO;
	}

	public void setIncomeTypeDAO(IncomeTypeDAO incomeTypeDAO) {
		this.incomeTypeDAO = incomeTypeDAO;
	}
	public IncomeTypeDAO getIncomeTypeDAO() {
		return incomeTypeDAO;
	}

	public NotesDAO getNotesDAO() {
		return notesDAO;
	}
	public void setNotesDAO(NotesDAO notesDAO) {
		this.notesDAO = notesDAO;
	}

	public IndicativeTermDetailDAO getIndicativeTermDetailDAO() {
		return indicativeTermDetailDAO;
	}
	public void setIndicativeTermDetailDAO(IndicativeTermDetailDAO indicativeTermDetailDAO) {
		this.indicativeTermDetailDAO = indicativeTermDetailDAO;
	}

	public FinancePremiumDetailDAO getFinancePremiumDetailDAO() {
	    return financePremiumDetailDAO;
    }
	public void setFinancePremiumDetailDAO(FinancePremiumDetailDAO financePremiumDetailDAO) {
	    this.financePremiumDetailDAO = financePremiumDetailDAO;
    }
	
}
