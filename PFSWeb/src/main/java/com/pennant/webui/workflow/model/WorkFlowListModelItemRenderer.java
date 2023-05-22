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
 *
 * FileName : WorkFlowListModelItemRender.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 26-04-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.workflow.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.WorkFlowDetails;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class WorkFlowListModelItemRenderer implements ListitemRenderer<WorkFlowDetails>, Serializable {

	private static final long serialVersionUID = 2925499383404057064L;

	public WorkFlowListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, WorkFlowDetails data, int count) {

		String status = null;
		final WorkFlowDetails workFlowDetails = (WorkFlowDetails) data;

		Listcell lc = new Listcell(workFlowDetails.getWorkFlowType());
		lc.setParent(item);

		lc = new Listcell(workFlowDetails.getWorkFlowSubType());
		lc.setParent(item);

		lc = new Listcell(workFlowDetails.getWorkFlowDesc());
		lc.setParent(item);

		if (workFlowDetails.isWorkFlowActive()) {
			status = "Active";
		} else {
			status = "InActive";
		}
		lc = new Listcell(status);
		lc.setParent(item);

		item.setAttribute("id", data.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onWorkFlowItemDoubleClicked");
	}

}