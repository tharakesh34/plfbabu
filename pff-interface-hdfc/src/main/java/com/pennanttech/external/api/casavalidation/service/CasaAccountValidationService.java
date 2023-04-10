package com.pennanttech.external.api.casavalidation.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
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
import com.pennanttech.external.api.casavalidation.dao.ExtApiDao;
import com.pennanttech.external.api.casavalidation.model.CasaAccountValidationReq;
import com.pennanttech.external.api.casavalidation.model.CasaAccountValidationResp;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.BankAccountValidationService;

public class CasaAccountValidationService implements BankAccountValidationService {

	private static final Logger logger = LogManager.getLogger(CasaAccountValidationService.class);
	private ExtApiDao extApiDao;

	public boolean validateBankAccount(BankAccountValidation bankAccountValidations) {
		logger.debug(Literal.ENTERING);

		// Load properties from ExtApi properties file
		Properties prop = loadProperties();

		// Create Request Object
		CasaAccountValidationReq request = getRequestObject(bankAccountValidations.getAcctNum(), prop);

		// Log Request into EXTAPILOG table
		long id = logRequest(request.toString());

		// Get URL from properties file
		String url = (String) prop.get("WSDL_URL");

		// Call Rest API
		CasaAccountValidationResp resp = postRequest(request, url);

		// Log Response into EXTAPILOG table by Id
		try {
			logResp(resp.getXmlResponse(), id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.debug(Literal.LEAVING);
		// Creating response to send
		return getAccountStatus(resp, (String) prop.get("ACCOUNT_STATUS"), bankAccountValidations);

	}

	private boolean getAccountStatus(CasaAccountValidationResp resp, String prop,
			BankAccountValidation bankAccValidations) {
		logger.debug(Literal.ENTERING);
		if ("0".equals(resp.getResponseCodes().getErrorCode())) {
			String[] params = Pattern.compile("\\|").split(prop);
			if (params != null) {
				for (String st : params) {
					String[] accStat = st.split("~");
					if (accStat[0]
							.equals(resp.getRespData().getCustDetails().getCasaAcc().getAccDtls().getAccountStatus())) {
						bankAccValidations.setReason(accStat[1]);

						if (accStat[2].equals("Y")) {
							return true;
						}
					}
				}
			}
			logger.debug(Literal.LEAVING);
			return false;
		}

		return false;

	}

	private Properties loadProperties() {
		logger.debug(Literal.ENTERING);
		Properties prop = new Properties();
		try (InputStream inputStream = this.getClass().getResourceAsStream("/properties/ExtApi.properties")) {
			prop.load(inputStream);
		} catch (IOException ioException) {
			logger.debug(ioException.getMessage());
		}
		logger.debug(Literal.LEAVING);
		return prop;
	}

	private CasaAccountValidationResp postRequest(CasaAccountValidationReq req, String url) {
		logger.debug(Literal.ENTERING);
		CasaAccountValidationResp resp = null;
		url = "http://192.168.121.149:8080/product";
		HttpHeaders headers = new HttpHeaders();
		RestTemplate restTemplate = getRestTemplate();
		headers.setContentType(org.springframework.http.MediaType.APPLICATION_XML);
		HttpEntity<String> request = new HttpEntity<>(req.toString(), headers);
		ResponseEntity<String> respEntity = null;
		try {
			respEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
			JAXBContext jaxbContext = JAXBContext.newInstance(CasaAccountValidationResp.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			resp = (CasaAccountValidationResp) jaxbUnmarshaller.unmarshal(new StringReader(respEntity.getBody()));
			resp.setXmlResponse(respEntity.getBody());
		} catch (JAXBException | RestClientException jae) {
			logger.error(Literal.EXCEPTION, jae);
			resp = getResponseData(jae.toString());
		}
		logger.debug(Literal.LEAVING);
		return resp;
	}

	private long logRequest(String request) {
		return extApiDao.insertReqData(request);
	}

	private void logResp(String resp, long id) throws Exception {
		extApiDao.logResponseById(id, resp);

	}

	private CasaAccountValidationReq getRequestObject(String accNo, Properties prop) {
		logger.debug(Literal.ENTERING);
		CasaAccountValidationReq accValidationReq = new CasaAccountValidationReq();
		accValidationReq.setExtsysname(prop.getProperty("EXTSYSNAME"));
		accValidationReq.setIdtxn(prop.getProperty("IDTXN"));
		accValidationReq.setIduser(prop.getProperty("IDUSER"));
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

	private CasaAccountValidationResp getResponseData(String msg) {
		logger.debug(Literal.ENTERING);
		CasaAccountValidationResp excResp = new CasaAccountValidationResp();
		excResp.setException(true);
		excResp.setXmlResponse(msg);
		logger.debug(Literal.LEAVING);
		return excResp;
	}

	public void setExtApiDao(ExtApiDao extApiDao) {
		this.extApiDao = extApiDao;
	}

}
