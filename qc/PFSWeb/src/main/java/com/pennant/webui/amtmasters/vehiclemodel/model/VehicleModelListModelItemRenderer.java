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
 * FileName    		:  VehicleModelListModelItemRenderer.java                                                   * 	  
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

package com.pennant.webui.amtmasters.vehiclemodel.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.amtmasters.VehicleModel;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class VehicleModelListModelItemRenderer implements ListitemRenderer<VehicleModel>, Serializable {

	private static final long serialVersionUID = 1L;

	public VehicleModelListModelItemRenderer() {

	}

	@Override
	public void render(Listitem item, VehicleModel vehicleModel, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(vehicleModel.getLovDescVehicleManufacturerName());
		lc.setParent(item);
		lc = new Listcell(vehicleModel.getVehicleModelDesc());
		lc.setParent(item);
		lc = new Listcell(vehicleModel.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(vehicleModel.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", vehicleModel.getId());
		item.setAttribute("vehicleManufacturerId", vehicleModel.getVehicleManufacturerId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onVehicleModelItemDoubleClicked");
	}
}