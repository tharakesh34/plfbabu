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
 * FileName    		:  ModuleListcode.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  19-07-2011															*
 *                                                                  
 * Modified Date    :  19-07-2011															*
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
package com.pennant.backend.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ModuleListcode implements Serializable{
	
	private static final long serialVersionUID = -7808745343602549220L;
	
	private String listCode;
	private String moduleListName;
	private String[] fieldHeading;
	private ArrayList<ValueLabel> valueLabels;
	
	public ModuleListcode(String listCode,String moduleListName,ArrayList<ValueLabel> valueLabels,String[] fieldHeading){
		this.listCode = listCode;
		this.moduleListName =moduleListName;
		this.fieldHeading=fieldHeading;
		this.valueLabels=valueLabels;
	}
	public String getListCode() {
		return listCode;
	}
	public void setListCode(String listCode) {
		this.listCode = listCode;
	}
	public String getModuleListName() {
		return moduleListName;
	}
	public void setModuleListName(String moduleListName) {
		this.moduleListName = moduleListName;
	}
	public ArrayList<ValueLabel> getValueLabels() {
		return valueLabels;
	}
	public void setValueLabels(ArrayList<ValueLabel> valueLabels) {
		this.valueLabels = valueLabels;
	}


	public String[] getFieldHeading() {
		return fieldHeading;
	}


	public void setFieldHeading(String[] fieldHeading) {
		this.fieldHeading = fieldHeading;
	}
	
	
}
