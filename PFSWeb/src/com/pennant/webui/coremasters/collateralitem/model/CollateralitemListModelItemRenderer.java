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
 * FileName    		:  CollateralitemListModelItemRenderer.java                                                   * 	  
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

package com.pennant.webui.coremasters.collateralitem.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.coremasters.Collateralitem;
import com.pennant.backend.util.PennantJavaUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class CollateralitemListModelItemRenderer implements ListitemRenderer<Collateralitem>, Serializable {

	private static final long serialVersionUID = 1L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, Collateralitem collateralitem, int count) throws Exception {

		//final Collateralitem collateralitem = (Collateralitem) data;
		Listcell lc;
	  	lc = new Listcell(collateralitem.getHYCUS());
		lc.setParent(item);
	  	lc = new Listcell(collateralitem.getHYCLC());
		lc.setParent(item);
	  	lc = new Listcell(collateralitem.getHYDLP());
		lc.setParent(item);
	  	lc = new Listcell(collateralitem.getHYAB());
		lc.setParent(item);
	  	lc = new Listcell(collateralitem.getHYAS());
		lc.setParent(item);
	  	lc = new Listcell(collateralitem.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(collateralitem.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", collateralitem);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onCollateralitemItemDoubleClicked");
	}
}