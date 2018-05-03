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
 *																							*
 * FileName    		:  ModuleSearchBox.java		                                            * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-05-2013    														*
 *                                                                  						*
 * Modified Date    :  23-05-2013    														*
 *                                                                  						*
 * Description 		:  Module Search box                                            		*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-11-2011       Satish/Chaitanya	      0.1       		                            * 
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

package com.pennant.component;

import org.zkforge.ckez.CKeditor;

public class PTCKeditor extends CKeditor {
	private static final long serialVersionUID = -4246285143621221275L;
	
	public static final String ckEditorReadonlyUrl = "/ptjs/ckEditorReadonlyConfig.js";
	public static final String ckEditorUrl = "/ptjs/ckEditorConfig.js";
	public static final String SIMPLE_LIST = "/ptjs/ckEditorConfigSimpleList.js";
	
	/**
	 * PTCKeditor
	 * Constructor
	 * Defining the components and events
	 */
	public PTCKeditor() {
		super();
		this.setCustomConfigurationsPath(ckEditorUrl);
	}
	
	public void setReadonly(boolean readonly){
		if(readonly){
			this.setCustomConfigurationsPath(ckEditorReadonlyUrl);
		}else{
			this.setCustomConfigurationsPath(ckEditorUrl);
		}
		//this.setCustomConfigurationsPath(ckEditorUrl); --TODO : Commented for setting Readonly Condition
	}
	
	public boolean isReadonly(){
		if(this.getCustomConfigurationsPath().equals(ckEditorReadonlyUrl)){
			return true;
		}
		return false;
	}
	
	
}
