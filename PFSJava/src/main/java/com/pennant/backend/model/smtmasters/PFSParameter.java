/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ******************************************************************************************** 
 * FILE HEADER *
 ******************************************************************************************** 
 * * FileName : PFSParameter.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-07-2011 * * Modified Date :
 * 12-07-2011 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 12-07-2011 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */
package com.pennant.backend.model.smtmasters;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>PFSParameter table</b>.<br>
 * 
 */
public class PFSParameter extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 2039677648479855637L;

	private String sysParmCode = null;
	private String sysParmDesc;
	private String sysParmType;
	private boolean sysParmMaint;
	private String sysParmValue;
	private int sysParmLength;
	private int sysParmDec;
	private String sysParmList;
	private String sysParmValdMod;
	private String sysParmDescription;
	private boolean newRecord;
	private String lovValue;
	private PFSParameter befImage;
	private LoggedInUser userDetails;

	public PFSParameter() {
		super();
	}

	public PFSParameter(String id) {
		super();
		this.setId(id);
	}

	public PFSParameter getBefImage() {
		return this.befImage;
	}

	public void setBefImage(PFSParameter beforeImage) {
		this.befImage = beforeImage;
	}

	public String getId() {
		return sysParmCode;
	}

	public void setId(String id) {
		this.sysParmCode = id;
	}

	public void setSysParmCode(String sysParmCode) {
		this.sysParmCode = sysParmCode;
	}

	public String getSysParmCode() {
		return sysParmCode;
	}

	public void setSysParmDesc(String sysParmDesc) {
		this.sysParmDesc = sysParmDesc;
	}

	public String getSysParmDesc() {
		return sysParmDesc;
	}

	public void setSysParmType(String sysParmType) {
		this.sysParmType = sysParmType;
	}

	public String getSysParmType() {
		return sysParmType;
	}

	public void setSysParmMaint(boolean sysParmMaint) {
		this.sysParmMaint = sysParmMaint;
	}

	public boolean isSysParmMaint() {
		return sysParmMaint;
	}

	public void setSysParmValue(String sysParmValue) {
		this.sysParmValue = sysParmValue;
	}

	public String getSysParmValue() {
		return sysParmValue;
	}

	public void setSysParmLength(int sysParmLength) {
		this.sysParmLength = sysParmLength;
	}

	public int getSysParmLength() {
		return sysParmLength;
	}

	public void setSysParmDec(int sysParmDec) {
		this.sysParmDec = sysParmDec;
	}

	public int getSysParmDec() {
		return sysParmDec;
	}

	public void setSysParmList(String sysParmList) {
		this.sysParmList = sysParmList;
	}

	public String getSysParmList() {
		return sysParmList;
	}

	public void setSysParmValdMod(String sysParmValdMod) {
		this.sysParmValdMod = sysParmValdMod;
	}

	public String getSysParmValdMod() {
		return sysParmValdMod;
	}

	public void setSysParmDescription(String sysParmDescription) {
		this.sysParmDescription = sysParmDescription;
	}

	public String getSysParmDescription() {
		return sysParmDescription;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
}
