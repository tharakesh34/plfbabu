package com.pennanttech.pennapps.pff.verification.service;

import java.util.ArrayList;
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
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.collateral.impl.DocumentDetailValidation;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pennapps.pff.verification.DocumentType;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.dao.RiskContainmentUnitDAO;
import com.pennanttech.pennapps.pff.verification.dao.VerificationDAO;
import com.pennanttech.pennapps.pff.verification.model.RCUDocument;
import com.pennanttech.pennapps.pff.verification.model.RiskContainmentUnit;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pff.core.TableType;

public class RiskContainmentUnitServiceImpl extends GenericService<RiskContainmentUnit>
		implements RiskContainmentUnitService {

	private static final Logger logger = Logger.getLogger(RiskContainmentUnitServiceImpl.class);

	@Autowired
	private AuditHeaderDAO auditHeaderDAO;
	@Autowired
	private RiskContainmentUnitDAO riskContainmentUnitDAO;
	@Autowired
	private VerificationDAO verificationDAO;
	@Autowired
	private DocumentDetailsDAO documentDetailsDAO;
	@Autowired
	private DocumentManagerDAO documentManagerDAO;
	@Autowired
	private CustomerDocumentDAO customerDocumentDAO;
	private DocumentDetailValidation documentValidation;
	
	public RiskContainmentUnitServiceImpl() {
		super();
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		RiskContainmentUnit rcu = (RiskContainmentUnit) auditHeader.getAuditDetail().getModelData();

		List<AuditDetail> auditDetails = new ArrayList<>();
		TableType tableType = TableType.MAIN_TAB;
		if (rcu.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (rcu.isNew()) {
			rcu.setId(Long.parseLong(riskContainmentUnitDAO.save(rcu, tableType)));
			auditHeader.getAuditDetail().setModelData(rcu);
			auditHeader.setAuditReference(String.valueOf(rcu.getId()));
		} else {
			riskContainmentUnitDAO.update(rcu, tableType);
		}

		// Documents
		if (rcu.getDocuments() != null && !rcu.getDocuments().isEmpty()) {
			List<AuditDetail> details = rcu.getAuditDetailMap().get("DocumentDetails");
			details = saveOrUpdateDocuments(details, rcu, tableType.getSuffix());
			auditDetails.addAll(details);
		}

		// RCU Documents
		if (rcu.getRcuDocuments() != null && !rcu.getRcuDocuments().isEmpty()) {
			List<AuditDetail> details = rcu.getAuditDetailMap().get("RCUDocumentDetails");
			details = processingRCUDocumnets(details, rcu, tableType.getSuffix());
			auditDetails.addAll(details);
		}

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	private List<AuditDetail> processingRCUDocumnets(List<AuditDetail> auditDetails, RiskContainmentUnit rcu,
			String type) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			RCUDocument rcuDocument = (RCUDocument) auditDetails.get(i).getModelData();

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;

			String rcdType = "";
			String recordStatus = "";

			if (StringUtils.isEmpty(type.toString())) {
				approveRec = true;
				rcuDocument.setRoleCode("");
				rcuDocument.setNextRoleCode("");
				rcuDocument.setTaskId("");
				rcuDocument.setNextTaskId("");
			}

			if (rcuDocument.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (rcuDocument.isNewRecord()) {
				saveRecord = true;
				if (rcuDocument.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					rcuDocument.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (rcuDocument.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					rcuDocument.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (rcuDocument.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					rcuDocument.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (rcuDocument.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (rcuDocument.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (rcuDocument.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (rcuDocument.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = rcuDocument.getRecordType();
				recordStatus = rcuDocument.getRecordStatus();
				rcuDocument.setRecordType("");
				rcuDocument.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			if (saveRecord) {
				riskContainmentUnitDAO.saveDocuments(rcuDocument, type);
			}

			if (updateRecord) {
				riskContainmentUnitDAO.updateDocuments(rcuDocument, type);
			}

			if (deleteRecord) {
				riskContainmentUnitDAO.deleteRCUDocuments(rcuDocument, type);
			}

			if (approveRec) {
				rcuDocument.setRecordType(rcdType);
				rcuDocument.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(rcuDocument);
		}

		logger.debug("Leaving");
		return auditDetails;

	}

	/**
	 * Method For Preparing List of AuditDetails for Document Details
	 * 
	 * @param auditDetails
	 * @param rcu
	 * @param type
	 * @return
	 */
	private List<AuditDetail> saveOrUpdateDocuments(List<AuditDetail> auditDetails, RiskContainmentUnit rcu,
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
			documentDetails.setLastMntBy(rcu.getLastMntBy());
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
					documentDetails.setReferenceId(String.valueOf(rcu.getVerificationId()));
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

	@Override
	public RiskContainmentUnit getRiskContainmentUnit(RiskContainmentUnit rcu) {
		RiskContainmentUnit riskContainmentUnit = riskContainmentUnitDAO.getRiskContainmentUnit(rcu.getVerificationId(),
				"_View");
		if (riskContainmentUnit != null) {

			// RCU Document Details
			List<RCUDocument> rcuDocuments = riskContainmentUnitDAO.getRCUDocuments(rcu.getVerificationId(), "_View");
			riskContainmentUnit.setRcuDocuments(rcuDocuments);

			// Document Details
			List<DocumentDetails> documentList = documentDetailsDAO.getDocumentDetailsByRef(
					String.valueOf(rcu.getVerificationId()), VerificationType.RCU.getCode(), "", "_View");
			if (riskContainmentUnit.getDocuments() != null && !riskContainmentUnit.getDocuments().isEmpty()) {
				riskContainmentUnit.getDocuments().addAll(documentList);
			} else {
				riskContainmentUnit.setDocuments(documentList);
			}
		}

		return riskContainmentUnit;
	}

	@Override
	public RiskContainmentUnit getApprovedRiskContainmentUnit(long verificationId) {
		return riskContainmentUnitDAO.getRiskContainmentUnit(verificationId, "");
	}

	@Override
	public RiskContainmentUnit getRiskContainmentUnit(long verificationId) {
		return riskContainmentUnitDAO.getRiskContainmentUnit(verificationId, "_View");
	}
	
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();
		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		RiskContainmentUnit rcu = (RiskContainmentUnit) auditHeader.getAuditDetail().getModelData();
		auditDetails.addAll(deleteChilds(rcu, "", auditHeader.getAuditTranType()));
		riskContainmentUnitDAO.delete(rcu, TableType.MAIN_TAB);

		String[] fields = PennantJavaUtil.getFieldDetails(new RiskContainmentUnit(), rcu.getExcludeFields());
		auditHeader.setAuditDetail(
				new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], rcu.getBefImage(), rcu));
		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

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

		RiskContainmentUnit rcu = new RiskContainmentUnit();
		BeanUtils.copyProperties((RiskContainmentUnit) auditHeader.getAuditDetail().getModelData(), rcu);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(rcu.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(riskContainmentUnitDAO.getRiskContainmentUnit(rcu.getVerificationId(), ""));
		}

		if (rcu.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			auditDetails.addAll(deleteChilds(rcu, "", tranType));
			riskContainmentUnitDAO.delete(rcu, TableType.MAIN_TAB);
		} else {
			rcu.setRoleCode("");
			rcu.setNextRoleCode("");
			rcu.setTaskId("");
			rcu.setNextTaskId("");
			rcu.setWorkflowId(0);

			if (rcu.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				rcu.setRecordType("");
				riskContainmentUnitDAO.save(rcu, TableType.MAIN_TAB);
				verificationDAO.updateVerifiaction(rcu.getId(), rcu.getVerificationDate(), rcu.getStatus());
			} else {
				tranType = PennantConstants.TRAN_UPD;
				rcu.setRecordType("");
				riskContainmentUnitDAO.update(rcu, TableType.MAIN_TAB);
				verificationDAO.updateVerifiaction(rcu.getId(), rcu.getVerificationDate(), rcu.getStatus());
			}

			// Document Details
			List<DocumentDetails> documentsList = rcu.getDocuments();
			if (documentsList != null && documentsList.size() > 0) {
				List<AuditDetail> details = rcu.getAuditDetailMap().get("DocumentDetails");
				details = saveOrUpdateDocuments(details, rcu, "");
				auditDetails.addAll(details);
			}

			// RCU Document Details
			List<RCUDocument> rcuDocuments = rcu.getRcuDocuments();
			if (rcuDocuments != null && !rcuDocuments.isEmpty()) {
				List<AuditDetail> details = rcu.getAuditDetailMap().get("RCUDocumentDetails");
				details = processingRCUDocumnets(details, rcu, "");
				auditDetails.addAll(details);
			}

		}

		List<AuditDetail> auditDetailList = new ArrayList<>();

		String[] fields = PennantJavaUtil.getFieldDetails(new RiskContainmentUnit(), rcu.getExcludeFields());
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		auditDetailList.addAll(deleteChilds(rcu, "_Temp", auditHeader.getAuditTranType()));
		riskContainmentUnitDAO.delete(rcu, TableType.TEMP_TAB);

		auditHeader.setAuditDetail(
				new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], rcu.getBefImage(), rcu));
		auditHeader.setAuditDetails(auditDetailList);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(rcu);
		auditHeader.setAuditDetails(auditDetails);
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
		List<AuditDetail> auditDetails = new ArrayList<>();
		RiskContainmentUnit rcu = (RiskContainmentUnit) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		String[] fields = PennantJavaUtil.getFieldDetails(new RiskContainmentUnit(), rcu.getExcludeFields());
		auditHeader.setAuditDetail(
				new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], rcu.getBefImage(), rcu));

		auditDetails.addAll(deleteChilds(rcu, "_Temp", auditHeader.getAuditTranType()));
		riskContainmentUnitDAO.delete(rcu, TableType.TEMP_TAB);

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	// Method for Deleting all records related to FI setup in _Temp/Main tables depend on method type
	public List<AuditDetail> deleteChilds(RiskContainmentUnit rcu, String tableType, String auditTranType) {
		logger.debug("Entering");

		List<AuditDetail> auditList = new ArrayList<>();

		// Document Details.
		List<AuditDetail> documentDetails = rcu.getAuditDetailMap().get("DocumentDetails");
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

		// RCU Document Details.
		List<AuditDetail> rcuDocuments = rcu.getAuditDetailMap().get("RCUDocumentDetails");
		if (rcuDocuments != null && !rcuDocuments.isEmpty()) {
			RCUDocument document = new RCUDocument();
			List<RCUDocument> documents = new ArrayList<>();
			String[] fields = PennantJavaUtil.getFieldDetails(document, document.getExcludeFields());
			for (int i = 0; i < rcuDocuments.size(); i++) {
				document = (RCUDocument) rcuDocuments.get(i).getModelData();
				document.setRecordType(PennantConstants.RECORD_TYPE_CAN);
				documents.add(document);
				auditList.add(
						new AuditDetail(auditTranType, i + 1, fields[0], fields[1], document.getBefImage(), document));
			}
			riskContainmentUnitDAO.deleteRCUDocumentsList(documents, tableType);
		}

		logger.debug("Leaving");
		return auditList;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		auditHeader = getAuditDetails(auditHeader, method);
		RiskContainmentUnit rcu = (RiskContainmentUnit) auditDetail.getModelData();
		String usrLanguage = rcu.getUserDetails().getLanguage();

		// Document details Validation
		List<DocumentDetails> docuemnts = rcu.getDocuments();
		if (docuemnts != null && !docuemnts.isEmpty()) {
			List<AuditDetail> details = rcu.getAuditDetailMap().get("DocumentDetails");
			details = getDocumentValidation().vaildateDetails(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Write the required validation over hear.

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		RiskContainmentUnit rcu = (RiskContainmentUnit) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (rcu.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		// Document Details
		if (rcu.getDocuments() != null && rcu.getDocuments().size() > 0) {
			auditDetailMap.put("DocumentDetails", setDocumentDetailsAuditData(rcu, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("DocumentDetails"));
		}

		// RCU Document Details
		if (rcu.getRcuDocuments() != null && !rcu.getRcuDocuments().isEmpty()) {
			auditDetailMap.put("RCUDocumentDetails", setRCUDocumentDetailsAuditData(rcu, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("RCUDocumentDetails"));
		}
		rcu.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(rcu);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug("Leaving");
		return auditHeader;
	}

	public List<AuditDetail> setRCUDocumentDetailsAuditData(RiskContainmentUnit rcu, String auditTranType,
			String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<>();

		RCUDocument rcuDocument = new RCUDocument();
		String[] fields = PennantJavaUtil.getFieldDetails(rcuDocument, rcuDocument.getExcludeFields());

		for (int i = 0; i < rcu.getRcuDocuments().size(); i++) {
			RCUDocument rcuDocumentDetails = rcu.getRcuDocuments().get(i);

			if (StringUtils.isEmpty(StringUtils.trimToEmpty(rcuDocumentDetails.getRecordType()))) {
				continue;
			}

			rcuDocumentDetails.setWorkflowId(rcu.getWorkflowId());
			boolean isRcdType = false;

			if (rcuDocumentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				rcuDocumentDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (rcuDocumentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				rcuDocumentDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (rcu.isWorkflow()) {
					isRcdType = true;
				}
			} else if (rcuDocumentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				rcuDocumentDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				rcuDocumentDetails.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (rcuDocumentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (rcuDocumentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| rcuDocumentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			rcuDocumentDetails.setRecordStatus(rcu.getRecordStatus());
			rcuDocumentDetails.setUserDetails(rcu.getUserDetails());
			rcuDocumentDetails.setLastMntOn(rcu.getLastMntOn());
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
					rcuDocumentDetails.getBefImage(), rcuDocumentDetails));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	public List<AuditDetail> setDocumentDetailsAuditData(RiskContainmentUnit rcu, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<>();

		DocumentDetails document = new DocumentDetails();
		String[] fields = PennantJavaUtil.getFieldDetails(document, document.getExcludeFields());

		for (int i = 0; i < rcu.getDocuments().size(); i++) {
			DocumentDetails documentDetails = rcu.getDocuments().get(i);

			if (StringUtils.isEmpty(StringUtils.trimToEmpty(documentDetails.getRecordType()))) {
				continue;
			}

			documentDetails.setWorkflowId(rcu.getWorkflowId());
			boolean isRcdType = false;

			if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (rcu.isWorkflow()) {
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

			documentDetails.setRecordStatus(rcu.getRecordStatus());
			documentDetails.setUserDetails(rcu.getUserDetails());
			documentDetails.setLastMntOn(rcu.getLastMntOn());
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], documentDetails.getBefImage(),
					documentDetails));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	public DocumentDetailValidation getDocumentValidation() {
		if (documentValidation == null) {
			this.documentValidation = new DocumentDetailValidation(documentDetailsDAO, documentManagerDAO,
					customerDocumentDAO);
		}
		return documentValidation;
	}

	@Override
	public DocumentManager getDocumentById(Long docRefId) {
		return documentManagerDAO.getById(docRefId);
	}

	@Override
	public List<Long> getRCUVerificaationIds(List<Verification> verifications, String keyRef) {
		List<Long> rcuIds = new ArrayList<>();
		List<RiskContainmentUnit> rcuList = riskContainmentUnitDAO.getList(keyRef);
		for (RiskContainmentUnit rcu : rcuList) {
			for (Verification Verification : verifications) {
				if (rcu.getVerificationId() == Verification.getId()) {
					rcuIds.add(Verification.getId());
				}
			}

		}
		return rcuIds;
	}

	@Override
	public void save(Verification verification, TableType tableType) {
		setRcuFields(verification);
		setRcuDocumentWorkFlowFields(verification);
		riskContainmentUnitDAO.save(verification.getRcuVerification(), tableType);
	}

	private void setRcuFields(Verification verification) {

		RiskContainmentUnit rcu = verification.getRcuVerification();

		if (rcu == null) {
			rcu = new RiskContainmentUnit();
			verification.setRcuVerification(rcu);
		}

		rcu.setVerificationId(verification.getId());
		rcu.setVersion(1);
		rcu.setLastMntBy(verification.getLastMntBy());
		rcu.setLastMntOn(verification.getLastMntOn());
		setWorkflowFields(rcu);

	}

	private void setWorkflowFields(RiskContainmentUnit rcu) {
		String workFlowType = "MSTGRP1";//ModuleUtil.getWorkflowType("MSTGRP1");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getDetailsByType(workFlowType);
		WorkflowEngine engine = new WorkflowEngine(
				WorkFlowUtil.getWorkflow(workFlowDetails.getWorkFlowId()).getWorkFlowXml());

		rcu.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
		rcu.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		rcu.setWorkflowId(workFlowDetails.getWorkflowId());
		rcu.setRoleCode(workFlowDetails.getFirstTaskOwner());
		rcu.setNextRoleCode(workFlowDetails.getFirstTaskOwner());
		rcu.setTaskId(engine.getUserTaskId(rcu.getRoleCode()));
		rcu.setNextTaskId(engine.getUserTaskId(rcu.getNextRoleCode()) + ";");
	}

	private void setRcuDocumentWorkFlowFields(Verification verification) {
		RiskContainmentUnit rcu = verification.getRcuVerification();
		for (RCUDocument docuemnt : verification.getRcuDocuments()) {
			docuemnt.setVerificationId(rcu.getVerificationId());
			docuemnt.setVersion(rcu.getVersion());
			docuemnt.setLastMntBy(rcu.getLastMntBy());
			docuemnt.setLastMntOn(rcu.getLastMntOn());
			docuemnt.setRecordStatus(rcu.getRecordStatus());
			docuemnt.setRecordType(rcu.getRecordType());
			docuemnt.setWorkflowId(rcu.getWorkflowId());
			docuemnt.setRoleCode(rcu.getRoleCode());
			docuemnt.setNextRoleCode(rcu.getNextRoleCode());
			docuemnt.setTaskId(rcu.getTaskId());
			docuemnt.setNextTaskId(rcu.getNextTaskId());
		}
	}

	@Override
	public void saveDocuments(List<RCUDocument> rcuDocuments, TableType tableType) {
		riskContainmentUnitDAO.saveDocuments(rcuDocuments, tableType);

	}

	@Override
	public void deleteDocuments(long verificationId, TableType tableType) {
		riskContainmentUnitDAO.deleteDocuments(verificationId, tableType);

	}

	@Override
	public List<RCUDocument> getDocuments(String keyReference, TableType tableType, DocumentType documentType) {
		return riskContainmentUnitDAO.getDocuments(keyReference, tableType, documentType);
	}

}
