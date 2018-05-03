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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Constraint;

public class PTNumberValidator implements Constraint{
	private static final Logger logger = Logger.getLogger(PTNumberValidator.class);

	private String fieldParm;
	private boolean mandatory=false;
	private boolean negative=false;
	private int maxValue;
	private int minValue;
	private boolean minValid=false;
	private boolean maxValid=false;

	public PTNumberValidator(String fieldParm, boolean mandatory,boolean negative,int maxValue) {
		setFieldParm(fieldParm);
		setMandatory(mandatory);
		setNegative(negative);
		setMaxValue(maxValue);
		setMinValue(0);
	}

	public PTNumberValidator(String fieldParm, boolean mandatory,boolean negative,int minValue,int maxValue) {
		setFieldParm(fieldParm);
		setMandatory(mandatory);
		setNegative(negative);
		setMaxValue(maxValue);
		setMinValue(minValue);
		
	}

	public PTNumberValidator(String fieldParm, boolean mandatory,boolean negative) {
		setFieldParm(fieldParm);
		setMandatory(mandatory);
		setNegative(negative);
		setMaxValue(0);
		setMinValue(0);
		
	}

	public PTNumberValidator(String fieldParm, boolean mandatory) {
		setFieldParm(fieldParm);
		setMandatory(mandatory);
		setMaxValue(0);
		setMinValue(0);
	}

	public PTNumberValidator(String fieldParm) {
		this.fieldParm = fieldParm;
		setMaxValue(0);
		setMinValue(0);
	}

	public void validate(Component comp, Object value) throws WrongValueException {
		
		String errorMessage=getErrorMessage(value);
		if(StringUtils.isNotBlank(errorMessage)){
			throw new WrongValueException(comp, errorMessage);
		}
	}

	
	private String getErrorMessage(Object value){

		int rateValue = 0;
		
		if(value!=null && StringUtils.isNotBlank(value.toString()) ){
			if(Integer.class.isInstance(value)){
				rateValue=(Integer) value;
			}else{

				try {
					rateValue = Integer.parseInt(value.toString());
				} catch (Exception e) {
					logger.error("Exception: ", e);
					return Labels.getLabel("NUMBER_INVALID", new String[] {fieldParm});
				}
			}
		}
		
		//Mandatory Validation with Empty Value
		if (mandatory && value == null) {
			return Labels.getLabel("FIELD_IS_MAND", new String[] {fieldParm});		
		}

		//Mandatory Validation
		if (mandatory && rateValue == 0) {
			if(minValid){
				return Labels.getLabel("NUMBER_MINVALUE_EQ", new String[] {fieldParm, String.valueOf(minValue)});	
			}else{
				return Labels.getLabel("NUMBER_MINVALUE", new String[] {fieldParm, "0"});		
			}
		}

		// Negative Validation
		if(rateValue < 0 && !negative){
			return Labels.getLabel("NUMBER_NOT_NEGATIVE", new String[] {fieldParm});
		}

		
		if (minValid && maxValid) {
			if(rateValue <minValue || rateValue >maxValue ){
				return Labels.getLabel("NUMBER_RANGE_EQ", new String[] {fieldParm,String.valueOf(minValue),String.valueOf(maxValue)});
			}
			return null;	
		}

		
		if(maxValid){
			if(rateValue > maxValue){
				return Labels.getLabel("NUMBER_MAXVALUE_EQ", new String[] {fieldParm,String.valueOf(maxValue)});
			}else{
				return null;
			}
		}

		if(minValid){
			if(rateValue < minValue){
				return Labels.getLabel("NUMBER_MINVALUE_EQ", new String[] {fieldParm,String.valueOf(maxValue)});
			}else{
				return null;
			}
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

	int getMaxValue() {
		return maxValue;
	}

	void setMaxValue(int maxValue) {
		if(maxValue!=0){
			maxValid=true;
		}
		this.maxValue = maxValue;
		
	}

	int getMinValue() {
		return minValue;
	}

	void setMinValue(int minValue) {
		if(minValue!=0){
			minValid=true;
		}
		this.minValue = minValue;
	}

	String getFieldParm() {
		return fieldParm;
	}

	boolean isMandatory() {
		return mandatory;
	}

	boolean isNegative() {
		return negative;
	}
}
