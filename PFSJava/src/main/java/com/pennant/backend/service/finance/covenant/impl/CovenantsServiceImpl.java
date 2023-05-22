/**
 * 
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
 * * FileName : CovenantsServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-08-2013 * * Modified
 * Date : 14-08-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-08-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.finance.covenant.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.covenant.CovenantsDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.covenant.Covenant;
import com.pennant.backend.model.finance.covenant.CovenantDocument;
import com.pennant.backend.model.finance.covenant.CovenantType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.customermasters.impl.CustomerDataService;
import com.pennant.backend.service.finance.covenant.CovenantsService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.model.dms.DMSModule;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>FinancePurposeDetail</b>.<br>
 * 
 */
public class CovenantsServiceImpl extends GenericService<Covenant> implements CovenantsService {
	private static final Logger logger = LogManager.getLogger(CovenantsServiceImpl.class);

	private CovenantsDAO covenantsDAO;
	private FinanceMainDAO financeMainDAO;
	private FinanceTypeDAO financeTypeDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private DocumentDetailsDAO documentDetailsDAO;
	private CustomerDataService customerDataService;

	public CovenantsServiceImpl() {
		super();
	}

	@Override
	public List<Covenant> getCovenants(String finReference, String module, TableType tableType) {
		List<Covenant> covenants = covenantsDAO.getCovenants(finReference, module, tableType);

		if (CollectionUtils.isEmpty(covenants)) {
			return covenants;
		}

		for (Covenant covenant : covenants) {
			List<CovenantDocument> covenantDocuments = covenantsDAO.getCovenantDocuments(covenant.getId(), tableType);

			if (CollectionUtils.isEmpty(covenantDocuments)) {
				continue;
			}

			for (CovenantDocument covenantDocument : covenantDocuments) {
				covenantDocument.setDocumentDetail(
						documentDetailsDAO.getDocumentDetails(covenantDocument.getDocumentId(), tableType.getSuffix()));
			}

			covenant.setCovenantDocuments(covenantDocuments);
		}

		return covenants;
	}

	@Override
	public List<AuditDetail> doProcess(List<Covenant> covenants, TableType tableType, String tranType,
			boolean isApproveRcd, int docSize) {
		List<AuditDetail> auditDetails = new ArrayList<>();

		auditDetails.addAll(processCovenants(covenants, tableType, tranType, isApproveRcd));

		List<DocumentDetails> documents = new ArrayList<>();

		for (Covenant covenant : covenants) {// if covenants tab is not available in loan queue below list is getting
												// empty
			if (CollectionUtils.isNotEmpty(covenant.getDocumentDetails())) {
				for (DocumentDetails document : covenant.getDocumentDetails()) {
					document.setLastMntBy(covenant.getLastMntBy());
					document.setLastMntOn(covenant.getLastMntOn());
					document.setReferenceId(covenant.getKeyReference());
					document.setFinReference(covenant.getKeyReference());
					documents.add(document);
				}
			} else if (CollectionUtils.isNotEmpty(covenant.getCovenantDocuments())) {// we are preparing document list
																						// by using covenants doc
				for (CovenantDocument covenantDocument : covenant.getCovenantDocuments()) {
					DocumentDetails documentDetail = covenantDocument.getDocumentDetail();
					if (documentDetail != null) {
						documentDetail.setLastMntBy(covenant.getLastMntBy());
						documentDetail.setLastMntOn(covenant.getLastMntOn());
						documentDetail.setDocName(covenantDocument.getDocName());
						documentDetail.setReferenceId(covenant.getKeyReference());
						documentDetail.setFinReference(covenant.getKeyReference());
						documents.add(documentDetail);
					}
				}
			}
		}

		auditDetails.addAll(processDocumentDetails(documents, tableType, tranType, isApproveRcd, docSize));

		Date nextFrequencyDate = null;
		Date frequencyDate = null;
		String frequence = null;

		List<AuditDetail> covenantDocAudit = new ArrayList<>();
		for (Covenant covenant : covenants) {
			frequence = covenant.getFrequency();
			nextFrequencyDate = covenant.getNextFrequencyDate();
			for (CovenantDocument covenantDocument : covenant.getCovenantDocuments()) {
				frequencyDate = covenantDocument.getFrequencyDate();
				covenantDocument.setCovenantId(covenant.getId());
				if (covenantDocument.getDocumentDetail() != null) {
					if (covenantDocument.getDocumentId() == null || covenantDocument.getDocumentId() == 0
							|| covenantDocument.getDocumentId() == Long.MIN_VALUE) {
						covenantDocument.setDocumentId(covenantDocument.getDocumentDetail().getDocId());
					}

				}

				if (nextFrequencyDate != null && frequencyDate != null) {
					nextFrequencyDate = DateUtil.getDatePart(nextFrequencyDate);
					frequencyDate = DateUtil.getDatePart(frequencyDate);
					if (DateUtil.compare(nextFrequencyDate, nextFrequencyDate) == 0) {
						if ("M".equals(frequence)) {
							frequencyDate = DateUtil.addMonths(frequencyDate, 1);
						} else if ("Q".equals(frequence)) {
							frequencyDate = DateUtil.addMonths(frequencyDate, 3);
						} else if ("H".equals(frequence)) {
							frequencyDate = DateUtil.addMonths(frequencyDate, 6);
						} else if ("A".equals(frequence)) {
							frequencyDate = DateUtil.addMonths(frequencyDate, 12);
						}
					}

					covenant.setNextFrequencyDate(frequencyDate);
				}
			}

			processDocuments(covenantDocAudit, covenant.getCovenantDocuments(), tableType, tranType, isApproveRcd);
		}

		// auditDetails.addAll(auditDetails);
		return auditDetails;

	}

	@Override
	public List<AuditDetail> saveOrUpdate(List<Covenant> covenants, TableType tableType, String auditTranType) {
		return doProcess(covenants, tableType, auditTranType, false, 0);
	}

	@Override
	public List<AuditDetail> processCovenants(List<Covenant> covenants, TableType tableType, String tranType,
			boolean isApproveRcd) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();

		if (CollectionUtils.isEmpty(covenants)) {
			return auditDetails;
		}

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		int i = 0;
		Covenant object = new Covenant();
		String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());

		for (Covenant covenant : covenants) {
			if (StringUtils.isEmpty(StringUtils.trimToEmpty(covenant.getRecordType()))) {
				continue;
			}

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = isApproveRcd;
			String rcdType = "";
			String recordStatus = "";

			if (StringUtils.isEmpty(tableType.name())) {
				approveRec = true;
				covenant.setRoleCode("");
				covenant.setNextRoleCode("");
				covenant.setTaskId("");
				covenant.setNextTaskId("");
			}

			covenant.setWorkflowId(0);
			if (StringUtils.equalsIgnoreCase(covenant.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (covenant.isNewRecord()) {
				saveRecord = true;
				if (PennantConstants.RCD_ADD.equalsIgnoreCase(covenant.getRecordType())) {
					covenant.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(covenant.getRecordType())) {
					covenant.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(covenant.getRecordType())) {
					covenant.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (StringUtils.equalsIgnoreCase(covenant.getRecordType(), (PennantConstants.RECORD_TYPE_NEW))) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (StringUtils.equalsIgnoreCase(covenant.getRecordType(), (PennantConstants.RECORD_TYPE_UPD))) {
				updateRecord = true;
			} else if (StringUtils.equalsIgnoreCase(covenant.getRecordType(), (PennantConstants.RECORD_TYPE_DEL))) {
				if (approveRec) {
					deleteRecord = true;
				} else if (covenant.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = covenant.getRecordType();
				recordStatus = covenant.getRecordStatus();
				covenant.setRecordType("");
				covenant.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				covenantsDAO.save(covenant, tableType);
			}

			if (updateRecord) {
				covenantsDAO.update(covenant, tableType);
			}

			if (deleteRecord) {
				covenantsDAO.delete(covenant, tableType);
			}

			if (approveRec) {
				covenant.setRecordType(rcdType);
				covenant.setRecordStatus(recordStatus);
			}

			auditDetails.add(new AuditDetail(tranType, ++i, fields[0], fields[1], covenant.getBefImage(), covenant));
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	private List<AuditDetail> processDocumentDetails(List<DocumentDetails> documents, TableType tableType,
			String tranType, boolean isApproveRcd, int docSize) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<>();

		if (CollectionUtils.isEmpty(documents)) {
			return auditDetails;
		}

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = isApproveRcd;

		int i = docSize;
		DocumentDetails object = new DocumentDetails();
		String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());

		for (DocumentDetails document : documents) {
			if (document.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				document.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				document.setNewRecord(true);
			} else if (document.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				document.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			} else if (document.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				document.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			if (StringUtils.isBlank(document.getRecordType())) {
				continue;
			}

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			boolean isTempRecord = false;
			if (StringUtils.isEmpty(tranType) || tranType.equals(PennantConstants.PREAPPROVAL_TABLE_TYPE)
					|| "A".equals(tranType)) {
				approveRec = true;
				document.setRoleCode("");
				document.setNextRoleCode("");
				document.setTaskId("");
				document.setNextTaskId("");
			}

			document.setWorkflowId(0);

			if (DocumentCategories.CUSTOMER.getKey().equals(document.getCategoryCode())) {
				approveRec = true;
			}

			if (document.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
				isTempRecord = true;
			} else if (document.isNewRecord()) {
				saveRecord = true;
				if (document.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					document.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (document.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					document.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (document.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					document.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (document.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (document.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (document.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (document.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = document.getRecordType();
				recordStatus = document.getRecordStatus();
				document.setRecordType("");
				document.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			if (saveRecord) {
				saveDocument(DMSModule.FINANCE, DMSModule.COVENANT, document);
				documentDetailsDAO.save(document, tableType.getSuffix());
			}

			if (updateRecord) {
				saveDocument(DMSModule.FINANCE, DMSModule.COVENANT, document);
				documentDetailsDAO.update(document, tableType.getSuffix());
			}

			if (deleteRecord && ((StringUtils.isEmpty(tableType.getSuffix()) && !isTempRecord)
					|| (StringUtils.isNotEmpty(tableType.getSuffix())))) {
				if (!tableType.getSuffix().equals(PennantConstants.PREAPPROVAL_TABLE_TYPE)) {
					documentDetailsDAO.delete(document, tableType.getSuffix());
				}
			}

			if (approveRec) {
				document.setFinEvent("");
				document.setRecordType(rcdType);
				document.setRecordStatus(recordStatus);
			}

			auditDetails.add(new AuditDetail(tranType, ++i, fields[0], fields[1], document.getBefImage(), document));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public void processDocuments(List<AuditDetail> auditDetails, List<CovenantDocument> covenantDocuments,
			TableType tableType, String tranType, boolean isApproveRcd) {
		logger.debug(Literal.ENTERING);

		if (CollectionUtils.isEmpty(covenantDocuments)) {
			return;
		}

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		CovenantDocument object = new CovenantDocument();
		String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());

		for (CovenantDocument document : covenantDocuments) {
			if (StringUtils.isEmpty(StringUtils.trimToEmpty(document.getRecordType()))) {
				continue;
			}

			List<CovenantDocument> documents = new ArrayList<>();

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = isApproveRcd;
			String rcdType = "";
			String recordStatus = "";

			if (StringUtils.isEmpty(tableType.name())) {
				approveRec = true;
				document.setRoleCode("");
				document.setNextRoleCode("");
				document.setTaskId("");
				document.setNextTaskId("");
			}

			document.setWorkflowId(0);
			if (StringUtils.equalsIgnoreCase(document.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (document.isNewRecord()) {
				saveRecord = true;
				if (PennantConstants.RCD_ADD.equalsIgnoreCase(document.getRecordType())) {
					document.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(document.getRecordType())) {
					document.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(document.getRecordType())) {
					document.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (StringUtils.equalsIgnoreCase(document.getRecordType(), (PennantConstants.RECORD_TYPE_NEW))) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (StringUtils.equalsIgnoreCase(document.getRecordType(), (PennantConstants.RECORD_TYPE_UPD))) {
				updateRecord = true;
			} else if (StringUtils.equalsIgnoreCase(document.getRecordType(), (PennantConstants.RECORD_TYPE_DEL))) {
				if (approveRec) {
					deleteRecord = true;
				} else if (document.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = document.getRecordType();
				recordStatus = document.getRecordStatus();
				document.setRecordType("");
				document.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			documents.add(document);

			if (saveRecord) {
				covenantsDAO.saveDocuments(documents, tableType);
			}

			if (updateRecord) {
				covenantsDAO.updateDocuments(documents, tableType);
			}

			if (deleteRecord) {
				covenantsDAO.deleteDocuments(documents, tableType);
			}

			if (approveRec) {
				document.setRecordType(rcdType);
				document.setRecordStatus(recordStatus);
			}

			auditDetails.add(new AuditDetail(tranType, auditDetails.size() + 1, fields[0], fields[1],
					document.getBefImage(), document));
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<AuditDetail> doApprove(List<Covenant> covenants, TableType tableType, String auditTranType,
			int docSize) {
		return doProcess(covenants, tableType, auditTranType, true, docSize);
	}

	@Override
	public List<AuditDetail> delete(List<Covenant> covenants, TableType tableType, String tranType) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<>();

		if (CollectionUtils.isEmpty(covenants)) {
			return auditDetails;
		}

		CovenantDocument object = new CovenantDocument();
		String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());

		int i = 0;
		int j = 0;
		for (Covenant covenant : covenants) {
			List<CovenantDocument> documents = new ArrayList<>();
			List<DocumentDetails> documentDetails = new ArrayList<>();
			if (CollectionUtils.isNotEmpty(covenant.getCovenantDocuments())) {
				for (CovenantDocument covenantDocument : covenant.getCovenantDocuments()) {
					documents.add(covenantDocument);
					if (covenantDocument.getDocumentDetail() != null) {
						documentDetails.add(covenantDocument.getDocumentDetail());
					}
					auditDetails.add(new AuditDetail(tranType, ++j, fields[0], fields[1],
							covenantDocument.getBefImage(), covenantDocument));
				}
				covenantsDAO.deleteDocuments(documents, tableType);
				documentDetailsDAO.deleteList(documentDetails, tableType.getSuffix());
			}

		}

		Covenant parentObject = new Covenant();
		fields = PennantJavaUtil.getFieldDetails(parentObject, parentObject.getExcludeFields());

		for (Covenant covenant : covenants) {
			covenantsDAO.delete(covenant, tableType);
			fields = PennantJavaUtil.getFieldDetails(covenant, covenant.getExcludeFields());
			auditDetails.add(new AuditDetail(tranType, ++i, fields[0], fields[1], covenant.getBefImage(), covenant));
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	private List<AuditDetail> getAuditDetails(List<Covenant> covenants, String tranType, String method,
			long workFlowId) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<>();

		Covenant object = new Covenant();
		String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());

		int i = 0;
		for (Covenant covenant : covenants) {
			if ("doApprove".equals(method)
					&& !StringUtils.trimToEmpty(covenant.getRecordStatus()).equals(PennantConstants.RCD_STATUS_SAVED)) {
				if ("doApprove".equals(method) && !StringUtils.trimToEmpty(covenant.getRecordType())
						.equals(PennantConstants.RECORD_TYPE_DEL)) {
					covenant.setWorkflowId(0);
					covenant.setNewRecord(true);
				} else {
					covenant.setWorkflowId(0);
				}
			} else {
				covenant.setWorkflowId(workFlowId);
			}

			if (StringUtils.isEmpty(StringUtils.trimToEmpty(covenant.getRecordType()))) {
				continue;
			}

			boolean isRcdType = false;

			if (StringUtils.equalsIgnoreCase(covenant.getRecordType(), PennantConstants.RCD_ADD)) {
				covenant.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(covenant.getRecordType())) {
				covenant.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(covenant.getRecordType())) {
				covenant.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				covenant.setNewRecord(true);
			}

			if (!tranType.equals(PennantConstants.TRAN_WF)) {
				if (covenant.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (covenant.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| covenant.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					tranType = PennantConstants.TRAN_DEL;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}

			if (StringUtils.isNotEmpty(covenant.getRecordType())) {
				auditDetails
						.add(new AuditDetail(tranType, ++i, fields[0], fields[1], covenant.getBefImage(), covenant));
			}
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public List<AuditDetail> validate(List<Covenant> covenants, long workflowId, String method, String auditTranType,
			String usrLanguage) {
		return doValidation(covenants, workflowId, method, auditTranType, usrLanguage);
	}

	private List<AuditDetail> doValidation(List<Covenant> covenants, long workflowId, String method,
			String auditTranType, String usrLanguage) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<>();

		if (CollectionUtils.isEmpty(covenants)) {
			return auditDetails;
		}

		List<AuditDetail> list = getAuditDetails(covenants, auditTranType, method, workflowId);
		auditDetails.addAll(validateCovenant(list, method, usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public List<AuditDetail> validateCovenant(List<AuditDetail> auditDetails, String usrLanguage, String method) {
		List<AuditDetail> aAuditDetails = new ArrayList<>();
		logger.debug(Literal.ENTERING);

		for (AuditDetail auditDetail : auditDetails) {
			validate(auditDetail, usrLanguage, method);
			aAuditDetails.add(auditDetail);
		}

		return aAuditDetails;
	}

	private void validate(AuditDetail auditDetail, String usrLanguage, String method) {
		auditDetail.setErrorDetails(new ArrayList<>());
		Covenant covenant = (Covenant) auditDetail.getModelData();
		Covenant tempCovenant = null;

		if (covenant.isWorkflow()) {
			tempCovenant = covenantsDAO.getCovenant(covenant.getId(), covenant.getModule(), TableType.TEMP_TAB);
		}

		Covenant befCovenant = covenantsDAO.getCovenant(covenant.getId(), covenant.getModule(), TableType.MAIN_TAB);
		CovenantType oldCovenant = covenant.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = covenant.getKeyReference();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (covenant.isNewRecord()) {
			if (!covenant.isWorkflow()) {
				if (befCovenant != null) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else {
				if (covenant.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					if (befCovenant != null || tempCovenant != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else {
					if (befCovenant == null || tempCovenant != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			if (!covenant.isWorkflow()) {

				if (befCovenant == null) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldCovenant != null && !oldCovenant.getLastMntOn().equals(befCovenant.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, valueParm),
									usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, valueParm),
									usrLanguage));
						}
					}
				}
			} else {

				if (tempCovenant == null) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempCovenant != null && oldCovenant != null
						&& !oldCovenant.getLastMntOn().equals(tempCovenant.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !covenant.isWorkflow()) {
			auditDetail.setBefImage(befCovenant);
		}
	}

	@Override
	public FinanceDetail getFinanceDetailById(String finreference, String type, String userRole, String moduleDefiner,
			String eventCodeRef) {
		logger.debug(Literal.ENTERING);

		// Finance Details
		FinanceDetail fd = new FinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();

		FinanceMain fm = financeMainDAO.getFinanceMainByRef(finreference, type, false);
		schdData.setFinanceMain(fm);
		schdData.setFinanceType(financeTypeDAO.getFinanceTypeByID(fm.getFinType(), "_AView"));

		schdData.setFinID(fm.getFinID());
		schdData.setFinReference(finreference);

		// Finance Schedule Details
		schdData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(fm.getFinID(), type, false));

		// Finance Customer Details
		if (schdData.getFinanceMain().getCustID() != 0 && schdData.getFinanceMain().getCustID() != Long.MIN_VALUE) {
			fd.setCustomerDetails(
					customerDataService.getCustomerDetailsbyID(schdData.getFinanceMain().getCustID(), true, "_View"));
		}

		List<Covenant> covenants = covenantsDAO.getCovenants(finreference, "Loan", TableType.VIEW);

		if (CollectionUtils.isNotEmpty(covenants)) {
			for (Covenant covenant : covenants) {
				covenant.getCovenantDocuments()
						.addAll(covenantsDAO.getCovenantDocuments(covenant.getId(), TableType.VIEW));
			}
		}

		fd.setCovenants(covenants);

		return fd;
	}

	@Override
	public List<AuditDetail> validateOTC(FinanceDetail financeDetail) {
		List<AuditDetail> auditDetails = new ArrayList<>();

		List<FinAdvancePayments> finAdvancePayments = financeDetail.getAdvancePaymentsList();
		List<Covenant> covenants = financeDetail.getCovenants();

		if (CollectionUtils.isEmpty(finAdvancePayments) || CollectionUtils.isEmpty(covenants)) {
			return auditDetails;
		}

		for (FinAdvancePayments finAdvancePayment : finAdvancePayments) {
			boolean isAllowedMethod = false;
			boolean isDocumentReceived = false;
			boolean otcCovenant = false;

			for (Covenant covenant : covenants) {
				otcCovenant = covenant.isOtc();
				String repaymethods = StringUtils.trimToEmpty(covenant.getAllowedPaymentModes());

				if (otcCovenant && StringUtils.isEmpty(repaymethods)) {
					continue;
				} else if (otcCovenant) {
					for (String rpymethod : repaymethods.split(",")) {
						if (StringUtils.equals(finAdvancePayment.getPaymentType(), rpymethod)) {
							isAllowedMethod = true;
							break;
						}
					}
				}
			}

			if (otcCovenant && !isAllowedMethod) {
				for (Covenant covenant : covenants) {
					for (CovenantDocument document : covenant.getCovenantDocuments()) {
						if (document.getDocumentReceivedDate() != null) {
							isDocumentReceived = true;
							break;
						}
					}
					if (!isDocumentReceived) {
						String[] valueParm = new String[2];
						AuditDetail detail = new AuditDetail();
						valueParm[0] = finAdvancePayment.getPaymentType();
						valueParm[1] = Labels.getLabel("label_FinCovenantTypeDialog_AlwOTC.value");
						detail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41101", valueParm, null));
						auditDetails.add(detail);
						break;
					}
				}
			}
		}
		return auditDetails;
	}

	@Override
	public List<ErrorDetail> validatePDDDocuments(String finReference, List<ErrorDetail> errorDetails) {
		Date appDate = SysParamUtil.getAppDate();
		List<Covenant> list = covenantsDAO.getCovenants(finReference);

		for (Covenant covenant : list) {
			boolean oneTime = false;
			if ("O".equals(covenant.getFrequency()) || StringUtils.equals(covenant.getFrequency(), null)) {
				oneTime = true;
			}
			List<CovenantDocument> covenantDocuments = covenantsDAO.getCovenantDocuments(covenant.getId(),
					TableType.BOTH_TAB);
			if (covenant.isAllowPostPonement() && covenant.getExtendedDate() != null) {
				if (DateUtil.compare(appDate, covenant.getExtendedDate()) >= 0) {
					if (covenantDocuments.isEmpty()
							|| !isReceived(covenantDocuments, covenant.getReceivableDate(), oneTime)) {
						String[] errParam = new String[2];
						errParam[0] = StringUtils.trimToEmpty(covenant.getDocTypeName());
						errParam[1] = DateUtil.format(covenant.getExtendedDate(), DateFormat.LONG_DATE);
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("ADM004", errParam)));
					}
				}
			} else if (DateUtil.compare(appDate, covenant.getReceivableDate()) >= 0) {
				if (covenantDocuments.isEmpty()
						|| !isReceived(covenantDocuments, covenant.getReceivableDate(), oneTime)) {
					String[] errParam = new String[2];
					errParam[0] = StringUtils.trimToEmpty(covenant.getDocTypeName());
					errParam[1] = DateUtil.format(covenant.getReceivableDate(), DateFormat.LONG_DATE);
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("ADM004", errParam)));
				}

			} else if (covenant.getGraceDays() > 0) {
				if (covenant.getGraceDueDate() != null && DateUtil.compare(appDate, covenant.getGraceDueDate()) >= 0) {
					if (covenantDocuments.isEmpty()
							|| !isReceived(covenantDocuments, covenant.getNextFrequencyDate(), oneTime)) {
						String[] errParam = new String[2];
						errParam[0] = StringUtils.trimToEmpty(covenant.getDocTypeName());
						errParam[1] = DateUtil.format(covenant.getGraceDueDate(), DateFormat.LONG_DATE);
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("ADM004", errParam)));
					}
				}
			} else if (covenant.getNextFrequencyDate() != null
					&& DateUtil.compare(appDate, covenant.getNextFrequencyDate()) >= 0) {
				if (covenantDocuments.isEmpty()) {
					String[] errParam = new String[2];
					errParam[0] = StringUtils.trimToEmpty(covenant.getDocTypeName());
					errParam[1] = DateUtil.format(covenant.getNextFrequencyDate(), DateFormat.LONG_DATE);
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("ADM004", errParam)));
				} else {
					int count = 0;
					for (CovenantDocument document : covenantDocuments) {

						if (document.getFrequencyDate() == null) {
							continue;
						}

						if (DateUtil.compare(covenant.getNextFrequencyDate(), document.getFrequencyDate()) == 0
								&& document.getDocumentReceivedDate() != null) {
							count++;
							break;
						}
					}
					if (count <= 0) {
						String[] errParam = new String[2];
						errParam[0] = StringUtils.trimToEmpty(covenant.getDocTypeName());
						errParam[1] = DateUtil.format(covenant.getNextFrequencyDate(), DateFormat.LONG_DATE);
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("ADM004", errParam)));
					}
				}
			}

			for (CovenantDocument document : covenantDocuments) {
				if (document.getFrequencyDate() != null && (DateUtil.compare(document.getFrequencyDate(),
						covenant.getReceivableDate()) == 0
						|| DateUtil.compare(document.getFrequencyDate(), covenant.getNextFrequencyDate()) == 0)) {
					continue;
				}
				if (document.getFrequencyDate() != null
						&& document.getFrequencyDate().compareTo(covenant.getNextFrequencyDate()) <= 0
						&& document.getDocumentReceivedDate() == null) {
					String[] errParam = new String[2];
					errParam[0] = StringUtils.trimToEmpty(covenant.getDocTypeName());
					errParam[1] = DateUtil.format(document.getFrequencyDate(), DateFormat.LONG_DATE);
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("ADM004", errParam)));
					break;
				}
			}
		}
		return errorDetails;
	}

	@Override
	public void deleteDocumentByDocumentId(Long documentId, String tableType) {
		covenantsDAO.deleteDocumentByDocumentId(documentId, tableType);
	}

	private boolean isReceived(List<CovenantDocument> covenantDocuments, Date receivableDate, boolean oneTime) {
		for (CovenantDocument covenantDocument : covenantDocuments) {
			if (covenantDocument.getFrequencyDate() != null
					&& covenantDocument.getFrequencyDate().compareTo(receivableDate) == 0) {
				if (covenantDocument.getDocumentReceivedDate() != null) {
					return true;
				}
			}
			if (oneTime && covenantDocument.getDocumentReceivedDate() != null) {
				return true;
			}
		}
		return false;
	}

	public void setCovenantsDAO(CovenantsDAO covenantsDAO) {
		this.covenantsDAO = covenantsDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public DocumentDetailsDAO getDocumentDetailsDAO() {
		return documentDetailsDAO;
	}

	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	public CovenantsDAO getCovenantsDAO() {
		return covenantsDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}

	@Autowired
	public void setCustomerDataService(CustomerDataService customerDataService) {
		this.customerDataService = customerDataService;
	}

}