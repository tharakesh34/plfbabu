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
 * * FileName : CostOfFundListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 *
 * * Modified Date : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.applicationmaster.costoffund.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.applicationmaster.CostOfFund;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class CostOfFundListModelItemRenderer implements ListitemRenderer<CostOfFund>, Serializable {
	private static final long serialVersionUID = -6273517593116519304L;

	public CostOfFundListModelItemRenderer() {

	}

	@Override
	public void render(Listitem item, CostOfFund CostOfFund, int count) {
		Listcell lc;
		lc = new Listcell(CostOfFund.getCofCode());
		lc.setParent(item);
		lc = new Listcell(CostOfFund.getLovDescCofTypeName());
		lc.setParent(item);
		lc = new Listcell(DateUtil.format(CostOfFund.getCofEffDate(), PennantConstants.dateFormat));
		lc.setParent(item);
		lc = new Listcell(CurrencyUtil.format(CostOfFund.getCofRate(), PennantConstants.defaultCCYDecPos));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell(CostOfFund.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(CostOfFund.getRecordType()));
		lc.setParent(item);

		item.setAttribute("cofCode", CostOfFund.getCofCode());
		item.setAttribute("currency", CostOfFund.getCurrency());
		item.setAttribute("cofEffDate", CostOfFund.getCofEffDate());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onCostOfFundItemDoubleClicked");
	}
}