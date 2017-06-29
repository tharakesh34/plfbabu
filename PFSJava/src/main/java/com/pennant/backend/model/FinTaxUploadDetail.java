package com.pennant.backend.model;

import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * @author durgaprasad.g
 * 
 */
public class FinTaxUploadDetail extends AbstractWorkflowEntity {
	private static final long	serialVersionUID	= 1L;

	private String				batchReference;
	private String				taxCode;
	private String				aggrementNo;
	private String				applicableFor;
	private String				applicant;
	private boolean				taxExempted			= false;
	private String				addrLine1;
	private String				addrLine2;
	private String				addrLine3;
	private String				addrLine4;
	private String				country;
	private String				province;
	private String				city;
	private String				pinCode;
	private boolean				newRecord			= false;
	private LoggedInUser		userDetails;
	private FinTaxUploadDetail	befImage;
	private int					seqNo;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		
		return excludeFields;
	}

	public String getBatchReference() {
		return batchReference;
	}

	public void setBatchReference(String batchReference) {
		this.batchReference = batchReference;
	}

	public String getTaxCode() {
		return taxCode;
	}

	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}

	public String getAggrementNo() {
		return aggrementNo;
	}

	public void setAggrementNo(String aggrementNo) {
		this.aggrementNo = aggrementNo;
	}

	public String getApplicableFor() {
		return applicableFor;
	}

	public void setApplicableFor(String applicableFor) {
		this.applicableFor = applicableFor;
	}

	public String getApplicant() {
		return applicant;
	}

	public void setApplicant(String applicant) {
		this.applicant = applicant;
	}

	public boolean isTaxExempted() {
		return taxExempted;
	}

	public void setTaxExempted(boolean taxExempted) {
		this.taxExempted = taxExempted;
	}

	public String getAddrLine1() {
		return addrLine1;
	}

	public void setAddrLine1(String addrLine1) {
		this.addrLine1 = addrLine1;
	}

	public String getAddrLine2() {
		return addrLine2;
	}

	public void setAddrLine2(String addrLine2) {
		this.addrLine2 = addrLine2;
	}

	public String getAddrLine3() {
		return addrLine3;
	}

	public void setAddrLine3(String addrLine3) {
		this.addrLine3 = addrLine3;
	}

	public String getAddrLine4() {
		return addrLine4;
	}

	public void setAddrLine4(String addrLine4) {
		this.addrLine4 = addrLine4;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPinCode() {
		return pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public FinTaxUploadDetail getBefImage() {
		return befImage;
	}

	public void setBefImage(FinTaxUploadDetail befImage) {
		this.befImage = befImage;
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public int getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}

}
