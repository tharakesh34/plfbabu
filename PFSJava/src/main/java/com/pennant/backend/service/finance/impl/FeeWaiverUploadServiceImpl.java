package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.feewaiverupload.FeeWaiverUploadDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.expenses.FeeWaiverUploadHeader;
import com.pennant.backend.model.finance.FeeWaiverDetail;
import com.pennant.backend.model.finance.FeeWaiverHeader;
import com.pennant.backend.model.finance.FeeWaiverUpload;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FeeWaiverHeaderService;
import com.pennant.backend.service.finance.FeeWaiverUploadService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.UploadConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.FinServiceEvent;

public class FeeWaiverUploadServiceImpl extends GenericService<FeeWaiverUpload> implements FeeWaiverUploadService {
	private static final Logger logger = LogManager.getLogger(FeeWaiverUploadServiceImpl.class);

	private FeeWaiverUploadDAO feeWaiverUploadDAO;
	private FeeWaiverHeaderService feeWaiverHeaderService;
	private FinanceMainDAO financeMainDAO;
	private Map<String, BigDecimal> taxPercentages;

	public List<ErrorDetail> validateWaiverUploads(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<ErrorDetail> errorDetails = new ArrayList<>();
		FeeWaiverUploadHeader uploadHeader = (FeeWaiverUploadHeader) auditHeader.getAuditDetail().getModelData();
		List<AuditDetail> auditDetails = null;

		if (uploadHeader.getAuditDetailMap().get("WaiverUploads") != null) {

			int successCount = 0;
			int failCount = 0;
			auditDetails = uploadHeader.getAuditDetailMap().get("WaiverUploads");

			for (AuditDetail auditDetail : auditDetails) {
				auditDetail.setModelData((FeeWaiverUpload) auditDetail.getModelData());
				FeeWaiverUpload uploadFeeWaiver = (FeeWaiverUpload) auditDetail.getModelData();
				if (UploadConstants.UPLOAD_STATUS_SUCCESS.equals(uploadFeeWaiver.getStatus())) {
					successCount++;
				} else {
					failCount++;
				}
				List<ErrorDetail> details = auditDetail.getErrorDetails();
				if (details != null) {
					errorDetails.addAll(details);
				}
			}

			uploadHeader.setSuccessCount(successCount);
			uploadHeader.setFailedCount(failCount);
			uploadHeader.setTotalRecords(successCount + failCount);
			auditHeader.getAuditDetail().setModelData(uploadHeader);
		}

		logger.debug(Literal.LEAVING);
		return errorDetails;
	}

	public List<AuditDetail> delete(List<FeeWaiverUpload> uploadList, String tableType, String aTranType,
			long uploadId) {
		List<AuditDetail> auditDetails = new ArrayList<>();

		if (CollectionUtils.isEmpty(uploadList)) {
			return auditDetails;
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FeeWaiverUpload(),
				new FeeWaiverUpload().getExcludeFields());

		int i = 0;
		for (FeeWaiverUpload fwu : uploadList) {
			if (StringUtils.isNotEmpty(fwu.getRecordType()) || StringUtils.isEmpty(tableType)) {
				auditDetails.add(new AuditDetail(aTranType, i++, fields[0], fields[1], fwu.getBefImage(), fwu));
			}
		}

		feeWaiverUploadDAO.deleteByUploadId(uploadId, tableType);
		return auditDetails;
	}

	public List<AuditDetail> processWaiverUploadsDetails(List<AuditDetail> auditDetails, long uploadId, String type)
			throws Exception {
		logger.debug(Literal.ENTERING);

		List<FeeWaiverUpload> list = new ArrayList<>();

		for (int i = 0; i < auditDetails.size(); i++) {
			FeeWaiverUpload uploadWaiver = (FeeWaiverUpload) auditDetails.get(i).getModelData();
			boolean saveRecord = false;
			boolean updateRecord = false;
			boolean deleteRecord = false;
			boolean approveRec = false;
			String rcdType = "";

			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				uploadWaiver.setRoleCode("");
				uploadWaiver.setNextRoleCode("");
				uploadWaiver.setTaskId("");
				uploadWaiver.setNextTaskId("");
				uploadWaiver.setWorkflowId(0);
			}

			String recordType = uploadWaiver.getRecordType();
			uploadWaiver.setUploadId(uploadId);

			if (PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(recordType)) {
				deleteRecord = true;
			} else if (uploadWaiver.isNewRecord()) {
				saveRecord = true;
				if (PennantConstants.RCD_ADD.equalsIgnoreCase(recordType)) {
					uploadWaiver.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(recordType)) {
					uploadWaiver.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(recordType)) {
					uploadWaiver.setRecordType(PennantConstants.RECORD_TYPE_UPD);
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
				} else if (uploadWaiver.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = recordType;
				recordStatus = uploadWaiver.getRecordStatus();
				uploadWaiver.setRecordType("");
				uploadWaiver.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				if (approveRec) {
					list.add(uploadWaiver);
				}
				feeWaiverUploadDAO.save(uploadWaiver, type);
			}

			if (updateRecord) {
				feeWaiverUploadDAO.update(uploadWaiver, type);
			}
			if (deleteRecord) {
				// getUploadManualAdviseDAO().delete(uploadAdvise, type); // because delete will not be applicable
				// here
			}
			if (approveRec) {
				uploadWaiver.setRecordType(rcdType);
				uploadWaiver.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(uploadWaiver);

			Map<Long, FeeWaiverHeader> map = new HashMap<>();

			List<FeeWaiverDetail> feeWaiverDetails = new ArrayList<>();

			if (CollectionUtils.isNotEmpty(list)) {
				for (FeeWaiverUpload fwu : list) {
					if (!UploadConstants.UPLOAD_STATUS_FAIL.equals(fwu.getStatus())) {
						FeeWaiverHeader feeWaiverHeader = map.get(fwu.getUploadId());
						if (feeWaiverHeader == null) {
							feeWaiverHeader = prepareHeader(fwu);
							map.put(fwu.getUploadId(), feeWaiverHeader);
						}
						processWaiverDetails(feeWaiverHeader, fwu);

						for (FeeWaiverDetail fwd : feeWaiverHeader.getFeeWaiverDetails()) {
							feeWaiverDetails.add(fwd);
						}

					}
				}

				for (Long key : map.keySet()) {
					FeeWaiverHeader feeWaiverHeader = map.get(key);
					feeWaiverHeader.setFeeWaiverDetails(feeWaiverDetails);
					AuditHeader auditHeader = getAuditHeader(feeWaiverHeader, PennantConstants.TRAN_WF);
					this.feeWaiverHeaderService.doApprove(auditHeader);
					feeWaiverHeader = (FeeWaiverHeader) auditHeader.getAuditDetail().getModelData();
					feeWaiverUploadDAO.deleteByUploadId(key, "_TEMP");
				}
			}
		}

		logger.debug(Literal.LEAVING);

		return auditDetails;
	}

	private AuditHeader getAuditHeader(FeeWaiverHeader aFeeWaiver, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFeeWaiver.getBefImage(), aFeeWaiver);
		return new AuditHeader(String.valueOf(aFeeWaiver.getWaiverId()), String.valueOf(aFeeWaiver.getWaiverId()), null,
				null, auditDetail, aFeeWaiver.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
	}

	public List<AuditDetail> setWaiverUploadsAuditData(List<FeeWaiverUpload> waiverUploadList, String auditTranType,
			String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FeeWaiverUpload(),
				new FeeWaiverUpload().getExcludeFields());

		for (int i = 0; i < waiverUploadList.size(); i++) {

			FeeWaiverUpload waiverUpload = waiverUploadList.get(i);

			if (StringUtils.isEmpty(waiverUpload.getRecordType())) {
				continue;
			}

			boolean isRcdType = false;
			if (PennantConstants.RCD_ADD.equalsIgnoreCase(waiverUpload.getRecordType())) {
				waiverUpload.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(waiverUpload.getRecordType())) {
				waiverUpload.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(waiverUpload.getRecordType())) {
				waiverUpload.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			if ("saveOrUpdate".equals(method) && isRcdType) {
				waiverUpload.setNewRecord(true);
			}
			if (!PennantConstants.TRAN_WF.equals(auditTranType)) {
				if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(waiverUpload.getRecordType())) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(waiverUpload.getRecordType())
						|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(waiverUpload.getRecordType())) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], waiverUpload.getBefImage(),
					waiverUpload));
		}

		logger.debug(Literal.LEAVING);

		return auditDetails;
	}

	public FeeWaiverHeader prepareHeader(FeeWaiverUpload feeWaiverUpload) {
		logger.debug(Literal.ENTERING);

		long finID = financeMainDAO.getFinID(feeWaiverUpload.getFinReference());

		FeeWaiverHeader feeWaiverHeader = new FeeWaiverHeader();
		feeWaiverHeader.setWaiverId(Long.MIN_VALUE);
		feeWaiverHeader.setFinReference(feeWaiverUpload.getFinReference());
		feeWaiverHeader.setRemarks(feeWaiverUpload.getRemarks());
		feeWaiverHeader.setEvent(FinServiceEvent.FEEWAIVERS);
		feeWaiverHeader.setPostingDate(SysParamUtil.getAppDate());
		feeWaiverHeader.setValueDate(feeWaiverUpload.getValueDate());
		feeWaiverHeader.setFinSourceID(UploadConstants.FINSOURCE_ID_UPLOAD);
		feeWaiverHeader.setVersion(1);
		feeWaiverHeader.setLastMntBy(feeWaiverUpload.getLastMntBy());
		feeWaiverHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		feeWaiverHeader.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		feeWaiverHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		feeWaiverHeader.setNewRecord(true);
		feeWaiverHeader.setUserDetails(feeWaiverUpload.getUserDetails());
		feeWaiverHeader.setWaiverId(feeWaiverUpload.getWaiverId());
		feeWaiverHeader.setFinID(finID);

		logger.debug(Literal.LEAVING);
		return feeWaiverHeader;
	}

	public FeeWaiverHeader processWaiverDetails(FeeWaiverHeader feeWaiverHeader, FeeWaiverUpload feeWaiverUpload) {
		FinanceMain financeMain = this.financeMainDAO.getFinanceMainById(feeWaiverHeader.getFinID(), "", false);
		List<FeeWaiverDetail> feeWaiverDetailsList = new ArrayList<>();
		BigDecimal amount = feeWaiverUpload.getWaivedAmount();

		if (StringUtils.equals(financeMain.getRcdMaintainSts(), FinServiceEvent.FEEWAIVERS)) {
			feeWaiverHeader.setFinReference(financeMain.getFinReference());
			feeWaiverHeader = feeWaiverHeaderService.getFeeWaiverByFinRef(feeWaiverHeader);
		} else {
			// get fee waiver details from manual advise and finoddetails to prepare the list.
			feeWaiverHeader.setNewRecord(true);
			feeWaiverHeader.setFinReference(financeMain.getFinReference());
			feeWaiverHeader = feeWaiverHeaderService.getFeeWaiverByFinRef(feeWaiverHeader);
		}

		feeWaiverDetailsList = doFillFeeWaiverDetails(feeWaiverHeader, feeWaiverDetailsList, feeWaiverUpload);
		for (FeeWaiverDetail detail : feeWaiverDetailsList) {
			String feetypecode = StringUtils.trimToEmpty(feeWaiverUpload.getFeeTypeCode());
			if (feetypecode.equals(detail.getFeeTypeCode().trim())) {
				detail.setLastMntOn(feeWaiverHeader.getLastMntOn());
				if (amount.compareTo(BigDecimal.ZERO) == 0) {
					detail.setCurrWaiverAmount(amount);
					detail.setCurrWaiverGST(BigDecimal.ZERO);
					detail.setCurrActualWaiver(BigDecimal.ZERO);
					// Preparing GST
					prepareGST(detail, amount);
					detail.setBalanceAmount(detail.getReceivableAmount().subtract(detail.getCurrWaiverAmount()));
					break;
				} else {
					detail.setCurrWaiverAmount(amount);
					// Preparing GST
					prepareGST(detail, amount);
					detail.setBalanceAmount(detail.getReceivableAmount().subtract(detail.getCurrWaiverAmount()));
					break;
				}
			}
		}

		return feeWaiverHeader;
	}

	private List<FeeWaiverDetail> doFillFeeWaiverDetails(FeeWaiverHeader feeWaiverHeader,
			List<FeeWaiverDetail> feeWaiverDetailsList, FeeWaiverUpload feeWaiverUpload) {
		logger.debug(Literal.ENTERING);

		if (feeWaiverHeader.isNewRecord()) {
			for (FeeWaiverDetail feeWaiverDetail : feeWaiverHeader.getFeeWaiverDetails()) {
				if (feeWaiverDetail.getBalanceAmount() != null
						&& feeWaiverDetail.getBalanceAmount().compareTo(BigDecimal.ZERO) > 0) {
					if (feeWaiverDetail.getFeeTypeCode().equals(feeWaiverUpload.getFeeTypeCode().trim())) {
						feeWaiverDetail.setWaivedAmount(feeWaiverUpload.getWaivedAmount());
						feeWaiverDetail.setFeeTypeCode(feeWaiverUpload.getFeeTypeCode());
						feeWaiverDetailsList.add(feeWaiverDetail);
					}
				}
			}
		} else {
			updatePaybleAmounts(feeWaiverHeader.getFeeWaiverDetails(), feeWaiverDetailsList);
		}
		feeWaiverHeader.setFeeWaiverDetails(feeWaiverDetailsList);

		logger.debug(Literal.LEAVING);
		return feeWaiverDetailsList;
	}

	private void updatePaybleAmounts(List<FeeWaiverDetail> feeWaiversList, List<FeeWaiverDetail> feeWaiverDetailsList) {
		logger.debug(Literal.ENTERING);

		for (FeeWaiverDetail feeWaiver : feeWaiversList) {
			if (feeWaiver.getReceivableAmount().compareTo(BigDecimal.ZERO) > 0) {
				feeWaiverDetailsList.add(feeWaiver);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void prepareGST(FeeWaiverDetail feeWaiverDetail, BigDecimal waiverAmount) {
		logger.debug(Literal.ENTERING);

		Map<String, BigDecimal> gstPercentages = getTaxPercentages(feeWaiverDetail.getFinID());

		if (feeWaiverDetail.isTaxApplicable()) {
			TaxAmountSplit taxSplit = GSTCalculator.getInclusiveGST(waiverAmount, gstPercentages);
			GSTCalculator.calculateActualGST(feeWaiverDetail, taxSplit, gstPercentages);

			feeWaiverDetail.setCurrActualWaiver(waiverAmount.subtract(taxSplit.gettGST()));
			feeWaiverDetail.setCurrWaiverGST(taxSplit.gettGST());
		} else {
			feeWaiverDetail.setWaivedAmount(waiverAmount);
			feeWaiverDetail.setCurrWaiverAmount(waiverAmount);
			feeWaiverDetail.setCurrActualWaiver(waiverAmount);
			feeWaiverDetail.setCurrWaiverGST(BigDecimal.ZERO);
		}
	}

	private Map<String, BigDecimal> getTaxPercentages(long finID) {
		if (taxPercentages == null) {
			taxPercentages = GSTCalculator.getTaxPercentages(finID);
		}

		return taxPercentages;
	}

	public List<FeeWaiverUpload> getFeeWaiverListByUploadId(long uploadId) {
		return feeWaiverUploadDAO.getFeeWaiverListByUploadId(uploadId, "_View");
	}

	public void setFeeWaiverUploadDAO(FeeWaiverUploadDAO feeWaiverUploadDAO) {
		this.feeWaiverUploadDAO = feeWaiverUploadDAO;
	}

	public void setFeeWaiverHeaderService(FeeWaiverHeaderService feeWaiverHeaderService) {
		this.feeWaiverHeaderService = feeWaiverHeaderService;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

}
