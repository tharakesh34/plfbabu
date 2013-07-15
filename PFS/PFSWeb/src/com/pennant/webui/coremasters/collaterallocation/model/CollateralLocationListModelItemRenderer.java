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
 * FileName    		:  CollateralLocationListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-02-2013    														*
 *                                                                  						*
 * Modified Date    :  20-02-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 20-02-2013       Pennant	                 0.1                                            * 
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

package com.pennant.webui.coremasters.collaterallocation.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.coremasters.CollateralLocation;
import com.pennant.backend.util.PennantJavaUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class CollateralLocationListModelItemRenderer implements ListitemRenderer<CollateralLocation>, Serializable {

	private static final long serialVersionUID = 1L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, CollateralLocation collateralLocation, int count) throws Exception {

		//final CollateralLocation collateralLocation = (CollateralLocation) data;
		Listcell lc;
	  	lc = new Listcell(collateralLocation.getHZCLO());
		lc.setParent(item);
	  	lc = new Listcell(collateralLocation.getHZCLC());
		lc.setParent(item);
	  	lc = new Listcell(collateralLocation.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(collateralLocation.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", collateralLocation);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onCollateralLocationItemDoubleClicked");
	}
}