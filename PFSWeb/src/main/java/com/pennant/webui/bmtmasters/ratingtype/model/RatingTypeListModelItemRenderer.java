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
 * * FileName : RatingTypeListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 *
 * * Modified Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.bmtmasters.ratingtype.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.bmtmasters.RatingType;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class RatingTypeListModelItemRenderer implements ListitemRenderer<RatingType>, Serializable {

	private static final long serialVersionUID = 1L;

	public RatingTypeListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, RatingType ratingType, int count) {

		Listcell lc;
		lc = new Listcell(ratingType.getRatingType());
		lc.setParent(item);
		lc = new Listcell(ratingType.getRatingTypeDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbValueType = new Checkbox();
		cbValueType.setDisabled(true);
		cbValueType.setChecked(ratingType.isValueType());
		lc.appendChild(cbValueType);
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.formateInt(ratingType.getValueLen()));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbRatingIsActive = new Checkbox();
		cbRatingIsActive.setDisabled(true);
		cbRatingIsActive.setChecked(ratingType.isRatingIsActive());
		lc.appendChild(cbRatingIsActive);
		lc.setParent(item);
		lc = new Listcell(ratingType.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(ratingType.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", ratingType.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onRatingTypeItemDoubleClicked");
	}
}