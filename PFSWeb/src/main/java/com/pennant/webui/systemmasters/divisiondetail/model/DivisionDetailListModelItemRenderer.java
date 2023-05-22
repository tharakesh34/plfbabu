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
 * * FileName : DivisionDetailListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 02-08-2013 * * Modified Date : 02-08-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 02-08-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.divisiondetail.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.DivisionDetail;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class DivisionDetailListModelItemRenderer implements ListitemRenderer<DivisionDetail>, Serializable {

	private static final long serialVersionUID = 1L;

	public DivisionDetailListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, DivisionDetail divisionDetail, int count) {

		Listcell lc;
		lc = new Listcell(divisionDetail.getDivisionCode());
		lc.setParent(item);
		lc = new Listcell(divisionDetail.getDivisionCodeDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbActive = new Checkbox();
		cbActive.setDisabled(true);
		cbActive.setChecked(divisionDetail.isActive());
		lc.appendChild(cbActive);
		lc.setParent(item);
		lc = new Listcell(divisionDetail.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(divisionDetail.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", divisionDetail.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onDivisionDetailItemDoubleClicked");
	}
}