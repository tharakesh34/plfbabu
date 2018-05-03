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
 * FileName    		:  EMailType.java                                                   * 	  
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

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>EMailType table</b>.<br>
 * 
 */
public class EMailType extends AbstractWorkflowEntity {

	private static final long serialVersionUID = -6429940451674759071L;

	private String emailTypeCode;
	private String emailTypeDesc;
	private int emailTypePriority;
	private boolean emailTypeIsActive;
	private boolean newRecord;
	private String lovValue;
	private EMailType befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public EMailType() {
		super();
	}

	public EMailType(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return emailTypeCode;
	}

	public void setId(String id) {
		this.emailTypeCode = id;
	}

	public String getEmailTypeCode() {
		return emailTypeCode;
	}

	public void setEmailTypeCode(String emailTypeCode) {
		this.emailTypeCode = emailTypeCode;
	}

	public String getEmailTypeDesc() {
		return emailTypeDesc;
	}

	public void setEmailTypeDesc(String emailTypeDesc) {
		this.emailTypeDesc = emailTypeDesc;
	}

	public int getEmailTypePriority() {
		return emailTypePriority;
	}

	public void setEmailTypePriority(int emailTypePriority) {
		this.emailTypePriority = emailTypePriority;
	}

	public boolean isEmailTypeIsActive() {
		return emailTypeIsActive;
	}

	public void setEmailTypeIsActive(boolean emailTypeIsActive) {
		this.emailTypeIsActive = emailTypeIsActive;
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

	public EMailType getBefImage() {
		return this.befImage;
	}

	public void setBefImage(EMailType beforeImage) {
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
}
