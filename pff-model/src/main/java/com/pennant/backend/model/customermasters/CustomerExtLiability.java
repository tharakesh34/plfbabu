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

	
}
