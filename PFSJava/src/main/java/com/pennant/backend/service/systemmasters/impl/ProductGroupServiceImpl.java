package com.pennant.backend.service.systemmasters.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.systemmasters.ProductGroupDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.ProductGroup;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.ProductGroupService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class ProductGroupServiceImpl extends GenericService<ProductGroup> implements ProductGroupService {

	private static final Logger logger = LogManager.getLogger(ProductGroupServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private ProductGroupDAO productGroupDAO;

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		ProductGroup productGroup = (ProductGroup) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (productGroup.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (productGroup.isNewRecord()) {
			productGroup.setId(Long.parseLong(getProductGroupDAO().save(productGroup, tableType)));
			auditHeader.getAuditDetail().setModelData(productGroup);
			auditHeader.setAuditReference(String.valueOf(productGroup.getId()));
		} else {
			getProductGroupDAO().update(productGroup, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public ProductGroup getProductGroup(long id) {
		return getProductGroupDAO().getProductGroup(id, "_View");
	}

	@Override
	public ProductGroup getApprovedProductGroup(long id) {
		return getProductGroupDAO().getProductGroup(id, "_AView");
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		ProductGroup productGroup = (ProductGroup) auditHeader.getAuditDetail().getModelData();
		getProductGroupDAO().delete(productGroup, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

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

		ProductGroup productGroup = new ProductGroup();
		BeanUtils.copyProperties((ProductGroup) auditHeader.getAuditDetail().getModelData(), productGroup);

		getProductGroupDAO().delete(productGroup, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(productGroup.getRecordType())) {
			/*
			 * auditHeader.getAuditDetail().setBefImage(((DealerGroupDAO)
			 * dealerGroup).getDealerGroup(dealerGroup.getId(), ""));
			 */
			auditHeader.getAuditDetail()
					.setBefImage(getProductGroupDAO().getProductGroup(productGroup.getProductGroupId(), ""));
		}

		if (productGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getProductGroupDAO().delete(productGroup, TableType.MAIN_TAB);
		} else {
			productGroup.setRoleCode("");
			productGroup.setNextRoleCode("");
			productGroup.setTaskId("");
			productGroup.setNextTaskId("");
			productGroup.setWorkflowId(0);

			if (productGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				productGroup.setRecordType("");
				getProductGroupDAO().save(productGroup, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				productGroup.setRecordType("");
				getProductGroupDAO().update(productGroup, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(productGroup);
		getAuditHeaderDAO().addAudit(auditHeader);

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

		ProductGroup productGroup = (ProductGroup) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getProductGroupDAO().delete(productGroup, TableType.TEMP_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public ProductGroupDAO getProductGroupDAO() {
		return productGroupDAO;
	}

	public void setProductGroupDAO(ProductGroupDAO productGroupDAO) {
		this.productGroupDAO = productGroupDAO;
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
		ProductGroup productGroup = (ProductGroup) auditDetail.getModelData();

		String[] parameters = new String[2];
		parameters[0] = PennantJavaUtil.getLabel("label_dealercode") + ": " + productGroup.getModelId();
		parameters[1] = PennantJavaUtil.getLabel("label_groupId") + ": " + productGroup.getProductCategoryId();

		// Check the unique keys.
		if (productGroup.isNewRecord() && getProductGroupDAO().isDuplicateKey(productGroup.getId(),
				productGroup.getModelId(), productGroup.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}
		/*
		 * // If Builder Group is already utilized in Builder Company if
		 * (StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, productGroup.getRecordType())) { boolean workflowExists
		 * = getProductGroupDAO().isIdExists(productGroup.getId()); if (workflowExists) { auditDetail.setErrorDetail(new
		 * ErrorDetail(PennantConstants.KEY_FIELD, "41006", parameters, null)); } }
		 */

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

}
