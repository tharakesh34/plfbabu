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

package com.pennanttech.pennapps.pff.verification.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.collateral.ExtendedFieldRenderDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.collateral.impl.DocumentDetailValidation;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.model.dms.DMSModule;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pennapps.pff.service.hook.PostExteranalServiceHook;
import com.pennanttech.pennapps.pff.verification.Decision;
import com.pennanttech.pennapps.pff.verification.RequestType;
import com.pennanttech.pennapps.pff.verification.VerificationCategory;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.dao.TechnicalVerificationDAO;
import com.pennanttech.pennapps.pff.verification.dao.VerificationDAO;
import com.pennanttech.pennapps.pff.verification.fi.TVStatus;
import com.pennanttech.pennapps.pff.verification.model.TechnicalVerification;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pff.core.TableType;
import com.pennapps.core.util.ObjectUtil;

/**
 * Service implementation for methods that depends on <b>TechnicalVerification</b>.<br>
 */
public class TechnicalVerificationServiceImpl extends GenericService<TechnicalVerification>
		implements TechnicalVerificationService {
	private static final Logger logger = LogManager.getLogger(TechnicalVerificationServiceImpl.class);

	@Autowired
	private AuditHeaderDAO auditHeaderDAO;
	@Autowired
	private TechnicalVerificationDAO technicalVerificationDAO;
	@Autowired
	private VerificationDAO verificationDAO;
	@Autowired
	private ExtendedFieldDetailsService extendedFieldDetailsService;
	@Autowired
	private ExtendedFieldRenderDAO extendedFieldRenderDAO;
	@Autowired
	private DocumentDetailsDAO documentDetailsDAO;
	private DocumentDetailValidation documentValidation;
	private RuleService ruleService;
	@Autowired(required = false)
	@Qualifier("verificationPostExteranalServiceHook")
	private PostExteranalServiceHook postExteranalServiceHook;

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * verification_fi/verification_fi_Temp by using verification_fiDAO's save method b) Update the Record in the table.
	 * based on the module workFlow Configuration. by using verification_fiDAO's update method 3) Audit the record in to
	 * AuditHeader and Adtverification_fi by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		TechnicalVerification tv = (TechnicalVerification) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (tv.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		/*
		 * if (tv.getVerificationCategory() == VerificationCategory.ONEPAGER.getKey()) { getDocument(tv); }
		 */

		if (tv.isNewRecord()) {
			tv.setId(Long.parseLong(technicalVerificationDAO.save(tv, tableType)));
			auditHeader.getAuditDetail().setModelData(tv);
			auditHeader.setAuditReference(String.valueOf(tv.getId()));
		} else {
			technicalVerificationDAO.update(tv, tableType);
		}

		// Extended field Details
		if (tv.getExtendedFieldRender() != null) {
			List<AuditDetail> details = tv.getAuditDetailMap().get("ExtendedFieldDetails");
			StringBuilder tableName = new StringBuilder();
			tableName.append(CollateralConstants.VERIFICATION_MODULE);
			tableName.append("_");
			tableName.append(tv.getExtendedFieldHeader().getSubModuleName());
			tableName.append("_TV");
			details = extendedFieldDetailsService.processingExtendedFieldDetailList(details, tableName.toString(),
					tableType.getSuffix());
			auditDetails.addAll(details);
		}

		// TV documents
		if (tv.getDocuments() != null && !tv.getDocuments().isEmpty()) {
			List<AuditDetail> details = tv.getAuditDetailMap().get("DocumentDetails");
			details = saveOrUpdateDocuments(details, tv, tableType.getSuffix());
			auditDetails.addAll(details);
		}

		// One Pager Extended field Details
		if (tv.getOnePagerExtRender() != null) {
			List<AuditDetail> details = tv.getAuditDetailMap().get("OnePagerExtFieldDetails");
			StringBuilder tableName = new StringBuilder();
			tableName.append(CollateralConstants.VERIFICATION_MODULE);
			tableName.append("_");
			tableName.append(tv.getOnePagerExtHeader().getSubModuleName());
			tableName.append("_ED");
			details = extendedFieldDetailsService.processingExtendedFieldDetailList(details, tableName.toString(),
					tableType.getSuffix());
			auditDetails.addAll(details);
		}

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);
		// calling post hoot
		if (postExteranalServiceHook != null) {
			postExteranalServiceHook.doProcess(auditHeader, "saveOrUpdate");
		}
		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * verification_fi by using verification_fiDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and Adtverification_fi by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader aAuditHeader) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		aAuditHeader = businessValidation(aAuditHeader, "delete");
		if (!aAuditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return aAuditHeader;
		}

		AuditHeader auditHeader = ObjectUtil.clone(aAuditHeader);

		TechnicalVerification technicalVerification = (TechnicalVerification) auditHeader.getAuditDetail()
				.getModelData();
		auditDetails.addAll(
				deleteChilds(technicalVerification, TableType.MAIN_TAB.getSuffix(), auditHeader.getAuditTranType()));

		technicalVerificationDAO.delete(technicalVerification, TableType.MAIN_TAB);

		String[] fields = PennantJavaUtil.getFieldDetails(new TechnicalVerification(),
				technicalVerification.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				technicalVerification.getBefImage(), technicalVerification));
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	// Method for Deleting all records related to Customer in _Temp/Main tables
	// depend on method type
	public List<AuditDetail> deleteChilds(TechnicalVerification tv, String tableType, String auditTranType) {
		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		// Extended field Render Details.
		List<AuditDetail> extendedDetails = tv.getAuditDetailMap().get("ExtendedFieldDetails");
		if (extendedDetails != null && extendedDetails.size() > 0) {
			// Table Name
			StringBuilder tableName = new StringBuilder();
			tableName.append(CollateralConstants.VERIFICATION_MODULE);
			tableName.append("_");
			tableName.append(tv.getExtendedFieldHeader().getSubModuleName());
			tableName.append("_TV");
			auditList.addAll(extendedFieldDetailsService.delete(tv.getExtendedFieldHeader(),
					String.valueOf(tv.getVerificationId()), tableName.toString(), tableType, auditTranType,
					extendedDetails));
		}

		// Document Details.
		List<AuditDetail> documentDetails = tv.getAuditDetailMap().get("DocumentDetails");
		if (documentDetails != null && documentDetails.size() > 0) {
			DocumentDetails document = new DocumentDetails();
			List<DocumentDetails> documents = new ArrayList<>();
			String[] fields = PennantJavaUtil.getFieldDetails(document, document.getExcludeFields());
			for (int i = 0; i < documentDetails.size(); i++) {
				document = (DocumentDetails) documentDetails.get(i).getModelData();
				document.setRecordType(PennantConstants.RECORD_TYPE_CAN);
				documents.add(document);
				auditList.add(
						new AuditDetail(auditTranType, i + 1, fields[0], fields[1], document.getBefImage(), document));
			}
			documentDetailsDAO.deleteList(documents, tableType);
		}

		// One Pager Extended field Render Details.
		List<AuditDetail> onePagerExtDetails = tv.getAuditDetailMap().get("OnePagerExtFieldDetails");
		if (onePagerExtDetails != null && onePagerExtDetails.size() > 0) {
			// Table Name
			StringBuilder tableName = new StringBuilder();
			tableName.append(CollateralConstants.VERIFICATION_MODULE);
			tableName.append("_");
			tableName.append(tv.getOnePagerExtHeader().getSubModuleName());
			tableName.append("_ed");
			auditList.addAll(extendedFieldDetailsService.delete(tv.getOnePagerExtHeader(),
					String.valueOf(tv.getVerificationId()), tableName.toString(), tableType, auditTranType,
					onePagerExtDetails));
		}

		return auditList;
	}

	/**
	 * getverification_fi fetch the details by using verification_fiDAO's getverification_fiById method.
	 * 
	 * @param id id of the TechnicalVerification.
	 * @return verification_fi
	 */
	@Override
	public TechnicalVerification getTechnicalVerification(long id, String type) {
		TechnicalVerification technicalVerification = technicalVerificationDAO.getTechnicalVerification(id, type);
		if (technicalVerification != null) {
			// FI Document Details
			List<DocumentDetails> documentList = documentDetailsDAO.getDocumentDetailsByRef(String.valueOf(id),
					VerificationType.TV.getCode(), "", "_View");
			if (technicalVerification.getDocuments() != null && !technicalVerification.getDocuments().isEmpty()) {
				technicalVerification.getDocuments().addAll(documentList);
			} else {
				technicalVerification.setDocuments(documentList);
			}
		}
		return technicalVerification;
	}

	/**
	 * getApprovedverification_fiById fetch the details by using verification_fiDAO's getverification_fiById method .
	 * with parameter id and type as blank. it fetches the approved records from the verification_fi.
	 * 
	 * @param id id of the TechnicalVerification. (String)
	 * @return verification_fi
	 */
	@Override
	public TechnicalVerification getApprovedTechnicalVerification(long id) {
		return technicalVerificationDAO.getTechnicalVerification(id, "__AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using technicalVerificationDAO.delete with
	 * parameters technicalVerification,"" b) NEW Add new record in to main table by using technicalVerificationDAO.save
	 * with parameters technicalVerification,"" c) EDIT Update record in the main table by using
	 * technicalVerificationDAO.update with parameters technicalVerification,"" 3) Delete the record from the workFlow
	 * table by using technicalVerificationDAO.delete with parameters technicalVerification,"_Temp" 4) Audit the record
	 * in to AuditHeader and Adtverification_fi by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the
	 * record in to AuditHeader and Adtverification_fi by using auditHeaderDAO.addAudit(auditHeader) based on the
	 * transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader aAuditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		aAuditHeader = businessValidation(aAuditHeader, "doApprove");

		if (!aAuditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return aAuditHeader;
		}

		AuditHeader auditHeader = ObjectUtil.clone(aAuditHeader);

		TechnicalVerification tv = new TechnicalVerification();
		BeanUtils.copyProperties(auditHeader.getAuditDetail().getModelData(), tv);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(tv.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(
					technicalVerificationDAO.getTechnicalVerification(tv.getId(), TableType.MAIN_TAB.getSuffix()));
		}

		if (tv.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			auditDetails.addAll(deleteChilds(tv, TableType.MAIN_TAB.getSuffix(), tranType));
			technicalVerificationDAO.delete(tv, TableType.MAIN_TAB);
		} else {
			tv.setRoleCode("");
			tv.setNextRoleCode("");
			tv.setTaskId("");
			tv.setNextTaskId("");
			tv.setWorkflowId(0);

			/*
			 * if (tv.getVerificationCategory() == VerificationCategory.ONEPAGER.getKey()) { getDocument(tv); }
			 */
			if (tv.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				tv.setRecordType("");
				technicalVerificationDAO.save(tv, TableType.MAIN_TAB);
				verificationDAO.updateVerifiaction(tv.getId(), tv.getVerifiedDate(), tv.getStatus());
			} else {
				tranType = PennantConstants.TRAN_UPD;
				tv.setRecordType("");
				technicalVerificationDAO.update(tv, TableType.MAIN_TAB);
				verificationDAO.updateVerifiaction(tv.getId(), tv.getVerifiedDate(), tv.getStatus());
			}

			// Extended field Details
			if (tv.getExtendedFieldRender() != null) {
				List<AuditDetail> details = tv.getAuditDetailMap().get("ExtendedFieldDetails");

				// Table Name
				StringBuilder tableName = new StringBuilder();
				tableName.append(CollateralConstants.VERIFICATION_MODULE);
				tableName.append("_");
				tableName.append(tv.getExtendedFieldHeader().getSubModuleName());
				tableName.append("_TV");

				details = extendedFieldDetailsService.processingExtendedFieldDetailList(details, tableName.toString(),
						TableType.MAIN_TAB.getSuffix());
				auditDetails.addAll(details);
			}

			// FI Document Details
			List<DocumentDetails> documentsList = tv.getDocuments();
			if (documentsList != null && documentsList.size() > 0) {
				List<AuditDetail> details = tv.getAuditDetailMap().get("DocumentDetails");
				details = saveOrUpdateDocuments(details, tv, "");
				auditDetails.addAll(details);
			}

			// One Pager Extended field Details
			if (tv.getOnePagerExtRender() != null) {
				List<AuditDetail> details = tv.getAuditDetailMap().get("OnePagerExtFieldDetails");

				// Table Name
				StringBuilder tableName = new StringBuilder();
				tableName.append(CollateralConstants.VERIFICATION_MODULE);
				tableName.append("_");
				tableName.append(tv.getOnePagerExtHeader().getSubModuleName());
				tableName.append("_ed");

				details = extendedFieldDetailsService.processingExtendedFieldDetailList(details, tableName.toString(),
						TableType.MAIN_TAB.getSuffix());
				auditDetails.addAll(details);
			}

		}
		List<AuditDetail> auditDetailList = new ArrayList<AuditDetail>();

		auditDetailList.addAll(deleteChilds(tv, TableType.TEMP_TAB.getSuffix(), auditHeader.getAuditTranType()));
		technicalVerificationDAO.delete(tv, TableType.TEMP_TAB);
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		String[] fields = PennantJavaUtil.getFieldDetails(new TechnicalVerification(), tv.getExcludeFields());
		auditHeader.setAuditDetail(
				new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1], tv.getBefImage(), tv));
		auditHeader.setAuditDetails(auditDetailList);
		auditHeaderDAO.addAudit(auditHeader);
		// calling post hoot
		if (postExteranalServiceHook != null) {
			postExteranalServiceHook.doProcess(aAuditHeader, "doApprove");
		}

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using technicalVerificationDAO.delete with parameters technicalVerification,"_Temp" 3) Audit
	 * the record in to AuditHeader and Adtverification_fi by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader (auditHeader)
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
		List<AuditDetail> auditDetails = new ArrayList<>();
		TechnicalVerification tv = (TechnicalVerification) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditDetails.addAll(deleteChilds(tv, "_Temp", auditHeader.getAuditTranType()));
		technicalVerificationDAO.delete(tv, TableType.TEMP_TAB);

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);

		TechnicalVerification tv = (TechnicalVerification) auditHeader.getAuditDetail().getModelData();
		String usrLanguage = tv.getUserDetails().getLanguage();

		// Extended field details Validation
		if (tv.getExtendedFieldRender() != null) {
			List<AuditDetail> details = tv.getAuditDetailMap().get("ExtendedFieldDetails");
			ExtendedFieldHeader extHeader = tv.getExtendedFieldHeader();

			StringBuilder tableName = new StringBuilder();
			tableName.append(CollateralConstants.VERIFICATION_MODULE);
			tableName.append("_");
			tableName.append(extHeader.getSubModuleName());
			tableName.append("_TV");

			details = extendedFieldDetailsService.vaildateDetails(details, method, usrLanguage, tableName.toString());
			auditDetails.addAll(details);
		}

		// TV Document details Validation
		List<DocumentDetails> docuemnts = tv.getDocuments();
		if (docuemnts != null && !docuemnts.isEmpty()) {
			List<AuditDetail> details = tv.getAuditDetailMap().get("DocumentDetails");
			details = getDocumentValidation().vaildateDetails(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		// One Pager Extended field details Validation
		if (tv.getOnePagerExtRender() != null) {
			List<AuditDetail> details = tv.getAuditDetailMap().get("OnePagerExtFieldDetails");
			ExtendedFieldHeader extHeader = tv.getOnePagerExtHeader();

			StringBuilder tableName = new StringBuilder();
			tableName.append(CollateralConstants.VERIFICATION_MODULE);
			tableName.append("_");
			tableName.append(extHeader.getSubModuleName());
			tableName.append("_ed");

			details = extendedFieldDetailsService.vaildateDetails(details, method, usrLanguage, tableName.toString());
			auditDetails.addAll(details);
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}
		auditHeader = nextProcess(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		TechnicalVerification tv = (TechnicalVerification) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (tv.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		// Extended Field Details
		if (tv.getExtendedFieldRender() != null) {
			auditDetailMap.put("ExtendedFieldDetails", extendedFieldDetailsService
					.setExtendedFieldsAuditData(tv.getExtendedFieldRender(), auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("ExtendedFieldDetails"));
		}

		// TV Document Details
		if (tv.getDocuments() != null && tv.getDocuments().size() > 0) {
			auditDetailMap.put("DocumentDetails", setDocumentDetailsAuditData(tv, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("DocumentDetails"));
		}

		// One Pager Detail Extended Field Details
		if (tv.getOnePagerExtRender() != null) {
			auditDetailMap.put("OnePagerExtFieldDetails", extendedFieldDetailsService
					.setExtendedFieldsAuditData(tv.getOnePagerExtRender(), auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("OnePagerExtFieldDetails"));
		}

		tv.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(tv);
		auditHeader.setAuditDetails(auditDetails);

		return auditHeader;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param detail
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	public List<AuditDetail> setDocumentDetailsAuditData(TechnicalVerification fieldInvestigation, String auditTranType,
			String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<>();

		DocumentDetails document = new DocumentDetails();
		String[] fields = PennantJavaUtil.getFieldDetails(document, document.getExcludeFields());

		for (int i = 0; i < fieldInvestigation.getDocuments().size(); i++) {
			DocumentDetails documentDetails = fieldInvestigation.getDocuments().get(i);

			if (StringUtils.isEmpty(StringUtils.trimToEmpty(documentDetails.getRecordType()))) {
				continue;
			}

			documentDetails.setWorkflowId(fieldInvestigation.getWorkflowId());
			boolean isRcdType = false;

			if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (fieldInvestigation.isWorkflow()) {
					isRcdType = true;
				}
			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				documentDetails.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			documentDetails.setRecordStatus(fieldInvestigation.getRecordStatus());
			documentDetails.setUserDetails(fieldInvestigation.getUserDetails());
			documentDetails.setLastMntOn(fieldInvestigation.getLastMntOn());
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], documentDetails.getBefImage(),
					documentDetails));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public void save(TechnicalVerification technicalVerification, TableType tempTab) {
		setWorkFlowDetails(technicalVerification);
		technicalVerificationDAO.save(technicalVerification, tempTab);
		technicalVerificationDAO.saveCollateral(technicalVerification.getCollateralRef(),
				technicalVerification.getCollateralType(), technicalVerification.getVerificationId());
	}

	@Override
	public List<Long> getTechnicalVerificaationIds(List<Verification> verifications, String keyRef) {
		List<Long> tvIds = new ArrayList<>();
		List<TechnicalVerification> tvList = technicalVerificationDAO.getList(keyRef);
		for (TechnicalVerification technicalVerification : tvList) {
			for (Verification Verification : verifications) {
				if (technicalVerification.getVerificationId() == Verification.getId()) {
					tvIds.add(Verification.getId());
				}
			}

		}
		return tvIds;
	}

	@Override
	public List<TechnicalVerification> getList(String keyReference) {

		return technicalVerificationDAO.getList(keyReference);
	}

	@Override
	public boolean isCollateralChanged(Verification verification, TableType tableType) {
		List<Map<String, Object>> previous = null;
		List<Map<String, Object>> current = null;
		String tableName = "collateral_" + verification.getReferenceType();

		try {
			previous = extendedFieldRenderDAO.getExtendedFieldMapByVerificationId(verification.getId(),
					tableName + "_ed_tv");
			current = extendedFieldRenderDAO.getExtendedFieldMap(verification.getReferenceFor(),
					tableName + "_ed" + tableType.getSuffix(), null);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		if (previous == null || current == null || previous.isEmpty()) {
			return false;
		}

		String prvRecrdStatus = "";
		if (CollectionUtils.isNotEmpty(previous)) {
			prvRecrdStatus = (String) previous.get(0).get("RECORDSTATUS");
		}
		if (previous.size() != current.size() && !StringUtils.equals("Approved", prvRecrdStatus)) {
			return true;
		}

		for (Map<String, Object> prv : previous) {
			for (Map<String, Object> item : current) {
				if (prv.get("reference").equals(item.get("reference")) && prv.get("seqno").equals(item.get("seqno"))
						&& !prv.get("version").equals(item.get("version"))) {
					return true;
				}
			}
		}
		return false;
	}

	private void setTvFields(Verification verification) {
		TechnicalVerification tv = new TechnicalVerification();
		tv.setVerificationId(verification.getId());
		tv.setCollateralRef(verification.getReferenceFor());
		tv.setCollateralType(verification.getReferenceType());
		tv.setType(verification.getRequestType());
		tv.setVersion(1);
		tv.setLastMntBy(verification.getLastMntBy());
		tv.setLastMntOn(verification.getLastMntOn());
		setWorkFlowDetails(tv);
		verification.setTechnicalVerification(tv);
	}

	private void setWorkFlowDetails(TechnicalVerification tv) {
		String workFlowType = ModuleUtil.getWorkflowType("TechnicalVerification");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getDetailsByType(workFlowType);
		WorkflowEngine engine = new WorkflowEngine(
				WorkFlowUtil.getWorkflow(workFlowDetails.getWorkFlowId()).getWorkFlowXml());

		tv.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
		tv.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		tv.setWorkflowId(workFlowDetails.getWorkflowId());
		tv.setRoleCode(workFlowDetails.getFirstTaskOwner());
		tv.setNextRoleCode(workFlowDetails.getFirstTaskOwner());
		tv.setTaskId(engine.getUserTaskId(tv.getRoleCode()));
		tv.setNextTaskId(engine.getUserTaskId(tv.getNextRoleCode()) + ";");
	}

	@Override
	public void save(Verification item) {
		if ((item.getRequestType() == RequestType.INITIATE.getKey()
				|| item.getDecision() == Decision.RE_INITIATE.getKey())) {
			setTvFields(item);
			save(item.getTechnicalVerification(), TableType.TEMP_TAB);
		}
	}

	@Override
	public void saveCollateral(String reference, String collateralType, long verificationId) {
		technicalVerificationDAO.saveCollateral(reference, collateralType, verificationId);
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from technicalVerificationDAO.getErrorDetail with Error ID and language as parameters. if any error/Warnings then
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

	/**
	 * Method For Preparing List of AuditDetails for Document Details
	 * 
	 * @param auditDetails
	 * @param tv
	 * @param type
	 * @return
	 */
	private List<AuditDetail> saveOrUpdateDocuments(List<AuditDetail> auditDetails, TechnicalVerification tv,
			String type) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			DocumentDetails documentDetails = (DocumentDetails) auditDetails.get(i).getModelData();

			if (StringUtils.isBlank(documentDetails.getRecordType())) {
				continue;
			}

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			boolean isTempRecord = false;
			if (StringUtils.isEmpty(type) || type.equals(PennantConstants.PREAPPROVAL_TABLE_TYPE)) {
				approveRec = true;
				documentDetails.setRoleCode("");
				documentDetails.setNextRoleCode("");
				documentDetails.setTaskId("");
				documentDetails.setNextTaskId("");
			}
			documentDetails.setLastMntBy(tv.getLastMntBy());
			documentDetails.setWorkflowId(0);

			if (DocumentCategories.CUSTOMER.getKey().equals(documentDetails.getCategoryCode())) {
				approveRec = true;
			}

			if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
				isTempRecord = true;
			} else if (documentDetails.isNewRecord()) {
				saveRecord = true;
				if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					documentDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					documentDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					documentDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (documentDetails.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = documentDetails.getRecordType();
				recordStatus = documentDetails.getRecordStatus();
				documentDetails.setRecordType("");
				documentDetails.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			if (saveRecord) {
				if (StringUtils.isEmpty(documentDetails.getReferenceId())) {
					documentDetails.setReferenceId(String.valueOf(tv.getVerificationId()));
				}
				saveDocument(DMSModule.FINANCE, DMSModule.TV, documentDetails);
				documentDetailsDAO.save(documentDetails, type);
			}

			if (updateRecord) {
				saveDocument(DMSModule.FINANCE, DMSModule.TV, documentDetails);
				documentDetailsDAO.update(documentDetails, type);
			}

			if (deleteRecord && ((StringUtils.isEmpty(type) && !isTempRecord) || (StringUtils.isNotEmpty(type)))) {
				if (!type.equals(PennantConstants.PREAPPROVAL_TABLE_TYPE)) {
					documentDetailsDAO.delete(documentDetails, type);
				}
			}

			if (approveRec) {
				documentDetails.setFinEvent("");
				documentDetails.setRecordType(rcdType);
				documentDetails.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(documentDetails);

		}
		logger.debug("Leaving");
		return auditDetails;
	}

	public DocumentDetailValidation getDocumentValidation() {
		if (documentValidation == null) {
			this.documentValidation = new DocumentDetailValidation(documentDetailsDAO);
		}
		return documentValidation;
	}

	private void getDocument(TechnicalVerification tv) {
		DocumentDetails dd = new DocumentDetails();
		dd.setFinReference(tv.getKeyReference());
		dd.setDocName(tv.getDocumentName());
		if (tv.getDocumentRef() != 0) {
			byte[] olddocumentManager = getDocumentImage(tv.getDocumentRef());
			if (olddocumentManager != null) {
				byte[] arr1 = olddocumentManager;
				byte[] arr2 = tv.getDocImage();
				if (!Arrays.equals(arr1, arr2)) {
					dd.setDocImage(tv.getDocImage());
					saveDocument(DMSModule.FINANCE, DMSModule.TV, dd);
					tv.setDocumentRef(dd.getDocRefId());
				}
			}
		} else {
			dd.setDocImage(tv.getDocImage());
			saveDocument(DMSModule.FINANCE, DMSModule.TV, dd);
			tv.setDocumentRef(dd.getDocRefId());
		}
	}

	@Override
	public void getDocumentImage(TechnicalVerification tv) {
		byte[] data = getDocumentImage(tv.getDocumentRef());
		if (data != null) {
			tv.setDocImage(data);
		}

	}

	@Override
	public TechnicalVerification getVerificationFromRecording(long verificationId) {
		return technicalVerificationDAO.getTechnicalVerification(verificationId, "_View");
	}

	@Override
	public Map<String, Object> getCostOfPropertyValue(String collRef, String subModuleName, String column) {
		return technicalVerificationDAO.getCostOfPropertyValue(collRef, subModuleName, column);
	}

	@Override
	public String getPropertyCity(String collRef, String subModuleName) {
		return technicalVerificationDAO.getPropertyCity(collRef, subModuleName);
	}

	@Override
	public String getCollaterlType(long id) {
		return technicalVerificationDAO.getCollaterlType(id);
	}

	@Override
	// Validate Technical verification recoding count based on fin asset value
	// using rule.
	public AuditDetail validateTVCount(FinanceDetail financeDetail) {

		AuditDetail auditDetail = new AuditDetail();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String finReference = financeMain.getFinReference();
		if (CollectionUtils.isNotEmpty(financeDetail.getCollateralAssignmentList())) {

			Rule tvCountRule = ruleService.getApprovedRuleById("TVCOUNT", RuleConstants.MODULE_ELGRULE,
					RuleConstants.EVENT_ELGRULE);

			String sqlRule = "";

			if (tvCountRule != null) {
				sqlRule = tvCountRule.getSQLRule();
			}

			for (CollateralAssignment collAssignment : financeDetail.getCollateralAssignmentList()) {
				List<Verification> verifications = verificationDAO.getVerificationCount(finReference,
						collAssignment.getCollateralRef(), VerificationType.TV.getKey(), TVStatus.POSITIVE.getKey());

				int internalTvCount = 0;
				int onePagerTvCount = 0;
				int externalTvCount = 0;
				boolean isValidTvCount = false;

				List<Long> externalAgencies = new ArrayList<>();

				if (CollectionUtils.isNotEmpty(verifications)) {
					for (Verification verification : verifications) {
						if (verification.getVerificationCategory() == VerificationCategory.EXTERNAL.getKey()
								&& !externalAgencies.contains(verification.getAgency())) {
							externalAgencies.add(verification.getAgency());
							externalTvCount = externalTvCount + 1;
						} else if (verification.getVerificationCategory() == VerificationCategory.INTERNAL.getKey()) {
							internalTvCount = internalTvCount + 1;
						} else if (verification.getVerificationCategory() == VerificationCategory.ONEPAGER.getKey()) {
							onePagerTvCount = onePagerTvCount + 1;
						}
					}
				}

				if (StringUtils.isNotEmpty(sqlRule)) {

					Map<String, Object> fieldsAndValues = new HashMap<>();
					fieldsAndValues.put("roleCode", financeMain.getRoleCode());
					fieldsAndValues.put("internalTvCount", internalTvCount);
					fieldsAndValues.put("onePagerTvCount", onePagerTvCount);
					fieldsAndValues.put("externalTvCount", externalTvCount);
					fieldsAndValues.put("currentAssetValue", PennantApplicationUtil
							.formateAmount(financeMain.getFinAssetValue(), PennantConstants.defaultCCYDecPos));
					isValidTvCount = (boolean) RuleExecutionUtil.executeRule(sqlRule, fieldsAndValues,
							financeDetail.getFinScheduleData().getFinanceMain().getFinCcy(), RuleReturnType.BOOLEAN);
					if (!isValidTvCount) {
						auditDetail.setErrorDetail(ErrorUtil
								.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "30563", null, null), ""));
						break;
					}
				}
			}
		}
		return auditDetail;
	}

	@Autowired
	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}

	@Override
	public List<Verification> getTvValuation(List<Long> verificationIDs, String type) {
		return technicalVerificationDAO.getTvValuation(verificationIDs, type);
	}

	public void setPostExteranalServiceHook(PostExteranalServiceHook postExteranalServiceHook) {
		this.postExteranalServiceHook = postExteranalServiceHook;
	}

}