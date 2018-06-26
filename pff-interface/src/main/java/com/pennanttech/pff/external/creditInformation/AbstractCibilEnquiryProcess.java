package com.pennanttech.pff.external.creditInformation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennanttech.logging.model.InterfaceLogDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.InterfaceConstants;
import com.pennanttech.pff.core.util.DateUtil;
import com.pennanttech.pff.dao.CreditInterfaceDAO;
import com.pennanttech.pff.external.AbstractInterface;
import com.pennanttech.pff.external.CreditInformation;
import com.pennanttech.pff.external.util.StaticListUtil;
import com.pennanttech.pff.logging.dao.InterfaceLoggingDAO;

public class AbstractCibilEnquiryProcess extends AbstractInterface implements CreditInformation {
	private static final Logger logger = Logger.getLogger(AbstractCibilEnquiryProcess.class);

	JSONObject jsonObject;
	@Autowired(required = false)
	CibilResponseDetails responseDetails;

	private String CBIL_REPORT_MEMBER_CODE;
	private String CBIL_REPORT_MEMBER_ID;
	private String CBIL_REPORT_MEMBER_PASSWORD;
	private String CBIL_ENQUIRY_SCORE_TYPE;
	private Map<String, String> parameters = new HashMap<>();
	private Map<String, String> cibilStateCodes = new HashMap<>();
	private Map<String, String> cibilStateCodesForRequest = new HashMap<>();
	private Map<String, String> cibilIdTypes = new HashMap<>();
	private Map<String, String> cibilIdTypesForRequest = new HashMap<>();
	private Map<String, String> cibilPhoneTypes = new HashMap<>();
	private Map<String, String> cibilloanTypes = new HashMap<>();
	private Map<String, String> cibilOccupationTypes = StaticListUtil.getCibilOccupationCode();
	private Map<String, String> cibilIncomeIndicator = StaticListUtil.getCibilIncomeIndicator();
	private Map<String, String> cibilMonthlyAnnualIncomeIndicator = StaticListUtil
			.getCibilMonthlyAnnualIncomeIndicator();
	private Map<String, String> cibilAddrCategory = StaticListUtil.getCibilAddrCategory();
	private Map<String, String> cibilResidenceCode = StaticListUtil.getCibilResidenceCode();
	private Map<String, String> cibilOwnershipTypes = StaticListUtil.getCibilOnwerShipTypes();
	private Map<String, String> cibilPaymentFreqTypes = StaticListUtil.getCibilPaymentFreqTypes();
	private final String responseSize = "1";
	@Autowired(required = false)
	private InterfaceLoggingDAO interfaceLoggingDAO;
	private BigDecimal cibilDefaultLoanAmt = BigDecimal.valueOf(999999999);

	@Autowired(required = false)
	private CreditInterfaceDAO creditInterfaceDao;
	private final String extConfigFileName = "RetailCibilConsumer.properties";

	/**
	 * Method to get the CIBIL details of the Customer and set these details to
	 * ExtendedFieldDetails.
	 * 
	 * @param auditHeader
	 * @return auditHeader
	 * @throws Exception
	 */
	@Override
	public AuditHeader getCreditEnquiryDetails(AuditHeader auditHeader,  boolean isFromCustomer) throws Exception {
		logger.debug(Literal.ENTERING);

		String cibilReq = (String) getSMTParameter("CBIL_PROCESS_REQ", String.class);

		if ("N".equals(cibilReq)) {
			return auditHeader;
		}

		Customer customer = null;
		FinanceDetail financeDetail = null;
		CustomerDetails customerDetails = null;

		if (isFromCustomer) {
			customerDetails = (CustomerDetails) auditHeader.getAuditDetail().getModelData();
			customer = customerDetails.getCustomer();
		} else {
			financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
			customerDetails = financeDetail.getCustomerDetails();
			customer = customerDetails.getCustomer();
		}

		loadParameters();
		loadAccountTypes();
		loadCibilStateCodes();
		loadCibilStateCodesforRequest();
		loadCibilIdTypes();
		loadCibilIdTypesforRequest();
		loadCibilPhoneTypes();
		loadCibilLoanTypes();

		if (StringUtils.equals("RETAIL", customer.getCustCtgCode())) {
			// Check score for primary applicant.
			processRetailCustomer(financeDetail, customerDetails, false);
		}

		// Check score for co-applicants.
		getCoAppCustomer(financeDetail);

		return auditHeader;
	}

	/**
	 * Method to get the CIBIL details of the Customer and set these details to
	 * ExtendedFieldDetails.
	 * 
	 * @param auditHeader
	 * @return auditHeader
	 * @throws Exception
	 */
	@Override
	public CustomerDetails procesCreditEnquiry(CustomerDetails customerDetails,  FinanceMain financeMain, boolean override)
			throws Exception {
		logger.debug(Literal.ENTERING);

		try {

			String cibilReq = (String) getSMTParameter("CBIL_PROCESS_REQ", String.class);

			if ("N".equals(cibilReq)) {
				customerDetails.setCibilExecuted(false);
				return customerDetails;
			}

			Customer customer = null;
			customer = customerDetails.getCustomer();
			loadParameters();
			loadAccountTypes();
			loadCibilStateCodes();
			loadCibilStateCodesforRequest();
			loadCibilIdTypes();
			loadCibilIdTypesforRequest();
			loadCibilPhoneTypes();
			loadCibilLoanTypes();

			if (StringUtils.equals("RETAIL", customer.getCustCtgCode())) {

				Timestamp reqSentOn = new Timestamp(System.currentTimeMillis());
				Map<String, Object> appplicationdata = new HashMap<>();
				String builder = prepareRequest(financeMain, customerDetails, customer.getCustCIF(), reqSentOn,
						appplicationdata);

				if (builder == null) {
					customerDetails.setCibilExecuted(false);
					return customerDetails;
				}

				StringBuilder tableName = new StringBuilder();
				tableName.append(InterfaceConstants.MODULE_CUSTOMER);
				tableName.append("_");
				tableName.append(customer.getCustCtgCode());
				tableName.append("_ED");

				String prvString = "";
				Map<String, Object> mapoldData = creditInterfaceDao.getExtendedField(customer.getCustCIF(),
						tableName.toString());
				if (mapoldData != null && mapoldData.containsKey("cibilRequest")) {
					prvString = mapoldData.get("cibilRequest").toString();
				}

				if (StringUtils.equals(prvString, builder) && !override) {
					// return warning
					customerDetails.setCibilALreadyRun(true);
					return customerDetails;
				}

				// Check score for primary applicant.
				Map<String, Object> mapdata = excuteCibil(customer.getCustCIF(), reqSentOn, appplicationdata, builder);

				if (mapdata != null && !mapdata.isEmpty()) {
					Map<String, Object> mapvalidData = validateExtendedMapValues(mapdata);
					appplicationdata.put("cibilRequest", builder);
					appplicationdata.put("JsonResponse", jsonObject.toString());
					appplicationdata.putAll(mapvalidData);

				}

				appplicationdata.put("Reference", customer.getCustCIF());
				prepareResponseObj(appplicationdata, customerDetails);

				Map<String, Object> extFieldMap = creditInterfaceDao.getExtendedField(customer.getCustCIF(),
						tableName.toString());
				if (extFieldMap == null) {
					creditInterfaceDao.saveExtendedDetails(appplicationdata, "", tableName.toString());
				} else {
					int seqNo = (int) extFieldMap.get("SeqNo");
					creditInterfaceDao.updateExtendedDetails(customer.getCustCIF(), seqNo, appplicationdata, "",
							tableName.toString());
				}

			}
		} catch (Exception e) {
			logger.debug(e);
			customerDetails.setCibilExecuted(false);
		}

		// save it to database

		return customerDetails;
	}

	/**
	 * Method for process the CIBIL details of Applicant.
	 * 
	 * @param financeDetail
	 * @param customerDetails
	 */
	private void processRetailCustomer(FinanceDetail financeDetail, CustomerDetails customerDetails, boolean coApp) {

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String reference = customerDetails.getCustomer().getCustCIF();
		Timestamp reqSentOn = new Timestamp(System.currentTimeMillis());
		Map<String, Object> appplicationdata = new HashMap<>();

		String builder = prepareRequest(financeMain, customerDetails, reference, reqSentOn, appplicationdata);

		if (builder == null) {
			return;
		}

		String prvString = "";// TODO;
		if (customerDetails.getExtendedFieldRender().getMapValues().get("cibilRequest") != null) {
			prvString = (String) customerDetails.getExtendedFieldRender().getMapValues().get("cibilRequest");
		}

		if (StringUtils.equals(prvString, builder)) {
			return;
		}

		Map<String, Object> mapdata = excuteCibil(reference, reqSentOn, appplicationdata, builder);

		if (mapdata != null && !mapdata.isEmpty()) {
			Map<String, Object> mapvalidData = validateExtendedMapValues(mapdata);
			appplicationdata.put("JsonResponse", jsonObject.toString());
			appplicationdata.put("cibilRequest", builder);
			appplicationdata.putAll(mapvalidData);

		}

		if (coApp) {
			prepareResponseObjforCoapp(appplicationdata, customerDetails, financeDetail);
		} else {
			prepareResponseObj(appplicationdata, customerDetails);
		}
	}

	private Map<String, Object> excuteCibil(String reference, Timestamp reqSentOn, Map<String, Object> appplicationdata,
			String builder) {
		String response = null;
		Map<String, Object> mapdata = null;
		try {
			response = sendRequest(builder.toString());
			jsonObject = new JSONObject();
			 responseDetails = new CibilResponseDetails();
			if (response.startsWith("ERRR")) {
				parseErrorResponse(response);
				String error = getActualError(response);
				doInterfaceLogging(reference, builder.toString(), response, "999", error, reqSentOn,
						InterfaceConstants.STATUS_FAILED);
				appplicationdata.put(InterfaceConstants.RSN_CODE, error);
				logger.debug("Error Respone: " + jsonObject.toString());

			} else {
				parseHeaderResponse(response);
				HashMap<String, String> detailsResult = responseDetails.getCibilResponseArray(response);
				jsonObject = parseDetailsresponse(detailsResult);
				logger.debug("JSON Respone : " + jsonObject.toString());
				mapdata = getPropValueFromResp(jsonObject.toString(), extConfigFileName);
				appplicationdata.put(InterfaceConstants.RSN_CODE, "");
				doInterfaceLogging(reference, builder.toString(), response, null, null, reqSentOn,
						InterfaceConstants.STATUS_SUCCESS);
				logger.debug("Success Respone :" + jsonObject.toString());
			}

		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);

			String error = e.getMessage();
			if (error == null) {
				error = e.getStackTrace().toString();
			}
			if (error.length() > 200) {
				error.substring(0, 200);
			}

			appplicationdata.put(InterfaceConstants.RSN_CODE, error);
			doInterfaceLogging(reference, builder.toString(), null, null, error, reqSentOn,
					InterfaceConstants.STATUS_FAILED);
		}
		return mapdata;
	}

	/**
	 * Method for process the CIBIL details of Applicant.
	 * 
	 * @param financeDetail
	 */
	private void getCoAppCustomer(FinanceDetail financeDetail) {

		List<JointAccountDetail> coapplicants = financeDetail.getJountAccountDetailList();
		if (coapplicants == null || coapplicants.isEmpty()) {
			return;
		}

		List<Long> coApplicantIDs = new ArrayList<Long>(1);
		for (JointAccountDetail coApplicant : coapplicants) {
			coApplicantIDs.add(coApplicant.getCustID());
		}
		List<CustomerDetails> coApplicantCustomers = getCoApplicants(coApplicantIDs);

		for (CustomerDetails customerDetails : coApplicantCustomers) {
			if (StringUtils.equals("RETAIL", customerDetails.getCustomer().getCustCtgCode())) {
				processRetailCustomer(financeDetail, customerDetails, true);
			}
		}

		logger.debug(Literal.LEAVING);
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
		List<Customer> coAppCustomers = creditInterfaceDao.getCustomerByID(customerIds, "_AVIEW");

		String module = InterfaceConstants.MODULE_CUSTOMER;

		for (Customer customer : coAppCustomers) {
			CustomerDetails customerDetails = new CustomerDetails();
			customerDetails.setCustID(customer.getCustID());
			customerDetails.setCustCIF(customer.getCustCIF());
			extendedFieldHeader = creditInterfaceDao.getExtendedFieldHeaderByModuleName(module,
					customer.getCustCtgCode());
			if (extendedFieldHeader != null) {
				ExtendedFieldRender extendedFieldRender = new ExtendedFieldRender();
				extendedFieldRender.setReference(customer.getCustCIF());

				tableName.append(module);
				tableName.append("_");
				tableName.append(customer.getCustCtgCode());
				tableName.append("_ED");
				extMapValues = creditInterfaceDao.getExtendedField(customer.getCustCIF(), tableName.toString());
				extendedFieldRender.setSeqNo(Integer.valueOf(extMapValues.get("SeqNo").toString()));

				extendedFieldRender.setMapValues(extMapValues);
				customerDetails.setExtendedFieldHeader(extendedFieldHeader);
				customerDetails.setExtendedFieldRender(extendedFieldRender);
				tableName.setLength(0);
				customerDetailList.add(customerDetails);
			}
		}
		logger.debug(Literal.LEAVING);
		return customerDetailList;
	}

	protected List<CustomerDetails> getCoApplicants(List<Long> coApplicantIDs) {
		return creditInterfaceDao.getCoApplicants(coApplicantIDs, "_VIEW");
	}

	private String prepareRequest(FinanceMain financeMain, CustomerDetails customerDetails, String reference,
			Timestamp reqSentOn, Map<String, Object> appplicationdata) {
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
			logger.debug(Literal.EXCEPTION, e);

			String error = e.getMessage();
			if (error == null) {
				error = e.getStackTrace().toString();
			}
			if (error.length() > 200) {
				error.substring(0, 200);
			}
			appplicationdata.put(InterfaceConstants.RSN_CODE, e.getMessage());
			doInterfaceLogging(reference, builder.toString(), null, null, error, reqSentOn,
					InterfaceConstants.STATUS_FAILED);

		}
		return builder.toString();
	}

	/**
	 * Method for load the properties file, then iterate the keys of that file
	 * and map to jsonResponse.
	 * 
	 * @param jsonResponse
	 * @param extConfigFileName
	 * @return extendedMappedValues
	 */
	private Map<String, Object> getPropValueFromResp(String jsonResponse, String extConfigFileName)
			throws FileNotFoundException {
		logger.debug(Literal.ENTERING);

		Map<String, Object> extendedFieldMap = new HashMap<>(1);
		Properties properties = new Properties();
		InputStream inputStream = this.getClass().getResourceAsStream("/properties/RetailCibilConsumer.properties");

		try {
			properties.load(inputStream);
		} catch (IOException ioException) {
			logger.debug(ioException.getMessage());
		}
		Enumeration<?> e = properties.propertyNames();
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

	private void loadCibilStateCodes() {
		logger.info("Loading Cibil StateCodes..");
		MapSqlParameterSource paramMap;

		StringBuilder sql = new StringBuilder();
		paramMap = new MapSqlParameterSource();

		sql.append("SELECT CODE, DESCRIPTION FROM CIBIL_STATES_MAPPING");

		namedJdbcTemplate.query(sql.toString(), paramMap, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				cibilStateCodes.put(rs.getString("CODE"), rs.getString("DESCRIPTION"));
			}
		});
	}

	private void loadCibilStateCodesforRequest() {
		logger.info("Loading Cibil StateCodes for Request..");
		MapSqlParameterSource paramMap;

		StringBuilder sql = new StringBuilder();
		paramMap = new MapSqlParameterSource();

		sql.append("SELECT CPPROVINCE, CODE FROM CIBIL_STATES_MAPPING");

		namedJdbcTemplate.query(sql.toString(), paramMap, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				cibilStateCodesForRequest.put(rs.getString("CPPROVINCE"), rs.getString("CODE"));
			}
		});
	}

	private void loadCibilIdTypes() {
		logger.info("Loading Cibil Id Types..");
		MapSqlParameterSource paramMap;

		StringBuilder sql = new StringBuilder();
		paramMap = new MapSqlParameterSource();

		sql.append("SELECT CODE, DESCRIPTION FROM CIBIL_DOCUMENT_TYPES");

		namedJdbcTemplate.query(sql.toString(), paramMap, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				cibilIdTypes.put(rs.getString("CODE"), rs.getString("DESCRIPTION"));
			}
		});
	}

	private void loadCibilIdTypesforRequest() {
		logger.info("Loading Cibil Id Types..");
		MapSqlParameterSource paramMap;

		StringBuilder sql = new StringBuilder();
		paramMap = new MapSqlParameterSource();

		sql.append("SELECT CODE, DOCTYPECODE FROM CIBIL_DOCUMENT_TYPES_MAPPING");

		namedJdbcTemplate.query(sql.toString(), paramMap, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				cibilIdTypesForRequest.put(rs.getString("DOCTYPECODE"), rs.getString("CODE"));
			}
		});
	}

	private void loadCibilPhoneTypes() {
		logger.info("Loading Cibil Phone Types..");
		MapSqlParameterSource paramMap;

		StringBuilder sql = new StringBuilder();
		paramMap = new MapSqlParameterSource();

		sql.append("SELECT CODE, DESCRIPTION FROM CIBIL_PHONE_TYPES");

		namedJdbcTemplate.query(sql.toString(), paramMap, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				cibilPhoneTypes.put(rs.getString("CODE"), rs.getString("DESCRIPTION"));
			}
		});
	}

	private void loadCibilLoanTypes() {
		logger.info("Loading Cibil LoanTypes..");
		MapSqlParameterSource paramMap;

		StringBuilder sql = new StringBuilder();
		paramMap = new MapSqlParameterSource();

		sql.append("SELECT CODE, DESCRIPTION FROM CIBIL_ACCOUNT_TYPES");

		namedJdbcTemplate.query(sql.toString(), paramMap, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				cibilloanTypes.put(rs.getString("CODE"), rs.getString("DESCRIPTION"));
			}
		});
	}

	/**
	 * 
	 * The TUEF Enquiry Header Segment marks the beginning of the Enquiry
	 * Record, and:
	 * <li>It is a Required segment.
	 * <li>It is of a fixed size of 115 bytes.
	 * <li>It can appear only once per Enquiry Record.
	 * <li>All the fields must be provided otherwise the entire Enquiry Record
	 * is rejected</li>.
	 * <li>All fields must be valid.</li>
	 * 
	 */
	private void prepareCibilHeader(StringBuilder builder, CustomerDetails customerDetails, FinanceMain financeMain)
			throws Exception {
		logger.debug(Literal.ENTERING);

		builder.append(InterfaceConstants.Enquiry_Header_Segment);
		builder.append(InterfaceConstants.Enquiry_Header_version);
		builder.append(StringUtils.rightPad(CBIL_REPORT_MEMBER_CODE, 25, ""));
		builder.append(StringUtils.rightPad("", 2, ""));
		builder.append(StringUtils.rightPad(CBIL_REPORT_MEMBER_ID, 30, ""));
		builder.append(StringUtils.rightPad(CBIL_REPORT_MEMBER_PASSWORD, 30, ""));

		
		String enqPurpose = null;
		if (financeMain != null) {
			enqPurpose = parameters.get(financeMain.getFinType());
		}
		if (enqPurpose != null) {
			builder.append(enqPurpose);
		} else {
			builder.append("00");
		}

		int currencyEditField = (int) getSMTParameter("APP_DFT_CURR_EDIT_FIELD", Integer.class);

		BigDecimal finAmount = financeMain.getFinAssetValue();
		finAmount = formateAmount(finAmount, currencyEditField);
		if (finAmount.compareTo(cibilDefaultLoanAmt) > 0) {
			finAmount = cibilDefaultLoanAmt;
		}
		builder.append(StringUtils.leftPad(String.valueOf(finAmount), 9, "0"));
		builder.append(StringUtils.rightPad("", 03, ""));
		builder.append(CBIL_ENQUIRY_SCORE_TYPE);
		builder.append(InterfaceConstants.Output_Format);
		builder.append(responseSize);
		builder.append(InterfaceConstants.Input_Output_Media);
		builder.append(InterfaceConstants.Authentication_Method);

		logger.debug(Literal.LEAVING);

	}

	public static BigDecimal formateAmount(BigDecimal amount, int dec) {
		BigDecimal bigDecimal = BigDecimal.ZERO;

		if (amount != null) {
			bigDecimal = amount.divide(new BigDecimal(Math.pow(10, dec)));
		}
		return bigDecimal;
	}

	/**
	 * The PN Segment describes personal consumer information, and:
	 * <li>It is a Required segment.</li>
	 * <li>It is variable in length and can be of a maximum size of 174
	 * bytes.</li>
	 * <li>It occurs only once per record.</li>
	 * <li>Tag 06 is reserved for future use.</li>
	 */

	private void prepareCustomerNameSegment(StringBuilder builder, CustomerDetails customerDetails) throws Exception {
		logger.debug(Literal.ENTERING);
		writeValue(builder, InterfaceConstants.Name_Segment, "N01", "03");
		writeCustomerName(builder, customerDetails.getCustomer());
		writeValue(builder, "07",
				DateUtil.format(customerDetails.getCustomer().getCustDOB(), InterfaceConstants.cibildateFormat), "08");
		if ("M".equals(customerDetails.getCustomer().getCustGenderCode())) {
			writeValue(builder, "08", "2", "01");
		} else if ("F".equals(customerDetails.getCustomer().getCustGenderCode())) {
			writeValue(builder, "08", "1", "01");
		} else {
			writeValue(builder, "08", "3", "01");
		}

		logger.debug(Literal.LEAVING);

	}

	/**
	 * 
	 * The ID Segment contains identification information about the consumer
	 * applying for credit, and:  This is a Required segment (with ID Type of
	 * 01, 02, 03, or 04) when no valid Telephone (PT) segment is provided
	 * except when the Enquiry Purpose (Positions 94-95 of the TUEF Enquiry
	 * Header Segment) is “Account Review”, “Retro Enquiry” or “Locate Plus”.
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

			if (++i > 8) {
				break;
			}

			String code = document.getCustDocCategory();

			if (!cibilIdTypesForRequest.containsKey(code)) {
				continue;
			}

			writeValue(builder, InterfaceConstants.Identification_Segment, "I0" + i, "03");
			writeValue(builder, "01", cibilIdTypesForRequest.get(code), "02");
			writeValue(builder, "02", document.getCustDocTitle(), 30, "ID");

		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The PT Segment contains the known phone numbers of the consumer, and:
	 * <li> This is a Required segment when no valid Identification (ID)
	 * segment with either Income Tax ID Number, Passport Number, Voter ID
	 * Number or Driver’s License Number is provided except when the Enquiry
	 * Purpose</li>
	 * <li>It is variable in length and can be of a maximum size of 51
	 * bytes</li>
	 * <li>This can occur maximum of 4 times per Enquiry Record.</li>
	 */
	private void prepareTelephoneSegment(StringBuilder builder, CustomerDetails customerDetails) {
		List<CustomerPhoneNumber> phoneNumbers = customerDetails.getCustomerPhoneNumList();

		if (phoneNumbers == null || phoneNumbers.isEmpty()) {
			return;
		}

		int i = 0;
		for (CustomerPhoneNumber phoneNumber : phoneNumbers) {
			if (++i > 4) {
				break;
			}

			writeValue(builder, InterfaceConstants.Telephone_Segment,
					"T" + StringUtils.leftPad(String.valueOf(i), 2, "0"), "03");
			writeValue(builder, "01", phoneNumber.getPhoneNumber(), 20, "PT");

			if (phoneNumber.getPhoneTypeCode() != null) {
				if (phoneNumber.getPhoneTypeCode().equals("MOBILE1") || phoneNumber.getPhoneTypeCode().equals("MOBILE2")
						|| phoneNumber.getPhoneTypeCode().equals("OFFMOB")) {
					writeValue(builder, "03", "01", "02");
				} else if (phoneNumber.getPhoneTypeCode().equals("HOME")) {
					writeValue(builder, "03", "02", "02");
				} else if (phoneNumber.getPhoneTypeCode().equals("OFFICE")
						|| phoneNumber.getPhoneTypeCode().equals("OFFFAX")) {
					writeValue(builder, "03", "03", "02");
				} else {
					writeValue(builder, "03", "00", "02");
				}
			} else {
				writeValue(builder, "03", "00", "02");
			}
		}

	}

	/**
	 * The PA Segment(s) contain(s) the known address(es) of the consumer, and
	 * <li>It is a Required segment.</li>
	 * <li>It is variable in length and can be a maximum of 259 bytes.</li>
	 * <li>It occurs at least once, but no more than twice per Enquiry Record.
	 * Where possible, enter the Current Address in the first Address Segment
	 * and the Permanent Address in the second Address Segment</li>.
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
			String state = address.getCustAddrProvince();
			state = cibilStateCodesForRequest.get(state);
			writeValue(builder, "06", state, "02");
			writeValue(builder, "07", address.getCustAddrZIP(), 10, InterfaceConstants.Address_Segment);

			if (address.getCustAddrType() != null) {
				if (address.getCustAddrType().equals("PER")) {
					writeValue(builder, "08", "01", "02");
				} else if (address.getCustAddrType().equals("CURRES")) {
					writeValue(builder, "08", "02", "02");
				} else if (address.getCustAddrType().equals("HEADOFF") || address.getCustAddrType().equals("BUS")) {
					writeValue(builder, "08", "03", "02");
				} else {
					writeValue(builder, "08", "04", "02");
				}
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
		builder.append(StringUtils.leftPad(
				String.valueOf(builder.toString().length() + (InterfaceConstants.EndCharacterLength)), 5, "0"));
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
					writeValue(writer, "0".concat(String.valueOf(++field)), builder.toString(), 26,
							InterfaceConstants.Name_Segment);
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
					writeValue(writer, "0".concat(String.valueOf(++field)), builder.toString(), 40,
							InterfaceConstants.Address_Segment);
				}
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	/**
	 * Sends the Request through SocketIp
	 * 
	 * @param requestmessage
	 * @return response
	 * @throws Exception
	 */
	private String sendRequest(String requestMessage) throws Exception {
		logger.debug(Literal.ENTERING);

		StringBuilder builder = new StringBuilder();

		String socketIp = (String) getSMTParameter("CIBIL_SOCKET_IP", String.class);
		int port = (int) getSMTParameter("CIBIL_SOCKET_PORT", Integer.class);

		try {
			Socket socket = new Socket(socketIp, port);
			socket.setSoTimeout(10000);
			logger.debug("Connected to CIBIL");
			OutputStream out = socket.getOutputStream();
			InputStream in = socket.getInputStream();

			int i = 19;
			char c = (char) i;
			requestMessage = requestMessage + c;
			out.write(requestMessage.getBytes(), 0, requestMessage.getBytes().length);
			out.flush();

			// Receive the same string back from the server
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}

			socket.close();

		} catch (Exception e) {
			logger.debug(e.getMessage());
			throw e;
		} finally {

			/*
			 * if (socket != null && !socket.isClosed()) { socket.close(); }
			 */
		}
		return builder.toString();
	}

	private void parseErrorResponse(String response) throws Exception {
		logger.debug(Literal.ENTERING);
		String header = response.substring(0, 18);
		parseErrorHeder(header);
		// get the details Map as key value format where key is segment and
		// value as segment data.
		HashMap<String, String> detailsResult = responseDetails.getCibilResponseArray(response);
		parseDetailsresponse(detailsResult);

		logger.debug(Literal.LEAVING);

	}

	private String getActualError(String response) {
		String str = response.substring(54, 56);
		switch (str) {
		case "03":
			return "Invalid Version in Enquiry Header Segment";
		case "04":
			return "Invalid Field Length";
		case "05":
			return "Invalid Total Length";
		case "06":
			return "Invalid Enquiry Purpose";
		case "07":
			return "Invalid Enquiry Amount";
		case "08":
			return "Invalid Enquiry Member User ID/Password	";
		case "09":
			return "Required Enquiry Segment Missing";
		case "10":
			return "Invalid Enquiry Data";
		case "11":
			return "CIBIL System Error";
		case "12":
			return "Invalid Segment Tag";
		case "13":
			return "Invalid Segment Order";
		case "14":
			return "Invalid Field Tag Order";
		case "15":
			return "Missing Required Field";
		case "16":
			return "Requested Response Size Exceeded";
		case "17":
			return "Invalid Input/Output Media";
		default:
			return "Invalid Request";
		}
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
		iLogDetail.setEndPoint((String) getSMTParameter("CIBIL_SOCKET_IP", String.class));
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
		logger.debug(Literal.LEAVING);
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
					requiredValue = responseDetails.getCibilResponseDetails(required[1]);

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

					String gender = jsonObject.get("Gender").toString();
					if (gender == "1") {
						jsonObject.put("Gender", "Female");
					} else {
						jsonObject.put("Gender", "Male");
					}

					String dob = jsonObject.get("DateofBirth").toString();
					String date = getFormattedDate(dob);
					jsonObject.put("DateofBirth", date);
				}
				// Id Segment repeated segment
				if ("ID".equals(key)) {
					JSONArray Array = new JSONArray();
					JSONObject id = null;
					String idSegment = detailsResult.get("ID");
					String[] required = idSegment.split("ID03I0");

					for (int i = 1; i < required.length; i++) {
						String idNo = required[i].substring(0, 1);
						requiredValue = responseDetails
								.getCibilResponseDetails(required[i].substring(1, required[i].length()));
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

						String idType = id.get("IDType").toString();
						id.put("IDType", cibilIdTypes.get(idType));

						if (!jsonObject.isNull("IssueDate")) {
							String dob = jsonObject.get("IssueDate").toString();
							String date = getFormattedDate(dob);
							jsonObject.put("IssueDate", date);
						}

						if (!jsonObject.isNull("ExpirationDate")) {
							String dob = jsonObject.get("ExpirationDate").toString();
							String date = getFormattedDate(dob);
							jsonObject.put("ExpirationDate", date);
						}

						Array.add(id);
						id = null;
					}

					jsonObject.put("ID", Array);

				}

				// Telephone Segment repeated segment
				if ("PT".equals(key)) {
					JSONArray Array = new JSONArray();
					JSONObject pt = null;

					String ptSegment = detailsResult.get("PT");
					String[] required = ptSegment.split("PT03T0");
					for (int i = 1; i < required.length; i++) {
						String telePhone = required[i].substring(0, 1);
						requiredValue = responseDetails
								.getCibilResponseDetails(required[i].substring(1, required[i].length()));
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

						String phoneType = pt.get("TelephoneType").toString();
						pt.put("TelephoneType", cibilPhoneTypes.get(phoneType));

						Array.add(pt);
						pt = null;
					}
					jsonObject.put("TelephoneSegment", Array);
				}

				// Email Contact Segment repeated segment
				if ("EC".equals(key)) {
					JSONArray Array = new JSONArray();
					JSONObject ec = null;
					String ecSegment = detailsResult.get("EC");
					String[] required = ecSegment.split("EC03C0");
					for (int i = 1; i < required.length; i++) {
						String email = required[i].substring(0, 1);
						requiredValue = responseDetails
								.getCibilResponseDetails(required[i].substring(1, required[i].length()));
						requiredValue.put("00", email);
						Iterator<String> it = requiredValue.keySet().iterator();
						ec = new JSONObject();
						while (it.hasNext()) {
							String ecKey = it.next();
							if (ecKey.equals("00") && !StringUtils.trimToEmpty(requiredValue.get("00")).equals("")) {
								ec.put("EMail", requiredValue.get("00"));
							}
							if (ecKey.equals("01") && !StringUtils.trimToEmpty(requiredValue.get("01")).equals("")) {
								ec.put("EMailID", requiredValue.get("01"));
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
					requiredValue = responseDetails.getCibilResponseDetails(required[1]);

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

					if (!jsonObject.isNull("OccupationCode")) {
						String occCode = (String) jsonObject.get("OccupationCode");
						jsonObject.put("OccupationCode", cibilOccupationTypes.get(occCode));
					}

					if (!jsonObject.isNull("NetORGrossIncomeIndicator")) {
						String netOrGrossIndicator = (String) jsonObject.get("NetORGrossIncomeIndicator");
						jsonObject.put("NetORGrossIncomeIndicator", cibilIncomeIndicator.get(netOrGrossIndicator));
					}

					if (!jsonObject.isNull("MonthlyORAnnualIncomeIndicator")) {
						String monOrAnnualIncomeIndicator = (String) jsonObject.get("MonthlyORAnnualIncomeIndicator");
						jsonObject.put("MonthlyORAnnualIncomeIndicator",
								cibilMonthlyAnnualIncomeIndicator.get(monOrAnnualIncomeIndicator));
					}
				}
				// Enquiry Account Number Segment repeated segment
				if ("PI".equals(key)) {
					JSONArray Array = new JSONArray();
					JSONObject pi = null;

					String piSegment = detailsResult.get("PI");
					String[] required = piSegment.split("PI03I0");
					for (int i = 1; i < required.length; i++) {
						String accNo = required[i].substring(0, 1);
						requiredValue = responseDetails
								.getCibilResponseDetails(required[i].substring(1, required[i].length()));
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
					requiredValue = responseDetails.getCibilResponseDetails(reqScSegment[1]);
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
						requiredValue = responseDetails
								.getCibilResponseDetails(required[i].substring(1, required[i].length()));
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

						String stateCode = pa.get("StateCode").toString();
						pa.put("StateCode", cibilStateCodes.get(stateCode));

						if (!pa.isNull("AddressCategory")) {
							String addCategory = (String) pa.get("AddressCategory");
							pa.put("AddressCategory", cibilAddrCategory.get(addCategory));
						}

						if (!pa.isNull("ResidenceCode")) {
							String residenceCode = (String) pa.get("ResidenceCode");
							pa.put("ResidenceCode", cibilResidenceCode.get(residenceCode));
						}

						if (!pa.isNull("DateReported")) {
							String date = pa.get("DateReported").toString();
							String formatteddate = getFormattedDate(date);
							pa.put("DateReported", formatteddate);
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
						requiredValue = responseDetails
								.getCibilResponseDetails(required[i].substring(3, required[i].length()));
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

						if (!tl.isNull("DateOpenedORDisbursed")) {
							String date = tl.get("DateOpenedORDisbursed").toString();
							String formatteddate = getFormattedDate(date);
							tl.put("DateOpenedORDisbursed", formatteddate);
						}

						if (!tl.isNull("DateofLastPayment")) {
							String date = tl.get("DateofLastPayment").toString();
							String formatteddate = getFormattedDate(date);
							tl.put("DateofLastPayment", formatteddate);
						}

						if (!tl.isNull("DateClosed")) {
							String date = tl.get("DateClosed").toString();
							String formatteddate = getFormattedDate(date);
							tl.put("DateClosed", formatteddate);
						}

						if (!tl.isNull("DateReportedandCertified")) {
							String date = tl.get("DateReportedandCertified").toString();
							String formatteddate = getFormattedDate(date);
							tl.put("DateReportedandCertified", formatteddate);
						}

						if (!tl.isNull("PaymentHistoryStartDate")) {
							String date = tl.get("PaymentHistoryStartDate").toString();
							String formatteddate = getFormattedDate(date);
							tl.put("PaymentHistoryStartDate", formatteddate);
						}

						if (!tl.isNull("PaymentHistoryEndDate")) {
							String date = tl.get("PaymentHistoryEndDate").toString();
							String formatteddate = getFormattedDate(date);
							tl.put("PaymentHistoryEndDate", formatteddate);
						}

						if (!tl.isNull("OwnershipIndicator")) {
							String ownershipCode = (String) tl.get("OwnershipIndicator");
							tl.put("OwnershipIndicator", cibilOwnershipTypes.get(ownershipCode));
						}

						if (!tl.isNull("AccountType")) {
							String accType = (String) tl.get("AccountType");
							tl.put("AccountType", cibilloanTypes.get(accType));
						}

						if (!tl.isNull("HighCreditORSanctionedAmount")) {
							String data = tl.get("HighCreditORSanctionedAmount").toString();
							BigDecimal amount = BigDecimal.valueOf(Long.valueOf(data));
							String formattedAmount = formatAmount(amount, 2, false);
							tl.put("HighCreditORSanctionedAmount", formattedAmount);
						}

						if (!tl.isNull("CurrentBalance")) {
							String data = tl.get("CurrentBalance").toString();
							BigDecimal amount = BigDecimal.valueOf(Long.valueOf(data));
							String formattedAmount = formatAmount(amount, 2, false);
							tl.put("CurrentBalance", formattedAmount);
						}

						if (!tl.isNull("AmountOverdue")) {
							String data = tl.get("AmountOverdue").toString();
							BigDecimal amount = BigDecimal.valueOf(Long.valueOf(data));
							String formattedAmount = formatAmount(amount, 2, false);
							tl.put("AmountOverdue", formattedAmount);
						}

						if (!tl.isNull("CreditLimit")) {
							String data = tl.get("CreditLimit").toString();
							BigDecimal amount = BigDecimal.valueOf(Long.valueOf(data));
							String formattedAmount = formatAmount(amount, 2, false);
							tl.put("CreditLimit", formattedAmount);
						}

						if (!tl.isNull("CashLimit")) {
							String data = tl.get("CashLimit").toString();
							BigDecimal amount = BigDecimal.valueOf(Long.valueOf(data));
							String formattedAmount = formatAmount(amount, 2, false);
							tl.put("CashLimit", formattedAmount);
						}

						if (!tl.isNull("EMIAmount")) {
							String data = tl.get("EMIAmount").toString();
							BigDecimal amount = BigDecimal.valueOf(Long.valueOf(data));
							String formattedAmount = formatAmount(amount, 2, false);
							tl.put("EMIAmount", formattedAmount);
						}

						if (!tl.isNull("PaymentFrequency")) {
							String paymentFreq = (String) tl.get("PaymentFrequency");
							tl.put("PaymentFrequency", cibilPaymentFreqTypes.get(paymentFreq));
						}

						if (!tl.isNull("PaymentHistory1") && !tl.isNull("PaymentHistoryEndDate")
								&& !tl.isNull("PaymentHistoryStartDate")) {
							String paymentHistory = (String) tl.get("PaymentHistory1");
							String paymentHistoryStDate = (String) tl.get("PaymentHistoryStartDate");

							SimpleDateFormat sdf = new SimpleDateFormat(InterfaceConstants.dateFormat);
							Date date = sdf.parse(paymentHistoryStDate);

							int startMonth = DateUtil.getMonth(date);
							String startYear = String.valueOf(DateUtil.getYear(date));
							int reqYear = Integer.parseInt(startYear.substring(2));

							List<String> ph = java.util.Arrays.asList(paymentHistory.split("(?<=\\G...)"));
							String finalKey = "";
							for (String t : ph) {
								t = t.concat("(" + startMonth + "-" + reqYear + ")   ");
								startMonth = startMonth - 1;
								if (startMonth == 0) {
									startMonth = 12;
									reqYear = reqYear - 1;
								}

								finalKey = finalKey.concat(t);
							}

							tl.put("PaymentHistory1", finalKey);
						}

						Array.add(tl);

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
						requiredValue = responseDetails
								.getCibilResponseDetails(required[i].substring(3, required[i].length()));
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

						String enqPurpose = iq.get("EnquiryPurpose").toString();
						iq.put("EnquiryPurpose", cibilloanTypes.get(enqPurpose));

						if (!iq.isNull("DateofEnquiry")) {
							String date = iq.get("DateofEnquiry").toString();
							String formatteddate = getFormattedDate(date);
							iq.put("DateofEnquiry", formatteddate);
						}

						if (!iq.isNull("EnquiryAmount")) {
							String data = iq.get("EnquiryAmount").toString();
							BigDecimal amount = BigDecimal.valueOf(Long.valueOf(data));
							String formattedAmount = formatAmount(amount, 2, false);
							iq.put("EnquiryAmount", formattedAmount);
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
					requiredValue = responseDetails.getCibilResponseDetails(required[1]);

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
					requiredValue = responseDetails.getCibilResponseDetails(required[1]);

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

			/*
			 * // Account Segment repeated Segment JSONArray Array = new
			 * JSONArray(); JSONObject jo = null; String tlSegment =
			 * detailsResult.get("TL"); String[] required =
			 * tlSegment.split("TL04T"); for (int i = 1; i < required.length;
			 * i++) {
			 * 
			 * String AccSno = required[i].substring(0, 3); requiredValue =
			 * responseDetails.getCibilResponseDetails(required[i].substring(3,
			 * required[i].length())); requiredValue.put("00", AccSno);
			 * Iterator<String> it = requiredValue.keySet().iterator(); jo = new
			 * JSONObject(); while (it.hasNext()) { String tlKey = it.next();
			 * 
			 * for (int j = 0; j < StaticListUtil.getAccountSummary().size();
			 * j++) {
			 * 
			 * String value =
			 * StaticListUtil.getAccountSummary().get(j).getValue(); String
			 * label = StaticListUtil.getAccountSummary().get(j).getLabel();
			 * 
			 * if (tlKey.equals(value) &&
			 * !StringUtils.trimToEmpty(requiredValue.get(value)).equals("")) {
			 * jo.put(label, requiredValue.get(value)); } }
			 * 
			 * }
			 * 
			 * 
			 * 
			 * Array.add(jo); jo = null; } jsonObject.put("AccountSegment",
			 * Array);
			 */

			logger.debug(Literal.LEAVING);
		} catch (Exception e) {
			logger.debug("Error parsing response in  " + key + " segment");
			throw e;

		}
		return jsonObject;
	}

	private String getFormattedDate(String date) {
		try {
			SimpleDateFormat df = new SimpleDateFormat(InterfaceConstants.cibildateFormat);
			java.util.Date uDate = null;
			uDate = df.parse(date);
			return (DateUtil.format(uDate, InterfaceConstants.dateFormat));
		} catch (ParseException e) {
			logger.debug("ParseException...." + e.getMessage());
		}
		return null;
	}

	public static String formatAmount(BigDecimal value, int decPos, boolean debitCreditSymbol) {

		if (value != null && value.compareTo(BigDecimal.ZERO) != 0) {
			DecimalFormat df = new DecimalFormat();

			String format = "";

			if (InterfaceConstants.INDIAN_IMPLEMENTATION) {
				format = "###,###,###,###";// Can be modified for Local Currency
											// format indication
			} else {
				format = "###,###,###,###";
			}

			StringBuffer sb = new StringBuffer(format);
			boolean negSign = false;

			if (decPos > 0) {
				// sb.append('.');
				for (int i = 0; i < decPos; i++) {
					// sb.append('0');
				}

				if (value.compareTo(BigDecimal.ZERO) == -1) {
					negSign = true;
					value = value.multiply(new BigDecimal("-1"));
				}

				if (negSign) {
					value = value.multiply(new BigDecimal("-1"));
				}
			}

			if (debitCreditSymbol) {
				String s = sb.toString();
				sb.append(" 'Cr';").append(s).append(" 'Dr'");
			}

			df.applyPattern(sb.toString());
			return df.format(value);
		} else {
			String string = "0.";
			for (int i = 0; i < decPos; i++) {
				string = string.concat("0");
			}
			return string;
		}
	}

	/**
	 * Method for prepare the Extended Field details map according to the given
	 * response.
	 * 
	 * @param extendedResMapObject
	 * @param financeDetail
	 */
	private void prepareResponseObj(Map<String, Object> validatedMap, CustomerDetails customerDetails) {
		logger.debug(Literal.ENTERING);
		if (validatedMap != null) {
			Map<String, Object> extendedMapObject = null;
			extendedMapObject = getExtendedMapValues(customerDetails);
			if (customerDetails.getExtendedFieldRender() != null) {
				extendedMapObject = customerDetails.getExtendedFieldRender().getMapValues();
			}
			if (extendedMapObject == null) {
				extendedMapObject = new HashMap<String, Object>();
			}
			for (Entry<String, Object> entry : validatedMap.entrySet()) {
				extendedMapObject.put(entry.getKey(), entry.getValue());
			}

			if (customerDetails.getExtendedFieldRender() == null) {
				customerDetails.setExtendedFieldRender(new ExtendedFieldRender());
			}

			try {
				customerDetails.getExtendedFieldRender().setMapValues(extendedMapObject);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		logger.debug(Literal.LEAVING);
	}

	protected Map<String, Object> getExtendedMapValues(Object object) {
		Map<String, Object> extendedMapObject = null;
		if (object instanceof FinanceDetail) {
			FinanceDetail financeDetail = (FinanceDetail) object;
			if (financeDetail.getExtendedFieldRender() != null) {
				extendedMapObject = financeDetail.getExtendedFieldRender().getMapValues();
			}
		} else if (object instanceof CustomerDetails) {
			CustomerDetails customerDetails = (CustomerDetails) object;
			extendedMapObject = customerDetails.getExtendedFieldRender().getMapValues();
		} else {
			extendedMapObject = null;
		}
		return extendedMapObject;
	}

	private void prepareResponseObjforCoapp(Map<String, Object> validatedMap, CustomerDetails customerDetail,
			FinanceDetail financeDetail) {
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

		List<JointAccountDetail> coApplist = financeDetail.getJountAccountDetailList();
		for (JointAccountDetail jointAccountDetail : coApplist) {
			jointAccountDetail.setCustomerDetails(customerDetail);
		}

		logger.debug(Literal.LEAVING);

	}

	/**
	 * Method for validate the jsonResponseMap based on the configuration.
	 * 
	 * @param extendedFieldMap
	 * @return validatedMap
	 */
	private Map<String, Object> validateExtendedMapValues(Map<String, Object> extendedFieldMap) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> validatedMap = new HashMap<>(1);
		Set<String> fieldNames = extendedFieldMap.keySet();
		List<ExtendedFieldDetail> configurationList = null;
		if (fieldNames == null || (fieldNames != null && fieldNames.isEmpty())) {
			logger.info("Response Elements Not Configured.");
		} else {

			configurationList = creditInterfaceDao.getExtendedFieldDetailsByFieldName(fieldNames);
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
						Date dateValue = null;
						try {
							if (StringUtils.isNotBlank(jsonRespValue)) {
								dateValue = DateUtil.parse(jsonRespValue, InterfaceConstants.dateFormat);
							}
						} catch (Exception e) {
							logger.error("Exception : ", e);
						}
						validatedMap.put(key, dateValue);
						break;

					case InterfaceConstants.FIELDTYPE_TIME:
						String time = null;
						try {
							if (StringUtils.isNotBlank(jsonRespValue) && jsonRespValue.length() == 6) {
								time = jsonRespValue.substring(0, 2) + ":" + jsonRespValue.substring(2, 4) + ":"
										+ jsonRespValue.substring(4, 6);
								dateValue = DateUtil.parse(time, InterfaceConstants.DBTimeFormat);
							}
						} catch (Exception e) {
							logger.error("Exception : ", e);
						}
						// validatedMap.put(key, time);
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
							if (staticList.contains(InterfaceConstants.DELIMITER_COMMA)) {
								values = staticList.split(InterfaceConstants.DELIMITER_COMMA);
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
						if (multiStaticList != null && multiStaticList.contains(InterfaceConstants.DELIMITER_COMMA)) {
							values1 = multiStaticList.split(InterfaceConstants.DELIMITER_COMMA);
						}
						if (fieldValue != null && jsonRespValue.contains(InterfaceConstants.DELIMITER_COMMA)) {
							fieldvalues = jsonRespValue.split(InterfaceConstants.DELIMITER_COMMA);
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
						if (jsonRespValue.length() <= InterfaceConstants.LENGTH_FREQUENCY) {
							validatedMap.put(key, jsonRespValue);
						} else {
							logger.error(InterfaceConstants.wrongLengthMSG + configuration.getFieldLabel());
						}

						break;

					case InterfaceConstants.FIELDTYPE_ACCOUNT:
						if (jsonRespValue.length() <= InterfaceConstants.LENGTH_ACCOUNT) {
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
	 * private void prepareAccountNumSegment(StringBuilder builder,
	 * FinanceDetail finDetail) { FinanceMain financeMain =
	 * finDetail.getFinScheduleData().getFinanceMain(); writeValue(builder,
	 * "06", financeMain.getFinReference(), "02"); }
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

		String date = jsonObject.get("DateProcessed").toString();
		String foramtteddate = getFormattedDate(date);
		jsonObject.put("DateProcessed", foramtteddate);

		String time = jsonObject.get("TimeProcessed").toString();
		String formattedTime = time.substring(0, 2) + ":" + time.substring(2, 4) + ":" + time.substring(4, 6);
		jsonObject.put("TimeProcessed", formattedTime);

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
		logger.debug("Parsed Error header respone " + jsonObject.toString());

		logger.debug(Literal.LEAVING);
	}

}
