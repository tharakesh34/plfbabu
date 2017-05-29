package com.pennant.fusioncharts;

public class ChartDivisionalLineGrid extends ChartUtil {
	private int numDivLines=0;
	private String divLineColor;
	private int divLineThickness=0; 
	private int divLineAlpha=0;
	private boolean divLineIsDashed=false;
	private int divLineDashLen=0;
	private int divLineDashGap=0;
	
	private String zeroPlaneColor;
	private int zeroPlaneThickness;
	private int zeroPlaneAlpha=0; 
	
	private boolean zeroPlaneShowBorder=false; 
	private String zeroPlaneBorderColor;
	
	private boolean showAlternateHGridColor=false;
	private String alternateHGridColor;
	private int alternateHGridAlpha=0;

	private boolean showAlternateVGridColor=false;
	private String alternateVGridColor;
	private int alternateVGridAlpha=0;

	public ChartDivisionalLineGrid() {
		super();
	}
	
	public void setDivLine(int numDivLines,String divLineColor,int divLineThickness,int divLineAlpha){
		this.numDivLines=numDivLines;
		this.divLineColor=divLineColor;
		this.divLineThickness=divLineThickness;
		this.divLineAlpha=divLineAlpha;
		
	}
	
	/*
	 * Set the Division Line Property Dased 
	 * @param Boolean (Division Line Dashed)
	 * @param int (Dashed length)
	 * @param int (Dashed Gap)
	 * 
	 * */
	
	public void setDivLineDashed(boolean divLineIsDashed,int divLineDashLen,int divLineDashGap){
		this.divLineIsDashed=divLineIsDashed;
		this.divLineDashLen=divLineDashLen;
		this.divLineDashGap=divLineDashGap;
	}

	/*
	 * Set the Zero Panel 
	 * @param String (Plane Color)
	 * @param int (Thickness)
	 * @param int (Alpha)
	 * 
	 * */
	public void setZeroPanel(String zeroPlaneColor, int zeroPlaneThickness,int zeroPlaneAlpha){
		this.zeroPlaneColor=zeroPlaneColor;
		this.zeroPlaneThickness=zeroPlaneThickness;
		this.zeroPlaneAlpha=zeroPlaneAlpha;
	}

	/*
	 * Set the Zero Panel Border 
	 * @param Boolean (Plane Border Show)
	 * @param String (Panel border Color)
	 * 
	 * */
	
	public void setZeroPanelBorder(boolean zeroPlaneShowBorder,String zeroPlaneBorderColor){
		this.zeroPlaneShowBorder=zeroPlaneShowBorder;
		this.zeroPlaneBorderColor=zeroPlaneBorderColor;
	}

	/*
	 * Set the Zero Panel Border 
	 * @param Boolean (Show Alternative H Grid Color)
	 * @param String (Color)
	 * @param int (Alpha)
	 * 
	 * */
	
	public void setAlternateHGrid(boolean showAlternateHGridColor,String alternateHGridColor,int alternateHGridAlpha){
		this.showAlternateHGridColor=showAlternateHGridColor;
		this.alternateHGridColor=alternateHGridColor;
		this.alternateHGridAlpha=alternateHGridAlpha;
			
	}

	
	/*
	 * Set the Zero Panel Border 
	 * @param Boolean (Show Alternative V Grid Colour)
	 * @param String (Color)
	 * @param int (Alpha)
	 * 
	 * */
	
	public void setAlternateVGrid(boolean showAlternateVGridColor,String alternateVGridColor,int alternateVGridAlpha){
		this.showAlternateVGridColor=showAlternateVGridColor;
		this.alternateVGridColor=alternateVGridColor;
		this.alternateVGridAlpha=alternateVGridAlpha;
			
	}
	public String getDivisionalLineGrid(){
		StringBuffer lineGrid= new StringBuffer();
		

		lineGrid = getIntElement("numDivLines", this.numDivLines, lineGrid);
		lineGrid = getIntElement("divLineColor", this.divLineColor, lineGrid);
		lineGrid = getIntElement("divLineThickness", this.divLineThickness, lineGrid);
		lineGrid = getIntElement("divLineAlpha", this.divLineAlpha, lineGrid);
		
		if(divLineIsDashed){
			lineGrid = getIntElement("divLineIsDashed", this.divLineIsDashed, lineGrid);
			lineGrid = getIntElement("divLineDashLen", this.divLineDashLen, lineGrid);
			lineGrid = getIntElement("divLineDashGap", this.divLineDashGap, lineGrid);
		}
		
		lineGrid = getIntElement("zeroPlaneColor", this.zeroPlaneColor, lineGrid);
		lineGrid = getIntElement("zeroPlaneThickness", this.zeroPlaneThickness, lineGrid);
		lineGrid = getIntElement("zeroPlaneAlpha", this.zeroPlaneAlpha, lineGrid);
		
		if(zeroPlaneShowBorder){
			lineGrid = getIntElement("zeroPlaneShowBorder", this.zeroPlaneShowBorder, lineGrid);
			lineGrid = getIntElement("zeroPlaneBorderColor", this.zeroPlaneBorderColor, lineGrid);
			
		} 
		
		if(showAlternateHGridColor){
			lineGrid = getIntElement("showAlternateHGridColor", this.showAlternateHGridColor, lineGrid);
			lineGrid = getIntElement("alternateHGridColor", this.alternateHGridColor, lineGrid);
			lineGrid = getIntElement("alternateHGridAlpha", this.alternateHGridAlpha, lineGrid);
		}
		
		if(showAlternateVGridColor){
			lineGrid = getIntElement("showAlternateVGridColor", this.showAlternateVGridColor, lineGrid);
			lineGrid = getIntElement("alternateVGridColor", this.alternateVGridColor, lineGrid);
			lineGrid = getIntElement("alternateVGridAlpha", this.alternateVGridAlpha, lineGrid);
		}

		return lineGrid.toString();
	}
}
