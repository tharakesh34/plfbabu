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
 * * FileName : BlackListReasonCodeListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 05-05-2011 * * Modified Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.blacklistreasoncode.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.BlackListReasonCode;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class BlackListReasonCodeListModelItemRenderer implements ListitemRenderer<BlackListReasonCode>, Serializable {

	private static final long serialVersionUID = 3825281702936630616L;

	public BlackListReasonCodeListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, BlackListReasonCode blackListReasonCode, int count) {

		Listcell lc;
		lc = new Listcell(blackListReasonCode.getBLRsnCode());
		lc.setParent(item);
		lc = new Listcell(blackListReasonCode.getBLRsnDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbBLIsActive = new Checkbox();
		cbBLIsActive.setDisabled(true);
		cbBLIsActive.setChecked(blackListReasonCode.isBLIsActive());
		lc.appendChild(cbBLIsActive);
		lc.setParent(item);
		lc = new Listcell(blackListReasonCode.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(blackListReasonCode.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", blackListReasonCode.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onBlackListReasonCodeItemDoubleClicked");
	}
}