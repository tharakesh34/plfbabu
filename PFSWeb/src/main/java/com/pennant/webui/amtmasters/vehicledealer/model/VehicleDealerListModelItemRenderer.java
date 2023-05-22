/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : VehicleDealerListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 29-09-2011 * * Modified Date : 29-09-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 29-09-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.amtmasters.vehicledealer.model;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class VehicleDealerListModelItemRenderer implements ListitemRenderer<VehicleDealer>, Serializable {

	private static final long serialVersionUID = 8501192412376141440L;

	public VehicleDealerListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, VehicleDealer vehicleDealer, int count) {
		Listcell lc;
		lc = new Listcell(PennantApplicationUtil.getLabelDesc(StringUtils.trimToEmpty(vehicleDealer.getDealerType()),
				PennantStaticListUtil.getDealerType()));
		lc.setParent(item);
		lc = new Listcell(vehicleDealer.getDealerName());
		lc.setParent(item);
		lc = new Listcell(vehicleDealer.getEmail());
		lc.setParent(item);
		lc = new Listcell(vehicleDealer.getDealerTelephone());
		lc.setParent(item);
		lc = new Listcell(vehicleDealer.getDealerProvince());
		lc.setParent(item);
		lc = new Listcell(vehicleDealer.getDealerCity());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbActive = new Checkbox();
		cbActive.setDisabled(true);
		cbActive.setChecked(vehicleDealer.isActive());
		lc.appendChild(cbActive);
		lc.setParent(item);
		lc = new Listcell(vehicleDealer.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(vehicleDealer.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", vehicleDealer.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onVehicleDealerItemDoubleClicked");
	}
}