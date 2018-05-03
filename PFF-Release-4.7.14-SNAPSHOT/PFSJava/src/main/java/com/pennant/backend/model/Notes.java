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
 * FileName    		:  Notes.java                           								*
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-03-2011    														*
 *                                                                  						*
 * Modified Date    :  29-03-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-02-2011       PENNANT TECHONOLOGIES	                 0.1                            * 
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
package com.pennant.backend.model;

import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

import com.pennant.backend.util.PennantApplicationUtil;
import com.pennanttech.pennapps.core.model.AbstractEntity;

/**
 * Model class for the <b>Notes table</b>.<br>
 */
public class Notes extends AbstractEntity implements Entity {
	private static final long serialVersionUID = -8921214349365225047L;

	private long noteId = Long.MIN_VALUE;
	private String moduleName = "";
	private String reference = "";
	private String remarkType = "";
	private String alignType = "";
	private String remarks = "";
	private String usrLogin = "";
	private String roleCode = "";
	private String roleDesc = "";
	private String usrName = "";
	private String usrFName = "";
	private String usrMName = "";
	private String usrLName = "";

	public Notes() {
		super();
	}

	public Notes(long id) {
		super();
		this.setId(id);
	}

	public boolean isNew() {
		return getId() == Long.MIN_VALUE;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getId() {
		return noteId;
	}

	public void setId(long id) {
		this.noteId = id;
	}

	public long getNoteId() {
		return noteId;
	}

	public void setNoteId(long noteId) {
		this.noteId = noteId;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getRemarkType() {
		return remarkType;
	}

	public void setRemarkType(String remarkType) {
		this.remarkType = remarkType;
	}

	public String getAlignType() {
		return alignType;
	}

	public void setAlignType(String alignType) {
		this.alignType = alignType;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public long getInputBy() {
		return super.getLastMntBy();
	}

	public void setInputBy(long inputBy) {
		super.setLastMntBy(inputBy);
	}

	public Timestamp getInputDate() {
		return super.getLastMntOn();
	}

	public void setInputDate(Timestamp inputDate) {
		super.setLastMntOn(inputDate);
	}

	public String getUsrLogin() {
		return usrLogin;
	}

	public void setUsrLogin(String usrLogin) {
		this.usrLogin = usrLogin;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public void setRoleDesc(String roleDesc) {
		this.roleDesc = roleDesc;
	}

	public String getRoleDesc() {
		return roleDesc;
	}

	public String getUsrFName() {
		return usrFName;
	}

	public void setUsrFName(String usrFName) {
		this.usrFName = usrFName;
	}

	public String getUsrMName() {
		return usrMName;
	}

	public void setUsrMName(String usrMName) {
		this.usrMName = usrMName;
	}

	public String getUsrLName() {
		return usrLName;
	}

	public void setUsrLName(String usrLName) {
		this.usrLName = usrLName;
	}

	public String getUsrName() {
		if (StringUtils.isBlank(this.usrName)) {
			return PennantApplicationUtil.getFullName(this.usrFName, this.usrMName, this.usrLName);
		} else {
			return usrName;
		}
	}

	public void setUsrName(String usrName) {
		this.usrName = usrName;
	}

}
