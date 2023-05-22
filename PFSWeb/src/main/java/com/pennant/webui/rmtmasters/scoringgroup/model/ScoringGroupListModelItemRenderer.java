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
 * * FileName : ScoringGroupListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-12-2011
 * * * Modified Date : 05-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.rmtmasters.scoringgroup.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.rmtmasters.ScoringGroup;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class ScoringGroupListModelItemRenderer implements ListitemRenderer<ScoringGroup>, Serializable {

	private static final long serialVersionUID = 4089320198269727613L;

	public ScoringGroupListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, ScoringGroup scoringGroup, int count) {

		Listcell lc;
		lc = new Listcell(scoringGroup.getScoreGroupCode());
		lc.setParent(item);
		lc = new Listcell(scoringGroup.getScoreGroupName());
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.getLabelDesc(scoringGroup.getCategoryType(),
				PennantAppUtil.getcustCtgCodeList()));
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.formateInt(scoringGroup.getMinScore()));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbIsoverride = new Checkbox();
		cbIsoverride.setDisabled(true);
		cbIsoverride.setChecked(scoringGroup.isIsOverride());
		lc.appendChild(cbIsoverride);
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.formateInt(scoringGroup.getOverrideScore()));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell(scoringGroup.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(scoringGroup.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", scoringGroup.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onScoringGroupItemDoubleClicked");
	}
}