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
 * FileName    		:  DispatchModeListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  18-08-2011    														*
 *                                                                  						*
 * Modified Date    :  18-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-08-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.systemmasters.dispatchmode.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.DispatchMode;
import com.pennant.backend.util.PennantJavaUtil;


/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class DispatchModeListModelItemRenderer implements ListitemRenderer<DispatchMode>, Serializable {

	private static final long serialVersionUID = -3813890359709017692L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, DispatchMode dispatchMode, int count) throws Exception {

		//final DispatchMode dispatchMode = (DispatchMode) data;
		Listcell lc;
		lc = new Listcell(dispatchMode.getDispatchModeCode());
		lc.setParent(item);
		lc = new Listcell(dispatchMode.getDispatchModeDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbDispatchModeIsActive = new Checkbox();
		cbDispatchModeIsActive.setDisabled(true);
		cbDispatchModeIsActive.setChecked(dispatchMode.isDispatchModeIsActive());
		lc.appendChild(cbDispatchModeIsActive);
		lc.setParent(item);
		lc = new Listcell(dispatchMode.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(dispatchMode.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", dispatchMode);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onDispatchModeItemDoubleClicked");
	}
}