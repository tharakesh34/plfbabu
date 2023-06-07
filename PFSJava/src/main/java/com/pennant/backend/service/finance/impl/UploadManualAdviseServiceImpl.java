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

package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.manualadviseupload.UploadManualAdviseDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.expenses.UploadHeader;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.UploadManualAdvise;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.feetype.FeeTypeService;
import com.pennant.backend.service.finance.ManualAdviseService;
import com.pennant.backend.service.finance.UploadManualAdviseService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.UploadConstants;
import com.pennant.pff.fee.AdviseType;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class UploadManualAdviseServiceImpl extends GenericService<UploadManualAdvise>
		implements UploadManualAdviseService {
	private static final Logger logger = LogManager.getLogger(UploadManualAdviseServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private FinanceMainDAO financeMainDAO;
	private UploadManualAdviseDAO uploadManualAdviseDAO;
	private ManualAdviseService manualAdviseService;
	private FeeTypeService feeTypeService;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	/**
	 * @return the auditHeaderDAO
	 */
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	/**
	 * @param auditHeaderDAO the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table RefundUploads/RefundUploads_Temp
	 * by using RefundUploadsDAO's save method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using RefundUploadsDAO's update method 3) Audit the record in to AuditHeader and
	 * AdtRefundUploads by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		String tableType = "";
		UploadManualAdvise manualAdviseUpload = (UploadManualAdvise) auditHeader.getAuditDetail().getModelData();

		if (manualAdviseUpload.isWorkflow()) {
			tableType = "_TEMP";
		}

		if (manualAdviseUpload.isNewRecord()) {
			getUploadManualAdviseDAO().save(manualAdviseUpload, tableType);
			auditHeader.getAuditDetail().setModelData(manualAdviseUpload);
			auditHeader.setAuditReference(String.valueOf(manualAdviseUpload.getFinReference()));
		} else {
			getUploadManualAdviseDAO().update(manualAdviseUpload, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");

		return auditHeader;
	}

	public List<UploadManualAdvise> getManualAdviseUploadsByUploadId(long uploadId) {
		return getUploadManualAdviseDAO().getAdviseUploadsByUploadId(uploadId, "_TView");
	}

	/**
	 * getRefundUploadsById fetch the details by using RefundUploadsDAO's getRefundUploadByRef method.
	 * 
	 * @param uploadId (long)
	 * @return RefundUploads
	 */
	public List<UploadManualAdvise> getApprovedManualAdviseUploadsByUploadId(long uploadId) {
		return getUploadManualAdviseDAO().getAdviseUploadsByUploadId(uploadId, "_View");
	}

	/**
	 * doApprove method does the following steps. 1) Does the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * based on the Record type do following actions a) DELETE Delete the record from the main table by using
	 * getUploadManualAdviseDAO().delete with parameters promotionFee,"" b) NEW Add new record in to main table by using
	 * getUploadManualAdviseDAO().save with parameters promotionFee,"" c) EDIT Update record in the main table by using
	 * getUploadManualAdviseDAO().update with parameters promotionFee,"" 3) Delete the record from the workFlow table by
	 * using getUploadManualAdviseDAO().delete with parameters promotionFee,"_Temp" 4) Audit the record in to
	 * AuditHeader and AdtAdviseUploads by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record
	 * in to AuditHeader and AdtAdviseUploads by using auditHeaderDAO.addAudit(auditHeader) based on the transaction
	 * Type.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		UploadManualAdvise uploadManualAdvise = new UploadManualAdvise();
		BeanUtils.copyProperties(auditHeader.getAuditDetail().getModelData(), uploadManualAdvise);

		if (PennantConstants.RECORD_TYPE_DEL.equals(uploadManualAdvise.getRecordType())) {
			tranType = PennantConstants.TRAN_DEL;
			// getUploadManualAdviseDAO().delete(uploadManualAdvise, ""); // because delete will not be applicable here
		} else {
			uploadManualAdvise.setRoleCode("");
			uploadManualAdvise.setNextRoleCode("");
			uploadManualAdvise.setTaskId("");
			uploadManualAdvise.setNextTaskId("");
			uploadManualAdvise.setWorkflowId(0);

			if (PennantConstants.RECORD_TYPE_NEW.equals(uploadManualAdvise.getRecordType())) {
				tranType = PennantConstants.TRAN_ADD;
				uploadManualAdvise.setRecordType("");
				getUploadManualAdviseDAO().save(uploadManualAdvise, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				uploadManualAdvise.setRecordType("");
				getUploadManualAdviseDAO().update(uploadManualAdvise, "");
			}
		}

		getUploadManualAdviseDAO().deleteByUploadId(uploadManualAdvise.getUploadId(), "_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(uploadManualAdvise);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");

		return auditHeader;
	}

	private AuditHeader getAuditHeader(ManualAdvise aManualAdvise, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aManualAdvise.getBefImage(), aManualAdvise);
		return new AuditHeader(String.valueOf(aManualAdvise.getAdviseID()), String.valueOf(aManualAdvise.getAdviseID()),
				null, null, auditDetail, aManualAdvise.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getUploadManualAdviseDAO().delete with parameters promotionFee,"_Temp" 3) Audit the
	 * record in to AuditHeader and AdtRefundUploads by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		// UploadManualAdvise uploadManualAdvise = (UploadManualAdvise) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		// getUploadManualAdviseDAO().delete(uploadManualAdvise, "_TEMP"); // because delete will not be applicable here

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");

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
		logger.debug("Entering");

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug("Leaving");

		return auditHeader;
	}

	public List<ErrorDetail> validateAdviseUploads(AuditHeader auditHeader, String usrLanguage, String method) {
		logger.debug("Entering");

		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		UploadHeader uploadHeader = (UploadHeader) auditHeader.getAuditDetail().getModelData();
		List<AuditDetail> auditDetails = null;

		// Manual Advise Uploads
		if (uploadHeader.getAuditDetailMap().get("AdviseUploads") != null) {

			int successCount = 0;
			int failCount = 0;
			auditDetails = uploadHeader.getAuditDetailMap().get("AdviseUploads");

			for (AuditDetail auditDetail : auditDetails) {
				AuditDetail adviseAudit = validation(auditDetail, usrLanguage, method);
				UploadManualAdvise uploadManualAdvise = (UploadManualAdvise) auditDetail.getModelData();
				if (UploadConstants.UPLOAD_STATUS_SUCCESS.equals(uploadManualAdvise.getStatus())) {
					successCount++;
				} else {
					failCount++;
				}
				List<ErrorDetail> details = adviseAudit.getErrorDetails();
				if (details != null) {
					errorDetails.addAll(details);
				}
			}

			// Success and failed count updation
			uploadHeader.setSuccessCount(successCount);
			uploadHeader.setFailedCount(failCount);
			uploadHeader.setTotalRecords(successCount + failCount);
			auditHeader.getAuditDetail().setModelData(uploadHeader);
		}

		logger.debug("Leaving");

		return errorDetails;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getUploadManualAdviseDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings
	 * then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");

		UploadManualAdvise adviseUpload = (UploadManualAdvise) auditDetail.getModelData();

		// Check the unique keys.
		/*
		 * if (adviseUpload.isNewRecord() && !UploadConstants.UPLOAD_STATUS_SUCCESS.equals(adviseUpload.getStatus())) {
		 * validateLengths(adviseUpload); if (!UploadConstants.UPLOAD_STATUS_FAIL.equals(adviseUpload.getStatus())) {
		 * validateData(adviseUpload); } }
		 */

		auditDetail.setModelData(adviseUpload);

		return auditDetail;
	}

	public List<AuditDetail> setAdviseUploadsAuditData(List<UploadManualAdvise> adviseUploadList, String auditTranType,
			String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new UploadManualAdvise(),
				new UploadManualAdvise().getExcludeFields());

		for (int i = 0; i < adviseUploadList.size(); i++) {

			UploadManualAdvise adviseUpload = adviseUploadList.get(i);

			if (StringUtils.isEmpty(adviseUpload.getRecordType())) {
				continue;
			}

			boolean isRcdType = false;
			if (PennantConstants.RCD_ADD.equalsIgnoreCase(adviseUpload.getRecordType())) {
				adviseUpload.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(adviseUpload.getRecordType())) {
				adviseUpload.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(adviseUpload.getRecordType())) {
				adviseUpload.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			if ("saveOrUpdate".equals(method) && isRcdType) {
				adviseUpload.setNewRecord(true);
			}
			if (!PennantConstants.TRAN_WF.equals(auditTranType)) {
				if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(adviseUpload.getRecordType())) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(adviseUpload.getRecordType())
						|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(adviseUpload.getRecordType())) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], adviseUpload.getBefImage(),
					adviseUpload));
		}

		logger.debug("Leaving");

		return auditDetails;
	}

	public ManualAdvise prepareDetails(UploadManualAdvise uploadManualAdvise) {
		logger.debug("Entering");

		ManualAdvise manualAdvise = new ManualAdvise();
		manualAdvise.setAdviseID(Long.MIN_VALUE);
		if (UploadConstants.UPLOAD_PAYABLE_ADVISE.equals(uploadManualAdvise.getAdviseType())) {
			manualAdvise.setAdviseType(AdviseType.PAYABLE.id());
		} else {
			manualAdvise.setAdviseType(AdviseType.RECEIVABLE.id());
		}
		manualAdvise.setFinID(uploadManualAdvise.getFinID());
		manualAdvise.setFinReference(uploadManualAdvise.getFinReference());
		manualAdvise.setFeeTypeCode(uploadManualAdvise.getFeeTypeCode());
		manualAdvise.setFeeTypeID(uploadManualAdvise.getFeeTypeID());
		manualAdvise.setSequence(0);
		manualAdvise.setAdviseAmount(uploadManualAdvise.getAdviseAmount());
		manualAdvise.setBounceID(0);
		manualAdvise.setReceiptID(0);
		manualAdvise.setPaidAmount(BigDecimal.ZERO);
		manualAdvise.setWaivedAmount(BigDecimal.ZERO);
		manualAdvise.setRemarks(uploadManualAdvise.getRemarks());
		manualAdvise.setValueDate(uploadManualAdvise.getValueDate());
		manualAdvise.setPostDate(SysParamUtil.getAppDate());
		manualAdvise.setReservedAmt(BigDecimal.ZERO);
		manualAdvise.setBalanceAmt(uploadManualAdvise.getAdviseAmount());
		manualAdvise.setVersion(1);
		manualAdvise.setLastMntBy(uploadManualAdvise.getLastMntBy());
		manualAdvise.setLastMntOn(uploadManualAdvise.getLastMntOn());
		manualAdvise.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		manualAdvise.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		manualAdvise.setNewRecord(true);
		manualAdvise.setUserDetails(uploadManualAdvise.getUserDetails());
		manualAdvise.setFinSource(UploadConstants.FINSOURCE_ID_UPLOAD);
		FeeType javaFeeType = feeTypeService.getApprovedFeeTypeById(manualAdvise.getFeeTypeID());
		manualAdvise.setTaxApplicable(javaFeeType.isTaxApplicable());
		manualAdvise.setTaxComponent(javaFeeType.getTaxComponent());
		com.pennant.backend.model.finance.FeeType modelFeeType = new com.pennant.backend.model.finance.FeeType();
		BeanUtils.copyProperties(javaFeeType, modelFeeType);
		manualAdvise.setFeeType(modelFeeType);
		manualAdvise.setAdviseID(uploadManualAdvise.getAdviseId());
		manualAdvise.setTaxComponent(modelFeeType.getTaxComponent());

		logger.debug("Leaving");
		return manualAdvise;
	}

	public List<AuditDetail> processAdviseUploadsDetails(List<AuditDetail> auditDetails, long uploadId, String type) {
		logger.debug("Entering");

		for (int i = 0; i < auditDetails.size(); i++) {
			UploadManualAdvise uploadAdvise = (UploadManualAdvise) auditDetails.get(i).getModelData();
			boolean saveRecord = false;
			boolean updateRecord = false;
			boolean deleteRecord = false;
			boolean approveRec = false;
			String rcdType = "";

			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				uploadAdvise.setRoleCode("");
				uploadAdvise.setNextRoleCode("");
				uploadAdvise.setTaskId("");
				uploadAdvise.setNextTaskId("");
				uploadAdvise.setWorkflowId(0);
			}

			String recordType = uploadAdvise.getRecordType();
			uploadAdvise.setUploadId(uploadId);

			if (PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(recordType)) {
				deleteRecord = true;
			} else if (uploadAdvise.isNewRecord()) {
				saveRecord = true;
				if (PennantConstants.RCD_ADD.equalsIgnoreCase(recordType)) {
					uploadAdvise.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(recordType)) {
					uploadAdvise.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(recordType)) {
					uploadAdvise.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(recordType)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (PennantConstants.RECORD_TYPE_UPD.equalsIgnoreCase(recordType)) {
				updateRecord = true;
			} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(recordType)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (uploadAdvise.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = recordType;
				recordStatus = uploadAdvise.getRecordStatus();
				uploadAdvise.setRecordType("");
				uploadAdvise.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				if (approveRec) {
					if (!UploadConstants.UPLOAD_STATUS_FAIL.equals(uploadAdvise.getStatus())) {
						ManualAdvise manualAdvise = prepareDetails(uploadAdvise);
						AuditHeader auditHeader = getAuditHeader(manualAdvise, PennantConstants.TRAN_WF);
						this.manualAdviseService.doApprove(auditHeader);
						manualAdvise = (ManualAdvise) auditHeader.getAuditDetail().getModelData();
						long adviseID = manualAdvise.getAdviseID();
						uploadAdvise.setManualAdviseId(adviseID);
					}
				}
				if (type.equals("")) {
					uploadManualAdviseDAO.deleteByUploadId(uploadId, "_temp");
				}
				getUploadManualAdviseDAO().save(uploadAdvise, type);
			}

			if (updateRecord) {
				getUploadManualAdviseDAO().update(uploadAdvise, type);
			}
			if (deleteRecord) {
				// getUploadManualAdviseDAO().delete(uploadAdvise, type); // because delete will not be applicable here
			}
			if (approveRec) {
				uploadAdvise.setRecordType(rcdType);
				uploadAdvise.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(uploadAdvise);
		}

		logger.debug("Leaving");

		return auditDetails;
	}

	public List<AuditDetail> delete(List<UploadManualAdvise> adviseUploadList, String tableType, String auditTranType,
			long uploadId) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (CollectionUtils.isNotEmpty(adviseUploadList)) {
			String[] fields = PennantJavaUtil.getFieldDetails(new UploadManualAdvise(),
					new UploadManualAdvise().getExcludeFields());
			for (int i = 0; i < adviseUploadList.size(); i++) {
				UploadManualAdvise adviseUpload = adviseUploadList.get(i);
				if (StringUtils.isNotEmpty(adviseUpload.getRecordType()) || StringUtils.isEmpty(tableType)) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							adviseUpload.getBefImage(), adviseUpload));
				}
			}
			getUploadManualAdviseDAO().deleteByUploadId(uploadId, tableType);
		}
		return auditDetails;
	}

	/**
	 * @return the refundUploadDAO
	 */
	public UploadManualAdviseDAO getUploadManualAdviseDAO() {
		return uploadManualAdviseDAO;
	}

	/**
	 * @param refundUploadDAO the refundUploadDAO to set
	 */
	public void setUploadManualAdviseDAO(UploadManualAdviseDAO uploadManualAdviseDAO) {
		this.uploadManualAdviseDAO = uploadManualAdviseDAO;
	}

	public List<UploadManualAdvise> getManualAdviseListByUploadId(long uploadId) {
		return getUploadManualAdviseDAO().getAdviseUploadsByUploadId(uploadId, "_View");
	}

	public ManualAdviseService getManualAdviseService() {
		return manualAdviseService;
	}

	public void setManualAdviseService(ManualAdviseService manualAdviseService) {
		this.manualAdviseService = manualAdviseService;
	}

	public FeeTypeService getFeeTypeService() {
		return feeTypeService;
	}

	public void setFeeTypeService(FeeTypeService feeTypeService) {
		this.feeTypeService = feeTypeService;
	}

}