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
 * * FileName : ScheduleMethodListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 12-09-2011 * * Modified Date : 12-09-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-09-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.staticparms.schedulemethod.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.staticparms.ScheduleMethod;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class ScheduleMethodListModelItemRenderer implements ListitemRenderer<ScheduleMethod>, Serializable {

	private static final long serialVersionUID = -1371764366423030379L;

	public ScheduleMethodListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, ScheduleMethod scheduleMethod, int count) {

		Listcell lc;
		lc = new Listcell(scheduleMethod.getSchdMethod());
		lc.setParent(item);
		lc = new Listcell(scheduleMethod.getSchdMethodDesc());
		lc.setParent(item);
		lc = new Listcell(scheduleMethod.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(scheduleMethod.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", scheduleMethod.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onScheduleMethodItemDoubleClicked");
	}
}