package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.receiptupload.ReceiptUploadDetail;
import com.pennant.backend.model.receiptupload.UploadAlloctionDetail;
import com.pennant.backend.service.finance.ReceiptUploadHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.ReceiptUploadConstants.ReceiptDetailStatus;
import com.pennant.batchupload.fileprocessor.BatchUploadProcessorConstatnt;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.presentment.model.PresentmentDetail;

public class ReceiptResponseProcess {
	private static final Logger logger = LogManager.getLogger(ReceiptResponseProcess.class);

	private ReceiptUploadHeaderService receiptUploadHeaderService;

	StringBuilder remarks = null;
	PresentmentDetail detail = null;
	int recordCount = 0;
	int successCount = 0;
	int failedCount = 0;
	long batchId = 0;

	// Constant values used in the interface
	private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
	private static final String REGIX = "[/:\\s]";

	public ReceiptResponseProcess(ReceiptUploadHeaderService receiptUploadHeaderService) {
		this.receiptUploadHeaderService = receiptUploadHeaderService;
	}

	public void processResponse() {
		logger.debug(Literal.ENTERING);

		recordCount = 0;
		successCount = 0;
		failedCount = 0;
		batchId = 0;
		remarks = null;

		String procName = "RECEIPT_RESPONSE_SCHEDULE";
		procName = procName.concat("_").concat(DateUtil.getSysDate(DateFormat.LONG_DATE_TIME));
		batchId = receiptUploadHeaderService.saveReceiptResponseFileHeader(procName);
		String date = DateUtil.getSysDate(DATE_FORMAT);
		date = date.replaceAll(REGIX, "");
		long jobid = Long.valueOf(date);

		List<ReceiptUploadDetail> listReceiptDetails = receiptUploadHeaderService.getReceiptResponseDetails();

		// update list of data with job id for not repeat again
		this.receiptUploadHeaderService.updatePickBatchId(jobid);

		if (!doCheckConnectionEstablished()) {
			for (ReceiptUploadDetail receiptUpladDetail : listReceiptDetails) {

				receiptUpladDetail.setProcessingStatus(ReceiptDetailStatus.SUCCESS.getValue());
				receiptUpladDetail.setReason(Labels.getLabel("label_ReceiptUpload_Connection_API_Failed"));

				if (ReceiptDetailStatus.SUCCESS.getValue() == receiptUpladDetail.getProcessingStatus()) {
					successCount = successCount + 1;
				} else {
					failedCount = failedCount + 1;
				}

				receiptUploadHeaderService.updateReceiptResponseDetails(receiptUpladDetail, jobid);
			}
			listReceiptDetails = new ArrayList<>();
		}

		for (ReceiptUploadDetail receiptUploadDetail : listReceiptDetails) {

			List<UploadAlloctionDetail> listAllocationDetails = receiptUploadHeaderService
					.getReceiptResponseAllocationDetails(receiptUploadDetail.getRootId());
			receiptUploadDetail.setListAllocationDetails(listAllocationDetails);

			// mapping details to json object
			Map<String, Object> mapList = createJsonObject(receiptUploadDetail);

			// get respose,check whether it is success or not
			ReceiptUploadDetail receiptresponseDetail = callApi(mapList);

			if (ReceiptDetailStatus.SUCCESS.getValue() == receiptresponseDetail.getProcessingStatus()) {
				successCount = successCount + 1;
			} else {
				failedCount = failedCount + 1;
			}

			receiptUploadHeaderService.updateReceiptResponseDetails(receiptresponseDetail, jobid);

		}

		// Update the Status of the file as Reading Successful
		remarks = new StringBuilder();
		if (failedCount > 0) {
			remarks.append(" Completed with exceptions, total Records: ");
			remarks.append(recordCount);
			remarks.append(", Sucess: ");
			remarks.append(successCount + ".");
			remarks.append(", Failure: ");
			remarks.append(failedCount + ".");
		} else {
			remarks.append(" Completed successfully, total Records: ");
			remarks.append(recordCount);
			remarks.append(", Sucess: ");
			remarks.append(successCount + ".");
		}
		receiptUploadHeaderService.updateReceiptResponseFileHeader(batchId, recordCount, successCount, failedCount,
				remarks.toString());

		logger.debug(Literal.LEAVING);
	}

	private boolean doCheckConnectionEstablished() {
		WebClient client = null;
		Response response = null;

		try {
			String url = SysParamUtil.getValueAsString("RECEIPTAPIURL");
			client = getClient(url, "1231212");
			response = client.get();
		} catch (Exception e) {
			client.close();
			client = null;
			return false;
		} finally {
			if (client == null) {
				return false;
			} else {
				client.close();
			}
			client = null;
		}

		// Connection Failed
		if (response.getStatus() == 404) {
			return false;
		}

		return true;
	}

	private Map<String, Object> createJsonObject(ReceiptUploadDetail receiptUploadDetail) {

		JSONObject reqJson = new JSONObject();
		String errorCode = null;
		String errorMsg = null;

		Map<String, Object> mapList = new HashMap<>();

		// receipt purpose
		String url = SysParamUtil.getValueAsString("RECEIPTAPIURL");
		if (StringUtils.equalsIgnoreCase(receiptUploadDetail.getReceiptPurpose(), "ES")) {
			url = url + "finInstructionRest/loanInstructionService/earlySettlement";

			// fromDate
			reqJson.put("fromDate",
					DateUtil.format(receiptUploadDetail.getReceivedDate(), PennantConstants.APIDateFormatter));
		} else if (StringUtils.equalsIgnoreCase(receiptUploadDetail.getReceiptPurpose(), "SP")) {
			url = url + "finInstructionRest/loanInstructionService/manualPayment";
		} else if (StringUtils.equalsIgnoreCase(receiptUploadDetail.getReceiptPurpose(), "EP")) {
			url = url + "finInstructionRest/loanInstructionService/partialSettlement";
		} else {
			url = " ";
			errorMsg = Labels.getLabel("inValid_ReceiptPurpose");
			errorCode = PennantConstants.ERR_9999;
		}

		// root id
		try {
			reqJson.put("rootId", receiptUploadDetail.getId());
		} catch (Exception e) {
			if (StringUtils.isEmpty(errorCode)) {
				errorMsg = Labels.getLabel("inValid_RootID");
				errorCode = PennantConstants.ERR_9999;
			}
		}

		// Receipt Purpose
		reqJson.put("receiptPurpose", receiptUploadDetail.getReceiptPurpose());

		// reCalType
		reqJson.put("reCalType", receiptUploadDetail.getEffectSchdMethod());

		// Finance Reference
		reqJson.put("finReference", receiptUploadDetail.getReference());

		// Receipt Amount
		try {
			if (StringUtils.isBlank(String.valueOf(receiptUploadDetail.getReceiptAmount()))) {
				reqJson.put("amount", BigDecimal.ZERO);
			} else {
				reqJson.put("amount", receiptUploadDetail.getReceiptAmount().multiply(new BigDecimal(100)));
			}
		} catch (Exception e) {
			if (StringUtils.isEmpty(errorCode)) {
				errorMsg = Labels.getLabel("inValid_ReceiptAmount");
				errorCode = PennantConstants.ERR_9999;
			}
		}

		// Receipt Mode
		reqJson.put("paymentMode", receiptUploadDetail.getReceiptMode());

		// Excess Adjust TO
		reqJson.put("excessAdjustTo", receiptUploadDetail.getExcessAdjustTo());

		// Allocation Type
		reqJson.put("allocationType", receiptUploadDetail.getAllocationType());

		// Remarks
		reqJson.put("remarks", receiptUploadDetail.getRemarks());

		// Value Date
		try {
			reqJson.put("valueDate",
					DateUtil.format(receiptUploadDetail.getValueDate(), PennantConstants.APIDateFormatter));
		} catch (Exception e) {
			if (StringUtils.isEmpty(errorCode)) {
				errorMsg = Labels.getLabel("inValid_ValueDate");
				errorCode = PennantConstants.ERR_9999;
			}
		}

		// Receipt Received Date/Receipt Value Date
		try {
			reqJson.put("receivedDate",
					DateUtil.format(receiptUploadDetail.getReceivedDate(), PennantConstants.APIDateFormatter));
		} catch (Exception e) {
			if (StringUtils.isEmpty(errorCode)) {
				errorMsg = Labels.getLabel("inValid_ReceivedDate");
				errorCode = PennantConstants.ERR_9999;
			}
		}

		// Partner Bank
		reqJson.put("depositAccount", receiptUploadDetail.getFundingAc());

		// Transaction Reference
		reqJson.put("transactionRef", receiptUploadDetail.getTransactionRef());

		// Payment Reference
		reqJson.put("paymentRef", receiptUploadDetail.getPaymentRef());

		// Favour Number
		reqJson.put("favourNumber", receiptUploadDetail.getFavourNumber());

		// Bank Code
		reqJson.put("bankCode", receiptUploadDetail.getBankCode());

		// Cheque Number
		reqJson.put("chequeNo", receiptUploadDetail.getChequeNo());

		// Status
		reqJson.put("status", receiptUploadDetail.getStatus());

		// Deposit Date
		try {
			reqJson.put("depositDate",
					DateUtil.format(receiptUploadDetail.getDepositDate(), PennantConstants.APIDateFormatter));
		} catch (Exception e) {
			if (StringUtils.isEmpty(errorCode)) {
				errorMsg = Labels.getLabel("inValid_DepositDate");
				errorCode = PennantConstants.ERR_9999;
			}
		}

		// Realization Date
		try {
			reqJson.put("realizationDate",
					DateUtil.format(receiptUploadDetail.getRealizationDate(), PennantConstants.APIDateFormatter));
		} catch (Exception e) {
			if (StringUtils.isEmpty(errorCode)) {
				errorMsg = Labels.getLabel("inValid_RealizationDate");
				errorCode = PennantConstants.ERR_9999;
			}
		}

		// Instrument Date -- Not using
		try {
			reqJson.put("instrumentDate",
					DateUtil.format(receiptUploadDetail.getInstrumentDate(), PennantConstants.APIDateFormatter));
		} catch (Exception e) {
			if (StringUtils.isEmpty(errorCode)) {
				errorMsg = Labels.getLabel("inValid_InstrumentDate");
				errorCode = PennantConstants.ERR_9999;
			}
		}

		reqJson.put("reqType", "Post");
		reqJson.put("isUpload", true);
		reqJson.put("receiptResponse", true);// for response job
		// reqJson.put("entity", this.receiptUploadHeader.getEntityCode());
		// reqJson.put("entityDesc",
		// this.receiptUploadHeader.getEntityCodeDesc());

		JSONArray allocationDetailsReq = new JSONArray();
		Map<String, Boolean> keyMap = new HashMap<>();

		// Reading Allocations based on ROOT ID
		for (UploadAlloctionDetail uploadAllocationDetails : receiptUploadDetail.getListAllocationDetails()) {

			JSONObject allocation = new JSONObject();

			// Allocation Type
			allocation.put("allocationType", uploadAllocationDetails.getAllocationType());

			// Allocation To
			allocation.put("referenceCode", uploadAllocationDetails.getReferenceCode());

			// Allocation Paid Amount
			if (!StringUtils.isBlank(String.valueOf(uploadAllocationDetails.getPaidAmount()))) {
				allocation.put("paidAmount", uploadAllocationDetails.getPaidAmount().multiply(new BigDecimal(100)));
			} else {
				allocation.put("paidAmount", BigDecimal.ZERO);
			}

			if (!StringUtils.isBlank(String.valueOf(uploadAllocationDetails.getWaivedAmount()))) {
				allocation.put("waivedAmount", uploadAllocationDetails.getWaivedAmount().multiply(new BigDecimal(100)));
			} else {
				allocation.put("waivedAmount", BigDecimal.ZERO);
			}

			allocationDetailsReq.put(allocation);

			String key = uploadAllocationDetails.getAllocationType();
			if (StringUtils.isNotEmpty(uploadAllocationDetails.getReferenceCode())
					&& (StringUtils.contains(uploadAllocationDetails.getAllocationType(), "M")
							|| StringUtils.contains(uploadAllocationDetails.getAllocationType(), "B")
							|| StringUtils.contains(uploadAllocationDetails.getAllocationType(), "F"))) {
				key = key + "_" + uploadAllocationDetails.getReferenceCode();
			}
			if (keyMap.containsKey(key) && StringUtils.isEmpty(errorCode)) {
				errorMsg = Labels.getLabel("Duplicate_AllocationDetail");
				errorCode = PennantConstants.ERR_9999;
			}
			keyMap.put(key, true);

		}

		reqJson.put("allocationDetails", allocationDetailsReq);

		Calendar calendar = Calendar.getInstance();
		String messageId = calendar.getTimeInMillis() + "/" + receiptUploadDetail.getRootId();

		mapList.put("jsonObject", reqJson);
		mapList.put("errorCode", errorCode);
		mapList.put("errorMsg", errorMsg);
		mapList.put("url", url);
		mapList.put("messageId", messageId);
		mapList.put("rootId", receiptUploadDetail.getId());

		logger.info("New json request:" + reqJson.toString());
		return mapList;

	}

	/**
	 * calling api with json object and returning array as response
	 * 
	 * @param jsondata
	 * @param messageId
	 * @return
	 * @throws Exception
	 */
	private ReceiptUploadDetail callApi(Map<String, Object> mapList) {

		String errorMsg = (String) mapList.get("errorMsg");
		String errorCode = (String) mapList.get("errorCode");
		WebClient client = null;
		String extraHeaderValue = null;
		String[] responseArray = new String[4];
		String headerMessageId = (String) mapList.get("messageId");
		String url = (String) mapList.get("url");
		JSONObject jsondata = (JSONObject) mapList.get("jsonObject");
		ReceiptUploadDetail receiptUploadDetail = new ReceiptUploadDetail();

		try {

			// API CALL for ENQUIRY RESULT
			if (StringUtils.isNotBlank(url) && StringUtils.isEmpty(errorCode)) {

				client = getClient(url, headerMessageId);
				Response response = client.post(jsondata.toString());

				String body = response.readEntity(String.class);
				if (headerMessageId == null && StringUtils.isBlank(body)) {
					throw new RuntimeException(BatchUploadProcessorConstatnt.UNABLE_TO_PROCESS);
				}

				logger.info("MESSAGEID :: " + headerMessageId + "  API RESPONSE :: " + body);

				if (response.getStatus() == 200 && body != null) {

					JSONObject parentBody = new JSONObject(body);
					if (!parentBody.isNull(BatchUploadProcessorConstatnt.FIN_REFERENCE)) {
						extraHeaderValue = String.valueOf(parentBody.get(BatchUploadProcessorConstatnt.FIN_REFERENCE));
					}
					parentBody = parentBody.getJSONObject(BatchUploadProcessorConstatnt.RETURN_STATUS);
					errorMsg = parentBody.getString(BatchUploadProcessorConstatnt.RETURN_TEXT);
					errorCode = parentBody.getString(BatchUploadProcessorConstatnt.RETURN_CODE);

				} else {
					errorCode = String.valueOf(response.getStatus());
					errorMsg = String.valueOf(response.getEntity());
				}
			}
		} finally {
			if (client != null) {
				client.close();
				client = null;
			}
		}

		responseArray[0] = errorCode;
		responseArray[1] = errorMsg;
		responseArray[2] = extraHeaderValue;
		responseArray[3] = headerMessageId;

		receiptUploadDetail.setId((long) mapList.get("rootId"));
		if (StringUtils.equals("0000", errorCode)) {
			receiptUploadDetail.setProcessingStatus(ReceiptDetailStatus.SUCCESS.getValue());
			receiptUploadDetail.setReason("");
		} else {
			receiptUploadDetail.setProcessingStatus(ReceiptDetailStatus.FAILED.getValue());
			receiptUploadDetail.setReason(errorCode + " : " + errorMsg);
		}
		return receiptUploadDetail;

	}

	/**
	 * util method to get Webclient
	 * 
	 * @param serviceEndPoint url to hit the api
	 * @param messageId       to pass in input header
	 */
	private WebClient getClient(String serviceEndPoint, String messageId) {
		String authorization = SysParamUtil.getValueAsString("URLAuthorization");
		WebClient client = null;
		try {
			client = WebClient.create(serviceEndPoint);
			client.accept(MediaType.APPLICATION_JSON);
			client.type(MediaType.APPLICATION_JSON);
			client.header(BatchUploadProcessorConstatnt.AUTHORIZATION_KEY, authorization);
			client.header(BatchUploadProcessorConstatnt.MESSAGE_ID, messageId);

		} catch (Exception e) {
			logger.error(BatchUploadProcessorConstatnt.EXCEPTION, e);
		}
		return client;
	}

	public ReceiptUploadHeaderService getReceiptUploadHeaderService() {
		return receiptUploadHeaderService;
	}

	public void setReceiptUploadHeaderService(ReceiptUploadHeaderService receiptUploadHeaderService) {
		this.receiptUploadHeaderService = receiptUploadHeaderService;
	}
}
