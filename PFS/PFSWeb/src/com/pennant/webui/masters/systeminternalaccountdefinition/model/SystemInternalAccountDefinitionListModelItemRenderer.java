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
 * FileName    		:  SystemInternalAccountDefinitionListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-12-2011    														*
 *                                                                  						*
 * Modified Date    :  17-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.masters.systeminternalaccountdefinition.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.masters.SystemInternalAccountDefinition;
import com.pennant.backend.util.PennantJavaUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class SystemInternalAccountDefinitionListModelItemRenderer implements ListitemRenderer, Serializable {

	private static final long serialVersionUID = 1L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, Object data, int count) throws Exception {

		final SystemInternalAccountDefinition systemInternalAccountDefinition = (SystemInternalAccountDefinition) data;
		Listcell lc;
	  	lc = new Listcell(systemInternalAccountDefinition.getSIACode());
		lc.setParent(item);
	  	lc = new Listcell(systemInternalAccountDefinition.getSIAName());
		lc.setParent(item);
	  	lc = new Listcell(systemInternalAccountDefinition.getSIAShortName());
		lc.setParent(item);
	  	lc = new Listcell(systemInternalAccountDefinition.getSIAAcType());
		lc.setParent(item);
	  	lc = new Listcell();
	  	String sIANumber = systemInternalAccountDefinition.getSIANumber();
	  	if(SystemParameterDetails.getSystemParameterValue("CBI_AVAIL").equals("Y")){
	  		sIANumber = sIANumber.substring(2);
	  	}
	  	lc.setLabel(sIANumber);
		lc.setParent(item);
	  	lc = new Listcell(systemInternalAccountDefinition.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(systemInternalAccountDefinition.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", data);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onSystemInternalAccountDefinitionItemDoubleClicked");
	}
}