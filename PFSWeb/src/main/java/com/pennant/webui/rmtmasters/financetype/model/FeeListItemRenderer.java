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
 * * FileName : FeeListItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 01-11-2011 * * Modified
 * Date : 01-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 01-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.rmtmasters.financetype.model;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.rulefactory.Rule;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class FeeListItemRenderer implements ListitemRenderer<Rule>, Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(FeeListItemRenderer.class);

	public FeeListItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, Rule fee, int count) {

		logger.debug("Entering");
		Listcell lc;
		lc = new Listcell(fee.getRuleCode());
		lc.setParent(item);

		lc = new Listcell(fee.getRuleEvent());
		lc.setParent(item);

		lc = new Listcell(fee.getRuleModule());
		lc.setParent(item);

		lc = new Listcell(fee.getRecordType());
		lc.setParent(item);
		item.setAttribute("data", fee);

		ComponentsCtrl.applyForward(item, "onDoubleClick=onFeeItemDoubleClicked");

		logger.debug("Leaving");
	}

}
