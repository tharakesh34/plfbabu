package com.pennant.backend.model.applicationmaster;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class PoliceCaseDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 384180539764860246L;
	
	private String 			custCIF; 
	private String 			custFName;					
	private String 			custLName;					
	private String 			mobileNumber;				
	private String 			custNationality;				
	private String 			custCRCPR;					
	private String 			custPassportNo;	
	private Date 			custDOB;
	private String 			custProduct;
	private boolean 		newRecord = false;
	private String 			lovValue;
	private PoliceCaseDetail befImage;
	private LoggedInUser  userDetails;
	private boolean		 	  override;
	private String 			  policeCaseRule;
	private boolean 		  newPolicecaseRecord = true;
	private String			  custCtgCode;
	private String    	 	  finReference;
	private String 		 	  overrideUser;
	private String        	  rules;
	private boolean 		  newRule;
	private String 			  overridenby;
	private String 			 lovDescNationalityDesc;
	private String 			 likeCustFName;
	private String 			 likeCustMName;
	private String 			 likeCustLName;

	public boolean isNew() {
		return isNewRecord();
	}

	public PoliceCaseDetail() {
		super();
	}

	public PoliceCaseDetail(String id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
		excludeFields.add("finReference");
		excludeFields.add("overrideUser");
		excludeFields.add("rules");
		excludeFields.add("newRule");
		excludeFields.add("overridenby");
		excludeFields.add("policeCaseRule");
		excludeFields.add("custCtgCode");
		excludeFields.add("lovDescNationalityDesc");
		excludeFields.add("newPolicecaseRecord");
		excludeFields.add("likeCustFName");
		excludeFields.add("likeCustMName");
		excludeFields.add("likeCustLName");
		excludeFields.add("override");
		return excludeFields;
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return custCIF;
	}	
	public void setId (String id) {
		this.custCIF = id;
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
	public String getCustPassportNo() {
		return custPassportNo;
	}
	public void setCustPassportNo(String custPassportNo) {
		this.custPassportNo = custPassportNo;
	}
	public Date getCustDOB() {
		return custDOB;
	}
	public void setCustDOB(Date custDOB) {
		this.custDOB = custDOB;
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
	public PoliceCaseDetail getBefImage() {
		return befImage;
	}
	public void setBefImage(PoliceCaseDetail befImage) {
		this.befImage = befImage;
	}
	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getCustProduct() {
	    return custProduct;
    }

	public void setCustProduct(String custProduct) {
	    this.custProduct = custProduct;
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

	public String getOverrideUser() {
		return overrideUser;
	}

	public void setOverrideUser(String overrideUser) {
		this.overrideUser = overrideUser;
	}

	public String getRules() {
		return rules;
	}

	public void setRules(String rules) {
		this.rules = rules;
	}

	public boolean isNewRule() {
		return newRule;
	}

	public void setNewRule(boolean newRule) {
		this.newRule = newRule;
	}

	public String getOverridenby() {
		return overridenby;
	}

	public void setOverridenby(String overridenby) {
		this.overridenby = overridenby;
	}

	public String getPoliceCaseRule() {
	    return policeCaseRule;
    }

	public void setPoliceCaseRule(String policeCaseRule) {
	    this.policeCaseRule = policeCaseRule;
    }

	public String getCustCtgCode() {
		return custCtgCode;
	}
	public void setCustCtgCode(String custCtgCode) {
		this.custCtgCode = custCtgCode;
	}

	public String getLovDescNationalityDesc() {
	    return lovDescNationalityDesc;
    }

	public void setLovDescNationalityDesc(String lovDescNationalityDesc) {
	    this.lovDescNationalityDesc = lovDescNationalityDesc;
    }

	public boolean isNewPolicecaseRecord() {
	    return newPolicecaseRecord;
    }

	public void setNewPolicecaseRecord(boolean newPolicecaseRecord) {
	    this.newPolicecaseRecord = newPolicecaseRecord;
    }

	public String getMobileNumber() {
	    return mobileNumber;
    }

	public void setMobileNumber(String mobileNumber) {
	    this.mobileNumber = mobileNumber;
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
	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

}