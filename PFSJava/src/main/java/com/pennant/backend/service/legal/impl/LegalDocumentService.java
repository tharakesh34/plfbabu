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
 * FileName    		:  LegalDocumentServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  18-06-2018    														*
 *                                                                  						*
 * Modified Date    :  18-06-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-06-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.service.legal.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennant.backend.dao.legal.LegalDocumentDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennant.backend.model.legal.LegalDetail;
import com.pennant.backend.model.legal.LegalDocument;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>LegalDocument</b>.<br>
 */
public class LegalDocumentService extends GenericService<LegalDocument> {
	private static final Logger logger = Logger.getLogger(LegalDocumentService.class);

	private LegalDocumentDAO legalDocumentDAO;
	private DocumentManagerDAO documentManagerDAO;

	public LegalDocumentDAO getLegalDocumentDAO() {
		return legalDocumentDAO;
	}

	public void setLegalDocumentDAO(LegalDocumentDAO legalDocumentDAO) {
		this.legalDocumentDAO = legalDocumentDAO;
	}

	public DocumentManagerDAO getDocumentManagerDAO() {
		return documentManagerDAO;
	}

	public void setDocumentManagerDAO(DocumentManagerDAO documentManagerDAO) {
		this.documentManagerDAO = documentManagerDAO;
	}

	public List<AuditDetail> vaildateDetails(List<AuditDetail> auditDetails, String method, String usrLanguage) {
		if (auditDetails != null && auditDetails.size() > 0) {
			List<AuditDetail> details = new ArrayList<AuditDetail>();
			for (int i = 0; i < auditDetails.size(); i++) {
				AuditDetail auditDetail = validate(auditDetails.get(i), method, usrLanguage);
				details.add(auditDetail);
			}
			return details;
		}
		return new ArrayList<AuditDetail>();

	}

	private AuditDetail validate(AuditDetail auditDetail, String method, String usrLanguage) {

		LegalDocument legalDocument = (LegalDocument) auditDetail.getModelData();
		LegalDocument tempLegalDocument = null;

		if (legalDocument.isWorkflow()) {
			tempLegalDocument = getLegalDocumentDAO().getLegalDocument(legalDocument.getLegalId(),
					legalDocument.getLegalDocumentId(), TableType.TEMP_TAB.getSuffix());
		}
		LegalDocument befApplicantDetail = getLegalDocumentDAO().getLegalDocument(legalDocument.getLegalId(),
				legalDocument.getLegalDocumentId(), TableType.MAIN_TAB.getSuffix());
		LegalDocument oldApplicantDetail = legalDocument.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = StringUtils.trimToEmpty(legalDocument.getDocumentName());
		valueParm[1] = legalDocument.getDocumentNo();

		errParm[0] = PennantJavaUtil.getLabel("label_LegalReference") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_Title") + ":" + valueParm[1];

		if (legalDocument.isNew()) {
			if (!legalDocument.isWorkflow()) {
				if (befApplicantDetail != null) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else {
				if (legalDocument.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					if (befApplicantDetail != null || tempLegalDocument != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befApplicantDetail == null || tempLegalDocument != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			if (!legalDocument.isWorkflow()) {
				if (befApplicantDetail == null) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, null));
				} else {

					if (oldApplicantDetail != null
							&& !oldApplicantDetail.getLastMntOn().equals(befApplicantDetail.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, null));
						} else {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, null));
						}
					}
				}
			} else {

				if (tempLegalDocument == null) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

				if (tempLegalDocument != null && oldApplicantDetail != null
						&& !oldApplicantDetail.getLastMntOn().equals(tempLegalDocument.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !legalDocument.isWorkflow()) {
			legalDocument.setBefImage(befApplicantDetail);
		}
		return auditDetail;
	}

	public List<AuditDetail> getDocumentDetailsAuditData(LegalDetail legalDetail, String auditTranType, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		String[] fields = PennantJavaUtil.getFieldDetails(new LegalDocument(), new LegalDocument().getExcludeFields());
		List<LegalDocument> detailList = legalDetail.getDocumentList();

		for (int i = 0; i < detailList.size(); i++) {

			LegalDocument detail = detailList.get(i);
			if (StringUtils.isEmpty(StringUtils.trimToEmpty(detail.getRecordType()))) {
				continue;
			}

			boolean isRcdType = false;
			if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				detail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (legalDetail.isWorkflow()) {
					isRcdType = true;
				}
			} else if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				detail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				detail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| detail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], detail.getBefImage(), detail));
		}
		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	public List<AuditDetail> processingDocumentDetail(LegalDetail legalDetail, List<AuditDetail> auditDetails,
			TableType tableType) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			LegalDocument legalDocument = (LegalDocument) auditDetails.get(i).getModelData();
			legalDocument.setLegalId(legalDetail.getLegalId());

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(tableType.getSuffix())) {
				approveRec = true;
				legalDocument.setRoleCode("");
				legalDocument.setNextRoleCode("");
				legalDocument.setTaskId("");
				legalDocument.setNextTaskId("");
			}

			legalDocument.setWorkflowId(0);
			if (legalDocument.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (legalDocument.isNewRecord()) {
				saveRecord = true;
				if (legalDocument.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					legalDocument.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (legalDocument.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					legalDocument.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (legalDocument.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					legalDocument.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (legalDocument.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (legalDocument.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (legalDocument.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (legalDocument.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = legalDocument.getRecordType();
				recordStatus = legalDocument.getRecordStatus();
				legalDocument.setRecordType("");
				legalDocument.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				if ((legalDocument.getDocumentReference() == 0 || legalDocument.getDocumentReference() == Long.MIN_VALUE) && legalDocument.getDocImage() != null) {
					DocumentManager documentManager = new DocumentManager();
					documentManager.setDocImage(legalDocument.getDocImage());
					legalDocument.setDocumentReference(getDocumentManagerDAO().save(documentManager));
				}
				getLegalDocumentDAO().save(legalDocument, tableType);
			}

			if (updateRecord) {
				if ((legalDocument.getDocumentReference() == 0 || legalDocument.getDocumentReference() == Long.MIN_VALUE) && legalDocument.getDocImage() != null) {
					DocumentManager documentManager = new DocumentManager();
					documentManager.setDocImage(legalDocument.getDocImage());
					legalDocument.setDocumentReference(getDocumentManagerDAO().save(documentManager));
				}
				getLegalDocumentDAO().update(legalDocument, tableType);
			}

			if (deleteRecord) {
				getLegalDocumentDAO().delete(legalDocument, tableType);
			}

			if (approveRec) {
				legalDocument.setRecordType(rcdType);
				legalDocument.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(legalDocument);
		}
		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	public List<AuditDetail> deleteDocumentDetails(List<AuditDetail> documentDetails, String tableType,
			String auditTranType) {
		List<AuditDetail> auditList = new ArrayList<AuditDetail>();

		LegalDocument documentDetail = null;
		String[] fields = PennantJavaUtil.getFieldDetails(new LegalDocument(), new LegalDocument().getExcludeFields());
		for (int i = 0; i < documentDetails.size(); i++) {
			documentDetail = (LegalDocument) documentDetails.get(i).getModelData();
			documentDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], documentDetail.getBefImage(),
					documentDetail));
		}
		getLegalDocumentDAO().deleteList(documentDetail, tableType);
		return auditList;
	}

	public List<LegalDocument> getLegalDocumenttDetailsList(long legalId, String type) {
		List<LegalDocument> documents = getLegalDocumentDAO().getLegalDocumenttDetailsList(legalId, type);
		if (CollectionUtils.isNotEmpty(documents)) {
			for (LegalDocument legalDocument : documents) {
				if (legalDocument.getDocumentReference() != 0 && legalDocument.getDocumentReference() != Long.MIN_VALUE) {
					DocumentManager documentManager = getDocumentManagerDAO().getById(legalDocument.getDocumentReference());
					legalDocument.setDocImage(documentManager.getDocImage());
				}
			}
		}
		return documents;
	}

}