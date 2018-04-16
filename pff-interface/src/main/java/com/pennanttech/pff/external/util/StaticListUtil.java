package com.pennanttech.pff.external.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pennanttech.dataengine.constants.ValueLabel;

public class StaticListUtil {

	public static List<String> getHeaderDetails() {
		List<String> header = new ArrayList<String>();
		header.add("SegmentTag");
		header.add("Version");
		header.add("ApplicationReferenceNumber");
		header.add("FutureUse1");
		header.add("EnquiryMemberUserID");
		header.add("EnquiryPassword");
		header.add("EnquiryPurpose");
		header.add("EnquiryAmount");
		header.add("FutureUse2");
		header.add("ScoreType");
		header.add("OutputFormat");
		header.add("ResponseSize");
		header.add("InputOutputMedia");
		header.add("AuthenticationMethod");

		return header;

	}

	public static String[] getErrorHeaderIndexes() {

		String[] values = new String[] { "0,4", "4,12", "12,18" };

		return values;

	}

	public static List<String> getErrorResponseHeader() {
		ArrayList<String> errorDetails = new ArrayList<String>();
		errorDetails.add("SegmentTag");
		errorDetails.add("DateProcessed");
		errorDetails.add("TimeProcessed");

		return errorDetails;

	}

	public static ArrayList<ValueLabel> getNameSegmentFieldTypes() {

		ArrayList<ValueLabel> pnSegmentValueLabl = new ArrayList<ValueLabel>(18);
		pnSegmentValueLabl = new ArrayList<ValueLabel>();
		pnSegmentValueLabl.add(new ValueLabel("01", "ConsumerNameField1"));
		pnSegmentValueLabl.add(new ValueLabel("02", "ConsumerNameField2"));
		pnSegmentValueLabl.add(new ValueLabel("03", "ConsumerNameField3"));
		pnSegmentValueLabl.add(new ValueLabel("07", "DateofBirth"));
		pnSegmentValueLabl.add(new ValueLabel("08", "Gender"));

		return pnSegmentValueLabl;
	}

	public static ArrayList<ValueLabel> getTelePhoneSegmentFieldTypes() {

		ArrayList<ValueLabel> TpSegmentValueLabel = new ArrayList<ValueLabel>(18);
		TpSegmentValueLabel = new ArrayList<ValueLabel>();

		TpSegmentValueLabel.add(new ValueLabel("00", "TelePhone"));
		TpSegmentValueLabel.add(new ValueLabel("01", "TelephoneNumber"));
		TpSegmentValueLabel.add(new ValueLabel("02", "TelephoneExtension"));
		TpSegmentValueLabel.add(new ValueLabel("03", "TelephoneType"));
		TpSegmentValueLabel.add(new ValueLabel("90", "PhSNoEnrichedThroughEnquiry"));

		return TpSegmentValueLabel;
	}

	public static ArrayList<ValueLabel> getIdSegmentFieldTypes() {

		ArrayList<ValueLabel> idSegmentValueLabl = new ArrayList<ValueLabel>();
		idSegmentValueLabl = new ArrayList<ValueLabel>();

		idSegmentValueLabl.add(new ValueLabel("00", "IDNo"));
		idSegmentValueLabl.add(new ValueLabel("01", "IDType"));
		idSegmentValueLabl.add(new ValueLabel("02", "IDNumber"));
		idSegmentValueLabl.add(new ValueLabel("03", "IssueDate"));
		idSegmentValueLabl.add(new ValueLabel("04", "ExpirationDate"));
		idSegmentValueLabl.add(new ValueLabel("90", "EnrichedThroughEnquiry"));

		return idSegmentValueLabl;
	}

	public static ArrayList<ValueLabel> getEmpSegmentFieldTypes() {

		ArrayList<ValueLabel> idSegmentValueLabl = new ArrayList<ValueLabel>();
		idSegmentValueLabl = new ArrayList<ValueLabel>();
		idSegmentValueLabl.add(new ValueLabel("01", "AccountType"));
		idSegmentValueLabl.add(new ValueLabel("02", "DateReportedandCertified"));
		idSegmentValueLabl.add(new ValueLabel("03", "OccupationCode"));
		idSegmentValueLabl.add(new ValueLabel("04", "Income"));
		idSegmentValueLabl.add(new ValueLabel("05", "NetORGrossIncomeIndicator"));
		idSegmentValueLabl.add(new ValueLabel("06", "MonthlyORAnnualIncomeIndicator"));
		idSegmentValueLabl.add(new ValueLabel("80", "DateofEntryforErrorCode"));
		idSegmentValueLabl.add(new ValueLabel("82", "ErrorCode"));
		idSegmentValueLabl.add(new ValueLabel("83", "DateofEntryforCIBILRemarksCode"));
		idSegmentValueLabl.add(new ValueLabel("84", "CIBILRemarksCode"));
		idSegmentValueLabl.add(new ValueLabel("85", "DateofEntryforErrorORDisputeRemarksCode"));
		idSegmentValueLabl.add(new ValueLabel("86", "ErrorORDisputeRemarksCode1"));
		idSegmentValueLabl.add(new ValueLabel("87", "ErrorORDisputeRemarksCode1"));

		return idSegmentValueLabl;
	}

	public static ArrayList<ValueLabel> getScoreSegmentFieldTypes() {

		ArrayList<ValueLabel> idSegmentValueLabl = new ArrayList<ValueLabel>();
		idSegmentValueLabl = new ArrayList<ValueLabel>();
		idSegmentValueLabl.add(new ValueLabel("01", "ScoreCardName"));
		idSegmentValueLabl.add(new ValueLabel("02", "ScoreCardVersion"));
		idSegmentValueLabl.add(new ValueLabel("03", "ScoreDate"));
		idSegmentValueLabl.add(new ValueLabel("04", "Score"));
		idSegmentValueLabl.add(new ValueLabel("05", "ExclusionCode1"));
		idSegmentValueLabl.add(new ValueLabel("06", "ExclusionCode2"));
		idSegmentValueLabl.add(new ValueLabel("07", "ExclusionCode3"));
		idSegmentValueLabl.add(new ValueLabel("08", "ExclusionCode4"));
		idSegmentValueLabl.add(new ValueLabel("09", "ExclusionCode5"));
		idSegmentValueLabl.add(new ValueLabel("10", "ExclusionCode6"));
		idSegmentValueLabl.add(new ValueLabel("11", "ExclusionCode7"));
		idSegmentValueLabl.add(new ValueLabel("12", "ExclusionCode8"));
		idSegmentValueLabl.add(new ValueLabel("13", "ExclusionCode9"));
		idSegmentValueLabl.add(new ValueLabel("14", "ExclusionCode10"));
		idSegmentValueLabl.add(new ValueLabel("25", "ReasonCode1"));
		idSegmentValueLabl.add(new ValueLabel("26", "ReasonCode2"));
		idSegmentValueLabl.add(new ValueLabel("27", "ReasonCode3"));
		idSegmentValueLabl.add(new ValueLabel("28", "ReasonCode4"));
		idSegmentValueLabl.add(new ValueLabel("29", "ReasonCode5"));
		idSegmentValueLabl.add(new ValueLabel("75", "ScoreErrorCode"));

		return idSegmentValueLabl;
	}

	public static ArrayList<ValueLabel> getAddressSegmentFieldTypes() {

		ArrayList<ValueLabel> addressSegmentValueLabl = new ArrayList<ValueLabel>();
		addressSegmentValueLabl = new ArrayList<ValueLabel>();

		addressSegmentValueLabl.add(new ValueLabel("00", "Address"));
		addressSegmentValueLabl.add(new ValueLabel("01", "AddressLine1"));
		addressSegmentValueLabl.add(new ValueLabel("02", "AddressLine2"));
		addressSegmentValueLabl.add(new ValueLabel("03", "AddressLine3"));
		addressSegmentValueLabl.add(new ValueLabel("04", "AddressLine4"));
		addressSegmentValueLabl.add(new ValueLabel("05", "AddressLine5"));
		addressSegmentValueLabl.add(new ValueLabel("06", "StateCode"));
		addressSegmentValueLabl.add(new ValueLabel("07", "PinCode"));
		addressSegmentValueLabl.add(new ValueLabel("08", "AddressCategory"));
		addressSegmentValueLabl.add(new ValueLabel("09", "ResidenceCode"));
		addressSegmentValueLabl.add(new ValueLabel("10", "DateReported"));
		addressSegmentValueLabl.add(new ValueLabel("11", "MemberShortName"));
		addressSegmentValueLabl.add(new ValueLabel("90", "AddressEnrichedThroughEnquiry"));

		return addressSegmentValueLabl;
	}

	public static ArrayList<ValueLabel> getAccountSegmentFieldTypes() {

		ArrayList<ValueLabel> addressSegmentValueLabl = new ArrayList<ValueLabel>();
		addressSegmentValueLabl = new ArrayList<ValueLabel>();

		addressSegmentValueLabl.add(new ValueLabel("00", "AccSno"));
		addressSegmentValueLabl.add(new ValueLabel("02", "ReportingMemberShortName"));
		addressSegmentValueLabl.add(new ValueLabel("03", "AccountNumber"));
		addressSegmentValueLabl.add(new ValueLabel("04", "AccountType"));
		addressSegmentValueLabl.add(new ValueLabel("05", "OwnershipIndicator"));
		addressSegmentValueLabl.add(new ValueLabel("08", "DateOpenedORDisbursed"));
		addressSegmentValueLabl.add(new ValueLabel("09", "DateofLastPayment"));
		addressSegmentValueLabl.add(new ValueLabel("10", "DateClosed"));
		addressSegmentValueLabl.add(new ValueLabel("11", "DateReportedandCertified"));
		addressSegmentValueLabl.add(new ValueLabel("12", "HighCreditORSanctionedAmount"));
		addressSegmentValueLabl.add(new ValueLabel("13", "CurrentBalance"));
		addressSegmentValueLabl.add(new ValueLabel("14", "AmountOverdue"));
		addressSegmentValueLabl.add(new ValueLabel("28", "PaymentHistory1"));
		addressSegmentValueLabl.add(new ValueLabel("29", "PaymentHistory2"));
		addressSegmentValueLabl.add(new ValueLabel("30", "PaymentHistoryStartDate"));
		addressSegmentValueLabl.add(new ValueLabel("31", "PaymentHistoryEndDate"));
		addressSegmentValueLabl.add(new ValueLabel("32", "SuitFiledORSWilful_Default"));
		addressSegmentValueLabl.add(new ValueLabel("33", "WrittenoffandSettledStatus"));
		addressSegmentValueLabl.add(new ValueLabel("34", "ValueofCollateral"));
		addressSegmentValueLabl.add(new ValueLabel("35", "TypeofCollateral"));
		addressSegmentValueLabl.add(new ValueLabel("36", "CreditLimit"));
		addressSegmentValueLabl.add(new ValueLabel("37", "CashLimit"));
		addressSegmentValueLabl.add(new ValueLabel("38", "RateOfInterest"));
		addressSegmentValueLabl.add(new ValueLabel("39", "RepaymentTenure"));
		addressSegmentValueLabl.add(new ValueLabel("40", "EMIAmount"));
		addressSegmentValueLabl.add(new ValueLabel("41", "WrittenoffAmountTotal"));
		addressSegmentValueLabl.add(new ValueLabel("42", "WrittenoffAmount_Principal"));
		addressSegmentValueLabl.add(new ValueLabel("43", "SettlementAmount"));
		addressSegmentValueLabl.add(new ValueLabel("44", "PaymentFrequency"));
		addressSegmentValueLabl.add(new ValueLabel("45", "ActualPaymentAmount"));
		addressSegmentValueLabl.add(new ValueLabel("80", "DateofEntryforErrorCode"));
		addressSegmentValueLabl.add(new ValueLabel("82", "ErrorCode"));
		addressSegmentValueLabl.add(new ValueLabel("83", "DateofEntryforCIBILRemarksCode"));
		addressSegmentValueLabl.add(new ValueLabel("84", "CIBILRemarksCode"));
		addressSegmentValueLabl.add(new ValueLabel("85", "DateofEntryforErrorORDisputeRemarksCode"));
		addressSegmentValueLabl.add(new ValueLabel("86", "Error_DisputeRemarksCode1"));
		addressSegmentValueLabl.add(new ValueLabel("87", "Error_DisputeRemarksCode2"));

		return addressSegmentValueLabl;
	}

	public static ArrayList<ValueLabel> getEnqSegmentFieldTypes() {

		ArrayList<ValueLabel> enqSegmentValueLabl = new ArrayList<ValueLabel>();
		enqSegmentValueLabl = new ArrayList<ValueLabel>();
		enqSegmentValueLabl.add(new ValueLabel("00", "EnqSno"));
		enqSegmentValueLabl.add(new ValueLabel("01", "DateofEnquiry"));
		enqSegmentValueLabl.add(new ValueLabel("04", "EnquiringMemberShortName"));
		enqSegmentValueLabl.add(new ValueLabel("05", "EnquiryPurpose"));
		enqSegmentValueLabl.add(new ValueLabel("06", "EnquiryAmount"));

		return enqSegmentValueLabl;
	}

	public static ArrayList<ValueLabel> getDrSegmentFieldTypes() {

		ArrayList<ValueLabel> drSegmentValueLabl = new ArrayList<ValueLabel>();
		drSegmentValueLabl = new ArrayList<ValueLabel>();
		drSegmentValueLabl.add(new ValueLabel("01", "DateofEntry"));
		drSegmentValueLabl.add(new ValueLabel("02", "DisputeRemarksLine1"));
		drSegmentValueLabl.add(new ValueLabel("03", "DisputeRemarksLine2"));
		drSegmentValueLabl.add(new ValueLabel("04", "DisputeRemarksLine3"));
		drSegmentValueLabl.add(new ValueLabel("05", "DisputeRemarksLine4"));
		drSegmentValueLabl.add(new ValueLabel("06", "DisputeRemarksLine5"));
		drSegmentValueLabl.add(new ValueLabel("07", "DisputeRemarksLine6"));

		return drSegmentValueLabl;
	}

	public static ArrayList<ValueLabel> getUrSegmentFieldTypes() {

		ArrayList<ValueLabel> urSegmentValueLabl = new ArrayList<ValueLabel>();
		urSegmentValueLabl = new ArrayList<ValueLabel>();
		urSegmentValueLabl.add(new ValueLabel("01", "ApplicationReference"));
		urSegmentValueLabl.add(new ValueLabel("03", "InvalidVersion"));
		urSegmentValueLabl.add(new ValueLabel("04", "InvalidFieldLength"));
		urSegmentValueLabl.add(new ValueLabel("05", "InvalidTotalLength"));
		urSegmentValueLabl.add(new ValueLabel("06", "InvalidEnquiryPurpose"));
		urSegmentValueLabl.add(new ValueLabel("07", "InvalidEnquiryAmount"));
		urSegmentValueLabl.add(new ValueLabel("08", "InvalidEnquiryMemberUserID_Password"));
		urSegmentValueLabl.add(new ValueLabel("09", "RequiredEnquirySegmentMissing"));
		urSegmentValueLabl.add(new ValueLabel("10", "InvalidEnquiryData"));
		urSegmentValueLabl.add(new ValueLabel("11", "CIBILSystemError"));
		urSegmentValueLabl.add(new ValueLabel("12", "InvalidSegmentTag"));
		urSegmentValueLabl.add(new ValueLabel("13", "InvalidSegmentOrder"));
		urSegmentValueLabl.add(new ValueLabel("14", "InvalidFieldTagOrder"));
		urSegmentValueLabl.add(new ValueLabel("15", "MissingRequiredField"));
		urSegmentValueLabl.add(new ValueLabel("16", "RequestedResponseSizeExceeded"));
		urSegmentValueLabl.add(new ValueLabel("17", "InvalidInput_OutputMedia"));

		return urSegmentValueLabl;
	}

	public static List<String> getResponseHeaders() {
		List<String> header = new ArrayList<String>();
		header.add("SegmentTag");
		header.add("Version");
		header.add("ApplicationReferenceNumber");
		header.add("FutureUse1");
		header.add("FutureUse2");
		header.add("EnquiryMemberUserID");
		header.add("SubjectReturnCode");
		header.add("EnquiryControlNumber");
		header.add("DateProcessed");
		header.add("TimeProcessed");
		return header;

	}

	public static String[] getHeaderIndexes() {

		String[] values = new String[] { "0,4", "4,6", "6,31", "31,33", "33,37", "37,67", "67,68", "68,80", "80,88",
				"88,94" };

		return values;

	}
	
	
	public static final HashMap<String, String> getCibilOccupationCode() {
		HashMap<String, String> occupationTypes = new HashMap<String, String>();
		occupationTypes.put("01", "Salaried");
		occupationTypes.put("02", "Self Employeed Professional");
		occupationTypes.put("03", "Self Employed");
		occupationTypes.put("04", "Others");
		return occupationTypes;

	}
	
	public static final HashMap<String, String> getCibilIncomeIndicator() {
		HashMap<String, String> incomeIndicator = new HashMap<String, String>();
		incomeIndicator.put("G", "Gross Income");
		incomeIndicator.put("N", "Net Income");
		return incomeIndicator;

	}
	
	public static final HashMap<String, String> getCibilMonthlyAnnualIncomeIndicator() {
		HashMap<String, String> incomeIndicator = new HashMap<String, String>();
		incomeIndicator.put("M", "Monthly");
		incomeIndicator.put("A", "Annual");
		return incomeIndicator;

	}
	
	public static final HashMap<String, String> getCibilAddrCategory() {
		HashMap<String, String> addrCategory = new HashMap<String, String>();
		addrCategory.put("01", "Permanent Address");
		addrCategory.put("02", "Residence Address");
		addrCategory.put("03", "Office Address");
		addrCategory.put("04", "Not Categorized");
		return addrCategory;

	}
	
	
	public static final HashMap<String, String> getCibilResidenceCode() {
		HashMap<String, String> residenceCode = new HashMap<String, String>();
		residenceCode.put("01", "Owned");
		residenceCode.put("02", "Rented");
		return residenceCode;

	}
	
	public static final HashMap<String, String> getCibilIDTypes() {
		HashMap<String, String> telephoneTypes = new HashMap<String, String>();
		telephoneTypes.put("01", "Pan Number");
		telephoneTypes.put("02", "PassPortNumber");
		telephoneTypes.put("03", "Voter ID Number");
		telephoneTypes.put("04", "Driving License");
		telephoneTypes.put("05", "Ration Card Number");
		telephoneTypes.put("06", "Universal ID Number");
		return telephoneTypes;

	}
	
	
	
}
