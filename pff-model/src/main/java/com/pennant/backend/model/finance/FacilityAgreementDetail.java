package com.pennant.backend.model.finance;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class FacilityAgreementDetail {
	private String amendment;
	private String annualReview;
	private String availmentNew;
	private String date;
	private String cafRef;
	private String custCode;
	private String custNumber;
	private String custName;
	private String countryOfDomicile;
	private String countryOfRisk;
	private String establishedDate;
	private String natureOfBusiness;
	private String natureOfBusinessCode;
	private String sicCode;
	private String customerRiskType;
	private String connectedCustomer;
	private String relatedCustomer;
	private String deadline;
	private String nextReviewDate;
	private String relationshipManager;
	private String countryManager;
	private String levelofApprovalRequired;
	private String countryLimitsAdequacy;
	private String reviewCenter;
	private String relationshipSince;
	private String custGroupCode;
	private String custGroupName;

	private String customerBackGround;// HTML
	private String strength;// HTML
	private String weaknesses;// HTML
	private String sourceOfRepayment;// HTML
	private String adequacyOfCashFlows;// HTML
	private String typesOfSecurities;// HTML
	private String guaranteeDescription;// HTML
	private String financialSummary;// HTML
	private String mitigants;// HTML

	private String purpose;// HTML
	private String accountRelation;// HTML
	private String antiMoneyLaunderClear;
	private String limitAndAncillary;// HTML
	private String antiMoneyLaunderSection;// HTML
	private String interim;
	private String customerType;

	// facility term sheet
	private String totFacilityccy;
	private String totFacilityAmt;
	private String totFacilityAmtinUSD;
	private String totExposure;
	private String totExsisting;

	private String proposedFinalTakeccy;
	private String proposedFinalTakeAmt;
	private String proposedFinalTakeAmtinUSD;

	private String finalMaturityDate;
	private String totalTenor;
	private String averageLife;
	private String annualFeesCommission;
	private String otherFees;

	// Risk rating
	private String totalScoring;
	private String proposedGrade;
	private String proposedDesc;

	private String AIBObligator;
	private String AIBCountry;
	private String AIBObligatorDesc;
	private String AIBCountryDesc;

	private String CIObligator;
	private String FITCHObligator;
	private String MOODYObligator;
	private String SNPObligator;

	private String CICountry;
	private String FITCHCountry;
	private String MOODYCountry;
	private String SNPCountry;

	private String riskLimit;
	private String riskExposure;

	private String purposeOfSubmission;

	private String totSharePerc;
	// Collateral
	private String totValue;
	private String totCover;
	// ===
	private String applicationDate;
	private String totCustFin;
	private String adtYear1 = "";
	private String adtYear2 = "";
	private String adtYear3 = "";

	public FacilityAgreementDetail() {
	    super();
	}

	public String getAmendment() {
		return StringUtils.trimToEmpty(amendment);
	}

	public void setAmendment(String amendment) {
		this.amendment = amendment;
	}

	public String getAnnualReview() {
		return StringUtils.trimToEmpty(annualReview);
	}

	public void setAnnualReview(String annualReview) {
		this.annualReview = annualReview;
	}

	public String getAvailmentNew() {
		return StringUtils.trimToEmpty(availmentNew);
	}

	public void setAvailmentNew(String availmentNew) {
		this.availmentNew = availmentNew;
	}

	public String getDate() {
		return StringUtils.trimToEmpty(date);
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getCafRef() {
		return StringUtils.trimToEmpty(cafRef);
	}

	public void setCafRef(String cafRef) {
		this.cafRef = cafRef;
	}

	public String getCustCode() {
		return StringUtils.trimToEmpty(custCode);
	}

	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}

	public String getCustNumber() {
		return StringUtils.trimToEmpty(custNumber);
	}

	public void setCustNumber(String custNumber) {
		this.custNumber = custNumber;
	}

	public String getCustName() {
		return StringUtils.trimToEmpty(custName);
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getCountryOfDomicile() {
		return StringUtils.trimToEmpty(countryOfDomicile);
	}

	public void setCountryOfDomicile(String countryOfDomicile) {
		this.countryOfDomicile = countryOfDomicile;
	}

	public String getCountryOfRisk() {
		return StringUtils.trimToEmpty(countryOfRisk);
	}

	public void setCountryOfRisk(String countryOfRisk) {
		this.countryOfRisk = countryOfRisk;
	}

	public String getEstablishedDate() {
		return StringUtils.trimToEmpty(establishedDate);
	}

	public void setEstablishedDate(String establishedDate) {
		this.establishedDate = establishedDate;
	}

	public String getNatureOfBusiness() {
		return StringUtils.trimToEmpty(natureOfBusiness);
	}

	public void setNatureOfBusiness(String natureOfBusiness) {
		this.natureOfBusiness = natureOfBusiness;
	}

	public String getNatureOfBusinessCode() {
		return StringUtils.trimToEmpty(natureOfBusinessCode);
	}

	public void setNatureOfBusinessCode(String natureOfBusinessCode) {
		this.natureOfBusinessCode = natureOfBusinessCode;
	}

	public String getSicCode() {
		return StringUtils.trimToEmpty(sicCode);
	}

	public void setSicCode(String sicCode) {
		this.sicCode = sicCode;
	}

	public String getCustomerRiskType() {
		return StringUtils.trimToEmpty(customerRiskType);
	}

	public void setCustomerRiskType(String customerRiskType) {
		this.customerRiskType = customerRiskType;
	}

	public String getConnectedCustomer() {
		return StringUtils.trimToEmpty(connectedCustomer);
	}

	public void setConnectedCustomer(String connectedCustomer) {
		this.connectedCustomer = connectedCustomer;
	}

	public String getRelatedCustomer() {
		return StringUtils.trimToEmpty(relatedCustomer);
	}

	public void setRelatedCustomer(String relatedCustomer) {
		this.relatedCustomer = relatedCustomer;
	}

	public String getDeadline() {
		return StringUtils.trimToEmpty(deadline);
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	public String getNextReviewDate() {
		return StringUtils.trimToEmpty(nextReviewDate);
	}

	public void setNextReviewDate(String nextReviewDate) {
		this.nextReviewDate = nextReviewDate;
	}

	public String getRelationshipManager() {
		return StringUtils.trimToEmpty(relationshipManager);
	}

	public void setRelationshipManager(String relationshipManager) {
		this.relationshipManager = relationshipManager;
	}

	public String getCountryManager() {
		return StringUtils.trimToEmpty(countryManager);
	}

	public void setCountryManager(String countryManager) {
		this.countryManager = countryManager;
	}

	public String getLevelofApprovalRequired() {
		return StringUtils.trimToEmpty(levelofApprovalRequired);
	}

	public void setLevelofApprovalRequired(String levelofApprovalRequired) {
		this.levelofApprovalRequired = levelofApprovalRequired;
	}

	public String getCountryLimitsAdequacy() {
		return StringUtils.trimToEmpty(countryLimitsAdequacy);
	}

	public void setCountryLimitsAdequacy(String countryLimitsAdequacy) {
		this.countryLimitsAdequacy = countryLimitsAdequacy;
	}

	public String getReviewCenter() {
		return StringUtils.trimToEmpty(reviewCenter);
	}

	public void setReviewCenter(String reviewCenter) {
		this.reviewCenter = reviewCenter;
	}

	public String getRelationshipSince() {
		return StringUtils.trimToEmpty(relationshipSince);
	}

	public void setRelationshipSince(String relationshipSince) {
		this.relationshipSince = relationshipSince;
	}

	public String getCustGroupCode() {
		return StringUtils.trimToEmpty(custGroupCode);
	}

	public void setCustGroupCode(String custGroupCode) {
		this.custGroupCode = custGroupCode;
	}

	public String getCustGroupName() {
		return StringUtils.trimToEmpty(custGroupName);
	}

	public void setCustGroupName(String custGroupName) {
		this.custGroupName = custGroupName;
	}

	public String getCustomerBackGround() {
		return customerBackGround;
	}

	public String getStrength() {
		return strength;
	}

	public String getWeaknesses() {
		return weaknesses;
	}

	public String getSourceOfRepayment() {
		return sourceOfRepayment;
	}

	public String getAdequacyOfCashFlows() {
		return adequacyOfCashFlows;
	}

	public String getTypesOfSecurities() {
		return typesOfSecurities;
	}

	public String getGuaranteeDescription() {
		return guaranteeDescription;
	}

	public String getFinancialSummary() {
		return financialSummary;
	}

	public String getMitigants() {
		return mitigants;
	}

	public void setMitigants(String mitigants) {
		this.mitigants = mitigants;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getInterim() {
		return interim;
	}

	public void setInterim(String interim) {
		this.interim = interim;
	}

	public String getAccountRelation() {
		return accountRelation;
	}

	public void setAccountRelation(String accountRelation) {
		this.accountRelation = accountRelation;
	}

	public String getAntiMoneyLaunderClear() {
		return antiMoneyLaunderClear;
	}

	public void setAntiMoneyLaunderClear(String antiMoneyLaunderClear) {
		this.antiMoneyLaunderClear = antiMoneyLaunderClear;
	}

	public String getLimitAndAncillary() {
		return limitAndAncillary;
	}

	public void setLimitAndAncillary(String limitAndAncillary) {
		this.limitAndAncillary = limitAndAncillary;
	}

	public String getAntiMoneyLaunderSection() {
		return antiMoneyLaunderSection;
	}

	public void setAntiMoneyLaunderSection(String antiMoneyLaunderSection) {
		this.antiMoneyLaunderSection = antiMoneyLaunderSection;
	}

	public String getCustomerType() {
		return StringUtils.trimToEmpty(customerType);
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public void setCustomerBackGround(String htmlCustomerBackGround) {
		this.customerBackGround = htmlCustomerBackGround;
	}

	public void setStrength(String htmlStrength) {
		this.strength = htmlStrength;
	}

	public void setWeaknesses(String htmlWeaknesses) {
		this.weaknesses = htmlWeaknesses;
	}

	public void setSourceOfRepayment(String htmlSourceOfRepayment) {
		this.sourceOfRepayment = htmlSourceOfRepayment;
	}

	public void setAdequacyOfCashFlows(String htmlAdequacyOfCashFlows) {
		this.adequacyOfCashFlows = htmlAdequacyOfCashFlows;
	}

	public void setTypesOfSecurities(String htmlTypesOfSecurities) {
		this.typesOfSecurities = htmlTypesOfSecurities;
	}

	public void setGuaranteeDescription(String htmlGuaranteeDescription) {
		this.guaranteeDescription = htmlGuaranteeDescription;
	}

	public void setFinancialSummary(String htmlFinancialSummary) {
		this.financialSummary = htmlFinancialSummary;
	}

	public String getTotFacilityccy() {
		return StringUtils.trimToEmpty(totFacilityccy);
	}

	public void setTotFacilityccy(String totFacilityccy) {
		this.totFacilityccy = totFacilityccy;
	}

	public String getTotFacilityAmt() {
		return StringUtils.trimToEmpty(totFacilityAmt);
	}

	public void setTotFacilityAmt(String totFacilityAmt) {
		this.totFacilityAmt = totFacilityAmt;
	}

	public String getTotFacilityAmtinUSD() {
		return StringUtils.trimToEmpty(totFacilityAmtinUSD);
	}

	public void setTotFacilityAmtinUSD(String totFacilityAmtinUSD) {
		this.totFacilityAmtinUSD = totFacilityAmtinUSD;
	}

	public String getTotExposure() {
		return StringUtils.trimToEmpty(totExposure);
	}

	public void setTotExposure(String totExposure) {
		this.totExposure = totExposure;
	}

	public String getTotExsisting() {
		return StringUtils.trimToEmpty(totExsisting);
	}

	public void setTotExsisting(String totExsisting) {
		this.totExsisting = totExsisting;
	}

	public String getProposedFinalTakeccy() {
		return StringUtils.trimToEmpty(proposedFinalTakeccy);
	}

	public void setProposedFinalTakeccy(String proposedFinalTakeccy) {
		this.proposedFinalTakeccy = proposedFinalTakeccy;
	}

	public String getProposedFinalTakeAmt() {
		return StringUtils.trimToEmpty(proposedFinalTakeAmt);
	}

	public void setProposedFinalTakeAmt(String proposedFinalTakeAmt) {
		this.proposedFinalTakeAmt = proposedFinalTakeAmt;
	}

	public String getProposedFinalTakeAmtinUSD() {
		return StringUtils.trimToEmpty(proposedFinalTakeAmtinUSD);
	}

	public void setProposedFinalTakeAmtinUSD(String proposedFinalTakeAmtinUSD) {
		this.proposedFinalTakeAmtinUSD = proposedFinalTakeAmtinUSD;
	}

	public String getFinalMaturityDate() {
		return StringUtils.trimToEmpty(finalMaturityDate);
	}

	public void setFinalMaturityDate(String finalMaturityDate) {
		this.finalMaturityDate = finalMaturityDate;
	}

	public String getTotalTenor() {
		return StringUtils.trimToEmpty(totalTenor);
	}

	public void setTotalTenor(String totalTenor) {
		this.totalTenor = totalTenor;
	}

	public String getAverageLife() {
		return StringUtils.trimToEmpty(averageLife);
	}

	public void setAverageLife(String averageLife) {
		this.averageLife = averageLife;
	}

	public String getAnnualFeesCommission() {
		return StringUtils.trimToEmpty(annualFeesCommission);
	}

	public void setAnnualFeesCommission(String annualFeesCommission) {
		this.annualFeesCommission = annualFeesCommission;
	}

	public String getOtherFees() {
		return StringUtils.trimToEmpty(otherFees);
	}

	public void setOtherFees(String otherFees) {
		this.otherFees = otherFees;
	}

	public String getApplicationDate() {
		return StringUtils.trimToEmpty(applicationDate);
	}

	public void setApplicationDate(String applicationDate) {
		this.applicationDate = applicationDate;
	}

	public String getTotCustFin() {
		return StringUtils.trimToEmpty(totCustFin);
	}

	public void setTotCustFin(String totCustFin) {
		this.totCustFin = totCustFin;
	}

	public String getAdtYear1() {
		return StringUtils.trimToEmpty(adtYear1);
	}

	public void setAdtYear1(String adtYear1) {
		this.adtYear1 = adtYear1;
	}

	public String getAdtYear2() {
		return StringUtils.trimToEmpty(adtYear2);
	}

	public void setAdtYear2(String adtYear2) {
		this.adtYear2 = adtYear2;
	}

	public String getAdtYear3() {
		return StringUtils.trimToEmpty(adtYear3);
	}

	public void setAdtYear3(String adtYear3) {
		this.adtYear3 = adtYear3;
	}

	public String getTotalScoring() {
		return StringUtils.trimToEmpty(totalScoring);
	}

	public void setTotalScoring(String totalScoring) {
		this.totalScoring = totalScoring;
	}

	public String getProposedGrade() {
		return StringUtils.trimToEmpty(proposedGrade);
	}

	public void setProposedGrade(String proposedGrade) {
		this.proposedGrade = proposedGrade;
	}

	public String getProposedDesc() {
		return StringUtils.trimToEmpty(proposedDesc);
	}

	public void setProposedDesc(String proposedDesc) {
		this.proposedDesc = proposedDesc;
	}

	public String getAIBObligator() {
		return StringUtils.trimToEmpty(AIBObligator);
	}

	public void setAIBObligator(String aIBObligator) {
		AIBObligator = aIBObligator;
	}

	public String getAIBCountry() {
		return StringUtils.trimToEmpty(AIBCountry);
	}

	public void setAIBCountry(String aIBCountry) {
		AIBCountry = aIBCountry;
	}

	public String getAIBObligatorDesc() {
		return StringUtils.trimToEmpty(AIBObligatorDesc);
	}

	public void setAIBObligatorDesc(String aIBObligatorDesc) {
		AIBObligatorDesc = aIBObligatorDesc;
	}

	public String getAIBCountryDesc() {
		return StringUtils.trimToEmpty(AIBCountryDesc);
	}

	public void setAIBCountryDesc(String aIBCountryDesc) {
		AIBCountryDesc = aIBCountryDesc;
	}

	public String getCIObligator() {
		return StringUtils.trimToEmpty(CIObligator);
	}

	public void setCIObligator(String cIObligator) {
		CIObligator = cIObligator;
	}

	public String getFITCHObligator() {
		return StringUtils.trimToEmpty(FITCHObligator);
	}

	public void setFITCHObligator(String fINCHObligator) {
		FITCHObligator = fINCHObligator;
	}

	public String getMOODYObligator() {
		return StringUtils.trimToEmpty(MOODYObligator);
	}

	public void setMOODYObligator(String mOODYObligator) {
		MOODYObligator = mOODYObligator;
	}

	public String getSNPObligator() {
		return StringUtils.trimToEmpty(SNPObligator);
	}

	public void setSNPObligator(String sNPObligator) {
		SNPObligator = sNPObligator;
	}

	public String getCICountry() {
		return StringUtils.trimToEmpty(CICountry);
	}

	public void setCICountry(String cICountry) {
		CICountry = cICountry;
	}

	public String getFITCHCountry() {
		return StringUtils.trimToEmpty(FITCHCountry);
	}

	public void setFITCHCountry(String fINCHCountry) {
		FITCHCountry = fINCHCountry;
	}

	public String getMOODYCountry() {
		return StringUtils.trimToEmpty(MOODYCountry);
	}

	public void setMOODYCountry(String mOODYCountry) {
		MOODYCountry = mOODYCountry;
	}

	public String getSNPCountry() {
		return StringUtils.trimToEmpty(SNPCountry);
	}

	public void setSNPCountry(String sNPCountry) {
		SNPCountry = sNPCountry;
	}

	public String getRiskLimit() {
		return StringUtils.trimToEmpty(riskLimit);
	}

	public void setRiskLimit(String riskLimit) {
		this.riskLimit = riskLimit;
	}

	public String getRiskExposure() {
		return StringUtils.trimToEmpty(riskExposure);
	}

	public void setRiskExposure(String riskExposure) {
		this.riskExposure = riskExposure;
	}

	public String getPurposeOfSubmission() {
		return StringUtils.trimToEmpty(purposeOfSubmission);
	}

	public void setPurposeOfSubmission(String purposeOfSubmission) {
		this.purposeOfSubmission = purposeOfSubmission;
	}

	public String getTotSharePerc() {
		return StringUtils.trimToEmpty(totSharePerc);
	}

	public void setTotSharePerc(String totSharePerc) {
		this.totSharePerc = totSharePerc;
	}

	public String getTotValue() {
		return StringUtils.trimToEmpty(totValue);
	}

	public String getTotCover() {
		return StringUtils.trimToEmpty(totCover);
	}

	public void setTotValue(String totValue) {
		this.totValue = totValue;
	}

	public void setTotCover(String totCover) {
		this.totCover = totCover;
	}

	// Html Fileds

	public String getHtmlCustomerBackGround() {
		return StringUtils.trimToEmpty(customerBackGround);
	}

	public String getHtmlStrength() {
		return StringUtils.trimToEmpty(strength);
	}

	public String getHtmlWeaknesses() {
		return StringUtils.trimToEmpty(weaknesses);
	}

	public String getHtmlSourceOfRepayment() {
		return StringUtils.trimToEmpty(sourceOfRepayment);
	}

	public String getHtmlAdequacyOfCashFlows() {
		return StringUtils.trimToEmpty(adequacyOfCashFlows);
	}

	public String getHtmlTypesOfSecurities() {
		return StringUtils.trimToEmpty(typesOfSecurities);
	}

	public String getHtmlGuaranteeDescription() {
		return StringUtils.trimToEmpty(guaranteeDescription);
	}

	public String getHtmlFinancialSummary() {
		return StringUtils.trimToEmpty(financialSummary);
	}

	public String getHtmlMitigants() {
		return StringUtils.trimToEmpty(mitigants);
	}

	public String getHtmlPurpose() {
		return StringUtils.trimToEmpty(purpose);
	}

	public String getHtmlAccountRelation() {
		return StringUtils.trimToEmpty(accountRelation);
	}

	public String getHtmllimitAndAncillary() {
		return StringUtils.trimToEmpty(limitAndAncillary);
	}

	public String getHtmlantiMoneyLaunderSection() {
		return StringUtils.trimToEmpty(antiMoneyLaunderSection);
	}

	// These Classes Are Declared as Inner Class
	private List<CheckListDetails> checkListDetails;
	private List<CustomerFinance> customerFinances;

	private List<CustomerCreditReview> creditReviewsBalance;

	private List<ScoringHeader> finScoringHeaderDetails;
	private List<ScoringHeader> nonFinScoringHeaderDetails;
	private List<Recommendation> recommendations;
	private List<ExceptionList> exceptionLists;
	private List<ProposedFacility> proposedFacilities;
	private List<Shareholder> shareholders;
	private List<FacilityCollateral> collaterals;
	private List<GroupRecommendation> groupRecommendations;

	public void setCheckListDetails(List<CheckListDetails> checkListDetails) {
		this.checkListDetails = checkListDetails;
	}

	public List<CheckListDetails> getCheckListDetails() {
		return checkListDetails;
	}

	public List<ScoringHeader> getFinScoringHeaderDetails() {
		return finScoringHeaderDetails;
	}

	public void setFinScoringHeaderDetails(List<ScoringHeader> finScoringHeaderDetails) {
		this.finScoringHeaderDetails = finScoringHeaderDetails;
	}

	public List<ScoringHeader> getNonFinScoringHeaderDetails() {
		return nonFinScoringHeaderDetails;
	}

	public void setNonFinScoringHeaderDetails(List<ScoringHeader> nonFinScoringHeaderDetails) {
		this.nonFinScoringHeaderDetails = nonFinScoringHeaderDetails;
	}

	public void setCreditReviewsBalance(List<CustomerCreditReview> creditReviewsBalance) {
		this.creditReviewsBalance = creditReviewsBalance;
	}

	public List<CustomerCreditReview> getCreditReviewsBalance() {
		return creditReviewsBalance;
	}

	public void setRecommendations(List<Recommendation> recommendations) {
		this.recommendations = recommendations;
	}

	public List<Recommendation> getRecommendations() {
		return recommendations;
	}

	public void setExceptionLists(List<ExceptionList> exceptionLists) {
		this.exceptionLists = exceptionLists;
	}

	public List<ExceptionList> getExceptionLists() {
		return exceptionLists;
	}

	public void setCustomerFinances(List<CustomerFinance> customerFinances) {
		this.customerFinances = customerFinances;
	}

	public List<CustomerFinance> getCustomerFinances() {
		return customerFinances;
	}

	public List<ProposedFacility> getProposedFacilities() {
		return proposedFacilities;
	}

	public void setProposedFacilities(List<ProposedFacility> proposedFacilities) {
		this.proposedFacilities = proposedFacilities;
	}

	public List<Shareholder> getShareholders() {
		return shareholders;
	}

	public void setShareholders(List<Shareholder> shareholders) {
		this.shareholders = shareholders;
	}

	public List<FacilityCollateral> getCollaterals() {
		return collaterals;
	}

	public void setCollaterals(List<FacilityCollateral> collaterals) {
		this.collaterals = collaterals;
	}

	public List<GroupRecommendation> getGroupRecommendations() {
		return groupRecommendations;
	}

	public void setGroupRecommendations(List<GroupRecommendation> groupRecommendations) {
		this.groupRecommendations = groupRecommendations;
	}

	// =================================================///
	// ============== Inner Classes====================///
	// =================================================///
	public class CheckListDetails {
		private long questionId;
		private String question;
		private List<CheckListAnsDetails> listquestionAns;

		public CheckListDetails() {
		    super();
		}

		public String getQuestion() {
			return StringUtils.trimToEmpty(question);
		}

		public void setQuestion(String question) {
			this.question = question;
		}

		public void setQuestionId(long questionId) {
			this.questionId = questionId;
		}

		public long getQuestionId() {
			return questionId;
		}

		public List<CheckListAnsDetails> getListquestionAns() {
			return listquestionAns;
		}

		public void setListquestionAns(List<CheckListAnsDetails> listquestionAns) {
			this.listquestionAns = listquestionAns;
		}

	}

	public class CheckListAnsDetails {
		private long questionId;
		private String questionAns;
		private String questionRem;

		public CheckListAnsDetails() {
		    super();
		}

		public void setQuestionId(long questionId) {
			this.questionId = questionId;
		}

		public long getQuestionId() {
			return questionId;
		}

		public String getQuestionAns() {
			return StringUtils.trimToEmpty(questionAns);
		}

		public void setQuestionAns(String questionAns) {
			this.questionAns = questionAns;
		}

		public String getQuestionRem() {
			return StringUtils.trimToEmpty(questionRem);
		}

		public void setQuestionRem(String questionRem) {
			this.questionRem = questionRem;
		}
	}

	public class CustomerFinance {
		private String dealDate;
		private String dealType;
		private String originalAmount;
		private String monthlyInstalment;
		private String outstandingBalance;

		public CustomerFinance() {
		    super();
		}

		public String getDealDate() {
			return StringUtils.trimToEmpty(dealDate);
		}

		public void setDealDate(String dealDate) {
			this.dealDate = dealDate;
		}

		public String getDealType() {
			return StringUtils.trimToEmpty(dealType);
		}

		public void setDealType(String dealType) {
			this.dealType = dealType;
		}

		public String getOriginalAmount() {
			return StringUtils.trimToEmpty(originalAmount);
		}

		public void setOriginalAmount(String originalAmount) {
			this.originalAmount = originalAmount;
		}

		public String getMonthlyInstalment() {
			return StringUtils.trimToEmpty(monthlyInstalment);
		}

		public void setMonthlyInstalment(String monthlyInstalment) {
			this.monthlyInstalment = monthlyInstalment;
		}

		public String getOutstandingBalance() {
			return StringUtils.trimToEmpty(outstandingBalance);
		}

		public void setOutstandingBalance(String outstandingBalance) {
			this.outstandingBalance = outstandingBalance;
		}
	}

	public class CustomerCreditReview {
		private String categoryName;
		private List<CustomerCreditReviewDetails> customerCreditReviewDetails = new ArrayList<CustomerCreditReviewDetails>();

		public CustomerCreditReview() {
		    super();
		}

		public String getCategoryName() {
			return StringUtils.trimToEmpty(categoryName);
		}

		public void setCategoryName(String categoryName) {
			this.categoryName = categoryName;
		}

		public List<CustomerCreditReviewDetails> getCustomerCreditReviewDetails() {
			return customerCreditReviewDetails;
		}

		public void setCustomerCreditReviewDetails(List<CustomerCreditReviewDetails> customerCreditReviewDetails) {
			this.customerCreditReviewDetails = customerCreditReviewDetails;
		}
	}

	public class CustomerCreditReviewDetails {
		private String subCategoryName;
		private String year1;
		private String year2;
		private String year3;

		public CustomerCreditReviewDetails() {
		    super();
		}

		public String getSubCategoryName() {
			return StringUtils.trimToEmpty(subCategoryName);
		}

		public void setSubCategoryName(String subCategoryName) {
			this.subCategoryName = subCategoryName;
		}

		public String getYear1() {
			return StringUtils.trimToEmpty(year1);
		}

		public void setYear1(String year1) {
			this.year1 = year1;
		}

		public String getYear2() {
			return StringUtils.trimToEmpty(year2);
		}

		public void setYear2(String year2) {
			this.year2 = year2;
		}

		public String getYear3() {
			return StringUtils.trimToEmpty(year3);
		}

		public void setYear3(String year3) {
			this.year3 = year3;
		}

	}

	// Scoring Details

	public class ScoringHeader {

		private String scoringGroup;
		private List<ScoringDetails> scoringDetails = new ArrayList<ScoringDetails>();

		public ScoringHeader() {
		    super();
		}

		public String getScoringGroup() {
			return StringUtils.trimToEmpty(scoringGroup);
		}

		public void setScoringGroup(String scoringGroup) {
			this.scoringGroup = scoringGroup;
		}

		public List<ScoringDetails> getScoringDetails() {
			return scoringDetails;
		}

		public void setScoringDetails(List<ScoringDetails> scoringDetails) {
			this.scoringDetails = scoringDetails;
		}
	}

	public class ScoringDetails {

		private String scoringMetric;
		private String scoringDesc;
		private String metricMaxScore;
		private String calcScore;

		public ScoringDetails() {
		    super();
		}

		public String getScoringMetric() {
			return StringUtils.trimToEmpty(scoringMetric);
		}

		public void setScoringMetric(String scoringMetric) {
			this.scoringMetric = scoringMetric;
		}

		public String getScoringDesc() {
			return StringUtils.trimToEmpty(scoringDesc);
		}

		public void setScoringDesc(String scoringDesc) {
			this.scoringDesc = scoringDesc;
		}

		public String getMetricMaxScore() {
			return StringUtils.trimToEmpty(metricMaxScore);
		}

		public void setMetricMaxScore(String metricMaxScore) {
			this.metricMaxScore = metricMaxScore;
		}

		public String getCalcScore() {
			return StringUtils.trimToEmpty(calcScore);
		}

		public void setCalcScore(String calcScore) {
			this.calcScore = calcScore;
		}

	}

	public class GroupRecommendation {
		private String userRole;
		private List<Recommendation> recommendations;

		public GroupRecommendation() {
		    super();
		}

		public void setUserRole(String userRole) {
			this.userRole = userRole;
		}

		public String getUserRole() {
			return StringUtils.trimToEmpty(userRole);
		}

		public void setRecommendations(List<Recommendation> recommendations) {
			this.recommendations = recommendations;
		}

		public List<Recommendation> getRecommendations() {
			return recommendations;
		}
	}

	public class Recommendation {
		private String userName;
		private String userRole;
		private String commentedDate;
		private String noteType;
		private String noteDesc;

		public Recommendation() {
		    super();
		}

		public String getUserName() {
			return StringUtils.trimToEmpty(userName);
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getCommentedDate() {
			return StringUtils.trimToEmpty(commentedDate);
		}

		public void setCommentedDate(String commentedDate) {
			this.commentedDate = commentedDate;
		}

		public String getNoteType() {
			return StringUtils.trimToEmpty(noteType);
		}

		public void setNoteType(String noteType) {
			this.noteType = noteType;
		}

		public String getNoteDesc() {
			return StringUtils.trimToEmpty(noteDesc);
		}

		public String getHtmlNoteDesc() {
			return StringUtils.trimToEmpty(noteDesc);
		}

		public void setNoteDesc(String noteDesc) {
			this.noteDesc = noteDesc;
		}

		public void setUserRole(String userRole) {
			this.userRole = userRole;
		}

		public String getUserRole() {
			return StringUtils.trimToEmpty(userRole);
		}

	}

	public class ExceptionList {
		private String exceptionItem;
		private String exceptionDesc;

		public ExceptionList() {
		    super();
		}

		public String getExceptionItem() {
			return StringUtils.trimToEmpty(exceptionItem);
		}

		public void setExceptionItem(String exceptionItem) {
			this.exceptionItem = exceptionItem;
		}

		public String getExceptionDesc() {
			return StringUtils.trimToEmpty(exceptionDesc);
		}

		public void setExceptionDesc(String exceptionDesc) {
			this.exceptionDesc = exceptionDesc;
		}

	}

	public class ProposedFacility {
		private String bookingUnit;
		private String facilityDesc;
		private String tenor;
		private String pricing;
		private String exposure;
		private String limitExisting;
		private String limitNew;

		private String facilityPurpose;
		private String facilityType;
		private String drawDownPeriod;
		private String repaymentSchedule;
		private String profitRate;
		private String securityDescription;
		private String commission;
		private String guarantee;
		private String covenants;
		private String documentsRequired;
		private String revolving;
		private String transactionType;
		private String agentBank;

		private String totalFacilityCcy;
		private String underWritingCcy;
		private String propFinalTakeCcy;

		private String totalFacilityAmount;
		private String underWritingAmount;
		private String propFinalTakeAmount;

		private String totalFacilityAmountUSD;
		private String underWritingAmountUSD;
		private String propFinalTakeAmountUSD;

		// NONC
		private String date;
		private String cafRef;
		private String customerType;
		private String custNumber;
		private String custName;
		private String relationshipSince;
		private String natureOfBusiness;
		private String natureOfBusinessCode;
		private String totalScoring;
		private String nextReviewDate;
		private String countryOfRisk;

		public ProposedFacility() {
		    super();
		}

		public String getHtmlFacilityDesc() {
			return StringUtils.trimToEmpty(facilityDesc);
		}

		public String getHtmlFacilityPurpose() {
			return StringUtils.trimToEmpty(facilityPurpose);
		}

		public String getHtmlSecurityDescription() {
			return StringUtils.trimToEmpty(securityDescription);
		}

		public String getHtmlCommission() {
			return StringUtils.trimToEmpty(commission);
		}

		public String getHtmlConvenants() {
			return StringUtils.trimToEmpty(covenants);
		}

		public String getHtmlGuarantee() {
			return StringUtils.trimToEmpty(guarantee);
		}

		public String getHtmlDocumentsRequired() {
			return StringUtils.trimToEmpty(documentsRequired);
		}

		public String getBookingUnit() {
			return StringUtils.trimToEmpty(bookingUnit);
		}

		public void setBookingUnit(String bookingUnit) {
			this.bookingUnit = bookingUnit;
		}

		public String getFacilityDesc() {
			return StringUtils.trimToEmpty(facilityDesc);
		}

		public void setFacilityDesc(String facilityDesc) {
			this.facilityDesc = facilityDesc;
		}

		public String getTenor() {
			return StringUtils.trimToEmpty(tenor);
		}

		public void setTenor(String tenor) {
			this.tenor = tenor;
		}

		public String getPricing() {
			return StringUtils.trimToEmpty(pricing);
		}

		public void setPricing(String pricing) {
			this.pricing = pricing;
		}

		public String getExposure() {
			return StringUtils.trimToEmpty(exposure);
		}

		public void setExposure(String exposure) {
			this.exposure = exposure;
		}

		public String getLimitExisting() {
			return StringUtils.trimToEmpty(limitExisting);
		}

		public void setLimitExisting(String limitExisting) {
			this.limitExisting = limitExisting;
		}

		public String getLimitNew() {
			return StringUtils.trimToEmpty(limitNew);
		}

		public void setLimitNew(String limitNew) {
			this.limitNew = limitNew;
		}

		public String getFacilityPurpose() {
			return StringUtils.trimToEmpty(facilityPurpose);
		}

		public void setFacilityPurpose(String facilityFor) {
			this.facilityPurpose = facilityFor;
		}

		public String getFacilityType() {
			return StringUtils.trimToEmpty(facilityType);
		}

		public void setFacilityType(String facilityType) {
			this.facilityType = facilityType;
		}

		public String getDrawDownPeriod() {
			return StringUtils.trimToEmpty(drawDownPeriod);
		}

		public void setDrawDownPeriod(String drawDownPeriod) {
			this.drawDownPeriod = drawDownPeriod;
		}

		public String getRepaymentSchedule() {
			return StringUtils.trimToEmpty(repaymentSchedule);
		}

		public void setRepaymentSchedule(String repaymentSchedule) {
			this.repaymentSchedule = repaymentSchedule;
		}

		public String getProfitRate() {
			return StringUtils.trimToEmpty(profitRate);
		}

		public void setProfitRate(String profitRate) {
			this.profitRate = profitRate;
		}

		public String getSecurityDescription() {
			return StringUtils.trimToEmpty(securityDescription);
		}

		public void setSecurityDescription(String securityDescription) {
			this.securityDescription = securityDescription;
		}

		public String getCommission() {
			return commission;
		}

		public void setCommission(String commission) {
			this.commission = commission;
		}

		public String getGuarantee() {
			return guarantee;
		}

		public void setGuarantee(String guarantee) {
			this.guarantee = guarantee;
		}

		public String getCovenants() {
			return covenants;
		}

		public void setCovenants(String covenants) {
			this.covenants = covenants;
		}

		public String getDocumentsRequired() {
			return StringUtils.trimToEmpty(documentsRequired);

		}

		public void setDocumentsRequired(String documentsRequired) {
			this.documentsRequired = documentsRequired;
		}

		public void setRevolving(String revolving) {
			this.revolving = revolving;
		}

		public String getRevolving() {
			return StringUtils.trimToEmpty(revolving);
		}

		public void setTransactionType(String transactionType) {
			this.transactionType = transactionType;
		}

		public String getTransactionType() {
			return StringUtils.trimToEmpty(transactionType);
		}

		public void setAgentBank(String agentBank) {
			this.agentBank = agentBank;
		}

		public String getAgentBank() {
			return StringUtils.trimToEmpty(agentBank);
		}

		public String getTotalFacilityCcy() {
			return StringUtils.trimToEmpty(totalFacilityCcy);
		}

		public void setTotalFacilityCcy(String totalFacilityCcy) {
			this.totalFacilityCcy = totalFacilityCcy;
		}

		public String getUnderWritingCcy() {
			return StringUtils.trimToEmpty(underWritingCcy);
		}

		public void setUnderWritingCcy(String underWritingCcy) {
			this.underWritingCcy = underWritingCcy;
		}

		public String getPropFinalTakeCcy() {
			return StringUtils.trimToEmpty(propFinalTakeCcy);
		}

		public void setPropFinalTakeCcy(String propFinalTakeCcy) {
			this.propFinalTakeCcy = propFinalTakeCcy;
		}

		public String getTotalFacilityAmount() {
			return StringUtils.trimToEmpty(totalFacilityAmount);
		}

		public void setTotalFacilityAmount(String totalFacilityAmount) {
			this.totalFacilityAmount = totalFacilityAmount;
		}

		public String getUnderWritingAmount() {
			return StringUtils.trimToEmpty(underWritingAmount);
		}

		public void setUnderWritingAmount(String underWritingAmount) {
			this.underWritingAmount = underWritingAmount;
		}

		public String getPropFinalTakeAmount() {
			return StringUtils.trimToEmpty(propFinalTakeAmount);
		}

		public void setPropFinalTakeAmount(String propFinalTakeAmount) {
			this.propFinalTakeAmount = propFinalTakeAmount;
		}

		public String getTotalFacilityAmountUSD() {
			return StringUtils.trimToEmpty(totalFacilityAmountUSD);
		}

		public void setTotalFacilityAmountUSD(String totalFacilityAmountUSD) {
			this.totalFacilityAmountUSD = totalFacilityAmountUSD;
		}

		public String getUnderWritingAmountUSD() {
			return StringUtils.trimToEmpty(underWritingAmountUSD);
		}

		public void setUnderWritingAmountUSD(String underWritingAmountUSD) {
			this.underWritingAmountUSD = underWritingAmountUSD;
		}

		public String getPropFinalTakeAmountUSD() {
			return StringUtils.trimToEmpty(propFinalTakeAmountUSD);
		}

		public void setPropFinalTakeAmountUSD(String propFinalTakeAmountUSD) {
			this.propFinalTakeAmountUSD = propFinalTakeAmountUSD;
		}

		// NONC
		public String getDate() {
			return StringUtils.trimToEmpty(date);
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getCafRef() {
			return StringUtils.trimToEmpty(cafRef);
		}

		public void setCafRef(String cafRef) {
			this.cafRef = cafRef;
		}

		public String getCustomerType() {
			return StringUtils.trimToEmpty(customerType);
		}

		public void setCustomerType(String customerType) {
			this.customerType = customerType;
		}

		public String getCustNumber() {
			return StringUtils.trimToEmpty(custNumber);
		}

		public void setCustNumber(String custNumber) {
			this.custNumber = custNumber;
		}

		public String getCustName() {
			return StringUtils.trimToEmpty(custName);
		}

		public void setCustName(String custName) {
			this.custName = custName;
		}

		public String getRelationshipSince() {
			return StringUtils.trimToEmpty(relationshipSince);
		}

		public void setRelationshipSince(String relationshipSince) {
			this.relationshipSince = relationshipSince;
		}

		public String getNatureOfBusiness() {
			return StringUtils.trimToEmpty(natureOfBusiness);
		}

		public void setNatureOfBusiness(String natureOfBusiness) {
			this.natureOfBusiness = natureOfBusiness;
		}

		public String getNatureOfBusinessCode() {
			return StringUtils.trimToEmpty(natureOfBusinessCode);
		}

		public void setNatureOfBusinessCode(String natureOfBusinessCode) {
			this.natureOfBusinessCode = natureOfBusinessCode;
		}

		public String getTotalScoring() {
			return StringUtils.trimToEmpty(totalScoring);
		}

		public void setTotalScoring(String totalScoring) {
			this.totalScoring = totalScoring;
		}

		public String getNextReviewDate() {
			return StringUtils.trimToEmpty(nextReviewDate);
		}

		public void setNextReviewDate(String nextReviewDate) {
			this.nextReviewDate = nextReviewDate;
		}

		public String getCountryOfRisk() {
			return StringUtils.trimToEmpty(countryOfRisk);
		}

		public void setCountryOfRisk(String countryOfRisk) {
			this.countryOfRisk = countryOfRisk;
		}

	}

	public class Shareholder {
		private String shareholderName;
		private String shareholderPercentage;

		public Shareholder() {
		    super();
		}

		public String getShareholderName() {
			return StringUtils.trimToEmpty(shareholderName);
		}

		public void setShareholderName(String shareholderName) {
			this.shareholderName = shareholderName;
		}

		public String getShareholderPercentage() {
			return StringUtils.trimToEmpty(shareholderPercentage);
		}

		public void setShareholderPercentage(String shareholderPercentage) {
			this.shareholderPercentage = shareholderPercentage;
		}

	}

	public class FacilityCollateral {
		private String securityType;
		private String marketValue;
		private String margin;
		private String bankValue;
		private String cover;

		public FacilityCollateral() {
		    super();
		}

		public String getSecurityType() {
			return StringUtils.trimToEmpty(securityType);
		}

		public void setSecurityType(String securityType) {
			this.securityType = securityType;
		}

		public String getMarketValue() {
			return StringUtils.trimToEmpty(marketValue);
		}

		public void setMarketValue(String marketValue) {
			this.marketValue = marketValue;
		}

		public String getMargin() {
			return StringUtils.trimToEmpty(margin);
		}

		public void setMargin(String margin) {
			this.margin = margin;
		}

		public String getBankValue() {
			return StringUtils.trimToEmpty(bankValue);
		}

		public void setBankValue(String bankValue) {
			this.bankValue = bankValue;
		}

		public String getCover() {
			return StringUtils.trimToEmpty(cover);
		}

		public void setCover(String cover) {
			this.cover = cover;
		}

	}

}
