package com.pennanttech.niyogin.holdfinance.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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
import com.pennanttech.pff.InterfaceConstants;
import com.pennanttech.pff.external.HoldFinanceService;
import com.pennanttech.pff.external.service.NiyoginService;

public class HoldFinanceServiceImpl extends NiyoginService implements HoldFinanceService {

	private static final Logger	logger				= Logger.getLogger(HoldFinanceServiceImpl.class);
	private String				extConfigFileName	= "holdFinance.properties";
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

		if (financeDetail.getReasonHeader() == null) {
			return auditHeader;
		}
		HoldFinanceRequest holdFinanceRequest = prepareRequestObj(financeDetail);
		Map<String, Object> appplicationdata = new HashMap<>();
		//send request and log
		String reference = financeMain.getFinReference();
		String errorCode = null;
		String errorDesc = null;
		String reuestString = null;
		String jsonResponse = null;

		try {
			reuestString = client.getRequestString(holdFinanceRequest);
			jsonResponse = client.post(serviceUrl, reuestString);
			//check response for error
			errorCode = getErrorCode(jsonResponse);
			errorDesc = getErrorMessage(jsonResponse);

			doInterfaceLogging(reference, reuestString, jsonResponse, errorCode, errorDesc);
			if (StringUtils.isEmpty(errorCode)) {
				Map<String, Object> mapdata = getPropValueFromResp(jsonResponse, extConfigFileName);
				Map<String, Object> mapvalidData = validateExtendedMapValues(mapdata);
				appplicationdata.putAll(mapvalidData);
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
			errorDesc = getWriteException(e);
			errorDesc = getTrimmedMessage(errorDesc);
			doExceptioLogging(reference, reuestString, jsonResponse, errorDesc);
		}
		prepareResponseObj(appplicationdata, financeDetail);
		logger.debug(Literal.LEAVING);
		return auditHeader;
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
			for (ReasonDetails detail : detailsList) {
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
	 * Method for prepare Success logging
	 * 
	 * @param reference
	 * @param requets
	 * @param response
	 * @param errorCode
	 * @param errorDesc
	 */
	private void doInterfaceLogging(String reference, String requets, String response, String errorCode,
			String errorDesc) {
		logger.debug(Literal.ENTERING);
		InterfaceLogDetail iLogDetail = new InterfaceLogDetail();
		iLogDetail.setReference(reference);
		String[] values = serviceUrl.split("/");
		iLogDetail.setServiceName(values[values.length - 1]);
		iLogDetail.setEndPoint(serviceUrl);
		iLogDetail.setRequest(requets);
		iLogDetail.setReqSentOn(new Timestamp(System.currentTimeMillis()));

		iLogDetail.setResponse(response);
		iLogDetail.setRespReceivedOn(new Timestamp(System.currentTimeMillis()));
		iLogDetail.setStatus(InterfaceConstants.STATUS_SUCCESS);
		iLogDetail.setErrorCode(errorCode);
		if (errorDesc != null && errorDesc.length() > 200) {
			iLogDetail.setErrorDesc(errorDesc.substring(0, 190));
		}

		logInterfaceDetails(iLogDetail);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for failure logging.
	 * 
	 * @param reference
	 * @param requets
	 * @param response
	 * @param errorCode
	 * @param errorDesc
	 */
	private void doExceptioLogging(String reference, String requets, String response, String errorDesc) {
		logger.debug(Literal.ENTERING);
		InterfaceLogDetail iLogDetail = new InterfaceLogDetail();
		iLogDetail.setReference(reference);
		String[] values = serviceUrl.split("/");
		iLogDetail.setServiceName(values[values.length - 1]);
		iLogDetail.setEndPoint(serviceUrl);
		iLogDetail.setRequest(requets);
		iLogDetail.setReqSentOn(new Timestamp(System.currentTimeMillis()));

		iLogDetail.setResponse(response);
		iLogDetail.setRespReceivedOn(new Timestamp(System.currentTimeMillis()));
		iLogDetail.setStatus(InterfaceConstants.STATUS_FAILED);
		iLogDetail.setErrorCode(InterfaceConstants.ERROR_CODE);
		iLogDetail.setErrorDesc(errorDesc);

		logInterfaceDetails(iLogDetail);
		logger.debug(Literal.LEAVING);
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

}
