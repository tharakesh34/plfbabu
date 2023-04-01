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
 * * FileName : SplRateListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * *
 * Modified Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.splrate.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.SplRate;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class SplRateListModelItemRenderer implements ListitemRenderer<SplRate>, Serializable {

	private static final long serialVersionUID = 8827094920109804515L;

	public SplRateListModelItemRenderer() {

	}

	@Override
	public void render(Listitem item, SplRate splRate, int count) {

		Listcell lc;
		lc = new Listcell(splRate.getSRType() + "-" + splRate.getLovDescSRTypeName());
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(splRate.getSREffDate()));
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.formatRate(splRate.getSRRate().doubleValue(), 9));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell(splRate.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(splRate.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", splRate.getId());
		item.setAttribute("sREffDate", splRate.getSREffDate());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onSplRateItemDoubleClicked");
	}
}