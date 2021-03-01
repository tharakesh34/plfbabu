package com.pennant.backend.service.finance.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinOCRDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinOCRDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinOCRDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class FinOCRDetailServiceImpl extends GenericService<FinOCRDetail> implements FinOCRDetailService {
	private static final Logger logger = LogManager.getLogger(FinOCRDetailServiceImpl.class);
	private AuditHeaderDAO auditHeaderDAO;
	private FinOCRDetailDAO finOCRDetailDAO;

	public FinOCRDetailServiceImpl() {
		super();
	}

	@Override
	public List<FinOCRDetail> getFinOCRDetailByHeaderID(long headerID, String type) {
		return finOCRDetailDAO.getFinOCRDetailsByHeaderID(headerID, type);
	}

	@Override
	public FinOCRDetail getFinOCRDetailById(long detailID, String type) {
		return finOCRDetailDAO.getFinOCRDetailById(detailID, type);
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		auditHeader = businessValidation(auditHeader);

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		FinOCRDetail finOCRDetail = (FinOCRDetail) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (finOCRDetail.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (finOCRDetail.isNew()) {
			finOCRDetail.setDetailID(finOCRDetailDAO.save(finOCRDetail, tableType.getSuffix()));
			auditHeader.getAuditDetail().setModelData(finOCRDetail);
			auditHeader.setAuditReference(String.valueOf(finOCRDetail.getDetailID()));
		} else {
			finOCRDetailDAO.update(finOCRDetail, tableType.getSuffix());
		}

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		auditHeader = businessValidation(auditHeader);

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FinOCRDetail finOCRDetail = (FinOCRDetail) auditHeader.getAuditDetail().getModelData();
		finOCRDetailDAO.delete(finOCRDetail, TableType.MAIN_TAB.getSuffix());

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		String tranType = "";
		auditHeader = businessValidation(auditHeader);

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FinOCRDetail finOCRDetail = new FinOCRDetail();
		BeanUtils.copyProperties((FinOCRDetail) auditHeader.getAuditDetail().getModelData(), finOCRDetail);

		finOCRDetailDAO.delete(finOCRDetail, TableType.TEMP_TAB.getSuffix());

		if (!PennantConstants.RECORD_TYPE_NEW.equals(finOCRDetail.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(
					finOCRDetailDAO.getFinOCRDetailById(finOCRDetail.getDetailID(), TableType.TEMP_TAB.getSuffix()));
		}

		if (finOCRDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			finOCRDetailDAO.delete(finOCRDetail, TableType.MAIN_TAB.getSuffix());
		} else {
			finOCRDetail.setRoleCode("");
			finOCRDetail.setNextRoleCode("");
			finOCRDetail.setTaskId("");
			finOCRDetail.setNextTaskId("");
			finOCRDetail.setWorkflowId(0);

			if (finOCRDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				finOCRDetail.setRecordType("");
				finOCRDetailDAO.save(finOCRDetail, TableType.MAIN_TAB.getSuffix());
			} else {
				tranType = PennantConstants.TRAN_UPD;
				finOCRDetail.setRecordType("");
				finOCRDetailDAO.update(finOCRDetail, TableType.MAIN_TAB.getSuffix());
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(finOCRDetail);
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FinOCRDetail finOCRDetail = (FinOCRDetail) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		finOCRDetailDAO.delete(finOCRDetail, TableType.TEMP_TAB.getSuffix());

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader) {
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

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setFinOCRDetailDAO(FinOCRDetailDAO finOCRDetailDAO) {
		this.finOCRDetailDAO = finOCRDetailDAO;
	}

}
