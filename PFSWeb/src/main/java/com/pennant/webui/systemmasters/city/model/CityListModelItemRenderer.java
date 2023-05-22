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
 * * FileName : CityListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 * *
 * Modified Date : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.city.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class CityListModelItemRenderer implements ListitemRenderer<City>, Serializable {

	private static final long serialVersionUID = -5018118741984246012L;

	public CityListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, City city, int count) {

		Listcell lc;
		lc = new Listcell(city.getPCCountry() + "-" + city.getLovDescPCCountryName());
		lc.setParent(item);
		lc = new Listcell(city.getPCProvince() + "-" + city.getLovDescPCProvinceName());
		lc.setParent(item);
		lc = new Listcell(city.getPCCity());
		lc.setParent(item);
		lc = new Listcell(city.getPCCityName());
		lc.setParent(item);
		lc = new Listcell(city.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(city.getRecordType()));
		lc.setParent(item);

		item.setAttribute("pcCountry", city.getPCCountry());
		item.setAttribute("pcProvince", city.getPCProvince());
		item.setAttribute("pcCity", city.getPCCity());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onCityItemDoubleClicked");
	}
}