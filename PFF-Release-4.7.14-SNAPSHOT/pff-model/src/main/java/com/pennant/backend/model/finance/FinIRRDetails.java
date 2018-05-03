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
 * FileName    		:  IRRFeeType.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-06-2017    														*
 *                                                                  						*
 * Modified Date    :  21-06-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-06-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.applicationmaster.IRRCode;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>IRRFeeType table</b>.<br>
 * 
 */
public class FinIRRDetails extends AbstractWorkflowEntity  implements Entity {
	private static final long	serialVersionUID	= 1L;
	
	private long				iRRID;
	private String				finReference;
	private String				iRRCode;
	private BigDecimal 			iRR;
	private String			    irrCodeDesc;
	private boolean 			newRecord=false;
	private String			    lovValue;
	private IRRCode			    befImage;
	private  LoggedInUser	    userDetails;
	
	public boolean isNew() {
		return isNewRecord();
	}
	public String getiRRCode() {
		return iRRCode;
	}

	public void setiRRCode(String iRRCode) {
		this.iRRCode = iRRCode;
	}

	public long getiRRID() {
		return iRRID;
	}

	public void setiRRID(long iRRID) {
		this.iRRID = iRRID;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
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

	public IRRCode getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(IRRCode beforeImage){
		this.befImage=beforeImage;
	}

	public  LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails( LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}
	@Override
	public long getId() {
		return 0;
	}
	@Override
	public void setId(long id) {
		
	}
	public String getIrrCodeDesc() {
		return irrCodeDesc;
	}
	public void setIrrCodeDesc(String irrCodeDesc) {
		this.irrCodeDesc = irrCodeDesc;
	}
	public BigDecimal getIRR() {
		return iRR;
	}
	public void setIRR(BigDecimal iRR) {
		this.iRR = iRR;
	}
	
	
}
