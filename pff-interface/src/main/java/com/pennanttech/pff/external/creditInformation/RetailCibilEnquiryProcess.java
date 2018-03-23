package com.pennanttech.pff.external.creditInformation;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.zkoss.json.JSONArray;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennanttech.logging.model.InterfaceLogDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.InterfaceConstants;
import com.pennanttech.pff.core.util.DateUtil;
import com.pennanttech.pff.external.AbstractInterface;
import com.pennanttech.pff.external.CreditInformation;
import com.pennanttech.pff.external.util.StaticListUtil;
import com.pennanttech.pff.logging.dao.InterfaceLoggingDAO;

public class RetailCibilEnquiryProcess extends AbstractInterface implements CreditInformation {
	private static final Logger logger = Logger.getLogger(RetailCibilEnquiryProcess.class);

	JSONObject jsonObject = new JSONObject();
	CibilResponseDetails details;
	private String CBIL_REPORT_MEMBER_CODE;
	private String CBIL_REPORT_MEMBER_ID;
	private String CBIL_REPORT_MEMBER_PASSWORD;
	private String CBIL_ENQUIRY_SCORE_TYPE;
	private Map<String, String> parameters = new HashMap<>();
	@Autowired(required = false)
	private InterfaceLoggingDAO interfaceLoggingDAO;
	public static final String	DELIMITER_COMMA		= ",";
	public static final int		LENGTH_ACCOUNT		= 50;
	public static final int		LENGTH_FREQUENCY	= 5;
	
	public static final String	RSN_CODE			= "ERROR_DETAILS";


	private static final String DATE_FORMAT = "ddMMYYYY";
	private final String		extConfigFileName	= "RetailCibilConsumer.properties";


	/**
	 * Method to  get the CIBIL details of the Customer and set these details to ExtendedFieldDetails.
	 * 
	 * @param auditHeader
	 * @return auditHeader
	 * @throws Exception 
	 */
	@Override
	public AuditHeader getCreditEnquiryDetails(AuditHeader auditHeader) throws Exception {
		logger.debug(Literal.ENTERING);

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		CustomerDetails customerDetails = financeDetail.getCustomerDetails();
		Customer customer = customerDetails.getCustomer();

		loadParameters();
		loadAccountTypes();
		
		if (StringUtils.equals("RETAIL", customer.getCustCtgCode())) {
			processRetailCustomer(financeDetail, customerDetails);

		} else if (StringUtils.equals("CORP", customer.getCustCtgCode())) {

		}

		return auditHeader;
	}


	/**
	 * Method for process the CIBIL details of Applicant.
	 * 
	 * @param financeDetail
	 * @param customerDetails
	 */
	private void processRetailCustomer(FinanceDetail financeDetail, CustomerDetails customerDetails) {
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		String reference = customerDetails.getCustomer().getCustCIF();
		Timestamp reqSentOn = new Timestamp(System.currentTimeMillis());
		Map<String, Object> appplicationdata = new HashMap<>();

		String builder = prepareRequest(financeMain, customerDetails, reference, reqSentOn, appplicationdata);
		String response = null;

		try {
			response = sendRequest(builder.toString());
			if (response.startsWith("ERRR")) {
				parseErrorResponse(response);
				doInterfaceLogging(reference, builder.toString(), response, "InvalidRequest", jsonObject.toString(),
						reqSentOn, InterfaceConstants.STATUS_FAILED);
				appplicationdata.put(RSN_CODE, "Invalid Request");
				logger.debug("Error Respone: " + jsonObject.toString());

			} else {
				parseHeaderResponse(response);
				HashMap<String, String> detailsResult = details.getCibilResponseArray(response);
				jsonObject = parseDetailsresponse(detailsResult);
				Map<String, Object> mapdata = getPropValueFromResp(jsonObject, extConfigFileName);
				Map<String, Object> mapvalidData = validateExtendedMapValues(mapdata);
				appplicationdata.putAll(mapvalidData);
				doInterfaceLogging(reference, builder.toString(), response, null, jsonObject.toString(), reqSentOn,
						InterfaceConstants.STATUS_SUCCESS);
				logger.debug("Success Respone :" + jsonObject.toString());
			}

		} catch (Exception e) {
			logger.debug(e.getMessage());
			appplicationdata.put(RSN_CODE, e.getMessage());
			doInterfaceLogging(reference, builder.toString(), null, null, e.getMessage(), reqSentOn,
					InterfaceConstants.STATUS_FAILED);
		}

		prepareResponseObj(appplicationdata, customerDetails);

	}


	private String prepareRequest(FinanceMain financeMain, CustomerDetails customerDetails, String reference,
			Timestamp reqSentOn, Map<String, Object> appplicationdata) {
		String response = null;
		StringBuilder builder = new StringBuilder();
		try {
			prepareCibilHeader(builder, customerDetails, financeMain);
			prepareCustomerNameSegment(builder, customerDetails);
			prepareIdentificationSegment(builder, customerDetails);
			prepareTelephoneSegment(builder, customerDetails);
			prepareAddressSegment(builder, customerDetails);
			prepareEndSegment(builder);
			logger.debug("Request String : " + builder.toString());

			

		} catch (Exception e) {
			logger.debug(e.getMessage());
			appplicationdata.put(RSN_CODE, e.getMessage());
			doInterfaceLogging(reference, builder.toString(), null, null, e.getMessage(), reqSentOn, InterfaceConstants.STATUS_FAILED);

		}
		return response;
	}

	
	private Map<String, Object> getPropValueFromResp(JSONObject jsonObject, String extConfigFileName) {

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
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			Object value = null;
			try {
				value = JsonPath.read(jsonObject, properties.getProperty(key));
			} catch (PathNotFoundException pathNotFoundException) {
				value = null;
			}
			extendedFieldMap.put(key, value);
		}
		logger.debug(Literal.LEAVING);

		return extendedFieldMap;
	}


	private void loadParameters() {
		this.CBIL_REPORT_MEMBER_CODE = (String) getSMTParameter("CBIL_REPORT_MEMBER_CODE", String.class);
		this.CBIL_REPORT_MEMBER_ID = (String) getSMTParameter("CBIL_REPORT_MEMBER_ID", String.class);
		this.CBIL_REPORT_MEMBER_PASSWORD = (String) getSMTParameter("CBIL_REPORT_MEMBER_PASSWORD", String.class);
		this.CBIL_ENQUIRY_SCORE_TYPE = (String) getSMTParameter("CBIL_ENQUIRY_SCORE_TYPE", String.class);
	}
	
	
	private void loadAccountTypes() {
		logger.info("Loading AccountTypes..");
		MapSqlParameterSource paramMap;

		StringBuilder sql = new StringBuilder();
		paramMap = new MapSqlParameterSource();

		sql.append("SELECT CODE, FINTYPE FROM CIBIL_ACCOUNT_TYPES_MAPPING");

		namedJdbcTemplate.query(sql.toString(), paramMap, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				parameters.put(rs.getString("FINTYPE"), rs.getString("CODE"));
			}
		});
	}
	
	/**
	 * 
	 * The TUEF Enquiry Header Segment marks the beginning of the Enquiry Record, and:
	 * <li>It is a Required segment.
	 * <li>It is of a fixed size of 115 bytes.
	 * <li>It can appear only once per Enquiry Record.
	 * <li>All the fields must be provided otherwise the entire Enquiry Record is rejected</li>.
	 * <li>All fields must be valid.</li>
	 * 
	 */
	private void prepareCibilHeader(StringBuilder builder, CustomerDetails customerDetails, FinanceMain financeMain)
			throws Exception {
		logger.debug(Literal.ENTERING);

		builder.append(InterfaceConstants.Enquiry_Header_Segment);
		builder.append(InterfaceConstants.Enquiry_Header_version);
		builder.append(StringUtils.rightPad(CBIL_REPORT_MEMBER_CODE, 25, ""));
		builder.append(StringUtils.rightPad("0", 2, "0"));
		builder.append(StringUtils.rightPad(CBIL_REPORT_MEMBER_ID, 30, ""));
		builder.append(StringUtils.rightPad(CBIL_REPORT_MEMBER_PASSWORD, 30, ""));
		
		String enqPurpose = parameters.get(financeMain.getFinType());
		builder.append(enqPurpose);
		
		builder.append(StringUtils.rightPad(String.valueOf(financeMain.getFinAmount()), 9, "0"));
		builder.append(StringUtils.rightPad("", 03, ""));
		builder.append(CBIL_ENQUIRY_SCORE_TYPE);
		builder.append(InterfaceConstants.Output_Format);
		builder.append("1");
		builder.append(InterfaceConstants.Input_Output_Media);
		builder.append(InterfaceConstants.Authentication_Method);

		logger.debug(Literal.LEAVING);

	}
	
	/**
	 * The PN Segment describes personal consumer information, and:
	 * <li>It is a Required segment.</li>
	 * <li>It is variable in length and can be of a maximum size of 174 bytes.</li>
	 * <li>It occurs only once per record.</li>
	 * <li>Tag 06 is reserved for future use.</li>
	 */

	private void prepareCustomerNameSegment(StringBuilder builder, CustomerDetails customerDetails) throws Exception {
		logger.debug(Literal.ENTERING);
		writeValue(builder, InterfaceConstants.Name_Segment, "N01", "03");
		writeCustomerName(builder, customerDetails.getCustomer());
		writeValue(builder, "07", DateUtil.format(customerDetails.getCustomer().getCustDOB(), DATE_FORMAT), "08");
		logger.debug(Literal.LEAVING);

	}
	
	
	/**
	 * 
	 * The ID Segment contains identification information about the consumer applying for credit, and:  This is a
	 * Required segment (with ID Type of 01, 02, 03, or 04) when no valid Telephone (PT) segment is provided except when
	 * the Enquiry Purpose (Positions 94-95 of the TUEF Enquiry Header Segment) is “Account Review”, “Retro Enquiry” or
	 * “Locate Plus”.
	 * <li>It is variable in length and can be a maximum of 47 bytes.</li>
	 * <li>This can occur maximum of 8 times per Enquiry Record.</li>
	 * <li>The ID Type(s) should be unique within the same Enquiry Record.</li>
	 * 
	 */
	private void prepareIdentificationSegment(StringBuilder builder, CustomerDetails customerDetails) {
		logger.debug(Literal.ENTERING);

		List<CustomerDocument> documents = customerDetails.getCustomerDocumentsList();

		int i = 0;
		for (CustomerDocument document : documents) {
			String docCode = document.getCustDocCategory();
			if (docCode == null || "07".equals(docCode) || "08".equals(docCode)) {
				continue;
			}

			if (++i > 8) {
				break;
			}

			writeValue(builder, InterfaceConstants.Identification_Segment, "I0" + i, "03");
			writeValue(builder, "01", document.getCustDocCategory(), "02");
			writeValue(builder, "02", document.getCustDocTitle(), 30, "ID");

			if (document.getCustDocIssuedOn() != null) {
				writeValue(builder, "03", DateUtil.format(document.getCustDocIssuedOn(), DATE_FORMAT), "08");
			}
			if (document.getCustDocExpDate() != null) {
				writeValue(builder, "04", DateUtil.format(document.getCustDocExpDate(), DATE_FORMAT), "08");

			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The PT Segment contains the known phone numbers of the consumer, and:
	 * <li> This is a Required segment when no valid Identification (ID) segment with either Income Tax ID Number,
	 * Passport Number, Voter ID Number or Driver’s License Number is provided except when the Enquiry Purpose</li>
	 * <li>It is variable in length and can be of a maximum size of 51 bytes</li>
	 * <li>This can occur maximum of 4 times per Enquiry Record.</li>
	 */
	private void prepareTelephoneSegment(StringBuilder builder, CustomerDetails customerDetails) {
		List<CustomerPhoneNumber> phoneNumbers = customerDetails.getCustomerPhoneNumList();

		if (phoneNumbers == null || phoneNumbers.isEmpty()) {
			return;
		}

		int i = 0;
		for (CustomerPhoneNumber phoneNumber : phoneNumbers) {
			if (++i > 10) {
				break;
			}

			writeValue(builder, InterfaceConstants.Telephone_Segment,
					"T" + StringUtils.leftPad(String.valueOf(i), 2, "0"), "03");
			writeValue(builder, "01", phoneNumber.getPhoneNumber(), 20, "PT");
			if (phoneNumber.getPhoneTypeCode() != null) {
				writeValue(builder, "03", phoneNumber.getPhoneTypeCode(), "02");
			} else {
				writeValue(builder, "03", "00", "02");
			}
		}

	}

	/**
	 * The PA Segment(s) contain(s) the known address(es) of the consumer, and
	 * <li>It is a Required segment.</li>
	 * <li>It is variable in length and can be a maximum of 259 bytes.</li>
	 * <li>It occurs at least once, but no more than twice per Enquiry Record. Where possible, enter the Current Address
	 * in the first Address Segment and the Permanent Address in the second Address Segment</li>.
	 */
	private void prepareAddressSegment(StringBuilder builder, CustomerDetails customerDetails) throws Exception {

		List<CustomerAddres> addresses = customerDetails.getAddressList();

		int i = 0;
		for (CustomerAddres address : addresses) {
			if (++i > 2) {
				break;
			}
			writeValue(builder, InterfaceConstants.Address_Segment,
					"A".concat(StringUtils.leftPad(String.valueOf(i), 2, "0")), "03");
			writeCustomerAddress(builder, address);

			writeValue(builder, "06", address.getCustAddrProvince(), "02");
			writeValue(builder, "07", address.getCustAddrZIP(), 10, InterfaceConstants.Address_Segment);

			if (address.getCustAddrType() != null) {
				writeValue(builder, "08", address.getCustAddrType(), "02");
			} else {
				writeValue(builder, "08", "04", "02");
			}
		}

	}
	
	/**
	 * The ES Segment marks the end of the Enquiry Record, and:
	 * <li>It is a Required segment.</li>
	 * <li>It is of a fixed size of 15 bytes.</li>
	 * <li>It can appear only once per Enquiry Record.</li>
	 */
	private void prepareEndSegment(StringBuilder builder) {
		builder.append(InterfaceConstants.End_Segment);
		builder.append(StringUtils.leftPad(String.valueOf(builder.toString().length() + (InterfaceConstants.EndCharacters).toString().length()), 5, "0"));
		builder.append((InterfaceConstants.EndCharacters).toString());
	}
	
	
	/**
	 * Append the fixed length tags to provided StringBuilder
	 * 
	 * @param builder
	 *            The StringBuilder to append the tags.
	 * @param fieldTag
	 *            Filed Tag
	 * @param value
	 *            Filed value
	 * @param size
	 *            length of the value
	 */
	private void writeValue(StringBuilder builder, String fieldTag, String value, String size) {
		writeValue(builder, concat(fieldTag, size, value));
	}

	private String concat(String fieldTag, String length, String value) {
		return fieldTag.concat(length).concat(value);
	}

	private void writeValue(StringBuilder builder, String value) {
		builder.append(value);
	}

	/**
	 * Append variable length tags to provided StringBuilder
	 * 
	 * @param builder
	 *            The StringBuilder tags.
	 * @param fieldTag
	 *            Field Tag
	 * @param value
	 *            Field Value
	 * @param maxLength
	 *            Max length of the tag
	 * @throws IllegalArgumentException
	 */
	private void writeValue(StringBuilder builder, String fieldTag, String value, int maxLength, String segment) {
		if (StringUtils.isBlank(value)) {
			return;
		}

		int size = value.length();
		String length = null;

		if (maxLength < 99) {
			length = StringUtils.leftPad(String.valueOf(size), 2, "0");
		} else if (maxLength < 999) {
			length = StringUtils.leftPad(String.valueOf(size), 3, "0");
		} else if (maxLength < 9999) {
			length = StringUtils.leftPad(String.valueOf(size), 4, "0");
		}

		if (value.length() > maxLength) {
			throw new IllegalArgumentException(
					String.format("Max length exceeded for the tag %s in segment %s", fieldTag, segment));
		}

		builder.append(concat(fieldTag, length, value));

	}

	private void writeCustomerName(StringBuilder writer, Customer customer) throws Exception {
		StringBuilder builder = new StringBuilder();

		if (customer.getCustFName() != null) {
			builder.append(StringUtils.trimToEmpty(customer.getCustFName()));
			if (customer.getCustMName() != null || customer.getCustLName() != null) {
				builder.append(" ");
			}
		}

		if (customer.getCustMName() != null) {
			builder.append(StringUtils.trimToEmpty(customer.getCustMName()));
			if (customer.getCustLName() != null) {
				builder.append(" ");
			}
		}

		if (customer.getCustLName() != null) {
			builder.append(StringUtils.trimToEmpty(customer.getCustLName()));
		}

		String customerName = builder.toString();
		try {
			Pattern regex = Pattern.compile("[^\\s\"']+|\"[^\"]*\"|'[^']*'");
			Matcher regexMatcher = regex.matcher(customerName);

			builder = new StringBuilder();
			int field = 0;
			while (regexMatcher.find()) {
				if (field >= 5) {
					break;
				}

				String name = regexMatcher.group();

				if ((builder.length() + name.length()) < 26) {
					if (builder.length() > 0) {
						builder.append(" ");
					}
					builder.append(name);

				} else {
					writeValue(writer, "0".concat(String.valueOf(++field)),
							StringUtils.substring(builder.toString(), 0, 26), 26, InterfaceConstants.Name_Segment);
					builder = new StringBuilder();
					builder.append(name);
				}

			}

			if (builder.length() > 0) {
				if (builder.length() > 26) {
					writeValue(writer, "0".concat(String.valueOf(++field)),
							StringUtils.substring(builder.toString(), 0, 26), 26, InterfaceConstants.Name_Segment);
				} else {
					writeValue(writer, "0".concat(String.valueOf(++field)), builder.toString(), 26, InterfaceConstants.Name_Segment);
				}
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}
	}

	private void writeCustomerAddress(StringBuilder writer, CustomerAddres custAddr) throws IOException {
		StringBuilder builder = new StringBuilder();

		if (custAddr.getCustAddrHNbr() != null) {
			builder.append(StringUtils.trimToEmpty(custAddr.getCustAddrHNbr()));
			if (custAddr.getCustFlatNbr() != null || custAddr.getCustAddrStreet() != null
					|| custAddr.getCustAddrLine1() != null || custAddr.getCustAddrLine2() != null) {
				builder.append(" ");
			}
		}

		if (custAddr.getCustAddrStreet() != null) {
			builder.append(StringUtils.trimToEmpty(custAddr.getCustAddrStreet()));
			if (custAddr.getCustFlatNbr() != null || custAddr.getCustAddrLine1() != null
					|| custAddr.getCustAddrLine2() != null) {
				builder.append(" ");
			}
		}

		if (custAddr.getCustFlatNbr() != null) {
			builder.append(StringUtils.trimToEmpty(custAddr.getCustFlatNbr()));
			if (custAddr.getCustAddrLine1() != null || custAddr.getCustAddrLine2() != null) {
				builder.append(" ");
			}
		}

		if (custAddr.getCustAddrLine1() != null) {
			builder.append(StringUtils.trimToEmpty(custAddr.getCustAddrLine1()));
			if (custAddr.getCustAddrLine2() != null) {
				builder.append(" ");
			}
		}

		if (custAddr.getCustAddrLine2() != null) {
			builder.append(StringUtils.trimToEmpty(custAddr.getCustAddrLine2()));
		}

		String custAddress = builder.toString();
		try {

			Pattern regex = Pattern.compile("[^\\s\"']+|\"[^\"]*\"|'[^']*'");
			Matcher regexMatcher = regex.matcher(custAddress);

			builder = new StringBuilder();
			int field = 0;
			while (regexMatcher.find()) {
				if (field >= 5) {
					break;
				}

				String address = regexMatcher.group();

				if ((builder.length() + address.length()) < 40) {
					if (builder.length() > 0) {
						builder.append(" ");
					}
					builder.append(address);

				} else {
					writeValue(writer, "0".concat(String.valueOf(++field)),
							StringUtils.substring(builder.toString(), 0, 40), 40, InterfaceConstants.Address_Segment);
					builder = new StringBuilder();
					builder.append(address);
				}

			}

			if (builder.length() > 0) {
				if (builder.length() > 40) {
					writeValue(writer, "0".concat(String.valueOf(++field)),
							StringUtils.substring(builder.toString(), 0, 40), 40, "PA");
				} else {
					writeValue(writer, "0".concat(String.valueOf(++field)), builder.toString(), 40, InterfaceConstants.Address_Segment);
				}
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	
	/**
	 * Sends the Request through SocketIp
	 * @param requestmessage
	 * @return response
	 * @throws Exception
	 */
	private String sendRequest(String requestmessage) throws Exception {
		logger.debug(Literal.ENTERING);

		String response;
		CibilClient client = new CibilClient();
		requestmessage = requestmessage + (char) 19;
		// We want to make timeout if  response takes longer than 60 seconds
		client.setDefaultTimeout(InterfaceConstants.SCOKET_TIMEOUT);
		client.connect(InterfaceConstants.SOCKET_IP);
		try {
			client.sendData(requestmessage);
		} catch (Exception e) {
			
		}

		response = client.getData();
		logger.debug("response :" + response);
		client.disconnect();
		logger.debug("Disconnected to the socket succesfully");

		return response.toString();
	}
	
	
	private void parseErrorResponse(String response) throws Exception {
		logger.debug(Literal.ENTERING);
		String header = response.substring(0, 18);
		parseErrorHeder(header);
		// get the details Map as key value format where key is segment and
		// value as segment data.
		HashMap<String, String> detailsResult = details.getCibilResponseArray(response);
		parseDetailsresponse(detailsResult);
		logger.debug("Literal.LEAVING");

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
	 * @param status
	 */
	private void doInterfaceLogging(String reference, String requets, String response, String errorCode,
			String errorDesc, Timestamp reqSentOn, String status) {
		logger.debug(Literal.ENTERING);
		InterfaceLogDetail iLogDetail = new InterfaceLogDetail();
		iLogDetail.setReference(reference);
		iLogDetail.setServiceName("CIBIL");
		iLogDetail.setEndPoint(InterfaceConstants.SOCKET_IP);
		iLogDetail.setRequest(requets);
		iLogDetail.setReqSentOn(reqSentOn);

		iLogDetail.setResponse(response);
		iLogDetail.setRespReceivedOn(new Timestamp(System.currentTimeMillis()));
		iLogDetail.setStatus(status);
		iLogDetail.setErrorCode(errorCode);
		if (errorDesc != null && errorDesc.length() > 200) {
			iLogDetail.setErrorDesc(errorDesc.substring(0, 190));
		}

		logInterfaceDetails(iLogDetail);
		logger.debug(Literal.LEAVING);
	}
	
	private void parseHeaderResponse(String cibilResponse) {
		logger.debug(Literal.ENTERING);
		String responseHeader = cibilResponse.substring(0, 94);
		jsonResopnseHeader(responseHeader);
		logger.debug("Literal.ENTERING");
	}
	
	/**
	 * parses the response from CIBIL and puts in JSON object
	 * 
	 * @param detailsResult
	 */

	private JSONObject parseDetailsresponse(HashMap<String, String> detailsResult) throws Exception {
		logger.debug(Literal.ENTERING);

		String key = null;

		try {
			LinkedHashMap<String, String> requiredValue = new LinkedHashMap<String, String>();
			Iterator<String> itr = detailsResult.keySet().iterator();
			while (itr.hasNext()) {
				key = itr.next();
				// Name Segment Non repeated segment
				if ("PN".equals(key)) {
					String pnSegment = detailsResult.get("PN");
					String[] required = pnSegment.split("PN03N01");
					requiredValue = details.getCibilResponseDetails(required[1]);

					Iterator<String> it = requiredValue.keySet().iterator();
					while (it.hasNext()) {
						String PnKey = it.next();

						for (int i = 0; i < StaticListUtil.getNameSegmentFieldTypes().size(); i++) {
							String value = StaticListUtil.getNameSegmentFieldTypes().get(i).getValue();
							String label = StaticListUtil.getNameSegmentFieldTypes().get(i).getLabel();
							if (PnKey.equals(value) && !StringUtils.trimToEmpty(requiredValue.get(value)).equals("")) {
								jsonObject.put(label, requiredValue.get(value));
							}
						}
					}
				}
				// Id Segment  repeated segment
				if ("ID".equals(key)) {
					JSONArray Array = new JSONArray();
					JSONObject id = null;
					String idSegment = detailsResult.get("ID");
					String[] required = idSegment.split("ID03I0");
					for (int i = 1; i < required.length; i++) {
						String idNo = required[i].substring(0, 1);
						requiredValue = details.getCibilResponseDetails(required[i].substring(1, required[i].length()));
						requiredValue.put("00", idNo);
						Iterator<String> it = requiredValue.keySet().iterator();
						id = new JSONObject();
						while (it.hasNext()) {
							String idKey = it.next();
							for (int j = 0; j < StaticListUtil.getIdSegmentFieldTypes().size(); j++) {
								String value = StaticListUtil.getIdSegmentFieldTypes().get(j).getValue();
								String label = StaticListUtil.getIdSegmentFieldTypes().get(j).getLabel();
								if (idKey.equals(value)
										&& !StringUtils.trimToEmpty(requiredValue.get(value)).equals("")) {
									id.put(label, requiredValue.get(value));
								}
							}
						}
						Array.add(id);
						id = null;
					}

					jsonObject.put("ID", Array);

				}
				// Telephone Segment  repeated segment
				if ("PT".equals(key)) {
					JSONArray Array = new JSONArray();
					JSONObject pt = null;

					String ptSegment = detailsResult.get("PT");
					String[] required = ptSegment.split("PT03T0");
					for (int i = 1; i < required.length; i++) {
						String telePhone = required[i].substring(0, 1);
						requiredValue = details.getCibilResponseDetails(required[i].substring(1, required[i].length()));
						requiredValue.put("00", telePhone);
						Iterator<String> it = requiredValue.keySet().iterator();
						pt = new JSONObject();
						while (it.hasNext()) {
							String ptKey = it.next();
							for (int j = 0; j < StaticListUtil.getTelePhoneSegmentFieldTypes().size(); j++) {
								String value = StaticListUtil.getTelePhoneSegmentFieldTypes().get(j).getValue();
								String label = StaticListUtil.getTelePhoneSegmentFieldTypes().get(j).getLabel();

								if (ptKey.equals(value)
										&& !StringUtils.trimToEmpty(requiredValue.get(value)).equals("")) {
									pt.put(label, requiredValue.get(value));
								}
							}
						}
						Array.add(pt);
						pt = null;
					}
					jsonObject.put("TelephoneSegment", Array);
				}

				// Email Contact Segment  repeated segment
				if ("EC".equals(key)) {
					JSONArray Array = new JSONArray();
					JSONObject ec = null;
					String ecSegment = detailsResult.get("EC");
					String[] required = ecSegment.split("EC03C0");
					for (int i = 1; i < required.length; i++) {
						String email = required[i].substring(0, 1);
						requiredValue = details.getCibilResponseDetails(required[i].substring(1, required[i].length()));
						requiredValue.put("00", email);
						Iterator<String> it = requiredValue.keySet().iterator();
						ec = new JSONObject();
						while (it.hasNext()) {
							String ecKey = it.next();
							if (ecKey.equals("00") && !StringUtils.trimToEmpty(requiredValue.get("00")).equals("")) {
								ec.put("E-Mail", requiredValue.get("00"));
							}
							if (ecKey.equals("01") && !StringUtils.trimToEmpty(requiredValue.get("01")).equals("")) {
								ec.put("E-MailID", requiredValue.get("01"));
							}
						}
						Array.add(ec);
						ec = null;
					}
					jsonObject.put("EmailContactSegment", Array);
				}
				// Employment Segment Non repeated segment
				if ("EM".equals(key)) {
					String pnSegment = detailsResult.get("EM");
					String[] required = pnSegment.split("EM03E01");
					requiredValue = details.getCibilResponseDetails(required[1]);

					Iterator<String> it = requiredValue.keySet().iterator();
					while (it.hasNext()) {
						String PnKey = it.next();

						for (int i = 0; i < StaticListUtil.getEmpSegmentFieldTypes().size(); i++) {
							String value = StaticListUtil.getEmpSegmentFieldTypes().get(i).getValue();
							String label = StaticListUtil.getEmpSegmentFieldTypes().get(i).getLabel();
							if (PnKey.equals(value) && !StringUtils.trimToEmpty(requiredValue.get(value)).equals("")) {
								jsonObject.put(label, requiredValue.get(value));
							}
						}
					}

				}
				// Enquiry Account Number Segment  repeated segment
				if ("PI".equals(key)) {
					JSONArray Array = new JSONArray();
					JSONObject pi = null;

					String piSegment = detailsResult.get("PI");
					String[] required = piSegment.split("PI03I0");
					for (int i = 1; i < required.length; i++) {
						String accNo = required[i].substring(0, 1);
						requiredValue = details.getCibilResponseDetails(required[i].substring(1, required[i].length()));
						requiredValue.put("00", accNo);
						Iterator<String> it = requiredValue.keySet().iterator();
						pi = new JSONObject();
						while (it.hasNext()) {
							String piKey = it.next();
							if (piKey.equals("00") && !StringUtils.trimToEmpty(requiredValue.get("00")).equals("")) {
								pi.put("Accno", requiredValue.get("00"));
							}
							if (piKey.equals("01") && !StringUtils.trimToEmpty(requiredValue.get("01")).equals("")) {
								pi.put("EnqAccountNumber", requiredValue.get("01"));
							}
						}
						Array.add(pi);
						pi = null;
					}

					jsonObject.put("EnquiryAccountNumberSegment", Array);

				}
				// Score Segment Non repeated segment
				if ("SC".equals(key)) {
					String scSegment = detailsResult.get("SC");
					String[] req = scSegment.split("SC10");
					if (req[1].substring(0, 10).equals("CIBILTUSCR")) {
						jsonObject.put("ScoreName", "CIBILTUSCR");
					}
					String[] reqScSegment = scSegment.split("SC10CIBILTUSCR");
					requiredValue = details.getCibilResponseDetails(reqScSegment[1]);
					Iterator<String> it = requiredValue.keySet().iterator();
					while (it.hasNext()) {
						String ScKey = it.next();
						for (int i = 0; i < StaticListUtil.getScoreSegmentFieldTypes().size(); i++) {
							String value = StaticListUtil.getScoreSegmentFieldTypes().get(i).getValue();
							String label = StaticListUtil.getScoreSegmentFieldTypes().get(i).getLabel();
							if (ScKey.equals(value) && !StringUtils.trimToEmpty(requiredValue.get(value)).equals("")) {
								jsonObject.put(label, requiredValue.get(value));
							}
						}
					}
				}

				// Address Segment Repeated Segment
				if ("PA".equals(key)) {
					JSONArray Array = new JSONArray();
					JSONObject pa = null;

					String paSegment = detailsResult.get("PA");
					String[] required = paSegment.split("PA03A0");
					for (int i = 1; i < required.length; i++) {
						String address = required[i].substring(0, 1);
						requiredValue = details.getCibilResponseDetails(required[i].substring(1, required[i].length()));
						requiredValue.put("00", address);
						Iterator<String> it = requiredValue.keySet().iterator();
						pa = new JSONObject();
						while (it.hasNext()) {
							String paKey = it.next();

							for (int j = 0; j < StaticListUtil.getAddressSegmentFieldTypes().size(); j++) {

								String value = StaticListUtil.getAddressSegmentFieldTypes().get(j).getValue();
								String label = StaticListUtil.getAddressSegmentFieldTypes().get(j).getLabel();

								if (paKey.equals(value)
										&& !StringUtils.trimToEmpty(requiredValue.get(value)).equals("")) {
									pa.put(label, requiredValue.get(value));
								}
							}
						}
						Array.add(pa);
						pa = null;
					}
					jsonObject.put("AddressSegment", Array);
				}

				// Account Segment repeated Segment
				if ("TL".equals(key)) {
					JSONArray Array = new JSONArray();
					JSONObject tl = null;
					String tlSegment = detailsResult.get("TL");
					String[] required = tlSegment.split("TL04T");
					for (int i = 1; i < required.length; i++) {
						String AccSno = required[i].substring(0, 3);
						requiredValue = details.getCibilResponseDetails(required[i].substring(3, required[i].length()));
						requiredValue.put("00", AccSno);
						Iterator<String> it = requiredValue.keySet().iterator();
						tl = new JSONObject();
						while (it.hasNext()) {
							String tlKey = it.next();

							for (int j = 0; j < StaticListUtil.getAccountSegmentFieldTypes().size(); j++) {

								String value = StaticListUtil.getAccountSegmentFieldTypes().get(j).getValue();
								String label = StaticListUtil.getAccountSegmentFieldTypes().get(j).getLabel();

								if (tlKey.equals(value)
										&& !StringUtils.trimToEmpty(requiredValue.get(value)).equals("")) {
									tl.put(label, requiredValue.get(value));
								}
							}

						}
						Array.add(tl);
						tl = null;
					}
					jsonObject.put("AccountSegment", Array);
				}
				// Enquiry Segment repeated segment
				if ("IQ".equals(key)) {
					JSONArray Array = new JSONArray();
					JSONObject iq = null;

					String iqSegment = detailsResult.get("IQ");
					String[] required = iqSegment.split("IQ04I");
					for (int i = 1; i < required.length; i++) {
						String EnqSno = required[i].substring(0, 3);
						requiredValue = details.getCibilResponseDetails(required[i].substring(3, required[i].length()));
						requiredValue.put("00", EnqSno);
						Iterator<String> it = requiredValue.keySet().iterator();
						iq = new JSONObject();
						while (it.hasNext()) {
							String iqKey = it.next();
							for (int j = 0; j < StaticListUtil.getEnqSegmentFieldTypes().size(); j++) {

								String value = StaticListUtil.getEnqSegmentFieldTypes().get(j).getValue();
								String label = StaticListUtil.getEnqSegmentFieldTypes().get(j).getLabel();

								if (iqKey.equals(value)
										&& !StringUtils.trimToEmpty(requiredValue.get(value)).equals("")) {
									iq.put(label, requiredValue.get(value));
								}
							}
						}
						Array.add(iq);
						iq = null;
					}
					jsonObject.put("EnquirySegment", Array);
				}
				// Consumer Dispute Remarks Segment(DR) non repeated segment
				if ("DR".equals(key)) {

					String drSegment = detailsResult.get("DR");
					String[] required = drSegment.split("DR03D01");
					requiredValue = details.getCibilResponseDetails(required[1]);

					Iterator<String> it = requiredValue.keySet().iterator();
					while (it.hasNext()) {
						String drKey = it.next();

						for (int i = 0; i < StaticListUtil.getDrSegmentFieldTypes().size(); i++) {
							String value = StaticListUtil.getDrSegmentFieldTypes().get(i).getValue();
							String label = StaticListUtil.getDrSegmentFieldTypes().get(i).getLabel();

							if (drKey.equals(value) && !StringUtils.trimToEmpty(requiredValue.get(value)).equals("")) {
								jsonObject.put(label, requiredValue.get(value));
							}
						}
					}

				}
				// Error Segment-Non repeated segment
				if ("UR".equals(key)) {

					String drSegment = detailsResult.get("UR");
					String[] required = drSegment.split("UR03U01");
					requiredValue = details.getCibilResponseDetails(required[1]);

					Iterator<String> it = requiredValue.keySet().iterator();
					while (it.hasNext()) {
						String drKey = it.next();

						for (int i = 0; i < StaticListUtil.getUrSegmentFieldTypes().size(); i++) {
							String value = StaticListUtil.getUrSegmentFieldTypes().get(i).getValue();
							String label = StaticListUtil.getUrSegmentFieldTypes().get(i).getLabel();

							if (drKey.equals(value) && !StringUtils.trimToEmpty(requiredValue.get(value)).equals("")) {
								jsonObject.put(label, requiredValue.get(value));
							}
						}
					}

				}
				// End Segment non repeated segment
				if ("ES".equals(key)) {
					String drSegment = detailsResult.get("ES");
					String[] required = drSegment.split("ES07");
					jsonObject.put("LengthofTransmission", required[1].substring(0, 7));
					jsonObject.put("EndCharacters", required[1].substring(7, 11) + "**");

				}
			}
			logger.debug(Literal.LEAVING);
		} catch (Exception e) {
			logger.debug("Error parsing response in  " + key + " segment");
			throw e;

		}
		return jsonObject;
	}
	
	/**
	 * Method for prepare the Extended Field details map according to the given response.
	 * 
	 * @param extendedResMapObject
	 * @param financeDetail
	 */
	protected void prepareResponseObj(Map<String, Object> validatedMap, CustomerDetails customerDetail) {
		logger.debug(Literal.ENTERING);
		if (validatedMap != null) {
			Map<String, Object> extendedMapObject = customerDetail.getExtendedFieldRender().getMapValues();
			if (extendedMapObject == null) {
				extendedMapObject = new HashMap<String, Object>();
			}
			for (Entry<String, Object> entry : validatedMap.entrySet()) {
				extendedMapObject.put(entry.getKey(), entry.getValue());
			}
			customerDetail.getExtendedFieldRender().setMapValues(extendedMapObject);
		}
		logger.debug(Literal.LEAVING);
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

			configurationList = interfaceLoggingDAO.getExtendedFieldDetailsByFieldName(fieldNames);
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

					case InterfaceConstants.FIELDTYPE_TEXT:
					case InterfaceConstants.FIELDTYPE_MULTILINETEXT:
					case InterfaceConstants.FIELDTYPE_LISTFIELD:
						if (jsonRespValue.length() > configuration.getFieldLength()) {
							validatedMap.put(key, jsonRespValue.substring(0, configuration.getFieldLength()));
						} else {
							validatedMap.put(key, jsonRespValue);
						}
						break;
					case InterfaceConstants.FIELDTYPE_UPPERTEXT:
						if (jsonRespValue.length() > configuration.getFieldLength()) {
							String value = jsonRespValue.substring(0, configuration.getFieldLength());
							validatedMap.put(key, value.toUpperCase());
						} else {
							validatedMap.put(key, jsonRespValue.toUpperCase());
						}
						break;

					case InterfaceConstants.FIELDTYPE_ADDRESS:
						if (jsonRespValue.length() > 100) {
							validatedMap.put(key, jsonRespValue.substring(0, 100));
						} else {
							validatedMap.put(key, jsonRespValue);
						}
						break;

					case InterfaceConstants.FIELDTYPE_DATE:
					case InterfaceConstants.FIELDTYPE_TIME:
						Date dateValue = null;
						try {
							DateFormat formatter = new SimpleDateFormat(InterfaceConstants.InterfaceDateFormatter);
							dateValue = formatter.parse(jsonRespValue);
						} catch (Exception e) {
							logger.error("Exception : ", e);
						}
						validatedMap.put(key, dateValue);
						break;

					case InterfaceConstants.FIELDTYPE_DATETIME:
						Date dateTimeVal = null;
						try {
							DateFormat formatter = new SimpleDateFormat(InterfaceConstants.InterfaceDateFormatter);
							dateTimeVal = formatter.parse(jsonRespValue);
						} catch (Exception e) {
							logger.error("Exception : ", e);
						}
						validatedMap.put(key, dateTimeVal);
						break;

					case InterfaceConstants.FIELDTYPE_BOOLEAN:
						Boolean booleanValue = false;
						if (StringUtils.equals(jsonRespValue, "true") || StringUtils.equals(jsonRespValue, "false")) {
							booleanValue = jsonRespValue.equals("true") ? true : false;
						} else if (StringUtils.equals(jsonRespValue, "1") || StringUtils.equals(jsonRespValue, "0")) {
							booleanValue = jsonRespValue.equals("1") ? true : false;
						} else {
							logger.error(InterfaceConstants.wrongValueMSG + configuration.getFieldLabel());
						}
						validatedMap.put(key, booleanValue);
						break;

					case InterfaceConstants.FIELDTYPE_AMOUNT:
						BigDecimal decimalValue = BigDecimal.ZERO;
						try {
							double rateValue = Double.parseDouble(jsonRespValue);
							decimalValue = BigDecimal.valueOf(rateValue);
						} catch (Exception e) {
							logger.error("Exception : ", e);
						}
						if (jsonRespValue.length() > configuration.getFieldLength() + 2) {
							logger.error(InterfaceConstants.wrongLengthMSG + configuration.getFieldLabel());
						}
						decimalValue = decimalValue.setScale(configuration.getFieldPrec(), RoundingMode.HALF_DOWN);
						validatedMap.put(key, decimalValue);
						break;

					case InterfaceConstants.FIELDTYPE_INT:
						int intValue = 0;
						if (jsonRespValue.length() > configuration.getFieldLength()) {
							logger.error(InterfaceConstants.wrongLengthMSG + configuration.getFieldLabel());
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
								logger.error(InterfaceConstants.wrongLengthMSG + configuration.getFieldLabel());
							}
						}
						validatedMap.put(key, intValue);
						break;

					case InterfaceConstants.FIELDTYPE_LONG:
						long longValue = 0;
						if (jsonRespValue.length() > configuration.getFieldLength()) {
							logger.error(InterfaceConstants.wrongLengthMSG + configuration.getFieldLabel());
						}
						try {
							longValue = Long.parseLong(jsonRespValue);
						} catch (Exception e) {
							logger.error("Exception : ", e);
						}
						if (configuration.getFieldMaxValue() > 0 || configuration.getFieldMinValue() > 0) {
							if (!(longValue >= configuration.getFieldMinValue()
									&& longValue <= configuration.getFieldMaxValue())) {
								logger.error(InterfaceConstants.wrongLengthMSG + configuration.getFieldLabel());
							}
						}
						validatedMap.put(key, longValue);
						break;

					case InterfaceConstants.FIELDTYPE_RADIO:
						int radioValue = 0;
						if (StringUtils.equals(InterfaceConstants.FIELDTYPE_RADIO, configuration.getFieldType())) {
							try {
								radioValue = Integer.parseInt(jsonRespValue);
							} catch (Exception e) {
								logger.error("Exception : ", e);
							}
							if (radioValue > configuration.getFieldLength()) {
								logger.error(InterfaceConstants.wrongLengthMSG + configuration.getFieldLabel());
							} else {
								validatedMap.put(key, radioValue);
							}
						}
						break;

					case InterfaceConstants.FIELDTYPE_STATICCOMBO:
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
								logger.error(InterfaceConstants.wrongValueMSG + configuration.getFieldLabel());
							}
						} else {
							logger.error(InterfaceConstants.wrongValueMSG + configuration.getFieldLabel());
						}
						break;

					case InterfaceConstants.FIELDTYPE_PHONE:
						if (jsonRespValue.length() > 10) {
							validatedMap.put(key, jsonRespValue.substring(0, 10));
							break;
						} else {
							validatedMap.put(key, jsonRespValue);
						}
						break;
					case InterfaceConstants.FIELDTYPE_DECIMAL:
					case InterfaceConstants.FIELDTYPE_ACTRATE:
						Double decValue = getDecimalValue(configuration, jsonRespValue);
						validatedMap.put(key, decValue);
						break;

					case InterfaceConstants.FIELDTYPE_PERCENTAGE:
						double percentage = 0;
						if (jsonRespValue.length() > (configuration.getFieldLength() - configuration.getFieldPrec())) {
							logger.error(InterfaceConstants.wrongLengthMSG + configuration.getFieldLabel());
						}
						try {
							percentage = Double.valueOf(jsonRespValue);
						} catch (Exception e) {
							logger.error("Exception : ", e);
						}
						if (percentage < 0 || percentage > 100) {
							logger.error(InterfaceConstants.wrongLengthMSG + configuration.getFieldLabel());
						}
						if (configuration.getFieldMaxValue() > 0 || configuration.getFieldMinValue() > 0) {
							if (percentage > configuration.getFieldMaxValue()
									|| percentage < configuration.getFieldMinValue()) {
								logger.error(InterfaceConstants.wrongLengthMSG + configuration.getFieldLabel());
							}
						}
						validatedMap.put(key, percentage);
						break;

					case InterfaceConstants.FIELDTYPE_MULTISTATICCOMBO:
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
									logger.error(InterfaceConstants.wrongLengthMSG + configuration.getFieldLabel());

								} else {
									validatedMap.put(key, jsonRespValue);
								}
							}
						}
						break;

					case InterfaceConstants.FIELDTYPE_FRQ:
						if (jsonRespValue.length() <= LENGTH_FREQUENCY) {
							validatedMap.put(key, jsonRespValue);
						} else {
							logger.error(InterfaceConstants.wrongLengthMSG + configuration.getFieldLabel());
						}

						break;

					case InterfaceConstants.FIELDTYPE_ACCOUNT:
						if (jsonRespValue.length() <= LENGTH_ACCOUNT) {
							validatedMap.put(key, jsonRespValue);
						} else {
							logger.error(InterfaceConstants.wrongLengthMSG + configuration.getFieldLabel());
						}
						break;

					case InterfaceConstants.FIELDTYPE_MULTIEXTENDEDCOMBO:
					case InterfaceConstants.FIELDTYPE_EXTENDEDCOMBO:
					case InterfaceConstants.FIELDTYPE_BASERATE:
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
		logger.debug(Literal.LEAVING);
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
			logger.error(InterfaceConstants.wrongLengthMSG + configuration.getFieldLabel());
		}

		if (aftrPrcsnValue != null && aftrPrcsnValue.length() > configuration.getFieldPrec()) {
			logger.error(InterfaceConstants.wrongLengthMSG + configuration.getFieldLabel());
		}
		try {
			decValue = Double.parseDouble(jsonRespValue);
		} catch (Exception e) {
			logger.error("Exception : ", e);
		}

		if (configuration.getFieldMaxValue() > 0 || configuration.getFieldMinValue() > 0) {
			if (Math.round(decValue) > configuration.getFieldMaxValue()
					|| Math.round(decValue) < configuration.getFieldMinValue()) {
				logger.error(InterfaceConstants.wrongLengthMSG + configuration.getFieldLabel());
			}
		}
		BigDecimal amount = new BigDecimal(decValue);
		amount = amount.setScale(configuration.getFieldPrec(), RoundingMode.HALF_DOWN);

		logger.debug(Literal.LEAVING);
		return amount.doubleValue();
	}
	
	
	protected void logInterfaceDetails(InterfaceLogDetail interfaceLogDetail) {
		try {
			interfaceLoggingDAO.save(interfaceLogDetail);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}
	

	/*
	 * private void prepareAccountNumSegment(StringBuilder builder, FinanceDetail finDetail) { FinanceMain financeMain =
	 * finDetail.getFinScheduleData().getFinanceMain(); writeValue(builder, "06", financeMain.getFinReference(), "02");
	 * }
	 */

	private void jsonResopnseHeader(String responseHeader) {
		logger.debug(Literal.ENTERING);
		String[] values = StaticListUtil.getHeaderIndexes();
		for (int i = 0; i < values.length; i++) {
			String index = values[i];
			String[] value = index.split(",");
			jsonObject.put(StaticListUtil.getResponseHeaders().get(i),
					responseHeader.substring(Integer.parseInt(value[0]), Integer.parseInt(value[1])));
		}
		logger.debug("Parsed header respone :" + jsonObject.toString());
		logger.debug(Literal.LEAVING);

	}

	private void parseErrorHeder(String errorHeader) {
		logger.debug(Literal.ENTERING);
		String[] values = StaticListUtil.getErrorHeaderIndexes();

		for (int i = 0; i < values.length; i++) {
			String index = values[i];
			String[] value = index.split(",");
			jsonObject.put(StaticListUtil.getErrorResponseHeader().get(i),
					errorHeader.substring(Integer.parseInt(value[0]), Integer.parseInt(value[1])));
		}
		logger.debug("Error Response");
		logger.debug("Parsed Error header respone------------->" + jsonObject.toString());

		logger.debug(Literal.ENTERING);
	}

}
