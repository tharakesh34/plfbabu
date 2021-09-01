package com.pennant.backend.service.systemmasters.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.systemmasters.PMAYDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.PMAY;
import com.pennant.backend.model.finance.PmayEligibilityLog;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.PMAYService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class PMAYServiceImpl extends GenericService<PMAY> implements PMAYService {
	private static final Logger logger = LogManager.getLogger(PMAYServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private PMAYDAO pmayDAO;
	private FinanceMainDAO financeMainDAO;

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		boolean flag = false;

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		PMAY pmay = (PMAY) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (pmay.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}
		// updating the PMAY flag to true if loan fall into any one of the category
		if (pmay != null && StringUtils.isNotBlank(pmay.getPmayCategory())) {
			if (!"NA".equals(pmay.getPmayCategory())) {
				flag = true;
			}
			financeMainDAO.updatePmay(pmay.getFinID(), flag, TableType.MAIN_TAB.getSuffix());
		}

		if (pmay.isNewRecord()) {
			pmay.setFinReference(pmayDAO.save(pmay, tableType));
			auditHeader.getAuditDetail().setModelData(pmay);
			auditHeader.setAuditReference(pmay.getFinReference());
		} else {
			pmayDAO.update(pmay, tableType);
		}

		auditHeaderDAO.addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public AuditDetail saveOrUpdate(PMAY pmay, TableType tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		String[] fields = PennantJavaUtil.getFieldDetails(pmay, pmay.getExcludeFields());

		pmay.setWorkflowId(0);
		if (pmay.isNewRecord()) {
			pmayDAO.save(pmay, tableType);
			List<PmayEligibilityLog> pmayEligibilityLogList = pmay.getPmayEligibilityLogList();
			if (pmayEligibilityLogList != null) {
				for (PmayEligibilityLog pmayEligibilityLog : pmayEligibilityLogList) {
					if (pmayEligibilityLog.isNewRecord()) {
						pmayEligibilityLog.setFinReference(pmay.getFinReference());
						pmayDAO.save(pmayEligibilityLog, TableType.MAIN_TAB);
					} else {
						pmayDAO.update(pmayEligibilityLog, TableType.MAIN_TAB);
					}
				}
			}
		} else {
			try {
				pmayDAO.update(pmay, tableType);
			} catch (ConcurrencyException e) {
				logger.info("ConcurrencyException suppressed");
			}

			List<PmayEligibilityLog> pmayEligibilityLogList = pmay.getPmayEligibilityLogList();
			if (pmayEligibilityLogList != null) {
				for (PmayEligibilityLog pmayEligibilityLog : pmayEligibilityLogList) {
					if (pmayEligibilityLog.isNewRecord()) {
						pmayEligibilityLog.setFinReference(pmay.getFinReference());
						pmayDAO.save(pmayEligibilityLog, TableType.MAIN_TAB);
					} else {
						pmayDAO.update(pmayEligibilityLog, TableType.MAIN_TAB);
					}
				}
			}
		}

		logger.debug(Literal.LEAVING);
		return new AuditDetail(auditTranType, 1, fields[0], fields[1], pmay.getBefImage(), pmay);

	}

	@Override
	public AuditDetail doApprove(PMAY pmay, TableType tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		String[] fields = PennantJavaUtil.getFieldDetails(pmay, pmay.getExcludeFields());

		pmay.setRoleCode("");
		pmay.setNextRoleCode("");
		pmay.setTaskId("");
		pmay.setNextTaskId("");
		pmay.setWorkflowId(0);
		pmay.setRecordType("");
		pmay.setRecordStatus("Approved");
		pmayDAO.save(pmay, tableType);
		List<PmayEligibilityLog> pmayEligibilityLogList = pmay.getPmayEligibilityLogList();
		for (PmayEligibilityLog pmayEligibilityLog : pmayEligibilityLogList) {
			if (pmayEligibilityLog.isNewRecord()) {
				pmayEligibilityLog.setFinReference(pmay.getFinReference());
				pmayDAO.save(pmayEligibilityLog, TableType.MAIN_TAB);
			} else {
				pmayDAO.update(pmayEligibilityLog, TableType.MAIN_TAB);
			}
		}
		// deleting the records from temp while approve
		pmayDAO.delete(pmay, TableType.TEMP_TAB);
		logger.debug(Literal.LEAVING);
		return new AuditDetail(auditTranType, 1, fields[0], fields[1], pmay.getBefImage(), pmay);
	}

	@Override
	public AuditDetail delete(PMAY pmay, TableType tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		String[] fields = PennantJavaUtil.getFieldDetails(pmay, pmay.getExcludeFields());

		pmayDAO.delete(pmay, tableType);
		List<PmayEligibilityLog> pmayEligibilityLogList = pmay.getPmayEligibilityLogList();
		for (PmayEligibilityLog pmayEligibilityLog : pmayEligibilityLogList) {
			// assetBaseDAO.delete(assetBase, tableType.getSuffix());
		}
		logger.debug(Literal.LEAVING);
		return new AuditDetail(auditTranType, 1, fields[0], fields[1], pmay.getBefImage(), pmay);
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}
		PMAY pmay = (PMAY) auditHeader.getAuditDetail().getModelData();
		pmayDAO.delete(pmay, TableType.MAIN_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public PMAY getPMAY(long finID, String tableType) {
		PMAY pmay = pmayDAO.getPMAY(finID, tableType);
		if (pmay != null) {
			List<PmayEligibilityLog> eligibilityLogList = pmayDAO.getEligibilityLogList(finID, "");
			pmay.setPmayEligibilityLogList(eligibilityLogList);
		}
		return pmay;
	}

	@Override
	public PMAY getApprovedPMAY(long finID) {
		return pmayDAO.getPMAY(finID, "_AView");
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		PMAY pmay = new PMAY();
		BeanUtils.copyProperties((PMAY) auditHeader.getAuditDetail().getModelData(), pmay);

		pmayDAO.delete(pmay, TableType.TEMP_TAB);
		PMAY pmayFromLoan = pmayDAO.getPMAY(pmay.getFinID(), "");
		if (!PennantConstants.RECORD_TYPE_NEW.equals(pmay.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(pmayFromLoan);
		}

		if (pmay.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			pmayDAO.delete(pmay, TableType.MAIN_TAB);
		} else {
			pmay.setRoleCode("");
			pmay.setNextRoleCode("");
			pmay.setTaskId("");
			pmay.setNextTaskId("");
			pmay.setWorkflowId(0);

			if (pmay.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW) && pmayFromLoan == null) {
				tranType = PennantConstants.TRAN_ADD;
				pmay.setRecordType("");
				pmayDAO.save(pmay, TableType.MAIN_TAB);
				List<PmayEligibilityLog> pmayEligibilityLogList = pmay.getPmayEligibilityLogList();
				for (PmayEligibilityLog pmayEligibilityLog : pmayEligibilityLogList) {

					if (pmayEligibilityLog.isNewRecord()) {
						pmayEligibilityLog.setFinReference(pmay.getFinReference());
						pmayDAO.save(pmayEligibilityLog, TableType.MAIN_TAB);
					} else {
						pmayDAO.update(pmayEligibilityLog, TableType.MAIN_TAB);
					}
				}
			} else {
				tranType = PennantConstants.TRAN_UPD;
				pmay.setRecordType("");
				pmayDAO.update(pmay, TableType.MAIN_TAB);
				List<PmayEligibilityLog> pmayEligibilityLogList = pmay.getPmayEligibilityLogList();
				for (PmayEligibilityLog pmayEligibilityLog : pmayEligibilityLogList) {

					if (pmayEligibilityLog.isNewRecord()) {
						pmayEligibilityLog.setFinReference(pmay.getFinReference());
						pmayDAO.save(pmayEligibilityLog, TableType.MAIN_TAB);
					} else {
						pmayDAO.update(pmayEligibilityLog, TableType.MAIN_TAB);
					}
				}
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(pmay);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		PMAY pmay = (PMAY) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		pmayDAO.delete(pmay, TableType.TEMP_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		PMAY pmay = (PMAY) auditDetail.getModelData();

		String[] parameters = new String[2];
		parameters[0] = PennantJavaUtil.getLabel("label_PmayDialog_FinReference.value") + ": " + pmay.getFinReference();
		// Check the unique keys.
		if (pmay.isNewRecord() && PennantConstants.RECORD_TYPE_NEW.equals(pmay.getRecordType()) && pmayDAO
				.isDuplicateKey(pmay.getFinID(), pmay.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@Override
	public long generateDocSeq() {
		return pmayDAO.generateDocSeq();
	}

	@Override
	public List<PmayEligibilityLog> getAllRecordIdForPmay() {
		return pmayDAO.getAllRecordIdForPmay();
	}

	@Override
	public void update(PmayEligibilityLog pmayEligibilityLog) {
		logger.debug(Literal.ENTERING);
		pmayDAO.update(pmayEligibilityLog);
		if (StringUtils.isNotEmpty(pmayEligibilityLog.getApplicantId())) {
			String custCif = pmayDAO.getCustCif(pmayEligibilityLog.getFinID());
			pmayDAO.update(custCif, pmayEligibilityLog.getApplicantId());
		}

		logger.debug(Literal.LEAVING);
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setPmayDAO(PMAYDAO pmayDAO) {
		this.pmayDAO = pmayDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

}
