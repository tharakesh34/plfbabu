/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *
 * FileName    		:  FinnovCibilEnquiryProcess.java										*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  23-05-2018															*
 *                                                                  
 * Modified Date    :  23-05-2018															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-05-2018       Pennant	                 1.0          Created as part of Finnov 
 * 														  Profectus integration			    * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennanttech.pff.external.creditInformation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.web.client.RestTemplate;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.model.systemmasters.City;
import com.pennanttech.logging.model.InterfaceLogDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.InterfaceConstants;
import com.pennanttech.pff.core.util.DateUtil;
import com.pennanttech.pff.dao.CreditInterfaceDAO;
import com.pennanttech.pff.external.AbstractInterface;
import com.pennanttech.pff.external.FinnovService;
import com.pennanttech.pff.external.util.StaticListUtil;
import com.pennanttech.pff.logging.dao.InterfaceLoggingDAO;
import com.pennanttech.pff.model.AddressDetails;
import com.pennanttech.pff.model.FinnovRequest;

public class FinnovCibilEnquiryProcess extends AbstractInterface implements FinnovService {
	private static final String FINNOV = "FINNOV";

	private static final String FINNOV_HANDSHAKE = "FINNOV_HANDSHAKE";

	private static final Logger logger = Logger.getLogger(FinnovCibilEnquiryProcess.class);

	private String handShakeUrl;
	private String finnovDataUrl;
	private Map<String, Object> extendedMap = null;
	@Autowired(required = false)
	private InterfaceLoggingDAO interfaceLoggingDAO;
	@Autowired(required = false)
	private CreditInterfaceDAO creditInterfaceDao;
	private final String extConfigFileName = "FinnovCibilFields.properties";
	private String jsonResponse;
	
	private Map<String,String> cibilIdTypes=new HashMap<>();
	private Map<String, String> cibilPhoneTypes = new HashMap<>();
	private Map<String, String> cibilOccupationTypes = StaticListUtil.getCibilOccupationCode();
	private Map<String,String> cibilStateCodes=new HashMap<>();
	private Map<String, String> cibilAddrCategory = StaticListUtil.getCibilAddrCategory();
	private Map<String, String> cibilResidenceCode = StaticListUtil.getCibilResidenceCode();
	private Map<String,String> cibilloanTypes=new HashMap<>();
	private Map<String, String> cibilOwnershipTypes = StaticListUtil.getCibilOnwerShipTypes();

	@Override
	public AuditHeader getFinnovReport(AuditHeader auditHeader) {

		logger.debug(Literal.ENTERING);

		if (StringUtils.isBlank(handShakeUrl)||StringUtils.isBlank(finnovDataUrl)) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}
		
		loadCibilIdTypes();
		loadCibilPhoneTypes();
		loadCibilStateCodes();
		loadCibilLoanTypes();
		
		Map<String, Object> appplicationdata = new HashMap<>();
		Customer customer = null;
		FinanceDetail financeDetail = null;
		CustomerDetails customerDetails = null;

		Object object = auditHeader.getAuditDetail().getModelData();

		if (object instanceof FinanceDetail) {
			financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
			customerDetails = financeDetail.getCustomerDetails();
			customer = customerDetails.getCustomer();
		} else {
			customerDetails = (CustomerDetails) auditHeader.getAuditDetail().getModelData();
			customer = customerDetails.getCustomer();
			extendedMap = getExtendedMapValues(customerDetails);
		}
		
		if(null!=customerDetails&&null!=customerDetails.getCustomer()&&StringUtils.equals("RETAIL", customerDetails.getCustomer().getCustCtgCode())) {
			String reference = customer.getCustCIF();
			Timestamp reqSentOn = new Timestamp(System.currentTimeMillis());
			JSONClient client = new JSONClient();
			String finnovRequest=null;
			try {
				String handShakeResponse = client.get(handShakeUrl);
				//String handShakeResponse= "{\"access_token\":\"5e3dd69c-9c14-436e-bc01-09d88efefea1\",\"token_type\":\"bearer\",\"expires_in\":11124,\"scope\":\"trust read write\"}";
				String accessKey = null;
				try{
					accessKey = JsonPath.read(handShakeResponse, "access_token");
				}catch(Exception e){
					logger.debug(Literal.EXCEPTION, e);
				}
				if(StringUtils.isEmpty(accessKey)){
					doInterfaceLogging(reference, handShakeUrl, null, handShakeResponse, "", "", reqSentOn,FINNOV_HANDSHAKE,InterfaceConstants.STATUS_FAILED);
					logger.debug(Literal.LEAVING);
					return auditHeader;
				}else{
					doInterfaceLogging(reference, handShakeUrl, null, handShakeResponse, "", "", reqSentOn,FINNOV_HANDSHAKE,InterfaceConstants.STATUS_SUCCESS);
				}
				FinnovRequest request = prepareRequestObj(customerDetails);
				finnovRequest = client.getRequestString(request);
				
				boolean processed = compareRequest(finnovRequest, reference, FINNOV, InterfaceConstants.STATUS_SUCCESS);
				if (processed) {
					logger.debug(Literal.LEAVING);
					return auditHeader;
				}
				jsonResponse = client.post(finnovDataUrl, finnovRequest, accessKey);
				//jsonResponse="{\"success\":true,\"errorMsg\":null,\"erroCode\":null,\"cibilProfile\":{\"id\":\"5b02783993f4501dfe5badad\",\"tuefResponse\":{\"segmentTag\":\"TUEF\",\"version\":\"12\",\"memberReferenceNumber\":\"PROFECTUS882245\",\"enquiryMemberUserId\":\"NB43338888_C2C\",\"subjectReturnCode\":\"1\",\"enquiryControlNumber\":\"002411001863\",\"dateAndTimeProcessed\":1526887800030},\"nameSegmentResponse\":{\"segmentTag\":null,\"nameField1\":\"MR SANJAYKUMAR\",\"nameField2\":\"MANGALAPRASAD GUPTA\",\"nameField3\":null,\"nameField4\":null,\"nameField5\":null,\"dateOfBirth\":246133800000,\"gender\":\"Male\",\"errorCodeDateEntry\":null,\"errorSegmentTag\":null,\"errorCode\":null,\"cibilRemarkCodeDateOfEntry\":null,\"cibilRemarkCode\":null,\"error_disputeRemarkCodeDateOfEntry\":null,\"error_disputeRemarkCode1\":null,\"error_disputeRemarkCode2\":null},\"ids\":[{\"idType\":1,\"idNumber\":\"AILPG9330J\",\"issueDate\":null,\"expirationDate\":null,\"enrichThroughEnquiry\":null},{\"idType\":6,\"idNumber\":\"644936927838\",\"issueDate\":null,\"expirationDate\":null,\"enrichThroughEnquiry\":null}],\"telphones\":[{\"telephoneNo\":\"7666610625\",\"telephoneExtension\":null,\"telephoneType\":3,\"enrichThroughEnquiry\":\"Y\"},{\"telephoneNo\":\"9834186379\",\"telephoneExtension\":null,\"telephoneType\":3,\"enrichThroughEnquiry\":null},{\"telephoneNo\":\"7666610625\",\"telephoneExtension\":\"91\",\"telephoneType\":1,\"enrichThroughEnquiry\":null},{\"telephoneNo\":\"022-2360790\",\"telephoneExtension\":null,\"telephoneType\":3,\"enrichThroughEnquiry\":null}],\"emails\":null,\"employment\":{\"accountType\":\"06\",\"dateReportedAndCertified\":1504117800000,\"occupationCode\":3,\"income\":25000.0,\"netGrossIncomeIndicator\":null,\"monthlyAnnulIncomeIndicator\":null,\"dateofEntryForErrorCode\":null,\"errorCode\":null,\"cibilRemarkCodeDateOfEntry\":null,\"cibilRemarkCode\":null,\"error_disputeRemarkCodeDateOfEntry\":null,\"error_disputeRemarkCode1\":null,\"error_disputeRemarkCode2\":null},\"enquiryAccountNumbers\":null,\"scores\":[{\"scoreName\":\"CIBILTUSC2\",\"scoreCardName\":\"04\",\"scoreCardVersion\":\"10\",\"scoreDate\":1526841000000,\"score\":767,\"exclusionCode1\":null,\"exclusionCode2\":null,\"exclusionCode3\":null,\"exclusionCode4\":null,\"exclusionCode5\":null,\"exclusionCode6\":null,\"exclusionCode7\":null,\"exclusionCode8\":null,\"exclusionCode9\":null,\"exclusionCode10\":null,\"reasonCode1\":\"17\",\"reasonCode2\":\"05\",\"reasonCode3\":\"14\",\"reasonCode4\":null,\"reasonCode5\":null,\"errorCode\":null},{\"scoreName\":\"PLSCORE\",\"scoreCardName\":\"02\",\"scoreCardVersion\":\"10\",\"scoreDate\":1526841000000,\"score\":769,\"exclusionCode1\":null,\"exclusionCode2\":null,\"exclusionCode3\":null,\"exclusionCode4\":null,\"exclusionCode5\":null,\"exclusionCode6\":null,\"exclusionCode7\":null,\"exclusionCode8\":null,\"exclusionCode9\":null,\"exclusionCode10\":null,\"reasonCode1\":\"19\",\"reasonCode2\":\"08\",\"reasonCode3\":\"10\",\"reasonCode4\":null,\"reasonCode5\":null,\"errorCode\":null}],\"addresses\":[{\"addressLine1\":\"SHOP NO1 SAI VILLA APT GALA NAGAR ACHOLA\",\"addressLine2\":null,\"addressLine3\":null,\"addressLine4\":null,\"addressLine5\":null,\"stateCode\":\"27\",\"pinCode\":\"401203\",\"addressCategory\":\"04\",\"residenceCode\":null,\"dateReported\":1522434600000,\"memberShortName\":null,\"enrichThroughEnquiry\":null},{\"addressLine1\":\"NALASOPARA, THANE\",\"addressLine2\":null,\"addressLine3\":null,\"addressLine4\":null,\"addressLine5\":null,\"stateCode\":\"27\",\"pinCode\":\"401209\",\"addressCategory\":\"02\",\"residenceCode\":null,\"dateReported\":1509388200000,\"memberShortName\":null,\"enrichThroughEnquiry\":\"Y\"},{\"addressLine1\":\"FLAT NO 102 A WING KAILASH DARSHAN 1\",\"addressLine2\":\"TULIANG ALKA PURI ACOLE ROAD NALASOPARA\",\"addressLine3\":\"EAST\",\"addressLine4\":null,\"addressLine5\":null,\"stateCode\":\"27\",\"pinCode\":\"401209\",\"addressCategory\":\"02\",\"residenceCode\":\"01\",\"dateReported\":1503945000000,\"memberShortName\":null,\"enrichThroughEnquiry\":\"Y\"},{\"addressLine1\":\"A 102 KAILASH DARSHAN ,KAPURI ACHOLE\",\"addressLine2\":\"ROAD ,TULINJ NALASOPARA , , MUMBAI\",\"addressLine3\":null,\"addressLine4\":null,\"addressLine5\":null,\"stateCode\":\"27\",\"pinCode\":\"400001\",\"addressCategory\":\"02\",\"residenceCode\":null,\"dateReported\":1497465000000,\"memberShortName\":null,\"enrichThroughEnquiry\":\"Y\"}],\"accounts\":[{\"reportingMemberShortName\":\"NOT DISCLOSED\",\"accountNo\":null,\"accountType\":\"10\",\"ownershipIndicator\":\"1\",\"opened_disbursed\":1519669800000,\"lastPaymentDate\":null,\"dateClosed\":null,\"reportedAndCertifiedDate\":1525026600000,\"highCredit_SanctionedAmount\":11800.0,\"currentBalance\":26100.0,\"amountOverdue\":null,\"rawPaymentHistory1\":\"000000000\",\"rawPaymentHistory2\":\"\",\"rawPaymentHistory\":\"000000000\",\"paymentHistoryInDPD\":[\"000\",\"000\",\"000\"],\"paymentHistoryStartDate\":1522521000000,\"paymentHistoryEndDate\":1517423400000,\"paymentDates\":[\"3 2018\",\"2 2018\",\"1 2018\"],\"suitFiled_wilfulDefault\":null,\"writtenOffAndSettledStatus\":null,\"valueOfCollateral\":null,\"typeOfCollateral\":null,\"creditLimit\":0,\"cashLimit\":0,\"rateOfInterest\":0.0,\"repaymentTenure\":null,\"eMIAmount\":0.0,\"writtenOffAmountTotal\":0.0,\"writtenOffAmountPrinciple\":0.0,\"settlementAmount\":0.0,\"paymentFrequency\":null,\"actualPaymentAmount\":0.0,\"errorCodeEntryDate\":null,\"errorCode\":null,\"cibilRemarkEntryDate\":null,\"cibilRemarkCode\":null,\"error_disputeRemarkCodeEntryDate\":null,\"error_disputeRemarkCode1\":null,\"error_disputeRemarkCode2\":null,\"numberOfPaymentHistory\":0,\"dateToPaymentMap\":{\"3 2018\":\"000\",\"2 2018\":\"000\",\"1 2018\":\"000\"}},{\"reportingMemberShortName\":\"NOT DISCLOSED\",\"accountNo\":null,\"accountType\":\"06\",\"ownershipIndicator\":\"1\",\"opened_disbursed\":1486578600000,\"lastPaymentDate\":1502562600000,\"dateClosed\":1502649000000,\"reportedAndCertifiedDate\":1504117800000,\"highCredit_SanctionedAmount\":7539.0,\"currentBalance\":0.0,\"amountOverdue\":null,\"rawPaymentHistory1\":\"000000000000000000\",\"rawPaymentHistory2\":\"\",\"rawPaymentHistory\":\"000000000000000000\",\"paymentHistoryInDPD\":[\"000\",\"000\",\"000\",\"000\",\"000\",\"000\"],\"paymentHistoryStartDate\":1501525800000,\"paymentHistoryEndDate\":1488306600000,\"paymentDates\":[\"7 2017\",\"6 2017\",\"5 2017\",\"4 2017\",\"3 2017\",\"2 2017\"],\"suitFiled_wilfulDefault\":null,\"writtenOffAndSettledStatus\":null,\"valueOfCollateral\":null,\"typeOfCollateral\":null,\"creditLimit\":0,\"cashLimit\":0,\"rateOfInterest\":0.0,\"repaymentTenure\":6,\"eMIAmount\":1257.0,\"writtenOffAmountTotal\":0.0,\"writtenOffAmountPrinciple\":0.0,\"settlementAmount\":0.0,\"paymentFrequency\":3,\"actualPaymentAmount\":1254.0,\"errorCodeEntryDate\":null,\"errorCode\":null,\"cibilRemarkEntryDate\":null,\"cibilRemarkCode\":null,\"error_disputeRemarkCodeEntryDate\":null,\"error_disputeRemarkCode1\":null,\"error_disputeRemarkCode2\":null,\"numberOfPaymentHistory\":0,\"dateToPaymentMap\":{\"7 2017\":\"000\",\"6 2017\":\"000\",\"5 2017\":\"000\",\"4 2017\":\"000\",\"3 2017\":\"000\",\"2 2017\":\"000\"}},{\"reportingMemberShortName\":\"NOT DISCLOSED\",\"accountNo\":null,\"accountType\":\"02\",\"ownershipIndicator\":\"4\",\"opened_disbursed\":1260729000000,\"lastPaymentDate\":1363285800000,\"dateClosed\":null,\"reportedAndCertifiedDate\":1522434600000,\"highCredit_SanctionedAmount\":665724.0,\"currentBalance\":0.0,\"amountOverdue\":null,\"rawPaymentHistory1\":\"000000000000000000000XXX000000000000000000XXX000000000\",\"rawPaymentHistory2\":\"000000000000000000000000000000000000000000XXXXXXXXXXXX\",\"rawPaymentHistory\":\"000000000000000000000XXX000000000000000000XXX000000000000000000000000000000000000000000000000000XXXXXXXXXXXX\",\"paymentHistoryInDPD\":[\"000\",\"000\",\"000\",\"000\",\"000\",\"000\",\"000\",\"XXX\",\"000\",\"000\",\"000\",\"000\",\"000\",\"000\",\"XXX\",\"000\",\"000\",\"000\",\"000\",\"000\",\"000\",\"000\",\"000\",\"000\",\"000\",\"000\",\"000\",\"000\",\"000\",\"000\",\"000\",\"000\",\"XXX\",\"XXX\",\"XXX\",\"XXX\"],\"paymentHistoryStartDate\":1519842600000,\"paymentHistoryEndDate\":1427826600000,\"paymentDates\":[\"2 2018\",\"1 2018\",\"0 2018\",\"11 2017\",\"10 2017\",\"9 2017\",\"8 2017\",\"7 2017\",\"6 2017\",\"5 2017\",\"4 2017\",\"3 2017\",\"2 2017\",\"1 2017\",\"0 2017\",\"11 2016\",\"10 2016\",\"9 2016\",\"8 2016\",\"7 2016\",\"6 2016\",\"5 2016\",\"4 2016\",\"3 2016\",\"2 2016\",\"1 2016\",\"0 2016\",\"11 2015\",\"10 2015\",\"9 2015\",\"8 2015\",\"7 2015\",\"6 2015\",\"5 2015\",\"4 2015\",\"3 2015\"],\"suitFiled_wilfulDefault\":null,\"writtenOffAndSettledStatus\":null,\"valueOfCollateral\":\"835000\",\"typeOfCollateral\":\"01\",\"creditLimit\":0,\"cashLimit\":0,\"rateOfInterest\":14.75,\"repaymentTenure\":168,\"eMIAmount\":9001.0,\"writtenOffAmountTotal\":0.0,\"writtenOffAmountPrinciple\":0.0,\"settlementAmount\":0.0,\"paymentFrequency\":3,\"actualPaymentAmount\":0.0,\"errorCodeEntryDate\":null,\"errorCode\":null,\"cibilRemarkEntryDate\":null,\"cibilRemarkCode\":null,\"error_disputeRemarkCodeEntryDate\":null,\"error_disputeRemarkCode1\":null,\"error_disputeRemarkCode2\":null,\"numberOfPaymentHistory\":0,\"dateToPaymentMap\":{\"2 2018\":\"000\",\"1 2018\":\"000\",\"0 2018\":\"000\",\"11 2017\":\"000\",\"10 2017\":\"000\",\"9 2017\":\"000\",\"8 2017\":\"000\",\"7 2017\":\"XXX\",\"6 2017\":\"000\",\"5 2017\":\"000\",\"4 2017\":\"000\",\"3 2017\":\"000\",\"2 2017\":\"000\",\"1 2017\":\"000\",\"0 2017\":\"XXX\",\"11 2016\":\"000\",\"10 2016\":\"000\",\"9 2016\":\"000\",\"8 2016\":\"000\",\"7 2016\":\"000\",\"6 2016\":\"000\",\"5 2016\":\"000\",\"4 2016\":\"000\",\"3 2016\":\"000\",\"2 2016\":\"000\",\"1 2016\":\"000\",\"0 2016\":\"000\",\"11 2015\":\"000\",\"10 2015\":\"000\",\"9 2015\":\"000\",\"8 2015\":\"000\",\"7 2015\":\"000\",\"6 2015\":\"XXX\",\"5 2015\":\"XXX\",\"4 2015\":\"XXX\",\"3 2015\":\"XXX\"}},{\"reportingMemberShortName\":\"NOT DISCLOSED\",\"accountNo\":null,\"accountType\":\"06\",\"ownershipIndicator\":\"1\",\"opened_disbursed\":1202668200000,\"lastPaymentDate\":1265826600000,\"dateClosed\":1265826600000,\"reportedAndCertifiedDate\":1267295400000,\"highCredit_SanctionedAmount\":39684.0,\"currentBalance\":0.0,\"amountOverdue\":null,\"rawPaymentHistory1\":\"000000000000000000000000000000XXX000000STDXXXSTDSTDXXX\",\"rawPaymentHistory2\":\"000000XXX000XXXXXX000\",\"rawPaymentHistory\":\"000000000000000000000000000000XXX000000STDXXXSTDSTDXXX000000XXX000XXXXXX000\",\"paymentHistoryInDPD\":[\"000\",\"000\",\"000\",\"000\",\"000\",\"000\",\"000\",\"000\",\"000\",\"000\",\"XXX\",\"000\",\"000\",\"STD\",\"XXX\",\"STD\",\"STD\",\"XXX\",\"000\",\"000\",\"XXX\",\"000\",\"XXX\",\"XXX\",\"000\"],\"paymentHistoryStartDate\":1264962600000,\"paymentHistoryEndDate\":1201804200000,\"paymentDates\":[\"1 2010\",\"0 2010\",\"11 2009\",\"10 2009\",\"9 2009\",\"8 2009\",\"7 2009\",\"6 2009\",\"5 2009\",\"4 2009\",\"3 2009\",\"2 2009\",\"1 2009\",\"0 2009\",\"11 2008\",\"10 2008\",\"9 2008\",\"8 2008\",\"7 2008\",\"6 2008\",\"5 2008\",\"4 2008\",\"3 2008\",\"2 2008\",\"1 2008\"],\"suitFiled_wilfulDefault\":null,\"writtenOffAndSettledStatus\":null,\"valueOfCollateral\":null,\"typeOfCollateral\":null,\"creditLimit\":0,\"cashLimit\":0,\"rateOfInterest\":0.0,\"repaymentTenure\":null,\"eMIAmount\":0.0,\"writtenOffAmountTotal\":0.0,\"writtenOffAmountPrinciple\":0.0,\"settlementAmount\":0.0,\"paymentFrequency\":null,\"actualPaymentAmount\":0.0,\"errorCodeEntryDate\":null,\"errorCode\":null,\"cibilRemarkEntryDate\":null,\"cibilRemarkCode\":null,\"error_disputeRemarkCodeEntryDate\":null,\"error_disputeRemarkCode1\":null,\"error_disputeRemarkCode2\":null,\"numberOfPaymentHistory\":0,\"dateToPaymentMap\":{\"1 2010\":\"000\",\"0 2010\":\"000\",\"11 2009\":\"000\",\"10 2009\":\"000\",\"9 2009\":\"000\",\"8 2009\":\"000\",\"7 2009\":\"000\",\"6 2009\":\"000\",\"5 2009\":\"000\",\"4 2009\":\"000\",\"3 2009\":\"XXX\",\"2 2009\":\"000\",\"1 2009\":\"000\",\"0 2009\":\"STD\",\"11 2008\":\"XXX\",\"10 2008\":\"STD\",\"9 2008\":\"STD\",\"8 2008\":\"XXX\",\"7 2008\":\"000\",\"6 2008\":\"000\",\"5 2008\":\"XXX\",\"4 2008\":\"000\",\"3 2008\":\"XXX\",\"2 2008\":\"XXX\",\"1 2008\":\"000\"}},{\"reportingMemberShortName\":\"NOT DISCLOSED\",\"accountNo\":null,\"accountType\":\"05\",\"ownershipIndicator\":\"1\",\"opened_disbursed\":1173724200000,\"lastPaymentDate\":1288463400000,\"dateClosed\":null,\"reportedAndCertifiedDate\":1288809000000,\"highCredit_SanctionedAmount\":30000.0,\"currentBalance\":0.0,\"amountOverdue\":null,\"rawPaymentHistory1\":\"000XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX000000XXXXXX000\",\"rawPaymentHistory2\":\"XXX000XXX000000000000000000000000000000000000000000XXX\",\"rawPaymentHistory\":\"000XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX000000XXXXXX000XXX000XXX000000000000000000000000000000000000000000XXX\",\"paymentHistoryInDPD\":[\"000\",\"XXX\",\"XXX\",\"XXX\",\"XXX\",\"XXX\",\"XXX\",\"XXX\",\"XXX\",\"XXX\",\"XXX\",\"XXX\",\"XXX\",\"000\",\"000\",\"XXX\",\"XXX\",\"000\",\"XXX\",\"000\",\"XXX\",\"000\",\"000\",\"000\",\"000\",\"000\",\"000\",\"000\",\"000\",\"000\",\"000\",\"000\",\"000\",\"000\",\"000\",\"XXX\"],\"paymentHistoryStartDate\":1288549800000,\"paymentHistoryEndDate\":1196447400000,\"paymentDates\":[\"10 2010\",\"9 2010\",\"8 2010\",\"7 2010\",\"6 2010\",\"5 2010\",\"4 2010\",\"3 2010\",\"2 2010\",\"1 2010\",\"0 2010\",\"11 2009\",\"10 2009\",\"9 2009\",\"8 2009\",\"7 2009\",\"6 2009\",\"5 2009\",\"4 2009\",\"3 2009\",\"2 2009\",\"1 2009\",\"0 2009\",\"11 2008\",\"10 2008\",\"9 2008\",\"8 2008\",\"7 2008\",\"6 2008\",\"5 2008\",\"4 2008\",\"3 2008\",\"2 2008\",\"1 2008\",\"0 2008\",\"11 2007\"],\"suitFiled_wilfulDefault\":null,\"writtenOffAndSettledStatus\":null,\"valueOfCollateral\":null,\"typeOfCollateral\":null,\"creditLimit\":0,\"cashLimit\":0,\"rateOfInterest\":0.0,\"repaymentTenure\":null,\"eMIAmount\":0.0,\"writtenOffAmountTotal\":0.0,\"writtenOffAmountPrinciple\":0.0,\"settlementAmount\":0.0,\"paymentFrequency\":null,\"actualPaymentAmount\":0.0,\"errorCodeEntryDate\":null,\"errorCode\":null,\"cibilRemarkEntryDate\":null,\"cibilRemarkCode\":null,\"error_disputeRemarkCodeEntryDate\":null,\"error_disputeRemarkCode1\":null,\"error_disputeRemarkCode2\":null,\"numberOfPaymentHistory\":0,\"dateToPaymentMap\":{\"10 2010\":\"000\",\"9 2010\":\"XXX\",\"8 2010\":\"XXX\",\"7 2010\":\"XXX\",\"6 2010\":\"XXX\",\"5 2010\":\"XXX\",\"4 2010\":\"XXX\",\"3 2010\":\"XXX\",\"2 2010\":\"XXX\",\"1 2010\":\"XXX\",\"0 2010\":\"XXX\",\"11 2009\":\"XXX\",\"10 2009\":\"XXX\",\"9 2009\":\"000\",\"8 2009\":\"000\",\"7 2009\":\"XXX\",\"6 2009\":\"XXX\",\"5 2009\":\"000\",\"4 2009\":\"XXX\",\"3 2009\":\"000\",\"2 2009\":\"XXX\",\"1 2009\":\"000\",\"0 2009\":\"000\",\"11 2008\":\"000\",\"10 2008\":\"000\",\"9 2008\":\"000\",\"8 2008\":\"000\",\"7 2008\":\"000\",\"6 2008\":\"000\",\"5 2008\":\"000\",\"4 2008\":\"000\",\"3 2008\":\"000\",\"2 2008\":\"000\",\"1 2008\":\"000\",\"0 2008\":\"000\",\"11 2007\":\"XXX\"}}],\"enquiries\":[{\"dateOfEnquiry\":1526495400000,\"enquiryMemberShortName\":\"NOT DISCLOSED\",\"enquiryPurpose\":\"05\",\"enquiryAmount\":1003000.0},{\"dateOfEnquiry\":1525890600000,\"enquiryMemberShortName\":\"NOT DISCLOSED\",\"enquiryPurpose\":\"03\",\"enquiryAmount\":2500000.0},{\"dateOfEnquiry\":1525631400000,\"enquiryMemberShortName\":\"NOT DISCLOSED\",\"enquiryPurpose\":\"52\",\"enquiryAmount\":600000.0},{\"dateOfEnquiry\":1524421800000,\"enquiryMemberShortName\":\"PROFCAP\",\"enquiryPurpose\":\"51\",\"enquiryAmount\":2000000.0},{\"dateOfEnquiry\":1523385000000,\"enquiryMemberShortName\":\"NOT DISCLOSED\",\"enquiryPurpose\":\"09\",\"enquiryAmount\":3000000.0},{\"dateOfEnquiry\":1520965800000,\"enquiryMemberShortName\":\"NOT DISCLOSED\",\"enquiryPurpose\":\"09\",\"enquiryAmount\":2060000.0},{\"dateOfEnquiry\":1520793000000,\"enquiryMemberShortName\":\"NOT DISCLOSED\",\"enquiryPurpose\":\"52\",\"enquiryAmount\":625000.0},{\"dateOfEnquiry\":1519151400000,\"enquiryMemberShortName\":\"NOT DISCLOSED\",\"enquiryPurpose\":\"10\",\"enquiryAmount\":10000.0},{\"dateOfEnquiry\":1516213800000,\"enquiryMemberShortName\":\"NOT DISCLOSED\",\"enquiryPurpose\":\"02\",\"enquiryAmount\":10000.0},{\"dateOfEnquiry\":1511548200000,\"enquiryMemberShortName\":\"NOT DISCLOSED\",\"enquiryPurpose\":\"02\",\"enquiryAmount\":1000.0},{\"dateOfEnquiry\":1509388200000,\"enquiryMemberShortName\":\"NOT DISCLOSED\",\"enquiryPurpose\":\"10\",\"enquiryAmount\":100000.0},{\"dateOfEnquiry\":1506277800000,\"enquiryMemberShortName\":\"NOT DISCLOSED\",\"enquiryPurpose\":\"02\",\"enquiryAmount\":2000000.0},{\"dateOfEnquiry\":1503945000000,\"enquiryMemberShortName\":\"NOT DISCLOSED\",\"enquiryPurpose\":\"03\",\"enquiryAmount\":2000000.0},{\"dateOfEnquiry\":1499884200000,\"enquiryMemberShortName\":\"NOT DISCLOSED\",\"enquiryPurpose\":\"51\",\"enquiryAmount\":2000009.0},{\"dateOfEnquiry\":1497465000000,\"enquiryMemberShortName\":\"NOT DISCLOSED\",\"enquiryPurpose\":\"03\",\"enquiryAmount\":2000009.0},{\"dateOfEnquiry\":1486405800000,\"enquiryMemberShortName\":\"NOT DISCLOSED\",\"enquiryPurpose\":\"06\",\"enquiryAmount\":7539.0},{\"dateOfEnquiry\":1259001000000,\"enquiryMemberShortName\":\"NOT DISCLOSED\",\"enquiryPurpose\":\"02\",\"enquiryAmount\":665724.0},{\"dateOfEnquiry\":1258309800000,\"enquiryMemberShortName\":\"NOT DISCLOSED\",\"enquiryPurpose\":\"02\",\"enquiryAmount\":665724.0},{\"dateOfEnquiry\":1202063400000,\"enquiryMemberShortName\":\"NOT DISCLOSED\",\"enquiryPurpose\":\"06\",\"enquiryAmount\":50000.0},{\"dateOfEnquiry\":1173637800000,\"enquiryMemberShortName\":\"NOT DISCLOSED\",\"enquiryPurpose\":\"05\",\"enquiryAmount\":35000.0}],\"disputeRemark\":null,\"totalEnquiries\":20,\"lastThirtyDayEnquiries\":4,\"lastThreeMonthEnquries\":7,\"lastTwelveMonthEnquiries\":15,\"lastTwoYearEnquiries\":16,\"cibilSummary\":{\"applicantName\":\"MR SANJAYKUMAR MANGALAPRASAD GUPTA null\",\"cibilScore\":767,\"personalLoanScore\":769,\"totalEnquiries\":20,\"lastThirtyDayEnquiries\":4,\"lastThreeMonthEnquiries\":7,\"lastTwelveMonthEnquiries\":15,\"lastTwoYearEnquiries\":16,\"highCredit\":754747.0,\"currentBalance\":26100.0,\"recentReportingDate\":\"\",\"oldestReportingDate\":\"\",\"totalNoOfAccount\":5,\"noOfOpenAccountIndi\":2,\"noOfOpenAccountOther\":1,\"totalNoOfOverDueAccount\":0,\"totalReportedMonths\":106,\"delinquentUpto90D\":0,\"delinquentAbove90D\":0,\"delinquentUpto12M\":null,\"totalSubLossMonth\":0,\"lastSanctionedLoanDate\":\"0\",\"totalZeroBalanceAccounts\":4,\"totalOutstandingBalance\":null,\"overdueBalance\":0.0,\"kycDetails\":null,\"openAccountDetailSummary\":{\"individualSecuredAccounts\":{\"totalSanctionedAmount\":0.0,\"totalCurrentAmount\":0.0,\"totalEmiAmount\":0.0,\"totalOverDueAmount\":0.0,\"regularAccount\":0,\"totalDPDLessThan30D\":0,\"totalDPDLessThan90D\":0,\"totalDPDGreaterThan90D\":0,\"totalWrittenOffAccount\":0,\"totalSuitFiledAccount\":0,\"totalAccount\":0},\"individualUnSecuredAccounts\":{\"totalSanctionedAmount\":41800.0,\"totalCurrentAmount\":26100.0,\"totalEmiAmount\":0.0,\"totalOverDueAmount\":0.0,\"regularAccount\":0,\"totalDPDLessThan30D\":22,\"totalDPDLessThan90D\":0,\"totalDPDGreaterThan90D\":0,\"totalWrittenOffAccount\":0,\"totalSuitFiledAccount\":0,\"totalAccount\":0},\"individualJointSecuredAccounts\":{\"totalSanctionedAmount\":0.0,\"totalCurrentAmount\":0.0,\"totalEmiAmount\":0.0,\"totalOverDueAmount\":0.0,\"regularAccount\":0,\"totalDPDLessThan30D\":0,\"totalDPDLessThan90D\":0,\"totalDPDGreaterThan90D\":0,\"totalWrittenOffAccount\":0,\"totalSuitFiledAccount\":0,\"totalAccount\":0},\"individualJointUnSecuredAccounts\":{\"totalSanctionedAmount\":0.0,\"totalCurrentAmount\":0.0,\"totalEmiAmount\":0.0,\"totalOverDueAmount\":0.0,\"regularAccount\":0,\"totalDPDLessThan30D\":0,\"totalDPDLessThan90D\":0,\"totalDPDGreaterThan90D\":0,\"totalWrittenOffAccount\":0,\"totalSuitFiledAccount\":0,\"totalAccount\":0},\"individualGuarantorSecuredAccounts\":{\"totalSanctionedAmount\":0.0,\"totalCurrentAmount\":0.0,\"totalEmiAmount\":0.0,\"totalOverDueAmount\":0.0,\"regularAccount\":0,\"totalDPDLessThan30D\":0,\"totalDPDLessThan90D\":0,\"totalDPDGreaterThan90D\":0,\"totalWrittenOffAccount\":0,\"totalSuitFiledAccount\":0,\"totalAccount\":0},\"individualGuarantorUnSecuredAccounts\":{\"totalSanctionedAmount\":0.0,\"totalCurrentAmount\":0.0,\"totalEmiAmount\":0.0,\"totalOverDueAmount\":0.0,\"regularAccount\":0,\"totalDPDLessThan30D\":0,\"totalDPDLessThan90D\":0,\"totalDPDGreaterThan90D\":0,\"totalWrittenOffAccount\":0,\"totalSuitFiledAccount\":0,\"totalAccount\":0}},\"totalAccountDetailSummary\":{\"individualSecuredAccounts\":{\"totalSanctionedAmount\":0.0,\"totalCurrentAmount\":0.0,\"totalEmiAmount\":0.0,\"totalOverDueAmount\":0.0,\"regularAccount\":0,\"totalDPDLessThan30D\":0,\"totalDPDLessThan90D\":0,\"totalDPDGreaterThan90D\":0,\"totalWrittenOffAccount\":0,\"totalSuitFiledAccount\":0,\"totalAccount\":0},\"individualUnSecuredAccounts\":{\"totalSanctionedAmount\":89023.0,\"totalCurrentAmount\":26100.0,\"totalEmiAmount\":1257.0,\"totalOverDueAmount\":0.0,\"regularAccount\":0,\"totalDPDLessThan30D\":44,\"totalDPDLessThan90D\":0,\"totalDPDGreaterThan90D\":0,\"totalWrittenOffAccount\":0,\"totalSuitFiledAccount\":0,\"totalAccount\":0},\"individualJointSecuredAccounts\":{\"totalSanctionedAmount\":0.0,\"totalCurrentAmount\":0.0,\"totalEmiAmount\":0.0,\"totalOverDueAmount\":0.0,\"regularAccount\":0,\"totalDPDLessThan30D\":0,\"totalDPDLessThan90D\":0,\"totalDPDGreaterThan90D\":0,\"totalWrittenOffAccount\":0,\"totalSuitFiledAccount\":0,\"totalAccount\":0},\"individualJointUnSecuredAccounts\":{\"totalSanctionedAmount\":0.0,\"totalCurrentAmount\":0.0,\"totalEmiAmount\":0.0,\"totalOverDueAmount\":0.0,\"regularAccount\":0,\"totalDPDLessThan30D\":0,\"totalDPDLessThan90D\":0,\"totalDPDGreaterThan90D\":0,\"totalWrittenOffAccount\":0,\"totalSuitFiledAccount\":0,\"totalAccount\":0},\"individualGuarantorSecuredAccounts\":{\"totalSanctionedAmount\":0.0,\"totalCurrentAmount\":0.0,\"totalEmiAmount\":0.0,\"totalOverDueAmount\":0.0,\"regularAccount\":0,\"totalDPDLessThan30D\":0,\"totalDPDLessThan90D\":0,\"totalDPDGreaterThan90D\":0,\"totalWrittenOffAccount\":0,\"totalSuitFiledAccount\":0,\"totalAccount\":0},\"individualGuarantorUnSecuredAccounts\":{\"totalSanctionedAmount\":0.0,\"totalCurrentAmount\":0.0,\"totalEmiAmount\":0.0,\"totalOverDueAmount\":0.0,\"regularAccount\":0,\"totalDPDLessThan30D\":0,\"totalDPDLessThan90D\":0,\"totalDPDGreaterThan90D\":0,\"totalWrittenOffAccount\":0,\"totalSuitFiledAccount\":0,\"totalAccount\":0}},\"indexMap\":{\"openAccountDetailSummaryindividualUnSecuredAccountstotalSanctionedAmount\":[0,4]}}}}";
				String finalJsonResponse=processResponse(jsonResponse,request,customerDetails);
				if(StringUtils.isNotEmpty(finalJsonResponse)){
					jsonResponse=finalJsonResponse;
				}
				boolean success = false;
				String errorCode=null;
				String errorMsg=null;
				try{
					success = JsonPath.read(jsonResponse, "success");
					errorCode = JsonPath.read(handShakeResponse, "erroCode");
					errorMsg = JsonPath.read(handShakeResponse, "errorMsg");
				}catch(Exception e){
				}
				if(BooleanUtils.isTrue(success)){
					doInterfaceLogging(reference, finnovDataUrl,finnovRequest, jsonResponse, "", "", reqSentOn,FINNOV,InterfaceConstants.STATUS_SUCCESS);
				}else{
					doInterfaceLogging(reference, finnovDataUrl,finnovRequest, jsonResponse, errorCode, errorMsg, reqSentOn,FINNOV,InterfaceConstants.STATUS_FAILED);
					return auditHeader;
				}
				//System.out.println(jsonResponse);
				Map<String, Object> mapdata = getPropValueFromResp(jsonResponse, extConfigFileName);
				Map<String, Object> mapvalidData = validateExtendedMapValues(mapdata);
				appplicationdata.putAll(mapvalidData);
				logger.debug("Success Respone :" + jsonResponse);
				
			} catch (Exception e) {
				logger.debug(Literal.EXCEPTION, e);
				String error = e.getMessage();
				if (error == null) {
					error = e.getStackTrace().toString();
				}
				if (error.length() > 200) {
					error.substring(0, 200);
				}
				doInterfaceLogging(reference, finnovDataUrl,finnovRequest, jsonResponse, "", error, reqSentOn,FINNOV,InterfaceConstants.STATUS_FAILED);
			}
			
			prepareResponseObj(appplicationdata, customerDetails);
		}
		
		logger.debug(Literal.LEAVING);

		return auditHeader;
	}
	
	private String processResponse(String jsonResponse, FinnovRequest request, CustomerDetails customerDetails) {
		logger.debug(Literal.ENTERING);
		String finalJsonResponse=null;
		try{
			JSONObject jsonObject=new JSONObject(jsonResponse);
			if(null!=request){
				jsonObject.put("panNumber", StringUtils.trimToEmpty(request.getPanCardNo()));
			}
			if(null!=customerDetails&&null!=customerDetails.getCustomer()){
				jsonObject.put("customerId", StringUtils.trimToEmpty(customerDetails.getCustomer().getCustCIF()));
			}
			Object cibilObject=jsonObject.get("cibilProfile");
			if(null!=cibilObject&&cibilObject instanceof JSONObject){
				JSONObject cibilJsonObject=(JSONObject)cibilObject;
				populateTuefResponse(cibilJsonObject);
				populateNameSegmentResponse(cibilJsonObject);
				populateIds(cibilJsonObject);
				populateTelephoneDetails(cibilJsonObject);
				populateEmployeeDetails(cibilJsonObject);
				populateAddressDetails(cibilJsonObject);
				populateAccountDetails(cibilJsonObject);
				populateEnquiryDetails(cibilJsonObject);
			}
			
			if(null!=cibilObject&&cibilObject instanceof JSONObject){
				jsonObject.put("cibilProfile", cibilObject);
			}
			if(null!=jsonObject){
				finalJsonResponse=jsonObject.toString();
			}
		}catch(Exception e){
			logger.debug(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return finalJsonResponse;
	}

	private void populateNameSegmentResponse(JSONObject jsonObject) {
		Object nameSegmentResponse=jsonObject.get("nameSegmentResponse");
		if(null!=nameSegmentResponse &&(nameSegmentResponse instanceof JSONObject)){
			JSONObject object=(JSONObject)nameSegmentResponse;
			object.put("dateOfBirth", dateParser(object.get("dateOfBirth")));
		}
	}

	private void populateTuefResponse(JSONObject jsonObject) {
		Object tuefResponse=jsonObject.get("tuefResponse");
		if(null!=tuefResponse &&(tuefResponse instanceof JSONObject)){
			JSONObject object=(JSONObject)tuefResponse;
			object.put("dateAndTimeProcessed", dateParser(object.get("dateAndTimeProcessed")));
		}
	}

	private String dateParser(Object dateAndTime) {
		String parsedDate=null;
		if(null!=dateAndTime&& dateAndTime instanceof Long){
			parsedDate = Instant.ofEpochMilli((long) dateAndTime).atZone(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		}
		return StringUtils.trimToEmpty(parsedDate);
	}

	private void populateEnquiryDetails(JSONObject jsonObject) {
		Object ids=jsonObject.get("enquiries");
		if(null!=ids && (ids instanceof JSONArray)){
			JSONArray idArrays=(JSONArray)ids;
			idArrays.forEach((id)->{
				if(null!=id &&(id instanceof JSONObject)){
					JSONObject object=(JSONObject)id;
					object.put("dateOfEnquiry", dateParser(object.get("dateOfEnquiry")));
					object.put("enquiryPurpose", StringUtils.trimToEmpty(cibilloanTypes.get(object.get("enquiryPurpose"))));
				}
			});
		}
	}

	private void populateAccountDetails(JSONObject jsonObject) {
		Object ids=jsonObject.get("accounts");
		if(null!=ids && (ids instanceof JSONArray)){
			JSONArray idArrays=(JSONArray)ids;
			idArrays.forEach((id)->{
				if(null!=id &&(id instanceof JSONObject)){
					JSONObject object=(JSONObject)id;
					object.put("accountType", StringUtils.trimToEmpty(cibilloanTypes.get(object.get("accountType"))));
					object.put("ownershipIndicator", StringUtils.trimToEmpty(cibilOwnershipTypes.get(object.get("ownershipIndicator"))));
					object.put("opened_disbursed", dateParser(object.get("opened_disbursed")));
					object.put("lastPaymentDate", dateParser(object.get("lastPaymentDate")));
					object.put("dateClosed", dateParser(object.get("dateClosed")));
					object.put("reportedAndCertifiedDate", dateParser(object.get("reportedAndCertifiedDate")));
					object.put("paymentHistoryStartDate", dateParser(object.get("paymentHistoryStartDate")));
					object.put("paymentHistoryEndDate", dateParser(object.get("paymentHistoryEndDate")));
					Object dateToPayment = object.get("dateToPaymentMap");
					StringBuilder builder=new StringBuilder();
					if(null!=dateToPayment && (dateToPayment instanceof JSONObject)){
						JSONObject dateToPaymentJson=(JSONObject)dateToPayment;
						String[] paymentKeys=JSONObject.getNames(dateToPaymentJson);
						if(ArrayUtils.isNotEmpty(paymentKeys)){
							for(String paymentKey:paymentKeys){
								builder.append(paymentKey).append(" (").append(dateToPaymentJson.get(paymentKey)).append(") ");
								if(!paymentKey.equals(paymentKeys[paymentKeys.length-1])){
									builder.append(",");
								}
							}
						}
					}
					object.put("dateToPaymentMap", StringUtils.trimToEmpty(builder.toString()));
				}
			});
		}
	}

	private void populateAddressDetails(JSONObject jsonObject) {
		Object ids=jsonObject.get("addresses");
		if(null!=ids && (ids instanceof JSONArray)){
			JSONArray idArrays=(JSONArray)ids;
			idArrays.forEach((id)->{
				if(null!=id &&(id instanceof JSONObject)){
					JSONObject object=(JSONObject)id;
					object.put("dateReported", dateParser(object.get("dateReported")));
					object.put("stateCode", StringUtils.trimToEmpty(cibilStateCodes.get(object.get("stateCode"))));
					object.put("addressCategory", StringUtils.trimToEmpty(cibilAddrCategory.get(object.get("addressCategory"))));
					object.put("residenceCode", StringUtils.trimToEmpty(cibilResidenceCode.get(object.get("residenceCode"))));
				}
			});
		}
	}

	private void populateEmployeeDetails(JSONObject jsonObject) {
		Object id=jsonObject.get("employment");
		if(null!=id &&(id instanceof JSONObject)){
			JSONObject object=(JSONObject)id;
			object.put("dateReportedAndCertified", dateParser(object.get("dateReportedAndCertified")));
			if(CollectionUtils.isNotEmpty(cibilOccupationTypes.keySet())){
				for(String key:cibilOccupationTypes.keySet()){
					if(Integer.parseInt(key)==(int)object.get("occupationCode")){
						object.put("occupationCode", StringUtils.trimToEmpty(cibilOccupationTypes.get(key)));
						break;
					}
				}
			}
		}
	}

	private void populateTelephoneDetails(JSONObject jsonObject) {
		Object ids=jsonObject.get("telphones");
		if(null!=ids && (ids instanceof JSONArray)){
			JSONArray idArrays=(JSONArray)ids;
			for(Object id:idArrays){
				if(null!=id &&(id instanceof JSONObject)){
					JSONObject object=(JSONObject)id;
					if(CollectionUtils.isNotEmpty(cibilPhoneTypes.keySet())){
						for(String key:cibilPhoneTypes.keySet()){
							if(Integer.parseInt(key)==(int)object.get("telephoneType")){
								object.put("telephoneType", StringUtils.trimToEmpty(cibilPhoneTypes.get(key)));
								break;
							}
						}
					}
				}
			}
		}
	}

	private void populateIds(JSONObject jsonObject) {
		Object ids=jsonObject.get("ids");
		if(null!=ids && (ids instanceof JSONArray)){
			JSONArray idArrays=(JSONArray)ids;
			for(Object id:idArrays){
				if(null!=id &&(id instanceof JSONObject)){
					JSONObject object=(JSONObject)id;
					object.put("expirationDate", dateParser(object.get("expirationDate")));
					if(CollectionUtils.isNotEmpty(cibilIdTypes.keySet())){
						for(String key:cibilIdTypes.keySet()){
							if(Integer.parseInt(key)==(int)object.get("idType")){
								object.put("idType", StringUtils.trimToEmpty(cibilIdTypes.get(key)));
								break;
							}
						}
					}
				}
			}
		}
	}

	private boolean compareRequest(String currentData, String reference, String service, String status) {
		String previousData = interfaceLoggingDAO.getPreviousDataifAny(reference, service, status);
		if (previousData == null) {
			return false;
		} else if (StringUtils.equals(previousData, currentData)) {
			return true;
		}
		return false;
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
	 * Method for prepare the Extended Field details map according to the given response.
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
	
	/**
	 * Method for load the properties file, then iterate the keys of that file and map to jsonResponse.
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
		InputStream inputStream = this.getClass().getResourceAsStream("/properties/"+extConfigFileName);

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
								SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
								dateValue = format.parse(jsonRespValue);
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
							SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
							dateValue = format.parse(jsonRespValue);
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
			validatedMap.put("JsonResponse", jsonResponse);
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

	public String getHandShakeResponse() {
		logger.debug(Literal.ENTERING);

		ResponseEntity<String> response;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "");
		headers.add("Content-Type", "application/json");

		try {
			HttpEntity<String> request = new HttpEntity<>(headers);
			RestTemplate restTemplate = new RestTemplate();
			response = restTemplate.exchange("IP/Cibil/oauth/token?grant_type=client_credentials&client_id=nbfc&client_secret=nbfc",
					HttpMethod.GET, request, String.class);
		} catch (Exception e) {
			logger.debug(e.getMessage());
			return null;
		}

		logger.debug(Literal.LEAVING);
		return response.getBody();
	}

	private void doInterfaceLogging(String reference,String url, String requets, String response, String errorCode,
			String errorDesc, Timestamp reqSentOn, String serviceName, String status) {
		logger.debug(Literal.ENTERING);
		InterfaceLogDetail iLogDetail = new InterfaceLogDetail();
		iLogDetail.setReference(reference);
		iLogDetail.setServiceName(serviceName);
		iLogDetail.setEndPoint(url);
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

	protected void logInterfaceDetails(InterfaceLogDetail interfaceLogDetail) {
		try {
			interfaceLoggingDAO.save(interfaceLogDetail);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}

	private FinnovRequest prepareRequestObj(CustomerDetails customerDetails) {
		FinnovRequest finnovRequest = new FinnovRequest();
		if(MapUtils.isNotEmpty(extendedMap)&&null!=extendedMap.get("ENQUIRYAMOUNT")&&StringUtils.isNotBlank(extendedMap.get("ENQUIRYAMOUNT").toString())){
			finnovRequest.setEnquiryAmount(Integer.parseInt(extendedMap.get("ENQUIRYAMOUNT").toString()));
		}
		if(null!=customerDetails.getCustomer()){
			finnovRequest.setPanCardNo(customerDetails.getCustomer().getCustCRCPR());
			finnovRequest.setName(customerDetails.getCustomer().getCustFName().concat(StringUtils.SPACE).concat(customerDetails.getCustomer().getCustLName()));
			finnovRequest.setDateOfBirth(DateUtil.format(customerDetails.getCustomer().getCustDOB(), "yyyy-MM-dd"));
			finnovRequest.setGender(customerDetails.getCustomer().getLovDescCustGenderCodeName());
		}
		if (customerDetails.getAddressList() != null) {
			CustomerAddres customerAddres = getAddress(customerDetails.getAddressList());
			finnovRequest.setCurrentAddress(prepareAddress(customerAddres));
		}

		return finnovRequest;

	}

	public static CustomerAddres getAddress(List<CustomerAddres> addressList) {
		CustomerAddres address = new CustomerAddres();
		if (addressList != null && !addressList.isEmpty()) {
			if (addressList.size() > 1) {
				sortCustomerAddres(addressList);
			}
			address = addressList.get(0);
		}
		return address;
	}

	/**
	 * Method for sort the given CustomerAddresList based on their Priority High to Low.
	 * 
	 * @param list
	 */
	public static void sortCustomerAddres(List<CustomerAddres> list) {

		if (list != null && !list.isEmpty()) {
			Collections.sort(list, new Comparator<CustomerAddres>() {
				@Override
				public int compare(CustomerAddres detail1, CustomerAddres detail2) {
					return detail2.getCustAddrPriority() - detail1.getCustAddrPriority();
				}
			});
		}
	}
	
	/**
	 * Method for prepare the Address request object.
	 * 
	 * @param custAddres
	 * @return address
	 */
	private AddressDetails prepareAddress(CustomerAddres custAddres) {
		logger.debug(Literal.ENTERING);
		AddressDetails address = new AddressDetails();
		String houseNo;
		if (StringUtils.isNotBlank(custAddres.getCustAddrHNbr())) {
			houseNo = custAddres.getCustAddrHNbr();
		} else {
			houseNo = Objects.toString(custAddres.getCustFlatNbr(), "");
		}
		address.setLine1(houseNo);

		if (StringUtils.isNotBlank(custAddres.getCustAddrStreet())) {
			address.setLine2(custAddres.getCustAddrStreet());
		}

		City city = getCityDetails(custAddres);
		if (city != null) {
			address.setCity(city.getPCCityName());
			address.setState(city.getLovDescPCProvinceName());
			address.setPostalCode((custAddres.getCustAddrZIP()));
		}

		logger.debug(Literal.ENTERING);
		return address;

	}
	
	protected City getCityDetails(CustomerAddres address) {
		City city = creditInterfaceDao.getCityDetails(address.getCustAddrCountry(), address.getCustAddrProvince(),
				address.getCustAddrCity(), "_AView");
		return city;
	}

	public String getHandShakeUrl() {
		return handShakeUrl;
	}

	public void setHandShakeUrl(String handShakeUrl) {
		this.handShakeUrl = handShakeUrl;
	}

	public String getFinnovDataUrl() {
		return finnovDataUrl;
	}

	public void setFinnovDataUrl(String finnovDataUrl) {
		this.finnovDataUrl = finnovDataUrl;
	}
}
