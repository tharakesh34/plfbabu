package com.pennanttech.pff.documents.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.covenant.CovenantsDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.covenant.Covenant;
import com.pennant.backend.model.finance.covenant.CovenantDocument;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.covenant.CovenantsService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.documents.dao.DocumentDao;
import com.pennanttech.pff.documents.model.Document;
import com.pennanttech.pff.documents.model.DocumentStatus;
import com.pennanttech.pff.documents.model.DocumentStatusDetail;
import com.pennanttech.pff.external.service.ExternalFinanceSystemService;

public class DocumentServiceImpl extends GenericService<DocumentStatus> implements DocumentService {
	private static Logger logger = LogManager.getLogger(DocumentServiceImpl.class);

	private DocumentDao documentDao;
	private CovenantsDAO covenantsDAO;
	private AuditHeaderDAO auditHeaderDAO;
	private CovenantsService covenantsService;

	@Autowired(required = false)
	private ExternalFinanceSystemService externalFinanceSystemService;

	@Override
	public DocumentStatus getDocumentStatus(String finReference) {
		DocumentStatus ds = documentDao.getDocumentStatus(finReference);

		if (ds == null) {
			ds = new DocumentStatus();
			ds.setFinReference(finReference);
			ds.setNewRecord(true);
		}

		List<Document> documents = documentDao.getDocuments(finReference);
		List<Document> listDocs = new ArrayList<Document>();

		listDocs.addAll(documents);

		List<Long> docIdList = new ArrayList<>();
		for (Document document : documents) {
			docIdList.add(document.getId());
		}

		List<DocumentStatusDetail> list = documentDao.getDocumentStatus(docIdList);
		for (Document document : documents) {
			for (DocumentStatusDetail dsd : list) {
				Document doc = dsd.getDocument();
				if (document.getId() == doc.getId()) {
					dsd.setDocId(document.getId());
					doc.setCustId(document.getCustId());
					doc.setFinReference(document.getFinReference());
					doc.setDocType(document.getDocType());
					doc.setDocCategory(document.getDocCategory());
					doc.setDocName(document.getDocName());
					listDocs.remove(document);
				}
			}
		}

		for (Document document : listDocs) {
			DocumentStatusDetail dsd = new DocumentStatusDetail();
			Document doc = dsd.getDocument();
			dsd.setNewRecord(true);
			dsd.setDocId(document.getId());
			doc.setCustId(document.getCustId());
			doc.setFinReference(document.getFinReference());
			doc.setDocType(document.getDocType());
			doc.setDocCategory(document.getDocCategory());
			doc.setDocName(document.getDocName());
			list.add(dsd);
		}

		if (list.isEmpty()) {
			for (Document document : documents) {
				DocumentStatusDetail dsd = new DocumentStatusDetail();
				Document doc = dsd.getDocument();
				dsd.setNewRecord(true);
				dsd.setDocId(document.getId());
				doc.setCustId(document.getCustId());
				doc.setFinReference(document.getFinReference());
				doc.setDocType(document.getDocType());
				doc.setDocCategory(document.getDocCategory());
				doc.setDocName(document.getDocName());
				list.add(dsd);
			}
		}

		ds.setDsList(list);
		return ds;
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		DocumentStatusDetail ds = (DocumentStatusDetail) auditHeader.getAuditDetail().getModelData();
		documentDao.delete(ds, TableType.MAIN_TAB);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();
		auditHeader = businessValidation(auditHeader, PennantConstants.method_saveOrUpdate);

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		TableType tableType = TableType.MAIN_TAB;
		DocumentStatus ds = (DocumentStatus) auditHeader.getAuditDetail().getModelData();

		if (ds.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (ds.isNewRecord()) {
			ds.setId(Long.valueOf(documentDao.save(ds, tableType)));
			auditHeader.getAuditDetail().setModelData(ds);
			auditHeader.setAuditReference(String.valueOf(ds.getId()));
		} else {
			documentDao.update(ds, tableType);
		}

		// Retrieving List of Audit Details For check list detail related modules
		if (!ds.getDsList().isEmpty()) {
			List<AuditDetail> details = ds.getAuditDetailMap().get("DocumentStatusDetail");
			details = processDocuments(details, tableType, ds.getId());
			auditDetails.addAll(details);
		}

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private List<AuditDetail> processDocuments(List<AuditDetail> auditDetails, TableType type, long headerId) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			DocumentStatusDetail dsd = (DocumentStatusDetail) auditDetails.get(i).getModelData();

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			dsd.setHeaderId(headerId);
			if (StringUtils.isEmpty(type.getSuffix())) {
				approveRec = true;
				dsd.setVersion(dsd.getVersion() + 1);
				dsd.setRoleCode("");
				dsd.setNextRoleCode("");
				dsd.setTaskId("");
				dsd.setNextTaskId("");
			}

			dsd.setWorkflowId(0);

			if (PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(dsd.getRecordType())) {
				deleteRecord = true;
			} else if (dsd.isNewRecord()) {
				saveRecord = true;
				if (PennantConstants.RCD_ADD.equalsIgnoreCase(dsd.getRecordType())) {
					dsd.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(dsd.getRecordType())) {
					dsd.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(dsd.getRecordType())) {
					dsd.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(dsd.getRecordType())) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (PennantConstants.RECORD_TYPE_UPD.equalsIgnoreCase(dsd.getRecordType())) {
				updateRecord = true;
			} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(dsd.getRecordType())) {
				if (approveRec) {
					deleteRecord = true;
				} else if (dsd.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = dsd.getRecordType();
				recordStatus = dsd.getRecordStatus();
				dsd.setRecordType("");
				dsd.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

			}
			if (saveRecord) {
				documentDao.save(dsd, type);
			}

			if (updateRecord) {
				documentDao.update(dsd, type);
			}

			if (deleteRecord) {
				documentDao.delete(dsd, type);
			}

			if (approveRec) {
				dsd.setRecordType(rcdType);
				dsd.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(dsd);
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	private List<AuditDetail> setDocumentStatusDetail(DocumentStatus ds, String auditTranType, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();
		String[] fields = PennantJavaUtil.getFieldDetails(new DocumentStatusDetail(),
				new DocumentStatusDetail().getExcludeFields());

		for (int i = 0; i < ds.getDsList().size(); i++) {
			DocumentStatusDetail dsd = ds.getDsList().get(i);

			// Skipping the process of current iteration when the child was not modified to avoid unnecessary processing
			if (StringUtils.isEmpty(dsd.getRecordType())) {
				continue;
			}

			dsd.setWorkflowId(ds.getWorkflowId());
			dsd.setHeaderId(ds.getId());

			boolean isRcdType = false;

			if (PennantConstants.RCD_ADD.equalsIgnoreCase(dsd.getRecordType())) {
				dsd.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(dsd.getRecordType())) {
				dsd.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (ds.isWorkflow()) {
					isRcdType = true;
				}
			} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(dsd.getRecordType())) {
				dsd.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if (PennantConstants.method_saveOrUpdate.equals(method) && isRcdType) {
				dsd.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(dsd.getRecordType())) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(dsd.getRecordType())
						|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(dsd.getRecordType())) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			dsd.setRecordStatus(ds.getRecordStatus());
			dsd.setUserDetails(ds.getUserDetails());
			dsd.setLastMntOn(ds.getLastMntOn());
			dsd.setLastMntBy(ds.getLastMntBy());
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], dsd.getBefImage(), dsd));
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();
		DocumentStatus ds = (DocumentStatus) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if ((PennantConstants.method_saveOrUpdate.equals(method) || PennantConstants.method_doApprove.equals(method)
				|| PennantConstants.method_doReject.equals(method)) && ds.isWorkflow()) {
			auditTranType = PennantConstants.TRAN_WF;
		}

		if (ds.getDsList().isEmpty()) {
			return auditHeader;
		}

		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<>();
		auditDetailMap.put("DocumentStatusDetail", setDocumentStatusDetail(ds, auditTranType, method));
		auditDetails.addAll(auditDetailMap.get("DocumentStatusDetail"));

		ds.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(ds);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		AuditDetail auditDetail = auditHeader.getAuditDetail();
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader = nextProcess(auditHeader);
		return auditHeader;
	}

	@Override
	public AuditHeader doApprove(AuditHeader ah) {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<>();

		ah = businessValidation(ah, PennantConstants.method_doApprove);
		if (!ah.isNextProcess()) {
			return ah;
		}
		TableType tableType = TableType.MAIN_TAB;
		DocumentStatus ds = new DocumentStatus();
		BeanUtils.copyProperties(ah.getAuditDetail().getModelData(), ds);

		if (ds.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			List<AuditDetail> listDeletion = listDeletion(ds, tableType, ah.getAuditTranType());
			documentDao.delete(ds, tableType);
			auditDetails.addAll(listDeletion);
		} else {
			ds.setRoleCode("");
			ds.setNextRoleCode("");
			ds.setTaskId("");
			ds.setNextTaskId("");
			ds.setWorkflowId(0);

			if (ds.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				ds.setRecordType("");
				documentDao.save(ds, tableType);
			}
		}

		// Retrieving List of Audit Details For checkList details modules
		if (!ds.getDsList().isEmpty()) {
			List<AuditDetail> details = ds.getAuditDetailMap().get("DocumentStatusDetail");
			details = processDocuments(details, tableType, ds.getId());
			auditDetails.addAll(details);
		}

		markCovenants(auditDetails, ds);

		notifyDocStatus(ds);

		ah.setAuditDetails(getListAuditDetails(listDeletion(ds, TableType.TEMP_TAB, ah.getAuditTranType())));
		ah.setAuditTranType(tranType);
		ah.getAuditDetail().setAuditTranType(tranType);
		ah.getAuditDetail().setModelData(ds);

		auditHeaderDAO.addAudit(ah);

		documentDao.delete(ds, TableType.TEMP_TAB);
		ah.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(ah);

		logger.debug(Literal.LEAVING);

		return ah;
	}

	private void notifyDocStatus(DocumentStatus ds) {
		logger.debug(Literal.ENTERING);

		List<DocumentStatusDetail> list = new ArrayList<>();

		for (DocumentStatusDetail dsd : ds.getDsList()) {
			if (dsd.getProcessed() == 0 && StringUtils.isNotBlank(dsd.getStatus())) {
				list.add(dsd);
			}
		}

		String finReference = ds.getFinReference();

		if (list.isEmpty()) {
			return;
		}

		if (externalFinanceSystemService != null) {
			externalFinanceSystemService.getDocumentStatusList(list, finReference);
			documentDao.updateStaus(list);

		}

		logger.debug(Literal.LEAVING);

	}

	private void markCovenants(List<AuditDetail> ad, DocumentStatus ds) {
		logger.info(Literal.ENTERING);

		List<String> list = new ArrayList<>();
		Date appDate = SysParamUtil.getAppDate();

		for (DocumentStatusDetail dsd : ds.getDsList()) {
			if (StringUtils.isNotBlank(dsd.getCovenants())) {

				for (String key : dsd.getCovenants().split(",")) {
					list.add(key);
				}

			}
		}

		if (list.isEmpty()) {
			return;
		}

		List<Covenant> covenantsList = covenantsDAO.getCovenants(list, TableType.VIEW);

		List<Long> covList = new ArrayList<>();
		for (Covenant cov : covenantsList) {
			covList.add(cov.getId());
		}

		List<CovenantDocument> cdList = covenantsDAO.getCovenantDocuments(covList, TableType.VIEW);

		List<CovenantDocument> covDocList = new ArrayList<>();

		if (cdList != null && cdList.size() > 0) {

			covenantsDAO.updateCovenantDocuments(cdList, TableType.TEMP_TAB);
			covenantsDAO.updateCovenantDocuments(cdList, TableType.MAIN_TAB);

			for (CovenantDocument cdv : cdList) {
				if (covenantsList != null && covenantsList.size() > 0) {
					Predicate<Covenant> condition = cov -> cov.getId() == cdv.getCovenantId();
					covenantsList.removeIf(condition);
				}
			}
		}

		for (Covenant covenant : covenantsList) {
			CovenantDocument cd = new CovenantDocument();
			cd.setVersion(1);
			cd.setRecordType(PennantConstants.RCD_ADD);

			cd.setCovenantId(covenant.getId());
			cd.setCovenantType("PDD");
			cd.setReceivableDate(covenant.getReceivableDate());
			cd.setFrequencyDate(cd.getFrequencyDate());
			cd.setDocumentReceivedDate(appDate);
			cd.setNewRecord(true);
			for (DocumentStatusDetail dsd : ds.getDsList()) {
				if (dsd.getCovenants().contains(String.valueOf(cd.getCovenantId()))) {
					cd.setDocumentId(dsd.getDocId());
					break;
				}
			}
			covDocList.add(cd);

		}

		List<Long> covenantList = new ArrayList<>();
		for (CovenantDocument covDoc : covDocList) {
			covenantList.add(covDoc.getCovenantId());
		}

		if (!covenantList.isEmpty()) {

			List<Covenant> covTempList = covenantsDAO.getCovenantsId(covenantList, TableType.TEMP_TAB);
			List<Covenant> covMainList = covenantsDAO.getCovenantsId(covenantList, TableType.MAIN_TAB);

			List<CovenantDocument> covDocTempList = new ArrayList<>();
			List<CovenantDocument> covDocMainList = new ArrayList<>();

			if (!covTempList.isEmpty()) {
				for (Covenant covenant : covTempList) {
					CovenantDocument cd = new CovenantDocument();
					cd.setVersion(1);
					cd.setRecordType(PennantConstants.RCD_ADD);
					cd.setCovenantId(covenant.getId());
					cd.setCovenantType("PDD");
					cd.setReceivableDate(covenant.getReceivableDate());
					cd.setFrequencyDate(cd.getFrequencyDate());
					cd.setDocumentReceivedDate(appDate);
					cd.setNewRecord(true);
					for (DocumentStatusDetail dsd : ds.getDsList()) {
						if (dsd.getCovenants().contains(String.valueOf(cd.getCovenantId()))) {
							cd.setDocumentId(dsd.getDocId());
							break;
						}
					}
					covDocTempList.add(cd);
				}
				covenantsService.processDocuments(ad, covDocTempList, TableType.TEMP_TAB, PennantConstants.TRAN_ADD,
						false);
			}

			if (!covMainList.isEmpty()) {
				for (Covenant covenant : covMainList) {
					CovenantDocument cd = new CovenantDocument();
					cd.setVersion(1);
					cd.setRecordType(PennantConstants.RCD_ADD);
					cd.setCovenantId(covenant.getId());
					cd.setCovenantType("PDD");
					cd.setReceivableDate(covenant.getReceivableDate());
					cd.setFrequencyDate(cd.getFrequencyDate());
					cd.setDocumentReceivedDate(appDate);
					cd.setNewRecord(true);
					for (DocumentStatusDetail dsd : ds.getDsList()) {
						if (dsd.getCovenants().contains(String.valueOf(cd.getCovenantId()))) {
							cd.setDocumentId(dsd.getDocId());
							break;
						}
					}
					covDocMainList.add(cd);
				}
				covenantsService.processDocuments(ad, covDocMainList, TableType.MAIN_TAB, "", true);
			}
		}

		logger.info(Literal.LEAVING);

	}

	private List<AuditDetail> getListAuditDetails(List<AuditDetail> list) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetailsList = new ArrayList<>();

		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				String transType = "";
				String rcdType = "";
				DocumentStatusDetail dsd = (DocumentStatusDetail) ((AuditDetail) list.get(i)).getModelData();
				rcdType = dsd.getRecordType();

				if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(rcdType)) {
					transType = PennantConstants.TRAN_ADD;
				} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(rcdType)
						|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(rcdType)) {
					transType = PennantConstants.TRAN_DEL;
				} else {
					transType = PennantConstants.TRAN_UPD;
				}

				if (StringUtils.isNotEmpty(transType)) {
					// check and change below line for Complete code
					auditDetailsList.add(new AuditDetail(transType, ((AuditDetail) list.get(i)).getAuditSeq(),
							dsd.getBefImage(), dsd));
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return auditDetailsList;
	}

	private List<AuditDetail> listDeletion(DocumentStatus ds, TableType tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditList = new ArrayList<>();
		if (ds.getDsList().isEmpty()) {
			return auditList;
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new DocumentStatusDetail());
		for (int i = 0; i < ds.getDsList().size(); i++) {
			DocumentStatusDetail dsd = ds.getDsList().get(i);
			if (!StringUtils.isEmpty(dsd.getRecordType()) || StringUtils.isEmpty(tableType.getSuffix())) {
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], dsd.getBefImage(), dsd));
			}
		}
		DocumentStatusDetail child = ds.getDsList().get(0);
		documentDao.deleteChildrens(child.getHeaderId(), tableType);

		logger.debug(Literal.LEAVING);
		return auditList;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}
		DocumentStatus ds = (DocumentStatus) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		documentDao.deleteChildrens(ds.getId(), TableType.TEMP_TAB);
		documentDao.delete(ds, TableType.TEMP_TAB);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public int resetDocumentStatus(long docId) {
		DocumentStatusDetail ds = documentDao.getDocumentStatusByDocId(docId, "");

		if (ds == null || ds.getProcessed() == 0 || !ds.getStatus().equals("R")) {
			return 0;
		}

		DocumentStatusDetail befImage = new DocumentStatusDetail();

		BeanUtils.copyProperties(ds, befImage);
		ds.setBefImage(befImage);

		AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_UPD, 1, ds.getBefImage(), ds);
		AuditHeader ah = new AuditHeader(String.valueOf(ds.getId()), null, null, null, auditDetail, null,
				new HashMap<>());

		ds.setStatus("");
		ds.setRemarks("");
		ds.setProcessed(0);
		int count = documentDao.update(ds, TableType.MAIN_TAB);

		auditHeaderDAO.addAudit(ah);
		return count;
	}

	public void setDocumentDao(DocumentDao documentDao) {
		this.documentDao = documentDao;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setCovenantsDAO(CovenantsDAO covenantsDAO) {
		this.covenantsDAO = covenantsDAO;
	}

	public void setCovenantsService(CovenantsService covenantsService) {
		this.covenantsService = covenantsService;
	}

	public void setExternalFinanceSystemService(ExternalFinanceSystemService externalFinanceSystemService) {
		this.externalFinanceSystemService = externalFinanceSystemService;
	}

}
