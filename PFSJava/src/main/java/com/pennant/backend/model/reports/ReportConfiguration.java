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
 * FileName    		: ReportSearchTemplate.java                                             * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-09-2012    														*
 *                                                                  						*
 * Modified Date    :  23-09-2012     														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-09-2012             Pennant	                 0.1                                    * 
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
package com.pennant.backend.model.reports;

import java.util.List;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

public class ReportConfiguration extends AbstractWorkflowEntity implements Entity{

	private static final long serialVersionUID = -619353564397203914L;


	private long reportID = Long.MIN_VALUE;
	private String  reportName;
	private String   reportHeading;
	private boolean   promptRequired;
	private String   reportJasperName;
	private boolean  showTempLibrary;
	private String  dataSourceName;	
	private String  menuItemCode;	
	private boolean alwMultiFormat = false;
	private boolean newRecord=false;
	private String lovValue;
	private ReportConfiguration befImage;
	private LoggedInUser userDetails;
	private boolean whereCondition;
	
    private  List<ReportFilterFields> listReportFieldsDetails;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public List<ReportFilterFields> getListReportFieldsDetails() {
		return listReportFieldsDetails;
	}
	public void setListReportFieldsDetails(
			List<ReportFilterFields> listReportFieldsDetails) {
		this.listReportFieldsDetails = listReportFieldsDetails;
	}
	public boolean isNew() {
		return isNewRecord();
	}
	public ReportConfiguration() {
		super();
	}
	public ReportConfiguration(long id) {
		super();
		this.setId(id);
	}

	public long getId() {
		return reportID;
	}
	public void setId (long id) {
		this.reportID = id;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getReportHeading() {
		return reportHeading;
	}

	public void setReportHeading(String reportHeading) {
		this.reportHeading = reportHeading;
	}

	public boolean isPromptRequired() {
		return promptRequired;
	}

	public void setPromptRequired(boolean promptRequired) {
		this.promptRequired = promptRequired;
	}

	public String getReportJasperName() {
		return reportJasperName;
	}

	public void setReportJasperName(String reportJasperName) {
		this.reportJasperName = reportJasperName;
	}

	public boolean isShowTempLibrary() {
		return showTempLibrary;
	}

	public void setShowTempLibrary(boolean showTempLibrary) {
		this.showTempLibrary = showTempLibrary;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	public String getMenuItemCode() {
		return menuItemCode;
	}

	public void setMenuItemCode(String menuItemCode) {
		this.menuItemCode = menuItemCode;
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

	public ReportConfiguration getBefImage() {
		return befImage;
	}

	public void setBefImage(ReportConfiguration befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setReportID(long reportID) {
		this.reportID = reportID;
	}
	public long getReportID() {
		return reportID;
	}
	public void setAlwMultiFormat(boolean alwMultiFormat) {
	    this.alwMultiFormat = alwMultiFormat;
    }
	public boolean isAlwMultiFormat() {
	    return alwMultiFormat;
    }
	public boolean isWhereCondition() {
		return whereCondition;
	}
	public void setWhereCondition(boolean whereCondition) {
		this.whereCondition = whereCondition;
	}
}
