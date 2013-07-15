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

public class ModuleStaticListcode implements Serializable{

	private static final long serialVersionUID = -2824453371288579564L;
	
	private String listCode;
	private String moduleListName;
	private String[] fieldHeading;
	private String[] valueLabel; 
	
	public ModuleStaticListcode(String listCode,String moduleListName,String[] fieldHeading,String[] valueLabel){
		this.listCode = listCode;
		this.moduleListName =moduleListName;
		this.fieldHeading=fieldHeading;
		this.valueLabel=valueLabel;
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
	public String[] getFieldHeading() {
		return fieldHeading;
	}
	public void setFieldHeading(String[] fieldHeading) {
		this.fieldHeading = fieldHeading;
	}
	public void setValueLabel(String[] valueLabel) {
		this.valueLabel = valueLabel;
	}
	public String[] getValueLabel() {
		return valueLabel;
	}

}
