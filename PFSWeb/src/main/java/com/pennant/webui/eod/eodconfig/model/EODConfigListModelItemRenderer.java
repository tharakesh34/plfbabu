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
 * * FileName : EODConfigListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 24-05-2017 *
 * * Modified Date : 24-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 24-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.eod.eodconfig.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.eod.EODConfig;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class EODConfigListModelItemRenderer implements ListitemRenderer<EODConfig>, Serializable {

	private static final long serialVersionUID = 1L;

	public EODConfigListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, EODConfig eODConfig, int count) {

		Listcell lc;
		lc = new Listcell();
		final Checkbox cbExtMnthRequired = new Checkbox();
		cbExtMnthRequired.setDisabled(true);
		cbExtMnthRequired.setChecked(eODConfig.isExtMnthRequired());
		lc.appendChild(cbExtMnthRequired);
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(eODConfig.getMnthExtTo()));
		lc.setParent(item);
		lc = new Listcell(eODConfig.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(eODConfig.getRecordType()));
		lc.setParent(item);
		item.setAttribute("eodConfigId", eODConfig.getEodConfigId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onEODConfigItemDoubleClicked");
	}
}