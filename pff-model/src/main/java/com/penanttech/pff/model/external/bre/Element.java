package com.penanttech.pff.model.external.bre;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import com.penanttech.pff.model.external.bre.BREService.TotalMarketValue;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Element {
	@JsonProperty("STATUS")
	private String status;
	@JsonProperty("DV_REASON_CODE")
	private String dv_reason_code;
	@JsonProperty("VERIFICATION_TYPE")
	private String verification_type;
	@JsonProperty("DOCUMENT_CODE")
	private String document_code;
	@JsonProperty("DV_DUMMY1")
	private String dv_dummy1;
	@JsonProperty("DV_DUMMY2")
	private String dv_dummy2;

	@JsonProperty("PD_TYPE_FLAG")
	private String pd_type_flag;
	
	@JsonProperty("FCU_STATUS")
	private String fcu_status;
	

	@JsonProperty("COLLATERAL_ID")
	private String collateral_id;
	@JsonProperty("PROPERTY_IDENTIFIED")
	private String property_identified;
	@JsonProperty("COST_OF_PROPERTY")
	private String cost_of_property;
	@JsonProperty("PROPERTY_TYPE")
	private String property_type;
	@JsonProperty("NEGATIVE_AREA_FLAG")
	private String negative_area_flag;
	@JsonProperty("NEGATIVE_PROPERTY")
	private String negative_property;
	@JsonProperty("PROPERTY_OGL")
	private String property_ogl;
	@JsonProperty("PROPERTY_AGE")
	private String property_age;
	@JsonProperty("RESIDUAL_AGE")
	private String residual_age;
	@JsonProperty("PROJECT_TYPE")
	private String project_type;
	@JsonProperty("PROPERTY_USAGE")
	private String property_usage;
	@JsonProperty("PLOT_VALUE")
	private String plot_value;
	@JsonProperty("AGREEMENT_VALUE")
	private String agreement_value;
	@JsonProperty("TOTAL_DOCUMENT_VALUE")
	private String total_document_value;
	@JsonProperty("SALE_DEED_VALUE")
	private String sale_deed_value;
	@JsonProperty("PURCHASE_VALUE")
	private String purchase_value;
	@JsonProperty("OTHER_CHARGE")
	private String other_charge;
	@JsonProperty("CUSTOMER_DECLARED_VALUE")
	private String customer_declared_value;
	@JsonProperty("COLLATERAL_TYPE")
	private String collateral_type;
	@JsonProperty("PROPERTY_STATUS")
	private String property_status;
	@JsonProperty("VALUATION_FEEDBACK")
	private String valuation_feedback;
	@JsonProperty("TOTAL_MARKET_VALUE")
	TotalMarketValue total_market_value;
	@JsonProperty("MARKET_VALUE1")
	private String market_value1;
	@JsonProperty("MARKET_VALUE2")
	private String market_value2;
	@JsonProperty("STAMP_DUTY")
	private String stamp_duty;
	@JsonProperty("REGISTRATION_CHARGES")
	private String registration_charges;
	@JsonProperty("AMENITIES_1")
	private String amenities_1;
	@JsonProperty("AMENITIES_2")
	private String amenities_2;
	@JsonProperty("AMENITIES_3")
	private String amenities_3;
	@JsonProperty("ASSET_USAGE")
	private String asset_usage;
	@JsonProperty("CONSIDERABLE_MV")
	private String considerable_mv;
	@JsonProperty("TOTAL_DV")
	private String total_dv;
	@JsonProperty("LTV_AS_PER_NHB")
	private String ltv_as_per_nhb;
	@JsonProperty("LTV_AS_PER_POLICY_ON_DV")
	private String ltv_as_per_policy_on_dv;
	@JsonProperty("LTV_AS_PER_POLICY_ON_MV")
	private String ltv_as_per_policy_on_mv;
	@JsonProperty("LTV_ON_AV_AS_PER_SCHEME")
	private String ltv_on_av_as_per_scheme;
	@JsonProperty("LTV_ON_DV_AS_PER_SCHEME")
	private String ltv_on_dv_as_per_scheme;
	@JsonProperty("LTV_ON_MV_AS_PER_SCHEME")
	private String ltv_on_mv_as_per_scheme;
	@JsonProperty("LTV_ON_AV_PLUS_GST_AS_PER_SCHEME")
	private String ltv_on_av_plus_gst_as_per_scheme;
	@JsonProperty("TYPE_OF_COLLATERAL")
	private String type_of_collateral;
	@JsonProperty("PROPERTY_SUBTYPE")
	private String property_subtype;
	@JsonProperty("GST_CHARGES_CUSTOMER_FUNDED")
	private String gst_charges_customer_funded;
	@JsonProperty("GST_CHARGES_PCHFL_FUNDED")
	private String gst_charges_pchfl_funded;

	//Address Details
	@JsonProperty("ADDRESS_TYPE")
	private String addressType;
	@JsonProperty("STATE")
	private String state;
	@JsonProperty("CITY")
	private String city;
	@JsonProperty("CITY_CATEGORY")
	private String cityCategory;
	@JsonProperty("PINCODE")
	private String pincode;
	@JsonProperty("COUNTRY")
	private String country;
	@JsonProperty("ADDRESS_LINE1")
	private String addressLine1;
	@JsonProperty("ADDRESS_LINE2")
	private String addressLine2;
	@JsonProperty("ADDRESS_LINE3")
	private String addressLine3;
	
	@JsonProperty("ACCOMODATION_TYPE")
	private String accomodationType;
	@JsonProperty("STABILITY_YEARS")
	private String stabilityYears;
	@JsonProperty("STABILITY_MONTHS")
	private String stabilityMonths;
	@JsonProperty("ELIGIBLE_COUNTRY")
	private String eligibleCountry;
	@JsonProperty("ADDRESS_DUMMY1")
	private String addressDummy1;
	@JsonProperty("ADDRESS_DUMMY2")
	private String addressDummy2;
	@JsonProperty("ADDRESS_DUMMY3")
	private String addressDummy3;
	@JsonProperty("ADDRESS_DUMMY4")
	private String addressDummy4;

	@JsonProperty("ADDRESS_CATEGORY")
	private String addressCategory;
	@JsonProperty("RESIDENCE_CODE")
	private String residenceCode;
	@JsonProperty("ADDRESS_LINE4")
	private String addressLine4;
	@JsonProperty("ADDRESS_LINE5")
	private String addressLine5;
	@JsonProperty("STATE_CODE")
	private String stateCode;

	//Account Details
	@JsonProperty("BUREAU_TENURE")
	private String bureauTenure;
	@JsonProperty("ACCOUNT_TYPE")
	private String accountType;
	@JsonProperty("ACCOUNT_NUMBER")
	private String accountNumber;
	@JsonProperty("AMOUNT_OVERDUE")
	private String amountOverdue;
	@JsonProperty("CURRENT_BALANCE")
	private String currentBalance;
	@JsonProperty("DATE_CLOSED")
	private String dateClosed;
	@JsonProperty("DATE_OF_LAST_PAYMENT")
	private String dateOfLastPayment;
	@JsonProperty("DATE_OPENED_DISBURSED")
	private String dateOpenedDisbursed;
	@JsonProperty("DATE_REPORTED")
	private String dateReported;
	@JsonProperty("EMI_AMOUNT")
	private String emiAmount;
	@JsonProperty("HIGH_CREDIT_SANCTIONED_AMOUNT")
	private String highCreditSanctionedAmount;
	@JsonProperty("OWNERSHIP_INDICATOR")
	private String ownershipIndicator;
	@JsonProperty("PAYMENT_FREQUENCY")
	private String paymentFrequency;
	@JsonProperty("PAYMENT_HISTORY1")
	private String paymentHistory1;
	@JsonProperty("PAYMENT_HISTORY2")
	private String paymentHistory2;
	@JsonProperty("PAYMENT_HISTORY_END_DATE")
	private String paymentHistoryEndDate;
	@JsonProperty("PAYMENT_HISTORY_START_DATE")
	private String paymentHistoryStartDate;
	@JsonProperty("RATE_OF_INTEREST")
	private String rateOfInterest;
	@JsonProperty("REPAYMENT_TENURE")
	private String repaymentTenure;
	@JsonProperty("SETTLEMENT_AMOUNT")
	private String settlementAmount;
	@JsonProperty("SUIT_FILED_STATUS")
	private String suitFiledStatus;
	@JsonProperty("TYPE_COLLATERAL")
	private String typeCollateral;
	@JsonProperty("VALUE_COLLATERAL")
	private String valueCollateral;
	@JsonProperty("WOF_PRINCIPAL")
	private String wofPrincipal;
	@JsonProperty("WOF_SETTLED_STATUS")
	private String wofSettledStatus;
	@JsonProperty("CREDIT_LIMIT")
	private String creditLimit;
	@JsonProperty("CASH_LIMIT")
	private String cashLimit;
	@JsonProperty("WOF_TOTAL_AMOUNT")
	private String wofTotalAmount;

	//Enquiry Detail		
	@JsonProperty("DATE_OF_ENQUIRY")
	private String dateOfEnquiry;
	@JsonProperty("ENQUIRY_PURPOSE")
	private String enquiryPurpose;
	@JsonProperty("ENQIURY_AMOUNT")
	private String enqiuryAmount;
	@JsonProperty("MEMBER_NAME")
	private String memberName;

	@JsonCreator
	public Element() {
	}

	public String getAddressCategory() {
		return addressCategory;
	}

	public void setAddressCategory(String addressCategory) {
		this.addressCategory = addressCategory;
	}

	public String getResidenceCode() {
		return residenceCode;
	}

	public void setResidenceCode(String residenceCode) {
		this.residenceCode = residenceCode;
	}

	

	public String getAddressLine4() {
		return addressLine4;
	}

	public void setAddressLine4(String addressLine4) {
		this.addressLine4 = addressLine4;
	}

	public String getAddressLine5() {
		return addressLine5;
	}

	public void setAddressLine5(String addressLine5) {
		this.addressLine5 = addressLine5;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	

	public String getDateOfEnquiry() {
		return dateOfEnquiry;
	}

	public void setDateOfEnquiry(String dateOfEnquiry) {
		this.dateOfEnquiry = dateOfEnquiry;
	}

	public String getEnquiryPurpose() {
		return enquiryPurpose;
	}

	public void setEnquiryPurpose(String enquiryPurpose) {
		this.enquiryPurpose = enquiryPurpose;
	}

	public String getEnqiuryAmount() {
		return enqiuryAmount;
	}

	public void setEnqiuryAmount(String enqiuryAmount) {
		this.enqiuryAmount = enqiuryAmount;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getBureauTenure() {
		return bureauTenure;
	}

	public void setBureauTenure(String bureauTenure) {
		this.bureauTenure = bureauTenure;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAmountOverdue() {
		return amountOverdue;
	}

	public void setAmountOverdue(String amountOverdue) {
		this.amountOverdue = amountOverdue;
	}

	public String getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(String currentBalance) {
		this.currentBalance = currentBalance;
	}

	public String getDateClosed() {
		return dateClosed;
	}

	public void setDateClosed(String dateClosed) {
		this.dateClosed = dateClosed;
	}

	public String getDateOfLastPayment() {
		return dateOfLastPayment;
	}

	public void setDateOfLastPayment(String dateOfLastPayment) {
		this.dateOfLastPayment = dateOfLastPayment;
	}

	public String getDateOpenedDisbursed() {
		return dateOpenedDisbursed;
	}

	public void setDateOpenedDisbursed(String dateOpenedDisbursed) {
		this.dateOpenedDisbursed = dateOpenedDisbursed;
	}

	public String getDateReported() {
		return dateReported;
	}

	public void setDateReported(String dateReported) {
		this.dateReported = dateReported;
	}

	public String getEmiAmount() {
		return emiAmount;
	}

	public void setEmiAmount(String emiAmount) {
		this.emiAmount = emiAmount;
	}

	public String getHighCreditSanctionedAmount() {
		return highCreditSanctionedAmount;
	}

	public void setHighCreditSanctionedAmount(String highCreditSanctionedAmount) {
		this.highCreditSanctionedAmount = highCreditSanctionedAmount;
	}

	public String getOwnershipIndicator() {
		return ownershipIndicator;
	}

	public void setOwnershipIndicator(String ownershipIndicator) {
		this.ownershipIndicator = ownershipIndicator;
	}

	public String getPaymentFrequency() {
		return paymentFrequency;
	}

	public void setPaymentFrequency(String paymentFrequency) {
		this.paymentFrequency = paymentFrequency;
	}

	public String getPaymentHistory1() {
		return paymentHistory1;
	}

	public void setPaymentHistory1(String paymentHistory1) {
		this.paymentHistory1 = paymentHistory1;
	}

	public String getPaymentHistory2() {
		return paymentHistory2;
	}

	public void setPaymentHistory2(String paymentHistory2) {
		this.paymentHistory2 = paymentHistory2;
	}

	public String getPaymentHistoryEndDate() {
		return paymentHistoryEndDate;
	}

	public void setPaymentHistoryEndDate(String paymentHistoryEndDate) {
		this.paymentHistoryEndDate = paymentHistoryEndDate;
	}

	public String getPaymentHistoryStartDate() {
		return paymentHistoryStartDate;
	}

	public void setPaymentHistoryStartDate(String paymentHistoryStartDate) {
		this.paymentHistoryStartDate = paymentHistoryStartDate;
	}

	public String getRateOfInterest() {
		return rateOfInterest;
	}

	public void setRateOfInterest(String rateOfInterest) {
		this.rateOfInterest = rateOfInterest;
	}

	public String getRepaymentTenure() {
		return repaymentTenure;
	}

	public void setRepaymentTenure(String repaymentTenure) {
		this.repaymentTenure = repaymentTenure;
	}

	public String getSettlementAmount() {
		return settlementAmount;
	}

	public void setSettlementAmount(String settlementAmount) {
		this.settlementAmount = settlementAmount;
	}

	public String getSuitFiledStatus() {
		return suitFiledStatus;
	}

	public void setSuitFiledStatus(String suitFiledStatus) {
		this.suitFiledStatus = suitFiledStatus;
	}

	public String getTypeCollateral() {
		return typeCollateral;
	}

	public void setTypeCollateral(String typeCollateral) {
		this.typeCollateral = typeCollateral;
	}

	public String getValueCollateral() {
		return valueCollateral;
	}

	public void setValueCollateral(String valueCollateral) {
		this.valueCollateral = valueCollateral;
	}

	public String getWofPrincipal() {
		return wofPrincipal;
	}

	public void setWofPrincipal(String wofPrincipal) {
		this.wofPrincipal = wofPrincipal;
	}

	public String getWofSettledStatus() {
		return wofSettledStatus;
	}

	public void setWofSettledStatus(String wofSettledStatus) {
		this.wofSettledStatus = wofSettledStatus;
	}

	public String getCreditLimit() {
		return creditLimit;
	}

	public void setCreditLimit(String creditLimit) {
		this.creditLimit = creditLimit;
	}

	public String getCashLimit() {
		return cashLimit;
	}

	public void setCashLimit(String cashLimit) {
		this.cashLimit = cashLimit;
	}

	public String getWofTotalAmount() {
		return wofTotalAmount;
	}

	public void setWofTotalAmount(String wofTotalAmount) {
		this.wofTotalAmount = wofTotalAmount;
	}

	

	public String getAccomodationType() {
		return accomodationType;
	}

	public void setAccomodationType(String accomodationType) {
		this.accomodationType = accomodationType;
	}

	public String getStabilityYears() {
		return stabilityYears;
	}

	public void setStabilityYears(String stabilityYears) {
		this.stabilityYears = stabilityYears;
	}

	public String getStabilityMonths() {
		return stabilityMonths;
	}

	public void setStabilityMonths(String stabilityMonths) {
		this.stabilityMonths = stabilityMonths;
	}

	public String getEligibleCountry() {
		return eligibleCountry;
	}

	public void setEligibleCountry(String eligibleCountry) {
		this.eligibleCountry = eligibleCountry;
	}

	public String getAddressDummy1() {
		return addressDummy1;
	}

	public void setAddressDummy1(String addressDummy1) {
		this.addressDummy1 = addressDummy1;
	}

	public String getAddressDummy2() {
		return addressDummy2;
	}

	public void setAddressDummy2(String addressDummy2) {
		this.addressDummy2 = addressDummy2;
	}

	public String getAddressDummy3() {
		return addressDummy3;
	}

	public void setAddressDummy3(String addressDummy3) {
		this.addressDummy3 = addressDummy3;
	}

	public String getAddressDummy4() {
		return addressDummy4;
	}

	public void setAddressDummy4(String addressDummy4) {
		this.addressDummy4 = addressDummy4;
	}

	public String getCollateral_id() {
		return collateral_id;
	}

	public String getProperty_identified() {
		return property_identified;
	}

	public String getCost_of_property() {
		return cost_of_property;
	}

	public String getProperty_type() {
		return property_type;
	}

	public String getNegative_area_flag() {
		return negative_area_flag;
	}

	public String getNegative_property() {
		return negative_property;
	}

	public String getProperty_ogl() {
		return property_ogl;
	}

	public String getProperty_age() {
		return property_age;
	}

	public String getResidual_age() {
		return residual_age;
	}

	public String getProject_type() {
		return project_type;
	}

	public String getProperty_usage() {
		return property_usage;
	}

	public String getPlot_value() {
		return plot_value;
	}

	public String getAgreement_value() {
		return agreement_value;
	}

	public String getTotal_document_value() {
		return total_document_value;
	}

	public String getSale_deed_value() {
		return sale_deed_value;
	}

	public String getPurchase_value() {
		return purchase_value;
	}

	public String getOther_charge() {
		return other_charge;
	}

	public String getCustomer_declared_value() {
		return customer_declared_value;
	}

	public String getCollateral_type() {
		return collateral_type;
	}

	public String getProperty_status() {
		return property_status;
	}

	public String getValuation_feedback() {
		return valuation_feedback;
	}

	public TotalMarketValue getTotal_market_value() {
		return total_market_value;
	}

	public String getMarket_value1() {
		return market_value1;
	}

	public String getMarket_value2() {
		return market_value2;
	}

	public String getStamp_duty() {
		return stamp_duty;
	}

	public String getRegistration_charges() {
		return registration_charges;
	}

	public String getAmenities_1() {
		return amenities_1;
	}

	public String getAmenities_2() {
		return amenities_2;
	}

	public String getAmenities_3() {
		return amenities_3;
	}

	public String getAsset_usage() {
		return asset_usage;
	}

	public String getConsiderable_mv() {
		return considerable_mv;
	}

	public String getTotal_dv() {
		return total_dv;
	}

	public String getLtv_as_per_nhb() {
		return ltv_as_per_nhb;
	}

	public String getLtv_as_per_policy_on_dv() {
		return ltv_as_per_policy_on_dv;
	}

	public String getLtv_as_per_policy_on_mv() {
		return ltv_as_per_policy_on_mv;
	}

	public String getLtv_on_av_as_per_scheme() {
		return ltv_on_av_as_per_scheme;
	}

	public String getLtv_on_dv_as_per_scheme() {
		return ltv_on_dv_as_per_scheme;
	}

	public String getLtv_on_mv_as_per_scheme() {
		return ltv_on_mv_as_per_scheme;
	}

	public String getLtv_on_av_plus_gst_as_per_scheme() {
		return ltv_on_av_plus_gst_as_per_scheme;
	}

	public void setLtv_on_av_plus_gst_as_per_scheme(String ltv_on_av_plus_gst_as_per_scheme) {
		this.ltv_on_av_plus_gst_as_per_scheme = ltv_on_av_plus_gst_as_per_scheme;
	}

	public String getType_of_collateral() {
		return type_of_collateral;
	}

	public String getProperty_subtype() {
		return property_subtype;
	}

	public String getGst_charges_customer_funded() {
		return gst_charges_customer_funded;
	}

	public String getGst_charges_pchfl_funded() {
		return gst_charges_pchfl_funded;
	}

	// Setter Methods 

	public void setCollateral_id(String collateral_id) {
		this.collateral_id = collateral_id;
	}

	public void setProperty_identified(String property_identified) {
		this.property_identified = property_identified;
	}

	public void setCost_of_property(String cost_of_property) {
		this.cost_of_property = cost_of_property;
	}

	public void setProperty_type(String property_type) {
		this.property_type = property_type;
	}

	public void setNegative_area_flag(String negative_area_flag) {
		this.negative_area_flag = negative_area_flag;
	}

	public void setNegative_property(String negative_property) {
		this.negative_property = negative_property;
	}

	public void setProperty_ogl(String property_ogl) {
		this.property_ogl = property_ogl;
	}

	public void setProperty_age(String property_age) {
		this.property_age = property_age;
	}

	public void setResidual_age(String residual_age) {
		this.residual_age = residual_age;
	}

	public void setProject_type(String project_type) {
		this.project_type = project_type;
	}

	public void setProperty_usage(String property_usage) {
		this.property_usage = property_usage;
	}

	public void setPlot_value(String plot_value) {
		this.plot_value = plot_value;
	}

	public void setAgreement_value(String agreement_value) {
		this.agreement_value = agreement_value;
	}

	public void setTotal_document_value(String total_document_value) {
		this.total_document_value = total_document_value;
	}

	public void setSale_deed_value(String sale_deed_value) {
		this.sale_deed_value = sale_deed_value;
	}

	public void setPurchase_value(String purchase_value) {
		this.purchase_value = purchase_value;
	}

	public void setOther_charge(String other_charge) {
		this.other_charge = other_charge;
	}

	public void setCustomer_declared_value(String customer_declared_value) {
		this.customer_declared_value = customer_declared_value;
	}

	public void setCollateral_type(String collateral_type) {
		this.collateral_type = collateral_type;
	}

	public void setProperty_status(String property_status) {
		this.property_status = property_status;
	}

	public void setValuation_feedback(String valuation_feedback) {
		this.valuation_feedback = valuation_feedback;
	}

	public void setTotal_market_value(TotalMarketValue total_market_value) {
		this.total_market_value = total_market_value;
	}

	public void setMarket_value1(String market_value1) {
		this.market_value1 = market_value1;
	}

	public void setMarket_value2(String market_value2) {
		this.market_value2 = market_value2;
	}

	public void setStamp_duty(String stamp_duty) {
		this.stamp_duty = stamp_duty;
	}

	public void setRegistration_charges(String registration_charges) {
		this.registration_charges = registration_charges;
	}

	public void setAmenities_1(String amenities_1) {
		this.amenities_1 = amenities_1;
	}

	public void setAmenities_2(String amenities_2) {
		this.amenities_2 = amenities_2;
	}

	public void setAmenities_3(String amenities_3) {
		this.amenities_3 = amenities_3;
	}

	public void setAsset_usage(String asset_usage) {
		this.asset_usage = asset_usage;
	}

	public void setConsiderable_mv(String considerable_mv) {
		this.considerable_mv = considerable_mv;
	}

	public void setTotal_dv(String total_dv) {
		this.total_dv = total_dv;
	}

	public void setLtv_as_per_nhb(String ltv_as_per_nhb) {
		this.ltv_as_per_nhb = ltv_as_per_nhb;
	}

	public void setLtv_as_per_policy_on_dv(String ltv_as_per_policy_on_dv) {
		this.ltv_as_per_policy_on_dv = ltv_as_per_policy_on_dv;
	}

	public void setLtv_as_per_policy_on_mv(String ltv_as_per_policy_on_mv) {
		this.ltv_as_per_policy_on_mv = ltv_as_per_policy_on_mv;
	}

	public void setLtv_on_av_as_per_scheme(String ltv_on_av_as_per_scheme) {
		this.ltv_on_av_as_per_scheme = ltv_on_av_as_per_scheme;
	}

	public void setLtv_on_dv_as_per_scheme(String ltv_on_dv_as_per_scheme) {
		this.ltv_on_dv_as_per_scheme = ltv_on_dv_as_per_scheme;
	}

	public void setLtv_on_mv_as_per_scheme(String ltv_on_mv_as_per_scheme) {
		this.ltv_on_mv_as_per_scheme = ltv_on_mv_as_per_scheme;
	}

	public void setType_of_collateral(String type_of_collateral) {
		this.type_of_collateral = type_of_collateral;
	}

	public void setProperty_subtype(String property_subtype) {
		this.property_subtype = property_subtype;
	}

	public void setGst_charges_customer_funded(String gst_charges_customer_funded) {
		this.gst_charges_customer_funded = gst_charges_customer_funded;
	}

	public void setGst_charges_pchfl_funded(String gst_charges_pchfl_funded) {
		this.gst_charges_pchfl_funded = gst_charges_pchfl_funded;
	}

	// Getter Methods 

	public String getPd_type_flag() {
		return pd_type_flag;
	}

	// Setter Methods 

	public void setPd_type_flag(String pd_type_flag) {
		this.pd_type_flag = pd_type_flag;
	}
	
	// Getter Methods 

	public String getFcu_status() {
		return fcu_status;
	}

	public void setFcu_status(String fcu_status) {
		this.fcu_status = fcu_status;
	}

	public String getStatus() {
		return status;
	}

	public String getDv_reason_code() {
		return dv_reason_code;
	}

	public String getVerification_type() {
		return verification_type;
	}

	public String getDocument_code() {
		return document_code;
	}

	public String getDv_dummy1() {
		return dv_dummy1;
	}

	public String getDv_dummy2() {
		return dv_dummy2;
	}

	// Setter Methods 

	public void setStatus(String status) {
		this.status = status;
	}

	public void setDv_reason_code(String dv_reason_code) {
		this.dv_reason_code = dv_reason_code;
	}

	public void setVerification_type(String verification_type) {
		this.verification_type = verification_type;
	}

	public void setDocument_code(String document_code) {
		this.document_code = document_code;
	}

	public void setDv_dummy1(String dv_dummy1) {
		this.dv_dummy1 = dv_dummy1;
	}

	public void setDv_dummy2(String dv_dummy2) {
		this.dv_dummy2 = dv_dummy2;
	}
	

	public String getAddressType() {
		return addressType;
	}

	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCityCategory() {
		return cityCategory;
	}

	public void setCityCategory(String cityCategory) {
		this.cityCategory = cityCategory;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getAddressLine3() {
		return addressLine3;
	}

	public void setAddressLine3(String addressLine3) {
		this.addressLine3 = addressLine3;
	}
}
