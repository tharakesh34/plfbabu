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
 * * FileName : AccountingSetListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 14-12-2011 * * Modified Date : 14-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.rmtmasters.accountingset.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.rmtmasters.AccountingSet;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class AccountingSetListModelItemRenderer implements ListitemRenderer<AccountingSet>, Serializable {

	private static final long serialVersionUID = 2572007482335898401L;

	public AccountingSetListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, AccountingSet accountingSet, int count) {

		Listcell lc;
		lc = new Listcell(accountingSet.getEventCode());
		lc.setParent(item);
		lc = new Listcell(accountingSet.getLovDescEventCodeName());
		lc.setParent(item);
		lc = new Listcell(accountingSet.getAccountSetCode());
		lc.setParent(item);
		lc = new Listcell(accountingSet.getAccountSetCodeName());
		lc.setParent(item);
		lc = new Listcell(accountingSet.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(accountingSet.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", accountingSet.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onAccountingSetItemDoubleClicked");
	}
}