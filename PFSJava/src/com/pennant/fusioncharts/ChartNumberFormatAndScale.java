

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
 * FileName    		:  ChartNumberFormatAndScale.java                                            * 	  
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

public class ChartNumberFormatAndScale {

	//@see http://docs.fusioncharts.com/charts-->Quick Chart Configuration/NumberFormating
	//Format attributes 

	private int      decimals=0;               //number of decimals after point
	private boolean  forceDecimals=false;     //if value is 0.120 FChart disaplays as 0.12 but if want to show 0.120 set it true
	private boolean  formatNumber=true;       //if value is 12,000 .if donotshow comma set to false 
	private String   numberPrefix;            //Number prefix like $10 $2
	private String   numberSuffix;            //Number suffix like 1.2% 10%
	private boolean  forceYAxisValueDecimals=true;// opt to just show decimals on y-axis values in this case (when adjustDiv is false)
	private int      yAxisValueDecimals;

	//Applicable for dual axis charts
	private int      sDecimals=0;    
	private boolean  sForceDecimals=false;
	private boolean  sFormatNumber=true;
	private String   sNumberPrefix;
	private String   sNumberSuffix;
	private boolean  forceSYAxisValueDecimals=true;
	private int      sYAxisValueDecimals;

	//Scaling Attributes

	private boolean  formatNumberScale=true;  //if value is 12,000 FChart shows as 12k .if want to show 12,000 set it to false
	private String   defaultNumberScale;
	private String   numberScaleValue;
	private String   numberScaleUnit;

	//Applicable for dual axis charts
	private boolean  sFormatNumberScale=true;
	private String   sDefaultNumberScale;
	private String   sNumberScaleValue;
	private String   sNumberScaleUnit;


	public void setNumberFormat(int decimals,String numberPrefix
			,String numberSuffix,boolean forceDecimals){
		this.decimals=decimals;
		this.numberPrefix=numberPrefix;
		this.numberSuffix=numberSuffix;
		this.forceDecimals=forceDecimals;
	}

	public void setNumberFormatForSecondYAxis(int decimals,String numberPrefix
			,String numberSuffix,boolean forceDecimals){

		this.sDecimals=decimals;
		this.sNumberPrefix=numberPrefix;
		this.sNumberSuffix=numberSuffix;
		this.sForceDecimals=forceDecimals;
	}


	public void setNumberScale(boolean formatNumberScale,String defaultNumberScale
			,String numberScaleValue,String numberScaleUnit){

		this.formatNumberScale=formatNumberScale;
		this.defaultNumberScale=defaultNumberScale;
		this.numberScaleValue=numberScaleValue;
		this.numberScaleUnit=numberScaleUnit;

	}
	public void setNumberScaleSecondYAxis(boolean formatNumberScale
			,String defaultNumberScale,String numberScaleValue,String numberScaleUnit){

		this.sFormatNumberScale=formatNumberScale;
		this.sDefaultNumberScale=defaultNumberScale;
		this.sNumberScaleValue=numberScaleValue;
		this.sNumberScaleUnit=numberScaleUnit;


	}


	//Getters and Setters

	public String getDefaultNumberScale() {
		return defaultNumberScale;
	}

	public void setDefaultNumberScale(String defaultNumberScale) {
		this.defaultNumberScale = defaultNumberScale;
	}

	public String getNumberScaleValue() {
		return numberScaleValue;
	}

	public void setNumberScaleValue(String numberScaleValue) {
		this.numberScaleValue = numberScaleValue;
	}

	public String getNumberScaleUnit() {
		return numberScaleUnit;
	}

	public void setNumberScaleUnit(String numberScaleUnit) {
		this.numberScaleUnit = numberScaleUnit;
	}

	public String getsDefaultNumberScale() {
		return sDefaultNumberScale;
	}

	public void setsDefaultNumberScale(String sDefaultNumberScale) {
		this.sDefaultNumberScale = sDefaultNumberScale;
	}

	public String getsNumberScaleValue() {
		return sNumberScaleValue;
	}

	public void setsNumberScaleValue(String sNumberScaleValue) {
		this.sNumberScaleValue = sNumberScaleValue;
	}

	public String getsNumberScaleUnit() {
		return sNumberScaleUnit;
	}

	public void setsNumberScaleUnit(String sNumberScaleUnit) {
		this.sNumberScaleUnit = sNumberScaleUnit;
	}

	public int getDecimals() {
		return decimals;
	}

	public void setDecimals(int decimals) {
		this.decimals = decimals;
	}

	public boolean isForceDecimals() {
		return forceDecimals;
	}

	public void setForceDecimals(boolean forceDecimals) {
		this.forceDecimals = forceDecimals;
	}

	public boolean isFormatNumberScale() {
		return formatNumberScale;
	}

	public void setFormatNumberScale(boolean formatNumberScale) {
		this.formatNumberScale = formatNumberScale;
	}

	public boolean isFormatNumber() {
		return formatNumber;
	}

	public void setFormatNumber(boolean formatNumber) {
		this.formatNumber = formatNumber;
	}

	public String getNumberPrefix() {
		return numberPrefix;
	}

	public void setNumberPrefix(String numberPrefix) {
		this.numberPrefix = numberPrefix;
	}

	public String getNumberSuffix() {
		return numberSuffix;
	}

	public void setNumberSuffix(String numberSuffix) {
		this.numberSuffix = numberSuffix;
	}

	public int getsDecimals() {
		return sDecimals;
	}

	public void setsDecimals(int sDecimals) {
		this.sDecimals = sDecimals;
	}

	public boolean issForceDecimals() {
		return sForceDecimals;
	}

	public void setsForceDecimals(boolean sForceDecimals) {
		this.sForceDecimals = sForceDecimals;
	}

	public boolean issFormatNumberScale() {
		return sFormatNumberScale;
	}

	public void setsFormatNumberScale(boolean sFormatNumberScale) {
		this.sFormatNumberScale = sFormatNumberScale;
	}

	public boolean issFormatNumber() {
		return sFormatNumber;
	}

	public void setsFormatNumber(boolean sFormatNumber) {
		this.sFormatNumber = sFormatNumber;
	}

	public String getsNumberPrefix() {
		return sNumberPrefix;
	}

	public void setsNumberPrefix(String sNumberPrefix) {
		this.sNumberPrefix = sNumberPrefix;
	}

	public String getsNumberSuffix() {
		return sNumberSuffix;
	}

	public void setsNumberSuffix(String sNumberSuffix) {
		this.sNumberSuffix = sNumberSuffix;
	}
	
	public boolean isForceYAxisValueDecimals() {
		return forceYAxisValueDecimals;
	}

	public void setForceYAxisValueDecimals(boolean forceYAxisValueDecimals) {
		this.forceYAxisValueDecimals = forceYAxisValueDecimals;
	}

	public int getyAxisValueDecimals() {
		return yAxisValueDecimals;
	}

	public void setyAxisValueDecimals(int yAxisValueDecimals) {
		this.yAxisValueDecimals = yAxisValueDecimals;
	}

	public boolean isForceSYAxisValueDecimals() {
		return forceSYAxisValueDecimals;
	}

	public void setForceSYAxisValueDecimals(boolean forceSYAxisValueDecimals) {
		this.forceSYAxisValueDecimals = forceSYAxisValueDecimals;
	}

	public int getsYAxisValueDecimals() {
		return sYAxisValueDecimals;
	}

	public void setsYAxisValueDecimals(int sYAxisValueDecimals) {
		this.sYAxisValueDecimals = sYAxisValueDecimals;
	}
}
