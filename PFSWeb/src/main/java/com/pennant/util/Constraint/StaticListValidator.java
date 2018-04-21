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

package com.pennant.util.Constraint;

import java.util.List;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Constraint;

import com.pennant.backend.model.Property;
import com.pennant.backend.model.ValueLabel;

public class StaticListValidator implements Constraint{

	List<?> valueList;
	private String fieldParm="";

	public StaticListValidator(List<?> valueList, String fieldParm) {
		this.valueList=valueList;	
		setFieldParm(fieldParm);
	}

	public void validate(Component comp, Object value) {

		boolean validate=false;
		
		if (value == null || "".equals(value)) {
			validate =false;
		}else{ 
			for (int i = 0; i < this.valueList.size(); i++) {
				Object object = this.valueList.get(i);

				if (object instanceof ValueLabel) {
					if (value.equals(((ValueLabel) object).getLabel())) {
						validate = true;
						break;
					}
				} else if (object instanceof Property) {
					if (value.equals(((Property) object).getValue())) {
						validate = true;
						break;
					}
				} else {
					throw new IllegalArgumentException("Unsupported List.");
				}	
			} 
		}
	
		if (!validate){
			throw new WrongValueException(comp, Labels.getLabel("STATIC_INVALID",new String[]{fieldParm}));
		}
	}
	
	public String getFieldParm() {
		return fieldParm;
	}

	public void setFieldParm(String fieldParm) {
		this.fieldParm = fieldParm;
	}


}

