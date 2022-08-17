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
 * * FileName : TdsReceivableServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-09-2020 * *
 * Modified Date : 03-09-2020 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-09-2020 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.tds.receivables.impl;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.tds.receivables.TdsReceivableDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.tds.receivables.TdsReceivable;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.tds.receivables.TdsReceivableService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.model.dms.DMSModule;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>TdsReceivable</b>.<br>
 */
public class TdsReceivableServiceImpl extends GenericService<TdsReceivable> implements TdsReceivableService {
	private static final Logger logger = LogManager.getLogger(TdsReceivableServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private TdsReceivableDAO tdsReceivableDAO;
	private DocumentDetailsDAO documentDetailsDAO;

	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	/**
	 * @param auditHeaderDAO the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	/**
	 * @param tdsReceivableDAO the tdsReceivableDAO to set
	 */
	public void setTdsReceivableDAO(TdsReceivableDAO tdsReceivableDAO) {
		this.tdsReceivableDAO = tdsReceivableDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * TDS_RECEIVABLES/TDS_RECEIVABLES_Temp by using TDS_RECEIVABLESDAO's save method b) Update the Record in the table.
	 * based on the module workFlow Configuration. by using TDS_RECEIVABLESDAO's update method 3) Audit the record in to
	 * AuditHeader and AdtTDS_RECEIVABLES by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		TdsReceivable tdsReceivable = (TdsReceivable) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;

		if (tdsReceivable.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		saveDocument(tdsReceivable, tableType);

		if (tdsReceivable.isNew()) {
			tdsReceivable.setId(Long.parseLong(tdsReceivableDAO.save(tdsReceivable, tableType)));
			auditHeader.getAuditDetail().setModelData(tdsReceivable);
			auditHeader.setAuditReference(String.valueOf(tdsReceivable.getId()));
		} else {
			tdsReceivableDAO.update(tdsReceivable, tableType);
		}

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * TDS_RECEIVABLES by using TDS_RECEIVABLESDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtTDS_RECEIVABLES by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		TdsReceivable tdsReceivable = (TdsReceivable) auditHeader.getAuditDetail().getModelData();
		tdsReceivableDAO.delete(tdsReceivable, TableType.MAIN_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getTDS_RECEIVABLES fetch the details by using TDS_RECEIVABLESDAO's getTDS_RECEIVABLESById method.
	 * 
	 * @param iD iD of the TdsReceivable.
	 * @return TDS_RECEIVABLES
	 */
	@Override
	public TdsReceivable getTdsReceivable(long id, TableType type) {
		return tdsReceivableDAO.getTdsReceivable(id, type);
	}

	/**
	 * getApprovedTDS_RECEIVABLESById fetch the details by using TDS_RECEIVABLESDAO's getTDS_RECEIVABLESById method .
	 * with parameter id and type as blank. it fetches the approved records from the TDS_RECEIVABLES.
	 * 
	 * @param iD iD of the TdsReceivable. (String)
	 * @return TDS_RECEIVABLES
	 */
	public TdsReceivable getApprovedTdsReceivable(long id) {
		return tdsReceivableDAO.getTdsReceivable(id, TableType.MAIN_TAB);
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getTdsReceivableDAO().delete with
	 * parameters tdsReceivable,"" b) NEW Add new record in to main table by using getTdsReceivableDAO().save with
	 * parameters tdsReceivable,"" c) EDIT Update record in the main table by using getTdsReceivableDAO().update with
	 * parameters tdsReceivable,"" 3) Delete the record from the workFlow table by using getTdsReceivableDAO().delete
	 * with parameters tdsReceivable,"_Temp" 4) Audit the record in to AuditHeader and AdtTDS_RECEIVABLES by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtTDS_RECEIVABLES
	 * by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		TdsReceivable tdsReceivable = new TdsReceivable();
		DocumentDetails dd = new DocumentDetails();

		BeanUtils.copyProperties((TdsReceivable) auditHeader.getAuditDetail().getModelData(), tdsReceivable);

		dd.setFinReference(tdsReceivable.getTanNumber());
		dd.setDocName(tdsReceivable.getUploadCertificate());

		deleteDocument(tdsReceivable, dd);

		tdsReceivableDAO.delete(tdsReceivable, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(tdsReceivable.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(tdsReceivableDAO.getTdsReceivable(tdsReceivable.getId(), TableType.MAIN_TAB));
		}

		if (tdsReceivable.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			tdsReceivableDAO.delete(tdsReceivable, TableType.MAIN_TAB);
			documentDetailsDAO.delete(dd, TableType.MAIN_TAB.getSuffix());
		} else {
			tdsReceivable.setRoleCode("");
			tdsReceivable.setNextRoleCode("");
			tdsReceivable.setTaskId("");
			tdsReceivable.setNextTaskId("");
			tdsReceivable.setWorkflowId(0);
			dd.setDocImage(tdsReceivable.getDocumentDetails().getDocImage());
			dd.setDoctype(tdsReceivable.getDocumentDetails().getDoctype());
			dd.setDocCategory(tdsReceivable.getDocumentDetails().getDocCategory());
			saveDocument(DMSModule.FINANCE, DMSModule.TAN, dd);
			documentDetailsDAO.save(dd, TableType.MAIN_TAB.getSuffix());
			tdsReceivable.setDocID(dd.getDocId());
			tdsReceivable.setStatus(null);

			if (tdsReceivable.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				tdsReceivable.setRecordType("");
				tdsReceivableDAO.save(tdsReceivable, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				tdsReceivable.setRecordType("");
				tdsReceivableDAO.update(tdsReceivable, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(tdsReceivable);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getTdsReceivableDAO().delete with parameters tdsReceivable,"_Temp" 3) Audit the record in
	 * to AuditHeader and AdtTDS_RECEIVABLES by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		DocumentDetails dd = new DocumentDetails();
		TdsReceivable tdsReceivable = (TdsReceivable) auditHeader.getAuditDetail().getModelData();

		deleteDocument(tdsReceivable, dd);

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		tdsReceivableDAO.delete(tdsReceivable, TableType.TEMP_TAB);

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
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public DocumentDetails getDocumentDetails(long id, String type) {
		DocumentDetails documentDetails = documentDetailsDAO.getDocumentDetails(id, type);

		if (documentDetails == null || documentDetails.getDocRefId() == Long.MIN_VALUE
				|| documentDetails.getDocImage() != null) {
			return documentDetails;
		}

		byte[] docImg = getdocImage(documentDetails.getDocRefId());
		documentDetails.setDocImage(docImg);

		return documentDetails;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getTdsReceivableDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		TdsReceivable tdsReceivable = (TdsReceivable) auditDetail.getModelData();

		// Check the unique keys.
		if (tdsReceivable.isNew()
				&& tdsReceivableDAO.isDuplicateKey(tdsReceivable.getId(), tdsReceivable.getCertificateNumber(),
						tdsReceivable.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			String status = tdsReceivableDAO.getStatus(tdsReceivable.getCertificateNumber());

			if (status == null) {
				parameters[0] = PennantJavaUtil.getLabel("label_CertificateNumber") + ": "
						+ tdsReceivable.getCertificateNumber();

				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
			} else {
				final String msg = Labels.getLabel("Tds_certificate_message");

				if (MessageUtil.confirm(msg) == MessageUtil.YES) {
					logger.debug(Literal.LEAVING);
					return auditDetail;
				} else {
					auditDetail.setErrorDetail(new ErrorDetail("90002", null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	private byte[] getdocImage(Long id) {
		return dMSService.getById(id);
	}

	private void deleteDocument(TdsReceivable tdsReceivable, DocumentDetails dd) {
		if (tdsReceivable.getDocID() != 0 && !tdsReceivable.isNewRecord()) {
			tdsReceivable.setDocRefId(tdsReceivable.getDocumentDetails().getDocRefId());
			byte[] olddocumentManager = getDocumentImage(tdsReceivable.getDocRefId());

			if (olddocumentManager != null) {
				dd.setDocId(tdsReceivable.getDocID());
				documentDetailsDAO.delete(dd, TableType.TEMP_TAB.getSuffix());
			}
		}
	}

	private void saveDocument(TdsReceivable tdsReceivable, TableType tableType) {
		DocumentDetails dd = new DocumentDetails();
		dd.setFinReference(tdsReceivable.getTanNumber());
		dd.setDocName(tdsReceivable.getUploadCertificate());

		if (tdsReceivable.getDocID() != 0 && !tdsReceivable.isNewRecord()) {
			tdsReceivable.setDocRefId(tdsReceivable.getDocumentDetails().getDocRefId());
			byte[] olddocumentManager = tdsReceivable.getDocumentDetails().getDocImage();
			if (tdsReceivable.getDocImage() != null) {
				byte[] arr1 = olddocumentManager;
				byte[] arr2 = tdsReceivable.getDocImage();
				if (!Arrays.equals(arr1, arr2)) {
					dd.setDocImage(tdsReceivable.getDocImage());
					dd.setDoctype(tdsReceivable.getDocType());
					dd.setDocCategory(tdsReceivable.getDocCategory());
					saveDocument(DMSModule.FINANCE, DMSModule.TAN, dd);
					documentDetailsDAO.save(dd, tableType.getSuffix());
					tdsReceivable.setDocID(dd.getDocId());
				}
			}
		} else {
			dd.setDocImage(tdsReceivable.getDocImage());
			dd.setDoctype(tdsReceivable.getDocType());
			dd.setDocCategory(tdsReceivable.getDocCategory());
			dd.setUserDetails(tdsReceivable.getUserDetails());
			saveDocument(DMSModule.FINANCE, DMSModule.TAN, dd);
			documentDetailsDAO.save(dd, tableType.getSuffix());
			tdsReceivable.setDocID(dd.getDocId());
		}
	}
}