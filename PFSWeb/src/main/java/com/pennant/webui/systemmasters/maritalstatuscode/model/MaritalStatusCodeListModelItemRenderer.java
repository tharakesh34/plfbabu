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
 * * FileName : MaritalStatusCodeListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 03-05-2011 * * Modified Date : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.maritalstatuscode.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.MaritalStatusCode;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class MaritalStatusCodeListModelItemRenderer implements ListitemRenderer<MaritalStatusCode>, Serializable {

	private static final long serialVersionUID = 7785935117741551251L;

	public MaritalStatusCodeListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, MaritalStatusCode maritalStatusCode, int count) {

		Listcell lc;
		lc = new Listcell(maritalStatusCode.getMaritalStsCode());
		lc.setParent(item);
		lc = new Listcell(maritalStatusCode.getMaritalStsDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbMaritalStsIsActive = new Checkbox();
		cbMaritalStsIsActive.setDisabled(true);
		cbMaritalStsIsActive.setChecked(maritalStatusCode.isMaritalStsIsActive());
		lc.appendChild(cbMaritalStsIsActive);
		lc.setParent(item);
		lc = new Listcell(maritalStatusCode.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(maritalStatusCode.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", maritalStatusCode.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onMaritalStatusCodeItemDoubleClicked");
	}
}