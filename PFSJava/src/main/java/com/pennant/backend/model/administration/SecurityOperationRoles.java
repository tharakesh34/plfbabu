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
 * FileName    		:  SecurityOperationRoles.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-03-2014    														*
 *                                                                  						*
 * Modified Date    :  10-03-2014    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-03-2014       Pennant	                 0.1                                            * 
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

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class SecurityOperationRoles extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private  long       oprRoleID=Long.MIN_VALUE;
	private  long       oprID;
	private  long       roleID;
	private String    lovDescRoleCd;//role code
	private LoggedInUser    userDetails;
	private SecurityOperationRoles befImage;
	private  String  lovDescOprCode;
	private  String  lovDescOprDesc;
	 private String      lovDescRoleCode;
	private  int     lovDescRightType;
	private  String  lovDescRightName;
	private boolean newRecord;
	
	private String lovDescRoleDesc;
	public SecurityOperationRoles() {
		super();
	}
	public SecurityOperationRoles(long oprRoleID) {
		super();
		this.oprRoleID = oprRoleID;
	}
	public String getLovDescOprCode() {
		return lovDescOprCode;
	}
	public void setLovDescOprCode(String lovDescOprCode) {
		this.lovDescOprCode = lovDescOprCode;
	}
	public String getLovDescOprDesc() {
		return lovDescOprDesc;
	}
	public void setLovDescOprDesc(String lovDescOprDesc) {
		this.lovDescOprDesc = lovDescOprDesc;
	}
	public int getLovDescRightType() {
		return lovDescRightType;
	}
	public void setLovDescRightType(int lovDescRightType) {
		this.lovDescRightType = lovDescRightType;
	}
	public String getLovDescRightName() {
		return lovDescRightName;
	}
	public void setLovDescRightName(String lovDescRightName) {
		this.lovDescRightName = lovDescRightName;
	}
	public void setId(long id){
		this.oprRoleID =id ;

	}
	public long getId(){
		return oprRoleID;
	}
	public long getOprRoleID() {
		return oprRoleID;
	}
	public void setOprRoleID(long oprRoleID) {
		this.oprRoleID = oprRoleID;
	}
	public long getOprID() {
		return oprID;
	}
	public void setOprID(long oprID) {
		this.oprID = oprID;
	}
	public long getRoleID() {
		return roleID;
	}
	public void setRoleID(long roleID) {
		this.roleID = roleID;
	}
	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
	public SecurityOperationRoles getBefImage() {
		return befImage;
	}
	public void setBefImage(SecurityOperationRoles befImage) {
		this.befImage = befImage;
	}
	
	public boolean isNew() {
		return false;
	}
	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
	public String getLovDescRoleCd() {
		return lovDescRoleCd;
	}
	public void setLovDescRoleCd(String lovDescRoleCd) {
		this.lovDescRoleCd = lovDescRoleCd;
	}
	public String getLovDescRoleCode() {
		return lovDescRoleCode;
	}
	public void setLovDescRoleCode(String lovDescRoleCode) {
		this.lovDescRoleCode = lovDescRoleCode;
	}
	public String getLovDescRoleDesc() {
		return lovDescRoleDesc;
	}
	public void setLovDescRoleDesc(String lovDescRoleDesc) {
		this.lovDescRoleDesc = lovDescRoleDesc;
	}

}
