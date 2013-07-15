package com.pennant.fusioncharts;


public class ChartCosmetics extends ChartUtil {
	
	private String bgColor;
	private int bgAlpha=0;
	private int bgRatio=0;
	private int bgAngle=0;
	
	private boolean showBorder =false;
	private String borderColor;
	private int borderThickness=0;
	private int borderAlpha=0;
	
	private String bgImage;
	private int bgImageAlpha=0;
	private String bgImageDisplayMode;
	private String bgImageVAlign;
	private String bgImageHAlign;
	private int bgImageScale;
	
	private String logoURL;
	private int logoPosition=0;
	private int logoAlpha=0;
	private int logoScale=0;
	private String logoLink;
	
	private String canvasBgColor;
	private int canvasBgAlpha=0;
	private int canvasBgRatio=0;
	private int canvasBgAngle=0;

	private String canvasBorderColor;
	private int canvasBorderThickness=0;
	private int canvasBorderAlpha=0;
	
	public void setBackground (String bgColor,int bgAlpha,int bgRatio,int bgAngle){
		this.bgColor=bgColor;
		this.bgAlpha=bgAlpha;
		this.bgRatio=bgRatio;
		this.bgAngle=bgAngle;
	}
	
	
	public void setBorder(boolean showBorder,String borderColor,int borderThickness,int borderAlpha){
		
		this.showBorder =showBorder;
		this.borderColor=borderColor;
		this.borderThickness=borderThickness;
		this.borderAlpha=borderAlpha;
		
	} 
	
	
	public void setBorderImage(String bgImage,int bgImageAlpha,String bgImageDisplayMode,String bgImageVAlign,String bgImageHAlign,int bgImageScale ){
		this.bgImage=bgImage;
		this.bgImageAlpha=bgImageAlpha;
		this.bgImageDisplayMode =bgImageDisplayMode ;
		this.bgImageVAlign=bgImageVAlign;
		this.bgImageHAlign=bgImageHAlign;
		this.bgImageScale=bgImageScale;
		
	}
	
	public void setLogo(String logoURL,int logoPosition,int logoAlpha,int logoScale,String logoLink ){
		this.logoURL=logoURL;
		this.logoPosition=logoPosition ;
		this.logoAlpha=logoAlpha;	
		this.logoScale =logoScale ;
		this.logoLink=logoLink;
	}
	
	public void setCanvasBackground(String canvasBgColor,int canvasBgAlpha,int canvasBgRatio,int canvasBgAngle){
		this.canvasBgColor=canvasBgColor;
		this.canvasBgAlpha=canvasBgAlpha;
		this.canvasBgRatio=canvasBgRatio;
		this.canvasBgAngle=canvasBgAngle;
				
	}
	
	public void setCanvasBorder(String canvasBorderColor,int canvasBorderThickness,	int canvasBorderAlpha){
		this.canvasBorderColor=canvasBorderColor;
		this.canvasBorderThickness=canvasBorderThickness;
		this.canvasBorderAlpha=canvasBorderAlpha;
				
	}
}

