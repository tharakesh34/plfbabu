package com.pennant.backend.service.applicationmaster.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.SettlementTypeDetailDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.applicationmaster.SettlementTypeDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.SettlementTypeDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.TableType;

public class SettlementTypeDetailServiceImpl extends GenericService<SettlementTypeDetail>
		implements SettlementTypeDetailService {

	private static Logger logger = LogManager.getLogger(SettlementTypeDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private SettlementTypeDetailDAO settlementTypeDetailDAO;

	public SettlementTypeDetailServiceImpl() {
		super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public SettlementTypeDetailDAO getSettlementTypeDetailDAO() {
		return settlementTypeDetailDAO;
	}

	public void setSettlementTypeDetailDAO(SettlementTypeDetailDAO settlementTypeDetailDAO) {
		this.settlementTypeDetailDAO = settlementTypeDetailDAO;
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {

		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		SettlementTypeDetail settlementTypeDetail = (SettlementTypeDetail) auditHeader.getAuditDetail().getModelData();
		TableType tableType = TableType.MAIN_TAB;

		if (settlementTypeDetail.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (settlementTypeDetail.isNewRecord()) {
			settlementTypeDetail.setId(settlementTypeDetailDAO.save(settlementTypeDetail, tableType));
			auditHeader.getAuditDetail().setModelData(settlementTypeDetail);
			auditHeader.setAuditReference(String.valueOf(settlementTypeDetail.getId()));
		} else {
			settlementTypeDetailDAO.update(settlementTypeDetail, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {

		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		SettlementTypeDetail settlementTypeDetail = (SettlementTypeDetail) auditHeader.getAuditDetail().getModelData();

		getSettlementTypeDetailDAO().delete(settlementTypeDetail, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	@Override
	public SettlementTypeDetail getSettlementByCode(String code) {
		return getSettlementTypeDetailDAO().getSettlementByCode(code, "_View");
	}

	@Override
	public SettlementTypeDetail getSettlementById(long id) {
		return getSettlementTypeDetailDAO().getSettlementById(id, "_View");
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {

		logger.debug("Entering");

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		SettlementTypeDetail settlementTypeDetail = new SettlementTypeDetail();
		BeanUtils.copyProperties(auditHeader.getAuditDetail().getModelData(), settlementTypeDetail);

		getSettlementTypeDetailDAO().delete(settlementTypeDetail, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(settlementTypeDetail.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(settlementTypeDetailDAO.getSettlementById(settlementTypeDetail.getId(), ""));
		}

		if (settlementTypeDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getSettlementTypeDetailDAO().delete(settlementTypeDetail, TableType.MAIN_TAB);

		} else {
			settlementTypeDetail.setRoleCode("");
			settlementTypeDetail.setNextRoleCode("");
			settlementTypeDetail.setTaskId("");
			settlementTypeDetail.setNextTaskId("");
			settlementTypeDetail.setWorkflowId(0);

			if (settlementTypeDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				settlementTypeDetail.setRecordType("");
				getSettlementTypeDetailDAO().save(settlementTypeDetail, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				settlementTypeDetail.setRecordType("");
				getSettlementTypeDetailDAO().update(settlementTypeDetail, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(settlementTypeDetail);
		// getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {

		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		SettlementTypeDetail settlementTypeDetail = (SettlementTypeDetail) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getSettlementTypeDetailDAO().delete(settlementTypeDetail, TableType.TEMP_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug("Entering");

		// Get the model object.
		SettlementTypeDetail settlementTypeDetail = (SettlementTypeDetail) auditDetail.getModelData();
		String code = settlementTypeDetail.getSettlementCode();
		long id = settlementTypeDetail.getId();

		// Check the unique keys.
		if (settlementTypeDetail.isNewRecord()
				&& PennantConstants.RECORD_TYPE_NEW.equals(settlementTypeDetail.getRecordType())
				&& settlementTypeDetailDAO.isDuplicateKey(code, id,
						settlementTypeDetail.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_SettlementCode") + ": " + code;

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	}

}
