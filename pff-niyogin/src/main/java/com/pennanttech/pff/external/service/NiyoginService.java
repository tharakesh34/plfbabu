package com.pennanttech.pff.external.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.InterfaceConstants;
import com.pennanttech.pff.external.dao.NiyoginDAOImpl;
import com.pennanttech.pff.logging.dao.InterfaceLoggingDAO;

public abstract class NiyoginService {
	private static final Logger	logger				= Logger.getLogger(NiyoginService.class);

	private InterfaceLoggingDAO	interfaceLoggingDAO;
	private NiyoginDAOImpl		niyoginDAOImpl;
	protected JSONClient		client;
	public static final int		LENGTH_ACCOUNT		= 50;
	public static final int		LENGTH_FREQUENCY	= 5;
	public static final String	APIDateFormatter	= "yyyy-MM-dd'T'HH:mm:ss";
	public static final String	DELIMITER_COMMA		= ",";
	public static final String	LIST_DELIMETER		= "||";

	private String				ERRORCODE			= "$.errorCode";
	private String				ERRORMESSAGE		= "$.message";
	private String				STATUSCODE			= "$.statusCode";

	public String				reference;
	private final String		wrongValueMSG		= App.getLabel("WRONG_VALUE_EXT");
	private final String		wrongLengthMSG		= App.getLabel("WRONG_LENGTH_EXT");

	public NiyoginService() {
		super();
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
	protected Map<String, Object> getPropValueFromResp(String jsonResponse, String extConfigFileName) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> extendedFieldMap = new HashMap<>(1);
		Properties properties = new Properties();
		InputStream inputStream = this.getClass().getResourceAsStream("/properties/" + extConfigFileName);
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
		List<ExtendedFieldDetail> configurationList = null;
		if (fieldNames == null || (fieldNames != null && fieldNames.isEmpty())) {
			logger.info("Response Elements Not Configured.");
		} else {

			configurationList = niyoginDAOImpl.getExtendedFieldDetailsByFieldName(fieldNames);
			for (String field : fieldNames) {
				try {
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

					String jsonRespValue = Objects.toString(fieldValue, null);
					if (jsonRespValue == null) {
						continue;
					}

					switch (configuration.getFieldType()) {

					case ExtendedFieldConstants.FIELDTYPE_TEXT:
					case ExtendedFieldConstants.FIELDTYPE_MULTILINETEXT:
					case ExtendedFieldConstants.FIELDTYPE_LISTFIELD:
						if (jsonRespValue.length() > configuration.getFieldLength()) {
							validatedMap.put(key, jsonRespValue.substring(0, configuration.getFieldLength()));
						} else {
							validatedMap.put(key, jsonRespValue);
						}
						break;
					case ExtendedFieldConstants.FIELDTYPE_UPPERTEXT:
						if (jsonRespValue.length() > configuration.getFieldLength()) {
							String value = jsonRespValue.substring(0, configuration.getFieldLength());
							validatedMap.put(key, value.toUpperCase());
						} else {
							validatedMap.put(key, jsonRespValue.toUpperCase());
						}
						break;

					case ExtendedFieldConstants.FIELDTYPE_ADDRESS:
						if (jsonRespValue.length() > 100) {
							validatedMap.put(key, jsonRespValue.substring(0, 100));
						} else {
							validatedMap.put(key, jsonRespValue);
						}
						break;

					case ExtendedFieldConstants.FIELDTYPE_DATE:
					case ExtendedFieldConstants.FIELDTYPE_TIME:
						Date dateValue = null;
						try {
							DateFormat formatter = new SimpleDateFormat(InterfaceConstants.InterfaceDateFormatter);
							dateValue = formatter.parse(jsonRespValue);
						} catch (Exception e) {
							logger.error("Exception : ", e);
						}
						validatedMap.put(key, dateValue);
						break;

					case ExtendedFieldConstants.FIELDTYPE_DATETIME:
						Date dateTimeVal = null;
						try {
							DateFormat formatter = new SimpleDateFormat(InterfaceConstants.InterfaceDateFormatter);
							dateTimeVal = formatter.parse(jsonRespValue);
						} catch (Exception e) {
							logger.error("Exception : ", e);
						}
						validatedMap.put(key, dateTimeVal);
						break;

					case ExtendedFieldConstants.FIELDTYPE_BOOLEAN:
						Boolean booleanValue = false;
						if (StringUtils.equals(jsonRespValue, "true") || StringUtils.equals(jsonRespValue, "false")) {
							booleanValue = jsonRespValue.equals("true") ? true : false;
						} else if (StringUtils.equals(jsonRespValue, "1") || StringUtils.equals(jsonRespValue, "0")) {
							booleanValue = jsonRespValue.equals("1") ? true : false;
						} else {
							logger.error(wrongValueMSG + configuration.getFieldLabel());
						}
						validatedMap.put(key, booleanValue);
						break;

					case ExtendedFieldConstants.FIELDTYPE_AMOUNT:
						BigDecimal decimalValue = BigDecimal.ZERO;
						try {
							double rateValue = Double.parseDouble(jsonRespValue);
							decimalValue = BigDecimal.valueOf(rateValue);
						} catch (Exception e) {
							logger.error("Exception : ", e);
						}
						if (jsonRespValue.length() > configuration.getFieldLength() + 2) {
							logger.error(wrongLengthMSG + configuration.getFieldLabel());
						}
						decimalValue = decimalValue.setScale(configuration.getFieldPrec(), RoundingMode.HALF_DOWN);
						validatedMap.put(key, decimalValue);
						break;

					case ExtendedFieldConstants.FIELDTYPE_INT:
						int intValue = 0;
						if (jsonRespValue.length() > configuration.getFieldLength()) {
							logger.error(wrongLengthMSG + configuration.getFieldLabel());
						}
						try {
							if (!StringUtils.isEmpty(jsonRespValue)) {
								intValue = Integer.parseInt(jsonRespValue);
							}
						} catch (Exception e) {
							logger.error("Exception : ", e);
						}
						if (configuration.getFieldMaxValue() > 0 || configuration.getFieldMinValue() > 0) {
							if (!(intValue >= configuration.getFieldMinValue()
									&& intValue <= configuration.getFieldMaxValue())) {
								logger.error(wrongLengthMSG + configuration.getFieldLabel());
							}
						}
						validatedMap.put(key, intValue);
						break;

					case ExtendedFieldConstants.FIELDTYPE_LONG:
						long longValue = 0;
						if (jsonRespValue.length() > configuration.getFieldLength()) {
							logger.error(wrongLengthMSG + configuration.getFieldLabel());
						}
						try {
							longValue = Long.parseLong(jsonRespValue);
						} catch (Exception e) {
							logger.error("Exception : ", e);
						}
						if (configuration.getFieldMaxValue() > 0 || configuration.getFieldMinValue() > 0) {
							if (!(longValue >= configuration.getFieldMinValue()
									&& longValue <= configuration.getFieldMaxValue())) {
								logger.error(wrongLengthMSG + configuration.getFieldLabel());
							}
						}
						validatedMap.put(key, longValue);
						break;

					case ExtendedFieldConstants.FIELDTYPE_RADIO:
						int radioValue = 0;
						if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_RADIO, configuration.getFieldType())) {
							try {
								radioValue = Integer.parseInt(jsonRespValue);
							} catch (Exception e) {
								logger.error("Exception : ", e);
							}
							if (radioValue > configuration.getFieldLength()) {
								logger.error(wrongLengthMSG + configuration.getFieldLabel());
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
									if (vale.equals(jsonRespValue)) {
										validatedMap.put(key, jsonRespValue);
										break;
									}
								}
							} else if (staticList.equals(jsonRespValue)) {
								validatedMap.put(key, jsonRespValue);
								break;
							} else {
								logger.error(wrongValueMSG + configuration.getFieldLabel());
							}
						} else {
							logger.error(wrongValueMSG + configuration.getFieldLabel());
						}
						break;

					//TODO:any regix validation
					case ExtendedFieldConstants.FIELDTYPE_PHONE:
						if (jsonRespValue.length() > 10) {
							validatedMap.put(key, jsonRespValue.substring(0, 10));
							break;
						} else {
							validatedMap.put(key, jsonRespValue);
						}
						break;
					case ExtendedFieldConstants.FIELDTYPE_DECIMAL:
					case ExtendedFieldConstants.FIELDTYPE_ACTRATE:
						Double decValue = getDecimalValue(configuration, jsonRespValue);
						validatedMap.put(key, decValue);
						break;

					case ExtendedFieldConstants.FIELDTYPE_PERCENTAGE:
						double percentage = 0;
						if (jsonRespValue.length() > (configuration.getFieldLength() - configuration.getFieldPrec())) {
							logger.error(wrongLengthMSG + configuration.getFieldLabel());
						}
						try {
							percentage = Double.valueOf(jsonRespValue);
						} catch (Exception e) {
							logger.error("Exception : ", e);
						}
						if (percentage < 0 || percentage > 100) {
							logger.error(wrongLengthMSG + configuration.getFieldLabel());
						}
						if (configuration.getFieldMaxValue() > 0 || configuration.getFieldMinValue() > 0) {
							if (percentage > configuration.getFieldMaxValue()
									|| percentage < configuration.getFieldMinValue()) {
								logger.error(wrongLengthMSG + configuration.getFieldLabel());
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
						if (fieldValue != null && jsonRespValue.contains(DELIMITER_COMMA)) {
							fieldvalues = jsonRespValue.split(DELIMITER_COMMA);
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
									logger.error(wrongLengthMSG + configuration.getFieldLabel());

								} else {
									validatedMap.put(key, jsonRespValue);
								}
							}
						}
						break;

					case ExtendedFieldConstants.FIELDTYPE_FRQ:
						if (jsonRespValue.length() <= LENGTH_FREQUENCY) {
							validatedMap.put(key, jsonRespValue);
						} else {
							logger.error(wrongLengthMSG + configuration.getFieldLabel());
						}

						break;

					case ExtendedFieldConstants.FIELDTYPE_ACCOUNT:
						if (jsonRespValue.length() <= LENGTH_ACCOUNT) {
							validatedMap.put(key, jsonRespValue);
						} else {
							logger.error(wrongLengthMSG + configuration.getFieldLabel());
						}
						break;

					case ExtendedFieldConstants.FIELDTYPE_MULTIEXTENDEDCOMBO:
					case ExtendedFieldConstants.FIELDTYPE_EXTENDEDCOMBO:
					case ExtendedFieldConstants.FIELDTYPE_BASERATE:
						validatedMap.put(key, jsonRespValue);
						break;
					default:
						break;
					}

				} catch (Exception e) {
					logger.error("Exception", e);
				}
			}
		}
		logger.debug(Literal.ENTERING);
		return validatedMap;
	}

	/**
	 * Method for validate the response value and return decimal value.
	 * 
	 * @param configuration
	 * @param jsonRespValue
	 * @return
	 */
	private double getDecimalValue(ExtendedFieldDetail configuration, String jsonRespValue) {
		logger.debug(Literal.ENTERING);

		double decValue = 0;
		String beforePrcsnValue = null;
		String aftrPrcsnValue = null;

		if (jsonRespValue.contains(".")) {
			beforePrcsnValue = jsonRespValue.substring(0, jsonRespValue.indexOf("."));
			aftrPrcsnValue = jsonRespValue.substring(jsonRespValue.indexOf(".") + 1, jsonRespValue.length());
		} else {
			beforePrcsnValue = jsonRespValue;
		}

		if (beforePrcsnValue.length() > configuration.getFieldLength()) {
			logger.error(wrongLengthMSG + configuration.getFieldLabel());
		}

		if (aftrPrcsnValue != null && aftrPrcsnValue.length() > configuration.getFieldPrec()) {
			logger.error(wrongLengthMSG + configuration.getFieldLabel());
		}
		try {
			decValue = Double.parseDouble(jsonRespValue);
		} catch (Exception e) {
			logger.error("Exception : ", e);
		}

		if (configuration.getFieldMaxValue() > 0 || configuration.getFieldMinValue() > 0) {
			if (Math.round(decValue) > configuration.getFieldMaxValue()
					|| Math.round(decValue) < configuration.getFieldMinValue()) {
				logger.error(wrongLengthMSG + configuration.getFieldLabel());
			}
		}
		BigDecimal amount = new BigDecimal(decValue);
		amount = amount.setScale(configuration.getFieldPrec(), RoundingMode.HALF_DOWN);

		logger.debug(Literal.LEAVING);
		return amount.doubleValue();
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
		try {
			interfaceLoggingDAO.save(interfaceLogDetail);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
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

	public String getDocumentNumber(List<CustomerDocument> customerDetails, String type) {
		String docNumber = "";
		if (StringUtils.isBlank(type)) {
			return docNumber;
		}
		if (customerDetails != null && !customerDetails.isEmpty()) {
			String[] docTypes = null;
			if (type.contains(",")) {
				docTypes = type.split(",");
			} else {
				docTypes = new String[1];
				docTypes[0] = type;
			}

			if (docTypes != null) {
				for (int i = 0; i < docTypes.length; i++) {
					for (CustomerDocument customerDocument : customerDetails) {
						if (StringUtils.equals(docTypes[i], customerDocument.getCustDocCategory())) {
							docNumber = customerDocument.getCustDocTitle();
							return docNumber;
						}
					}
				}
			}
		}
		return docNumber;
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

	protected long getCustomerId(String custCIF) {
		return niyoginDAOImpl.getCustomerId(custCIF);
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
	 * 
	 * @param id
	 * @return
	 */
	protected List<HoldReason> getholdReasonsById(List<Long> reasonIds) {
		return niyoginDAOImpl.getholdReasonsById(reasonIds);
	}

	protected Object getSMTParameter(String sysParmCode, Class<?> type) {
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

	/**
	 * Method for Fetch the CustomerTypeCode description for given CustTypeCode.
	 * 
	 * @param custTypeCode
	 * @return
	 */
	protected String getCustTypeDesc(String custTypeCode) {
		return niyoginDAOImpl.getCustTypeDesc(custTypeCode);
	}

	protected String getLovFieldDetailByCode(String fieldCodeValue) {
		return niyoginDAOImpl.getLovFieldDetailByCode(fieldCodeValue, "");
	}

	/**
	 * Method for get the customer PanNumber.
	 * 
	 * @param customerDetails
	 * @return
	 */
	public String getPanNumber(List<CustomerDocument> customerDetails) {
		String pannumber = "";
		if (customerDetails != null && !customerDetails.isEmpty()) {
			String[] pancards = null;
			String panCard = StringUtils.trimToEmpty((String) getSMTParameter("PAN_DOC_TYPE", String.class));
			if (panCard.contains(",")) {
				pancards = panCard.split(",");
			} else {
				pancards = new String[1];
				pancards[0] = panCard;
			}

			if (pancards != null) {
				for (int i = 0; i < pancards.length; i++) {
					for (CustomerDocument customerDocument : customerDetails) {
						if (StringUtils.equals(pancards[i], customerDocument.getCustDocCategory())) {
							pannumber = customerDocument.getCustDocTitle();
							return pannumber;
						}
					}
				}
			}
		}
		return pannumber;
	}

	/**
	 * Method for format the Amount Fields
	 * 
	 * @param amount
	 * @return
	 */
	public static BigDecimal formateAmount(BigDecimal amount) {
		BigDecimal bigDecimal = BigDecimal.ZERO;

		if (amount != null) {
			bigDecimal = amount.divide(new BigDecimal(Math.pow(10, 2)));
		}
		return bigDecimal;
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

	public String getval(Object object) {
		return Objects.toString(object, "");
	}

	public String getErrorCode(String jsonResponse) {
		return Objects.toString(getValueFromResponse(jsonResponse, ERRORCODE), "");
	}

	public String getErrorMessage(String jsonResponse) {
		return Objects.toString(getValueFromResponse(jsonResponse, ERRORMESSAGE), "");
	}

	public String getStatusCode(String jsonResponse) {
		return Objects.toString(getValueFromResponse(jsonResponse, STATUSCODE), "");
	}

	public Object getValueFromResponse(String jsonResponse, String keypath) {
		Object value = null;
		try {
			value = JsonPath.read(jsonResponse, keypath);
		} catch (PathNotFoundException pathNotFoundException) {
			value = null;
		}
		return value;
	}

	public String getTrimmedMessage(String errorDesc) {
		if (errorDesc != null && errorDesc.length() > 149) {
			errorDesc = errorDesc.substring(0, 143);
		}
		return errorDesc;
	}

	public String getWriteException(Exception e) {
		String errorDesc;
		StringWriter writer = new StringWriter();
		e.printStackTrace(new PrintWriter(writer));
		errorDesc = writer.toString();
		return errorDesc;
	}

}