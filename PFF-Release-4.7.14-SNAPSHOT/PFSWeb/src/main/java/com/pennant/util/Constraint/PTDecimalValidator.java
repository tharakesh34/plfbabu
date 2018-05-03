/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *
 * FileName    		:  IntValidator.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  26-04-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/
package com.pennant.util.Constraint;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Constraint;

import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantRegularExpressions;

public class PTDecimalValidator implements Constraint{
	private static final Logger logger = Logger.getLogger(PTDecimalValidator.class);

	private String fieldParm;
	private boolean mandatory=false;
	private boolean negative=false;
	private BigDecimal maxValue;
	private BigDecimal minValue;
	private int decPos ;
	private boolean minValid=false;
	private boolean maxValid=false;
	private String regExp;

	
	public PTDecimalValidator(String fieldParm, int decPos, boolean mandatory,boolean negative,double maxValue) {
		setFieldParm(fieldParm);
		setMandatory(mandatory);
		setNegative(negative);
		setMaxValue(maxValue);
		setMinValue(0);
		setDecPos(decPos);
		
	}
	
	
	public PTDecimalValidator(String fieldParm, String regExp, int decPos, boolean mandatory,boolean negative) {
		setFieldParm(fieldParm);
		setRegExp(regExp);
		setMandatory(mandatory);
		setNegative(negative);
		setDecPos(decPos);
		
	}

	public PTDecimalValidator(String fieldParm, int decPos, boolean mandatory,boolean negative,double minValue,double maxValue) {
		setFieldParm(fieldParm);
		setMandatory(mandatory);
		setNegative(negative);
		setMaxValue(maxValue);
		setMinValue(minValue);
		setDecPos(decPos);
		
	}

	public PTDecimalValidator(String fieldParm, int decPos, boolean mandatory,boolean negative) {
		setFieldParm(fieldParm);
		setMandatory(mandatory);
		setNegative(negative);
		setMaxValue(0);
		setMinValue(0);
		setDecPos(decPos);
		
	}

	public PTDecimalValidator(String fieldParm, int decPos,boolean mandatory) {
		setFieldParm(fieldParm);
		setMandatory(mandatory);
		setDecPos(decPos);
		setMaxValue(0);
		setMinValue(0);
	}

	public PTDecimalValidator(String fieldParm, int decPos) {
		this.fieldParm = fieldParm;
		setMaxValue(0);
		setMinValue(0);
		setDecPos(decPos);
	}

	public void validate(Component comp, Object value) throws WrongValueException {
		
		String errorMessage=getErrorMessage(value);
		if(StringUtils.isNotBlank(errorMessage)){
			throw new WrongValueException(comp, errorMessage);
		}
	}

	
	private String getErrorMessage(Object value){

		double rateValue = 0;
		BigDecimal decimalValue= BigDecimal.ZERO;
		boolean validRegex=false;
		String compValue="";
		
		if(value!=null){
			compValue= value.toString();
		}
		
		if(value!=null && StringUtils.isNotBlank(value.toString()) ){
			
			if(BigDecimal.class.isInstance(value)){
				decimalValue=(BigDecimal) value;
				rateValue=decimalValue.doubleValue();
			}else if(Double.class.isInstance(value)){
				rateValue=(Double) value;
				decimalValue = BigDecimal.valueOf(rateValue);
			}else{

				try {
					rateValue = Double.parseDouble(value.toString());
					decimalValue = BigDecimal.valueOf(rateValue);
				} catch (Exception e) {
					logger.error("Exception: ", e);
					return Labels.getLabel("DECIMAL_INVALID", new String[] {fieldParm});
				}
			}
		} else if (mandatory && value == null) {
			return Labels.getLabel("FIELD_IS_MAND", new String[] {fieldParm});
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
				logger.error("Exception: ", e);
				return Labels.getLabel("REGEX_INVALID", new String[] {regExp});
			}
			
			Matcher matcher =  pattern.matcher(compValue);
			validRegex=matcher.matches();

			if(!validRegex){
					return Labels.getLabel(regExp, new String[] {fieldParm});
			}
			
		}
		
		
		//Mandatory Validation
		if (mandatory && rateValue == 0) {
			if(minValid){
				return Labels.getLabel("NUMBER_MINVALUE", new String[] {fieldParm, String.valueOf(minValue)});	
			}else{
				return Labels.getLabel("NUMBER_MINVALUE", new String[] {fieldParm, "0"});	
			}
		}

		// Negative Validation
		if(rateValue < 0 && !negative){
			return Labels.getLabel("NUMBER_NOT_NEGATIVE", new String[] {fieldParm});
		}
		

		String strMaxAmount = PennantApplicationUtil.amountFormate(maxValue, 0);
		String strMinAmount=PennantApplicationUtil.amountFormate(minValue, 0);
		
		
		if (minValid && maxValid) {
			if(decimalValue.compareTo(minValue)==-1 || decimalValue.compareTo(maxValue )==1){
				return Labels.getLabel("NUMBER_RANGE_EQ", new String[] {fieldParm,strMinAmount,strMaxAmount});
			}
			return null;	
		}

		
		if(maxValid){
			if(decimalValue.compareTo(maxValue )==1){
				return Labels.getLabel("NUMBER_MAXVALUE_EQ", new String[] {fieldParm,strMaxAmount});
			}else{
				return null;
			}
		}

		if(minValid){
			if(decimalValue.compareTo(minValue)==-1){
				return Labels.getLabel("NUMBER_MINVALUE_EQ", new String[] {fieldParm,strMinAmount});
			}else{
				return null;
			}
		}
		
		
		if(decimalValue.compareTo(BigDecimal.ZERO)!=0 && decimalValue.scale()>decPos){
			return Labels.getLabel("DECIMAL_INVALID", new String[] {fieldParm,String.valueOf(decPos)});
		}
		
		
		
		return null;
	}



	void setFieldParm(String fieldParm) {
		this.fieldParm = fieldParm;
	}

	void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	void setNegative(boolean negative) {
		this.negative = negative;
	}

	BigDecimal getMaxValue() {
		return maxValue;
	}

	void setMaxValue(double maxValue) {
		if(maxValue!=0){
			maxValid=true;
		}
		this.maxValue = BigDecimal.valueOf(maxValue);
	}


	BigDecimal getMinValue() {
		return minValue;
	}

	void setMinValue(double minValue) {
		if(minValue!=0){
			minValid=true;
		}
		this.minValue = BigDecimal.valueOf(minValue);
	}

	public String getRegExp() {
		return regExp;
	}
	public void setRegExp(String regExp) {
		this.regExp = regExp;
	}
	
	int getDecPos() {
		return decPos;
	}

	void setDecPos(int decPos) {
		this.decPos = decPos;
	}

	public String getFieldParm() {
		return fieldParm;
	}

	boolean isMandatory() {
		return mandatory;
	}

	boolean isNegative() {
		return negative;
	}
}
