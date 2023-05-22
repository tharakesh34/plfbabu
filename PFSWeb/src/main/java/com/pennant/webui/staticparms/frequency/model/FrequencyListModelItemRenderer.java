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
 * * FileName : FrequencyListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 *
 * * Modified Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.staticparms.frequency.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.staticparms.Frequency;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class FrequencyListModelItemRenderer implements ListitemRenderer<Frequency>, Serializable {

	private static final long serialVersionUID = 194466328972174678L;

	public FrequencyListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, Frequency frequency, int count) {

		Listcell lc;
		lc = new Listcell(frequency.getFrqCode());
		lc.setParent(item);
		lc = new Listcell(frequency.getFrqDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbFrqIsActive = new Checkbox();
		cbFrqIsActive.setDisabled(true);
		cbFrqIsActive.setChecked(frequency.isFrqIsActive());
		lc.appendChild(cbFrqIsActive);
		lc.setParent(item);
		lc = new Listcell(frequency.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(frequency.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", frequency.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onFrequencyItemDoubleClicked");
	}
}