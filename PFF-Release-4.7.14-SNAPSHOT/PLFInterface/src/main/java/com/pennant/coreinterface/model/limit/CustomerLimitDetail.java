package com.pennant.coreinterface.model.limit;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.pennant.mq.util.InterfaceMasterConfigUtil;

@XmlRootElement(name = "LimitDetailsReply")
public class CustomerLimitDetail implements Serializable {

	private static final long serialVersionUID = -5384003278372748606L;
	
	// request fields
	private String referenceNumber;
	private String limitRef;
	private String branchCode;
	
	// response fields
	private String custCIF;
	private String limitDesc;
	private String revolvingType;
	private Date limitExpiryDate;
	private String limitCcy;
	private String approvedLimitCcy;
	private BigDecimal approvedLimit = BigDecimal.ZERO;
	private String outstandingAmtCcy;
	private BigDecimal outstandingAmt = BigDecimal.ZERO;
	private String blockedAmtCcy;
	private BigDecimal blockedAmt = BigDecimal.ZERO;
	private String reservedAmtCcy;
	private BigDecimal reservedAmt = BigDecimal.ZERO;
	private String availableAmtCcy;
	private BigDecimal availableAmt = BigDecimal.ZERO;
	private int tenor;
	private String tenorUnit;
	private String repricingFrequency;
	private String repaymentTerm;
	private String limitAvailabilityPeriod;
	private Date finalMaturityDate;
	private String pricingIndex;
	private BigDecimal spread = BigDecimal.ZERO;
	private BigDecimal minimumPrice = BigDecimal.ZERO;
	private BigDecimal maximumPricing = BigDecimal.ZERO;
	private String pricingSchema;
	private BigDecimal commissionPercent = BigDecimal.ZERO;
	private BigDecimal commissionAmount = BigDecimal.ZERO;
	private String commissionFreq;
	private String studyFee;
	private BigDecimal margin = BigDecimal.ZERO;
	private BigDecimal hamJad = BigDecimal.ZERO;
	private BigDecimal otherFeePercent = BigDecimal.ZERO;
	private BigDecimal otherFeeAmount = BigDecimal.ZERO;
	private String covenant;
	private String termsConditions;
	private String notes = "Test";
	private String returnCode;
	private String returnText;
	private String timeStamp;

	public CustomerLimitDetail() {
		super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	@XmlElement(name = "ReferenceNum")
	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	@XmlElement(name = "LimitRef")
	public String getLimitRef() {
		return limitRef;
	}

	public void setLimitRef(String limitRef) {
		this.limitRef = limitRef;
	}
	
	@XmlElement(name = "BranchCode")
	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
	
	@XmlElement(name = "LimitDesc")
	public String getLimitDesc() {
		return limitDesc;
	}

	public void setLimitDesc(String limitDesc) {
		this.limitDesc = limitDesc;
	}

	@XmlElement(name = "Rev_Nrev")
	public String getRevolvingType() {
		return revolvingType;
	}

	public void setRevolvingType(String revolvingType) {
		this.revolvingType = revolvingType;
	}

	@XmlElement(name = "LimitExpiryDate")
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	public Date getLimitExpiryDate() {
		return limitExpiryDate;
	}

	public void setLimitExpiryDate(Date limitExpiryDate) {
		this.limitExpiryDate = limitExpiryDate;
	}

	@XmlElement(name = "Currency")
	public String getLimitCcy() {
		return limitCcy;
	}

	public void setLimitCcy(String limitCcy) {
		this.limitCcy = limitCcy;
	}

	@XmlElement(name = "ApprovedLimitCurrency")
	public String getApprovedLimitCcy() {
		return approvedLimitCcy;
	}

	public void setApprovedLimitCcy(String approvedLimitCcy) {
		this.approvedLimitCcy = approvedLimitCcy;
	}

	@XmlElement(name = "ApprovedLimit")
	public BigDecimal getApprovedLimit() {
		return approvedLimit;
	}

	public void setApprovedLimit(BigDecimal approvedLimit) {
		this.approvedLimit = approvedLimit;
	}

	@XmlElement(name = "OutstandingAmountCurrency")
	public String getOutstandingAmtCcy() {
		return outstandingAmtCcy;
	}

	public void setOutstandingAmtCcy(String outstandingAmtCcy) {
		this.outstandingAmtCcy = outstandingAmtCcy;
	}

	@XmlElement(name = "OutstandingAmount")
	public BigDecimal getOutstandingAmt() {
		return outstandingAmt;
	}

	public void setOutstandingAmt(BigDecimal outstandingAmt) {
		this.outstandingAmt = outstandingAmt;
	}

	@XmlElement(name = "BlockedAmountCurrency")
	public String getBlockedAmtCcy() {
		return blockedAmtCcy;
	}

	public void setBlockedAmtCcy(String blockedAmtCcy) {
		this.blockedAmtCcy = blockedAmtCcy;
	}

	@XmlElement(name = "BlockedAmount")
	public BigDecimal getBlockedAmt() {
		return blockedAmt;
	}

	public void setBlockedAmt(BigDecimal blockedAmt) {
		this.blockedAmt = blockedAmt;
	}

	@XmlElement(name = "ReservedAmountCurrency")
	public String getReservedAmtCcy() {
		return reservedAmtCcy;
	}

	public void setReservedAmtCcy(String reservedAmtCcy) {
		this.reservedAmtCcy = reservedAmtCcy;
	}

	@XmlElement(name = "ReservedAmount")
	public BigDecimal getReservedAmt() {
		return reservedAmt;
	}

	public void setReservedAmt(BigDecimal reservedAmt) {
		this.reservedAmt = reservedAmt;
	}

	@XmlElement(name = "AvailableAmountCurrency")
	public String getAvailableAmtCcy() {
		return availableAmtCcy;
	}

	public void setAvailableAmtCcy(String availableAmtCcy) {
		this.availableAmtCcy = availableAmtCcy;
	}

	@XmlElement(name = "AvailableAmount")
	public BigDecimal getAvailableAmt() {
		return availableAmt;
	}

	public void setAvailableAmt(BigDecimal availableAmt) {
		this.availableAmt = availableAmt;
	}

	@XmlElement(name = "Tenor")
	public int getTenor() {
		return tenor;
	}

	public void setTenor(int tenor) {
		this.tenor = tenor;
	}

	@XmlElement(name = "TenorUnit")
	public String getTenorUnit() {
		return tenorUnit;
	}

	public void setTenorUnit(String tenorUnit) {
		this.tenorUnit = tenorUnit;
	}

	@XmlElement(name = "RepricingFrequency")
	public String getRepricingFrequency() {
		return repricingFrequency;
	}

	public void setRepricingFrequency(String repricingFrequency) {
		this.repricingFrequency = repricingFrequency;
	}

	@XmlElement(name = "RepaymentTerm")
	public String getRepaymentTerm() {
		return repaymentTerm;
	}

	public void setRepaymentTerm(String repaymentTerm) {
		this.repaymentTerm = repaymentTerm;
	}

	@XmlElement(name = "LimitAvailabilityPeriod")
	public String getLimitAvailabilityPeriod() {
		return limitAvailabilityPeriod;
	}

	public void setLimitAvailabilityPeriod(String limitAvailabilityPeriod) {
		this.limitAvailabilityPeriod = limitAvailabilityPeriod;
	}

	@XmlElement(name = "FinalMaturityDate")
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	public Date getFinalMaturityDate() {
		return finalMaturityDate;
	}

	public void setFinalMaturityDate(Date finalMaturityDate) {
		this.finalMaturityDate = finalMaturityDate;
	}

	@XmlElement(name = "PricingIndex")
	public String getPricingIndex() {
		return pricingIndex;
	}

	public void setPricingIndex(String pricingIndex) {
		this.pricingIndex = pricingIndex;
	}

	@XmlElement(name = "Spread")
	public BigDecimal getSpread() {
		return spread;
	}

	public void setSpread(BigDecimal spread) {
		this.spread = spread;
	}

	@XmlElement(name = "MinimumPrice")
	public BigDecimal getMinimumPrice() {
		return minimumPrice;
	}

	@XmlElement(name = "MaximumPricing")
	public BigDecimal getMaximumPricing() {
		return maximumPricing;
	}

	public void setMaximumPricing(BigDecimal maximumPricing) {
		this.maximumPricing = maximumPricing;
	}

	public void setMinimumPrice(BigDecimal minimumPrice) {
		this.minimumPrice = minimumPrice;
	}

	@XmlElement(name = "PricingSchema")
	public String getPricingSchema() {
		return pricingSchema;
	}

	public void setPricingSchema(String pricingSchema) {
		this.pricingSchema = pricingSchema;
	}

	@XmlElement(name = "CommissionPercent")
	public BigDecimal getCommissionPercent() {
		return commissionPercent;
	}

	public void setCommissionPercent(BigDecimal commissionPercent) {
		this.commissionPercent = commissionPercent;
	}

	@XmlElement(name = "CommissionAmount")
	public BigDecimal getCommissionAmount() {
		return commissionAmount;
	}

	public void setCommissionAmount(BigDecimal commissionAmount) {
		this.commissionAmount = commissionAmount;
	}

	@XmlElement(name = "CommissionFreq")
	public String getCommissionFreq() {
		return commissionFreq;
	}

	public void setCommissionFreq(String commissionFreq) {
		this.commissionFreq = commissionFreq;
	}

	@XmlElement(name = "StudyFee")
	public String getStudyFee() {
		return studyFee;
	}

	public void setStudyFee(String studyFee) {
		this.studyFee = studyFee;
	}

	@XmlElement(name = "Margin")
	public BigDecimal getMargin() {
		return margin;
	}

	public void setMargin(BigDecimal margin) {
		this.margin = margin;
	}

	@XmlElement(name = "HamJad")
	public BigDecimal getHamJad() {
		return hamJad;
	}

	public void setHamJad(BigDecimal hamJad) {
		this.hamJad = hamJad;
	}

	@XmlElement(name = "OtherFeePercent")
	public BigDecimal getOtherFeePercent() {
		return otherFeePercent;
	}

	public void setOtherFeePercent(BigDecimal otherFeePercent) {
		this.otherFeePercent = otherFeePercent;
	}

	@XmlElement(name = "OtherFeeAmount")
	public BigDecimal getOtherFeeAmount() {
		return otherFeeAmount;
	}

	public void setOtherFeeAmount(BigDecimal otherFeeAmount) {
		this.otherFeeAmount = otherFeeAmount;
	}

	@XmlElement(name = "Covenant")
	public String getCovenant() {
		return covenant;
	}

	public void setCovenant(String covenant) {
		this.covenant = covenant;
	}

	@XmlElement(name = "TermsConditions")
	public String getTermsConditions() {
		return termsConditions;
	}

	public void setTermsConditions(String termsConditions) {
		this.termsConditions = termsConditions;
	}

	@XmlElement(name = "Note")
	public String getNotes() {
		return notes;
	}

	public void setNotes(String note) {
		this.notes = note;
	}

	@XmlElement(name = "CustRef")
	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	@XmlElement(name = "ReturnCode")
	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	@XmlElement(name = "ReturnText")
	public String getReturnText() {
		return returnText;
	}

	public void setReturnText(String returnText) {
		this.returnText = returnText;
	}

	@XmlElement(name = "TimeStamp")
	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	private static class DateFormatterAdapter extends XmlAdapter<String, Date> {
		private final SimpleDateFormat dateFormat = new SimpleDateFormat(InterfaceMasterConfigUtil.DBDateFormat);

		@Override
		public Date unmarshal(final String v) throws Exception {
			return dateFormat.parse(v);
		}

		@Override
		public String marshal(final Date v) throws Exception {
			return dateFormat.format(v);
		}
	}
	
}
