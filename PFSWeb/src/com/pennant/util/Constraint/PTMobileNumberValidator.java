package com.pennant.util.Constraint;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Constraint;

public class PTMobileNumberValidator implements Constraint{
	private String fieldParm;
	private boolean mandatory=false;
	private final String MOBILE_REGEX = "^\\+971[0-9]{9}";
	private int maxLength=9;
	
	public PTMobileNumberValidator(String fieldParm,boolean mandatory) {
		setFieldParm(fieldParm);
		setMandatory(mandatory);
	}
	
	@Override
	public void validate(Component comp, Object value) throws WrongValueException {
		String errorMessage=getErrorMessage(value);
		if(!StringUtils.trimToEmpty(errorMessage).equals("")){
			throw new WrongValueException(comp, errorMessage);
		}
	}

	
	
	private String getErrorMessage(Object value){

		String compValue=null;
		boolean validRegex=false;

		if(value!=null){
			compValue= value.toString();
		}
		
		if (StringUtils.trim(compValue).equals("") ) {
			if(isMandatory()){
				return Labels.getLabel("FIELD_IS_MAND", new String[] {fieldParm});	
			}else{
				compValue="";
				return null;
			}
		} 
	
		Pattern pattern = Pattern.compile(MOBILE_REGEX);
		Matcher matcher =  pattern.matcher(compValue);
		validRegex=matcher.matches();
		
		if(!validRegex){
				return Labels.getLabel("FIELD_MOBILE", new String[] {fieldParm});
		}

		if(compValue.length()!=maxLength){
			return Labels.getLabel("FIELD_ALLOWED_MANFILL", new String[] {fieldParm,String.valueOf(maxLength)});
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

