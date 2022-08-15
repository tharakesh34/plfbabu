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
 * * FileName : WeekendMasterListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 11-07-2011 * * Modified Date : 11-07-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 11-07-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.smtmasters.weekendmaster.model;

import java.io.Serializable;
import java.util.StringTokenizer;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.smtmasters.WeekendMaster;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class WeekendMasterListModelItemRenderer implements ListitemRenderer<WeekendMaster>, Serializable {

	private static final long serialVersionUID = -7702036064069683433L;

	public WeekendMasterListModelItemRenderer() {

	}

	@Override
	public void render(Listitem item, WeekendMaster weekendMaster, int count) {

		Listcell lc;
		lc = new Listcell(weekendMaster.getWeekendCode());
		lc.setParent(item);
		lc = new Listcell(weekendMaster.getWeekendDesc());
		lc.setParent(item);
		StringTokenizer st = new StringTokenizer(weekendMaster.getWeekend(), ",");
		String str = "";
		while (st.hasMoreTokens()) {
			str = str + PennantStaticListUtil.getWeekName().get(Integer.parseInt(st.nextToken()) - 1).getLabel() + ",";
		}
		lc = new Listcell(str.substring(0, str.length() - 1));
		lc.setParent(item);
		lc = new Listcell(weekendMaster.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(weekendMaster.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", weekendMaster.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onWeekendMasterItemDoubleClicked");
	}
}