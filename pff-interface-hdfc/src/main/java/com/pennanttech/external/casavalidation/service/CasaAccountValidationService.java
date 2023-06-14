package com.pennanttech.external.casavalidation.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.pennant.backend.model.pennydrop.BankAccountValidation;
import com.pennanttech.external.app.constants.ExtIntfConfigConstants;
import com.pennanttech.external.casavalidation.model.CasaAccountValidationReq;
import com.pennanttech.external.casavalidation.model.CasaAccountValidationResp;
import com.pennanttech.logging.model.InterfaceLogDetail;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.BankAccountValidationService;
import com.pennanttech.pff.logging.dao.InterfaceLoggingDAO;

public class CasaAccountValidationService implements BankAccountValidationService, ExtIntfConfigConstants {

	private static final Logger logger = LogManager.getLogger(CasaAccountValidationService.class);
	private InterfaceLoggingDAO interfaceLoggingDAO;

	public boolean validateBankAccount(BankAccountValidation bankAccountValidation) {
		boolean accStatus = false;
		logger.debug(Literal.ENTERING);
		InterfaceLogDetail logDetails = new InterfaceLogDetail();
		CasaAccountValidationResp resp = null;
		String errorDesc = null;
		String errorCode = null;
		Properties hdfcErrorCodeprop = null;
		try {
			Properties hdfcInterfaceProp = new Properties();
			InputStream inputStream = this.getClass().getResourceAsStream("/properties/HDFCInterface.properties");
			hdfcInterfaceProp.load(inputStream);
			hdfcErrorCodeprop = new Properties();
			InputStream errorCodesinputStream = this.getClass()
					.getResourceAsStream("/properties/HDFCInterefaceErrorCodes.properties");
			hdfcErrorCodeprop.load(errorCodesinputStream);
			CasaAccountValidationReq request = getRequestObject(bankAccountValidation.getAcctNum(), hdfcInterfaceProp);
			String url = (String) hdfcInterfaceProp.get("CASACCVALIDATION.WSDL_URL");
			logDetails.setEndPoint(url);
			logDetails.setRequest(request.toString());
			logDetails.setReference(bankAccountValidation.getAcctNum());
			logDetails.setServiceName(CASA_ACC_VALIDATILON);
			logDetails.setReqSentOn(new Timestamp(System.currentTimeMillis()));
			// Call Rest API
			resp = postRequest(request, url);
			logDetails.setRespReceivedOn(new Timestamp(System.currentTimeMillis()));
			logDetails.setResponse(resp.getXmlResponse());
			if (resp != null && resp.getResponseCodes() != null && resp.getResponseCodes().getErrorCode() != null
					&& resp.getResponseCodes().getErrorCode().equals("0")) {

				String[] params = Pattern.compile("\\|")
						.split(hdfcInterfaceProp.getProperty("CASACCVALIDATION.ACCOUNT_STATUS"));
				if (params != null) {
					for (String st : params) {
						String[] accStat = st.split("~");
						if (accStat != null && accStat.length >= 3) {
							if (accStat[0].equals(
									resp.getRespData().getCustDetails().getCasaAcc().getAccDtls().getAccountStatus())) {
								bankAccountValidation.setReason(accStat[1]);

								if ("Y".equals(accStat[2])) {
									accStatus = true;
								}
							}
						} else {
							errorCode = "CASA.E505";
							errorDesc = hdfcErrorCodeprop.getProperty(errorCode);
							bankAccountValidation.setReason(errorDesc);
							return accStatus;
						}
					}
				} else {
					errorCode = "CASA.E504";
					errorDesc = hdfcErrorCodeprop.getProperty(errorCode);
					logger.debug(errorDesc);
					bankAccountValidation.setReason(errorDesc);
					return accStatus;
				}
			}
		} catch (IOException e) {
			logger.error(Literal.EXCEPTION, e);
			errorCode = "CASA.E502";
			errorDesc = hdfcErrorCodeprop.getProperty(errorCode);
			logDetails.setResponse(e.getMessage());
			bankAccountValidation.setReason(errorDesc);
		} catch (JAXBException e) {
			errorCode = "CASA.E503";
			errorDesc = hdfcErrorCodeprop.getProperty(errorCode);
			logDetails.setResponse(e.getMessage());
			logger.error(Literal.EXCEPTION, e);
			bankAccountValidation.setReason(errorDesc);
		} catch (RestClientException e) {
			errorCode = "CASA.E501";
			errorDesc = hdfcErrorCodeprop.getProperty(errorCode);
			logDetails.setResponse(e.getMessage());
			logger.error(Literal.EXCEPTION, e);
			bankAccountValidation.setReason(errorDesc);
		} finally {
			logDetails.setErrorCode(errorCode);
			logDetails.setErrorDesc(errorDesc);
			interfaceLoggingDAO.save(logDetails);
		}
		logger.debug(Literal.LEAVING);
		return accStatus;
	}

	private CasaAccountValidationResp postRequest(CasaAccountValidationReq req, String url)
			throws JAXBException, RestClientException {
		logger.debug(Literal.ENTERING);
		CasaAccountValidationResp resp = null;
		HttpHeaders headers = new HttpHeaders();
		RestTemplate restTemplate = getRestTemplate();
		headers.setContentType(org.springframework.http.MediaType.APPLICATION_XML);
		HttpEntity<String> request = new HttpEntity<>(req.toString(), headers);
		ResponseEntity<String> respEntity = null;
		respEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
		JAXBContext jaxbContext = JAXBContext.newInstance(CasaAccountValidationResp.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		resp = (CasaAccountValidationResp) jaxbUnmarshaller.unmarshal(new StringReader(respEntity.getBody()));
		resp.setXmlResponse(respEntity.getBody());
		logger.debug(Literal.LEAVING);
		return resp;
	}

	private CasaAccountValidationReq getRequestObject(String accNo, Properties prop) {
		logger.debug(Literal.ENTERING);
		CasaAccountValidationReq accValidationReq = new CasaAccountValidationReq();
		accValidationReq.setExtsysname(prop.getProperty("CASACCVALIDATION.EXTSYSNAME"));
		accValidationReq.setIdtxn(prop.getProperty("CASACCVALIDATION.IDTXN"));
		accValidationReq.setIduser(prop.getProperty("CASACCVALIDATION.IDUSER"));
		CasaAccountValidationReq.CustAcctDetails accDetails = accValidationReq.new CustAcctDetails();
		accDetails.setAccountNo(accNo);
		accValidationReq.setCustacctdetails(accDetails);
		logger.debug(Literal.LEAVING);
		return accValidationReq;
	}

	private RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		SimpleClientHttpRequestFactory httpRequestFactory = new SimpleClientHttpRequestFactory();
		String proxyUrl = App.getProperty("external.interface.proxy.host");
		String proxyPort = App.getProperty("external.interface.proxy.port");
		String proxyRequired = App.getProperty("portal.proxy.required");
		if (StringUtils.equals(proxyRequired, "true") && StringUtils.isNotEmpty(proxyUrl)
				&& StringUtils.isNotEmpty(proxyPort)) {
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyUrl, Integer.parseInt(proxyPort)));
			httpRequestFactory.setProxy(proxy);
		}
		restTemplate.setRequestFactory(httpRequestFactory);
		return restTemplate;
	}

	public void setInterfaceLoggingDAO(InterfaceLoggingDAO interfaceLoggingDAO) {
		this.interfaceLoggingDAO = interfaceLoggingDAO;
	}
}
