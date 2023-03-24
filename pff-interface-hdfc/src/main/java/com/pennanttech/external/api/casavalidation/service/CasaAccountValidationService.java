package com.pennanttech.external.api.casavalidation.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
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

import com.pennanttech.external.ExternalAPIHook;
import com.pennanttech.external.api.casavalidation.dao.ExtApiDao;
import com.pennanttech.external.api.casavalidation.model.CasaAccountValidationReq;
import com.pennanttech.external.api.casavalidation.model.CasaAccountValidationResp;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;

public class CasaAccountValidationService implements ExternalAPIHook {
	private static final Logger logger = LogManager.getLogger(CasaAccountValidationService.class);
	private ExtApiDao extApiDao;

	public String validateAccountNumber(String accNo) {

		// Load properties from ExtApi properties file
		Properties prop = loadProperties();

		// Create Request Object
		CasaAccountValidationReq request = getRequestObject(accNo, prop);

		// Log Request into EXTAPILOG table
		long id = logRequest(request.toString());

		// Get URL from properties file
		String url = (String) prop.get("WSDL_URL");

		// Call Rest API
		CasaAccountValidationResp resp = postRequest(request, url);

		// Log Response into EXTAPILOG table by Id
		logResp(resp.getXmlResponse(), id);

		// Create response to send as needed
		// TODO
		String accStatus = getAccountStatus(resp, prop);

		return accStatus;
	}

	private String getAccountStatus(CasaAccountValidationResp resp, Properties prop) {

		if (resp.isException) {
			return "";
		}

		String status = null;
		String accStatus = prop.getProperty("ACCOUNT_STATUS");
		String[] params = Pattern.compile("\\|").split(accStatus);
		if (params != null) {
			for (String st : params) {
				String[] accStat = st.split("~");
				if (accStat[0]
						.equals(resp.getRespData().getCustDetails().getCasaAcc().getAccDtls().getAccountStatus())) {
					return accStat[1] + "~" + accStat[2];
				}
			}
		}
		return status;
	}

	private Properties loadProperties() {
		Properties prop = new Properties();
		try {
			InputStream inputStream = this.getClass().getResourceAsStream("/properties/ExtApi.properties");
			prop.load(inputStream);
		} catch (IOException ioException) {
			logger.debug(ioException.getMessage());
		}
		return prop;
	}

	private CasaAccountValidationResp postRequest(CasaAccountValidationReq req, String url) {
		CasaAccountValidationResp resp = null;
		HttpHeaders headers = new HttpHeaders();
		RestTemplate restTemplate = getRestTemplate();
		headers.setContentType(org.springframework.http.MediaType.APPLICATION_XML);

		Map<String, String> map = new HashMap<String, String>();
		map.put("extsysname", req.getExtsysname());
		map.put("idtxn", req.getIdtxn());
		map.put("iduser", req.getIduser());
		map.put("accountNumber", req.getCustacctdetails().getAccountNo());
		HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);
		ResponseEntity<String> respEntity = null;
		try {
			respEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
			JAXBContext jaxbContext = JAXBContext.newInstance(CasaAccountValidationResp.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			resp = (CasaAccountValidationResp) jaxbUnmarshaller.unmarshal(new StringReader(respEntity.getBody()));
			resp.setXmlResponse(respEntity.getBody());
		} catch (JAXBException jae) {
			logger.error(Literal.EXCEPTION, jae);
			resp = getResponseData(jae.toString());
		} catch (RestClientException rce) {
			logger.error(Literal.EXCEPTION, rce);
			resp = getResponseData(rce.toString());
		}
		return resp;
	}

	private long logRequest(String request) {
		return extApiDao.insertReqData(request);
	}

	private void logResp(String resp, long id) {
		extApiDao.logResponseById(id, resp);
	}

	private CasaAccountValidationReq getRequestObject(String accNo, Properties prop) {
		CasaAccountValidationReq accValidationReq = new CasaAccountValidationReq();
		accValidationReq.setExtsysname(prop.getProperty("EXTSYSNAME"));
		accValidationReq.setIdtxn(prop.getProperty("IDTXN"));
		accValidationReq.setIduser(prop.getProperty("IDUSER"));
		CasaAccountValidationReq.CustAcctDetails accDetails = accValidationReq.new CustAcctDetails();
		accDetails.setAccountNo(accNo);
		accValidationReq.setCustacctdetails(accDetails);
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
		CasaAccountValidationResp excResp = new CasaAccountValidationResp();
		excResp.setException(true);
		excResp.setXmlResponse(msg);
		return excResp;
	}

	public void setExtApiDao(ExtApiDao extApiDao) {
		this.extApiDao = extApiDao;
	}

}
