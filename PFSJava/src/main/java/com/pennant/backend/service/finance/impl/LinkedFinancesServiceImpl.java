package com.pennant.backend.service.finance.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinMaintainInstructionDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.LinkedFinancesDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinMaintainInstruction;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.LinkedFinances;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.LinkedFinancesService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;

public class LinkedFinancesServiceImpl extends GenericService<FinanceDetail> implements LinkedFinancesService {
	private static final Logger logger = LogManager.getLogger(LinkedFinancesServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private LinkedFinancesDAO linkedFinancesDAO;
	private FinanceMainDAO financeMainDAO;
	private FinMaintainInstructionDAO finMaintainInstructionDAO;

	public LinkedFinancesServiceImpl() {
		super();
	}

	@Override
	public List<LinkedFinances> getLinkedFinancesByRef(String financeReference, String type) {
		return linkedFinancesDAO.getLinkedFinancesByFinRef(financeReference, type);
	}

	public FinMaintainInstruction getFinMaintainInstructionByFinRef(String finreference, String event) {
		return finMaintainInstructionDAO.getFinMaintainInstructionByFinRef(finreference, event, "_Temp");
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinMaintainInstruction finMainInst = finScheduleData.getFinMaintainInstruction();

		if (!FinServiceEvent.ORG.equals(financeDetail.getModuleDefiner())) {
			TableType tableType = TableType.MAIN_TAB;
			if (finMainInst.isWorkflow()) {
				tableType = TableType.TEMP_TAB;
			}

			finMainInst.setEvent(FinServiceEvent.LINKDELINK);
			// Setting work FLow Details for child
			for (LinkedFinances linkedFin : financeDetail.getLinkedFinancesList()) {
				linkedFin.setRoleCode(finMainInst.getRoleCode());
				linkedFin.setNextRoleCode(finMainInst.getNextRoleCode());
				linkedFin.setTaskId(finMainInst.getTaskId());
				linkedFin.setNextTaskId(finMainInst.getNextTaskId());
				linkedFin.setWorkflowId(finMainInst.getWorkflowId());
				linkedFin.setRecordStatus(finMainInst.getRecordStatus());
				linkedFin.setLastMntBy(finMainInst.getLastMntBy());
				linkedFin.setLastMntOn(finMainInst.getLastMntOn());
			}

			if (finMainInst.isNewRecord()) {
				finMaintainInstructionDAO.save(finMainInst, tableType);
				linkedFinancesDAO.saveList(financeDetail.getLinkedFinancesList(), "_TEMP");
				auditHeader.getAuditDetail().setModelData(finMainInst);
				auditHeader.setAuditReference(finMainInst.getFinReference());
			} else {
				List<LinkedFinances> saveFinances = new ArrayList<>();
				List<LinkedFinances> updateFinances = new ArrayList<>();
				finMaintainInstructionDAO.update(finMainInst, tableType);
				for (LinkedFinances linkedFin : financeDetail.getLinkedFinancesList()) {
					if (linkedFin.isNewRecord()) {
						saveFinances.add(linkedFin);
						if (saveFinances.size() == PennantConstants.CHUNK_SIZE) {
							linkedFinancesDAO.saveList(saveFinances, "_Temp");
						}
					} else {
						updateFinances.add(linkedFin);
						if (updateFinances.size() == PennantConstants.CHUNK_SIZE) {
							linkedFinancesDAO.updateList(updateFinances, "_Temp");
						}
					}
				}

				linkedFinancesDAO.saveList(saveFinances, "_Temp");
				linkedFinancesDAO.updateList(updateFinances, "_Temp");
			}
		}

		List<AuditDetail> auditDetails = new ArrayList<>();
		String[] fields = PennantJavaUtil.getFieldDetails(new LinkedFinances(),
				new LinkedFinances().getExcludeFields());
		int i = 0;
		for (LinkedFinances linkedFinance : financeDetail.getLinkedFinancesList()) {
			auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), ++i, fields[0], fields[1],
					linkedFinance.getBefImage(), linkedFinance));
		}

		auditHeader.setAuditDetails(auditDetails);
		auditHeader.setAuditDetail(getAuditDetail(finMainInst, 1, auditHeader.getAuditTranType()));
		auditHeader.getAuditDetail().setModelData(finMainInst);
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

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinMaintainInstruction finMaintainInstruction = finScheduleData.getFinMaintainInstruction();
		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<>();
		List<AuditDetail> oldAuditDetails = new ArrayList<>();

		if (!FinServiceEvent.ORG.equals(financeDetail.getModuleDefiner())) {
			FinMaintainInstruction finMainInst = new FinMaintainInstruction();
			BeanUtils.copyProperties(finMaintainInstruction, finMainInst);

			if (!PennantConstants.RECORD_TYPE_NEW.equals(finMainInst.getRecordType())) {
				auditHeader.getAuditDetail().setBefImage(
						finMaintainInstructionDAO.getFinMaintainInstructionById(finMainInst.getFinMaintainId(), ""));
			}

			int j = 0;
			for (LinkedFinances linkFin : financeDetail.getLinkedFinancesList()) {
				LinkedFinances linFinance = new LinkedFinances();
				BeanUtils.copyProperties(linkFin, linFinance);
				String[] fields = PennantJavaUtil.getFieldDetails(new LinkedFinances(),
						new LinkedFinances().getExcludeFields());
				oldAuditDetails
						.add(new AuditDetail(PennantConstants.TRAN_WF, ++j, fields[0], fields[1], null, linFinance));
				if (!PennantConstants.RECORD_TYPE_NEW.equals(linFinance.getRecordType())) {
					linkFin.setBefImage(linkedFinancesDAO.getLinkedFinancesByLinkRef(linFinance.getLinkedReference(),
							linFinance.getFinReference(), ""));
				}
			}

			if (finMainInst.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
				tranType = PennantConstants.TRAN_DEL;
				finMaintainInstructionDAO.delete(finMainInst, TableType.MAIN_TAB);
				linkedFinancesDAO.delete(finMainInst.getFinReference(), TableType.MAIN_TAB.getSuffix());
			} else {
				finMainInst.setRoleCode("");
				finMainInst.setNextRoleCode("");
				finMainInst.setTaskId("");
				finMainInst.setNextTaskId("");
				finMainInst.setWorkflowId(0);

				// Setting CHild Details
				int i = 0;
				for (LinkedFinances linkFin : financeDetail.getLinkedFinancesList()) {
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
						linkedFinancesDAO.deleteByLinkedReference(linkFin.getLinkedReference(),
								linkFin.getFinReference(), "");
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
			linkedFinancesDAO.delete(finMainInst.getFinReference(), "_Temp");

			// WorkFlow Image For Audit
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			auditHeader.getAuditDetail().setAuditTranType(PennantConstants.TRAN_WF);
			auditHeader.setAuditDetails(oldAuditDetails);
			auditHeader.setAuditDetail(getAuditDetail(finMaintainInstruction, 1, auditHeader.getAuditTranType()));
			auditHeader.getAuditDetail().setModelData(finMaintainInstruction);
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
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (!FinServiceEvent.ORG.equals(financeDetail.getModuleDefiner())) {
			finMaintainInstructionDAO.delete(finMainInst, TableType.TEMP_TAB);
		}

		int i = 0;
		for (LinkedFinances linkFin : financeDetail.getLinkedFinancesList()) {
			String[] fields = PennantJavaUtil.getFieldDetails(new LinkedFinances(),
					new LinkedFinances().getExcludeFields());
			auditDetails.add(new AuditDetail(PennantConstants.TRAN_WF, ++i, fields[0], fields[1], linkFin.getBefImage(),
					linkFin));
		}

		linkedFinancesDAO.delete(finMain.getFinReference(), "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetail(getAuditDetail(finMainInst, 1, auditHeader.getAuditTranType()));
		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5)
	 * for any mismatch conditions Fetch the error details from getLinkedFinancesDAO().getErrorDetail with Error ID and
	 * language as parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validate(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
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
			}

			linkedFinancesDAO.saveList(saveFinances, type);
			linkedFinancesDAO.updateList(updateFinances, type);
		}

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
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
		linkedFinancesDAO.delete(financeMain.getFinReference(), "_Temp");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new LinkedFinances(),
				new LinkedFinances().getExcludeFields());
		int i = 0;

		for (LinkedFinances linkedFinance : financeDetail.getLinkedFinancesList()) {
			auditDetails.add(new AuditDetail(PennantConstants.TRAN_WF, ++i, fields[0], fields[1],
					linkedFinance.getBefImage(), linkedFinance));
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
			linkedFinancesDAO.delete(financeMain.getFinReference(), "");
		} else {
			for (LinkedFinances linkedFinances : financeDetail.getLinkedFinancesList()) {
				linkedFinances.setRoleCode("");
				linkedFinances.setNextRoleCode("");
				linkedFinances.setTaskId("");
				linkedFinances.setNextTaskId("");
				linkedFinances.setWorkflowId(0);
				linkedFinances.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				linkedFinances.setLastMntBy(financeMain.getLastMntBy());
				linkedFinances.setLastMntOn(financeMain.getLastMntOn());
				linkedFinances.setVersion(financeMain.getVersion());

				if (PennantConstants.RCD_DEL.equals(linkedFinances.getStatus())) {
					tranType = PennantConstants.TRAN_WF;
					linkedFinancesDAO.deleteByLinkedReference(linkedFinances.getLinkedReference(),
							linkedFinances.getFinReference(), "_Temp");
				} else {
					if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						tranType = PennantConstants.TRAN_ADD;
						linkedFinances.setRecordType("");
						linkedFinances.setStatus(PennantConstants.RCD_STATUS_APPROVED);
						linkedFinancesDAO.save(linkedFinances, "");
					} else {
						tranType = PennantConstants.TRAN_UPD;
						linkedFinances.setRecordType("");
						linkedFinances.setStatus(PennantConstants.RCD_STATUS_APPROVED);
						linkedFinancesDAO.update(linkedFinances, "");
					}
				}
			}
		}
		linkedFinancesDAO.delete(financeMain.getFinReference(), "_Temp");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
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

	@Override
	public List<LinkedFinances> getLinkedFinancesByFinRef(String ref, String type) {
		return linkedFinancesDAO.getLinkedFinancesByFin(ref, type);
	}

	@Override
	public FinanceMain getFinMainByFinRef(String finReference) {
		return financeMainDAO.getFinMainLinkedFinancesByFinRef(finReference, "_LFView");
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

}
