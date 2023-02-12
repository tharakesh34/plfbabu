package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * @author rahul.k This class uses for generating report for EarlySettlement(Closing the loan before maturity date)
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ForeClosureReport implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1459470132840547209L;

	private String calDate;
	private String custName;
	private String address;
	private String custCIF;
	private String vanNumber = "";
	private long finID;
	private String finReference;
	private BigDecimal finAmount = BigDecimal.ZERO;
	private String finAmountInWords = "Zero Only";
	private BigDecimal finAssetValue = BigDecimal.ZERO;
	private String finAssetValueInWords = "";
	private String disbursalDate;
	private String chrgTillDate;
	private String receiptDate;
	private BigDecimal outstandingPri = BigDecimal.ZERO;
	private String outstandingPriInWords = "Zero Only";
	private BigDecimal latePayCharges = BigDecimal.ZERO;
	private String latePayChargesInWords = "Zero Only";
	private BigDecimal cheqBncCharges = BigDecimal.ZERO;
	private String cheqBncChargesInWords = "Zero Only";
	private BigDecimal instForTheMonth = BigDecimal.ZERO;
	private String instForTheMonthInWords = "Zero Only";
	private BigDecimal foreClosFees = BigDecimal.ZERO;
	private String foreClosFeesInWords = "Zero Only";
	private BigDecimal principalAmt = BigDecimal.ZERO;
	private BigDecimal interestAmt = BigDecimal.ZERO;
	private BigDecimal pendingInsts = BigDecimal.ZERO;
	private String pendingInstsInWords = "Zero Only";
	private BigDecimal tds = BigDecimal.ZERO;
	private BigDecimal refund = BigDecimal.ZERO;
	private BigDecimal intOnTerm = BigDecimal.ZERO;
	private BigDecimal totWaiver = BigDecimal.ZERO;
	private BigDecimal totalDues = BigDecimal.ZERO;
	private BigDecimal advInsts = BigDecimal.ZERO;
	private BigDecimal otherRefunds = BigDecimal.ZERO;
	private String otherRefundsInWords = "Zero Only";
	private BigDecimal totalRefunds = BigDecimal.ZERO;
	private String totalRefundsInWords = "";
	private BigDecimal netReceivable = BigDecimal.ZERO;
	private String netReceivableInWords = "Zero Only";

	private BigDecimal netReceivableNoRefund = BigDecimal.ZERO;
	private String netReceivableNoRefundInWords = "Zero Only";

	private BigDecimal manualAdviceAmt = BigDecimal.ZERO;
	private BigDecimal latePayInterestAmt = BigDecimal.ZERO;
	private String total;
	private String entityDesc;
	private String productDesc;

	private String valueDate1;
	private String valueDate2;
	private String valueDate3;
	private String valueDate4;
	private String valueDate5;
	private String valueDate6;
	private String valueDate7;
	private BigDecimal amount1 = BigDecimal.ZERO;
	private BigDecimal amount2 = BigDecimal.ZERO;
	private BigDecimal amount3 = BigDecimal.ZERO;
	private BigDecimal amount4 = BigDecimal.ZERO;
	private BigDecimal amount5 = BigDecimal.ZERO;
	private BigDecimal amount6 = BigDecimal.ZERO;
	private BigDecimal amount7 = BigDecimal.ZERO;
	private BigDecimal intPerday = BigDecimal.ZERO;
	private int noOfIntDays;
	private String nameOftheBorrowers;
	private String custSalutation;
	private String collateralAddress;
	private String linkedFinRef;
	private BigDecimal actPercentage = BigDecimal.ZERO;

	// Variable to set 18% GST on foreclosure charges
	private BigDecimal gstOnForeClosFees = BigDecimal.ZERO;
	// Variable to set excluding 18% GST on foreclosure charges GHF
	private BigDecimal foreClosFeesExGST = BigDecimal.ZERO;

	private BigDecimal chargesIncGST = BigDecimal.ZERO;
	private String chargesIncGSTInWords = "Zero Only";
	// Customer Address
	private String custAddrHNbr;
	private String custFlatNo;
	private String custAddrStreet;
	private String custAddrCityName;
	private String custAddrProvinceName;
	private String custAddrZIP;
	private String custAddrCountryName;
	private String validTill;
	private List<Map<String, Object>> collAddressList = new ArrayList<>();
	private String valueDate;
	private BigDecimal oneDayInterest;
	private String custMobile;
	private String firstInstDate = "";

	public ForeClosureReport() {
		super();
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public BigDecimal getFinAmount() {
		return finAmount;
	}

	public void setFinAmount(BigDecimal finAmount) {
		this.finAmount = finAmount;
	}

	public BigDecimal getFinAssetValue() {
		return finAssetValue;
	}

	public void setFinAssetValue(BigDecimal finAssetValue) {
		this.finAssetValue = finAssetValue;
	}

	public BigDecimal getOutstandingPri() {
		return outstandingPri;
	}

	public void setOutstandingPri(BigDecimal outstandingPri) {
		this.outstandingPri = outstandingPri;
	}

	public BigDecimal getLatePayCharges() {
		return latePayCharges;
	}

	public void setLatePayCharges(BigDecimal latePayCharges) {
		this.latePayCharges = latePayCharges;
	}

	public BigDecimal getCheqBncCharges() {
		return cheqBncCharges;
	}

	public void setCheqBncCharges(BigDecimal cheqBncCharges) {
		this.cheqBncCharges = cheqBncCharges;
	}

	public BigDecimal getForeClosFees() {
		return foreClosFees;
	}

	public void setForeClosFees(BigDecimal foreClosFees) {
		this.foreClosFees = foreClosFees;
	}

	public BigDecimal getPendingInsts() {
		return pendingInsts;
	}

	public void setPendingInsts(BigDecimal pendingInsts) {
		this.pendingInsts = pendingInsts;
	}

	public BigDecimal getRefund() {
		return refund;
	}

	public void setRefund(BigDecimal refund) {
		this.refund = refund;
	}

	public BigDecimal getIntOnTerm() {
		return intOnTerm;
	}

	public void setIntOnTerm(BigDecimal intOnTerm) {
		this.intOnTerm = intOnTerm;
	}

	public BigDecimal getTotalDues() {
		return totalDues;
	}

	public void setTotalDues(BigDecimal totalDues) {
		this.totalDues = totalDues;
	}

	public BigDecimal getAdvInsts() {
		return advInsts;
	}

	public void setAdvInsts(BigDecimal advInsts) {
		this.advInsts = advInsts;
	}

	public BigDecimal getOtherRefunds() {
		return otherRefunds;
	}

	public void setOtherRefunds(BigDecimal otherRefunds) {
		this.otherRefunds = otherRefunds;
	}

	public BigDecimal getTotalRefunds() {
		return totalRefunds;
	}

	public void setTotalRefunds(BigDecimal totalRefunds) {
		this.totalRefunds = totalRefunds;
	}

	public BigDecimal getNetReceivable() {
		return netReceivable;
	}

	public void setNetReceivable(BigDecimal netReceivable) {
		this.netReceivable = netReceivable;
	}

	public BigDecimal getAmount1() {
		return amount1;
	}

	public void setAmount1(BigDecimal amount1) {
		this.amount1 = amount1;
	}

	public BigDecimal getAmount2() {
		return amount2;
	}

	public void setAmount2(BigDecimal amount2) {
		this.amount2 = amount2;
	}

	public BigDecimal getAmount3() {
		return amount3;
	}

	public void setAmount3(BigDecimal amount3) {
		this.amount3 = amount3;
	}

	public BigDecimal getAmount4() {
		return amount4;
	}

	public void setAmount4(BigDecimal amount4) {
		this.amount4 = amount4;
	}

	public BigDecimal getAmount5() {
		return amount5;
	}

	public void setAmount5(BigDecimal amount5) {
		this.amount5 = amount5;
	}

	public BigDecimal getAmount6() {
		return amount6;
	}

	public void setAmount6(BigDecimal amount6) {
		this.amount6 = amount6;
	}

	public BigDecimal getAmount7() {
		return amount7;
	}

	public void setAmount7(BigDecimal amount7) {
		this.amount7 = amount7;
	}

	public String getLinkedFinRef() {
		return linkedFinRef;
	}

	public void setLinkedFinRef(String linkedFinRef) {
		this.linkedFinRef = linkedFinRef;
	}

	public String getCalDate() {
		return calDate;
	}

	public void setCalDate(String calDate) {
		this.calDate = calDate;
	}

	public String getDisbursalDate() {
		return disbursalDate;
	}

	public void setDisbursalDate(String disbursalDate) {
		this.disbursalDate = disbursalDate;
	}

	public String getValueDate1() {
		return valueDate1;
	}

	public void setValueDate1(String valueDate1) {
		this.valueDate1 = valueDate1;
	}

	public String getValueDate2() {
		return valueDate2;
	}

	public void setValueDate2(String valueDate2) {
		this.valueDate2 = valueDate2;
	}

	public String getValueDate3() {
		return valueDate3;
	}

	public void setValueDate3(String valueDate3) {
		this.valueDate3 = valueDate3;
	}

	public String getValueDate4() {
		return valueDate4;
	}

	public void setValueDate4(String valueDate4) {
		this.valueDate4 = valueDate4;
	}

	public String getValueDate5() {
		return valueDate5;
	}

	public void setValueDate5(String valueDate5) {
		this.valueDate5 = valueDate5;
	}

	public String getValueDate6() {
		return valueDate6;
	}

	public void setValueDate6(String valueDate6) {
		this.valueDate6 = valueDate6;
	}

	public String getValueDate7() {
		return valueDate7;
	}

	public void setValueDate7(String valueDate7) {
		this.valueDate7 = valueDate7;
	}

	public BigDecimal getTds() {
		return tds;
	}

	public void setTds(BigDecimal tds) {
		this.tds = tds;
	}

	public BigDecimal getTotWaiver() {
		return totWaiver;
	}

	public void setTotWaiver(BigDecimal totWaiver) {
		this.totWaiver = totWaiver;
	}

	public BigDecimal getInstForTheMonth() {
		return instForTheMonth;
	}

	public void setInstForTheMonth(BigDecimal instForTheMonth) {
		this.instForTheMonth = instForTheMonth;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getEntityDesc() {
		return entityDesc;
	}

	public void setEntityDesc(String entityDesc) {
		this.entityDesc = entityDesc;
	}

	public String getProductDesc() {
		return productDesc;
	}

	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}

	public String getChrgTillDate() {
		return chrgTillDate;
	}

	public void setChrgTillDate(String chrgTillDate) {
		this.chrgTillDate = chrgTillDate;
	}

	public String getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(String receiptDate) {
		this.receiptDate = receiptDate;
	}

	public BigDecimal getIntPerday() {
		return intPerday;
	}

	public void setIntPerday(BigDecimal intPerday) {
		this.intPerday = intPerday;
	}

	public int getNoOfIntDays() {
		return noOfIntDays;
	}

	public void setNoOfIntDays(int noOfIntDays) {
		this.noOfIntDays = noOfIntDays;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getVanNumber() {
		return vanNumber;
	}

	public void setVanNumber(String vanNumber) {
		this.vanNumber = vanNumber;
	}

	public BigDecimal getManualAdviceAmt() {
		return manualAdviceAmt;
	}

	public void setManualAdviceAmt(BigDecimal manualAdviceAmt) {
		this.manualAdviceAmt = manualAdviceAmt;
	}

	public BigDecimal getPrincipalAmt() {
		return principalAmt;
	}

	public void setPrincipalAmt(BigDecimal principalAmt) {
		this.principalAmt = principalAmt;
	}

	public BigDecimal getInterestAmt() {
		return interestAmt;
	}

	public void setInterestAmt(BigDecimal interestAmt) {
		this.interestAmt = interestAmt;
	}

	public BigDecimal getActPercentage() {
		return actPercentage;
	}

	public void setActPercentage(BigDecimal actPercentage) {
		this.actPercentage = actPercentage;
	}

	public BigDecimal getLatePayInterestAmt() {
		return latePayInterestAmt;
	}

	public void setLatePayInterestAmt(BigDecimal latePayInterestAmt) {
		this.latePayInterestAmt = latePayInterestAmt;
	}

	public String getCustSalutation() {
		return custSalutation;
	}

	public void setCustSalutation(String custSalutation) {
		this.custSalutation = custSalutation;
	}

	public String getNameOftheBorrowers() {
		return nameOftheBorrowers;
	}

	public void setNameOftheBorrowers(String nameOftheBorrowers) {
		this.nameOftheBorrowers = nameOftheBorrowers;
	}

	public String getCollateralAddress() {
		return collateralAddress;
	}

	public void setCollateralAddress(String collateralAddress) {
		this.collateralAddress = collateralAddress;
	}

	public BigDecimal getGstOnForeClosFees() {
		return gstOnForeClosFees;
	}

	public void setGstOnForeClosFees(BigDecimal gstOnForeClosFees) {
		this.gstOnForeClosFees = gstOnForeClosFees;
	}

	public BigDecimal getChargesIncGST() {
		return chargesIncGST;
	}

	public void setChargesIncGST(BigDecimal chargesIncGST) {
		this.chargesIncGST = chargesIncGST;
	}

	public String getFinAmountInWords() {
		return finAmountInWords;
	}

	public void setFinAmountInWords(String finAmountInWords) {
		this.finAmountInWords = finAmountInWords;
	}

	public String getOutstandingPriInWords() {
		return outstandingPriInWords;
	}

	public void setOutstandingPriInWords(String outstandingPriInWords) {
		this.outstandingPriInWords = outstandingPriInWords;
	}

	public String getLatePayChargesInWords() {
		return latePayChargesInWords;
	}

	public void setLatePayChargesInWords(String latePayChargesInWords) {
		this.latePayChargesInWords = latePayChargesInWords;
	}

	public String getCheqBncChargesInWords() {
		return cheqBncChargesInWords;
	}

	public void setCheqBncChargesInWords(String cheqBncChargesInWords) {
		this.cheqBncChargesInWords = cheqBncChargesInWords;
	}

	public String getInstForTheMonthInWords() {
		return instForTheMonthInWords;
	}

	public void setInstForTheMonthInWords(String instForTheMonthInWords) {
		this.instForTheMonthInWords = instForTheMonthInWords;
	}

	public String getForeClosFeesInWords() {
		return foreClosFeesInWords;
	}

	public void setForeClosFeesInWords(String foreClosFeesInWords) {
		this.foreClosFeesInWords = foreClosFeesInWords;
	}

	public String getPendingInstsInWords() {
		return pendingInstsInWords;
	}

	public void setPendingInstsInWords(String pendingInstsInWords) {
		this.pendingInstsInWords = pendingInstsInWords;
	}

	public String getChargesIncGSTInWords() {
		return chargesIncGSTInWords;
	}

	public void setChargesIncGSTInWords(String chargesIncGSTInWords) {
		this.chargesIncGSTInWords = chargesIncGSTInWords;
	}

	public BigDecimal getForeClosFeesExGST() {
		return foreClosFeesExGST;
	}

	public void setForeClosFeesExGST(BigDecimal foreClosFeesExGST) {
		this.foreClosFeesExGST = foreClosFeesExGST;
	}

	public String getNetReceivableInWords() {
		return netReceivableInWords;
	}

	public void setNetReceivableInWords(String netReceivableInWords) {
		this.netReceivableInWords = netReceivableInWords;
	}

	public String getOtherRefundsInWords() {
		return otherRefundsInWords;
	}

	public void setOtherRefundsInWords(String otherRefundsInWords) {
		this.otherRefundsInWords = otherRefundsInWords;
	}

	public String getFinAssetValueInWords() {
		return finAssetValueInWords;
	}

	public void setFinAssetValueInWords(String finAssetValueInWords) {
		this.finAssetValueInWords = finAssetValueInWords;
	}

	public String getCustAddrHNbr() {
		return custAddrHNbr;
	}

	public void setCustAddrHNbr(String custAddrHNbr) {
		this.custAddrHNbr = custAddrHNbr;
	}

	public String getCustFlatNo() {
		return custFlatNo;
	}

	public void setCustFlatNo(String custFlatNo) {
		this.custFlatNo = custFlatNo;
	}

	public String getCustAddrStreet() {
		return custAddrStreet;
	}

	public void setCustAddrStreet(String custAddrStreet) {
		this.custAddrStreet = custAddrStreet;
	}

	public String getCustAddrCityName() {
		return custAddrCityName;
	}

	public void setCustAddrCityName(String custAddrCityName) {
		this.custAddrCityName = custAddrCityName;
	}

	public String getCustAddrProvinceName() {
		return custAddrProvinceName;
	}

	public void setCustAddrProvinceName(String custAddrProvinceName) {
		this.custAddrProvinceName = custAddrProvinceName;
	}

	public String getCustAddrZIP() {
		return custAddrZIP;
	}

	public void setCustAddrZIP(String custAddrZIP) {
		this.custAddrZIP = custAddrZIP;
	}

	public String getCustAddrCountryName() {
		return custAddrCountryName;
	}

	public void setCustAddrCountryName(String custAddrCountryName) {
		this.custAddrCountryName = custAddrCountryName;
	}

	public BigDecimal getNetReceivableNoRefund() {
		return netReceivableNoRefund;
	}

	public void setNetReceivableNoRefund(BigDecimal netReceivableNoRefund) {
		this.netReceivableNoRefund = netReceivableNoRefund;
	}

	public String getNetReceivableNoRefundInWords() {
		return netReceivableNoRefundInWords;
	}

	public void setNetReceivableNoRefundInWords(String netReceivableNoRefundInWords) {
		this.netReceivableNoRefundInWords = netReceivableNoRefundInWords;
	}

	public String getValidTill() {
		return validTill;
	}

	public void setValidTill(String validTill) {
		this.validTill = validTill;
	}

	public String getTotalRefundsInWords() {
		return totalRefundsInWords;
	}

	public void setTotalRefundsInWords(String totalRefundsInWords) {
		this.totalRefundsInWords = totalRefundsInWords;
	}

	public List<Map<String, Object>> getCollAddressList() {
		return collAddressList;
	}

	public void setCollAddressList(List<Map<String, Object>> collAddressList) {
		this.collAddressList = collAddressList;
	}

	public BigDecimal getOneDayInterest() {
		return oneDayInterest;
	}

	public void setOneDayInterest(BigDecimal oneDayInterest) {
		this.oneDayInterest = oneDayInterest;
	}

	public String getValueDate() {
		return valueDate;
	}

	public void setValueDate(String valueDate) {
		this.valueDate = valueDate;
	}

	public String getCustMobile() {
		return custMobile;
	}

	public void setCustMobile(String custMobile) {
		this.custMobile = custMobile;
	}

	public String getFirstInstDate() {
		return firstInstDate;
	}

	public void setFirstInstDate(String firstInstDate) {
		this.firstInstDate = firstInstDate;
	}

}
