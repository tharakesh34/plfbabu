package com.pennanttech.niyogin.holdfinance.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.reason.details.ReasonDetails;
import com.pennant.backend.model.reason.details.ReasonHeader;
import com.pennanttech.logging.model.InterfaceLogDetail;
import com.pennanttech.niyogin.holdfinance.model.HoldFinanceRequest;
import com.pennanttech.niyogin.holdfinance.model.HoldReason;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.HoldFinanceService;
import com.pennanttech.pff.external.service.NiyoginService;

public class HoldFinanceServiceImpl extends NiyoginService implements HoldFinanceService {

	private static final Logger	logger				= Logger.getLogger(HoldFinanceServiceImpl.class);
	private String				extConfigFileName	= "holdFinance";
	private String				serviceUrl;

	/**
	 * Method for execute Hold Loan service<br>
	 * 
	 * @param auditHeader
	 */
	@Override
	public AuditHeader executeHoldFinance(AuditHeader auditHeader) throws InterfaceException {
		logger.debug(Literal.ENTERING);
		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String finReference = financeMain.getFinReference();
		if (financeDetail.getReasonHeader()==null) {
			return auditHeader;
		}
		HoldFinanceRequest holdFinanceRequest = prepareRequestObj(financeDetail);
		Map<String, Object> validatedMap = null;
		Map<String, Object> extendedFieldMap = null;

		// logging fields Data
		reqSentOn = new Timestamp(System.currentTimeMillis());
		reference = finReference;

		try {
			extendedFieldMap = post(serviceUrl, holdFinanceRequest, extConfigFileName);
		} catch (InterfaceException e) {
			throw new InterfaceException(e.getErrorCode(), e.getErrorMessage());
		}

		try {
			validatedMap = validateExtendedMapValues(extendedFieldMap);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			doLogError(e, serviceUrl, holdFinanceRequest);
			throw new InterfaceException("9999", e.getMessage());
		}

		// success case logging
		doInterfaceLogging(holdFinanceRequest, finReference);
		prepareResponseObj(validatedMap, financeDetail);
		logger.debug(Literal.LEAVING);
		return null;
	}

	/**
	 * Method for prepare the HoldFinance request object.
	 * 
	 * @param financeDetail
	 * @return
	 */
	private HoldFinanceRequest prepareRequestObj(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		Customer customer = financeDetail.getCustomerDetails().getCustomer();

		HoldFinanceRequest holdFinanceRequest = new HoldFinanceRequest();
		holdFinanceRequest.setLoanReference(financeDetail.getFinScheduleData().getFinanceMain().getFinReference());
		holdFinanceRequest.setCif(customer.getCustCIF());
		holdFinanceRequest.setCustomerName(customer.getCustShrtName());

		ReasonHeader reasonHeader = financeDetail.getReasonHeader();
		List<ReasonDetails> detailsList = reasonHeader.getDetailsList();
		List<HoldReason> holdReasons = null;

		if (detailsList != null && !detailsList.isEmpty()) {
			List<Long> idList = new ArrayList<>();
			for(ReasonDetails detail: detailsList) {
				idList.add(detail.getReasonId());
			}
			holdReasons = getholdReasonsById(idList);
		}
		if (holdReasons != null && !holdReasons.isEmpty()) {
			holdFinanceRequest.setHoldCategory(String.valueOf(holdReasons.get(0).getHoldCatageory()));
			holdFinanceRequest.setHoldReasons(holdReasons);
		}
		holdFinanceRequest.setRemarks(reasonHeader.getRemarks());
		logger.debug(Literal.LEAVING);
		return holdFinanceRequest;
	}

	/**
	 * Method for prepare data and logging
	 * 
	 * @param holdFinanceRequest
	 * @param reference
	 */
	private void doInterfaceLogging(HoldFinanceRequest holdFinanceRequest, String reference) {
		InterfaceLogDetail interfaceLogDetail = prepareLoggingData(serviceUrl, holdFinanceRequest, jsonResponse,
				reqSentOn, status, errorCode, errorDesc, reference);
		logInterfaceDetails(interfaceLogDetail);
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

}
