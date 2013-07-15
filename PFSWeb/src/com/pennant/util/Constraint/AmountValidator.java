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
 * FileName    		:  AmountValidator.java													*                           
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

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Constraint;

import com.pennant.util.PennantAppUtil;

public class AmountValidator implements Constraint{

	private int len;
	private int decPos;
	private String fieldParm="Amount";
	private boolean negative=false; 
	
	public AmountValidator(int len,int decPos) {
		this.setLen(len);
		this.setDecPos(decPos);
		this.setNegative(true);
	}
	
	public AmountValidator(int len,int decPos,String fieldParm,boolean negative) {
		this.setLen(len);
		this.setDecPos(decPos);
		this.setFieldParm(fieldParm);
		this.setNegative(negative);
		
	}
	
	public AmountValidator(int len,int decPos,String fieldParm) {
		this.setLen(len);
		this.setDecPos(decPos);
		this.setFieldParm(fieldParm);
		this.setNegative(true);
	}
	
	public void validate(Component comp, Object value) throws WrongValueException {
		
		boolean validate=false;
		double rateValue = 0;
		double maxRate = Math.pow(10, (len-decPos));
		
		if (value!=null){
			rateValue= new BigDecimal(value.toString()).doubleValue();
			validate=true;
		}
		
		if (rateValue==0){
			throw new WrongValueException(comp, Labels.getLabel("AMOUNT_NO_ZERO",new String[]{fieldParm}));
		}
		
		if (rateValue<=(maxRate*-1) || rateValue>=maxRate){
			String fmtAmount=PennantAppUtil.amountFormate(new BigDecimal(maxRate), decPos);
			throw new WrongValueException(comp, Labels.getLabel("AMOUNT_RANGE",new String[] {fieldParm,String.valueOf(fmtAmount),String.valueOf(fmtAmount)}));
		}

		if (!isNegative() && rateValue < 0){
			throw new WrongValueException(comp, Labels.getLabel("AMOUNT_NOT_NEGATIVE",new String[]{fieldParm}));
		}
		
		if (!validate){
			throw new WrongValueException(comp, Labels.getLabel("AMOUNT_NO_NUMBER",new String[]{fieldParm}));
		}		
	}

	public void setLen(int len) {
		this.len = len;
	}

	public int getLen() {
		return len;
	}

	public void setDecPos(int decPos) {
		this.decPos = decPos;
	}

	public int getDecPos() {
		return decPos;
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
