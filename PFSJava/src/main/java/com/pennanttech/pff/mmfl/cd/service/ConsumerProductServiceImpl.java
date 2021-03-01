package com.pennanttech.pff.mmfl.cd.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.mmfl.cd.model.ConsumerProduct;
import com.pennattech.pff.mmfl.cd.dao.ConsumerProductDAO;

public class ConsumerProductServiceImpl extends GenericService<ConsumerProduct> implements ConsumerProductService {
	private static final Logger logger = LogManager.getLogger(ConsumerProductServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private ConsumerProductDAO consumerProductDAO;

	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}
		ConsumerProduct consumerProduct = (ConsumerProduct) auditHeader.getAuditDetail().getModelData();
		TableType tableType = TableType.MAIN_TAB;
		if (consumerProduct.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}
		if (consumerProduct.isNew()) {
			consumerProduct.setProductId(Long.parseLong(consumerProductDAO.save(consumerProduct, tableType)));
			auditHeader.getAuditDetail().setModelData(consumerProduct);
			auditHeader.setAuditReference(String.valueOf(consumerProduct.getProductId()));
		} else {
			consumerProductDAO.update(consumerProduct, tableType);
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

		ConsumerProduct consumerProduct = (ConsumerProduct) auditHeader.getAuditDetail().getModelData();
		consumerProductDAO.delete(consumerProduct, TableType.MAIN_TAB);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public ConsumerProduct getConsumerProduct(long id) {
		return consumerProductDAO.getConsumerProduct(id, "_View");
	}

	public ConsumerProduct getApprovedConsumerProduct(long id) {
		return consumerProductDAO.getConsumerProduct(id, "_AView");
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

		ConsumerProduct consumerProduct = new ConsumerProduct();
		BeanUtils.copyProperties((ConsumerProduct) auditHeader.getAuditDetail().getModelData(), consumerProduct);

		consumerProductDAO.delete(consumerProduct, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(consumerProduct.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(consumerProductDAO.getConsumerProduct(consumerProduct.getProductId(), ""));
		}

		if (consumerProduct.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			consumerProductDAO.delete(consumerProduct, TableType.MAIN_TAB);
		} else {
			consumerProduct.setRoleCode("");
			consumerProduct.setNextRoleCode("");
			consumerProduct.setTaskId("");
			consumerProduct.setNextTaskId("");
			consumerProduct.setWorkflowId(0);

			if (consumerProduct.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				consumerProduct.setRecordType("");
				consumerProductDAO.save(consumerProduct, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				consumerProduct.setRecordType("");
				consumerProductDAO.update(consumerProduct, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(consumerProduct);
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

		ConsumerProduct consumerProduct = (ConsumerProduct) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		consumerProductDAO.delete(consumerProduct, TableType.TEMP_TAB);
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
		ConsumerProduct ConsumerProduct = (ConsumerProduct) auditDetail.getModelData();
		String code = ConsumerProduct.getModelId();

		// Check the unique keys.
		if (ConsumerProduct.isNew() && PennantConstants.RECORD_TYPE_NEW.equals(ConsumerProduct.getRecordType())
				&& consumerProductDAO.isDuplicateKey(ConsumerProduct,
						ConsumerProduct.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_StockCompanyDialogue_CompanyCode.value") + ": " + code;
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41014", parameters, null));
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setConsumerProductDAO(ConsumerProductDAO consumerProductDAO) {
		this.consumerProductDAO = consumerProductDAO;
	}

}
