package com.pennant.backend.service.systemmasters.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.systemmasters.OCRDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.ocrmaster.OCRDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.OCRDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class OCRDetailServiceImpl extends GenericService<OCRDetail> implements OCRDetailService {
	private static Logger logger = LogManager.getLogger(OCRDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private OCRDetailDAO ocrDetailDAO;

	public OCRDetailServiceImpl() {
		super();
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		OCRDetail ocrDetail = (OCRDetail) auditHeader.getAuditDetail().getModelData();
		TableType tableType = TableType.MAIN_TAB;
		if (ocrDetail.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (ocrDetail.isNew()) {
			ocrDetailDAO.save(ocrDetail, tableType);
			auditHeader.getAuditDetail().setModelData(ocrDetail);
			auditHeader.setAuditReference(ocrDetail.getDetailID() + PennantConstants.KEY_SEPERATOR
					+ ocrDetail.getHeaderID() + PennantConstants.KEY_SEPERATOR + ocrDetail.getStepSequence());

		} else {
			ocrDetailDAO.update(ocrDetail, tableType);
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

		OCRDetail ocrDetail = (OCRDetail) auditHeader.getAuditDetail().getModelData();
		ocrDetailDAO.delete(ocrDetail, TableType.MAIN_TAB);

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public OCRDetail getOCRDetailById(long detailID, int stepSequence, long headerID) {
		return ocrDetailDAO.getOCRDetail(headerID, "_View");
	}

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		OCRDetail ocrDetail = new OCRDetail();
		BeanUtils.copyProperties((OCRDetail) auditHeader.getAuditDetail().getModelData(), ocrDetail);

		ocrDetailDAO.delete(ocrDetail, TableType.TEMP_TAB);
		if (!PennantConstants.RECORD_TYPE_NEW.equals(ocrDetail.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(ocrDetailDAO.getOCRDetail(ocrDetail.getHeaderID(), ""));
		}

		if (ocrDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			ocrDetailDAO.delete(ocrDetail, TableType.MAIN_TAB);

		} else {
			ocrDetail.setRoleCode("");
			ocrDetail.setNextRoleCode("");
			ocrDetail.setTaskId("");
			ocrDetail.setNextTaskId("");
			ocrDetail.setWorkflowId(0);

			if (ocrDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				ocrDetail.setRecordType("");
				ocrDetailDAO.save(ocrDetail, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				ocrDetail.setRecordType("");
				ocrDetailDAO.update(ocrDetail, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(ocrDetail);
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		OCRDetail ocrDetail = (OCRDetail) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		ocrDetailDAO.delete(ocrDetail, TableType.TEMP_TAB);

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

		// Get the model object.
		OCRDetail ocrDetail = (OCRDetail) auditDetail.getModelData();

		// Check the unique keys.
		if (ocrDetail.isNew() && PennantConstants.RECORD_TYPE_NEW.equals(ocrDetail.getRecordType())
				&& ocrDetailDAO.isDuplicateKey(ocrDetail.getStepSequence(), ocrDetail.getDetailID(),
						ocrDetail.getHeaderID(), ocrDetail.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];

			parameters[0] = PennantJavaUtil.getLabel("label_PCCountry") + ":" + ocrDetail.getStepSequence();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41008", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@Override
	public OCRDetail getApprovedOCRDetailById(long detailID, int stepSequence, long headerID) {
		return ocrDetailDAO.getOCRDetail(detailID, "_AView");
	}

	public void setOcrDetailDAO(OCRDetailDAO ocrDetailDAO) {
		this.ocrDetailDAO = ocrDetailDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

}
