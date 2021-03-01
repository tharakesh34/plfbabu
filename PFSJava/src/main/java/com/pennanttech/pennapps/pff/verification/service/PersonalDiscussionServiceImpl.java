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

package com.pennanttech.pennapps.pff.verification.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.collateral.impl.DocumentDetailValidation;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.model.dms.DMSModule;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pennapps.pff.verification.Decision;
import com.pennanttech.pennapps.pff.verification.RequestType;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.dao.PersonalDiscussionDAO;
import com.pennanttech.pennapps.pff.verification.dao.VerificationDAO;
import com.pennanttech.pennapps.pff.verification.model.PersonalDiscussion;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for moethods that depends on <b>PersonalDiscussion</b>.<br>
 */
public class PersonalDiscussionServiceImpl extends GenericService<PersonalDiscussionService>
		implements PersonalDiscussionService {
	private static final Logger logger = LogManager.getLogger(PersonalDiscussionServiceImpl.class);

	@Autowired
	private AuditHeaderDAO auditHeaderDAO;
	@Autowired
	private PersonalDiscussionDAO personalDiscussionDAO;
	@Autowired
	private VerificationDAO verificationDAO;
	@Autowired
	private ExtendedFieldDetailsService extendedFieldDetailsService;
	@Autowired
	private DocumentDetailsDAO documentDetailsDAO;
	private DocumentDetailValidation documentValidation;

	public PersonalDiscussionServiceImpl() {
		super();
	}

	@Override
	public void save(PersonalDiscussion pd, TableType tempTab) {
		setWorkflowData(pd);
		personalDiscussionDAO.save(pd, tempTab);
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * verification_pd/verification_pd_Temp by using verification_pdDAO's save method b) Update the Record in the table.
	 * based on the module workFlow Configuration. by using verification_pdDAO's update method 3) Audit the record in to
	 * AuditHeader and Adtverification_pd by using auditHeaderDAO.addAudit(auditHeader)
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

		PersonalDiscussion pd = (PersonalDiscussion) auditHeader.getAuditDetail().getModelData();

		List<AuditDetail> auditDetails = new ArrayList<>();
		TableType tableType = TableType.MAIN_TAB;
		if (pd.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (pd.isNew()) {
			pd.setId(Long.parseLong(personalDiscussionDAO.save(pd, tableType)));
			auditHeader.getAuditDetail().setModelData(pd);
			auditHeader.setAuditReference(String.valueOf(pd.getId()));
		} else {
			personalDiscussionDAO.update(pd, tableType);
		}
		// Extended field Details
		if (pd.getExtendedFieldRender() != null) {
			List<AuditDetail> details = pd.getAuditDetailMap().get("ExtendedFieldDetails");
			StringBuilder tableName = new StringBuilder();
			tableName.append(CollateralConstants.VERIFICATION_MODULE);
			tableName.append("_");
			tableName.append(pd.getExtendedFieldHeader().getSubModuleName());
			tableName.append("_ED");
			details = extendedFieldDetailsService.processingExtendedFieldDetailList(details, tableName.toString(),
					tableType.getSuffix());
			auditDetails.addAll(details);
		}

		// PD documents
		if (pd.getDocuments() != null && !pd.getDocuments().isEmpty()) {
			List<AuditDetail> details = pd.getAuditDetailMap().get("DocumentDetails");
			details = saveOrUpdateDocuments(details, pd, tableType.getSuffix());
			auditDetails.addAll(details);
		}

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * Method For Preparing List of AuditDetails for Document Details
	 * 
	 * @param auditDetails
	 * @param fi
	 * @param type
	 * @return
	 */
	private List<AuditDetail> saveOrUpdateDocuments(List<AuditDetail> auditDetails, PersonalDiscussion pd,
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
			documentDetails.setLastMntBy(pd.getLastMntBy());
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
				} else if (documentDetails.isNew()) {
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

			documentDetails.setCustId(pd.getCustId());
			documentDetails.setFinReference(pd.getKeyReference());

			if (saveRecord) {
				if (StringUtils.isEmpty(documentDetails.getReferenceId())) {
					documentDetails.setReferenceId(String.valueOf(pd.getVerificationId()));
				}
				saveDocument(DMSModule.FINANCE, DMSModule.PD, documentDetails);
				documentDetailsDAO.save(documentDetails, type);
			}

			if (updateRecord) {
				saveDocument(DMSModule.FINANCE, DMSModule.PD, documentDetails);
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

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * verification_pd by using verification_pdDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and Adtverification_pd by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();
		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		PersonalDiscussion personalDiscussion = (PersonalDiscussion) auditHeader.getAuditDetail().getModelData();
		auditDetails.addAll(deleteChilds(personalDiscussion, "", auditHeader.getAuditTranType()));
		personalDiscussionDAO.delete(personalDiscussion, TableType.MAIN_TAB);

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getverification_pd fetch the details by using verification_pdDAO's getverification_pdById method.
	 * 
	 * @param id
	 *            id of the PersonalDiscussion.
	 * @return verification_pd
	 */
	@Override
	public PersonalDiscussion getPersonalDiscussion(long id, String type) {
		PersonalDiscussion personalDiscussion = personalDiscussionDAO.getPersonalDiscussion(id, type);
		if (personalDiscussion != null) {
			// FI Document Details
			List<DocumentDetails> documentList = documentDetailsDAO.getDocumentDetailsByRef(String.valueOf(id),
					VerificationType.PD.getCode(), "", "_View");
			if (personalDiscussion.getDocuments() != null && !personalDiscussion.getDocuments().isEmpty()) {
				personalDiscussion.getDocuments().addAll(documentList);
			} else {
				personalDiscussion.setDocuments(documentList);
			}
		}

		return personalDiscussion;
	}

	/**
	 * getApprovedverification_pdById fetch the details by using verification_pdDAO's getverification_pdById method .
	 * with parameter id and type as blank. it fetches the approved records from the verification_pd.
	 * 
	 * @param id
	 *            id of the PersonalDiscussion. (String)
	 * @return verification_pd
	 */
	@Override
	public PersonalDiscussion getApprovedPersonalDiscussion(long id) {
		return personalDiscussionDAO.getPersonalDiscussion(id, "");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using personalDiscussionDAO.delete with
	 * parameters personalDiscussion,"" b) NEW Add new record in to main table by using personalDiscussionDAO.save with
	 * parameters personalDiscussion,"" c) EDIT Update record in the main table by using personalDiscussionDAO.update
	 * with parameters personalDiscussion,"" 3) Delete the record from the workFlow table by using
	 * personalDiscussionDAO.delete with parameters personalDiscussion,"_Temp" 4) Audit the record in to AuditHeader and
	 * Adtverification_pd by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to
	 * AuditHeader and Adtverification_pd by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";

		List<AuditDetail> auditDetails = new ArrayList<>();
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		PersonalDiscussion pd = new PersonalDiscussion();
		BeanUtils.copyProperties(auditHeader.getAuditDetail().getModelData(), pd);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(pd.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(personalDiscussionDAO.getPersonalDiscussion(pd.getId(), ""));
		}

		if (pd.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			auditDetails.addAll(deleteChilds(pd, "", tranType));
			personalDiscussionDAO.delete(pd, TableType.MAIN_TAB);
		} else {
			pd.setRoleCode("");
			pd.setNextRoleCode("");
			pd.setTaskId("");
			pd.setNextTaskId("");
			pd.setWorkflowId(0);

			if (pd.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				pd.setRecordType("");
				personalDiscussionDAO.save(pd, TableType.MAIN_TAB);
				verificationDAO.updateVerifiaction(pd.getId(), pd.getVerifiedDate(), pd.getStatus());
			} else {
				tranType = PennantConstants.TRAN_UPD;
				pd.setRecordType("");
				personalDiscussionDAO.update(pd, TableType.MAIN_TAB);
				verificationDAO.updateVerifiaction(pd.getId(), pd.getVerifiedDate(), pd.getStatus());
			}

			// Extended field Details
			if (pd.getExtendedFieldRender() != null) {
				List<AuditDetail> details = pd.getAuditDetailMap().get("ExtendedFieldDetails");

				// Table Name
				StringBuilder tableName = new StringBuilder();
				tableName.append(CollateralConstants.VERIFICATION_MODULE);
				tableName.append("_");
				tableName.append(pd.getExtendedFieldHeader().getSubModuleName());
				tableName.append("_ed");

				details = extendedFieldDetailsService.processingExtendedFieldDetailList(details, tableName.toString(),
						TableType.MAIN_TAB.getSuffix());
				auditDetails.addAll(details);
			}

			// FI Document Details
			List<DocumentDetails> documentsList = pd.getDocuments();
			if (documentsList != null && !documentsList.isEmpty()) {
				List<AuditDetail> details = pd.getAuditDetailMap().get("DocumentDetails");
				details = saveOrUpdateDocuments(details, pd, "");
				auditDetails.addAll(details);
			}

		}

		List<AuditDetail> auditDetailList = new ArrayList<>();

		String[] fields = PennantJavaUtil.getFieldDetails(new PersonalDiscussion(), pd.getExcludeFields());
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		auditDetailList.addAll(deleteChilds(pd, "_Temp", auditHeader.getAuditTranType()));
		personalDiscussionDAO.delete(pd, TableType.TEMP_TAB);

		auditHeader.setAuditDetail(
				new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], pd.getBefImage(), pd));
		auditHeader.setAuditDetails(auditDetailList);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(pd);
		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	// Method for Deleting all records related to FI setup in _Temp/Main tables
	// depend on method type
	public List<AuditDetail> deleteChilds(PersonalDiscussion pd, String tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditList = new ArrayList<>();

		// Extended field Render Details.
		List<AuditDetail> extendedDetails = pd.getAuditDetailMap().get("ExtendedFieldDetails");
		if (extendedDetails != null && !extendedDetails.isEmpty()) {
			// Table Name
			StringBuilder tableName = new StringBuilder();
			tableName.append(CollateralConstants.VERIFICATION_MODULE);
			tableName.append("_");
			tableName.append(pd.getExtendedFieldHeader().getSubModuleName());
			tableName.append("_ED");
			auditList.addAll(extendedFieldDetailsService.delete(pd.getExtendedFieldHeader(),
					String.valueOf(pd.getVerificationId()), tableName.toString(), tableType, auditTranType,
					extendedDetails));
		}

		// Document Details.
		List<AuditDetail> documentDetails = pd.getAuditDetailMap().get("DocumentDetails");
		if (documentDetails != null && !documentDetails.isEmpty()) {
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

		logger.debug("Leaving");
		return auditList;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using personalDiscussionDAO.delete with parameters personalDiscussion,"_Temp" 3) Audit the
	 * record in to AuditHeader and Adtverification_pd by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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
		List<AuditDetail> auditDetails = new ArrayList<>();
		PersonalDiscussion personalDiscussion = (PersonalDiscussion) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		String[] fields = PennantJavaUtil.getFieldDetails(new PersonalDiscussion(),
				personalDiscussion.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				personalDiscussion.getBefImage(), personalDiscussion));

		auditDetails.addAll(deleteChilds(personalDiscussion, "_Temp", auditHeader.getAuditTranType()));
		personalDiscussionDAO.delete(personalDiscussion, TableType.TEMP_TAB);

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

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

		List<AuditDetail> auditDetails = new ArrayList<>();
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		getAuditDetails(auditHeader, method);
		PersonalDiscussion personalDiscussion = (PersonalDiscussion) auditDetail.getModelData();
		String usrLanguage = personalDiscussion.getUserDetails().getLanguage();

		// Extended field details Validation
		if (personalDiscussion.getExtendedFieldRender() != null) {
			List<AuditDetail> details = personalDiscussion.getAuditDetailMap().get("ExtendedFieldDetails");
			ExtendedFieldHeader extHeader = personalDiscussion.getExtendedFieldHeader();

			StringBuilder tableName = new StringBuilder();
			tableName.append(CollateralConstants.VERIFICATION_MODULE);
			tableName.append("_");
			tableName.append(extHeader.getSubModuleName());
			tableName.append("_ED");

			details = extendedFieldDetailsService.vaildateDetails(details, method, usrLanguage, tableName.toString());
			auditDetails.addAll(details);
		}

		// FI Document details Validation
		List<DocumentDetails> docuemnts = personalDiscussion.getDocuments();
		if (docuemnts != null && !docuemnts.isEmpty()) {
			List<AuditDetail> details = personalDiscussion.getAuditDetailMap().get("DocumentDetails");
			details = getDocumentValidation().vaildateDetails(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from personalDiscussionDAO.getErrorDetail with Error ID and language as parameters. if any error/Warnings then
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
	 * Common Method for Retrieving AuditDetails List
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<>();

		PersonalDiscussion personalDiscussion = (PersonalDiscussion) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (personalDiscussion.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		// Extended Field Details
		if (personalDiscussion.getExtendedFieldRender() != null) {
			auditDetailMap.put("ExtendedFieldDetails", extendedFieldDetailsService.setExtendedFieldsAuditData(
					personalDiscussion.getExtendedFieldRender(), auditTranType, method, null));
			auditDetails.addAll(auditDetailMap.get("ExtendedFieldDetails"));
		}

		// FI Document Details
		if (personalDiscussion.getDocuments() != null && personalDiscussion.getDocuments().size() > 0) {
			auditDetailMap.put("DocumentDetails",
					setDocumentDetailsAuditData(personalDiscussion, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("DocumentDetails"));
		}
		personalDiscussion.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(personalDiscussion);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug("Leaving");
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
	public List<AuditDetail> setDocumentDetailsAuditData(PersonalDiscussion personalDiscussion, String auditTranType,
			String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<>();

		DocumentDetails document = new DocumentDetails();
		String[] fields = PennantJavaUtil.getFieldDetails(document, document.getExcludeFields());

		for (int i = 0; i < personalDiscussion.getDocuments().size(); i++) {
			DocumentDetails documentDetails = personalDiscussion.getDocuments().get(i);

			if (StringUtils.isEmpty(StringUtils.trimToEmpty(documentDetails.getRecordType()))) {
				continue;
			}

			documentDetails.setWorkflowId(personalDiscussion.getWorkflowId());
			boolean isRcdType = false;

			if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (personalDiscussion.isWorkflow()) {
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

			documentDetails.setRecordStatus(personalDiscussion.getRecordStatus());
			documentDetails.setUserDetails(personalDiscussion.getUserDetails());
			documentDetails.setLastMntOn(personalDiscussion.getLastMntOn());
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], documentDetails.getBefImage(),
					documentDetails));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public List<Long> getPersonalDiscussionIds(List<Verification> verifications, String keyRef) {
		List<Long> fiIds = new ArrayList<>();
		List<PersonalDiscussion> fiList = personalDiscussionDAO.getList(keyRef);
		for (PersonalDiscussion personalDiscussion : fiList) {
			for (Verification verification : verifications) {
				if (personalDiscussion.getVerificationId() == verification.getId()) {
					fiIds.add(verification.getId());
				}
			}

		}
		return fiIds;
	}

	@Override
	public void save(CustomerDetails applicant, List<CustomerPhoneNumber> phoneNumbers, Verification item) {
		for (CustomerAddres address : applicant.getAddressList()) {
			if ((item.getRequestType() == RequestType.INITIATE.getKey()
					|| item.getDecision() == Decision.RE_INITIATE.getKey()) && (item.getCustId() == address.getCustID())
					&& item.getReferenceFor().equals(address.getCustAddrType())) {
				setPDFields(item, address, phoneNumbers);
				personalDiscussionDAO.save(item.getPersonalDiscussion(), TableType.TEMP_TAB);
				break;
			}
		}
	}

	private void setPDFields(Verification verification, CustomerAddres address,
			List<CustomerPhoneNumber> phoneNumbers) {

		PersonalDiscussion pd = new PersonalDiscussion();

		pd.setVerificationId(verification.getId());
		pd.setAddressType(address.getCustAddrType());
		pd.setName(verification.getCustomerName());
		pd.setHouseNumber(address.getCustAddrHNbr());
		pd.setFlatNumber(address.getCustFlatNbr());
		pd.setStreet(address.getCustAddrStreet());
		pd.setAddressLine1(address.getCustAddrLine1());
		pd.setAddressLine2(address.getCustAddrLine2());
		pd.setAddressLine3(address.getCustAddrLine3());
		pd.setAddressLine4(address.getCustAddrLine4());
		pd.setAddressLine5(null);
		pd.setCountry(address.getCustAddrCountry());
		pd.setProvince(address.getCustAddrProvince());
		pd.setCity(address.getCustAddrCity());
		pd.setVersion(1);
		pd.setLastMntBy(verification.getLastMntBy());
		pd.setLastMntOn(verification.getLastMntOn());
		setWorkflowData(pd);

		if (CollectionUtils.isNotEmpty(phoneNumbers)) {
			Collections.sort(phoneNumbers, new PhonePriority());
			pd.setContactNumber1((phoneNumbers.get(0)).getPhoneNumber());
			if (phoneNumbers.size() > 1) {
				pd.setContactNumber2((phoneNumbers.get(1)).getPhoneNumber());
			}
		}

		pd.setPoBox(address.getCustPOBox());
		pd.setZipCode(address.getCustAddrZIP());

		verification.setPersonalDiscussion(pd);
	}

	private void setWorkflowData(PersonalDiscussion fi) {
		String workFlowType = ModuleUtil.getWorkflowType("PersonalDiscussion");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getDetailsByType(workFlowType);
		WorkflowEngine engine = new WorkflowEngine(
				WorkFlowUtil.getWorkflow(workFlowDetails.getWorkFlowId()).getWorkFlowXml());

		fi.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
		fi.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		fi.setWorkflowId(workFlowDetails.getWorkflowId());
		fi.setRoleCode(workFlowDetails.getFirstTaskOwner());
		fi.setNextRoleCode(workFlowDetails.getFirstTaskOwner());
		fi.setTaskId(engine.getUserTaskId(fi.getRoleCode()));
		fi.setNextTaskId(engine.getUserTaskId(fi.getNextRoleCode()) + ";");
	}

	@Override
	public boolean isAddressChanged(Verification verification) {
		List<PersonalDiscussion> list = personalDiscussionDAO.getList(verification.getReference());

		PersonalDiscussion current = verification.getPersonalDiscussion();
		for (PersonalDiscussion previous : list) {
			if (previous.getCif().equals(verification.getCif())
					&& previous.getAddressType().equals(current.getAddressType())) {
				if (isAddressChange(previous, current)) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean isAddressChanged(long verificationId, CustomerAddres customerAddres) {
		PersonalDiscussion newAddress = null;
		PersonalDiscussion oldAddress;

		Verification verification = new Verification();
		setPDFields(verification, customerAddres, null);

		oldAddress = personalDiscussionDAO.getPersonalDiscussion(verificationId, "_view");

		if (oldAddress == null) {
			return false;
		} else {
			return isAddressChange(oldAddress, newAddress);
		}
	}

	@Override
	public boolean isAddressChange(CustomerAddres oldAddress, CustomerAddres newAddress) {

		if (oldAddress == null || newAddress == null) {
			return false;
		}

		if (!StringUtils.equals(oldAddress.getCustAddrHNbr(), newAddress.getCustAddrHNbr())) {
			return true;
		}
		if (!StringUtils.equals(oldAddress.getCustFlatNbr(), newAddress.getCustFlatNbr())) {
			return true;
		}
		if (!StringUtils.equals(oldAddress.getCustAddrStreet(), newAddress.getCustAddrStreet())) {
			return true;
		}
		if (!StringUtils.equals(oldAddress.getCustAddrLine1(), newAddress.getCustAddrLine1())) {
			return true;
		}
		if (!StringUtils.equals(oldAddress.getCustAddrLine2(), newAddress.getCustAddrLine2())) {
			return true;
		}
		if (!StringUtils.equals(oldAddress.getCustAddrLine3(), newAddress.getCustAddrLine3())) {
			return true;
		}
		if (!StringUtils.equals(oldAddress.getCustAddrLine4(), newAddress.getCustAddrLine4())) {
			return true;
		}
		if (!StringUtils.equals(oldAddress.getCustAddrCountry(), newAddress.getCustAddrCountry())) {
			return true;
		}
		if (!StringUtils.equals(oldAddress.getCustAddrProvince(), newAddress.getCustAddrProvince())) {
			return true;
		}
		if (!StringUtils.equals(oldAddress.getCustAddrCity(), newAddress.getCustAddrCity())) {
			return true;
		}
		if (!StringUtils.equals(oldAddress.getCustAddrZIP(), newAddress.getCustAddrZIP())) {
			return true;
		}
		if (!StringUtils.equals(oldAddress.getCustPOBox(), newAddress.getCustPOBox())) {
			return true;
		}
		return false;
	}

	private boolean isAddressChange(PersonalDiscussion oldAddress, PersonalDiscussion newAddress) {

		if (oldAddress == null || newAddress == null) {
			return false;
		}

		if (!StringUtils.equals(oldAddress.getHouseNumber(), newAddress.getHouseNumber())) {
			return true;
		}
		if (!StringUtils.equals(oldAddress.getFlatNumber(), newAddress.getFlatNumber())) {
			return true;
		}
		if (!StringUtils.equals(oldAddress.getStreet(), newAddress.getStreet())) {
			return true;
		}
		if (!StringUtils.equals(oldAddress.getAddressLine1(), newAddress.getAddressLine1())) {
			return true;
		}
		if (!StringUtils.equals(oldAddress.getAddressLine2(), newAddress.getAddressLine2())) {
			return true;
		}
		if (!StringUtils.equals(oldAddress.getAddressLine3(), newAddress.getAddressLine3())) {
			return true;
		}
		if (!StringUtils.equals(oldAddress.getAddressLine4(), newAddress.getAddressLine4())) {
			return true;
		}
		if (!StringUtils.equals(oldAddress.getCountry(), newAddress.getCountry())) {
			return true;
		}
		if (!StringUtils.equals(oldAddress.getProvince(), newAddress.getProvince())) {
			return true;
		}
		if (!StringUtils.equals(oldAddress.getCity(), newAddress.getCity())) {
			return true;
		}
		if (!StringUtils.equals(oldAddress.getZipCode(), newAddress.getZipCode())) {
			return true;
		}
		if (!StringUtils.equals(oldAddress.getPoBox(), newAddress.getPoBox())) {
			return true;
		}
		return false;
	}

	@Override
	public List<PersonalDiscussion> getList(String keyReference) {
		return personalDiscussionDAO.getList(keyReference);
	}

	public DocumentDetailValidation getDocumentValidation() {
		if (documentValidation == null) {
			this.documentValidation = new DocumentDetailValidation(documentDetailsDAO);
		}
		return documentValidation;
	}

	@Override
	public PersonalDiscussion getVerificationFromRecording(long verificationId) {
		return personalDiscussionDAO.getPersonalDiscussion(verificationId, "_view");
	}

	public class PhonePriority implements Comparator<CustomerPhoneNumber> {
		@Override
		public int compare(CustomerPhoneNumber o1, CustomerPhoneNumber o2) {
			return o2.getPhoneTypePriority() - o1.getPhoneTypePriority();
		}

	}
}