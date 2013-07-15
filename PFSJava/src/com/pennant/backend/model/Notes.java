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

import java.util.Date;

/**
 * Model class for the <b>Notes table</b>.<br>
 */
public class Notes implements java.io.Serializable, Entity {

	private static final long serialVersionUID = -8921214349365225047L;

	private long 		noteId = Long.MIN_VALUE;
	private String 		moduleName="";
	private String 		reference="";
	private String 		remarkType="";
	private String 		alignType="";
	private int 		version;
	private String 		remarks="";
	private long 		inputBy;
	private Date 		inputDate;
	private String 		usrLogin="";

	public Notes() {
	}

	public Notes(long id) {
		this.setId(id);
	}
	
	public Notes(long noteId,String moduleName, String reference, String remarkType, String alignType,
			int version, String remarks,long inputBy,Date inputDate,String usrLogin) {
		super();
		this.noteId = noteId;
		this.moduleName = moduleName;
		this.reference = reference;
		this.remarkType = remarkType;
		this.alignType = alignType;
		this.version = version;
		this.remarks = remarks;
		this.inputBy = inputBy;
		this.inputDate = inputDate;
		this.usrLogin = usrLogin;
	}
	
	public boolean isNew() {
		return (getId() == Long.MIN_VALUE);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public long getId() {
		return noteId;
	}
	public void setId(long id) {
		this.noteId= id;
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

	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}

	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public long getInputBy() {
		return inputBy;
	}
	public void setInputBy(long inputBy) {
		this.inputBy = inputBy;
	}

	public Date getInputDate() {
		return inputDate;
	}
	public void setInputDate(java.util.Date date) {
		this.inputDate = date;
	}

	public String getUsrLogin() {
		return usrLogin;
	}
	public void setUsrLogin(String usrLogin) {
		this.usrLogin = usrLogin;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof Notes) {
			Notes notes = (Notes) obj;
			return equals(notes);
		}

		return false;
	}

}
