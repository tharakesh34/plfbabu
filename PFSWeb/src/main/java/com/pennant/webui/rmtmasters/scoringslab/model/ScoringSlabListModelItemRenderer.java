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
 * * FileName : ScoringSlabListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-12-2011
 * * * Modified Date : 05-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.rmtmasters.scoringslab.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.rmtmasters.ScoringSlab;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class ScoringSlabListModelItemRenderer implements ListitemRenderer<ScoringSlab>, Serializable {

	private static final long serialVersionUID = 4953613230892783520L;

	public ScoringSlabListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, ScoringSlab scoringSlab, int count) {

		Listcell lc;
		lc = new Listcell(String.valueOf(scoringSlab.getScoringSlab()));
		lc.setParent(item);
		lc = new Listcell(scoringSlab.getCreditWorthness());
		lc.setParent(item);
		lc = new Listcell(scoringSlab.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(scoringSlab.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", scoringSlab);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onScoringSlabItemDoubleClicked");
	}
}