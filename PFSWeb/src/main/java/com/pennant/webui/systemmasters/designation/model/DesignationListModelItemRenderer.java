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
 * * FileName : DesignationListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011
 * * * Modified Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.designation.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.Designation;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class DesignationListModelItemRenderer implements ListitemRenderer<Designation>, Serializable {

	private static final long serialVersionUID = -7810505551485114617L;

	public DesignationListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, Designation designation, int count) {

		Listcell lc;
		lc = new Listcell(designation.getDesgCode());
		lc.setParent(item);
		lc = new Listcell(designation.getDesgDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbDesgIsActive = new Checkbox();
		cbDesgIsActive.setDisabled(true);
		cbDesgIsActive.setChecked(designation.isDesgIsActive());
		lc.appendChild(cbDesgIsActive);
		lc.setParent(item);
		lc = new Listcell(designation.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(designation.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", designation.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onDesignationItemDoubleClicked");
	}
}