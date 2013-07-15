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
 * FileName    		:  DashboardDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-06-2011    														*
 *                                                                  						*
 * Modified Date    :  14-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-06-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.dashboard;

import java.sql.Timestamp;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.fusioncharts.ChartCosmetics;
import com.pennant.fusioncharts.ChartsConfig;

/**
 * Model class for the <b>DashboardDetail table</b>.<br>
 *
 */
public class DashboardConfiguration implements java.io.Serializable {

	private static final long serialVersionUID = 7784852736807398980L;
	
	private String dashboardCode;
	private String dashboardDesc;
	private String dashboardType;

	private String   query;
	private String   dataXML;
	private boolean  drillDownChart;
	private boolean  lovDescIsDataAsXml;
	private String remarks;
	private String dimension;
	private String caption;
	private String subCaption;
	private  boolean        adtDataSource;
	private  boolean        multiSeries;
	private ChartsConfig    lovDescChartsConfig;
	private ChartCosmetics 	lovDescChartCosmetics;
	private String seriesType;
	private String seriesValues;
	private String fieldQuery;
	private String colorRangeXML;
	
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private DashboardConfiguration befImage;
	private LoginUserDetails userDetails;
	private Object  lovDescDataObject;
	private long    lovDescUsrId;
	private String  lovDescUsrRoles;

	private int    version;
	private String recordStatus;
	private String roleCode="";
	private String nextRoleCode= "";
	private String taskId="";
	private String nextTaskId= "";
	private String recordType;
	private String userAction = "Save";
	private long   workflowId = 0;


	public String getDimension() {
		return dimension;
	}

	public void setDimension(String dimension) {
		this.dimension = dimension;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getSubCaption() {
		return subCaption;
	}

	public void setSubCaption(String subCaption) {
		this.subCaption = subCaption;
	}


	public boolean isNew() {
		return isNewRecord();
	}

	public DashboardConfiguration() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("DashboardConfiguration");
	}

	public DashboardConfiguration(String id) {
		this.setId(id);
	}

	//Getter and Setter methods

	public String getId() {
		return dashboardCode;
	}

	public void setId (String id) {
		this.dashboardCode = id;
	}

	public String getDashboardCode() {
		return dashboardCode;
	}
	public void setDashboardCode(String dashboardCode) {
		this.dashboardCode = dashboardCode;
	}



	public String getDashboardDesc() {
		return dashboardDesc;
	}
	public void setDashboardDesc(String dashboardDesc) {
		this.dashboardDesc = dashboardDesc;
	}



	public String getDashboardType() {
		return dashboardType;
	}
	public void setDashboardType(String dashboardType) {
		this.dashboardType = dashboardType;
	}


	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
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

	public DashboardConfiguration getBefImage(){
		return this.befImage;
	}

	public void setBefImage(DashboardConfiguration beforeImage){
		this.befImage=beforeImage;
	}

	public LoginUserDetails getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoginUserDetails userDetails) {
		this.userDetails = userDetails;
	}

	public String getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getNextRoleCode() {
		return nextRoleCode;
	}

	public void setNextRoleCode(String nextRoleCode) {
		this.nextRoleCode = nextRoleCode;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getNextTaskId() {
		return nextTaskId;
	}

	public void setNextTaskId(String nextTaskId) {
		this.nextTaskId = nextTaskId;
	}

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public String getUserAction() {
		return userAction;
	}

	public void setUserAction(String userAction) {
		this.userAction = userAction;
	}

	public boolean isWorkflow() {
		if (this.workflowId==0){
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

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public void setLovDescUsrId(long lovDescUsrId) {
		this.lovDescUsrId = lovDescUsrId;
	}

	public long getLovDescUsrId() {
		return lovDescUsrId;
	}

	public void setLovDescChartsConfig(ChartsConfig lovDescChartsConfig) {
		this.lovDescChartsConfig = lovDescChartsConfig;
	}

	public ChartsConfig getLovDescChartsConfig() {
		return lovDescChartsConfig;
	}

	public void setLovDescChartCosmetics(ChartCosmetics lovDesChartCosmetics) {
		this.lovDescChartCosmetics = lovDesChartCosmetics;
	}

	public ChartCosmetics getLovDescChartCosmetics() {
		return lovDescChartCosmetics;
	}


	// Overidden Equals method to handle the comparision
	public boolean equals(DashboardConfiguration dashboardConfiguration) {
		return getId() == dashboardConfiguration.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof DashboardConfiguration) {
			DashboardConfiguration dashboardConfiguration = (DashboardConfiguration) obj;
			return equals(dashboardConfiguration);
		}
		return false;
	}

	public void setAdtDataSource(boolean adtDataSource) {
		this.adtDataSource = adtDataSource;
	}

	public boolean isAdtDataSource() {
		return adtDataSource;
	}

	public void setLovDescDataObject(Object lovDescDataObject) {
		this.lovDescDataObject = lovDescDataObject;
	}

	public Object getLovDescDataObject() {
		return lovDescDataObject;
	}

	public void setLovDescUsrRoles(String lovDescUsrRoles) {
		this.lovDescUsrRoles = lovDescUsrRoles;
	}

	public String getLovDescUsrRoles() {
		return lovDescUsrRoles;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setMultiSeries(boolean multiSeries) {
		this.multiSeries = multiSeries;
	}

	public boolean isMultiSeries() {
		return multiSeries;
	}
	public String getDataXML() {
		return dataXML;
	}

	public void setDataXML(String dataXML) {
		this.dataXML = dataXML;
	}

	public void setLovDescIsDataAsXml(boolean lovDescIsDataAsXml) {
		this.lovDescIsDataAsXml = lovDescIsDataAsXml;
	}

	public boolean isLovDescIsDataAsXml() {
		return lovDescIsDataAsXml;
	}

	public void setDrillDownChart(boolean drillDownChart) {
		this.drillDownChart = drillDownChart;
	}

	public boolean isDrillDownChart() {
		return drillDownChart;
	}
	
	
	public String getSeriesType() {
		return seriesType;
	}

	public void setSeriesType(String seriesType) {
		this.seriesType = seriesType;
	}

	public String getSeriesValues() {
		return seriesValues;
	}

	public void setSeriesValues(String seriesValues) {
		this.seriesValues = seriesValues;
	}

	public String getFieldQuery() {
		return fieldQuery;
	}

	public void setFieldQuery(String fieldQuery) {
		this.fieldQuery = fieldQuery;
	}

	public String getColorRangeXML() {
		return colorRangeXML;
	}

	public void setColorRangeXML(String colorRangeXML) {
		this.colorRangeXML = colorRangeXML;
	}

	public String getRenderAs(){
		if("3D".equals(getDimension())){
			if("line".equals(getDashboardType())){
				return "renderAs='line'";
			}else if("area".equals(getDashboardType())){
				return "renderAs='Area'";
			}
		}
		return null;
	} 
}
