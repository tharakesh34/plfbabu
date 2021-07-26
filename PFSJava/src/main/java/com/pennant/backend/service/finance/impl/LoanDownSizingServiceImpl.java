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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jaxen.JaxenException;
import org.springframework.beans.BeanUtils;

import com.pennant.app.core.ChangeGraceEndService;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinAssetAmtMovementDAO;
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
import com.pennant.backend.service.finance.GenericFinanceDetailService;
import com.pennant.backend.service.finance.LoanDownSizingService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;

public class LoanDownSizingServiceImpl extends GenericFinanceDetailService implements LoanDownSizingService {
	private static final Logger logger = LogManager.getLogger(LoanDownSizingServiceImpl.class);

	private ChangeGraceEndService changeGraceEndService;
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

	public FinScheduleData getFinSchDataByFinRef(String finRef, String type, long logKey) {
		logger.debug(Literal.ENTERING);

		FinScheduleData finSchData = new FinScheduleData();
		finSchData.setFinReference(finRef);

		if (logKey == 0) {
			FinanceMain financeMain = financeMainDAO.getFinanceMainById(finRef, "_View", false);
			finSchData.setFinanceMain(financeMain);

			String finType = financeMain.getFinType();
			finSchData.setFinanceType(financeTypeDAO.getFinanceTypeByID(finType, "_AView"));
		}

		// Schedule, Disbursement Details and Repay Instructions
		finSchData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finRef, type, false));
		finSchData.setDisbursementDetails(financeDisbursementDAO.getFinanceDisbursementDetails(finRef, type, false));
		finSchData.setRepayInstructions(repayInstructionDAO.getRepayInstructions(finRef, type, false));

		logger.debug(Literal.LEAVING);
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

		changeGraceEndService.changeGraceEnd(finScheduleData, true);

		return finScheduleData;
	}

	public AEEvent getChangeGrcEndPostings(FinScheduleData finScheduleData) throws Exception {
		return changeGraceEndService.getChangeGrcEndPostings(finScheduleData);

	}

	public List<FinAssetAmtMovement> getFinAssetAmtMovements(String finRef, String movementType) {
		return finAssetAmtMovementDAO.getFinAssetAmtMovements(finRef, movementType);
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, PennantConstants.method_saveOrUpdate);
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
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
			rcdMaintainSts = FinServiceEvent.LOANDOWNSIZING;
		}
		financeMain.setRcdMaintainSts(rcdMaintainSts);

		if (financeMain.isNew()) {

			financeMainDAO.save(financeMain, tableType, false);
			finServiceInstructionDAO.save(finServInst, tableType.getSuffix());

			auditHeader.setAuditReference(financeMain.getFinReference());
			auditHeader.getAuditDetail().setModelData(financeMain);

		} else {
			financeMainDAO.update(financeMain, tableType, false);

			finServiceInstructionDAO.deleteList(financeMain.getFinReference(), rcdMaintainSts, tableType.getSuffix());
			finServiceInstructionDAO.save(finServInst, tableType.getSuffix());

		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				financeMain.getBefImage(), financeMain));

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) throws InterfaceException, JaxenException {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, PennantConstants.method_doApprove);
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		financeDetail.setModuleDefiner(FinServiceEvent.LOANDOWNSIZING);

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
				financeMainDAO.update(financeMain, TableType.MAIN_TAB, false);
			} else {

				// Update Revised Sanctioned Amount and Save FinServiceInstructions
				finServiceInstructionDAO.save(finServInst, "");
				financeMainDAO.updateFinAssetValue(financeMain);
			}
		}

		// Sanctioned Amount Movements
		finAssetAmtMovementDAO.saveFinAssetAmtMovement(assetAmtMovt);

		// Delete from Staging Tables
		financeMainDAO.delete(financeMain, TableType.TEMP_TAB, false, true);
		finServiceInstructionDAO.deleteList(financeMain.getFinReference(), FinServiceEvent.LOANDOWNSIZING,
				"_Temp");

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				financeMain.getBefImage(), financeMain));

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(financeMain);

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * Delete and Save Schedule dependent tables
	 * 
	 * @param financeDetail
	 * @param finServInst
	 */
	private void processScheduleDetails(FinanceDetail financeDetail, FinServiceInstruction finServInst) {
		logger.debug(Literal.ENTERING);

		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		String finReference = financeMain.getFinReference();

		// START : prepare FinLogEntryDetail data
		FinLogEntryDetail entryDetail = new FinLogEntryDetail();

		entryDetail.setReversalCompleted(false);
		entryDetail.setFinReference(finReference);
		entryDetail.setPostDate(SysParamUtil.getAppDate());
		entryDetail.setSchdlRecal(financeMain.isScheduleRegenerated());
		entryDetail.setEventAction(AccountingEvent.GRACEEND);

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

		logger.debug(Literal.LEAVING);
	}

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

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, PennantConstants.method_doReject);
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = finScheduleData.getFinanceMain();

		financeMainDAO.delete(financeMain, TableType.TEMP_TAB, false, true);
		finServiceInstructionDAO.deleteList(financeMain.getFinReference(), FinServiceEvent.LOANDOWNSIZING,
				"_Temp");

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				financeMain.getBefImage(), financeMain));

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);

		FinanceDetail financeDetail = (FinanceDetail) auditDetail.getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];

		valueParm[0] = financeMain.getFinReference();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + " : " + valueParm[0];

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if (StringUtils.equals(PennantConstants.method_doApprove, method)
				&& !PennantConstants.RECORD_TYPE_NEW.equals(financeMain.getRecordType())) {
			auditDetail.setBefImage(financeMainDAO.getFinanceMainById(financeMain.getFinReference(), "", false));
		}

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	public void setChangeGraceEndService(ChangeGraceEndService changeGraceEndService) {
		this.changeGraceEndService = changeGraceEndService;
	}

	public void setFinAssetAmtMovementDAO(FinAssetAmtMovementDAO finAssetAmtMovementDAO) {
		this.finAssetAmtMovementDAO = finAssetAmtMovementDAO;
	}
}
