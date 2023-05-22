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
 * * FileName : CasteListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 * *
 * Modified Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.caste.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.Caste;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class CasteListModelItemRenderer implements ListitemRenderer<Caste>, Serializable {

	private static final long serialVersionUID = 6352065299727172054L;

	public CasteListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, Caste caste, int count) {

		Listcell lc;
		// caste code
		lc = new Listcell(caste.getCasteCode());
		lc.setParent(item);
		// caste description
		lc = new Listcell(caste.getCasteDesc());
		lc.setParent(item);
		// active
		lc = new Listcell();
		final Checkbox cbCasteIsActive = new Checkbox();
		cbCasteIsActive.setDisabled(true);
		cbCasteIsActive.setChecked(caste.isCasteIsActive());
		lc.appendChild(cbCasteIsActive);
		lc.setParent(item);
		// record status
		lc = new Listcell(caste.getRecordStatus());
		lc.setParent(item);
		// record type
		lc = new Listcell(PennantJavaUtil.getLabel(caste.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", caste.getId());
		item.setAttribute("casteCode", caste.getCasteCode());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onCasteItemDoubleClicked");
	}
}