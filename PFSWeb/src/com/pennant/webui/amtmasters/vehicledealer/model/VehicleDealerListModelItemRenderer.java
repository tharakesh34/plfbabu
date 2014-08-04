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
 * FileName    		:  VehicleDealerListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-09-2011    														*
 *                                                                  						*
 * Modified Date    :  29-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 29-09-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.amtmasters.vehicledealer.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class VehicleDealerListModelItemRenderer implements ListitemRenderer<VehicleDealer>, Serializable {

	private static final long serialVersionUID = 8501192412376141440L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, VehicleDealer vehicleDealer, int count) throws Exception {
		Listcell lc;
		lc = new Listcell(PennantAppUtil.getlabelDesc(vehicleDealer.getDealerType(), PennantStaticListUtil.getDealerType()));
		lc.setParent(item);
	  	lc = new Listcell(vehicleDealer.getDealerName());
		lc.setParent(item);
		lc = new Listcell(vehicleDealer.getDealerTelephone());
		lc.setParent(item);
		lc = new Listcell(vehicleDealer.getDealerFax());
		lc.setParent(item);
	  	lc = new Listcell(vehicleDealer.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(vehicleDealer.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", vehicleDealer);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onVehicleDealerItemDoubleClicked");
	}
}