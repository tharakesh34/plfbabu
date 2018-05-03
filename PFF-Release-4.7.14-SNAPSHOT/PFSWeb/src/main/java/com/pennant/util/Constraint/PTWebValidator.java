package com.pennant.util.Constraint;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Constraint;

public class PTWebValidator implements Constraint{
	private String fieldParm;
	private boolean mandatory=false;
	private final String WEB_REGEX = "^[wW]{3}[\\.]{1}([a-zA-z]+[0-9]*)(\\.?[a-zA-Z]{2,4})?\\.{1}[a-zA-Z]{2,3}";
	private int maxLength=100;
	
	public PTWebValidator(String fieldParm,boolean mandatory) {
		setFieldParm(fieldParm);
		setMandatory(mandatory);
	}
	
	@Override
	public void validate(Component comp, Object value) throws WrongValueException {
		String errorMessage=getErrorMessage(value);
		if(StringUtils.isNotBlank(errorMessage)){
			throw new WrongValueException(comp, errorMessage);
		}
	}

	
	
	private String getErrorMessage(Object value){

		String compValue=null;
		boolean validRegex=false;

		if(value!=null){
			compValue= value.toString();
		}
		
		if (StringUtils.isBlank(compValue) ) {
			if(isMandatory()){
				return Labels.getLabel("FIELD_IS_MAND", new String[] {fieldParm});	
			}else{
				compValue="";
				return null;
			}
		} else{
			if(compValue.length()>maxLength){
				return Labels.getLabel("FIELD_ALLOWED_MAXLENGTH", new String[] {fieldParm,String.valueOf(maxLength)});
			}
	
			Pattern pattern = Pattern.compile(WEB_REGEX);
			Matcher matcher =  pattern.matcher(compValue);
			validRegex=matcher.matches();
			
			if(!validRegex){
					return Labels.getLabel("FIELD_WEBSITE", new String[] {fieldParm});
			}
		}
		return null;
	}

	String getFieldParm() {
		return fieldParm;
	}

	boolean isMandatory() {
		return mandatory;
	}

	void setFieldParm(String fieldParm) {
		this.fieldParm = fieldParm;
	}

	void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}
}

