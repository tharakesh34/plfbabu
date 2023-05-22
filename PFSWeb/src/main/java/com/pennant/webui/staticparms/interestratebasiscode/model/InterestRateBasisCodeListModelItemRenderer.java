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
 * * FileName : InterestRateBasisCodeListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 06-05-2011 * * Modified Date : 06-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 06-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.staticparms.interestratebasiscode.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.staticparms.InterestRateBasisCode;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class InterestRateBasisCodeListModelItemRenderer
		implements ListitemRenderer<InterestRateBasisCode>, Serializable {

	private static final long serialVersionUID = 5364048272277594011L;

	public InterestRateBasisCodeListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, InterestRateBasisCode interestRateBasisCode, int count) {

		Listcell lc;
		lc = new Listcell(interestRateBasisCode.getIntRateBasisCode());
		lc.setParent(item);
		lc = new Listcell(interestRateBasisCode.getIntRateBasisDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbIntRateBasisIsActive = new Checkbox();
		cbIntRateBasisIsActive.setDisabled(true);
		cbIntRateBasisIsActive.setChecked(interestRateBasisCode.isIntRateBasisIsActive());
		lc.appendChild(cbIntRateBasisIsActive);
		lc.setParent(item);
		lc = new Listcell(interestRateBasisCode.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(interestRateBasisCode.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", interestRateBasisCode.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onInterestRateBasisCodeItemDoubleClicked");
	}
}