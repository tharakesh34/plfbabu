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
 * FileName    		:  LegalNoteServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-06-2018    														*
 *                                                                  						*
 * Modified Date    :  19-06-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-06-2018       PENNANT	                 0.1                                            * 
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.legal.LegalNoteDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.legal.LegalDetail;
import com.pennant.backend.model.legal.LegalNote;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>LegalNote</b>.<br>
 */
public class LegalNoteService extends GenericService<LegalNote> {
	private static final Logger logger = Logger.getLogger(LegalNoteService.class);

	private LegalNoteDAO legalNoteDAO;

	public LegalNoteDAO getLegalNoteDAO() {
		return legalNoteDAO;
	}

	public void setLegalNoteDAO(LegalNoteDAO legalNoteDAO) {
		this.legalNoteDAO = legalNoteDAO;
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
		LegalNote legalNote = (LegalNote) auditDetail.getModelData();
		LegalNote templegalNote = null;

		if (legalNote.isWorkflow()) {
			templegalNote = getLegalNoteDAO()
					.getLegalNote(legalNote.getLegalNoteId(), TableType.TEMP_TAB.getSuffix());
		}
		LegalNote befLegalNote = getLegalNoteDAO()
				.getLegalNote(legalNote.getLegalNoteId(), TableType.MAIN_TAB.getSuffix());
		LegalNote oldLegalNote = legalNote.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(legalNote.getCode());
		valueParm[1] = legalNote.getDescription();

		errParm[0] = PennantJavaUtil.getLabel("label_LegalReference") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_Title") + ":" + valueParm[1];

		if (legalNote.isNew()) {
			if (!legalNote.isWorkflow()) {
				if (befLegalNote != null) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else {
				if (legalNote.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					if (befLegalNote != null || templegalNote != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befLegalNote == null || templegalNote != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			if (!legalNote.isWorkflow()) {
				if (befLegalNote == null) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, null));
				} else {

					if (oldLegalNote != null
							&& !oldLegalNote.getLastMntOn().equals(befLegalNote.getLastMntOn())) {
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

				if (templegalNote == null) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

				if (templegalNote != null && oldLegalNote != null
						&& !oldLegalNote.getLastMntOn().equals(templegalNote.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !legalNote.isWorkflow()) {
			legalNote.setBefImage(befLegalNote);
		}
		return auditDetail;
	}

	public List<AuditDetail> getDetailsAuditData(LegalDetail legalDetail, String auditTranType, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		String[] fields = PennantJavaUtil.getFieldDetails(new LegalNote(),
				new LegalNote().getExcludeFields());
		List<LegalNote> detailList = legalDetail.getLegalNotesList();

		for (int i = 0; i < detailList.size(); i++) {

			LegalNote detail = detailList.get(i);
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

	public List<AuditDetail> processingDetails(LegalDetail legalDetail, List<AuditDetail> auditDetails,
			TableType tableType) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			LegalNote legalNote = (LegalNote) auditDetails.get(i).getModelData();
			legalNote.setLegalId(legalDetail.getLegalId());

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(tableType.getSuffix())) {
				approveRec = true;
				legalNote.setRoleCode("");
				legalNote.setNextRoleCode("");
				legalNote.setTaskId("");
				legalNote.setNextTaskId("");
			}

			legalNote.setWorkflowId(0);
			if (legalNote.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (legalNote.isNewRecord()) {
				saveRecord = true;
				if (legalNote.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					legalNote.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (legalNote.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					legalNote.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (legalNote.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					legalNote.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (legalNote.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (legalNote.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (legalNote.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (legalNote.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = legalNote.getRecordType();
				recordStatus = legalNote.getRecordStatus();
				legalNote.setRecordType("");
				legalNote.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				getLegalNoteDAO().save(legalNote, tableType);
			}

			if (updateRecord) {
				getLegalNoteDAO().update(legalNote, tableType);
			}

			if (deleteRecord) {
				getLegalNoteDAO().delete(legalNote, tableType);
			}

			if (approveRec) {
				legalNote.setRecordType(rcdType);
				legalNote.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(legalNote);
		}
		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	public List<AuditDetail> deleteDetails(List<AuditDetail> legalNotes, String tableType, String auditTranType) {
		List<AuditDetail> auditList = new ArrayList<AuditDetail>();

		LegalNote legalNote = null;
		String[] fields = PennantJavaUtil.getFieldDetails(new LegalNote(),
				new LegalNote().getExcludeFields());
		for (int i = 0; i < legalNotes.size(); i++) {
			legalNote = (LegalNote) legalNotes.get(i).getModelData();
			legalNote.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], legalNote.getBefImage(),
					legalNote));
		}
		getLegalNoteDAO().deleteList(legalNote, tableType);
		return auditList;
	}

	public List<LegalNote> getDetailsList(long legalId, String type) {
		return getLegalNoteDAO().getLegalNoteList(legalId, type);
	}
}