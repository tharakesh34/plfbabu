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
	private String custShrtName;
	private Date custDOB;
	private String custCtgCode;
	private String custDftBranch;
	private String custSector;
	private String custSubSector;
	private String custNationality;
	private String custDocType;
	private String custDocTitle;
	
	private String custSalutationCode;
	private Date   custPassportExpiry;
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


	
	public CustomerDedup(){
		super();
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
			}else  if(!StringUtils.trimToEmpty(befImage.getCustShrtName()).equals(StringUtils.trim(getCustShrtName()))){
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


	public void setCustCtgCode(String custCtgCode) {
	    this.custCtgCode = custCtgCode;
    }


	public String getCustCtgCode() {
	    return custCtgCode;
    }


	public void setCustDftBranch(String custDftBranch) {
	    this.custDftBranch = custDftBranch;
    }


	public String getCustDftBranch() {
	    return custDftBranch;
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

	public void setCustDocTitle(String custDocTitle) {
	    this.custDocTitle = custDocTitle;
    }

	public String getCustDocTitle() {
	    return custDocTitle;
    }

	public void setCustDocType(String custDocType) {
	    this.custDocType = custDocType;
    }

	public String getCustDocType() {
	    return custDocType;
    }
	
}
