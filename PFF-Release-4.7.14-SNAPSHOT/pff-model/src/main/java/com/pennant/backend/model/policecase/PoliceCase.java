package com.pennant.backend.model.policecase;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class PoliceCase implements Serializable {
	private static final long serialVersionUID = 384180539764860246L;
	private String    	finReference;
	private String 		custCIF; 						
	private String 		custFName;					
	private String 		custLName;					
	private Date 		custDOB;
	private String 		custCRCPR;					
	private String 		mobileNumber;				
	private String 		custNationality;				
	private String 		custProduct;
	private String 		policeCaseRule;
	private boolean		override;
	private String 		overrideUser;
	private String 		custPassportNo;	
	private String		custCtgcCode;
	private String       rules;
	private boolean 	newRule;
	private String 		overridenby;
	private boolean 	newPolicecaseRecord = true;
	//Audit Purpose Fields
	private long lastMntBy;
	private String roleCode;
	private String recordStatus;
	
	public PoliceCase() {
		
	}
	
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("overrideUser");
		excludeFields.add("rules");
		excludeFields.add("newRule");
		excludeFields.add("overridenby");
		excludeFields.add("policeCaseRule");
		excludeFields.add("custCtgcCode");
		excludeFields.add("lovDescNationalityDesc");
		excludeFields.add("newPolicecaseRecord");
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
	
	public String getCustNationality() {
		return custNationality;
	}
	public void setCustNationality(String custNationality) {
		this.custNationality = custNationality;
	}
	
	public String getCustCRCPR() {
		return custCRCPR;
	}
	public void setCustCRCPR(String custCRCPR) {
		this.custCRCPR = custCRCPR;
	}
	
	
	public Date getCustDOB() {
		return custDOB;
	}
	public void setCustDOB(Date custDOB) {
		this.custDOB = custDOB;
	}
	
	public String getCustProduct() {
		return custProduct;
	}
	public void setCustProduct(String custProduct) {
		this.custProduct = custProduct;
	}
	
	public String getPoliceCaseRule() {
		return policeCaseRule;
	}
	public void setPoliceCaseRule(String policeCaseRule) {
		this.policeCaseRule = policeCaseRule;
	}
	
	public String getCustCtgcCode() {
		return custCtgcCode;
	}
	public void setCustCtgcCode(String custCtgcCode) {
		this.custCtgcCode = custCtgcCode;
	}

	public boolean isOverride() {
		return override;
	}
	public void setOverride(boolean override) {
		this.override = override;
	}
	
	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}
	

	public String getRules() {
	    return rules;
    }
	public void setRules(String rules) {
	    this.rules = rules;
    }
	public String getCustPassportNo() {
	    return custPassportNo;
    }
	public void setCustPassportNo(String custPassportNo) {
	    this.custPassportNo = custPassportNo;
    }


	public String getOverrideUser() {
	    return overrideUser;
    }

	public void setOverrideUser(String overrideUser) {
	    this.overrideUser = overrideUser;
    }

	public String getOverridenby() {
	    return overridenby;
    }

	public void setOverridenby(String overridenby) {
	    this.overridenby = overridenby;
    }

	public boolean isNewRule() {
	    return newRule;
    }
	public void setNewRule(boolean newRule) {
	    this.newRule = newRule;
    }
	public boolean isNewPolicecaseRecord() {
	    return newPolicecaseRecord;
    }
	public void setNewPolicecaseRecord(boolean newPolicecaseRecord) {
	    this.newPolicecaseRecord = newPolicecaseRecord;
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


	public String getMobileNumber() {
	    return mobileNumber;
    }


	public void setMobileNumber(String mobileNumber) {
	    this.mobileNumber = mobileNumber;
    }

}
