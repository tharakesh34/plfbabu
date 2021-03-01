package com.pennant.backend.service.dealermapping.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.dealermapping.DealerMappingDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.dealermapping.DealerMapping;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.dealermapping.DealerMappingService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class DealerMappingServiceImpl extends GenericService<DealerMapping> implements DealerMappingService {

	private static final Logger logger = LogManager.getLogger(DealerMappingServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private DealerMappingDAO dealerMappingDAO;

	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}
		DealerMapping dealerMapping = (DealerMapping) auditHeader.getAuditDetail().getModelData();
		TableType tableType = TableType.MAIN_TAB;
		if (dealerMapping.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}
		if (dealerMapping.isNew()) {
			dealerMapping.setId(Long.parseLong(dealerMappingDAO.save(dealerMapping, tableType)));
			auditHeader.getAuditDetail().setModelData(dealerMapping);
			auditHeader.setAuditReference(String.valueOf(dealerMapping.getId()));
		} else {
			dealerMappingDAO.update(dealerMapping, tableType);
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

		DealerMapping dealerMapping = (DealerMapping) auditHeader.getAuditDetail().getModelData();
		dealerMappingDAO.delete(dealerMapping, TableType.MAIN_TAB);
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

		DealerMapping dealerMapping = new DealerMapping();
		BeanUtils.copyProperties((DealerMapping) auditHeader.getAuditDetail().getModelData(), dealerMapping);

		dealerMappingDAO.delete(dealerMapping, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(dealerMapping.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(dealerMappingDAO.getDealerMappingById(dealerMapping.getId(), ""));
		}

		if (dealerMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			dealerMappingDAO.delete(dealerMapping, TableType.MAIN_TAB);
		} else {
			dealerMapping.setRoleCode("");
			dealerMapping.setNextRoleCode("");
			dealerMapping.setTaskId("");
			dealerMapping.setNextTaskId("");
			dealerMapping.setWorkflowId(0);

			if (dealerMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				dealerMapping.setRecordType("");
				dealerMappingDAO.save(dealerMapping, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				dealerMapping.setRecordType("");
				dealerMappingDAO.update(dealerMapping, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(dealerMapping);
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

		DealerMapping dealerMapping = (DealerMapping) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		dealerMappingDAO.delete(dealerMapping, TableType.TEMP_TAB);
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
		DealerMapping dealerMapping = (DealerMapping) auditDetail.getModelData();
		String code = String.valueOf(dealerMapping.getMerchantName());

		// Check the unique keys.
		if (dealerMapping.isNew() && PennantConstants.RECORD_TYPE_NEW.equals(dealerMapping.getRecordType())
				&& dealerMappingDAO.isDuplicateKey(dealerMapping,
						dealerMapping.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_DealerMapping_MerchantName.value") + ": " + code;
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41014", parameters, null));
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@Override
	public long getDealerCode() {
		return dealerMappingDAO.getDealerCode();
	}

	@Override
	public DealerMapping getDealerMappingById(long id) {
		return dealerMappingDAO.getDealerMappingById(id, "_View");
	}

	public void setDealerMappingDAO(DealerMappingDAO dealerMappingDAO) {
		this.dealerMappingDAO = dealerMappingDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

}
