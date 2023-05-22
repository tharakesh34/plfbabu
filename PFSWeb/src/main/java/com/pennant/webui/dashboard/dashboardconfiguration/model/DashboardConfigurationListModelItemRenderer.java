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
 * * FileName : DashboardDetailListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 14-06-2011 * * Modified Date : 14-06-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-06-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.dashboard.dashboardconfiguration.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class DashboardConfigurationListModelItemRenderer
		implements ListitemRenderer<DashboardConfiguration>, Serializable {

	private static final long serialVersionUID = -2685804825608978299L;

	public DashboardConfigurationListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, DashboardConfiguration dashboardConfig, int count) {

		Listcell lc;
		lc = new Listcell(dashboardConfig.getDashboardCode());
		lc.setParent(item);
		lc = new Listcell(dashboardConfig.getDashboardDesc());
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.getLabelDesc(dashboardConfig.getDashboardType(),
				PennantStaticListUtil.getDashBoardType()));
		lc.setParent(item);
		lc = new Listcell(dashboardConfig.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(dashboardConfig.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", dashboardConfig.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onDashboardDetailItemDoubleClicked");
	}
}