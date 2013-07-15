package com.pennant.fusioncharts;

import org.apache.commons.lang.StringUtils;

public final class ChartStyleDefinition extends ChartUtil {
	private String name;
		private String type;
	
	private String font;
	private int size=0;
	private String color;
	private String align;
	private boolean bold=false;
	private boolean italic=false;
	private boolean underline=false;
	private String bgColor;
	private String borderColor;
	private boolean isHTML=false;
	private int leftMargin=0;
	private int letterSpacing=0;
	private String param;
	private String start;
	private int duration;
	private String easing;
	private int distance=0;
	private int angle=0;
	private int alpha=0;
	private int blurX;
	private int blurY;
	private int strength;
	private int quality;
	private String shadowColor;
	private int shadowAlpha=0;
	private String highlightColor;
	private int highlightAlpha;
	

	// For Font 	
	public ChartStyleDefinition(String name, String font, String color,
			String align, boolean bold, boolean italic, String bgColor,
			String borderColor, boolean isHTML, int leftMargin, int letterSpacing) {
		super();
		this.name = name;
		this.type="font";
		this.font = font;
		this.color = color;
		this.align = align;
		this.bold = bold;
		this.italic = italic;
		this.bgColor = bgColor;
		this.borderColor = borderColor;
		this.isHTML = isHTML;
		this.leftMargin = leftMargin;
		this.letterSpacing = letterSpacing;
	}

	// For Animation 	
	public ChartStyleDefinition(String name, String param, String start,
			int duration, String easing) {
		super();
		this.name = name;
		this.type="ANIMATION";
		this.param = param;
		this.start = start;
		this.duration = duration;
		this.easing = easing;
	}

	// For Shadow
	public ChartStyleDefinition(String name, String color, int distance,
			int angle, int  alpha, int blurX, int blurY, int strength,
			int quality) {
		super();
		this.name = name;
		this.type="Shadow";
		this.color = color;
		this.distance = distance;
		this.angle = angle;
		this.alpha = alpha;
		this.blurX = blurX;
		this.blurY = blurY;
		this.strength = strength;
		this.quality = quality;
	}
	
	// For Glow 
	public ChartStyleDefinition(String name, String color,int alpha, int blurX, int blurY, int strength,int quality) {
		super();
		this.name = name;
		this.type="Glow";
		this.color = color;
		this.alpha = alpha;
		this.blurX = blurX;
		this.blurY = blurY;
		this.strength = strength;
		this.quality = quality;
	}
	
	
	
	// For Bevel 
	public ChartStyleDefinition(String name,int distance, int angle, int blurX,
			int blurY, int strength, int quality, String shadowColor,
			int shadowAlpha, String highlightColor, int highlightAlpha) {
		super();
		this.name = name;
		this.type="Bevel";
		this.distance = distance;
		this.angle = angle;
		this.blurX = blurX;
		this.blurY = blurY;
		this.strength = strength;
		this.quality = quality;
		this.shadowColor = shadowColor;
		this.shadowAlpha = shadowAlpha;
		this.highlightColor = highlightColor;
		this.highlightAlpha = highlightAlpha;
	}

	// For Blur
	public ChartStyleDefinition(String name,int blurX,int blurY, int quality) {
		super();
		this.name = name;
		this.type="Blur";
		this.blurX = blurX;
		this.blurY = blurY;
		this.quality = quality;
	}
	
	
	
	public String getFontStyle (){
		StringBuffer fontStyle= new StringBuffer("< style "); 
		if(StringUtils.trimToEmpty(this.name).equals("")){
			return "";
		}else{
			fontStyle = getIntElement("name", this.name, fontStyle);
		}

		fontStyle = getIntElement("type", this.type, fontStyle);
		fontStyle = getIntElement("font", this.font, fontStyle);
		fontStyle = getIntElement("size", this.size, fontStyle);
		fontStyle = getIntElement("color", this.color, fontStyle);
		fontStyle = getIntElement("align", this.align, fontStyle);
		fontStyle = getIntElement("bold", this.bold, fontStyle);
		fontStyle = getIntElement("italic", this.italic, fontStyle);
		fontStyle = getIntElement("underline", this.underline, fontStyle);
		fontStyle = getIntElement("bgColor", this.bgColor, fontStyle);
		fontStyle = getIntElement("borderColor", this.borderColor, fontStyle);
		fontStyle = getIntElement("isHTML", this.isHTML, fontStyle);
		fontStyle = getIntElement("leftMargin", this.leftMargin, fontStyle);
		fontStyle = getIntElement("letterSpacing", this.letterSpacing, fontStyle);
		fontStyle.append("/>");
		
		return fontStyle.toString();
	}
	
	public String getAnimationStyle (){
		StringBuffer fontStyle= new StringBuffer("< style "); 
		if(StringUtils.trimToEmpty(this.name).equals("")){
			return "";
		}else{
			fontStyle = getIntElement("name", this.name, fontStyle);
		}

		fontStyle = getIntElement("type", this.type, fontStyle);
		fontStyle = getIntElement("param", this.param, fontStyle);
		fontStyle = getIntElement("start", this.start, fontStyle);
		fontStyle = getIntElement("duration", this.duration, fontStyle);
		fontStyle = getIntElement("easing", this.easing, fontStyle);

		fontStyle.append("/>");
		return fontStyle.toString();
	}

	
	public String getShadowStyle (){
		StringBuffer fontStyle= new StringBuffer("< style "); 
		if(StringUtils.trimToEmpty(this.name).equals("")){
			return "";
		}else{
			fontStyle = getIntElement("name", this.name, fontStyle);
		}

		fontStyle = getIntElement("type", this.type, fontStyle);
		fontStyle = getIntElement("distance", this.distance, fontStyle);
		fontStyle = getIntElement("angle", this.angle, fontStyle);
		fontStyle = getIntElement("color", this.color, fontStyle);
		fontStyle = getIntElement("angle", this.angle, fontStyle);
		fontStyle = getIntElement("alpha", this.alpha, fontStyle);
		fontStyle = getIntElement("blurX", this.blurX, fontStyle);
		fontStyle = getIntElement("blurY", this.blurY, fontStyle);
		fontStyle = getIntElement("strength", this.strength, fontStyle);
		fontStyle = getIntElement("quality", this.quality, fontStyle);
		
		fontStyle.append("/>");
		return fontStyle.toString();
	}
	

	public String getGlowStyle (){
		StringBuffer fontStyle= new StringBuffer("< style "); 
		if(StringUtils.trimToEmpty(this.name).equals("")){
			return "";
		}else{
			fontStyle = getIntElement("name", this.name, fontStyle);
		}

		fontStyle = getIntElement("type", this.type, fontStyle);
		fontStyle = getIntElement("color", this.color, fontStyle);
		fontStyle = getIntElement("alpha", this.alpha, fontStyle);
		fontStyle = getIntElement("blurX", this.blurX, fontStyle);
		fontStyle = getIntElement("blurY", this.blurY, fontStyle);
		fontStyle = getIntElement("strength", this.strength, fontStyle);
		fontStyle = getIntElement("quality", this.quality, fontStyle);
		
		fontStyle.append("/>");
		return fontStyle.toString();
	}
	

	public String getBevelStyle (){
		StringBuffer fontStyle= new StringBuffer("< style "); 
		if(StringUtils.trimToEmpty(this.name).equals("")){
			return "";
		}else{
			fontStyle = getIntElement("name", this.name, fontStyle);
		}

		fontStyle = getIntElement("angle", this.angle, fontStyle);
		fontStyle = getIntElement("distance", this.distance, fontStyle);
		fontStyle = getIntElement("shadowColor", this.shadowColor, fontStyle);
		fontStyle = getIntElement("shadowAlpha", this.shadowAlpha, fontStyle);
		fontStyle = getIntElement("highlightColor", this.highlightColor, fontStyle);
		fontStyle = getIntElement("highlightAlpha", this.highlightAlpha, fontStyle);
		fontStyle = getIntElement("blurX", this.blurX, fontStyle);
		fontStyle = getIntElement("blurY", this.blurY, fontStyle);
		fontStyle = getIntElement("strength", this.strength, fontStyle);
		fontStyle = getIntElement("quality", this.quality, fontStyle);
		
		fontStyle.append("/>");
		return fontStyle.toString();
	}
	
	public String getBlurStyle(){
		StringBuffer fontStyle= new StringBuffer("< style "); 
		if(StringUtils.trimToEmpty(this.name).equals("")){
			return "";
		}else{
			fontStyle = getIntElement("name", this.name, fontStyle);
		}

		fontStyle = getIntElement("blurX", this.blurX, fontStyle);
		fontStyle = getIntElement("blurY", this.blurY, fontStyle);
		fontStyle = getIntElement("quality", this.quality, fontStyle);
		
		fontStyle.append("/>");
		return fontStyle.toString();
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

}
