package com.pennant.fusioncharts;

import org.apache.commons.lang.StringUtils;

public class ChartStyleApplication {

	private String toObject;
	private String styles;
	
	
	public ChartStyleApplication(String toObject, String styles) {
		super();
		this.toObject = toObject;
		this.styles = styles;
	}


	public String getApplicationString(){
		StringBuffer fontStyle= new StringBuffer("< apply  "); 
		if(StringUtils.trimToEmpty(this.toObject).equals("")){
			return "";
		}else{
			fontStyle = getIntElement("toObject", this.toObject, fontStyle);
		}

		fontStyle = getIntElement("styles", this.styles, fontStyle);
		fontStyle.append("/>");
		return fontStyle.toString();
	}
	
	private StringBuffer getIntElement(String field,String value,StringBuffer buffer){
		
		if(!StringUtils.trimToEmpty(value).equals("")){
			buffer.append(" field ='");
			buffer.append(value);
			buffer.append("' ");
		}

		
		return buffer;
	}


	public String getToObject() {
		return toObject;
	}


	public String getStyles() {
		return styles;
	}
	
}
