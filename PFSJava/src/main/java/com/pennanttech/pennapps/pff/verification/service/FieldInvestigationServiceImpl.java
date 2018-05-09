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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerDocumentDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennant.backend.dao.systemmasters.AddressTypeDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.collateral.impl.DocumentDetailValidation;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pennapps.pff.verification.Decision;
import com.pennanttech.pennapps.pff.verification.RequestType;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.dao.FieldInvestigationDAO;
import com.pennanttech.pennapps.pff.verification.dao.VerificationDAO;
import com.pennanttech.pennapps.pff.verification.model.FieldInvestigation;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>FieldInvestigation</b>.<br>
 */
public class FieldInvestigationServiceImpl extends GenericService<FieldInvestigation>
		implements FieldInvestigationService {
	private static final Logger logger = Logger.getLogger(FieldInvestigationServiceImpl.class);

	@Autowired
	private AuditHeaderDAO auditHeaderDAO;
	@Autowired
	private FieldInvestigationDAO fieldInvestigationDAO;
	@Autowired
	private VerificationDAO verificationDAO;
	@Autowired
	private AddressTypeDAO addressTypeDAO;
	@Autowired
	private ExtendedFieldDetailsService extendedFieldDetailsService;
	@Autowired
	private DocumentDetailsDAO documentDetailsDAO;
	@Autowired
	private DocumentManagerDAO documentManagerDAO;
	@Autowired
	private CustomerDocumentDAO customerDocumentDAO;
	private DocumentDetailValidation documentValidation;
	
	
	public FieldInvestigationServiceImpl() {
		super();
	}

	@Override
	public void save(FieldInvestigation fi, TableType tempTab) {
		setAudit(fi);
		fieldInvestigationDAO.save(fi, tempTab);
	}
	
	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * verification_fi/verification_fi_Temp by using verification_fiDAO's save method b) Update the Record in the table.
	 * based on the module workFlow Configuration. by using verification_fiDAO's update method 3) Audit the record in to
	 * AuditHeader and Adtverification_fi by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		FieldInvestigation fi = (FieldInvestigation) auditHeader.getAuditDetail().getModelData();

		List<AuditDetail> auditDetails = new ArrayList<>();
		TableType tableType = TableType.MAIN_TAB;
		if (fi.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (fi.isNew()) {
			fi.setId(Long.parseLong(fieldInvestigationDAO.save(fi, tableType)));
			auditHeader.getAuditDetail().setModelData(fi);
			auditHeader.setAuditReference(String.valueOf(fi.getId()));
		} else {
			fieldInvestigationDAO.update(fi, tableType);
		}
		// Extended field Details
		if (fi.getExtendedFieldRender() != null) {
			List<AuditDetail> details = fi.getAuditDetailMap().get("ExtendedFieldDetails");
			StringBuilder tableName = new StringBuilder();
			tableName.append(CollateralConstants.VERIFICATION_MODULE);
			tableName.append("_");
			tableName.append(fi.getExtendedFieldHeader().getSubModuleName());
			tableName.append("_ED");
			details = extendedFieldDetailsService.processingExtendedFieldDetailList(details, tableName.toString(),
					tableType.getSuffix());
			auditDetails.addAll(details);
		}
		
		// FI documents
		if (fi.getDocuments() != null && !fi.getDocuments().isEmpty()) {
			List<AuditDetail> details = fi.getAuditDetailMap().get("DocumentDetails");
			details = saveOrUpdateDocuments(details, fi, tableType.getSuffix());
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
	private List<AuditDetail> saveOrUpdateDocuments(List<AuditDetail> auditDetails,
			FieldInvestigation fi, String type) {
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
			documentDetails.setLastMntBy(fi.getLastMntBy());
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

			if (saveRecord) {
				if (StringUtils.isEmpty(documentDetails.getReferenceId())) {
					documentDetails.setReferenceId(String.valueOf(fi.getVerificationId()));
				}
				if (documentDetails.getDocRefId() <= 0) {
					DocumentManager documentManager = new DocumentManager();
					documentManager.setDocImage(documentDetails.getDocImage());
					documentDetails.setDocRefId(documentManagerDAO.save(documentManager));
				}
				// Pass the docRefId here to save this in place of docImage column. Or add another column for now to
				// save this.
				documentDetailsDAO.save(documentDetails, type);
			}

			if (updateRecord) {
				// When a document is updated, insert another file into the DocumentManager table's.
				// Get the new DocumentManager.id & set to documentDetails.getDocRefId()
				if (documentDetails.getDocRefId() <= 0) {
					DocumentManager documentManager = new DocumentManager();
					documentManager.setDocImage(documentDetails.getDocImage());
					documentDetails.setDocRefId(documentManagerDAO.save(documentManager));
				}
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
	 * verification_fi by using verification_fiDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and Adtverification_fi by using auditHeaderDAO.addAudit(auditHeader)
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

		FieldInvestigation fieldInvestigation = (FieldInvestigation) auditHeader.getAuditDetail().getModelData();
		auditDetails.addAll(deleteChilds(fieldInvestigation, "", auditHeader.getAuditTranType()));
		fieldInvestigationDAO.delete(fieldInvestigation, TableType.MAIN_TAB);

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getverification_fi fetch the details by using verification_fiDAO's getverification_fiById method.
	 * 
	 * @param id
	 *            id of the FieldInvestigation.
	 * @return verification_fi
	 */
	@Override
	public FieldInvestigation getFieldInvestigation(long id) {
		FieldInvestigation fieldInvestigation = fieldInvestigationDAO.getFieldInvestigation(id, "_View");
		if (fieldInvestigation != null) {
			// FI Document Details
			List<DocumentDetails> documentList = documentDetailsDAO.getDocumentDetailsByRef(String.valueOf(id),
					VerificationType.FI.getCode(), "", "_View");
			if (fieldInvestigation.getDocuments() != null && !fieldInvestigation.getDocuments().isEmpty()) {
				fieldInvestigation.getDocuments().addAll(documentList);
			} else {
				fieldInvestigation.setDocuments(documentList);
			}
		}

		return fieldInvestigation;
	}

	/**
	 * getApprovedverification_fiById fetch the details by using verification_fiDAO's getverification_fiById method .
	 * with parameter id and type as blank. it fetches the approved records from the verification_fi.
	 * 
	 * @param id
	 *            id of the FieldInvestigation. (String)
	 * @return verification_fi
	 */
	public FieldInvestigation getApprovedFieldInvestigation(long id) {
		return fieldInvestigationDAO.getFieldInvestigation(id, "");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using fieldInvestigationDAO.delete with
	 * parameters fieldInvestigation,"" b) NEW Add new record in to main table by using fieldInvestigationDAO.save
	 * with parameters fieldInvestigation,"" c) EDIT Update record in the main table by using
	 * fieldInvestigationDAO.update with parameters fieldInvestigation,"" 3) Delete the record from the workFlow
	 * table by using fieldInvestigationDAO.delete with parameters fieldInvestigation,"_Temp" 4) Audit the record
	 * in to AuditHeader and Adtverification_fi by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the
	 * record in to AuditHeader and Adtverification_fi by using auditHeaderDAO.addAudit(auditHeader) based on the
	 * transaction Type.
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

		FieldInvestigation fi = new FieldInvestigation();
		BeanUtils.copyProperties((FieldInvestigation) auditHeader.getAuditDetail().getModelData(), fi);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(fi.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(fieldInvestigationDAO.getFieldInvestigation(fi.getId(), ""));
		}

		if (fi.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			auditDetails.addAll(deleteChilds(fi, "", tranType));
			fieldInvestigationDAO.delete(fi, TableType.MAIN_TAB);
		} else {
			fi.setRoleCode("");
			fi.setNextRoleCode("");
			fi.setTaskId("");
			fi.setNextTaskId("");
			fi.setWorkflowId(0);

			if (fi.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				fi.setRecordType("");
				fieldInvestigationDAO.save(fi, TableType.MAIN_TAB);
				verificationDAO.updateVerifiaction(fi.getId(), fi.getDate(), fi.getStatus());
			} else {
				tranType = PennantConstants.TRAN_UPD;
				fi.setRecordType("");
				fieldInvestigationDAO.update(fi, TableType.MAIN_TAB);
				verificationDAO.updateVerifiaction(fi.getId(), fi.getDate(), fi.getStatus());
			}
			
			// Extended field Details
			if (fi.getExtendedFieldRender() != null) {
				List<AuditDetail> details = fi.getAuditDetailMap().get("ExtendedFieldDetails");

				// Table Name
				StringBuilder tableName = new StringBuilder();
				tableName.append(CollateralConstants.VERIFICATION_MODULE);
				tableName.append("_");
				tableName.append(fi.getExtendedFieldHeader().getSubModuleName());
				tableName.append("_ed");

				details = extendedFieldDetailsService.processingExtendedFieldDetailList(details, tableName.toString(),
						TableType.MAIN_TAB.getSuffix());
				auditDetails.addAll(details);
			}

			// FI Document Details
			List<DocumentDetails> documentsList = fi.getDocuments();
			if (documentsList != null && documentsList.size() > 0) {
				List<AuditDetail> details = fi.getAuditDetailMap().get("DocumentDetails");
				details = saveOrUpdateDocuments(details, fi, "");
				auditDetails.addAll(details);
			}

		}

		List<AuditDetail> auditDetailList = new ArrayList<>();

		String[] fields = PennantJavaUtil.getFieldDetails(new FieldInvestigation(), fi.getExcludeFields());
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		auditDetailList.addAll(deleteChilds(fi, "_Temp", auditHeader.getAuditTranType()));
		fieldInvestigationDAO.delete(fi, TableType.TEMP_TAB);

		auditHeader.setAuditDetail(
				new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], fi.getBefImage(), fi));
		auditHeader.setAuditDetails(auditDetailList);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(fi);
		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	// Method for Deleting all records related to FI setup in _Temp/Main tables depend on method type
	public List<AuditDetail> deleteChilds(FieldInvestigation fi, String tableType, String auditTranType) {
		logger.debug("Entering");

		List<AuditDetail> auditList = new ArrayList<>();
		
		// Extended field Render Details.
		List<AuditDetail> extendedDetails = fi.getAuditDetailMap().get("ExtendedFieldDetails");
		if (extendedDetails != null && !extendedDetails.isEmpty()) {
			// Table Name
			StringBuilder tableName = new StringBuilder();
			tableName.append(CollateralConstants.VERIFICATION_MODULE);
			tableName.append("_");
			tableName.append(fi.getExtendedFieldHeader().getSubModuleName());
			tableName.append("_ED");
			auditList.addAll(extendedFieldDetailsService.delete(fi.getExtendedFieldHeader(), String.valueOf(fi.getVerificationId()),
					tableName.toString(), tableType, auditTranType, extendedDetails));
		}

		// Document Details.
		List<AuditDetail> documentDetails = fi.getAuditDetailMap().get("DocumentDetails");
		if (documentDetails != null && documentDetails.size() > 0) {
			DocumentDetails document = new DocumentDetails();
			List<DocumentDetails> documents = new ArrayList<>();
			String[] fields = PennantJavaUtil.getFieldDetails(document, document.getExcludeFields());
			for (int i = 0; i < documentDetails.size(); i++) {
				document = (DocumentDetails) documentDetails.get(i).getModelData();
				document.setRecordType(PennantConstants.RECORD_TYPE_CAN);
				documents.add(document);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], document.getBefImage(), document));
			}
			documentDetailsDAO.deleteList(documents, tableType);
		}

		logger.debug("Leaving");
		return auditList;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using fieldInvestigationDAO.delete with parameters fieldInvestigation,"_Temp" 3) Audit the
	 * record in to AuditHeader and Adtverification_fi by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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
		FieldInvestigation fieldInvestigation = (FieldInvestigation) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		String[] fields = PennantJavaUtil.getFieldDetails(new FieldInvestigation(),
				fieldInvestigation.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				fieldInvestigation.getBefImage(), fieldInvestigation));

		auditDetails.addAll(deleteChilds(fieldInvestigation, "_Temp", auditHeader.getAuditTranType()));
		fieldInvestigationDAO.delete(fieldInvestigation, TableType.TEMP_TAB);

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

		auditHeader = getAuditDetails(auditHeader, method);
		FieldInvestigation fieldInvestigation = (FieldInvestigation) auditDetail.getModelData();
		String usrLanguage = fieldInvestigation.getUserDetails().getLanguage();
		
		// Extended field details Validation
		if (fieldInvestigation.getExtendedFieldRender() != null) {
			List<AuditDetail> details = fieldInvestigation.getAuditDetailMap().get("ExtendedFieldDetails");
			ExtendedFieldHeader extHeader = fieldInvestigation.getExtendedFieldHeader();

			StringBuilder tableName = new StringBuilder();
			tableName.append(CollateralConstants.VERIFICATION_MODULE);
			tableName.append("_");
			tableName.append(extHeader.getSubModuleName());
			tableName.append("_ED");

			details = extendedFieldDetailsService.vaildateDetails(details, method, usrLanguage, tableName.toString());
			auditDetails.addAll(details);
		}

		// FI Document details Validation
		List<DocumentDetails> docuemnts = fieldInvestigation.getDocuments();
		if (docuemnts != null && !docuemnts.isEmpty()) {
			List<AuditDetail> details = fieldInvestigation.getAuditDetailMap().get("DocumentDetails");
			details = getDocumentValidation().vaildateDetails(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from fieldInvestigationDAO.getErrorDetail with Error ID and language as parameters. if any error/Warnings
	 * then assign the to auditDeail Object
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
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FieldInvestigation fieldInvestigation = (FieldInvestigation) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (fieldInvestigation.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}
		
		// Extended Field Details
		if (fieldInvestigation.getExtendedFieldRender() != null) {
			auditDetailMap.put("ExtendedFieldDetails", extendedFieldDetailsService
					.setExtendedFieldsAuditData(fieldInvestigation.getExtendedFieldRender(), auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("ExtendedFieldDetails"));
		}

		// FI Document Details
		if (fieldInvestigation.getDocuments() != null && fieldInvestigation.getDocuments().size() > 0) {
			auditDetailMap.put("DocumentDetails",
					setDocumentDetailsAuditData(fieldInvestigation, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("DocumentDetails"));
		}
		fieldInvestigation.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(fieldInvestigation);
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
	public List<AuditDetail> setDocumentDetailsAuditData(FieldInvestigation fieldInvestigation, String auditTranType,
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
	public List<Long> getFieldInvestigationIds(List<Verification> verifications, String keyRef) {
		List<Long> fiIds = new ArrayList<>();
		List<FieldInvestigation> fiList = fieldInvestigationDAO.getList(keyRef);
		for (FieldInvestigation fieldInvestigation : fiList) {
			for (Verification verification : verifications) {
				if (fieldInvestigation.getVerificationId() == verification.getId()) {
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
				setFiFields(item, address, phoneNumbers);
				fieldInvestigationDAO.save(item.getFieldInvestigation(), TableType.TEMP_TAB);
				break;
			}
		}
	}

	private void setFiFields(Verification verification, CustomerAddres address,
			List<CustomerPhoneNumber> phoneNumbers) {

		FieldInvestigation fi = new FieldInvestigation();

		fi.setVerificationId(verification.getId());
		fi.setAddressType(address.getCustAddrType());
		fi.setName(verification.getCustomerName());
		fi.setHouseNumber(address.getCustAddrHNbr());
		fi.setFlatNumber(address.getCustFlatNbr());
		fi.setStreet(address.getCustAddrStreet());
		fi.setAddressLine1(address.getCustAddrLine1());
		fi.setAddressLine2(address.getCustAddrLine2());
		fi.setAddressLine3(address.getCustAddrLine3());
		fi.setAddressLine4(address.getCustAddrLine4());
		fi.setAddressLine5(null);
		fi.setCountry(address.getCustAddrCountry());
		fi.setProvince(address.getCustAddrProvince());
		fi.setCity(address.getCustAddrCity());
		fi.setVersion(1);
		fi.setLastMntBy(verification.getLastMntBy());
		fi.setLastMntOn(verification.getLastMntOn());
		setAudit(fi);
		Collections.sort(phoneNumbers, new PhonePriority());
		fi.setContactNumber1((phoneNumbers.get(0)).getPhoneNumber());
		if (phoneNumbers.size() > 1) {
			fi.setContactNumber2((phoneNumbers.get(1)).getPhoneNumber());
		}
		fi.setPoBox(address.getCustPOBox());
		fi.setZipCode(address.getCustAddrZIP());

		verification.setFieldInvestigation(fi);
	}

	private void setAudit(FieldInvestigation fi) {
		String workFlowType = ModuleUtil.getWorkflowType("FieldInvestigation");
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

	private List<Verification> getScreenVerifications(Verification verification) {
		List<Verification> verifications = new ArrayList<>();
		List<CustomerDetails> customerDetailsList = verification.getCustomerDetailsList();
		List<String> requiredCodes = addressTypeDAO.getFiRequiredCodes();

		for (CustomerDetails customerDetails : customerDetailsList) {
			if (customerDetails.getAddressList() == null) {
				continue;
			}
			
			for (CustomerAddres address : customerDetails.getAddressList()) {
				Verification vrf = new Verification();
				vrf.setNewRecord(true);
				vrf.setVerificationType(verification.getVerificationType());
				vrf.setModule(verification.getModule());
				vrf.setKeyReference(verification.getKeyReference());
				vrf.setVerificationType(VerificationType.FI.getKey());
				vrf.setCustId(customerDetails.getCustomer().getCustID());
				vrf.setCif(customerDetails.getCustomer().getCustCIF());
				vrf.setReference(vrf.getCif());
				vrf.setCustomerName(customerDetails.getCustomer().getCustShrtName());
				if (verification.getCif().equals(vrf.getCif())) {
					vrf.setReferenceType("Primary");
				} else {
					vrf.setReferenceType("Co-applicant");
				}

				if (requiredCodes.contains(address.getCustAddrType())) {
					vrf.setRequestType(RequestType.INITIATE.getKey());
				} else {
					vrf.setRequestType(RequestType.NOT_REQUIRED.getKey());
				}
				vrf.setRecordType(address.getRecordType());
				vrf.setReferenceFor(address.getCustAddrType());
				vrf.setCreatedOn(DateUtil.getDatePart(DateUtil.getSysDate()));
				setFiFields(vrf, address, customerDetails.getCustomerPhoneNumList());

				verifications.add(vrf);
			}
		}
		return verifications;
	}

	@Override
	public Verification getFiVeriFication(Verification verification) {
		logger.info(Literal.ENTERING);
		List<Verification> preVerifications = verificationDAO.getVeriFications(verification.getKeyReference(),
				VerificationType.FI.getKey());
		List<Verification> screenVerifications = getScreenVerifications(verification);

		setLastStatus(screenVerifications);

		if (!preVerifications.isEmpty()) {
			List<FieldInvestigation> fiList = fieldInvestigationDAO.getList(verification.getKeyReference());

			for (Verification pvr : preVerifications) {
				for (FieldInvestigation fi : fiList) {
					if (pvr.getId() == fi.getVerificationId()) {
						pvr.setFieldInvestigation(fi);
					}
				}
			}
		}
		getChangedVerifications(preVerifications, screenVerifications, verification.getKeyReference());
		verification.setVerifications(
				compareVerifications(screenVerifications, preVerifications, verification.getKeyReference()));

		logger.info(Literal.LEAVING);
		return verification;
	}

	private void setLastStatus(List<Verification> verifications) {
		String[] cif = new String[verifications.size()];

		int i = 0;
		for (Verification verification : verifications) {
			cif[i++] = verification.getCif();
		}
		if (cif.length != 0) {
			List<FieldInvestigation> list = fieldInvestigationDAO.getList(cif);

			for (Verification verification : verifications) {
				FieldInvestigation current = verification.getFieldInvestigation();
				for (FieldInvestigation previous : list) {
					if (previous.getCif().equals(verification.getCif())
							&& previous.getAddressType().equals(current.getAddressType())) {
						if (!isAddressChange(previous, current)) {
							verification.setStatus(previous.getStatus());
							verification.setVerificationDate(new Timestamp(previous.getDate().getTime()));
						}
					}
				}
			}
		}
	}

	private void getChangedVerifications(List<Verification> oldList, List<Verification> newList,
			String keyReference) {
		List<Long> fiIds = getFieldInvestigationIds(oldList, keyReference);
		for (Verification newVer : newList) {
			for (Verification oldVer : oldList) {
				if (oldVer.getCustId().compareTo(newVer.getCustId()) == 0
						&& oldVer.getReferenceFor().equals(newVer.getReferenceFor())) {
					if (oldVer.getRequestType() != RequestType.INITIATE.getKey() || !fiIds.contains(oldVer.getId())) {
						break;
					}
					if (oldVer.getRequestType() == RequestType.INITIATE.getKey()
							&& isAddressChange(oldVer.getFieldInvestigation(), newVer.getFieldInvestigation())
							&& fiIds.contains(oldVer.getId())) {
						newVer.setReinitid(oldVer.getId());
					}
				}

			}
		}
	}

	private boolean isAddressChange(FieldInvestigation oldAddress, FieldInvestigation newAddress) {

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

	private List<Verification> compareVerifications(List<Verification> screenVerifications,
			List<Verification> preVerifications, String keyReference) {
		List<Verification> tempList = new ArrayList<>();
		tempList.addAll(screenVerifications);
		Collections.reverse(preVerifications);
		tempList.addAll(preVerifications);
		Collections.reverse(preVerifications);
		List<Long> fiIds = getFieldInvestigationIds(preVerifications, keyReference);

		screenVerifications.addAll(preVerifications);

		for (Verification vrf : tempList) {
			for (Verification preVrf : preVerifications) {
				if (vrf.getCustId().compareTo(preVrf.getCustId()) == 0
						&& vrf.getReferenceFor().equals(preVrf.getReferenceFor())
						&& (StringUtils.isEmpty(vrf.getRecordType())
								|| !vrf.getRecordType().equals(PennantConstants.RCD_UPD))
						&& vrf.getReinitid() == null
						&& !isAddressChange(preVrf.getFieldInvestigation(), vrf.getFieldInvestigation())
						&& !fiIds.contains(vrf.getId())) {
					screenVerifications.remove(vrf);
					preVerifications.remove(preVrf);
					break;
				}
			}
		}

		return screenVerifications;
	}

	@Override
	public boolean isAddressesAdded(List<CustomerAddres> screenCustomerAddresses,
			List<CustomerAddres> savedCustomerAddresses) {
		boolean flag = true;
	
		for (CustomerAddres screenCustomerAddres : screenCustomerAddresses) {
			for (CustomerAddres savedCustomerAddres : savedCustomerAddresses) {
				if (savedCustomerAddres.getCustAddrType().equals(screenCustomerAddres.getCustAddrType())
						|| (StringUtils.isNotEmpty(screenCustomerAddres.getRecordType())
								&& screenCustomerAddres.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL))) {
					flag = false;
				}
			}
			if (flag) {
				return flag;
			}
			flag = true;
		}
		return false;
	}

	@Override
	public boolean isAddressChanged(CustomerAddres newAddress, CustomerAddres oldAddress) {

		if (!StringUtils.equals(newAddress.getCustAddrHNbr(), oldAddress.getCustAddrHNbr())) {
			return true;
		}
		if (!StringUtils.equals(newAddress.getCustFlatNbr(), oldAddress.getCustFlatNbr())) {
			return true;
		}
		if (!StringUtils.equals(newAddress.getCustAddrStreet(), oldAddress.getCustAddrStreet())) {
			return true;
		}
		if (!StringUtils.equals(newAddress.getCustAddrLine1(), oldAddress.getCustAddrLine1())) {
			return true;
		}
		if (!StringUtils.equals(newAddress.getCustAddrLine2(), oldAddress.getCustAddrLine2())) {
			return true;
		}
		if (!StringUtils.equals(newAddress.getCustAddrLine3(), oldAddress.getCustAddrLine3())) {
			return true;
		}
		if (!StringUtils.equals(newAddress.getCustAddrLine4(), oldAddress.getCustAddrLine4())) {
			return true;
		}
		if (!StringUtils.equals(newAddress.getCustAddrCountry(), oldAddress.getCustAddrCountry())) {
			return true;
		}
		if (!StringUtils.equals(newAddress.getCustAddrProvince(), oldAddress.getCustAddrProvince())) {
			return true;
		}
		if (!StringUtils.equals(newAddress.getCustAddrCity(), oldAddress.getCustAddrCity())) {
			return true;
		}
		if (!StringUtils.equals(newAddress.getCustPOBox(), oldAddress.getCustPOBox())) {
			return true;
		}
		if (!StringUtils.equals(newAddress.getCustAddrZIP(), oldAddress.getCustAddrZIP())) {
			return true;
		}

		return false;
	}

	public class PhonePriority implements Comparator<CustomerPhoneNumber> {
		@Override
		public int compare(CustomerPhoneNumber o1, CustomerPhoneNumber o2) {
			return o2.getPhoneTypePriority() - o1.getPhoneTypePriority();
		}

	}

	@Override
	public List<FieldInvestigation> getList(String keyReference) {
		return fieldInvestigationDAO.getList(keyReference);
	}

	public DocumentDetailValidation getDocumentValidation() {
		if (documentValidation == null) {
			this.documentValidation = new DocumentDetailValidation(documentDetailsDAO, documentManagerDAO, customerDocumentDAO);
		}
		return documentValidation;
	}
}