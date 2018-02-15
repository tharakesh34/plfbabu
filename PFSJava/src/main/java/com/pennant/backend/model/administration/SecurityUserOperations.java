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
 * FileName    		: SecurityUserOperations.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-06-2011    														*
 *                                                                  						*
 * Modified Date    :  27-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.model.administration;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class SecurityUserOperations extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 3894711431224067299L;
	private long usrOprID = Long.MIN_VALUE;
	private long usrID;
	private String lovDescFirstName;
	private String lovDescMiddleName;
	private String lovDescLastName;

	private long oprID;
	private String lovDescOprCd;// operation code
	private String lovDescOprDesc;
	
	private LoggedInUser userDetails;
	private SecurityUserOperations befImage;
	private boolean newRecord;

	private String lovDescUsrFName;
	private String lovDescUsrMName;
	private String lovDescUsrLName;

	public SecurityUserOperations() {
		super();
	}

	public SecurityUserOperations(long usrOprID) {
		super();
		this.usrOprID = usrOprID;
	}

	public long getUsrOprID() {
		return usrOprID;
	}

	public void setUsrOprID(long usrOprID) {
		this.usrOprID = usrOprID;
	}

	public long getUsrID() {
		return usrID;
	}

	public void setUsrID(long usrID) {
		this.usrID = usrID;
	}

	public long getOprID() {
		return oprID;
	}

	public void setOprID(long oprID) {
		this.oprID = oprID;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public SecurityUserOperations getBefImage() {
		return befImage;
	}

	public void setBefImage(SecurityUserOperations befImage) {
		this.befImage = befImage;
	}

	@Override
	public long getId() {
		return usrOprID;

	}

	@Override
	public void setId(long usrOprID) {

		this.usrOprID = usrOprID;
	}

	@Override
	public boolean isNew() {
		return isNewRecord();
	}

	public String getLovDescFirstName() {
		return lovDescFirstName;
	}

	public void setLovDescFirstName(String lovDescFirstName) {
		this.lovDescFirstName = lovDescFirstName;
	}

	public String getLovDescMiddleName() {
		return lovDescMiddleName;
	}

	public void setLovDescMiddleName(String lovDescMiddleName) {
		this.lovDescMiddleName = lovDescMiddleName;
	}

	public String getLovDescLastName() {
		return lovDescLastName;
	}

	public void setLovDescLastName(String lovDescLastName) {
		this.lovDescLastName = lovDescLastName;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public String getLovDescUsrFName() {
		return lovDescUsrFName;
	}

	public void setLovDescUsrFName(String lovDescUsrFName) {
		this.lovDescUsrFName = lovDescUsrFName;
	}

	public String getLovDescUsrMName() {
		return lovDescUsrMName;
	}

	public void setLovDescUsrMName(String lovDescUsrMName) {
		this.lovDescUsrMName = lovDescUsrMName;
	}

	public String getLovDescUsrLName() {
		return lovDescUsrLName;
	}

	public void setLovDescUsrLName(String lovDescUsrLName) {
		this.lovDescUsrLName = lovDescUsrLName;
	}

	public String getLovDescOprCd() {
		return lovDescOprCd;
	}

	public void setLovDescOprCd(String lovDescOprCd) {
		this.lovDescOprCd = lovDescOprCd;
	}

	public String getLovDescOprDesc() {
		return lovDescOprDesc;
	}

	public void setLovDescOprDesc(String lovDescOprDesc) {
		this.lovDescOprDesc = lovDescOprDesc;
	}

}
