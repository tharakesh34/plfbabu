package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.ChangeTDSDAO;
import com.pennant.backend.dao.finance.FinMaintainInstructionDAO;
import com.pennant.backend.dao.finance.FinServiceInstrutionDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.LowerTaxDeductionDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinMaintainInstruction;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.LowerTaxDeduction;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.ChangeTDSService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;

public class ChangeTDSServiceImpl extends GenericService<FinMaintainInstruction> implements ChangeTDSService {
	private static final Logger logger = LogManager.getLogger(ChangeTDSServiceImpl.class);

	private ChangeTDSDAO changeTDSDAO;
	private FinServiceInstrutionDAO finServiceInstructionDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceMainDAO financeMainDAO;
	private FinMaintainInstructionDAO finMaintainInstructionDAO;
	private AuditHeaderDAO auditHeaderDAO;
	private LowerTaxDeductionDAO lowerTaxDeductionDAO;

	public ChangeTDSServiceImpl() {
		super();
	}

	@Override
	public FinanceMain getFinanceBasicDetailByRef(long finID) {
		return changeTDSDAO.getFinanceBasicDetailByRef(finID);
	}

	@Override
	public boolean isTDSCheck(String reference, Date appDate) {
		return changeTDSDAO.isTDSCheck(reference, appDate);
	}

	public Date getInstallmentDate(String reference, Date appDate) {
		return changeTDSDAO.getInstallmentDate(reference, appDate);
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FinMaintainInstruction fmi = (FinMaintainInstruction) auditHeader.getAuditDetail().getModelData();

		String finReference = fmi.getFinReference();

		String table = "_View";
		if (!fmi.isNewRecord()) {
			table = "_Temp";
		}

		FinanceMain fm = financeMainDAO.getFinanceMainByRef(finReference, table, false);
		long finID = fm.getFinID();

		FinScheduleData schdData = new FinScheduleData();
		List<FinanceScheduleDetail> schedules = null;
		schedules = financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false);
		schdData.setFinanceMain(fm);
		schdData.setFinanceScheduleDetails(schedules);

		TableType tableType = TableType.MAIN_TAB;
		if (fmi.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (tableType == TableType.MAIN_TAB) {
			fm.setRcdMaintainSts("");
		}
		FinanceMain befImage = new FinanceMain();
		BeanUtils.copyProperties(fm, befImage);
		fm.setBefImage(befImage);

		fm.setNextRoleCode(fmi.getNextRoleCode());
		fm.setRecordStatus(fmi.getRecordStatus());
		fm.setRcdMaintainSts(FinServiceEvent.CHANGETDS);
		fm.setRoleCode(fmi.getRoleCode());
		fm.setTaskId(fmi.getTaskId());
		fm.setNextTaskId(fmi.getNextTaskId());
		fm.setWorkflowId(fmi.getWorkflowId());
		fm.setLastMntOn(fmi.getLastMntOn());
		fm.setLastMntBy(fmi.getLastMntBy());
		fm.setVersion(fmi.getVersion());

		FinServiceInstruction inst = new FinServiceInstruction();
		inst.setFinEvent(FinServiceEvent.CHANGETDS);
		inst.setFinID(fm.getFinID());
		inst.setFinReference(fm.getFinReference());
		inst.setMaker(auditHeader.getAuditUsrId());
		inst.setMakerAppDate(SysParamUtil.getAppDate());
		inst.setMakerSysDate(DateUtil.getSysDate());
		inst.setLinkedTranID(0);

		List<LowerTaxDeduction> ltdList = new ArrayList<LowerTaxDeduction>();

		LowerTaxDeduction lowerTaxDeduction = new LowerTaxDeduction();
		lowerTaxDeduction.setFinID(fm.getFinID());
		lowerTaxDeduction.setFinReference(fm.getFinReference());
		lowerTaxDeduction.setStartDate(fmi.getTdsStartDate());
		lowerTaxDeduction.setEndDate(fmi.getTdsEndDate());
		lowerTaxDeduction.setPercentage(fmi.getTdsPercentage());
		lowerTaxDeduction.setLimitAmt(fmi.getTdsLimit());
		ltdList.add(lowerTaxDeduction);

		schdData.setLowerTaxDeductionDetails(ltdList);

		finServiceInstructionDAO.deleteList(finID, FinServiceEvent.CHANGETDS, tableType.getSuffix());

		if (fmi.isNewRecord()) {
			financeMainDAO.save(fm, tableType, false);
			fmi.setFinMaintainId(Long.parseLong(finMaintainInstructionDAO.save(fmi, tableType)));
			schdData.getLowerTaxDeductionDetails().get(0).setFinMaintainId(fmi.getFinMaintainId());
			lowerTaxDeductionDAO.save(schdData.getLowerTaxDeductionDetails().get(0), tableType.getSuffix());
			auditHeader.getAuditDetail().setModelData(fmi);
			auditHeader.setAuditReference(String.valueOf(fmi.getFinMaintainId()));
		} else {
			financeMainDAO.update(fm, tableType, false);
			finMaintainInstructionDAO.update(fmi, tableType);
			lowerTaxDeductionDAO.update(schdData.getLowerTaxDeductionDetails().get(0), tableType.getSuffix());

		}
		finServiceInstructionDAO.save(inst, tableType.getSuffix());

		// Add Audit
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;

	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method, false);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FinMaintainInstruction fmi = (FinMaintainInstruction) auditHeader.getAuditDetail().getModelData();

		FinanceMain fm = new FinanceMain();
		fm.setFinID(fmi.getFinID());
		fm.setFinReference(fmi.getFinReference());

		financeMainDAO.deleteFinreference(fm, TableType.TEMP_TAB, false, false);

		finMaintainInstructionDAO.delete(fmi, TableType.MAIN_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		FinMaintainInstruction fmi = new FinMaintainInstruction();
		long finID = fmi.getFinID();

		BeanUtils.copyProperties((FinMaintainInstruction) auditHeader.getAuditDetail().getModelData(), fmi);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(fmi.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(finMaintainInstructionDAO.getFinMaintainInstructionById(fmi.getFinMaintainId(), ""));
		}

		if (fmi.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {

			tranType = PennantConstants.TRAN_DEL;
			finMaintainInstructionDAO.delete(fmi, TableType.MAIN_TAB);

		} else {

			fmi.setRoleCode("");
			fmi.setNextRoleCode("");
			fmi.setTaskId("");
			fmi.setNextTaskId("");
			fmi.setWorkflowId(0);

			if (fmi.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				fmi.setRecordType("");
				finMaintainInstructionDAO.save(fmi, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				fmi.setRecordType("");
				finMaintainInstructionDAO.update(fmi, TableType.MAIN_TAB);
			}
		}

		// calculate TDS amount from application date
		FinScheduleData schdData = calScheduleTDSAmount(fmi);

		// update TdsAmount and TDS Applicable flag in schedule details
		financeScheduleDetailDAO.updateTDS(schdData.getFinanceScheduleDetails());

		// save FinInstruction to maintain records
		FinServiceInstruction fsi = null;
		List<FinServiceInstruction> fsiList = finServiceInstructionDAO.getFinServiceInstructions(finID, "_Temp",
				FinServiceEvent.CHANGETDS);
		Date appDate = SysParamUtil.getAppDate();

		if (fsiList.size() > 0) {
			fsi = fsiList.get(0);
			fsi.setChecker(auditHeader.getAuditUsrId());
			fsi.setCheckerAppDate(appDate);
			fsi.setCheckerSysDate(DateUtil.getSysDate());
		} else {
			fsi = new FinServiceInstruction();
			fsi.setFinID(fmi.getFinID());
			fsi.setFinReference(fmi.getFinReference());
			fsi.setFromDate(appDate);
			fsi.setFinEvent(FinServiceEvent.CHANGETDS);
			fsi.setChecker(auditHeader.getAuditUsrId());
			fsi.setCheckerAppDate(appDate);
			fsi.setCheckerSysDate(DateUtil.getSysDate());
			fsi.setMaker(auditHeader.getAuditUsrId());
			fsi.setMakerAppDate(appDate);
			fsi.setMakerSysDate(DateUtil.getSysDate());
			fsi.setLinkedTranID(0);
		}

		finServiceInstructionDAO.deleteList(finID, "", TableType.TEMP_TAB.getSuffix());
		finServiceInstructionDAO.save(fsi, "");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinID(fmi.getFinID());
		financeMain.setFinReference(fmi.getFinReference());
		financeMainDAO.deleteFinreference(financeMain, TableType.TEMP_TAB, false, true);

		financeMain.setTDSApplicable(fmi.istDSApplicable());
		// update tdsApplicable in FinanceMain table
		financeMainDAO.updateTdsApplicable(financeMain);

		finMaintainInstructionDAO.delete(fmi, TableType.TEMP_TAB);

		for (LowerTaxDeduction deductions : schdData.getLowerTaxDeductionDetails()) {
			if (deductions.getFinMaintainId() == fmi.getId()) {
				lowerTaxDeductionDAO.delete(deductions, TableType.TEMP_TAB.getSuffix());
			}
		}

		for (LowerTaxDeduction deductions : schdData.getLowerTaxDeductionDetails()) {
			if (deductions.getFinMaintainId() == fmi.getId()) {
				deductions.setRoleCode(fmi.getRoleCode());
				deductions.setNextRoleCode(fmi.getNextRoleCode());
				deductions.setRecordStatus(fmi.getRecordStatus());
				deductions.setRecordType(fmi.getRecordType());
				deductions.setTaskId(fmi.getTaskId());
				deductions.setNextTaskId(fmi.getNextTaskId());
				deductions.setWorkflowId(fmi.getWorkflowId());
				deductions.setLastMntOn(fmi.getLastMntOn());
				deductions.setLastMntBy(fmi.getLastMntBy());
				deductions.setVersion(fmi.getVersion());
				deductions.setWorkflowId(fmi.getWorkflowId());
				lowerTaxDeductionDAO.save(deductions, "");
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1,
				auditHeader.getAuditDetail().getBefImage(), auditHeader.getAuditDetail().getModelData()));

		auditHeaderDAO.addAudit(auditHeader);

		// Audit for Before And After Images
		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetails(auditDetails);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(fmi);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fmi.getBefImage(), fmi));

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doReject");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FinMaintainInstruction fmi = (FinMaintainInstruction) auditHeader.getAuditDetail().getModelData();

		long finID = fmi.getFinID();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinID(fmi.getFinID());
		financeMain.setFinReference(fmi.getFinReference());
		financeMainDAO.deleteFinreference(financeMain, TableType.TEMP_TAB, false, false);

		finServiceInstructionDAO.deleteList(finID, FinServiceEvent.CHANGETDS, TableType.TEMP_TAB.getSuffix());

		finMaintainInstructionDAO.delete(fmi, TableType.TEMP_TAB);

		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fmi.getBefImage(), fmi));

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FinMaintainInstruction fmi = (FinMaintainInstruction) auditHeader.getAuditDetail().getModelData();

		fmi.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(fmi);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug("Leaving ");
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method,
			boolean isUniqueCheckReq) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		FinMaintainInstruction fmi = (FinMaintainInstruction) auditDetail.getModelData();

		long finID = fmi.getFinID();

		// Check the unique keys.
		if (isUniqueCheckReq && fmi.isNewRecord() && finMaintainInstructionDAO.isDuplicateKey(fmi.getEvent(), finID,
				fmi.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];
			parameters[0] = PennantJavaUtil.getLabel("label_FinMaintainInstruction_Event") + ": " + fmi.getEvent();
			parameters[1] = PennantJavaUtil.getLabel("label_FinReference") + " : " + fmi.getFinReference();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	private FinScheduleData calScheduleTDSAmount(FinMaintainInstruction fmi) {
		logger.debug(Literal.ENTERING);

		Date appDate = SysParamUtil.getAppDate();
		FinScheduleData schdData = new FinScheduleData();

		long finID = fmi.getFinID();
		FinanceMain fm = financeMainDAO.getFinanceMainById(finID, "", false);

		List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false);

		fm.setEventFromDate(appDate);

		if (fmi.istDSApplicable()) {
			fm.setTDSApplicable(fmi.istDSApplicable());
		}

		schdData.setFinanceMain(fm);
		schdData.setFinanceScheduleDetails(schedules);

		schdData.setLowerTaxDeductionDetails(lowerTaxDeductionDAO.getLowerTaxDeductionDetails(finID, ""));

		List<LowerTaxDeduction> ltdList = new ArrayList<>();

		LowerTaxDeduction ltd = new LowerTaxDeduction();
		ltd.setFinID(fm.getFinID());
		ltd.setFinReference(fm.getFinReference());
		ltd.setStartDate(fmi.getTdsStartDate());
		ltd.setEndDate(fmi.getTdsEndDate());
		ltd.setPercentage(fmi.getTdsPercentage());
		ltd.setFinMaintainId(fmi.getFinMaintainId());
		ltd.setLimitAmt(fmi.getTdsLimit());
		ltdList.add(ltd);

		schdData.getLowerTaxDeductionDetails().addAll(ltdList);

		// PSD - 199558
		Date eventFromDate = null;
		for (FinanceScheduleDetail curSchd : schdData.getFinanceScheduleDetails()) {

			// Back Dated Schedules
			if (curSchd.getSchDate().compareTo(appDate) <= 0) {
				continue;
			}

			// Presented Schedules, Freezing Period
			if (curSchd.getPresentmentId() > 0) {
				continue;
			}

			// Future Paid Schedules
			if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0
					|| curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0) {

				continue;
			}

			if (eventFromDate == null) {
				eventFromDate = curSchd.getSchDate();
			}

			curSchd.setTDSApplicable(fmi.istDSApplicable());
		}

		fm.setEventFromDate(eventFromDate);
		schdData = ScheduleCalculator.procReCalTDSAmount(schdData);

		logger.debug(Literal.LEAVING);
		return schdData;
	}

	public void setChangeTDSDAO(ChangeTDSDAO changeTDSDAO) {
		this.changeTDSDAO = changeTDSDAO;
	}

	public void setFinServiceInstructionDAO(FinServiceInstrutionDAO finServiceInstructionDAO) {
		this.finServiceInstructionDAO = finServiceInstructionDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setFinMaintainInstructionDAO(FinMaintainInstructionDAO finMaintainInstructionDAO) {
		this.finMaintainInstructionDAO = finMaintainInstructionDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setLowerTaxDeductionDAO(LowerTaxDeductionDAO lowerTaxDeductionDAO) {
		this.lowerTaxDeductionDAO = lowerTaxDeductionDAO;
	}
}