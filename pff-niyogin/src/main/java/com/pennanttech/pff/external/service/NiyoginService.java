package com.pennanttech.pff.external.service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennanttech.dataengine.util.DateUtil;
import com.pennanttech.logging.model.InterfaceLogDetail;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.InterfaceConstants;
import com.pennanttech.pff.logging.dao.InterfaceLoggingDAO;

public abstract class NiyoginService {
	private static final Logger				logger				= Logger.getLogger(NiyoginService.class);

	protected DataSource					dataSource;
	protected JdbcTemplate					jdbcTemplate;
	protected NamedParameterJdbcTemplate	namedJdbcTemplate;
	
	private InterfaceLoggingDAO	interfaceLoggingDAO;
	
	protected DataSourceTransactionManager	transManager;
	protected DefaultTransactionDefinition	transDef;
	
	public static final int					LENGTH_ACCOUNT		= 50;
	public static final int					LENGTH_FREQUENCY	= 5;
	public static final String APIDateFormatter = "yyyy-MM-dd'T'HH:mm:ss";
	public static final String DELIMITER_COMMA = ",";

	public NiyoginService() {
		super();
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		setTransManager(dataSource);
	}

	protected Object getSMTParameter(String sysParmCode, Class<?> type) {
		MapSqlParameterSource paramMap;

		StringBuilder sql = new StringBuilder();
		paramMap = new MapSqlParameterSource();

		sql.append("SELECT SYSPARMVALUE FROM SMTPARAMETERS where SYSPARMCODE = :SYSPARMCODE");
		paramMap.addValue("SYSPARMCODE", sysParmCode);

		try {
			return namedJdbcTemplate.queryForObject(sql.toString(), paramMap, type);
		} catch (Exception e) {
			logger.error("The parameter code " + sysParmCode + " not configured.");
		} finally {
			paramMap = null;
			sql = null;
		}
		return null;
	}

	protected int updateParameter(String sysParmCode, Object value) throws Exception {
		MapSqlParameterSource paramMap;

		StringBuilder sql = new StringBuilder();
		paramMap = new MapSqlParameterSource();

		sql.append("UPDATE SMTPARAMETERS SET SYSPARMVALUE = :SYSPARMVALUE where SYSPARMCODE = :SYSPARMCODE");
		paramMap.addValue("SYSPARMCODE", sysParmCode);
		paramMap.addValue("SYSPARMVALUE", value);

		try {
			return namedJdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			logger.error("Entering", e);
			throw new Exception("Unable to update the " + sysParmCode + ".");
		}
	}

	/**
	 * Method for fetch the ExtendedFieldDetails based on given fieldaNames
	 * 
	 * @param fieldNames
	 * @return extendedFieldDetailList
	 * @throws Exception
	 */
	protected List<ExtendedFieldDetail> getExtendedFieldDetailsByFieldName(Set<String> fieldNames) throws Exception {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder();
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		sql.append("SELECT *  FROM EXTENDEDFIELDDETAIL WHERE FIELDNAME IN(:fieldNames)");
		paramMap.addValue("fieldNames", fieldNames);
		logger.debug("selectSql: " + sql.toString());
		try {
			RowMapper<ExtendedFieldDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
					.newInstance(ExtendedFieldDetail.class);
			logger.debug(Literal.LEAVING);
			return this.namedJdbcTemplate.query(sql.toString(), paramMap, typeRowMapper);

		} catch (Exception e) {
			logger.error("Exception", e);
			throw new Exception("Unable to Retrive  the ExtendedFields.");
		}
	}

	protected Date getValueDate() {
		String appDate;
		try {
			appDate = (String) getSMTParameter("APP_VALUEDATE", String.class);
			return DateUtil.parse(appDate, "yyyy-MM-dd"); // FIXME Deriving Application date should be from single place for all modules.
		} catch (Exception e) {

		}
		return null;
	}

	protected Date getAppDate() {
		String appDate;
		try {
			appDate = (String) getSMTParameter("APP_DATE", String.class);
			return DateUtil.parse(appDate, "yyyy-MM-dd"); // FIXME Deriving Application date should be from single place for all modules.
		} catch (Exception e) {

		}
		return null;
	}

	public static MapSqlParameterSource getMapSqlParameterSource(Map<String, Object> map) {
		MapSqlParameterSource parmMap = new MapSqlParameterSource();

		for (Entry<String, Object> entry : map.entrySet()) {
			parmMap.addValue(entry.getKey(), entry.getValue());
		}

		return parmMap;
	}

	private void setTransManager(DataSource dataSource) {
		this.dataSource = dataSource;
		this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

		this.transManager = new DataSourceTransactionManager(dataSource);
		this.transDef = new DefaultTransactionDefinition();
		this.transDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		this.transDef.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		this.transDef.setTimeout(120);
	}

	protected long getSeq(String seqName) {
		logger.debug("Entering");
		StringBuilder sql = null;

		try {
			sql = new StringBuilder();
			sql.append("UPDATE ").append(seqName);
			sql.append(" SET SEQNO = SEQNO + 1");
			this.namedJdbcTemplate.update(sql.toString(), new MapSqlParameterSource());
		} catch (Exception e) {
			logger.error("Exception", e);
		}

		try {
			sql = new StringBuilder();
			sql.append("SELECT SEQNO FROM ").append(seqName);
			return this.namedJdbcTemplate.queryForObject(sql.toString(), new MapSqlParameterSource(), Long.class);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		logger.error(Literal.LEAVING);
		return 0;
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
		try {
			List<ExtendedFieldDetail> configurationList = getExtendedFieldDetailsByFieldName(fieldNames);
			for (String field : fieldNames) {
				String key = field;
				Object fieldValue = extendedFieldMap.get(field);
				ExtendedFieldDetail configuration = null;
				for (ExtendedFieldDetail extdetail : configurationList) {
					if (StringUtils.equals(key, extdetail.getFieldName())) {
						configuration = extdetail;
						break;
					}
				}
				if (configuration == null) {
					continue;
				}
				String jsonResponseValue = Objects.toString(fieldValue, "");
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
					Boolean  booleanValue;
					if (StringUtils.equals(jsonResponseValue, "true")
							|| StringUtils.equals(jsonResponseValue, "false")) {
						 booleanValue = jsonResponseValue.equals("true") ? true : false;
					} else if (StringUtils.equals(jsonResponseValue, "1")
							|| StringUtils.equals(jsonResponseValue, "0")) {
						 booleanValue = jsonResponseValue.equals("1") ? true : false;
					} else {
						throw new InterfaceException("9999", wrongValueMSG + configuration.getFieldLabel());
					}
					validatedMap.put(key,booleanValue);
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
						if(!StringUtils.isEmpty(jsonResponseValue)){
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
						try{
							  radioValue=Integer.parseInt(jsonResponseValue);
						}catch (Exception e) {
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

					validatedMap.put(key, String.valueOf(Math
							.round((Integer.valueOf(jsonResponseValue) / Math.pow(10, configuration.getFieldPrec())))));
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
		logger.debug(Literal.ENTERING);
		return validatedMap;
	}
	
	protected void logInterfaceDetails(InterfaceLogDetail interfaceLogDetail) {
		interfaceLoggingDAO.save(interfaceLogDetail);
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
		detail.setServiceName(values[values.length-1]);
		detail.setEndPoint(url);
		try {
			if(request != null) {
				if(request != null && request instanceof String) {
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
		if(errorDesc != null && errorDesc.length() > 200) {
		detail.setErrorDesc(errorDesc.substring(0, 190));
		}
		return detail;
	}
	
	public String formatDate(Date inputDate, String pattern) {
		String formattedDate = null;
		if(inputDate == null) {
			return null;
		}
		SimpleDateFormat dateFormatter = new SimpleDateFormat(pattern);
		formattedDate = dateFormatter.format(inputDate);
		return formattedDate;
	}
	
	/**
	 * Take String Date and return UTIL Date in DB Format
	 * 
	 * @param date
	 *            (Date)
	 * 
	 * @return Date
	 */
	public static Date getFormattedDate(String date, String pattern) {
		if (date == null) {
			return null;
		}
		return parseDate(date, pattern);
	}
	
	private static Date parseDate(String date, String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		java.util.Date uDate = null;
		try {
			uDate = df.parse(date);
		} catch (ParseException e) {
			logger.error("Exception: ", e);
		}
		
		return new Date(uDate.getTime());
	}
	
	public void setInterfaceLoggingDAO(InterfaceLoggingDAO interfaceLoggingDAO) {
		this.interfaceLoggingDAO = interfaceLoggingDAO;
	}
}