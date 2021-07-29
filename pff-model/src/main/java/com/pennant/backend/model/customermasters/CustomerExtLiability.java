package com.pennant.backend.model.customermasters;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

@XmlType(propOrder = { "seqNo", "finType", "loanBankName", "instalmentAmount", "outstandingBalance", "originalAmount",
		"finDate", "finStatus" })
public class CustomerExtLiability extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id;
	private long linkId;
	@XmlElement(name = "liabilitySeq")
	private int seqNo;
	private long custId;
	private String custCif;
	private String custShrtName;
	@XmlElement
	private String finType;
	@XmlElement
	private Date finDate;
	@XmlElement(name = "bankName")
	private String loanBank;
	@XmlElement
	private BigDecimal rateOfInterest = BigDecimal.ZERO;
	@XmlElement
	private int tenure;
	@XmlElement
	private BigDecimal instalmentAmount = BigDecimal.ZERO;
	@XmlElement(name = "outStandingBal")
	private BigDecimal outstandingBalance = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal originalAmount = BigDecimal.ZERO;
	@XmlElement
	private int balanceTenure;
	@XmlElement
	private int bounceInstalments;
	@XmlElement
	private BigDecimal principalOutstanding = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal overdueAmount = BigDecimal.ZERO;
	@XmlElement
	private String finStatus;
	@XmlElement
	private boolean foir;
	@XmlElement
	private int source;
	@XmlElement
	private int checkedBy;
	@XmlElement
	private String securityDetails;
	@XmlElement
	private String loanPurpose;
	@XmlElement
	private String repayBank;
	private String loanBankName;
	private String finTypeDesc;
	private String custStatusDesc;
	private String repayBankName;
	@XmlElement
	private String otherFinInstitute;
	private CustomerExtLiability befImage;
	private LoggedInUser userDetails;
	private String sourceId;
	private String inputSource;
	private int custType = 1;

	@XmlElement
	private BigDecimal imputedEmi = BigDecimal.ZERO;
	@XmlElement
	private String ownerShip;
	@XmlElement
	private boolean lastTwentyFourMonths;
	@XmlElement
	private boolean lastSixMonths;
	@XmlElement
	private boolean lastThreeMonths;

	@XmlElement
	private BigDecimal currentOverDue = BigDecimal.ZERO;

	@XmlElement
	private String repayFromAccNo;
	@XmlElement
	private String remarks;
	@XmlElement
	private int noOfBouncesInSixMonths;
	@XmlElement
	private int noOfBouncesInTwelveMonths;
	@XmlElement
	private boolean consideredBasedOnRTR;
	@XmlElement
	private int mob;

	@XmlElementWrapper(name = "extLiabilitiesPayments")
	@XmlElement(name = "extLiabilitiesPayment")
	private List<ExtLiabilityPaymentdetails> extLiabilitiesPayments = new ArrayList<>();

	public boolean isNew() {
		return isNewRecord();
	}

	public CustomerExtLiability() {
		super();
	}

	public CustomerExtLiability(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("custId");
		excludeFields.add("custCif");
		excludeFields.add("custShrtName");
		excludeFields.add("liabilityLinkId");
		excludeFields.add("loanBankName");
		excludeFields.add("finTypeDesc");
		excludeFields.add("custStatusDesc");
		excludeFields.add("repayBankName");
		excludeFields.add("liabilitySeq");
		excludeFields.add("sourceId");
		excludeFields.add("inputSource");
		excludeFields.add("custType");
		excludeFields.add("extLiabilitiesPayments");
		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getLinkId() {
		return linkId;
	}

	public void setLinkId(long linkId) {
		this.linkId = linkId;
	}

	public long getCustId() {
		return custId;
	}

	public void setCustId(long custId) {
		this.custId = custId;
	}

	public String getCustCif() {
		return custCif;
	}

	public void setCustCif(String custCif) {
		this.custCif = custCif;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public Date getFinDate() {
		return finDate;
	}

	public void setFinDate(Date finDate) {
		this.finDate = finDate;
	}

	public String getLoanBank() {
		return loanBank;
	}

	public void setLoanBank(String loanBank) {
		this.loanBank = loanBank;
	}

	public BigDecimal getRateOfInterest() {
		return rateOfInterest;
	}

	public void setRateOfInterest(BigDecimal rateOfInterest) {
		this.rateOfInterest = rateOfInterest;
	}

	public int getTenure() {
		return tenure;
	}

	public void setTenure(int tenure) {
		this.tenure = tenure;
	}

	public BigDecimal getInstalmentAmount() {
		return instalmentAmount;
	}

	public void setInstalmentAmount(BigDecimal instalmentAmount) {
		this.instalmentAmount = instalmentAmount;
	}

	public BigDecimal getOutstandingBalance() {
		return outstandingBalance;
	}

	public void setOutstandingBalance(BigDecimal outstandingBalance) {
		this.outstandingBalance = outstandingBalance;
	}

	public int getBalanceTenure() {
		return balanceTenure;
	}

	public void setBalanceTenure(int balanceTenure) {
		this.balanceTenure = balanceTenure;
	}

	public int getBounceInstalments() {
		return bounceInstalments;
	}

	public void setBounceInstalments(int bounceInstalments) {
		this.bounceInstalments = bounceInstalments;
	}

	public BigDecimal getPrincipalOutstanding() {
		return principalOutstanding;
	}

	public void setPrincipalOutstanding(BigDecimal principalOutstanding) {
		this.principalOutstanding = principalOutstanding;
	}

	public BigDecimal getOverdueAmount() {
		return overdueAmount;
	}

	public void setOverdueAmount(BigDecimal overdueAmount) {
		this.overdueAmount = overdueAmount;
	}

	public String getFinStatus() {
		return finStatus;
	}

	public void setFinStatus(String finStatus) {
		this.finStatus = finStatus;
	}

	public boolean isFoir() {
		return foir;
	}

	public void setFoir(boolean foir) {
		this.foir = foir;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public int getCheckedBy() {
		return checkedBy;
	}

	public void setCheckedBy(int checkedBy) {
		this.checkedBy = checkedBy;
	}

	public String getSecurityDetails() {
		return securityDetails;
	}

	public void setSecurityDetails(String securityDetails) {
		this.securityDetails = securityDetails;
	}

	public String getLoanPurpose() {
		return loanPurpose;
	}

	public void setLoanPurpose(String loanPurpose) {
		this.loanPurpose = loanPurpose;
	}

	public String getRepayBank() {
		return repayBank;
	}

	public void setRepayBank(String repayBank) {
		this.repayBank = repayBank;
	}

	public String getLoanBankName() {
		return loanBankName;
	}

	public void setLoanBankName(String loanBankName) {
		this.loanBankName = loanBankName;
	}

	public String getFinTypeDesc() {
		return finTypeDesc;
	}

	public void setFinTypeDesc(String finTypeDesc) {
		this.finTypeDesc = finTypeDesc;
	}

	public String getCustStatusDesc() {
		return custStatusDesc;
	}

	public void setCustStatusDesc(String custStatusDesc) {
		this.custStatusDesc = custStatusDesc;
	}

	public String getRepayBankName() {
		return repayBankName;
	}

	public void setRepayBankName(String repayBankName) {
		this.repayBankName = repayBankName;
	}

	public CustomerExtLiability getBefImage() {
		return befImage;
	}

	public void setBefImage(CustomerExtLiability befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public BigDecimal getOriginalAmount() {
		return originalAmount;
	}

	public void setOriginalAmount(BigDecimal originalAmount) {
		this.originalAmount = originalAmount;
	}

	public int getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}

	public String getInputSource() {
		return inputSource;
	}

	public void setInputSource(String inputSource) {
		this.inputSource = inputSource;
	}

	public int getCustType() {
		return custType;
	}

	public void setCustType(int custType) {
		this.custType = custType;
	}

	public void setLoginDetails(LoggedInUser userDetails) {
		setLastMntBy(userDetails.getUserId());
		this.userDetails = userDetails;
	}

	public String getOtherFinInstitute() {
		return otherFinInstitute;
	}

	public void setOtherFinInstitute(String otherFinInstitute) {
		this.otherFinInstitute = otherFinInstitute;
	}

	public List<ExtLiabilityPaymentdetails> getExtLiabilitiesPayments() {
		return extLiabilitiesPayments;
	}

	public void setExtLiabilitiesPayments(List<ExtLiabilityPaymentdetails> extLiabilitiesPayments) {
		this.extLiabilitiesPayments = extLiabilitiesPayments;
	}

	public BigDecimal getImputedEmi() {
		return imputedEmi;
	}

	public void setImputedEmi(BigDecimal imputedEmi) {
		this.imputedEmi = imputedEmi;
	}

	public String getOwnerShip() {
		return ownerShip;
	}

	public void setOwnerShip(String ownerShip) {
		this.ownerShip = ownerShip;
	}

	public boolean isLastTwentyFourMonths() {
		return lastTwentyFourMonths;
	}

	public void setLastTwentyFourMonths(boolean lastTwentyFourMonths) {
		this.lastTwentyFourMonths = lastTwentyFourMonths;
	}

	public boolean isLastSixMonths() {
		return lastSixMonths;
	}

	public void setLastSixMonths(boolean lastSixMonths) {
		this.lastSixMonths = lastSixMonths;
	}

	public BigDecimal getCurrentOverDue() {
		return currentOverDue;
	}

	public void setCurrentOverDue(BigDecimal currentOverDue) {
		this.currentOverDue = currentOverDue;
	}

	public boolean isLastThreeMonths() {
		return lastThreeMonths;
	}

	public void setLastThreeMonths(boolean lastThreeMonths) {
		this.lastThreeMonths = lastThreeMonths;
	}

	public boolean isConsideredBasedOnRTR() {
		return consideredBasedOnRTR;
	}

	public void setConsideredBasedOnRTR(boolean consideredBasedOnRTR) {
		this.consideredBasedOnRTR = consideredBasedOnRTR;
	}

	public int getMob() {
		return mob;
	}

	public void setMob(int mob) {
		this.mob = mob;
	}

	public String getRepayFromAccNo() {
		return repayFromAccNo;
	}

	public void setRepayFromAccNo(String repayFromAccNo) {
		this.repayFromAccNo = repayFromAccNo;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public int getNoOfBouncesInSixMonths() {
		return noOfBouncesInSixMonths;
	}

	public void setNoOfBouncesInSixMonths(int noOfBouncesInSixMonths) {
		this.noOfBouncesInSixMonths = noOfBouncesInSixMonths;
	}

	public int getNoOfBouncesInTwelveMonths() {
		return noOfBouncesInTwelveMonths;
	}

	public void setNoOfBouncesInTwelveMonths(int noOfBouncesInTwelveMonths) {
		this.noOfBouncesInTwelveMonths = noOfBouncesInTwelveMonths;
	}

}