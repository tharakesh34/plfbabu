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
 * FileName    		:  DocumentType.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.systemmasters;

import java.sql.Timestamp;

import com.pennant.backend.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>DocumentType table</b>.<br>
 * 
 */
public class DocumentType extends AbstractWorkflowEntity {

	private static final long serialVersionUID = -4784526426453453903L;

	private String docTypeCode;
	private String docTypeDesc;
	private boolean docIsMandatory;
	private boolean docExpDateIsMand;
	private boolean docIssueDateMand;
	private boolean docIdNumMand;
	private boolean docTypeIsActive;
	private boolean docIsCustDoc;
	private boolean docIssuedAuthorityMand;
	private boolean docIsPdfExtRequired;
	private boolean docIsPasswordProtected;
	private long pdfMappingRef;

	private boolean newRecord;
	private String lovValue;
	private DocumentType befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public DocumentType() {
		super();
	}

	public DocumentType(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return docTypeCode;
	}

	public void setId(String id) {
		this.docTypeCode = id;
	}

	public String getDocTypeCode() {
		return docTypeCode;
	}

	public void setDocTypeCode(String docTypeCode) {
		this.docTypeCode = docTypeCode;
	}

	public String getDocTypeDesc() {
		return docTypeDesc;
	}

	public void setDocTypeDesc(String docTypeDesc) {
		this.docTypeDesc = docTypeDesc;
	}

	public boolean isDocIsMandatory() {
		return docIsMandatory;
	}

	public void setDocIsMandatory(boolean docIsMandatory) {
		this.docIsMandatory = docIsMandatory;
	}

	public boolean isDocTypeIsActive() {
		return docTypeIsActive;
	}

	public void setDocTypeIsActive(boolean docTypeIsActive) {
		this.docTypeIsActive = docTypeIsActive;
	}

	public boolean isDocIsCustDoc() {
		return docIsCustDoc;
	}

	public void setDocIsCustDoc(boolean docIsCustDoc) {
		this.docIsCustDoc = docIsCustDoc;
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

	public DocumentType getBefImage() {
		return this.befImage;
	}

	public void setBefImage(DocumentType beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public boolean isDocExpDateIsMand() {
		return docExpDateIsMand;
	}

	public void setDocExpDateIsMand(boolean docExpDateIsMand) {
		this.docExpDateIsMand = docExpDateIsMand;
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
	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}
	public boolean isDocIsPdfExtRequired() {
		return docIsPdfExtRequired;
	}
	public void setDocIsPdfExtRequired(boolean docIsPdfExtRequired) {
		this.docIsPdfExtRequired = docIsPdfExtRequired;
	}

	public boolean isDocIsPasswordProtected() {
		return docIsPasswordProtected;
	}

	public void setDocIsPasswordProtected(boolean docIsPasswordProtected) {
		this.docIsPasswordProtected = docIsPasswordProtected;
	}

	public long getPdfMappingRef() {
		return pdfMappingRef;
	}

	public void setPdfMappingRef(long pdfMappingRef) {
		this.pdfMappingRef = pdfMappingRef;
	}
}
