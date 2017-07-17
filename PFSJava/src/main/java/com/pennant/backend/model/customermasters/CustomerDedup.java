package com.pennant.backend.model.customermasters;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;

/**
 * Model class for the <b>Customer table</b>.<br>
 *
 */
@XmlType(propOrder = { "custCIF", "custCtgCode", "custDftBranch", "custFName","custLName", "custShrtName", "custDOB",
		"custCRCPR", "custSector"})
@XmlAccessorType(XmlAccessType.NONE)
public class CustomerDedup {
	@XmlElement(name="cif")
	private String custCIF ;
	private String finReference;
	@XmlElement(name="lastName")
	private String custLName;
	@XmlElement(name="firstName")
	private String custFName;
	@XmlElement(name="shortName ")
	private String custShrtName;
	private String custMotherMaiden;
	@XmlElement(name="dateofBirth")
	private Date custDOB;
	@XmlElement(name="custPAN")
	private String custCRCPR;
	private String custPassportNo;
	private String mobileNumber;
	private String custNationality;
	private String stage;
	private String dedupRule;
	private boolean override;
	private String overrideUser;
	private String module;
	private String likeCustFName;
	private String likeCustMName;
	private String likeCustLName;
	
	//Audit Purpose Fields
	private long lastMntBy;
	private String roleCode;
	private String recordStatus;
	
	// For Internal use //Not in the table should be exculed for audit
	private long custId =Long.MIN_VALUE;
	private String custCoreBank;
	@XmlElement(name="categoryCode")
	private String custCtgCode;
	@XmlElement(name="defaultBranch")
	private String custDftBranch;
	@XmlElement(name="sector ")
	private String custSector;
	private String custSubSector;
	private String custDocType;
	private String custDocTitle;	
	private String custSalutationCode;
	private Date   custPassportExpiry;
	private String custCOB;
	private String custTradeLicenceNum;
	private String custVisaNum;
	private String phoneNumber;
	private String custPOB;
	private String custResdCountry;
	private String custEMail;
	private String engineNumber;
	private String chassisNumber;
	private CustomerDedup befImage;
	private String queryField;
	private String overridenby;
	private boolean isNewRule;
	private boolean newCustDedupRecord = true;
	private String tradeLicenceNo;
	private String registrationNo;
	private String titleDeedNo;
	private int appScore;
	private String sourceSystem;
	private String address;
	



	
	public CustomerDedup(){
		super();
	}
	
	//Getter and Setter methods
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("custId");
		excludeFields.add("custCoreBank");
		excludeFields.add("custCtgCode");
		excludeFields.add("custDftBranch");
		excludeFields.add("custSector");
		excludeFields.add("custSubSector");
		excludeFields.add("custDocType");
		excludeFields.add("custDocTitle");	
		excludeFields.add("custSalutationCode");
		excludeFields.add("custPassportExpiry");
		excludeFields.add("custCOB");
		excludeFields.add("custTradeLicenceNum");
		excludeFields.add("custVisaNum");
		excludeFields.add("phoneNumber");
		excludeFields.add("custPOB");
		excludeFields.add("custResdCountry");
		excludeFields.add("custEMail");
		excludeFields.add("engineNumber");
		excludeFields.add("chassisNumber");
		excludeFields.add("queryField");
		excludeFields.add("overridenby");
		excludeFields.add("isNewRule");
		excludeFields.add("newCustDedupRecord");
		excludeFields.add("likeCustFName");
		excludeFields.add("likeCustMName");
		excludeFields.add("likeCustLName");
		excludeFields.add("custMotherMaiden");
		excludeFields.add("tradeLicenceNo");
		excludeFields.add("registrationNo");
		excludeFields.add("titleDeedNo");
		excludeFields.add("appScore");
		excludeFields.add("sourceSystem");
		return excludeFields;
	}
	
	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
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

	public String getCustCRCPR() {
		return custCRCPR;
	}

	public void setCustCRCPR(String custCRCPR) {
		this.custCRCPR = custCRCPR;
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
	public String getCustPassportNo() {
		return custPassportNo;
	}
	public CustomerDedup getBefImage() {
		return befImage;
	}
	public void setBefImage(CustomerDedup befImage) {
		this.befImage = befImage;
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
	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public boolean isOverride() {
	    return override;
    }

	public void setOverride(boolean override) {
	    this.override = override;
    }

	
	public String getOverrideUser() {
		return overrideUser;
	}

	public void setOverrideUser(String overrideUser) {
		this.overrideUser = overrideUser;
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

	public String getModule() {
	    return module;
    }

	public void setModule(String module) {
	    this.module = module;
    }
	
	public String getLikeCustFName() {
	    return likeCustFName;
    }

	public void setLikeCustFName(String likeCustFName) {
	    this.likeCustFName = likeCustFName;
    }

	public String getLikeCustMName() {
	    return likeCustMName;
    }

	public void setLikeCustMName(String likeCustMName) {
	    this.likeCustMName = likeCustMName;
    }

	public String getLikeCustLName() {
	    return likeCustLName;
    }

	public void setLikeCustLName(String likeCustLName) {
	    this.likeCustLName = likeCustLName;
    }

	public long getLastMntBy() {
	    return lastMntBy;
    }
	public void setLastMntBy(long lastMntBy) {
	    this.lastMntBy = lastMntBy;
    }

	public String getRoleCode() {
	    return roleCode;
    }
	public void setRoleCode(String roleCode) {
	    this.roleCode = roleCode;
    }

	public String getRecordStatus() {
	    return recordStatus;
    }
	public void setRecordStatus(String recordStatus) {
	    this.recordStatus = recordStatus;
    }

	public long getCustId() {
	    return custId;
    }

	public void setCustId(long custId) {
	    this.custId = custId;
    }

	public String getCustCoreBank() {
	    return custCoreBank;
    }

	public void setCustCoreBank(String custCoreBank) {
	    this.custCoreBank = custCoreBank;
    }

	public String getCustCtgCode() {
	    return custCtgCode;
    }

	public void setCustCtgCode(String custCtgCode) {
	    this.custCtgCode = custCtgCode;
    }

	public String getCustDftBranch() {
		return custDftBranch;
	}

	public void setCustDftBranch(String custDftBranch) {
		this.custDftBranch = custDftBranch;
	}

	public String getCustSector() {
		return custSector;
	}

	public void setCustSector(String custSector) {
		this.custSector = custSector;
	}

	public String getCustSubSector() {
		return custSubSector;
	}

	public void setCustSubSector(String custSubSector) {
		this.custSubSector = custSubSector;
	}

	public String getCustDocType() {
		return custDocType;
	}

	public void setCustDocType(String custDocType) {
		this.custDocType = custDocType;
	}

	public String getCustDocTitle() {
		return custDocTitle;
	}

	public void setCustDocTitle(String custDocTitle) {
		this.custDocTitle = custDocTitle;
	}

	public String getCustSalutationCode() {
		return custSalutationCode;
	}

	public void setCustSalutationCode(String custSalutationCode) {
		this.custSalutationCode = custSalutationCode;
	}

	public Date getCustPassportExpiry() {
		return custPassportExpiry;
	}

	public void setCustPassportExpiry(Date custPassportExpiry) {
		this.custPassportExpiry = custPassportExpiry;
	}

	public String getCustCOB() {
		return custCOB;
	}

	public void setCustCOB(String custCOB) {
		this.custCOB = custCOB;
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

	public String getEngineNumber() {
		return engineNumber;
	}

	public void setEngineNumber(String engineNumber) {
		this.engineNumber = engineNumber;
	}

	public String getChassisNumber() {
		return chassisNumber;
	}

	public void setChassisNumber(String chassisNumber) {
		this.chassisNumber = chassisNumber;
	}

	public void setCustPassportNo(String custPassportNo) {
		this.custPassportNo = custPassportNo;
	}

	public String getCustMotherMaiden() {
		return custMotherMaiden;
	}

	public void setCustMotherMaiden(String custMotherMaiden) {
		this.custMotherMaiden = custMotherMaiden;
	}

	public String getTradeLicenceNo() {
	    return tradeLicenceNo;
    }

	public void setTradeLicenceNo(String tradeLicenceNo) {
	    this.tradeLicenceNo = tradeLicenceNo;
    }

	public String getRegistrationNo() {
	    return registrationNo;
    }

	public void setRegistrationNo(String registrationNo) {
	    this.registrationNo = registrationNo;
    }

	public String getTitleDeedNo() {
	    return titleDeedNo;
    }

	public void setTitleDeedNo(String titleDeedNo) {
	    this.titleDeedNo = titleDeedNo;
    }

	public int getAppScore() {
		return appScore;
	}

	public void setAppScore(int appScore) {
		this.appScore = appScore;
	}

	public String getSourceSystem() {
		return sourceSystem;
	}

	public void setSourceSystem(String sourceSystem) {
		this.sourceSystem = sourceSystem;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	
}
