package com.pennanttech.external.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.sql.Timestamp;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennanttech.logging.model.InterfaceLogDetail;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.InterfaceConstants;
import com.pennanttech.pff.logging.dao.InterfaceLoggingDAO;

public abstract class SoapClient<T> {
	protected static Logger logger = LogManager.getLogger(SoapClient.class.getClass());

	protected static final String READ_TIMEOUT = "exteranal.interface.read.timeout";
	protected static final String CONNECTION_TIMEOUT = "exteranal.interface.connection.timeout";

	protected int readTimeout = 0;
	protected int connTimeout = 0;

	@Autowired
	private InterfaceLoggingDAO interfaceLoggingDAO;

	/**
	 * Set the properties.
	 */
	protected void doSetProperties(String readKey, String connKey) {
		try {
			if (StringUtils.trimToNull(readKey) != null) {
				readTimeout = Integer.valueOf(App.getProperty(readKey));
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		try {
			if (StringUtils.trimToNull(connKey) != null) {
				connTimeout = Integer.valueOf(App.getProperty(connKey));
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	protected SoapServiceDetail processMessage(SoapServiceDetail serviceDetail) {
		InterfaceLogDetail logDetail = null;
		String url = App.getProperty(serviceDetail.getServiceUrl());

		if (StringUtils.isEmpty(url)) {
			throw new InterfaceException("8905", "Invalid URL Configuration");
		}
		String errorCode = "0000";
		String errorDesc = null;
		String status = InterfaceConstants.STATUS_SUCCESS;

		try {
			SOAPMessage request = getSoapMessage(serviceDetail);
			ByteArrayOutputStream soapRequest = new ByteArrayOutputStream();
			request.writeTo(soapRequest);
			logger.debug(serviceDetail.getServiceName() + " Request " + soapRequest.toString());
			serviceDetail.setRequestString(soapRequest.toString());
			logDetail = logData(serviceDetail, url);

			SOAPMessage response = executeMessage(request, url);
			ByteArrayOutputStream soapResposne = new ByteArrayOutputStream();
			response.writeTo(soapResposne);
			logger.debug(serviceDetail.getServiceName() + " Response" + soapResposne.toString());
			serviceDetail.setResponseString(soapResposne.toString());
			serviceDetail = getResponse(response, serviceDetail);
		} catch (InterfaceException e) {
			status = InterfaceConstants.STATUS_FAILED;
			errorCode = e.getErrorCode();
			errorDesc = e.getErrorMessage();
			throw e;
		} catch (Exception e) {
			status = InterfaceConstants.STATUS_FAILED;
			errorCode = "8904";
			errorDesc = e.getMessage();
			throw new InterfaceException("8904", e.getMessage());
		} finally {
			if (logDetail != null) {
				logDetail.setResponse(
						StringUtils.left(StringUtils.trimToEmpty(serviceDetail.getResponseString()), 1000));
				logDetail.setRespReceivedOn(new Timestamp(System.currentTimeMillis()));
				logDetail.setStatus(status);
				logDetail.setErrorCode(errorCode);
				logDetail.setErrorDesc(StringUtils.left(StringUtils.trimToEmpty(errorDesc), 200));
				updateResponse(logDetail);
			}
		}

		return serviceDetail;
	}

	private InterfaceLogDetail logData(SoapServiceDetail serviceDetail, String url) {
		logger.debug(Literal.ENTERING);
		InterfaceLogDetail logDetail = new InterfaceLogDetail();
		logDetail.setReference(serviceDetail.getReference());
		logDetail.setServiceName(serviceDetail.getServiceName());
		logDetail.setEndPoint(url + ";" + serviceDetail.getServiceEndPoint());
		logDetail.setRequest(StringUtils.left(StringUtils.trimToEmpty(serviceDetail.getRequestString()), 1000));
		logDetail.setReqSentOn(new Timestamp(System.currentTimeMillis()));

		if (interfaceLoggingDAO != null) {
			interfaceLoggingDAO.save(logDetail);
		}

		logger.debug(Literal.LEAVING);
		return logDetail;
	}

	public SOAPMessage getSoapMessage(SoapServiceDetail serviceDetail) {
		try {
			logger.debug(Literal.ENTERING);
			MessageFactory mf = MessageFactory.newInstance(serviceDetail.getProtocol());
			SOAPMessage message = mf.createMessage();
			SOAPBody body = message.getSOAPBody();
			JAXBContext jaxbContext = JAXBContext.newInstance(serviceDetail.getRequestData().getClass());
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
			marshaller.marshal(serviceDetail.getRequestData(), body);

			MimeHeaders headers = message.getMimeHeaders();
			headers.addHeader("SOAPAction", serviceDetail.getServiceEndPoint());

			return message;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new InterfaceException("8902", e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public SoapServiceDetail getResponse(SOAPMessage response, SoapServiceDetail serviceDetail) {
		logger.debug(Literal.ENTERING);
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(serviceDetail.getResponceData().getClass());
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			serviceDetail
					.setResponceData((T) unmarshaller.unmarshal(response.getSOAPBody().extractContentAsDocument()));
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new InterfaceException("8903", e.getMessage());
		}
		logger.debug(Literal.LEAVING);
		return serviceDetail;
	}

	private SOAPMessage executeMessage(SOAPMessage message, String url) {
		logger.debug(Literal.ENTERING);
		SOAPMessage response = null;
		try {
			SOAPConnectionFactory sfc = SOAPConnectionFactory.newInstance();
			SOAPConnection connection = sfc.createConnection();

			URL endpoint = new URL(null, url, new URLStreamHandler() {
				@Override
				protected URLConnection openConnection(URL url) throws IOException {
					URL target = new URL(url.toString());
					URLConnection connection = target.openConnection();
					// Connection settings
					connection.setConnectTimeout(connTimeout);
					connection.setReadTimeout(readTimeout);
					return (connection);
				}
			});

			response = connection.call(message, endpoint);

		} catch (Exception e) {
			logger.error("Error", e);
			throw new InterfaceException("8900", e.getMessage());

		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	protected void updateResponse(InterfaceLogDetail logDetail) {
		logger.debug(Literal.ENTERING);
		if (interfaceLoggingDAO != null) {
			interfaceLoggingDAO.update(logDetail);
		}
		logger.debug(Literal.LEAVING);
	}

	protected long getTransactionId() {
		if (interfaceLoggingDAO != null) {
			return interfaceLoggingDAO.getSequence();
		}

		return System.currentTimeMillis();
	}
}
