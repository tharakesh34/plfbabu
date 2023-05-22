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
 *********************************************************************************************
 * FILE HEADER *
 *********************************************************************************************
 *
 * FileName : RuleListModelItemRenderer.java
 * 
 * Author : PENNANT TECHONOLOGIES
 * 
 * Creation Date : 03-06-2011
 * 
 * Modified Date : 03-06-2011
 * 
 * Description :
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-06-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.rulefactory.rule.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 */
public class RuleListModelItemRenderer implements ListitemRenderer<Rule>, Serializable {

	private static final long serialVersionUID = -5076855951380074437L;

	public RuleListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, Rule rule, int count) {

		Listcell lc;
		lc = new Listcell(rule.getRuleEvent());
		lc.setParent(item);
		lc = new Listcell(rule.getRuleCode());
		lc.setParent(item);
		lc = new Listcell(rule.getRuleCodeDesc());
		lc.setParent(item);
		lc = new Listcell(rule.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(rule.getRecordType()));
		lc.setParent(item);

		item.setAttribute("ruleCode", rule.getRuleCode());
		item.setAttribute("ruleEvent", rule.getRuleEvent());
		item.setAttribute("active", rule.isActive());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onRuleItemDoubleClicked");
	}
}