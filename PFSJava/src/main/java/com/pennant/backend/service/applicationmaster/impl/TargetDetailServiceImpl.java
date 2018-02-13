package com.pennant.backend.service.applicationmaster.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.TargetDetailDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.applicationmaster.TargetDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.TargetDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class TargetDetailServiceImpl extends GenericService<TargetDetail> implements TargetDetailService {
	private static Logger logger = Logger.getLogger(TargetDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private TargetDetailDAO targetDetailDAO;

	public TargetDetailServiceImpl() {
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
		TargetDetail targetDetail = (TargetDetail) auditHeader.getAuditDetail().getModelData();

		if (targetDetail.isWorkflow()) {
			tableType = "_Temp";
		}

		if (targetDetail.isNew()) {
			targetDetail.setTargetCode(getTargetDetailDAO().save(targetDetail, tableType));
			auditHeader.getAuditDetail().setModelData(targetDetail);
			auditHeader.setAuditReference(targetDetail.getTargetCode());
		} else {
			getTargetDetailDAO().update(targetDetail, tableType);
		}
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	@Override
	public TargetDetail getTargetDetailById(String id) {
		return getTargetDetailDAO().getTargetDetailById(id, "_View");
	}

	@Override
	public TargetDetail getApprovedTargetDetailById(String id) {
		return getTargetDetailDAO().getTargetDetailById(id, "_AView");
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		TargetDetail targetDetail = (TargetDetail) auditHeader.getAuditDetail().getModelData();
		getTargetDetailDAO().delete(targetDetail, "");
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
		TargetDetail targetDetail = new TargetDetail();
		BeanUtils.copyProperties((TargetDetail) auditHeader.getAuditDetail().getModelData(), targetDetail);
		if (targetDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getTargetDetailDAO().delete(targetDetail, "");
		} else {
			targetDetail.setRoleCode("");
			targetDetail.setNextRoleCode("");
			targetDetail.setTaskId("");
			targetDetail.setNextTaskId("");
			targetDetail.setWorkflowId(0);

			if (targetDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				targetDetail.setRecordType("");
				getTargetDetailDAO().save(targetDetail, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				targetDetail.setRecordType("");
				getTargetDetailDAO().update(targetDetail, "");
			}
		}
		getTargetDetailDAO().delete(targetDetail, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(targetDetail);
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
		TargetDetail targetDetail = (TargetDetail) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getTargetDetailDAO().delete(targetDetail, "_Temp");
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

		TargetDetail targetDetail = (TargetDetail) auditDetail.getModelData();
		TargetDetail tempTargetDetail = null;

		if (targetDetail.isWorkflow()) {
			tempTargetDetail = getTargetDetailDAO().getTargetDetailById(targetDetail.getId(), "_Temp");
		}

		TargetDetail beftargetDetail = getTargetDetailDAO().getTargetDetailById(targetDetail.getId(), "");
		TargetDetail oldtargetDetail = targetDetail.getBefImage() ;

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = targetDetail.getTargetCode();
		errParm[0] = PennantJavaUtil.getLabel("label_TargetCode") + ":"+ valueParm[0];

		if (targetDetail.isNew()) { // for New record or new record into work flow

			if (!targetDetail.isWorkflow()) {// With out Work flow only new records
				if (beftargetDetail != null) { // Record Already Exists in the table then error
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001",errParm, null));
				}
			} else { // with work flow
				if (targetDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new 
					if (beftargetDetail != null || tempTargetDetail != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,null));
					}
				} else { // if records not exists in the Main flow table
					if (beftargetDetail == null || tempTargetDetail != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!targetDetail.isWorkflow()) { // With out Work flow for update and delete
				if (beftargetDetail == null) { // if records not exists in the main table
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002",errParm, null));
				} else {
					if (oldtargetDetail != null
							&& !oldtargetDetail.getLastMntOn().equals(beftargetDetail.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003",errParm, null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004",errParm, null));
						}
					}
				}
			} else {
				if (tempTargetDetail == null) { // if records not exists in the Work flow table
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005",errParm, null));
				}

				if (tempTargetDetail != null
						&& oldtargetDetail != null
						&& !oldtargetDetail.getLastMntOn().equals(tempTargetDetail.getLastMntOn())) {
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005",errParm, null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if ("doApprove".equals(StringUtils.trimToEmpty(method))
				|| !targetDetail.isWorkflow()) {
			auditDetail.setBefImage(beftargetDetail);
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

	public TargetDetailDAO getTargetDetailDAO() {
		return targetDetailDAO;
	}

	public void setTargetDetailDAO(TargetDetailDAO targetDetailDAO) {
		this.targetDetailDAO = targetDetailDAO;
	}

}
