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
 * FileName    		:  StaticListValidator.java												*                           
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

package com.pennant.webui.util.constraint;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Constraint;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.util.PennantConstants;

public class PTListValidator implements Constraint{

	private List<ValueLabel> valueList;
	private String fieldParm="";
	private boolean mandatory=false;
	
	
	public PTListValidator(String fieldParm, List<ValueLabel> valueList) {
		this.valueList=valueList;	
		setFieldParm(fieldParm);
	}
	
	public PTListValidator(String fieldParm,List<ValueLabel> valueList,boolean mandatory) {
		this.valueList=valueList;	
		setFieldParm(fieldParm);
		setMandatory(mandatory);
	}
	
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
		compValue = StringUtils.trimToNull(compValue);
		
		if (compValue == null || PennantConstants.List_Select.equals(compValue) || Labels.getLabel("Combo.Select").equals(compValue)) {
			if(isMandatory()){
				return Labels.getLabel("FIELD_IS_MAND", new String[] {fieldParm});	
			}else{
				compValue="";
				return null;
			}
		} 

		if(this.valueList==null){
			return "";
		}
		
		for (int i = 0; i < this.valueList.size(); i++) {
			if (compValue.equals(this.valueList.get(i).getLabel()) ){
				return null;
			}	
		} 
		
		
		return Labels.getLabel("STATIC_INVALID", new String[] {fieldParm});
	}
	
	
	String getFieldParm() {
		return fieldParm;
	}

	void setFieldParm(String fieldParm) {
		this.fieldParm = fieldParm;
	}

	List<ValueLabel> getValueList() {
		return valueList;
	}

	void setValueList(List<ValueLabel> valueList) {
		this.valueList = valueList;
	}

	boolean isMandatory() {
		return mandatory;
	}

	void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}



}

