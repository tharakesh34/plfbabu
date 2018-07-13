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
 * FileName    		:  LegalApplicantDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  16-06-2018    														*
 *                                                                  						*
 * Modified Date    :  16-06-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 16-06-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.model.legal;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.customermasters.Customer;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>LegalApplicantDetail table</b>.<br>
 *
 */
@XmlType(propOrder = { "legalApplicantId", "legalReference", "title", "propertyOwnersName", "age", "relationshipType",
		"iDType", "iDNo", "remarks" })
@XmlAccessorType(XmlAccessType.FIELD)
public class LegalApplicantDetail extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;

	private long legalApplicantId = Long.MIN_VALUE;
	private long legalId = Long.MIN_VALUE;
	private long customerId = Long.MIN_VALUE;
	private int seqNum = 0;
	private String legalReference;;
	private String title;
	private String titleName;
	private String propertyOwnersName;
	private int age;
	private String relationshipType;
	private String iDType;
	private String iDTypeName;
	private String iDNo;
	private String remarks;
	private Customer customer;
	private boolean isDefault;
	private String modtString;
	@XmlTransient
	private boolean newRecord = false;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private LegalApplicantDetail befImage;
	@XmlTransient
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public LegalApplicantDetail() {
		super();
	}

	public LegalApplicantDetail(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("titleName");
		excludeFields.add("iDTypeName");
		excludeFields.add("legalReference");
		excludeFields.add("seqNum");
		excludeFields.add("customer");
		excludeFields.add("isDefault");
		excludeFields.add("modtString");
		return excludeFields;
	}

	public long getId() {
		return legalApplicantId;
	}

	public void setId(long id) {
		this.legalApplicantId = id;
	}

	public long getLegalApplicantId() {
		return legalApplicantId;
	}

	public void setLegalApplicantId(long legalApplicantId) {
		this.legalApplicantId = legalApplicantId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitleName() {
		return this.titleName;
	}

	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}

	public String getPropertyOwnersName() {
		return propertyOwnersName;
	}

	public void setPropertyOwnersName(String propertyOwnersName) {
		this.propertyOwnersName = propertyOwnersName;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getRelationshipType() {
		return relationshipType;
	}

	public void setRelationshipType(String relationshipType) {
		this.relationshipType = relationshipType;
	}

	public String getIDType() {
		return iDType;
	}

	public void setIDType(String iDType) {
		this.iDType = iDType;
	}

	public String getIDTypeName() {
		return this.iDTypeName;
	}

	public void setIDTypeName(String iDTypeName) {
		this.iDTypeName = iDTypeName;
	}

	public String getIDNo() {
		return iDNo;
	}

	public void setIDNo(String iDNo) {
		this.iDNo = iDNo;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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

	public LegalApplicantDetail getBefImage() {
		return this.befImage;
	}

	public void setBefImage(LegalApplicantDetail beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public long getLegalId() {
		return legalId;
	}

	public void setLegalId(long legalId) {
		this.legalId = legalId;
	}

	public String getLegalReference() {
		return legalReference;
	}

	public void setLegalReference(String legalReference) {
		this.legalReference = legalReference;
	}

	public int getSeqNum() {
		return seqNum;
	}

	public void setSeqNum(int seqNum) {
		this.seqNum = seqNum;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public String getModtString() {
		return modtString;
	}

	public void setModtString(String modtString) {
		this.modtString = modtString;
	}

}
