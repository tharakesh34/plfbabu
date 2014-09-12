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
 * FileName    		:  ModuleMapping.java													*                           
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
package com.pennant.backend.model;

import java.io.Serializable;

public class ModuleMapping implements Serializable {
	
	private static final long serialVersionUID = 374707365259033361L;

	private String moduleName;
	private String tabelName;
	private String[] dbObjectNames;
	private String lovDBObjectName;
	@SuppressWarnings("rawtypes")
	private Class moduleClass;
	private Object moduleObject;
	private String[] lovFields;
	private String[][] lovCondition;
	private String workflowType;
	private int lovListWidth=0;
	
	
	@SuppressWarnings("rawtypes")
	public ModuleMapping (Class moduleClass,String tabelName,String[] lovFields,String[][] lovCondition,String workflowType,int lovListWidth){
		setModuleName(moduleClass.getSimpleName());
		setTabelName(tabelName);
		setLovDBObjectName(tabelName);
		setModuleClass(moduleClass);
		setLovFields(lovFields);
		setLovCondition(lovCondition);
		setWorkflowType(workflowType);
		setLovListWidth(lovListWidth);
	}
	
	public ModuleMapping (Object moduleObject,String[] dbObjectNames,String[] lovFields,String[][] lovCondition,String workflowType,int lovListWidth){
		setModuleName(moduleObject.getClass().getSimpleName());
		setDbObjectNames(dbObjectNames);
		if(dbObjectNames!=null){
			setTabelName(dbObjectNames[0]);
			if(dbObjectNames.length>1){
				setLovDBObjectName(dbObjectNames[1]);
			}else{
				setLovDBObjectName(dbObjectNames[0]);
			}	
		}
		setModuleClass(moduleObject.getClass());
		setModuleObject(moduleObject);
		setLovFields(lovFields);
		setLovCondition(lovCondition);
		setWorkflowType(workflowType);
		setLovListWidth(lovListWidth);
	}

	@SuppressWarnings("rawtypes")
	public ModuleMapping (String moduleName,Class moduleClass,String tabelName,String[] lovFields,String[][] lovCondition,String workflowType,int lovListWidth){
		setModuleName(moduleName);
		setTabelName(tabelName);
		setLovDBObjectName(tabelName);
		setModuleClass(moduleClass);
		setLovFields(lovFields);
		setLovCondition(lovCondition);
		setWorkflowType(workflowType);
		setLovListWidth(lovListWidth);
	}

	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public String getTabelName() {
		return tabelName;
	}
	public void setTabelName(String tabelName) {
		this.tabelName = tabelName;
	}
	@SuppressWarnings("rawtypes")
	public Class getModuleClass() {
		return moduleClass;
	}
	@SuppressWarnings("rawtypes")
	public void setModuleClass(Class moduleClass) {
		this.moduleClass = moduleClass;
	}
	public void setModuleObject(Object moduleObject) {
		this.moduleObject = moduleObject;
	}

	public Object getModuleObject() {
		return moduleObject;
	}

	public String[] getLovFields() {
		return lovFields;
	}
	public void setLovFields(String[] lovFields) {
		this.lovFields = lovFields;
	}
	public String[][] getLovCondition() {
		return lovCondition;
	}
	public void setLovCondition(String[][] lovCondition) {
		this.lovCondition = lovCondition;
	}

	public String getWorkflowType() {
		return workflowType;
	}
	public void setWorkflowType(String workflowType) {
		this.workflowType = workflowType;
	}

	public int getLovListWidth() {
		return lovListWidth;
	}

	public void setLovListWidth(int lovListWidth) {
		this.lovListWidth = lovListWidth;
	}

	public String[] getDbObjectNames() {
		return dbObjectNames;
	}

	public void setDbObjectNames(String[] dbObjectNames) {
		this.dbObjectNames = dbObjectNames;
	}

	public String getLovDBObjectName() {
		return lovDBObjectName;
	}

	private void setLovDBObjectName(String lovDBObjectName) {
		this.lovDBObjectName = lovDBObjectName;
	}
	
}
