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
 * * FileName : ScoringTypeListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 08-11-2011
 * * * Modified Date : 08-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 08-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.bmtmasters.scoringtype.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.bmtmasters.ScoringType;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class ScoringTypeListModelItemRenderer implements ListitemRenderer<ScoringType>, Serializable {

	private static final long serialVersionUID = 7903665974929957450L;

	public ScoringTypeListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, ScoringType scoringType, int count) {

		Listcell lc;
		lc = new Listcell(scoringType.getScoType());
		lc.setParent(item);
		lc = new Listcell(scoringType.getScoDesc());
		lc.setParent(item);
		lc = new Listcell(scoringType.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(scoringType.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", scoringType);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onScoringTypeItemDoubleClicked");
	}
}