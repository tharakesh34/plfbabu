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
 * * FileName : AssignmentPartnerListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 12-09-2018 * * Modified Date : 12-09-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-09-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.applicationmaster.assignmentpartner.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.AssignmentPartner;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class AssignmentPartnerListModelItemRenderer implements ListitemRenderer<AssignmentPartner>, Serializable {

	private static final long serialVersionUID = 1L;

	public AssignmentPartnerListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, AssignmentPartner assignmentPartner, int count) {

		Listcell lc;
		lc = new Listcell(assignmentPartner.getCode());
		lc.setParent(item);
		lc = new Listcell(assignmentPartner.getEntityCodeName());
		lc.setParent(item);
		lc = new Listcell(assignmentPartner.getGLCode());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbActive = new Checkbox();
		cbActive.setDisabled(true);
		cbActive.setChecked(assignmentPartner.isActive());
		lc.appendChild(cbActive);
		lc.setParent(item);
		lc = new Listcell(assignmentPartner.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(assignmentPartner.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", assignmentPartner.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onAssignmentPartnerItemDoubleClicked");
	}
}