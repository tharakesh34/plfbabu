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
 * FileName    		:  HolidayMaster.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-07-2011    														*
 *                                                                  						*
 * Modified Date    :  11-07-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-07-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.smtmasters;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>HolidayMaster table</b>.<br>
 * 
 */
public class HolidayMaster implements java.io.Serializable {

	private static final long serialVersionUID = 7047561032260705575L;

	private String holidayCode = "GEN";
	private String holidayCodeDesc = "General";
	private BigDecimal holidayYear;
	private String holidayType;
	private String holidays;
	private String holidayDesc1;
	private String holidayDesc2;
	private String holidayDesc3;
	private String holidaysDesc;

	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord = false;
	private String lovValue;
	private HolidayMaster befImage;
	private LoginUserDetails userDetails;
	private List<HolidayDetail> lovDescHolidayDetails;

	private String userAction = "Save";
	private long workflowId = 0;

	public boolean isNew() {
		return isNewRecord();
	}

	public HolidayMaster() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("HolidayMaster");
	}

	public HolidayMaster(String id) {
		this.setId(id);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public String getId() {
		return holidayCode;
	}
	public void setId(String id) {
		this.holidayCode = id;
	}

	public String getHolidayCode() {
		return holidayCode;
	}
	public void setHolidayCode(String holidayCode) {
		this.holidayCode = holidayCode;
	}

	public String getHolidayCodeDesc() {
		return holidayCodeDesc;
	}
	public void setHolidayCodeDesc(String holidayCodeDesc) {
		this.holidayCodeDesc = holidayCodeDesc;
	}

	public BigDecimal getHolidayYear() {
		return holidayYear;
	}
	public void setHolidayYear(BigDecimal holidayYear) {
		this.holidayYear = holidayYear;
	}

	public String getHolidayType() {
		return holidayType;
	}
	public void setHolidayType(String holidayType) {
		this.holidayType = holidayType;
	}

	public String getHolidays() {
		return holidays;
	}
	public void setHolidays(String holidays) {
		this.holidays = holidays;
	}

	public String getHolidayDesc1() {
		return holidayDesc1;
	}
	public void setHolidayDesc1(String holidayDesc1) {
		this.holidayDesc1 = holidayDesc1;
	}

	public String getHolidayDesc2() {
		return holidayDesc2;
	}
	public void setHolidayDesc2(String holidayDesc2) {
		this.holidayDesc2 = holidayDesc2;
	}

	public String getHolidayDesc3() {
		return holidayDesc3;
	}
	public void setHolidayDesc3(String holidayDesc3) {
		this.holidayDesc3 = holidayDesc3;
	}

	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}

	public long getLastMntBy() {
		return lastMntBy;
	}
	public void setLastMntBy(long lastMntBy) {
		this.lastMntBy = lastMntBy;
	}

	public Timestamp getLastMntOn() {
		return lastMntOn;
	}
	public void setLastMntOn(Timestamp lastMntON) {
		this.lastMntOn = lastMntON;
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

	public HolidayMaster getBefImage() {
		return this.befImage;
	}
	public void setBefImage(HolidayMaster beforeImage) {
		this.befImage = beforeImage;
	}

	public LoginUserDetails getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoginUserDetails userDetails) {
		this.userDetails = userDetails;
	}

	public String getUserAction() {
		return userAction;
	}
	public void setUserAction(String userAction) {
		this.userAction = userAction;
	}

	public boolean isWorkflow() {
		if (this.workflowId == 0) {
			return false;
		}
		return true;
	}
	
	public long getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}

	// Overidden Equals method to handle the comparision
	public boolean equals(HolidayMaster holidayMaster) {
		return getId() == holidayMaster.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof HolidayMaster) {
			HolidayMaster holidayMaster = (HolidayMaster) obj;
			return equals(holidayMaster);
		}
		return false;
	}

	public List<HolidayDetail> getHolidayDetails() {
		if (lovDescHolidayDetails == null || lovDescHolidayDetails.size() <= 0) {
			this.lovDescHolidayDetails = getHolidayList(this.holidayYear);
		}

		return lovDescHolidayDetails;
	}
	public void setHolidayDetails(List<HolidayDetail> holidayDetails) {
		this.lovDescHolidayDetails = holidayDetails;
	}

	public String getHolidaysDesc() {
		return StringUtils.trimToEmpty(this.holidayDesc1)
				+ StringUtils.trimToEmpty(this.holidayDesc2)
				+ StringUtils.trimToEmpty(this.holidayDesc3).trim();
	}
	public void setHolidaysDesc(String holidaysDesc) {
		this.holidaysDesc = holidaysDesc;

		if (holidaysDesc != null) {
			if (holidaysDesc.trim().length() >= 5000) {
				this.holidayDesc1 = this.holidaysDesc;
			} else if (holidaysDesc.trim().length() >= 10000) {
				this.holidayDesc1 = this.holidaysDesc.substring(0, 4999);
				this.holidayDesc1 = this.holidaysDesc.substring(5000);
			} else {
				if (holidaysDesc.length() < 4999) {
					this.holidayDesc1 = this.holidaysDesc.substring(0,
							holidaysDesc.length());
				} else {
					this.holidayDesc1 = this.holidaysDesc.substring(0, 4999);
					this.holidayDesc1 = this.holidaysDesc.substring(5000, 9999);
					this.holidayDesc1 = this.holidaysDesc.substring(10000);
				}
			}
		} else {
			this.holidayDesc1 = null;
			this.holidayDesc2 = null;
			this.holidayDesc3 = null;
		}

	}

	public List<HolidayDetail> getHolidayList(BigDecimal holidayYear) {
		String[] holiday = null;
	//	String[] holidayDesc = null;
		List<HolidayDetail> holidayList = null;

		if (!StringUtils.trimToEmpty(holidays).equals("")) {
			holiday = holidays.split(",");
		//	holidayDesc = getHolidaysDesc().split(",");
		}

		if (holidayYear == null || holidayYear.intValue() == 0) {
			holidayYear = this.holidayYear;
		}

		if (holidayYear == null || holidayYear.intValue() == 0) {
			Calendar calendar = Calendar.getInstance();
			holidayYear = new BigDecimal(calendar.get(Calendar.YEAR));
		}

		if (holiday != null) {
			holidayList = new ArrayList<HolidayDetail>(holiday.length);
			for (int i = 0; i < holiday.length; i++) {
				if (validNumber(holiday[i])) {
					holidayList.add(new HolidayDetail(this.holidayCode,
							this.holidayCodeDesc, holidayYear, holidayType,
							Integer.parseInt(holiday[i]), ""));//holidayDesc[i]
				}
			}
		}
		return holidayList;
	}

	private boolean validNumber(String number) {
		boolean valid = false;

		try {
			Integer.parseInt(number);
			valid = true;
		} catch (NumberFormatException ex) {
			valid = false;
		}

		return valid;
	}
}
