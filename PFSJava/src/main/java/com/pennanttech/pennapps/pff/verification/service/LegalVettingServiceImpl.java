package com.pennanttech.pennapps.pff.verification.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerDocumentDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
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
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pennapps.pff.service.hook.PostExteranalServiceHook;
import com.pennanttech.pennapps.pff.verification.DocumentType;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.dao.LegalVettingDAO;
import com.pennanttech.pennapps.pff.verification.dao.VerificationDAO;
import com.pennanttech.pennapps.pff.verification.model.LVDocument;
import com.pennanttech.pennapps.pff.verification.model.LegalVetting;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pff.core.TableType;
import com.pennapps.core.util.ObjectUtil;

public class LegalVettingServiceImpl extends GenericService<LegalVetting> implements LegalVettingService {
	private static final Logger logger = LogManager.getLogger(LegalVerificationServiceImpl.class);

	@Autowired
	private AuditHeaderDAO auditHeaderDAO;
	@Autowired
	private LegalVettingDAO legalVettingDAO;
	@Autowired
	private VerificationDAO verificationDAO;
	@Autowired
	private ExtendedFieldDetailsService extendedFieldDetailsService;
	@Autowired
	private DocumentDetailsDAO documentDetailsDAO;
	@Autowired
	private DocumentManagerDAO documentManagerDAO;
	@Autowired
	private CustomerDocumentDAO customerDocumentDAO;
	private DocumentDetailValidation documentValidation;
	@Autowired(required = false)
	@Qualifier("verificationPostExteranalServiceHook")
	private PostExteranalServiceHook postExteranalServiceHook;

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * verification_vetting/verification_vetting_Temp by using verification_vetting DAO's save method b) Update the
	 * Record in the table. based on the module workFlow Configuration. by using verification_vetting DAO's update
	 * method 3) Audit the record in to AuditHeader and Adtverification_vetting by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		LegalVetting vetting = (LegalVetting) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (vetting.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (vetting.isNewRecord()) {
			legalVettingDAO.save(vetting, tableType);
			auditHeader.getAuditDetail().setModelData(vetting);
			auditHeader.setAuditReference(String.valueOf(vetting.getVerificationId()));
		} else {
			legalVettingDAO.update(vetting, tableType);
		}

		// Extended field Details
		if (vetting.getExtendedFieldRender() != null) {
			List<AuditDetail> details = vetting.getAuditDetailMap().get("ExtendedFieldDetails");
			StringBuilder tableName = new StringBuilder();
			tableName.append(CollateralConstants.VERIFICATION_MODULE);
			tableName.append("_");
			tableName.append(vetting.getExtendedFieldHeader().getSubModuleName());
			tableName.append("_ED");
			details = extendedFieldDetailsService.processingExtendedFieldDetailList(details, tableName.toString(),
					tableType.getSuffix());
			auditDetails.addAll(details);
		}

		// Documents
		if (vetting.getDocuments() != null && !vetting.getDocuments().isEmpty()) {
			List<AuditDetail> details = vetting.getAuditDetailMap().get("DocumentDetails");
			details = saveOrUpdateDocuments(details, vetting, tableType.getSuffix());
			auditDetails.addAll(details);
		}

		// LV Documents
		if (vetting.getVettingDocuments() != null && !vetting.getVettingDocuments().isEmpty()) {
			List<AuditDetail> details = vetting.getAuditDetailMap().get("LVDocumentDetails");
			details = processingLVDocumnets(details, tableType.getSuffix());
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

	@Override
	public LegalVetting getLegalVetting(LegalVetting lv, String type) {
		LegalVetting legalVetting = legalVettingDAO.getLegalVetting(lv.getVerificationId(), type);
		if (legalVetting != null) {
			List<LVDocument> lvDocuments = legalVettingDAO.getLVDocuments(lv.getVerificationId(), type);
			legalVetting.setVettingDocuments(lvDocuments);

			// LV Document Details
			List<DocumentDetails> documentList = documentDetailsDAO.getDocumentDetailsByRef(
					String.valueOf(lv.getVerificationId()), VerificationType.VETTING.getCode(), "", type);
			if (legalVetting.getDocuments() != null && !legalVetting.getDocuments().isEmpty()) {
				legalVetting.getDocuments().addAll(documentList);
			} else {
				legalVetting.setDocuments(documentList);
			}
		}
		return legalVetting;
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

		List<AuditDetail> auditDetails = new ArrayList<>();
		aAuditHeader = businessValidation(aAuditHeader, "delete");
		if (!aAuditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return aAuditHeader;
		}

		AuditHeader auditHeader = ObjectUtil.clone(aAuditHeader);

		LegalVetting legalVetting = (LegalVetting) auditHeader.getAuditDetail().getModelData();
		auditDetails.addAll(deleteChilds(legalVetting, TableType.MAIN_TAB.getSuffix(), auditHeader.getAuditTranType()));

		legalVettingDAO.delete(legalVetting, TableType.MAIN_TAB);

		String[] fields = PennantJavaUtil.getFieldDetails(new LegalVetting(), legalVetting.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				legalVetting.getBefImage(), legalVetting));
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doApprove(AuditHeader aAuditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<>();
		aAuditHeader = businessValidation(aAuditHeader, "doApprove");

		if (!aAuditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return aAuditHeader;
		}

		AuditHeader auditHeader = ObjectUtil.clone(aAuditHeader);

		LegalVetting lv = new LegalVetting();
		BeanUtils.copyProperties(auditHeader.getAuditDetail().getModelData(), lv);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(lv.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(
					legalVettingDAO.getLegalVetting(lv.getVerificationId(), TableType.MAIN_TAB.getSuffix()));
		}

		if (lv.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			auditDetails.addAll(deleteChilds(lv, TableType.MAIN_TAB.getSuffix(), tranType));
			legalVettingDAO.delete(lv, TableType.MAIN_TAB);
		} else {
			lv.setRoleCode("");
			lv.setNextRoleCode("");
			lv.setTaskId("");
			lv.setNextTaskId("");
			lv.setWorkflowId(0);

			if (lv.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				lv.setRecordType("");
				legalVettingDAO.saveLV(lv, TableType.MAIN_TAB);
				verificationDAO.updateVerifiaction(lv.getVerificationId(), lv.getVerificationDate(), lv.getStatus());
			} else {
				lv.setRecordType("");
				legalVettingDAO.update(lv, TableType.MAIN_TAB);
				verificationDAO.updateVerifiaction(lv.getVerificationId(), lv.getVerificationDate(), lv.getStatus());
			}

			// Extended field Details
			if (lv.getExtendedFieldRender() != null) {
				List<AuditDetail> details = lv.getAuditDetailMap().get("ExtendedFieldDetails");

				// Table Name
				StringBuilder tableName = new StringBuilder();
				tableName.append(CollateralConstants.VERIFICATION_MODULE);
				tableName.append("_");
				tableName.append(lv.getExtendedFieldHeader().getSubModuleName());
				tableName.append("_ed");

				details = extendedFieldDetailsService.processingExtendedFieldDetailList(details, tableName.toString(),
						TableType.MAIN_TAB.getSuffix());
				auditDetails.addAll(details);
			}

			// Document Details
			List<DocumentDetails> documentsList = lv.getDocuments();
			if (documentsList != null && !documentsList.isEmpty()) {
				List<AuditDetail> details = lv.getAuditDetailMap().get("DocumentDetails");
				details = saveOrUpdateDocuments(details, lv, "");
				auditDetails.addAll(details);
			}

			// LV Document Details
			List<LVDocument> lvDocuments = lv.getVettingDocuments();
			if (lvDocuments != null && !lvDocuments.isEmpty()) {
				List<AuditDetail> details = lv.getAuditDetailMap().get("LVDocumentDetails");
				details = processingLVDocumnets(details, "");
				auditDetails.addAll(details);
			}

		}
		List<AuditDetail> auditDetailList = new ArrayList<>();

		auditDetailList.addAll(deleteChilds(lv, TableType.TEMP_TAB.getSuffix(), auditHeader.getAuditTranType()));
		legalVettingDAO.delete(lv, TableType.TEMP_TAB);
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		String[] fields = PennantJavaUtil.getFieldDetails(new LegalVetting(), lv.getExcludeFields());
		auditHeader.setAuditDetail(
				new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1], lv.getBefImage(), lv));
		auditHeader.setAuditDetails(auditDetailList);
		auditHeaderDAO.addAudit(auditHeader);
		// calling post hoot
		if (postExteranalServiceHook != null) {
			postExteranalServiceHook.doProcess(auditHeader, "doApprove");
		}
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
		LegalVetting lv = (LegalVetting) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditDetails.addAll(deleteChilds(lv, "_Temp", auditHeader.getAuditTranType()));
		legalVettingDAO.delete(lv, TableType.TEMP_TAB);

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public void save(Verification verification, TableType tableType) {
		setLvFields(verification);
		setLVDocumentWorkFlowFields(verification);
		legalVettingDAO.save(verification.getLegalVetting(), tableType);
	}

	@Override
	public void saveDocuments(List<LVDocument> lvDocuments, TableType tableType) {
		for (LVDocument lvDocument : lvDocuments) {
			lvDocument.setRemarks1("");
			lvDocument.setRemarks2("");
			lvDocument.setRemarks3("");
		}
		legalVettingDAO.saveDocuments(lvDocuments, tableType);
	}

	@Override
	public void deleteDocuments(long verificationId, TableType tableType) {
		legalVettingDAO.deleteDocuments(verificationId, tableType);
	}

	@Override
	public LegalVetting getLVFromStage(long verificationId) {
		return legalVettingDAO.getLVFromStage(verificationId);
	}

	@Override
	public List<LVDocument> getLVDocumentsFromStage(long verificationId) {
		return legalVettingDAO.getLVDocumentsFromStage(verificationId);
	}

	@Override
	public List<Long> getLegalVettingIds(List<Verification> verifications, String keyRef) {
		List<Long> lvIds = new ArrayList<>();
		List<LegalVetting> fiList = legalVettingDAO.getList(keyRef);
		for (LegalVetting lv : fiList) {
			for (Verification verification : verifications) {
				if (lv.getVerificationId() == verification.getId()) {
					lvIds.add(verification.getId());
				}
			}

		}
		return lvIds;
	}

	@Override
	public DocumentManager getDocumentById(long id) {
		return documentManagerDAO.getById(id);
	}

	@Override
	public List<LVDocument> getLVDocuments(String keyReference, int docTypeKey) {
		return legalVettingDAO.getLVDocuments(keyReference, docTypeKey);
	}

	@Override
	public boolean isVettingExists(long id) {
		return legalVettingDAO.isVettingExists(id);
	}

	@Override
	public List<LegalVetting> getList(String keyReference) {
		return legalVettingDAO.getList(keyReference);
	}

	@Override
	public List<LVDocument> getLVDocuments(long id) {
		return legalVettingDAO.getLVDocuments(id, "_View");
	}

	@Override
	public List<LVDocument> getDocuments(String keyReference, TableType tableType, DocumentType documentType) {
		return legalVettingDAO.getDocuments(keyReference, tableType, documentType);
	}

	@Override
	public boolean isCollateralDocumentsChanged(String collateralRef) {
		int count1 = legalVettingDAO.getLVDocumentsCount(collateralRef);
		int count2 = legalVettingDAO.getCollateralDocumentCount(collateralRef);
		if (count1 == count2) {
			return false;
		}
		return true;
	}

	@Override
	public LegalVetting getApprovedLegalVetting(long verificationId, String documentSubId) {
		return legalVettingDAO.getLegalVetting(verificationId, "_AView");
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		List<AuditDetail> auditDetails = new ArrayList<>();
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		getAuditDetails(auditHeader, method);

		LegalVetting lv = (LegalVetting) auditHeader.getAuditDetail().getModelData();
		String usrLanguage = lv.getUserDetails().getLanguage();

		// Extended field details Validation
		if (lv.getExtendedFieldRender() != null) {
			List<AuditDetail> details = lv.getAuditDetailMap().get("ExtendedFieldDetails");
			ExtendedFieldHeader extHeader = lv.getExtendedFieldHeader();

			StringBuilder tableName = new StringBuilder();
			tableName.append(CollateralConstants.VERIFICATION_MODULE);
			tableName.append("_");
			tableName.append(extHeader.getSubModuleName());
			tableName.append("_ED");

			details = extendedFieldDetailsService.vaildateDetails(details, method, usrLanguage, tableName.toString());
			auditDetails.addAll(details);
		}

		// Document details Validation
		List<DocumentDetails> docuemnts = lv.getDocuments();
		if (docuemnts != null && !docuemnts.isEmpty()) {
			List<AuditDetail> details = lv.getAuditDetailMap().get("DocumentDetails");
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

	private List<AuditDetail> saveOrUpdateDocuments(List<AuditDetail> auditDetails, LegalVetting lv, String type) {
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
			documentDetails.setLastMntBy(lv.getLastMntBy());
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
					documentDetails.setReferenceId(String.valueOf(lv.getVerificationId()));
				}
				if (documentDetails.getDocRefId() == null && documentDetails.getDocImage() != null) {
					DocumentManager documentManager = new DocumentManager();
					documentManager.setDocImage(documentDetails.getDocImage());
					documentDetails.setDocRefId(documentManagerDAO.save(documentManager));
				}
				// Pass the docRefId here to save this in place of docImage
				// column. Or add another column for now to
				// save this.
				documentDetailsDAO.save(documentDetails, type);
			}

			if (updateRecord) {
				// When a document is updated, insert another file into the
				// DocumentManager table's.
				// Get the new DocumentManager.id & set to
				// documentDetails.getDocRefId()
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
		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	private List<AuditDetail> processingLVDocumnets(List<AuditDetail> auditDetails, String type) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			LVDocument lvDocument = (LVDocument) auditDetails.get(i).getModelData();

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;

			String rcdType = "";
			String recordStatus = "";

			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				lvDocument.setRoleCode("");
				lvDocument.setNextRoleCode("");
				lvDocument.setTaskId("");
				lvDocument.setNextTaskId("");
			}

			if (lvDocument.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (lvDocument.isNewRecord()) {
				saveRecord = true;
				if (lvDocument.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					lvDocument.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (lvDocument.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					lvDocument.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (lvDocument.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					lvDocument.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (lvDocument.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (lvDocument.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (lvDocument.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (lvDocument.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = lvDocument.getRecordType();
				recordStatus = lvDocument.getRecordStatus();
				lvDocument.setRecordType("");
				lvDocument.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			if (saveRecord) {
				legalVettingDAO.saveDocuments(lvDocument, type);
			}

			if (updateRecord) {
				legalVettingDAO.updateDocuments(lvDocument, type);
			}

			if (deleteRecord) {
				legalVettingDAO.deleteLVDocuments(lvDocument, type);
			}

			if (approveRec) {
				lvDocument.setRecordType(rcdType);
				lvDocument.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(lvDocument);
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;

	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Write the required validation over hear.

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {

		List<AuditDetail> auditDetails = new ArrayList<>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<>();

		LegalVetting lv = (LegalVetting) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (lv.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		// Extended Field Details
		if (lv.getExtendedFieldRender() != null) {
			auditDetailMap.put("ExtendedFieldDetails", extendedFieldDetailsService
					.setExtendedFieldsAuditData(lv.getExtendedFieldRender(), auditTranType, method, null));
			auditDetails.addAll(auditDetailMap.get("ExtendedFieldDetails"));
		}

		// Document Details
		if (lv.getDocuments() != null && !lv.getDocuments().isEmpty()) {
			auditDetailMap.put("DocumentDetails", setDocumentDetailsAuditData(lv, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("DocumentDetails"));
		}

		// LV Document Details
		if (lv.getVettingDocuments() != null && !lv.getVettingDocuments().isEmpty()) {
			auditDetailMap.put("LVDocumentDetails", setLVDocumentDetailsAuditData(lv, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("LVDocumentDetails"));
		}

		lv.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(lv);
		auditHeader.setAuditDetails(auditDetails);

		return auditHeader;
	}

	public List<AuditDetail> setDocumentDetailsAuditData(LegalVetting legalVetting, String auditTranType,
			String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();

		DocumentDetails document = new DocumentDetails();
		String[] fields = PennantJavaUtil.getFieldDetails(document, document.getExcludeFields());

		for (int i = 0; i < legalVetting.getDocuments().size(); i++) {
			DocumentDetails documentDetails = legalVetting.getDocuments().get(i);

			if (StringUtils.isEmpty(StringUtils.trimToEmpty(documentDetails.getRecordType()))) {
				continue;
			}

			documentDetails.setWorkflowId(legalVetting.getWorkflowId());
			boolean isRcdType = false;

			if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (legalVetting.isWorkflow()) {
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

			documentDetails.setRecordStatus(legalVetting.getRecordStatus());
			documentDetails.setUserDetails(legalVetting.getUserDetails());
			documentDetails.setLastMntOn(legalVetting.getLastMntOn());
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], documentDetails.getBefImage(),
					documentDetails));
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	public List<AuditDetail> setLVDocumentDetailsAuditData(LegalVetting legalVetting, String auditTranType,
			String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();

		LVDocument lvDocument = new LVDocument();
		String[] fields = PennantJavaUtil.getFieldDetails(lvDocument, lvDocument.getExcludeFields());

		for (int i = 0; i < legalVetting.getVettingDocuments().size(); i++) {
			LVDocument lvDocumentDetails = legalVetting.getVettingDocuments().get(i);

			if (StringUtils.isEmpty(StringUtils.trimToEmpty(lvDocumentDetails.getRecordType()))) {
				continue;
			}

			lvDocumentDetails.setWorkflowId(legalVetting.getWorkflowId());
			boolean isRcdType = false;

			if (lvDocumentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				lvDocumentDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (lvDocumentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				lvDocumentDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (legalVetting.isWorkflow()) {
					isRcdType = true;
				}
			} else if (lvDocumentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				lvDocumentDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				lvDocumentDetails.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (lvDocumentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (lvDocumentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| lvDocumentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			lvDocumentDetails.setRecordStatus(legalVetting.getRecordStatus());
			lvDocumentDetails.setUserDetails(legalVetting.getUserDetails());
			lvDocumentDetails.setLastMntOn(legalVetting.getLastMntOn());
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
					lvDocumentDetails.getBefImage(), lvDocumentDetails));
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	// Method for Deleting all records related to Legal Vetting childs in
	// _Temp/Main tables depend on method type
	public List<AuditDetail> deleteChilds(LegalVetting lv, String tableType, String auditTranType) {
		List<AuditDetail> auditList = new ArrayList<>();
		// Extended field Render Details.
		List<AuditDetail> extendedDetails = lv.getAuditDetailMap().get("ExtendedFieldDetails");
		if (extendedDetails != null && !extendedDetails.isEmpty()) {
			// Table Name
			StringBuilder tableName = new StringBuilder();
			tableName.append(CollateralConstants.VERIFICATION_MODULE);
			tableName.append("_");
			tableName.append(lv.getExtendedFieldHeader().getSubModuleName());
			tableName.append("_ED");
			auditList.addAll(extendedFieldDetailsService.delete(lv.getExtendedFieldHeader(),
					String.valueOf(lv.getVerificationId()), tableName.toString(), tableType, auditTranType,
					extendedDetails));
		}

		// Document Details.
		List<AuditDetail> documentDetails = lv.getAuditDetailMap().get("DocumentDetails");
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

		// LV Document Details.
		List<AuditDetail> lvDocuments = lv.getAuditDetailMap().get("LVDocumentDetails");
		if (lvDocuments != null && !lvDocuments.isEmpty()) {
			LVDocument document = new LVDocument();
			List<LVDocument> documents = new ArrayList<>();
			String[] fields = PennantJavaUtil.getFieldDetails(document, document.getExcludeFields());
			for (int i = 0; i < lvDocuments.size(); i++) {
				document = (LVDocument) lvDocuments.get(i).getModelData();
				document.setRecordType(PennantConstants.RECORD_TYPE_CAN);
				documents.add(document);
				auditList.add(
						new AuditDetail(auditTranType, i + 1, fields[0], fields[1], document.getBefImage(), document));
			}
			legalVettingDAO.deleteLVDocumentsList(documents, tableType);
		}

		return auditList;
	}

	private void setLvFields(Verification verification) {
		LegalVetting lv = verification.getLegalVetting();
		lv = new LegalVetting();
		lv.setVerificationId(verification.getId());
		lv.setVersion(1);
		lv.setLastMntBy(verification.getLastMntBy());
		lv.setLastMntOn(verification.getLastMntOn());
		setAudit(lv);
		verification.setLegalVetting(lv);

	}

	private void setAudit(LegalVetting lv) {
		String workFlowType = ModuleUtil.getWorkflowType("LegalVetting");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getDetailsByType(workFlowType);
		WorkflowEngine engine = new WorkflowEngine(
				WorkFlowUtil.getWorkflow(workFlowDetails.getWorkFlowId()).getWorkFlowXml());

		lv.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
		lv.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		lv.setWorkflowId(workFlowDetails.getWorkflowId());
		lv.setRoleCode(workFlowDetails.getFirstTaskOwner());
		lv.setNextRoleCode(workFlowDetails.getFirstTaskOwner());
		lv.setTaskId(engine.getUserTaskId(lv.getRoleCode()));
		lv.setNextTaskId(engine.getUserTaskId(lv.getNextRoleCode()) + ";");
	}

	private void setLVDocumentWorkFlowFields(Verification verification) {
		LegalVetting legalVetting = verification.getLegalVetting();
		for (LVDocument lvDocument : verification.getVettingDocuments()) {
			lvDocument.setVersion(legalVetting.getVersion());
			lvDocument.setLastMntBy(legalVetting.getLastMntBy());
			lvDocument.setLastMntOn(legalVetting.getLastMntOn());
			lvDocument.setRecordStatus(legalVetting.getRecordStatus());
			lvDocument.setRecordType(legalVetting.getRecordType());
			lvDocument.setWorkflowId(legalVetting.getWorkflowId());
			lvDocument.setRoleCode(legalVetting.getRoleCode());
			lvDocument.setNextRoleCode(legalVetting.getNextRoleCode());
			lvDocument.setTaskId(legalVetting.getTaskId());
			lvDocument.setNextTaskId(legalVetting.getNextTaskId());
		}
	}

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public LegalVettingDAO getLegalVettingDAO() {
		return legalVettingDAO;
	}

	public void setLegalVettingDAO(LegalVettingDAO legalVettingDAO) {
		this.legalVettingDAO = legalVettingDAO;
	}

	public VerificationDAO getVerificationDAO() {
		return verificationDAO;
	}

	public void setVerificationDAO(VerificationDAO verificationDAO) {
		this.verificationDAO = verificationDAO;
	}

	public ExtendedFieldDetailsService getExtendedFieldDetailsService() {
		return extendedFieldDetailsService;
	}

	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	public DocumentDetailsDAO getDocumentDetailsDAO() {
		return documentDetailsDAO;
	}

	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	public DocumentManagerDAO getDocumentManagerDAO() {
		return documentManagerDAO;
	}

	public void setDocumentManagerDAO(DocumentManagerDAO documentManagerDAO) {
		this.documentManagerDAO = documentManagerDAO;
	}

	public CustomerDocumentDAO getCustomerDocumentDAO() {
		return customerDocumentDAO;
	}

	public void setCustomerDocumentDAO(CustomerDocumentDAO customerDocumentDAO) {
		this.customerDocumentDAO = customerDocumentDAO;
	}

	public DocumentDetailValidation getDocumentValidation() {
		if (documentValidation == null) {
			this.documentValidation = new DocumentDetailValidation(documentDetailsDAO, documentManagerDAO,
					customerDocumentDAO);
		}
		return documentValidation;
	}

	public void setDocumentValidation(DocumentDetailValidation documentValidation) {
		this.documentValidation = documentValidation;
	}

	public void setPostExteranalServiceHook(PostExteranalServiceHook postExteranalServiceHook) {
		this.postExteranalServiceHook = postExteranalServiceHook;
	}

}
