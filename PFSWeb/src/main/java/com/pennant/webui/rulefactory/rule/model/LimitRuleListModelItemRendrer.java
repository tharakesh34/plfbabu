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
 * * FileName : DedupParmListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 23-08-2011 *
 * * Modified Date : 23-08-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 23-08-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.rulefactory.rule.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.rulefactory.LimitFilterQuery;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class LimitRuleListModelItemRendrer implements ListitemRenderer<LimitFilterQuery>, Serializable {
	private static final long serialVersionUID = 2857143548087995871L;

	public LimitRuleListModelItemRendrer() {
		super();
	}

	@Override
	public void render(Listitem item, LimitFilterQuery dedupParm, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(dedupParm.getQueryCode());
		lc.setParent(item);
		lc = new Listcell(dedupParm.getQueryDesc());
		lc.setParent(item);
		lc = new Listcell();
		Checkbox ckActive = new Checkbox();
		ckActive.setChecked(dedupParm.isActive());
		ckActive.setDisabled(true);
		ckActive.setParent(lc);
		lc.setParent(item);

		lc = new Listcell(dedupParm.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(dedupParm.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", dedupParm);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onDedupParmItemDoubleClicked");
	}
}