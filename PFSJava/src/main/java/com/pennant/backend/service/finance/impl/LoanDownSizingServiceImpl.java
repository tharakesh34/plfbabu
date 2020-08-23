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
 *
 * FileName    		:  LoanDownSizingServiceImpl.java										*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  16-11-2018															*
 *                                                                  
 * Modified Date    :  16-11-2018															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 16-11-2018       Pennant	                 0.1                                            * 
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
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;
import org.springframework.beans.BeanUtils;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.core.ChangeGraceEndService;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinAssetAmtMovementDAO;
import com.pennant.backend.dao.finance.FinLogEntryDetailDAO;
import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.dao.finance.FinServiceInstrutionDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.dao.finance.SubventionDetailDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinAssetAmtMovement;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.GenericFinanceDetailService;
import com.pennant.backend.service.finance.LoanDownSizingService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class LoanDownSizingServiceImpl extends GenericFinanceDetailService implements LoanDownSizingService {

	private static final Logger logger = Logger.getLogger(LoanDownSizingServiceImpl.class);

	private CustomerDetailsService customerDetailsService;
	private ChangeGraceEndService changeGraceEndService;

	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinServiceInstrutionDAO finServiceInstructionDAO;
	private FinanceTypeDAO financeTypeDAO;
	private AuditHeaderDAO auditHeaderDAO;

	// Process Schedule Details
	private FinanceProfitDetailDAO profitDetailsDAO;
	private FinanceDisbursementDAO financeDisbursementDAO;
	private RepayInstructionDAO repayInstructionDAO;
	private FinLogEntryDetailDAO finLogEntryDetailDAO;
	private FinODPenaltyRateDAO finODPenaltyRateDAO;
	private SubventionDetailDAO subventionDetailDAO;
	private FinAssetAmtMovementDAO finAssetAmtMovementDAO;

	/**
	 * Method for retrieving required finance data for DownSizing
	 */
	@Override
	public FinanceDetail getDownSizingFinance(FinanceMain aFinanceMain, String rcdMaintainSts) {
		logger.debug(Literal.ENTERING);

		String finReference = aFinanceMain.getId();

		// Finance Details
		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData finScheduleData = getFinSchDataByFinRef(finReference, "", 0);

		List<FinServiceInstruction> finservInstList = new ArrayList<FinServiceInstruction>();
		if (StringUtils.isNotBlank(rcdMaintainSts)) {
			finservInstList = finServiceInstructionDAO.getFinServiceInstructions(finReference, "_Temp", rcdMaintainSts);
		} else {
			finservInstList.add(new FinServiceInstruction());
		}

		// TODO : Customer Details
		financeDetail.setCustomerDetails(getCustomerDetails(aFinanceMain));

		finScheduleData.setFinServiceInstructions(finservInstList);
		financeDetail.setFinScheduleData(finScheduleData);

		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	/**
	 * 
	 * @param aFinanceMain
	 * @return
	 */
	private CustomerDetails getCustomerDetails(FinanceMain aFinanceMain) {

		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setCustID(aFinanceMain.getCustID());

		Customer customer = new Customer();
		customer.setCustID(aFinanceMain.getCustID());
		customer.setCustCIF(aFinanceMain.getLovDescCustCIF());
		customer.setCustShrtName(aFinanceMain.getLovDescCustShrtName());

		customerDetails.setCustomer(customer);
		return customerDetails;
	}

	/**
	 * Method to fetch finance details by id from given table type
	 * 
	 * @param finRef
	 *            (String)
	 * @param isWIF
	 *            (boolean)
	 **/
	public FinScheduleData getFinSchDataByFinRef(String finRef, String type, long logKey) {
		logger.debug("Entering");

		FinScheduleData finSchData = new FinScheduleData();
		finSchData.setFinReference(finRef);

		if (logKey == 0) {

			// FinanceMain
			FinanceMain financeMain = getFinanceMainDAO().getFinanceMainById(finRef, "_View", false);
			finSchData.setFinanceMain(financeMain);

			// FinanceType
			String finType = financeMain.getFinType();
			finSchData.setFinanceType(financeTypeDAO.getFinanceTypeByID(finType, "_AView"));
			//FIXME  Depeneds with underconstruction changes
			// ACCRUAL Amount
			/*
			 * BigDecimal PftAccrued = profitDetailsDAO.getAccrueAmount(finRef, financeMain.getProductCategory());
			 * finSchData.setAccrueValue(PftAccrued);
			 */

			// Repay, Fees and Penalty Details
			/*
			 * finSchData.setRepayDetails(getFinanceRepaymentsDAO().getFinRepayListByFinRef(finRef, false, ""));
			 * finSchData.setFinFeeDetailList(getFinFeeDetailService().getFinFeeDetailById(finRef, false, ""));
			 * finSchData.setFeeRules(getFinFeeChargesDAO().getFeeChargesByFinRef(finRef, "", false, ""));
			 * finSchData.setPenaltyDetails(getRecoveryDAO().getFinancePenaltysByFinRef(finRef, ""));
			 */
		}

		// Schedule, Disbursement Details and Repay Instructions
		finSchData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finRef, type, false));
		finSchData.setDisbursementDetails(financeDisbursementDAO.getFinanceDisbursementDetails(finRef, type, false));
		finSchData.setRepayInstructions(repayInstructionDAO.getRepayInstructions(finRef, type, false));

		logger.debug("Leaving");
		return finSchData;
	}

	/**
	 * Re build the schedule based on new grace end date
	 * 
	 * End Grace / PRE EMI Period After Full Disbursement
	 * 
	 * @param finScheduleData
	 * @return
	 */
	public FinScheduleData changeGraceEndAfterFullDisb(FinScheduleData finScheduleData) {

		// New Grace End Date
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		financeMain.setEventFromDate(SysParamUtil.getAppDate());

		finScheduleData = getChangeGraceEndService().changeGraceEnd(finScheduleData, true);

		return finScheduleData;
	}

	/**
	 * get Accounting Entries
	 * 
	 * @param finScheduleData
	 * @return
	 */
	public AEEvent getChangeGrcEndPostings(FinScheduleData finScheduleData) throws Exception {
		return getChangeGraceEndService().getChangeGrcEndPostings(finScheduleData);

	}

	/**
	 * 
	 * @param finRef
	 * @param movementType
	 * @return
	 */
	public List<FinAssetAmtMovement> getFinAssetAmtMovements(String finRef, String movementType) {
		return getFinAssetAmtMovementDAO().getFinAssetAmtMovements(finRef, movementType);
	}

	/**
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, PennantConstants.method_saveOrUpdate);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();

		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		FinServiceInstruction finServInst = finScheduleData.getFinServiceInstructions().get(0);

		String rcdMaintainSts = "";
		TableType tableType = TableType.MAIN_TAB;

		if (financeMain.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
			rcdMaintainSts = FinanceConstants.FINSER_EVENT_LOANDOWNSIZING;
		}
		financeMain.setRcdMaintainSts(rcdMaintainSts);

		if (financeMain.isNew()) {

			getFinanceMainDAO().save(financeMain, tableType, false);
			finServiceInstructionDAO.save(finServInst, tableType.getSuffix());

			auditHeader.setAuditReference(financeMain.getFinReference());
			auditHeader.getAuditDetail().setModelData(financeMain);

		} else {
			getFinanceMainDAO().update(financeMain, tableType, false);

			finServiceInstructionDAO.deleteList(financeMain.getFinReference(), rcdMaintainSts, tableType.getSuffix());
			finServiceInstructionDAO.save(finServInst, tableType.getSuffix());

		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				financeMain.getBefImage(), financeMain));

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) throws InterfaceException, JaxenException {
		logger.debug("Entering");

		String tranType = "";
		auditHeader = businessValidation(auditHeader, PennantConstants.method_doApprove);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		financeDetail.setModuleDefiner(FinanceConstants.FINSER_EVENT_LOANDOWNSIZING);

		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinServiceInstruction finServInst = finScheduleData.getFinServiceInstructions().get(0);
		BigDecimal downSizingAmt = finServInst.getAmount();

		FinanceMain financeMain = new FinanceMain();
		BeanUtils.copyProperties(finScheduleData.getFinanceMain(), financeMain);

		financeMain.setRcdMaintainSts("");
		financeMain.setRoleCode("");
		financeMain.setNextRoleCode("");
		financeMain.setTaskId("");
		financeMain.setNextTaskId("");
		financeMain.setWorkflowId(0);

		// Revised FinAssetValue Value
		FinAssetAmtMovement assetAmtMovt = prepareSanAmtMovement(financeMain, finServInst);
		financeMain.setFinAssetValue(financeMain.getFinAssetValue().subtract(downSizingAmt));

		if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
			tranType = PennantConstants.TRAN_ADD;
			financeMain.setRecordType("");
		} else {
			tranType = PennantConstants.TRAN_UPD;
			financeMain.setRecordType("");

			// FULL DISB : Schedule Changed By Grace Period Ended
			if (financeMain.isScheduleRegenerated()) {

				// 1. Postings ----> Execute Accounting Details Process
				auditHeader = executeAccountingProcess(auditHeader, SysParamUtil.getAppDate());

				if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
					auditHeader.setErrorList(auditHeader.getErrorMessage());
					return auditHeader;
				}

				// 2. Data Saving ----> Schedule Tables
				processScheduleDetails(financeDetail, finServInst);
				getFinanceMainDAO().update(financeMain, TableType.MAIN_TAB, false);
			} else {

				// Update Revised Sanctioned Amount and Save FinServiceInstructions
				finServiceInstructionDAO.save(finServInst, "");
				getFinanceMainDAO().updateFinAssetValue(financeMain);
			}
		}

		// Sanctioned Amount Movements
		finAssetAmtMovementDAO.saveFinAssetAmtMovement(assetAmtMovt);

		// Delete from Staging Tables
		getFinanceMainDAO().delete(financeMain, TableType.TEMP_TAB, false, true);
		finServiceInstructionDAO.deleteList(financeMain.getFinReference(), FinanceConstants.FINSER_EVENT_LOANDOWNSIZING,
				"_Temp");

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				financeMain.getBefImage(), financeMain));

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(financeMain);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Delete and Save Schedule dependent tables
	 * 
	 * @param financeDetail
	 * @param finServInst
	 */
	private void processScheduleDetails(FinanceDetail financeDetail, FinServiceInstruction finServInst) {
		logger.debug("Entering");

		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		String finReference = financeMain.getFinReference();

		// START : prepare FinLogEntryDetail data
		FinLogEntryDetail entryDetail = new FinLogEntryDetail();

		entryDetail.setReversalCompleted(false);
		entryDetail.setFinReference(finReference);
		entryDetail.setPostDate(SysParamUtil.getAppDate());
		entryDetail.setSchdlRecal(financeMain.isScheduleRegenerated());
		entryDetail.setEventAction(AccountEventConstants.ACCEVENT_GRACEEND);

		long logKey = finLogEntryDetailDAO.save(entryDetail);
		// END

		// START : Fetch Existing data before Modification
		FinScheduleData oldFinSchdData = getFinSchDataByFinRef(finReference, "", -1);
		oldFinSchdData.setFinReference(finReference);
		oldFinSchdData.setFinanceMain(financeMain);

		// Save Schedule Details For Future Modifications
		listSave(oldFinSchdData, "_Log", false, logKey, finServInst.getServiceSeqId());

		// END

		// delete from Staging table and schedule tables delete and insert
		listDeletion(finScheduleData, financeDetail.getModuleDefiner(), "", false);
		finServiceInstructionDAO.deleteList(financeMain.getFinReference(), financeDetail.getModuleDefiner(), "");

		// save latest schedule details
		listSave(finScheduleData, "", false, 0, finServInst.getServiceSeqId());

		logger.debug("Leaving");
	}

	/**
	 * 
	 * @param financeMain
	 * @param finServInst
	 * @return
	 */
	private FinAssetAmtMovement prepareSanAmtMovement(FinanceMain financeMain, FinServiceInstruction finServInst) {

		FinAssetAmtMovement assetAmtMovt = new FinAssetAmtMovement();

		assetAmtMovt.setFinServiceInstID(finServInst.getServiceSeqId());
		assetAmtMovt.setFinReference(financeMain.getFinReference());
		assetAmtMovt.setMovementDate(SysParamUtil.getAppDate());

		// TODO : Not Always 1
		assetAmtMovt.setMovementOrder(1);

		// DS : DownSizing, US : UpSizing
		assetAmtMovt.setMovementType(FinanceConstants.MOVEMENTTYPE_DOWNSIZING);

		BigDecimal rvsdSanAmount = financeMain.getFinAssetValue().subtract(finServInst.getAmount());

		// DownSizing Amount
		assetAmtMovt.setMovementAmount(finServInst.getAmount());
		assetAmtMovt.setRevisedSanctionedAmt(rvsdSanAmount);
		assetAmtMovt.setSanctionedAmt(financeMain.getFinAssetValue());
		assetAmtMovt.setDisbursedAmt(financeMain.getFinCurrAssetValue());
		assetAmtMovt.setAvailableAmt(rvsdSanAmount.subtract(financeMain.getFinCurrAssetValue()));

		assetAmtMovt.setLastMntBy(financeMain.getLastMntBy());
		assetAmtMovt.setLastMntOn(financeMain.getLastMntOn());

		return assetAmtMovt;
	}

	/**
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, PennantConstants.method_doReject);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = finScheduleData.getFinanceMain();

		getFinanceMainDAO().delete(financeMain, TableType.TEMP_TAB, false, true);
		finServiceInstructionDAO.deleteList(financeMain.getFinReference(), FinanceConstants.FINSER_EVENT_LOANDOWNSIZING,
				"_Temp");

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				financeMain.getBefImage(), financeMain));

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
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
	 * Method for Validate Finance Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @param isWIF
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");

		FinanceDetail financeDetail = (FinanceDetail) auditDetail.getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];

		valueParm[0] = financeMain.getFinReference();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + " : " + valueParm[0];

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if (StringUtils.equals(PennantConstants.method_doApprove, method)
				&& !PennantConstants.RECORD_TYPE_NEW.equals(financeMain.getRecordType())) {
			auditDetail.setBefImage(getFinanceMainDAO().getFinanceMainById(financeMain.getFinReference(), "", false));
		}

		return auditDetail;
	}

	// setters / getters

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public FinServiceInstrutionDAO getFinServiceInstructionDAO() {
		return finServiceInstructionDAO;
	}

	public void setFinServiceInstructionDAO(FinServiceInstrutionDAO finServiceInstructionDAO) {
		this.finServiceInstructionDAO = finServiceInstructionDAO;
	}

	public ChangeGraceEndService getChangeGraceEndService() {
		return changeGraceEndService;
	}

	public void setChangeGraceEndService(ChangeGraceEndService changeGraceEndService) {
		this.changeGraceEndService = changeGraceEndService;
	}

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public FinLogEntryDetailDAO getFinLogEntryDetailDAO() {
		return finLogEntryDetailDAO;
	}

	public void setFinLogEntryDetailDAO(FinLogEntryDetailDAO finLogEntryDetailDAO) {
		this.finLogEntryDetailDAO = finLogEntryDetailDAO;
	}

	public FinanceProfitDetailDAO getProfitDetailsDAO() {
		return profitDetailsDAO;
	}

	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

	public RepayInstructionDAO getRepayInstructionDAO() {
		return repayInstructionDAO;
	}

	public void setRepayInstructionDAO(RepayInstructionDAO repayInstructionDAO) {
		this.repayInstructionDAO = repayInstructionDAO;
	}

	public FinanceDisbursementDAO getFinanceDisbursementDAO() {
		return financeDisbursementDAO;
	}

	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	public FinODPenaltyRateDAO getFinODPenaltyRateDAO() {
		return finODPenaltyRateDAO;
	}

	public void setFinODPenaltyRateDAO(FinODPenaltyRateDAO finODPenaltyRateDAO) {
		this.finODPenaltyRateDAO = finODPenaltyRateDAO;
	}

	public SubventionDetailDAO getSubventionDetailDAO() {
		return subventionDetailDAO;
	}

	public void setSubventionDetailDAO(SubventionDetailDAO subventionDetailDAO) {
		this.subventionDetailDAO = subventionDetailDAO;
	}

	public FinAssetAmtMovementDAO getFinAssetAmtMovementDAO() {
		return finAssetAmtMovementDAO;
	}

	public void setFinAssetAmtMovementDAO(FinAssetAmtMovementDAO finAssetAmtMovementDAO) {
		this.finAssetAmtMovementDAO = finAssetAmtMovementDAO;
	}
}
