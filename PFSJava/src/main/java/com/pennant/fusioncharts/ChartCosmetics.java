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
	
	public ChartCosmetics() {
		super();
	}
	
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


	public String getBgColor() {
		return bgColor;
	}
	public void setBgColor(String bgColor) {
		this.bgColor = bgColor;
	}

	public int getBgAlpha() {
		return bgAlpha;
	}
	public void setBgAlpha(int bgAlpha) {
		this.bgAlpha = bgAlpha;
	}

	public int getBgRatio() {
		return bgRatio;
	}
	public void setBgRatio(int bgRatio) {
		this.bgRatio = bgRatio;
	}

	public int getBgAngle() {
		return bgAngle;
	}
	public void setBgAngle(int bgAngle) {
		this.bgAngle = bgAngle;
	}

	public boolean isShowBorder() {
		return showBorder;
	}
	public void setShowBorder(boolean showBorder) {
		this.showBorder = showBorder;
	}

	public String getBorderColor() {
		return borderColor;
	}
	public void setBorderColor(String borderColor) {
		this.borderColor = borderColor;
	}

	public int getBorderThickness() {
		return borderThickness;
	}
	public void setBorderThickness(int borderThickness) {
		this.borderThickness = borderThickness;
	}

	public int getBorderAlpha() {
		return borderAlpha;
	}
	public void setBorderAlpha(int borderAlpha) {
		this.borderAlpha = borderAlpha;
	}

	public String getBgImage() {
		return bgImage;
	}
	public void setBgImage(String bgImage) {
		this.bgImage = bgImage;
	}

	public int getBgImageAlpha() {
		return bgImageAlpha;
	}
	public void setBgImageAlpha(int bgImageAlpha) {
		this.bgImageAlpha = bgImageAlpha;
	}

	public String getBgImageDisplayMode() {
		return bgImageDisplayMode;
	}
	public void setBgImageDisplayMode(String bgImageDisplayMode) {
		this.bgImageDisplayMode = bgImageDisplayMode;
	}

	public String getBgImageVAlign() {
		return bgImageVAlign;
	}
	public void setBgImageVAlign(String bgImageVAlign) {
		this.bgImageVAlign = bgImageVAlign;
	}

	public String getBgImageHAlign() {
		return bgImageHAlign;
	}
	public void setBgImageHAlign(String bgImageHAlign) {
		this.bgImageHAlign = bgImageHAlign;
	}

	public int getBgImageScale() {
		return bgImageScale;
	}
	public void setBgImageScale(int bgImageScale) {
		this.bgImageScale = bgImageScale;
	}

	public String getLogoURL() {
		return logoURL;
	}
	public void setLogoURL(String logoURL) {
		this.logoURL = logoURL;
	}

	public int getLogoPosition() {
		return logoPosition;
	}
	public void setLogoPosition(int logoPosition) {
		this.logoPosition = logoPosition;
	}

	public int getLogoAlpha() {
		return logoAlpha;
	}
	public void setLogoAlpha(int logoAlpha) {
		this.logoAlpha = logoAlpha;
	}

	public int getLogoScale() {
		return logoScale;
	}
	public void setLogoScale(int logoScale) {
		this.logoScale = logoScale;
	}

	public String getLogoLink() {
		return logoLink;
	}
	public void setLogoLink(String logoLink) {
		this.logoLink = logoLink;
	}

	public String getCanvasBgColor() {
		return canvasBgColor;
	}
	public void setCanvasBgColor(String canvasBgColor) {
		this.canvasBgColor = canvasBgColor;
	}

	public int getCanvasBgAlpha() {
		return canvasBgAlpha;
	}
	public void setCanvasBgAlpha(int canvasBgAlpha) {
		this.canvasBgAlpha = canvasBgAlpha;
	}

	public int getCanvasBgRatio() {
		return canvasBgRatio;
	}
	public void setCanvasBgRatio(int canvasBgRatio) {
		this.canvasBgRatio = canvasBgRatio;
	}

	public int getCanvasBgAngle() {
		return canvasBgAngle;
	}
	public void setCanvasBgAngle(int canvasBgAngle) {
		this.canvasBgAngle = canvasBgAngle;
	}

	public String getCanvasBorderColor() {
		return canvasBorderColor;
	}
	public void setCanvasBorderColor(String canvasBorderColor) {
		this.canvasBorderColor = canvasBorderColor;
	}

	public int getCanvasBorderThickness() {
		return canvasBorderThickness;
	}
	public void setCanvasBorderThickness(int canvasBorderThickness) {
		this.canvasBorderThickness = canvasBorderThickness;
	}

	public int getCanvasBorderAlpha() {
		return canvasBorderAlpha;
	}
	public void setCanvasBorderAlpha(int canvasBorderAlpha) {
		this.canvasBorderAlpha = canvasBorderAlpha;
	}
	
	
}

