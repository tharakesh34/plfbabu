package com.pennanttech.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.util.APIHeader;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.expenses.UploadHeader;
import com.pennant.backend.model.refundupload.RefundUpload;
import com.pennant.backend.service.finance.UploadHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class RefundUploadController extends ExtendedTestClass {
	private final Logger logger = LogManager.getLogger(getClass());

	private UploadHeaderService uploadHeaderService;

	private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
	private static final String REGIX = "[/:\\s]";

	/**
	 * Method for create Mandate in PLF system.
	 * 
	 * @param mandate
	 * @return Mandate
	 */
	public RefundUpload createRefundUpload(RefundUpload refundUpload) {
		logger.debug("Entering");
		RefundUpload response = null;
		try {
			// setting required values which are not received from API
			prepareRequiredData(refundUpload);
			refundUpload.setRecordType(PennantConstants.RCD_ADD);
			refundUpload.setNewRecord(true);
			refundUpload.setVersion(1);
			refundUpload.setStatus(UploadConstants.REFUND_UPLOAD_STATUS_SUCCESS);

			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);

			String pickUpBatchId = DateUtil.getSysDate(DATE_FORMAT);
			pickUpBatchId = pickUpBatchId.replaceAll(REGIX, "");

			List<RefundUpload> refundUploads = new ArrayList<RefundUpload>();
			UploadHeader uploadHeader = new UploadHeader();
			uploadHeader.setFileName("RefundUpload_API_" + pickUpBatchId + ".csv");
			uploadHeader.setTotalRecords(1);
			uploadHeader.setVersion(1);
			uploadHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			uploadHeader.setNewRecord(true);
			uploadHeader.setUserDetails(refundUpload.getUserDetails());
			uploadHeader.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			uploadHeader.setLastMntBy(refundUpload.getLastMntBy());
			uploadHeader.setLastMntOn(refundUpload.getLastMntOn());
			uploadHeader.setModule(UploadConstants.UPLOAD_MODULE_REFUND);
			uploadHeader.setFinSource(UploadConstants.FINSOURCE_ID_API);
			uploadHeader.setTransactionDate(DateUtil.getSysDate());
			refundUploads.add(refundUpload);
			uploadHeader.setRefundUploads(refundUploads);
			// set the headerDetails to AuditHeader
			AuditHeader auditHeader = getAuditHeader(uploadHeader, PennantConstants.TRAN_WF);
			auditHeader.setApiHeader(reqHeaderDetails);

			auditHeader = uploadHeaderService.doApprove(auditHeader);

			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = new RefundUpload();
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				}
			} else {
				UploadHeader responseUploadHeader = (UploadHeader) auditHeader.getAuditDetail().getModelData();
				if (CollectionUtils.isNotEmpty(responseUploadHeader.getRefundUploads())) {
					response = responseUploadHeader.getRefundUploads().get(0);
					if (UploadConstants.REFUND_UPLOAD_STATUS_FAIL.equals(response.getStatus())) {
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
					} else {
						response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
					}
					doEmptyResponseObject(response);
				}
			}
		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new RefundUpload();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}
		logger.debug("Leaving");

		return response;
	}

	/**
	 * Setting default values from Mandate object
	 * 
	 * @param refundUpload
	 * 
	 */
	private void prepareRequiredData(RefundUpload refundUpload) {
		logger.debug("Entering");

		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		refundUpload.setUserDetails(userDetails);
		refundUpload.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		refundUpload.setLastMntBy(userDetails.getUserId());
		refundUpload.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		refundUpload.setFinSource(UploadConstants.FINSOURCE_ID_API);

		logger.debug("Leaving");
	}

	/**
	 * Nullify the un-necessary objects to prepare response in a structured format specified in API.
	 * 
	 * @param refundUpload
	 */
	private void doEmptyResponseObject(RefundUpload refundUpload) {
		refundUpload.setFinReference(null);
		refundUpload.setPayableAmount(null);
		refundUpload.setType(null);
		refundUpload.setFeeType(null);
		refundUpload.setPaymentDate(null);
		refundUpload.setPaymentType(null);
		refundUpload.setPartnerBank(null);
		refundUpload.setRemarks(null);
		refundUpload.setIFSC(null);
		refundUpload.setMICR(null);
		refundUpload.setAccountNumber(null);
		refundUpload.setAccountHolderName(null);
		refundUpload.setPhoneNumber(null);
		refundUpload.setIssuingBank(null);
		refundUpload.setFavourName(null);
		refundUpload.setPrintingLocation(null);
		refundUpload.setPayableLocation(null);
		refundUpload.setValueDate(null);
		if (UploadConstants.REFUND_UPLOAD_STATUS_SUCCESS.equals(refundUpload.getStatus())) {
			refundUpload.setStatus("Success");
		} else if (UploadConstants.REFUND_UPLOAD_STATUS_FAIL.equals(refundUpload.getStatus())) {
			refundUpload.setStatus("Failed");
		}
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aUploadHeader
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(UploadHeader aUploadHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aUploadHeader.getBefImage(), aUploadHeader);
		return new AuditHeader(String.valueOf(aUploadHeader.getUploadId()), String.valueOf(aUploadHeader.getUploadId()),
				null, null, auditDetail, aUploadHeader.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
	}

	public void setUploadHeaderService(UploadHeaderService uploadHeaderService) {
		this.uploadHeaderService = uploadHeaderService;
	}

}
