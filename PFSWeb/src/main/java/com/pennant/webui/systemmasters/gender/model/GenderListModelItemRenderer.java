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
 * * FileName : GenderListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 * *
 * Modified Date : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.gender.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.Gender;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class GenderListModelItemRenderer implements ListitemRenderer<Gender>, Serializable {

	private static final long serialVersionUID = 5688039398131772187L;

	public GenderListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, Gender gender, int count) {

		Listcell lc;
		lc = new Listcell(gender.getGenderCode());
		lc.setParent(item);
		lc = new Listcell(gender.getGenderDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbGenderIsActive = new Checkbox();
		cbGenderIsActive.setDisabled(true);
		cbGenderIsActive.setChecked(gender.isGenderIsActive());
		lc.appendChild(cbGenderIsActive);
		lc.setParent(item);
		lc = new Listcell(gender.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(gender.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", gender.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onGenderItemDoubleClicked");
	}
}