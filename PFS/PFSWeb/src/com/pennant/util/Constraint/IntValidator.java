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

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Constraint;

public class IntValidator implements Constraint{

	private int len;
	private String fieldParm="";
	private boolean negative=false;
	
	public IntValidator(int len) {
		this.setLen(len);
	}
	
	public IntValidator(int len,String fieldParm) {
		this.setLen(len);
		this.setFieldParm(fieldParm);
	}
	
	public IntValidator(int len,String fieldParm,boolean negative) {
		this.setLen(len);
		this.setFieldParm(fieldParm);
		this.setNegative(negative);
	}
	
	public void validate(Component comp, Object value) throws WrongValueException {
		
		boolean validate=false;
		int rateValue = 0;
		double maxValue = Math.pow(10, (len));
		
		if (value!=null){
			rateValue= Integer.parseInt(value.toString());
			validate=true;
		}
		
		if (rateValue==0){
			throw new WrongValueException(comp, Labels.getLabel("FIELD_NO_ZERO",new String[]{fieldParm}));
		}
		
		if (rateValue<=(maxValue*-1) || rateValue>=maxValue){
			throw new WrongValueException(comp, Labels.getLabel("FIELD_RANGE",new String[] {fieldParm,String.valueOf(maxValue*-1),String.valueOf(maxValue)}));
		}
		
		if (!isNegative() && rateValue < 0){
			throw new WrongValueException(comp, Labels.getLabel("FIELD_NO_NEGATIVE",new String[]{fieldParm}));
		}
		
		if (!validate){
			throw new WrongValueException(comp, Labels.getLabel("FIELD_NO_NUMBER",new String[]{fieldParm}));
		}		
	}

	public void setLen(int len) {
		this.len = len;
	}

	public int getLen() {
		return len;
	}

	public String getFieldParm() {
		return fieldParm;
	}

	public void setFieldParm(String fieldParm) {
		this.fieldParm = fieldParm;
	}
	
	public boolean isNegative() {
		return negative;
	}

	public void setNegative(boolean negative) {
		this.negative = negative;
	}
	
}
