package com.pennanttech.niyogin.dms.service;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennanttech.logging.model.InterfaceLogDetail;
import com.pennanttech.niyogin.dms.model.DocumentRequest;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.InterfaceConstants;
import com.pennanttech.pff.external.DocumentManagementService;
import com.pennanttech.pff.external.service.NiyoginService;

public class DocumentManagerServiceImpl extends NiyoginService implements DocumentManagementService {
	private static final Logger	logger				= Logger.getLogger(DocumentManagerServiceImpl.class);

	private final String		extConfigFileName	= "dms.properties";
	private String				serviceUrl;

	private String				ERRORCODE			= "$.data.error.errorCode";
	private String				ERRORMESSAGE		= "$.data.error.message";

	/**
	 * Method for Fetch online documents from DMS interface using Reference
	 * 
	 * @param docExternalRefIds
	 */
	@Override
	public DocumentDetails getExternalDocument(String docExternalRefId, String sourceReference) {
		DocumentDetails detail = new DocumentDetails();
		DocumentRequest dmsRequest = prepareRequestObj(docExternalRefId);

		String reference = StringUtils.isEmpty(sourceReference) ? "DOCUMENT" : sourceReference;
		String errorCode = null;
		String errorDesc = null;
		String reuestString = null;
		String jsonResponse = null;
		try {
			logger.debug("ServiceURL : " + serviceUrl);

			reuestString = client.getRequestString(dmsRequest);
			jsonResponse = client.post(serviceUrl, reuestString);
			errorDesc = Objects.toString(getValueFromResponse(jsonResponse, ERRORMESSAGE), "");
			errorCode = Objects.toString(getValueFromResponse(jsonResponse, ERRORCODE), "");

			doInterfaceLogging(reference, reuestString, jsonResponse, errorCode, errorDesc);

			if (StringUtils.isEmpty(errorCode)) {
				//read values from response and load it to extended map
				Map<String, Object> mapdata = getPropValueFromResp(jsonResponse, extConfigFileName);
				if (mapdata.get("DOCSOURCE") != null) {
					detail.setDocImage(mapdata.get("DOCSOURCE").toString().getBytes());
					detail.setDocUri(Objects.toString(mapdata.get("DOWNLOADURL"), ""));
					detail.setDocName(Objects.toString(mapdata.get("DOCNAME"), ""));
					detail.setPassword(Objects.toString(mapdata.get("DOCPASSWORD"), ""));
				}
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
			errorDesc = getWriteException(e);
			errorDesc = getTrimmedMessage(errorDesc);
			doExceptioLogging(reference, reuestString, jsonResponse, errorDesc);
			throw new InterfaceException("9999", e.getMessage());
		}

		logger.debug(Literal.LEAVING);
		return detail;
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

	private DocumentRequest prepareRequestObj(String docExternalRefId) {
		DocumentRequest request = new DocumentRequest();
		request.setDocRefId(docExternalRefId);
		return request;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}
}
