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
 * * FileName : SplRateCodeListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011
 * * * Modified Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.splratecode.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.SplRateCode;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class SplRateCodeListModelItemRenderer implements ListitemRenderer<SplRateCode>, Serializable {

	private static final long serialVersionUID = -1623457201283911487L;

	public SplRateCodeListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, SplRateCode splRateCode, int count) {

		Listcell lc;
		lc = new Listcell(splRateCode.getSRType());
		lc.setParent(item);
		lc = new Listcell(splRateCode.getSRTypeDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbSRIsActive = new Checkbox();
		cbSRIsActive.setDisabled(true);
		cbSRIsActive.setChecked(splRateCode.isSRIsActive());
		lc.appendChild(cbSRIsActive);
		lc.setParent(item);
		lc = new Listcell(splRateCode.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(splRateCode.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", splRateCode.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onSplRateCodeItemDoubleClicked");
	}
}