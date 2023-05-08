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
 * * FileName : SegmentListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 * *
 * Modified Date : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.segment.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.Segment;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class SegmentListModelItemRenderer implements ListitemRenderer<Segment>, Serializable {

	private static final long serialVersionUID = 5566099050192789157L;

	public SegmentListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, Segment segment, int count) {

		Listcell lc;
		lc = new Listcell(segment.getSegmentCode());
		lc.setParent(item);
		lc = new Listcell(segment.getSegmentDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbSegmentIsActive = new Checkbox();
		cbSegmentIsActive.setDisabled(true);
		cbSegmentIsActive.setChecked(segment.isSegmentIsActive());
		lc.appendChild(cbSegmentIsActive);
		lc.setParent(item);
		lc = new Listcell(segment.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(segment.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", segment.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onSegmentItemDoubleClicked");
	}
}