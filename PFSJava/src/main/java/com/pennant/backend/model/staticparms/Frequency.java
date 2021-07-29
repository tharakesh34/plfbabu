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
 * FileName    		:  Frequency.java                                                   * 	  
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

package com.pennant.backend.model.staticparms;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>Frequency table</b>.<br>
 * 
 */
public class Frequency extends AbstractWorkflowEntity {

	private static final long serialVersionUID = -2944905072362782172L;

	private String frqCode;
	private String frqDesc;
	private boolean frqIsActive;
	private String lovValue;
	private Frequency befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public Frequency() {
		super();
	}

	public Frequency(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return frqCode;
	}

	public void setId(String id) {
		this.frqCode = id;
	}

	public String getFrqCode() {
		return frqCode;
	}

	public void setFrqCode(String frqCode) {
		this.frqCode = frqCode;
	}

	public String getFrqDesc() {
		return frqDesc;
	}

	public void setFrqDesc(String frqDesc) {
		this.frqDesc = frqDesc;
	}

	public boolean isFrqIsActive() {
		return frqIsActive;
	}

	public void setFrqIsActive(boolean frqIsActive) {
		this.frqIsActive = frqIsActive;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public Frequency getBefImage() {
		return this.befImage;
	}

	public void setBefImage(Frequency beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
}
