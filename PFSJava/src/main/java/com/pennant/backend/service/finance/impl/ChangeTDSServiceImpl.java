package com.pennant.backend.service.finance.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.DateUtility;
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
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
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
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;

public class ChangeTDSServiceImpl extends GenericService<FinMaintainInstruction> implements ChangeTDSService {
	private static final Logger logger = LogManager.getLogger(ChangeTDSServiceImpl.class);

	private ChangeTDSDAO changeTDSDAO;
	private FinServiceInstrutionDAO finServiceInstructionDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceMainDAO financeMainDAO;
	private FinanceTypeDAO financeTypeDAO;
	private FinMaintainInstructionDAO finMaintainInstructionDAO;
	private AuditHeaderDAO auditHeaderDAO;
	private LowerTaxDeductionDAO lowertaxDeductionDAO;

	public ChangeTDSServiceImpl() {
		super();
	}

	@Override
	public FinanceMain getFinanceBasicDetailByRef(String finReference) {
		return getChangeTDSDAO().getFinanceBasicDetailByRef(finReference);
	}

	@Override
	public boolean isTDSCheck(String reference, Date appDate) {
		return getChangeTDSDAO().isTDSCheck(reference, appDate);
	}

	public Date getInstallmentDate(String reference, Date appDate) {
		return getChangeTDSDAO().getInstallmentDate(reference, appDate);
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * FinMaintainInstructions/BMTFinMaintainInstructions_Temp by using FinMaintainInstructionDAO's save method b)
	 * Update the Record in the table. based on the module workFlow Configuration. by using FinMaintainInstructionDAO's
	 * update method 3) Audit the record in to AuditHeader and AdtBMTFinMaintainInstructions by using
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

		FinMaintainInstruction finMaintainInstruction = (FinMaintainInstruction) auditHeader.getAuditDetail()
				.getModelData();

		FinanceMain financeMain = new FinanceMain();
		if (finMaintainInstruction.isNewRecord()) {
			financeMain = getFinanceMainDAO().getFinanceMainById(finMaintainInstruction.getFinReference(), "_View",
					false);
		} else {
			financeMain = getFinanceMainDAO().getFinanceMainById(finMaintainInstruction.getFinReference(), "_Temp",
					false);
		}

		FinScheduleData finScheduleData = new FinScheduleData();
		List<FinanceScheduleDetail> finScheduleList = getFinanceScheduleDetailDAO()
				.getFinScheduleDetails(finMaintainInstruction.getFinReference(), "", false);
		finScheduleData.setFinanceMain(financeMain);
		finScheduleData.setFinanceScheduleDetails(finScheduleList);

		TableType tableType = TableType.MAIN_TAB;
		if (finMaintainInstruction.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (tableType == TableType.MAIN_TAB) {
			financeMain.setRcdMaintainSts("");
		}
		FinanceMain befImage = new FinanceMain();
		BeanUtils.copyProperties(financeMain, befImage);
		financeMain.setBefImage(befImage);

		financeMain.setNextRoleCode(finMaintainInstruction.getNextRoleCode());
		financeMain.setRecordStatus(finMaintainInstruction.getRecordStatus());
		financeMain.setRcdMaintainSts(FinServiceEvent.CHANGETDS);
		financeMain.setRoleCode(finMaintainInstruction.getRoleCode());
		financeMain.setTaskId(finMaintainInstruction.getTaskId());
		financeMain.setNextTaskId(finMaintainInstruction.getNextTaskId());
		financeMain.setWorkflowId(finMaintainInstruction.getWorkflowId());
		financeMain.setLastMntOn(finMaintainInstruction.getLastMntOn());
		financeMain.setLastMntBy(finMaintainInstruction.getLastMntBy());
		financeMain.setVersion(finMaintainInstruction.getVersion());

		FinServiceInstruction inst = new FinServiceInstruction();
		inst.setFinEvent(FinServiceEvent.CHANGETDS);
		inst.setFinReference(financeMain.getFinReference());
		inst.setMaker(auditHeader.getAuditUsrId());
		inst.setMakerAppDate(DateUtility.getAppDate());
		inst.setMakerSysDate(DateUtility.getSysDate());
		inst.setLinkedTranId(0);

		List<LowerTaxDeduction> ltdList = new ArrayList<LowerTaxDeduction>();

		LowerTaxDeduction lowerTaxDeduction = new LowerTaxDeduction();
		lowerTaxDeduction.setFinReference(financeMain.getFinReference());
		lowerTaxDeduction.setStartDate(finMaintainInstruction.getTdsStartDate());
		lowerTaxDeduction.setEndDate(finMaintainInstruction.getTdsEndDate());
		lowerTaxDeduction.setPercentage(finMaintainInstruction.getTdsPercentage());
		lowerTaxDeduction.setLimitAmt(finMaintainInstruction.getTdsLimit());
		ltdList.add(lowerTaxDeduction);

		finScheduleData.setLowerTaxDeductionDetails(ltdList);

		getFinServiceInstructionDAO().deleteList(financeMain.getFinReference(), FinServiceEvent.CHANGETDS,
				tableType.getSuffix());

		if (finMaintainInstruction.isNewRecord()) {
			getFinanceMainDAO().save(financeMain, tableType, false);
			finMaintainInstruction.setFinMaintainId(
					Long.parseLong(getFinMaintainInstructionDAO().save(finMaintainInstruction, tableType)));
			finScheduleData.getLowerTaxDeductionDetails().get(0)
					.setFinMaintainId(finMaintainInstruction.getFinMaintainId());
			getLowertaxDeductionDAO().save(finScheduleData.getLowerTaxDeductionDetails().get(0), tableType.getSuffix());
			auditHeader.getAuditDetail().setModelData(finMaintainInstruction);
			auditHeader.setAuditReference(String.valueOf(finMaintainInstruction.getFinMaintainId()));
		} else {
			getFinanceMainDAO().update(financeMain, tableType, false);
			getFinMaintainInstructionDAO().update(finMaintainInstruction, tableType);
			getLowertaxDeductionDAO().update(finScheduleData.getLowerTaxDeductionDetails().get(0),
					tableType.getSuffix());

		}
		finServiceInstructionDAO.save(inst, tableType.getSuffix());

		// Add Audit
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method, false);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader = nextProcess(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * FinMaintainInstructions by using FinMaintainInstructionDAO's delete method with type as Blank 3) Audit the record
	 * in to AuditHeader and AdtBMTFinMaintainInstructions by using auditHeaderDAO.addAudit(auditHeader)
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

		FinMaintainInstruction finMaintainInstruction = (FinMaintainInstruction) auditHeader.getAuditDetail()
				.getModelData();

		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finMaintainInstruction.getFinReference());

		getFinanceMainDAO().deleteFinreference(financeMain, TableType.TEMP_TAB, false, false);

		getFinMaintainInstructionDAO().delete(finMaintainInstruction, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		FinMaintainInstruction finMaintainInstruction = new FinMaintainInstruction();
		BeanUtils.copyProperties((FinMaintainInstruction) auditHeader.getAuditDetail().getModelData(),
				finMaintainInstruction);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(finMaintainInstruction.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(finMaintainInstructionDAO
					.getFinMaintainInstructionById(finMaintainInstruction.getFinMaintainId(), ""));
		}

		if (finMaintainInstruction.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {

			tranType = PennantConstants.TRAN_DEL;
			getFinMaintainInstructionDAO().delete(finMaintainInstruction, TableType.MAIN_TAB);

		} else {

			finMaintainInstruction.setRoleCode("");
			finMaintainInstruction.setNextRoleCode("");
			finMaintainInstruction.setTaskId("");
			finMaintainInstruction.setNextTaskId("");
			finMaintainInstruction.setWorkflowId(0);

			if (finMaintainInstruction.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				finMaintainInstruction.setRecordType("");
				getFinMaintainInstructionDAO().save(finMaintainInstruction, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				finMaintainInstruction.setRecordType("");
				getFinMaintainInstructionDAO().update(finMaintainInstruction, TableType.MAIN_TAB);
			}
		}

		//calculate TDS amount from application date 
		FinScheduleData finScheduleData = calScheduleTDSAmount(finMaintainInstruction);

		//update TdsAmount and TDS Applicable flag in schedule details 
		getFinanceScheduleDetailDAO().updateTDS(finScheduleData.getFinanceScheduleDetails());

		// save FinInstruction to maintain records
		FinServiceInstruction finServiceInstruction = null;
		List<FinServiceInstruction> finServInstList = getFinServiceInstructionDAO().getFinServiceInstructions(
				finScheduleData.getFinanceMain().getFinReference(), "_Temp", FinServiceEvent.CHANGETDS);
		Date appDate = SysParamUtil.getAppDate();
		if (finServInstList.size() > 0) {
			finServiceInstruction = finServInstList.get(0);
			finServiceInstruction.setChecker(auditHeader.getAuditUsrId());
			finServiceInstruction.setCheckerAppDate(appDate);
			finServiceInstruction.setCheckerSysDate(DateUtility.getSysDate());

		} else {
			finServiceInstruction = new FinServiceInstruction();
			finServiceInstruction.setFinReference(finMaintainInstruction.getFinReference());
			finServiceInstruction.setFromDate(appDate);
			finServiceInstruction.setFinEvent(FinServiceEvent.CHANGETDS);
			finServiceInstruction.setChecker(auditHeader.getAuditUsrId());
			finServiceInstruction.setCheckerAppDate(appDate);
			finServiceInstruction.setCheckerSysDate(DateUtility.getSysDate());
			finServiceInstruction.setMaker(auditHeader.getAuditUsrId());
			finServiceInstruction.setMakerAppDate(appDate);
			finServiceInstruction.setMakerSysDate(DateUtility.getSysDate());
			finServiceInstruction.setLinkedTranId(0);
		}
		getFinServiceInstructionDAO().deleteList(finServiceInstruction.getFinReference(), "",
				TableType.TEMP_TAB.getSuffix());
		getFinServiceInstructionDAO().save(finServiceInstruction, "");

		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finMaintainInstruction.getFinReference());
		getFinanceMainDAO().deleteFinreference(financeMain, TableType.TEMP_TAB, false, true);

		financeMain.setTDSApplicable(finMaintainInstruction.istDSApplicable());
		// update tdsApplicable in FinanceMain table
		getFinanceMainDAO().updateTdsApplicable(financeMain);

		getFinMaintainInstructionDAO().delete(finMaintainInstruction, TableType.TEMP_TAB);

		for (LowerTaxDeduction deductions : finScheduleData.getLowerTaxDeductionDetails()) {
			if (deductions.getFinMaintainId() == finMaintainInstruction.getId()) {
				getLowertaxDeductionDAO().delete(deductions, TableType.TEMP_TAB.getSuffix());
			}
		}

		for (LowerTaxDeduction deductions : finScheduleData.getLowerTaxDeductionDetails()) {
			if (deductions.getFinMaintainId() == finMaintainInstruction.getId()) {
				deductions.setRoleCode(finMaintainInstruction.getRoleCode());
				deductions.setNextRoleCode(finMaintainInstruction.getNextRoleCode());
				deductions.setRecordStatus(finMaintainInstruction.getRecordStatus());
				deductions.setRecordType(finMaintainInstruction.getRecordType());
				deductions.setTaskId(finMaintainInstruction.getTaskId());
				deductions.setNextTaskId(finMaintainInstruction.getNextTaskId());
				deductions.setWorkflowId(finMaintainInstruction.getWorkflowId());
				deductions.setLastMntOn(finMaintainInstruction.getLastMntOn());
				deductions.setLastMntBy(finMaintainInstruction.getLastMntBy());
				deductions.setVersion(finMaintainInstruction.getVersion());
				deductions.setWorkflowId(finMaintainInstruction.getWorkflowId());
				getLowertaxDeductionDAO().save(deductions, "");
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1,
				auditHeader.getAuditDetail().getBefImage(), auditHeader.getAuditDetail().getModelData()));

		getAuditHeaderDAO().addAudit(auditHeader);

		// Audit for Before And After Images
		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetails(auditDetails);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(finMaintainInstruction);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1,
				finMaintainInstruction.getBefImage(), finMaintainInstruction));

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		FinMaintainInstruction finMaintainInstruction = (FinMaintainInstruction) auditHeader.getAuditDetail()
				.getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(finMaintainInstruction.getFinReference());
		getFinanceMainDAO().deleteFinreference(financeMain, TableType.TEMP_TAB, false, false);

		getFinServiceInstructionDAO().deleteList(financeMain.getFinReference(), FinServiceEvent.CHANGETDS,
				TableType.TEMP_TAB.getSuffix());

		getFinMaintainInstructionDAO().delete(finMaintainInstruction, TableType.TEMP_TAB);

		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1,
				finMaintainInstruction.getBefImage(), finMaintainInstruction));

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FinMaintainInstruction finMaintainInstruction = (FinMaintainInstruction) auditHeader.getAuditDetail()
				.getModelData();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (finMaintainInstruction.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		finMaintainInstruction.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(finMaintainInstruction);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getFinMaintainInstructionDAO().getErrorDetail with Error ID and language as parameters. if any
	 * error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method,
			boolean isUniqueCheckReq) {
		logger.debug("Entering");

		// Get the model object.
		FinMaintainInstruction finMaintainInstruction = (FinMaintainInstruction) auditDetail.getModelData();

		// Check the unique keys.
		if (isUniqueCheckReq && finMaintainInstruction.isNewRecord()
				&& finMaintainInstructionDAO.isDuplicateKey(finMaintainInstruction.getEvent(),
						finMaintainInstruction.getFinReference(),
						finMaintainInstruction.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];
			parameters[0] = PennantJavaUtil.getLabel("label_FinMaintainInstruction_Event") + ": "
					+ finMaintainInstruction.getEvent();
			parameters[1] = PennantJavaUtil.getLabel("label_FinReference") + " : "
					+ finMaintainInstruction.getFinReference();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	}

	/**
	 * 
	 * @param finMaintainInstruction
	 */
	private FinScheduleData calScheduleTDSAmount(FinMaintainInstruction finMaintainInstruction) {
		logger.debug("Entering");

		Date appDate = DateUtility.getAppDate();
		FinScheduleData finScheduleData = new FinScheduleData();

		// get schedule details
		List<FinanceScheduleDetail> finScheduleList = getFinanceScheduleDetailDAO()
				.getFinScheduleDetails(finMaintainInstruction.getFinReference(), "", false);

		FinanceMain financeMain = getFinanceMainDAO().getFinanceMainById(finMaintainInstruction.getFinReference(), "",
				false);
		financeMain.setEventFromDate(appDate);

		finScheduleData.setFinanceMain(financeMain);
		finScheduleData.setFinanceScheduleDetails(finScheduleList);

		finScheduleData.setLowerTaxDeductionDetails(
				getLowertaxDeductionDAO().getLowerTaxDeductionDetails(financeMain.getFinReference(), ""));

		List<LowerTaxDeduction> ltdList = new ArrayList<LowerTaxDeduction>();

		LowerTaxDeduction lowerTaxDeduction = new LowerTaxDeduction();
		lowerTaxDeduction.setFinReference(financeMain.getFinReference());
		lowerTaxDeduction.setStartDate(finMaintainInstruction.getTdsStartDate());
		lowerTaxDeduction.setEndDate(finMaintainInstruction.getTdsEndDate());
		lowerTaxDeduction.setPercentage(finMaintainInstruction.getTdsPercentage());
		lowerTaxDeduction.setFinMaintainId(finMaintainInstruction.getFinMaintainId());
		lowerTaxDeduction.setLimitAmt(finMaintainInstruction.getTdsLimit());
		ltdList.add(lowerTaxDeduction);

		finScheduleData.getLowerTaxDeductionDetails().addAll(ltdList);

		for (FinanceScheduleDetail schedule : finScheduleData.getFinanceScheduleDetails()) {
			if (schedule.getSchDate().compareTo(appDate) >= 0 && schedule.getPresentmentId() == 0) {
				schedule.setTDSApplicable(finMaintainInstruction.istDSApplicable());
			}
		}

		finScheduleData = ScheduleCalculator.procReCalTDSAmount(finScheduleData);

		logger.debug("Leaving");
		return finScheduleData;
	}

	// Setters and getters

	public ChangeTDSDAO getChangeTDSDAO() {
		return changeTDSDAO;
	}

	public void setChangeTDSDAO(ChangeTDSDAO changeTDSDAO) {
		this.changeTDSDAO = changeTDSDAO;
	}

	public FinServiceInstrutionDAO getFinServiceInstructionDAO() {
		return finServiceInstructionDAO;
	}

	public void setFinServiceInstructionDAO(FinServiceInstrutionDAO finServiceInstructionDAO) {
		this.finServiceInstructionDAO = finServiceInstructionDAO;
	}

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

	public FinMaintainInstructionDAO getFinMaintainInstructionDAO() {
		return finMaintainInstructionDAO;
	}

	public void setFinMaintainInstructionDAO(FinMaintainInstructionDAO finMaintainInstructionDAO) {
		this.finMaintainInstructionDAO = finMaintainInstructionDAO;
	}

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public LowerTaxDeductionDAO getLowertaxDeductionDAO() {
		return lowertaxDeductionDAO;
	}

	@Autowired
	public void setLowertaxDeductionDAO(LowerTaxDeductionDAO lowertaxDeductionDAO) {
		this.lowertaxDeductionDAO = lowertaxDeductionDAO;
	}

}
