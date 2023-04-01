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
 * * FileName : ProvisionListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 31-05-2012 *
 * * Modified Date : 31-05-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 31-05-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.financemanagement.suspense.model;

import java.io.Serializable;

import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.finance.FinanceSuspDetails;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class SuspenseDetailListModelItemRenderer implements ListitemRenderer<FinanceSuspDetails>, Serializable {

	private int formatter;

	public SuspenseDetailListModelItemRenderer(int formatter) {
		super();
		this.formatter = formatter;
	}

	private static final long serialVersionUID = -4554647022945989420L;

	@Override
	public void render(Listitem item, FinanceSuspDetails suspDetails, int count) {

		Listcell lc;
		lc = new Listcell(DateUtil.formatToLongDate(suspDetails.getFinTrfDate()));
		lc.setParent(item);
		String movement = "Suspense";
		if ("R".equals(suspDetails.getFinTrfMvt())) {
			movement = "Release";
		}
		lc = new Listcell(movement);
		lc.setParent(item);
		lc = new Listcell(String.valueOf(suspDetails.getFinSuspSeq()));
		lc.setParent(item);
		lc = new Listcell(CurrencyUtil.format(suspDetails.getFinTrfAmt(), formatter));
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(suspDetails.getFinODDate()));
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(suspDetails.getFinTrfFromDate()));
		lc.setParent(item);

	}
}