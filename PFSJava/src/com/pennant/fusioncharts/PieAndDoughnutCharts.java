
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
 * FileName    		:  PieAndDoughnutCharts.java                                            * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-02-2012    														*
 *                                                                  						*
 * Modified Date    :  23-02-2012   														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-02-2012        Pennant	                0.1                                         * 
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
package com.pennant.fusioncharts;
public class PieAndDoughnutCharts {
	// @see http://docs.fusioncharts.com/charts-->Quick Chart Configuration/PieAndDoughnutCharts 
	
	private boolean showlegend;               // set true for slide-out effect
	private boolean enablesmartlabels;
	private boolean showlabels;
	private boolean showpercentvalues;
	private boolean skipOverlapLabels;         
	private boolean manageLabelOverflow=true;
	private String  smartLineColor;            //Line connecting with label and chart      
	private String  smartLineThickness;
	private String  smartLineAlpha;
	private boolean isSmartLineSlanted=true;   //smart line style
	private boolean showPercentValues;
	private boolean showPercentInToolTip;
	private boolean enableRotation;           //Chart rotation
	private int     pieRadius;                
	private int     startingAngle;
	private int     slicingDistance;         
	
	
	public PieAndDoughnutCharts(int pieRadius,boolean showlegend,boolean enableRotation,String smartLineColor){
		this.pieRadius=pieRadius;
		this.showlegend=showlegend;
		this.enableRotation=enableRotation;
		this.smartLineColor=smartLineColor;
		
	}
	
	

	public boolean isShowlegend() {
		return showlegend;
	}
	public void setShowlegend(boolean showlegend) {
		this.showlegend = showlegend;
	}
	public boolean isEnablesmartlabels() {
		return enablesmartlabels;
	}
	public void setEnablesmartlabels(boolean enablesmartlabels) {
		this.enablesmartlabels = enablesmartlabels;
	}
	public boolean isShowlabels() {
		return showlabels;
	}
	public void setShowlabels(boolean showlabels) {
		this.showlabels = showlabels;
	}
	public boolean isShowpercentvalues() {
		return showpercentvalues;
	}
	public void setShowpercentvalues(boolean showpercentvalues) {
		this.showpercentvalues = showpercentvalues;
	}
	public boolean isSkipOverlapLabels() {
		return skipOverlapLabels;
	}
	public void setSkipOverlapLabels(boolean skipOverlapLabels) {
		this.skipOverlapLabels = skipOverlapLabels;
	}
	public boolean isManageLabelOverflow() {
		return manageLabelOverflow;
	}
	public void setManageLabelOverflow(boolean manageLabelOverflow) {
		this.manageLabelOverflow = manageLabelOverflow;
	}
	public String getSmartLineColor() {
		return smartLineColor;
	}
	public void setSmartLineColor(String smartLineColor) {
		this.smartLineColor = smartLineColor;
	}
	public String getSmartLineThickness() {
		return smartLineThickness;
	}
	public void setSmartLineThickness(String smartLineThickness) {
		this.smartLineThickness = smartLineThickness;
	}
	public String getSmartLineAlpha() {
		return smartLineAlpha;
	}
	public void setSmartLineAlpha(String smartLineAlpha) {
		this.smartLineAlpha = smartLineAlpha;
	}
	public boolean isSmartLineSlanted() {
		return isSmartLineSlanted;
	}
	public void setSmartLineSlanted(boolean isSmartLineSlanted) {
		this.isSmartLineSlanted = isSmartLineSlanted;
	}
	public boolean isShowPercentValues() {
		return showPercentValues;
	}
	public void setShowPercentValues(boolean showPercentValues) {
		this.showPercentValues = showPercentValues;
	}
	public boolean isShowPercentInToolTip() {
		return showPercentInToolTip;
	}
	public void setShowPercentInToolTip(boolean showPercentInToolTip) {
		this.showPercentInToolTip = showPercentInToolTip;
	}
	public boolean isEnableRotation() {
		return enableRotation;
	}
	public void setEnableRotation(boolean enableRotation) {
		this.enableRotation = enableRotation;
	}
	public int getPieRadius() {
		return pieRadius;
	}
	public void setPieRadius(int pieRadius) {
		this.pieRadius = pieRadius;
	}
	public int getStartingAngle() {
		return startingAngle;
	}
	public void setStartingAngle(int startingAngle) {
		this.startingAngle = startingAngle;
	}
	public int getSlicingDistance() {
		return slicingDistance;
	}
	public void setSlicingDistance(int slicingDistance) {
		this.slicingDistance = slicingDistance;
	}
}
