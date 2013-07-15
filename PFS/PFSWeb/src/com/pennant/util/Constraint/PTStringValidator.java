package com.pennant.util.Constraint;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Constraint;

import com.pennant.backend.util.PennantRegularExpressions;

public class PTStringValidator implements Constraint{

	private String fieldParm;
	private String regExp;
	private int minLength;
	private int maxLength;
	private boolean mandatory=false;
	private boolean minLenValid=false;
	private boolean maxLenValid=false;
	
	
	public PTStringValidator (String fieldParm,String regExp){
		setFieldParm(fieldParm);
		setRegExp(regExp);
		setMinLength(0);
		setMaxLength(0);
	}

	
	public PTStringValidator (String fieldParm,String regExp,boolean mandatory){
		setFieldParm(fieldParm);
		setRegExp(regExp);
		setMandatory(mandatory);
		setMinLength(0);
		setMaxLength(0);

	}

	public PTStringValidator (String fieldParm,String regExp,boolean mandatory,int maxLength){
		setFieldParm(fieldParm);
		setRegExp(regExp);
		setMandatory(mandatory);
		setMinLength(0);
		setMaxLength(maxLength);
	}

	public PTStringValidator (String fieldParm,String regExp,int maxLength){
		setFieldParm(fieldParm);
		setRegExp(regExp);
		setMandatory(mandatory);
		setMinLength(0);
		setMaxLength(maxLength);
	}

	public PTStringValidator (String fieldParm,String regExp,boolean mandatory,int minLength,int maxLength){
		setFieldParm(fieldParm);
		setRegExp(regExp);
		setMandatory(mandatory);
		setMinLength(minLength);
		setMaxLength(maxLength);
	}

	public PTStringValidator (String fieldParm,String regExp,int minLength,int maxLength){
		setFieldParm(fieldParm);
		setRegExp(regExp);
		setMandatory(mandatory);
		setMinLength(minLength);
		setMaxLength(maxLength);
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
		
		if(regExp!=null){
			if(PennantRegularExpressions.getRegexMapper(regExp)==null){
				return Labels.getLabel("REGEX_INVALID", new String[] {regExp});
			}

			Pattern pattern=null;
			try {
				pattern = Pattern.compile(PennantRegularExpressions
						.getRegexMapper(regExp));
			} catch (Exception e) {
				return Labels.getLabel("REGEX_INVALID", new String[] {regExp});
			}
			
			Matcher matcher =  pattern.matcher(compValue);
			validRegex=matcher.matches();

			if(!validRegex){
					return Labels.getLabel(regExp, new String[] {fieldParm});
			}
			
		}
		if(minLenValid && maxLenValid){
			if(compValue.length()< minLength || compValue.length() > maxLength){
				return Labels.getLabel("FIELD_ALLOWED_RANGE", new String[] {fieldParm,String.valueOf(minLength),String.valueOf(maxLength)});
			}
			
			return null;
		}
		
		if(minLenValid){

			if(compValue.length()< minLength){
				return Labels.getLabel("FIELD_ALLOWED_MINLENGTH", new String[] {fieldParm,String.valueOf(minLength)});
			}
			return null;
		}

		if(maxLenValid){
			if(compValue.length() != maxLength){
				return Labels.getLabel("FIELD_ALLOWED_MANFILL", new String[] {fieldParm,String.valueOf(maxLength)});
			}
		}

		return "";
	}

	
	String getFieldParm() {
		return fieldParm;
	}


	void setFieldParm(String fieldParm) {
		this.fieldParm = fieldParm;
	}


	String getRegExp() {
		return regExp;
	}


	void setRegExp(String regExp) {
		this.regExp = regExp;
	}


	int getMinLength() {
		return minLength;
	}


	void setMinLength(int minLength) {
		this.minLength = minLength;
		if(minLength!=0){
			minLenValid=true;
		}
	}


	int getMaxLength() {
		return maxLength;
	}


	void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
		if(maxLength!=0){
			maxLenValid=true;
		}
	}


	boolean isMandatory() {
		return mandatory;
	}


	void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}
}
