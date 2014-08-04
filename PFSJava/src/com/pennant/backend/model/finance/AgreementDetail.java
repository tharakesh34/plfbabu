package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class AgreementDetail {

	//=============== Basic Details=============//
	
	private String userId;
	private String userName;
	private String applicationDate;
	
	//=============== End ======================//
	
	// =========== Customer ====================//
	
	private long custID;
	private String custCIF = "";
	private String custName;
	private String custNationality;
	private String custCPRNo;
	private String custEmail;
	private String custPhone;
	private String custFax;
	private String branch;
	private String custDOB;
	private String custAge;
	private String custSector;
	private String custSubSector;
	private BigDecimal custTotalIncome;
	private BigDecimal custTotalExpense;
	private String custTotIncome;
	private String custTotExpense;
	private String custOccupation;
	private String custDocumentid;
	private String noOfDependents;
	private String custCompanyName;
	private String custYearsExp;
	private String custPrevCompanyName;
	private String custYearsService;
	private String custDSR;
	private String custAddress;
	private String custJointDOB;
	private String custJointAge;
	private boolean jointCust;
	private String custJointName;

	
	//=============== End ======================//
	
	// =========== Vehicle Finance =============//
	
	private String vehicleType;
	private String modelYear;
	private String model;
	private String carChasisNo;
	private String carColor;
	private String carCapacity;
	private String insuranceType;
	private String insuranceDesc;
	private String engineNo;
	private String purchaseOrder;
	private String purchaseOrderDate;
	private String merchantName;
	private String merchantPhone;
	private String merchantFax;
	private String quotationDate;
	private String quotationNo;
	private String carRegistrationNo;
	private String vehicleStatus;
	private String guarantorName;
	
	//=============== End ======================//
	
	// =========== Finance Basic================//
	
	private String finRef;
	private String finCcy = "";
	private String referenceNo;
	private String noOfPayments;
	private String startDate;
	private String endDate;
	private String contractDate = "";
	private String price;
	private String downPayment;
	private String costOfGoods;
	private String authorization1;
	private String authorization2;
	private String takafulInsurance;
	private String advacePayment;
	private String profit;
	private String remainingBal;
	private String finAmount;
	private String lpoPrice;
	private String finAmountInWords;
	private String totalAmount;
	private String tenureMonths;
	private String instRate;
	private String custAccountNo;
	private String repayAccount;
	private String repayFrq;
	private String nextRepayDate;
	private String lastRepayDate;
	private String totRepayPrdAmount;
	private String nextInstAmount;
	private String ODchargeamtPage;
	private String finPurpose;
	
	//=============== End ======================//
	
	//=============Customer Income==============//
	
	private List<CustomerIncomeCategory> custincomeCategories;
	
	//=====================End =================//

	//====== Customer Existing Finances ========//
	
	private List<CustomerFinance> customerFinances;
	private String totCustFin;
	
	//=============== End ======================//
	
	//======Mortgage Finance ===================//
	
	private String assetType;
	private String assetarea;
	private String assetRegistration;
	private String deedno;
	private String assetStatus;
	private String assetareainSF;
	private String assetage;
	private String assetareainSM;
	private String assetMarketvle;
	private String assetPricePF;
	private String assetFinRatio;
	
	//=============== End ======================//

	//====== Goods Finance =====================//
	
	private List<GoodLoanDetails> goodsLoanDetails;
	
	//=============== End ======================//

	//====== Commodity Finance =================//
	
	private String finTypeDesc;
	private int tenureDays;
	private String brokerName;
	private String splInstruction;
	private List<CommidityLoanDetails> commidityLoanDetail;
	
	//=============== End ======================//
	
	//======== Credit review====================//
	
	private String adtYear1 = "";
	private String adtYear2 = "";
	private String adtYear3 = "";
	private List<CustomerCreditReview> creditReviewsBalance;
	private List<CustomerCreditReview> creditReviewsRatio;
	
	//=============== End ======================//

	//=============== Scoring Details===========//
	
	private List<ScoringHeader> finScoringHeaderDetails;
	private List<ScoringHeader> nonFinScoringHeaderDetails;
	
	//=============== End ======================//
	
	//=============Check List Details===========//
	
	private List<CheckListDetails> checkListDetails;
	
	//=============== End ======================//
	

	private List<FinanceScheduleReportData> scheduleData;
	
	private List<Recommendation> recommendations;
	private List<GroupRecommendation> groupRecommendations;
	
	private List<ExceptionList> exceptionLists;
	
	private int lovDescCcyFormatter;






	public String getUserId() {
		return StringUtils.trimToEmpty(userId);
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return StringUtils.trimToEmpty(userName);
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public long getCustID() {
		return custID;
	}

	public String getCustCIF() {
		return StringUtils.trimToEmpty(custCIF);
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getCustName() {
		return StringUtils.trimToEmpty(custName);
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getCustNationality() {
		return StringUtils.trimToEmpty(custNationality);
	}

	public void setCustNationality(String custNationality) {
		this.custNationality = custNationality;
	}

	public String getCustCPRNo() {
		return StringUtils.trimToEmpty(custCPRNo);
	}

	public void setCustCPRNo(String custCPRNo) {
		this.custCPRNo = custCPRNo;
	}

	public void setCustEmail(String custEmail) {
		this.custEmail = custEmail;
	}

	public String getCustEmail() {
		return StringUtils.trimToEmpty(custEmail);
	}

	public String getModel() {
		return StringUtils.trimToEmpty(model);
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getCarChasisNo() {
		return StringUtils.trimToEmpty(carChasisNo);
	}

	public void setCarChasisNo(String carChasisNo) {
		this.carChasisNo = carChasisNo;
	}

	public String getCarColor() {
		return StringUtils.trimToEmpty(carColor);
	}

	public void setCarColor(String carColor) {
		this.carColor = carColor;
	}

	public String getCarCapacity() {
		return StringUtils.trimToEmpty(carCapacity);
	}

	public void setCarCapacity(String carCapacity) {
		this.carCapacity = carCapacity;
	}

	public String getInsuranceType() {
		return StringUtils.trimToEmpty(insuranceType);
	}

	public void setInsuranceType(String insuranceType) {
		this.insuranceType = insuranceType;
	}

	public String getInsuranceDesc() {
		return StringUtils.trimToEmpty(insuranceDesc);
	}

	public void setInsuranceDesc(String insuranceDesc) {
		this.insuranceDesc = insuranceDesc;
	}

	public String getCostOfGoods() {
		return StringUtils.trimToEmpty(costOfGoods);
	}

	public void setCostOfGoods(String costOfGoods) {
		this.costOfGoods = costOfGoods;
	}

	public String getAdvacePayment() {
		return StringUtils.trimToEmpty(advacePayment);
	}

	public void setAdvacePayment(String advacePayment) {
		this.advacePayment = advacePayment;
	}

	public String getTakafulInsurance() {
		return StringUtils.trimToEmpty(takafulInsurance);
	}

	public void setTakafulInsurance(String takafulInsurance) {
		this.takafulInsurance = takafulInsurance;
	}

	public String getProfit() {
		return StringUtils.trimToEmpty(profit);
	}

	public void setProfit(String profit) {
		this.profit = profit;
	}

	public String getRemainingBal() {
		return StringUtils.trimToEmpty(remainingBal);
	}

	public void setRemainingBal(String remainingBal) {
		this.remainingBal = remainingBal;
	}

	public String getPurchaseOrder() {
		return StringUtils.trimToEmpty(purchaseOrder);
	}

	public void setPurchaseOrder(String purchaseOrder) {
		this.purchaseOrder = purchaseOrder;
	}

	public String getPurchaseOrderDate() {
		return StringUtils.trimToEmpty(purchaseOrderDate);
	}

	public void setPurchaseOrderDate(String purchaseOrderDate) {
		this.purchaseOrderDate = purchaseOrderDate;
	}

	public String getMerchantName() {
		return StringUtils.trimToEmpty(merchantName);
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getMerchantPhone() {
		return StringUtils.trimToEmpty(merchantPhone);
	}

	public void setMerchantPhone(String merchantPhone) {
		this.merchantPhone = merchantPhone;
	}

	public String getQuotationNo() {
		return StringUtils.trimToEmpty(quotationNo);
	}

	public void setQuotationNo(String quotationNo) {
		this.quotationNo = quotationNo;
	}

	public String getQuotationDate() {
		return StringUtils.trimToEmpty(quotationDate);
	}

	public void setQuotationDate(String quotationDate) {
		this.quotationDate = quotationDate;
	}

	public String getVehicleType() {
		return StringUtils.trimToEmpty(vehicleType);
	}

	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}

	public String getModelYear() {
		return StringUtils.trimToEmpty(modelYear);
	}

	public void setModelYear(String manufactureYear) {
		this.modelYear = manufactureYear;
	}

	public String getEngineNo() {
		return StringUtils.trimToEmpty(engineNo);
	}

	public void setEngineNo(String engineNo) {
		this.engineNo = engineNo;
	}

	public String getPrice() {
		return StringUtils.trimToEmpty(price);
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getCustPhone() {
		return StringUtils.trimToEmpty(custPhone);
	}

	public void setCustPhone(String custPhone) {
		this.custPhone = custPhone;
	}

	public String getCustFax() {
		return StringUtils.trimToEmpty(custFax);
	}

	public void setCustFax(String custFax) {
		this.custFax = custFax;
	}

	public String getApplicationDate() {
		return StringUtils.trimToEmpty(applicationDate);
	}

	public void setApplicationDate(String applicationDate) {
		this.applicationDate = applicationDate;
	}

	public String getDownPayment() {
		return StringUtils.trimToEmpty(downPayment);
	}

	public void setDownPayment(String downPayment) {
		this.downPayment = downPayment;
	}

	public String getCustCompanyName() {
		return StringUtils.trimToEmpty(custCompanyName);
	}

	public void setCustCompanyName(String custCompanyName) {
		this.custCompanyName = custCompanyName;
	}

	public String getCustDocumentid() {
		return StringUtils.trimToEmpty(custDocumentid);
	}

	public void setCustDocumentid(String custDocumentid) {
		this.custDocumentid = custDocumentid;
	}

	public String getCarRegistrationNo() {
		return StringUtils.trimToEmpty(carRegistrationNo);
	}

	public void setCarRegistrationNo(String carRegistrationNo) {
		this.carRegistrationNo = carRegistrationNo;
	}

	public String getCustAccountNo() {
		return StringUtils.trimToEmpty(custAccountNo);
	}

	public void setCustAccountNo(String custAccountNo) {
		this.custAccountNo = custAccountNo;
	}

	public String getRepayAccount() {
		return StringUtils.trimToEmpty(repayAccount);
	}

	public void setRepayAccount(String repayAccount) {
		this.repayAccount = repayAccount;
	}

	public String getTotalAmount() {
		return StringUtils.trimToEmpty(totalAmount);
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getMerchantFax() {
		return StringUtils.trimToEmpty(merchantFax);
	}

	public void setMerchantFax(String merchantFax) {
		this.merchantFax = merchantFax;
	}

	public String getNoOfPayments() {
		return StringUtils.trimToEmpty(noOfPayments);
	}

	public void setNoOfPayments(String noOfPayments) {
		this.noOfPayments = noOfPayments;
	}

	public String getStartDate() {
		return StringUtils.trimToEmpty(startDate);
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return StringUtils.trimToEmpty(endDate);
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getReferenceNo() {
		return StringUtils.trimToEmpty(referenceNo);
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	public String getODchargeamtPage() {
		return StringUtils.trimToEmpty(ODchargeamtPage);
	}

	public void setODchargeamtPage(String oDchargeamtPage) {
		ODchargeamtPage = oDchargeamtPage;
	}

	public String getFinPurpose() {
    	return StringUtils.trimToEmpty(finPurpose);
    }

	public void setFinPurpose(String finPurpose) {
    	this.finPurpose = finPurpose;
    }

	public String getAuthorization1() {
		return StringUtils.trimToEmpty(authorization1);
	}

	public void setAuthorization1(String authorization1) {
		this.authorization1 = authorization1;
	}

	public String getAuthorization2() {
		return StringUtils.trimToEmpty(authorization2);
	}

	public void setAuthorization2(String authorization2) {
		this.authorization2 = authorization2;
	}

	public String getAssetarea() {
		return StringUtils.trimToEmpty(assetarea);
	}

	public void setAssetarea(String assetarea) {
		this.assetarea = assetarea;
	}

	public String getAssetRegistration() {
		return StringUtils.trimToEmpty(assetRegistration);
	}

	public void setAssetRegistration(String assetRegistration) {
		this.assetRegistration = assetRegistration;
	}

	public String getDeedno() {
		return StringUtils.trimToEmpty(deedno);
	}

	public void setDeedno(String deedno) {
		this.deedno = deedno;
	}

	public String getAssetStatus() {
		return StringUtils.trimToEmpty(assetStatus);
	}

	public void setAssetStatus(String assetStatus) {
		this.assetStatus = assetStatus;
	}

	public String getAssetareainSF() {
		return StringUtils.trimToEmpty(assetareainSF);
	}

	public void setAssetareainSF(String assetareainSF) {
		this.assetareainSF = assetareainSF;
	}

	public String getAssetage() {
		return StringUtils.trimToEmpty(assetage);
	}

	public void setAssetage(String assetage) {
		this.assetage = assetage;
	}

	public String getAssetareainSM() {
		return StringUtils.trimToEmpty(assetareainSM);
	}

	public void setAssetareainSM(String assetareainSM) {
		this.assetareainSM = assetareainSM;
	}

	public String getAssetMarketvle() {
		return StringUtils.trimToEmpty(assetMarketvle);
	}

	public void setAssetMarketvle(String assetMarketvle) {
		this.assetMarketvle = assetMarketvle;
	}

	public String getAssetPricePF() {
		return StringUtils.trimToEmpty(assetPricePF);
	}

	public void setAssetPricePF(String assetPricePF) {
		this.assetPricePF = assetPricePF;
	}

	public String getAssetFinRatio() {
		return StringUtils.trimToEmpty(assetFinRatio);
	}

	public void setAssetFinRatio(String assetFinRatio) {
		this.assetFinRatio = assetFinRatio;
	}

	public String getContractDate() {
		return StringUtils.trimToEmpty(contractDate);
	}

	public void setContractDate(String contractDate) {
		this.contractDate = contractDate;
	}

	public String getCustYearsExp() {
		return StringUtils.trimToEmpty(custYearsExp);
	}

	public void setCustYearsExp(String custYearsExp) {
		this.custYearsExp = custYearsExp;
	}

	public String getCustOccupation() {
		return StringUtils.trimToEmpty(custOccupation);
	}

	public void setCustOccupation(String custOccupation) {
		this.custOccupation = custOccupation;
	}

	public String getCustPrevCompanyName() {
		return StringUtils.trimToEmpty(custPrevCompanyName);
	}

	public void setCustPrevCompanyName(String custPrevCompanyName) {
		this.custPrevCompanyName = custPrevCompanyName;
	}

	public String getCustDOB() {
		return StringUtils.trimToEmpty(custDOB);
	}

	public void setCustDOB(String custDOB) {
		this.custDOB = custDOB;
	}

	public String getCustJointDOB() {
		return StringUtils.trimToEmpty(custJointDOB);
	}

	public void setCustJointDOB(String custJointDOB) {

		this.custJointDOB = custJointDOB;
	}

	public String getCustJointAge() {
		return StringUtils.trimToEmpty(custJointAge);
	}

	public void setCustJointAge(String custJointAge) {
		this.custJointAge = custJointAge;
	}
	
	public String getCustJointName() {
		return StringUtils.trimToEmpty(custJointName);
	}

	public void setCustJointName(String custJointName) {
		this.custJointName = custJointName;
	}
	public boolean  isJointCust() {
    	return jointCust;
    }

	public void setJointCust(boolean jointCust) {
    	this.jointCust = jointCust;
    }

	public String getCustAge() {
		return StringUtils.trimToEmpty(custAge);
	}

	public void setCustAge(String custAge) {
		this.custAge = custAge;
	}

	public String getFinAmount() {
		return StringUtils.trimToEmpty(finAmount);
	}

	public void setFinAmount(String finAmount) {
		this.finAmount = finAmount;
	}
	
	public String getLpoPrice() {
    	return StringUtils.trimToEmpty(lpoPrice);
    }

	public void setLpoPrice(String lpoPrice) {
    	this.lpoPrice = lpoPrice;
    }

	public String getFinAmountInWords() {
		return StringUtils.trimToEmpty(finAmountInWords);
	}

	public void setFinAmountInWords(String finAmountInWords) {
		this.finAmountInWords = finAmountInWords;
	}

	public String getInstRate() {
		return StringUtils.trimToEmpty(instRate);
	}

	public void setInstRate(String instRate) {
		this.instRate = instRate;
	}

	public String getTenureMonths() {
		return StringUtils.trimToEmpty(tenureMonths);
	}

	public void setTenureMonths(String tenureMonths) {
		this.tenureMonths = tenureMonths;
	}

	public String getVehicleStatus() {
		return StringUtils.trimToEmpty(vehicleStatus);
	}

	public void setVehicleStatus(String vehicleStatus) {
		this.vehicleStatus = vehicleStatus;
	}

	public String getGuarantorName() {
    	return StringUtils.trimToEmpty(guarantorName);
    }

	public void setGuarantorName(String guarantorName) {
    	this.guarantorName = guarantorName;
    }

	public String getBranch() {
		return StringUtils.trimToEmpty(branch);
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getAssetType() {
		return StringUtils.trimToEmpty(assetType);
	}

	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}

	public void setGoodsLoanDetails(List<GoodLoanDetails> goodsLoanDetails) {
		this.goodsLoanDetails = goodsLoanDetails;
	}

	public List<GoodLoanDetails> getGoodsLoanDetails() {
		return goodsLoanDetails;
	}

	public void setCheckListDetails(List<CheckListDetails> checkListDetails) {
		this.checkListDetails = checkListDetails;
	}

	public List<CheckListDetails> getCheckListDetails() {
		return checkListDetails;
	}

	public class GoodLoanDetails {
		private String supplierName;
		private String itemType;
		private String itemNumber;
		private String itemDescription;
		private String unitPrice;
		private String quantity;

		public String getSupplierName() {
			return StringUtils.trimToEmpty(supplierName);
		}

		public void setSupplierName(String supplierName) {
			this.supplierName = supplierName;
		}

		public String getItemType() {
			return StringUtils.trimToEmpty(itemType);
		}

		public void setItemType(String itemType) {
			this.itemType = itemType;
		}

		public String getItemNumber() {
			return StringUtils.trimToEmpty(itemNumber);
		}

		public void setItemNumber(String itemNumber) {
			this.itemNumber = itemNumber;
		}

		public String getItemDescription() {
			return StringUtils.trimToEmpty(itemDescription);
		}

		public void setItemDescription(String itemDescription) {
			this.itemDescription = itemDescription;
		}

		public String getUnitPrice() {
			return StringUtils.trimToEmpty(unitPrice);
		}

		public void setUnitPrice(String unitPrice) {
			this.unitPrice = unitPrice;
		}

		public String getQuantity() {
			return StringUtils.trimToEmpty(quantity);
		}

		public void setQuantity(String quantity) {
			this.quantity = quantity;
		}

	}

	public class CommidityLoanDetails {
		private String itemType;
		private String quantity;
		private String unitBuyPrice;
		private String buyAmount;
		private String unitSellPrice;
		private String sellAmount;

		public String getItemType() {
			return StringUtils.trimToEmpty(itemType);
		}

		public void setItemType(String itemType) {
			this.itemType = itemType;
		}

		public String getQuantity() {
			return StringUtils.trimToEmpty(quantity);
		}

		public void setQuantity(String quantity) {
			this.quantity = quantity;
		}

		public String getUnitBuyPrice() {
			return StringUtils.trimToEmpty(unitBuyPrice);
		}

		public void setUnitBuyPrice(String unitBuyPrice) {
			this.unitBuyPrice = unitBuyPrice;
		}

		public String getBuyAmount() {
			return StringUtils.trimToEmpty(buyAmount);
		}

		public void setBuyAmount(String buyAmount) {
			this.buyAmount = buyAmount;
		}

		public String getUnitSellPrice() {
			return StringUtils.trimToEmpty(unitSellPrice);
		}

		public void setUnitSellPrice(String unitSellPrice) {
			this.unitSellPrice = unitSellPrice;
		}

		public String getSellAmount() {
			return StringUtils.trimToEmpty(sellAmount);
		}

		public void setSellAmount(String sellAmount) {
			this.sellAmount = sellAmount;
		}

	}

	public class CheckListDetails {
		private long questionId;
		private String question;
		private List<CheckListAnsDetails> listquestionAns;

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

	public class CustomerIncomeCategory {
		private String incomeCategory;
		private List<CustomerIncomeDetails> customerIncomeDetails;

		public void setCustomerIncomeDetails(List<CustomerIncomeDetails> customerIncomeDetails) {
			this.customerIncomeDetails = customerIncomeDetails;
		}

		public List<CustomerIncomeDetails> getCustomerIncomeDetails() {
			return customerIncomeDetails;
		}

		public String getIncomeCategory() {
			return StringUtils.trimToEmpty(incomeCategory);
		}

		public void setIncomeCategory(String incomeCategory) {
			this.incomeCategory = incomeCategory;
		}

	}

	public class CustomerIncomeDetails {

		private String incomeType;
		private String income;
		private String jointCust;

		public String getIncomeType() {
			return StringUtils.trimToEmpty(incomeType);
		}

		public void setIncomeType(String incomeType) {
			this.incomeType = incomeType;
		}

		public String getIncome() {
			return StringUtils.trimToEmpty(income);
		}

		public void setIncome(String income) {
			this.income = income;
		}

		public String getJointCust() {
			return StringUtils.trimToEmpty(jointCust);
		}

		public void setJointCust(String jointCust) {
			this.jointCust = jointCust;
		}

	}

	public class CustomerFinance {
		private String dealDate;
		private String dealType;
		private String originalAmount;
		private String monthlyInstalment;
		private String outstandingBalance;

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

	//==========================


	public String getAdtYear1() {
		return adtYear1;
	}

	public void setAdtYear1(String adtYear1) {
		this.adtYear1 = adtYear1;
	}

	public String getAdtYear2() {
		return adtYear2;
	}

	public void setAdtYear2(String adtYear2) {
		this.adtYear2 = adtYear2;
	}

	public String getAdtYear3() {
		return adtYear3;
	}

	public void setAdtYear3(String adtYear3) {
		this.adtYear3 = adtYear3;
	}

	public class CustomerCreditReview {
		private String categoryName;
		private List<CustomerCreditReviewDetails> customerCreditReviewDetails = new ArrayList<CustomerCreditReviewDetails>();

		public String getCategoryName() {
			return StringUtils.trimToEmpty(categoryName);
		}

		public void setCategoryName(String categoryName) {
			this.categoryName = categoryName;
		}

		public List<CustomerCreditReviewDetails> getCustomerCreditReviewDetails() {
			return customerCreditReviewDetails;
		}

		public void setCustomerCreditReviewDetails(
		        List<CustomerCreditReviewDetails> customerCreditReviewDetails) {
			this.customerCreditReviewDetails = customerCreditReviewDetails;
		}
	}

	public class CustomerCreditReviewDetails {
		private String subCategoryName;
		private String year1;
		private String year2;
		private String year3;

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
	//==========================
	private String totMaxScore = "";
	private String totMinScore = "";
	private String totCalcScore = "";
	private String overrideScore = "";
	private String isOverrideScore = "";

	public String getTotMaxScore() {
		return StringUtils.trimToEmpty(totMaxScore);
	}

	public void setTotMaxScore(String totMaxScore) {
		this.totMaxScore = totMaxScore;
	}

	public String getTotMinScore() {
		return StringUtils.trimToEmpty(totMinScore);
	}

	public void setTotMinScore(String totMinScore) {
		this.totMinScore = totMinScore;
	}

	public String getTotCalcScore() {
		return StringUtils.trimToEmpty(totCalcScore);
	}

	public void setTotCalcScore(String totCalcScore) {
		this.totCalcScore = totCalcScore;
	}

	public String getOverrideScore() {
		return StringUtils.trimToEmpty(overrideScore);
	}

	public void setOverrideScore(String overrideScore) {
		this.overrideScore = overrideScore;
	}

	public String getIsOverrideScore() {
		return StringUtils.trimToEmpty(isOverrideScore);
	}

	public void setIsOverrideScore(String isOverrideScore) {
		this.isOverrideScore = isOverrideScore;
	}

	public class ScoringHeader {

		public ScoringHeader() {
		}

		private String scoringGroup;
		private List<ScoringDetails> scoringDetails = new ArrayList<ScoringDetails>();

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

		public ScoringDetails() {
		}

		private String scoringMetric;
		private String scoringDesc;
		private String metricMaxScore;
		private String calcScore;

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

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public void setLovDescCcyFormatter(int lovDescCcyFormatter) {
		this.lovDescCcyFormatter = lovDescCcyFormatter;
	}

	public int getLovDescCcyFormatter() {
		return lovDescCcyFormatter;
	}

	public void setCustTotalIncome(BigDecimal custTotalIncome) {
		this.custTotalIncome = custTotalIncome;
	}

	public BigDecimal getCustTotalIncome() {
		return custTotalIncome;
	}

	public void setCustTotalExpense(BigDecimal custTotalExpense) {
		this.custTotalExpense = custTotalExpense;
	}

	public BigDecimal getCustTotalExpense() {
		return custTotalExpense;
	}

	public String getCustTotIncome() {
		return StringUtils.trimToEmpty(custTotIncome);
	}

	public void setCustTotIncome(String custTotIncome) {
		this.custTotIncome = custTotIncome;
	}

	public String getCustTotExpense() {
		return StringUtils.trimToEmpty(custTotExpense);
	}

	public void setCustTotExpense(String custTotExpense) {
		this.custTotExpense = custTotExpense;
	}

	public void setCustYearsService(String custYearsService) {
		this.custYearsService = custYearsService;
	}

	public String getCustYearsService() {
		return StringUtils.trimToEmpty(custYearsService);
	}

	public void setNoOfDependents(String noOfDependents) {
		this.noOfDependents = noOfDependents;
	}

	public String getNoOfDependents() {
		return noOfDependents;
	}

	public void setCustomerFinances(List<CustomerFinance> customerFinances) {
		this.customerFinances = customerFinances;
	}

	public List<CustomerFinance> getCustomerFinances() {
		return customerFinances;
	}

	public void setFinRef(String finRef) {
		this.finRef = finRef;
	}

	public String getFinRef() {
		return StringUtils.trimToEmpty(finRef);
	}

	public void setTotCustFin(String totCustFin) {
		this.totCustFin = totCustFin;
	}

	public String getTotCustFin() {
		return StringUtils.trimToEmpty(totCustFin);
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

	public void setCreditReviewsRatio(List<CustomerCreditReview> creditReviewsRatio) {
		this.creditReviewsRatio = creditReviewsRatio;
	}

	public List<CustomerCreditReview> getCreditReviewsRatio() {
		return creditReviewsRatio;
	}

	public List<CommidityLoanDetails> getCommidityLoanDetails() {
		return commidityLoanDetail;
	}

	public void setCommidityLoanDetails(List<CommidityLoanDetails> commidityLoanDetails) {
		this.commidityLoanDetail = commidityLoanDetails;
	}

	public void setTenureDays(int tenureDays) {
		this.tenureDays = tenureDays;
	}

	public int getTenureDays() {
		return tenureDays;
	}

	public void setBrokerName(String brokerName) {
		this.brokerName = brokerName;
	}

	public String getBrokerName() {
		return brokerName;
	}

	public void setSplInstruction(String splInstruction) {
		this.splInstruction = splInstruction;
	}

	public String getSplInstruction() {
		return splInstruction;
	}

	public void setFinTypeDesc(String finTypeDesc) {
		this.finTypeDesc = finTypeDesc;
	}

	public String getFinTypeDesc() {
		return finTypeDesc;
	}

	public void setCustSector(String custSector) {
		this.custSector = custSector;
	}

	public String getCustSector() {
		return custSector;
	}

	public void setCustSubSector(String custSubSector) {
		this.custSubSector = custSubSector;
	}

	public String getCustSubSector() {
		return custSubSector;
	}

	public void setCustincomeCategories(List<CustomerIncomeCategory> custincomeCategories) {
		this.custincomeCategories = custincomeCategories;
	}

	public List<CustomerIncomeCategory> getCustincomeCategories() {
		return custincomeCategories;
	}
	
	public String getCustAddress() {
    	return StringUtils.trimToEmpty(custAddress);
    }

	public void setCustAddress(String custAddress) {
    	this.custAddress = custAddress;
    }
	public void setCustDSR(String custDSR) {
		this.custDSR = custDSR;
	}

	public String getCustDSR() {
		return custDSR;
	}

	public String getRepayFrq() {
		return repayFrq;
	}

	public void setRepayFrq(String repayFrq) {
		this.repayFrq = repayFrq;
	}

	public String getNextRepayDate() {
		return nextRepayDate;
	}

	public void setNextRepayDate(String nextRepayDate) {
		this.nextRepayDate = nextRepayDate;
	}

	public String getLastRepayDate() {
		return lastRepayDate;
	}

	public void setLastRepayDate(String lastRepayDate) {
		this.lastRepayDate = lastRepayDate;
	}

	public String getTotRepayPrdAmount() {
		return totRepayPrdAmount;
	}

	public void setTotRepayPrdAmount(String totRepayPrdAmount) {
		this.totRepayPrdAmount = totRepayPrdAmount;
	}

	public String getNextInstAmount() {
		return nextInstAmount;
	}

	public void setNextInstAmount(String nextInstAmount) {
		this.nextInstAmount = nextInstAmount;
	}

	public void setScheduleData(List<FinanceScheduleReportData> scheduleData) {
		this.scheduleData = scheduleData;
	}

	public List<FinanceScheduleReportData> getScheduleData() {
		return scheduleData;
	}

	public void setRecommendations(List<Recommendation> recommendations) {
		this.recommendations = recommendations;
	}

	public List<Recommendation> getRecommendations() {
		return recommendations;
	}
	public List<GroupRecommendation> getGroupRecommendations() {
		return groupRecommendations;
	}

	public void setGroupRecommendations(List<GroupRecommendation> groupRecommendations) {
		this.groupRecommendations = groupRecommendations;
	}
	public void setExceptionLists(List<ExceptionList> exceptionLists) {
		this.exceptionLists = exceptionLists;
	}

	public List<ExceptionList> getExceptionLists() {
		return exceptionLists;
	}
	// Unused
		private String custEducation;
		private String custJointNationality;
		private String custJointCPR;
	
		private String bankName;
		private String bankPhone;
		private String bankFax;
		private String vehicleBankDate;
		private String inputUserName;

		private String basicSalary;
		private String fixedAllowance;
		private String varAllowance;
		private String totalDeposits6;
		private String totalWD6;
		private String monthlyAvgdeposit6;
		private String monthlyAvgWD6;
		private String marginIncome;
		private String mrgInper;
		private String totalOtherIncome;
		private String mrgOper;
		private String marginOthIncome;
		private String grossBusinessinc;
		private String carSpecifications;
		private String dayofWeek;
		
		private String dayOfContarctDate = "";
		private String monthOfContarctDate = "";
		private String yearOfContarctDate = "";
		private String custSalutation = "";
		private String custFullName = "";
	
		private String agentName = "";
		private String agentAddr1 = "";
		private String agentAddr2 = "";
		private String agentCity = "";
		private String agentCountry = "";
		private String disbursementAmt;
		private String financeStartDate;
		private String disbursementAccount;	
	
		public String getBankName() {
			return StringUtils.trimToEmpty(bankName);
		}
	
		public void setBankName(String bankName) {
			this.bankName = bankName;
		}
	
		public String getBankPhone() {
			return StringUtils.trimToEmpty(bankPhone);
		}
	
		public void setBankPhone(String bankPhone) {
			this.bankPhone = bankPhone;
		}
	
		public String getBankFax() {
			return StringUtils.trimToEmpty(bankFax);
		}
	
		public void setBankFax(String bankFax) {
			this.bankFax = bankFax;
		}
		public String getCustEducation() {
			return StringUtils.trimToEmpty(custEducation);
		}
	
		public void setCustEducation(String custEducation) {
			this.custEducation = custEducation;
		}
		public String getCustJointNationality() {
			return StringUtils.trimToEmpty(custJointNationality);
		}
	
		public void setCustJointNationality(String custJointNationality) {
			this.custJointNationality = custJointNationality;
		}
	
		public String getCustJointCPR() {
			return StringUtils.trimToEmpty(custJointCPR);
		}
	
		public void setCustJointCPR(String custJointCPR) {
			this.custJointCPR = custJointCPR;
		}
	
		public String getInputUserName() {
			return StringUtils.trimToEmpty(inputUserName);
		}
	
		public void setInputUserName(String inputUserName) {
			this.inputUserName = inputUserName;
		}
	
		public String getVehicleBankDate() {
			return StringUtils.trimToEmpty(vehicleBankDate);
		}
	
		public void setVehicleBankDate(String vehicleBankDate) {
			this.vehicleBankDate = vehicleBankDate;
		}
		public String getDayofWeek() {
	    	return StringUtils.trimToEmpty(dayofWeek);
	    }
	
		public void setDayofWeek(String dayofWeek) {
	    	this.dayofWeek = dayofWeek;
	    }
	
		public String getAgentName() {
			return agentName;
		}
	
		public void setAgentName(String agentName) {
			this.agentName = agentName;
		}
	
		public String getAgentAddr1() {
			return agentAddr1;
		}
	
		public void setAgentAddr1(String agentAddr1) {
			this.agentAddr1 = agentAddr1;
		}
	
		public String getAgentAddr2() {
			return agentAddr2;
		}
	
		public void setAgentAddr2(String agentAddr2) {
			this.agentAddr2 = agentAddr2;
		}
	
		public String getAgentCity() {
			return agentCity;
		}
	
		public void setAgentCity(String agentCity) {
			this.agentCity = agentCity;
		}
	
		public String getAgentCountry() {
			return agentCountry;
		}
	
		public void setAgentCountry(String agentCountry) {
			this.agentCountry = agentCountry;
		}
	
		public void setDisbursementAccount(String disbursementAccount) {
			this.disbursementAccount = disbursementAccount;
		}
	
		public String getDisbursementAccount() {
			return disbursementAccount;
		}
	
		public void setDisbursementAmt(String disbursementAmt) {
			this.disbursementAmt = disbursementAmt;
		}
	
		public String getDisbursementAmt() {
			return disbursementAmt;
		}
	
		public void setFinanceStartDate(String financeStartDate) {
			this.financeStartDate = financeStartDate;
		}
	
		public String getFinanceStartDate() {
			return financeStartDate;
		}
	
		public String getDayOfContarctDate() {
			return dayOfContarctDate;
		}
	
		public void setDayOfContarctDate(String dayOfContarctDate) {
			this.dayOfContarctDate = dayOfContarctDate;
		}
	
		public String getMonthOfContarctDate() {
			return monthOfContarctDate;
		}
	
		public void setMonthOfContarctDate(String monthOfContarctDate) {
			this.monthOfContarctDate = monthOfContarctDate;
		}
	
		public String getYearOfContarctDate() {
			return yearOfContarctDate;
		}
	
		public void setYearOfContarctDate(String yearOfContarctDate) {
			this.yearOfContarctDate = yearOfContarctDate;
		}
	
		public String getCustSalutation() {
			return custSalutation;
		}
	
		public void setCustSalutation(String custSalutation) {
			this.custSalutation = custSalutation;
		}
	
		public String getCustFullName() {
			return custFullName;
		}
	
		public void setCustFullName(String custFullName) {
			this.custFullName = custFullName;
		}
	
		public String getBasicSalary() {
			return StringUtils.trimToEmpty(basicSalary);
		}
	
		public void setBasicSalary(String basicSalary) {
			this.basicSalary = basicSalary;
		}
	
		public String getFixedAllowance() {
			return StringUtils.trimToEmpty(fixedAllowance);
		}
	
		public void setFixedAllowance(String fixedAllowance) {
			this.fixedAllowance = fixedAllowance;
		}
	
		public String getVarAllowance() {
			return StringUtils.trimToEmpty(varAllowance);
		}
	
		public void setVarAllowance(String varAllowance) {
			this.varAllowance = varAllowance;
		}
	
		public String getTotalDeposits6() {
			return StringUtils.trimToEmpty(totalDeposits6);
		}
	
		public void setTotalDeposits6(String totalDeposits6) {
			this.totalDeposits6 = totalDeposits6;
		}
	
		public String getTotalWD6() {
			return StringUtils.trimToEmpty(totalWD6);
		}
	
		public void setTotalWD6(String totalWD6) {
			this.totalWD6 = totalWD6;
		}
	
		public String getMonthlyAvgdeposit6() {
			return StringUtils.trimToEmpty(monthlyAvgdeposit6);
		}
	
		public void setMonthlyAvgdeposit6(String monthlyAvgdeposit6) {
			this.monthlyAvgdeposit6 = monthlyAvgdeposit6;
		}
	
		public String getMonthlyAvgWD6() {
			return StringUtils.trimToEmpty(monthlyAvgWD6);
		}
	
		public void setMonthlyAvgWD6(String monthlyAvgWD6) {
			this.monthlyAvgWD6 = monthlyAvgWD6;
		}
	
		public String getMarginIncome() {
			return StringUtils.trimToEmpty(marginIncome);
		}
	
		public void setMarginIncome(String marginIncome) {
			this.marginIncome = marginIncome;
		}
	
		public String getMrgInper() {
			return StringUtils.trimToEmpty(mrgInper);
		}
	
		public void setMrgInper(String mrgInper) {
			this.mrgInper = mrgInper;
		}
	
		public String getTotalOtherIncome() {
			return StringUtils.trimToEmpty(totalOtherIncome);
		}
	
		public void setTotalOtherIncome(String totalOtherIncome) {
			this.totalOtherIncome = totalOtherIncome;
		}
	
		public String getMrgOper() {
			return StringUtils.trimToEmpty(mrgOper);
		}
	
		public void setMrgOper(String mrgOper) {
			this.mrgOper = mrgOper;
		}
	
		public String getMarginOthIncome() {
			return StringUtils.trimToEmpty(marginOthIncome);
		}
	
		public void setMarginOthIncome(String marginOthIncome) {
			this.marginOthIncome = marginOthIncome;
		}
	
		public String getGrossBusinessinc() {
			return StringUtils.trimToEmpty(grossBusinessinc);
		}
	
		public void setGrossBusinessinc(String grossBusinessinc) {
			this.grossBusinessinc = grossBusinessinc;
		}
	
		public String getCarSpecifications() {
			return StringUtils.trimToEmpty(carSpecifications);
		}
	
		public void setCarSpecifications(String carSpecifications) {
			this.carSpecifications = carSpecifications;
		}
}
