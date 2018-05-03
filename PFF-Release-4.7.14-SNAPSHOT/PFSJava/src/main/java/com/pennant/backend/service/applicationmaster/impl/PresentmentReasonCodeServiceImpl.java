package com.pennant.backend.service.applicationmaster.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.PresentmentReasonCodeDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.applicationmaster.PresentmentReasonCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.PresentmentReasonCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class PresentmentReasonCodeServiceImpl extends GenericService<PresentmentReasonCode> implements PresentmentReasonCodeService {
	private static Logger logger = Logger.getLogger(PresentmentReasonCodeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private PresentmentReasonCodeDAO presentmentReasonCodeDAO;

	public PresentmentReasonCodeServiceImpl() {
		super();
	}
	

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType = "";
		PresentmentReasonCode presentmentReasonCode = (PresentmentReasonCode) auditHeader.getAuditDetail().getModelData();

		if (presentmentReasonCode.isWorkflow()) {
			tableType = "_Temp";
		}

		if (presentmentReasonCode.isNew()) {
			presentmentReasonCode.setCode(getPresentmentReasonCodeDAO().save(presentmentReasonCode, tableType));
			auditHeader.getAuditDetail().setModelData(presentmentReasonCode);
			auditHeader.setAuditReference(presentmentReasonCode.getCode());
		} else {
			getPresentmentReasonCodeDAO().update(presentmentReasonCode, tableType);
		}
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	@Override
	public PresentmentReasonCode getPresentmentReasonCodeById(String id) {
		return getPresentmentReasonCodeDAO().getPresentmentReasonCodeById(id, "_View");
	}

	@Override
	public PresentmentReasonCode getApprovedPresentmentReasonCodeById(String id) {
		return getPresentmentReasonCodeDAO().getPresentmentReasonCodeById(id, "_AView");
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		PresentmentReasonCode presentmentReasonCode = (PresentmentReasonCode) auditHeader.getAuditDetail().getModelData();
		getPresentmentReasonCodeDAO().delete(presentmentReasonCode, "");
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
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
		PresentmentReasonCode presentmentReasonCode = new PresentmentReasonCode();
		BeanUtils.copyProperties((PresentmentReasonCode) auditHeader.getAuditDetail().getModelData(), presentmentReasonCode);
		if (presentmentReasonCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getPresentmentReasonCodeDAO().delete(presentmentReasonCode, "");
		} else {
			presentmentReasonCode.setRoleCode("");
			presentmentReasonCode.setNextRoleCode("");
			presentmentReasonCode.setTaskId("");
			presentmentReasonCode.setNextTaskId("");
			presentmentReasonCode.setWorkflowId(0);

			if (presentmentReasonCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				presentmentReasonCode.setRecordType("");
				getPresentmentReasonCodeDAO().save(presentmentReasonCode, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				presentmentReasonCode.setRecordType("");
				getPresentmentReasonCodeDAO().update(presentmentReasonCode, "");
			}
		}
		getPresentmentReasonCodeDAO().delete(presentmentReasonCode, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(presentmentReasonCode);
		getAuditHeaderDAO().addAudit(auditHeader);
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
		PresentmentReasonCode presentmentReasonCode = (PresentmentReasonCode) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getPresentmentReasonCodeDAO().delete(presentmentReasonCode, "_Temp");
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}


	private AuditHeader businessValidation(AuditHeader auditHeader,
			String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage,
			String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());

		PresentmentReasonCode presentmentReasonCode = (PresentmentReasonCode) auditDetail.getModelData();
		PresentmentReasonCode tempPresentmentReasonCode = null;

		if (presentmentReasonCode.isWorkflow()) {
			tempPresentmentReasonCode = getPresentmentReasonCodeDAO().getPresentmentReasonCodeById(presentmentReasonCode.getId(), "_Temp");
		}

		PresentmentReasonCode befPresentmentReasonCode = getPresentmentReasonCodeDAO().getPresentmentReasonCodeById(presentmentReasonCode.getId(), "");
		PresentmentReasonCode oldPresentmentReasonCode = presentmentReasonCode.getBefImage() ;

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = presentmentReasonCode.getCode();
		errParm[0] = PennantJavaUtil.getLabel("label_Code") + ":"+ valueParm[0];

		if (presentmentReasonCode.isNew()) { // for New record or new record into work flow

			if (!presentmentReasonCode.isWorkflow()) {// With out Work flow only new records
				if (befPresentmentReasonCode != null) { // Record Already Exists in the table then error
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001",errParm, null));
				}
			} else { // with work flow
				if (presentmentReasonCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new 
					if (befPresentmentReasonCode != null || tempPresentmentReasonCode != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,null));
					}
				} else { // if records not exists in the Main flow table
					if (befPresentmentReasonCode == null || tempPresentmentReasonCode != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!presentmentReasonCode.isWorkflow()) { // With out Work flow for update and delete
				if (befPresentmentReasonCode == null) { // if records not exists in the main table
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002",errParm, null));
				} else {
					if (oldPresentmentReasonCode != null
							&& !oldPresentmentReasonCode.getLastMntOn().equals(befPresentmentReasonCode.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003",errParm, null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004",errParm, null));
						}
					}
				}
			} else {
				if (tempPresentmentReasonCode == null) { // if records not exists in the Work flow table
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005",errParm, null));
				}

				if (tempPresentmentReasonCode != null
						&& oldPresentmentReasonCode != null
						&& !oldPresentmentReasonCode.getLastMntOn().equals(tempPresentmentReasonCode.getLastMntOn())) {
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005",errParm, null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if ("doApprove".equals(StringUtils.trimToEmpty(method))
				|| !presentmentReasonCode.isWorkflow()) {
			auditDetail.setBefImage(befPresentmentReasonCode);
		}
		logger.debug("Leaving");
		return auditDetail;
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

	public PresentmentReasonCodeDAO getPresentmentReasonCodeDAO() {
		return presentmentReasonCodeDAO;
	}


	public void setPresentmentReasonCodeDAO(PresentmentReasonCodeDAO presentmentReasonCodeDAO) {
		this.presentmentReasonCodeDAO = presentmentReasonCodeDAO;
	}
}
