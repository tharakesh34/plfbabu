package com.pennant.coreinterface.model;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class CoreCustomerDedup {
	private long custId =Long.MIN_VALUE;
	private String custCIF ;
	private String custCoreBank;
	private String custFName;
	private String custLName;
	private String custMotherMaiden;
	private String custCRCPR;
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
	private String mobileNumber;
	private String custPOB;
	private String custResdCountry;
	private String custEMail;
	
	private boolean override;
	private String overrideUser;
	
	private CoreCustomerDedup befImage;
	private List<CoreCustomerDedup> dedupList;
	
	// For Internal use
	private String finReference;
	private String custCtgType;
	private String queryField;
	private String overridenby;
	private boolean isNewRule;
	private boolean newCustDedupRecord = true;
	private String dedupRule;
	private String stage;


	
	public CoreCustomerDedup(){
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
	public String getCustFName() {
		return custFName;
	}

	public void setCustFName(String custFName) {
		this.custFName = custFName;
	}

	public String getCustLName() {
		return custLName;
	}

	public void setCustLName(String custLName) {
		this.custLName = custLName;
	}
	
	public String getCustMotherMaiden() {
		return custMotherMaiden;
	}

	public void setCustMotherMaiden(String custMotherMaiden) {
		this.custMotherMaiden = custMotherMaiden;
	}


	public String getCustCRCPR() {
		return custCRCPR;
	}

	public void setCustCRCPR(String custCRCPR) {
		this.custCRCPR = custCRCPR;
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
	public CoreCustomerDedup getBefImage() {
		return befImage;
	}
	public void setBefImage(CoreCustomerDedup befImage) {
		this.befImage = befImage;
	}
	public List<CoreCustomerDedup> getDedupList() {
		return dedupList;
	}
	public void setDedupList(List<CoreCustomerDedup> dedupList) {
		this.dedupList = dedupList;
	}
	
	public boolean isChanged() {
		boolean changed =false;
		
		if(befImage==null){
			changed=true;
		}else{
			if (!StringUtils.equals(befImage.getCustCIF(), getCustCIF())){
				changed=true;
			}else if(!StringUtils.equals(befImage.getCustCoreBank(), getCustCoreBank())){
				changed=true;
			}else  if(!StringUtils.equals(befImage.getCustShrtName(), getCustShrtName())){
				changed=true;
			}else if(befImage.getCustDOB().equals(getCustDOB())){
				changed=true;
			}else if(!StringUtils.equals(befImage.getCustPassportNo(), getCustPassportNo())){
				changed=true;
			}else if(!StringUtils.equals(befImage.getCustTradeLicenceNum(), getCustTradeLicenceNum())){
				changed=true;
			}else if(!StringUtils.equals(befImage.getCustVisaNum(), getCustVisaNum())){
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
	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
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


	public boolean isOverride() {
	    return override;
    }

	public void setOverride(boolean override) {
	    this.override = override;
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
	
	public String getOverrideUser() {
		return overrideUser;
	}

	public void setOverrideUser(String overrideUser) {
		this.overrideUser = overrideUser;
	}

	public String getCustCtgType() {
		return custCtgType;
	}

	public void setCustCtgType(String custCtgType) {
		this.custCtgType = custCtgType;
	}

	public String getQueryField() {
		return queryField;
	}

	public void setQueryField(String queryField) {
		this.queryField = queryField;
	}

	public String getOverridenby() {
		return overridenby;
	}

	public void setOverridenby(String overridenby) {
		this.overridenby = overridenby;
	}

	public boolean isNewRule() {
		return isNewRule;
	}

	public void setNewRule(boolean isNewRule) {
		this.isNewRule = isNewRule;
	}

	public boolean isNewCustDedupRecord() {
	    return newCustDedupRecord;
    }

	public void setNewCustDedupRecord(boolean newCustDedupRecord) {
	    this.newCustDedupRecord = newCustDedupRecord;
    }

	public String getFinReference() {
	    return finReference;
    }

	public void setFinReference(String finReference) {
	    this.finReference = finReference;
    }

	public String getDedupRule() {
	    return dedupRule;
    }

	public void setDedupRule(String dedupRule) {
	    this.dedupRule = dedupRule;
    }

	public String getStage() {
	    return stage;
    }

	public void setStage(String stage) {
	    this.stage = stage;
    }

}
