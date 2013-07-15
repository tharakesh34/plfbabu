package com.pennant.backend.model.customermasters;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Model class for the <b>Customer table</b>.<br>
 *
 */
public class CustomerDedup {
	
	private long custId =Long.MIN_VALUE;
	private String custCIF ;
	private String custCoreBank;
	private String custSalutationCode;
	private String custFName;
	private String custMName;
	private String custLName;
	private String custMotherMaiden;
	private String likeCustFName;
	private String likeCustMName;
	private String likeCustLName;
	private String likecustMotherMaiden;
	private String custNationality;
	private Date   custPassportExpiry;
	private String custShrtName;
	private Date custDOB;
	private String custCOB;
	private String custPassportNo;
	private String custTradeLicenceNum;
	private String custVisaNum;
	private String phoneNumber;
	private String custPOB;
	private String custResdCountry;
	private String custEMail;
	private CustomerDedup befImage;
	private List<CustomerDedup> dedupList;
	private String dedupFields;
	
	public CustomerDedup(){
		super();
	}
	
	
	public CustomerDedup(long custId, String custCIF, String custCoreBank,String custSalutationCode,
			String custFName, String custMName, String custLName, String custMotherMaiden, String likecustFName,
			String likecustMName, String likecustLName, String likecustMotherMaiden, String custNationality,
			Date custPassportExpiry, String custShrtName, Date custDOB,String custCOB, String custPassportNo, String custTradeLicenceNum,
			String custVisaNum, String phoneNumber, String custPOB, String custResdCountry, String custEMail) {
		super();
		this.custId = custId;
		this.custCIF = custCIF;
		this.custCoreBank = custCoreBank;
		this.custSalutationCode = custSalutationCode;
		this.custFName = custFName;
		this.custMName = custMName;
		this.custLName = custLName;
		this.custMotherMaiden = custMotherMaiden;
		this.likeCustFName = likecustFName;
		this.likeCustMName = likecustMName;
		this.likeCustLName = likecustLName;
		this.likecustMotherMaiden = likecustMotherMaiden;
		this.custNationality = custNationality;
		this.custPassportExpiry = custPassportExpiry;
		this.custShrtName = custShrtName;
		this.custDOB = custDOB;
		this.custCOB = custCOB;
		this.custPassportNo = custPassportNo;
		this.custTradeLicenceNum = custTradeLicenceNum;
		this.custVisaNum = custVisaNum;
		this.phoneNumber = phoneNumber;
		this.custPOB = custPOB;
		this.custResdCountry = custResdCountry;
		this.custEMail = custEMail;
	}
	
	//Getter and Setter methods
	
	
	public String getCustCIF() {
		return custCIF;
	}
	/**
	 * @return the custId
	 */
	public long getCustId() {
		return custId;
	}
	/**
	 * @param custId the custId to set
	 */
	public void setCustId(long custId) {
		this.custId = custId;
	}
	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}
	public String getCustCoreBank() {
		return custCoreBank;
	}
	public void setCustCoreBank(String custCoreBank) {
		this.custCoreBank = custCoreBank;
	}
	public String getCustSalutationCode() {
		return custSalutationCode;
	}
	public void setCustSalutationCode(String custSalutationCode) {
		this.custSalutationCode = custSalutationCode;
	}
	public String getCustFName() {
		return custFName;
	}
	public void setCustFName(String custFName) {
		this.custFName = custFName;
	}
	public String getCustMName() {
		return custMName;
	}
	public void setCustMName(String custMName) {
		this.custMName = custMName;
	}
	public String getCustLName() {
		return custLName;
	}
	public void setCustLName(String custLName) {
		this.custLName = custLName;
	}
	public String getCustShrtName() {
		return custShrtName;
	}
	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}
	public Date getCustDOB() {
		return custDOB;
	}
	public void setCustDOB(Date custDOB) {
		this.custDOB = custDOB;
	}
	public String getCustCOB() {
		return custCOB;
	}
	public void setCustCOB(String custCOB) {
		this.custCOB = custCOB;
	}
	public String getCustPassportNo() {
		return custPassportNo;
	}
	public void setCustPassportNo(String custPassportNo) {
		this.custPassportNo = custPassportNo;
	}
	public String getCustTradeLicenceNum() {
		return custTradeLicenceNum;
	}
	public void setCustTradeLicenceNum(String custTradeLicenceNum) {
		this.custTradeLicenceNum = custTradeLicenceNum;
	}
	public String getCustVisaNum() {
		return custVisaNum;
	}
	public void setCustVisaNum(String custVisaNum) {
		this.custVisaNum = custVisaNum;
	}
	public CustomerDedup getBefImage() {
		return befImage;
	}
	public void setBefImage(CustomerDedup befImage) {
		this.befImage = befImage;
	}
	public List<CustomerDedup> getDedupList() {
		return dedupList;
	}
	public void setDedupList(List<CustomerDedup> dedupList) {
		this.dedupList = dedupList;
	}
	
	public boolean isChanged() {
		boolean changed =false;
		
		if(befImage==null){
			changed=true;
		}else{
			if (!StringUtils.trimToEmpty(befImage.getCustCIF()).equals(StringUtils.trim(getCustCIF()))){
				changed=true;
			}else if(!StringUtils.trimToEmpty(befImage.getCustCoreBank()).equals(StringUtils.trim(getCustCoreBank()))){
				changed=true;
			}else if(!StringUtils.trimToEmpty(befImage.getCustFName()).equals(StringUtils.trim(getCustFName()))){
				changed=true;
			}else if(!StringUtils.trimToEmpty(befImage.getCustMName()).equals(StringUtils.trim(getCustMName()))){
				changed=true;
			}else if(!StringUtils.trimToEmpty(befImage.getCustLName()).equals(StringUtils.trim(getCustLName()))) {
				changed=true;
			}else if(!StringUtils.trimToEmpty(befImage.getCustShrtName()).equals(StringUtils.trim(getCustShrtName()))){
				changed=true;
			}else if(befImage.getCustDOB().equals(getCustDOB())){
				changed=true;
			}else if(!StringUtils.trimToEmpty(befImage.getCustPassportNo()).equals(StringUtils.trim(getCustPassportNo()))){
				changed=true;
			}else if(!StringUtils.trimToEmpty(befImage.getCustTradeLicenceNum()).equals(StringUtils.trim(getCustTradeLicenceNum()))){
				changed=true;
			}else if(!StringUtils.trimToEmpty(befImage.getCustVisaNum()).equals(StringUtils.trim(getCustVisaNum()))){
				changed=true;
			}
		}
		
		
		return changed;
	}
	public String getCustMotherMaiden() {
		return custMotherMaiden;
	}
	public void setCustMotherMaiden(String custMotherMaiden) {
		this.custMotherMaiden = custMotherMaiden;
	}
	public String getLikeCustFName() {
		return likeCustFName;
	}
	public void setLikeCustFName(String likecustFName) {
		this.likeCustFName = likecustFName;
	}
	public String getLikeCustMName() {
		return likeCustMName;
	}
	public void setLikeCustMName(String likecustMName) {
		this.likeCustMName = likecustMName;
	}
	public String getLikeCustLName() {
		return likeCustLName;
	}
	public void setLikeCustLName(String likecustLName) {
		this.likeCustLName = likecustLName;
	}
	public String getLikecustMotherMaiden() {
		return likecustMotherMaiden;
	}
	public void setLikecustMotherMaiden(String likecustMotherMaiden) {
		this.likecustMotherMaiden = likecustMotherMaiden;
	}
	public String getCustNationality() {
		return custNationality;
	}
	public void setCustNationality(String custNationality) {
		this.custNationality = custNationality;
	}
	public Date getCustPassportExpiry() {
		return custPassportExpiry;
	}
	public void setCustPassportExpiry(Date custPassportExpiry) {
		this.custPassportExpiry = custPassportExpiry;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}		
	public String getCustPOB() {
		return custPOB;
	}
	public void setCustPOB(String custPOB) {
		this.custPOB = custPOB;
	}
	public String getCustResdCountry() {
		return custResdCountry;
	}
	public void setCustResdCountry(String custResdCountry) {
		this.custResdCountry = custResdCountry;
	}
	public String getCustEMail() {
		return custEMail;
	}
	public void setCustEMail(String custEMail) {
		this.custEMail = custEMail;
	}
	public String getDedupFields() {
		return dedupFields;
	}
	public void setDedupFields(String dedupFields) {
		this.dedupFields = dedupFields;
	}
	
}
