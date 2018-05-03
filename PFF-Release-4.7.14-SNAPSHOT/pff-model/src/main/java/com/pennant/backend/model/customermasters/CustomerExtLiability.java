package com.pennant.backend.model.customermasters;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

@XmlType(propOrder = { "liabilitySeq", "finType", "bankName", "instalmentAmount", "outStandingBal", "originalAmount",
		"finDate", "finStatus" })
@XmlAccessorType(XmlAccessType.NONE)
public class CustomerExtLiability extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;
	
	private long custID = Long.MIN_VALUE;
	@XmlElement
	private int liabilitySeq;
	@XmlElement
	private Date   finDate;
	@XmlElement
	private String finType;
	@XmlElement
	private String bankName;
	private String lovDescBankName;
	private String lovDescFinType;
	@XmlElement
	private BigDecimal 	originalAmount = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal 	instalmentAmount = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal 	outStandingBal = BigDecimal.ZERO;
	@XmlElement
	private String finStatus;
	private String lovDescFinStatus;

	private boolean newRecord=false;
	private String lovValue;
	private CustomerExtLiability befImage;
	private LoggedInUser userDetails;
	private String lovDescCustRecordType;
	private String lovDescCustCIF;
	private String lovDescCustShrtName;
	private String sourceId;
	
	//As per Profectus documnet below fields added
	private BigDecimal roi = BigDecimal.ZERO;
	private int tenure = 0;
	private int tenureBal = 0;
	private int bounceNo = 0;
	private BigDecimal pos = BigDecimal.ZERO;
	private BigDecimal overdue = BigDecimal.ZERO;
	private boolean emiCnsdrForFOIR = false;
	private String source;
	private String checkedBy;
	private String securityDetail;
	private String endUseOfFunds;
	private String repayFrom;
	private String loanpurposedesc;
	private String lovdescrepayfrom;
	
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

	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
		excludeFields.add("sourceId");
		excludeFields.add("lovdescrepayfrom");
		excludeFields.add("loanpurposedesc");
		return excludeFields;
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//	
	
	public long getId() {
		return custID;
	}
	public void setId (long id) {
		this.custID = id;
	}
	
	
	public long getCustID() {
		return custID;
	}
	public void setCustID(long custID) {
		this.custID = custID;
	}

	public int getLiabilitySeq() {
		return liabilitySeq;
	}
	public void setLiabilitySeq(int liabilitySeq) {
		this.liabilitySeq = liabilitySeq;
	}

	public Date getFinDate() {
		return finDate;
	}
	public void setFinDate(Date finDate) {
		this.finDate = finDate;
	}

	public String getFinType() {
		return finType;
	}
	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getLovDescBankName() {
		return lovDescBankName;
	}
	public void setLovDescBankName(String lovDescBankName) {
		this.lovDescBankName = lovDescBankName;
	}

	public String getLovDescFinType() {
		return lovDescFinType;
	}
	public void setLovDescFinType(String lovDescFinType) {
		this.lovDescFinType = lovDescFinType;
	}

	public BigDecimal getOriginalAmount() {
		return originalAmount;
	}
	public void setOriginalAmount(BigDecimal originalAmount) {
		this.originalAmount = originalAmount;
	}

	public BigDecimal getInstalmentAmount() {
		return instalmentAmount;
	}
	public void setInstalmentAmount(BigDecimal instalmentAmount) {
		this.instalmentAmount = instalmentAmount;
	}

	public BigDecimal getOutStandingBal() {
		return outStandingBal;
	}
	public void setOutStandingBal(BigDecimal outStandingBal) {
		this.outStandingBal = outStandingBal;
	}

	public String getFinStatus() {
		return finStatus;
	}
	public void setFinStatus(String finStatus) {
		this.finStatus = finStatus;
	}

	public String getLovDescFinStatus() {
		return lovDescFinStatus;
	}
	public void setLovDescFinStatus(String lovDescFinStatus) {
		this.lovDescFinStatus = lovDescFinStatus;
	}

	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
	
	public String getLovValue() {
		return lovValue;
	}
	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public CustomerExtLiability getBefImage(){
		return this.befImage;
	}
	public void setBefImage(CustomerExtLiability beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getLovDescCustRecordType() {
		return lovDescCustRecordType;
	}
	public void setLovDescCustRecordType(String lovDescCustRecordType) {
		this.lovDescCustRecordType = lovDescCustRecordType;
	}

	public String getLovDescCustCIF() {
		return lovDescCustCIF;
	}
	public void setLovDescCustCIF(String lovDescCustCIF) {
		this.lovDescCustCIF = lovDescCustCIF;
	}

	public String getLovDescCustShrtName() {
		return lovDescCustShrtName;
	}
	public void setLovDescCustShrtName(String lovDescCustShrtName) {
		this.lovDescCustShrtName = lovDescCustShrtName;
	}
	
	public void setLoginDetails(LoggedInUser userDetails){
		setLastMntBy(userDetails.getUserId());
		this.userDetails=userDetails;
		
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public BigDecimal getRoi() {
		return roi;
	}

	public void setRoi(BigDecimal roi) {
		this.roi = roi;
	}

	public int getTenure() {
		return tenure;
	}

	public void setTenure(int tenure) {
		this.tenure = tenure;
	}

	public int getTenureBal() {
		return tenureBal;
	}

	public void setTenureBal(int tenureBal) {
		this.tenureBal = tenureBal;
	}

	public int getBounceNo() {
		return bounceNo;
	}

	public void setBounceNo(int bounceNo) {
		this.bounceNo = bounceNo;
	}

	public BigDecimal getPos() {
		return pos;
	}

	public void setPos(BigDecimal pos) {
		this.pos = pos;
	}

	public BigDecimal getOverdue() {
		return overdue;
	}

	public void setOverdue(BigDecimal overdue) {
		this.overdue = overdue;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getCheckedBy() {
		return checkedBy;
	}

	public void setCheckedBy(String checkedBy) {
		this.checkedBy = checkedBy;
	}

	public String getSecurityDetail() {
		return securityDetail;
	}

	public void setSecurityDetail(String securityDetail) {
		this.securityDetail = securityDetail;
	}

	public String getEndUseOfFunds() {
		return endUseOfFunds;
	}

	public void setEndUseOfFunds(String endUseOfFunds) {
		this.endUseOfFunds = endUseOfFunds;
	}

	public String getRepayFrom() {
		return repayFrom;
	}

	public void setRepayFrom(String repayFrom) {
		this.repayFrom = repayFrom;
	}

	public String getLovdescrepayfrom() {
		return lovdescrepayfrom;
	}

	public void setLovdescrepayfrom(String lovdescrepayfrom) {
		this.lovdescrepayfrom = lovdescrepayfrom;
	}

	public String getLoanpurposedesc() {
		return loanpurposedesc;
	}

	public void setLoanpurposedesc(String loanpurposedesc) {
		this.loanpurposedesc = loanpurposedesc;
	}

	public boolean isEmiCnsdrForFOIR() {
		return emiCnsdrForFOIR;
	}

	public void setEmiCnsdrForFOIR(boolean emiCnsdrForFOIR) {
		this.emiCnsdrForFOIR = emiCnsdrForFOIR;
	}

}
