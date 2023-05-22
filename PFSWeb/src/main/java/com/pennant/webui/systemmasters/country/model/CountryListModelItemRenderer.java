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
 * * FileName : CountryListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 * *
 * Modified Date : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.country.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class CountryListModelItemRenderer implements ListitemRenderer<Country>, Serializable {

	private static final long serialVersionUID = 4901084385021031196L;

	public CountryListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, Country country, int count) {

		Listcell lc;
		lc = new Listcell(country.getCountryCode());
		lc.setParent(item);
		lc = new Listcell(country.getCountryDesc());
		lc.setParent(item);
		lc = new Listcell(CurrencyUtil.format(country.getCountryParentLimit(), 0));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell(CurrencyUtil.format(country.getCountryResidenceLimit(), 0));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell(CurrencyUtil.format(country.getCountryRiskLimit(), 0));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbCountryIsActive = new Checkbox();
		cbCountryIsActive.setDisabled(true);
		cbCountryIsActive.setChecked(country.isCountryIsActive());
		lc.appendChild(cbCountryIsActive);
		lc.setParent(item);
		lc = new Listcell(country.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(country.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", country.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onCountryItemDoubleClicked");
	}
}