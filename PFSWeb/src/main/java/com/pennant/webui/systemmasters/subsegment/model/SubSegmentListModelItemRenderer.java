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
 * * FileName : SubSegmentListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 *
 * * Modified Date : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.subsegment.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.SubSegment;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class SubSegmentListModelItemRenderer implements ListitemRenderer<SubSegment>, Serializable {

	private static final long serialVersionUID = 9032616208152658049L;

	public SubSegmentListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, SubSegment subSegment, int count) {

		Listcell lc;
		lc = new Listcell(subSegment.getSegmentCode());
		lc.setParent(item);
		lc = new Listcell(subSegment.getSubSegmentCode());
		lc.setParent(item);
		lc = new Listcell(subSegment.getSubSegmentDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbSubSegmentIsActive = new Checkbox();
		cbSubSegmentIsActive.setDisabled(true);
		cbSubSegmentIsActive.setChecked(subSegment.isSubSegmentIsActive());
		lc.appendChild(cbSubSegmentIsActive);
		lc.setParent(item);
		lc = new Listcell(subSegment.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(subSegment.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", subSegment.getId());
		item.setAttribute("subSegmentCode", subSegment.getSubSegmentCode());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onSubSegmentItemDoubleClicked");
	}
}