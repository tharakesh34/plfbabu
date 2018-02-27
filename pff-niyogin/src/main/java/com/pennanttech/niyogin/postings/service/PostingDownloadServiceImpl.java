package com.pennanttech.niyogin.postings.service;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;

import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.logging.model.InterfaceLogDetail;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.InterfaceConstants;
import com.pennanttech.pff.external.PostingDownloadService;
import com.pennanttech.pff.external.service.NiyoginService;

public class PostingDownloadServiceImpl extends NiyoginService implements PostingDownloadService {
	private final Logger	logger	= Logger.getLogger(getClass());

	private DataSource		dataSource;
	private String			serviceUrl;

	@Override
	public void sendPostings(Date postingDate, long userId) {
		logger.debug(Literal.ENTERING);
		try {
			String fileLocation = executeDataEngine(postingDate, userId);
			String jsonRequest = convertCSVToJSON(fileLocation);
			sendRequest(jsonRequest, postingDate);
		} catch (Exception e) {
			logger.error("Exception", e);
			throw new InterfaceException("9999", e.getMessage());
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for sending voucher details to SOA and log the transaction details
	 * 
	 * @param requestString
	 * @param postingDate
	 */
	private void sendRequest(String requestString, Date postingDate) {
		logger.debug(Literal.ENTERING);
		String reference = DateUtil.formatToShortDate(postingDate);
		String errorCode = null;
		String errorDesc = null;
		String jsonResponse = null;
		Timestamp reqSentOn = new Timestamp(System.currentTimeMillis());
		try {
			logger.info("Final JSON Request:" + requestString);
			jsonResponse = client.post(serviceUrl, requestString);
			//check response for error
			errorCode = getErrorCode(jsonResponse);
			errorDesc = getErrorMessage(jsonResponse);

			doInterfaceLogging(reference, serviceUrl, requestString, jsonResponse, errorCode, errorDesc, reqSentOn);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			errorDesc = getWriteException(e);
			errorDesc = getTrimmedMessage(errorDesc);
			doExceptionLogging(reference, serviceUrl, requestString, jsonResponse, errorDesc, reqSentOn);
		}
		logger.debug(Literal.LEAVING);
	}

	private String convertCSVToJSON(String fileLocation) throws Exception {
		File file = new File(fileLocation);
		DocumentConvertion convertion = new DocumentConvertion();
		JSONArray jsonArray = convertion.doProcess(file);
		String jsonRequest = jsonArray.toString();
		jsonRequest = "{\"data\"" + ":" + jsonRequest.concat("}");

		return jsonRequest;
	}

	private String executeDataEngine(Date postingDate, long userId) throws Exception {
		logger.debug(Literal.ENTERING);

		Map<String, Object> filterMap = new HashMap<>();
		filterMap.put("POSTDATE", postingDate);

		DataEngineExport dataEngine = null;
		dataEngine = new DataEngineExport(dataSource, userId, App.DATABASE.name(), true, getAppDate());

		dataEngine.setFilterMap(filterMap);
		dataEngine.setValueDate(getAppDate());
		dataEngine.exportData("POSTINGS_DOWNLOAD");

		Configuration config = dataEngine.getConfigurationByName("POSTINGS_DOWNLOAD");
		DataEngineStatus status = dataEngine.getDataEngineStatus();
		String file = "";
		if (status != null) {
			if (StringUtils.equals(status.getStatus(), "F") || status.getTotalRecords() <= 0) {
				throw new AppException("9999", status.getStatus());
			}
			file = status.getFileName();
			if (config != null) {
				file = config.getUploadPath() + "\\" + dataEngine.getDataEngineStatus().getFileName();
			}
		}
		logger.debug(Literal.LEAVING);
		return file;
	}

	/**
	 * Method for prepare Success logging
	 * 
	 * @param reference
	 * @param requets
	 * @param response
	 * @param errorCode
	 * @param errorDesc
	 * @param reqSentOn
	 */
	private void doInterfaceLogging(String reference, String serviceUrl, String requets, String response,
			String errorCode, String errorDesc, Timestamp reqSentOn) {
		logger.debug(Literal.ENTERING);
		InterfaceLogDetail iLogDetail = new InterfaceLogDetail();
		iLogDetail.setReference(reference);
		String[] values = serviceUrl.split("/");
		iLogDetail.setServiceName(values[values.length - 1]);
		iLogDetail.setEndPoint(serviceUrl);
		iLogDetail.setRequest(requets);
		iLogDetail.setReqSentOn(reqSentOn);

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
	 * @param reqSentOn
	 */
	private void doExceptionLogging(String reference, String serviceUrl, String requets, String response,
			String errorDesc, Timestamp reqSentOn) {
		logger.debug(Literal.ENTERING);
		InterfaceLogDetail iLogDetail = new InterfaceLogDetail();
		iLogDetail.setReference(reference);
		String[] values = serviceUrl.split("/");
		iLogDetail.setServiceName(values[values.length - 1]);
		iLogDetail.setEndPoint(serviceUrl);
		iLogDetail.setRequest(requets);
		iLogDetail.setReqSentOn(reqSentOn);

		iLogDetail.setResponse(response);
		iLogDetail.setRespReceivedOn(new Timestamp(System.currentTimeMillis()));
		iLogDetail.setStatus(InterfaceConstants.STATUS_FAILED);
		iLogDetail.setErrorCode(InterfaceConstants.ERROR_CODE);
		iLogDetail.setErrorDesc(errorDesc);

		logInterfaceDetails(iLogDetail);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

}
