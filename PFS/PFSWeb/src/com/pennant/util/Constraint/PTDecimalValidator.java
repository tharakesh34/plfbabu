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

import org.apache.commons.lang.StringUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Constraint;

import com.pennant.backend.util.PennantApplicationUtil;

public class PTDecimalValidator implements Constraint{

	private String fieldParm;
	private boolean mandatory=false;
	private boolean negative=false;
	private BigDecimal maxValue;
	private BigDecimal minValue;
	private int decPos ;
	private boolean minValid=false;
	private boolean maxValid=false;

	
	public PTDecimalValidator(String fieldParm, int decPos, boolean mandatory,boolean negative,double maxValue) {
		setFieldParm(fieldParm);
		setMandatory(mandatory);
		setNegative(negative);
		setMaxValue(maxValue);
		setMinValue(0);
		setDecPos(decPos);
		
	}

	public PTDecimalValidator(String fieldParm, int decPos, boolean mandatory,boolean negative,double minValue,double maxValue) {
		setFieldParm(fieldParm);
		setMandatory(mandatory);
		setNegative(negative);
		setMaxValue(maxValue);
		setMinValue(minValue);
		
	}

	public PTDecimalValidator(String fieldParm, int decPos, boolean mandatory,boolean negative) {
		setFieldParm(fieldParm);
		setMandatory(mandatory);
		setNegative(negative);
		setMaxValue(0);
		setMinValue(0);
		
	}

	public PTDecimalValidator(String fieldParm, int decPos,boolean mandatory) {
		setFieldParm(fieldParm);
		setMandatory(mandatory);
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
		if(!StringUtils.trimToEmpty(errorMessage).equals("")){
			throw new WrongValueException(comp, errorMessage);
		}
	}

	
	private String getErrorMessage(Object value){

		double rateValue = 0;
		BigDecimal decimalValue= new BigDecimal(0);
		
		if(value!=null && !StringUtils.trim(value.toString()).equals("") ){
			
			if(BigDecimal.class.isInstance(value)){
				decimalValue=(BigDecimal) value;
				rateValue=decimalValue.doubleValue();
			}else if(Double.class.isInstance(value)){
				rateValue=(Integer) value;
				decimalValue= new BigDecimal(rateValue);
			}else{

				try {
					rateValue = Double.parseDouble(value.toString());
					decimalValue= new BigDecimal(rateValue);
				} catch (Exception e) {
					return Labels.getLabel("DECIMAL_INVALID", new String[] {fieldParm});
				}
			}
		}
		
		
		//Mandatory Validation
		if(mandatory & rateValue==0){
			return Labels.getLabel("FIELD_IS_MAND", new String[] {fieldParm});		
		}

		// Negative Validation
		if(rateValue < 0 && !negative){
			return Labels.getLabel("NUMBER_NOT_NEGATIVE", new String[] {fieldParm});
		}
		

		String strMaxAmount = PennantApplicationUtil.amountFormate(maxValue, decPos);
		String strMinAmount=PennantApplicationUtil.amountFormate(minValue, decPos);
		
		
		if(minValid & maxValid){
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
		
		
		if(decimalValue.compareTo(BigDecimal.ZERO)!=0 && decimalValue.scale()!=decPos){
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
		this.maxValue = new BigDecimal(maxValue);
	}


	BigDecimal getMinValue() {
		return minValue;
	}

	void setMinValue(double minValue) {
		if(minValue!=0){
			minValid=true;
		}
		this.minValue = new BigDecimal(minValue);
	}


	
	int getDecPos() {
		return decPos;
	}

	void setDecPos(int decPos) {
		this.decPos = decPos;
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
