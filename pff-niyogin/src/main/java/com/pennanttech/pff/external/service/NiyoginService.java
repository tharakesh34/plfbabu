package com.pennanttech.pff.external.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.model.systemmasters.City;
import com.pennanttech.dataengine.util.DateUtil;
import com.pennanttech.logging.model.InterfaceLogDetail;
import com.pennanttech.niyogin.clients.JSONClient;
import com.pennanttech.niyogin.holdfinance.model.HoldReason;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.InterfaceConstants;
import com.pennanttech.pff.external.dao.NiyoginDAOImpl;
import com.pennanttech.pff.logging.dao.InterfaceLoggingDAO;

public abstract class NiyoginService {
	private static final Logger	logger				= Logger.getLogger(NiyoginService.class);

	private InterfaceLoggingDAO	interfaceLoggingDAO;
	private NiyoginDAOImpl	niyoginDAOImpl;
	private JSONClient			client;
	public static final int		LENGTH_ACCOUNT		= 50;
	public static final int		LENGTH_FREQUENCY	= 5;
	public static final String	APIDateFormatter	= "yyyy-MM-dd'T'HH:mm:ss";
	public static final String	DELIMITER_COMMA		= ",";

	public String				status				= InterfaceConstants.STATUS_SUCCESS;
	public String				errorCode			= null;
	public String				errorDesc			= null;
	public String				jsonResponse		= null;
	public Timestamp			reqSentOn			= null;
	
	public String				errorCodeKey		= "ERRORCODE";
	public String				errorDescKey		= "ERRORMESSAGE";

	public String				reference;


	public NiyoginService() {
		super();
	}

	/**
	 * 
	 * 
	 * @param serviceUrl
	 * @param requestObject
	 * @param extConfigFileName
	 * @return
	 */
	protected Map<String, Object> post(String serviceUrl, Object requestObject, String extConfigFileName) {
		Map<String, Object> extendedFieldMap = null;
		try {
			logger.debug("ServiceURL : " + serviceUrl);
			jsonResponse = client.post(serviceUrl, requestObject);
			extendedFieldMap = getExtendedMapValues(jsonResponse, extConfigFileName);

			// error validation on Response status
			if (extendedFieldMap.get(errorCodeKey) != null) {
				errorCode = Objects.toString(extendedFieldMap.get(errorCodeKey));
				errorDesc = Objects.toString(extendedFieldMap.get(errorDescKey));
				throw new InterfaceException(errorCode, errorDesc);
			} else {
				extendedFieldMap.remove(errorCodeKey);
				extendedFieldMap.remove(errorDescKey);
			}
			//extendedFieldMap.put("jsonResponse", jsonResponse);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			status = "FAILED";
			errorCode = "9999";
			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			errorDesc = writer.toString();
			InterfaceLogDetail interfaceLogDetail = prepareLoggingData(serviceUrl, requestObject, jsonResponse, reqSentOn,
					status, errorCode, errorDesc, reference);
			logInterfaceDetails(interfaceLogDetail);
			throw new InterfaceException("9999", e.getMessage());
		}
		return extendedFieldMap;
	}

	/**
	 * 
	 * 
	 * @param jsonResponse
	 * @param responseClass
	 * @param isList
	 * @return
	 */
	public Object getResponseObject(String jsonResponse, Class<?> responseClass, boolean isList) {
		return client.getResponseObject(jsonResponse, responseClass, isList);
	}

	/**
	 * Method for load the properties file, then iterate the keys of that file and map to jsonResponse.
	 * 
	 * @param jsonResponse
	 * @param extConfigFileName
	 * @return extendedMappedValues
	 */
	protected Map<String, Object> getExtendedMapValues(String jsonResponse, String extConfigFileName) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> extendedFieldMap = new HashMap<>(1);
		Properties properties = new Properties();
		InputStream inputStream = this.getClass().getResourceAsStream("/" + extConfigFileName + ".properties");
		try {
			properties.load(inputStream);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
		Enumeration<?> e = properties.propertyNames();
		//Configuration conf = Configuration.defaultConfiguration();
		//conf = conf.addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			Object value = null;
			try {
				value = JsonPath.read(jsonResponse, properties.getProperty(key));
			} catch (PathNotFoundException pathNotFoundException) {
				value = null;
			}
			extendedFieldMap.put(key, value);
		}
		logger.debug(Literal.LEAVING);

		return extendedFieldMap;
	}

	/**
	 * Method for validate the jsonResponseMap based on the configuration.
	 * 
	 * @param extendedFieldMap
	 * @return validatedMap
	 */
	protected Map<String, Object> validateExtendedMapValues(Map<String, Object> extendedFieldMap) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> validatedMap = new HashMap<>(1);
		Set<String> fieldNames = extendedFieldMap.keySet();
		String wrongValueMSG = "Inavalid Data received from interface for extended field:";
		String wrongLengthMSG = "Total length is Excedeed for extended field:";
		List<ExtendedFieldDetail> configurationList=null;
		if (fieldNames == null || (fieldNames != null && fieldNames.isEmpty())) {
			throw new InterfaceException("9999", "Invalid configuration");
		} else {
		try {
			configurationList = niyoginDAOImpl.getExtendedFieldDetailsByFieldName(fieldNames);
			for (String field : fieldNames) {
				String key = field;
				Object fieldValue = extendedFieldMap.get(field);
				ExtendedFieldDetail configuration = null;
				if (configurationList == null || configurationList.isEmpty()) {
					return validatedMap;
				}
				for (ExtendedFieldDetail extdetail : configurationList) {
					if (StringUtils.equals(key, extdetail.getFieldName())) {
						configuration = extdetail;
						break;
					}
				}
				if (configuration == null) {
					continue;
				}

				String jsonResponseValue = Objects.toString(fieldValue, null);
				if (jsonResponseValue == null) {
					continue;
				}

				switch (configuration.getFieldType()) {

				case ExtendedFieldConstants.FIELDTYPE_TEXT:
				case ExtendedFieldConstants.FIELDTYPE_MULTILINETEXT:
				case ExtendedFieldConstants.FIELDTYPE_UPPERTEXT:
					if (jsonResponseValue.length() > configuration.getFieldLength()) {
						if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_UPPERTEXT,
								configuration.getFieldType())) {
							String value = jsonResponseValue.substring(0, configuration.getFieldLength());
							validatedMap.put(key, value.toUpperCase());
						} else {
							validatedMap.put(key, jsonResponseValue.substring(0, configuration.getFieldLength()));
						}
						break;
					}
					if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_UPPERTEXT, configuration.getFieldType())) {
						validatedMap.put(key, jsonResponseValue.toUpperCase());
					} else {
						validatedMap.put(key, jsonResponseValue);
					}
					break;

				case ExtendedFieldConstants.FIELDTYPE_ADDRESS:
					if (jsonResponseValue.length() > 100) {
						validatedMap.put(key, jsonResponseValue.substring(0, 100));
					} else {
						validatedMap.put(key, jsonResponseValue);
					}
					break;

				case ExtendedFieldConstants.FIELDTYPE_DATE:
				case ExtendedFieldConstants.FIELDTYPE_TIME:
					Date dateValue = null;
					try {
						DateFormat formatter = new SimpleDateFormat(InterfaceConstants.InterfaceDateFormatter);
						dateValue = formatter.parse(jsonResponseValue);
					} catch (Exception e) {
						throw new InterfaceException("9999", wrongValueMSG + configuration.getFieldLabel());
					}
					validatedMap.put(key, dateValue);
					break;

				case ExtendedFieldConstants.FIELDTYPE_DATETIME:
					Date dateTimeVal = null;
					try {
						DateFormat formatter = new SimpleDateFormat(InterfaceConstants.InterfaceDateFormatter);
						dateTimeVal = formatter.parse(jsonResponseValue);
					} catch (Exception e) {
						throw new InterfaceException("9999", wrongValueMSG + configuration.getFieldLabel());
					}
					validatedMap.put(key, dateTimeVal);
					break;

				case ExtendedFieldConstants.FIELDTYPE_BOOLEAN:
					Boolean booleanValue;
					if (StringUtils.equals(jsonResponseValue, "true")
							|| StringUtils.equals(jsonResponseValue, "false")) {
						booleanValue = jsonResponseValue.equals("true") ? true : false;
					} else if (StringUtils.equals(jsonResponseValue, "1")
							|| StringUtils.equals(jsonResponseValue, "0")) {
						booleanValue = jsonResponseValue.equals("1") ? true : false;
					} else {
						throw new InterfaceException("9999", wrongValueMSG + configuration.getFieldLabel());
					}
					validatedMap.put(key, booleanValue);
					break;

				case ExtendedFieldConstants.FIELDTYPE_AMOUNT:
					BigDecimal decimalValue = BigDecimal.ZERO;
					try {
						double rateValue = Double.parseDouble(jsonResponseValue);
						decimalValue = BigDecimal.valueOf(rateValue);
					} catch (Exception e) {
						throw new InterfaceException("9999", wrongValueMSG + configuration.getFieldLabel());
					}
					if (jsonResponseValue.length() > configuration.getFieldLength() + 2) {
						throw new InterfaceException("9999", wrongLengthMSG + configuration.getFieldLabel());
					}
					validatedMap.put(key, decimalValue);
					break;

				case ExtendedFieldConstants.FIELDTYPE_INT:
					int intValue = 0;
					if (jsonResponseValue.length() > configuration.getFieldLength()) {
						throw new InterfaceException("9999", wrongLengthMSG + configuration.getFieldLabel());
					}
					try {
						if (!StringUtils.isEmpty(jsonResponseValue)) {
							intValue = Integer.parseInt(jsonResponseValue);
						}
					} catch (Exception e) {
						throw new InterfaceException("9999", wrongValueMSG + configuration.getFieldLabel());
					}
					if (configuration.getFieldMaxValue() > 0 || configuration.getFieldMinValue() > 0) {
						if (!(intValue >= configuration.getFieldMinValue()
								&& intValue <= configuration.getFieldMaxValue())) {
							throw new InterfaceException("9999", wrongLengthMSG + configuration.getFieldLabel());
						}
					}
					validatedMap.put(key, intValue);
					break;

				case ExtendedFieldConstants.FIELDTYPE_LONG:
					long longValue = 0;
					if (jsonResponseValue.length() > configuration.getFieldLength()) {
						throw new InterfaceException("9999", wrongLengthMSG + configuration.getFieldLabel());
					}
					try {
						longValue = Long.parseLong(jsonResponseValue);
					} catch (Exception e) {
						throw new InterfaceException("9999", wrongValueMSG + configuration.getFieldLabel());
					}
					if (configuration.getFieldMaxValue() > 0 || configuration.getFieldMinValue() > 0) {
						if (!(longValue >= configuration.getFieldMinValue()
								&& longValue <= configuration.getFieldMaxValue())) {
							throw new InterfaceException("9999", wrongLengthMSG + configuration.getFieldLabel());
						}
					}
					validatedMap.put(key, longValue);
					break;

				case ExtendedFieldConstants.FIELDTYPE_RADIO:
					int radioValue = 0;
					if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_RADIO, configuration.getFieldType())) {
						try {
							radioValue = Integer.parseInt(jsonResponseValue);
						} catch (Exception e) {
							throw new InterfaceException("9999", wrongValueMSG + configuration.getFieldLabel());
						}
						if (radioValue > configuration.getFieldLength()) {
							throw new InterfaceException("9999", wrongLengthMSG + configuration.getFieldLabel());
						} else {
							validatedMap.put(key, radioValue);
						}
					}
					break;

				case ExtendedFieldConstants.FIELDTYPE_STATICCOMBO:
					String[] values = new String[0];
					String staticList = configuration.getFieldList();
					if (staticList != null) {
						if (staticList.contains(DELIMITER_COMMA)) {
							values = staticList.split(DELIMITER_COMMA);
							for (String vale : values) {
								if (vale.equals(jsonResponseValue)) {
									validatedMap.put(key, jsonResponseValue);
									break;
								}
							}
						} else if (staticList.equals(jsonResponseValue)) {
							validatedMap.put(key, jsonResponseValue);
							break;
						} else {
							throw new InterfaceException("9999", wrongValueMSG + configuration.getFieldLabel());
						}
					} else {
						throw new InterfaceException("9999", wrongValueMSG + configuration.getFieldLabel());
					}
					break;

				//TODO:any regix validation
				case ExtendedFieldConstants.FIELDTYPE_PHONE:
					if (jsonResponseValue.length() > 10) {
						validatedMap.put(key, jsonResponseValue.substring(0, 10));
						break;
					} else {
						validatedMap.put(key, jsonResponseValue);
					}
					break;

				case ExtendedFieldConstants.FIELDTYPE_DECIMAL:
					if (jsonResponseValue.length() > configuration.getFieldLength()) {
						throw new InterfaceException("9999", wrongLengthMSG + configuration.getFieldLabel());
					}

					if (configuration.getFieldMaxValue() > 0 || configuration.getFieldMinValue() > 0) {
						if (Integer.valueOf(jsonResponseValue) > configuration.getFieldMaxValue()
								|| Integer.valueOf(jsonResponseValue) < configuration.getFieldMinValue()) {
							throw new InterfaceException("9999", wrongLengthMSG + configuration.getFieldLabel());
						}
					}

					validatedMap.put(key, Math.round((Integer.valueOf(jsonResponseValue) / Math.pow(10, configuration.getFieldPrec()))));
					break;

				case ExtendedFieldConstants.FIELDTYPE_ACTRATE:
					double actRate = 0;
					if (jsonResponseValue.length() > (configuration.getFieldLength() - configuration.getFieldPrec())) {
						throw new InterfaceException("9999", wrongLengthMSG + configuration.getFieldLabel());
					}
					try {
						actRate = Double.valueOf(jsonResponseValue);
					} catch (Exception e) {
						throw new InterfaceException("9999", wrongValueMSG + configuration.getFieldLabel());
					}

					if (configuration.getFieldMaxValue() > 0 || configuration.getFieldMinValue() > 0) {
						if (Integer.valueOf(jsonResponseValue) > configuration.getFieldMaxValue()
								|| Integer.valueOf(jsonResponseValue) < configuration.getFieldMinValue()) {
							throw new InterfaceException("9999", wrongLengthMSG + configuration.getFieldLabel());
						}
					}
					validatedMap.put(key, actRate);
					break;

				case ExtendedFieldConstants.FIELDTYPE_PERCENTAGE:
					double percentage = 0;
					if (jsonResponseValue.length() > (configuration.getFieldLength() - configuration.getFieldPrec())) {
						throw new InterfaceException("9999", wrongLengthMSG + configuration.getFieldLabel());
					}
					try {
						percentage = Double.valueOf(jsonResponseValue);
					} catch (Exception e) {
						throw new InterfaceException("9999", wrongValueMSG + configuration.getFieldLabel());
					}
					if (percentage < 0 || percentage > 100) {
						throw new InterfaceException("9999", wrongLengthMSG + configuration.getFieldLabel());
					}
					if (configuration.getFieldMaxValue() > 0 || configuration.getFieldMinValue() > 0) {
						if (percentage > configuration.getFieldMaxValue()
								|| percentage < configuration.getFieldMinValue()) {
							throw new InterfaceException("9999", wrongLengthMSG + configuration.getFieldLabel());
						}
					}
					validatedMap.put(key, percentage);
					break;

				case ExtendedFieldConstants.FIELDTYPE_MULTISTATICCOMBO:
					String[] values1 = new String[0];
					String[] fieldvalues = new String[0];
					String multiStaticList = configuration.getFieldList();
					if (multiStaticList != null && multiStaticList.contains(DELIMITER_COMMA)) {
						values1 = multiStaticList.split(DELIMITER_COMMA);
					}
					if (fieldValue != null && jsonResponseValue.contains(DELIMITER_COMMA)) {
						fieldvalues = jsonResponseValue.split(DELIMITER_COMMA);
					}
					if (values1.length > 0) {
						for (int i = 0; i <= fieldvalues.length - 1; i++) {
							boolean isValid1 = false;
							for (int j = 0; j <= values1.length - 1; j++) {
								if (StringUtils.equals(fieldvalues[i], values1[j])) {
									isValid1 = true;
								}
							}
							if (!isValid1) {
								throw new InterfaceException("9999", wrongLengthMSG + configuration.getFieldLabel());

							} else {
								validatedMap.put(key, jsonResponseValue);
							}
						}
					}
					break;

				case ExtendedFieldConstants.FIELDTYPE_FRQ:
					if (jsonResponseValue.length() <= LENGTH_FREQUENCY) {
						validatedMap.put(key, jsonResponseValue);
					} else {
						throw new InterfaceException("9999", wrongLengthMSG + configuration.getFieldLabel());
					}

					break;

				case ExtendedFieldConstants.FIELDTYPE_ACCOUNT:
					if (jsonResponseValue.length() <= LENGTH_ACCOUNT) {
						validatedMap.put(key, jsonResponseValue);
					} else {
						throw new InterfaceException("9999", wrongLengthMSG + configuration.getFieldLabel());
					}
					break;

				case ExtendedFieldConstants.FIELDTYPE_MULTIEXTENDEDCOMBO:
				case ExtendedFieldConstants.FIELDTYPE_EXTENDEDCOMBO:
				case ExtendedFieldConstants.FIELDTYPE_BASERATE:
					validatedMap.put(key, jsonResponseValue);
					break;
				default:
					break;
				}
			}
		} catch (Exception e) {
			logger.error("Exception", e);
			throw new InterfaceException("9999", "Unable to process");
		}
		}
		logger.debug(Literal.ENTERING);
		return validatedMap;
	}

	/**
	 * Method for prepare the Extended Field details map according to the given response.
	 * 
	 * @param extendedResMapObject
	 * @param financeDetail
	 */
	protected void prepareResponseObj(Map<String, Object> validatedMap, FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		if (validatedMap != null) {
			Map<String, Object> extendedMapObject = financeDetail.getExtendedFieldRender().getMapValues();
			if (extendedMapObject == null) {
				extendedMapObject = new HashMap<String, Object>();
			}
			for (Entry<String, Object> entry : validatedMap.entrySet()) {
				extendedMapObject.put(entry.getKey(), entry.getValue());
			}
			financeDetail.getExtendedFieldRender().setMapValues(extendedMapObject);
		}
		logger.debug(Literal.LEAVING);
	}

	protected void logInterfaceDetails(InterfaceLogDetail interfaceLogDetail) {
		interfaceLoggingDAO.save(interfaceLogDetail);
		doClearInterfaceLogDetails();
	}

	private void doClearInterfaceLogDetails() {
		this.status = InterfaceConstants.STATUS_SUCCESS;
		this.errorCode = null;
		this.errorDesc = null;
		this.jsonResponse = null;
		this.reqSentOn = null;
	}

	/**
	 * 
	 * @param url
	 * @param request
	 * @param response
	 * @param reqSentOn
	 * @param status
	 * @param errorCode
	 * @param errorDesc
	 * @param reference
	 * @return
	 */
	protected InterfaceLogDetail prepareLoggingData(String url, Object request, String response, Timestamp reqSentOn,
			String status, String errorCode, String errorDesc, String reference) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.SORT_PROPERTIES_ALPHABETICALLY, false);
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector());
		mapper.setSerializationInclusion(Inclusion.NON_NULL);
		mapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		mapper.setDateFormat(dateFormat);

		InterfaceLogDetail detail = new InterfaceLogDetail();
		detail.setReference(reference);
		String[] values = url.split("/");
		detail.setServiceName(values[values.length - 1]);
		detail.setEndPoint(url);
		try {
			if (request != null) {
				if (request != null && request instanceof String) {
					detail.setRequest(request.toString());
				}
				detail.setRequest(mapper.writeValueAsString(request));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		detail.setReqSentOn(reqSentOn);
		detail.setResponse(response);
		detail.setRespReceivedOn(new Timestamp(System.currentTimeMillis()));
		detail.setStatus(status);
		detail.setErrorCode(errorCode);
		if (errorDesc != null && errorDesc.length() > 200) {
			detail.setErrorDesc(errorDesc.substring(0, 190));
		}
		return detail;
	}

	/**
	 * Method for log the Exception details
	 * 
	 * @param e
	 * @param serviceUrl
	 * @param requestObject
	 */
	protected void doLogError(Exception e, String serviceUrl, Object requestObject) {
		logger.debug(Literal.ENTERING);

		status = "FAILED";
		errorCode = "9999";
		StringWriter writer = new StringWriter();
		e.printStackTrace(new PrintWriter(writer));
		errorDesc = writer.toString();
		InterfaceLogDetail interfaceLogDetail = prepareLoggingData(serviceUrl, requestObject, jsonResponse, reqSentOn,
				status, errorCode, errorDesc, reference);
		logInterfaceDetails(interfaceLogDetail);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for get the appDate
	 * 
	 * @return appDate
	 */
	protected Date getAppDate() {
		String appDate;
		try {
			appDate = (String) niyoginDAOImpl.getSMTParameter("APP_DATE", String.class);
			return DateUtil.parse(appDate, "yyyy-MM-dd"); // FIXME Deriving Application date should be from single place for all modules.
		} catch (Exception e) {

		}
		return null;
	}

	public String getPanNumber(List<CustomerDocument> customerDetails) {
		String pannumber = "";
		if (customerDetails != null && !customerDetails.isEmpty()) {
			String[] pancards = null;
			String panCard = StringUtils.trimToEmpty((String) getSMTParameter("PAN_DOC_TYPE", String.class));
			if (panCard.contains(",")) {
				pancards = panCard.split(",");
			}else{
				pancards=new String[1];
				pancards[0]=panCard;
			}

			if (pancards != null) {
				for (int i = 0; i < pancards.length; i++) {
					for (CustomerDocument customerDocument : customerDetails) {
						if (StringUtils.equals(pancards[i],customerDocument.getCustDocCategory())) {
							pannumber=customerDocument.getCustDocTitle();
							return pannumber;
						}
					}
				}
			}
		}
		return pannumber;
	}
	
	/**
	 * Method for getting the City
	 * 
	 * @param address
	 * @return
	 */
	protected City getCityDetails(CustomerAddres address) {
		City city = niyoginDAOImpl.getCityDetails(address.getCustAddrCountry(), address.getCustAddrProvince(),
				address.getCustAddrCity(), "_AView");
		return city;
	}

	/**
	 * Method for get the Co-Applicants
	 * 
	 * @param coApplicantIDs
	 * @return
	 */
	protected List<CustomerDetails> getCoApplicants(List<Long> coApplicantIDs) {
		return niyoginDAOImpl.getCoApplicants(coApplicantIDs, "_VIEW");
	}

	
	/**
	 * Method for get the list of email's for given customerIds
	 * 
	 * @param customerIds
	 * @return
	 */
	protected List<CustomerEMail> getCustomersEmails(Set<Long> customerIds) {
		return niyoginDAOImpl.getCustomersEmails(customerIds, "_VIEW");
	}
	
	/**
	 * Method for get the list of documents for given customerIds
	 * 
	 * @param customerIds
	 * @return
	 */
	protected List<CustomerDocument> getCustomersDocuments(long customerId) {
		return niyoginDAOImpl.getCustomerDocumentByCustomer(customerId, "");
	}
	/**
	 * Method for get the pincodeGroupId
	 * 
	 * @param pincode
	 */
	protected long getPincodeGroupId(String pincode) {
		return niyoginDAOImpl.getPincodeGroupId(pincode);
	}
	
	/**
	 * Method for get the Hold reasons.
	 * @param id
	 * @return
	 */
	protected List<HoldReason> getholdReasonsById(List<Long> reasonIds) {
		return niyoginDAOImpl.getholdReasonsById(reasonIds);
	}
	
	protected Object getSMTParameter(String sysParmCode, Class<?> type){
		return niyoginDAOImpl.getSMTParameter(sysParmCode, type);
	}

	/**
	 * Method for get the CoApplicants with ExtendedField Details
	 * 
	 * @param customerIds
	 * @return
	 */
	protected List<CustomerDetails> getCoApplicantsWithExtFields(List<Long> customerIds) {
		logger.debug(Literal.ENTERING);
		StringBuilder tableName = new StringBuilder("");
		ExtendedFieldHeader extendedFieldHeader = null;
		Map<String, Object> extMapValues = null;
		List<CustomerDetails> customerDetailList = new ArrayList<CustomerDetails>(1);
		List<Customer> customers = niyoginDAOImpl.getCustomerByID(customerIds, "_AVIEW");
		String module = ExtendedFieldConstants.MODULE_CUSTOMER;
		for (Customer customer : customers) {
			CustomerDetails customerDetails = new CustomerDetails();
			customerDetails.setCustID(customer.getCustID());
			customerDetails.setCustCIF(customer.getCustCIF());
			extendedFieldHeader = niyoginDAOImpl.getExtendedFieldHeaderByModuleName(module, customer.getCustCtgCode());
			if (extendedFieldHeader != null) {
				ExtendedFieldRender extendedFieldRender = new ExtendedFieldRender();
				extendedFieldRender.setReference(customer.getCustCIF());
				tableName.append(module);
				tableName.append("_");
				tableName.append(customer.getCustCtgCode());
				tableName.append("_ED");
				extMapValues = niyoginDAOImpl.getExtendedField(customer.getCustCIF(), tableName.toString());
				extendedFieldRender.setMapValues(extMapValues);
				customerDetails.setExtendedFieldRender(extendedFieldRender);
				tableName.setLength(0);
				customerDetailList.add(customerDetails);
			}
		}
		logger.debug(Literal.LEAVING);
		return customerDetailList;
	}
	
	protected String getLovFieldDetailByCode(String fieldCode, String fieldCodeValue) {
		return niyoginDAOImpl.getLovFieldDetailByCode(fieldCode, fieldCodeValue, "");
	}
	
	public void setInterfaceLoggingDAO(InterfaceLoggingDAO interfaceLoggingDAO) {
		this.interfaceLoggingDAO = interfaceLoggingDAO;
	}

	public void setNiyoginDAOImpl(NiyoginDAOImpl niyoginDAOImpl) {
		this.niyoginDAOImpl = niyoginDAOImpl;
	}

	public void setClient(JSONClient client) {
		this.client = client;
	}

	

}