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
 *
 * FileName : LoanDownSizingServiceImpl.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 16-11-2018 *
 * 
 * Modified Date : 16-11-2018 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 16-11-2018 Pennant 0.1 * * * * * * * * *
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

	@Override
	public FinanceDetail getDownSizingFinance(FinanceMain fm, String rcdMaintainSts) {
		logger.debug(Literal.ENTERING);

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		// Finance Details
		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData finScheduleData = getFinSchDataByFinRef(finReference, "", 0);

		List<FinServiceInstruction> finservInstList = new ArrayList<FinServiceInstruction>();
		if (StringUtils.isNotBlank(rcdMaintainSts)) {
			finservInstList = finServiceInstructionDAO.getFinServiceInstructions(finID, "_Temp", rcdMaintainSts);
		} else {
			finservInstList.add(new FinServiceInstruction());
		}

		// TODO : Customer Details
		financeDetail.setCustomerDetails(getCustomerDetails(fm));

		finScheduleData.setFinServiceInstructions(finservInstList);
		financeDetail.setFinScheduleData(finScheduleData);

		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	private CustomerDetails getCustomerDetails(FinanceMain fm) {
		CustomerDetails cd = new CustomerDetails();
		cd.setCustID(fm.getCustID());

		Customer customer = new Customer();
		customer.setCustID(fm.getCustID());
		customer.setCustCIF(fm.getLovDescCustCIF());
		customer.setCustShrtName(fm.getLovDescCustShrtName());

		cd.setCustomer(customer);
		return cd;
	}

	public FinScheduleData getFinSchDataByFinRef(long finID, String type, long logKey) {
		logger.debug(Literal.ENTERING);

		FinScheduleData finSchData = new FinScheduleData();

		if (logKey == 0) {
			FinanceMain fm = financeMainDAO.getFinanceMainById(finID, "_View", false);
			finSchData.setFinReference(fm.getFinReference());
			finSchData.setFinanceMain(fm);

			String finType = fm.getFinType();
			finSchData.setFinanceType(financeTypeDAO.getFinanceTypeByID(finType, "_AView"));
		}

		// Schedule, Disbursement Details and Repay Instructions
		finSchData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finID, type, false));
		finSchData.setDisbursementDetails(financeDisbursementDAO.getFinanceDisbursementDetails(finID, type, false));
		finSchData.setRepayInstructions(repayInstructionDAO.getRepayInstructions(finID, type, false));

		logger.debug(Literal.LEAVING);

		return finSchData;
	}

	public FinScheduleData changeGraceEndAfterFullDisb(FinScheduleData schdData) {
		FinanceMain fm = schdData.getFinanceMain();
		fm.setEventFromDate(SysParamUtil.getAppDate());

		changeGraceEndService.changeGraceEnd(schdData, true);

		return schdData;
	}

	public AEEvent getChangeGrcEndPostings(FinScheduleData schdData) throws Exception {
		return changeGraceEndService.getChangeGrcEndPostings(schdData);

	}

	public List<FinAssetAmtMovement> getFinAssetAmtMovements(long finID, String movementType) {
		return finAssetAmtMovementDAO.getFinAssetAmtMovements(finID, movementType);
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, PennantConstants.method_saveOrUpdate);
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FinanceDetail fd = (FinanceDetail) auditHeader.getAuditDetail().getModelData();

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		FinServiceInstruction finServInst = schdData.getFinServiceInstructions().get(0);

		String rcdMaintainSts = "";
		TableType tableType = TableType.MAIN_TAB;

		if (fm.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
			rcdMaintainSts = FinServiceEvent.LOANDOWNSIZING;
		}
		fm.setRcdMaintainSts(rcdMaintainSts);

		if (fm.isNewRecord()) {

			financeMainDAO.save(fm, tableType, false);
			finServiceInstructionDAO.save(finServInst, tableType.getSuffix());

			auditHeader.setAuditReference(fm.getFinReference());
			auditHeader.getAuditDetail().setModelData(fm);

		} else {
			financeMainDAO.update(fm, tableType, false);

			finServiceInstructionDAO.deleteList(fm.getFinID(), rcdMaintainSts, tableType.getSuffix());
			finServiceInstructionDAO.save(finServInst, tableType.getSuffix());

		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), fm.getExcludeFields());
		String auditTranType = auditHeader.getAuditTranType();
		auditHeader.setAuditDetail(new AuditDetail(auditTranType, 1, fields[0], fields[1], fm.getBefImage(), fm));

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

		FinanceDetail fd = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		fd.setModuleDefiner(FinServiceEvent.LOANDOWNSIZING);

		FinScheduleData schdData = fd.getFinScheduleData();
		FinServiceInstruction finServInst = schdData.getFinServiceInstructions().get(0);
		BigDecimal downSizingAmt = finServInst.getAmount();

		FinanceMain fdm = new FinanceMain();
		BeanUtils.copyProperties(schdData.getFinanceMain(), fdm);

		fdm.setRcdMaintainSts("");
		fdm.setRoleCode("");
		fdm.setNextRoleCode("");
		fdm.setTaskId("");
		fdm.setNextTaskId("");
		fdm.setWorkflowId(0);

		// Revised FinAssetValue Value
		FinAssetAmtMovement assetAmtMovt = prepareSanAmtMovement(fdm, finServInst);
		fdm.setFinAssetValue(fdm.getFinAssetValue().subtract(downSizingAmt));

		if (fdm.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
			tranType = PennantConstants.TRAN_ADD;
			fdm.setRecordType("");
		} else {
			tranType = PennantConstants.TRAN_UPD;
			fdm.setRecordType("");

			// FULL DISB : Schedule Changed By Grace Period Ended
			if (fdm.isScheduleRegenerated()) {

				// 1. Postings ----> Execute Accounting Details Process
				auditHeader = executeAccountingProcess(auditHeader, SysParamUtil.getAppDate());

				if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
					auditHeader.setErrorList(auditHeader.getErrorMessage());
					return auditHeader;
				}

				// 2. Data Saving ----> Schedule Tables
				processScheduleDetails(fd, finServInst);
				financeMainDAO.update(fdm, TableType.MAIN_TAB, false);
			} else {

				// Update Revised Sanctioned Amount and Save FinServiceInstructions
				finServiceInstructionDAO.save(finServInst, "");
				financeMainDAO.updateFinAssetValue(fdm);
			}
		}

		// Sanctioned Amount Movements
		finAssetAmtMovementDAO.saveFinAssetAmtMovement(assetAmtMovt);

		// Delete from Staging Tables
		financeMainDAO.delete(fdm, TableType.TEMP_TAB, false, true);
		finServiceInstructionDAO.deleteList(fdm.getFinID(), FinServiceEvent.LOANDOWNSIZING, "_Temp");

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), fdm.getExcludeFields());
		String auditTranType = auditHeader.getAuditTranType();
		auditHeader.setAuditDetail(new AuditDetail(auditTranType, 1, fields[0], fields[1], fdm.getBefImage(), fdm));

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(fdm);

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private void processScheduleDetails(FinanceDetail fd, FinServiceInstruction finServInst) {
		logger.debug(Literal.ENTERING);

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		String finReference = fm.getFinReference();

		// START : prepare FinLogEntryDetail data
		FinLogEntryDetail entryDetail = new FinLogEntryDetail();

		entryDetail.setReversalCompleted(false);
		entryDetail.setFinReference(finReference);
		entryDetail.setPostDate(SysParamUtil.getAppDate());
		entryDetail.setSchdlRecal(fm.isScheduleRegenerated());
		entryDetail.setEventAction(AccountingEvent.GRACEEND);

		long logKey = finLogEntryDetailDAO.save(entryDetail);
		// END

		// START : Fetch Existing data before Modification
		FinScheduleData oldFinSchdData = getFinSchDataByFinRef(finReference, "", -1);
		oldFinSchdData.setFinReference(finReference);
		oldFinSchdData.setFinanceMain(fm);

		// Save Schedule Details For Future Modifications
		listSave(oldFinSchdData, "_Log", false, logKey, finServInst.getServiceSeqId());

		// END

		// delete from Staging table and schedule tables delete and insert
		listDeletion(schdData, fd.getModuleDefiner(), "", false);
		finServiceInstructionDAO.deleteList(fm.getFinID(), fd.getModuleDefiner(), "");

		// save latest schedule details
		listSave(schdData, "", false, 0, finServInst.getServiceSeqId());

		logger.debug(Literal.LEAVING);
	}

	private FinAssetAmtMovement prepareSanAmtMovement(FinanceMain fm, FinServiceInstruction finServInst) {
		FinAssetAmtMovement assetAmtMovt = new FinAssetAmtMovement();

		assetAmtMovt.setFinServiceInstID(finServInst.getServiceSeqId());
		assetAmtMovt.setFinReference(fm.getFinReference());
		assetAmtMovt.setMovementDate(SysParamUtil.getAppDate());

		// TODO : Not Always 1
		assetAmtMovt.setMovementOrder(1);

		// DS : DownSizing, US : UpSizing
		assetAmtMovt.setMovementType(FinanceConstants.MOVEMENTTYPE_DOWNSIZING);

		BigDecimal rvsdSanAmount = fm.getFinAssetValue().subtract(finServInst.getAmount());

		// DownSizing Amount
		assetAmtMovt.setMovementAmount(finServInst.getAmount());
		assetAmtMovt.setRevisedSanctionedAmt(rvsdSanAmount);
		assetAmtMovt.setSanctionedAmt(fm.getFinAssetValue());
		assetAmtMovt.setDisbursedAmt(fm.getFinCurrAssetValue());
		assetAmtMovt.setAvailableAmt(rvsdSanAmount.subtract(fm.getFinCurrAssetValue()));

		assetAmtMovt.setLastMntBy(fm.getLastMntBy());
		assetAmtMovt.setLastMntOn(fm.getLastMntOn());

		return assetAmtMovt;
	}

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, PennantConstants.method_doReject);
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FinanceDetail fd = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		financeMainDAO.delete(fm, TableType.TEMP_TAB, false, true);
		finServiceInstructionDAO.deleteList(fm.getFinID(), FinServiceEvent.LOANDOWNSIZING, "_Temp");

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), fm.getExcludeFields());
		String auditTranType = auditHeader.getAuditTranType();
		auditHeader.setAuditDetail(new AuditDetail(auditTranType, 1, fields[0], fields[1], fm.getBefImage(), fm));

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

		FinanceDetail fd = (FinanceDetail) auditDetail.getModelData();
		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];

		valueParm[0] = fm.getFinReference();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + " : " + valueParm[0];

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if (StringUtils.equals(PennantConstants.method_doApprove, method)
				&& !PennantConstants.RECORD_TYPE_NEW.equals(fm.getRecordType())) {
			auditDetail.setBefImage(financeMainDAO.getFinanceMainById(fm.getFinID(), "", false));
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
