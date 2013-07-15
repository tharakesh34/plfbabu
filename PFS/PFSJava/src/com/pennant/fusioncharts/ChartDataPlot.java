
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
 * FileName    		:  ChartDataPlot.java                                            * 	  
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
public class ChartDataPlot {
	//@see http://docs.fusioncharts.com/charts-->Quick Chart Configuration/DataPlot

	private String  plotGradientColor;  //Color of gradient
	private int     plotFillAngle;      //this attribute lets you set the fill angle for gradient
	private String  plotFillRatio;      // this attribute lets you set the ratio for gradient. e.g '70,30' 0r '40,60'
	private String  plotFillAlpha ;     //this attribute lets you set the fill alpha for gradient.
	private boolean showPlotBorder;     //set true to show plot border
	private boolean plotBorderDashed;   //set true for dash line border
	private int     plotBorderDashLen;  
	private int     plotBorderDashGap; 
	private boolean useRoundEdges;     //set true for round edge glass effect border
	
	
	public ChartDataPlot(String plotGradientColor,boolean useRoundEdges,int plotFillAngle,String plotFillRatio,String plotFillAlpha){
		this.plotGradientColor=plotGradientColor;
		this.useRoundEdges=useRoundEdges;
		this.plotFillAngle=plotFillAngle;
		this.plotFillRatio=plotFillRatio;
		this.plotFillAlpha=plotFillAlpha;
			
	}
	public void setPlotBorder(boolean showPlotBorder,boolean plotBorderDashed,int plotBorderDashLen,
			int plotBorderDashGap){
		
		this.showPlotBorder=showPlotBorder;
		this.plotBorderDashed=plotBorderDashed;
		this.plotBorderDashLen=plotBorderDashLen;
		this.plotBorderDashGap=plotBorderDashGap;
		
	}
	//Getters and Setters 
	
	public String getPlotGradientColor() {
		return plotGradientColor;
	}
	public void setPlotGradientColor(String plotGradientColor) {
		this.plotGradientColor = plotGradientColor;
	}
	public int getPlotFillAngle() {
		return plotFillAngle;
	}
	public void setPlotFillAngle(int plotFillAngle) {
		this.plotFillAngle = plotFillAngle;
	}
	public String getPlotFillRatio() {
		return plotFillRatio;
	}
	public void setPlotFillRatio(String plotFillRatio) {
		this.plotFillRatio = plotFillRatio;
	}
	public String getPlotFillAlpha() {
		return plotFillAlpha;
	}
	public void setPlotFillAlpha(String plotFillAlpha) {
		this.plotFillAlpha = plotFillAlpha;
	}
	public boolean isShowPlotBorder() {
		return showPlotBorder;
	}
	public void setShowPlotBorder(boolean showPlotBorder) {
		this.showPlotBorder = showPlotBorder;
	}
	public boolean isPlotBorderDashed() {
		return plotBorderDashed;
	}
	public void setPlotBorderDashed(boolean plotBorderDashed) {
		this.plotBorderDashed = plotBorderDashed;
	}
	public int getPlotBorderDashLen() {
		return plotBorderDashLen;
	}
	public void setPlotBorderDashLen(int plotBorderDashLen) {
		this.plotBorderDashLen = plotBorderDashLen;
	}
	public int getPlotBorderDashGap() {
		return plotBorderDashGap;
	}
	public void setPlotBorderDashGap(int plotBorderDashGap) {
		this.plotBorderDashGap = plotBorderDashGap;
	}
	public boolean isUseRoundEdges() {
		return useRoundEdges;
	}
	public void setUseRoundEdges(boolean useRoundEdges) {
		this.useRoundEdges = useRoundEdges;
	}
}
