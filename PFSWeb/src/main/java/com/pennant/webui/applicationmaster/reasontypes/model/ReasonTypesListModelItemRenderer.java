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
 * * FileName : ReasonTypesListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 19-12-2017
 * * * Modified Date : 19-12-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 19-12-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.applicationmaster.reasontypes.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.ReasonTypes;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class ReasonTypesListModelItemRenderer implements ListitemRenderer<ReasonTypes>, Serializable {

	private static final long serialVersionUID = 1L;

	public ReasonTypesListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, ReasonTypes reasonTypes, int count) {

		Listcell lc;
		lc = new Listcell(reasonTypes.getCode());
		lc.setParent(item);
		lc = new Listcell(reasonTypes.getDescription());
		lc.setParent(item);
		lc = new Listcell(reasonTypes.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(reasonTypes.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", reasonTypes.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onReasonTypesItemDoubleClicked");
	}
}