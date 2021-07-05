package com.pennanttech.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.BounceReasonDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.financemanagement.PresentmentDetailDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.applicationmaster.BounceReason;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.PresentmentHeader;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.service.financemanagement.PresentmentDetailService;
import com.pennant.backend.service.partnerbank.PartnerBankService;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.ExtendedTestClass;
import com.pennanttech.controller.PresentmentServiceController;
import com.pennanttech.model.presentment.Presentment;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pffws.PresentmentRestService;
import com.pennanttech.pffws.PresentmentSoapService;
import com.pennanttech.ws.model.presentment.PresentmentResponse;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class PresentmentWebServiceImpl extends ExtendedTestClass
		implements PresentmentRestService, PresentmentSoapService {
	private static final Logger logger = LogManager.getLogger(PresentmentWebServiceImpl.class);

	private PresentmentDetailService presentmentDetailService;
	private PresentmentServiceController presentmentServiceController;
	private PartnerBankService partnerBankService;
	private BounceReasonDAO bounceReasonDAO;
	private FinanceMainDAO financeMainDAO;
	private PresentmentDetailDAO presentmentDetailDAO;

	@Override
	public PresentmentResponse extractPresentmentDetails(PresentmentHeader header) throws ServiceException {
		logger.info("Processing the presentment extaction request...");

		PresentmentResponse response = new PresentmentResponse();

		String presentmentType = header.getPresentmentType();
		String entityCode = header.getEntityCode();
		String mandateType = header.getMandateType();
		Date fromDate = header.getFromDate();
		Date toDate = header.getToDate();
		Date appDate = SysParamUtil.getAppDate();
		int alwdDaysFromAppDate = SysParamUtil.getValueAsInt("PRESENTMENT_EXTRACT_ALW_DAYS_FROM_APP_DATE");
		int daysbetweenFromAndTo = DateUtil.getDaysBetween(toDate, appDate);

		logger.info("Presentment Type >>{}", presentmentType);
		logger.info("Entity Code >>{}", entityCode);
		logger.info("Mandate Type >>{}", mandateType);
		logger.info("From Date >>{}", fromDate);
		logger.info("To Date >>{}", toDate);
		logger.info("App Date >>{}", appDate);
		logger.info("Extraction allowed days between appdate and to date >>{}", alwdDaysFromAppDate);
		logger.info("Number of days between from date and to date >>{}", daysbetweenFromAndTo);

		if (StringUtils.isBlank(presentmentType)) {
			String[] valueParm = new String[1];
			valueParm[0] = "Presentment Type";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return response;
		}

		if (!PennantConstants.PROCESS_PRESENTMENT.equals(presentmentType)
				&& !PennantConstants.PROCESS_REPRESENTMENT.equals(presentmentType)) {
			String[] valueParm = new String[1];
			valueParm[0] = "Presentment Type";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return response;
		}

		if (StringUtils.isBlank(entityCode)) {
			String[] valueParm = new String[1];
			valueParm[0] = "Entity Code";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return response;
		}

		if (StringUtils.isBlank(mandateType)) {
			String[] valueParm = new String[1];
			valueParm[0] = "Payment Mode";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return response;
		}

		if (!isValidPaymentMode(mandateType)) {
			String[] valueParm = new String[4];
			valueParm[0] = "Payment";
			valueParm[1] = "Mode";
			valueParm[2] = "is";
			valueParm[3] = "Invalid";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("30550", valueParm));
			return response;
		}

		if (fromDate == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "From Date";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return response;
		}

		if (toDate == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "To Date";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return response;
		}
		if (toDate.compareTo(fromDate) < 0) {
			String[] valueParm = new String[4];
			valueParm[0] = "To Date";
			valueParm[1] = "should be";
			valueParm[2] = "greater than";
			valueParm[3] = "from date";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("30550", valueParm));
			return response;
		}

		int diffentDays = SysParamUtil.getValueAsInt("PRESENTMENT_DAYS_DEF");

		if (DateUtil.getDaysBetween(fromDate, toDate) >= diffentDays) {
			String[] valueParm = new String[4];
			valueParm[0] = "From Date";
			valueParm[1] = "and To Date";
			valueParm[2] = "difference should be less than or equal to";
			valueParm[3] = String.valueOf(diffentDays);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("30550", valueParm));
			return response;
		}

		if (alwdDaysFromAppDate > 0 && daysbetweenFromAndTo >= alwdDaysFromAppDate) {
			String[] valueParm = new String[4];
			valueParm[0] = "To Date";
			valueParm[1] = "and App Date";
			valueParm[2] = "difference should be less than or equal to";
			valueParm[3] = String.valueOf(alwdDaysFromAppDate);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("30550", valueParm));
			return response;
		}

		response = presentmentServiceController.getExtractedPresentments(header);

		logger.info("Presentment extaction request processed successfully.");
		return response;
	}

	private boolean isValidPaymentMode(String mandateType) {
		return MandateConstants.TYPE_DDM.equals(mandateType) || MandateConstants.TYPE_ECS.equals(mandateType)
				|| MandateConstants.TYPE_NACH.equals(mandateType) || MandateConstants.TYPE_PDC.equals(mandateType)
				|| MandateConstants.TYPE_EMANDATE.equals(mandateType);
	}

	@Override
	public PresentmentResponse approvePresentmentDetails(PresentmentHeader presentmentHeader) throws ServiceException {
		logger.debug(Literal.ENTERING);
		PresentmentResponse response = new PresentmentResponse();

		long headerId = presentmentHeader.getId();
		String reference = presentmentHeader.getReference();
		long partnerBankId = presentmentHeader.getPartnerBankId();

		logger.info("PresentmentHeader Id >>{}", headerId);
		logger.info("Presentment Batch Reference >>{}", reference);
		logger.info("PartnerBankId>>{}", partnerBankId);

		// fetching presentment Header by presentment Header Id
		if (headerId <= 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "Presentment Header Id";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return response;
		}

		PresentmentHeader presentHeader = presentmentDetailService.getPresentmentHeader(headerId);
		if (presentHeader == null) {
			String[] valueParm = new String[2];
			valueParm[0] = "presentment";
			valueParm[1] = "HeaderId";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("41002", valueParm));
			return response;
		}
		if (StringUtils.isBlank(reference)) {
			String[] valueParm = new String[1];
			valueParm[0] = "Batch Reference";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return response;
		}
		if (!StringUtils.equals(presentHeader.getReference(), reference)) {
			String[] valueParm = new String[2];
			valueParm[0] = "Batch";
			valueParm[1] = "Reference";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("41002", valueParm));
			return response;
		}
		if (partnerBankId <= 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "PartnerBank Id";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return response;
		}
		PartnerBank partnerBank = partnerBankService.getApprovedPartnerBankById(partnerBankId);
		if (partnerBank == null) {
			String[] valueParm = new String[2];
			valueParm[0] = "PartnerBank";
			valueParm[1] = "Id";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("41002", valueParm));
			return response;
		}
		if (!StringUtils.equals(partnerBank.getEntity(), presentHeader.getEntityCode())) {
			String[] valueParm = new String[2];
			valueParm[0] = "PartnerBank";
			valueParm[1] = "Id";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("41002", valueParm));
			return response;
		}
		if (!partnerBank.isAlwReceipt()) {
			String[] valueParm = new String[2];
			valueParm[0] = "PartnerBank";
			valueParm[1] = "Id";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("41002", valueParm));
			return response;
		}

		// calling controller to approve Presentment Details
		presentHeader.setPartnerBankId(presentmentHeader.getPartnerBankId());
		response = presentmentServiceController.approvePresentments(presentHeader);

		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public PresentmentResponse getApprovedPresentment(PresentmentDetail pd) throws ServiceException {
		logger.debug(Literal.ENTERING);

		long id = pd.getId();
		String finReference = pd.getFinReference();

		logger.info("PresentmentDetail Id{}", id);
		logger.info("FinReference>>{}", finReference);

		PresentmentResponse response = new PresentmentResponse();
		if (id <= 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "Presentment Id";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return response;
		}
		if (StringUtils.isBlank(finReference)) {
			String[] valueParm = new String[1];
			valueParm[0] = "FinReference";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return response;
		}
		response = presentmentServiceController.getApprovedPresentment(pd);
		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public WSReturnStatus uploadPresentment(Presentment presentment) throws ServiceException {
		logger.debug(Literal.ENTERING);

		// validating presentments
		String batchId = presentment.getBatchId();
		String status = presentment.getStatus();
		String returnReason = presentment.getReturnReason();
		String agreementNo = presentment.getAgreementNo();

		logger.info("Presentment BatchId", batchId);
		logger.info("Status >>{}", status);
		logger.info("ReturnReason >>{}", returnReason);
		logger.info("FinReference >>{}", agreementNo);

		if (StringUtils.isBlank(batchId)) {
			String[] valueParm = new String[1];
			valueParm[0] = "Unique Reference";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}
		if (batchId.length() != 29) {
			String[] valueParm = new String[4];
			valueParm[0] = "Unique Reference";
			valueParm[1] = "length";
			valueParm[2] = "should be ";
			valueParm[3] = "29";
			return APIErrorHandlerService.getFailedStatus("30550", valueParm);
		}
		if (StringUtils.isBlank(status)) {
			String[] valueParm = new String[1];
			valueParm[0] = "Presentment Status";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}
		int statusLength = status.length();
		int minLength = ImplementationConstants.PRESENTMENT_EXPORT_STATUS_MIN_LENGTH;
		int maxLength = ImplementationConstants.PRESENTMENT_EXPORT_STATUS_MAX_LENGTH;
		if (statusLength != minLength && statusLength != maxLength) {
			String[] valueParm = new String[4];
			valueParm[0] = "Status";
			valueParm[1] = "length";
			valueParm[2] = "should be";
			valueParm[3] = "minimum";
			return APIErrorHandlerService.getFailedStatus("30550", valueParm);
		}

		if (status != null && !(StringUtils.equals(RepayConstants.PAYMENT_PAID, status))
				&& !(StringUtils.equals(RepayConstants.PAYMENT_SUCCESS, status))) {
			if (StringUtils.isBlank(returnReason)) {
				String[] valueParm = new String[1];
				valueParm[0] = "Reject Reason";
				return APIErrorHandlerService.getFailedStatus("90502", valueParm);
			}
		}
		if (StringUtils.isBlank(agreementNo)) {
			String[] valueParm = new String[1];
			valueParm[0] = "FinReference";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}

		if (agreementNo.length() > 14) {
			String[] valueParm = new String[4];
			valueParm[0] = "FinReference";
			valueParm[1] = "length";
			valueParm[2] = "should be";
			valueParm[3] = "less than 15";
			return APIErrorHandlerService.getFailedStatus("30550", valueParm);
		}
		if (RepayConstants.PEXC_FAILURE.equals(status)) {
			BounceReason bounceReason = bounceReasonDAO.getBounceReasonByReturnCode(returnReason, "");
			if (bounceReason == null) {
				String[] valueParm = new String[4];
				valueParm[0] = "Reject";
				valueParm[1] = "Reason";
				valueParm[2] = "is";
				valueParm[3] = "Invalid";
				return APIErrorHandlerService.getFailedStatus("30550", valueParm);
			}
		}
		WSReturnStatus wsReturnStatus = presentmentServiceController.uploadPresentment(presentment);

		logger.debug(Literal.LEAVING);
		return wsReturnStatus;
	}

	@Override
	public PresentmentResponse getPresentmentStatus(String finReference) throws ServiceException {
		PresentmentResponse response = new PresentmentResponse();

		if (StringUtils.isBlank(finReference)) {
			String[] valueParm = new String[1];
			valueParm[0] = "FinReference";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return response;
		}

		if (financeMainDAO.getFinanceCountById(finReference, "", false) <= 0) {
			String valueParm[] = new String[2];
			valueParm[0] = finReference;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
			return response;
		}
		List<PresentmentDetail> statusByFinRef = presentmentDetailDAO.getPresentmentStatusByFinRef(finReference);

		if (CollectionUtils.isEmpty(statusByFinRef)) {
			String[] valueParm = new String[4];
			valueParm[0] = "No";
			valueParm[1] = "Presentments";
			valueParm[2] = "should be";
			valueParm[3] = "Avaiable with Finreference: " + finReference;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("30550", valueParm));
			return response;
		} else {
			response.setPresentmentDetails(statusByFinRef);
		}

		response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		return response;
	}

	@Autowired
	public void setPresentmentDetailService(PresentmentDetailService presentmentDetailService) {
		this.presentmentDetailService = presentmentDetailService;
	}

	@Autowired
	public void setPresentmentServiceController(PresentmentServiceController presentmentServiceController) {
		this.presentmentServiceController = presentmentServiceController;
	}

	@Autowired
	public void setPartnerBankService(PartnerBankService partnerBankService) {
		this.partnerBankService = partnerBankService;
	}

	@Autowired
	public void setBounceReasonDAO(BounceReasonDAO bounceReasonDAO) {
		this.bounceReasonDAO = bounceReasonDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setPresentmentDetailDAO(PresentmentDetailDAO presentmentDetailDAO) {
		this.presentmentDetailDAO = presentmentDetailDAO;
	}
}
