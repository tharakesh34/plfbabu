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
 * * FileName : SalutationListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 *
 * * Modified Date : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.salutation.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.Salutation;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class SalutationListModelItemRenderer implements ListitemRenderer<Salutation>, Serializable {

	private static final long serialVersionUID = -8249375246622770860L;

	public SalutationListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, Salutation salutation, int count) {

		Listcell lc;
		lc = new Listcell(salutation.getSalutationCode());
		lc.setParent(item);
		lc = new Listcell(salutation.getSaluationDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbSalutationIsActive = new Checkbox();
		cbSalutationIsActive.setDisabled(true);
		cbSalutationIsActive.setChecked(salutation.isSalutationIsActive());
		lc.appendChild(cbSalutationIsActive);
		lc.setParent(item);
		lc = new Listcell(salutation.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(salutation.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", salutation);

		ComponentsCtrl.applyForward(item, "onDoubleClick=onSalutationItemDoubleClicked");
	}
}