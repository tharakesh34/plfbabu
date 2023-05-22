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
 * * FileName : SubSectorListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 *
 * * Modified Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.subsector.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.SubSector;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class SubSectorListModelItemRenderer implements ListitemRenderer<SubSector>, Serializable {

	private static final long serialVersionUID = -7933062641604193890L;

	public SubSectorListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, SubSector subSector, int count) {

		Listcell lc;
		lc = new Listcell(subSector.getSectorCode());
		lc.setParent(item);
		lc = new Listcell(subSector.getSubSectorCode());
		lc.setParent(item);
		lc = new Listcell(subSector.getSubSectorDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbSubSectorIsActive = new Checkbox();
		cbSubSectorIsActive.setDisabled(true);
		cbSubSectorIsActive.setChecked(subSector.isSubSectorIsActive());
		lc.appendChild(cbSubSectorIsActive);
		lc.setParent(item);
		lc = new Listcell(subSector.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(subSector.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", subSector.getId());
		item.setAttribute("subSectorCode", subSector.getSubSectorCode());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onSubSectorItemDoubleClicked");
	}
}