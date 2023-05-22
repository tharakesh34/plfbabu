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
 * * FileName : AssignmentListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-09-2018 *
 * * Modified Date : 12-09-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-09-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.systemmasters.assignment.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.Assignment;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class AssignmentListModelItemRenderer implements ListitemRenderer<Assignment>, Serializable {

	private static final long serialVersionUID = 1L;

	public AssignmentListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, Assignment assignment, int count) {

		Listcell lc;
		lc = new Listcell(String.format("%08d", assignment.getId()));
		lc.setParent(item);
		lc = new Listcell(assignment.getDealCode());
		lc.setParent(item);
		lc = new Listcell(assignment.getLoanType());
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(assignment.getDisbDate()));
		lc.setParent(item);
		lc = new Listcell(assignment.getOpexFeeType());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbActive = new Checkbox();
		cbActive.setDisabled(true);
		cbActive.setChecked(assignment.isActive());
		lc.appendChild(cbActive);
		lc.setParent(item);
		lc = new Listcell(assignment.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(assignment.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", assignment.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onAssignmentItemDoubleClicked");
	}
}