/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ******************************************************************************************** 
 * FILE HEADER *
 ******************************************************************************************** 
 * * FileName : CustomerRating.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * * Modified Date
 * : 26-05-2011 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.backend.model.customermasters;

import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>CustomerRating table</b>.<br>
 * 
 */
public class CustomerRating extends AbstractWorkflowEntity {

	private static final long serialVersionUID = -5720554941556360647L;

	private long custID = Long.MIN_VALUE;
	private String lovDescCustShrtName;
	private String custRatingType;
	private String lovDescCustRatingTypeName;
	private String custRatingCode;
	private String lovDesccustRatingCodeDesc;
	private String custRating;
	private String lovDescCustRatingName;
	private boolean valueType;
	private boolean newRecord;
	private String lovValue;
	private CustomerRating befImage;
	private LoggedInUser userDetails;
	private String lovDescCustRecordType;
	private String lovDescCustCIF;

	public boolean isNew() {
		return isNewRecord();
	}

	public CustomerRating() {
		super();
	}

	public CustomerRating(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
		return excludeFields;
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getId() {
		return custID;
	}

	public void setId(long id) {
		this.custID = id;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public String getLovDescCustShrtName() {
		return lovDescCustShrtName;
	}

	public void setLovDescCustShrtName(String lovDescCustShrtName) {
		this.lovDescCustShrtName = lovDescCustShrtName;
	}

	public String getCustRatingType() {
		return custRatingType;
	}

	public void setCustRatingType(String custRatingType) {
		this.custRatingType = custRatingType;
	}

	public String getLovDescCustRatingTypeName() {
		return this.lovDescCustRatingTypeName;
	}

	public void setLovDescCustRatingTypeName(String lovDescCustRatingTypeName) {
		this.lovDescCustRatingTypeName = lovDescCustRatingTypeName;
	}

	public String getCustRatingCode() {
		return custRatingCode;
	}

	public void setCustRatingCode(String custRatingCode) {
		this.custRatingCode = custRatingCode;
	}
	public String getCustRating() {
		return custRating;
	}

	public void setCustRating(String custRating) {
		this.custRating = custRating;
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

	public String getLovDesccustRatingCodeDesc() {
    	return lovDesccustRatingCodeDesc;
    }

	public void setLovDesccustRatingCodeDesc(String lovDesccustRatingCodeDesc) {
    	this.lovDesccustRatingCodeDesc = lovDesccustRatingCodeDesc;
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

	public CustomerRating getBefImage() {
		return this.befImage;
	}

	public void setBefImage(CustomerRating beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public void setValueType(boolean valueType) {
		this.valueType = valueType;
	}

	public boolean isValueType() {
		return valueType;
	}

	public void setLoginDetails(LoggedInUser userDetails) {
		setLastMntBy(userDetails.getUserId());
		this.userDetails = userDetails;

	}

	public void setLovDescCustRatingName(String lovDescCustRatingName) {
	    this.lovDescCustRatingName = lovDescCustRatingName;
    }

	public String getLovDescCustRatingName() {
	    return lovDescCustRatingName;
    }

}
