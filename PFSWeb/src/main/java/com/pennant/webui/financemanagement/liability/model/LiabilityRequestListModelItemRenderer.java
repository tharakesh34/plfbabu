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
 * * FileName : LiabilityRequestListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 31-12-2015 * * Modified Date : 31-12-2015 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 31-12-2015 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.financemanagement.liability.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.finance.liability.LiabilityRequest;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class LiabilityRequestListModelItemRenderer implements ListitemRenderer<LiabilityRequest>, Serializable {

	private static final long serialVersionUID = -3191789325449044840L;

	@Override
	public void render(Listitem item, LiabilityRequest liabilityRequest, int count) {

		Listcell lc;
		lc = new Listcell(liabilityRequest.getFinType());
		lc.setParent(item);

		lc = new Listcell(liabilityRequest.getCustCIF());
		lc.setParent(item);
		lc = new Listcell(liabilityRequest.getFinReference());
		lc.setParent(item);
		lc = new Listcell(liabilityRequest.getFinBranch());
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(liabilityRequest.getFinStartDate()));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(liabilityRequest.getNumberOfTerms()));
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(liabilityRequest.getMaturityDate()));
		lc.setParent(item);
		lc = new Listcell(liabilityRequest.getFinCcy());
		lc.setParent(item);
		lc = new Listcell(CurrencyUtil.format(liabilityRequest.getFinAmount(),
				CurrencyUtil.getFormat(liabilityRequest.getFinCcy())));
		lc.setParent(item);
		lc = new Listcell(liabilityRequest.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(liabilityRequest.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", liabilityRequest.getId());
		ComponentsCtrl.applyForward(item, "onDoubleClick=onLiabilityRequestItemDoubleClicked");
	}
}