package com.pennant.backend.service.finance.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ReferenceGenerator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinMaintainInstructionDAO;
import com.pennant.backend.dao.finance.FinServiceInstrutionDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.LinkedFinancesDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinMaintainInstruction;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.LinkedFinances;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.finance.LinkedFinancesService;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;

public class LinkedFinancesServiceImpl extends GenericService<FinanceDetail> implements LinkedFinancesService {
	private static final Logger logger = LogManager.getLogger(LinkedFinancesServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private LinkedFinancesDAO linkedFinancesDAO;
	private FinanceMainDAO financeMainDAO;
	private FinMaintainInstructionDAO finMaintainInstructionDAO;
	private FinanceTypeDAO financeTypeDAO;
	private ExtendedFieldDetailsService extendedFieldDetailsService;
	private FinServiceInstrutionDAO finServiceInstructionDAO;

	public LinkedFinancesServiceImpl() {
		super();
	}

	@Override
	public List<LinkedFinances> getLinkedFinancesByRef(String finReference, String type) {
		return linkedFinancesDAO.getLinkedFinancesByFinRef(finReference, type);
	}

	@Override
	public FinMaintainInstruction getFinMaintainInstructionByFinRef(long finID, String event) {
		return finMaintainInstructionDAO.getFinMaintainInstructionByFinRef(finID, event,
				TableType.TEMP_TAB.getSuffix());
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinScheduleData fsd = fd.getFinScheduleData();
		FinMaintainInstruction fmi = fsd.getFinMaintainInstruction();
		long serviceUID = Long.MIN_VALUE;

		fd.getExtendedFieldHeader().setEvent(FinServiceEvent.LINKDELINK);

		List<FinServiceInstruction> serviceInstructions = getServiceInstructions(fd);

		if (fd.getExtendedFieldRender() != null && fd.getExtendedFieldRender().getInstructionUID() != Long.MIN_VALUE) {
			serviceUID = fd.getExtendedFieldRender().getInstructionUID();
		}

		for (FinServiceInstruction fsi : serviceInstructions) {
			serviceUID = fsi.getInstructionUID();
			if (ObjectUtils.isEmpty(fsi.getInitiatedDate())) {
				fsi.setInitiatedDate(SysParamUtil.getAppDate());
			}
		}

		fmi.setFinServiceInstructions(serviceInstructions);
		fsd.setFinMaintainInstruction(fmi);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		String tempTab = TableType.TEMP_TAB.getSuffix();
		if (!FinServiceEvent.ORG.equals(fd.getModuleDefiner())) {
			TableType tableType = TableType.MAIN_TAB;
			if (fmi.isWorkflow()) {
				tableType = TableType.TEMP_TAB;
			}

			fmi.setEvent(FinServiceEvent.LINKDELINK);
			for (LinkedFinances linkedFin : fd.getLinkedFinancesList()) {
				if (!linkedFin.isNewRecord()) {
					linkedFin.setNewRecord(fmi.isNewRecord());
				}

				linkedFin.setRoleCode(fmi.getRoleCode());
				linkedFin.setNextRoleCode(fmi.getNextRoleCode());
				linkedFin.setTaskId(fmi.getTaskId());
				linkedFin.setNextTaskId(fmi.getNextTaskId());
				linkedFin.setWorkflowId(fmi.getWorkflowId());
				linkedFin.setRecordStatus(fmi.getRecordStatus());
				linkedFin.setLastMntBy(fmi.getLastMntBy());
				linkedFin.setLastMntOn(fmi.getLastMntOn());

				financeMainDAO.updateMaintainceStatus(linkedFin.getFinID(), FinServiceEvent.LINKDELINK);
			}

			if (fmi.isNewRecord()) {
				finMaintainInstructionDAO.save(fmi, tableType);
				auditHeader.getAuditDetail().setModelData(fmi);
				auditHeader.setAuditReference(fmi.getFinReference());
			} else {
				finMaintainInstructionDAO.update(fmi, tableType);
			}

			financeMainDAO.updateMaintainceStatus(fmi.getFinID(), FinServiceEvent.LINKDELINK);
			List<LinkedFinances> saveFinances = new ArrayList<>();
			List<LinkedFinances> updateFinances = new ArrayList<>();

			for (LinkedFinances linkedFin : fd.getLinkedFinancesList()) {
				if (linkedFin.isNewRecord()) {
					saveFinances.add(linkedFin);
					if (saveFinances.size() == PennantConstants.CHUNK_SIZE) {
						linkedFinancesDAO.saveList(saveFinances, tempTab);
						saveFinances.isEmpty();
					}
				} else {
					updateFinances.add(linkedFin);
					if (updateFinances.size() == PennantConstants.CHUNK_SIZE) {
						linkedFinancesDAO.updateList(updateFinances, tempTab);
						updateFinances.isEmpty();
					}
				}
			}

			if (CollectionUtils.isNotEmpty(saveFinances)) {
				linkedFinancesDAO.saveList(saveFinances, tempTab);
			}

			if (CollectionUtils.isNotEmpty(updateFinances)) {
				linkedFinancesDAO.updateList(updateFinances, tempTab);
			}
		}

		List<AuditDetail> auditDetails = new ArrayList<>();

		if (fd.getExtendedFieldRender() != null) {
			List<AuditDetail> details = fd.getAuditDetailMap().get("ExtendedFieldDetails");

			details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
					ExtendedFieldConstants.MODULE_LOAN, fd.getExtendedFieldHeader().getEvent(), tempTab, serviceUID);
			auditDetails.addAll(details);
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new LinkedFinances(),
				new LinkedFinances().getExcludeFields());
		int i = 0;
		for (LinkedFinances linkedFinance : fd.getLinkedFinancesList()) {
			auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), ++i, fields[0], fields[1],
					linkedFinance.getBefImage(), linkedFinance));
		}

		auditHeader.setAuditDetails(auditDetails);
		auditHeader.setAuditDetail(getAuditDetail(fmi, 1, auditHeader.getAuditTranType()));
		auditHeader.getAuditDetail().setModelData(fmi);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FinanceDetail fd = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinScheduleData fsd = fd.getFinScheduleData();
		FinMaintainInstruction fmi = fsd.getFinMaintainInstruction();
		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<>();
		List<AuditDetail> oldAuditDetails = new ArrayList<>();

		fd.getExtendedFieldHeader().setEvent(FinServiceEvent.LINKDELINK);

		List<FinServiceInstruction> serviceInstructions = getServiceInstructions(fd);
		long serviceUID = Long.MIN_VALUE;

		if (fd.getExtendedFieldRender() != null && fd.getExtendedFieldRender().getInstructionUID() != Long.MIN_VALUE) {
			serviceUID = fd.getExtendedFieldRender().getInstructionUID();
		}

		for (FinServiceInstruction finServInst : serviceInstructions) {
			serviceUID = finServInst.getInstructionUID();
			if (ObjectUtils.isEmpty(finServInst.getInitiatedDate())) {
				finServInst.setInitiatedDate(SysParamUtil.getAppDate());
			}
		}

		fmi.setFinServiceInstructions(serviceInstructions);

		if (!FinServiceEvent.ORG.equals(fd.getModuleDefiner())) {
			FinMaintainInstruction finMainInst = new FinMaintainInstruction();
			BeanUtils.copyProperties(fmi, finMainInst);

			if (!PennantConstants.RECORD_TYPE_NEW.equals(finMainInst.getRecordType())) {
				auditHeader.getAuditDetail().setBefImage(
						finMaintainInstructionDAO.getFinMaintainInstructionById(finMainInst.getFinMaintainId(), ""));
			}

			if (CollectionUtils.isNotEmpty(fmi.getFinServiceInstructions())) {
				finServiceInstructionDAO.saveList(fmi.getFinServiceInstructions(), "");
			}

			finMainInst.setNewRecord(false);

			int j = 0;
			for (LinkedFinances linkFin : fd.getLinkedFinancesList()) {
				LinkedFinances linFinance = new LinkedFinances();
				BeanUtils.copyProperties(linkFin, linFinance);
				String[] fields = PennantJavaUtil.getFieldDetails(new LinkedFinances(),
						new LinkedFinances().getExcludeFields());
				oldAuditDetails
						.add(new AuditDetail(PennantConstants.TRAN_WF, ++j, fields[0], fields[1], null, linFinance));
				if (!PennantConstants.RECORD_TYPE_NEW.equals(linFinance.getRecordType())) {
					linkFin.setBefImage(linkedFinancesDAO.getLinkedFinancesByLinkRef(linFinance.getLinkedReference(),
							linFinance.getFinID(), ""));
				}
			}

			if (finMainInst.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
				tranType = PennantConstants.TRAN_DEL;
				finMaintainInstructionDAO.delete(finMainInst, TableType.MAIN_TAB);
				linkedFinancesDAO.delete(finMainInst.getFinID(), TableType.MAIN_TAB.getSuffix());
			} else {
				finMainInst.setRoleCode("");
				finMainInst.setNextRoleCode("");
				finMainInst.setTaskId("");
				finMainInst.setNextTaskId("");
				finMainInst.setWorkflowId(0);

				// Setting CHild Details
				int i = 0;
				for (LinkedFinances linkFin : fd.getLinkedFinancesList()) {
					linkFin.setRoleCode("");
					linkFin.setNextRoleCode("");
					linkFin.setTaskId("");
					linkFin.setNextTaskId("");
					linkFin.setWorkflowId(0);
					linkFin.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					linkFin.setLastMntBy(finMainInst.getLastMntBy());
					linkFin.setLastMntOn(finMainInst.getLastMntOn());
					linkFin.setVersion(linkFin.getVersion() + 1);

					if (PennantConstants.RCD_DEL.equals(linkFin.getStatus())) {
						linkedFinancesDAO.deleteByLinkedReference(linkFin.getLinkedReference(), linkFin.getFinID(), "");
					} else {
						if (linkFin.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							tranType = PennantConstants.TRAN_ADD;
							linkFin.setRecordType("");
							linkFin.setStatus(PennantConstants.RCD_STATUS_APPROVED);
							linkedFinancesDAO.save(linkFin, "");
						} else {
							tranType = PennantConstants.TRAN_UPD;
							linkFin.setRecordType("");
							linkFin.setStatus(PennantConstants.RCD_STATUS_APPROVED);
							linkedFinancesDAO.update(linkFin, "");
						}
					}

					financeMainDAO.updateMaintainceStatus(linkFin.getFinID(), "");
					String[] fields = PennantJavaUtil.getFieldDetails(new LinkedFinances(),
							new LinkedFinances().getExcludeFields());
					auditDetails
							.add(new AuditDetail(tranType, ++i, fields[0], fields[1], linkFin.getBefImage(), linkFin));
				}

				if (finMainInst.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_ADD;
					finMainInst.setRecordType("");
					finMaintainInstructionDAO.save(finMainInst, TableType.MAIN_TAB);
				} else {
					tranType = PennantConstants.TRAN_UPD;
					finMainInst.setRecordType("");
					finMaintainInstructionDAO.update(finMainInst, TableType.MAIN_TAB);
				}
			}

			finMaintainInstructionDAO.delete(finMainInst, TableType.TEMP_TAB);
			linkedFinancesDAO.delete(finMainInst.getFinID(), TableType.TEMP_TAB.getSuffix());
			financeMainDAO.updateMaintainceStatus(finMainInst.getFinID(), "");

			finServiceInstructionDAO.deleteList(fmi.getFinID(), fd.getExtendedFieldHeader().getEvent(), "_Temp");

			List<AuditDetail> details = fd.getAuditDetailMap().get("ExtendedFieldDetails");
			if (fd.getExtendedFieldRender() != null) {
				details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
						ExtendedFieldConstants.MODULE_LOAN, fd.getExtendedFieldHeader().getEvent(), "", serviceUID);
				auditDetails.addAll(details);
			}

			for (int i = 0; i < auditDetails.size(); i++) {
				auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
			}

			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			auditHeader.getAuditDetail().setAuditTranType(PennantConstants.TRAN_WF);
			auditHeader.setAuditDetails(oldAuditDetails);
			auditHeader.setAuditDetail(getAuditDetail(fmi, 1, auditHeader.getAuditTranType()));
			auditHeader.getAuditDetail().setModelData(fmi);
			auditHeaderDAO.addAudit(auditHeader);

			auditHeader.setAuditTranType(tranType);
			auditHeader.setAuditDetails(auditDetails);
			auditHeader.setAuditDetail(getAuditDetail(finMainInst, 1, auditHeader.getAuditTranType()));
			auditHeader.getAuditDetail().setAuditTranType(tranType);
			auditHeader.getAuditDetail().setModelData(finMainInst);
			auditHeaderDAO.addAudit(auditHeader);
		}

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinMaintainInstruction finMainInst = finScheduleData.getFinMaintainInstruction();
		List<AuditDetail> auditDetails = new ArrayList<>();

		if (!FinServiceEvent.ORG.equals(financeDetail.getModuleDefiner())) {
			finMaintainInstructionDAO.delete(finMainInst, TableType.TEMP_TAB);
		}

		int i = 0;
		for (LinkedFinances linkFin : financeDetail.getLinkedFinancesList()) {
			String[] fields = PennantJavaUtil.getFieldDetails(new LinkedFinances(),
					new LinkedFinances().getExcludeFields());
			auditDetails.add(new AuditDetail(PennantConstants.TRAN_WF, ++i, fields[0], fields[1], linkFin.getBefImage(),
					linkFin));

			financeMainDAO.updateMaintainceStatus(linkFin.getFinID(), "");
		}

		linkedFinancesDAO.delete(finMain.getFinID(), TableType.TEMP_TAB.getSuffix());
		financeMainDAO.updateMaintainceStatus(finMain.getFinID(), "");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetail(getAuditDetail(finMainInst, 1, auditHeader.getAuditTranType()));
		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validate(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);

		List<AuditDetail> auditDetails = new ArrayList<>();

		FinanceDetail fd = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinScheduleData fsd = fd.getFinScheduleData();
		FinMaintainInstruction fmi = fsd.getFinMaintainInstruction();

		String usrLanguage = PennantConstants.default_Language;

		if (fmi.getUserDetails() == null) {
			fmi.setUserDetails(new LoggedInUser());
			usrLanguage = fmi.getUserDetails().getLanguage();
		}

		getAuditDetails(auditHeader, method);

		// Extended field details Validation
		if (fd.getExtendedFieldRender() != null) {
			List<AuditDetail> details = fd.getAuditDetailMap().get("LoanExtendedFieldDetails");
			ExtendedFieldHeader extendedFieldHeader = fd.getExtendedFieldHeader();
			if (extendedFieldHeader != null) {
				StringBuilder sb = new StringBuilder();
				sb.append(extendedFieldHeader.getModuleName());
				sb.append("_");
				sb.append(extendedFieldHeader.getSubModuleName());
				if (extendedFieldHeader.getEvent() != null) {
					sb.append("_");
					sb.append(PennantStaticListUtil.getFinEventCode(extendedFieldHeader.getEvent()));
				}
				sb.append("_ED");
				details = extendedFieldDetailsService.vaildateDetails(details, method, usrLanguage, sb.toString());
				auditDetails.addAll(details);
			}
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		getAuditDetails(auditHeader, method);
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditDetail validate(AuditDetail auditDetail, String usrLanguage, String method) {
		auditDetail.setErrorDetails(new ArrayList<>());

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		return auditDetail;
	}

	public AuditDetail getAuditDetail(FinMaintainInstruction FinMainInst, int auditSeq, String transType) {
		String[] fields = PennantJavaUtil.getFieldDetails(new FinMaintainInstruction(),
				new FinMaintainInstruction().getExcludeFields());
		return new AuditDetail(transType, auditSeq, fields[0], fields[1], FinMainInst.getBefImage(), FinMainInst);
	}

	@Override
	public List<AuditDetail> saveOrUpdateLinkedFinanceList(FinanceDetail financeDetail, String type) {
		logger.debug(Literal.ENTERING);

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		for (LinkedFinances linkedFinances : financeDetail.getLinkedFinancesList()) {
			linkedFinances.setRoleCode(financeMain.getRoleCode());
			linkedFinances.setRecordStatus(financeMain.getRecordStatus());
			linkedFinances.setNextRoleCode(financeMain.getNextRoleCode());
			linkedFinances.setTaskId(financeMain.getTaskId());
			linkedFinances.setNextTaskId(financeMain.getNextTaskId());
			linkedFinances.setWorkflowId(financeMain.getWorkflowId());
			linkedFinances.setRecordType(financeMain.getRecordType());
			linkedFinances.setLastMntBy(financeMain.getLastMntBy());
			linkedFinances.setLastMntOn(financeMain.getLastMntOn());
			linkedFinances.setVersion(financeMain.getVersion());
		}

		String auditTranType;
		if (financeMain.isNewRecord()) {
			auditTranType = PennantConstants.TRAN_ADD;
			linkedFinancesDAO.saveList(financeDetail.getLinkedFinancesList(), type);
		} else {
			auditTranType = PennantConstants.TRAN_UPD;
			List<LinkedFinances> saveFinances = new ArrayList<>();
			List<LinkedFinances> updateFinances = new ArrayList<>();
			for (LinkedFinances linkedFin : financeDetail.getLinkedFinancesList()) {
				if (linkedFin.isNewRecord()) {
					saveFinances.add(linkedFin);
					if (saveFinances.size() == PennantConstants.CHUNK_SIZE) {
						linkedFinancesDAO.saveList(saveFinances, type);
					}
				} else {
					updateFinances.add(linkedFin);
					if (updateFinances.size() == PennantConstants.CHUNK_SIZE) {
						linkedFinancesDAO.updateList(updateFinances, type);
					}
				}

				financeMainDAO.updateMaintainceStatus(linkedFin.getFinID(), FinServiceEvent.LINKDELINK);
			}

			linkedFinancesDAO.saveList(saveFinances, type);
			linkedFinancesDAO.updateList(updateFinances, type);
		}

		financeMainDAO.updateMaintainceStatus(financeMain.getFinID(), FinServiceEvent.LINKDELINK);

		List<AuditDetail> auditDetails = new ArrayList<>();
		String[] fields = PennantJavaUtil.getFieldDetails(new LinkedFinances(),
				new LinkedFinances().getExcludeFields());
		int i = 0;
		for (LinkedFinances linkedFinance : financeDetail.getLinkedFinancesList()) {
			auditDetails.add(new AuditDetail(auditTranType, ++i, fields[0], fields[1], linkedFinance.getBefImage(),
					linkedFinance));
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public List<AuditDetail> doRejectLinkedFinanceList(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		linkedFinancesDAO.delete(financeMain.getFinID(), "_Temp");
		financeMainDAO.updateMaintainceStatus(financeMain.getFinID(), "");

		List<AuditDetail> auditDetails = new ArrayList<>();
		String[] fields = PennantJavaUtil.getFieldDetails(new LinkedFinances(),
				new LinkedFinances().getExcludeFields());
		int i = 0;

		for (LinkedFinances linkedFinance : financeDetail.getLinkedFinancesList()) {
			auditDetails.add(new AuditDetail(PennantConstants.TRAN_WF, ++i, fields[0], fields[1],
					linkedFinance.getBefImage(), linkedFinance));
			financeMainDAO.updateMaintainceStatus(linkedFinance.getFinID(), "");
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public List<AuditDetail> doApproveLinkedFinanceList(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String tranType = PennantConstants.TRAN_WF;

		if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			linkedFinancesDAO.delete(financeMain.getFinID(), "");
		} else {
			for (LinkedFinances lf : financeDetail.getLinkedFinancesList()) {
				lf.setRoleCode("");
				lf.setNextRoleCode("");
				lf.setTaskId("");
				lf.setNextTaskId("");
				lf.setWorkflowId(0);
				lf.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				lf.setLastMntBy(financeMain.getLastMntBy());
				lf.setLastMntOn(financeMain.getLastMntOn());
				lf.setVersion(financeMain.getVersion());

				if (PennantConstants.RCD_DEL.equals(lf.getStatus())) {
					tranType = PennantConstants.TRAN_WF;
					linkedFinancesDAO.deleteByLinkedReference(lf.getLinkedReference(), lf.getFinID(), "_Temp");
				} else {
					if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						tranType = PennantConstants.TRAN_ADD;
						lf.setRecordType("");
						lf.setStatus(PennantConstants.RCD_STATUS_APPROVED);
						linkedFinancesDAO.save(lf, "");
					} else {
						tranType = PennantConstants.TRAN_UPD;
						lf.setRecordType("");
						lf.setStatus(PennantConstants.RCD_STATUS_APPROVED);
						linkedFinancesDAO.update(lf, "");
					}
				}

				financeMainDAO.updateMaintainceStatus(lf.getFinID(), "");
			}
		}
		linkedFinancesDAO.delete(financeMain.getFinID(), "_Temp");
		financeMainDAO.updateMaintainceStatus(financeMain.getFinID(), "");

		List<AuditDetail> auditDetails = new ArrayList<>();
		String[] fields = PennantJavaUtil.getFieldDetails(new LinkedFinances(),
				new LinkedFinances().getExcludeFields());
		int i = 0;
		for (LinkedFinances linkedFinance : financeDetail.getLinkedFinancesList()) {
			auditDetails.add(
					new AuditDetail(tranType, ++i, fields[0], fields[1], linkedFinance.getBefImage(), linkedFinance));
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	private void getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<>();

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain fm = financeDetail.getFinScheduleData().getFinanceMain();

		String auditTranType = "";
		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (fm.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		List<ExtendedFieldRender> renderList = financeDetail.getExtendedFieldRenderList();
		if (renderList != null && !renderList.isEmpty()) {
			auditDetailMap.put("ExtendedFieldDetails",
					extendedFieldDetailsService.setExtendedFieldsAuditData(renderList, auditTranType, method, null));
			auditDetails.addAll(auditDetailMap.get("ExtendedFieldDetails"));
		}

		if (financeDetail.getExtendedFieldRender() != null) {
			ExtendedFieldRender extendedFieldRender = financeDetail.getExtendedFieldRender();
			if (extendedFieldRender.getInstructionUID() == Long.MIN_VALUE && fm.getInstructionUID() != Long.MIN_VALUE) {
				extendedFieldRender.setInstructionUID(fm.getInstructionUID());
			}
			auditDetailMap.put("LoanExtendedFieldDetails",
					extendedFieldDetailsService.setExtendedFieldsAuditData(financeDetail.getExtendedFieldHeader(),
							extendedFieldRender, auditTranType, method, ExtendedFieldConstants.MODULE_LOAN));
			financeDetail.setAuditDetailMap(auditDetailMap);
			auditDetails.addAll(auditDetailMap.get("LoanExtendedFieldDetails"));
		}

		if (financeDetail.getExtendedFieldRender() != null) {
			auditDetailMap.put("ExtendedFieldDetails",
					extendedFieldDetailsService.setExtendedFieldsAuditData(financeDetail.getExtendedFieldHeader(),
							financeDetail.getExtendedFieldRender(), auditTranType, method,
							financeDetail.getExtendedFieldHeader().getModuleName()));
			auditDetails.addAll(auditDetailMap.get("ExtendedFieldDetails"));
		}

		financeDetail.setAuditDetailMap(auditDetailMap);
		logger.debug(Literal.LEAVING);
	}

	private List<FinServiceInstruction> getServiceInstructions(FinanceDetail fd) {
		logger.debug(Literal.ENTERING);

		FinScheduleData fsd = fd.getFinScheduleData();
		FinMaintainInstruction fmi = fsd.getFinMaintainInstruction();

		String event = fd.getExtendedFieldHeader().getEvent();

		List<FinServiceInstruction> si = fsd.getFinServiceInstructions();

		if (CollectionUtils.isEmpty(si)) {
			FinServiceInstruction fsi = new FinServiceInstruction();
			fsi.setFinID(fmi.getFinID());
			fsi.setFinReference(fmi.getFinReference());
			fsi.setFinEvent(event);

			fsd.setFinServiceInstruction(fsi);
		}

		for (FinServiceInstruction fsi : fsd.getFinServiceInstructions()) {
			if (fsi.getInstructionUID() == Long.MIN_VALUE) {
				fsi.setInstructionUID(Long.valueOf(ReferenceGenerator.generateNewServiceUID()));
			}

			if (StringUtils.isEmpty(event) || FinServiceEvent.ORG.equals(event)) {
				if (!FinServiceEvent.ORG.equals(fsi.getFinEvent()) && !StringUtils.contains(fsi.getFinEvent(), "_O")) {
					fsi.setFinEvent(fsi.getFinEvent().concat("_O"));
				}
			}
		}

		logger.debug(Literal.LEAVING);
		return fsd.getFinServiceInstructions();
	}

	@Override
	public FinanceType getFinType(String finType) {
		return financeTypeDAO.getFinanceTypeByID(finType, "_AView");
	}

	@Override
	public List<LinkedFinances> getLinkedFinancesByFinRef(String ref, String type) {
		return linkedFinancesDAO.getLinkedFinancesByFin(ref, type);
	}

	@Override
	public FinanceMain getFinMainByFinRef(long finID) {
		return financeMainDAO.getFinMainLinkedFinancesByFinRef(finID);
	}

	@Override
	public List<LinkedFinances> getFinIsLinkedActive(String finReference) {
		return linkedFinancesDAO.getFinIsLinkedActive(finReference);
	}

	@Override
	public List<String> getFinReferences(String reference) {
		return linkedFinancesDAO.getFinReferences(reference);
	}

	public void setLinkedFinancesDAO(LinkedFinancesDAO linkedFinancesDAO) {
		this.linkedFinancesDAO = linkedFinancesDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setFinMaintainInstructionDAO(FinMaintainInstructionDAO finMaintainInstructionDAO) {
		this.finMaintainInstructionDAO = finMaintainInstructionDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	public void setFinServiceInstructionDAO(FinServiceInstrutionDAO finServiceInstructionDAO) {
		this.finServiceInstructionDAO = finServiceInstructionDAO;
	}

}