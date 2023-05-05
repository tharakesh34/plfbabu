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
 * * FileName : AccountsListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 02-01-2012 * *
 * Modified Date : 02-01-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 02-01-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.accounts.accounts.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.accounts.Accounts;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class AccountsListModelItemRenderer implements ListitemRenderer<Accounts>, Serializable {

	private static final long serialVersionUID = -1770311750492286100L;

	public AccountsListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, Accounts acounts, int count) {

		Listcell lc;
		lc = new Listcell(acounts.getAccountId());
		lc.setParent(item);
		lc = new Listcell(acounts.getAcCcy());
		lc.setParent(item);
		lc = new Listcell(acounts.getAcType());
		lc.setParent(item);
		lc = new Listcell(acounts.getAcBranch());
		lc.setParent(item);
		lc = new Listcell(acounts.getLovDescCustCIF());
		lc.setParent(item);
		lc = new Listcell(acounts.getAcFullName());
		lc.setParent(item);
		lc = new Listcell(acounts.getAcShortName());
		lc.setParent(item);
		lc = new Listcell(
				PennantApplicationUtil.getLabelDesc(acounts.getAcPurpose(), PennantStaticListUtil.getAccountPurpose()));
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbInternalAc = new Checkbox();
		cbInternalAc.setDisabled(true);
		cbInternalAc.setChecked(acounts.isInternalAc());
		lc.appendChild(cbInternalAc);
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbCustSysAc = new Checkbox();
		cbCustSysAc.setDisabled(true);
		cbCustSysAc.setChecked(acounts.isCustSysAc());
		lc.appendChild(cbCustSysAc);
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbAcInactive = new Checkbox();
		cbAcInactive.setDisabled(true);
		cbAcInactive.setChecked(acounts.isAcActive());
		lc.appendChild(cbAcInactive);
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbAcBlocked = new Checkbox();
		cbAcBlocked.setDisabled(true);
		cbAcBlocked.setChecked(acounts.isAcBlocked());
		lc.appendChild(cbAcBlocked);
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbAcClosed = new Checkbox();
		cbAcClosed.setDisabled(true);
		cbAcClosed.setChecked(acounts.isAcClosed());
		lc.appendChild(cbAcClosed);
		lc.setParent(item);
		lc = new Listcell(acounts.getHostAcNumber());
		lc.setParent(item);
		lc = new Listcell(acounts.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(acounts.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", acounts);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onAccountsItemDoubleClicked");
	}

}