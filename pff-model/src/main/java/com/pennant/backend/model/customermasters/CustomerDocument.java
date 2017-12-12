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
 * FileName    		:  CustomerDocument.java                                                * 	  
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

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>CustomerDocument table</b>.<br>
 * 
 */
@XmlType(propOrder = { "custDocCategory", "custDocTitle", "custDocIssuedCountry", "custDocSysName", "custDocIssuedOn", "custDocExpDate",
		"docPurpose", "custDocName", "custDocType", "custDocImage", "docUri" })
@XmlAccessorType(XmlAccessType.NONE)
public class CustomerDocument extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 6420966711989511378L;

	private long custID = Long.MIN_VALUE;
	private String lovDescCustShrtName;
	@XmlElement(name = "docFormat")
	private String custDocType;
	@XmlElement(name = "docName")
	private String custDocName;
	@XmlElement(name = "docCategory")
	private String custDocCategory;
	private String lovDescCustDocCategory;
	@XmlElement
	private String custDocTitle;
	@XmlElement(name = "custDocIssuedAuth")
	private String custDocSysName;
	private Timestamp custDocRcvdOn;
	@XmlElement
	private Date custDocExpDate;
	@XmlElement
	private Date custDocIssuedOn;
	@XmlElement
	private String custDocIssuedCountry;
	@XmlElement
	private String docPurpose;
	@XmlElement(name="docRefId")
	private String docUri;
	private String lovDescCustDocIssuedCountry;
	private boolean custDocIsVerified;
	private long custDocVerifiedBy;
	private boolean custDocIsAcrive;
	@XmlElement(name="docContent")
	private byte[] custDocImage;
	private long docRefId = Long.MIN_VALUE;
	private boolean newRecord = false;
	private String lovValue;
	private CustomerDocument befImage;
	private LoggedInUser userDetails;
	private String lovDescCustRecordType;
	private String lovDescCustCIF;
	private String lovDescCustDocVerifiedBy;
	private boolean lovDescdocExpDateIsMand = false;
	private boolean docIssueDateMand = false;
	private boolean docIdNumMand = false;
	private boolean docIssuedAuthorityMand = false;
	private boolean docIsMandatory = false;
	
	
	private String sourceId;

	public boolean isNew() {
		return isNewRecord();
	}

	public CustomerDocument() {
		super();
	}

	public CustomerDocument(long id) {
		super();
		this.setId(id);
	}

	public CustomerDocument(String docCategory, String doctype, String docName, byte[] docImage) {
		super();	
		this.custDocCategory = docCategory;
		this.custDocType = doctype;
		this.custDocTitle = docName;
		this.custDocImage = docImage;
		this.newRecord = true;
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("docIssueDateMand");
		excludeFields.add("docIdNumMand");
		excludeFields.add("docIssuedAuthorityMand");
		excludeFields.add("custDocImage");
		excludeFields.add("sourceId");
		excludeFields.add("docIsMandatory");
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

	public String getCustDocType() {
		return custDocType;
	}

	public void setCustDocType(String custDocType) {
		this.custDocType = custDocType;
	}

	public String getCustDocCategory() {
		return custDocCategory;
	}

	public void setCustDocCategory(String custDocCategory) {
		this.custDocCategory = custDocCategory;
	}

	public String getLovDescCustDocCategory() {
		return lovDescCustDocCategory;
	}

	public void setLovDescCustDocCategory(String lovDescCustDocCategory) {
		this.lovDescCustDocCategory = lovDescCustDocCategory;
	}

	public String getCustDocTitle() {
		return custDocTitle;
	}

	public void setCustDocTitle(String custDocTitle) {
		this.custDocTitle = custDocTitle;
	}

	public String getCustDocName() {
		return custDocName;
	}

	public void setCustDocName(String custDocName) {
		this.custDocName = custDocName;
	}

	public String getCustDocSysName() {
		return custDocSysName;
	}

	public void setCustDocSysName(String custDocSysName) {
		this.custDocSysName = custDocSysName;
	}

	public Timestamp getCustDocRcvdOn() {
		return custDocRcvdOn;
	}

	public void setCustDocRcvdOn(Timestamp custDocRcvdOn) {
		this.custDocRcvdOn = custDocRcvdOn;
	}

	public Date getCustDocExpDate() {
		return custDocExpDate;
	}

	public void setCustDocExpDate(Date custDocExpDate) {
		this.custDocExpDate = custDocExpDate;
	}

	public Date getCustDocIssuedOn() {
		return custDocIssuedOn;
	}

	public void setCustDocIssuedOn(Date custDocIssuedOn) {
		this.custDocIssuedOn = custDocIssuedOn;
	}

	public String getCustDocIssuedCountry() {
		return custDocIssuedCountry;
	}

	public void setCustDocIssuedCountry(String custDocIssuedCountry) {
		this.custDocIssuedCountry = custDocIssuedCountry;
	}

	public String getDocPurpose() {
		return docPurpose;
	}

	public void setDocPurpose(String docPurpose) {
		this.docPurpose = docPurpose;
	}

	public String getDocUri() {
		return docUri;
	}

	public void setDocUri(String docUri) {
		this.docUri = docUri;
	}
	
	public String getLovDescCustDocIssuedCountry() {
		return this.lovDescCustDocIssuedCountry;
	}

	public void setLovDescCustDocIssuedCountry(String lovDescCustDocIssuedCountry) {
		this.lovDescCustDocIssuedCountry = lovDescCustDocIssuedCountry;
	}

	public boolean isCustDocIsVerified() {
		return custDocIsVerified;
	}

	public void setCustDocIsVerified(boolean custDocIsVerified) {
		this.custDocIsVerified = custDocIsVerified;
	}

	public long getCustDocVerifiedBy() {
		return custDocVerifiedBy;
	}

	public void setCustDocVerifiedBy(long custDocVerifiedBy) {
		this.custDocVerifiedBy = custDocVerifiedBy;
	}

	public boolean isCustDocIsAcrive() {
		return custDocIsAcrive;
	}

	public void setCustDocIsAcrive(boolean custDocIsAcrive) {
		this.custDocIsAcrive = custDocIsAcrive;
	}

	public byte[] getCustDocImage() {
		return custDocImage;
	}

	public void setCustDocImage(byte[] custDocImage) {
		this.custDocImage = custDocImage;
	}

	public long getDocRefId() {
		return docRefId;
	}

	public void setDocRefId(long docRefId) {
		this.docRefId = docRefId;
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

	public CustomerDocument getBefImage() {
		return this.befImage;
	}

	public void setBefImage(CustomerDocument beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
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

	public void setLoginDetails(LoggedInUser userDetails) {
		setLastMntBy(userDetails.getUserId());
		this.userDetails = userDetails;

	}

	public String getLovDescCustDocVerifiedBy() {
		return lovDescCustDocVerifiedBy;
	}

	public void setLovDescCustDocVerifiedBy(String lovDescCustDocVerifiedBy) {
		this.lovDescCustDocVerifiedBy = lovDescCustDocVerifiedBy;
	}

	public boolean isLovDescdocExpDateIsMand() {
		return lovDescdocExpDateIsMand;
	}

	public void setLovDescdocExpDateIsMand(boolean lovDescdocExpDateIsMand) {
		this.lovDescdocExpDateIsMand = lovDescdocExpDateIsMand;
	}

	public boolean isDocIssueDateMand() {
		return docIssueDateMand;
	}

	public void setDocIssueDateMand(boolean docIssueDateMand) {
		this.docIssueDateMand = docIssueDateMand;
	}

	public boolean isDocIdNumMand() {
		return docIdNumMand;
	}

	public void setDocIdNumMand(boolean docIdNumMand) {
		this.docIdNumMand = docIdNumMand;
	}

	public boolean isDocIssuedAuthorityMand() {
		return docIssuedAuthorityMand;
	}

	public void setDocIssuedAuthorityMand(boolean docIssuedAuthorityMand) {
		this.docIssuedAuthorityMand = docIssuedAuthorityMand;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public boolean isDocIsMandatory() {
		return docIsMandatory;
	}

	public void setDocIsMandatory(boolean docIsMandatory) {
		this.docIsMandatory = docIsMandatory;
	}

}
