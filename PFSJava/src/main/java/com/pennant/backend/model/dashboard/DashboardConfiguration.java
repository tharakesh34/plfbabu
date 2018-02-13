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

import com.pennant.fusioncharts.ChartsConfig;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>DashboardDetail table</b>.<br>
 *
 */
public class DashboardConfiguration extends AbstractWorkflowEntity {

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
	private String seriesType;
	private String seriesValues;
	private String fieldQuery;
	private String colorRangeXML;
	
	private boolean newRecord;
	private String lovValue;
	private DashboardConfiguration befImage;
	private LoggedInUser userDetails;
	private Object  lovDescDataObject;
	private long    lovDescUsrId;
	private String  lovDescUsrRoles;
	
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
		super();
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

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
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
		if("Column+Line".equals(getDashboardType()) || "Staked Column+Line".equals(getDashboardType())){
			return "Line";
		}else if("area".equals(getDashboardType())){
			return "Area";
		}
		return null;
	} 
}
