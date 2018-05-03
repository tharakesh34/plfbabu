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
 * FileName    		:  InventorySettlementListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  24-06-2016    														*
 *                                                                  						*
 * Modified Date    :  24-06-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-06-2016       Pennant	                 0.1                                            * 
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

package com.pennant.webui.inventorysettlement.inventorysettlement.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.inventorysettlement.InventorySettlement;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class InventorySettlementListModelItemRenderer implements ListitemRenderer<InventorySettlement>, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public void render(Listitem item, InventorySettlement inventorySettlement, int count) throws Exception {

		Listcell lc;
	  	lc = new Listcell(String.valueOf(inventorySettlement.getId()));
		lc.setParent(item);
	  	lc = new Listcell(inventorySettlement.getBrokerCode());
		lc.setParent(item);
	  	lc = new Listcell(DateUtility.formatDate(inventorySettlement.getSettlementDate(), PennantConstants.dateFormat));
	  	lc.setParent(item);
	  	lc = new Listcell(inventorySettlement.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(inventorySettlement.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", inventorySettlement.getId());
		ComponentsCtrl.applyForward(item, "onDoubleClick=onInventorySettlementItemDoubleClicked");
	}
}