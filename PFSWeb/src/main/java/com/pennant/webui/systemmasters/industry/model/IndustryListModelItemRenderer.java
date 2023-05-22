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
 * * FileName : IndustryListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 * *
 * Modified Date : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.industry.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.Industry;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class IndustryListModelItemRenderer implements ListitemRenderer<Industry>, Serializable {

	private static final long serialVersionUID = -6686096785663103610L;

	public IndustryListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, Industry industry, int count) {

		Listcell lc;
		lc = new Listcell(industry.getIndustryCode());
		lc.setParent(item);
		lc = new Listcell(industry.getIndustryDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbIndustryIsActive = new Checkbox();
		cbIndustryIsActive.setDisabled(true);
		cbIndustryIsActive.setChecked(industry.isIndustryIsActive());
		lc.appendChild(cbIndustryIsActive);
		lc.setParent(item);
		lc = new Listcell(industry.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(industry.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", industry.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onIndustryItemDoubleClicked");
	}
}