package com.pennant.backend.service.transactionmapping.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.transactionmapping.TransactionMappingDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.transactionmapping.TransactionMappingService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.mmfl.cd.model.TransactionMapping;

public class TransactionMappingServiceImpl extends GenericService<TransactionMapping>
		implements TransactionMappingService {

	private static final Logger logger = LogManager.getLogger(TransactionMappingServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private TransactionMappingDAO transactionMappingDAO;

	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}
		TransactionMapping mapping = (TransactionMapping) auditHeader.getAuditDetail().getModelData();
		TableType tableType = TableType.MAIN_TAB;
		if (mapping.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}
		if (mapping.isNew()) {
			mapping.setId(Long.parseLong(transactionMappingDAO.save(mapping, tableType)));
			auditHeader.getAuditDetail().setModelData(mapping);
			auditHeader.setAuditReference(String.valueOf(mapping.getId()));
		} else {
			transactionMappingDAO.update(mapping, tableType);
		}
		auditHeaderDAO.addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		TransactionMapping mapping = (TransactionMapping) auditHeader.getAuditDetail().getModelData();
		transactionMappingDAO.delete(mapping, TableType.MAIN_TAB);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
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

		TransactionMapping mapping = new TransactionMapping();
		BeanUtils.copyProperties((TransactionMapping) auditHeader.getAuditDetail().getModelData(), mapping);

		transactionMappingDAO.delete(mapping, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(mapping.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(transactionMappingDAO.getTransactionMappingById(mapping.getId(), ""));
		}

		if (mapping.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			transactionMappingDAO.delete(mapping, TableType.MAIN_TAB);
		} else {
			mapping.setRoleCode("");
			mapping.setNextRoleCode("");
			mapping.setTaskId("");
			mapping.setNextTaskId("");
			mapping.setWorkflowId(0);

			if (mapping.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				mapping.setRecordType("");
				transactionMappingDAO.save(mapping, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				mapping.setRecordType("");
				transactionMappingDAO.update(mapping, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(mapping);
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

		TransactionMapping mapping = (TransactionMapping) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		transactionMappingDAO.delete(mapping, TableType.TEMP_TAB);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public TransactionMapping getTransactionMappingById(long id) {
		return transactionMappingDAO.getTransactionMappingById(id, "_View");
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
		TransactionMapping mapping = (TransactionMapping) auditDetail.getModelData();
		String code = String.valueOf(mapping.getTid());

		// Check the unique keys.
		if (mapping.isNew() && PennantConstants.RECORD_TYPE_NEW.equals(mapping.getRecordType()) && transactionMappingDAO
				.isDuplicateKey(mapping, mapping.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_TransactionMapping_TID.value") + ": " + code;
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41014", parameters, null));
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setTransactionMappingDAO(TransactionMappingDAO transactionMappingDAO) {
		this.transactionMappingDAO = transactionMappingDAO;
	}

}
