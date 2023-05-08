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
 * * FileName : FinanceStatusCodeListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 18-04-2017 * * Modified Date : 18-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 18-04-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.applicationmaster.financestatuscode.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.FinanceStatusCode;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class FinanceStatusCodeListModelItemRenderer implements ListitemRenderer<FinanceStatusCode>, Serializable {

	private static final long serialVersionUID = 1L;

	public FinanceStatusCodeListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, FinanceStatusCode financeStatusCode, int count) {

		Listcell lc;
		lc = new Listcell(financeStatusCode.getStatusCode());
		lc.setParent(item);
		lc = new Listcell(financeStatusCode.getStatusDesc());
		lc.setParent(item);
		lc = new Listcell(financeStatusCode.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(financeStatusCode.getRecordType()));
		lc.setParent(item);
		item.setAttribute("statusId", financeStatusCode.getStatusId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onFinanceStatusCodeItemDoubleClicked");
	}
}