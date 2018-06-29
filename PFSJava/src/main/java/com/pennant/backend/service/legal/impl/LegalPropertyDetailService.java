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
 * FileName    		:  LegalPropertyDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  16-06-2018    														*
 *                                                                  						*
 * Modified Date    :  16-06-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 16-06-2018       PENNANT	                 0.1                                            * 
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
import com.pennant.backend.dao.legal.LegalPropertyDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.legal.LegalDetail;
import com.pennant.backend.model.legal.LegalPropertyDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on
 * <b>LegalPropertyDetail</b>.<br>
 */
public class LegalPropertyDetailService extends GenericService<LegalPropertyDetail> {
	private static final Logger logger = Logger.getLogger(LegalPropertyDetailService.class);

	private LegalPropertyDetailDAO legalPropertyDetailDAO;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	/**
	 * @return the legalPropertyDetailDAO
	 */
	public LegalPropertyDetailDAO getLegalPropertyDetailDAO() {
		return legalPropertyDetailDAO;
	}

	/**
	 * @param legalPropertyDetailDAO
	 *            the legalPropertyDetailDAO to set
	 */
	public void setLegalPropertyDetailDAO(LegalPropertyDetailDAO legalPropertyDetailDAO) {
		this.legalPropertyDetailDAO = legalPropertyDetailDAO;
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

		LegalPropertyDetail legalPropertyDetail = (LegalPropertyDetail) auditDetail.getModelData();
		LegalPropertyDetail tempLegalPropertyDetail = null;

		if (legalPropertyDetail.isWorkflow()) {
			tempLegalPropertyDetail = getLegalPropertyDetailDAO().getLegalPropertyDetail(
					legalPropertyDetail.getLegalId(), legalPropertyDetail.getLegalPropertyId(),
					TableType.TEMP_TAB.getSuffix());
		}
		LegalPropertyDetail befApplicantDetail = getLegalPropertyDetailDAO().getLegalPropertyDetail(
				legalPropertyDetail.getLegalId(), legalPropertyDetail.getLegalPropertyId(),
				TableType.MAIN_TAB.getSuffix());
		LegalPropertyDetail oldApplicantDetail = legalPropertyDetail.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = StringUtils.trimToEmpty(legalPropertyDetail.getLegalReference());
		valueParm[1] = legalPropertyDetail.getPropertyType();

		errParm[0] = PennantJavaUtil.getLabel("label_LegalReference") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_Title") + ":" + valueParm[1];

		if (legalPropertyDetail.isNew()) {
			if (!legalPropertyDetail.isWorkflow()) {
				if (befApplicantDetail != null) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else {
				if (legalPropertyDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					if (befApplicantDetail != null || tempLegalPropertyDetail != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befApplicantDetail == null || tempLegalPropertyDetail != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			if (!legalPropertyDetail.isWorkflow()) {
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

				if (tempLegalPropertyDetail == null) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

				if (tempLegalPropertyDetail != null && oldApplicantDetail != null
						&& !oldApplicantDetail.getLastMntOn().equals(tempLegalPropertyDetail.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !legalPropertyDetail.isWorkflow()) {
			legalPropertyDetail.setBefImage(befApplicantDetail);
		}
		
		return auditDetail;
	}

	public List<AuditDetail> getPropertyDetailsAuditData(LegalDetail legalDetail, String auditTranType, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		String[] fields = PennantJavaUtil.getFieldDetails(new LegalPropertyDetail(),
				new LegalPropertyDetail().getExcludeFields());
		List<LegalPropertyDetail> detailList = legalDetail.getPropertyDetailList();

		for (int i = 0; i < detailList.size(); i++) {

			LegalPropertyDetail detail = detailList.get(i);
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
	
	public List<AuditDetail> processingPropertyDetail(LegalDetail legalDetail, List<AuditDetail> auditDetails, TableType tableType) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			LegalPropertyDetail legalPropertyDetail = (LegalPropertyDetail) auditDetails.get(i).getModelData();
			legalPropertyDetail.setLegalId(legalDetail.getLegalId());
			
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(tableType.getSuffix())) {
				approveRec = true;
				legalPropertyDetail.setRoleCode("");
				legalPropertyDetail.setNextRoleCode("");
				legalPropertyDetail.setTaskId("");
				legalPropertyDetail.setNextTaskId("");
			}

			legalPropertyDetail.setWorkflowId(0);
			if (legalPropertyDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (legalPropertyDetail.isNewRecord()) {
				saveRecord = true;
				if (legalPropertyDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					legalPropertyDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (legalPropertyDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					legalPropertyDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (legalPropertyDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					legalPropertyDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (legalPropertyDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (legalPropertyDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (legalPropertyDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (legalPropertyDetail.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = legalPropertyDetail.getRecordType();
				recordStatus = legalPropertyDetail.getRecordStatus();
				legalPropertyDetail.setRecordType("");
				legalPropertyDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				getLegalPropertyDetailDAO().save(legalPropertyDetail, tableType);
			}

			if (updateRecord) {
				getLegalPropertyDetailDAO().update(legalPropertyDetail, tableType);
			}

			if (deleteRecord) {
				getLegalPropertyDetailDAO().delete(legalPropertyDetail, tableType);
			}

			if (approveRec) {
				legalPropertyDetail.setRecordType(rcdType);
				legalPropertyDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(legalPropertyDetail);
		}
		logger.debug(Literal.LEAVING);
		return auditDetails;
	}
	
	public List<AuditDetail> deletePropertyDetails(List<AuditDetail> applicantDetails, String tableType, String auditTranType) {
		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		
		LegalPropertyDetail propertyDetail = null;
		String[] fields = PennantJavaUtil.getFieldDetails(new LegalPropertyDetail(), new LegalPropertyDetail().getExcludeFields());
		for (int i = 0; i < applicantDetails.size(); i++) {
			propertyDetail = (LegalPropertyDetail) applicantDetails.get(i).getModelData();
			propertyDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], propertyDetail.getBefImage(), propertyDetail));
		}
		getLegalPropertyDetailDAO().deleteList(propertyDetail, tableType);
		return auditList;
	}

	public List<LegalPropertyDetail> getPropertyDetailsList(long legalId, String tableType) {
		return getLegalPropertyDetailDAO().getPropertyDetailsList(legalId, tableType);
	}

}