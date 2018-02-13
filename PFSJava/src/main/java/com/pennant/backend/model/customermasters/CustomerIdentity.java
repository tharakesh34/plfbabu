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
 * FileName    		:  CustomerIdentity.java                                                   * 	  
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

import java.util.Date;

import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>CustomerIdentity table</b>.<br>
 *
 */
public class CustomerIdentity extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 2172724893551803043L;

	private long 	idCustID ;
	private String 	idType;
	private String 	lovDescIdTypeName;
	private String 	idIssuedBy;
	private String 	idRef;
	private String 	idIssueCountry;
	private String 	lovDescIdIssueCountryName;
	private Date	idIssuedOn;
	private Date 	idExpiresOn;
	private String 	idLocation;
	private boolean newRecord;
	private String 	lovValue;
	private String lovDescCustShrtName;
	private String lovDescCustRecordType;
	private String lovDescCustCIF;

	private CustomerIdentity befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public CustomerIdentity() {
		super();
	}

	public CustomerIdentity(long id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getId() {
		return idCustID;
	}
	public void setId (long id) {
		this.idCustID = id;
	}

	public long getIdCustID() {
		return idCustID;
	}
	public void setIdCustID(long idCustID) {
		this.idCustID = idCustID;
	}

	public String getIdType() {
		return idType;
	}
	public void setIdType(String idType) {
		this.idType = idType;
	}

	public String getLovDescIdTypeName() {
		return this.lovDescIdTypeName;
	}
	public void setLovDescIdTypeName(String lovDescIdTypeName) {
		this.lovDescIdTypeName = lovDescIdTypeName;
	}

	public String getIdIssuedBy() {
		return idIssuedBy;
	}
	public void setIdIssuedBy(String idIssuedBy) {
		this.idIssuedBy = idIssuedBy;
	}

	public String getIdRef() {
		return idRef;
	}
	public void setIdRef(String idRef) {
		this.idRef = idRef;
	}

	public String getIdIssueCountry() {
		return idIssueCountry;
	}
	public void setIdIssueCountry(String idIssueCountry) {
		this.idIssueCountry = idIssueCountry;
	}

	public String getLovDescIdIssueCountryName() {
		return this.lovDescIdIssueCountryName;
	}
	public void setLovDescIdIssueCountryName(String lovDescIdIssueCountryName) {
		this.lovDescIdIssueCountryName = lovDescIdIssueCountryName;
	}

	public Date getIdIssuedOn() {
		return idIssuedOn;
	}
	public void setIdIssuedOn(Date idIssuedOn) {
		this.idIssuedOn = idIssuedOn;
	}

	public Date getIdExpiresOn() {
		return idExpiresOn;
	}
	public void setIdExpiresOn(Date idExpiresOn) {
		this.idExpiresOn = idExpiresOn;
	}

	public String getIdLocation() {
		return idLocation;
	}
	public void setIdLocation(String idLocation) {
		this.idLocation = idLocation;
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

	public String getLovDescCustShrtName() {
		return lovDescCustShrtName;
	}
	public void setLovDescCustShrtName(String lovDescCustShrtName) {
		this.lovDescCustShrtName = lovDescCustShrtName;
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

	public CustomerIdentity getBefImage(){
		return this.befImage;
	}
	public void setBefImage(CustomerIdentity beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
}
