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
 * * FileName : ProfessionListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 *
 * * Modified Date : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.profession.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.Profession;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class ProfessionListModelItemRenderer implements ListitemRenderer<Profession>, Serializable {

	private static final long serialVersionUID = -2463856192651940409L;

	public ProfessionListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, Profession profession, int count) {

		Listcell lc;
		lc = new Listcell(profession.getProfessionCode());
		lc.setParent(item);
		lc = new Listcell(profession.getProfessionDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbProfessionSelfEmployee = new Checkbox();
		cbProfessionSelfEmployee.setDisabled(true);
		cbProfessionSelfEmployee.setChecked(profession.isSelfEmployee());
		lc.appendChild(cbProfessionSelfEmployee);
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbProfessionIsActive = new Checkbox();
		cbProfessionIsActive.setDisabled(true);
		cbProfessionIsActive.setChecked(profession.isProfessionIsActive());
		lc.appendChild(cbProfessionIsActive);
		lc.setParent(item);
		lc = new Listcell(profession.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(profession.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", profession.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onProfessionItemDoubleClicked");
	}
}