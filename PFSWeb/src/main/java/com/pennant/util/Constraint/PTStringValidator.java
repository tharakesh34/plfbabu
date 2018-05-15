package com.pennant.util.Constraint;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Constraint;

import com.pennant.backend.util.PennantRegularExpressions;

public class PTStringValidator implements Constraint{
	private static final Logger logger = Logger.getLogger(PTStringValidator.class);

	private String fieldParm;
	private String regExp;
	private int minLength;
	private int maxLength;
	private boolean mandatory=false;
	private boolean extendedCombo=false;
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
		setExtendedCombo(extendedCombo);
		setMinLength(0);
		setMaxLength(0);

	}
	
	public PTStringValidator (String fieldParm,String regExp,boolean mandatory,boolean extendedCombo){
		setFieldParm(fieldParm);
		setRegExp(regExp);
		setMandatory(mandatory);
		setExtendedCombo(extendedCombo);
		setMinLength(0);
		setMaxLength(0);
		
	}

	public PTStringValidator (String fieldParm,String regExp,boolean mandatory,int maxLength){
		setFieldParm(fieldParm);
		setRegExp(regExp);
		setMandatory(mandatory);
		setExtendedCombo(extendedCombo);
		setMinLength(0);
		setMaxLength(maxLength);
	}

	public PTStringValidator (String fieldParm,String regExp,int maxLength){
		setFieldParm(fieldParm);
		setRegExp(regExp);
		setMandatory(mandatory);
		setExtendedCombo(extendedCombo);
		setMinLength(0);
		setMaxLength(maxLength);
	}

	public PTStringValidator (String fieldParm,String regExp,boolean mandatory,int minLength,int maxLength){
		setFieldParm(fieldParm);
		setRegExp(regExp);
		setMandatory(mandatory);
		setExtendedCombo(extendedCombo);
		setMinLength(minLength);
		setMaxLength(maxLength);
	}

	public PTStringValidator (String fieldParm,String regExp,int minLength,int maxLength){
		setFieldParm(fieldParm);
		setRegExp(regExp);
		setMandatory(mandatory);
		setExtendedCombo(extendedCombo);
		setMinLength(minLength);
		setMaxLength(maxLength);
	}

	@Override
	public void validate(Component comp, Object value) throws WrongValueException {
		String errorMessage=getErrorMessage(value);
		if(StringUtils.isNotBlank(errorMessage)){
			if(isExtendedCombo()){
				Component nextComp = comp.getNextSibling();
				throw new WrongValueException(nextComp, errorMessage);
			}else{
				throw new WrongValueException(comp, errorMessage);
			}
		}
	}

	
	private String getErrorMessage(Object value){

		String compValue=null;
		boolean validRegex=false;
		String expression=null;
		
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
			
			if(regExp!=null){
				expression =PennantRegularExpressions.getRegexMapper(regExp);
			}
			
			if(expression==null){
				return Labels.getLabel("REGEX_INVALID", new String[] {regExp});
			}
			
			
		
			Pattern pattern=null;
			try {
				pattern = Pattern.compile(expression);
			} catch (Exception e) {
				logger.error("Exception: ", e);
				return Labels.getLabel("REGEX_INVALID", new String[] {expression});
			}
			
			Matcher matcher =  pattern.matcher(compValue);
				validRegex=matcher.matches();
	
				if(!validRegex){
						return Labels.getLabel(regExp, new String[] {fieldParm});
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
		}
		return "";
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getFieldParm() {
		return fieldParm;
	}
	public void setFieldParm(String fieldParm) {
		this.fieldParm = fieldParm;
	}

	public String getRegExp() {
		return regExp;
	}
	public void setRegExp(String regExp) {
		this.regExp = regExp;
	}

	public int getMinLength() {
		return minLength;
	}
	public void setMinLength(int minLength) {
		this.minLength = minLength;
		if(minLength!=0){
			minLenValid=true;
		}
	}

	public int getMaxLength() {
		return maxLength;
	}
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
		if(maxLength!=0){
			maxLenValid=true;
		}
	}

	public boolean isMandatory() {
		return mandatory;
	}
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public boolean isExtendedCombo() {
		return extendedCombo;
	}
	public void setExtendedCombo(boolean extendedCombo) {
		this.extendedCombo = extendedCombo;
	}
}
