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
 * * FileName : CurrencyListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * *
 * Modified Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.currency.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class CurrencyListModelItemRenderer implements ListitemRenderer<Currency>, Serializable {

	private static final long serialVersionUID = 9199981912283581234L;

	public CurrencyListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, Currency currency, int count) {

		Listcell lc;
		lc = new Listcell(currency.getCcyCode());
		lc.setParent(item);
		lc = new Listcell(currency.getCcyNumber());
		lc.setParent(item);
		lc = new Listcell(currency.getCcyDesc());
		lc.setParent(item);
		lc = new Listcell(currency.getCcySwiftCode());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbCcyIsActive = new Checkbox();
		cbCcyIsActive.setDisabled(true);
		cbCcyIsActive.setChecked(currency.isCcyIsActive());
		lc.appendChild(cbCcyIsActive);
		lc.setParent(item);
		lc = new Listcell(currency.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(currency.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", currency.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onCurrencyItemDoubleClicked");
	}
}