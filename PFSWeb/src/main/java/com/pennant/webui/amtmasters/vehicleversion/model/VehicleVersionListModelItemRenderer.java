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
 * FileName    		:  VehicleVersionListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-10-2011    														*
 *                                                                  						*
 * Modified Date    :  08-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.amtmasters.vehicleversion.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.amtmasters.VehicleVersion;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class VehicleVersionListModelItemRenderer implements ListitemRenderer<VehicleVersion>, Serializable {

	private static final long serialVersionUID = 8531638035713000598L;

	public VehicleVersionListModelItemRenderer() {

	}

	@Override
	public void render(Listitem item, VehicleVersion vehicleVersion, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(vehicleVersion.getLovDescVehicleModelDesc());
		lc.setParent(item);
		lc = new Listcell(vehicleVersion.getVehicleVersionCode());
		lc.setParent(item);
		lc = new Listcell(vehicleVersion.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(vehicleVersion.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", vehicleVersion.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onVehicleVersionItemDoubleClicked");
	}
}