/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : QueryDetailServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 09-05-2018 * *
 * Modified Date : 09-05-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 09-05-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.loanquery.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.loanquery.QueryDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.loanquery.QueryDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.loanquery.QueryDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.model.dms.DMSModule;
import com.pennanttech.pennapps.core.engine.workflow.model.ServiceTask;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.dms.service.DMSService;
import com.pennanttech.pennapps.pff.service.hook.PostExteranalServiceHook;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>QueryDetail</b>.<br>
 */
public class QueryDetailServiceImpl extends GenericService<QueryDetail> implements QueryDetailService {
	private static final Logger logger = LogManager.getLogger(QueryDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private QueryDetailDAO queryDetailDAO;
	private DocumentDetailsDAO documentDetailsDAO;
	private PostExteranalServiceHook postExteranalServiceHook;

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		QueryDetail queryDetail = (QueryDetail) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (queryDetail.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (queryDetail.isNewRecord()) {
			queryDetail.setId(Long.parseLong(queryDetailDAO.save(queryDetail, tableType)));
			auditHeader.getAuditDetail().setModelData(queryDetail);
			auditHeader.setAuditReference(String.valueOf(queryDetail.getId()));
		} else {
			queryDetailDAO.update(queryDetail, tableType);
		}

		if (queryDetail.getDocumentDetailsList() != null && !queryDetail.getDocumentDetailsList().isEmpty()) {
			for (DocumentDetails documentDetails : queryDetail.getDocumentDetailsList()) {
				documentDetails.setReferenceId(String.valueOf(queryDetail.getId()));
				documentDetails.setFinReference(queryDetail.getFinReference());
				documentDetails.setUserDetails(queryDetail.getUserDetails());
				documentDetails.setCustId(queryDetail.getCustId());
				if (documentDetails.isNewRecord() && (documentDetails.getDocRefId() == null)) {

					saveDocument(DMSModule.FINANCE, DMSModule.QUERY_MGMT, documentDetails);
					documentDetailsDAO.save(documentDetails, tableType.getSuffix());
				}
			}
		}

		if (postExteranalServiceHook != null && SysParamUtil.isAllowed(SMTParameterConstants.QUERY_NOTIFICATION_REQ)) {
			postExteranalServiceHook.doProcess(auditHeader, "saveOrUpdate");
		}

		logger.debug(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		QueryDetail queryDetail = (QueryDetail) auditHeader.getAuditDetail().getModelData();
		queryDetailDAO.delete(queryDetail, TableType.MAIN_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public QueryDetail getQueryDetail(long id) {
		logger.debug(Literal.ENTERING);

		QueryDetail queryDetail = queryDetailDAO.getQueryDetail(id, "_View");

		if (queryDetail == null) {
			return queryDetail;
		}

		String referenceId = queryDetail.getFinReference();
		String docModule = FinanceConstants.QUERY_MANAGEMENT;

		queryDetail.setDocumentDetailsList(documentDetailsDAO.getDocumentDetailsByRef(referenceId, docModule, "_View"));

		logger.debug(Literal.LEAVING);
		return queryDetail;
	}

	@Override
	public QueryDetail getApprovedQueryDetail(long id) {
		return queryDetailDAO.getQueryDetail(id, "_AView");
	}

	@Override
	public AuditHeader getQueryMgmtList(AuditHeader auditHeader, ServiceTask task, String role) {
		AuditDetail auditDetail = auditHeader.getAuditDetail();
		FinanceDetail financeDetail = (FinanceDetail) auditDetail.getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		if (!"Save".equalsIgnoreCase(financeDetail.getUserAction())
				&& !"Cancel".equalsIgnoreCase(financeDetail.getUserAction())
				&& !financeDetail.getUserAction().contains("Reject")
				&& !financeDetail.getUserAction().contains("Resubmit")
				&& !financeDetail.getUserAction().contains("Decline")
				&& !financeDetail.getUserAction().contains("Hold")) {
			String[] errParm = new String[1];
			String[] valueParm = new String[1];
			valueParm[0] = financeMain.getFinReference();
			errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ": " + valueParm[0];

			List<QueryDetail> list = queryDetailDAO.getQueryMgmtList(financeMain.getFinReference(), "_AView");

			if (list != null && list.size() > 0) {
				for (QueryDetail queryDetail : list) {
					if (task.getParameters() != null) {
						if (StringUtils.equals(queryDetail.getRaisedUsrRole(), role)) {
							if (!StringUtils.equals(queryDetail.getStatus(),
									Labels.getLabel("label_QueryDetailDialog_Closed"))) {
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
										new ErrorDetail(PennantConstants.KEY_FIELD, "Q001", null, null), "EN"));
								auditHeader.setAuditDetail(auditDetail);
								auditHeader.setErrorList(auditDetail.getErrorDetails());
								break;
							}
						}
					} else {
						if (!StringUtils.equals(queryDetail.getStatus(),
								Labels.getLabel("label_QueryDetailDialog_Closed"))) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "Q001", null, null), "EN"));
							auditHeader.setAuditDetail(auditDetail);
							auditHeader.setErrorList(auditDetail.getErrorDetails());

							break;
						}
					}
				}

			}
		}
		return auditHeader;
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		QueryDetail queryDetail = new QueryDetail();
		BeanUtils.copyProperties((QueryDetail) auditHeader.getAuditDetail().getModelData(), queryDetail);

		queryDetailDAO.delete(queryDetail, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(queryDetail.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(queryDetailDAO.getQueryDetail(queryDetail.getId(), ""));
		}

		if (queryDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			queryDetailDAO.delete(queryDetail, TableType.MAIN_TAB);
		} else {
			queryDetail.setRoleCode("");
			queryDetail.setNextRoleCode("");
			queryDetail.setTaskId("");
			queryDetail.setNextTaskId("");
			queryDetail.setWorkflowId(0);

			if (queryDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				queryDetail.setRecordType("");
				queryDetailDAO.save(queryDetail, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				queryDetail.setRecordType("");
				queryDetailDAO.update(queryDetail, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(queryDetail);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		QueryDetail queryDetail = (QueryDetail) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		queryDetailDAO.delete(queryDetail, TableType.TEMP_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
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

	@Override
	public List<QueryDetail> getQueryDetailsforAgreements(String reference) {
		logger.debug(Literal.ENTERING);
		List<QueryDetail> list = queryDetailDAO.getQueryMgmtListForAgreements(reference, "_AView");
		logger.debug(Literal.LEAVING);
		return list;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Write the required validation over hear.

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@Override
	public AuditHeader queryModuleUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		QueryDetail queryDetail = (QueryDetail) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;

		queryDetailDAO.update(queryDetail, tableType);

		// Documents
		if (queryDetail.getDocumentDetailsList() != null && !queryDetail.getDocumentDetailsList().isEmpty()) {
			for (DocumentDetails documentDetails : queryDetail.getDocumentDetailsList()) {
				documentDetails.setReferenceId(String.valueOf(queryDetail.getId()));
				documentDetails.setCustId(queryDetail.getCustId());
				if (documentDetails.isNewRecord() && documentDetails.getDocRefId() == null) {
					saveDocument(DMSModule.FINANCE, DMSModule.QUERY_MGMT, documentDetails);

					documentDetailsDAO.save(documentDetails, tableType.getSuffix());
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public List<QueryDetail> getUnClosedQurysForGivenRole(String reference, String currentRole) {
		logger.debug(Literal.ENTERING);
		List<QueryDetail> list = queryDetailDAO.getUnClosedQurysForGivenRole(reference, currentRole);
		logger.debug(Literal.LEAVING);
		return list;
	}

	@Override
	public List<QueryDetail> getQueryListByReference(String reference) {
		return queryDetailDAO.getQueryListByReference(reference);
	}

	@Override
	public byte[] getdocImage(Long id) {
		return dMSService.getById(id);
	}

	@Autowired(required = false)
	@Qualifier("queryPostExteranalServiceHook")
	public void setPostExteranalServiceHook(PostExteranalServiceHook postExteranalServiceHook) {
		this.postExteranalServiceHook = postExteranalServiceHook;
	}

	@Autowired
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	@Autowired
	public void setQueryDetailDAO(QueryDetailDAO queryDetailDAO) {
		this.queryDetailDAO = queryDetailDAO;
	}

	@Autowired
	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	@Autowired
	public void setdMSService(DMSService dMSService) {
		this.dMSService = dMSService;
	}

}