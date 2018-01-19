package com.pennanttech.niyogin.dms.service;

import java.io.PrintWriter;
import java.io.StringWriter;
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
import com.pennanttech.pff.external.DocumentManagementService;
import com.pennanttech.pff.external.service.NiyoginService;

public class DocumentManagerServiceImpl extends NiyoginService implements DocumentManagementService {
	private static final Logger	logger				= Logger.getLogger(DocumentManagerServiceImpl.class);

	private final String		extConfigFileName	= "dms";
	private String				serviceUrl;

	/**
	 * Method for Fetch online documents from DMS interface using Reference
	 * 
	 * @param docExternalRefIds
	 */
	@Override
	public DocumentDetails getExternalDocument(String docExternalRefId, String sourceReference) {
		DocumentDetails detail = new DocumentDetails();
		DocumentRequest dmsRequest = prepareRequestObj(docExternalRefId);
		reqSentOn = new Timestamp(System.currentTimeMillis());
		reference = StringUtils.isEmpty(sourceReference) ? "DOCUMENT" : sourceReference;
		try {
			logger.debug("ServiceURL : " + serviceUrl);
			String	reuestString = client.getRequestString(dmsRequest);
			jsonResponse = client.post(serviceUrl, reuestString);
			Map<String, Object> extendedFieldMap = getExtendedMapValues(jsonResponse, extConfigFileName);
			// error validation on Response status
			if (extendedFieldMap.get("ERRORMESSAGE") == null) {
				if (extendedFieldMap.get("DOCSOURCE") != null) {
					detail.setDocImage(extendedFieldMap.get("DOCSOURCE").toString().getBytes());
					detail.setDocUri(Objects.toString(extendedFieldMap.get("DOWNLOADURL"), ""));
					detail.setDocName(Objects.toString(extendedFieldMap.get("DOCNAME"), ""));
					detail.setPassword(Objects.toString(extendedFieldMap.get("DOCPASSWORD"), ""));
				}
			}
			logger.info("Response : " + jsonResponse);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			status = "FAILED";
			errorCode = "9999";
			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			errorDesc = writer.toString();
			doInterfaceLogging(dmsRequest, reference);
			throw new InterfaceException("9999", e.getMessage());
		}
		// success case logging
		doInterfaceLogging(dmsRequest, reference);
		logger.debug(Literal.LEAVING);
		return detail;
	}

	/**
	 * Method for prepare data and logging
	 * 
	 * @param documentRequest
	 * @param reference
	 */

	private void doInterfaceLogging(DocumentRequest request, String sourceReference) {

		InterfaceLogDetail interfaceLogDetail = prepareLoggingData(serviceUrl, request, jsonResponse, reqSentOn,
				status, errorCode, errorDesc, sourceReference);

		logInterfaceDetails(interfaceLogDetail);
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
