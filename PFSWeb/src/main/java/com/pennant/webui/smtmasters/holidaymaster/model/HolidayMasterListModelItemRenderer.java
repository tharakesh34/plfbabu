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
 * * FileName : HolidayMasterListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 11-07-2011 * * Modified Date : 11-07-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 11-07-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.smtmasters.holidaymaster.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.smtmasters.HolidayMaster;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class HolidayMasterListModelItemRenderer implements ListitemRenderer<HolidayMaster>, Serializable {

	private static final long serialVersionUID = 1L;

	public HolidayMasterListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, HolidayMaster holidayMaster, int count) {

		Listcell lc;
		lc = new Listcell(holidayMaster.getHolidayCode());
		lc.setParent(item);
		lc = new Listcell(String.valueOf(holidayMaster.getHolidayYear()));
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.getLabelDesc(holidayMaster.getHolidayType(),
				PennantStaticListUtil.getHolidayType()));
		lc.setParent(item);
		lc = new Listcell(holidayMaster.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(holidayMaster.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", holidayMaster.getId());
		item.setAttribute("year", holidayMaster.getHolidayYear());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onHolidayMasterItemDoubleClicked");
	}
}