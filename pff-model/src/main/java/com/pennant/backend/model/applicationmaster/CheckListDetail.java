/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  CheckListDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-12-2011    														*
 *                                                                  						*
 * Modified Date    :  12-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-12-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.model.applicationmaster;

import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>CheckListDetail table</b>.<br>
 *
 */
public class CheckListDetail extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = -3176600783924484359L;

	private long checkListId = 0;
	private long ansSeqNo=Long.MIN_VALUE;
	private String ansDesc;
	private String ansCond;
	private boolean remarksMand;
	private boolean remarksAllow;
	private boolean docRequired;
	private String docType;
	private boolean DocIsCustDOC;
	private boolean newRecord=false;
	private String lovValue;
	private CheckListDetail befImage;
	private LoggedInUser userDetails;

	private String lovDescCheckListDesc;
	private long lovDescCheckMinCount;
	private long lovDescCheckMaxCount;
	private String lovDescRemarks;
	private FinanceReferenceDetail lovDescFinRefDetail;
	private String lovDescUserRole;
	private String lovDescDocType;
	private String lovDescDocCategory;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("DocIsCustDOC");
		excludeFields.add("lovDescDocType");
		excludeFields.add("lovDescDocCategory");
		return excludeFields;
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public CheckListDetail() {
		super();
	}

	public CheckListDetail(long id) {
		super();
		this.setId(id);
	}

	//Getter and Setter methods

	public long getId() {
		return ansSeqNo;
	}

	public void setId (long id) {
		this.ansSeqNo = id;
	}

	public long getCheckListId() {
		return checkListId;
	}
	public void setCheckListId(long checkListId) {
		this.checkListId = checkListId;
	}




	public long getAnsSeqNo() {
		return ansSeqNo;
	}
	public void setAnsSeqNo(long ansSeqNo) {
		this.ansSeqNo = ansSeqNo;
	}




	public String getAnsDesc() {
		return ansDesc;
	}
	public void setAnsDesc(String ansDesc) {
		this.ansDesc = ansDesc;
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

	public CheckListDetail getBefImage(){
		return this.befImage;
	}

	public void setBefImage(CheckListDetail beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public boolean isDocRequired() {
    	return docRequired;
    }

	public void setDocRequired(boolean docRequired) {
    	this.docRequired = docRequired;
    }
	
	public void setLovDescCheckListDesc(String lovDescCheckListDesc) {
		this.lovDescCheckListDesc = lovDescCheckListDesc;
	}
	public String getLovDescCheckListDesc() {
		return lovDescCheckListDesc;
	}

	public void setLovDescFinRefDetail(FinanceReferenceDetail lovDescFinRefDetail) {
		this.lovDescFinRefDetail = lovDescFinRefDetail;
	}
	public FinanceReferenceDetail getLovDescFinRefDetail() {
		return lovDescFinRefDetail;
	}

	public void setLovDescUserRole(String lovDescUserRole) {
		this.lovDescUserRole = lovDescUserRole;
	}
	public String getLovDescUserRole() {
		return lovDescUserRole;
	}

	public void setLovDescRemarks(String lovDescRemarks) {
		this.lovDescRemarks = lovDescRemarks;
	}
	public String getLovDescRemarks() {
		return lovDescRemarks;
	}

	public void setAnsCond(String ansCond) {
		this.ansCond = ansCond;
	}
	public String getAnsCond() {
		return ansCond;
	}

	public void setRemarksMand(boolean remarksMand) {
		this.remarksMand = remarksMand;
	}
	public boolean isRemarksMand() {
		return remarksMand;
	}

	public void setRemarksAllow(boolean remarksAllow) {
		this.remarksAllow = remarksAllow;
	}
	public boolean isRemarksAllow() {
		return remarksAllow;
	}

	public void setDocType(String docType) {
	    this.docType = docType;
    }
	public String getDocType() {
	    return docType;
    }
	
	public boolean isDocIsCustDOC() {
    	return DocIsCustDOC;
    }

	public void setDocIsCustDOC(boolean docIsCustDOC) {
    	DocIsCustDOC = docIsCustDOC;
    }

	public void setLovDescCheckMinCount(long lovDescCheckMinCount) {
	    this.lovDescCheckMinCount = lovDescCheckMinCount;
    }

	public long getLovDescCheckMinCount() {
	    return lovDescCheckMinCount;
    }

	public void setLovDescCheckMaxCount(long lovDescCheckMaxCount) {
	    this.lovDescCheckMaxCount = lovDescCheckMaxCount;
    }

	public long getLovDescCheckMaxCount() {
	    return lovDescCheckMaxCount;
    }

	private long lovDescFinRefId;

	public long getLovDescFinRefId() {
    	return lovDescFinRefId;
    }

	public void setLovDescFinRefId(long lovDescFinRefId) {
    	this.lovDescFinRefId = lovDescFinRefId;
    }

	public String getLovDescDocType() {
	    return lovDescDocType;
    }

	public void setLovDescDocType(String lovDescDocType) {
	    this.lovDescDocType = lovDescDocType;
    }

	public String getLovDescDocCategory() {
		return lovDescDocCategory;
	}

	public void setLovDescDocCategory(String lovDescDocCategory) {
		this.lovDescDocCategory = lovDescDocCategory;
	}
	
}
