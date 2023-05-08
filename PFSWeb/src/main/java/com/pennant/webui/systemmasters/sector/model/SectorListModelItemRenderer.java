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
 * * FileName : SectorListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 * *
 * Modified Date : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.sector.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.systemmasters.Sector;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class SectorListModelItemRenderer implements ListitemRenderer<Sector>, Serializable {

	private static final long serialVersionUID = 2238613076188385979L;

	public SectorListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, Sector sector, int count) {

		Listcell lc;
		lc = new Listcell(sector.getSectorCode());
		lc.setParent(item);
		lc = new Listcell(sector.getSectorDesc());
		lc.setParent(item);
		lc = new Listcell(CurrencyUtil.format(sector.getSectorLimit(), 0));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbSectorIsActive = new Checkbox();
		cbSectorIsActive.setDisabled(true);
		cbSectorIsActive.setChecked(sector.isSectorIsActive());
		lc.appendChild(cbSectorIsActive);
		lc.setParent(item);
		lc = new Listcell(sector.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(sector.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", sector.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onSectorItemDoubleClicked");
	}
}