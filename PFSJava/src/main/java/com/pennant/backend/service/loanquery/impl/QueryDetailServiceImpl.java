/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  QueryDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  09-05-2018    														*
 *                                                                  						*
 * Modified Date    :  09-05-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 09-05-2018       PENNANT	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/
package com.pennant.backend.service.loanquery.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
import com.pennant.backend.model.legal.LegalDetail;
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
import com.pennanttech.pennapps.pff.service.hook.PostExteranalServiceHook;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>QueryDetail</b>.<br>
 */
public class QueryDetailServiceImpl extends GenericService<QueryDetail> implements QueryDetailService {
	private static final Logger logger = Logger.getLogger(QueryDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private QueryDetailDAO queryDetailDAO;
	private DocumentDetailsDAO documentDetailsDAO;
	private PostExteranalServiceHook postExteranalServiceHook;

	/**
	 * @return the auditHeaderDAO
	 */
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	/**
	 * @param auditHeaderDAO
	 *            the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	/**
	 * @return the queryDetailDAO
	 */
	public QueryDetailDAO getQueryDetailDAO() {
		return queryDetailDAO;
	}

	/**
	 * @param queryDetailDAO
	 *            the queryDetailDAO to set
	 */
	public void setQueryDetailDAO(QueryDetailDAO queryDetailDAO) {
		this.queryDetailDAO = queryDetailDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table QUERYDETAIL/QUERYDETAIL_Temp by
	 * using QUERYDETAILDAO's save method b) Update the Record in the table. based on the module workFlow Configuration.
	 * by using QUERYDETAILDAO's update method 3) Audit the record in to AuditHeader and AdtQUERYDETAIL by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		QueryDetail queryDetail = (QueryDetail) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (queryDetail.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (queryDetail.isNew()) {
			queryDetail.setId(Long.parseLong(getQueryDetailDAO().save(queryDetail, tableType)));
			auditHeader.getAuditDetail().setModelData(queryDetail);
			auditHeader.setAuditReference(String.valueOf(queryDetail.getId()));
		} else {
			getQueryDetailDAO().update(queryDetail, tableType);
		}

		// Documents
		if (queryDetail.getDocumentDetailsList() != null && !queryDetail.getDocumentDetailsList().isEmpty()) {
			for (DocumentDetails documentDetails : queryDetail.getDocumentDetailsList()) {
				documentDetails.setReferenceId(String.valueOf(queryDetail.getId()));
				documentDetails.setFinReference(queryDetail.getFinReference());
				documentDetails.setUserDetails(queryDetail.getUserDetails());
				documentDetails.setCustId(queryDetail.getCustId());
				if (documentDetails.isNew()
						&& (documentDetails.getDocRefId() == null || documentDetails.getDocRefId() <= 0)) {

					saveDocument(DMSModule.FINANCE, DMSModule.QUERY_MGMT, documentDetails);
					documentDetailsDAO.save(documentDetails, tableType.getSuffix());
				}
			}
		}

		if (postExteranalServiceHook != null && SysParamUtil.isAllowed(SMTParameterConstants.QUERY_NOTIFICATION_REQ)) {
			postExteranalServiceHook.doProcess(auditHeader, "saveOrUpdate");
		}
		// getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * QUERYDETAIL by using QUERYDETAILDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtQUERYDETAIL by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		QueryDetail queryDetail = (QueryDetail) auditHeader.getAuditDetail().getModelData();
		getQueryDetailDAO().delete(queryDetail, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getQUERYDETAIL fetch the details by using QUERYDETAILDAO's getQUERYDETAILById method.
	 * 
	 * @param id
	 *            id of the QueryDetail.
	 * @return QUERYDETAIL
	 */
	@Override
	public QueryDetail getQueryDetail(long id) {
		// LV Document Details
		QueryDetail queryDetail = getQueryDetailDAO().getQueryDetail(id, "_View");
		if (queryDetail != null) {
			List<DocumentDetails> documentList = documentDetailsDAO.getDocumentDetailsByRef(
					String.valueOf(queryDetail.getId()), FinanceConstants.QUERY_MANAGEMENT, "_View");
			queryDetail.setDocumentDetailsList(documentList);
		}
		return queryDetail;
	}

	/**
	 * getApprovedQUERYDETAILById fetch the details by using QUERYDETAILDAO's getQUERYDETAILById method . with parameter
	 * id and type as blank. it fetches the approved records from the QUERYDETAIL.
	 * 
	 * @param id
	 *            id of the QueryDetail. (String)
	 * @return QUERYDETAIL
	 */
	@Override
	public QueryDetail getApprovedQueryDetail(long id) {
		return getQueryDetailDAO().getQueryDetail(id, "_AView");
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

			List<QueryDetail> list = getQueryDetailDAO().getQueryMgmtList(financeMain.getFinReference(), "_AView");

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

	/**
	 * This method validating the all quarry's raised by users resolved or not.
	 * 
	 * @param auditHeader
	 * @return
	 */
	@Override
	public AuditDetail validate(AuditDetail auditDetail) {

		LegalDetail legalDetail = (LegalDetail) auditDetail.getModelData();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = legalDetail.getLegalReference();
		errParm[0] = PennantJavaUtil.getLabel("label_LegalReference") + ": " + valueParm[0];

		List<QueryDetail> list = getQueryDetailDAO().getQueryMgmtListByRef(legalDetail.getLegalReference(), "_AView");

		if (CollectionUtils.isNotEmpty(list)) {
			for (QueryDetail queryDetail : list) {
				if (!StringUtils.equals(queryDetail.getStatus(), Labels.getLabel("label_QueryDetailDialog_Closed"))) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "QRYMGMT1", errParm, valueParm), "EN"));
				}
			}
		}
		return auditDetail;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getQueryDetailDAO().delete with
	 * parameters queryDetail,"" b) NEW Add new record in to main table by using getQueryDetailDAO().save with
	 * parameters queryDetail,"" c) EDIT Update record in the main table by using getQueryDetailDAO().update with
	 * parameters queryDetail,"" 3) Delete the record from the workFlow table by using getQueryDetailDAO().delete with
	 * parameters queryDetail,"_Temp" 4) Audit the record in to AuditHeader and AdtQUERYDETAIL by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtQUERYDETAIL by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		QueryDetail queryDetail = new QueryDetail();
		BeanUtils.copyProperties((QueryDetail) auditHeader.getAuditDetail().getModelData(), queryDetail);

		getQueryDetailDAO().delete(queryDetail, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(queryDetail.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(queryDetailDAO.getQueryDetail(queryDetail.getId(), ""));
		}

		if (queryDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getQueryDetailDAO().delete(queryDetail, TableType.MAIN_TAB);
		} else {
			queryDetail.setRoleCode("");
			queryDetail.setNextRoleCode("");
			queryDetail.setTaskId("");
			queryDetail.setNextTaskId("");
			queryDetail.setWorkflowId(0);

			if (queryDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				queryDetail.setRecordType("");
				getQueryDetailDAO().save(queryDetail, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				queryDetail.setRecordType("");
				getQueryDetailDAO().update(queryDetail, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(queryDetail);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getQueryDetailDAO().delete with parameters queryDetail,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtQUERYDETAIL by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		QueryDetail queryDetail = (QueryDetail) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getQueryDetailDAO().delete(queryDetail, TableType.TEMP_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * Method to get the QueryDetails based on the loan reference.
	 * 
	 * @return List<QueryDetail>
	 * 
	 */
	@Override
	public List<QueryDetail> getQueryDetailsforAgreements(String finReference) {
		logger.debug(Literal.ENTERING);
		List<QueryDetail> list = getQueryDetailDAO().getQueryMgmtListForAgreements(finReference, "_AView");
		logger.debug(Literal.LEAVING);
		return list;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getQueryDetailDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Write the required validation over hear.

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@Override
	public AuditHeader queryModuleUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		QueryDetail queryDetail = (QueryDetail) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;

		getQueryDetailDAO().update(queryDetail, tableType);

		// Documents
		if (queryDetail.getDocumentDetailsList() != null && !queryDetail.getDocumentDetailsList().isEmpty()) {
			for (DocumentDetails documentDetails : queryDetail.getDocumentDetailsList()) {
				documentDetails.setReferenceId(String.valueOf(queryDetail.getId()));
				documentDetails.setCustId(queryDetail.getCustId());
				if (documentDetails.isNew() && documentDetails.getDocRefId() <= 0) {
					saveDocument(DMSModule.FINANCE, DMSModule.QUERY_MGMT, documentDetails);

					documentDetailsDAO.save(documentDetails, tableType.getSuffix());
				}
			}
		}
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pennant.backend.service.loanquery.QueryDetailService# getUnClosedQurysForGivenRole(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public List<QueryDetail> getUnClosedQurysForGivenRole(String finReference, String currentRole) {
		logger.debug(Literal.ENTERING);
		List<QueryDetail> list = queryDetailDAO.getUnClosedQurysForGivenRole(finReference, currentRole);
		logger.debug(Literal.LEAVING);
		return list;
	}

	public DocumentDetailsDAO getDocumentDetailsDAO() {
		return documentDetailsDAO;
	}

	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	@Override
	public List<QueryDetail> getQueryListByReference(String reference) {
		return queryDetailDAO.getQueryListByReference(reference);
	}

	@Autowired(required = false)
	@Qualifier("queryPostExteranalServiceHook")
	public void setPostExteranalServiceHook(PostExteranalServiceHook postExteranalServiceHook) {
		this.postExteranalServiceHook = postExteranalServiceHook;
	}

	@Override
	public byte[] getdocImage(Long id) {
		return dMSService.getById(id);
	}

}