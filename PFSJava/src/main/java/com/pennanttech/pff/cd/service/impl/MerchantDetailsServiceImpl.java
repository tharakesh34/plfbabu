package com.pennanttech.pff.cd.service.impl;

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
import com.pennanttech.pff.cd.model.MerchantDetails;
import com.pennanttech.pff.cd.service.MerchantDetailsService;
import com.pennanttech.pff.core.TableType;
import com.pennattech.pff.cd.dao.MerchantDetailsDAO;

public class MerchantDetailsServiceImpl extends GenericService<MerchantDetails> implements MerchantDetailsService {
	private static final Logger logger = LogManager.getLogger(MerchantDetailsServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private MerchantDetailsDAO merchantDetailsDAO;

	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}
		MerchantDetails merchantDetails = (MerchantDetails) auditHeader.getAuditDetail().getModelData();
		TableType tableType = TableType.MAIN_TAB;
		if (merchantDetails.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}
		if (merchantDetails.isNew()) {
			merchantDetails.setMerchantId(Long.parseLong(merchantDetailsDAO.save(merchantDetails, tableType)));
			auditHeader.getAuditDetail().setModelData(merchantDetails);
			auditHeader.setAuditReference(String.valueOf(merchantDetails.getMerchantId()));
		} else {
			merchantDetailsDAO.update(merchantDetails, tableType);
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

		MerchantDetails merchantDetails = (MerchantDetails) auditHeader.getAuditDetail().getModelData();
		merchantDetailsDAO.delete(merchantDetails, TableType.MAIN_TAB);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public MerchantDetails getMerchantDetails(long id) {
		return merchantDetailsDAO.getMerchantDetails(id, "_View");
	}

	public MerchantDetails getApprovedMerchantDetails(long id) {
		return merchantDetailsDAO.getMerchantDetails(id, "_AView");
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

		MerchantDetails merchantDetails = new MerchantDetails();
		BeanUtils.copyProperties((MerchantDetails) auditHeader.getAuditDetail().getModelData(), merchantDetails);

		merchantDetailsDAO.delete(merchantDetails, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(merchantDetails.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(merchantDetailsDAO.getMerchantDetails(merchantDetails.getMerchantId(), ""));
		}

		if (merchantDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			merchantDetailsDAO.delete(merchantDetails, TableType.MAIN_TAB);
		} else {
			merchantDetails.setRoleCode("");
			merchantDetails.setNextRoleCode("");
			merchantDetails.setTaskId("");
			merchantDetails.setNextTaskId("");
			merchantDetails.setWorkflowId(0);

			if (merchantDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				merchantDetails.setRecordType("");
				merchantDetailsDAO.save(merchantDetails, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				merchantDetails.setRecordType("");
				merchantDetailsDAO.update(merchantDetails, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(merchantDetails);
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

		MerchantDetails merchantDetails = (MerchantDetails) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		merchantDetailsDAO.delete(merchantDetails, TableType.TEMP_TAB);
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
		MerchantDetails merchantDetails = (MerchantDetails) auditDetail.getModelData();
		Long storeId = merchantDetails.getStoreId();
		int posId = merchantDetails.getPOSId();

		// Check the unique keys.
		if (merchantDetails.isNew() && PennantConstants.RECORD_TYPE_NEW.equals(merchantDetails.getRecordType())
				&& merchantDetailsDAO.isDuplicateKey(merchantDetails,
						merchantDetails.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_MerchantDetails_StoreId.value") + ": " + storeId + " And "
					+ PennantJavaUtil.getLabel("label_TransactionMapping_POSId.value") + ": " + posId + " combination ";
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41014", parameters, null));
		}

		if (merchantDetails.isNew() && PennantConstants.RECORD_TYPE_NEW.equals(merchantDetails.getRecordType())
				&& merchantDetailsDAO.isDuplicatePOSIdKey(merchantDetails,
						merchantDetails.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_TransactionMapping_POSId.value") + ": " + posId;
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41014", parameters, null));
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public MerchantDetailsDAO getMerchantDetailsDAO() {
		return merchantDetailsDAO;
	}

	public void setMerchantDetailsDAO(MerchantDetailsDAO merchantDetailsDAO) {
		this.merchantDetailsDAO = merchantDetailsDAO;
	}
}
