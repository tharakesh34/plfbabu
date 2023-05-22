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
 * * FileName : GroupStatusCodeListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 03-05-2011 * * Modified Date : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.groupstatuscode.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.GroupStatusCode;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class GroupStatusCodeListModelItemRenderer implements ListitemRenderer<GroupStatusCode>, Serializable {

	private static final long serialVersionUID = 4629316685450548899L;

	public GroupStatusCodeListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, GroupStatusCode groupStatusCode, int count) {

		Listcell lc;
		lc = new Listcell(groupStatusCode.getGrpStsCode());
		lc.setParent(item);
		lc = new Listcell(groupStatusCode.getGrpStsDescription());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbGrpStsIsActive = new Checkbox();
		cbGrpStsIsActive.setDisabled(true);
		cbGrpStsIsActive.setChecked(groupStatusCode.isGrpStsIsActive());
		lc.appendChild(cbGrpStsIsActive);
		lc.setParent(item);
		lc = new Listcell(groupStatusCode.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(groupStatusCode.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", groupStatusCode.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onGroupStatusCodeItemDoubleClicked");
	}
}