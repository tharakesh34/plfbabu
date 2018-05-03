package com.pennant.util.Constraint;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Constraint;

public class PTPhoneNumberValidator implements Constraint{
	private String fieldParm;
	private boolean mandatory=false;
	private final String PHONE_REGEX = "^[0-9]{11}";
	private int maxLength=11;
	private final String PHONE_COUNTRY_REGEX = "^[1-9]{1}[0-9]{0,3}";
	private final String PHONE_AREA_REGEX = "^[1-9]{1}[0-9]{0,3}";
	private final String PHONE_NUMBER_REGEX = "[0-9]{6,8}";
	
	public static final int VALIDATE_COUNTRY = 1;
	public static final int VALIDATE_AREACODE = 2;
	public static final int VALIDATE_NUMBER = 3;
	
	private int validateCode = 0;
	
	public PTPhoneNumberValidator(String fieldParm,boolean mandatory) {
		setFieldParm(fieldParm);
		setMandatory(mandatory);
	}
	
	public PTPhoneNumberValidator(String fieldParm, boolean mandatory, int codeType) {
		setFieldParm(fieldParm);
		setMandatory(mandatory);
		validateCode = codeType;
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
		}else{ 
			Pattern pattern = null;
			Matcher matcher = null;
			switch (validateCode) {
			case VALIDATE_COUNTRY:
				pattern = Pattern.compile(PHONE_COUNTRY_REGEX);
				matcher =  pattern.matcher(compValue);
				if(!matcher.matches()){
					return Labels.getLabel("FIELD_PHONE_COUNTRY", new String[] {fieldParm});
				}
				break;
			case VALIDATE_AREACODE:
				pattern = Pattern.compile(PHONE_AREA_REGEX);
				matcher =  pattern.matcher(compValue);
				if(!matcher.matches()){
					return Labels.getLabel("FIELD_PHONE_AREACODE", new String[] {fieldParm});
				}
				break;
			case VALIDATE_NUMBER:
				pattern = Pattern.compile(PHONE_NUMBER_REGEX);
				matcher =  pattern.matcher(compValue);
				if(!matcher.matches()){
					return Labels.getLabel("FIELD_PHONE_NUMBER", new String[] {fieldParm});
				}
				break;
			default:
				pattern = Pattern.compile(PHONE_REGEX);
				matcher =  pattern.matcher(compValue);
				
				if(!matcher.matches()){
						return Labels.getLabel("FIELD_PHONE", new String[] {fieldParm});
				}
	
				if(compValue.length()!=maxLength){
					return Labels.getLabel("FIELD_ALLOWED_MANFILL", new String[] {fieldParm,String.valueOf(maxLength)});
				}
				break;
			}
			pattern = null;
			matcher = null;
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

