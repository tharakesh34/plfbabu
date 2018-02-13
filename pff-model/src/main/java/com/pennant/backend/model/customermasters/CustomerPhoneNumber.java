/**
\ * Copyright 2011 - Pennant Technologies
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
 * FileName    		:  CustomerPhoneNumber.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.customermasters;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>CustomerPhoneNumber table</b>.<br>
 *
 */

@XmlType(propOrder = {"phoneTypeCode", "phoneCountryCode", "phoneAreaCode", "phoneNumber","phoneTypePriority"})
@XmlAccessorType(XmlAccessType.NONE)
public class CustomerPhoneNumber extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 4143719150678156593L;

	private long phoneCustID = Long.MIN_VALUE;
	private String lovDescCustShrtName;
	@XmlElement
	private String phoneTypeCode;
	private String lovDescPhoneTypeCodeName;
	@XmlElement
	private String phoneCountryCode;
	private String lovDescPhoneCountryName;
	@XmlElement
	private String phoneAreaCode;
	@XmlElement
	private String phoneNumber;
	private boolean newRecord;
	private String lovValue;
	private String lovDescCustRecordType;
	private String lovDescCustCIF;
	@XmlElement(name ="priority" )
	private int phoneTypePriority;

	private CustomerPhoneNumber befImage;
	private LoggedInUser userDetails;
	private String sourceId;
	private String phoneRegex;
	
	public boolean isNew() {
		return isNewRecord();
	}

	public CustomerPhoneNumber() {
		super();
	}

	public CustomerPhoneNumber(long id) {
		super();
		this.setId(id);
	}


	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
		excludeFields.add("sourceId");
		excludeFields.add("phoneRegex");
		return excludeFields;
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getId() {
		return phoneCustID;
	}
	public void setId (long id) {
		this.phoneCustID = id;
	}

	public long getPhoneCustID() {
		return phoneCustID;
	}
	public void setPhoneCustID(long phoneCustID) {
		this.phoneCustID = phoneCustID;
	}

	public String getLovDescCustShrtName() {
		return lovDescCustShrtName;
	}
	public void setLovDescCustShrtName(String lovDescCustShrtName) {
		this.lovDescCustShrtName = lovDescCustShrtName;
	}

	public String getPhoneTypeCode() {
		return phoneTypeCode;
	}
	public void setPhoneTypeCode(String phoneTypeCode) {
		this.phoneTypeCode = phoneTypeCode;
	}

	public String getLovDescPhoneTypeCodeName() {
		return this.lovDescPhoneTypeCodeName;
	}
	public void setLovDescPhoneTypeCodeName(String lovDescPhoneTypeCodeName) {
		this.lovDescPhoneTypeCodeName = lovDescPhoneTypeCodeName;
	}

	public String getPhoneCountryCode() {
		return phoneCountryCode;
	}
	public void setPhoneCountryCode(String phoneCountryCode) {
		this.phoneCountryCode = phoneCountryCode;
	}

	public String getPhoneAreaCode() {
		return phoneAreaCode;
	}
	public void setPhoneAreaCode(String phoneAreaCode) {
		this.phoneAreaCode = phoneAreaCode;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
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

	public CustomerPhoneNumber getBefImage(){
		return this.befImage;
	}
	public void setBefImage(CustomerPhoneNumber beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public void setLoginDetails(LoggedInUser userDetails){
		setLastMntBy(userDetails.getUserId());
		this.userDetails=userDetails;
	}

	public void setLovDescPhoneCountryName(String lovDescPhoneCountryName) {
		this.lovDescPhoneCountryName = lovDescPhoneCountryName;
	}
	public String getLovDescPhoneCountryName() {
		return lovDescPhoneCountryName;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public int getPhoneTypePriority() {
		return phoneTypePriority;
	}
	public void setPhoneTypePriority(int phoneTypePriority) {
		this.phoneTypePriority = phoneTypePriority;
	}

	public String getPhoneRegex() {
		return phoneRegex;
	}

	public void setPhoneRegex(String phoneRegex) {
		this.phoneRegex = phoneRegex;
	}

}
