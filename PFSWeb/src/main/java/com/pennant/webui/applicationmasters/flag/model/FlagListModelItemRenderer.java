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
 * * FileName : FlagListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-07-2015 * *
 * Modified Date : 14-07-2015 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-07-2015 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmasters.flag.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmasters.Flag;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class FlagListModelItemRenderer implements ListitemRenderer<Flag>, Serializable {

	private static final long serialVersionUID = 1L;

	public FlagListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, Flag flag, int count) {

		Listcell lc;
		lc = new Listcell(flag.getFlagCode());
		lc.setParent(item);
		lc = new Listcell(flag.getFlagDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbActive = new Checkbox();
		cbActive.setDisabled(true);
		cbActive.setChecked(flag.isActive());
		lc.appendChild(cbActive);
		lc.setParent(item);
		lc = new Listcell(flag.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(flag.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", flag.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onFlagItemDoubleClicked");
	}
}